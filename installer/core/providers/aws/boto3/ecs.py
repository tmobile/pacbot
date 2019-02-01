import boto3


def get_ecs_client(access_key, secret_key, region):
    return boto3.client(
        "ecs",
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def delete_task_definition(access_key, secret_key, region, task_definition):
    client = get_ecs_client(access_key, secret_key, region)
    # We need to get the list of all Active revisions of the given task definition
    # So we cannot use describe_task_definition which return the latest one only
    tasks_definitions = client.list_task_definitions(familyPrefix=task_definition)

    for task_def in tasks_definitions['taskDefinitionArns']:
        client.deregister_task_definition(taskDefinition=task_def)


def check_ecs_cluster_exists(cluster_name, access_key, secret_key, region):
    client = get_ecs_client(access_key, secret_key, region)
    try:
        response = client.describe_clusters(Names=[cluster_name])
        return True if len(response['clusters']) else False
    except:
        return False


def check_ecs_task_definition_exists(task_definition, access_key, secret_key, region):
    client = get_ecs_client(access_key, secret_key, region)
    try:
        response = client.describe_task_definition(taskDefinition=task_definition)
        return True if response['taskDefinition'] else False
    except:
        return False


def check_ecs_service_exists(service_name, cluster_name, access_key, secret_key, region):
    client = get_ecs_client(access_key, secret_key, region)
    try:
        response = client.describe_services(services=[service_name], cluster=cluster_name)
        return True if len(response['services']) else False
    except:
        return False


def get_all_task_arns(cluster_name, access_key, secret_key, region):
    client = get_ecs_client(access_key, secret_key, region)

    try:
        response = client.list_tasks(cluster=cluster_name)
    except:
        return []

    return response['taskArns']


def stop_all_services_in_a_cluster(cluster_name, access_key, secret_key, region):
    task_arns = get_all_task_arns(cluster_name, access_key, secret_key, region)

    client = get_ecs_client(access_key, secret_key, region)
    for task_arn in task_arns:
        client.stop_task(task=task_arn, cluster=cluster_name)
