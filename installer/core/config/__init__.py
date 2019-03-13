from core import constants as K
import importlib
import sys
import os


class Settings:
    """Framework settings config class which reads all the required variables from common and local configurations"""

    @classmethod
    def load_setings(cls, config_path):
        """
        Load settings variables from the given configuration path file

        Args:
            config_path (path): Path of the main configuration file
        """
        cls.set_class_attributes(config_path)
        cls.check_available_attrs_present()
        cls.set_system_required_settings()

    @classmethod
    def is_setting_variable(cls, setting):
        """
        Identify setting variable by checking the case. A variable will be considered as a settings only if its variable name
        contians uppercase letters

        Args:
            setting (str): Variable name
        """
        return True if setting.isupper() and not setting.startswith("__") and not setting.endswith("__") else False

    @classmethod
    def set_class_attributes(cls, config):
        """
        Identify the setting variables and attach that to as class property of this class

        Args:
            config (str): Config file
        """
        config_module = importlib.import_module(config)

        for setting in dir(config_module):
            if cls.is_setting_variable(setting):
                setattr(cls, setting, getattr(config_module, setting))

    @classmethod
    def set_system_required_settings(cls):
        """Framework related mandatory variables are set at this method"""
        setattr(cls, 'TERRAFORM_DIR', os.path.join(cls.DATA_DIR, 'terraform'))
        setattr(cls, 'OUTPUT_DIR', os.path.join(cls.DATA_DIR, 'output'))
        setattr(cls, 'OUTPUT_STATUS_DIR', os.path.join(cls.DATA_DIR, 'output', 'status'))
        setattr(cls, 'PYTHON_INTERPRETER', sys.executable)

    @classmethod
    def check_available_attrs_present(cls):
        """Check required Settings variables are available else exit the execution"""
        if not cls.check_required_dirs_are_available():
            sys.exit()

    @classmethod
    def get(cls, attr, default=None):
        """
        Return the setting variable value else default value if there is any

        Args:
            default (any): Default value if the settings variable is not set

        Returns:
            any: Value assigned to the setting variable
        """
        return getattr(cls, attr, default)

    @classmethod
    def set(cls, attr, value):
        """
        Set the valiue of setting variable with the value passed

        Args:
            value (any): Value passed to assign

        Returns:
            value (any): Value assigned to the setting variable
        """
        setattr(cls, attr, value)

        return cls.get(attr)

    @classmethod
    def check_required_dirs_are_available(cls):
        """
        Framework requires some mandatory directories. This method check whether all required dirs are available or not

        Returns:
            Boolean: Return True if all required dirs available else False
        """
        errors = []
        if cls.get('LOG_DIR', None) is None:
            errors.append(K.LOG_DIR_SETTINGS_NOT_FOUND)
        elif not os.path.isdir(cls.LOG_DIR):
            errors.append(K.LOG_DIR_NOT_FOUND)

        if cls.get('DATA_DIR', None) is None:
            errors.append(K.DATA_DIR_SETTINGS_NOT_FOUND)
        elif not os.path.isdir(cls.DATA_DIR):
            errors.append(K.DATA_DIR_NOT_FOUND)

        if cls.get('PROVISIONER_FILES_DIR_TO_COPY', None) is None:
            errors.append(K.PROVISIONER_FILES_DIR_SETTINGS_NOT_FOUND)
        elif not os.path.isdir(cls.PROVISIONER_FILES_DIR_TO_COPY):
            errors.append(K.PROVISIONER_FILES_DIR_NOT_FOUND)

        if errors:
            for error in errors:
                print("Error: %s" % error)
            return False

        return True
