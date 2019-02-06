import boto3


def get_iam_client(access_key, secret_key):
    return boto3.client(
        'iam',
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def get_iam_resource(access_key, secret_key):
    return boto3.resource(
        'iam',
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def get_user_name(access_key, secret_key):
    iam = get_iam_resource(access_key, secret_key)
    user_name = iam.CurrentUser().user_name

    return user_name


def get_aws_account_user(access_key, secret_key):
    return get_iam_resource.CurrentUser(access_key, secret_key)


def get_iam_user_policy_names(access_key, secret_key, user_name):
    iam_client = get_iam_client(access_key, secret_key)
    attached_policies = iam_client.list_attached_user_policies(UserName=user_name)['AttachedPolicies']
    attached_policy_names = [policy['PolicyName'] for policy in attached_policies]
    user_policy_names = iam_client.list_user_policies(UserName=user_name)['PolicyNames']

    return attached_policy_names + user_policy_names


def get_group_managed_policy_names(iam_client, groups):
    policy_names = []
    for group in groups:
        attached_policies = iam_client.list_attached_group_policies(GroupName=group['GroupName'])['AttachedPolicies']
        policy_names += [policy['PolicyName'] for policy in attached_policies]

    return policy_names


def get_group_policy_names(iam_client, groups):
    policy_names = []
    for group in groups:
        group_policy_names = iam_client.list_group_policies(GroupName=group['GroupName'])['PolicyNames']
        policy_names += group_policy_names

    return policy_names


def get_user_group_policy_names(access_key, secret_key, user_name):
    iam_client = get_iam_client(access_key, secret_key)
    groups = iam_client.list_groups_for_user(UserName=user_name)['Groups']
    group_managed_policy_names = get_group_managed_policy_names(iam_client, groups)
    group_policy_names = get_group_policy_names(iam_client, groups)

    return group_managed_policy_names + group_policy_names


def get_all_policy_names(access_key, secret_key):
    iam = get_iam_resource(access_key, secret_key)
    user_name = iam.CurrentUser().user_name

    user_policy_names = get_iam_user_policy_names(access_key, secret_key, user_name)
    user_group_policy_names = get_user_group_policy_names(access_key, secret_key, user_name)

    return user_policy_names + user_group_policy_names


def create_iam_service_linked_role(access_key, secret_key, service_name, desc):
    role_name = "AWSServiceRoleForAmazonElasticsearchService"
    iam_client = get_iam_client(access_key, secret_key)
    try:
        iam_client.create_service_linked_role(
            AWSServiceName=service_name,
            Description=desc
        )
        return True, None
    except Exception as e:
        return False, str(e)


def check_role_exists(role_name, access_key, secret_key):
    iam_client = get_iam_client(access_key, secret_key)
    try:
        role = iam_client.get_role(RoleName=role_name)
        return True if role else False
    except:
        return False


def check_policy_exists(policy_name, access_key, secret_key, account_id):
    iam_client = get_iam_client(access_key, secret_key)
    policy_arn = "arn:aws:iam::%s:policy/%s" % (str(account_id), policy_name)

    try:
        policy = iam_client.get_policy(PolicyArn=policy_arn)
        return True if policy else False
    except:
        return False


def check_instance_profile_exists(instance_profile_name, access_key, secret_key):
    iam_client = get_iam_client(access_key, secret_key)
    try:
        profile = iam_client.get_instance_profile(InstanceProfileName=instance_profile_name)
        return True if profile else False
    except:
        return False
