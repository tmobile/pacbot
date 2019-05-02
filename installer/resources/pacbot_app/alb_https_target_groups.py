from core.terraform.resources.aws.load_balancer import ALBTargetGroupResource
from resources.vpc.security_group import InfraSecurityGroupResource
from core.config import Settings


PATH_PREFIX = '/api/'
HEALTH_CHECK_MATCHING_LIST = "200,302,401"
HEALTH_CHECK_TIMEOUT = 60
HEALTH_CHECK_INTERVAL = 300


class BaseTG:
    # port = 80 if Settings.get('ALB_PROTOCOL', "HTTP") != "HTTPS" else 443
    # protocol = Settings.get('ALB_PROTOCOL', "HTTP")
    port = 443
    protocol = "HTTPS"

    target_type = "ip"
    create_before_destroy = True
    vpc_id = Settings.get('VPC')['ID']
    interval = HEALTH_CHECK_INTERVAL
    timeout = HEALTH_CHECK_TIMEOUT
    matcher = HEALTH_CHECK_MATCHING_LIST


class ConfigALBHttpsTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "config-https"
    path = PATH_PREFIX + "config"


class AdminALBHttpsTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "admin-https"
    path = PATH_PREFIX + "admin/api.html"


class ComplianceALBHttpsTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "compliance-https"
    path = PATH_PREFIX + "compliance/api.html"


class NotificationsALBHttpsTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "notifications-https"
    path = PATH_PREFIX + "notifications/api.html"


class StatisticsALBHttpsTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "statistics-https"
    path = PATH_PREFIX + "statistics/api.html"


class AssetALBHttpsTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "asset-https"
    path = PATH_PREFIX + "asset/api.html"


class AuthALBHttpsTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "auth-https"
    path = PATH_PREFIX + "auth/api.html"


class NginxALBHttpsTargetGroup(ALBTargetGroupResource, BaseTG):
    name = "ngnix-https"
    path = "/nginx"
    interval = 120
    matcher = "200"
