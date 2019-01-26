import boto3


def get_logs_client(access_key, secret_key, region):
    return boto3.client(
        "logs",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_log_group_exists(log_group_name, access_key, secret_key, region):
    client = get_logs_client(access_key, secret_key, region)
    try:
        response = client.describe_log_groups(logGroupNamePrefix=log_group_name)
        return True if len(response['logGroups']) else False
    except:
        return False
