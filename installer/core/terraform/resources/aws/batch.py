from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import batch


class BatchComputeEnvironmentResource(TerraformResource):
    """
    Base resource class for Terraform AWS Batch compute environment resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_batch_compute_environment"
    available_args = {
        'compute_environment_name': {'required': True, 'prefix': True, 'sep': '-'},
        'compute_resources': {
            'required': True,
            'inline_args': {
                'instance_role': {'required': True},
                'instance_type': {'required': True},
                'max_vcpus': {'required': True},
                'min_vcpus': {'required': True},
                'desired_vcpus': {'required': False},
                'ec2_key_pair': {'required': False, 'prefix': True, 'sep': '_'},
                'security_group_ids': {'required': True},
                'subnets': {'required': True},
                'resource_type': {'required': True, 'tf_arg_key': "type"},
                'compute_resources_tags': {'required': False, 'tf_arg_key': "tags"}
            }
        },
        'service_role': {'required': True},
        'env_type': {'required': True, 'tf_arg_key': "type"},
        'ecs_cluster_arn': {'required': False, 'prefix': True, 'sep': '-'},
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
        checked_details = {'attr': "name", 'value': self.get_input_attr('compute_environment_name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = batch.check_compute_env_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details


class BatchJobDefinitionResource(TerraformResource):
    """
    Base resource class for Terraform AWS Batch job definition resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_batch_job_definition"
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '-'},
        'jd_type': {'required': True, 'tf_arg_key': "type"},
        'retry_strategy': {
            'required': True,
            'inline_args': {
                'attempts': {'required': True},
            }
        },
        'container_properties': {'required': True},
        'parameters': {'required': False}
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
            exists = batch.check_job_definition_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details


class BatchJobQueueResource(TerraformResource):
    """
    Base resource class for Terraform AWS Batch job queue resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_batch_job_queue"
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '-'},
        'state': {'required': True},
        'priority': {'required': True},
        'compute_environments': {'required': True}
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
            exists = batch.check_job_queue_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details
