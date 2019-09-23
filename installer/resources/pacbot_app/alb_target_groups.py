from core.terraform.resources.aws.load_balancer import ALBTargetGroupResource
from resources.vpc.security_group import InfraSecurityGroupResource
from core.config import Settings
from resources.pacbot_app.utils import need_to_deploy_vulnerability_service


PATH_PREFIX = '/api/'
HEALTH_CHECK_MATCHING_LIST = "200,302,401"
HEALTH_CHECK_TIMEOUT = 60
HEALTH_CHECK_INTERVAL = 300


class BaseTG:
    # port = 80 if Settings.get('ALB_PROTOCOL', "HTTP") != "HTTPS" else 443
    # protocol = Settings.get('ALB_PROTOCOL', "HTTP")
    port = 80
    protocol = "HTTP"

    target_type = "ip"
    create_before_destroy = True
    vpc_id = Settings.get('VPC')['ID']
    interval = HEALTH_CHECK_INTERVAL
    timeout = HEALTH_CHECK_TIMEOUT
    matcher = HEALTH_CHECK_MATCHING_LIST


class ConfigALBTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "config"
    path = PATH_PREFIX + "config"


class AdminALBTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "admin"
    path = PATH_PREFIX + "admin/api.html"


class ComplianceALBTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "compliance"
    path = PATH_PREFIX + "compliance/api.html"


class NotificationsALBTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "notifications"
    path = PATH_PREFIX + "notifications/api.html"


class StatisticsALBTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "statistics"
    path = PATH_PREFIX + "statistics/api.html"


class AssetALBTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "asset"
    path = PATH_PREFIX + "asset/api.html"


class AuthALBTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "auth"
    path = PATH_PREFIX + "auth/api.html"


class VulnerabilityALBTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "vulnerability"
    path = PATH_PREFIX + "vulnerability/api.html"
    PROCESS = need_to_deploy_vulnerability_service()


class NginxALBTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "ngnix"
    path = "/nginx"
    interval = 120
    matcher = "200"
