from core.terraform.resources.aws.load_balancer import ALBListenerResource, ALBListenerRuleResource
from core.config import Settings
from resources.pacbot_app.alb import ApplicationLoadBalancer
from resources.pacbot_app import alb_target_groups as tg


PATH_PREFIX = '/api/'


class PacBotHttpsListener(ALBListenerResource):
    load_balancer_arn = ApplicationLoadBalancer.get_output_attr('arn')
    port = 443
    protocol = "HTTPS"
    ssl_policy = "ELBSecurityPolicy-2016-08"
    certificate_arn = Settings.get('SSL_CERTIFICATE_ARN')
    default_action_target_group_arn = tg.NginxALBTargetGroup.get_output_attr('arn')
    default_action_type = "forward"


class BaseLR:
    listener_arn = PacBotHttpsListener.get_output_attr('arn')
    action_type = "forward"
    condition_field = "path-pattern"


class ConfigALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.ConfigALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "config*"]


class AdminALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.AdminALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "admin*"]


class ComplianceALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.ComplianceALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "compliance*"]


class NotificationsALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.NotificationsALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "notifications*"]


class StatisticsALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.StatisticsALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "statistics*"]


class AssetALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.AssetALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "asset*"]


class AuthALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.AuthALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "auth*"]


class VulnerabilityALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.VulnerabilityALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "vulnerability*"]
