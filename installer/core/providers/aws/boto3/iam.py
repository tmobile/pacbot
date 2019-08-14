from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
from core.providers.aws.boto3 import prepare_aws_resource_with_given_cred
import boto3


def get_iam_client(aws_auth_cred):
    """
    Returns the client object for AWS IAM

    Args:
        aws_auth_cred (dict): AWS Auth details with region information

    Returns:
        obj: AWS IAM Object
    """
    return prepare_aws_client_with_given_cred('iam', aws_auth_cred)


def get_iam_resource(aws_auth_cred):
    """
    Returns the Resource client object for AWS IAM

    Args:
        aws_auth_cred (dict): AWS Auth details with region information

    Returns:
        obj: AWS IAM Resource Object
    """
    return prepare_aws_resource_with_given_cred('iam', aws_auth_cred)


def get_user_name(aws_auth_cred):
    """
    Returns the username of the given user credentails

    Args:
        aws_auth_cred (dict): AWS Auth details with region information

    Returns:
        user_name (str): AWS IAM User name
    """
    iam = get_iam_resource(aws_auth_cred)
    user_name = iam.CurrentUser().user_name

    return user_name


def get_current_user(aws_auth_cred):
    """
    Returns the user detials of the given user credentails

    Args:
        aws_auth_cred (dict): AWS Auth details with region information

    Returns:
        user (obj): AWS IAM User
    """
    iam = get_iam_resource(aws_auth_cred)
    return iam.CurrentUser()


def get_aws_account_user(aws_auth_cred):
    """
    Returns the user details of the current user

    Args:
        aws_auth_cred (dict): AWS Auth details with region information

    Returns:
        obj: AWS IAM User
    """
    return get_iam_resource.CurrentUser()


def get_iam_user_policy_names(user_name, aws_auth_cred):
    """
    Returns the policy names of the current user has

    Args:
        aws_auth_cred (dict): AWS Auth details with region information
        user_name (str): AWS user name

    Returns:
        policy_names (list): List of policy names the current user has
    """
    iam_client = get_iam_client(aws_auth_cred)
    attached_policies = iam_client.list_attached_user_policies(UserName=user_name)['AttachedPolicies']
    attached_policy_names = [policy['PolicyName'] for policy in attached_policies]
    user_policy_names = iam_client.list_user_policies(UserName=user_name)['PolicyNames']

    return attached_policy_names + user_policy_names


def get_group_managed_policy_names(iam_client, groups):
    """
    Returns the group managed policy names of the current user

    Args:
        iam_client (obj): IAM client obj
        groups (list): User groups

    Returns:
        policy_names (list): List of group managed policy names the current user has
    """
    policy_names = []
    for group in groups:
        attached_policies = iam_client.list_attached_group_policies(GroupName=group['GroupName'])['AttachedPolicies']
        policy_names += [policy['PolicyName'] for policy in attached_policies]

    return policy_names


def get_group_policy_names(iam_client, groups):
    """
    Returns the group policy names of the current user

    Args:
        iam_client (obj): IAM client obj
        groups (list): User groups

    Returns:
        policy_names (list): List of group policy names the current user has
    """
    policy_names = []
    for group in groups:
        group_policy_names = iam_client.list_group_policies(GroupName=group['GroupName'])['PolicyNames']
        policy_names += group_policy_names

    return policy_names


def get_user_group_policy_names(user_name, aws_auth_cred):
    """
    Returns all group user policies of a user

    Args:
        aws_auth_cred (dict): AWS Auth details with region information
        user_name (str): AWS user name

    Returns:
        policy_names (list): List of  all goup policy names the current user has
    """
    iam_client = get_iam_client(aws_auth_cred)
    groups = iam_client.list_groups_for_user(UserName=user_name)['Groups']
    group_managed_policy_names = get_group_managed_policy_names(iam_client, groups)
    group_policy_names = get_group_policy_names(iam_client, groups)

    return group_managed_policy_names + group_policy_names


def get_all_policy_names(aws_auth_cred):
    """
    Returns all group and user policies of a user

    Args:
        aws_auth_cred (dict): AWS Auth details with region information

    Returns:
        policy_names (list): List of  all goup policy names and user policy names the current user has
    """
    iam = get_iam_resource(aws_auth_cred)
    user_name = iam.CurrentUser().user_name

    user_policy_names = get_iam_user_policy_names(user_name, aws_auth_cred)
    user_group_policy_names = get_user_group_policy_names(user_name, aws_auth_cred)

    return user_policy_names + user_group_policy_names


def get_role_policy_names(role_name, aws_auth_cred):
    """
    Returns all policies under the given IAM role

    Args:
        aws_auth_cred (dict): AWS Auth details with region information
        role_name (str): Role name

    Returns:
        policy_names (list): List of  all goup policy names and user policy names the current user has
    """
    iam_client = get_iam_client(aws_auth_cred)
    attached_policies = iam_client.list_attached_role_policies(RoleName=role_name)['AttachedPolicies']

    return [policy['PolicyName'] for policy in attached_policies]


def create_iam_service_linked_role(service_name, desc, aws_auth_cred):
    """
    Create AWS RoleNameES service linked role

    Args:
        aws_auth_cred (dict): AWS Auth details with region information
        service_name (str): Service name
        desc (str): Descsription

    Returns:
        Set: True if created else false with error
    """
    role_name = "AWSServiceRoleForAmazonElasticsearchService"
    iam_client = get_iam_client(aws_auth_cred)
    try:
        iam_client.create_service_linked_role(
            AWSServiceName=service_name,
            Description=desc
        )
        return True, None
    except Exception as e:
        return False, str(e)


def check_role_exists(role_name, aws_auth_cred):
    """
    Check wheter the given IAM role already exists in the AWS Account

    Args:
        role_name (str): Role name
        aws_auth_cred (dict): AWS Auth details with region information

    Returns:
        Boolean: True if env exists else False
    """
    iam_client = get_iam_client(aws_auth_cred)
    try:
        role = iam_client.get_role(RoleName=role_name)
        return True if role else False
    except:
        return False


def check_policy_exists(policy_name, account_id, aws_auth_cred):
    """
    Check wheter the given IAM policy already exists in the AWS Account

    Args:
        policy_name (str): Policy name
        aws_auth_cred (dict): AWS Auth details with region information

    Returns:
        Boolean: True if env exists else False
    """
    iam_client = get_iam_client(aws_auth_cred)
    policy_arn = "arn:aws:iam::%s:policy/%s" % (str(account_id), policy_name)

    try:
        policy = iam_client.get_policy(PolicyArn=policy_arn)
        return True if policy else False
    except:
        return False


def check_instance_profile_exists(instance_profile_name, aws_auth_cred):
    """
    Check wheter the given IAM instance profile already exists in the AWS Account

    Args:
        instance_profile_name (str): Instance profile name
        aws_auth_cred (dict): AWS Auth details with region information

    Returns:
        Boolean: True if env exists else False
    """
    iam_client = get_iam_client(aws_auth_cred)
    try:
        profile = iam_client.get_instance_profile(InstanceProfileName=instance_profile_name)
        return True if profile else False
    except:
        return False
