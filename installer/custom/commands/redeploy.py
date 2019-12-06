from core.commands import BaseCommand
from core.config import Settings
from core import constants as K
from core.terraform.resources.aws.ecs import ECSTaskDefinitionResource, ECSClusterResource
from core.terraform.resources.aws.load_balancer import ALBTargetGroupResource
from resources.pacbot_app.alb import ApplicationLoadBalancer
from core.providers.aws.boto3 import elb
from core.terraform import PyTerraform
from core.providers.aws.boto3.ecs import stop_all_tasks_in_a_cluster, deregister_task_definition
from core.commands import BaseCommand
from core.config import Settings
from core.terraform import PyTerraform
from core import constants as K
from threading import Thread
import time
import importlib
import sys
import os


class Redeploy(BaseCommand):
    """
    This calss is defined to reinstall PacBot which is already installed by Redeploy command

    Attributes:
        validation_class (class): This validate the input and resources
        input_class (class): Main class to read input from user
        install_class (class): Provider based install class
        need_complete_install (boolean): True if complete installation is required else False
        dry_run (boolean): Need actual insalltion or not
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
        self.silent_install = True if any([x[1] for x in args if x[0] == "silent"]) else self.silent_install

    def _need_complete_installation(self):
        """
        Checj whether the redeploy need complete reinstallation.
        """
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

        terraform_with_targets = False if self.need_complete_install else True
        resources_to_install = self.get_complete_resources(input_instance) if self.need_complete_install else resources_to_install

        # self.run_pre_deployment_process(resources_to_process)
        self.run_real_deployment(input_instance, resources_to_destroy, resources_to_install, terraform_with_targets)

    def run_real_deployment(self, input_instance, resources_to_destroy, resources_to_install, terraform_with_targets):
        """
        Main thread method which invokes the 2 thread: one for actual execution and another for displaying status

        Args:
            input_instance (Input obj): Input object with values read from user
            resources_to_destroy (list): List of resources to be destroyed for recreation
            resources_to_install (list): List of resources to be recreated
            terraform_with_targets (boolean): This is True since redeployment is happening
        """
        self.terraform_thread = Thread(
            target=self.run_reinstallation,
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
        This is a place holder to run some script parallely if there is anything to do

        Args:
            resources_to_destroy (list): List of resources to be destroyed for recreation
            resources_to_install (list): List of resources to be recreated
            only_tasks (boolean): This flasg decides whther to deregister task definition or not
        """
        pass

    def generate_terraform_files_and_upgrade_state(self, input_instance):
        all_resources = self.get_complete_resources(input_instance)
        for resource in all_resources:
            resource.generate_terraform()
        PyTerraform.terrafomr12_upgrade()  # This is required only when terraform version 12 is used

    def run_reinstallation(self, input_instance, resources_to_destroy, resources_to_install, terraform_with_targets):
        """
        Execute the installation of resources by invoking the execute method of provider class

        Args:
            input_instance (Input obj): Input object with values read from user
            resources_to_destroy (list): List of resources to be destroyed for recreation
            resources_to_install (list): List of resources to be recreated
            terraform_with_targets (boolean): This is True since redeployment is happening
        """
        self.generate_terraform_files_and_upgrade_state(input_instance)

        installer = self.install_class(
            input_instance,
            check_dependent_resources=False
        )

        installer.execute(
            resources_to_destroy,
            resources_to_install,
            terraform_with_targets,
            self.dry_run
        )
