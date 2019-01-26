from core.terraform.resources.aws import redshift
from resources.vpc.security_group import InfraSecurityGroupResource
from core.config import Settings
import base64


class RedshiftParameterGroup(redshift.RedshiftParameterGroupResource):
    name = ""
    family = "redshift-1.0"
    parameter = [
        {
            'name': "require_ssl",
            'value': "false"
        }
    ]


class RedshiftSubnetGroup(redshift.RedshiftSubnetGroupResource):
    name = ""
    subnet_ids = Settings.get('VPC')['SUBNETS']
    tags = [
        {'environment': Settings.RESOURCE_NAME_PREFIX + "redshift"},
        {'Name': Settings.RESOURCE_NAME_PREFIX}
    ]


class RedshiftCluster(redshift.RedshiftClusterResource):
    cluster_identifier = "data"
    database_name = "pacbot_data"
    master_username = "pacbot"
    master_password = "***REMOVED***"
    node_type = "dc2.large"
    cluster_type = "single-node"
    number_of_nodes = 1
    skip_final_snapshot = True
    publicly_accessible = False
    vpc_security_group_ids = [InfraSecurityGroupResource.get_output_attr('id')]
    cluster_parameter_group_name = RedshiftParameterGroup.get_output_attr('name')
    cluster_subnet_group_name = RedshiftSubnetGroup.get_output_attr('name')

    @classmethod
    def get_redshift_info(cls):
        info = "%s:%s" % (cls.get_input_attr('master_username'), cls.get_input_attr('master_password'))

        return base64.b64encode(info.encode()).decode()  # Since base64 takes up only bytes we need to encode then decode

    @classmethod
    def get_redshift_url(cls):
        endpoint = cls.get_output_attr('endpoint')
        dbname = cls.get_input_attr('database_name')

        return "jdbc:redshift://%s/%s" % (endpoint, dbname)

    def render_output(self, outputs):
        if self.resource_in_tf_output(outputs):
            return {
                'Redshift Host': outputs[self.get_resource_id()]['endpoint'],
                'Redshift DB': self.get_input_attr('database_name')
            }
