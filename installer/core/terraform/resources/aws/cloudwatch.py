from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import cloudwatch_log
from core.providers.aws.boto3 import cloudwatch_event


class CloudWatchEventRuleResource(TerraformResource):
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
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = cloudwatch_event.check_rule_exists(
                checked_details['value'],
                input.aws_access_key,
                input.aws_secret_key,
                input.aws_region)

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
    resource_instance_name = "aws_cloudwatch_log_group"
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '/'},
        'name_prefix': {'required': False},
        'retention_in_days': {'required': False},
        'tags': {'required': False}
    }

    def check_exists_before(self, input, tf_outputs):
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = cloudwatch_log.check_log_group_exists(
                checked_details['value'],
                input.aws_access_key,
                input.aws_secret_key,
                input.aws_region)

        return exists, checked_details


class CloudWatchLogResourcePolicy(TerraformResource):
    resource_instance_name = "aws_cloudwatch_log_resource_policy"
    available_args = {
        'policy_name': {'required': True, 'prefix': True, 'sep': '/'},
        'policy_document': {'required': True}
    }
