import boto3


def prepare_aws_client_with_given_cred(service_name, aws_auth_cred=None):
    auth_data = {}

    if aws_auth_cred:
        if aws_auth_cred['aws_auth_option'] == 1:
            auth_data['aws_access_key_id'] = aws_auth_cred['aws_access_key']
            auth_data['aws_secret_access_key'] = aws_auth_cred['aws_secret_key']
        elif aws_auth_cred['aws_auth_option'] == 2:
            auth_data['aws_access_key_id'] = aws_auth_cred['tmp_credentials']['AccessKeyId']
            auth_data['aws_secret_access_key'] = aws_auth_cred['tmp_credentials']['SecretAccessKey']
            auth_data['aws_session_token'] = aws_auth_cred['tmp_credentials']['SessionToken']

        auth_data['region_name'] = aws_auth_cred['aws_region']

    return boto3.client(service_name, **auth_data)


def prepare_aws_resource_with_given_cred(service_name, aws_auth_cred=None):
    auth_data = {}

    if aws_auth_cred:
        if aws_auth_cred['aws_auth_option'] == 1:
            auth_data['aws_access_key_id'] = aws_auth_cred['aws_access_key']
            auth_data['aws_secret_access_key'] = aws_auth_cred['aws_secret_key']
        elif aws_auth_cred['aws_auth_option'] == 2:
            auth_data['aws_access_key_id'] = aws_auth_cred['tmp_credentials']['AccessKeyId']
            auth_data['aws_secret_access_key'] = aws_auth_cred['tmp_credentials']['SecretAccessKey']
            auth_data['aws_session_token'] = aws_auth_cred['tmp_credentials']['SessionToken']

        auth_data['region_name'] = aws_auth_cred['aws_region']

    return boto3.resource(service_name, **auth_data)
