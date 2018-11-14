import boto3
import botocore
import jsonRead
import checkresources
import time
from python_terraform import *
import sys


def _check_resource(accessKey, secretKey, region, resource, terraform):
    methodname = resource.replace('-', '_')
    return getattr(checkresources, '_check_%s' % methodname)(accessKey, secretKey, region, resource, terraform)


def _check_baserole(accessKey, secretKey, region, resource, terraform):
    iam = boto3.client('iam', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    policyarn = "arn:aws:iam::" + jsonRead._get_base_accountid() + ":policy/" + jsonRead._get_base_account_role_name()
    response = ''
    response_r4 = ''
    try:
        response = iam.get_policy(PolicyArn=policyarn)
    except Exception as e:
        pass
    try:
        response_r1 = iam.get_role(RoleName=jsonRead._get_base_account_role_name())
    except iam.exceptions.NoSuchEntityException as ne1:
        pass
    try:
        response_r2 = iam.get_role(RoleName=jsonRead._get_pacecs_role_name())
    except iam.exceptions.NoSuchEntityException as ne2:
        pass
    try:
        response_r3 = iam.get_role(RoleName=jsonRead._get_lambda_role_name())
    except iam.exceptions.NoSuchEntityException as ne3:
        pass
    try:
        response_r4 = iam.get_role(RoleName=jsonRead._get_pacrunbatch_role_name())
    except iam.exceptions.NoSuchEntityException as ne4:
        pass
    if response != '' and _check_in_terraform(response['Policy']['Arn'], terraform):
        if _check_in_terraform(response_r1['Role']['Arn'], terraform):
            if _check_in_terraform(response_r2['Role']['Arn'], terraform):
                if _check_in_terraform(response_r3['Role']['Arn'], terraform):
                    if _check_in_terraform(response_r4['Role']['Arn'], terraform):
                        return True
    _detach_policy(iam, jsonRead._get_base_account_role_name(), policyarn, -1)
    _detach_policy(iam, jsonRead._get_pacecs_role_name(), policyarn, -1)
    _detach_policy(iam, jsonRead._get_lambda_role_name(), policyarn, -1)
    _detach_policy(iam, jsonRead._get_pacrunbatch_role_name(), policyarn, -1)
    try:
        iam.delete_policy(PolicyArn=policyarn)
    except Exception as e:
        pass
    _remove(resource, True)
    return False


def _detach_policy(iam, rolename, policyarn, roleindex):
    policy_list = ['AmazonS3FullAccess', 'ReadOnlyAccess', 'AmazonGuardDutyReadOnlyAccess',
                   'service-role/AmazonECSTaskExecutionRolePolicy', 'AWSSupportAccess',
                   'CloudWatchLogsFullAccess', 'AmazonS3ReadOnlyAccess', 'CloudWatchLogsFullAccess',
                   'service-role/AWSBatchServiceRole', 'service-role/AmazonEC2ContainerServiceforEC2Role',
                   'CloudWatchFullAccess', 'AWSLambdaFullAccess', 'AWSBatchFullAccess']
    try:
        while roleindex < len(policy_list) - 1:
            roleindex += 1
            iam.detach_role_policy(RoleName=rolename, PolicyArn='arn:aws:iam::aws:policy/' + policy_list[roleindex])
    except iam.exceptions.DeleteConflictException as e:
        _detach_policy(iam, rolename, policyarn, roleindex)
    except iam.exceptions.NoSuchEntityException as ne:
        _detach_policy(iam, rolename, policyarn, roleindex)
    except IndexError as ie:
        pass
    try:
        iam.detach_role_policy(RoleName=rolename, PolicyArn=policyarn)
    except Exception as e:
        pass
    try:
        response = iam.list_instance_profiles_for_role(RoleName=rolename)
        if len(response['InstanceProfiles']) > 0:
            iam.remove_role_from_instance_profile(InstanceProfileName=rolename,
                                                  RoleName=rolename)
            iam.delete_instance_profile(InstanceProfileName=rolename)
    except iam.exceptions.DeleteConflictException as e:
        pass
    except Exception as e:
        pass
    try:
        iam.delete_role(RoleName=rolename)
    except iam.exceptions.NoSuchEntityException as ne1:
        pass
    except iam.exceptions.DeleteConflictException as ne2:
        pass


def _check_clientrole(accessKey, secretKey, region, resource, terraform):
    iam = boto3.client('sts', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    targetRole_Arn = 'arn:aws:iam::' + jsonRead._get_client_accountid() + ':role/' + jsonRead._get_client_assume_role()
    sts_assumerole = iam.assume_role(RoleArn=targetRole_Arn, RoleSessionName='pacbot_targetRole')
    targetConnection = boto3.Session(aws_access_key_id=sts_assumerole['Credentials']['AccessKeyId'],
                                     aws_secret_access_key=sts_assumerole['Credentials']['SecretAccessKey'],
                                     aws_session_token=sts_assumerole['Credentials']['SessionToken'])
    clientiam = targetConnection.client('iam')
    try:
        response = clientiam.get_role(RoleName=jsonRead._get_client_account_role_name())
    except Exception as e:
        return False
    if response != '' and _check_in_terraform(response['Role']['Arn'], terraform):
        return True
    policy_list = ['ReadOnlyAccess', 'AmazonGuardDutyReadOnlyAccess', 'AWSSupportAccess']
    try:
        for policy in policy_list:
            clientiam.detach_role_policy(RoleName=jsonRead._get_client_account_role_name(),
                                         PolicyArn='arn:aws:iam::aws:policy/' + policy)
        clientiam.delete_role(RoleName=jsonRead._get_client_account_role_name())
        _remove(resource, True)
    except Exception as e:
        pass
    return False


def _check_security(accessKey, secretKey, region, resource, terraform):
    group_name = "pacbot"
    client = boto3.client('ec2', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    security_groups = client.describe_security_groups(Filters=[{'Name': 'group-name', 'Values': [group_name]}])
    if len(security_groups['SecurityGroups']) > 0:
        for security_group in security_groups['SecurityGroups']:
            if security_group['GroupName'] == group_name:
                group_id = security_group['GroupId']
                jsonRead._write_json(resource, group_id)
                print("-- Skipping security group creation as it already exists.")
                return True

    ec2 = boto3.resource('ec2', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    security_group = ec2.SecurityGroup(jsonRead._get_security_id())
    return _check_in_terraform(security_group.id, terraform)


def _check_s3(accessKey, secretKey, region, resource, terraform):
    s3 = boto3.resource('s3', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    bucket_name = jsonRead._get_s3_bucket_name() + "-" + jsonRead._get_base_accountid()
    if s3.Bucket(bucket_name).creation_date:
        if _check_in_terraform(bucket_name, terraform):
            return True
        else:
            try:
                bucket = s3.Bucket(bucket_name)
                bucket.objects.all().delete()
                time.sleep(5)
                bucket.delete()
                time.sleep(10)
                _remove(resource, True)
            except Exception as e:
                _remove(resource, True)
    else:
        return False


def _check_rds(accessKey, secretKey, region, resource, terraform):
    rds = boto3.client('rds', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    rds_name = jsonRead._get_rds_identifier()
    response = ''
    try:
        try:
            response = rds.describe_db_instances(
                DBInstanceIdentifier=rds_name, Filters=[])
        except rds.exceptions.DBInstanceNotFoundFault as e:
            return False
        if response != '' and _check_in_terraform(response['DBInstances'][0]['DBInstanceArn'], terraform):
            return True
        else:
            while True:
                try:
                    response = rds.delete_db_instance(DBInstanceIdentifier=rds_name, SkipFinalSnapshot=True)
                    time.sleep(100)
                except Exception as db:
                    pass
                response = ''
                try:
                    response = rds.describe_db_instances(DBInstanceIdentifier=rds_name, Filters=[])
                except Exception as db1:
                    pass
                if response == '':
                    rds.delete_option_group(OptionGroupName=jsonRead._get_rds_option_name())
                    rds.delete_db_parameter_group(DBParameterGroupName=jsonRead._get_rds_param_name())
                    rds.delete_db_subnet_group(DBSubnetGroupName=jsonRead._get_rds_subnet_name())
                    return False
    except rds.exceptions.DBInstanceNotFoundFault as e:
        return False
    except KeyError as key:
        return False
    return False


def _check_es(accessKey, secretKey, region, resource, terraform):
    es = boto3.client('es', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    es_name = jsonRead._get_es_domain_name()
    try:
        response = es.describe_elasticsearch_domain(DomainName=es_name)
        if _check_in_terraform(response['DomainStatus']['DomainName'], terraform):
            return True
        else:
            while True:
                if not response['DomainStatus']['Deleted']:
                    es.delete_elasticsearch_domain(DomainName=es_name)
                    time.sleep(100)
                response = es.describe_elasticsearch_domain(DomainName=es_name)
    except es.exceptions.ResourceNotFoundException as re:
        _remove(resource, True)
        return False
    except KeyError as key:
        _remove(resource, True)
        return False
    except Exception as e:
        _remove(resource, True)
        return False


def _check_redshift(accessKey, secretKey, region, resource, terraform):
    redshift = boto3.client('redshift', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    redshift_name = jsonRead._get_redshift_name()
    try:
        response = redshift.describe_clusters(ClusterIdentifier=redshift_name)
        if _check_in_terraform(response['Clusters'][0]['Endpoint']['Address'], terraform) is True:
            return True
        else:
            while True:
                try:
                    response = redshift.describe_clusters(ClusterIdentifier=redshift_name)
                    if len(response['Clusters']) > 0:
                        if response['Clusters'][0]['ClusterStatus'] != 'deleting':
                            response = redshift.delete_cluster(
                                ClusterIdentifier=jsonRead._get_redshift_name(),
                                SkipFinalClusterSnapshot=True)
                            time.sleep(100)
                    else:
                        response = redshift.delete_cluster_subnet_group(
                            ClusterSubnetGroupName=jsonRead._get_subnet_name())
                        time.sleep(10)
                        response = redshift.delete_cluster_parameter_group(
                            ParameterGroupName=jsonRead._get_param_name())
                        time.sleep(10)
                        is_deleted = True
                except Exception as e:
                    _remove(resource, is_deleted)

    except redshift.exceptions.ClusterNotFoundFault as e:
        return False
    except KeyError as key:
        return False


def _check_batch(accessKey, secretKey, region, resource, terraform):
    batch = boto3.client('batch', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    response_batch = batch.describe_compute_environments(computeEnvironments=[jsonRead._get_compute_environment()])
    response_jd = batch.describe_job_definitions(jobDefinitionName=jsonRead._get_batch_job_def_name(), status='ACTIVE')
    response_jq = batch.describe_job_queues(jobQueues=jsonRead._get_job_queue_name())
    is_deleted = False
    try:
        if _check_in_terraform(response_batch['computeEnvironments'][0]['computeEnvironmentName'], terraform) is True:
            if _check_in_terraform(response_jd['jobDefinitions'][0]['jobDefinitionArn'], terraform) is True:
                if _check_in_terraform(response_jq['jobQueues'][0]['jobQueueArn'], terraform) is True:
                    if _check_in_terraform(response_jq['jobQueues'][1]['jobQueueArn'], terraform) is True:
                        return True
        else:
            while True:
                try:
                    response = batch.describe_job_queues(jobQueues=jsonRead._get_job_queue_name())
                    if len(response['jobQueues']) > 0:
                        if response != '' and response['jobQueues'][0]['state'] != 'DISABLED':
                            batch.update_job_queue(jobQueue=response['jobQueues'][0]['jobQueueName'],
                                                   state='DISABLED',)
                        elif response != '' and response['jobQueues'][0]['state'] == 'DISABLED':
                            batch.delete_job_queue(jobQueue=response['jobQueues'][0]['jobQueueName'])
                    else:
                        response = batch.describe_job_definitions(jobDefinitionName=jsonRead._get_batch_job_def_name(), status='ACTIVE')
                        if len(response['jobDefinitions']) > 0:
                            batch.deregister_job_definition(jobDefinition=response['jobDefinitions'][0]['jobDefinitionArn'])
                        else:
                            response = batch.describe_compute_environments(computeEnvironments=[jsonRead._get_compute_environment()])
                            if len(response['computeEnvironments']) > 0:
                                if response['computeEnvironments'][0]['state'] != 'DISABLED':
                                    batch.update_compute_environment(computeEnvironment=response['computeEnvironments'][0]['computeEnvironmentName'], state='DISABLED')
                                else:
                                    batch.delete_compute_environment(computeEnvironment=response['computeEnvironments'][0]['computeEnvironmentName'])
                                    is_deleted = True
                            else:
                                break
                except batch.exceptions.ClientException as e:
                    continue
    except IndexError as ie:
        _remove(resource, is_deleted)
        return False
    return False


def _check_lambda(accessKey, secretKey, region, function_name, cloudwatch_names):
    lambda_fn = boto3.client('lambda', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    try:
        response = lambda_fn.get_function(FunctionName=function_name)
    except lambda_fn.exceptions.ResourceNotFoundException as e:
        print function_name, "is unavailable"
    response = _check_cloud_watch(accessKey, secretKey, region, cloudwatch_names)


def _check_cloud_watch(accessKey, secretKey, region, resource, terraform, rules, fn_name):
    cloudwatch = boto3.client('events', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    to_be_deleted = False
    try:
        for rule in rules:
            response = cloudwatch.describe_rule(Name=rule)
            if not _check_in_terraform(response['Name'], terraform):
                to_be_deleted = True
    except Exception as e:
        return True
    if to_be_deleted:
        for rule in rules:
            try:
                cloudwatch.remove_targets(Rule=rule, Ids=[fn_name])
                cloudwatch.delete_rule(Name=rule)
            except Exception as e:
                continue
    return to_be_deleted


def _check_lambda_submit(accessKey, secretKey, region, resource, terraform):
    lambda_fn = boto3.client('lambda', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    try:
        response = lambda_fn.get_function(FunctionName=jsonRead._get_lambda_fn_name())
        to_be_deleted = False
        if response != '' and _check_in_terraform(response['Configuration']['FunctionArn'], terraform):
            if _check_cloud_watch(accessKey, secretKey, region, resource, terraform,
                                  jsonRead._get_submit_evnt_name(), jsonRead._get_lambda_fn_name()):
                to_be_deleted = True
        else:
            to_be_deleted = True
    except lambda_fn.exceptions.ResourceNotFoundException as e:
        return False
    try:
        if to_be_deleted:
            _check_cloud_watch(accessKey, secretKey, region, resource, terraform,
                               jsonRead._get_submit_evnt_name(), jsonRead._get_lambda_fn_name())
            lambda_fn.delete_function(FunctionName=jsonRead._get_lambda_fn_name())
            _remove(resource, True)
        else:
            return True
    except Exception as e:
        _remove(resource, True)
    return False


def _check_lambda_rule(accessKey, secretKey, region, resource, terraform):
    lambda_fn = boto3.client('lambda', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    rules = _get_rules()
    try:
        response = lambda_fn.get_function(FunctionName=jsonRead._get_lambda_rule_fn_name())
        to_be_deleted = False
        if response != '' and _check_in_terraform(response['Configuration']['FunctionArn'], terraform):
            if _check_cloud_watch(accessKey, secretKey, region, resource, terraform,
                                  rules, jsonRead._get_lambda_rule_fn_name()):
                to_be_deleted = True
        else:
            to_be_deleted = True
    except lambda_fn.exceptions.ResourceNotFoundException as e:
        return False
    try:
        if to_be_deleted:
            _check_cloud_watch(accessKey, secretKey, region, resource, terraform, rules,
                               jsonRead._get_lambda_rule_fn_name())
            lambda_fn.delete_function(FunctionName=jsonRead._get_lambda_rule_fn_name())
            _remove(resource, True)
        else:
            return True
    except Exception as e:
        _remove(resource, True)
    return False


def _check_loadbalancer(accessKey, secretKey, region, resource, terraform, albname):
    elb = boto3.client('elbv2', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    to_be_deleted = False
    try:
        response = elb.describe_load_balancers(Names=[albname])
        loadbalancerarn = ''
        targetgroup = []
        if response != '' and len(response['LoadBalancers']) > 0:
            loadbalancerarn = LoadBalancerArn = response['LoadBalancers'][0]['LoadBalancerArn']
            if not _check_in_terraform(response['LoadBalancers'][0]['LoadBalancerName'], terraform):
                response = elb.describe_target_groups(LoadBalancerArn=response['LoadBalancers'][0]['LoadBalancerArn'])
                for target in response['TargetGroups']:
                    targetgroup.append(target['TargetGroupArn'])
                    if not _check_in_terraform(target['TargetGroupName'], terraform):
                        to_be_deleted = True
            else:
                to_be_deleted = True
    except Exception as e:
        return False
    if to_be_deleted:
        elb.delete_load_balancer(LoadBalancerArn=loadbalancerarn)
        time.sleep(15)
        for target in targetgroup:
            elb.delete_target_group(TargetGroupArn=target)
    return to_be_deleted


def _check_oss_api(accessKey, secretKey, region, resource, terraform):
    if _check_loadbalancer(accessKey, secretKey, region, resource, terraform, jsonRead._get_alb_name()):
        logs = boto3.client('logs', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
        try:
            logs.delete_log_group(logGroupName='pacbot_oss_apis')
            return True
        except Exception as e:
            return False
    return False


def _check_oss_ui(accessKey, secretKey, region, resource, terraform):
    if _check_loadbalancer(accessKey, secretKey, region, resource, terraform, jsonRead._get_ui_alb_name()):
        logs = boto3.client('logs', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
        try:
            logs.delete_log_group(logGroupName='pacbot_oss_ui')
            return True
        except Exception as e:
            return False
    return False


def _check_s3_upload(accessKey, secretKey, region, resource, terraform):
    return False


def _check_in_terraform(awsname, terraform):
    if terraform == '':
        return False
    response = terraform.cmd('show')
    if response[0] == 0:
        if awsname in response[1]:
            return True


def _remove(resource, is_deleted):
    if is_deleted is not False:
        try:
            os.remove("./terraform/" + resource + "/terraform.tfstate")
        except OSError as ose:
            pass


def _get_rules():
    filename = "rule_engine_cloudwatch_rule.json"
    ruleuuid = []
    with open(filename, 'r') as data_file:
        rules = json.load(data_file)
    for rule in rules:
        ruleuuid.append(rule['ruleUUID'])
    return ruleuuid
