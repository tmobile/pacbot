from abc import ABCMeta
from core.config import Settings
from core.mixins import MsgMixin
from core import constants as K
from core.providers.aws.boto3.sts import get_user_account_id


class SystemInput(MsgMixin, metaclass=ABCMeta):
    """Base input class for installation/destruction/status commands. This class reads required input from user for the process to start"""

    def read_input(self):
        """Read required inputs from user for the process to start"""
        self.show_step_heading(K.INPUT_READING_STARTED)
        self.read_aws_access_key()
        self.read_aws_secret_key()
        self.read_aws_region()
        self.load_aws_account_id()
        self.show_step_finish(K.INPUT_READING_COMPLETED)

    def read_aws_access_key(self):
        """Read AWS access key from user if it is not already set in settings"""
        settings_access_key = getattr(Settings, 'AWS_ACCESS_KEY', None)
        if settings_access_key is None or settings_access_key == '':
            self.aws_access_key = input("\n\t%s" % K.AWS_ACCESS_KEY_INPUT)
            if len(self.aws_access_key) < 20:
                self.show_step_inner_error("\n\t" + K.INVALID_KEY)
                raise Exception(K.INVALID_KEY)
            Settings.set('AWS_ACCESS_KEY', self.aws_access_key)
        else:
            self.aws_access_key = settings_access_key

    def read_aws_secret_key(self):
        """Read AWS secret key from user if it is not already set in settings"""
        settings_secret_key = getattr(Settings, 'AWS_SECRET_KEY', None)
        if settings_secret_key is None or settings_secret_key == '':
            self.aws_secret_key = input("\n\t%s" % K.AWS_SECRET_KEY_INPUT)

            if len(self.aws_secret_key) < 25:
                self.show_step_inner_error("\n\t" + K.INVALID_KEY)
                raise Exception(K.INVALID_KEY)

            Settings.set('AWS_SECRET_KEY', self.aws_secret_key)
        else:
            self.aws_secret_key = settings_secret_key

    def read_aws_region(self):
        """Read AWS region from user if it is not already set in settings"""
        settings_region = getattr(Settings, 'AWS_REGION', None)
        if settings_region is None or settings_region == '':
            self.aws_region = input("\n\t%s" % K.AWS_REGION_INPUT)
            Settings.set('AWS_REGION', self.aws_region)
        else:
            self.aws_region = settings_region

    def load_aws_account_id(self):
        """Find AWS Account ID from the credentials given"""
        aws_account_id = get_user_account_id(Settings.AWS_ACCESS_KEY, Settings.AWS_SECRET_KEY)
        Settings.set('AWS_ACCOUNT_ID', aws_account_id)
        self.aws_account_id = aws_account_id


class SystemInstallInput(SystemInput):
    """Input class for installation. This class reads required input from user for the process to start"""

    def read_input(self):
        super().read_input()


class SystemDestroyInput(SystemInput):
    """Input class for destruction. This class reads required input from user for the process to start"""

    def read_input(self):
        super().read_input()


class SystemStatusInput(SystemInput):
    """Input class for Status command. This class reads required input from user for the process to start"""

    def read_input(self):
        Settings.set('AWS_ACCESS_KEY', "TempAccessKey")
        Settings.set('AWS_SECRET_KEY', "TempSecretKey")
        Settings.set('AWS_REGION', "TempRegion")
        Settings.set('AWS_ACCOUNT_ID', "TempAccountId")
