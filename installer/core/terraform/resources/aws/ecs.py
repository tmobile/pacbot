from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import ecs


class ECSClusterResource(TerraformResource):
    """
    Base resource class for Terraform AWS ECS cluster resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_ecs_cluster"
    setup_time = 600
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': "-"},
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
            exists = ecs.check_ecs_cluster_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details


class ECSTaskDefinitionResource(TerraformResource):
    """
    Base resource class for Terraform AWS ECS task definition resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_ecs_task_definition"
    setup_time = 600
    available_args = {
        'family': {'required': True, 'prefix': True, 'sep': "-"},
        'container_definitions': {'required': True},
        'requires_compatibilities': {'required': True},
        'network_mode': {'required': True},
        'cpu': {'required': True},
        'memory': {'required': True},
        'execution_role_arn': {'required': True},
        'task_role_arn': {'required': True},
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
        checked_details = {'attr': "name", 'value': self.get_input_attr('family')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = ecs.check_ecs_task_definition_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details


class ECSServiceResource(TerraformResource):
    """
    Base resource class for Terraform AWS ECS service resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_ecs_service"
    setup_time = 600
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': "-"},
        'task_definition': {'required': True},
        'desired_count': {'required': True},
        'launch_type': {'required': True},
        'cluster': {'required': True},
        'network_configuration': {
            'required': True,
            'inline_args': {
                'network_configuration_security_groups': {'required': True, 'tf_arg_key': "security_groups"},
                'network_configuration_subnets': {'required': True, 'tf_arg_key': "subnets"},
                'network_configuration_assign_public_ip': {'required': True, 'tf_arg_key': "assign_public_ip"},
            }
        },
        'load_balancer': {
            'required': True,
            'inline_args': {
                'load_balancer_target_group_arn': {'required': True, 'tf_arg_key': "target_group_arn"},
                'load_balancer_container_name': {'required': True, 'tf_arg_key': "container_name"},
                'load_balancer_container_port': {'required': True, 'tf_arg_key': "container_port"},
            }
        },
        'tags': {'required': False},
        'propagate_tags': {'required': False}
    }
