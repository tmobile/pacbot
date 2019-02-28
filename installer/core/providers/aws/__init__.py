from core.terraform.utils import get_terraform_provider_file
from core.mixins import MsgMixin
from core.terraform import PyTerraform
from core.terraform.resources import TerraformResource
from core import constants as K
from core.config import Settings
import inspect
import json
import os


class BaseAction(MsgMixin):
    check_dependent_resources = True
    total_resources_count = 0

    def __init__(self, input=None):
        self.input = input
        self.tf_outputs = PyTerraform.load_terraform_output_from_json_file()
        self.clear_status_dir_files()

    def clear_status_dir_files(self):
        item = os.walk(Settings.OUTPUT_STATUS_DIR).__next__()
        for f in item[2]:  # First arg is root, second arg is dirs and 3rd arg is files
            if str(f) != ".gitignore":
                os.unlink(os.path.join(Settings.OUTPUT_STATUS_DIR, f))

    def files_count_in_output_status_dir(self):
        path, dirs, files = os.walk(Settings.OUTPUT_STATUS_DIR).__next__()

        return len(files)

    def _create_terraform_provider_file(self):
        terraform_provider_file = get_terraform_provider_file()
        provider_script = {
            'provider': {
                'aws': {
                    'access_key': self.input.aws_access_key,
                    'secret_key': self.input.aws_secret_key,
                    'region': self.input.aws_region
                }
            }
        }

        with open(terraform_provider_file, "w") as jsonfile:
            json.dump(provider_script, jsonfile, indent=4)

    def _delete_terraform_provider_file(self):
        terraform_provider_file = get_terraform_provider_file()
        if os.path.isfile(terraform_provider_file):
            os.remove(terraform_provider_file)

    def _delete_all_terraform_files(self):
        for file in os.listdir(Settings.TERRAFORM_DIR):
            if file.endswith(".tf"):
                file_abs_path = os.path.join(Settings.TERRAFORM_DIR, file)
                os.remove(file_abs_path)

    def validate_resources(self, resources):
        return self.validate_resource_existence(resources)

    def validate_resource_existence(self, resources):
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
        self.total_resources_count = 0
        for resource in resources:
            resource_class = resource.__class__
            if TerraformResource in inspect.getmro(resource_class):
                self.total_resources_count += 1

    def validate_arguments(self, resources, terraform_with_targets):
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
        return str(resource.get_resource_id())

    def _get_terraform_output_count(self, prev_count):
        try:
            output = PyTerraform.load_terraform_output()
            return len(output)
        except:
            return prev_count
