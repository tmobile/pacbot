from core.commands import BaseCommand
from core.config import Settings
from core.terraform import PyTerraform
from core import constants as K
from threading import Thread
import time
import importlib
import sys
import os


class Reinstall(BaseCommand):
    """
    This calss is defined to reinstall PacBot which is already installed by Installer command

    Attributes:
        validation_class (class): This validate the input and resources
        input_class (class): Main class to read input from user
        install_class (class): Provider based install class
    """
    def __init__(self, args):

        Settings.set('SKIP_RESOURCE_EXISTENCE_CHECK', True)

        args.append((K.CATEGORY_FIELD_NAME, "deploy"))
        args.append((K.CATEGORY_FIELD_NAME, "upload_tf"))
        self.destroy_resource_tags_list = [v for (k, v) in args if k == self.category_field_name]

        args.append((K.CATEGORY_FIELD_NAME, "deploy"))
        args.append((K.CATEGORY_FIELD_NAME, "roles"))
        args.append((K.CATEGORY_FIELD_NAME, "all_read_role"))
        args.append((K.CATEGORY_FIELD_NAME, "batch-ecr"))
        args.append((K.CATEGORY_FIELD_NAME, "batch-job"))
        args.append((K.CATEGORY_FIELD_NAME, "submit-job"))
        args.append((K.CATEGORY_FIELD_NAME, "rule-engine-job"))
        args.append((K.CATEGORY_FIELD_NAME, "upload_tf"))
        self.reinstall_resource_tags_list = [v for (k, v) in args if k == self.category_field_name]

        self.need_complete_install = self._need_complete_installation()

        self.dry_run = True if any([x[1] for x in args if x[0] == "dry-run"]) else self.dry_run

    def _need_complete_installation(self):
        need_complete_install = False

        redshift_cluster_file_tf = os.path.join(Settings.TERRAFORM_DIR, "datastore_redshift_RedshiftCluster.tf")
        redshift_cluster_file_tf_json = os.path.join(Settings.TERRAFORM_DIR, "datastore_redshift_RedshiftCluster.tf.json")

        if os.path.exists(redshift_cluster_file_tf) or os.path.exists(redshift_cluster_file_tf_json):
            need_complete_install = True

        return need_complete_install

    def execute(self, provider):
        """
        Command execution starting point

        Args:
            provider (string): Provider name like AWS or Azure etc
        """
        self.initialize_install_classes(provider)

        if self.check_pre_requisites() is False:
            self.exit_system_with_pre_requisites_fail()

        input_instance = self.read_input()
        self.re_deploy_pacbot(input_instance)

    def initialize_install_classes(self, provider):
        """
        Initialise classes based on the provider

        Args:
            provider (string): Provider name like AWS or Azure etc
        """
        self.validation_class = getattr(importlib.import_module(
            provider.provider_module + '.validate'), 'SystemInstallValidation')
        self.input_class = getattr(importlib.import_module(
            provider.provider_module + '.input'), 'SystemInstallInput')
        self.install_class = getattr(importlib.import_module(
            provider.provider_module + '.reinstall'), 'ReInstall')

    def re_deploy_pacbot(self, input_instance):
        """
        Start method for redeploy

        Args:
            input_instance (Input object): User input values
        """
        resources_to_destroy = self.get_resources_to_process(self.destroy_resource_tags_list, input_instance)
        resources_to_install = self.get_resources_to_process(self.reinstall_resource_tags_list, input_instance)

        # try:
        #     resources_to_taint = self.get_resources_with_given_tags(input_instance, ["deploy"])
        #     resources_to_taint = [resource for resource in resources_to_taint if resource.PROCESS is True]
        #     response = PyTerraform().terraform_taint(resources_to_taint)  # If tainted or destroyed already then skip it
        # except Exception as e:
        #     pass

        terraform_with_targets = False if self.need_complete_install else True
        resources_to_install = self.get_complete_resources(input_instance) if self.need_complete_install else resources_to_install

        # self.run_pre_deployment_process(resources_to_process)
        self.run_real_deployment(input_instance, resources_to_destroy, resources_to_install, terraform_with_targets)

    def run_real_deployment(self, input_instance, resources_to_destroy, resources_to_install, terraform_with_targets):
        """
        Main thread method which invokes the 2 thread: one for actual execution and another for displaying status

        Args:
            input_instance (Input obj): Input object with values read from user
            resources_to_process (list): List of resources to be created/updated
            terraform_with_targets (boolean): This is True since redeployment is happening
        """
        self.terraform_thread = Thread(
            target=self.run_tf_apply,
            args=(input_instance, list(resources_to_destroy), list(resources_to_install), terraform_with_targets))
        # Dt-run variable is passed as it is rquired otherwise argument parsing issue will occur
        stop_related_task_thread = Thread(
            target=self.inactivate_required_services_for_redeploy,
            args=(list(resources_to_destroy), list(resources_to_install), self.dry_run))

        self.terraform_thread.start()
        stop_related_task_thread.start()

        self.terraform_thread.join()
        stop_related_task_thread.join()

    def inactivate_required_services_for_redeploy(self, resources_to_destroy, resources_to_install, dry_run):
        """
        Before redeploy get started or on redeploy happens stop the tasks and deregister task definition

        Args:
            resources_to_process (list): List of resources to be created/updated
            only_tasks (boolean): This flasg decides whther to deregister task definition or not
        """
        pass

    def run_tf_apply(self, input_instance, resources_to_destroy, resources_to_install, terraform_with_targets):
        """
        Execute the installation of resources by invoking the execute method of provider class

        Args:
            input_instance (Input obj): Input object with values read from user
            resources_to_process (list): List of resources to be created/updated
            terraform_with_targets (boolean): This is True since redeployment is happening
        """
        PyTerraform.terrafomr12_upgrade()  # This is required only when terraform version 12 is used
        self.install_class(
            input_instance,
            check_dependent_resources=False
        ).execute(
            resources_to_destroy,
            resources_to_install,
            terraform_with_targets,
            self.dry_run
        )
