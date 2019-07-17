from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
import boto3


def get_lambda_client(aws_auth_cred):
    """
    Returns the client object for AWS Lambda

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: Lambda Client Obj
    """
    return prepare_aws_client_with_given_cred("lambda", aws_auth_cred)


def check_function_exists(function_name, aws_auth_cred):
    """
    Checks the passed lambda function exists or not

    Args:
        function_name (str): AWS Lambda function name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        boolean: True if Lambda exists else False
    """
    client = get_lambda_client(aws_auth_cred)
    try:
        response = client.get_function(FunctionName=function_name)
        return True if response['Configuration'] else False
    except:
        return False
