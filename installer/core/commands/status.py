from core.commands import BaseCommand
from core.config import Settings
from core import constants as K
from core.terraform import PyTerraform
from core.mixins import MsgMixin
import importlib
import logging


class Status(BaseCommand, MsgMixin):
    """
    Actual status class which display the current status of install/destroy

    Attributes:
        terraform_with_targets (Boolean): Identify whether complete installation or partial installation is required.
             default to False
        input_class (class): Provider input class
    """

    terraform_with_targets = False

    def __init__(self, args):
        """
        Constructor method for status

        Args:
            args (List): List of key- value pair of args supplied to the command
        """
        self.terraform_with_targets = False
        super().__init__(args)
        logging.disable(logging.ERROR)

    def execute(self, provider):
        """
        Execution method which read inputs, initialises, validate and execute status command

        Args:
            provider (str): Provider name based on which the corresponding classes are retrieved
        """
        py_terraform = PyTerraform()
        self.initialize_classes(provider)
        input_instance = self.read_input()
        need_instance = False
        display_op_list = []

        resources = self.get_resources_to_process(self.resource_tags_list, input_instance)
        terraform_outputs = py_terraform.save_terraform_output()
        status = py_terraform.get_current_status()
        if not status and not terraform_outputs:
            print(K.NO_STATUS_OUTPUT)
        else:
            print(self.BCYAN_ANSI + "\n%s" % K.CURRENT_STATUS_MSG + self.RESET_ANSI)
            if terraform_outputs:
                print("\t%s" % K.CURRENTLY_INSTALLED_RESOURCES)
                for item in terraform_outputs:
                    print(self.CYAN_ANSI + "\t\t Resource: %s" % (item.replace("_", " ").title()) + self.RESET_ANSI)

            if status:
                cmd = self.BGREEN_ANSI + "Last Executed Command: " + self.RESET_ANSI + status['last_exec_command']
                self.show_inner_inline_message(cmd)
                cmd_status = self.BGREEN_ANSI + "Last Command Status: " + self.RESET_ANSI + status['status_code'].replace("_STATUS_", " ")
                self.show_inner_inline_message(cmd_status)
                cmd_details = self.BGREEN_ANSI + "Last Command Details: " + self.RESET_ANSI + status['description']
                self.show_inner_inline_message(cmd_details)
                cmd_time = self.BGREEN_ANSI + "Command Executed Time: " + self.RESET_ANSI + status['executed_time']
                self.show_inner_inline_message(cmd_time)

                for resource in resources:
                    output = resource.render_output(terraform_outputs)
                    if output:
                        display_op_list.append(output)

                self.display_op_msg(display_op_list)

    def initialize_classes(self, provider):
        """
        Identify and initialize the classes required for execution

        Args:
            provider (str): Provider name based on which corresponding classes are retrieved
        """
        self.input_class = getattr(importlib.import_module(
            provider.provider_module + '.input'), 'SystemStatusInput')
