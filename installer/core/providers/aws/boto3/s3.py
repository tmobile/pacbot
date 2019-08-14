from core.providers.aws.boto3 import prepare_aws_client_with_given_cred


def get_s3_client(aws_auth_cred):
    """
    Returns the client object for AWS EC2

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: AWS EC2 Client Object
    """
    return prepare_aws_client_with_given_cred('s3', aws_auth_cred)
