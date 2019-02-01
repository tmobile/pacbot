from core.terraform.resources import TerraformResource, TerraformData
from core.config import Settings
from core.providers.aws.boto3 import iam


class IAMRoleResource(TerraformResource):
    resource_instance_name = "aws_iam_role"
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '_'},
        'assume_role_policy': {'required': False},
        'arn': {'required': False},
        'path': {'required': False},
        'unique_id': {'required': False},
        'permissions_boundary': {'required': False},
        'description': {'required': False},
        'force_detach_policies': {'required': False},
        'tags': {'required': False}
    }

    description = Settings.RESOURCE_DESCRIPTION

    def check_exists_before(self, input, tf_outputs):
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = iam.check_role_exists(checked_details['value'], input.aws_access_key, input.aws_secret_key)

        return exists, checked_details


class IAMRolePolicyResource(TerraformResource):
    resource_instance_name = "aws_iam_policy"
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '_'},
        'policy': {'required': True},
        'path': {'required': False},
        'arn': {'required': False},
        'description': {'required': False}
    }

    description = Settings.RESOURCE_DESCRIPTION

    def check_exists_before(self, input, tf_outputs):
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False
        if not self.resource_in_tf_output(tf_outputs):
            exists = iam.check_policy_exists(checked_details['value'], input.aws_access_key, input.aws_secret_key, input.aws_account_id)

        return exists, checked_details


class IAMRolePolicyAttachmentResource(TerraformResource):
    resource_instance_name = "aws_iam_role_policy_attachment"
    available_args = {
        'role': {'required': True},
        'policy_arn': {'required': True},
    }


class IAMInstanceProfileResource(TerraformResource):
    resource_instance_name = "aws_iam_instance_profile"
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '_'},
        'role': {'required': True},
    }

    def check_exists_before(self, input, tf_outputs):
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False
        if not self.resource_in_tf_output(tf_outputs):
            exists = iam.check_instance_profile_exists(checked_details['value'], input.aws_access_key, input.aws_secret_key)

        return exists, checked_details


class IAMPolicyDocumentData(TerraformData):
    resource_instance_name = "aws_iam_policy_document"
    available_args = {
        'statement': {'required': True},
    }


class IamServiceLinkedRole(TerraformResource):
    resource_instance_name = "aws_iam_service_linked_role"
    available_args = {
        'aws_service_name': {'required': True},
        'custom_suffix ': {'required': False},
        'description': {'required': False},
    }

    description = Settings.RESOURCE_DESCRIPTION
