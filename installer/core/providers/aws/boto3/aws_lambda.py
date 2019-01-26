import boto3


def get_lambda_client(access_key, secret_key, region):
    return boto3.client(
        "lambda",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_function_exists(function_name, access_key, secret_key, region):
    client = get_lambda_client(access_key, secret_key, region)
    try:
        response = client.get_function(FunctionName=function_name)
        return True if response['Configuration'] else False
    except:
        return False
