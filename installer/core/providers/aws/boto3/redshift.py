import boto3


def get_redshift_client(access_key, secret_key, region):
    return boto3.client(
        'redshift',
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_redshift_cluster_exists(cluster_identifier, access_key, secret_key, region):
    client = get_redshift_client(access_key, secret_key, region)
    try:
        response = client.describe_clusters(
            ClusterIdentifier=cluster_identifier
        )
        return True if len(response['Clusters']) else False
    except:
        return False


def check_redshift_parameter_group_exists(name, access_key, secret_key, region):
    client = get_redshift_client(access_key, secret_key, region)
    try:
        response = client.describe_cluster_parameter_groups(
            ParameterGroupName=name
        )
        return True if len(response['ParameterGroups']) else False
    except:
        return False


def check_redshift_subnet_group_exists(name, access_key, secret_key, region):
    client = get_redshift_client(access_key, secret_key, region)
    try:
        response = client.describe_cluster_subnet_groups(
            ClusterSubnetGroupName=name
        )
        return True if len(response['ClusterSubnetGroups']) else False
    except:
        return False
