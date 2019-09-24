from core.config import Settings
from core.providers.aws import Install
from core import constants as K
from core.terraform import PyTerraform
from threading import Thread
import os
import sys


class ReInstall(Install):
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
        self.terraform_thread = Thread(target=self.re_create_resources, args=(list(resources), terraform_with_targets, dry_run))
        progressbar_thread = Thread(target=self.show_progress_status, args=(list(resources), terraform_with_targets, dry_run))

        self.terraform_thread.start()
        progressbar_thread.start()

        self.terraform_thread.join()
        progressbar_thread.join()

    def re_create_resources(self, resources, terraform_with_targets, dry_run):
        """
        Start installing the resources by calling PyTerraform class destroy

        Args:
            resources (list): Resources to be created
            terraform_with_targets (boolean): If partial install is to be done (if --tags is supplied)
            dry_run (boolean): Decides whether original install should be done
        """
        try:
            if not dry_run:
                PyTerraform().terraform_destroy(destroy_resources)
                self.run_post_destoy(resources)

            self.terraform_apply(resources, terraform_with_targets, dry_run)
        except Exception as e:
            self.executed_with_error = True
            self.exception = e

        self._cleanup_installation_process(dry_run)
