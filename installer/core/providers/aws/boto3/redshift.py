import boto3


def get_redshift_client(access_key, secret_key, region):
    """
    Returns the client object for AWS Redshift

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        obj: AWS Redshift Object
    """
    return boto3.client(
        'redshift',
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_redshift_cluster_exists(cluster_identifier, access_key, secret_key, region):
    """
    Check wheter the given Redshift cluster already exists in the AWS Account

    Args:
        cluster_identifier (str): Redshift cluster identifier
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_redshift_client(access_key, secret_key, region)
    try:
        response = client.describe_clusters(
            ClusterIdentifier=cluster_identifier
        )
        return True if len(response['Clusters']) else False
    except:
        return False


def check_redshift_parameter_group_exists(name, access_key, secret_key, region):
    """
    Check wheter the given Redshift Parameter Group already exists in the AWS Account

    Args:
        name (str): Redshift Parameter Group name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_redshift_client(access_key, secret_key, region)
    try:
        response = client.describe_cluster_parameter_groups(
            ParameterGroupName=name
        )
        return True if len(response['ParameterGroups']) else False
    except:
        return False


def check_redshift_subnet_group_exists(name, access_key, secret_key, region):
    """
    Check wheter the given Redshift SUbnet Group already exists in the AWS Account

    Args:
        name (str): Redshift Subnet Group name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_redshift_client(access_key, secret_key, region)
    try:
        response = client.describe_cluster_subnet_groups(
            ClusterSubnetGroupName=name
        )
        return True if len(response['ClusterSubnetGroups']) else False
    except:
        return False
