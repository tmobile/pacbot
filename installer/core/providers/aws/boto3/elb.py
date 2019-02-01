import boto3


def get_elbv2_client(access_key, secret_key, region):
    return boto3.client(
        "elbv2",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_alb_exists(alb_name, access_key, secret_key, region):
    client = get_elbv2_client(access_key, secret_key, region)
    try:
        response = client.describe_load_balancers(Names=[alb_name])
        return True if len(response['LoadBalancers']) else False
    except:
        return False


def check_target_group_exists(tg_name, access_key, secret_key, region):
    client = get_elbv2_client(access_key, secret_key, region)
    try:
        response = client.describe_target_groups(Names=[tg_name])
        return True if len(response['TargetGroups']) else False
    except:
        return False
