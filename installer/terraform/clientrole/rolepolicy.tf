resource "aws_iam_role" "clientrole" {
    name = "${var.client_accountrole}"
assume_role_policy=<<EOF
{
    "Version": "2012-10-17",
    "Statement": [
     {
        "Action": "sts:AssumeRole",
        "Principal": {
        "Service": "ec2.amazonaws.com",
         "AWS" : [
             "arn:aws:iam::${var.accountid}:role/${var.base_accountrole}"
         ]
      },
      "Effect": "Allow",
      "Sid": ""
    }
    ]
}
EOF
}
resource "aws_iam_role_policy_attachment" "pacman-attach1" {
    role       = "${aws_iam_role.clientrole.name}"
    policy_arn = "arn:aws:iam::aws:policy/ReadOnlyAccess"
}
resource "aws_iam_role_policy_attachment" "pacman-attach2" {
    role       = "${aws_iam_role.clientrole.name}"
     policy_arn = "arn:aws:iam::aws:policy/AmazonGuardDutyReadOnlyAccess"
}
resource "aws_iam_role_policy_attachment" "pacman-attach3" {
    role       = "${aws_iam_role.clientrole.name}"
    policy_arn = "arn:aws:iam::aws:policy/AWSSupportAccess"
}

