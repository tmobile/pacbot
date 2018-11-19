//Create Cloudwatch Log Group
resource "aws_cloudwatch_log_group" "apijobs" {
  name = "${var.apis_cloudwatch_group}"
  retention_in_days = 7
}

//Create Cloudwatch Log Group
resource "aws_cloudwatch_log_group" "uijobs" {
  name = "${var.ui_cloudwatch_group}"
  retention_in_days = 7
}

//Create ECS Cluster for Pacman-OSS-APIs
resource "aws_ecs_cluster" "cluster" {
  name = "${var.api-ecs-cluster}"
}

//Create Application Loadbalancer
resource "aws_lb" "alb_apijobs" {
  name            = "${var.alb_name}"
  internal           = true
  load_balancer_type = "application"
  security_groups    = ["${var.alb_sg}"]
  subnets            = ["${var.subnetid}"]

  tags {
    Name        = "${var.alb_name}"
  }
}

//Create config target group
resource "aws_alb_target_group" "config_alb_target_group" {
  name     = "${var.config_task_definition_name}-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = "${var.vpc-id}"
  target_type = "ip"

  lifecycle {
    create_before_destroy = true
  }

  health_check {
    path             = "/api/${var.config_task_definition_name}",
    interval         = "300",
    timeout          = "60",
    matcher          = "200,302,401"
  }
}

//Create admin target group
resource "aws_alb_target_group" "admin_alb_target_group" {
  name     = "${var.admin_task_definition_name}-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = "${var.vpc-id}"
  target_type = "ip"

  lifecycle {
    create_before_destroy = true
  }

  health_check {
    path             = "/api/${var.admin_task_definition_name}/api.html",
    interval         = "300",
    timeout          = "60",
    matcher          = "200,302,401"
  }
}

//Create compliance target group
resource "aws_alb_target_group" "compliance_alb_target_group" {
  name     = "${var.compliance_task_definition_name}-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = "${var.vpc-id}"
  target_type = "ip"

  lifecycle {
    create_before_destroy = true
  }

  health_check {
    path             = "/api/${var.compliance_task_definition_name}/api.html",
    interval         = "300",
    timeout          = "60",
    matcher          = "200,302,401"
  }
}

//Create notifications target group
resource "aws_alb_target_group" "notifications_alb_target_group" {
  name     = "${var.notifications_task_definition_name}-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = "${var.vpc-id}"
  target_type = "ip"

  lifecycle {
    create_before_destroy = true
  }

  health_check {
    path             = "/api/${var.notifications_task_definition_name}/api.html",
    interval         = "300",
    timeout          = "60",
    matcher          = "200,302,401"
  }
}

//Create statistics target group
resource "aws_alb_target_group" "statistics_alb_target_group" {
  name     = "${var.statistics_task_definition_name}-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = "${var.vpc-id}"
  target_type = "ip"

  lifecycle {
    create_before_destroy = true
  }

  health_check {
    path             = "/api/${var.statistics_task_definition_name}/api.html",
    interval         = "300",
    timeout          = "60",
    matcher          = "200,302,401"
  }
}

//Create asset target group
resource "aws_alb_target_group" "asset_alb_target_group" {
  name     = "${var.asset_task_definition_name}-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = "${var.vpc-id}"
  target_type = "ip"

  lifecycle {
    create_before_destroy = true
  }

  health_check {
    path             = "/api/${var.asset_task_definition_name}/api.html",
    interval         = "300",
    timeout          = "60",
    matcher          = "200,302,401"
  }
}

//Create auth target group
resource "aws_alb_target_group" "auth_alb_target_group" {
  name     = "${var.auth_task_definition_name}-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = "${var.vpc-id}"
  target_type = "ip"

  lifecycle {
    create_before_destroy = true
  }

  health_check {
    path             = "/api/${var.auth_task_definition_name}/api.html",
    interval         = "300",
    timeout          = "60",
    matcher          = "200,302,401"
  }
}

//Configure listener in ALB
# resource "aws_alb_listener" "apis" {
#   load_balancer_arn = "${aws_lb.alb_apijobs.arn}"
#   port              = "80"
#   protocol          = "HTTP"
#   depends_on        = ["aws_alb_target_group.config_alb_target_group", "aws_alb_target_group.admin_alb_target_group"]

#   default_action {
#     target_group_arn = "${aws_alb_target_group.config_alb_target_group.arn}"
#     type             = "forward"
#   }
# }

//Create target group
resource "aws_alb_target_group" "alb_target_group" {
  name     = "${var.task_definition_name}-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = "${var.vpc-id}"
  target_type = "ip"

  lifecycle {
    create_before_destroy = true
  }

  health_check {
    path             = "/${var.task_definition_name}",
    interval         = "120",
    timeout          = "60",
    matcher          = "200"
  }
}

