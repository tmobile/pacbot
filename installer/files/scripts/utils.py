from datetime import datetime
from docker import Client
import json
import boto3
import base64


def get_provider_credentials(provider, provider_json_file):
    if provider == "aws":  # TODO- write now we are supporting AWS only
        with open(provider_json_file, 'r') as jsonfile:
            data = json.load(jsonfile)

        aws_access_key = data['provider']['aws']['access_key']
        aws_secret_key = data['provider']['aws']['secret_key']
        region_name = data['provider']['aws']['region']

        return aws_access_key, aws_secret_key, region_name


def get_docker_push_aws_auth_config(aws_access_key, aws_secret_key, region_name, log_file):

    ecr = boto3.client(
        'ecr',
        region_name=region_name,
        aws_access_key_id=aws_access_key,
        aws_secret_access_key=aws_secret_key)
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
    with open(log_file, 'a') as f:
        f.write(message + "\n")


def write_to_debug_log(debug_log_file, msg):
    now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    with open(debug_log_file, 'a+') as logfile:
        logfile.write("%s: %s\n" % (now, msg))
