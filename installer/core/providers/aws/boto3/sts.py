from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
import boto3
import uuid


def get_sts_client(aws_auth_cred):
    """
    Returns AWS sts client object

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: AWS Sts client Object
    """
    return prepare_aws_client_with_given_cred('sts', aws_auth_cred)


def generate_temp_credentials(assume_role_arn, region_name):
    response = boto3.client(
        "sts",
        region_name=region_name
    ).assume_role(
        RoleArn=assume_role_arn,
        RoleSessionName=str(uuid.uuid4())
    )

    return response['Credentials']


def get_aws_caller_identity(aws_auth_cred):
    """
    Returns AWS user account id from the given credentials

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        account_id (str): AWS user account ID
    """
    return get_sts_client(aws_auth_cred).get_caller_identity()
