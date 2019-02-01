from core.terraform.resources import TerraformResource
from core.config import Settings


class NullResource(TerraformResource):
    resource_instance_name = "null_resource"
    setup_time = 60
    available_args = {
    }
