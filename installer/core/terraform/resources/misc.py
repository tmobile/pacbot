from core.terraform.resources import TerraformResource
from core.config import Settings


class NullResource(TerraformResource):
    """
    Base resource class for Terraform Null resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "null_resource"
    available_args = {
        'triggers': {'required': False},
    }
