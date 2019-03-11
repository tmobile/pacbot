from core.terraform.resources import TerraformData


class AwsCallerIdData(TerraformData):
    """
    Base resource class for Terraform AWS Caller ID data resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_caller_identity"
    available_args = {
    }


class AwsRegionData(TerraformData):
    """
    Base resource class for Terraform AWS region data resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_region"
    available_args = {
    }
