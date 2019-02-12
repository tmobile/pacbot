import boto3


def get_batch_client(access_key, secret_key, region):
    return boto3.client(
        'batch',
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def get_compute_environments(compute_envs, access_key, secret_key, region):
    client = get_batch_client(access_key, secret_key, region)

    response = client.describe_compute_environments(
        computeEnvironments=compute_envs
    )

    return response['computeEnvironments']


def check_compute_env_exists(compute_env, access_key, secret_key, region):
    if len(get_compute_environments([compute_env], access_key, secret_key, region)):
        return True
    else:
        return False


def get_job_definitions(job_def_name, access_key, secret_key, region):
    response = client.describe_job_definitions(
        jobDefinitionName=job_def_name,
        status='ACTIVE'
    )

    return response['jobDefinitions']


def check_job_definition_exists(job_def_name, access_key, secret_key, region):
    client = get_batch_client(access_key, secret_key, region)
    try:
        job_definitions = get_job_definitions(job_def_name, access_key, secret_key, region)
        return True if len(job_definitions) else False
    except:
        return False


def check_job_queue_exists(job_queue_name, access_key, secret_key, region):
    client = get_batch_client(access_key, secret_key, region)
    try:
        response = client.describe_job_queues(
            jobQueues=[job_queue_name],
        )
        return True if len(response['jobQueues']) else False
    except:
        return False
