from core.terraform.utils import get_terraform_scripts_and_files_dir, get_terraform_scripts_dir, get_terraform_provider_file
from core.terraform.resources.aws.ecr import ECRRepository
from core.terraform.resources.misc import NullResource
from core.config import Settings
from resources.s3.bucket import BucketStorage
from distutils.dir_util import copy_tree
from core.utils import run_command
from core.providers.aws.boto3.iam import create_iam_service_linked_role
from core.log import SysLog
import os
import sys


class APIEcrRepository(ECRRepository):
    name = "microservices"

    def pre_terraform_apply(self):
        status, msg = create_iam_service_linked_role(
            "ecs.amazonaws.com",
            Settings.RESOURCE_DESCRIPTION,
            Settings.AWS_AUTH_CRED)

        SysLog().write_debug_log("ECS IAM Service Linked role creation: Status:%s, Message: %s" % (str(status), msg))


class UIEcrRepository(ECRRepository):
    name = "webapp"


class BaseDockerImageBuild:
    image_creation_script = "create_docker_image_and_push_to_ecr.py"
    dest_dir = get_terraform_scripts_and_files_dir()

    def check_docker_image_permission(self):
        error, out, error_msg = run_command("docker image ls")
        if error or error_msg:
            raise Exception("Error: Either docker service is not available or user do not have permission to run")

    def copy_docker_files(self):
        src_dir = os.path.join(Settings.BASE_APP_DIR, 'resources', 'pacbot_app', 'files', self.docker_dir)
        docker_target_dir = os.path.join(self.dest_dir, self.docker_dir)
        copy_tree(src_dir, docker_target_dir)
        self.add_vaues_for_bucket_placeholder()

    def get_app_provisioners(self):
        docker_creation_script = os.path.join(
            get_terraform_scripts_dir(),
            self.image_creation_script)

        local_execs = [
            {
                'local-exec': {
                    'command': docker_creation_script,
                    'environment': {
                        'ECR_REPOSITORY': self.ecr_repo,
                        'PROVIDER_FILE': get_terraform_provider_file(),
                        'DOCKER_FILE': 'dockerfile',
                        'DOCKER_FILE_DIR': os.path.join(self.dest_dir, self.docker_dir),
                        'LOG_FILE': os.path.join(Settings.LOG_DIR, 'debug.log')
                    },
                    'interpreter': [Settings.PYTHON_INTERPRETER]
                }
            }
        ]

        return local_execs

    def add_vaues_for_bucket_placeholder(self):
        input_sh_file = os.path.join(self.dest_dir, self.docker_dir, "entrypoint.sh.tpl")
        output_sh_file = os.path.join(self.dest_dir, self.docker_dir, "entrypoint.sh")
        write_lines = []

        with open(input_sh_file, "r") as input_file:
            read_lines = input_file.readlines()
            for line in read_lines:
                s3_bukcet_base_path = os.path.join(BucketStorage.get_input_attr('bucket'), Settings.RESOURCE_NAME_PREFIX)
                write_lines.append(line.replace('{{s3-bucket-base-path}}', s3_bukcet_base_path))

        with open(output_sh_file, "w") as output_file:
            output_file.writelines(write_lines)


class APIDockerImageBuild(NullResource, BaseDockerImageBuild):
    DEPENDS_ON = [APIEcrRepository, BucketStorage]
    docker_dir = "api_docker"
    ecr_repo = APIEcrRepository.get_output_attr('repository_url')

    def get_provisioners(self):
        return self.get_app_provisioners()

    def pre_generate_terraform(self):
        self.check_docker_image_permission()
        self.copy_docker_files()


class UIDockerImageBuild(NullResource, BaseDockerImageBuild):
    DEPENDS_ON = [UIEcrRepository, APIDockerImageBuild]
    docker_dir = "ui_docker"
    ecr_repo = UIEcrRepository.get_output_attr('repository_url')

    def get_provisioners(self):
        return self.get_app_provisioners()

    def pre_generate_terraform(self):
        self.check_docker_image_permission()
        self.copy_docker_files()
