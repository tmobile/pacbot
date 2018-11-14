resource "aws_redshift_cluster" "pacbot-redshift-cluster" {
  cluster_identifier = "${var.cluster_identifier}"
  database_name      = "${var.cluster_database_name}"
  master_username    = "${var.cluster_master_username}"
  master_password    = "${var.cluster_master_password}"
  node_type          = "${var.cluster_node_type}"
  cluster_type       = "${var.cluster_type}"
  number_of_nodes    = "${var.cluster_number_of_nodes}"
  skip_final_snapshot = true
  publicly_accessible = false
  vpc_security_group_ids = ["${var.pacbot_sgid}"]
  cluster_parameter_group_name = "${var.parameter_group_name}"
  cluster_subnet_group_name = "${var.subnet_group_name}"
  depends_on = ["aws_redshift_parameter_group.pacbot_redshift_parameter_group",
                "aws_redshift_subnet_group.pacbot_redshift_subnet_group"]
}
output "redshift_endpoint" {
  value = "${aws_redshift_cluster.pacbot-redshift-cluster.endpoint}"
}
output "pacbot"
{
    value = "${aws_redshift_cluster.pacbot-redshift-cluster.endpoint}"
}
resource "aws_redshift_parameter_group" "pacbot_redshift_parameter_group" {
  name   = "${var.parameter_group_name}"
  description = "DO-NOT-DELETE-This resource is created as part of PacBot installation"
  family = "redshift-1.0"
  parameter {
    name  = "require_ssl"
    value = "false"
  }
}

resource "aws_redshift_subnet_group" "pacbot_redshift_subnet_group" {
  name       = "${var.subnet_group_name}"
  description = "DO-NOT-DELETE-This resource is created as part of PacBot installation"
  subnet_ids = ["${var.subnet_list}"]
  tags {
    environment = "pacbot-redshift"
  }
}
