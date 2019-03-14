from core.terraform.resources.aws import iam
from core.config import Settings
from resources.data.aws_info import AwsAccount


class ECSRolePolicyDocument(iam.IAMPolicyDocumentData):
    statement = [
        {
            'actions': ["sts:AssumeRole"],
            'principals': {
                'type': "Service",
                'identifiers': [
                    "ec2.amazonaws.com",
                    "ecs-tasks.amazonaws.com",
                    "ssm.amazonaws.com"
                ]
            }
        },
        {
            'actions': ["sts:AssumeRole"],
            'principals': {
                'type': "AWS",
                'identifiers': [
                    "arn:aws:iam::" + AwsAccount.get_output_attr('account_id') + ":root"
                ]
            },
            'condition': {
                'test': "Bool",
                'variable': "aws:MultiFactorAuthPresent",
                'values': ["false"]
            }
        }
    ]


class ECSRole(iam.IAMRoleResource):
    name = "ecs_role"
    assume_role_policy = ECSRolePolicyDocument.get_output_attr('json')
    force_detach_policies = True


class ECSRoleInstanceProfile(iam.IAMInstanceProfileResource):
    name = ECSRole.name + '_profile'
    role = ECSRole.get_output_attr('name')


class ECSReadOnlyAccessPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = ECSRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/ReadOnlyAccess"


class ECSAWSSupportPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = ECSRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/AWSSupportAccess"


class ECSContainerServiceForEC2PolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = ECSRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"


class ECSGuardDutyReadOnlyPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = ECSRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/AmazonGuardDutyReadOnlyAccess"


class ECSCloudWatchLogsFullAccessPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = ECSRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/CloudWatchLogsFullAccess"


class ECSAWSLambdaFullAccessPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = ECSRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/AWSLambdaFullAccess"


class ECSCloudWatchEventsFullAccessPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = ECSRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/CloudWatchEventsFullAccess"
