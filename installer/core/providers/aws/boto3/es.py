from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
import boto3


def get_es_client(aws_auth_cred):
    """
    Returns the client object for AWS Elasticsearch

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: AWS Elasticsearch Object
    """
    return prepare_aws_client_with_given_cred("es", aws_auth_cred)


def check_es_domain_exists(domain_name, aws_auth_cred):
    """
    Check wheter the given ES Domain already exists in the AWS Account

    Args:
        domain_name (str): ES Domain name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_es_client(aws_auth_cred)
    try:
        response = client.describe_elasticsearch_domain(
            DomainName=domain_name
        )
        return True if response['DomainStatus'] else False
    except:
        return False
