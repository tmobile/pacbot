from abc import ABCMeta
from core.config import Settings
from core.mixins import MsgMixin
from core import constants as K
from core.providers.aws.boto3.sts import get_aws_caller_identity
from core.providers.aws.boto3.sts import generate_temp_credentials
import uuid


class SystemInput(MsgMixin, metaclass=ABCMeta):
    """Base input class for installation/destruction/status commands. This class reads required input from user for the process to start"""
    AWS_AUTH_CRED = {}

    def __init__(self, silent_install=False):
        self.silent_install = silent_install

    def read_input(self):
        """Read required inputs from user for the process to start"""
        self.show_step_heading(K.INPUT_READING_STARTED)

        self.AWS_AUTH_CRED['aws_auth_option'] = self.read_aws_auth_mechanism()
        self.AWS_AUTH_CRED['aws_region'] = self.read_aws_region()

        if self.AWS_AUTH_CRED['aws_auth_option'] == 1:
            self.AWS_AUTH_CRED['aws_access_key'] = self.read_aws_access_key()
            self.AWS_AUTH_CRED['aws_secret_key'] = self.read_aws_secret_key()
        elif self.AWS_AUTH_CRED['aws_auth_option'] == 2:
            self.AWS_AUTH_CRED['assume_role_arn'] = self.read_aws_assume_role_arn()
            self.AWS_AUTH_CRED['tmp_credentials'] = generate_temp_credentials(
                self.AWS_AUTH_CRED['assume_role_arn'],
                self.AWS_AUTH_CRED['aws_region']
            )

        Settings.set('AWS_AUTH_CRED', self.AWS_AUTH_CRED)

        self.load_aws_account_details()
        self.show_step_finish(K.INPUT_READING_COMPLETED)

    def read_aws_auth_mechanism(self):
        if self.silent_install:
            auth_mechanism = getattr(Settings, 'AWS_AUTH_MECHANISM', None)
            if auth_mechanism in [1, 2, 3]:
                return auth_mechanism
            self.show_step_inner_error(K.AWS_AUTH_MECHANISM_NOT_SUPPLIED)
            raise Exception(K.AWS_AUTH_MECHANISM_NOT_SUPPLIED)

        while True:
            self.show_inner_inline_message("\n\t%s" % K.AWS_AUTH_MECHANISM)
            self.show_inner_inline_message("\n\t%s" % K.AWS_WITH_KEYS)
            self.show_inner_inline_message("\n\t%s" % K.AWS_WITH_ASSUME_ROLE)
            self.show_inner_inline_message("\n\t%s" % K.AWS_WITH_EC2_ROLE)
            auth_mechanism = int(input("\n\t%s" % K.AWS_CHOOSE_AUTH_OPTION))
            if auth_mechanism in [1, 2, 3]:
                break

            self.show_step_inner_warning(K.AWS_INCORRECT_MECHANISM)

        return auth_mechanism

    def read_aws_access_key(self):
        """Read AWS access key from user if it is not already set in settings"""
        settings_access_key = getattr(Settings, 'AWS_ACCESS_KEY', None)

        if settings_access_key is None or settings_access_key == '':
            if self.silent_install:
                self.show_step_inner_error(K.AWS_ACCESS_KEY_NOT_SUPPLIED)
                raise Exception(K.AWS_ACCESS_KEY_NOT_SUPPLIED)

            aws_access_key = input("\n\t%s" % K.AWS_ACCESS_KEY_INPUT)
            if len(aws_access_key) < 20:
                self.show_step_inner_error("\n\t" + K.INVALID_KEY)
                raise Exception(K.INVALID_KEY)
        else:
            aws_access_key = settings_access_key

        return aws_access_key

    def read_aws_secret_key(self):
        """Read AWS secret key from user if it is not already set in settings"""
        settings_secret_key = getattr(Settings, 'AWS_SECRET_KEY', None)
        if settings_secret_key is None or settings_secret_key == '':
            if self.silent_install:
                self.show_step_inner_error(K.AWS_SECRET_KEY_NOT_SUPPLIED)
                raise Exception(K.AWS_SECRET_KEY_NOT_SUPPLIED)

            aws_secret_key = input("\n\t%s" % K.AWS_SECRET_KEY_INPUT)
            if len(aws_secret_key) < 25:
                self.show_step_inner_error("\n\t" + K.INVALID_KEY)
                raise Exception(K.INVALID_KEY)
        else:
            aws_secret_key = settings_secret_key

        return aws_secret_key

    def read_aws_assume_role_arn(self):
        """Read AWS secret key from user if it is not already set in settings"""
        settings_assume_role_arn = getattr(Settings, 'AWS_ASSUME_ROLE_ARN', None)
        if settings_assume_role_arn is None or settings_assume_role_arn == '':
            if self.silent_install:
                self.show_step_inner_error(K.AWS_ASSUME_ROLE_NOT_SUPPLIED)
                raise Exception(K.AWS_ASSUME_ROLE_NOT_SUPPLIED)

            assume_role_arn = input("\n\t%s" % K.AWS_ASSUME_ROLE_INPUT)
        else:
            assume_role_arn = settings_assume_role_arn

        return assume_role_arn

    def read_aws_region(self):
        """Read AWS region from user if it is not already set in settings"""
        settings_region = getattr(Settings, 'AWS_REGION', None)
        if settings_region is None or settings_region == '':
            if self.silent_install:
                self.show_step_inner_error(K.AWS_REGION_NOT_SUPPLIED)
                raise Exception(K.AWS_REGION_NOT_SUPPLIED)

            aws_region = input("\n\t%s" % K.AWS_REGION_INPUT)
        else:
            aws_region = settings_region

        Settings.set('AWS_REGION', aws_region)

        return aws_region

    def load_aws_account_details(self):
        """Find AWS Account ID from the credentials given"""
        caller_identity = get_aws_caller_identity(self.AWS_AUTH_CRED)
        Settings.set('AWS_ACCOUNT_ID', caller_identity.get('Account'))
        Settings.set('CALLER_ARN', caller_identity.get('Arn'))
        self.AWS_ACCOUNT_ID = caller_identity.get('Account')
        self.CALLER_ARN = caller_identity.get('Arn')


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
