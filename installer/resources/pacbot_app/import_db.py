from core.terraform.resources.misc import NullResource
from core.terraform.utils import get_terraform_scripts_and_files_dir, get_terraform_scripts_dir, get_terraform_provider_file
from core.config import Settings
from resources.datastore.db import MySQLDatabase
from resources.datastore.es import ESDomain
from resources.data.aws_info import AwsAccount, AwsRegion
from shutil import copy2
import os


class ReplaceSQLPlaceHolder(NullResource):
    dest_file = os.path.join(get_terraform_scripts_and_files_dir(), 'DB_With_Values.sql')
    triggers = {'version': "1.1"}

    DEPENDS_ON = [MySQLDatabase, ESDomain]

    def get_provisioners(self):
        script = os.path.join(get_terraform_scripts_dir(), 'sql_replace_placeholder.py')
        db_user_name = MySQLDatabase.get_input_attr('username')
        db_password = MySQLDatabase.get_input_attr('password')
        db_host = MySQLDatabase.get_output_attr('endpoint')
        local_execs = [
            {
                'local-exec': {
                    'command': script,
                    'environment': {
                        'ENV_region': AwsRegion.get_output_attr('name'),
                        'ENV_account': AwsAccount.get_output_attr('account_id'),
                        'ENV_eshost': ESDomain.get_http_url(),
                        'ENV_esport': ESDomain.get_es_port(),
                        'SQL_FILE_PATH': self.dest_file
                    },
                    'interpreter': [Settings.PYTHON_INTERPRETER]
                }
            }
        ]

        return local_execs

    def pre_generate_terraform(self):
        src_file = os.path.join(Settings.BASE_APP_DIR, 'resources', 'pacbot_app', 'files', 'DB.sql')
        copy2(src_file, self.dest_file)


class ImportDbSql(NullResource):
    triggers = {'version': "1.1"}

    DEPENDS_ON = [MySQLDatabase, ReplaceSQLPlaceHolder]

    def get_provisioners(self):
        db_user_name = MySQLDatabase.get_input_attr('username')
        db_password = MySQLDatabase.get_input_attr('password')
        db_host = MySQLDatabase.get_output_attr('address')
        local_execs = [
            {
                'local-exec': {
                    'command': "mysql -u %s -p%s -h %s < %s" % (db_user_name, db_password, db_host, ReplaceSQLPlaceHolder.dest_file)
                }
            }

        ]

        return local_execs
