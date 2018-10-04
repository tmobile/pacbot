#! /usr/bin/env python
from __future__ import unicode_literals
import json
import os
import subprocess
import base64
import re
import shutil
import filecreator

filename='resource.json'
outputfilename='terraform_output.json'
with open(filename, 'r') as data_file:
    data = json.load(data_file)

rolename = data['role']
accesskey = data['access_key']
secretkey = data['secret_key']
region  =   data['region']
resources = data['resources']
splitter = ":"
resourcekeys = resources['IAMPolicies'].keys()
resourcevalues = resources['IAMPolicies'].values()
mergedlist = []
base_accountid=""
client_accountid=""
for resource in resourcevalues:
    for policy in resource:
        mergedlist.append(policy.encode('ascii'))

def get_keyByIndex(resourcename):
    try:
        return resourcekeys.index(resourcename)
    except ValueError as value:
        return -1

def expectedRole():
    return rolename

def getRegion():
    with open(outputfilename, 'r') as data_file:
        data = json.load(data_file)
    return data['region']
    #return region

def _get_base_account_role_name():
    return resources['IAMRoles']['baseaccountrole']

def _get_client_account_role_name():
    return resources['IAMRoles']['clientaccountrole']

def _get_pacecs_role_name():
    return resources['IAMRoles']['pacecsrole']

def _get_pacrunbatch_role_name():
    return resources['IAMRoles']['pacrunbatch']

def _get_ecstaskexecution_role_name():
    return resources['IAMRoles']['ecstaskexecution_role']

def _get_base_account_role_policy():
    return resources['baserolepolicy']

def _get_client_account_role_policy():
    return resources['clientolepolicy']

def _get_pac_ecs_role_policy():
    return resources['pacecsrolepolicy']

def _get_s3_bucket_name():
    return resources['S3Buckets']['bucketname']

def _get_folder_names():
    return resources['S3Buckets']['foldernames']

def _get_upload_file_names():
    return resources['Lambda']['uploadfilenames']

def _cluster_upload_file():
    return resources['S3Upload']['uploadfilenames']

def _get_lambda_role_name():
    return resources['IAMRoles']['lambda_role']

def _get_rds_name():
    return resources['RDSInstances']['rdsname']

def _get_rds_database():
    return resources['RDSInstances']['rdsdatabase']

def _get_rds_instance():
    return resources['RDSInstances']['rdsinstance']

def _get_rds_db_version():
    return resources['RDSInstances']['rdsdbversion']

def _get_rds_db_user_name():
    return resources['RDSInstances']['dbusername']

def _get_rds_db_password():
    return resources['RDSInstances']['dbpassword']

def _get_rds_engine_name():
    return resources['RDSInstances']['engine_name']

def _get_rds_engine_version():
    return resources['RDSInstances']['engine_version']

def _get_rds_family_name():
    return resources['RDSInstances']['family_name']

def _get_rds_option_name():
    return resources['RDSInstances']['option_group_name']

def _get_rds_param_name():
    return resources['RDSInstances']['parameter_group_name']

def _get_rds_subnet_name():
    return resources['RDSInstances']['subnet_group_name']

def _get_rds_identifier():
    return resources['RDSInstances']['identifier']

def _get_rds_snapshotname():
    return resources['RDSInstances']['snapshotname']


def _get_redshift_name():
    return resources['Redshift']['redshiftname']

def _get_redshift_database_name():
    return resources['Redshift']['redshiftdatabasename']

def _get_redshift_master_user_name():
    return resources['Redshift']['redshiftmasterusername']

def _get_redshift_master_password():
    return resources['Redshift']['redshiftmasterpassword']

def _get_node_type():
    return resources['Redshift']['nodetype']

def _get_cluster_type():
    return resources['Redshift']['clustertype']

def _get_node_count():
    return resources['Redshift']['nodecount']

def _get_param_name():
    return resources['Redshift']['parameter_group_name']

def _get_subnet_name():
    return resources['Redshift']['subnet_group_name']

def _get_es_domain_name():
    return resources['ES']['domain_name']

def _get_es_version():
    return resources['ES']['es_version']

def _get_es_instance_type():
    return resources['ES']['instance_type']

def _get_es_instance_count():
    return resources['ES']['instance_count']

def _get_es_volume_size():
    return resources['ES']['ebs_volume_size']

def _get_es_policy_name():
    return resources['ES']['es_policyname']

def _get_es_port():
    return resources['ES']['es_port']

def _get_compute_environment():
    return resources['Batch']['compute_environment']

def _get_compute_instance_type():
    return resources['Batch']['computeinstance_type']

def _get_compute_max_vcpu():
    return resources['Batch']['maxvcpu']

