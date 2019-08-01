from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
import boto3


def get_ecr_client(aws_auth_cred):
    """
    Returns the client object for AWS ECR (Elastic COntainer Repository)

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: AWS ECR Object
    """
    return prepare_aws_client_with_given_cred("ecr", aws_auth_cred)


def check_ecr_exists(repo_name, aws_auth_cred):
    """
    Check wheter the given ECR already exists in AWS account

    Args:
        repo_name (str): Repository name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_ecr_client(aws_auth_cred)
    try:
        response = client.describe_repositories(repositoryNames=[repo_name])
        return True if len(response['repositories']) else False
    except:
        return False
