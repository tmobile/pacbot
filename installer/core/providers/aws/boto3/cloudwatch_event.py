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


def get_targets_of_a_rule(rule_name, access_key, secret_key, region):
    """
    Returns the targets of the given cloudwatch rule

    Args:
        rule_name (str): Cloudwatch rule name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        targets (list): List of all targets attached to a rule
    """
    client = get_event_client(access_key, secret_key, region)

    try:
        response = client.list_targets_by_rule(
            Rule=rule_name
        )
    except:
        return []

    return response['Targets']


def remove_all_targets_of_a_rule(rule_name, access_key, secret_key, region):
    """
    Remove all targets of a rule

    Args:
        rule_name (str): Cloudwatch rule name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region
    """
    targets = get_targets_of_a_rule(rule_name, access_key, secret_key, region)

    target_ids = [item['Id'] for item in targets]

    if len(target_ids) > 0:
        client = get_event_client(access_key, secret_key, region)

        client.remove_targets(
            Rule=rule_name,
            Ids=target_ids,
            Force=True
        )
