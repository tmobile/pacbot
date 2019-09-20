from core.terraform.resources.aws.ecs import ECSTaskDefinitionResource
from resources.iam.ecs_role import ECSRole
from resources.pacbot_app.task_def_variables import ContainerDefinitions
from resources.pacbot_app.ecr import APIDockerImageBuild, UIDockerImageBuild
from resources.pacbot_app.utils import need_to_deploy_vulnerability_service


container_def = ContainerDefinitions()


class BaseTaskDefinition:
    requires_compatibilities = ["FARGATE"]
    network_mode = "awsvpc"
    cpu = 2048
    memory = 4096
    execution_role_arn = ECSRole.get_output_attr('arn')
    task_role_arn = ECSRole.get_output_attr('arn')


class NginxEcsTaskDefinition(ECSTaskDefinitionResource, BaseTaskDefinition):
    family = "webapp"
    container_name = "nginx"
    container_definitions = container_def.get_container_definitions('nginx')
    cpu = 512
    memory = 1024
    DEPENDS_ON = [UIDockerImageBuild]


class ConfigEcsTaskDefinition(ECSTaskDefinitionResource, BaseTaskDefinition):
    family = "config"
    container_definitions = container_def.get_container_definitions('config')
    DEPENDS_ON = [APIDockerImageBuild]


class AdminEcsTaskDefinition(ECSTaskDefinitionResource, BaseTaskDefinition):
    family = "admin"
    container_definitions = container_def.get_container_definitions('admin')
    DEPENDS_ON = [APIDockerImageBuild]


class ComplianceEcsTaskDefinition(ECSTaskDefinitionResource, BaseTaskDefinition):
    family = "compliance"
    container_definitions = container_def.get_container_definitions('compliance')
    DEPENDS_ON = [APIDockerImageBuild]


class NotificationsEcsTaskDefinition(ECSTaskDefinitionResource, BaseTaskDefinition):
    family = "notifications"
    container_definitions = container_def.get_container_definitions('notifications')
    DEPENDS_ON = [APIDockerImageBuild]


class StatisticsEcsTaskDefinition(ECSTaskDefinitionResource, BaseTaskDefinition):
    family = "statistics"
    container_definitions = container_def.get_container_definitions('statistics')
    DEPENDS_ON = [APIDockerImageBuild]


class AssetEcsTaskDefinition(ECSTaskDefinitionResource, BaseTaskDefinition):
    family = "asset"
    container_definitions = container_def.get_container_definitions('asset')
    DEPENDS_ON = [APIDockerImageBuild]


class AuthEcsTaskDefinition(ECSTaskDefinitionResource, BaseTaskDefinition):
    family = "auth"
    container_definitions = container_def.get_container_definitions('auth')
    DEPENDS_ON = [APIDockerImageBuild]


class VulnerabilityEcsTaskDefinition(ECSTaskDefinitionResource, BaseTaskDefinition):
    family = "vulnerability"
    container_definitions = container_def.get_container_definitions('vulnerability')
    DEPENDS_ON = [APIDockerImageBuild]
    PROCESS = need_to_deploy_vulnerability_service()
