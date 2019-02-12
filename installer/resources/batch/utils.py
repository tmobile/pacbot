from core.providers.aws.boto3 import batch
from core.providers.aws.boto3 import ecs
from core.config import Settings


def remove_batch_job_related_resources(compute_env_name, job_definition_name):
    deregister_ecs_task_definition_of_batch_job(job_definition_name)
    ecs_cluster = get_ecs_cluster_from_compute_env(compute_env_name)
    if ecs_cluster:
        ecs.stop_all_tasks_in_a_cluster(
            ecs_cluster,
            Settings.AWS_ACCESS_KEY,
            Settings.AWS_SECRET_KEY,
            Settings.AWS_REGION)

        ecs.delete_container_instances(
            ecs_cluster,
            Settings.AWS_ACCESS_KEY,
            Settings.AWS_SECRET_KEY,
            Settings.AWS_REGION)

        ecs.delete_cluster(
            ecs_cluster,
            Settings.AWS_ACCESS_KEY,
            Settings.AWS_SECRET_KEY,
            Settings.AWS_REGION)


def deregister_ecs_task_definition_of_batch_job(task_definition_name):
    ecs.deregister_task_definition(
        Settings.AWS_ACCESS_KEY,
        Settings.AWS_SECRET_KEY,
        Settings.AWS_REGION,
        task_definition_name
    )


def get_ecs_cluster_from_compute_env(compute_env_name):
    response = batch.get_compute_environments(
        [compute_env_name],
        Settings.AWS_ACCESS_KEY,
        Settings.AWS_SECRET_KEY,
        Settings.AWS_REGION
        )

    if response:
        return response[0]['ecsClusterArn']

    return None
