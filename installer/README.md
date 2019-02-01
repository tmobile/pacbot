# PacBot Installer

## Overview
This page describes the steps to install PacBot. PacBot is built to be deployed in AWS mostly using managed services.
There are 3 major components in PacBot.


```
PacBot Rule Engine                  : CloudWatch Rules, Lambda, AWS Batch, AWS ElasticSearch

PacBot Web Application (UI & APIs)  : AWS ECS, Fargate, AWS ElasticSearch, RDS

PacBot Inventory Collector          : Cloudwatch Rules, AWS Batch, AWS ElasticSearch, Redshift, RDS
```

## List of AWS resources that will be created by the installer.


**List of AWS resources that will be created by the installer.**

  * IAM Roles
  * IAM Policies
  * S3 Bucket
  * RDS
    * MySQL 5.6.X
  * Elasticsearch Service
    * Elasticsearch version 5.5
  * Redshift
    * Single Node
  * Batch
    * Compute environments, Job Definitions and Job Queues
  * Elastic Container Registry
    * Repositories - for batch job, API and UI
  * Elastic Container Service - [AWS Fargate](https://aws.amazon.com/fargate/)
    * Clusters - for APIs, UI and Batch
    * Task Definitions - for APIs and UI
  * Lambda Functions
    * SubmitBatchJob and SubmitRuleJob
  * CloudWatch Rules



## Steps to Install

This python installer script will launch the above listed AWS resources and configure them as required for the PacBot application. This will also
build the application from the source code. The built JARs and Angular app are then deployed in AWS ECS.

* [Prerequisites](##prerequisites)
* [Install](##Install)
* [Limitations](##limitations)

## Prerequisites

PacBot installer is developed using Python and Terraform. For the installer to run, you will need to have below listed dependencies installed correctly.

* Software Dependencies:
   1. Python supported version is 3.4 or above
   2. Following python packages are required.
      * docker-py (1.10)]
      * python-terraform (0.10)
      * boto3 (1.9)
      * gitpython
   3. Install the latest version of Terraform from https://learn.hashicorp.com/terraform/getting-started/install.html
   4. Install `node` version 8.15.0 or higher
   5. Install `npm` version 6.4.1 or higher
   6. Install the following npm packages
      * Install `Angular-CLI` version 7.1.4 or higher
      * Install `bower` version 1.8.4 or higher
   7. Install `java` version openjdk1.8 or higher
   8. Install `mvn`(Maven) version 3.0 or higher
   9. Install `docker` version 18.06 or higher
   10. Install `MySQL` version 15.1 or higher

* AWS IAM Permission Installer would need an IAM account to launch and configure the AWS resources. To keep it simple you can create an IAM account
with full access to above listed AWS service or temporarily assign Poweruser/Administrator permission. After the installation, you can remove the
IAM account.

* Make sure that docker service is running during the installation time.
* The installer box or machine from where the installation is happening should be on the same VPC or should be able to connect to MySQL DB

## System Setup To Run Installer

1. Installer System:
  ```
      Recommended to use Amazon Linux / CentOS 7 / Ubuntu
  ```
2. System Configurations:
  ```
      Recommended instance type: t2.medium (Minimum 4GB memory)
      VPC: Same as where PacBot is desired to be installed
  ```
3. Install Git

```
    sudo yum install git
```

4. Install Pip & required modules
```
    sudo yum install -y epel-release python3-pip
    sudo pip3 install -r requirements.txt
```

5. Install other dependencies
```
     sudo yum -y install java-1.8.0-openjdk docker maven unzip mysql
     sudo systemctl start docker
```

6. To install terraform, download the latest version
```
     wget https://releases.hashicorp.com/terraform/0.11.8/terraform_0.11.8_linux_amd64.zip
     unzip terraform_0.11.8_linux_amd64.zip
     mv terraform /usr/bin/
```

7. To install UI build dependencies, please click [here](https://github.com/tmobile/pacbot/wiki/UI-Development-&-Build)



## Install and Deploy PacBot


1. Clone the repo
```
    git clone git@github.com:tmobile/pacbot.git
```

2. Go to pacbot-installer directory

3. Create settings/local.py file by copying from settings/default.local.py

4. Update settings/local.py file with the required values - Mandaory Changes
```
   VPC ID
   VPC CIDR
   SUBNET IDS (2 Subnets are required. The second subnet is just for Redshit HA. Both the subnets should not be in the same AZ.)
```

5. Run the installer. (Go grab a coffee now :), it would take a while to provision the AWS resources)
```
    sudo python3 manager.py install
```

6. Installation logs will be available in logs directory
```
    tail -f logs/debug.log -> To see the debug log
    tail -f logs/error.log -> To see the error log
    tail -f logs/terraform_install.log -> To see Terraform Installation log
    tail -f logs/terraform_destroy.log -> To see Terraform Destroy log
```

**Once the installation is complete, go to the PacBot ELB URL to access the web application. Use the default credentials**
  * Admin User : admin@pacbot.org / pacman
  * Readonly User : user@pacbot.org / user


## Redeploy
Once you have installed the application and later if any updation occurs then you would be able to redeploy it without any change
in endpoints and URL. Please follow the below steps to redeploy the applications
1. Go to pacbot source code and pull the latest changes
```
    git pull --rebase
```

2. Go to pacbot-installer directory

3. Run the below command to redeploy the application
```
    sudo python3 manager.py redeploy
```

## Uninstall
```

sudo python3 manager.py destroy

```
'destroy' will terminate all the AWS resources created during the installation.

## Troubleshooting
Installation issues will be mostly around permissions and dependencies required by the installer. Please make sure all the dependencies are
installed correctly before installing PacBot.


## Adding New AWS Accounts to PacBot to Monitor


1. **IAM Role Changes**
   The account where PacBot is installed is called base account. The accounts that are monitored by PacBot is called client account.

    * Client Account Change: Create an IAM role named pacbot_ro and attach ReadOnlyAccess, AmazonGuardDutyReadOnlyAccess & AWSSupportAccess policies.
    Allow pacbot_ro from the base account to assume this role.
      Sample trust configuration for pacbot_ro role is here
        ```javascript
        {
          "Version":"2012-10-17",
          "Statement":[
            {"Effect":"Allow",
            "Principal":{
              "AWS":["arn:aws:iam::Base_Account_ID:role/pacbot_ro"]
              },
              "Action":"sts:AssumeRole"
              }]
          }
        ```

    * Base Account Change: Fetch client account pacbot_ro role arn and update pacbot_ro policy which is associated with pacman_role in Base account.
    Sample pacbot_ro policy,
        ```javascript
        {
          "Version":"2012-10-17",
          "Statement":[
            {"Sid":"",
            "Effect":"Allow",
            "Action":"sts:AssumeRole",
            "Resource":["arn:aws:iam::Client_Account_ID_1:role/pacman_ro","arn:aws:iam::Client_Account_ID_2:role/pacbot_ro"]
            }]
        }
        ```


2. **Cloudwatch Rule Changes**
*  Update "**accountinfo**" value (in _Constant (JSON text)_ of cloudwatch rule) with new client account ids in cloudwatch rule named "**AWS-Data-Collector**". Sample configuration is
  `{"encrypt":false,"value":"Base_Account_ID,Client_Account_ID_1,Client_Account_ID_2","key":"accountinfo"}`

```
New AWS account management page with features to add remove accounts is being developed. Watch out this section for the updates.
Till then you have to configure it manually
```


## Limitations:
   * Current version supports only AWS stack.
   * AWS Fargate is not available in all AWS regions.
Please visit  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/) for more information on
AWS regions and services.
