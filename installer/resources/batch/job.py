from core.terraform.resources.aws.batch import BatchJobDefinitionResource, BatchJobQueueResource
from core.providers.aws.boto3.ecs import delete_task_definition
from resources.datastore.es import ESDomain
from resources.batch.env import RuleEngineBatchJobEnv
from resources.batch.ecr import RuleEngineEcrRepository
from resources.data.aws_info import AwsAccount
from resources.pacbot_app.alb import ApplicationLoadBalancer
from core.config import Settings
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
        'memory': 5000,
        'vcpus': 1,
        'environment': [
            {'name': "ES_HOST", 'value': ESDomain.get_http_url_with_port()},
            {'name': "BASE_AWS_ACCOUNT", 'value': AwsAccount.get_output_attr('account_id')},
            {'name': "ES_URI", 'value': ESDomain.get_http_url_with_port()},
            {'name': "HEIMDALL_URI", 'value': ESDomain.get_http_url_with_port()},
            {'name': "PACMAN_API_URI", 'value': ApplicationLoadBalancer.get_api_base_url()}
        ]
    })

    def post_terraform_destroy(self):
        delete_task_definition(
            Settings.AWS_ACCESS_KEY,
            Settings.AWS_SECRET_KEY,
            Settings.AWS_REGION,
            self.get_input_attr('name')
        )


class RuleEngineJobQueue(BatchJobQueueResource):
    name = "rule-engine"
    state = "ENABLED"
    priority = 6
    compute_environments = [RuleEngineBatchJobEnv.get_output_attr('arn')]


class BatchJobsQueue(BatchJobQueueResource):
    name = "data"
    state = "ENABLED"
    priority = 6
    compute_environments = [RuleEngineBatchJobEnv.get_output_attr('arn')]
