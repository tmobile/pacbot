from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
import boto3


def get_elbv2_client(aws_auth_cred):
    """
    Returns the client object for AWS ELB

    Args:
       aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: AWS ELB Object
    """
    return prepare_aws_client_with_given_cred("elbv2", aws_auth_cred)


def get_alb(alb_name, aws_auth_cred):
    """
    Find and return loadbalancers of mentioned name

    Args:
        alb_name (str): Load balancer name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        alb (dict): Loadbalancer details
    """
    client = get_elbv2_client(aws_auth_cred)
    try:
        response = client.describe_load_balancers(Names=[alb_name])
        albs = response['LoadBalancers']

        return albs.pop() if len(albs) else None
    except:
        return None


def check_alb_exists(alb_name, aws_auth_cred):
    """
    Check whether the given ALB already exists in the AWS Account

    Args:
        alb_name (str): Load balancer name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    return True if get_alb(alb_name, aws_auth_cred) else False


def check_target_group_exists(tg_name, aws_auth_cred):
    """
    Check whether the given Target group already exists in the AWS Account

    Args:
        tg_name (str): Target group name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_elbv2_client(aws_auth_cred)
    try:
        response = client.describe_target_groups(Names=[tg_name])
        return True if len(response['TargetGroups']) else False
    except:
        return False


def delete_all_listeners_of_alb(alb_name, aws_auth_cred):
    """
    Delete all listeners and target roups of a load balancers

    Args:
        alb_name (str): Load balancer name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    alb = get_alb(alb_name, aws_auth_cred)

    if alb:
        client = get_elbv2_client(aws_auth_cred)
        listeners = client.describe_listeners(LoadBalancerArn=alb['LoadBalancerArn'])

        for listener in listeners['Listeners']:
            try:
                client.delete_listener(ListenerArn=listener['ListenerArn'])
            except:
                raise Exception("Not able to remove listener: %s" % listener['ListenerArn'])


def delete_alltarget_groups(tg_names, aws_auth_cred):
    client = get_elbv2_client(aws_auth_cred)
    try:
        target_groups = client.describe_target_groups(Names=tg_names)
        tgs = target_groups['TargetGroups']
    except:
        tgs = []

    for tg in tgs:
        try:
            client.delete_target_group(TargetGroupArn=tg['TargetGroupArn'])
        except:
            raise Exception("Not able to remove listener: %s" % tg['TargetGroupArn'])
