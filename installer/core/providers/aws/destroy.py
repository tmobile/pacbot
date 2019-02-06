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
    destroy_statuses = {
        "tf_destroy_start": 1,
        "tf_destroy_complete": 2,
        "execution_finished": 3
    }
    current_destroy_status = 1
    executed_with_error = False

    def __init__(self, args, input_obj):
        self.args = args
        super().__init__(input_obj)

    def execute(self, resources, terraform_with_targets, dry_run):
        error_response = self.validate_arguments(resources, terraform_with_targets)
        if not error_response:
            self._create_terraform_provider_file()
            self.run_tf_execution_and_status_threads(resources, terraform_with_targets, dry_run)
            self._delete_terraform_provider_file()
        else:
            self.exit_with_validation_errors(error_response)

    def run_tf_execution_and_status_threads(self, resources, terraform_with_targets, dry_run):
        thread1 = Thread(target=self.execute_terraform, args=(list(resources), terraform_with_targets, dry_run))
        thread2 = Thread(target=self.show_progress_status, args=(list(resources), terraform_with_targets, dry_run))

        thread1.start()
        thread2.start()

        thread1.join()
        thread2.join()

    def execute_terraform(self, resources, terraform_with_targets, dry_run):
        if not dry_run:
            try:
                self.run_pre_destoy(resources)
            except Exception as e:
                self._cleanup_destroy()
                self.executed_with_error = True
                raise e

            self.destroy_resources(resources, terraform_with_targets)
            self.run_post_destoy(resources)
            self.current_destroy_status = self.destroy_statuses.get('tf_destroy_complete')

        self._cleanup_destroy()

    def _cleanup_destroy(self):
        self.current_destroy_status = self.destroy_statuses.get('execution_finished')
        self._delete_terraform_provider_file()

    def destroy_resources(self, resources, terraform_with_targets):
        destroy_resources = resources if terraform_with_targets else None
        exception = None

        # May be timeout causes first destroy to be a failure hence attempt as many times as the value in the setting
        for attempt in range(Settings.DESTROY_NUM_ATTEMPTS):
            try:
                PyTerraform().terraform_destroy(destroy_resources)
                return
            except Exception as e:
                exception = e

        self._cleanup_destroy()
        self.executed_with_error = True
        raise Exception(exception)

    def run_pre_destoy(self, resources):
        for resource in resources:
            resource.pre_terraform_destroy()
            resource.remove_terraform()

    def run_post_destoy(self, resources):
        for resource in resources:
            resource.post_terraform_destroy()
            resource.remove_terraform()

    def show_progress_status(self, resources, terraform_with_targets, dry_run):
        done_text = "[Done]" + " " * 10 + "\n"
        len_str = str(len(resources))

        self.show_step_heading(K.TERRAFORM_DESTROY_STARTED, write_log=False)
        start_time = datetime.now()
        if dry_run:
            self.show_step_finish(K.TERRAFORM_DESTROY_DRY_RUN)
        else:
            while self.destroy_statuses.get('execution_finished') != self.current_destroy_status:
                self.show_progress_message(K.TERRAFORM_DESTROY_RUNNING, 1.5)
            self.erase_printed_line()
            if self.executed_with_error:
                self.show_step_finish(K.TERRAFORM_DESTROY_ERROR, write_log=False, color=self.ERROR_ANSI)
            else:
                self.show_step_finish(K.TERRAFORM_DESTROY_COMPLETED, write_log=False, color=self.GREEN_ANSI)
        end_time = datetime.now()
        self.display_process_duration(start_time, end_time)
