#! /usr/bin/env python
import boto3

import jsonRead
import awsterraformbuild
import time
import sys
import network
import base64
import os
import varsdata
from progressbar import ProgressBar
from termcolor import cprint

pbar = ProgressBar()

user_name = ''
user_arn = ''
policylist = []
assignedpolicieslist = []
client_accountid = ''

accessKey = raw_input("Enter base Account Access Key=")
secretKey = raw_input("Enter base Account Secret Key=")
region = raw_input("Enter base Account AWS region=")
# client_arn=raw_input("Enter service account role arn=")

vpcid = jsonRead._get_vpcid()
client_assumerole = ''  # client_arn.split("/")[1]
client_accountid = ''  # client_arn.split("::")[1].split(":")[0]

jsonRead._write_json("client_account_id", client_accountid)
jsonRead._write_json("client_assume_role", client_assumerole)

err_msg = "System is exiting"
vpcid = network._check_vpc(region, accessKey, secretKey, jsonRead._get_vpcid(), err_msg)
if vpcid is None:
    sys.exit()

cidr_input = jsonRead._get_cidr()
cidr_list = network._get_cidr_list()
subnet_list = network._get_subnetid(vpcid, region, accessKey, secretKey)
subnet_input = jsonRead._get_subnet()

'''
if cidr_input not in cidr_list:
   print "Please add correct CIDR in resource.json"
   pacman_exit(err_msg)

if subnet_input not in subnet_list:
   print "please add correct subnet in resource.json"
   pacman_exit(err_msg)
'''


def pacman_exit(err_msg):
    print err_msg
    sys.exit()

if set(cidr_input).intersection(cidr_list) is None:
    print "Please add correct CIDR in resource.json"
    pacman_exit(err_msg)

if set(subnet_input).intersection(subnet_list) is None:
    print "please add correct subnet in resource.json"
    pacman_exit(err_msg)


def config_line(header, name, detail, data):
    return header + ", " + name + ", " + detail + ", " + data


def output_lines(lines):
    lines.sort()
    for line in lines:
        print line


def _get_current_user(accessKey, secretKey):
    iam = boto3.resource('iam', aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    return iam.CurrentUser().user_name


def _get_current_arn(accessKey, secretKey):
    iam = boto3.resource('iam', aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    return iam.CurrentUser().arn


def _get_account_id(accessKey, secretKey):
    return boto3.client('sts', aws_access_key_id=accessKey, aws_secret_access_key=secretKey).get_caller_identity().get('Account')


#  To get Managed IAM policies
def _get_user_managed_policies(iam, user_name):
    response = iam.list_attached_user_policies(UserName=user_name)
    policies = response['AttachedPolicies']
    for policydetails in policies:
        for key in policydetails:
            if key == 'PolicyName':
                assignedpolicieslist.append(policydetails['PolicyName'])


# To get Inline IAM policies
def _get_user_inline_policies(iam, user_name):
    response = iam.list_user_policies(UserName=user_name)
    policies = response['PolicyNames']
    for policyname in policies:
        assignedpolicieslist.append(policyname)


# To get Group name for user
def _get_user_groups(iam, user_name):
    response = iam.list_groups_for_user(UserName=user_name)
    user_groups = response['Groups']
    grouplist = []
    for group in user_groups:
        grouplist.append(group['GroupName'])
    return grouplist


# To get Group Managed IAM Policies
def _get_group_managed_policies(iam, group_name):
    response = iam.list_attached_group_policies(GroupName=group_name)
    policies = response['AttachedPolicies']
    for policydetails in policies:
        for key in policydetails:
            if key == 'PolicyName':
                assignedpolicieslist.append(policydetails['PolicyName'])


# To get Group Inline IAM policies
def _get_group_inline_policies(iam, group_name):
    response = iam.list_group_policies(GroupName=group_name)
    policies = response['PolicyNames']
    for policyname in policies:
        assignedpolicieslist.append(policyname)


def _get_policy_details(iam, user_name):
    grouplist = _get_user_groups(iam, user_name)
    for group in grouplist:
        _get_group_managed_policies(iam, group)
        _get_group_inline_policies(iam, group)


def _access_validation(assignedList, user_name, user_arn):
    assignedPerm = jsonRead._get_aws_resource_name(assignedList)
    expectedResource = jsonRead._get_resources()
    expectedKeys = jsonRead._get_keys()
    isAccess = None
    NoAccess = None
    for resource in pbar(expectedKeys):
        print ""
        cprint("Checking " + str(resource) + " Permissions", 'green')
        time.sleep(1)
        if resource in assignedPerm:
            cprint("Yes neccsaary permissions are avaiable", 'green')
            isAccess = True
        else:
            user_contnue = raw_input("It seems no full access policy attached. If you have added custom policy with all permissions, please type Yes or No: ")
            if user_contnue != "Yes":
                cprint("Not having enough permissions", 'red')
                NoAccess = True
                sys.exit()
            else:
                isAccess = True

    if isAccess and NoAccess is None:
        print "Access is provided"
        print " Creating AWS resources"
        time.sleep(1)
        awsterraformbuild._create_aws_resources(accessKey, secretKey, region)
        print "All the resources are created"


def _destroy_pacman_resources(accessKey, secretKey, region):
    awsterraformbuild._destroy_aws_resources(accessKey, secretKey, region)


if __name__ == '__main__':
    try:
        user_name = _get_current_user(accessKey, secretKey)
        user_arn = _get_current_arn(accessKey, secretKey)
        # Get user Policies
        iam = boto3.client('iam', aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
        _get_user_managed_policies(iam, user_name)
        _get_user_inline_policies(iam, user_name)
        # Erase existing contents
        open('pacman_installation.log', 'w').close()
        # Get Group policies
        _get_policy_details(iam, user_name)
        jsonRead._write_json("base_account_id", _get_account_id(accessKey, secretKey))
        jsonRead._write_json("region", region)
        pacman_input = sys.argv[1].strip().lower()
        if pacman_input == "install":
            awsterraformbuild.pacman_installation.write("Starting pacman installation \n")
            _access_validation(assignedpolicieslist, user_name, user_arn)

        elif pacman_input == "destroy":
            awsterraformbuild.pacman_installation.write("Deleting pacman installation \n")
            _destroy_pacman_resources(accessKey, secretKey, region)
    except IndexError as ie:
        print "Give valid option"

    awsterraformbuild.pacman_installation.close()
