from core.config import Settings
from core.providers.aws import BaseAction
from core.log import SysLog
from core import constants as K
from core.terraform import PyTerraform
from core.terraform.utils import get_terraform_scripts_and_files_dir
from time import sleep
from distutils.dir_util import copy_tree
from threading import Thread
from datetime import datetime
from core.utils import exists_teraform_lock
import importlib
import os
import sys


class Install(BaseAction):
    FOLDER_EXISTS_ERROR_NO = 17
    executed_with_error = False
    install_statuses = {
        "tf_init_start": 1,
        "tf_init_complete": 2,
        "tf_plan_start": 3,
        "tf_plan_complete": 4,
        "tf_apply_start": 5,
        "tf_apply_complete": 6,
        "execution_finished": 10
    }
    current_install_status = 1
    terraform_outputs = {}

    def __init__(self, args, input_obj, check_dependent_resources=True):
        self.args = args
        self.check_dependent_resources = check_dependent_resources
        super().__init__(input_obj)

    def execute(self, resources, terraform_with_targets, dry_run):
        error_response = self.validate_arguments(resources, terraform_with_targets)
        if error_response:
            return self.exit_with_validation_errors(error_response)

        if self.validate_resources(resources):
            self.generate_terraform_files(resources, terraform_with_targets)
            self.run_tf_execution_and_status_threads(resources, terraform_with_targets, dry_run)

            if not self.executed_with_error:
                self.render_resource_outputs(resources)

    def run_tf_execution_and_status_threads(self, resources, terraform_with_targets, dry_run):
        thread1 = Thread(target=self.execute_terraform, args=(list(resources), terraform_with_targets, dry_run))
        thread2 = Thread(target=self.show_progress_status, args=(list(resources), terraform_with_targets, dry_run))

        thread1.start()
        thread2.start()

        thread1.join()
        thread2.join()

    def execute_terraform(self, resources, terraform_with_targets, dry_run):
        try:
            self.terraform_apply(resources, terraform_with_targets, dry_run)
            self._cleanup_execute_terraform(dry_run)
        except Exception as e:
            self._cleanup_execute_terraform(dry_run, e)

    def _cleanup_execute_terraform(self, dry_run, exception=None):
        py_terraform = PyTerraform()

        if not dry_run:
            self.terraform_outputs = py_terraform.save_terraform_output()

        self._delete_terraform_provider_file()
        self.current_install_status = self.install_statuses.get('execution_finished')
        if exception:
            self.executed_with_error = True
            raise exception

    def generate_terraform_files(self, resources, terraform_with_targets):
        if exists_teraform_lock():
            self.warn_another_process_running()
            raise Exception(K.ANOTHER_PROCESS_RUNNING)

        self.show_step_heading(K.TERRAFORM_GEN_STARTED)

        for resource in resources:
            try:
                resource.pre_generate_terraform()
            except Exception as e:
                self.show_step_inner_error(str(e))
                raise Exception(e)

        # If all resources need to be rebuild, then need to destroy those who have been removed from code
        if not terraform_with_targets:
            self._delete_all_terraform_files()

        self._create_terraform_support_dirs()
        for resource in resources:
            resource.generate_terraform()

        self._create_terraform_provider_file()
        self._copy_supporting_files()
        self.show_step_finish(K.TERRAFORM_GEN_COMPLETED, color=self.GREEN_ANSI)

    def terraform_apply(self, resources, terraform_with_targets, dry_run):
        apply_resources = resources if terraform_with_targets else None
        py_terraform = PyTerraform()

        self.current_install_status = self.install_statuses.get('tf_init_start')
        py_terraform.terraform_init()
        self.current_install_status = self.install_statuses.get('tf_init_complete')

        self.current_install_status = self.install_statuses.get('tf_plan_start')
        py_terraform.terraform_plan(apply_resources)
        self.current_install_status = self.install_statuses.get('tf_plan_complete')

        for resource in resources:
            resource.pre_terraform_apply()

        if not dry_run:
            self.current_install_status = self.install_statuses.get('tf_apply_start')
            py_terraform.terraform_apply(apply_resources)
            self.current_install_status = self.install_statuses.get('tf_apply_complete')
            self.terraform_outputs = py_terraform.save_terraform_output()  # Save Output if there is no exception
            for resource in resources:
                resource.post_terraform_apply()

    def render_resource_outputs(self, resources):
        if not self.executed_with_error and self.terraform_outputs:
            display_op_list = []
            for resource in resources:
                output = resource.render_output(self.terraform_outputs)
                if output:
                    display_op_list.append(output)

            self.display_op_msg(display_op_list)

    def _create_terraform_support_dirs(self):
        scripts_and_files_dir = get_terraform_scripts_and_files_dir()
        try:
            os.mkdir(scripts_and_files_dir)
        except OSError as e:
            if e.errno != self.FOLDER_EXISTS_ERROR_NO:
                print("Creation of the directory %s failed" % scripts_and_files_dir)
                raise Exception('Files direcotry creation in Terraform folder failed')

    def _copy_supporting_files(self):
        scripts_and_files_dir = get_terraform_scripts_and_files_dir()
        copy_tree(Settings.PROVISIONER_FILES_DIR_TO_COPY, scripts_and_files_dir)

    def show_progress_status(self, resources, terraform_with_targets, dry_run):
        self.render_terraform_init_progress()
        self.render_terraform_plan_progress()
        self.render_terraform_apply_progress(resources, terraform_with_targets, dry_run)

    def render_terraform_init_progress(self):
        start_time = datetime.now()
        self.show_step_heading(K.TERRAFORM_INIT_STARTED, write_log=False)
        while self.install_statuses.get('tf_init_complete') >= self.current_install_status:
            self.show_progress_message(K.TERRAFORM_INIT_RUNNING, 0.5)
        self.erase_printed_line()
        if self.executed_with_error:
            self.show_step_finish(K.EXECUTED_WITH_ERROR, color=self.ERROR_ANSI)
        else:
            self.show_step_finish(K.TERRAFORM_INIT_COMPLETED, write_log=False, color=self.GREEN_ANSI)
        end_time = datetime.now()
        self.display_process_duration(start_time, end_time)

    def render_terraform_plan_progress(self):
        # If Init doesn't end up in error
        if not self.executed_with_error:
            start_time = datetime.now()
            self.show_step_heading(K.TERRAFORM_PLAN_STARTED, write_log=False)
            while self.install_statuses.get('tf_plan_complete') >= self.current_install_status:
                self.show_progress_message(K.TERRAFORM_PLAN_RUNNING, 0.7)
            self.erase_printed_line()
            if self.executed_with_error:
                self.show_step_finish(K.EXECUTED_WITH_ERROR, color=self.ERROR_ANSI)
            else:
                self.show_step_finish(K.TERRAFORM_PLAN_COMPLETED, write_log=False, color=self.GREEN_ANSI)
            end_time = datetime.now()
            self.display_process_duration(start_time, end_time)

    def render_terraform_apply_progress(self, resources, terraform_with_targets, dry_run):
        # If Plan doesn't end up in error
        if not self.executed_with_error:
            start_time = datetime.now()
            self.show_step_heading(K.TERRAFORM_APPLY_STARTED, write_log=False)
            if dry_run:
                self.show_step_finish(K.TERRAFORM_APPLY_DRY_RUN, write_log=False)
            else:
                while self.install_statuses.get('execution_finished') > self.current_install_status:
                    self.show_progress_message(K.TERRAFORM_APPLY_RUNNING, 1.5)
                self.erase_printed_line()
                if self.executed_with_error:
                    self.show_step_finish(K.EXECUTED_WITH_ERROR, color=self.ERROR_ANSI)
                else:
                    self.show_step_finish(K.TERRAFORM_APPLY_COMPLETED, write_log=False, color=self.GREEN_ANSI)
            end_time = datetime.now()
            self.display_process_duration(start_time, end_time)