//Configure listener in ALB
resource "aws_alb_listener" "apis" {
  load_balancer_arn = "${aws_lb.alb_apijobs.arn}"
  port              = "80"
  protocol          = "HTTP"
  depends_on        = ["aws_alb_target_group.alb_target_group"]

  default_action {
    target_group_arn = "${aws_alb_target_group.alb_target_group.arn}"
    type             = "forward"
  }
}

//Create config listener rule in ALB
resource "aws_lb_listener_rule" "config" {
  listener_arn = "${aws_alb_listener.apis.arn}"

  action {
    type             = "forward"
    target_group_arn = "${aws_alb_target_group.config_alb_target_group.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/api/config*"]
  }
  depends_on = ["aws_lb.alb_apijobs", "aws_alb_listener.apis", "aws_alb_target_group.config_alb_target_group"]
}

//Create admin listener rule in ALB
resource "aws_lb_listener_rule" "admin" {
  listener_arn = "${aws_alb_listener.apis.arn}"

  action {
    type             = "forward"
    target_group_arn = "${aws_alb_target_group.admin_alb_target_group.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/api/admin*"]
  }
  depends_on = ["aws_lb.alb_apijobs","aws_alb_listener.apis", "aws_alb_target_group.admin_alb_target_group"]
}

//Create compliance listener rule in ALB
resource "aws_lb_listener_rule" "compliance" {
  listener_arn = "${aws_alb_listener.apis.arn}"

  action {
    type             = "forward"
    target_group_arn = "${aws_alb_target_group.compliance_alb_target_group.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/api/compliance*"]
  }
  depends_on = ["aws_lb.alb_apijobs","aws_alb_listener.apis", "aws_alb_target_group.compliance_alb_target_group"]
}

//Create notifications listener rule in ALB
resource "aws_lb_listener_rule" "notifications" {
  listener_arn = "${aws_alb_listener.apis.arn}"

  action {
    type             = "forward"
    target_group_arn = "${aws_alb_target_group.notifications_alb_target_group.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/api/notifications*"]
  }
  depends_on = ["aws_lb.alb_apijobs","aws_alb_listener.apis", "aws_alb_target_group.notifications_alb_target_group"]
}

//Create statistics listener rule in ALB
resource "aws_lb_listener_rule" "statistics" {
  listener_arn = "${aws_alb_listener.apis.arn}"

  action {
    type             = "forward"
    target_group_arn = "${aws_alb_target_group.statistics_alb_target_group.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/api/statistics*"]
  }
  depends_on = ["aws_lb.alb_apijobs","aws_alb_listener.apis", "aws_alb_target_group.statistics_alb_target_group"]
}


//Create asset listener rule in ALB
resource "aws_lb_listener_rule" "asset" {
  listener_arn = "${aws_alb_listener.apis.arn}"

  action {
    type             = "forward"
    target_group_arn = "${aws_alb_target_group.asset_alb_target_group.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/api/asset*"]
  }
  depends_on = ["aws_lb.alb_apijobs","aws_alb_listener.apis", "aws_alb_target_group.asset_alb_target_group"]
}

//Create auth listener rule in ALB
resource "aws_lb_listener_rule" "auth" {
  listener_arn = "${aws_alb_listener.apis.arn}"

  action {
    type             = "forward"
    target_group_arn = "${aws_alb_target_group.auth_alb_target_group.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/api/auth*"]
  }
  depends_on = ["aws_lb.alb_apijobs","aws_alb_listener.apis", "aws_alb_target_group.auth_alb_target_group"]
}

//Create Nginx container definition json
data "template_file" "nginx_task" {
  template = "${file("${path.module}/task_definition.json")}"

  vars {
    image           = "${var.ui_image}"
    ui_container_name = "${var.ui_container_name}"
    log_group       = "${aws_cloudwatch_log_group.uijobs.name}"
    prefix_name     = "${var.task_definition_name}"
    region          = "${var.region}"
  }
  depends_on = ["aws_cloudwatch_log_group.uijobs"]
}


//Create config api container definition json
data "template_file" "config_task" {
  template = "${file("${path.module}/config_task_definition.json")}"

  vars {
    image           = "${var.api_image}"
    log_group       = "${aws_cloudwatch_log_group.apijobs.name}"
    prefix_name     = "${var.config_task_definition_name}"
    region          = "${var.region}"
    JAR_FILE        = "${var.config_jar_file_name}"
    CONFIG_PASSWORD = "${var.CONFIG_PASSWORD}"
  }
  depends_on = ["aws_cloudwatch_log_group.apijobs"]
}

