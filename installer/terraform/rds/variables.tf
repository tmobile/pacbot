variable "aws_access_key" {}
variable "aws_secret_key" {}
variable "region" {}
variable "user_name" { type = "string" default = "" }
variable "user_pwd" { type = "string" default = "" }
variable "snapshotname" { type = "string" default = "pacmansnapshot" }
variable "rdsdatabase" {}
variable "rdsdbversion" {}
variable "rdsinstance" {}
variable "rdsname" {}
variable "cidr_block-ip" {type= "list" default = []}
variable "vpc-id" {}
variable "subnet_list" {type= "list" default = []}
variable "option_group_name" {}
variable "parameter_group_name" {}
variable "subnet_group_name" {}
variable "engine_name" {}
variable "engine_version" {}
variable "pacman_sgid" {}
variable "family_name" {}
variable "identifier" {}
