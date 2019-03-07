import boto3


def get_elbv2_client(access_key, secret_key, region):
    """
    Returns the client object for AWS ELB

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        obj: AWS ELB Object
    """
    return boto3.client(
        "elbv2",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_alb_exists(alb_name, access_key, secret_key, region):
    """
    Check wheter the given ALB already exists in the AWS Account

    Args:
        alb_name (str): Load balancer name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_elbv2_client(access_key, secret_key, region)
    try:
        response = client.describe_load_balancers(Names=[alb_name])
        return True if len(response['LoadBalancers']) else False
    except:
        return False


def check_target_group_exists(tg_name, access_key, secret_key, region):
    """
    Check wheter the given Target group already exists in the AWS Account

    Args:
        tg_name (str): Target group name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_elbv2_client(access_key, secret_key, region)
    try:
        response = client.describe_target_groups(Names=[tg_name])
        return True if len(response['TargetGroups']) else False
    except:
        return False
