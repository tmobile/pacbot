from core.terraform.resources.aws.aws_lambda import LambdaFunctionResource, LambdaPermission
from core.terraform.resources.aws.cloudwatch import CloudWatchEventRuleResource, CloudWatchEventTargetResource
from resources.datastore.es import ESDomainPolicy
from resources.datastore.db import MySQLDatabase
from resources.iam.lambda_role import LambdaRole
from resources.iam.base_role import BaseRole
from resources.s3.bucket import BucketStorage
from resources.batch.job import SubmitAndRuleEngineJobDefinition, BatchJobsQueue
from resources.data.aws_info import AwsAccount, AwsRegion
from resources.lambda_submit.s3_upload import UploadLambdaSubmitJobZipFile, BATCH_JOB_FILE_NAME
from resources.pacbot_app.alb import ApplicationLoadBalancer
import json


class SubmitJobLambdaFunction(LambdaFunctionResource):
    function_name = "datacollector"
    role = LambdaRole.get_output_attr('arn')
    handler = BATCH_JOB_FILE_NAME + ".lambda_handler"
    runtime = "python2.7"
    s3_bucket = BucketStorage.get_output_attr('bucket')
    s3_key = UploadLambdaSubmitJobZipFile.get_output_attr('id')
    environment = {
        'variables': {
            'JOB_QUEUE': BatchJobsQueue.get_input_attr('name'),
            'JOB_DEFINITION': SubmitAndRuleEngineJobDefinition.get_output_attr('arn'),
            'CONFIG_URL': ApplicationLoadBalancer.get_api_base_url() + "/config/batch,inventory/prd/latest",
            'CONFIG_CREDENTIALS': "dXNlcjpwYWNtYW4=",
            'CONFIG_SERVICE_URL': ApplicationLoadBalancer.get_http_url() + "/api/config/rule/prd/latest"
        }
    }

    DEPENDS_ON = [SubmitAndRuleEngineJobDefinition, BatchJobsQueue]


class DataCollectorEventRule(CloudWatchEventRuleResource):
    name = "AWS-Data-Collector"
    schedule_expression = "cron(0 * * * ? *)"

    DEPENDS_ON = [SubmitJobLambdaFunction]


class DataCollectorEventRuleLambdaPermission(LambdaPermission):
    statement_id = "AllowExecutionFromDataCollectorEvent"
    action = "lambda:InvokeFunction"
    function_name = SubmitJobLambdaFunction.get_output_attr('function_name')
    principal = "events.amazonaws.com"
    source_arn = DataCollectorEventRule.get_output_attr('arn')


class DataCollectorCloudWatchEventTarget(CloudWatchEventTargetResource):
    rule = DataCollectorEventRule.get_output_attr('name')
    arn = SubmitJobLambdaFunction.get_output_attr('arn')
    target_id = 'DataCollectorTarget'  # Unique identifier
    target_input = json.dumps({
        'jobName': "AWS-Data-Collector",
        'jobUuid': "pacman-aws-inventory-jar-with-dependencies",
        'jobType': "jar",
        'jobDesc': "AWS-Data-Collection",
        'environmentVariables': [
            {'name': "CONFIG_URL", 'value': ApplicationLoadBalancer.get_api_base_url() + "/config/batch,inventory/prd/latest"},
            {'name': "CONFIG_CREDENTIALS", 'value': "dXNlcjpwYWNtYW4="},
            {'name': "CONFIG_SERVICE_URL", 'value': ApplicationLoadBalancer.get_http_url() + "/api/config/rule/prd/latest"}
        ],
        'params': [
            {'encrypt': False, 'key': "package_hint", 'value': "com.tmobile.cso.pacman"},
            {'encrypt': False, 'key': "config_creds", 'value': "dXNlcjpwYWNtYW4="},
            {'encrypt': False, 'key': "accountinfo", 'value': AwsAccount.get_output_attr('account_id')},
        ]
    })


