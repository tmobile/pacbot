from core.utils import get_dir_file_names
from core.config import Settings
from core import constants as K
import os
import sys
import importlib


class Command:
    """
    This Base class identify and validate a command. If no valid command is found then lists the availabe commands

    Attributes:
        commands_dir_path (str): System commands directory where system commands are present
        custom_commands_dir_path (str): Custom commands directory where user defined commands are present
        mandatory_args (list): Mandatory arguments passed to CLI
        optional_args (list): Optional arguments passed to CLI
    """
    base_dir = 'commands'
    valid_arg_keys = [
        "--" + K.CATEGORY_FIELD_NAME,
        "--dry-run",
        "--silent"
    ]

    def __init__(self):
        self.commands_dir_path = self.get_core_commands_dir_path()
        self.custom_commands_dir_path = self.get_custom_commands_dir_path()
        self.parent_dir_name = self.commands_dir_path.split(os.sep)[-2]

    def get_command_class_instance(self, sys_argv):
        """
        Based on the command provided in the CLI identify the class instance corresponding to the command

        Args:
            sys_argv (dict): CLI Arguments supplied to the command

        Returns:
            command_class_instance (Command Instance):
                Returns commands/install::Install instance for install command
                Returns commands/destroy::Destroy instance for destroy command
                Returns commands/status::Status instance for status command
        """
        command_class = self.get_command_class_from_cli(sys_argv)

        self.mandatory_args = command_class.MANDATORY_ARGS
        self.optional_args = command_class.OPTIONAL_ARGS

        args_supplied = self.get_optional_args(sys_argv)
        command_class_instance = command_class(args_supplied)

        return command_class_instance

    def get_command_class_from_cli(self, sys_argv):
        """
        Based on the command provided in the CLI identify the class corresponding to the command

        Args:
            sys_argv (dict): CLI Arguments supplied to the command

        Returns:
            command_class (class):
                Returns commands/install::Install class for install command
                Returns commands/destroy::Destroy  class for destroy command
                Returns commands/status::Status class for status command
        """
        command_name = sys_argv[1] if len(sys_argv) > 1 else None

        valid_command = self.get_and_validate_command(command_name)
        if not valid_command:
            self.exit_system_showing_valid_commands()

        import_mod = valid_command['type'] + "." + self.base_dir + '.' + command_name

        command_module = importlib.import_module(import_mod)
        command_class = getattr(command_module, command_name.title())

        return command_class

    def get_optional_args(self, sys_argv):
        """
        Identify the optional aruments supplied by the user for the command

        Args:
            sys_argv (dict): CLI Arguments supplied to the command

        Returns:
            args_set (list): List of optional arguments supplied to the command
        """
        args_list = sys_argv[2:]
        args_set = []

        if args_list:
            args_set = []
            for item in args_list:
                splits = item.split('=')
                args_set.append((splits[0], True)) if len(splits) == 1 else args_set.append((splits[0], splits[1]))

            if not self.validate_optional_args(args_set):
                self.exit_system_showing_valid_optional_args()

            args_set = self._get_formatted_args(args_set)
        return args_set

    def _get_formatted_args(self, args_set):
        return [(arg[0].split('--')[1], arg[1])for arg in args_set]

    def exit_system_showing_valid_commands(self):
        """If the given command is not valid or if no command is supplied then exit execution with displaying the available commands"""
        print("**** Command Not Found *****\nValid Commands are")
        for item in self. get_valid_commands():
            print("   %s" % item)

        sys.exit()

    def get_and_validate_command(self, command_name):
        valid_commands = self.get_valid_commands()

        return valid_commands.get(command_name, None)

    def get_valid_commands(self):
        """
        Get all available valid commands

        Returns:
            commands_dict (dict): Available valid commands
        """
        core_command_file_names = get_dir_file_names(self.commands_dir_path)
        core_command_names = self.get_filtered_command_names(core_command_file_names)
        commands_dict = {name: {'type': "core"} for name in core_command_names}

        custom_command_file_names = get_dir_file_names(self.custom_commands_dir_path)
        custom_command_names = self.get_filtered_command_names(custom_command_file_names)
        commands_dict.update({name: {'type': "custom"} for name in custom_command_names})

        return commands_dict

    def get_filtered_command_names(self, file_names):
        """
        Get the command names from the files

        Args:
            file_names (list): List of file names of commands
        """
        def f1(x):
            return not x.startswith('__')

        def f2(x):
            return not x.startswith('.')

        def f3(x):
            return not x.endswith('__')

        def f4(x):
            return x.endswith('.py')

        return list(map(lambda x: x.split('.py')[0], filter(
            lambda x: all(f(x) for f in [f1, f2, f3, f4]), file_names)))

    def get_core_commands_dir_path(self):
        """
        To get absolute path of the system comamnd

        Returns:
            path (str): Absolute path of the command
        """
        return os.path.join(os.path.abspath(
            os.path.dirname(__file__)), self.base_dir)

    def get_custom_commands_dir_path(self):
        """
        To get absolute path of the user based comamnd

        Returns:
            path (str): Absolute path of the command
        """
        return os.path.join(Settings.BASE_APP_DIR, "custom", self.base_dir)

    def validate_optional_args(self, args_set):
        """
        Validate optional arguments supplied to the main command name

        Args:
            args_set (dict): Dict with key as parameter name and value as its value

        Returns:
            boolean: If valid arguments then True else False
        """
        for (key, val) in args_set:
            if not (key.startswith('--') and key in self.valid_arg_keys):
                return False

        return True

    def exit_system_showing_valid_optional_args(self):
        """Exit the execution as the optional parameter supplied is invalid"""
        print("Optional arguments supplied are not valid")
        sys.exit()