//Create admin api container definition json
data "template_file" "admin_task" {
  template = "${file("${path.module}/admin_task_definition.json")}"

  vars {
    image           = "${var.api_image}"
    log_group       = "${aws_cloudwatch_log_group.apijobs.name}"
    prefix_name     = "${var.admin_task_definition_name}"
    region          = "${var.region}"
    JAR_FILE        = "${var.admin_jar_file_name}"
    CONFIG_PASSWORD="${var.CONFIG_PASSWORD}"
    CONFIG_SERVER_URL="http://${aws_lb.alb_apijobs.dns_name}/api/config"
    ES_CLUSTER_NAME="${var.ES_CLUSTER_NAME}"
    ES_HEIMDALL_HOST_NAME="${var.ES_HEIMDALL_HOST_NAME}"
    ES_HEIMDALL_PORT="${var.ES_HEIMDALL_PORT}"
    ES_HOST_NAME="${var.ES_HOST_NAME}"
    ES_PORT="${var.ES_PORT}"
    LOGGING_ES_HOST_NAME="${var.LOGGING_ES_HOST_NAME}"
    LOGGING_ES_PORT="${var.LOGGING_ES_PORT}"
    PACMAN_HOST_NAME="http://${aws_lb.alb_apijobs.dns_name}"
    RDS_PASSWORD="${var.RDS_PASSWORD}"
    RDS_URL="${var.RDS_URL}"
    RDS_USERNAME="${var.RDS_USERNAME}"
    ES_UPDATE_HOST="${var.ES_UPDATE_HOST}"
    ES_UPDATE_PORT="${var.ES_UPDATE_PORT}"
    ES_UPDATE_CLUSTER_NAME="${var.ES_UPDATE_CLUSTER_NAME}"
    SECURITY_USERNAME = "${var.SECURITY_USERNAME}"
    SECURITY_PASSWORD = "${var.SECURITY_PASSWORD}"
    ACCESS_KEY = "${var.ACCESS_KEY}"
    SECRET_KEY = "${var.SECRET_KEY}"
    DOMAIN_URL = "http://${aws_lb.alb_apijobs.dns_name}/api/admin"
    ADMIN_SERVER = "${var.ADMIN_SERVER}"
    ROLE_ARN = "${var.ROLE_ARN}"
    JOB_FUNCTION_NAME = "${var.JOB_FUNCTION_NAME}"
    JOB_FUNCTION_ARN = "${var.JOB_FUNCTION_ARN}"
    RULE_FUNCTION_NAME = "${var.RULE_FUNCTION_NAME}"
    RULE_FUNCTION_ARN = "${var.RULE_FUNCTION_ARN}"
    RULE_BUCKET_REGION = "${var.RULE_BUCKET_REGION}"
    JOB_LAMBDA_REGION = "${var.JOB_LAMBDA_REGION}"
    JOB_BUCKET_REGION = "${var.JOB_BUCKET_REGION}"
    RULE_LAMBDA_REGION = "${var.RULE_LAMBDA_REGION}"
    RULE_JOB_BUCKET_NAME = "${var.RULE_JOB_BUCKET_NAME}"
  }
  depends_on = ["aws_cloudwatch_log_group.apijobs", "aws_lb.alb_apijobs"]
}

//Create compliance api container definition json
data "template_file" "compliance_task" {
  template = "${file("${path.module}/compliance_task_definition.json")}"

  vars {
    image           = "${var.api_image}"
    log_group       = "${aws_cloudwatch_log_group.apijobs.name}"
    prefix_name     = "${var.compliance_task_definition_name}"
    region          = "${var.region}"
    JAR_FILE        = "${var.compliance_jar_file_name}"
  	CONFIG_PASSWORD="${var.CONFIG_PASSWORD}"
  	CONFIG_SERVER_URL="http://${aws_lb.alb_apijobs.dns_name}/api/config"
  	ES_CLUSTER_NAME="${var.ES_CLUSTER_NAME}"
  	ES_HEIMDALL_HOST_NAME="${var.ES_HEIMDALL_HOST_NAME}"
  	ES_HEIMDALL_PORT="${var.ES_HEIMDALL_PORT}"
  	ES_HOST_NAME="${var.ES_HOST_NAME}"
  	ES_PORT="${var.ES_PORT}"
  	LOGGING_ES_HOST_NAME="${var.LOGGING_ES_HOST_NAME}"
  	LOGGING_ES_PORT="${var.LOGGING_ES_PORT}"
  	PACMAN_HOST_NAME="http://${aws_lb.alb_apijobs.dns_name}"
  	RDS_PASSWORD="${var.RDS_PASSWORD}"
  	RDS_URL="${var.RDS_URL}"
  	RDS_USERNAME="${var.RDS_USERNAME}"
  	REDSHIFT_URL="${var.REDSHIFT_URL}"
  	REDSHIFT_USER_NAME="${var.REDSHIFT_USERNAME}"
  	REDSHIFT_PASSWORD="${var.REDSHIFT_PASSWORD}"
  	ES_UPDATE_HOST="${var.ES_UPDATE_HOST}"
  	ES_UPDATE_PORT="${var.ES_UPDATE_PORT}"
  	ES_UPDATE_CLUSTER_NAME="${var.ES_UPDATE_CLUSTER_NAME}"
  	LDAP_DOMAIN="${var.LDAP_DOMAIN}"
  	LDAP_BASEDN="${var.LDAP_BASEDN}"
  	LDAP_PORT="${var.LDAP_PORT}"
  	LDAP_RESPONSETIMEOUT="${var.LDAP_RESPONSETIMEOUT}"
  	LDAP_CONNECTIONTIMEOUT="${var.LDAP_CONNECTIONTIMEOUT}"
  	LDAP_HOSTLIST="${var.LDAP_HOSTLIST}"
  	CERTIFICATE_FEATURE_ENABLED="${var.CERTIFICATE_FEATURE_ENABLED}"
  	PATCHING_FEATURE_ENABLED="${var.PATCHING_FEATURE_ENABLED}"
  	VULNERABILITY_FEATURE_ENABLED="${var.VULNERABILITY_FEATURE_ENABLED}"
    }
    depends_on = ["aws_cloudwatch_log_group.apijobs", "aws_lb.alb_apijobs"]
  }

