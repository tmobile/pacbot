from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import vpc


class SecurityGroupResource(TerraformResource):
    """
    Base resource class for Terraform AWS security group resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_security_group"
    OUTPUT_LIST = ['id']
    setup_time = 600

    available_args = {
        'name': {'required': True, 'prefix': True, 'sep': '_'},
        'description': {'required': False},
        'vpc_id': {'required': True},
        'ingress': {
            'required': True,
            # 'inline_args': {
            #     'from_port': {'required': True},
            #     'to_port': {'required': True},
            #     'protocol': {'required': True},
            #     'cidr_blocks': {'required': True},
            # }
        },
        'egress': {
            'required': True,
            # 'inline_args': {
            #     'from_port': {'required': True},
            #     'to_port': {'required': True},
            #     'protocol': {'required': True},
            #     'cidr_blocks': {'required': True},
            # }
        },
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
            exists = vpc.check_security_group_exists(
                checked_details['value'],
                self.get_input_attr('vpc_id'),
                input.AWS_AUTH_CRED)

        return exists, checked_details
