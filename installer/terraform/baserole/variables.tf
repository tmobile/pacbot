variable "aws_access_key" {type = "string" default = ""}
variable "aws_secret_key" {type = "string" default = ""}
variable "region" {}
variable "check" {}
variable "baseaccountrole" {}
variable "pacecsrole" {}
variable "accountid" {}
variable "lambda_role" {}
variable "ecstaskexecution_role" {}
variable "pacrunbatch" {}
variable "pacecsrolepolicy" {
  description = "IAM Policy to be attached to role"
  type = "list"
  default = []
}
variable "cidr_block-ip" { type = "list" default =[]}
variable "vpc-id" {}

