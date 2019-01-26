from core.terraform.resources.misc import NullResource
from resources.s3.bucket import BucketStorage
from core.config import Settings
from core.log import SysLog
import shutil
import boto3
import os


class UploadTrraform(NullResource):
    PROCESS = False

    def post_terraform_apply(self):
        archive_type = "zip"
        s3_client = boto3.client(
            "s3",
            region_name=Settings.AWS_REGION,
            aws_access_key_id=Settings.AWS_ACCESS_KEY,
            aws_secret_access_key=Settings.AWS_SECRET_KEY)

        zip_file_name = Settings.RESOURCE_NAME_PREFIX + "-terraform-installer-backup"
        zip_file_abs_path = os.path.join(Settings.BASE_APP_DIR, zip_file_name)
        dir_to_archive = Settings.DATA_DIR
        SysLog().write_debug_log("Started Archiving Terraform Directory")
        shutil.make_archive(zip_file_abs_path, archive_type, dir_to_archive)
        SysLog().write_debug_log("Completed Archiving")

        bucket_name = BucketStorage.get_input_attr('bucket')
        zip_file_name = zip_file_name + ".zip"
        zip_file_abs_path = zip_file_abs_path + ".zip"
        SysLog().write_debug_log("Started Uploading Archived Terraform(Zip File: %s) into S3 Bucket(Name: %s)" % (zip_file_abs_path, bucket_name))
        s3_client.upload_file(
            zip_file_abs_path,
            bucket_name,
            zip_file_name)

        os.remove(zip_file_abs_path)
