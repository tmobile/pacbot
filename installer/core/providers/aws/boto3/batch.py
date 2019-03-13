import boto3


def get_batch_client(access_key, secret_key, region):
    """
    Returns the client object for AWS Batch

    Args:
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        obj: AWS Batch Client Obj
    """
    return boto3.client(
        'batch',
        region_name=region,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key)


def get_compute_environments(compute_envs, access_key, secret_key, region):
    """
    Returns AWS Batch compute envs list with all details

    Args:
        compute_envs (list): List of compute env names
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        envs (list): List of all Batch compute envs with all details
    """
    client = get_batch_client(access_key, secret_key, region)

    response = client.describe_compute_environments(
        computeEnvironments=compute_envs
    )

    return response['computeEnvironments']


def check_compute_env_exists(compute_env, access_key, secret_key, region):
    """
    Check whether the given compute env name already exists in AWS account

    Args:
        compute_env (str): Compute env name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if env exists else False
    """
    if len(get_compute_environments([compute_env], access_key, secret_key, region)):
        return True
    else:
        return False


def get_job_definitions(job_def_name, access_key, secret_key, region):
    """
    Get all job definition versions with details

    Args:
        job_def_name (str): Job definiiton name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        jobDefinitions (list): List of all job definitions with details
    """
    response = client.describe_job_definitions(
        jobDefinitionName=job_def_name,
        status='ACTIVE'
    )

    return response['jobDefinitions']


def check_job_definition_exists(job_def_name, access_key, secret_key, region):
    """
    Check whether the given job definiiton exists in AWS Batch

    Args:
        job_def_name (str): Job definiiton name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if it already exists else False
    """
    client = get_batch_client(access_key, secret_key, region)
    try:
        job_definitions = get_job_definitions(job_def_name, access_key, secret_key, region)
        return True if len(job_definitions) else False
    except:
        return False


def check_job_queue_exists(job_queue_name, access_key, secret_key, region):
    """
    Check whether the given job queue exists in AWS Batch

    Args:
        job_queue_name (str): Job Queue name
        access_key (str): AWS Access Key
        secret_key (str): AWS Secret Key
        region (str): AWS Region

    Returns:
        Boolean: True if it already exists else False
    """
    client = get_batch_client(access_key, secret_key, region)
    try:
        response = client.describe_job_queues(
            jobQueues=[job_queue_name],
        )
        return True if len(response['jobQueues']) else False
    except:
        return False
