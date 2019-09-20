from pathlib import Path
import os
import sys


PROVIDER = 'AWS'
CURRENT_FILE_PATH = Path(os.path.join(os.path.abspath(os.path.dirname(__file__))))
BASE_APP_DIR = str(CURRENT_FILE_PATH.parent)
RESOURCES_FOLDER = 'resources'  # Provide only relative path

# This is commonn configuration should be used in all setup
SETUP_TITLE = "PACBOT"
SETUP_DESCRIPTION = "INFRA SETUP AND DEPLOYMENT"
LOADER_FILE_PATH = os.path.join(str(CURRENT_FILE_PATH), "loader")

# INSTALL_INPUTS_REQUIRED = [
#     {
#         'input_key': "USER_EMAIL_ID", 'input_msg': "Your email id to send emails: ", 'required': True
#     }
# ]

TOOLS_REQUIRED = {
    'Maven': "mvn --version",
    'Git': "git --version",
    'MySQL client': "mysql --version",
    'Terraform': "terraform --version",
    'Nodejs': "node --version",
    'npm': "npm --version",
    'Angular': "ng --version",
    'Yarn': 'yarn --version',
    'Docker': "docker --version"
}

PYTHON_PACKAGES_REQUIRED = [
    ("docker", "Client"),
    "boto3"
]

PROCESS_RESOURCES = {
    'data.aws_info': {'tags': ["roles"]},  # This should not be removed
    'iam.base_role': {'tags': ["roles"]},
    'iam.batch_role': {'tags': ["roles"]},
    'iam.ecs_role': {'tags': ["roles", "ecs_role"]},
    'iam.lambda_role': {'tags': ["roles"]},
    'iam.base_role_policy': {'tags': ["roles"]},
    'iam.all_read_role': {'tags': ["roles", "all_read_role"]},
    'vpc.security_group': {'tags': ["security"]},
    'datastore.db': {'tags': ["rds", "datastore"]},
    'datastore.es': {'tags': ["es", "datastore"]},
    'pacbot_app.alb': {'tags': ["infra"]},
    'pacbot_app.alb_target_groups': {'tags': ["infra", "deploy"]},
    'pacbot_app.alb_listener_rules': {'tags': ["infra", "deploy"]},
    'pacbot_app.ecr': {'tags': ["infra"]},
    'pacbot_app.cloudwatch_log_groups': {'tags': ["infra"]},
    'pacbot_app.build_ui_and_api': {'tags': ["deploy", "infra"]},
    'pacbot_app.import_db': {'tags': ["deploy", "app-import-db", "infra"]},
    'pacbot_app.ecs_task_defintions': {'tags': ["deploy", "task-definitions", "infra"]},
    'pacbot_app.ecs_services': {'tags': ["deploy", "ecs-services", "infra"]},
    's3.bucket': {'tags': ["s3"]},
    'batch.env': {'tags': ["batch"]},
    'batch.ecr': {'tags': ["batch", "batch-ecr"]},
    'batch.job': {'tags': ["batch", "infra", "batch-job"]},
    'lambda_submit.s3_upload': {'tags': ["submit-job", "batch", "infra"]},
    'lambda_submit.function': {'tags': ["submit-job", "batch", "infra"]},
    'lambda_rule_engine.s3_upload': {'tags': ["rule-engine-job", "batch"]},
    'lambda_rule_engine.function': {'tags': ["rule-engine-job", "batch", "infra"]},
    'pacbot_app.upload_terraform': {'tags': ["upload_tf"]},
}

DATA_DIR = os.path.join(BASE_APP_DIR, 'data')
LOG_DIR = os.path.join(BASE_APP_DIR, 'log')
PROVISIONER_FILES_DIR_TO_COPY = os.path.join(BASE_APP_DIR, 'files')
ALB_PROTOCOL = "HTTP"

DESTROY_NUM_ATTEMPTS = 3
SKIP_RESOURCE_EXISTENCE_CHECK = False
RESOURCE_NAME_PREFIX = "pacbot"
DEFAULT_RESOURCE_TAG = {"Application": "PacBot"}
CUSTOM_RESOURCE_TAGS = []

RESOURCE_DESCRIPTION = "DO-NOT-DELETE-IT - This has been created as part of pacbot installation"
AWS_POLICIES_REQUIRED = [
    "AmazonS3FullAccess",
    "AmazonRDSFullAccess",
    "AWSLambdaFullAccess",
    "AmazonEC2FullAccess",
    "IAMFullAccess",
    "AmazonESFullAccess"
]

AWS_ACCESS_KEY = ""
AWS_SECRET_KEY = ""
AWS_REGION = ""

VPC = {
    "ID": "vpc-1",
    "CIDR_BLOCKS": ["10.0.0.0/16"],
    "SUBNETS": ["subnet-1", "subnet-2"]
}
REQUIRE_SUBNETS_ON_DIFFERENT_ZONE = True

PACBOT_CODE_DIR = str(CURRENT_FILE_PATH.parent.parent)
PACBOT_LOGIN_CREDENTIALS = {
    'Admin': "admin@pacbot.org / pacman",
    'User': "user@pacbot.org / user"
}

MAKE_ALB_INTERNAL = True

MAIL_SERVER = "localhost"
MAIL_SERVER_PORT = 25
MAIL_PROTOCOL = "smtp"
MAIL_SERVER_USER = ""
MAIL_SERVER_PWD = ""
MAIL_SMTP_AUTH = ""
MAIL_SMTP_SSL_ENABLE = "true"
MAIL_SMTP_SSL_TEST_CONNECTION = "false"

ENABLE_VULNERABILITY_FEATURE = False

try:
    from settings.local import *
except:
    pass

if ALB_PROTOCOL == "HTTPS":
    PROCESS_RESOURCES['pacbot_app.alb_https_listener'] = {'tags': ["deploy"]}  # This should not be removed
