import boto3


def get_event_client(access_key, secret_key, region):
    """
    Returns the client object for AWS Events

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        obj: AWS Cloudwatch Event Client Obj
    """
    return boto3.client(
        "events",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_rule_exists(rule_name, access_key, secret_key, region):
    """
    Check wheter the given cloudwatch rule already exists in AWS account

    Args:
        rule_name (str): Cloudwatch rule name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_event_client(access_key, secret_key, region)
    try:
        response = client.describe_rule(Name=rule_name)
        return True if response else False
    except:
        return False
