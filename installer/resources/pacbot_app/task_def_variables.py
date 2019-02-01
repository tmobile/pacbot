from resources.pacbot_app.cloudwatch_log_groups import UiCloudWatchLogGroup, ApiCloudWatchLogGroup
from resources.pacbot_app.ecr import APIEcrRepository, UIEcrRepository
from resources.data.aws_info import AwsRegion
from resources.pacbot_app.alb import ApplicationLoadBalancer
from resources.datastore.es import ESDomain
from resources.datastore.db import MySQLDatabase
from resources.datastore.redshift import RedshiftCluster
from resources.iam.ecs_role import ECSRole
from resources.lambda_submit.function import SubmitJobLambdaFunction
from resources.lambda_rule_engine.function import RuleEngineLambdaFunction
from core.config import Settings
from resources.s3.bucket import BucketStorage
import json


class ContainerDefinitions:
    ui_image = UIEcrRepository.get_output_attr('repository_url') + ":" + "latest"
    api_image = APIEcrRepository.get_output_attr('repository_url') + ":" + "latest"
    ui_cw_log_group = UiCloudWatchLogGroup.get_output_attr('name')
    api_cw_log_group = ApiCloudWatchLogGroup.get_output_attr('name')
    CONFIG_PASSWORD = "pacman"
    CONFIG_SERVER_URL = ApplicationLoadBalancer.get_api_server_url('config')
    ES_CLUSTER_NAME = ESDomain.get_input_attr('domain_name')
    ES_HEIMDALL_HOST_NAME = ESDomain.get_output_attr('endpoint')
    ES_HEIMDALL_PORT = str(ESDomain.get_es_port())
    ES_HOST_NAME = ESDomain.get_output_attr('endpoint')
    ES_PORT = str(ESDomain.get_es_port())
    LOGGING_ES_HOST_NAME = ESDomain.get_output_attr('endpoint')
    LOGGING_ES_PORT = str(ESDomain.get_es_port())
    PACMAN_HOST_NAME = ApplicationLoadBalancer.get_http_url()
    RDS_USERNAME = MySQLDatabase.get_input_attr('username')
    RDS_PASSWORD = MySQLDatabase.get_input_attr('password')
    RDS_URL = MySQLDatabase.get_rds_db_url()
    REDSHIFT_URL = RedshiftCluster.get_redshift_url()
    REDSHIFT_USER_NAME = RedshiftCluster.get_input_attr('master_username')
    REDSHIFT_PASSWORD = RedshiftCluster.get_input_attr('master_password')
    ES_UPDATE_HOST = ESDomain.get_output_attr('endpoint')
    ES_UPDATE_PORT = str(ESDomain.get_es_port())
    ES_UPDATE_CLUSTER_NAME = ESDomain.get_input_attr('domain_name')
    LDAP_DOMAIN = "http://localhost"
    LDAP_PORT = "389"
    LDAP_BASEDN = "http://localhost"
    LDAP_HOSTLIST = "http://localhost"
    LDAP_RESPONSETIMEOUT = "60"
    LDAP_CONNECTIONTIMEOUT = "60"

    def get_container_definitions_without_env_vars(self, container_name):
        return {
            'name': container_name,
            "image": self.ui_image if container_name == 'nginx' else self.api_image,
            "essential": True,
            "entrypoint": ["sh", "-c"],
            "command": ["sh /entrypoint.sh"],
            "portMappings": [
                {
                    "containerPort": 80,
                    "hostPort": 80
                }
            ],
            "memory": 1024,
            "networkMode": "awsvpc",
            "logConfiguration": {
                "logDriver": "awslogs",
                "options": {
                    "awslogs-group": self.ui_cw_log_group if container_name == 'nginx' else self.api_cw_log_group,
                    "awslogs-region": AwsRegion.get_output_attr('name'),
                    "awslogs-stream-prefix": Settings.RESOURCE_NAME_PREFIX + "-" + container_name
                }
            }
        }

    def get_container_definitions(self, container_name):
        definitions = self.get_container_definitions_without_env_vars(container_name)
        env_vars = self._get_env_vars_for_container_service(container_name)
        if env_vars:
            definitions['environment'] = env_vars

        return json.dumps([definitions])

    def _get_env_vars_for_container_service(self, container_name):
        def function_not_found():
            return None
        fun_name = "get_%s_container_env_vars" % container_name.replace('-', '_')
        call_fun = getattr(self, fun_name, function_not_found)

        return call_fun()

    def get_config_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "config.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD}
        ]

    def get_admin_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-admin.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "ES_CLUSTER_NAME", 'value': self.ES_CLUSTER_NAME},
            {'name': "ES_HEIMDALL_HOST_NAME", 'value': self.ES_HEIMDALL_HOST_NAME},
            {'name': "ES_HEIMDALL_PORT", 'value': self.ES_HEIMDALL_PORT},
            {'name': "ES_HOST_NAME", 'value': self.ES_HOST_NAME},
            {'name': "ES_PORT", 'value': self.ES_PORT},
            {'name': "LOGGING_ES_HOST_NAME", 'value': self.LOGGING_ES_HOST_NAME},
            {'name': "LOGGING_ES_PORT", 'value': self.LOGGING_ES_PORT},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "RDS_PASSWORD", 'value': self.RDS_PASSWORD},
            {'name': "RDS_URL", 'value': self.RDS_URL},
            {'name': "RDS_USERNAME", 'value': self.RDS_USERNAME},
            {'name': "ES_UPDATE_HOST", 'value': self.ES_UPDATE_HOST},
            {'name': "ES_UPDATE_PORT", 'value': self.ES_UPDATE_PORT},
            {'name': "ES_UPDATE_CLUSTER_NAME", 'value': self.ES_UPDATE_CLUSTER_NAME},
            {'name': "SECURITY_USERNAME", 'value': "admin"},
            {'name': "SECURITY_PASSWORD", 'value': "admin@123"},
            {'name': "ACCESS_KEY", 'value': "test_key_1"},
            {'name': "SECRET_KEY", 'value': "test_key_2"},
            {'name': "DOMAIN_URL", 'value': ApplicationLoadBalancer.get_api_server_url('admin')},
            {'name': "ADMIN_SERVER", 'value': "http://localhost/pacmonitor"},
            {'name': "ROLE_ARN", 'value': ECSRole.get_output_attr('arn')},
            {'name': "JOB_FUNCTION_NAME", 'value': SubmitJobLambdaFunction.get_input_attr('function_name')},
            {'name': "JOB_FUNCTION_ARN", 'value': SubmitJobLambdaFunction.get_output_attr('arn')},
            {'name': "JOB_LAMBDA_REGION", 'value': AwsRegion.get_output_attr('name')},
            {'name': "JOB_BUCKET_REGION", 'value': AwsRegion.get_output_attr('name')},
            {'name': "RULE_FUNCTION_NAME", 'value': RuleEngineLambdaFunction.get_input_attr('function_name')},
            {'name': "RULE_FUNCTION_ARN", 'value': RuleEngineLambdaFunction.get_output_attr('arn')},
            {'name': "RULE_BUCKET_REGION", 'value': AwsRegion.get_output_attr('name')},
            {'name': "RULE_LAMBDA_REGION", 'value': AwsRegion.get_output_attr('name')},
            {'name': "RULE_JOB_BUCKET_NAME", 'value': BucketStorage.get_output_attr('bucket')}
        ]

    def get_compliance_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-compliance.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "ES_CLUSTER_NAME", 'value': self.ES_CLUSTER_NAME},
            {'name': "ES_HEIMDALL_HOST_NAME", 'value': self.ES_HEIMDALL_HOST_NAME},
            {'name': "ES_HEIMDALL_PORT", 'value': self.ES_HEIMDALL_PORT},
            {'name': "ES_HOST_NAME", 'value': self.ES_HOST_NAME},
            {'name': "ES_PORT", 'value': self.ES_PORT},
            {'name': "LOGGING_ES_HOST_NAME", 'value': self.LOGGING_ES_HOST_NAME},
            {'name': "LOGGING_ES_PORT", 'value': self.LOGGING_ES_PORT},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "RDS_PASSWORD", 'value': self.RDS_PASSWORD},
            {'name': "RDS_URL", 'value': self.RDS_URL},
            {'name': "RDS_USERNAME", 'value': self.RDS_USERNAME},
            {'name': "REDSHIFT_URL", 'value': self.REDSHIFT_URL},
            {'name': "REDSHIFT_USER_NAME", 'value': self.REDSHIFT_USER_NAME},
            {'name': "REDSHIFT_PASSWORD", 'value': self.REDSHIFT_PASSWORD},
            {'name': "ES_UPDATE_HOST", 'value': self.ES_UPDATE_HOST},
            {'name': "ES_UPDATE_PORT", 'value': self.ES_UPDATE_PORT},
            {'name': "ES_UPDATE_CLUSTER_NAME", 'value': self.ES_UPDATE_CLUSTER_NAME},
            {'name': "LDAP_DOMAIN", 'value': self.LDAP_DOMAIN},
            {'name': "LDAP_BASEDN", 'value': self.LDAP_BASEDN},
            {'name': "LDAP_PORT", 'value': self.LDAP_PORT},
            {'name': "LDAP_RESPONSETIMEOUT", 'value': self.LDAP_RESPONSETIMEOUT},
            {'name': "LDAP_CONNECTIONTIMEOUT", 'value': self.LDAP_CONNECTIONTIMEOUT},
            {'name': "LDAP_HOSTLIST", 'value': self.LDAP_HOSTLIST},
            {'name': "CERTIFICATE_FEATURE_ENABLED", 'value': "false"},
            {'name': "PATCHING_FEATURE_ENABLED", 'value': "false"},
            {'name': "VULNERABILITY_FEATURE_ENABLED", 'value': "false"}
        ]

    def get_notifications_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-notification.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "ES_CLUSTER_NAME", 'value': self.ES_CLUSTER_NAME},
            {'name': "ES_HEIMDALL_HOST_NAME", 'value': self.ES_HEIMDALL_HOST_NAME},
            {'name': "ES_HEIMDALL_PORT", 'value': self.ES_HEIMDALL_PORT},
            {'name': "ES_HOST_NAME", 'value': self.ES_HOST_NAME},
            {'name': "ES_PORT", 'value': self.ES_PORT},
            {'name': "LOGGING_ES_HOST_NAME", 'value': self.LOGGING_ES_HOST_NAME},
            {'name': "LOGGING_ES_PORT", 'value': self.LOGGING_ES_PORT},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "RDS_PASSWORD", 'value': self.RDS_PASSWORD},
            {'name': "RDS_URL", 'value': self.RDS_URL},
            {'name': "RDS_USERNAME", 'value': self.RDS_USERNAME},
            {'name': "REDSHIFT_URL", 'value': self.REDSHIFT_URL},
            {'name': "REDSHIFT_USER_NAME", 'value': self.REDSHIFT_USER_NAME},
            {'name': "REDSHIFT_PASSWORD", 'value': self.REDSHIFT_PASSWORD},
            {'name': "ES_UPDATE_HOST", 'value': self.ES_UPDATE_HOST},
            {'name': "ES_UPDATE_PORT", 'value': self.ES_UPDATE_PORT},
            {'name': "ES_UPDATE_CLUSTER_NAME", 'value': self.ES_UPDATE_CLUSTER_NAME},
            {'name': "LDAP_DOMAIN", 'value': self.LDAP_DOMAIN},
            {'name': "LDAP_BASEDN", 'value': self.LDAP_BASEDN},
            {'name': "LDAP_PORT", 'value': self.LDAP_PORT},
            {'name': "LDAP_RESPONSETIMEOUT", 'value': self.LDAP_RESPONSETIMEOUT},
            {'name': "LDAP_CONNECTIONTIMEOUT", 'value': self.LDAP_CONNECTIONTIMEOUT},
            {'name': "LDAP_HOSTLIST", 'value': self.LDAP_HOSTLIST}
        ]

    def get_statistics_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-statistics.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "ES_CLUSTER_NAME", 'value': self.ES_CLUSTER_NAME},
            {'name': "ES_HEIMDALL_HOST_NAME", 'value': self.ES_HEIMDALL_HOST_NAME},
            {'name': "ES_HEIMDALL_PORT", 'value': self.ES_HEIMDALL_PORT},
            {'name': "ES_HOST_NAME", 'value': self.ES_HOST_NAME},
            {'name': "ES_PORT", 'value': self.ES_PORT},
            {'name': "LOGGING_ES_HOST_NAME", 'value': self.LOGGING_ES_HOST_NAME},
            {'name': "LOGGING_ES_PORT", 'value': self.LOGGING_ES_PORT},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "RDS_PASSWORD", 'value': self.RDS_PASSWORD},
            {'name': "RDS_URL", 'value': self.RDS_URL},
            {'name': "RDS_USERNAME", 'value': self.RDS_USERNAME},
            {'name': "REDSHIFT_URL", 'value': self.REDSHIFT_URL},
            {'name': "REDSHIFT_USER_NAME", 'value': self.REDSHIFT_USER_NAME},
            {'name': "REDSHIFT_PASSWORD", 'value': self.REDSHIFT_PASSWORD},
            {'name': "ES_UPDATE_HOST", 'value': self.ES_UPDATE_HOST},
            {'name': "ES_UPDATE_PORT", 'value': self.ES_UPDATE_PORT},
            {'name': "ES_UPDATE_CLUSTER_NAME", 'value': self.ES_UPDATE_CLUSTER_NAME},
            {'name': "LDAP_DOMAIN", 'value': self.LDAP_DOMAIN},
            {'name': "LDAP_BASEDN", 'value': self.LDAP_BASEDN},
            {'name': "LDAP_PORT", 'value': self.LDAP_PORT},
            {'name': "LDAP_RESPONSETIMEOUT", 'value': self.LDAP_RESPONSETIMEOUT},
            {'name': "LDAP_CONNECTIONTIMEOUT", 'value': self.LDAP_CONNECTIONTIMEOUT},
            {'name': "LDAP_HOSTLIST", 'value': self.LDAP_HOSTLIST}
        ]

    def get_asset_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-asset.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "ES_CLUSTER_NAME", 'value': self.ES_CLUSTER_NAME},
            {'name': "ES_HEIMDALL_HOST_NAME", 'value': self.ES_HEIMDALL_HOST_NAME},
            {'name': "ES_HEIMDALL_PORT", 'value': self.ES_HEIMDALL_PORT},
            {'name': "ES_HOST_NAME", 'value': self.ES_HOST_NAME},
            {'name': "ES_PORT", 'value': self.ES_PORT},
            {'name': "LOGGING_ES_HOST_NAME", 'value': self.LOGGING_ES_HOST_NAME},
            {'name': "LOGGING_ES_PORT", 'value': self.LOGGING_ES_PORT},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "RDS_PASSWORD", 'value': self.RDS_PASSWORD},
            {'name': "RDS_URL", 'value': self.RDS_URL},
            {'name': "RDS_USERNAME", 'value': self.RDS_USERNAME},
            {'name': "REDSHIFT_URL", 'value': self.REDSHIFT_URL},
            {'name': "REDSHIFT_USER_NAME", 'value': self.REDSHIFT_USER_NAME},
            {'name': "REDSHIFT_PASSWORD", 'value': self.REDSHIFT_PASSWORD},
            {'name': "ES_UPDATE_HOST", 'value': self.ES_UPDATE_HOST},
            {'name': "ES_UPDATE_PORT", 'value': self.ES_UPDATE_PORT},
            {'name': "ES_UPDATE_CLUSTER_NAME", 'value': self.ES_UPDATE_CLUSTER_NAME},
            {'name': "LDAP_DOMAIN", 'value': self.LDAP_DOMAIN},
            {'name': "LDAP_BASEDN", 'value': self.LDAP_BASEDN},
            {'name': "LDAP_PORT", 'value': self.LDAP_PORT},
            {'name': "LDAP_RESPONSETIMEOUT", 'value': self.LDAP_RESPONSETIMEOUT},
            {'name': "LDAP_CONNECTIONTIMEOUT", 'value': self.LDAP_CONNECTIONTIMEOUT},
            {'name': "LDAP_HOSTLIST", 'value': self.LDAP_HOSTLIST},
            {'name': "CLOUD_INSIGHTS_COST_URL", 'value': "http://localhost"},
            {'name': "CLOUD_INSIGHTS_TOKEN_URL", 'value': "http://localhost"},
            {'name': "SVC_CORP_PASSWORD", 'value': "password"},
            {'name': "SVC_CORP_USER_ID", 'value': "testid"}
        ]

    def get_auth_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-auth.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "DOMAIN_URL", 'value': ApplicationLoadBalancer.get_api_server_url('auth')},
            {'name': "ES_CLUSTER_NAME", 'value': self.ES_CLUSTER_NAME},
            {'name': "ES_HEIMDALL_HOST_NAME", 'value': self.ES_HEIMDALL_HOST_NAME},
            {'name': "ES_HEIMDALL_PORT", 'value': self.ES_HEIMDALL_PORT},
            {'name': "ES_HOST_NAME", 'value': self.ES_HOST_NAME},
            {'name': "ES_PORT", 'value': self.ES_PORT},
            {'name': "LOGGING_ES_HOST_NAME", 'value': self.LOGGING_ES_HOST_NAME},
            {'name': "LOGGING_ES_PORT", 'value': self.LOGGING_ES_PORT},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "RDS_PASSWORD", 'value': self.RDS_PASSWORD},
            {'name': "RDS_URL", 'value': self.RDS_URL},
            {'name': "RDS_USERNAME", 'value': self.RDS_USERNAME},
            {'name': "REDSHIFT_URL", 'value': self.REDSHIFT_URL},
            {'name': "REDSHIFT_USER_NAME", 'value': self.REDSHIFT_USER_NAME},
            {'name': "REDSHIFT_PASSWORD", 'value': self.REDSHIFT_PASSWORD},
            {'name': "ES_UPDATE_HOST", 'value': self.ES_UPDATE_HOST},
            {'name': "ES_UPDATE_PORT", 'value': self.ES_UPDATE_PORT},
            {'name': "ES_UPDATE_CLUSTER_NAME", 'value': self.ES_UPDATE_CLUSTER_NAME},
            {'name': "LDAP_DOMAIN", 'value': self.LDAP_DOMAIN},
            {'name': "LDAP_BASEDN", 'value': self.LDAP_BASEDN},
            {'name': "LDAP_PORT", 'value': self.LDAP_PORT},
            {'name': "LDAP_RESPONSETIMEOUT", 'value': self.LDAP_RESPONSETIMEOUT},
            {'name': "LDAP_CONNECTIONTIMEOUT", 'value': self.LDAP_CONNECTIONTIMEOUT},
            {'name': "LDAP_HOSTLIST", 'value': self.LDAP_HOSTLIST},
            {'name': "OAUTH2_CLIENT_ID", 'value': "22e14922-87d7-4ee4-a470-da0bb10d45d3"}
        ]
