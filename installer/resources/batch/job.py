from core.terraform.resources.aws.batch import BatchJobDefinitionResource, BatchJobQueueResource
from core.providers.aws.boto3.ecs import deregister_task_definition
from core.config import Settings
from resources.datastore.es import ESDomain
from resources.batch.env import RuleEngineBatchJobEnv
from resources.batch.ecr import RuleEngineEcrRepository
from resources.data.aws_info import AwsAccount
from resources.pacbot_app.alb import ApplicationLoadBalancer
from resources.batch import utils
import json
import shutil


class SubmitAndRuleEngineJobDefinition(BatchJobDefinitionResource):
    name = 'rule-engine'
    jd_type = 'container'
    attempts = 2
    container_properties = json.dumps({
        'command': [
            "~/fetch_and_run.sh",
            "Ref::executableName",
            "Ref::params",
            "Ref::jvmMemParams",
            "Ref::ruleEngineExecutableName",
            "Ref::entryPoint"
        ],
        'image': RuleEngineEcrRepository.get_output_attr('repository_url'),
        'memory': 3072,
        'vcpus': 1,
        'environment': [
            {'name': "ES_HOST", 'value': ESDomain.get_http_url_with_port()},
            {'name': "BASE_AWS_ACCOUNT", 'value': AwsAccount.get_output_attr('account_id')},
            {'name': "ES_URI", 'value': ESDomain.get_http_url_with_port()},
            {'name': "HEIMDALL_URI", 'value': ESDomain.get_http_url_with_port()},
            {'name': "PACMAN_API_URI", 'value': ApplicationLoadBalancer.get_api_base_url()},
            {'name': "CONFIG_CREDENTIALS", 'value': "dXNlcjpwYWNtYW4="},
            {'name': "CONFIG_SERVICE_URL", 'value': ApplicationLoadBalancer.get_http_url() + "/api/config/rule/prd/latest"}
        ]
    })

    def post_terraform_destroy(self):
        deregister_task_definition(
            self.get_input_attr('name'),
            Settings.AWS_AUTH_CRED
        )

    def pre_terraform_destroy(self):
        compute_env = RuleEngineBatchJobEnv.get_input_attr('compute_environment_name')
        job_definition = self.get_input_attr('name')
        utils.remove_batch_job_related_resources(compute_env, job_definition)


class RuleEngineJobQueue(BatchJobQueueResource):
    name = "rule-engine"
    state = Settings.get('JOB_QUEUE_STATUS', "ENABLED")
    priority = 6
    compute_environments = [RuleEngineBatchJobEnv.get_output_attr('arn')]


class BatchJobsQueue(BatchJobQueueResource):
    name = "data"
    state = Settings.get('JOB_QUEUE_STATUS', "ENABLED")
    priority = 6
    compute_environments = [RuleEngineBatchJobEnv.get_output_attr('arn')]
