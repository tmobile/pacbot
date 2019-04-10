from core.terraform.resources.aws.load_balancer import LoadBalancerResource
from resources.vpc.security_group import InfraSecurityGroupResource
from core.config import Settings


class ApplicationLoadBalancer(LoadBalancerResource):
    name = ""
    internal = Settings.get('MAKE_ALB_INTERNAL', True)
    load_balancer_type = "application"
    security_groups = [InfraSecurityGroupResource.get_output_attr('id')]
    subnets = Settings.get('VPC')['SUBNETS']

    OUTPUT_LIST = ['dns_name']

    @classmethod
    def get_http_url(cls):
        return "%s://%s" % (Settings.get('ALB_PROTOCOL', "HTTP").lower(), cls.get_output_attr('dns_name'))

    @classmethod
    def get_api_base_url(cls):
        return "%s://%s/api" % (Settings.get('ALB_PROTOCOL', "HTTP").lower(), cls.get_output_attr('dns_name'))

    @classmethod
    def get_api_version_url(cls, service):
        version_url = cls.get_api_server_url(service)
        return version_url if service == "auth" else version_url + "/v1"

    @classmethod
    def get_api_server_url(cls, service):
        return "%s/%s" % (cls.get_api_base_url(), service)

    def render_output(self, outputs):
        if self.resource_in_tf_output(outputs):
            return {
                'Pacbot Domain': outputs[self.get_resource_id()]['dns_name'],
                'Admin': Settings.PACBOT_LOGIN_CREDENTIALS['Admin'],
                'User': Settings.PACBOT_LOGIN_CREDENTIALS['User']
            }
