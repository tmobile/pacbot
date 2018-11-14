variable "aws_access_key" {}
variable "aws_secret_key" {}
variable "region" {}
variable "subnetid" {type="list" default =[]}
variable "cidr_block-ip" {type="list" default =[]}
variable "vpc-id" {}
variable "compute_environment" {}
variable "computeinstance_type" {type="list"}
variable "maxvcpu" {}
variable "minvcpu" {}
variable "desiredvcpu" {}
variable "job_queue_name" {type = "list" default =[]}
variable "priority_value" {type = "list" default =[]}
variable "batch_job_definition_name" {}
variable "docker_parameters" {type = "list" default =[]}
variable "image_name" {}
variable "memory_size" {}
variable "job_definition_vcpu" {}
variable "attempts_number" {}
variable "resourcetype" {}
variable "keyname" {}
variable "accountid" {}
variable "client_accountid" {}
variable "pacbot_sgid" {}
variable "instance_profile" {}
variable "batchrole" {}
variable "ES_HOST" {}
variable "BASE_AWS_ACCOUNT" {}
variable "ES_URI" {}
variable "HEIMDALL_URI" {}
variable "PACMAN_API_URI" {}