def _get_compute_min_vcpu ():
    return resources['Batch']['minvcpu']

def _get_compute_desired_vcpu():
     return resources['Batch']['desiredvcpu']

def _get_job_queue_name():
     return resources['Batch']['job_queue_name']

def _get_priority_value():
     return resources['Batch']['priority_value']

def _get_batch_job_def_name():
    return resources['Batch']['batch_job_definition_name']

def get_DockerParams():
    return resources['Batch']['docker_parameters']

def _get_docker_params():
    return resources['Batch']['docker_parameters']

def _get_image_name():
    return _get_base_accountid() + ".dkr.ecr." + getRegion() + ".amazonaws.com/" + _get_batch_repo()+ splitter + "latest"

def _get_image_tag():
    return resources['Batch']['image_tag']

def _get_memory_size():
    return resources['Batch']['memory_size']

def _get_job_def_vcpu():
     return resources['Batch']['job_definition_vcpu']

def _get_attempts_number():
    return resources['Batch']['attempts_number']

def _get_resource_type():
    return resources['Batch']['resourcetype']

def _get_key_name():
    return resources['Batch']['keyname']

def _get_file_name():
    return resources['Batch']['filename']

def _get_lambda_fn_name():
    return resources['Lambda']['lambdafunctionname']

def _get_lambda_handler_name():
    return resources['Lambda']['lambdahandlername']

def _get_lambda_runtime_name():
    return resources['Lambda']['lambdaruntimename']

def _get_submit_file_name():
    return resources['Lambda']['submitfilename']

def _get_rule_file_name():
    return resources['Lambda']['rulefilename']

def _get_lambda_rule_fn_name():
    return resources['Lambda']['lambdarulefunctioname']

def _get_lambda_rule_handler_name():
    return resources['Lambda']['lambdarulehandlername']

def _get_submit_evnt_name():
    return resources['Lambda']['submiteventname']

def _get_rule_evnt_name():
    return resources['Lambda']['ruleeventname']

def _get_lambda_inventory():
    return resources['Lambda']['inventory']

def _get_lambda_backup():
    return resources['Lambda']['backup']

def _get_lambda_rules_keys():
    return resources['Lambda']['rules_keys']

def get_ValueByKey(resourcekey):
    return resourcevalues[resourcekey]

def get_KeyByValue(resourcevalue):
    try:
        return [key for key, value in resources.iteritems() if value == resourcevalue]
    except IndexError as index:
        return None

def ArePoliciesAvailable(Assigned, Expected):
    return set(Assigned)==set(Expected)

def getMissingPolicy(assignedList):
    return list(set(mergedlist)-set(assignedList))

def getMissingResource(key):
    try:
        return resources.get(key)
    except IndexError as index:
        return None
def isResourceAvailable(Assigned, Expected):
    return Assigned==Expected

def _get_aws_resource_name(assignedList):
    keylist=[]
    for key in resources['IAMPolicies'].keys():
        policycount=0
        expectedpolicycount=0
        for expectedpolicy in resources['IAMPolicies'][key]:
            policycount+=1
            for assignedpolicy in assignedList:
                if expectedpolicy==assignedpolicy:
                    expectedpolicycount+=1
                    if policycount==expectedpolicycount:
                        keylist.append(key)
    return keylist

def _get_keys():
    return resourcekeys

def _get_resources():
    return resources

def _get_vpcid():
    return resources['Network']['vpcID']

def _get_cidr():
    return resources['Network']['CIDR']

def _get_subnet():
    return resources['Network']['Subnet']

def _get_es_subnet():
    return resources['Network']['Subnet'][0]

def _write_json(key,value):
    with open(outputfilename, 'r') as data_file:
        data = json.load(data_file)
    data[key]=value
    with open(outputfilename, 'w') as data_file:
         data_file.write(json.dumps(data, sort_keys=True, indent=4, separators=(',', ': ')))

def _get_base_accountid():
    with open(outputfilename, 'r') as data_file:
        data = json.load(data_file)
    return data['base_account_id']

def _get_client_accountid():
    with open(outputfilename, 'r') as data_file:
            data = json.load(data_file)
    return data['client_account_id']

def _get_client_assume_role():
    with open(outputfilename, 'r') as data_file:
            data = json.load(data_file)
    return data['client_assume_role']

def _get_security_id():
    with open(outputfilename, 'r') as data_file:
            data = json.load(data_file)
    return data['security']

def _get_redshift_url():
    with open(outputfilename, 'r') as data_file:
            data = json.load(data_file)
    return data['redshift']

def _get_redshift_url_db():
    return "jdbc:redshift://" +_get_redshift_url() + "/" + _get_redshift_database_name()

def _get_rds_url():
    with open(outputfilename, 'r') as data_file:
            data = json.load(data_file)
    return data['rds']

