from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
import boto3


def get_event_client(aws_auth_cred):
    """
    Returns the client object for AWS Events

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: AWS Cloudwatch Event Client Obj
    """
    return prepare_aws_client_with_given_cred("events", aws_auth_cred)


def check_rule_exists(rule_name, aws_auth_cred):
    """
    Check wheter the given cloudwatch rule already exists in AWS account

    Args:
        rule_name (str): Cloudwatch rule name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_event_client(aws_auth_cred)
    try:
        response = client.describe_rule(Name=rule_name)
        return True if response else False
    except:
        return False


def get_targets_of_a_rule(rule_name, aws_auth_cred):
    """
    Returns the targets of the given cloudwatch rule

    Args:
        rule_name (str): Cloudwatch rule name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        targets (list): List of all targets attached to a rule
    """
    client = get_event_client(aws_auth_cred)

    try:
        response = client.list_targets_by_rule(
            Rule=rule_name
        )
    except:
        return []

    return response['Targets']


def remove_all_targets_of_a_rule(rule_name, aws_auth_cred):
    """
    Remove all targets of a rule

    Args:
        rule_name (str): Cloudwatch rule name
        aws_auth (dict): Dict containing AWS credentials
    """
    targets = get_targets_of_a_rule(rule_name, aws_auth_cred)

    target_ids = [item['Id'] for item in targets]

    if len(target_ids) > 0:
        client = get_event_client(aws_auth_cred)

        client.remove_targets(
            Rule=rule_name,
            Ids=target_ids,
            Force=True
        )
