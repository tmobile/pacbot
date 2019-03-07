import boto3


def get_ecr_client(access_key, secret_key, region):
    """
    Returns the client object for AWS ECR (Elastic COntainer Repository)

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        obj: AWS ECR Object
    """
    return boto3.client(
        "ecr",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_ecr_exists(repo_name, access_key, secret_key, region):
    """
    Check wheter the given ECR already exists in AWS account

    Args:
        repo_name (str): Repository name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_ecr_client(access_key, secret_key, region)
    try:
        response = client.describe_repositories(Names=[repo_name])
        return True if len(response['repositoryNames']) else False
    except:
        return False
