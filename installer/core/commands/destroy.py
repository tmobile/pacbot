from core.commands import BaseCommand
from core import constants as K
import importlib


class Destroy(BaseCommand):
    """
    Base install class which identify actual provide destroy class and execute destruction

    Attributes:
        terraform_with_targets (Boolean): Identify whether complete installation or partial installation is required
        validation_class (class): Provider validation class for validating inputs (aws validator)
        input_class (class): Provider input class
        destroy_class (class): Provider desttoy class
    """
    def __init__(self, args):
        """
        Constructor method for destroy

        Args:
            args (List): List of key- value pair of args supplied to the command
        """
        super().__init__(args)

    def execute(self, provider):
        """
        Execution method which read inputs, initialises, validate and execute desroy

        Args:
            provider (str): Provider name based on which the corresponding classes are retrieved
        """
        self.initialize_classes(provider)

        input_instance = self.read_input()
        if self.initialize_and_validate():
            if self.check_pre_requisites() is False:
                self.exit_system_with_pre_requisites_fail()

            resources_to_process = self.get_resources_to_process(self.resource_tags_list, input_instance)
            if resources_to_process:
                self.destroy_class(input_instance).execute(
                    resources_to_process,
                    self.terraform_with_targets,
                    self.dry_run
                )
            else:
                print(K.RESOURCES_EMPTY)

    def initialize_classes(self, provider):
        """
        Identify and initialize the classes required for execution

        Args:
            provider (str): Provider name based on which corresponding classes are retrieved
        """
        self.validation_class = getattr(importlib.import_module(
            provider.provider_module + '.validate'), 'SystemDestroyValidation')
        self.input_class = getattr(importlib.import_module(
            provider.provider_module + '.input'), 'SystemDestroyInput')
        self.destroy_class = getattr(importlib.import_module(
            provider.provider_module + '.destroy'), 'Destroy')
