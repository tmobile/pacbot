from core.commands import BaseCommand
from core.config import Settings
from core import constants as K
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
        args.append((K.CATEGORY_FIELD_NAME, "deploy"))
        args.append((K.CATEGORY_FIELD_NAME, "batch-ecr"))
        args.append((K.CATEGORY_FIELD_NAME, "batch-job"))
        args.append((K.CATEGORY_FIELD_NAME, "submit-job"))
        args.append((K.CATEGORY_FIELD_NAME, "rule-engine-job"))
        args.append((K.CATEGORY_FIELD_NAME, "upload_tf"))

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
        resources_to_process = self.get_resources_to_process(input_instance)
        terraform_with_targets = True

        self.install_class(
            self.args,
            input_instance,
            check_dependent_resources=False
        ).execute(
            resources_to_process,
            terraform_with_targets,
            self.dry_run
        )
