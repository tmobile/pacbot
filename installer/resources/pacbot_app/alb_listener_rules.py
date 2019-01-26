from core.terraform.resources.aws.load_balancer import ALBListenerResource, ALBListenerRuleResource
from resources.pacbot_app.alb import ApplicationLoadBalancer
from resources.pacbot_app import alb_target_groups as tg


PATH_PREFIX = '/api/'


class ApplicationLoadBalancerListener(ALBListenerResource):
    load_balancer_arn = ApplicationLoadBalancer.get_output_attr('arn')
    port = 80
    protocol = "HTTP"
    default_action_target_group_arn = tg.NginxALBTargetGroup.get_output_attr('arn')
    default_action_type = "forward"


class BaseLR:
    listener_arn = ApplicationLoadBalancerListener.get_output_attr('arn')
    action_type = "forward"
    condition_field = "path-pattern"


class ConfigALBListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.ConfigALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "config*"]


class AdminALBListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.AdminALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "admin*"]


class ComplianceALBListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.ComplianceALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "compliance*"]


# TODO: Commenting this out to use it in future
# class NotificationsALBListenerRule(ALBListenerRuleResource, BaseLR):
#     action_target_group_arn = tg.NotificationsALBTargetGroup.get_output_attr('arn')
#     condition_values = [PATH_PREFIX + "notifications*"]


class StatisticsALBListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.StatisticsALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "statistics*"]


class AssetALBListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.AssetALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "asset*"]


class AuthALBListenerRule(ALBListenerRuleResource, BaseLR):
    action_target_group_arn = tg.AuthALBTargetGroup.get_output_attr('arn')
    condition_values = [PATH_PREFIX + "auth*"]