//Create notifications api container definition json
data "template_file" "notifications_task" {
  template = "${file("${path.module}/notifications_task_definition.json")}"

  vars {
    image           = "${var.api_image}"
    log_group       = "${aws_cloudwatch_log_group.apijobs.name}"
    prefix_name     = "${var.notifications_task_definition_name}"
    region          = "${var.region}"
    JAR_FILE        = "${var.notifications_jar_file_name}"
    CONFIG_PASSWORD="${var.CONFIG_PASSWORD}"
  	CONFIG_SERVER_URL="http://${aws_lb.alb_apijobs.dns_name}/api/config"
  	ES_CLUSTER_NAME="${var.ES_CLUSTER_NAME}"
  	ES_HEIMDALL_HOST_NAME="${var.ES_HEIMDALL_HOST_NAME}"
  	ES_HEIMDALL_PORT="${var.ES_HEIMDALL_PORT}"
  	ES_HOST_NAME="${var.ES_HOST_NAME}"
  	ES_PORT="${var.ES_PORT}"
  	LOGGING_ES_HOST_NAME="${var.LOGGING_ES_HOST_NAME}"
  	LOGGING_ES_PORT="${var.LOGGING_ES_PORT}"
  	PACMAN_HOST_NAME="http://${aws_lb.alb_apijobs.dns_name}"
  	RDS_PASSWORD="${var.RDS_PASSWORD}"
  	RDS_URL="${var.RDS_URL}"
  	RDS_USERNAME="${var.RDS_USERNAME}"
  	REDSHIFT_URL="${var.REDSHIFT_URL}"
  	REDSHIFT_USER_NAME="${var.REDSHIFT_USERNAME}"
  	REDSHIFT_PASSWORD="${var.REDSHIFT_PASSWORD}"
  	ES_UPDATE_HOST="${var.ES_UPDATE_HOST}"
  	ES_UPDATE_PORT="${var.ES_UPDATE_PORT}"
  	ES_UPDATE_CLUSTER_NAME="${var.ES_UPDATE_CLUSTER_NAME}"
  	LDAP_DOMAIN="${var.LDAP_DOMAIN}"
  	LDAP_BASEDN="${var.LDAP_BASEDN}"
  	LDAP_PORT="${var.LDAP_PORT}"
  	LDAP_RESPONSETIMEOUT="${var.LDAP_RESPONSETIMEOUT}"
  	LDAP_CONNECTIONTIMEOUT="${var.LDAP_CONNECTIONTIMEOUT}"
  	LDAP_HOSTLIST="${var.LDAP_HOSTLIST}"
    }
    depends_on = ["aws_cloudwatch_log_group.apijobs", "aws_lb.alb_apijobs"]
  }

