#!/bin/bash

## Install git
sudo yum -y install git


## Install python3
sudo yum -y update
sudo yum -y install yum-utils
sudo yum -y groupinstall development
sudo yum -y install https://centos7.iuscommunity.org/ius-release.rpm
sudo yum -y install python3
sudo yum -y install python-pip
pip install virtualenv


## Install Terraform
sudo yum -y install unzip
wget https://releases.hashicorp.com/terraform/0.11.10/terraform_0.11.10_linux_amd64.zip .
unzip terraform_0.11.10_linux_amd64.zip
sudo mv terraform /usr/bin

## INSTALL NODEJS and dependencies
sudo yum install -y gcc-c++ make
curl -sL https://rpm.nodesource.com/setup_8.x | sudo bash
sudo yum install -y nodejs
sudo npm install -g bower

sudo npm install -g @angular/cli
# sudo npm audit fix
# npm install --save-dev @angular/cli@7.0.6

## Install Maven
sudo yum -y install java-1.8.0-openjdk
## iNSTALL MAVEN
sudo yum install -y maven


## Install docker
# sudo yum install -y epel-release
sudo yum -y install amazon-linux-extras install docker
sudo amazon-linux-extras install -y docker
sudo systemctl start docker


## Install mysql
sudo yum -y install mysql

echo alias cdd=\"cd $(pwd)\" >> ~/.bashrc
echo alias cdt=\"cd $(pwd)/data/terraform\" >> ~/.bashrc
echo alias cdl=\"cd $(pwd)/log\" >> ~/.bashrc
source ~/.bashrc


## Install virtualenv
mkdirs ~/envs/
virtualenv ~/envs/pacbot_env --python=python3
source ~/envs/pacbot_env/bin/activate
echo source ~/envs/pacbot_env/bin/activate >> ~/.bashrc
pip install -r requirements.txt
