import boto3


def get_sts_client(access_key, secret_key):
    return boto3.client(
        "sts",
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def get_user_account_id(access_key, secret_key):
    return get_sts_client(access_key, secret_key).get_caller_identity().get('Account')
