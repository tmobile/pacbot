from core.terraform.resources.aws import iam
from core.config import Settings
from resources.vpc.security_group import InfraSecurityGroupResource


class BatchRolePolicyDocument(iam.IAMPolicyDocumentData):
    statement = [
        {
            'actions': ["sts:AssumeRole"],
            'principals': [
                {
                    'type': "Service",
                    'identifiers': ["batch.amazonaws.com"]
                }
            ]
        }
    ]


class BatchRole(iam.IAMRoleResource):
    name = "run_batch"
    assume_role_policy = BatchRolePolicyDocument.get_output_attr('json')
    force_detach_policies = True


class BatchReadOnlyAccessPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = BatchRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/ReadOnlyAccess"


class BatchIAMRolePolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = BatchRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/service-role/AWSBatchServiceRole"


class BatchAWSSupportPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = BatchRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/AWSSupportAccess"
