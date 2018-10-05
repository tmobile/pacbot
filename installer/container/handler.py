#!/usr/bin/env python

import os
import sys
import os.path
import json
import click
import boto3
import json
import base64
import hashlib
import docker
from docker import Client
from botocore.exceptions import ClientError, ParamValidationError


def _create_image_digest(contents=None):
    if not contents:
        contents = 'docker_image{0}'.format(int(random() * 10 ** 6))
    return "sha256:%s" % hashlib.sha256(contents.encode('utf-8')).hexdigest()


def _create_image_manifest():
    return {
        "schemaVersion": 2,
        "mediaType": "application/vnd.docker.distribution.manifest.v2+json",
        "config":
            {
                "mediaType": "application/vnd.docker.container.image.v1+json",
                "size": 7023,
                "digest": _create_image_digest("config")
            },
        "layers": [
            {
                "mediaType": "application/vnd.docker.image.rootfs.diff.tar.gzip",
                "size": 32654,
                "digest": _create_image_digest("layer1")
            },
            {
                "mediaType": "application/vnd.docker.image.rootfs.diff.tar.gzip",
                "size": 16724,
                "digest": _create_image_digest("layer2")
            },
            {
                "mediaType": "application/vnd.docker.image.rootfs.diff.tar.gzip",
                "size": 73109,
                "digest": _create_image_digest("layer3")
            }]
    }


def _create_docker_image(dockerfilepath, repository, pacman_installation):
    docker_client = Client(base_url='unix://var/run/docker.sock')
    for info in docker_client.build(path=dockerfilepath, tag=repository, rm=True, stream=True):
        _logs_display(info, pacman_installation)
    return docker_client


def _create_ecr_image_push(region, accessKey, secretKey, dockerfilepath, repository, pacman_installation):
    ecr = boto3.client('ecr', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    pacman_installation.write("docker image building for " + repository + "\n")
    try:
        response = ecr.create_repository(repositoryName=repository)
    except Exception as rae:
        print "This Repository is already created", rae
    auth = ecr.get_authorization_token()
    token = auth["authorizationData"][0]["authorizationToken"]
    username, password = base64.b64decode(token).split(':')
    endpoint = auth["authorizationData"][0]["proxyEndpoint"]
    auth_config_payload = {'username': username, 'password': password}
    left, repo = endpoint.split("//")
    repo = repo+"/"+repository
    version_tag = repo
    local_tag = repository
    docker_client = _create_docker_image(dockerfilepath, repository, pacman_installation)
    print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
    docker_client = Client(base_url='unix://var/run/docker.sock')

    docker_client.tag(local_tag, version_tag)
    for info in docker_client.push(version_tag, stream=True, auth_config=auth_config_payload):
        _logs_display(info, pacman_installation)
    print "conatiner pushed into repository [", repo, "]"
    print ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
    pacman_installation.write("image creation completed")
    return repo


def _logs_display(logdetail, pacman_installation):
    for output in logdetail.split('\n'):
        output = output.strip()
        json_output = ""
        if output:
            try:
                json_output = json.loads(output)
            except ValueError as ex:
                print "Error parsing output from docker image build: ", ex.message
            if json_output:
                if "errorDetail" in json_output:
                    error = json_output["errorDetail"]
                    sys.stderr.write(error["message"])
                    print "error...............................", error
                    raise RuntimeError("Error on build - code " + str(error))
                elif "stream" in json_output:
                    pacman_installation.write(json_output["stream"])
                elif "status" in json_output:
                    sys.stdout.write(json_output["status"]+" ")


def delete_repo(region, accessKey, secretKey, repository, log_handler):
    '''
    Delete ECR repository from AWS.
    '''
    ecr = boto3.client('ecr', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    log_handler.write("Deleting ECR Repository: " + repository + "\n")
    try:
        response = ecr.delete_repository(repositoryName=repository, force=True)
    except Exception as rae:
        print("Error:=> " + str(rae))
