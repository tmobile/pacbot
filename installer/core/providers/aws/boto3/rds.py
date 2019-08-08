from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
import boto3


def get_rds_client(aws_auth_cred):
    """
    Returns the client object for AWS RDS

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: AWS RDS Object
    """
    return prepare_aws_client_with_given_cred("rds", aws_auth_cred)


def check_rds_instance_exists(instance_identifier, aws_auth_cred):
    """
    Check wheter the given RDS Instance already exists in the AWS Account

    Args:
        instance_identifier (str): RDS instance identifier
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_rds_client(aws_auth_cred)
    try:
        response = client.describe_db_instances(
            DBInstanceIdentifier=instance_identifier
        )
        return True if len(response['DBInstances']) else False
    except:
        return False


def check_rds_option_group_exists(name, aws_auth_cred):
    """
    Check wheter the given RDS Option Group already exists in the AWS Account

    Args:
        name (str): RDS Option Group name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_rds_client(aws_auth_cred)
    try:
        response = client.describe_option_groups(
            OptionGroupName=name
        )
        return True if len(response['OptionGroupsList']) else False
    except:
        return False


def check_rds_parameter_group_exists(name, aws_auth_cred):
    """
    Check wheter the given RDS Parameter Group already exists in the AWS Account

    Args:
        name (str): RDS Parameter Group name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_rds_client(aws_auth_cred)
    try:
        response = client.describe_db_parameter_groups(
            DBParameterGroupName=name
        )
        return True if len(response['DBParameterGroups']) else False
    except:
        return False


def check_rds_subnet_group_exists(name, aws_auth_cred):
    """
    Check wheter the given RDS SUbnet Group already exists in the AWS Account

    Args:
        name (str): RDS Subnet Group name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_rds_client(aws_auth_cred)
    try:
        response = client.describe_db_subnet_groups(
            DBSubnetGroupName=name
        )
        return True if len(response['DBSubnetGroups']) else False
    except:
        return False
