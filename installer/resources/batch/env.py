from core.terraform.resources.aws.batch import BatchComputeEnvironmentResource
from resources.iam.ecs_role import ECSRoleInstanceProfile
from resources.iam.batch_role import BatchRole, BatchIAMRolePolicyAttach
from resources.vpc.security_group import InfraSecurityGroupResource
from core.config import Settings
from core.providers.aws.boto3.vpc import get_ec2_client
from core.providers.aws.boto3.batch import get_compute_environments
from core.mixins import MsgMixin
import boto3
import os
import sys


class RuleEngineBatchJobEnv(BatchComputeEnvironmentResource):
    compute_environment_name = ""
    instance_role = ECSRoleInstanceProfile.get_output_attr('arn')
    instance_type = ["m4.xlarge"]
    max_vcpus = 256
    min_vcpus = 0
    desired_vcpus = 0
    ec2_key_pair = ""
    resource_type = "EC2"
    security_group_ids = [InfraSecurityGroupResource.get_output_attr('id')]
    subnets = Settings.get('VPC')['SUBNETS']
    env_type = "MANAGED"
    service_role = BatchRole.get_output_attr('arn')
    compute_resources_tags = [{'Application': Settings.RESOURCE_NAME_PREFIX}]

    DEPENDS_ON = [BatchIAMRolePolicyAttach]  # This is required otherwise policy would be dettached from Batchrole

    def pre_terraform_apply(self):
        ec2_client = get_ec2_client(self.input.aws_access_key, self.input.aws_secret_key, self.input.aws_region)
        ec2_key_pair = self.get_input_attr('ec2_key_pair')
        try:
            key_obj = ec2_client.create_key_pair(KeyName=ec2_key_pair)
            with open(os.path.join(Settings.OUTPUT_DIR, ec2_key_pair + ".pem"), "w") as keyfile:
                keyfile.write(key_obj['KeyMaterial'])
        except Exception as e:
            pass

    def check_batch_jobs_running(self):
        envs = get_compute_environments(
            [self.get_input_attr('compute_environment_name')],
            self.input.aws_access_key,
            self.input.aws_secret_key,
            self.input.aws_region)

        if not len(envs):
            return

        if envs[0]['computeResources']['desiredvCpus'] > int(self.get_input_attr('desired_vcpus')):
            return True

    def pre_generate_terraform(self):
        warn_msg = "Batch Jobs are running, please try after it gets completed."
        if self.check_batch_jobs_running():
            message = "\n\t ** %s **\n" % warn_msg
            print(MsgMixin.BERROR_ANSI + message + MsgMixin.RESET_ANSI)
            sys.exit()

    def pre_terraform_destroy(self):
        warn_msg = "Batch Jobs are running, please try after it gets completed OR manually cancel the jobs"
        if self.check_batch_jobs_running():
            raise Exception(warn_msg)

    def post_terraform_destroy(self):
        ec2_client = get_ec2_client(self.input.aws_access_key, self.input.aws_secret_key, self.input.aws_region)
        ec2_key_pair = self.get_input_attr('ec2_key_pair')
        try:
            key_obj = ec2_client.delete_key_pair(KeyName=ec2_key_pair)
        except Exception as e:
            print(ec2_key_pair + " Not able to delete Key Pair. Error: %s" % str(e))
