COMMAND_NOT_FOUND = "**** Command Not Found *****"
VALID_COMMANDS_ARE = "Valid Commands are"

CATEGORY_FIELD_NAME = "tags"

VALID = "Valid"
NOT_VALID = "Not Valid"
FOUND = "Found"
NOT_FOUND = "Not Found"
PRESENT = "Present"
NOT_PRESENT = "Not Present"
EXISTS = "Exists"
NOT_EXISTS = "Not Exists"

CORRECT_VPC_MSG = "Please update with correct VPC/CIDR/Subnet details in common.py OR create local.py and add VPC details"

PIP_CHECK_STARTED = "Checking if required python packages are available"
PIP_CHECK_COMPLETED = "Required python packages are available!!!"

pip_required = "Please install all the required python packages to start the execution\n"
PIP_INSTALL_MSG = pip_required + "Please use ``` sudo pip3 install -r requirements.txt ``` from the installer directory\n\n"

TOOLS_CHECK_STARTED = "Checking if required tools are availables"
TOOLS_CHECK_COMPLETED = "Required tools are available!!!"

INPUT_READING_STARTED = "Reading required inputs from user"
INPUT_READING_COMPLETED = "Required inputs are available!!!"

AWS_AUTH_MECHANISM = "Select AWS authentication mechanism: "
AWS_WITH_KEYS = "1. Using access key and secret key "
AWS_WITH_ASSUME_ROLE = "2. Assuming an IAM role"
AWS_WITH_EC2_ROLE = "3. Using IAM role attached to the this instance"
AWS_CHOOSE_AUTH_OPTION = "Type 1 or 2 or 3 to continue to create services in AWS: "
AWS_INCORRECT_MECHANISM = "Entered an incorrect value!!!"
AWS_AUTH_MECHANISM_NOT_SUPPLIED = "Please add value 1 or 2 or 3 for AWS_AUTH_MECHANISM in settings/local.py"
AWS_ACCESS_KEY_NOT_SUPPLIED = "Please enter value for AWS_ACCESS_KEY in settings/local.py file"
AWS_SECRET_KEY_NOT_SUPPLIED = "Please enter value for AWS_SECRET_KEY in settings/local.py file"
AWS_REGION_NOT_SUPPLIED = "Please enter value for AWS_REGION in settings/local.py file"
AWS_ASSUME_ROLE_NOT_SUPPLIED = "Please enter value for AWS_ASSUME_ROLE_ARN in settings/local.py file"

AWS_ACCESS_KEY_INPUT = "Please enter AWS access key: "
AWS_SECRET_KEY_INPUT = "Please enter AWS secret key: "
AWS_REGION_INPUT = "Please enter region: "
AWS_ASSUME_ROLE_INPUT = "Enter IAM role to be assumed: "
INVALID_KEY = "Entered invalid Key!!!"
SETTINGS_CHECK_STARTED = "Checking settings and inputs"
VPC_CHECK_STARTED = "Checking VPC and CIDR Blocks"
SUBNETS_CHECK_STARTED = "Checking VPC subnets"

INVALID_VPC = "VPC provided is not valid"
INVALID_CIDR = "Provided invalid CIDR blocks lists"
INVALID_SUBNETS = "Invalid subnets provided, please check"
INVALID_SUBNET_ZONES = "Subnets provided should be on different availability zones"

CHECKING_GROUP_POLICY = "Checking group-attached policies"
CHECKING_USER_POLICY = "Checking user-attached policies"
CHECKING_ROLE_POLICY = "Checkcing role policies"
FULL_ACCESS_POLICY = "Administrator access policy"
POLICY_YES_NO = "If you have added custom policies with all permissions, please type Yes or No"

LOG_DIR_NOT_FOUND = "Log directory is not found"
LOG_DIR_SETTINGS_NOT_FOUND = "LOG_DIR path settings is required for the installation"

DATA_DIR_NOT_FOUND = "Data directory is not found"
DATA_DIR_SETTINGS_NOT_FOUND = "DATA_DIR path settings is required for the installation"

RESOURCE_EXISTS_CHECK_STARTED = "Checking resource existence"
RESOURCE_EXISTS_CHECK_FAILED = "Resource existence check failed \nPlease delete all the existing resources or change their names\n"
RESOURCE_EXISTS_CHECK_COMPLETED = "Resource existence check completed!!!"

