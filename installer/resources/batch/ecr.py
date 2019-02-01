from core.terraform.utils import get_terraform_scripts_and_files_dir, get_terraform_scripts_dir, get_terraform_provider_file
from core.terraform.resources.aws.ecr import ECRRepository
from core.terraform.resources.misc import NullResource
from core.config import Settings
from resources.s3.bucket import BucketStorage
from distutils.dir_util import copy_tree
from core.utils import run_command
from docker import Client
import os
import sys


class RuleEngineEcrRepository(ECRRepository):
    name = "rule-engine"


class RuleEngineDockerImageBuild(NullResource):
    image_creation_script = 'create_docker_image_and_push_to_ecr.py'
    dest_dir = get_terraform_scripts_and_files_dir()
    DEPENDS_ON = [RuleEngineEcrRepository, BucketStorage]

    def check_docker_image_permission(self):
        error, out, error_msg = run_command("docker images")
        if error or error_msg:
            raise Exception("Error: Either docker service is not available or user do not have permission to run")

    def pre_generate_terraform(self):
        self.check_docker_image_permission()

        src_dir = os.path.join(Settings.BASE_APP_DIR, 'resources', 'batch', 'files')
        copy_tree(src_dir, self.dest_dir)
        self.add_vaues_for_bucket_placeholder()

    def get_provisioners(self):
        docker_creation_script = os.path.join(get_terraform_scripts_dir(), self.image_creation_script)
        local_execs = [
            {
                'local-exec': {
                    'command': docker_creation_script,
                    'environment': {
                        'ECR_REPOSITORY': RuleEngineEcrRepository.get_output_attr('repository_url'),
                        'PROVIDER_FILE': get_terraform_provider_file(),
                        'DOCKER_FILE': 'dockerfile',
                        'DOCKER_FILE_DIR': os.path.join(self.dest_dir, 'batch_docker'),
                        'LOG_FILE': os.path.join(Settings.LOG_DIR, 'debug.log')
                    },
                    'interpreter': [Settings.PYTHON_INTERPRETER]
                }
            }

        ]

        return local_execs

    def add_vaues_for_bucket_placeholder(self):
        input_sh_file = os.path.join(self.dest_dir, "batch_docker", "fetch_and_run.sh.tpl")
        output_sh_file = os.path.join(self.dest_dir, "batch_docker", "fetch_and_run.sh")
        write_lines = []

        with open(input_sh_file, "r") as input_file:
            read_lines = input_file.readlines()
            for line in read_lines:
                s3_bukcet_base_path = os.path.join(BucketStorage.get_input_attr('bucket'), Settings.RESOURCE_NAME_PREFIX)
                write_lines.append(line.replace('{{s3-bucket-base-path}}', s3_bukcet_base_path))

        with open(output_sh_file, "w") as output_file:
            output_file.writelines(write_lines)
