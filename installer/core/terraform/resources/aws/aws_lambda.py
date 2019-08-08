from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import aws_lambda


class LambdaFunctionResource(TerraformResource):
    """
    Base resource class for Terraform AWS Lambda function resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_lambda_function"
    available_args = {
        'function_name': {'required': True, 'prefix': True, 'sep': '-'},
        'filename': {'required': False},
        'role': {'required': True},
        'handler': {'required': True},
        'runtime': {'required': True},
        's3_bucket': {'required': True, },
        's3_key': {'required': True, },
        'environment': {'required': True},
        'description': {'required': False},
        'tags': {'required': False}
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
        checked_details = {'attr': "function_name", 'value': self.get_input_attr('function_name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = aws_lambda.check_function_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details


class LambdaPermission(TerraformResource):
    """
    Base resource class for Terraform AWS Lambda permission resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_lambda_permission"
    available_args = {
        'action': {'required': True},
        'function_name': {'required': True},
        'principal': {'required': True},
        'source_arn': {'required': False},
        'statement_id': {'required': False}
    }
