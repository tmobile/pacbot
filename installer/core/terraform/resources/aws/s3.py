from core.terraform.resources import TerraformResource
from core.config import Settings
import os


class S3Bucket(TerraformResource):
    """
    Base resource class for Terraform AWS S3 bucket resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_s3_bucket"
    available_args = {
        'bucket': {'required': True, 'prefix': True, 'sep': '-'},
        'acl': {'required': True},
        'policy': {'required': False},
        'force_destroy': {'required': False},
        'tags': {'required': False},
    }


class S3BucketObject(TerraformResource):
    """
    Base resource class for Terraform AWS S3 bucket object resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_s3_bucket_object"
    skip_source_exists_check = False
    available_args = {
        'bucket': {'required': True},
        'key': {'required': True},
        'source': {'required': True},
        'acl': {'required': False},
        'etag': {'required': False},
        'tags': {'required': False}
    }

    def pre_terraform_apply(self):
        if not os.path.exists(self.source) and self.skip_source_exists_check is not True:
            raise Exception("Source object not found for S3 upload. Source: %s, TF-Resource: %s" % (self.source, self.get_resource_id()))
