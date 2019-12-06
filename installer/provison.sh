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
# curl -sL https://rpm.nodesource.com/setup_8.x | sudo bash
curl --silent --location https://rpm.nodesource.com/setup_10.x | sudo bash -
sudo yum install -y nodejs
sudo npm install -g yarn
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


---------------------------------
-----Ubuntu-----
---------------------------------

sudo apt -y update
sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt-get update
sudo apt install -y openjdk-8-jdk
sudo  update-java-alternatives --set openjdk-8-jdk

sudo apt install -y maven
sudo apt install -y docker
sudo apt install -y docker.io
sudo systemctl start docker
sudo apt install -y python3
sudo apt install -y python3-venv
sudo apt install -y mysql-client

sudo apt -y install unzip
wget https://releases.hashicorp.com/terraform/0.11.10/terraform_0.11.10_linux_amd64.zip
unzip terraform_0.11.10_linux_amd64.zip
sudo mv terraform /usr/bin

echo alias cdd=\"cd $(pwd)\" >> ~/.bashrc
echo alias cdt=\"cd $(pwd)/data/terraform\" >> ~/.bashrc
echo alias cdl=\"cd $(pwd)/log\" >> ~/.bashrc
source ~/.bashrc

sudo apt install -y curl
curl -sL https://deb.nodesource.com/setup_10.x | sudo -E bash -
sudo apt -y update
sudo apt install -y nodejs
sudo apt install -y npm
sudo npm install -g yarn
sudo npm install -g @angular/cli

## Install virtualenv
mkdir ~/envs/
python3 -m venv ~/envs/pacbot_env
source ~/envs/pacbot_env/bin/activate
echo source ~/envs/pacbot_env/bin/activate >> ~/.bashrc
pip install -r requirements.txt

