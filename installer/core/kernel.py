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
    def execute(self, command_class_instance):
        self.initialize()
        self.execute_command(command_class_instance)

    def initialize(self):
        pass

    def execute_command(self, command_class_instance):
        command_class_instance.execute(self.provider)

    def do_pre_requisite_check(self):
        if self.is_another_process_running():
            return False

        if self._check_tools_are_available() and self._check_python_packages_are_available():
            if self.is_another_process_running():
                return False
            return True

        return False

    def is_another_process_running(self):
        if exists_teraform_lock():
            self.warn_another_process_running()
            return True

        return False

    def _check_tools_are_available(self):
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
    errors = []

    def __init__(self, config_path):
        self.load_settings(config_path)
        provider_name = Settings.get('PROVIDER', None)
        self.provider = Provider(provider_name)
        self.do_system_validation()
        super().__init__()

    def do_system_validation(self):
        if not self.provider.valid:
            self.exit_with_provider_not_found()

    def run(self, sys_args):
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
        Settings.load_setings(config_path)
