import boto3


def get_es_client(access_key, secret_key, region):
    return boto3.client(
        'es',
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_es_domain_exists(domain_name, access_key, secret_key, region):
    client = get_es_client(access_key, secret_key, region)
    try:
        response = client.describe_elasticsearch_domain(
            DomainName=domain_name
        )
        return True if response['DomainStatus'] else False
    except:
        return False
