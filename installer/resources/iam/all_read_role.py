from core.terraform.resources.aws import iam
from resources.iam.lambda_role import LambdaRole


class LambdaPolicyDocument(iam.IAMPolicyDocumentData):
    statement = [
        {
            'actions': ["sts:AssumeRole"],
            'principals': [
                {
                    'type': "Service",
                    'identifiers': ["lambda.amazonaws.com"]
                }
            ],
            'effect': "Allow"
        }
    ]


class AllReadRole(iam.IAMRoleResource):
    name = ""  # Empty string will take prefix as the name
    assume_role_policy = LambdaPolicyDocument.get_output_attr('json')
    force_detach_policies = True


class AllReadOnlyAccessPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = AllReadRole.get_output_attr('name')
    policy_arn = "arn:aws:iam::aws:policy/ReadOnlyAccess"


class AllReadRolePolicyDocument(iam.IAMPolicyDocumentData):
    statement = [
        {
            'actions': ["sts:AssumeRole"],
            'resources': [AllReadRole.get_output_attr('arn')]
        }
    ]


class AllReadRolePolicy(iam.IAMRolePolicyResource):
    name = ""
    path = '/'
    policy = AllReadRolePolicyDocument.get_output_attr('json')


class AllReadRoleLambdaPolicyAttach(iam.IAMRolePolicyAttachmentResource):
    role = LambdaRole.get_output_attr('name')
    policy_arn = AllReadRolePolicy.get_output_attr('arn')