//Create statistics api container definition json
data "template_file" "statistics_task" {
  template = "${file("${path.module}/statistics_task_definition.json")}"

  vars {
    image           = "${var.api_image}"
    log_group       = "${aws_cloudwatch_log_group.apijobs.name}"
    prefix_name     = "${var.statistics_task_definition_name}"
    region          = "${var.region}"
    JAR_FILE        = "${var.statistics_jar_file_name}"
    CONFIG_PASSWORD="${var.CONFIG_PASSWORD}"
  	CONFIG_SERVER_URL="http://${aws_lb.alb_apijobs.dns_name}/api/config"
  	ES_CLUSTER_NAME="${var.ES_CLUSTER_NAME}"
  	ES_HEIMDALL_HOST_NAME="${var.ES_HEIMDALL_HOST_NAME}"
  	ES_HEIMDALL_PORT="${var.ES_HEIMDALL_PORT}"
  	ES_HOST_NAME="${var.ES_HOST_NAME}"
  	ES_PORT="${var.ES_PORT}"
  	LOGGING_ES_HOST_NAME="${var.LOGGING_ES_HOST_NAME}"
  	LOGGING_ES_PORT="${var.LOGGING_ES_PORT}"
  	PACMAN_HOST_NAME="http://${aws_lb.alb_apijobs.dns_name}"
  	RDS_PASSWORD="${var.RDS_PASSWORD}"
  	RDS_URL="${var.RDS_URL}"
  	RDS_USERNAME="${var.RDS_USERNAME}"
  	REDSHIFT_URL="${var.REDSHIFT_URL}"
  	REDSHIFT_USER_NAME="${var.REDSHIFT_USERNAME}"
  	REDSHIFT_PASSWORD="${var.REDSHIFT_PASSWORD}"
  	ES_UPDATE_HOST="${var.ES_UPDATE_HOST}"
  	ES_UPDATE_PORT="${var.ES_UPDATE_PORT}"
  	ES_UPDATE_CLUSTER_NAME="${var.ES_UPDATE_CLUSTER_NAME}"
  	LDAP_DOMAIN="${var.LDAP_DOMAIN}"
  	LDAP_BASEDN="${var.LDAP_BASEDN}"
  	LDAP_PORT="${var.LDAP_PORT}"
  	LDAP_RESPONSETIMEOUT="${var.LDAP_RESPONSETIMEOUT}"
  	LDAP_CONNECTIONTIMEOUT="${var.LDAP_CONNECTIONTIMEOUT}"
  	LDAP_HOSTLIST="${var.LDAP_HOSTLIST}"
    }
    depends_on = ["aws_cloudwatch_log_group.apijobs", "aws_lb.alb_apijobs"]
  }


//Create asset api container definition json
data "template_file" "asset_task" {
  template = "${file("${path.module}/asset_task_definition.json")}"

  vars {
    image           = "${var.api_image}"
    log_group       = "${aws_cloudwatch_log_group.apijobs.name}"
    prefix_name     = "${var.asset_task_definition_name}"
    region          = "${var.region}"
    JAR_FILE        = "${var.asset_jar_file_name}"
    CONFIG_PASSWORD="${var.CONFIG_PASSWORD}"
  	CONFIG_SERVER_URL="http://${aws_lb.alb_apijobs.dns_name}/api/config"
  	ES_CLUSTER_NAME="${var.ES_CLUSTER_NAME}"
  	ES_HEIMDALL_HOST_NAME="${var.ES_HEIMDALL_HOST_NAME}"
  	ES_HEIMDALL_PORT="${var.ES_HEIMDALL_PORT}"
  	ES_HOST_NAME="${var.ES_HOST_NAME}"
  	ES_PORT="${var.ES_PORT}"
  	LOGGING_ES_HOST_NAME="${var.LOGGING_ES_HOST_NAME}"
  	LOGGING_ES_PORT="${var.LOGGING_ES_PORT}"
  	PACMAN_HOST_NAME="http://${aws_lb.alb_apijobs.dns_name}"
  	RDS_PASSWORD="${var.RDS_PASSWORD}"
  	RDS_URL="${var.RDS_URL}"
  	RDS_USERNAME="${var.RDS_USERNAME}"
  	REDSHIFT_URL="${var.REDSHIFT_URL}"
  	REDSHIFT_USER_NAME="${var.REDSHIFT_USERNAME}"
  	REDSHIFT_PASSWORD="${var.REDSHIFT_PASSWORD}"
  	ES_UPDATE_HOST="${var.ES_UPDATE_HOST}"
  	ES_UPDATE_PORT="${var.ES_UPDATE_PORT}"
  	ES_UPDATE_CLUSTER_NAME="${var.ES_UPDATE_CLUSTER_NAME}"
  	LDAP_DOMAIN="${var.LDAP_DOMAIN}"
  	LDAP_BASEDN="${var.LDAP_BASEDN}"
  	LDAP_PORT="${var.LDAP_PORT}"
  	LDAP_RESPONSETIMEOUT="${var.LDAP_RESPONSETIMEOUT}"
  	LDAP_CONNECTIONTIMEOUT="${var.LDAP_CONNECTIONTIMEOUT}"
  	LDAP_HOSTLIST="${var.LDAP_HOSTLIST}"
  	CLOUD_INSIGHTS_COST_URL="${var.CLOUD_INSIGHTS_COST_URL}"
  	CLOUD_INSIGHTS_TOKEN_URL="${var.CLOUD_INSIGHTS_TOKEN_URL}"
  	SVC_CORP_PASSWORD="${var.SVC_CORP_PASSWORD}"
  	SVC_CORP_USER_ID="${var.SVC_CORP_USER_ID}"
    }
    depends_on = ["aws_cloudwatch_log_group.apijobs", "aws_lb.alb_apijobs"]
  }


