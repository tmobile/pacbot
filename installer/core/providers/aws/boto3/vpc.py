import boto3


def get_ec2_client(access_key, secret_key, region):
    return boto3.client(
        'ec2',
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def get_vpc_details(access_key, secret_key, region, vpc_ids):
    response = get_ec2_client(access_key, secret_key, region).describe_vpcs(VpcIds=vpc_ids)

    return response["Vpcs"]


def get_vpc_subnets(access_key, secret_key, region, vpc_ids):
    response = get_ec2_client(access_key, secret_key, region).describe_subnets(Filters=[
        {
            'Name': 'vpc-id',
            'Values': vpc_ids
        }
    ])

    return response['Subnets']


def check_security_group_exists(group_name, vpc_id, access_key, secret_key, region):
    client = get_ec2_client(access_key, secret_key, region)
    try:
        response = client.describe_security_groups(
            Filters=[
                {'Name': "group-name", 'Values': [group_name]},
                {'Name': "vpc-id", 'Values': [vpc_id]},
            ]
        )
        return True if len(response['SecurityGroups']) else False
    except:
        return False
