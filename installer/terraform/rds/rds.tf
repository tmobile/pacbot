resource "aws_db_instance" "pacmandb" {
  identifier           = "${var.identifier}"
  allocated_storage    = 20
  storage_type         = "gp2"
  engine               = "${var.rdsdatabase}"
  engine_version       = "${var.rdsdbversion}"
  instance_class       = "${var.rdsinstance}"
  name                 = "${var.rdsname}"
  username             = "${var.user_name}"
  password             = "${var.user_pwd}"
  parameter_group_name = "${var.parameter_group_name}"
  db_subnet_group_name = "${var.subnet_group_name}"
  option_group_name    = "${var.option_group_name}"
  #final_snapshot_identifier = "${var.snapshotname}"
  skip_final_snapshot = true
  vpc_security_group_ids = ["${var.pacman_sgid}"]
  depends_on = ["aws_db_option_group.pacman_db_option_group",
                "aws_db_parameter_group.pacman_db_parameter_group"
                ,"aws_db_subnet_group.pacman_subnet_group"]
}
output "rds_endpoint" {
   value = "${aws_db_instance.pacmandb.endpoint}"
}

output "pacman" {
   value = "${aws_db_instance.pacmandb.endpoint}"
}

resource "aws_db_option_group" "pacman_db_option_group" {
  name                     = "${var.option_group_name}"
  option_group_description = "pacman Option Group"
  engine_name              = "${var.engine_name}"
  major_engine_version     = "${var.engine_version}"
}
resource "aws_db_parameter_group" "pacman_db_parameter_group" {
  name   = "${var.parameter_group_name}"
  family = "${var.family_name}"
}
resource "aws_db_subnet_group" "pacman_subnet_group" {
  name       = "${var.subnet_group_name}"
  subnet_ids = ["${var.subnet_list}"]
}
