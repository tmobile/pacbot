from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import ecr


class ECRRepository(TerraformResource):
    resource_instance_name = "aws_ecr_repository"
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '-'},
        'tags': {'required': False}
    }

    def check_exists_before(self, input, tf_outputs):
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = ecr.check_ecr_exists(
                checked_details['value'],
                input.aws_access_key,
                input.aws_secret_key,
                input.aws_region)

        return exists, checked_details
