import boto3


def get_elbv2_client(access_key, secret_key, region):
    """
    Returns the client object for AWS ELB

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        obj: AWS ELB Object
    """
    return boto3.client(
        "elbv2",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def get_alb(alb_name, access_key, secret_key, region):
    """
    Find and return loadbalancers of mentioned name

    Args:
        alb_name (str): Load balancer name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        alb (dict): Loadbalancer details
    """
    client = get_elbv2_client(access_key, secret_key, region)
    try:
        response = client.describe_load_balancers(Names=[alb_name])
        albs = response['LoadBalancers']

        return albs.pop() if len(albs) else None
    except:
        return None


def check_alb_exists(alb_name, access_key, secret_key, region):
    """
    Check whether the given ALB already exists in the AWS Account

    Args:
        alb_name (str): Load balancer name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    return True if get_alb(alb_name, access_key, secret_key, region) else False


def check_target_group_exists(tg_name, access_key, secret_key, region):
    """
    Check whether the given Target group already exists in the AWS Account

    Args:
        tg_name (str): Target group name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_elbv2_client(access_key, secret_key, region)
    try:
        response = client.describe_target_groups(Names=[tg_name])
        return True if len(response['TargetGroups']) else False
    except:
        return False


def delete_all_listeners_of_alb(alb_name, access_key, secret_key, region):
    """
    Delete all listeners and target roups of a load balancers

    Args:
        alb_name (str): Load balancer name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    alb = get_alb(alb_name, access_key, secret_key, region)

    if alb:
        client = get_elbv2_client(access_key, secret_key, region)
        listeners = client.describe_listeners(LoadBalancerArn=alb['LoadBalancerArn'])

        for listener in listeners['Listeners']:
            try:
                client.delete_listener(ListenerArn=listener['ListenerArn'])
            except:
                raise Exception("Not able to remove listener: %s" % listener['ListenerArn'])


def delete_alltarget_groups(tg_names, access_key, secret_key, region):
    client = get_elbv2_client(access_key, secret_key, region)
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
