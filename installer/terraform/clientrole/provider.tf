provider "aws" {
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region     = "${var.region}"
  assume_role{
    role_arn="arn:aws:iam::${var.client_accountid}:role/${var.client_assumerole}"
    session_name="Pacman_Session_Role"
  }
}
