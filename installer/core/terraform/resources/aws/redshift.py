from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import redshift


class RedshiftClusterResource(TerraformResource):
    """
    Base resource class for Terraform AWS Redshift resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_redshift_cluster"
    OUTPUT_LIST = ['endpoint']
    setup_time = 600
    available_args = {
        'cluster_identifier': {'required': True, 'prefix': True, 'sep': '-'},
        'database_name': {'required': True},
        'master_username': {'required': True},
        'master_password': {'required': True},
        'node_type': {'required': True},
        'cluster_type': {'required': True},
        'number_of_nodes': {'required': True},
        'skip_final_snapshot': {'required': True},
        'publicly_accessible': {'required': True},
        'vpc_security_group_ids': {'required': True},
        'cluster_parameter_group_name': {'required': False},
        'cluster_subnet_group_name': {'required': False},
        'tags': {'required': False}
    }

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
        checked_details = {'attr': "cluster_identifier", 'value': self.get_input_attr('cluster_identifier')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = redshift.check_redshift_cluster_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details


class RedshiftParameterGroupResource(TerraformResource):
    """
    Base resource class for Terraform AWS Redshift parameter group resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_redshift_parameter_group"
    setup_time = 60
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '-'},
        'family': {'required': True},
        'description': {'required': False},
        'parameter': {'required': False}
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
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = redshift.check_redshift_parameter_group_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details


class RedshiftSubnetGroupResource(TerraformResource):
    """
    Base resource class for Terraform AWS Redshift Subnet group resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_redshift_subnet_group"
    setup_time = 60
    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '-'},
        'subnet_ids': {'required': True},
        'description': {'required': False},
        'tags': {'required': False},
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
        checked_details = {'attr': "name", 'value': self.get_input_attr('name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = redshift.check_redshift_subnet_group_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details
