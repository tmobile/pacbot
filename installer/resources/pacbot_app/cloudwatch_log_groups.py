from core.terraform.resources.aws.cloudwatch import CloudWatchLogGroupResource


class ApiCloudWatchLogGroup(CloudWatchLogGroupResource):
    name = "apis"
    retention_in_days = 7


class UiCloudWatchLogGroup(CloudWatchLogGroupResource):
    name = "ui"
    retention_in_days = 7
