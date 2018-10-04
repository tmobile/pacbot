resource "aws_security_group" "pacman-sg" {
  name        = "pacman"
  description = "This resource is created as part of PacMan installation. Do not delete - It may break application"
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
    Name = "pacman"
  }
}

output "pacman_sgid" {
    value = "${aws_security_group.pacman-sg.id}"
}
output "pacman" {
    value = "${aws_security_group.pacman-sg.id}"
}
