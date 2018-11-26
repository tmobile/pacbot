import boto3
import jsonRead


def confirm_resource_deletion(aws_access_key, aws_secret_key, region, resource):
    '''
    This method calls the deletion confirmation function for each resource item
    '''
    methodname = resource.replace('-', '_')
    try:
        getattr(DestroyResource, 'confirm_%s_deletion' % methodname)(aws_access_key, aws_secret_key, region)
    except Exception as e:
        pass  # This method may not be defined


class DestroyResource(object):
    '''
    This class holds all the classmethod to confirm resource deletion
    '''
    @classmethod
    def confirm_s3_deletion(cls, aws_access_key, aws_secret_key, region):
        '''
        This is to confirm that S3 bucket and all its objects are deleted successfully else this will delete it
        '''
        s3_client = boto3.resource('s3', region_name=region, aws_access_key_id=aws_access_key, aws_secret_access_key=aws_secret_key)
        bucket_name = jsonRead._get_s3_bucket_name() + "-" + jsonRead._get_base_accountid()

        if s3_client.Bucket(bucket_name).creation_date:
            print("-- Deleting S3 Bucket and its objects forcefully as Terraform failed to delete it...")
            bucket = s3_client.Bucket(bucket_name)
            bucket.objects.all().delete()
            bucket.delete()
            print("  -- Deleted S3 Bucket and all its objects")

    @classmethod
    def confirm_oss_api_deletion(cls, aws_access_key, aws_secret_key, region):
        '''
        Delete ECR repository from AWS for API
        '''
        repository = jsonRead._get_api_repo()
        cls.__delete_ecr_repository(aws_access_key, aws_secret_key, region, repository)

        repository = jsonRead._get_ui_repo()
        cls.__delete_ecr_repository(aws_access_key, aws_secret_key, region, repository)

    @classmethod
    def confirm_oss_ui_deletion(cls, aws_access_key, aws_secret_key, region):
        '''
        Delete ECR repository from AWS for UI
        '''
        repository = jsonRead._get_ui_repo()
        cls.__delete_ecr_repository(aws_access_key, aws_secret_key, region, repository)

    @classmethod
    def confirm_batch_deletion(cls, aws_access_key, aws_secret_key, region):
        '''
        Delete ECR repository from AWS for Batch
        '''
        repository = jsonRead._get_batch_repo()
        cls.__delete_ecr_repository(aws_access_key, aws_secret_key, region, repository)
        cls.__deregister_batch_job_definition(aws_access_key, aws_secret_key, region)
        cls.__deregister_task_definition(aws_access_key, aws_secret_key, region)

    @classmethod
    def __deregister_batch_job_definition(cls, aws_access_key, aws_secret_key, region):
        '''
        De reistering all the Job definitions associated with Rule egine
        '''
        batch_client = boto3.client('batch', aws_access_key_id=aws_access_key, aws_secret_access_key=aws_secret_key, region_name=region)

        job_definition_name = jsonRead._get_batch_job_def_name()
        job_definitions = batch_client.describe_job_definitions(status="ACTIVE", jobDefinitionName=job_definition_name)

        if len(job_definitions['jobDefinitions']) > 0:
            print("-- Deregistering Rule Engine job definitions...")
            for job_definition in job_definitions['jobDefinitions']:
                batch_client.deregister_job_definition(jobDefinition=job_definition_name + ":" + str(job_definition['revision']))
            print("  -- Deregistered " + len(job_definitions['jobDefinitions']) + " Rule Engine batch job definitions.")

    @classmethod
    def __deregister_task_definition(cls, aws_access_key, aws_secret_key, region):
        '''
        De reistering all the Job definitions associated with Rule egine
        '''
        client = boto3.client('ecs', aws_access_key_id=aws_access_key, aws_secret_access_key=aws_secret_key, region_name=region)
        task_def_arns = client.list_task_definitions(status='ACTIVE')['taskDefinitionArns']

        search_task_def_name = ':task-definition/' + jsonRead._get_batch_job_def_name() + ':'

        for task_def_arn in task_def_arns:
            if search_task_def_name in task_def_arn:
                client.deregister_task_definition(taskDefinition=task_def_arn)
                print("  -- Deregistered " + jsonRead._get_batch_job_def_name() + " Task Definition.")

    @classmethod
    def __delete_ecr_repository(cls, aws_access_key, aws_secret_key, region, repository):
        ecr_client = boto3.client('ecr', aws_access_key_id=aws_access_key, aws_secret_access_key=aws_secret_key, region_name=region)
        print("--Deleting repository " + repository)

        try:
            response = ecr_client.delete_repository(repositoryName=repository, force=True)
            print("  -- Deleted repository: " + repository)
        except Exception as e:
            print("  -- skipping the deletion as the repository, " + repository + ", is not found.")

    @classmethod
    def confirm_baserole_deletion(cls, aws_access_key, aws_secret_key, region):
        '''
        Delete ECR repository from AWS for Batch
        '''
        iam_client = boto3.client('iam', aws_access_key_id=aws_access_key, aws_secret_access_key=aws_secret_key, region_name=region)
        policyarn = "arn:aws:iam::" + jsonRead._get_base_accountid() + ":policy/" + jsonRead._get_base_account_role_name()
        try:
            iam_client.delete_policy(PolicyArn=policyarn)
            print("  -- Deleting IAM policy, " + jsonRead._get_base_account_role_name())
        except Exception as e:
            pass
