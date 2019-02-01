from core.terraform.resources import TerraformData


class AwsCallerIdData(TerraformData):
    resource_instance_name = "aws_caller_identity"
    available_args = {
    }


class AwsRegionData(TerraformData):
    resource_instance_name = "aws_region"
    available_args = {
    }
