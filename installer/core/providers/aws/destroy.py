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
    destroy_start_time = datetime.now()
    destroy_statuses = {
        "tf_destroy_start": 1,
        "execution_finished": 3
    }
    exception = None
    terraform_thread = None

    def __init__(self, args, input_obj):
        self.args = args
        super().__init__(input_obj)

    def execute(self, resources, terraform_with_targets, dry_run):
        error_response = self.validate_arguments(resources, terraform_with_targets)
        if not error_response:
            self._create_terraform_provider_file()
            self.execute_terraform_destroy(resources, terraform_with_targets, dry_run)
            self._delete_terraform_provider_file()
        else:
            self.exit_with_validation_errors(error_response)

    def execute_terraform_destroy(self, resources, terraform_with_targets, dry_run):
        self.show_step_heading(K.TERRAFORM_DESTROY_STARTED, write_log=False)

        if not dry_run:
            self.destroy_start_time = datetime.now()
            self.current_destroy_status = self.destroy_statuses.get('tf_destroy_start')
            self.destroy_resources_and_show_progress(resources, terraform_with_targets)
            self._cleanup_destroy()
            if self.executed_with_error:
                raise Exception(self.exception)
        else:
            self.show_step_finish(K.TERRAFORM_DESTROY_DRY_RUN)

    def _cleanup_destroy(self):
        self._delete_terraform_provider_file()

    def run_pre_destoy(self, resources):
        for resource in resources:
            resource.pre_terraform_destroy()

    def run_post_destoy(self, resources):
        for resource in resources:
            resource.post_terraform_destroy()
            resource.remove_terraform()

    def destroy_resources_and_show_progress(self, resources, terraform_with_targets):
        self.terraform_thread = Thread(target=self.destroy_resources, args=(list(resources), terraform_with_targets))
        display_thread = Thread(target=self.show_progress_status, args=(list(resources), terraform_with_targets))

        self.terraform_thread.start()
        display_thread.start()

        self.terraform_thread.join()
        display_thread.join()

    def destroy_resources(self, resources, terraform_with_targets):
        destroy_resources = resources if terraform_with_targets else None
        self.run_pre_destoy(resources)

        # May be timeout causes first destroy to be a failure hence attempt as many times as the value in the setting
        for attempt in range(Settings.DESTROY_NUM_ATTEMPTS):
            try:
                PyTerraform().terraform_destroy(destroy_resources)
                self.run_post_destoy(resources)
                break
            except Exception as e:
                self.executed_with_error = True
                self.exception = e

        self.current_destroy_status = self.destroy_statuses.get('execution_finished')

    def show_progress_status(self, resources, terraform_with_targets):
        sleep(1)  # To sleep initaially for pre-destroy to process
        while self.destroy_statuses.get('execution_finished') != self.current_destroy_status and self.terraform_thread.isAlive():
            duration = self.CYAN_ANSI + self.get_duration(datetime.now() - self.destroy_start_time) + self.END_ANSI
            message = "%s. Time elapsed: %s" % (K.TERRAFORM_DESTROY_RUNNING, duration)
            self.show_progress_message(message, 1.5)

        self.erase_printed_line()
        if self.destroy_statuses.get('execution_finished') == self.current_destroy_status:
            if self.executed_with_error:
                self.show_step_finish(K.TERRAFORM_DESTROY_ERROR, write_log=False, color=self.ERROR_ANSI)
            else:
                self.show_step_finish(K.TERRAFORM_DESTROY_COMPLETED, write_log=False, color=self.GREEN_ANSI)
                end_time = datetime.now()
                self.display_process_duration(self.destroy_start_time, end_time)
