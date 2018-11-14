resource "aws_iam_role" "pac_run_batch" {
    name = "${var.pacrunbatch}"
    assume_role_policy="${var.check ? data.aws_iam_policy_document.batchpolicy.json : data.aws_iam_policy_document.batchassumepolicy.json}"
    description = "DO-NOT-DELETE-This resource is created as part of PacBot installation"
}
data "aws_iam_policy_document" "batchpolicy" {
    statement
     {
        actions = ["sts:AssumeRole"]
        principals {
            type="Service",
            identifiers = [
                "batch.amazonaws.com"
            ]
        }
        effect = "Allow"
    }
}
data "aws_iam_policy_document" "batchassumepolicy" {
    statement
     {
        actions = ["sts:AssumeRole"]
        principals {
            type="Service",
            identifiers = [
                "batch.amazonaws.com"
            ]
        }
      }
}

resource "aws_iam_role_policy_attachment" "pacbatch-attach1" {
    role       = "${aws_iam_role.pac_run_batch.name}"
    policy_arn = "arn:aws:iam::aws:policy/ReadOnlyAccess"
}

resource "aws_iam_role_policy_attachment" "pacbatch-attach2" {
    role       = "${aws_iam_role.pac_run_batch.name}"
    policy_arn = "arn:aws:iam::aws:policy/service-role/AWSBatchServiceRole"
}
resource "aws_iam_role_policy_attachment" "pacbatch-attach3" {
    role       = "${aws_iam_role.pac_run_batch.name}"
    policy_arn = "arn:aws:iam::aws:policy/AWSSupportAccess"
}

resource "aws_iam_role_policy_attachment" "pacattach-1" {
  role      = "${aws_iam_role.pac_run_batch.name}"
  policy_arn = "arn:aws:iam::${var.accountid}:policy/${var.baseaccountrole}"
}
