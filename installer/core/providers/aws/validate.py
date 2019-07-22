from abc import ABCMeta
from core.config import Settings
from core.providers.aws.boto3 import vpc
from core.providers.aws.boto3 import iam
from core import constants as K
from core.mixins import MsgMixin
import sys


class SystemValidation(MsgMixin, metaclass=ABCMeta):
    """
    Base class for system validation

    Attributes:
        error_message (str): Error message
        full_access_policies (str): Admin access policies with all permissions
    """
    error_message = None
    full_access_policies = ["AdministratorAccess"]

    def validate_vpc_and_cidr_blocks(self):
        """
        Check the VPC is correct and the CIDR block provided is also correct

        Returns:
            valid or not_valid (str): Configured string for valid and not valid conditions
        """
        self.error_message = None
        if Settings.get('VPC', None):
            vpc_ids = [Settings.VPC['ID']]
            cidr_blocks = Settings.VPC['CIDR_BLOCKS']
            try:
                vpcs = vpc.get_vpc_details(
                    vpc_ids,
                    Settings.AWS_AUTH_CRED
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
        """
        Check the subnets provided are present under the given VPC or not

        Returns:
            valid or not_valid (str): Configured string for valid and not valid conditions
        """
        self.error_message = None

        if Settings.get('VPC', None):
            vpc_ids = [Settings.VPC['ID']]
            if Settings.VPC.get('SUBNETS', None):
                if Settings.get('REQUIRE_SUBNETS_ON_DIFFERENT_ZONE', False):
                    subnet_ids = Settings.VPC['SUBNETS']
                    try:
                        valid_subnets = vpc.get_vpc_subnets(
                            vpc_ids,
                            Settings.AWS_AUTH_CRED
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

    def validate_policies(self):
        """
        Check required policies are present in in user or role

        Returns:
            boolean: True if all policies are present else False
        """
        aws_auth_option = Settings.AWS_AUTH_CRED['aws_auth_option']
        status = self.validate_user_policies() if aws_auth_option == 1 else self.validate_role_policies()

        if not status:
            yes_or_no = input("\n\t%s: " % self._input_message_in_color(K.POLICY_YES_NO))
            status = True if yes_or_no.lower() == "yes" else False

        return status

    def validate_user_policies(self):
        """
        Check required policies are present in user policies or not. Required policies are kept in the settings AWS_POLICIES_REQUIRED

        Returns:
            boolean: True if all policies are present else False
        """
        current_aws_user = iam.get_current_user(Settings.AWS_AUTH_CRED)
        user_name = current_aws_user.user_name

        if user_name:
            if self._check_user_policies(user_name) or self._check_group_policies(user_name):
                return True
        elif "root" in current_aws_user.arn:
            return True

        False

    def validate_role_policies(self):
        role_name = Settings.CALLER_ARN.split('/')[1]
        role_policy_names = iam.get_role_policy_names(role_name, Settings.AWS_AUTH_CRED)

        return self._check_required_policies_present(role_policy_names, K.CHECKING_ROLE_POLICY)

    def _check_group_policies(self, user_name):
        """
        Check required policies are present in user-group policies or not. Required policies are kept in the settings AWS_POLICIES_REQUIRED

        Returns:
            boolean: True if all policies are present else False
        """
        group_policy_names = iam.get_user_group_policy_names(user_name, Settings.AWS_AUTH_CRED)

        return self._check_required_policies_present(group_policy_names, K.CHECKING_GROUP_POLICY)

    def _check_user_policies(self, user_name):
        """
        This method uses the above methods and validate required policies are present in combine User and Group policies

        Returns:
            boolean: True if all policies are present else False
        """
        user_policy_names = iam.get_iam_user_policy_names(user_name, Settings.AWS_AUTH_CRED)

        return self._check_required_policies_present(user_policy_names, K.CHECKING_USER_POLICY)

    def _check_required_policies_present(self, policy_names, policy_type_msg):
        if self._has_full_access_policies(policy_names):
            self.show_step_inner_messaage(K.FULL_ACCESS_POLICY, K.PRESENT, None)
            return True

        if set(Settings.AWS_POLICIES_REQUIRED).difference(set(policy_names)):
            self.show_step_inner_messaage(policy_type_msg, K.NOT_PRESENT, self.error_message)
            return False

        self.show_step_inner_messaage(policy_type_msg, K.PRESENT, self.error_message)

        return True

    def _has_full_access_policies(self, policy_names):
        """
        Check if full access policies are present

        Returns:
            boolean: True if full access policies are present else False
        """
        return bool(set(self.full_access_policies).intersection(policy_names))


class SystemInstallValidation(SystemValidation):
    """Main class for validating install process"""
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

        status = self.validate_policies()

        return status


class SystemDestroyValidation(SystemValidation):
    """Main class for validating destroy process"""
    def validate(self):
        return True