//Create auth api container definition json
data "template_file" "auth_task" {
  template = "${file("${path.module}/auth_task_definition.json")}"

  vars {
    image           = "${var.api_image}"
    log_group       = "${aws_cloudwatch_log_group.apijobs.name}"
    prefix_name     = "${var.auth_task_definition_name}"
    region          = "${var.region}"
    JAR_FILE        = "${var.auth_jar_file_name}"
    CONFIG_PASSWORD="${var.CONFIG_PASSWORD}"
    DOMAIN_URL = "http://${aws_lb.alb_apijobs.dns_name}/api/auth"
  	CONFIG_SERVER_URL="http://${aws_lb.alb_apijobs.dns_name}/api/config"
  	ES_CLUSTER_NAME="${var.ES_CLUSTER_NAME}"
  	ES_HEIMDALL_HOST_NAME="${var.ES_HEIMDALL_HOST_NAME}"
  	ES_HEIMDALL_PORT="${var.ES_HEIMDALL_PORT}"
  	ES_HOST_NAME="${var.ES_HOST_NAME}"
  	ES_PORT="${var.ES_PORT}"
  	LOGGING_ES_HOST_NAME="${var.LOGGING_ES_HOST_NAME}"
  	LOGGING_ES_PORT="${var.LOGGING_ES_PORT}"
  	PACMAN_HOST_NAME="http://${aws_lb.alb_apijobs.dns_name}"
  	RDS_PASSWORD="${var.RDS_PASSWORD}"
  	RDS_URL="${var.RDS_URL}"
  	RDS_USERNAME="${var.RDS_USERNAME}"
  	REDSHIFT_URL="${var.REDSHIFT_URL}"
  	REDSHIFT_USER_NAME="${var.REDSHIFT_USERNAME}"
  	REDSHIFT_PASSWORD="${var.REDSHIFT_PASSWORD}"
  	ES_UPDATE_HOST="${var.ES_UPDATE_HOST}"
  	ES_UPDATE_PORT="${var.ES_UPDATE_PORT}"
  	ES_UPDATE_CLUSTER_NAME="${var.ES_UPDATE_CLUSTER_NAME}"
  	LDAP_DOMAIN="${var.LDAP_DOMAIN}"
  	LDAP_BASEDN="${var.LDAP_BASEDN}"
  	LDAP_PORT="${var.LDAP_PORT}"
  	LDAP_RESPONSETIMEOUT="${var.LDAP_RESPONSETIMEOUT}"
  	LDAP_CONNECTIONTIMEOUT="${var.LDAP_CONNECTIONTIMEOUT}"
  	LDAP_HOSTLIST="${var.LDAP_HOSTLIST}"
    OAUTH2_CLIENT_ID="${var.OAUTH2_CLIENT_ID}"
    }
    depends_on = ["aws_cloudwatch_log_group.apijobs", "aws_lb.alb_apijobs"]
  }

//Create Nginx Task definition
resource "aws_ecs_task_definition" "nginx" {
  family                   = "${var.task_definition_name}"
  container_definitions    = "${data.template_file.nginx_task.rendered}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = "${var.ecs_execution_role_arn}"
  task_role_arn            = "${var.ecs_execution_role_arn}"
}

//Create config api Task definition
resource "aws_ecs_task_definition" "config" {
  family                   = "${var.config_task_definition_name}"
  container_definitions    = "${data.template_file.config_task.rendered}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "2048"
  memory                   = "4096"
  execution_role_arn       = "${var.ecs_execution_role_arn}"
  task_role_arn            = "${var.ecs_execution_role_arn}"
}

//Create admin api Task definition
resource "aws_ecs_task_definition" "admin" {
  family                   = "${var.admin_task_definition_name}"
  container_definitions    = "${data.template_file.admin_task.rendered}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "2048"
  memory                   = "4096"
  execution_role_arn       = "${var.ecs_execution_role_arn}"
  task_role_arn            = "${var.ecs_execution_role_arn}"
}

//Create compliance api Task definition
resource "aws_ecs_task_definition" "compliance" {
  family                   = "${var.compliance_task_definition_name}"
  container_definitions    = "${data.template_file.compliance_task.rendered}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "2048"
  memory                   = "4096"
  execution_role_arn       = "${var.ecs_execution_role_arn}"
  task_role_arn            = "${var.ecs_execution_role_arn}"
}

//Create notifications api Task definition
resource "aws_ecs_task_definition" "notifications" {
  family                   = "${var.notifications_task_definition_name}"
  container_definitions    = "${data.template_file.notifications_task.rendered}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "2048"
  memory                   = "4096"
  execution_role_arn       = "${var.ecs_execution_role_arn}"
  task_role_arn            = "${var.ecs_execution_role_arn}"
}

