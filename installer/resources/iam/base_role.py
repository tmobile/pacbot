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


class BaseS3FullAccessPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = BaseRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"


class BaseECSTaskExecPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = BaseRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
