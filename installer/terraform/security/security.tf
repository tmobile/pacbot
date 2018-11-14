resource "aws_security_group" "pacbot-sg" {
  name        = "pacbot"
  description = "DO-NOT-DELETE-This resource is created as part of PacBot installation"
  vpc_id      = "${var.vpc-id}"

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["${var.cidr_block-ip}"]
  }
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["10.0.0.0/8"]
  }

  egress {
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    cidr_blocks     = ["0.0.0.0/0"]
  }
  tags {
    Name = "pacbot"
  }
}

output "pacbot_sgid" {
    value = "${aws_security_group.pacbot-sg.id}"
}
output "pacbot" {
    value = "${aws_security_group.pacbot-sg.id}"
}
