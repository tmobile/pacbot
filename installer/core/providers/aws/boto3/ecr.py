import boto3


def get_ecr_client(access_key, secret_key, region):
    return boto3.client(
        "ecr",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_ecr_exists(repo_name, access_key, secret_key, region):
    client = get_ecr_client(access_key, secret_key, region)
    try:
        response = client.describe_repositories(Names=[repo_name])
        return True if len(response['repositoryNames']) else False
    except:
        return False
