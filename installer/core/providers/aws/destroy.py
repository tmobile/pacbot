from core.config import Settings
from core.providers.aws import BaseAction
from core.terraform import PyTerraform
from core import constants as K
from time import sleep
from threading import Thread
from datetime import datetime
import importlib
import sys


class Destroy(BaseAction):
    executed_with_error = False

    def __init__(self, args, input_obj):
        self.args = args
        super().__init__(input_obj)

    def execute(self, resources, terraform_with_targets, dry_run):
        error_response = self.validate_arguments(resources, terraform_with_targets)
        if not error_response:
            self._create_terraform_provider_file()
            self.execute_terraform(resources, terraform_with_targets, dry_run)
            self._delete_terraform_provider_file()
        else:
            self.exit_with_validation_errors(error_response)

    def execute_terraform(self, resources, terraform_with_targets, dry_run):
        self.show_step_heading(K.TERRAFORM_DESTROY_STARTED, write_log=False)

        if not dry_run:
            self.run_pre_destoy(resources)
            start_time = datetime.now()
            self.destroy_resources(resources, terraform_with_targets, start_time)
            self.run_post_destoy(resources)
        else:
            self.show_step_finish(K.TERRAFORM_DESTROY_DRY_RUN)

        end_time = datetime.now()
        self.display_process_duration(start_time, end_time)
        self._cleanup_destroy()

    def _cleanup_destroy(self):
        self._delete_terraform_provider_file()

    def destroy_resources(self, resources, terraform_with_targets, start_time):
        destroy_resources = resources if terraform_with_targets else None
        terraform = PyTerraform()
        p = terraform.terraform_destroy(destroy_resources)

        self.resource_count = len(resources)
        output_count = self._get_terraform_output_count(self.resource_count)
        real_resource_count = str(output_count)

        invoke_output = True
        while p.poll() is None:
            # Invoke output only if invoke_output variable is True
            invoke_output = False if invoke_output else True
            output_count = self._get_terraform_output_count(output_count) if invoke_output else output_count

            rsource_progress_metric = self.BGREEN_ANSI + str(output_count) + "/" + real_resource_count + self.END_ANSI
            duration = self.CYAN_ANSI + self.get_duration(datetime.now() - start_time) + self.END_ANSI
            message = "%s (%s) Time elapsed: %s" % (K.TERRAFORM_DESTROY_RUNNING, rsource_progress_metric, duration)
            self.show_progress_message(message, 1.5)

        self.erase_printed_line()

        terraform.process_destroy_result(p)
        self.show_step_finish(K.TERRAFORM_DESTROY_COMPLETED, write_log=False, color=self.GREEN_ANSI)

    def run_pre_destoy(self, resources):
        for resource in resources:
            resource.pre_terraform_destroy()

    def run_post_destoy(self, resources):
        for resource in resources:
            resource.post_terraform_destroy()
            resource.remove_terraform()
