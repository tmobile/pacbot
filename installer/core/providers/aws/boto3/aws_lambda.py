import boto3


def get_lambda_client(access_key, secret_key, region):
    """
    Returns the client object for AWS Lambda

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        obj: Lambda Client Obj
    """
    return boto3.client(
        "lambda",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_function_exists(function_name, access_key, secret_key, region):
    """
    Checks the passed lambda function exists or not

    Args:
        function_name (str): AWS Lambda function name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        boolean: True if Lambda exists else False
    """
    client = get_lambda_client(access_key, secret_key, region)
    try:
        response = client.get_function(FunctionName=function_name)
        return True if response['Configuration'] else False
    except:
        return False
