from core.commands import BaseCommand
from core.config import Settings
from core import constants as K
import importlib


class Install(BaseCommand):
    terraform_with_targets = False

    def __init__(self, args):
        self.terraform_with_targets = False
        super().__init__(args)

    def execute(self, provider):
        self.initialize_classes(provider)
        input_instance = self.read_input()

        if self.initialize_and_validate():
            if self.check_pre_requisites() is False:
                self.exit_system_with_pre_requisites_fail()

            resources_to_process = self.get_resources_to_process(input_instance)
            if resources_to_process:
                self.install_class(self.args, input_instance).execute(
                    resources_to_process,
                    self.terraform_with_targets,
                    self.dry_run
                )
            else:
                print(K.RESOURCES_EMPTY)

    def initialize_classes(self, provider):
        self.validation_class = getattr(importlib.import_module(
            provider.provider_module + '.validate'), 'SystemInstallValidation')
        self.input_class = getattr(importlib.import_module(
            provider.provider_module + '.input'), 'SystemInstallInput')
        self.install_class = getattr(importlib.import_module(
            provider.provider_module + '.install'), 'Install')
