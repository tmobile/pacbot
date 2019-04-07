from core.commands import BaseCommand
from core.config import Settings
from core import constants as K
from core.terraform import PyTerraform
from resources.iam.all_read_role import AllReadRole
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
        # args.append((K.CATEGORY_FIELD_NAME, "datastore"))
        # tf_outputs = PyTerraform.load_terraform_output_from_json_file()
        # role_file = os.path.join(Settings.TERRAFORM_DIR, "iam_all_read_role_AllReadRole.tf")
        # if not tf_outputs.get(AllReadRole.get_resource_id(), False):
        #     args.append((K.CATEGORY_FIELD_NAME, "all_read_role"))
        #     args.append((K.CATEGORY_FIELD_NAME, "ecs_role"))

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

    def upgrade_pacbot(self, input_instance):
        """
        Upgrade RDS, ES and roles if any by running terraform apply for those resources

        Args:
            input_instance (Input object): User input values
        """
        terraform_with_targets = False
        resources_to_process = self.get_complete_resources(input_instance)

        self.install_class(
            self.args,
            input_instance,
            check_dependent_resources=False
        ).execute(
            resources_to_process,
            terraform_with_targets,
            self.dry_run
        )
