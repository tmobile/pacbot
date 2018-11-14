variable "aws_access_key" {}
variable "aws_secret_key" {}
variable "region" {}
variable "cluster_identifier" {}
variable "cluster_database_name" {}
variable "cluster_master_username" {}
variable "cluster_master_password" {}
variable "cluster_node_type" {}
variable "cluster_type" {}
variable "cluster_number_of_nodes" {}
variable "subnet_list" { type = "list" default =[]}
variable "subnet_group_name" {}
variable "parameter_group_name" {}
variable "pacbot_sgid" {}

