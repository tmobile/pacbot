from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import rds


class RDSResource(TerraformResource):
    resource_instance_name = "aws_db_instance"
    OUTPUT_LIST = ['endpoint']
    setup_time = 600
    available_args = {
        'identifier': {'required': True, 'prefix': True, 'sep': '-'},
        'allocated_storage': {'required': True},
        'storage_type': {'required': True},
        'engine': {'required': True},
        'engine_version': {'required': True, },
        'instance_class': {'required': True, },
        'name': {'required': True},
        'username': {'required': True},
        'password': {'required': True},
        'db_subnet_group_name': {'required': False},
        'option_group_name': {'required': False},
        'skip_final_snapshot': {'required': True},
        'parameter_group_name': {'required': False},
        'vpc_security_group_ids': {'required': False},
        'final_snapshot_identifier': {'required': False},
        'tags': {'required': False}
    }

    def check_exists_before(self, input, tf_outputs):
        checked_details = {'attr': "identifier", 'value': self.get_input_attr('identifier')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = rds.check_rds_instance_exists(
                checked_details['value'],
                input.aws_access_key,
                input.aws_secret_key,
                input.aws_region)

        return exists, checked_details


class RDSOptionGroupResource(TerraformResource):
    resource_instance_name = "aws_db_option_group"
    setup_time = 60
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '-'},
        'engine_name': {'required': True},
        'major_engine_version': {'required': True},
        'option_group_description': {'required': False},
        'tags': {'required': False}
    }

    option_group_description = Settings.RESOURCE_DESCRIPTION

    def check_exists_before(self, input, tf_outputs):
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = rds.check_rds_option_group_exists(
                checked_details['value'],
                input.aws_access_key,
                input.aws_secret_key,
                input.aws_region)

        return exists, checked_details


class RDSParameterGroupResource(TerraformResource):
    resource_instance_name = "aws_db_parameter_group"
    setup_time = 60
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '-'},
        'family': {'required': True},
        'description': {'required': False},
        'tags': {'required': False}
    }

    description = Settings.RESOURCE_DESCRIPTION

    def check_exists_before(self, input, tf_outputs):
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = rds.check_rds_parameter_group_exists(
                checked_details['value'],
                input.aws_access_key,
                input.aws_secret_key,
                input.aws_region)

        return exists, checked_details


class RDSSubnetGroupResource(TerraformResource):
    resource_instance_name = "aws_db_subnet_group"
    setup_time = 60
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '-'},
        'subnet_ids': {'required': True},
        'description': {'required': False},
        'tags': {'required': False}
    }

    description = Settings.RESOURCE_DESCRIPTION

    def check_exists_before(self, input, tf_outputs):
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = rds.check_rds_subnet_group_exists(
                checked_details['value'],
                input.aws_access_key,
                input.aws_secret_key,
                input.aws_region)

        return exists, checked_details
