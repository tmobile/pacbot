from core.terraform.resources.aws import iam
from resources.iam.ecs_role import ECSRole
from core.config import Settings


class PolicyDocumentForBaseRole(iam.IAMPolicyDocumentData):
    statement = [
        {
            'actions': ["sts:AssumeRole"],
            'principals': {
                'type': "Service",
                'identifiers': [
                    "batch.amazonaws.com",
                    "ecs-tasks.amazonaws.com"
                ]
            }
        },
        {
            'actions': ["sts:AssumeRole"],
            'principals': {
                'type': "AWS",
                'identifiers': [ECSRole.get_output_attr('arn')]
            }
        }
    ]


class BaseRole(iam.IAMRoleResource):
    name = "ro"
    assume_role_policy = PolicyDocumentForBaseRole.get_output_attr('json')
    force_detach_policies = True


class BaseReadOnlyAccessPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = BaseRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/ReadOnlyAccess"


class BaseGuardDutyPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = BaseRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/AmazonGuardDutyReadOnlyAccess"


class BaseAWSSupportPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = BaseRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/AWSSupportAccess"


class ECSTaskExecutionRolePolicyDocument(iam.IAMPolicyDocumentData):
    statement = [
        {
            "effect": "Allow",
            "actions": ["ecr:*"],
            "resources": [
                "arn:aws:ecr:%s:%s:repository/%s-*" % (Settings.AWS_REGION, str(Settings.AWS_ACCOUNT_ID), Settings.RESOURCE_NAME_PREFIX)
            ]
        },
        {
            "effect": "Allow",
            "actions": ["logs:*"],
            "resources": ["*"]
        }
    ]


class ECSTaskExecutionRolePolicy(iam.IAMRolePolicyResource):
    name = "ecs_task_exec"
    path = '/'
    policy = ECSTaskExecutionRolePolicyDocument.get_output_attr('json')


class BaseECSTaskExecPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = BaseRole.get_output_attr('name')
    policy_arn = ECSTaskExecutionRolePolicy.get_output_attr('arn')
