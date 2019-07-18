from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import cloudwatch_log
from core.providers.aws.boto3 import cloudwatch_event


class CloudWatchEventRuleResource(TerraformResource):
    """
    Base resource class for Terraform AWS Cloudwatch event rule resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_cloudwatch_event_rule"
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '-'},
        'schedule_expression': {'required': True},
        'event_pattern': {'required': False},
        'role_arn ': {'required': False},
        'is_enabled ': {'required': False},
        'description': {'required': False}
    }
    description = Settings.RESOURCE_DESCRIPTION

    def check_exists_before(self, input, tf_outputs):
        """
        Check if the resource is already exists in AWS

        Args:
            input (instance): input object
            tf_outputs (dict): Terraform output dictionary

        Returns:
            exists (boolean): True if already exists in AWS else False
            checked_details (dict): Status of the existence check
        """
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = cloudwatch_event.check_rule_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details


class CloudWatchEventTargetResource(TerraformResource):
    resource_instance_name = "aws_cloudwatch_event_target"
    available_args = {
        'rule': {'required': True},
        'target_id': {'required': True},
        'arn': {'required': False},
        'target_input': {'required': False, 'tf_arg_key': 'input'},
        'run_command_targets': {'required': False}
    }


class CloudWatchLogGroupResource(TerraformResource):
    """
    Base resource class for Terraform AWS Cloudwatch log group resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_cloudwatch_log_group"
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '/'},
        'name_prefix': {'required': False},
        'retention_in_days': {'required': False},
        'tags': {'required': False}
    }

    def check_exists_before(self, input, tf_outputs):
        """
        Check if the resource is already exists in AWS

        Args:
            input (instance): input object
            tf_outputs (dict): Terraform output dictionary

        Returns:
            exists (boolean): True if already exists in AWS else False
            checked_details (dict): Status of the existence check
        """
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = cloudwatch_log.check_log_group_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details


class CloudWatchLogResourcePolicy(TerraformResource):
    """
    Base resource class for Terraform AWS Cloudwatch log policy resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_cloudwatch_log_resource_policy"
    available_args = {
        'policy_name': {'required': True, 'prefix': True, 'sep': '/'},
        'policy_document': {'required': True}
    }
