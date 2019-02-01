from abc import ABCMeta
from core.config import Settings
from core.providers.aws.boto3 import vpc
from core.providers.aws.boto3 import iam
from core import constants as K
from core.mixins import MsgMixin
import sys


class SystemValidation(MsgMixin, metaclass=ABCMeta):
    error_message = None
    full_access_policies = ["AdministratorAccess"]

    def validate_vpc_and_cidr_blocks(self):
        self.error_message = None
        if Settings.get('VPC', None):
            vpc_ids = [Settings.VPC['ID']]
            cidr_blocks = Settings.VPC['CIDR_BLOCKS']
            try:
                vpcs = vpc.get_vpc_details(
                    Settings.AWS_ACCESS_KEY,
                    Settings.AWS_SECRET_KEY,
                    Settings.AWS_REGION,
                    vpc_ids
                )
            except Exception as e:
                self.error_message = str(e) + "\n\t" + K.CORRECT_VPC_MSG
                return K.NOT_VALID

            valid_cidr_blocks = [vpc['CidrBlock'] for vpc in vpcs]
            if not set(cidr_blocks).issubset(set(valid_cidr_blocks)):
                self.error_message = K.INVALID_CIDR + "\n\t" + K.CORRECT_VPC_MSG
                return K.NOT_VALID

        return K.VALID

    def validate_subnet_ids(self):
        self.error_message = None

        if Settings.get('VPC', None):
            vpc_ids = [Settings.VPC['ID']]
            if Settings.VPC.get('SUBNETS', None):
                if Settings.get('REQUIRE_SUBNETS_ON_DIFFERENT_ZONE', False):
                    subnet_ids = Settings.VPC['SUBNETS']
                    try:
                        valid_subnets = vpc.get_vpc_subnets(
                            Settings.AWS_ACCESS_KEY,
                            Settings.AWS_SECRET_KEY,
                            Settings.AWS_REGION,
                            vpc_ids
                        )
                    except Exception as e:
                        self.error_message = str(e) + "\n\t" + K.CORRECT_VPC_MSG
                        return K.NOT_VALID

                    current_subnets = [subnet for subnet in valid_subnets if subnet['SubnetId'] in subnet_ids]
                    if len(current_subnets) != len(subnet_ids):
                        self.error_message = K.INVALID_SUBNETS
                        return K.NOT_VALID

                    if len(set([subnet['AvailabilityZone'] for subnet in current_subnets])) < 2:
                        self.error_message = K.INVALID_SUBNET_ZONES
                        return K.NOT_VALID

        return K.VALID

    def validate_user_policies(self):
        access_key, secret_key = Settings.AWS_ACCESS_KEY, Settings.AWS_SECRET_KEY
        user_name = iam.get_user_name(access_key, secret_key)

        # warning_message = "Policies (" + ", ".join(Settings.AWS_POLICIES_REQUIRED) + ") are required"
        # self.show_step_inner_warning(warning_message)

        if self._check_user_policies(access_key, secret_key, user_name):
            return True

        if self._check_group_policies(access_key, secret_key, user_name):
            return True

        yes_or_no = input("\n\t%s: " % self._input_message_in_color(K.POLICY_YES_NO))

        if yes_or_no.lower() == "yes":
            return True

        return False

    def _check_group_policies(self, access_key, secret_key, user_name):
        group_policy_names = iam.get_user_group_policy_names(access_key, secret_key, user_name)

        if self._has_full_access_policies(group_policy_names):
            self.show_step_inner_messaage(K.FULL_ACCESS_POLICY, K.PRESENT, None)
            return True

        if set(Settings.AWS_POLICIES_REQUIRED).difference(set(group_policy_names)):
            self.show_step_inner_messaage(K.CHECKING_GROUP_POLICY, K.NOT_PRESENT, self.error_message)
            return False

        self.show_step_inner_messaage(K.CHECKING_GROUP_POLICY, K.PRESENT, self.error_message)

        return True

    def _check_user_policies(self, access_key, secret_key, user_name):
        user_policy_names = iam.get_iam_user_policy_names(access_key, secret_key, user_name)

        if self._has_full_access_policies(user_policy_names):
            self.show_step_inner_messaage(K.FULL_ACCESS_POLICY, K.PRESENT, None)
            return True

        if set(Settings.AWS_POLICIES_REQUIRED).difference(set(user_policy_names)):
            self.show_step_inner_messaage(K.CHECKING_USER_POLICY, K.NOT_PRESENT, self.error_message)
            return False

        self.show_step_inner_messaage(K.CHECKING_USER_POLICY, K.PRESENT, self.error_message)

        return True

    def _has_full_access_policies(self, policy_names):
        return bool(set(self.full_access_policies).intersection(policy_names))


class SystemInstallValidation(SystemValidation):
    def validate(self):
        self.show_step_heading(K.SETTINGS_CHECK_STARTED)

        status = self.validate_vpc_and_cidr_blocks()
        self.show_step_inner_messaage(K.VPC_CHECK_STARTED, status, self.error_message)
        if status != K.VALID:
            return False

        status = self.validate_subnet_ids()
        self.show_step_inner_messaage(K.SUBNETS_CHECK_STARTED, status, self.error_message)
        if status != K.VALID:
            return False

        status = self.validate_user_policies()

        return status


class SystemDestroyValidation(SystemValidation):
    def validate(self):
        return True
