# Installation FAQs

### Installation is failed. What should I do now?

Installation could fail due to various reasons. If an error occurs then detailed messages will be stored in log/error.log. You can check the log file and identify the issue.
Please verify the following steps before you proceed further if any error is occured.

1. Is your installer machine has atleast 4 GB of ram?
  To install PacBot the installer machine should have atleast 4GB of ram. We recommend to use a **t2.medium** instance atleast

2. Is maven build failing?
  It could be possible to fail maven build if you run the installation from home directory of user. So we recommend to clone PacBot repo in /opt/ directory and start installation from there

3. Is your installer machine under the same VPC where you would like to install PacBot resources?
  The installer machine should be under the same VPC or there should be a VPC peering to connect to the resources created from the installer machine. This is required as installation script need to access MySQl to import initial data from sql file

4. Is your installaer machine has enough disk space?
  To be on the safer side please ensure that atleast 8GB disk space is there so that docker build can create image.

5. Is Amazon region has capacity to create 82 more CloudWatch rules?
  As part of PacBot installation 82 cloudwatch rules will be created. Normally AWS has limitation of 100 rules per region. So please ensure that there is room for 82 rules creation. You can contact the support to get an increased limit.


### Batch jobs stuck in runnable state and not moving to running state. Why?
  There can be various reasons due to which batch jobs remain in runnable state and do not advance. One reason could be the bad network configuration. For batch jobs to run the instances should have external network connectivity. Since the resources have no public IP address, they must have NAT gateway/instance attached to it.
  Please see more details about this here, https://docs.aws.amazon.com/batch/latest/userguide/troubleshooting.html#job_stuck_in_runnable


### I have created an intenet-facing(public) ALB but still the application is not loading Or seems to be very slow. Why?
  If you create the ALB as internet-facing then it should have subnet(s) with an internet-gateway attached to it. Otherwise communication between VPC and internet should not happen. So please ensure that internet-gateway is correctly configured to the subnet. You can check this by going to Load balancer and edit the subnet. There you will be able to see the warning if there is any.


### I have created an intenet-facing(public) ALB but APIs are failing. Why?
  If you make an ALB internet-facing and internet gateway is correctly configured with subnets then ever after APIs are failing then that might be because of security group inbound rules. You should either enable access from anywhere Or identify the container IPs and add every one of them to the security group. This is required as all API services except config service communicate with config service initially to get the configuration properties. So other APIs from their containers should be able to connect to config service which can happen only if those container IPs are enabled in the security group.


### I am idsconnected from installer machine before install/destroy command gets completed. What should I do now?
  It is always recommended to run the install or destroy command behind linux screen(https://linuxize.com/post/how-to-use-linux-screen/).
  After running install/destroy command if you get disconnected from the installer machine then the process will be running at the background. So wait for atleast 30 minutes and then again try to run the command again. If you get warning message saying "Another process running...", try to check any process with name **terraform** is running or not. If there is any such process then wait  till that completes. If there is no such process then please delete lock file from installer/data/terraform/.terraform.lock.info, and try to run the command again


### Is it required for installer machine to be running all the time?
  No, installer machine do not required to be running always. You can stop the instance once you have done the installation. If there is any newer version update occurs, you can start the machine, pull the latest PacBot code and run redeploy command. Then after that you can stop the instance.


### My installer machine got terminated accidently. How can I redeploy if latest version get released?
  Your installer machine got terminated? do not worry we will be saving the required state files in S3. What you have to do is ti follow the below steps
 1. Start a new instance under the same VPC
 2. Clone PacBot repo in /opt directory
 3. In S3 you can see pacbot bucket and there is a zip file with name pacbot-terraform-installer-backup.zip. Download the file and extract them inside installer directory to replace /installer/data dicrectory.
 4. Edit local.py file to have all configurations
 5. Try to run install command followed by redeploy command


### Destroy command threw timeout error. What should I do now?
  If destroy command not get executed successfully and terminated with timeout error then the destruction might be happening at the AWS. So wait for 30-60 minutes and run destroy command again.