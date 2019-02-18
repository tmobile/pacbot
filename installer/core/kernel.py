from core.command import Command
from core.config import Settings
from core.mixins import MsgMixin
from core.providers import Provider
from core.log import SysLog
from core.utils import run_command, exists_teraform_lock
from core import constants as K
import importlib
import sys


class Executor(MsgMixin):
    """
    This executes the the command which is provided in the CLI
    """
    def execute(self, command_class_instance):
        """
        Initialize and execute the command using the command class object

        Args:
            command_class_instance (Command obj): This object is decides the logic behind running the command
        """
        self.initialize()
        self.execute_command(command_class_instance)

    def initialize(self):
        pass

    def execute_command(self, command_class_instance):
        """
        Execute the command using the command class object

        Args:
            command_class_instance (Command obj): This object is decides the logic behind running the command
        """
        command_class_instance.execute(self.provider)

    def do_pre_requisite_check(self):
        """
        Before the execution starts, it checks for the pre-requisite. It would return either true or false

        Returns:
            Boolean: If the pre-requisite check passes then returns True else False
        """
        if self.is_another_process_running():
            return False

        if self._check_tools_are_available() and self._check_python_packages_are_available():
            if self.is_another_process_running():
                return False
            return True

        return False

    def is_another_process_running(self):
        """
        This method checks whether another Command is running currently

        Returns:
            Boolean: If another process is running then it returns True else False
        """
        if exists_teraform_lock():
            self.warn_another_process_running()
            return True

        return False

    def _check_tools_are_available(self):
        """
        Based on the settings variable TOOLS_REQUIRED, this method do validate all the tools

        Returns:
            Boolean: Return True if all tools are available else False
        """
        self.show_step_heading(K.TOOLS_CHECK_STARTED)
        tools_required = Settings.TOOLS_REQUIRED
        tools_available = True

        for tool_name, command in tools_required.items():
            error, msg, errmsg = run_command(command)
            display_message = "Tool: %s, checking" % tool_name
            status = K.NOT_FOUND if error or errmsg else K.FOUND
            tools_available = False if error or errmsg else tools_available
            self.show_step_inner_messaage(display_message, status, errmsg)

        if not tools_available:
            self.show_step_finish(K.ALL_TOOLS_NOT_AVAIALABLE, color=self.ERROR_ANSI)
            return False

        self.show_step_finish(K.TOOLS_CHECK_COMPLETED, color=self.GREEN_ANSI)

        return True

    def _check_python_packages_are_available(self):
        """
        Based on the settings variable PYTHON_PACKAGES_REQUIRED, this method do validate all the python packages

        Returns:
            Boolean: Return True if all python packages are available else False
        """
        self.show_step_heading(K.PIP_CHECK_STARTED)
        error = False
        for item in Settings.PYTHON_PACKAGES_REQUIRED:
            success, err_msg = self._module_available(item)
            status_msg = K.FOUND if success else K.NOT_FOUND
            if not success:
                error = True
            display_msg = "Package: %s, Module: %s" % (item[0], item[1]) if type(item) is tuple else "Module: %s" % item
            display_msg += ", checking"
            self.show_step_inner_messaage(display_msg, status_msg, err_msg)

        if error:
            self.show_step_finish(K.PIP_INSTALL_MSG, color=self.ERROR_ANSI)
            return False
        self.show_step_finish(K.PIP_CHECK_COMPLETED, color=self.GREEN_ANSI)

        return True

    def _module_available(self, item):
        """
        Based on the settings variable PYTHON_PACKAGES_REQUIRED, this method do validate all the python modules inside a package

        Returns:
            Boolean: Return True if all python modules are available else False
        """
        module_name = item
        try:
            if type(item) is tuple:
                module_name = item[0]
                module = importlib.import_module(item[0])
                if item[1] not in dir(module):
                    return False, "%s package doesn't have %s, " % item
            else:
                importlib.import_module(item)
        except:
            return False, None

        return True, None


class Kernel(Command, Executor):
    """
    Kernel module where the actual execution begins. Here system validation is done and settings/configurations are loaded.
    Starts running command if everything is successful

    Attributes:
        provider (Provider obj): The provider object which can be AWS, Azure etc
    """
    errors = []

    def __init__(self, config_path):
        """
        Constructor for the Kernel class, which do system validations and initialises Object attributes

        Args:
            config_path (str): This is the path to the main configuration/settings file
        """
        self.load_settings(config_path)
        provider_name = Settings.get('PROVIDER', None)
        self.provider = Provider(provider_name)
        self.do_system_validation()
        super().__init__()

    def do_system_validation(self):
        """Here the check for valid provider is done and passes the check if it is validelse exit the execution"""
        if not self.provider.valid:
            self.exit_with_provider_not_found()

    def run(self, sys_args):
        """
        Actual execution of the command is started from here

        Args:
            sys_args (dict): CLI Arguments supplied to the command
        """
        self.show_loading_messsage()
        Settings.set('running_command', ' '.join(sys_args))
        try:
            SysLog().debug_started(Settings.running_command)
            if self.do_pre_requisite_check():
                command_class_instance = self.get_command_class_instance(sys_args)  # Get the command list and optional commands
                self.execute(command_class_instance)
        except Exception as e:
            self.show_step_inner_error("Error occured, please check error log for more details")
            SysLog().write_error_log(str(e))

    def load_settings(self, config_path):
        """
        Load all the main and local configurations into the system

        Args:
            config_path (str): This is the path to the main configuration/settings file
        """
        Settings.load_setings(config_path)
