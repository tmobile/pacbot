from core.terraform.resources.misc import NullResource
from core.terraform.utils import get_terraform_scripts_and_files_dir, get_terraform_scripts_dir, get_terraform_provider_file
from core.config import Settings
from resources.datastore.db import MySQLDatabase
from resources.datastore.es import ESDomain
from resources.data.aws_info import AwsAccount, AwsRegion
from resources.pacbot_app.cloudwatch_log_groups import UiCloudWatchLogGroup, ApiCloudWatchLogGroup
from resources.pacbot_app.ecr import APIEcrRepository, UIEcrRepository
from resources.data.aws_info import AwsRegion
from resources.pacbot_app.alb import ApplicationLoadBalancer
from resources.datastore.es import ESDomain
from resources.iam.ecs_role import ECSRole
from resources.iam.base_role import BaseRole
from resources.lambda_submit.function import SubmitJobLambdaFunction
from resources.lambda_rule_engine.function import RuleEngineLambdaFunction
from resources.s3.bucket import BucketStorage

from shutil import copy2
import os


class ReplaceSQLPlaceHolder(NullResource):
    dest_file = os.path.join(get_terraform_scripts_and_files_dir(), 'DB_With_Values.sql')
    triggers = {'version': "1.1"}

    DEPENDS_ON = [MySQLDatabase, ESDomain]

    def get_provisioners(self):
        script = os.path.join(get_terraform_scripts_dir(), 'sql_replace_placeholder.py')
        db_user_name = MySQLDatabase.get_input_attr('username')
        db_password = MySQLDatabase.get_input_attr('password')
        db_host = MySQLDatabase.get_output_attr('endpoint')
        local_execs = [
            {
                'local-exec': {
                    'command': script,
                    'environment': {
                        'SQL_FILE_PATH': self.dest_file,
                        'ENV_region': AwsRegion.get_output_attr('name'),
                        'ENV_account': AwsAccount.get_output_attr('account_id'),
                        'ENV_eshost': ESDomain.get_http_url(),
                        'ENV_esport': ESDomain.get_es_port(),
                        'ENV_LOGGING_ES_HOST_NAME': ESDomain.get_output_attr('endpoint'),
                        'ENV_LOGGING_ES_PORT': str(ESDomain.get_es_port()),
                        'ENV_ES_HOST_NAME': ESDomain.get_output_attr('endpoint'),
                        'ENV_ES_PORT': str(ESDomain.get_es_port()),
                        'ENV_ES_CLUSTER_NAME': ESDomain.get_input_attr('domain_name'),
                        'ENV_ES_PORT_ADMIN': str(ESDomain.get_es_port()),
                        'ENV_ES_HEIMDALL_HOST_NAME': ESDomain.get_output_attr('endpoint'),
                        'ENV_ES_HEIMDALL_PORT': str(ESDomain.get_es_port()),
                        'ENV_ES_HEIMDALL_CLUSTER_NAME': ESDomain.get_input_attr('domain_name'),
                        'ENV_ES_HEIMDALL_PORT_ADMIN': str(ESDomain.get_es_port()),
                        'ENV_ES_UPDATE_HOST': ESDomain.get_output_attr('endpoint'),
                        'ENV_ES_UPDATE_PORT': str(ESDomain.get_es_port()),
                        'ENV_ES_UPDATE_CLUSTER_NAME': ESDomain.get_input_attr('domain_name'),
                        'ENV_PACMAN_HOST_NAME': ApplicationLoadBalancer.get_http_url(),
                        'ENV_RDS_URL': MySQLDatabase.get_rds_db_url(),
                        'ENV_RDS_USERNAME': MySQLDatabase.get_input_attr('username'),
                        'ENV_RDS_PASSWORD': MySQLDatabase.get_input_attr('password'),
                        'ENV_JOB_BUCKET_REGION': AwsRegion.get_output_attr('name'),
                        'ENV_RULE_JOB_BUCKET_NAME': BucketStorage.get_output_attr('bucket'),
                        'ENV_JOB_LAMBDA_REGION': AwsRegion.get_output_attr('name'),
                        'ENV_JOB_FUNCTION_NAME': SubmitJobLambdaFunction.get_input_attr('function_name'),
                        'ENV_JOB_FUNCTION_ARN': SubmitJobLambdaFunction.get_output_attr('arn'),
                        'ENV_RULE_BUCKET_REGION': AwsRegion.get_output_attr('name'),
                        'ENV_RULE_JOB_BUCKET_NAME': BucketStorage.get_output_attr('bucket'),
                        'ENV_RULE_LAMBDA_REGION': AwsRegion.get_output_attr('name'),
                        'ENV_RULE_FUNCTION_NAME': RuleEngineLambdaFunction.get_input_attr('function_name'),
                        'ENV_RULE_FUNCTION_ARN': RuleEngineLambdaFunction.get_output_attr('arn'),
                        'ENV_CLOUD_INSIGHTS_TOKEN_URL': "http://localhost",
                        'ENV_CLOUD_INSIGHTS_COST_URL': "http://localhost",
                        'ENV_SVC_CORP_USER_ID': "testid",
                        'ENV_SVC_CORP_PASSWORD': "password",
                        'ENV_CERTIFICATE_FEATURE_ENABLED': "false",
                        'ENV_PATCHING_FEATURE_ENABLED': "false",
                        'ENV_VULNERABILITY_FEATURE_ENABLED': str(Settings.get('ENABLE_VULNERABILITY_FEATURE', False)).lower(),
                        'ENV_MAIL_SERVER': Settings.MAIL_SERVER,
                        'ENV_PACMAN_S3': "pacman-email-templates",
                        'ENV_DATA_IN_DIR': "inventory",
                        'ENV_DATA_BKP_DIR': "backup",
                        'ENV_PAC_ROLE': BaseRole.get_input_attr('name'),
                        'ENV_BASE_REGION': AwsRegion.get_output_attr('name'),
                        'ENV_DATA_IN_S3': BucketStorage.get_output_attr('bucket'),
                        'ENV_BASE_ACCOUNT': AwsAccount.get_output_attr('account_id'),
                        'ENV_PAC_RO_ROLE': BaseRole.get_input_attr('name'),
                        'ENV_MAIL_SERVER_PORT': Settings.MAIL_SERVER_PORT,
                        'ENV_MAIL_PROTOCOL': Settings.MAIL_PROTOCOL,
                        'ENV_MAIL_SERVER_USER': Settings.MAIL_SERVER_USER,
                        'ENV_MAIL_SERVER_PWD': Settings.MAIL_SERVER_PWD,
                        'ENV_MAIL_SMTP_AUTH': Settings.MAIL_SMTP_AUTH,
                        'ENV_MAIL_SMTP_SSL_ENABLE': Settings.MAIL_SMTP_SSL_ENABLE,
                        'ENV_MAIL_SMTP_SSL_TEST_CONNECTION': Settings.MAIL_SMTP_SSL_TEST_CONNECTION,
                        'ENV_PACMAN_LOGIN_USER_NAME': "admin@pacbot.org",
                        'ENV_PACMAN_LOGIN_PASSWORD': "pacman",
                        'ENV_CONFIG_CREDENTIALS': "dXNlcjpwYWNtYW4=",
                        'ENV_CONFIG_SERVICE_URL': ApplicationLoadBalancer.get_http_url() + "/api/config/rule/prd/latest",
                        'ENV_PACBOT_AUTOFIX_RESOURCEOWNER_FALLBACK_MAILID': Settings.get('USER_EMAIL_ID', "")
                    },
                    'interpreter': [Settings.PYTHON_INTERPRETER]
                }
            }
        ]

        return local_execs

    def pre_generate_terraform(self):
        src_file = os.path.join(Settings.BASE_APP_DIR, 'resources', 'pacbot_app', 'files', 'DB.sql')
        copy2(src_file, self.dest_file)


class ImportDbSql(NullResource):
    triggers = {'version': "1.1"}

    DEPENDS_ON = [MySQLDatabase, ReplaceSQLPlaceHolder]

    def get_provisioners(self):
        db_user_name = MySQLDatabase.get_input_attr('username')
        db_password = MySQLDatabase.get_input_attr('password')
        db_host = MySQLDatabase.get_output_attr('address')
        local_execs = [
            {
                'local-exec': {
                    'command': "mysql -u %s --password=%s -h %s < %s" % (db_user_name, db_password, db_host, ReplaceSQLPlaceHolder.dest_file)
                }
            }

        ]

        return local_execs
