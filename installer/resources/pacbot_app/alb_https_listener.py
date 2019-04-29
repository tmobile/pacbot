from core.terraform.resources.aws.load_balancer import ALBListenerResource, ALBListenerRuleResource
from core.config import Settings
from resources.pacbot_app.alb import ApplicationLoadBalancer
from resources.pacbot_app import alb_https_target_groups as tg


PATH_PREFIX = '/api/'


class PacBotHttpsListener(ALBListenerResource):
    load_balancer_arn = ApplicationLoadBalancer.get_output_attr('arn')
    port = 443
    protocol = "HTTPS"
    ssl_policy = "ELBSecurityPolicy-2016-08"
    certificate_arn = Settings.get('SSL_CERTIFICATE_ARN')
    default_action_target_group_arn = tg.NginxALBHttpsTargetGroup.get_output_attr('arn')
    default_action_type = "forward"


class BaseLR:
    listener_arn = PacBotHttpsListener.get_output_attr('arn')
    action_type = "forward"
    condition_field = "path-pattern"


class ConfigALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.ConfigALBHttpsTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "config*"]


class AdminALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.AdminALBHttpsTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "admin*"]


class ComplianceALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.ComplianceALBHttpsTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "compliance*"]


class NotificationsALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.NotificationsALBHttpsTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "notifications*"]


class StatisticsALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.StatisticsALBHttpsTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "statistics*"]


class AssetALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.AssetALBHttpsTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "asset*"]


class AuthALBHttpsListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.AuthALBHttpsTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "auth*"]
