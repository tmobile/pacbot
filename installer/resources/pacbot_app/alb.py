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
        pacbot_domain = cls.get_output_attr('dns_name')
        return "%s://%s" % ("http", pacbot_domain)

        # TODO: Replace with this once dev team fix https issue
        # pacbot_domain = Settings.get('PACBOT_DOMAIN', None)
        # pacbot_domain = pacbot_domain if pacbot_domain else cls.get_output_attr('dns_name')
        # return "%s://%s" % (Settings.get('ALB_PROTOCOL', "HTTP").lower(), pacbot_domain)

    @classmethod
    def get_pacbot_domain_url(cls):
        pacbot_domain = Settings.get('PACBOT_DOMAIN', None)
        pacbot_domain = pacbot_domain if pacbot_domain else cls.get_output_attr('dns_name')

        return "%s://%s" % (Settings.get('ALB_PROTOCOL', "HTTP").lower(), pacbot_domain)

    @classmethod
    def get_api_base_url(cls):
        pacbot_domain = cls.get_output_attr('dns_name')
        return "%s://%s/api" % ("http", pacbot_domain)

        # TODO: Replace with this once dev team fix https issue
        # pacbot_domain = Settings.get('PACBOT_DOMAIN', None)
        # pacbot_domain = pacbot_domain if pacbot_domain else cls.get_output_attr('dns_name')
        # return "%s://%s/api" % (Settings.get('ALB_PROTOCOL', "HTTP").lower(), pacbot_domain)

    @classmethod
    def get_api_version_url(cls, service):
        version_url = cls.get_api_server_url(service)
        return version_url if service == "auth" else version_url + "/v1"

    @classmethod
    def get_api_server_url(cls, service):
        return "%s/%s" % (cls.get_api_base_url(), service)

    def _get_printable_abs_url(self, dns_name):
        """
        This function returns the absolute URL of the domain ie. with http/https

        Args:
            dns_name (str): Loadbalancer dns name

        Returns:
            url (str): abs url of pacbot
        """
        pacbot_domain = Settings.get('PACBOT_DOMAIN', None)
        pacbot_domain = pacbot_domain if pacbot_domain else dns_name

        return "%s://%s" % (Settings.get('ALB_PROTOCOL', "HTTP").lower(), pacbot_domain)

    def render_output(self, outputs):
        if self.resource_in_tf_output(outputs):
            abs_url = self._get_printable_abs_url(outputs[self.get_resource_id()]['dns_name'])
            return {
                'Pacbot URL': abs_url,
                'Admin': Settings.PACBOT_LOGIN_CREDENTIALS['Admin'],
                'User': Settings.PACBOT_LOGIN_CREDENTIALS['User']
            }
