from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
import boto3


def get_ec2_client(aws_auth_cred):
    """
    Returns the client object for AWS EC2

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: AWS EC2 Client Object
    """
    return prepare_aws_client_with_given_cred('ec2', aws_auth_cred)


def get_vpc_details(vpc_ids, aws_auth_cred):
    """
    Find VPC details of all the ids passed to this method

    Args:
        aws_auth (dict): Dict containing AWS credentials
        vpc_ids (list): List of VOC Ids

    Returns:
        VPCS (list): List of all VPC objects
    """
    response = get_ec2_client(aws_auth_cred).describe_vpcs(VpcIds=vpc_ids)

    return response["Vpcs"]


def get_vpc_subnets(vpc_ids, aws_auth_cred):
    """
    Find all subnets under a VPC

    Args:
        aws_auth (dict): Dict containing AWS credentials
        vpc_ids (list): List of VOC Ids

    Returns:
        Subnets (list): List of all subnets object
    """
    response = get_ec2_client(aws_auth_cred).describe_subnets(Filters=[
        {
            'Name': 'vpc-id',
            'Values': vpc_ids
        }
    ])

    return response['Subnets']


def check_security_group_exists(group_name, vpc_id, aws_auth_cred):
    """
    Check wheter the given security group already exists in the AWS Account

    Args:
        group_name (str): Security group name
        vpc_id (str): VPC id under which the group should be searched
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_ec2_client(aws_auth_cred)
    try:
        response = client.describe_security_groups(
            Filters=[
                {'Name': "group-name", 'Values': [group_name]},
                {'Name': "vpc-id", 'Values': [vpc_id]},
            ]
        )
        return True if len(response['SecurityGroups']) else False
    except:
        return False
