resource "aws_batch_compute_environment" "pacman-compute-environment" {
  compute_environment_name = "${var.compute_environment}"
  compute_resources {
    instance_role = "arn:aws:iam::${var.accountid}:instance-profile/${var.instance_profile}"
    instance_type = ["${var.computeinstance_type}"] 
    max_vcpus = "${var.maxvcpu}"
    min_vcpus = "${var.minvcpu}"
    desired_vcpus = "${var.desiredvcpu}"
    ec2_key_pair = "${var.keyname}"
    security_group_ids = ["${var.pacman_sgid}"]
    subnets = [
      "${var.subnetid}"
    ]
    type = "${var.resourcetype}"
  }
  service_role = "arn:aws:iam::${var.accountid}:role/${var.batchrole}"
  type = "MANAGED" 
}
