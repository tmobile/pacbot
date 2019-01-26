from core.terraform.resources.aws.s3 import S3Bucket
from core.config import Settings


class BucketStorage(S3Bucket):
    bucket = "data-" + Settings.AWS_REGION + "-" + Settings.AWS_ACCOUNT_ID
    acl = "private"
    force_destroy = True