//Create statistics api Task definition
resource "aws_ecs_task_definition" "statistics" {
  family                   = "${var.statistics_task_definition_name}"
  container_definitions    = "${data.template_file.statistics_task.rendered}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "2048"
  memory                   = "4096"
  execution_role_arn       = "${var.ecs_execution_role_arn}"
  task_role_arn            = "${var.ecs_execution_role_arn}"
}

//Create asset api Task definition
resource "aws_ecs_task_definition" "asset" {
  family                   = "${var.asset_task_definition_name}"
  container_definitions    = "${data.template_file.asset_task.rendered}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "2048"
  memory                   = "4096"
  execution_role_arn       = "${var.ecs_execution_role_arn}"
  task_role_arn            = "${var.ecs_execution_role_arn}"
}

//Create auth api Task definition
resource "aws_ecs_task_definition" "auth" {
  family                   = "${var.auth_task_definition_name}"
  container_definitions    = "${data.template_file.auth_task.rendered}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "2048"
  memory                   = "4096"
  execution_role_arn       = "${var.ecs_execution_role_arn}"
  task_role_arn            = "${var.ecs_execution_role_arn}"
}

//Get family of Nginx task definition
data "aws_ecs_task_definition" "nginx" {
  task_definition = "${aws_ecs_task_definition.nginx.family}"
  depends_on = ["aws_ecs_task_definition.nginx"]
}

//Get family of config task definition
data "aws_ecs_task_definition" "config" {
  task_definition = "${aws_ecs_task_definition.config.family}"
  depends_on = ["aws_ecs_task_definition.config"]
}

//Get family of admin task definition
data "aws_ecs_task_definition" "admin" {
  task_definition = "${aws_ecs_task_definition.admin.family}"
  depends_on = ["aws_ecs_task_definition.admin"]
}

//Get family of asset task definition
data "aws_ecs_task_definition" "asset" {
  task_definition = "${aws_ecs_task_definition.asset.family}"
  depends_on = ["aws_ecs_task_definition.asset"]
}

//Get family of compliance task definition
data "aws_ecs_task_definition" "compliance" {
  task_definition = "${aws_ecs_task_definition.compliance.family}"
  depends_on = ["aws_ecs_task_definition.compliance"]
}

//Get family of notifications task definition
data "aws_ecs_task_definition" "notifications" {
  task_definition = "${aws_ecs_task_definition.notifications.family}"
  depends_on = ["aws_ecs_task_definition.notifications"]
}

//Get family of statistics task definition
data "aws_ecs_task_definition" "statistics" {
  task_definition = "${aws_ecs_task_definition.statistics.family}"
  depends_on = ["aws_ecs_task_definition.statistics"]
}

//Get family of auth task definition
data "aws_ecs_task_definition" "auth" {
  task_definition = "${aws_ecs_task_definition.auth.family}"
  depends_on = ["aws_ecs_task_definition.auth"]
}

//Create Nginx service in ECS
resource "aws_ecs_service" "nginx" {
  name            = "${var.task_definition_name}"
  task_definition = "${aws_ecs_task_definition.nginx.family}:${max("${aws_ecs_task_definition.nginx.revision}", "${data.aws_ecs_task_definition.nginx.revision}")}"
  desired_count   = 1
  launch_type     = "FARGATE"
  cluster =       "${aws_ecs_cluster.cluster.id}"

  network_configuration {
    security_groups = ["${var.alb_sg}"]
    subnets         = ["${var.subnetid}"]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.alb_target_group.arn}"
    container_name   = "${var.ui_container_name}"
    container_port   = "80"
  }
    depends_on = ["aws_alb_target_group.alb_target_group", "aws_lb.alb_apijobs","aws_alb_listener.apis"]
}


//Create config service in ECS
resource "aws_ecs_service" "config" {
  name            = "${var.config_task_definition_name}"
  task_definition = "${aws_ecs_task_definition.config.family}:${max("${aws_ecs_task_definition.config.revision}", "${data.aws_ecs_task_definition.config.revision}")}"
  desired_count   = 1
  launch_type     = "FARGATE"
  cluster =       "${aws_ecs_cluster.cluster.id}"

  network_configuration {
    security_groups = ["${var.alb_sg}"]
    subnets         = ["${var.subnetid}"]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.config_alb_target_group.arn}"
    container_name   = "${var.config_task_definition_name}"
    container_port   = "80"
  }
    depends_on = ["aws_alb_target_group.config_alb_target_group", "aws_lb.alb_apijobs", "aws_lb_listener_rule.config"]
}

//Create admin service in ECS
resource "aws_ecs_service" "admin" {
  name            = "${var.admin_task_definition_name}"
  task_definition = "${aws_ecs_task_definition.admin.family}:${max("${aws_ecs_task_definition.admin.revision}", "${data.aws_ecs_task_definition.admin.revision}")}"
  desired_count   = 1
  launch_type     = "FARGATE"
  cluster =       "${aws_ecs_cluster.cluster.id}"

  network_configuration {
    security_groups = ["${var.alb_sg}"]
    subnets         = ["${var.subnetid}"]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.admin_alb_target_group.arn}"
    container_name   = "${var.admin_task_definition_name}"
    container_port   = "80"
  }
    depends_on = ["aws_alb_target_group.admin_alb_target_group", "aws_lb.alb_apijobs", "aws_lb_listener_rule.admin"]
}

