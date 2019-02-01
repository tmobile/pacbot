import boto3


def get_rds_client(access_key, secret_key, region):
    return boto3.client(
        'rds',
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def check_rds_instance_exists(instance_identifier, access_key, secret_key, region):
    client = get_rds_client(access_key, secret_key, region)
    try:
        response = client.describe_db_instances(
            DBInstanceIdentifier=instance_identifier
        )
        return True if len(response['DBInstances']) else False
    except:
        return False


def check_rds_option_group_exists(name, access_key, secret_key, region):
    client = get_rds_client(access_key, secret_key, region)
    try:
        response = client.describe_option_groups(
            OptionGroupName=name
        )
        return True if len(response['OptionGroupsList']) else False
    except:
        return False


def check_rds_parameter_group_exists(name, access_key, secret_key, region):
    client = get_rds_client(access_key, secret_key, region)
    try:
        response = client.describe_db_parameter_groups(
            DBParameterGroupName=name
        )
        return True if len(response['DBParameterGroups']) else False
    except:
        return False


def check_rds_subnet_group_exists(name, access_key, secret_key, region):
    client = get_rds_client(access_key, secret_key, region)
    try:
        response = client.describe_db_subnet_groups(
            DBSubnetGroupName=name
        )
        return True if len(response['DBSubnetGroups']) else False
    except:
        return False
