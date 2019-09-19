from core.terraform.resources.aws.ecs import ECSServiceResource
from core.terraform.resources.aws.ecs import ECSClusterResource
from core.terraform.resources.misc import NullResource
from core.config import Settings
from resources.pacbot_app import ecs_task_defintions as td
from resources.pacbot_app import alb_target_groups as tg
from resources.vpc.security_group import InfraSecurityGroupResource
from resources.pacbot_app import alb_listener_rules as alr
from resources.pacbot_app.build_ui_and_api import BuildUiAndApis
from resources.pacbot_app.import_db import ImportDbSql
from resources.pacbot_app.utils import need_to_deploy_vulnerability_service
import os


class ApplicationECSCluster(ECSClusterResource):
    name = ""


class BaseEcsService:
    desired_count = 1
    launch_type = "FARGATE"
    cluster = ApplicationECSCluster.get_output_attr('id')
    network_configuration_security_groups = [InfraSecurityGroupResource.get_output_attr('id')]
    network_configuration_subnets = Settings.get('VPC')['SUBNETS']
    network_configuration_assign_public_ip = True
    load_balancer_container_port = 80
    tags = None
    # propagate_tags = "SERVICE"  #The new ARN and resource ID format must be enabled to propagate tags


class NginxEcsService(BaseEcsService, ECSServiceResource):
    name = "webapp"
    task_definition = td.NginxEcsTaskDefinition.get_output_attr('arn')
    load_balancer_target_group_arn = tg.NginxALBTargetGroup.get_output_attr('arn')
    load_balancer_container_name = td.NginxEcsTaskDefinition.container_name
    DEPENDS_ON = [BuildUiAndApis, alr.ApplicationLoadBalancerListener]


class ConfigEcsService(BaseEcsService, ECSServiceResource):
    name = "config"
    task_definition = td.ConfigEcsTaskDefinition.get_output_attr('arn')
    load_balancer_target_group_arn = tg.ConfigALBTargetGroup.get_output_attr('arn')
    load_balancer_container_name = "config"
    DEPENDS_ON = [BuildUiAndApis, alr.ConfigALBListenerRule, ImportDbSql]


class WaitConfigServiceToUp(NullResource):
    DEPENDS_ON = [ConfigEcsService]

    def get_provisioners(self):
        '''
        This is to make config service run first as other services has dependancy on it
        '''
        return [{
            'local-exec': {
                'command': "import time; time.sleep(30)",
                'interpreter': [Settings.PYTHON_INTERPRETER, "-c"]
            }
        }]


class AdminEcsService(BaseEcsService, ECSServiceResource):
    name = "admin"
    task_definition = td.AdminEcsTaskDefinition.get_output_attr('arn')
    load_balancer_target_group_arn = tg.AdminALBTargetGroup.get_output_attr('arn')
    load_balancer_container_name = "admin"
    DEPENDS_ON = [alr.AdminALBListenerRule, WaitConfigServiceToUp]


class ComplianceEcsService(BaseEcsService, ECSServiceResource):
    name = "compliance"
    task_definition = td.ComplianceEcsTaskDefinition.get_output_attr('arn')
    load_balancer_target_group_arn = tg.ComplianceALBTargetGroup.get_output_attr('arn')
    load_balancer_container_name = "compliance"
    DEPENDS_ON = [alr.ComplianceALBListenerRule, WaitConfigServiceToUp]


class NotificationsEcsService(BaseEcsService, ECSServiceResource):
    name = "notifications"
    task_definition = td.NotificationsEcsTaskDefinition.get_output_attr('arn')
    load_balancer_target_group_arn = tg.NotificationsALBTargetGroup.get_output_attr('arn')
    load_balancer_container_name = "notifications"
    DEPENDS_ON = [alr.NotificationsALBListenerRule, WaitConfigServiceToUp]


class StatisticsEcsService(BaseEcsService, ECSServiceResource):
    name = "statistics"
    task_definition = td.StatisticsEcsTaskDefinition.get_output_attr('arn')
    load_balancer_target_group_arn = tg.StatisticsALBTargetGroup.get_output_attr('arn')
    load_balancer_container_name = "statistics"
    DEPENDS_ON = [alr.StatisticsALBListenerRule, WaitConfigServiceToUp]


class AssetEcsService(BaseEcsService, ECSServiceResource):
    name = "asset"
    task_definition = td.AssetEcsTaskDefinition.get_output_attr('arn')
    load_balancer_target_group_arn = tg.AssetALBTargetGroup.get_output_attr('arn')
    load_balancer_container_name = "asset"
    DEPENDS_ON = [alr.AssetALBListenerRule, WaitConfigServiceToUp]


class AuthEcsService(BaseEcsService, ECSServiceResource):
    name = "auth"
    task_definition = td.AuthEcsTaskDefinition.get_output_attr('arn')
    load_balancer_target_group_arn = tg.AuthALBTargetGroup.get_output_attr('arn')
    load_balancer_container_name = "auth"
    DEPENDS_ON = [alr.AuthALBListenerRule, WaitConfigServiceToUp]


class VulnerabilityEcsService(BaseEcsService, ECSServiceResource):
    name = "vulnerability"
    task_definition = td.VulnerabilityEcsTaskDefinition.get_output_attr('arn', 0)
    load_balancer_target_group_arn = tg.VulnerabilityALBTargetGroup.get_output_attr('arn', 0)
    load_balancer_container_name = "vulnerability"
    DEPENDS_ON = [alr.VulnerabilityALBListenerRule, WaitConfigServiceToUp]
    PROCESS = need_to_deploy_vulnerability_service()
