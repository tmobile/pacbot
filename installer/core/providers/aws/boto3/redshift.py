from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
import boto3


def get_redshift_client(aws_auth_cred):
    """
    Returns the client object for AWS Redshift

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: AWS Redshift Object
    """
    return prepare_aws_client_with_given_cred("redshift", aws_auth_cred)


def check_redshift_cluster_exists(cluster_identifier, aws_auth_cred):
    """
    Check wheter the given Redshift cluster already exists in the AWS Account

    Args:
        cluster_identifier (str): Redshift cluster identifier
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_redshift_client(aws_auth_cred)
    try:
        response = client.describe_clusters(
            ClusterIdentifier=cluster_identifier
        )
        return True if len(response['Clusters']) else False
    except:
        return False


def check_redshift_parameter_group_exists(name, aws_auth_cred):
    """
    Check wheter the given Redshift Parameter Group already exists in the AWS Account

    Args:
        name (str): Redshift Parameter Group name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_redshift_client(aws_auth_cred)
    try:
        response = client.describe_cluster_parameter_groups(
            ParameterGroupName=name
        )
        return True if len(response['ParameterGroups']) else False
    except:
        return False


def check_redshift_subnet_group_exists(name, aws_auth_cred):
    """
    Check wheter the given Redshift SUbnet Group already exists in the AWS Account

    Args:
        name (str): Redshift Subnet Group name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_redshift_client(aws_auth_cred)
    try:
        response = client.describe_cluster_subnet_groups(
            ClusterSubnetGroupName=name
        )
        return True if len(response['ClusterSubnetGroups']) else False
    except:
        return False
