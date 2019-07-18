from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import es


class ElasticsearchDomainResource(TerraformResource):
    """
    Base resource class for Terraform AWS ES Domain resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_elasticsearch_domain"
    OUTPUT_LIST = ['endpoint', 'kibana_endpoint']
    setup_time = 600

    available_args = {
        'domain_name': {'required': True, 'prefix': True, 'sep': '-'},
        'elasticsearch_version': {'required': True},
        'tags': {'required': False},
        'cluster_config': {
            'required': True,
            'inline_args': {
                'instance_type': {'required': True},
                'instance_count': {'required': True},
                'dedicated_master_enabled': {'required': True},
                'zone_awareness_enabled': {'required': True},
            }
        },
        'ebs_options': {
            'required': True,
            'inline_args': {
                'ebs_enabled': {'required': True},
                'volume_type': {'required': True},
                'volume_size': {'required': True},
            }
        },
        'vpc_options': {
            'required': True,
            'inline_args': {
                'security_group_ids': {'required': True},
                'subnet_ids': {'required': True}
            }
        },
        'snapshot_options': {
            'required': False,
            'inline_args': {
                'automated_snapshot_start_hour': {'required': True}
            }
        },
        'access_policies': {'required': False, },
        'log_publishing_options': {
            'required': False,
            'inline_args': {
                'cloudwatch_log_group_arn': {'required': True},
                'log_type': {'required': True},
            }
        }
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
        checked_details = {'attr': "domain_name", 'value': self.get_input_attr('domain_name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = es.check_es_domain_exists(
                checked_details['value'],
                input.AWS_AUTH_CRED)

        return exists, checked_details


class ElasticsearchDomainPolicyResource(TerraformResource):
    """
    Base resource class for Terraform AWS ES Domain policy resource

    Attributes:
        resource_instance_name (str): Type of resource instance
        available_args (dict): Instance configurations
    """
    resource_instance_name = "aws_elasticsearch_domain_policy"
    setup_time = 60
    available_args = {
        'domain_name': {'required': True},
        'access_policies': {'required': True, 'type': 'json'},
    }
