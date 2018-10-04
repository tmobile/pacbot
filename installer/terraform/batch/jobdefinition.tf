resource "aws_batch_job_definition" "pacman_job_definition" {
    name = "${var.batch_job_definition_name}"
    type = "container"
    container_properties = <<CONTAINER_PROPERTIES
{
    "command": ["${var.docker_parameters[0]}","${var.docker_parameters[1]}","${var.docker_parameters[2]}",
        "${var.docker_parameters[3]}","${var.docker_parameters[4]}","${var.docker_parameters[5]}"],
    "image": "${var.image_name}",
    "memory": ${var.memory_size},
    "vcpus": ${var.job_definition_vcpu},
    "environment": [
        {"name": "ES_HOST", "value": "http://${var.ES_HOST}:80"},
        {"name": "BASE_AWS_ACCOUNT", "value": "${var.BASE_AWS_ACCOUNT}"},
        {"name": "ES_URI", "value": "http://${var.ES_URI}:80"},
        {"name": "HEIMDALL_URI", "value": "http://${var.HEIMDALL_URI}:80"},
        {"name": "PACMAN_API_URI", "value": "${var.PACMAN_API_URI}/api/"}
    ]
}
CONTAINER_PROPERTIES
    retry_strategy
 {
    attempts = "${var.attempts_number}"
}
}
output "pacmanrevision" {
   value = "${aws_batch_job_definition.pacman_job_definition.revision}"
}
output "pacman" {
   value = "${aws_batch_job_definition.pacman_job_definition.revision}"
}

