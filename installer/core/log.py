from core.config import Settings
from datetime import datetime
from core import constants as K
import os
import traceback
import sys


class SysLog:
    """
    Main logger to log the execution process at different stages in specific log files

    Attributes:
        line_lenght (int): Starting/Ending messaage length
        error_log (path): Path of the error log file
        debug_log (path): Path of the debug log file
        terraform_install_log (path): Path of terraform apply log file
        terraform_destroy_log (path): Path of terraform destroy log file
    """
    line_lenght = 100

    def __init__(self):
        self.error_log = os.path.join(Settings.LOG_DIR, 'error.log')
        self.debug_log = os.path.join(Settings.LOG_DIR, 'debug.log')
        self.terraform_install_log = os.path.join(Settings.LOG_DIR, 'terraform_install.log')
        self.terraform_destroy_log = os.path.join(Settings.LOG_DIR, 'terraform_destroy.log')

    def write_error_log(self, msg, with_trace=True):
        """
        Write message with traceback to error log file

        Args:
            msg (str): Error message
            with_trace (Boolean): Decides whether to add trace to the log file
        """
        with open(self.error_log, 'a+') as logfile:
            logfile.write('Running Command: %s\n' % Settings.running_command)
            if with_trace:
                traceback.print_exc(file=logfile)
            msg += '\nError Message: %s\n%s\n' % (msg, '-' * 50)
            logfile.write(msg)

        self.write_debug_log(msg)

    def write_debug_log(self, msg):
        """
        Write debug message to debug log file

        Args:
            msg (str): Debug message
        """
        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        with open(self.debug_log, 'a+') as logfile:
            logfile.write("%s: %s\n" % (now, msg))

    def debug_started(self, command):
        """
        Write debug start message to debug log file

        Args:
            command (str): Command name supplied for the execution
        """
        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        lines = [
            "%s\n" % ("*" * self.line_lenght),
            "Command: %s\n" % command,
            "DateTime: %s\n" % now,
            "%s\n" % ("*" * self.line_lenght)
        ]

        with open(self.debug_log, 'a+') as logfile:
            logfile.writelines(lines)

    def write_terraform_init_log(self, response):
        """
        Write terraform init command response to install log

        Args:
            response (str): Response of terraform init command
        """
        head_msg = "Terraform Init is done"
        with open(self.terraform_install_log, 'a+') as logfile:
            logfile.write(self._write_header(head_msg))
            logfile.write(response[1])

        self.write_debug_log(K.TERRAFORM_INIT_COMPLETED)

    def write_terraform_plan_log(self, response):
        """
        Write terraform plan command response to install log

        Args:
            response (str): Response of terraform plan command
        """
        head_msg = "Terraform Plan is done"
        with open(self.terraform_install_log, 'a+') as logfile:
            logfile.write(self._write_header(head_msg))
            logfile.write(response[1])

        self.write_debug_log(K.TERRAFORM_PLAN_COMPLETED)

    def write_terraform_apply_log_header(self, header=None):
        """
        Write terraform apply command response to install log

        Args:
            response (str): Response of terraform apply command
        """
        with open(self.terraform_install_log, 'a+') as logfile:
            logfile.write("*" * 100)
            logfile.write("\n*** Terraform Apply Started")
            logfile.write("\nDateTime: %s\n" % datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
            logfile.write("*" * 100)
        header = header if header else K.TERRAFORM_APPLY_STARTED
        self.write_debug_log(header)

    def write_terraform_destroy_log_header(self, header=None):
        """
        Write terraform destroy command response to destroy log

        Args:
            response (str): Response of terraform destroy command
        """
        with open(self.terraform_destroy_log, 'a+') as logfile:
            logfile.write("*" * 100)
            logfile.write("\n*** Terraform Destroy  Started ***")
            logfile.write("\nDateTime: %s\n" % datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
            logfile.write("*" * 100)
        header = header if header else K.TERRAFORM_DESTROY_STARTED
        self.write_debug_log(header)

    def _write_header(self, head_msg=None):
        """
        Return the write_header message for printing title of the log

        Args:
            head_msg (str): Message to be written if any

        Returns:
            header (str): A text to be printed on the header
        """
        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        header = "\n%s\nDateTime: %s \nMessage: %s \n" % ("*" * 100, now, head_msg)

        return header

    def get_terraform_install_log_file(self):
        """
        Return terraform install log file path

        Returns:
            terraform_install_log (path): Terraform install log file path
        """
        return self.terraform_install_log

    def get_terraform_destroy_log_file(self):
        """
        Return terraform destroy log file path

        Returns:
            terraform_destroy_log (path): Terraform destroy log file path
        """
        return self.terraform_destroy_log
