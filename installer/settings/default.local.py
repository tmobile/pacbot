# CRAETE local.py file by renaming/copying default.local.py
# User should update the VPC details below in local.py
VPC = {
    "ID": "vpc-1",
    "CIDR_BLOCKS": ["10.0.0.0/16"],
    "SUBNETS": ["subnet-1", "subnet-2"]
}

MAKE_ALB_INTERNAL = True

# System reads below data from user if not updated here
AWS_ACCESS_KEY = ""
AWS_SECRET_KEY = ""
AWS_REGION = ""