//Create compliance service in ECS
resource "aws_ecs_service" "compliance" {
  name            = "${var.compliance_task_definition_name}"
  task_definition = "${aws_ecs_task_definition.compliance.family}:${max("${aws_ecs_task_definition.compliance.revision}", "${data.aws_ecs_task_definition.compliance.revision}")}"
  desired_count   = 1
  launch_type     = "FARGATE"
  cluster =       "${aws_ecs_cluster.cluster.id}"

  network_configuration {
    security_groups = ["${var.alb_sg}"]
    subnets         = ["${var.subnetid}"]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.compliance_alb_target_group.arn}"
    container_name   = "${var.compliance_task_definition_name}"
    container_port   = "80"
  }
    depends_on = ["aws_alb_target_group.compliance_alb_target_group", "aws_lb.alb_apijobs", "aws_lb_listener_rule.compliance"]
}

//Create notifications service in ECS
resource "aws_ecs_service" "notifications" {
  name            = "${var.notifications_task_definition_name}"
  task_definition = "${aws_ecs_task_definition.notifications.family}:${max("${aws_ecs_task_definition.notifications.revision}", "${data.aws_ecs_task_definition.notifications.revision}")}"
  desired_count   = 1
  launch_type     = "FARGATE"
  cluster =       "${aws_ecs_cluster.cluster.id}"

  network_configuration {
    security_groups = ["${var.alb_sg}"]
    subnets         = ["${var.subnetid}"]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.notifications_alb_target_group.arn}"
    container_name   = "${var.notifications_task_definition_name}"
    container_port   = "80"
  }
    depends_on = ["aws_alb_target_group.notifications_alb_target_group", "aws_lb.alb_apijobs", "aws_lb_listener_rule.notifications"]
}

//Create statistics service in ECS
resource "aws_ecs_service" "statistics" {
  name            = "${var.statistics_task_definition_name}"
  task_definition = "${aws_ecs_task_definition.statistics.family}:${max("${aws_ecs_task_definition.statistics.revision}", "${data.aws_ecs_task_definition.statistics.revision}")}"
  desired_count   = 1
  launch_type     = "FARGATE"
  cluster =       "${aws_ecs_cluster.cluster.id}"

  network_configuration {
    security_groups = ["${var.alb_sg}"]
    subnets         = ["${var.subnetid}"]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.statistics_alb_target_group.arn}"
    container_name   = "${var.statistics_task_definition_name}"
    container_port   = "80"
  }
    depends_on = ["aws_alb_target_group.statistics_alb_target_group", "aws_lb.alb_apijobs", "aws_lb_listener_rule.statistics"]
}


//Create asset service in ECS
resource "aws_ecs_service" "asset" {
  name            = "${var.asset_task_definition_name}"
  task_definition = "${aws_ecs_task_definition.asset.family}:${max("${aws_ecs_task_definition.asset.revision}", "${data.aws_ecs_task_definition.asset.revision}")}"
  desired_count   = 1
  launch_type     = "FARGATE"
  cluster =       "${aws_ecs_cluster.cluster.id}"

  network_configuration {
    security_groups = ["${var.alb_sg}"]
    subnets         = ["${var.subnetid}"]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.asset_alb_target_group.arn}"
    container_name   = "${var.asset_task_definition_name}"
    container_port   = "80"
  }
    depends_on = ["aws_alb_target_group.asset_alb_target_group", "aws_lb.alb_apijobs", "aws_lb_listener_rule.asset"]
}

//Create auth service in ECS
resource "aws_ecs_service" "auth" {
  name            = "${var.auth_task_definition_name}"
  task_definition = "${aws_ecs_task_definition.auth.family}:${max("${aws_ecs_task_definition.auth.revision}", "${data.aws_ecs_task_definition.auth.revision}")}"
  desired_count   = 1
  launch_type     = "FARGATE"
  cluster =       "${aws_ecs_cluster.cluster.id}"

  network_configuration {
    security_groups = ["${var.alb_sg}"]
    subnets         = ["${var.subnetid}"]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.auth_alb_target_group.arn}"
    container_name   = "${var.auth_task_definition_name}"
    container_port   = "80"
  }
    depends_on = ["aws_alb_target_group.auth_alb_target_group", "aws_lb.alb_apijobs", "aws_lb_listener_rule.auth"]
}

output "alb_dns_name" {
  value = "${aws_lb.alb_apijobs.dns_name}"
}
output "pacbot" {
  value = "${aws_lb.alb_apijobs.dns_name}"
}

