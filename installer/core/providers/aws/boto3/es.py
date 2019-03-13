import boto3


def get_es_client(access_key, secret_key, region):
    """
    Returns the client object for AWS Elasticsearch

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        obj: AWS Elasticsearch Object
    """
    return boto3.client(
        'es',
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_es_domain_exists(domain_name, access_key, secret_key, region):
    """
    Check wheter the given ES Domain already exists in the AWS Account

    Args:
        domain_name (str): ES Domain name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_es_client(access_key, secret_key, region)
    try:
        response = client.describe_elasticsearch_domain(
            DomainName=domain_name
        )
        return True if response['DomainStatus'] else False
    except:
        return False
