import boto3


def get_ecs_client(access_key, secret_key, region):
    """
    Returns the client object for AWS ECS

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        obj: AWS ECS Object
    """
    return boto3.client(
        "ecs",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def deregister_task_definition(access_key, secret_key, region, task_definition):
    """
    Deregister all revisions of a given task definition from ECS

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region
        task_definition (str): Task definition name
    """
    client = get_ecs_client(access_key, secret_key, region)
    # We need to get the list of all Active revisions of the given task definition
    # So we cannot use describe_task_definition which return the latest one only
    tasks_definitions = client.list_task_definitions(familyPrefix=task_definition)

    for task_def in tasks_definitions['taskDefinitionArns']:
        client.deregister_task_definition(taskDefinition=task_def)


def check_ecs_cluster_exists(cluster, access_key, secret_key, region):
    """
    Check wheter the given ECS cluster already exists in AWS account

    Args:
        cluster (str): Repository name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_ecs_client(access_key, secret_key, region)
    try:
        response = client.describe_clusters(Names=[cluster])
        return True if len(response['clusters']) else False
    except:
        return False


def check_ecs_task_definition_exists(task_definition, access_key, secret_key, region):
    """
    Check wheter the given ECS Task definition already exists in AWS account

    Args:
        task_definition (str): Task Definition Name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_ecs_client(access_key, secret_key, region)
    try:
        response = client.describe_task_definition(taskDefinition=task_definition)
        return True if response['taskDefinition'] else False
    except:
        return False


def check_ecs_service_exists(service_name, cluster, access_key, secret_key, region):
    """
    Check wheter the given ECS CLuster service already exists in AWS account

    Args:
        service_name (str): ECS CLuster service name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    client = get_ecs_client(access_key, secret_key, region)
    try:
        response = client.describe_services(services=[service_name], cluster=cluster)
        return True if len(response['services']) else False
    except:
        return False


def get_all_task_arns(cluster, access_key, secret_key, region):
    """
    Get all task arns in a given cluster

    Args:
        cluster (str): Cluster name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        taskArns (list): List of all task arns
    """
    client = get_ecs_client(access_key, secret_key, region)

    try:
        response = client.list_tasks(cluster=cluster)
    except Exception as e:
        return []

    return response['taskArns']


def stop_all_tasks_in_a_cluster(cluster, access_key, secret_key, region):
    """
    Terminate all tasks in a given cluster

    Args:
        cluster (str): Cluster name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region
    """
    task_arns = get_all_task_arns(cluster, access_key, secret_key, region)

    client = get_ecs_client(access_key, secret_key, region)
    for task_arn in task_arns:
        client.stop_task(task=task_arn, cluster=cluster)


def delete_cluster(cluster, access_key, secret_key, region):
    """
    Delete a cluster from AWS ECS

    Args:
        cluster (str): Cluster name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        boolean: True if cluster get deleted else False
    """
    client = get_ecs_client(access_key, secret_key, region)

    try:
        client.delete_cluster(cluster=cluster)
        return True
    except Exception as e:
        return False


def delete_container_instances(cluster, access_key, secret_key, region):
    """
    Delete all contianer instances(Ec2) from a cluster

    Args:
        cluster (str): Cluster name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region
    """
    client = get_ecs_client(access_key, secret_key, region)
    container_arns = client.list_container_instances(cluster=cluster)['containerInstanceArns']
    for container_arn in container_arns:
        try:
            client.deregister_container_instance(cluster=cluster, containerInstance=container_arn, force=True)
        except:
            pass
