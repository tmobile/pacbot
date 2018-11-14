resource "aws_iam_role" "pacecsrole" {
    name = "${var.pacecsrole}"
assume_role_policy="${var.check ? data.aws_iam_policy_document.pacecspolicy.json : data.aws_iam_policy_document.pacecsassumepolicy.json}"
    description = "DO-NOT-DELETE-This resource is created as part of PacBot installation"
}
data "aws_iam_policy_document" "pacecspolicy" {
    statement
     {
        actions = ["sts:AssumeRole"]
        principals {
            type="Service",
            identifiers = [
                "ec2.amazonaws.com","ecs-tasks.amazonaws.com"
            ]
        }
        effect = "Allow"
    }
}
data "aws_iam_policy_document" "pacecsassumepolicy" {
    statement
     {
        actions = ["sts:AssumeRole"]
        principals {
            type="Service",
            identifiers = [
                "ec2.amazonaws.com","ecs-tasks.amazonaws.com"
            ]
        }
      }
      statement
       {
        actions = ["sts:AssumeRole"]
        principals {
            type="AWS",
            identifiers = [ "arn:aws:iam::${var.accountid}:role/${var.pacecsrole}",
                            "arn:aws:iam::${var.accountid}:root",
                "arn:aws:iam::${var.accountid}:role/${var.baseaccountrole}" ]
        }
        condition = {
            test = "Bool"
            variable = "aws:MultiFactorAuthPresent"
            values = ["false"]
        }
      }
}


resource "aws_iam_role_policy_attachment" "pacecs-policy-attachment" {
  role       = "${aws_iam_role.pacecsrole.name}"
  count      = "${length(var.pacecsrolepolicy)}"
  policy_arn = "${var.pacecsrolepolicy[count.index]}"
}


resource "aws_iam_instance_profile" "pacecs_instance_profile" {
  name  = "${var.pacecsrole}"
  role = "${aws_iam_role.pacecsrole.name}"
}
resource "aws_iam_role_policy_attachment" "pacecs-attach6" {
  role      = "${aws_iam_role.pacecsrole.name}"
  policy_arn = "arn:aws:iam::${var.accountid}:policy/${var.baseaccountrole}"
}

