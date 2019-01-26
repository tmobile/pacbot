import boto3


def get_event_client(access_key, secret_key, region):
    return boto3.client(
        "events",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_rule_exists(rule_name, access_key, secret_key, region):
    client = get_event_client(access_key, secret_key, region)
    try:
        response = client.describe_rule(Name=rule_name)
        return True if response else False
    except:
        return False
