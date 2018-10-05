resource "aws_lambda_function" "pacman-submitBatchjob" {
  function_name = "${var.functionname}"
  description   = "This resource is created as part of PacMan installation. Do not delete - It may break application"
  role          = "arn:aws:iam::${var.accountid}:role/${var.lambda_role}"
  handler       = "${var.handler_name}"
  runtime       = "${var.runtime_name}"
  s3_bucket     = "${var.s3_bucket_name}-${var.accountid}"
  s3_key        = "${var.s3_file_name}"
  environment {
    variables = {
        JOB_QUEUE = "${var.job_queue_name}"
        JOB_DEFINITION = "${var.jobdef_name}"
        }
    }

}

resource "aws_cloudwatch_event_rule" "pacman-cloudwatch-event" {
  name = "${var.event_name1}"
  depends_on = [
    "aws_lambda_function.pacman-submitBatchjob"
  ]
  schedule_expression = "cron(0 * * * ? *)"
}

resource "aws_cloudwatch_event_target" "target_for_cloudwatch" {
  target_id = "${var.targetid}"
  rule = "${aws_cloudwatch_event_rule.pacman-cloudwatch-event.name}"
  arn = "${aws_lambda_function.pacman-submitBatchjob.arn}"
  input = <<EOF
  {
  "jobName": "AWS-Data-Collector",
  "jobUuid": "pacman-aws-inventory-jar-with-dependencies",
  "environmentVariables": [
    {
      "name": "REDSHIFT_INFO",
      "value": "${var.redshift_info}"
    },
    {
      "name": "REDSHIFT_URL",
      "value": "${var.redshift_url}"
    }
  ],
  "params": [
    {
      "encrypt": false,
      "value": "com.tmobile.cso.pacman",
      "key": "package_hint"
    },
    {
      "encrypt": false,
      "value": "${var.accountid},${var.client_accountid}",
      "key": "accountinfo"
    },
    {
      "encrypt": false,
      "value": "${var.accountid}",
      "key": "base-account"
    },
    {
      "encrypt": false,
      "value": "${var.baseaccountrole}",
      "key": "discovery-role"
    },
    {
      "encrypt": false,
      "value": "${var.s3_bucket_name}-${var.accountid}",
      "key": "s3"
    },
    {
      "encrypt": false,
      "value": "${var.inventory}",
      "key": "s3-data"
    },
    {
      "encrypt": false,
      "value": "${var.backup}",
      "key": "s3-processed"
    },
    {
      "encrypt": false,
      "value": "${var.baseaccountrole}",
      "key": "s3-role"
    },
    {
      "encrypt": false,
      "value": "${var.region}",
      "key": "s3-region"
    },
    {
      "encrypt": false,
      "value": "/home/ec2-user/data",
      "key": "file-path"
    },
    {
      "encrypt": false,
      "value": "${var.region}",
      "key": "base-region"
    }
  ],
  "jobType": "jar",
  "jobDesc": "AWS-Data-Collection"
}
EOF
}

resource "aws_cloudwatch_event_rule" "pacman-redshfit-event" {
  name = "${var.event_name2}"
  depends_on = [
    "aws_lambda_function.pacman-submitBatchjob"
  ]
  schedule_expression = "cron(0 * * * ? *)"
}

resource "aws_cloudwatch_event_target" "pacman_redshift_for_cloudwatch" {
  target_id = "${var.targetid}"
  rule = "${aws_cloudwatch_event_rule.pacman-redshfit-event.name}"
  arn = "${aws_lambda_function.pacman-submitBatchjob.arn}"
  input = <<EOF
  {
  "jobName": "aws-redshift-es-data-shipper",
  "jobUuid": "data-shipper-jar-with-dependencies",
  "environmentVariables": [
    {
      "name": "ES_HOST",
      "value": "${var.es_url}"
    },
    {
      "name": "RDS_DB_URL",
      "value": "${var.rds_url}"
    },
    {
      "name": "REDSHIFT_DB_URL",
      "value": "${var.redshift_url}"
    },
    {
      "name": "ES_PORT",
      "value": "80"
    },
    {
      "name": "TYPE_CNFG_SVC_URL",
      "value": ""
    },
    {
      "name": "ASSET_API_URL",
      "value": "${var.alb_url}/api/asset/v1"
    },
    {
      "name": "CMPL_API_URL",
      "value": "${var.alb_url}/api/compliance/v1"
    },
    {
      "name": "STAT_API_URL",
      "value": "${var.alb_url}/api/statistics/v1"
    },
    {
      "name": "AUTH_API_URL",
      "value": "${var.alb_url}/api/auth"
    }
  ],
  "params": [
    {
      "encrypt": false,
      "value": "com.tmobile",
      "key": "package_hint"
    },
    {
      "encrypt": false,
      "value": "aws",
      "key": "datasource"
    },
    {
      "encrypt": false,
      "value": "${var.redshift_info}",
      "key": "redshiftinfo"
    },
    {
      "encrypt": false,
      "value": "${var.rds_info}",
      "key": "rdsinfo"
    },
    {
      "encrypt": false,
      "value": "MjJlMTQ5MjItODdkNy00ZWU0LWE0NzAtZGEwYmIxMGQ0NWQzOmNzcldwYzVwN0pGRjR2RVpCa3dHQ0FoNjdrR1FHd1h2NDZxdWc3djVad3RLZw==",
      "key": "apiauthinfo"
    }
  ],
  "jobType": "jar",
  "jobDesc": "Ship aws data periodically from redshfit to ES"
}
EOF
}

resource "aws_lambda_permission" "lambda_invoke1" {
  statement_id   = "AllowExecutionFromCloudWatch1"
  action         = "lambda:InvokeFunction"
  function_name  = "${var.functionname}"
  principal      = "events.amazonaws.com"
  source_arn     = "arn:aws:events:${var.region}:${var.accountid}:rule/${aws_cloudwatch_event_rule.pacman-cloudwatch-event.name}"
  depends_on = ["aws_lambda_function.pacman-submitBatchjob"  ]
}

resource "aws_lambda_permission" "lambda_invoke2" {
  statement_id   = "AllowExecutionFromCloudWatch2"
  action         = "lambda:InvokeFunction"
  function_name  = "${var.functionname}"
  principal      = "events.amazonaws.com"
  source_arn     = "arn:aws:events:${var.region}:${var.accountid}:rule/${aws_cloudwatch_event_rule.pacman-redshfit-event.name}"
  depends_on = [ "aws_lambda_function.pacman-submitBatchjob" ]
}
output "pacman" {
  value ="${aws_lambda_function.pacman-submitBatchjob.arn}"
}
