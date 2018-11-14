resource "aws_iam_role" "role" {
    name = "${var.baseaccountrole}"
    assume_role_policy = "${var.check ? data.aws_iam_policy_document.policy.json : data.aws_iam_policy_document.assumepolicy.json}"
    description = "DO-NOT-DELETE-This resource is created as part of PacBot installation"
}
resource "aws_iam_role_policy_attachment" "pacbot-attach1" {
    role       = "${aws_iam_role.role.name}"
    policy_arn = "arn:aws:iam::aws:policy/ReadOnlyAccess"
}
resource "aws_iam_role_policy_attachment" "pacbot-attach2" {
    role       = "${aws_iam_role.role.name}"
     policy_arn = "arn:aws:iam::aws:policy/AmazonGuardDutyReadOnlyAccess"
}
resource "aws_iam_role_policy_attachment" "pacbot-attach3" {
    role       = "${aws_iam_role.role.name}"
    policy_arn = "arn:aws:iam::aws:policy/AWSSupportAccess"
}
resource "aws_iam_role_policy_attachment" "pacbot-attach4" {
    role       = "${aws_iam_role.role.name}"
    policy_arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
}

resource "aws_iam_role_policy_attachment" "pacbot-attach5" {
    role       = "${aws_iam_role.role.name}"
    policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}


resource "aws_iam_role" "lambda_role" {
    name="${var.lambda_role}"
    assume_role_policy="${data.aws_iam_policy_document.policy.json}"
    description = "DO-NOT-DELETE-This resource is created as part of PacBot installation"
}

resource "aws_iam_role_policy_attachment" "lambda_attach1" {
    role       = "${aws_iam_role.lambda_role.name}"
    policy_arn = "arn:aws:iam::aws:policy/AWSLambdaFullAccess"
}

resource "aws_iam_role_policy_attachment" "lambda_attach2" {
    role       = "${aws_iam_role.lambda_role.name}"
    policy_arn = "arn:aws:iam::aws:policy/AWSBatchFullAccess"
}

resource "aws_iam_role_policy_attachment" "lambda_attach3" {
    role       = "${aws_iam_role.lambda_role.name}"
    policy_arn = "arn:aws:iam::aws:policy/CloudWatchFullAccess"
}

data "aws_iam_policy_document" "policy" {
    statement
     {
        actions = ["sts:AssumeRole"]
        principals {
            type="Service",
            identifiers = [
                "lambda.amazonaws.com"
            ]
        }
        effect = "Allow"
    }

}
data "aws_iam_policy_document" "assumepolicy" {
    statement
     {
        actions = ["sts:AssumeRole"]
        principals {
            type="Service",
            identifiers = [
                "batch.amazonaws.com","ecs-tasks.amazonaws.com"
            ]
        }
      }
      statement
       {
        actions = ["sts:AssumeRole"]
        principals {
            type="AWS",
            identifiers = [ "arn:aws:iam::${var.accountid}:role/${var.pacecsrole}" ]
        }

      }
}

data "aws_iam_policy_document" "pac_ro_policy_attach_1" {
statement {
    actions = [
      "sts:AssumeRole",
    ]
     resources = [ "arn:aws:iam::${var.accountid}:role/${var.baseaccountrole}" ]
}

}

data "aws_iam_policy_document" "pac_ro_policy_attach_2" {
statement {
    actions = [
      "sts:AssumeRole",
    ]
    resources = [
      "arn:aws:iam::${var.accountid}:role/${var.baseaccountrole}"
    ]
  }
}
resource "aws_iam_policy" "pac_ro_policy" {
  name   = "${var.baseaccountrole}"
  path   = "/"
  policy = "${var.check ? data.aws_iam_policy_document.pac_ro_policy_attach_1.json : data.aws_iam_policy_document.pac_ro_policy_attach_2.json}"
}

resource "aws_iam_role_policy_attachment" "baserole-attach-1" {
  role      = "${aws_iam_role.role.name}"
  policy_arn = "arn:aws:iam::${var.accountid}:policy/${var.baseaccountrole}"
}

resource "aws_iam_service_linked_role" "es" {
  aws_service_name = "es.amazonaws.com"
}

resource "aws_iam_service_linked_role" "ecs" {
  aws_service_name = "ecs.amazonaws.com"
}
