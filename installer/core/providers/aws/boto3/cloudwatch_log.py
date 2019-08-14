from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
import boto3


def get_logs_client(aws_auth_cred):
    """
    Returns the client object for AWS Cloudwatch Log

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: AWS Cloudwatch Event Log Obj
    """
    return prepare_aws_client_with_given_cred("logs", aws_auth_cred)


def check_log_group_exists(log_group_name, aws_auth_cred):
    """
    Check wheter the given cloudwatch log group already exists in AWS account

    Args:
        log_group_name (str): Cloudwatch log group name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_logs_client(aws_auth_cred)
    try:
        response = client.describe_log_groups(logGroupNamePrefix=log_group_name)
        return True if len(response['logGroups']) else False
    except:
        return False