def _get_rds_url_no_port():
    with open(outputfilename, 'r') as data_file:
            data = json.load(data_file)
    return data['rds'].split(":")[0]

def _get_rds_url_db():
    return "jdbc:mysql://" +_get_rds_url_no_port() + "/" + _get_rds_name()

def _get_es_url():
    with open(outputfilename, 'r') as data_file:
            data = json.load(data_file)
    return data['es']

def _get_job_revision():
    with open(outputfilename, 'r') as data_file:
            data = json.load(data_file)
    return data['batch']

def _get_dns_name():
    with open(outputfilename, 'r') as data_file:
        data = json.load(data_file)
    return "http://"+data['oss-api']

def _get_lambda_job_def_name():
    return _get_batch_job_def_name()+":"+_get_job_revision()

def _get_lambda_submit_evnt_name1():
    return _get_submit_evnt_name()[0]

def _get_lambda_submit_evnt_name2():
    return _get_submit_evnt_name()[1]

def _get_lambda_submit_job_queue_name():
    return _get_job_queue_name()[0]

def _get_lambda_rule_job_queue_name():
        return _get_job_queue_name()[1]

def _get_rds_info():
    return base64.b64encode(_get_rds_db_user_name() + ":" + _get_rds_db_password())

def _get_redshift_info():
    return base64.b64encode(_get_redshift_master_user_name() + ":" + _get_redshift_master_password())


def _get_api_ecs_cluster():
    return resources['OSS-API']['api-ecs-cluster']

def _get_ecs_execution_role_arn():
    return "arn:aws:iam::" + _get_base_accountid() + ":role/" + _get_pacecs_role_name()


#def _get_alb_sg():
#    return resources['OSS-API']['alb_sg']

def _get_alb_name():
    return resources['OSS-API']['alb_name']

def _get_api_image():
    return _get_base_accountid() + ".dkr.ecr." + getRegion() + ".amazonaws.com/" +  _get_api_repo()+ splitter + "latest"

def _get_api_container_name():
    return resources['OSS-API']['api_container_name']

def _get_config_task_definition_name():
    return resources['OSS-API']['config_task_definition_name']

def _get_admin_task_definition_name():
    return resources['OSS-API']['admin_task_definition_name']

def _get_asset_task_definition_name():
    return resources['OSS-API']['asset_task_definition_name']

def _get_compliance_task_definition_name():
    return resources['OSS-API']['compliance_task_definition_name']

def _get_notifications_task_definition_name():
    return resources['OSS-API']['notifications_task_definition_name']

def _get_statistics_task_definition_name():
    return resources['OSS-API']['statistics_task_definition_name']

def _get_auth_task_definition_name():
    return resources['OSS-API']['auth_task_definition_name']

def _get_notifications_jar_file_name():
    return resources['OSS-API']['notifications_jar_file_name']

def _get_statistics_jar_file_name():
    return resources['OSS-API']['statistics_jar_file_name']

def _get_config_jar_file_name():
    return resources['OSS-API']['config_jar_file_name']

def _get_admin_jar_file_name():
    return resources['OSS-API']['admin_jar_file_name']

def _get_asset_jar_file_name():
    return resources['OSS-API']['asset_jar_file_name']

def _get_auth_jar_file_name():
    return resources['OSS-API']['auth_jar_file_name']

def _get_compliance_jar_file_name():
    return resources['OSS-API']['compliance_jar_file_name']

def _get_notifications_jar_file_name():
    return resources['OSS-API']['notifications_jar_file_name']

def _get_statistics_jar_file_name():
    return resources['OSS-API']['statistics_jar_file_name']

def _get_pacman_url():
    return resources['OSS-API']['PACMAN_URL']

def _get_cloud_insights_token_url():
    return resources['OSS-API']['CLOUD_INSIGHTS_TOKEN_URL']

def _get_cloud_insights_cost_url():
    return resources['OSS-API']['CLOUD_INSIGHTS_COST_URL']

def _get_pacman_service_user():
    return resources['OSS-API']['PACMAN_SERVICE_USER']

def _get_pacman_service_password():
    return resources['OSS-API']['PACMAN_SERVICE_PASSWORD']

def _get_svc_corp_user_id():
    return resources['OSS-API']['SVC_CORP_USER_ID']

def _get_svc_corp_password():
    return resources['OSS-API']['SVC_CORP_PASSWORD']

def _get_apis_cloudwatch_group():
    return resources['OSS-API']['apis_cloudwatch_group']

def _get_CONFIG_PASSWORD():
    return resources['OSS-API']['CONFIG_PASSWORD']

def _get_LDAP_DOMAIN():
   return resources['OSS-API']['LDAP_DOMAIN']

