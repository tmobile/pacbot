#!/usr/bin/env python
import boto3
import sys

cidrvalues = {}
vpcchoicelist = []
cidrblocklist = []


def _get_subnetid(vpcid, region, accessKey, secretKey):
    subnetlist = []
    ec2 = boto3.client('ec2', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    response = ec2.describe_subnets(Filters=[
        {
            'Name': 'vpc-id',
            'Values': [vpcid]
        }
    ])
    for subnet in response['Subnets']:
        subnetlist.append(subnet['SubnetId'])
    return subnetlist


def create_KeyPair(region, accessKey, secretKey, keyname, filename):
    ec2 = boto3.client('ec2', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
    try:
        keypair = ec2.create_key_pair(KeyName=keyname)
        keyfile = open(filename, "w+")
        keyfile.write(keypair['KeyMaterial'])
        print "pacbot.ppk file generated & saved into the current location"
    except Exception as e:
        print "pacbot file is already created & stored"


def _get_cidr_list():
    return cidrblocklist


def _check_vpc(region, accessKey, secretKey, vpcid, errmsg):
    try:
        ec2 = boto3.client('ec2', region_name=region, aws_access_key_id=accessKey, aws_secret_access_key=secretKey)
        vpc_response = ec2.describe_vpcs(
            VpcIds=[
                vpcid,
            ]
        )
        for item in vpc_response["Vpcs"]:
            cidrblocklist.append(item["CidrBlock"])
        return vpcid
    except Exception as e:
        print errmsg
        return None
