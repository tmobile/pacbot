from core.providers.aws.boto3 import prepare_aws_client_with_given_cred
import boto3


def get_batch_client(aws_auth_cred):
    """
    Returns the client object for AWS Batch

    Args:
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        obj: AWS Batch Client Obj
    """
    return prepare_aws_client_with_given_cred('batch', aws_auth_cred)


def get_compute_environments(compute_envs, aws_auth_cred):
    """
    Returns AWS Batch compute envs list with all details

    Args:
        compute_envs (list): List of compute env names
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        envs (list): List of all Batch compute envs with all details
    """
    client = get_batch_client(aws_auth_cred)

    response = client.describe_compute_environments(
        computeEnvironments=compute_envs
    )

    return response['computeEnvironments']


def check_compute_env_exists(compute_env, aws_auth_cred):
    """
    Check whether the given compute env name already exists in AWS account

    Args:
        compute_env (str): Compute env name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if env exists else False
    """
    if len(get_compute_environments([compute_env], aws_auth_cred)):
        return True
    else:
        return False


def get_job_definitions(job_def_name, aws_auth_cred):
    """
    Get all job definition versions with details

    Args:
        job_def_name (str): Job definiiton name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        jobDefinitions (list): List of all job definitions with details
    """
    response = client.describe_job_definitions(
        jobDefinitionName=job_def_name,
        status='ACTIVE'
    )

    return response['jobDefinitions']


def check_job_definition_exists(job_def_name, aws_auth_cred):
    """
    Check whether the given job definiiton exists in AWS Batch

    Args:
        job_def_name (str): Job definiiton name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if it already exists else False
    """
    client = get_batch_client(aws_auth_cred)
    try:
        job_definitions = get_job_definitions(job_def_name, aws_auth_cred)
        return True if len(job_definitions) else False
    except:
        return False


def check_job_queue_exists(job_queue_name, aws_auth_cred):
    """
    Check whether the given job queue exists in AWS Batch

    Args:
        job_queue_name (str): Job Queue name
        aws_auth (dict): Dict containing AWS credentials

    Returns:
        Boolean: True if it already exists else False
    """
    client = get_batch_client(aws_auth_cred)
    try:
        response = client.describe_job_queues(
            jobQueues=[job_queue_name],
        )
        return True if len(response['jobQueues']) else False
    except:
        return False
