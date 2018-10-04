//Create Cloudwatch Log Group
resource "aws_cloudwatch_log_group" "uijobs" {
  name = "${var.ui_cloudwatch_group}"
  retention_in_days = 7
}

//Create ECS Cluster for Pacman-OSS-UI
resource "aws_ecs_cluster" "cluster" {
  name = "${var.ui-ecs-cluster}"
}

//Create container definition json
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

//Create Task definition
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

//Create Application Loadbalancer
resource "aws_lb" "alb_ui" {
  name            = "${var.alb_name}"
  internal           = true
  load_balancer_type = "application"
  security_groups    = ["${var.alb_sg}"]
  subnets            = ["${var.subnetid}"]

  tags {
    Name        = "${var.alb_name}"
  }
}

//Configure listener in ALB
resource "aws_alb_listener" "ui" {
  load_balancer_arn = "${aws_lb.alb_ui.arn}"
  port              = "80"
  protocol          = "HTTP"
  depends_on        = ["aws_alb_target_group.alb_target_group"]

  default_action {
    target_group_arn = "${aws_alb_target_group.alb_target_group.arn}"
    type             = "forward"
  }
}

data "aws_ecs_task_definition" "nginx" {
  task_definition = "${aws_ecs_task_definition.nginx.family}"
  depends_on = ["aws_ecs_task_definition.nginx"]
}


//Create service in ECS
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
    depends_on = ["aws_alb_target_group.alb_target_group", "aws_lb.alb_ui"]
}

output "ui_alb_dns_name" {
  value = "${aws_lb.alb_ui.dns_name}"
}
output "pacman" {
  value = "${aws_lb.alb_ui.dns_name}"
}
