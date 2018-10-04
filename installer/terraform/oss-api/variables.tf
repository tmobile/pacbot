variable "aws_access_key" {type = "string" default = ""}
variable "aws_secret_key" {type = "string" default = ""}

variable "api-ecs-cluster" {
  default = "pacman-oss-api"
}

variable "region" {
  default = ""
}

variable "apis_cloudwatch_group" {
  default = "pacman_oss_apis"
}

variable "vpc-id" {
  default = ""
}

variable "subnetid" {
  type = "list"
  default = []
}

variable "alb_sg" {
  default = ""
}

variable "alb_name" {
  default = ""
}

variable "api_image" {
  default = ""
}

variable "config_task_definition_name" {
  default = "config"
}

variable "admin_task_definition_name" {
  default = "admin"
}

variable "asset_task_definition_name" {
  default = "asset"
}

variable "compliance_task_definition_name" {
  default = "compliance"
}

variable "notifications_task_definition_name" {
  default = "notifications"
}

variable "statistics_task_definition_name" {
  default = "statistics"
}

variable "auth_task_definition_name" {
  default = "auth"
}

variable "config_jar_file_name" {
  default = "config.jar"
}

variable "admin_jar_file_name" {
  default = "pacman-api-admin.jar"
}

variable "asset_jar_file_name" {
  default = "pacman-api-asset.jar"
}

variable "compliance_jar_file_name" {
  default = "pacman-api-compliance.jar"
}

variable "notifications_jar_file_name" {
  default = "pacman-api-notification.jar"
}

variable "statistics_jar_file_name" {
  default = "pacman-api-statistics.jar"
}

variable "auth_jar_file_name" {
  default = "pacman-api-auth.jar"
}

variable "ecs_execution_role_arn" {
  default = ""
}
variable "ES_HEIMDALL_HOST_NAME" {
  default = ""
}

variable "ES_HEIMDALL_PORT" {
  default = ""
}

variable "ES_HOST_NAME" {
  default = ""
}

variable "ES_PORT" {
  default = ""
}

variable "LOGGING_ES_HOST_NAME" {
  default = ""
}

variable "LOGGING_ES_PORT" {
  default = ""
}

variable "ES_UPDATE_HOST" {
  default = ""
}

variable "ES_UPDATE_PORT" {
  default = ""
}

variable "RDS_PASSWORD" {
  default = ""
}

variable "RDS_URL" {
  default = ""
}

variable "RDS_HOST_ENDPOINT" {
  default = ""
}

variable "RDS_USERNAME" {
  default = ""
}

variable "REDSHIFT_URL" {
  default = ""
}

variable "REDSHIFT_USERNAME" {
  default = ""
}

variable "REDSHIFT_PASSWORD" {
  default = ""
}

variable "PACMAN_URL" {
  default = "http://localhost"
}

variable "PACMAN_SERVICE_USER" {
  default = "some_username"
}

variable "PACMAN_SERVICE_PASSWORD" {
  default = "some_password"
}

variable "CLOUD_INSIGHTS_TOKEN_URL" {
  default = "http://localhost"
}

variable "CLOUD_INSIGHTS_COST_URL" {
  default = "http://localhost"
}

variable "SVC_CORP_USER_ID" {
  default = "testid"
}

variable "SVC_CORP_PASSWORD" {
  default = "password"
}

variable "ES_CLUSTER_NAME" {
  default = ""
}

variable "RDS_HOSTNAME" {
}

variable "CONFIG_PASSWORD" {
  default = "pacman"
}

variable "LDAP_DOMAIN" {
  default = "http://localhost"
}

variable "LDAP_BASEDN" {
  default = "http://localhost"
}

variable "LDAP_PORT" {
  default = "389"
}

variable "LDAP_RESPONSETIMEOUT" {
  default = "60"
}

variable "LDAP_CONNECTIONTIMEOUT" {
  default = "60"
}

variable "LDAP_HOSTLIST" {
  default = "http://localhost"
}

variable "ES_UPDATE_CLUSTER_NAME" {
  default = ""
}

variable "CERTIFICATE_FEATURE_ENABLED" {
  default = "false"
}

variable "PATCHING_FEATURE_ENABLED" {
  default = "false"
}

variable "VULNERABILITY_FEATURE_ENABLED" {
  default = "false"
}

variable "ACCESS_KEY" {
  default = ""
}

variable "SECRET_KEY" {
  default = ""
}

variable "ENVIRONMENT" {
  default = ""
}

variable "SECURITY_USERNAME" {
  default = ""
}

variable "SECURITY_PASSWORD" {
  default = ""
}

variable "ROLE_ARN" {
  default = ""
}

variable "ADMIN_SERVER" {
  default = ""
}
