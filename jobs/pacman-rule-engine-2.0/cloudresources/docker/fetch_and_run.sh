#!/bin/bash
echo "fetching the executor...."
aws s3 cp s3://pacman-aws-rule-executables/$4  ~/$4
echo "got the executor file ==>"
echo $4
echo "fetching the executable...."
aws s3 cp s3://pacman-aws-rule-executables/$1  ~/$1
echo "got the executable file==>"
echo $1
echo "Launching jvm with following params"
echo $@
cd ~
echo "running at"
echo $PWD
echo "list of files here"
echo . *
java $3 -cp "./*:." $5 "$2"