TERRAFORM_GEN_STARTED = "Terraform file generation started"
TERRAFORM_GEN_COMPLETED = "Terraform file generation completed!!!"

PROVIDER_NOT_FOUND = "Please add correct provider in configuration"
PROVISIONER_FILES_DIR_NOT_FOUND = "Provisioners files directory is not found"
PROVISIONER_FILES_DIR_SETTINGS_NOT_FOUND = "Provisioners files directory setting is not found"

TERRAFORM_INIT_STARTED = "Terraform init started"
TERRAFORM_INIT_RUNNING = "Running terraform init"
TERRAFORM_INIT_ERROR = "Terraform init encountered an error. Please check error log for more details"
TERRAFORM_INIT_COMPLETED = "Terraform init executed successfully!!!"

TERRAFORM_PLAN_STARTED = "Terraform plan started"
TERRAFORM_PLAN_RUNNING = "Running terraform plan"
TERRAFORM_PLAN_ERROR = "Terraform plan encountered an error. Please check error log for more details"
TERRAFORM_PLAN_COMPLETED = "Terraform plan executed successfully!!!"

TERRAFORM_APPLY_STARTED = "Terraform apply started"
TERRAFORM_APPLY_RUNNING = "Creating / Updating resources"
TERRAFORM_APPLY_ERROR = "Terraform apply encountered an error. Please check error log for more details"
TERRAFORM_APPLY_COMPLETED = "Terraform apply executed successfully!!! Please check installation log for more details"
TERRAFORM_APPLY_DRY_RUN = "Terraform apply is not executed as dry-run is enabled"
TERRAFORM_OUTPUT_STORED = "Terraform output is stored"

TERRAFORM_DESTROY_STARTED = "Terraform destroy started"
TERRAFORM_REDEPLOY_DESTROY_STARTED = "Deleting resources for redeployment"
TERRAFORM_DESTROY_RUNNING = "Destroying resources"
TERRAFORM_DESTROY_ERROR = "Terraform destroy encountered an error"
TERRAFORM_DESTROY_COMPLETED = "Terraform destroy executed successfully!!! Please check destroy log for more details"
TERRAFORM_REDEP_DESTROY_COMPLETED = "Successful!!! Resources will be recreated in next steps."
TERRAFORM_DESTROY_DRY_RUN = "Terraform destroy is not executed as dry-run is enabled"

TERRAFORM_TAINT_STARTED = "Terraform taint(destroy and re-install) started"
TERRAFORM_TAINT_ERROR = "Terraform taint(destroy and re-install) encountered an error. Please check error log for more details"
TERRAFORM_TAINT_COMPLETED = "Terraform taint(destroy and re-install) executed successfully!!! Please check installation log for more details"

EXECUTED_WITH_ERROR = "Execution encountered error"

RESOURCES_EMPTY = "There is nothing to process!"

APPLY_STATUS_COMPLETED = 'APPLY_STATUS_COMPLETED'
APPLY_STATUS_ERROR = 'APPLY_STATUS_ERROR'
DESTROY_STATUS_COMPLETED = 'DESTROY_STATUS_COMPLETED'
DESTROY_STATUS_ERROR = 'DESTROY_STATUS_ERROR'

STATUS_CODE_MSGS = {
    'APPLY_STATUS_COMPLETED': "All resources are created/uodated successfully!",
    'APPLY_STATUS_ERROR': "All resources are not created/uodated successfully. Apply encountered error(s)",
    'DESTROY_STATUS_COMPLETED': "All resources are destroyed successfully!",
    'DESTROY_STATUS_ERROR': "All resources are not destroyed successfully. Destroy encountered error(s)"
}

CURRENT_STATUS_MSG = "Current Status:"
NO_STATUS_OUTPUT = "Nothing to show as you have not installed/destroyed anything.\n"
CURRENTLY_INSTALLED_RESOURCES = "Currently installed resources are:"

ALL_TOOLS_NOT_AVAIALABLE = "Please install all the required tools to start the execution\n"

ANOTHER_PROCESS_RUNNING = "Another process is running(terraform lock is found). Please wait till it completes"
