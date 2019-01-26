from core.terraform.resources import TerraformResource
from core.config import Settings
from core.providers.aws.boto3 import es


class ElasticsearchDomainResource(TerraformResource):
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
        checked_details = {'attr': "domain_name", 'value': self.get_input_attr('domain_name')}
        exists = False

        if not self.resource_in_tf_output(tf_outputs):
            exists = es.check_es_domain_exists(
                checked_details['value'],
                input.aws_access_key,
                input.aws_secret_key,
                input.aws_region)

        return exists, checked_details


class ElasticsearchDomainPolicyResource(TerraformResource):
    resource_instance_name = "aws_elasticsearch_domain_policy"
    setup_time = 60
    available_args = {
        'domain_name': {'required': True},
        'access_policies': {'required': True, 'type': 'json'},
    }
