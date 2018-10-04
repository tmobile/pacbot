variable "aws_access_key" {}
variable "aws_secret_key" {}
variable "region" {}
variable "functionname" {}
variable "handler_name" {}
variable "runtime_name" {}
variable "s3_bucket_name" {}
variable "s3_file_name" {}
variable "accountid" {}
variable "lambda_role" {}
variable "job_queue_name" {}
variable "jobdef_name" {}
variable "event_name" { default="event"}
variable "redshift_url" {}
variable "client_accountid" {}
variable "rdsurl" {}
variable "rdsinfo" {}
variable "redshiftinfo" {}
variable "targetid" {}
variable "rules" {
type = "list"
default = [] }
