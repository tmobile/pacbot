import boto3


def get_logs_client(access_key, secret_key, region):
    """
    Returns the client object for AWS Cloudwatch Log

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        obj: AWS Cloudwatch Event Log Obj
    """
    return boto3.client(
        "logs",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_log_group_exists(log_group_name, access_key, secret_key, region):
    """
    Check wheter the given cloudwatch log group already exists in AWS account

    Args:
        log_group_name (str): Cloudwatch log group name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_logs_client(access_key, secret_key, region)
    try:
        response = client.describe_log_groups(logGroupNamePrefix=log_group_name)
        return True if len(response['logGroups']) else False
    except:
        return False
