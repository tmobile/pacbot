from core.terraform.resources.aws.rds import RDSResource, RDSOptionGroupResource, RDSParameterGroupResource, RDSSubnetGroupResource
from resources.vpc.security_group import InfraSecurityGroupResource
from core.providers.aws.boto3.iam import create_iam_service_linked_role
from core.config import Settings
from core.log import SysLog
import base64


class DBOptionGroup(RDSOptionGroupResource):
    name = "mysql"
    engine_name = "mysql"
    major_engine_version = "5.6"


class DBParameterGroup(RDSParameterGroupResource):
    name = "mysql"
    family = "mysql5.6"


class DBSubnetGroup(RDSSubnetGroupResource):
    name = "mysql"
    subnet_ids = Settings.get('VPC')['SUBNETS']


class MySQLDatabase(RDSResource):
    name = "pacmandata"
    instance_class = Settings.get('RDS_INSTANCE_TYPE', "db.t2.medium")
    identifier = "data"
    storage_type = "gp2"
    engine = "mysql"
    engine_version = "5.6.40"
    allocated_storage = 10
    username = "pacbot"
    password = "***REMOVED***"
    parameter_group_name = DBParameterGroup.get_input_attr('name')
    option_group_name = DBOptionGroup.get_input_attr('name')
    db_subnet_group_name = DBSubnetGroup.get_input_attr('name')
    vpc_security_group_ids = [InfraSecurityGroupResource.get_output_attr('id')]
    skip_final_snapshot = True
    apply_immediately = True

    DEPENDS_ON = [DBOptionGroup, DBParameterGroup, DBSubnetGroup]

    @classmethod
    def get_rds_info(cls):
        info = "%s:%s" % (cls.get_input_attr('username'), cls.get_input_attr('password'))

        return base64.b64encode(info.encode()).decode()

    @classmethod
    def get_rds_db_url(cls):
        rds_endpoint = cls.get_output_attr('endpoint')
        db_name = cls.get_input_attr('name')

        return "jdbc:mysql://%s/%s" % (rds_endpoint, db_name)

    def render_output(self, outputs):
        if self.resource_in_tf_output(outputs):
            return {
                'MySQL Host': outputs[self.get_resource_id()]['endpoint'],
                'MySQL DB': self.get_input_attr('name')
            }

    def pre_terraform_apply(self):
        status, msg = create_iam_service_linked_role(
            "rds.amazonaws.com",
            Settings.RESOURCE_DESCRIPTION,
            Settings.AWS_AUTH_CRED)

        SysLog().write_debug_log("RDS IAM Service Linked role creation: Status:%s, Message: %s" % (str(status), msg))
