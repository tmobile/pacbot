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
import logging
import os
import sys


class Install(BaseAction):
    """
    AWS provider for destroy command

    Attributes:
        executed_with_error (boolean): this is set to True if any error occurs
        FOLDER_EXISTS_ERROR_NO (int): Error number of folder creation failure
        install_statuses (dict): Available destroy statuses
        terraform_thread (thread): Install python thread
        terraform_outputs (dict): Terraform output dict
        current_install_status (int): Current install status
    """
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
    terraform_thread = None

    def __init__(self, input_obj, check_dependent_resources=True):
        self.check_dependent_resources = check_dependent_resources
        super().__init__(input_obj)
        logging.disable(logging.ERROR)  # To disable python terraform unwanted warnings

    def execute(self, resources, terraform_with_targets, dry_run):
        """
        This is the starting method where install begins. This is the actual method called from the main install class

        Args:
            resources (list): Resources to be installed
            terraform_with_targets (boolean): If partial install is to be done (if --tags is supplied)
            dry_run (boolean): Decides whether original install should be done
        """
        error_response = self.validate_arguments(resources, terraform_with_targets)
        if error_response:
            return self.exit_with_validation_errors(error_response)

        if self.validate_resources(resources):
            self.generate_terraform_files(resources, terraform_with_targets)
            self.run_tf_execution_and_status_threads(resources, terraform_with_targets, dry_run)

            if not self.executed_with_error:
                self.render_resource_outputs(resources)
            else:
                raise self.exception

    def run_tf_execution_and_status_threads(self, resources, terraform_with_targets, dry_run):
        """
        Creates 2 thread
            1. For actualy installation
            2. For displaying the status of installation
        Since python is interpreted language we need to create threads to display the status in one and actual process in another

        Args:
            resources (list): Resources to be installed
            terraform_with_targets (boolean): If partial install is to be done (if --tags is supplied)
            dry_run (boolean): Decides whether original install should be done
        """
        self.terraform_thread = Thread(target=self.create_resources, args=(list(resources), terraform_with_targets, dry_run))
        progressbar_thread = Thread(target=self.show_progress_status, args=(list(resources), terraform_with_targets, dry_run))

        self.terraform_thread.start()
        progressbar_thread.start()

        self.terraform_thread.join()
        progressbar_thread.join()

    def create_resources(self, resources, terraform_with_targets, dry_run):
        """
        Start installing the resources by calling PyTerraform class destroy

        Args:
            resources (list): Resources to be created
            terraform_with_targets (boolean): If partial install is to be done (if --tags is supplied)
            dry_run (boolean): Decides whether original install should be done
        """
        try:
            self.terraform_apply(resources, terraform_with_targets, dry_run)
        except Exception as e:
            self.executed_with_error = True
            self.exception = e

        self._cleanup_installation_process(dry_run)

    def _cleanup_installation_process(self, dry_run):
        """
        Cleanup the process of installation once it is completed

        Args:
            dry_run (boolean): Decides whether original install should be done
        """
        py_terraform = PyTerraform()

        self.terraform_outputs = py_terraform.save_terraform_output()

        self._delete_terraform_provider_file()
        self.current_install_status = self.install_statuses.get('execution_finished')

    def generate_terraform_files(self, resources, terraform_with_targets):
        """
        Generate terraform files for the resources passed

        Args:
            resources (list): Resources to be created
            terraform_with_targets (boolean): If partial install is to be done (if --tags is supplied)
            dry_run (boolean): Decides whether original install should be done
        """
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
        """
        Call terraform apply command through PyTerraform class

        Args:
            resources (list): Resources to be created
            terraform_with_targets (boolean): If partial install is to be done (if --tags is supplied)
            dry_run (boolean): Decides whether original install should be done
        """
        apply_resources = resources if terraform_with_targets else None
        py_terraform = PyTerraform()

        self.current_install_status = self.install_statuses.get('tf_init_start')
        py_terraform.terraform_init()
        self.current_install_status = self.install_statuses.get('tf_init_complete')

        self.current_install_status = self.install_statuses.get('tf_plan_start')
        response = py_terraform.terraform_plan(apply_resources)
        self._set_resource_creation_count(response)
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

    def _set_resource_creation_count(self, plan_response):
        """
        Set resources craetion count from terraform plan

        Args:
            resources (list): Resources created
        """
        to_add = to_change = 0
        try:
            lines = plan_response[1].split("\n")
            for line in lines:
                if "Plan:" in line and "to add" in line and "to change" in line:  # This needs to be changed with reqular expression
                    req_str = line.split("Plan:")[1].strip()
                    to_add = int(req_str.split("to add,")[0].strip())
                    to_change = int(req_str.split("to add,")[1].strip().split("to change,")[0].strip())
                    break
        except Exception as e:
            return

        self.total_resources_count = to_add + to_change

    def render_resource_outputs(self, resources):
        """
        After installation is completed list down all the outputs to be rendered by calling render_output hook

        Args:
            resources (list): Resources created
        """
        if not self.executed_with_error and self.terraform_outputs:
            display_op_list = []
            for resource in resources:
                output = resource.render_output(self.terraform_outputs)
                if output:
                    display_op_list.append(output)

            self.display_op_msg(display_op_list)

    def _create_terraform_support_dirs(self):
        """Create all support directories and files required for installation"""
        scripts_and_files_dir = get_terraform_scripts_and_files_dir()
        try:
            os.mkdir(scripts_and_files_dir)
        except OSError as e:
            if e.errno != self.FOLDER_EXISTS_ERROR_NO:
                print("Creation of the directory %s failed" % scripts_and_files_dir)
                raise Exception('Files direcotry creation in Terraform folder failed')

    def _copy_supporting_files(self):
        """Copy all the files from main root file directory and copy them in scripts and files directory inside terraform dir"""
        scripts_and_files_dir = get_terraform_scripts_and_files_dir()
        copy_tree(Settings.PROVISIONER_FILES_DIR_TO_COPY, scripts_and_files_dir)

    def show_progress_status(self, resources, terraform_with_targets, dry_run):
        """
        Show the status of installation continously in this thread

        Args:
            resources (list): Resources to be created
            terraform_with_targets (boolean): If partial install is to be done (if --tags is supplied)
            dry_run (boolean): Decides whether original install should be done
        """
        self.render_terraform_init_progress()
        self.render_terraform_plan_progress()
        if not dry_run:
            self.render_terraform_apply_progress(resources, terraform_with_targets)
        else:
            message = "\n" + self.WARN_ANSI + K.TERRAFORM_APPLY_DRY_RUN + self.END_ANSI + "\n"
            self.show_step_finish(message, write_log=False)

    def render_terraform_init_progress(self):
        """Show the status of terraform init command execution"""
        start_time = datetime.now()
        self.show_step_heading(K.TERRAFORM_INIT_STARTED, write_log=False)
        while self.install_statuses.get('tf_init_complete') >= self.current_install_status and self.terraform_thread.isAlive():
            self.show_progress_message(K.TERRAFORM_INIT_RUNNING, 0.5)

        self._render_step_trail_message(K.TERRAFORM_INIT_COMPLETED, K.EXECUTED_WITH_ERROR, start_time)

    def render_terraform_plan_progress(self):
        """Show status of terraform plan command execution"""
        # If Init doesn't end up in error
        if not self.executed_with_error and self.terraform_thread.isAlive():
            start_time = datetime.now()
            self.show_step_heading(K.TERRAFORM_PLAN_STARTED, write_log=False)
            while self.install_statuses.get('tf_plan_complete') >= self.current_install_status and self.terraform_thread.isAlive():
                self.show_progress_message(K.TERRAFORM_PLAN_RUNNING, 0.7)

            self._render_step_trail_message(K.TERRAFORM_PLAN_COMPLETED, K.EXECUTED_WITH_ERROR, start_time)

    def render_terraform_apply_progress(self, resources, terraform_with_targets):
        """
        Show the status of actualy terraform apply

        Args:
            resources (list): Resources to be created
            terraform_with_targets (boolean): If partial install is to be done (if --tags is supplied)
        """
        counter = False
        # If Plan doesn't end up in error
        if not self.executed_with_error and self.terraform_thread.isAlive():
            start_time = datetime.now()
            self.show_step_heading(K.TERRAFORM_APPLY_STARTED, write_log=False)
            py_terraform = PyTerraform()
            output_count = prev_output_count = 0

            while self.install_statuses.get('execution_finished') > self.current_install_status and self.terraform_thread.isAlive():
                counter = False if counter else True
                duration = self.CYAN_ANSI + self.get_duration(datetime.now() - start_time) + self.END_ANSI
                if counter:
                    try:
                        # output_count = len(py_terraform.load_terraform_output())  # This uses terraform output command
                        output_count = self.files_count_in_output_status_dir() - 1
                        prev_output_count = output_count
                    except:
                        output_count = prev_output_count
                else:
                    output_count = prev_output_count

                duration_msg = ", Time elapsed: %s" % duration
                count_msg = self.GREEN_ANSI + str(output_count) + "/" + str(self.total_resources_count) + self.END_ANSI
                message = "Resources created: " + count_msg + duration_msg
                self.show_progress_message(message, 1.5)

            self.clear_status_dir_files()
            self._render_step_trail_message(K.TERRAFORM_APPLY_COMPLETED, K.EXECUTED_WITH_ERROR, start_time)

    def _render_step_trail_message(self, success_msg, error_msg, start_time):
        """
        At the end of installation show the status of the execution

        Args:
            success_msg (str): Success message to be displayed if isntallation gets completed
            error_msg (str): Error messaage if isntallation is not successful
            start_time (time): Time at which the Apply started
        """
        if self.executed_with_error:
            mesage, color, write_log = error_msg, self.ERROR_ANSI, True
        else:
            mesage, color, write_log = success_msg, self.GREEN_ANSI, False

        self.erase_printed_line()
        self.show_step_finish(mesage, write_log=write_log, color=color)
        end_time = datetime.now()
        self.display_process_duration(start_time, end_time)