class DataShipperEventRule(CloudWatchEventRuleResource):
    name = "aws-redshift-es-data-shipper"
    schedule_expression = "cron(0 * * * ? *)"

    DEPENDS_ON = [SubmitJobLambdaFunction, ESDomainPolicy]


class DataShipperEventRuleLambdaPermission(LambdaPermission):
    statement_id = "AllowExecutionFromDataShipper"
    action = "lambda:InvokeFunction"
    function_name = SubmitJobLambdaFunction.get_output_attr('function_name')
    principal = "events.amazonaws.com"
    source_arn = DataShipperEventRule.get_output_attr('arn')


class DataShipperCloudWatchEventTarget(CloudWatchEventTargetResource):
    rule = DataShipperEventRule.get_output_attr('name')
    arn = SubmitJobLambdaFunction.get_output_attr('arn')
    target_id = 'DataShipperTarger'  # Unique identifier
    target_input = json.dumps({
        'jobName': "aws-redshift-es-data-shipper",
        'jobUuid': "data-shipper-jar-with-dependencies",
        'jobType': "jar",
        'jobDesc': "Ship aws data periodically from redshfit to ES",
        'environmentVariables': [
            {'name': "CONFIG_URL", 'value': ApplicationLoadBalancer.get_api_base_url() + "/config/batch,data-shipper/prd/latest"},
            {'name': "ASSET_API_URL", 'value': ApplicationLoadBalancer.get_api_version_url('asset')},
            {'name': "CMPL_API_URL", 'value': ApplicationLoadBalancer.get_api_version_url('compliance')},
            {'name': "AUTH_API_URL", 'value': ApplicationLoadBalancer.get_api_version_url('auth')},
            {'name': "CONFIG_CREDENTIALS", 'value': "dXNlcjpwYWNtYW4="},
            {'name': "CONFIG_SERVICE_URL", 'value': ApplicationLoadBalancer.get_http_url() + "/api/config/rule/prd/latest"}

        ],
        'params': [
            {'encrypt': False, 'key': "package_hint", 'value': "com.tmobile"},
            {'encrypt': False, 'key': "datasource", 'value': "aws"},
            {'encrypt': False, 'key': "config_creds", 'value': "dXNlcjpwYWNtYW4="},
            {'encrypt': False, 'key': "apiauthinfo",
                'value': "MjJlMTQ5MjItODdkNy00ZWU0LWE0NzAtZGEwYmIxMGQ0NWQzOmNzcldwYzVwN0pGRjR2RVpCa3dHQ0FoNjdrR1FHd1h2NDZxdWc3djVad3RLZw=="}
        ]
    })


class RecommendationsCollectorEventRule(CloudWatchEventRuleResource):
    name = "AWS-Recommendations-Collector"
    schedule_expression = "cron(0 * * * ? *)"

    DEPENDS_ON = [SubmitJobLambdaFunction]


class RecommendationsCollectorEventRuleLambdaPermission(LambdaPermission):
    statement_id = "AllowExecutionFromRecommendationsCollectorEvent"
    action = "lambda:InvokeFunction"
    function_name = SubmitJobLambdaFunction.get_output_attr('function_name')
    principal = "events.amazonaws.com"
    source_arn = RecommendationsCollectorEventRule.get_output_attr('arn')


class RecommendationsCollectorCloudWatchEventTarget(CloudWatchEventTargetResource):
    rule = RecommendationsCollectorEventRule.get_output_attr('name')
    arn = SubmitJobLambdaFunction.get_output_attr('arn')
    target_id = 'RecommendationsCollectorTarget'  # Unique identifier
    target_input = json.dumps({
        'jobName': "aws-recommendations-collector",
        'jobUuid': "aws-recommendations-collector",
        'jobType': "jar",
        'jobDesc': "Index trusted advisor checks as recommendations",
        'environmentVariables': [
        ],
        'params': [
            {'encrypt': False, 'key': "package_hint", 'value': "com.tmobile.cso.pacbot"},
            {'encrypt': False, 'key': "config_creds", 'value': "dXNlcjpwYWNtYW4="},
        ]
    })
