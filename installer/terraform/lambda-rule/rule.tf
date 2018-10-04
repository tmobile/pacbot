resource "aws_lambda_function" "pacman-SubmitRuleExecutionJob" {
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
    count="${length(var.rules)}"
    name = "${lookup(var.rules[count.index],"ruleUUID")}"
    schedule_expression = "cron(0 * * * ? *)"
    depends_on = ["aws_lambda_function.pacman-SubmitRuleExecutionJob" ]
}

data "template_file" "data_json" {
    count = "${length(var.rules)}"
    template = "${file("cloudwatch_rule.json.tpl")}"
    vars {
        ruleId = "${(lookup(var.rules[count.index], "ruleId"))}"
        ruleUUID = "${lookup(var.rules[count.index], "ruleUUID")}"
        policyId = "${lookup(var.rules[count.index], "policyId")}"
        ruleName = "${lookup(var.rules[count.index], "ruleName")}"
        targetType = "${lookup(var.rules[count.index], "targetType")}"
        assetGroup = "${lookup(var.rules[count.index], "assetGroup")}"
        alexaKeyword = "${lookup(var.rules[count.index], "alexaKeyword")}"
        ruleParams ="${lookup(var.rules[count.index], "ruleParams")}"
        ruleFrequency = "${lookup(var.rules[count.index], "ruleFrequency")}"
        ruleExecutable = "${lookup(var.rules[count.index], "ruleExecutable")}"
        ruleRestUrl = "${lookup(var.rules[count.index], "ruleRestUrl")}"
        ruleType = "${lookup(var.rules[count.index], "ruleType")}"
        ruleArn = "${lookup(var.rules[count.index], "ruleArn")}"
        status = "${lookup(var.rules[count.index], "status")}"
        userId = "${lookup(var.rules[count.index], "userId")}"
        displayName = "${lookup(var.rules[count.index], "displayName")}"
        createdDate = "${lookup(var.rules[count.index], "createdDate")}"
        modifiedDate = "${lookup(var.rules[count.index], "modifiedDate")}"
  }
}

resource "aws_cloudwatch_event_target" "target_for_cloudwatch" {
      count="${length(var.rules)}"
      target_id = "${var.targetid}"
      rule = "${lookup(var.rules[count.index],"ruleUUID")}"
      arn = "${aws_lambda_function.pacman-SubmitRuleExecutionJob.arn}"
      input = "${lookup(var.rules[count.index], "ruleParams")}"
      depends_on = [ "aws_cloudwatch_event_rule.pacman-cloudwatch-event",
                "aws_lambda_function.pacman-SubmitRuleExecutionJob"]
}


resource "aws_lambda_permission" "lambda_function_invoke" {
    statement_id   = "sid-${var.accountid}"
    action         = "lambda:InvokeFunction"
    function_name  = "${var.functionname}"
    principal      = "events.amazonaws.com"
    depends_on = [ "aws_lambda_function.pacman-SubmitRuleExecutionJob",
                "aws_cloudwatch_event_target.target_for_cloudwatch"]
}

output "pacman" {
  value ="${aws_lambda_function.pacman-SubmitRuleExecutionJob.arn}"
}
