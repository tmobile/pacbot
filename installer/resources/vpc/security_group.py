from core.terraform.resources.aws.vpc import SecurityGroupResource
from core.config import Settings


class InfraSecurityGroupResource(SecurityGroupResource):
    name = ""
    vpc_id = Settings.get('VPC')['ID']

    ingress = [
        {
            'from_port': 0,
            'to_port': 0,
            'protocol': "-1",
            'cidr_blocks': Settings.get('VPC')['CIDR_BLOCKS'],
            'ipv6_cidr_blocks': [],
            'prefix_list_ids': [],
            'description': "",
            'self': False,
            'security_groups': []
        }
    ]

    egress = [
        {
            'from_port': 0,
            'to_port': 0,
            'protocol': "-1",
            'cidr_blocks': ["0.0.0.0/0"],
            'ipv6_cidr_blocks': [],
            'prefix_list_ids': [],
            'description': "",
            'self': False,
            'security_groups': []
        }
    ]
