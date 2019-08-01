from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import ecr


class ECRRepository(TerraformResource):
    """
    Base resource class for Terraform AWS ECR resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_ecr_repository"
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '-'},
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
            exists = ecr.check_ecr_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details
