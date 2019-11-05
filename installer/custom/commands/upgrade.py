from core.commands import BaseCommand
from core.config import Settings
from core import constants as K
from core.terraform import PyTerraform
from resources.iam.all_read_role import AllReadRole
from core.providers.aws.boto3 import elb
from core.terraform.resources.aws.load_balancer import ALBTargetGroupResource
from resources.pacbot_app.alb import ApplicationLoadBalancer
import importlib
import os


class Upgrade(BaseCommand):
    """
    This calss is defined to create new command to upgrade PacBot RDS, ES and roles

    Attributes:
        validation_class (class): This validate the input and resources
        input_class (class): Main class to read input from user
        install_class (class): Provider based install class

    """
    def __init__(self, args):
        Settings.set('SKIP_RESOURCE_EXISTENCE_CHECK', True)
        super().__init__(args)

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
        self.upgrade_pacbot(input_instance)

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
            provider.provider_module + '.install'), 'Install')

    def run_pre_deployment_process(self, resources_to_process):
        """
        Before redeploy get started do predeployment activities

        Args:
            resources_to_process (list): List of resources to be created/updated
        """
        if not self.dry_run:
            elb.delete_all_listeners_of_alb(
                ApplicationLoadBalancer.get_input_attr('name'),
                Settings.AWS_AUTH_CRED)

            tg_resources = self._get_resources_of_a_given_class_type(resources_to_process, ALBTargetGroupResource)
            tg_names = [resource.get_input_attr('name') for resource in tg_resources]
            elb.delete_alltarget_groups(
                tg_names,
                Settings.AWS_AUTH_CRED)

    def upgrade_pacbot(self, input_instance):
        """
        Upgrade RDS, ES and roles if any by running terraform apply for those resources

        Args:
            input_instance (Input object): User input values
        """
        terraform_with_targets = False
        resources_to_process = self.get_complete_resources(input_instance)
        self.run_pre_deployment_process(resources_to_process)

        self.install_class(
            input_instance,
            check_dependent_resources=False
        ).execute(
            resources_to_process,
            terraform_with_targets,
            self.dry_run
        )
