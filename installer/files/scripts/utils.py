from datetime import datetime
from docker import Client
import json
import boto3
import base64
import uuid


def get_provider_details(provider, provider_json_file):
    """
    From terraform provider file identify the credentials and return it

    Args:
        provider (str): Provider name of terraform
        provider_json_file (path): Json Provider file abs path

    Returns:
        aws_details (dict): Terrafrom AWS provider details
    """
    if provider == "aws":  # TODO- write now we are supporting AWS only
        with open(provider_json_file, 'r') as jsonfile:
            aws_provider = json.load(jsonfile)

        return aws_provider['provider']['aws']


def generate_temp_credentials(assume_role_arn, region_name):
    response = boto3.client(
        'sts',
        region_name=region_name
    ).assume_role(
        RoleArn=assume_role_arn,
        RoleSessionName=str(uuid.uuid4())
    )

    return response['Credentials']


def prepare_aws_client_with_given_aws_details(service_name, aws_details):
    auth_data = {}
    auth_data['region_name'] = aws_details['region']

    if 'access_key' in aws_details:
        auth_data['aws_access_key_id'] = aws_details['access_key']
        auth_data['aws_secret_access_key'] = aws_details['secret_key']
    elif 'assume_role' in aws_details:
        temp_cred = generate_temp_credentials(aws_details['assume_role']['role_arn'], auth_data['region_name'])
        auth_data['aws_access_key_id'] = temp_cred['AccessKeyId']
        auth_data['aws_secret_access_key'] = temp_cred['SecretAccessKey']
        auth_data['aws_session_token'] = temp_cred['SessionToken']

    return boto3.client(service_name, **auth_data)


def prepare_aws_resource_with_given_aws_details(service_name, aws_details):
    auth_data = {}
    auth_data['region_name'] = aws_details['region']

    if 'access_key' in aws_details:
        auth_data['aws_access_key_id'] = aws_details['access_key']
        auth_data['aws_secret_access_key'] = aws_details['secret_key']
    elif 'assume_role' in aws_details:
        temp_cred = generate_temp_credentials(aws_details['assume_role']['role_arn'], auth_data['region_name'])
        auth_data['aws_access_key_id'] = temp_cred['AccessKeyId']
        auth_data['aws_secret_access_key'] = temp_cred['SecretAccessKey']
        auth_data['aws_session_token'] = temp_cred['SessionToken']

    return boto3.resource(service_name, **auth_data)


def get_docker_push_aws_auth_config(aws_details, log_file):
    """
    Return AWS auth config for pushing docker image to ECR

    Args:
        aws_details (dict): AWS details
        log_file (path): Log file path

    Returns:
        auth_config_payload (dict): AWS auth config
    """
    ecr = prepare_aws_client_with_given_aws_details('ecr', aws_details)

    write_to_log_file(log_file, " " * 10 + "Generating Auth token using boto3...")

    auth = ecr.get_authorization_token()
    token = auth["authorizationData"][0]["authorizationToken"]
    decoded_token = base64.b64decode(token).decode()
    username = decoded_token.split(':')[0]
    password = decoded_token.split(':')[1]
    auth_config_payload = {'username': username, 'password': password}

    write_to_log_file(log_file, " " * 10 + "Auth token has been generated!!!")

    return auth_config_payload


def build_docker_image(docker_file_dir, docker_file, repository, log_file):
    """
    Build docker image from the given docker file

    Args:
        docker_file_dir (path): Docker file dir
        docker_file (path): Docker file
        repository (str): Repo name
        log_file (path): Log file path

    Returns:
        auth_config_payload (dict): AWS auth config
    """
    docker_client = Client(base_url='unix://var/run/docker.sock')
    write_to_debug_log(log_file, "Creating Docker image: %s ..." % str(repository))

    info = docker_client.build(
        dockerfile=docker_file,
        tag=repository,
        path=docker_file_dir,
        rm=True,
        stream=True)
    with open(log_file, 'a') as f:
        for item in info:
            f.write("%s %s\n" % (" " * 10, str(item)))

    write_to_debug_log(log_file, "Docker image: %s has been created locally!!!" % str(repository))

    return docker_client


def write_to_log_file(log_file, message):
    """
    Write log to the given file

    Args:
        message (str): Message to be logged
        log_file (path): Log file path
    """
    with open(log_file, 'a') as f:
        f.write(message + "\n")


def write_to_debug_log(debug_log_file, msg):
    """
    Write log to the debug file

    Args:
        message (str): Message to be logged
        debug_log_file (path): Debug Log file path
    """
    now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    with open(debug_log_file, 'a+') as logfile:
        logfile.write("%s: %s\n" % (now, msg))