def _get_LDAP_PORT():
    return resources['OSS-API']['LDAP_PORT']

def _get_LDAP_BASEDN():
    return resources['OSS-API']['LDAP_BASEDN']

def _get_LDAP_RESPONSETIMEOUT():
    return resources['OSS-API']['LDAP_RESPONSETIMEOUT']

def _get_LDAP_CONNECTIONTIMEOUT():
    return resources['OSS-API']['LDAP_CONNECTIONTIMEOUT']

def _get_LDAP_HOSTLIST():
    return resources['OSS-API']['LDAP_HOSTLIST']

def _get_CERTIFICATE_FEATURE_ENABLED():
    return resources['OSS-API']['CERTIFICATE_FEATURE_ENABLED']

def _get_PATCHING_FEATURE_ENABLED():
    return resources['OSS-API']['PATCHING_FEATURE_ENABLED']

def _get_VULNERABILITY_FEATURE_ENABLED():
    return resources['OSS-API']['VULNERABILITY_FEATURE_ENABLED']

def _get_ACCESS_KEY():
    return resources['OSS-API']['ACCESS_KEY']

def _get_SECRET_KEY():
    return resources['OSS-API']['SECRET_KEY']

def _get_SECURITY_USERNAME():
    return resources['OSS-API']['SECURITY_USERNAME']

def _get_SECURITY_PASSWORD():
    return resources['OSS-API']['SECURITY_PASSWORD']

def _get_ADMIN_SERVER():
    return resources['OSS-API']['ADMIN_SERVER']

def _get_ui_ecs_cluster():
    return resources['OSS-UI']['api-ecs-cluster']

def _get_ui_task_definition_name():
    return resources['OSS-UI']['task_definition_name']


#def _get_ecs_execution_role_arn():
#    return resources['OSS-API']['ecs_execution_role_arn']

#def _get_alb_sg():
#    return resources['OSS-API']['alb_sg']

def _get_ui_alb_name():
    return resources['OSS-UI']['alb_name']

def _get_ui_ecs_cluster():
    return  resources['OSS-UI']['api-ecs-cluster']

def _get_ui_image():
    return _get_base_accountid() + ".dkr.ecr." + getRegion() + ".amazonaws.com/" + _get_ui_repo()+ splitter + "latest"

def _get_ui_container_name():
    return resources['OSS-UI']['ui_container_name']

def _get_batch_repo():
    return resources['Repository']['batch']

def _get_api_repo():
    return resources['Repository']['oss-api']

def _get_ui_repo():
    return resources['Repository']['oss-ui']

def _build_ui_apps(aws_access_key,aws_secret_key,region):
    pacman_cwd = os.getcwd()
    upload_dir = os.getcwd() + "/terraform/s3-upload/upload"
    import ui.build_apps as BA
    BA.BuildPacman(
        _get_dns_name(),
        upload_dir,
        pacman_cwd + "/pacman_installation.log",
    ).build_api_and_ui_apps(
        aws_access_key,
        aws_secret_key,
        region,
        bucket=_get_s3_bucket_name() + '-' + _get_base_accountid()
    )
    os.chdir(pacman_cwd)


def _get_tf_vars():
    filecreator._create_tfvars_file()
    shutil.move("terraform.tfvars","terraform/lambda-rule/terraform.tfvars")
    return "Lambda"

def update_sql_file_with_values_for_varaiables():
    '''
    This method replace the hardcoded region-accountid combination with the dynamic
    '''
    sql_file = os.getcwd() + "/terraform/oss-api/DB.sql"
    sql_file_with_values = os.getcwd() + "/terraform/oss-api/DB_With_Values.sql"
    with open(sql_file, 'r') as f:
        lines = f.readlines()
    region = getRegion()
    account = _get_base_accountid()
    eshost = 'http://' + _get_es_url()
    esport = _get_es_port()
    for idx, line in enumerate(lines):
        if "SET @region='$region';" in line.decode('utf-8'):
            lines[idx] = line.decode('utf-8').replace("@region='$region'", "@region='" + region + "'")
        if "SET @account='$account';" in line.decode('utf-8'):
            lines[idx] = line.decode('utf-8').replace("@account='$account'", "@account='" + account + "'")
        if "SET @eshost='$eshost';" in line.decode('utf-8'):
            lines[idx] = line.decode('utf-8').replace("@eshost='$eshost'", "@eshost='" + eshost + "'")
        if "SET @esport='$esport';" in line.decode('utf-8'):
            lines[idx] = line.decode('utf-8').replace("@esport='$esport'", "@esport='" + esport + "'")

    with open(sql_file_with_values, 'w') as f:
        f.writelines(lines)

update_sql_file_with_values_for_varaiables()
