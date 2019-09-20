from resources.pacbot_app.cloudwatch_log_groups import UiCloudWatchLogGroup, ApiCloudWatchLogGroup
from resources.pacbot_app.ecr import APIEcrRepository, UIEcrRepository
from resources.data.aws_info import AwsRegion
from resources.pacbot_app.alb import ApplicationLoadBalancer
from resources.datastore.db import MySQLDatabase
from core.config import Settings
import json


class ContainerDefinitions:
    """Friend class for getting the container definitions of each service"""
    ui_image = UIEcrRepository.get_output_attr('repository_url') + ":" + "latest"
    api_image = APIEcrRepository.get_output_attr('repository_url') + ":" + "latest"
    ui_cw_log_group = UiCloudWatchLogGroup.get_output_attr('name')
    api_cw_log_group = ApiCloudWatchLogGroup.get_output_attr('name')
    CONFIG_PASSWORD = "pacman"
    CONFIG_SERVER_URL = ApplicationLoadBalancer.get_api_server_url('config')
    PACMAN_HOST_NAME = ApplicationLoadBalancer.get_http_url()
    RDS_USERNAME = MySQLDatabase.get_input_attr('username')
    RDS_PASSWORD = MySQLDatabase.get_input_attr('password')
    RDS_URL = MySQLDatabase.get_rds_db_url()

    def get_container_definitions_without_env_vars(self, container_name):
        """
        This method returns the basic common container definitioons for all task definitions

        Returns:
            container_definitions (dict): Container definitions
        """
        memory = 1024 if container_name == "nginx" else 3072
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
            "memory": memory,
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
        """
        This method find complete container definitions for a task definiiton and returns it

        Returns:
            container_definitions (json): Josn data of complete Container definitions
        """
        definitions = self.get_container_definitions_without_env_vars(container_name)
        env_vars = self._get_env_vars_for_container_service(container_name)
        if env_vars:
            definitions['environment'] = env_vars

        return json.dumps([definitions])

    def _get_env_vars_for_container_service(self, container_name):
        """
        Dynamically call the function based on the container name to get all environment variables

        Returns:
            env_variables (list): List of dict of env variables
        """
        def function_not_found():
            return None
        fun_name = "get_%s_container_env_vars" % container_name.replace('-', '_')
        call_fun = getattr(self, fun_name, function_not_found)

        return call_fun()

    def get_config_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "config.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "RDS_PASSWORD", 'value': self.RDS_PASSWORD},
            {'name': "RDS_URL", 'value': self.RDS_URL},
            {'name': "RDS_USERNAME", 'value': self.RDS_USERNAME},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
        ]

    def get_admin_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-admin.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "DOMAIN_URL", 'value': ApplicationLoadBalancer.get_api_server_url('admin')}
        ]

    def get_compliance_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-compliance.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "DOMAIN_URL", 'value': ApplicationLoadBalancer.get_api_server_url('compliance')}
        ]

    def get_notifications_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-notification.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "DOMAIN_URL", 'value': ApplicationLoadBalancer.get_api_server_url('notifications')}
        ]

    def get_statistics_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-statistics.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "DOMAIN_URL", 'value': ApplicationLoadBalancer.get_api_server_url('statistics')}
        ]

    def get_asset_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-asset.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "DOMAIN_URL", 'value': ApplicationLoadBalancer.get_api_server_url('asset')}
        ]

    def get_auth_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-auth.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "DOMAIN_URL", 'value': ApplicationLoadBalancer.get_api_server_url('auth')}
        ]

    def get_vulnerability_container_env_vars(self):
        return [
            {'name': "JAR_FILE", 'value': "pacman-api-vulnerability.jar"},
            {'name': "CONFIG_PASSWORD", 'value': self.CONFIG_PASSWORD},
            {'name': "CONFIG_SERVER_URL", 'value': self.CONFIG_SERVER_URL},
            {'name': "PACMAN_HOST_NAME", 'value': self.PACMAN_HOST_NAME},
            {'name': "DOMAIN_URL", 'value': ApplicationLoadBalancer.get_api_server_url('vulnerability')}
        ]
