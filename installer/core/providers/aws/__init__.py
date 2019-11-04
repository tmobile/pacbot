from core.terraform.utils import get_terraform_provider_file
from core.mixins import MsgMixin
from core.terraform import PyTerraform
from core.terraform.resources import TerraformResource
from core import constants as K
from core.config import Settings
import inspect
import json
import os
import uuid


class BaseAction(MsgMixin):
    """
    This Base class for AWS Install and Destroy classes.

    Attributes:
        check_dependent_resources (boolean): Check the resources added to DEPENDS_ON should be checked or not
        total_resources_count (int): Total number of resources to be installed/destroyed
        input (instance): Input instance to AWS install/destroy provider
        tf_outputs (dict): Terraform output dict
    """
    check_dependent_resources = True
    total_resources_count = 0

    def __init__(self, input=None):
        self.input = input
        self.tf_outputs = PyTerraform.load_terraform_output_from_json_file()
        self.clear_status_dir_files()

    def clear_status_dir_files(self):
        """
        Clear the files in status directory after installation. These files are used to get the count of installed
        resources during installation
        """
        item = os.walk(Settings.OUTPUT_STATUS_DIR).__next__()
        for f in item[2]:  # First arg is root, second arg is dirs and 3rd arg is files
            if str(f) != ".gitignore":
                os.unlink(os.path.join(Settings.OUTPUT_STATUS_DIR, f))

    def files_count_in_output_status_dir(self):
        """
        Total number of files present in the status directory. This number is used to identify as the number of
        installed resources

        Returns:
            files_count (int): Number of files present in the direcotry
        """
        path, dirs, files = os.walk(Settings.OUTPUT_STATUS_DIR).__next__()

        return len(files)

    def _create_terraform_provider_file(self):
        """Terraform provider file  is created as part of installation/destruction execution"""
        terraform_provider_file = get_terraform_provider_file()

        aws_provider = {'region': self.input.AWS_AUTH_CRED['aws_region']}
        if self.input.AWS_AUTH_CRED['aws_auth_option'] == 1:
            aws_provider['access_key'] = self.input.AWS_AUTH_CRED['aws_access_key']
            aws_provider['secret_key'] = self.input.AWS_AUTH_CRED['aws_secret_key']
        elif self.input.AWS_AUTH_CRED['aws_auth_option'] == 2:
            aws_provider['assume_role'] = {
                'role_arn': self.input.AWS_AUTH_CRED['assume_role_arn'],
                'session_name': str(uuid.uuid4())
            }

        provider_script = {
            'provider': {
                'aws': aws_provider
            }
        }

        with open(terraform_provider_file, "w") as jsonfile:
            json.dump(provider_script, jsonfile, indent=4)

    def _delete_terraform_provider_file(self):
        """Terraform provider file which is created as part of installation/destruction is removed after the execution"""
        terraform_provider_file = get_terraform_provider_file()
        if os.path.isfile(terraform_provider_file):
            os.remove(terraform_provider_file)

    def _delete_all_terraform_files(self):
        """"Delete all terraform files before terraform regeneration if the install is done on all resources"""
        for file in os.listdir(Settings.TERRAFORM_DIR):
            if file.endswith(".tf") or file.endswith(".tf.json"):
                file_abs_path = os.path.join(Settings.TERRAFORM_DIR, file)
                os.remove(file_abs_path)

    def validate_resources(self, resources):
        return self.validate_resource_existence(resources)

    def validate_resource_existence(self, resources):
        """
        Check whether the resource to be created as part of installation is already exists in AWS

        Args:
            resources (list): Resources to be installed

        Returns:
            can_continue_installation (boolean): True if any resource already present in AWS else False
        """
        can_continue_installation = True
        if not Settings.get('SKIP_RESOURCE_EXISTENCE_CHECK', False):
            self.show_step_heading(K.RESOURCE_EXISTS_CHECK_STARTED)
            for resource in resources:
                resource_class = resource.__class__
                if TerraformResource not in inspect.getmro(resource_class):
                    continue  # This means resource is a Variable or Data and not TF Resource

                self.show_progress_start_message("Checking resource existence for %s" % resource_class.__name__)
                exists, checked_details = resource.check_exists_before(self.input, self.tf_outputs)
                self.erase_printed_line()
                self.total_resources_count += 1

                if exists:
                    can_continue_installation = False
                    resource_name = resource.resource_instance_name.replace("_", " ").title()
                    message = "Resource: %s, %s: `%s`" % (resource_name, checked_details['attr'], checked_details['value'])
                    self.show_step_inner_messaage(message, K.EXISTS)

            if can_continue_installation:
                self.show_step_finish(K.RESOURCE_EXISTS_CHECK_COMPLETED, color=self.GREEN_ANSI)
            else:
                self.show_step_finish(K.RESOURCE_EXISTS_CHECK_FAILED, color=self.ERROR_ANSI)
            self.stdout_flush()
        else:
            self._load_total_resources_count(resources)

        return can_continue_installation

    def _load_total_resources_count(self, resources):
        """
        Find the number of real terraform resources to be created/destroyed

        Args:
            resources (list): All kind of resources to be installed/destroyed including data resources
        """
        self.total_resources_count = 0
        for resource in resources:
            resource_class = resource.__class__
            if TerraformResource in inspect.getmro(resource_class):
                self.total_resources_count += 1

    def validate_arguments(self, resources, terraform_with_targets):
        """
        Validate all arguments of all terraform resources

        Args:
            resources (list): All kind of resources to be installed/destroyed including data resources
            terraform_with_targets (boolean): True if subset of all resources to be installed else False

        Returns:
            key_msg (dict): Dict contains error messages if anny else empty dict
        """
        key_msg = {}
        if not terraform_with_targets:
            resource_id_with_depends_on = {}
            for resource in resources:
                resource_id_with_depends_on[self._get_depends_key(resource)] = resource.DEPENDS_ON
                success, msg_list = resource.validate_input_args()
                if not success:
                    key_msg[resource.__class__.__name__] = msg_list

            key_msg = self.validate_depends_on_resources(
                resource_id_with_depends_on, key_msg)

        return key_msg

    def validate_depends_on_resources(self, resource_id_with_depends_on, key_msg):
        """
        Validate resources availability for the DEPENDS_ON attribute

        Args:
            resource_id_with_depends_on (str): Resource ID for which the depends on resources to be validated
            key_msg (dict): Dict contains error messages if anny else empty dict

        Returns:
            key_msg (dict): Dict contains error messages if anny else empty dict
        """
        if self.check_dependent_resources:
            install_resource_keys = resource_id_with_depends_on.keys()

            for key, resource_classes in resource_id_with_depends_on.items():
                for resource_class in resource_classes:
                    if self._get_depends_key(resource_class) in install_resource_keys:
                        continue

                    if key in key_msg:
                        key_msg[key].append(
                            "Depends on resource is not found: %s" % resource_class.__name__)
                    else:
                        key_msg[key] = ["Depends on resource is not found: %s" %
                                        resource_class.__name__]

        return key_msg

    def _get_depends_key(self, resource):
        """
        Get resource id of the dependent resource

        Args:
            resource (object): terraform resource

        Returns:
            resource_id (str): Resource ID
        """
        return str(resource.get_resource_id())

    def _get_terraform_output_count(self, prev_count):
        """
        Get current terraform resources count by calling the output command

        Args:
            prev_count (int): Previous count obtained before the current instant

        Returns:
            count (int): Current resources count
        """
        try:
            output = PyTerraform.load_terraform_output()
            return len(output)
        except:
            return prev_count
