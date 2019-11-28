from core.config import Settings
from core.providers.aws.install import Install
from core import constants as K
from core.terraform import PyTerraform
from threading import Thread
from datetime import datetime
import os
import sys


class ReInstall(Install):  # Do not inherit Destroy
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
    destroy = False
    exception = None

    def execute(self, resources_to_destroy, resources_to_install, terraform_with_targets, dry_run):
        """
        This is the starting method where install begins. This is the actual method called from the main install class

        Args:
            resources (list): Resources to be installed
            terraform_with_targets (boolean): If partial install is to be done (if --tags is supplied)
            dry_run (boolean): Decides whether original install should be done
        """
        self.generate_terraform_files(resources_to_install, terraform_with_targets)
        self.run_tf_execution_and_status_threads(resources_to_destroy, resources_to_install, terraform_with_targets, dry_run)

        if not self.executed_with_error:
            self.render_resource_outputs(resources_to_install)
        else:
            raise self.exception

    def run_tf_execution_and_status_threads(self, resources_to_destroy, resources_to_install, terraform_with_targets, dry_run):
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
        self.terraform_thread = Thread(
            target=self.re_create_resources,
            args=(list(resources_to_destroy), list(resources_to_install), terraform_with_targets, dry_run)
        )
        progressbar_thread = Thread(target=self.show_progress_status_all, args=(list(resources_to_install), terraform_with_targets, dry_run))

        self.terraform_thread.start()
        progressbar_thread.start()

        self.terraform_thread.join()
        progressbar_thread.join()

    def re_create_resources(self, resources_to_destroy, resources_to_install, terraform_with_targets, dry_run):
        """
        Start installing the resources by calling PyTerraform class destroy

        Args:
            resources (list): Resources to be created
            terraform_with_targets (boolean): If partial install is to be done (if --tags is supplied)
            dry_run (boolean): Decides whether original install should be done
        """
        try:
            if not dry_run:
                PyTerraform().terraform_destroy(resources_to_destroy)
            self.destroy = True
            self.terraform_apply(resources_to_install, terraform_with_targets, dry_run)
        except Exception as e:
            self.executed_with_error = True
            self.exception = e
            self.destroy = True  # If there is any error in destroy set destroy to True

        self._cleanup_installation_process(dry_run)

    def show_progress_status_all(self, resources, terraform_with_targets, dry_run):
        """
        Show the status of installation continously in this thread

        Args:
            resources (list): Resources to be created
            terraform_with_targets (boolean): If partial install is to be done (if --tags is supplied)
            dry_run (boolean): Decides whether original install should be done
        """
        self.render_terraform_destroy_progress()  # Show destroy progress
        self.show_progress_status(resources, terraform_with_targets, dry_run)  # Show install progress

    def render_terraform_destroy_progress(self):
        """Show the status of terraform init command execution"""
        self.show_step_heading(K.TERRAFORM_REDEPLOY_DESTROY_STARTED, write_log=False)
        start_time = datetime.now()
        while self.destroy is False and self.terraform_thread.isAlive():
            duration = self.CYAN_ANSI + self.get_duration(datetime.now() - start_time) + self.END_ANSI
            message = "Time elapsed: %s" % duration
            self.show_progress_message(message, 1.5)
        end_time = datetime.now()
        self.erase_printed_line()
        if self.exception:
            self.show_step_finish(K.TERRAFORM_DESTROY_ERROR, write_log=False, color=self.ERROR_ANSI)
        else:
            self.show_step_finish(K.TERRAFORM_REDEP_DESTROY_COMPLETED, write_log=False, color=self.GREEN_ANSI)

        self.display_process_duration(start_time, end_time)
