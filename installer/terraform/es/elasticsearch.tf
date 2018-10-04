resource "aws_elasticsearch_domain" "pacmanes" {
  domain_name           = "${var.domain_name}"
  elasticsearch_version = "${var.es_version}"

  cluster_config {
    instance_type = "${var.instance_type}"
    instance_count = "${var.instance_count}"
    dedicated_master_enabled = false
    zone_awareness_enabled= false
  }
  ebs_options{
    ebs_enabled = true
    volume_type = "gp2"
    volume_size = "${var.ebs_volume_size}"
  }
  tags {
    Domain = "${var.domain_name}"
  }
  vpc_options {
    security_group_ids = ["${var.pacman_sgid}"]
    subnet_ids         = ["${var.subnetid}"]
  }
  snapshot_options {
    automated_snapshot_start_hour = 23
  }
}
resource "aws_elasticsearch_domain_policy" "domainpolicy" {
  domain_name = "${var.domain_name}"
  access_policies  = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal":"*",
      "Action":  "es:*",
       "Resource": "${aws_elasticsearch_domain.pacmanes.arn}/*"
    }
  ]
}
EOF
  depends_on = ["aws_elasticsearch_domain.pacmanes"]
}
output "es_endpoint" {
   value = "${aws_elasticsearch_domain.pacmanes.endpoint}"
}
output "pacman" {
   value = "${aws_elasticsearch_domain.pacmanes.endpoint}"
}
output "es_kibana" {
   value = "${aws_elasticsearch_domain.pacmanes.kibana_endpoint}"
}

