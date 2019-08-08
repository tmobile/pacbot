from utils import get_provider_details, get_docker_push_aws_auth_config, build_docker_image, write_to_log_file
from utils import write_to_debug_log
from docker import Client
import os


def build_and_push_docker_image(provider_json_file, ecr_repo, docker_file, docker_file_dir, log_file):
    """
    Build docket image and push that to the ECR repo.

    Args:
        provider_json_file (path): Path of the terraform provider file to get aws credentials
        docker_file (str): Docker file name
        ecr_repo (str): AWS ECR repo url
        docker_file_dir (path): Abs Path of folder where docker file is present
        log_file (path): Log file path

    Raises:
        If failed to push image to ECR
    """
    write_to_debug_log(log_file, "Docker image creation and push to ecr repo: %s is started" % str(ecr_repo))

    aws_details = get_provider_details("aws", provider_json_file)
    auth_config_payload = get_docker_push_aws_auth_config(aws_details, log_file)
    docker_client = build_docker_image(docker_file_dir, docker_file, ecr_repo, log_file)
    latest_tag = "latest"
    pushed = docker_client.push(ecr_repo, tag=latest_tag, auth_config=auth_config_payload)

    if pushed:
        write_to_debug_log(log_file, "Pushed docker image to repo: %s" % ecr_repo)
    else:
        write_to_log_file(log_file, " ERROR: failed to push. %s" % ecr_repo)
        raise Exception("Failed to push image: %s" % str(pushed))

    delete_docker_images_from_local(os.path.join(docker_file_dir, docker_file))


def delete_docker_images_from_local(docker_file_abs_path):
    """
    Delete docker image from local installer machine

    Args:
        docker_file_abs_path (path): Abs path of docker file

    Raises:
        If failed to push image to ECR
    """
    docker_client = Client(base_url='unix://var/run/docker.sock')

    # Delete original image
    docker_client.remove_image(ecr_repo, force=True)
    write_to_debug_log(log_file, "Deleted image %s from local !!!" % ecr_repo)

    # Delete Base Image
    with open(docker_file_abs_path, "r") as f:
        lines = f.readlines()

    for line in lines:
        if "FROM" in line:
            image_line = line.strip().strip("\n")
            break
    try:
        local_image_name = image_line.split("FROM ")[1].strip()
        docker_client.remove_image(local_image_name, force=True)
        write_to_debug_log(log_file, "Deleted image %s from local !!!" % local_image_name)
    except:
        pass


if __name__ == "__main__":
    """
    This script is executed from the provisioner of terraform resource to create docker image and push it
    """
    provider_json_file = os.getenv('PROVIDER_FILE')
    ecr_repo = os.getenv('ECR_REPOSITORY')
    docker_file = os.getenv('DOCKER_FILE')
    docker_file_dir = os.getenv('DOCKER_FILE_DIR')
    log_file = os.getenv('LOG_FILE', 'debug.log')

    build_and_push_docker_image(provider_json_file, ecr_repo, docker_file, docker_file_dir, log_file)
