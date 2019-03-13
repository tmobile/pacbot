import boto3


def get_ec2_client(access_key, secret_key, region):
    """
    Returns the client object for AWS EC2

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        obj: AWS EC2 Client Object
    """
    return boto3.client(
        'ec2',
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def get_vpc_details(access_key, secret_key, region, vpc_ids):
    """
    Find VPC details of all the ids passed to this method

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region
        vpc_ids (list): List of VOC Ids

    Returns:
        VPCS (list): List of all VPC objects
    """
    response = get_ec2_client(access_key, secret_key, region).describe_vpcs(VpcIds=vpc_ids)

    return response["Vpcs"]


def get_vpc_subnets(access_key, secret_key, region, vpc_ids):
    """
    Find all subnets under a VPC

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region
        vpc_ids (list): List of VOC Ids

    Returns:
        Subnets (list): List of all subnets object
    """
    response = get_ec2_client(access_key, secret_key, region).describe_subnets(Filters=[
        {
            'Name': 'vpc-id',
            'Values': vpc_ids
        }
    ])

    return response['Subnets']


def check_security_group_exists(group_name, vpc_id, access_key, secret_key, region):
    """
    Check wheter the given security group already exists in the AWS Account

    Args:
        group_name (str): Security group name
        vpc_id (str): VPC id under which the group should be searched
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_ec2_client(access_key, secret_key, region)
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
