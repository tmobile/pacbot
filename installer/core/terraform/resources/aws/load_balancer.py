from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import elb


class LoadBalancerResource(TerraformResource):
    resource_instance_name = "aws_lb"
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '-'},
        'internal': {'required': True},
        'load_balancer_type': {'required': True},
        'security_groups': {'required': False},
        'subnets': {'required': False},
        'tags': {'required': False}
    }

    def check_exists_before(self, input, tf_outputs):
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = elb.check_alb_exists(
                checked_details['value'],
                input.aws_access_key,
                input.aws_secret_key,
                input.aws_region)

        return exists, checked_details


class ALBListenerResource(TerraformResource):
    resource_instance_name = "aws_alb_listener"
    available_args = {
        'load_balancer_arn': {'required': True},
        'protocol': {'required': True},
        'port': {'required': True},
        'default_action': {
            'required': False,
            'inline_args': {
                'default_action_target_group_arn': {'required': True, 'tf_arg_key': 'target_group_arn'},
                'default_action_type': {'required': True, 'tf_arg_key': 'type'},
            }
        },

    }


class ALBListenerRuleResource(TerraformResource):
    resource_instance_name = "aws_lb_listener_rule"
    available_args = {
        'listener_arn': {'required': True},
        'priority': {'required': False},
        'action': {
            'required': True,
            'inline_args': {
                'action_target_group_arn': {'required': True, 'tf_arg_key': 'target_group_arn'},
                'action_type': {'required': True, 'tf_arg_key': 'type'}
            }
        },
        'condition': {
            'required': True,
            'inline_args': {
                'condition_field': {'required': True, 'tf_arg_key': 'field'},
                'condition_values': {'required': True, 'tf_arg_key': 'values'}
            }
        }
    }


class ALBTargetGroupResource(TerraformResource):
    resource_instance_name = "aws_alb_target_group"
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '-'},
        'port': {'required': True},
        'protocol': {'required': True},
        'vpc_id': {'required': False},
        'target_type': {'required': False},
        'lifecycle': {
            'required': True,
            'inline_args': {
                'create_before_destroy': {'required': True}
            }
        },
        'health_check': {
            'required': True,
            'inline_args': {
                'path': {'required': True},
                'interval': {'required': True},
                'timeout': {'required': True},
                'matcher': {'required': True}
            }
        },
        'tags': {'required': False}
    }

    def check_exists_before(self, input, tf_outputs):
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = elb.check_target_group_exists(
                checked_details['value'],
                input.aws_access_key,
                input.aws_secret_key,
                input.aws_region)

        return exists, checked_details
