from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
import boto3


def get_ecs_client(aws_auth_cred):
    """
    Returns the client object for AWS ECS

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: AWS ECS Object
    """
    return prepare_aws_client_with_given_cred("ecs", aws_auth_cred)


def deregister_task_definition(task_definition, aws_auth_cred):
    """
    Deregister all revisions of a given task definition from ECS

    Args:
        task_definition (str): Task definition name
        aws_auth (dict): Dict containing AWS credentials
    """
    client = get_ecs_client(aws_auth_cred)
    # We need to get the list of all Active revisions of the given task definition
    # So we cannot use describe_task_definition which return the latest one only
    tasks_definitions = client.list_task_definitions(familyPrefix=task_definition)

    for task_def in tasks_definitions['taskDefinitionArns']:
        client.deregister_task_definition(taskDefinition=task_def)


def check_ecs_cluster_exists(cluster, aws_auth_cred):
    """
    Check wheter the given ECS cluster already exists in AWS account

    Args:
        cluster (str): Repository name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_ecs_client(aws_auth_cred)
    try:
        response = client.describe_clusters(Names=[cluster])
        return True if len(response['clusters']) else False
    except:
        return False


def check_ecs_task_definition_exists(task_definition, aws_auth_cred):
    """
    Check wheter the given ECS Task definition already exists in AWS account

    Args:
        task_definition (str): Task Definition Name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_ecs_client(aws_auth_cred)
    try:
        response = client.describe_task_definition(taskDefinition=task_definition)
        return True if response['taskDefinition'] else False
    except:
        return False


def check_ecs_service_exists(service_name, cluster, aws_auth_cred):
    """
    Check wheter the given ECS CLuster service already exists in AWS account

    Args:
        service_name (str): ECS CLuster service name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    client = get_ecs_client(aws_auth_cred)
    try:
        response = client.describe_services(services=[service_name], cluster=cluster)
        return True if len(response['services']) else False
    except:
        return False


def get_all_task_arns(cluster, aws_auth_cred):
    """
    Get all task arns in a given cluster

    Args:
        cluster (str): Cluster name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        taskArns (list): List of all task arns
    """
    client = get_ecs_client(aws_auth_cred)

    try:
        response = client.list_tasks(cluster=cluster)
    except Exception as e:
        return []

    return response['taskArns']


def stop_all_tasks_in_a_cluster(cluster, aws_auth_cred):
    """
    Terminate all tasks in a given cluster

    Args:
        cluster (str): Cluster name
        aws_auth (dict): Dict containing AWS credentials
    """
    task_arns = get_all_task_arns(cluster, aws_auth_cred)

    client = get_ecs_client(aws_auth_cred)
    for task_arn in task_arns:
        client.stop_task(task=task_arn, cluster=cluster)


def delete_cluster(cluster, aws_auth_cred):
    """
    Delete a cluster from AWS ECS

    Args:
        cluster (str): Cluster name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        boolean: True if cluster get deleted else False
    """
    client = get_ecs_client(aws_auth_cred)

    try:
        client.delete_cluster(cluster=cluster)
        return True
    except Exception as e:
        return False


def delete_container_instances(cluster, aws_auth_cred):
    """
    Delete all contianer instances(Ec2) from a cluster

    Args:
        cluster (str): Cluster name
        aws_auth (dict): Dict containing AWS credentials
    """
    client = get_ecs_client(aws_auth_cred)
    container_arns = client.list_container_instances(cluster=cluster)['containerInstanceArns']
    for container_arn in container_arns:
        try:
            client.deregister_container_instance(cluster=cluster, containerInstance=container_arn, force=True)
        except:
            pass
