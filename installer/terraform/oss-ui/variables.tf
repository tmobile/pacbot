variable "aws_access_key" {type = "string" default = ""}
variable "aws_secret_key" {type = "string" default = ""}

variable "ui_cloudwatch_group" {
  default = "pacman_oss_ui"
}

variable "ui-ecs-cluster" {
  default = ""
}

variable "region" {
  default = ""
}

variable "task_definition_name" {
  default = "nginx"
}

variable "ecs_execution_role_arn" {
  default = ""
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
  default = "sukesh-alb-ui"
}

variable "ui_image" {
  default = ""
}

variable "ui_container_name" {
  default = "nginx"
}

