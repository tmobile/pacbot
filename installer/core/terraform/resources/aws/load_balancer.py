from core.terraform.resources import TerraformResource
from core.providers.aws.boto3 import elb
from core.config import Settings


class LoadBalancerResource(TerraformResource):
    """
    Base resource class for Terraform AWS ELB resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
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
            exists = elb.check_alb_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details


class ALBListenerResource(TerraformResource):
    """
    Base resource class for Terraform AWS ELB listener resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
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
        'ssl_policy': {'required': False},
        'certificate_arn': {'required': False}
    }

    def validate_input_args(self):
        """
        Check protocol is HTTPS then validate certificate ARN. If not Or correct ARN then fallback to original validation

        Returns:
            success (boolean): Validation is success or not
            msg_list (list): List of validation messages
        """
        if self.protocol == "HTTPS":
            if not Settings.get('SSL_CERTIFICATE_ARN', None):
                return False, ["Certifcate ARN is not found for ELB SSL Policy"]

        return super().validate_input_args()


class ALBListenerRuleResource(TerraformResource):
    """
    Base resource class for Terraform AWS ELB Listener rule resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
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
    """
    Base resource class for Terraform AWS ELB target group resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
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
            exists = elb.check_target_group_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details
