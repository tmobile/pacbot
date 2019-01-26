#!/bin/bash
aws s3 cp s3://{{s3-bucket-base-path}}/${JAR_FILE} /root && chmod 755 /root/${JAR_FILE}
/root/${JAR_FILE}
