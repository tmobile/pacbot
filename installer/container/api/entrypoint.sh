#!/bin/bash
aws s3 cp s3://pacman-data1/${JAR_FILE} /root && chmod 755 /root/${JAR_FILE}
/root/${JAR_FILE}
