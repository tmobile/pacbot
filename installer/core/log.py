from core.config import Settings
from datetime import datetime
from core import constants as K
import os
import traceback
import sys


class SysLog:
    line_lenght = 100

    def __init__(self):
        self.error_log = os.path.join(Settings.LOG_DIR, 'error.log')
        self.debug_log = os.path.join(Settings.LOG_DIR, 'debug.log')
        self.terraform_install_log = os.path.join(Settings.LOG_DIR, 'terraform_install.log')
        self.terraform_destroy_log = os.path.join(Settings.LOG_DIR, 'terraform_destroy.log')

    def write_error_log(self, msg, with_trace=True):

        with open(self.error_log, 'a+') as logfile:
            logfile.write('Running Command: %s\n' % Settings.running_command)
            if with_trace:
                traceback.print_exc(file=logfile)
            msg += '\nError Message: %s\n%s\n' % (msg, '-' * 50)
            logfile.write(msg)

        self.write_debug_log(msg)

    def write_debug_log(self, msg):
        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        with open(self.debug_log, 'a+') as logfile:
            logfile.write("%s: %s\n" % (now, msg))

    def debug_started(self, command):
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
        head_msg = "Terraform Init is done"
        with open(self.terraform_install_log, 'a+') as logfile:
            logfile.write(self._write_header(head_msg))
            logfile.write(response[1])

        self.write_debug_log(K.TERRAFORM_INIT_COMPLETED)

    def write_terraform_plan_log(self, response):
        head_msg = "Terraform Plan is done"
        with open(self.terraform_install_log, 'a+') as logfile:
            logfile.write(self._write_header(head_msg))
            logfile.write(response[1])

        self.write_debug_log(K.TERRAFORM_PLAN_COMPLETED)

    def write_terraform_apply_log_header(self):
        with open(self.terraform_install_log, 'a+') as logfile:
            logfile.write("*" * 100)
            logfile.write("\n*** Terraform Apply Started")
            logfile.write("\nDateTime: %s\n" % datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
            logfile.write("*" * 100)
        self.write_debug_log(K.TERRAFORM_APPLY_STARTED)

    def write_terraform_destroy_log_header(self):
        with open(self.terraform_destroy_log, 'a+') as logfile:
            logfile.write("*" * 100)
            logfile.write("\n*** Terraform Destroy  Started ***")
            logfile.write("\nDateTime: %s\n" % datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
            logfile.write("*" * 100)
        self.write_debug_log(K.TERRAFORM_DESTROY_STARTED)

    def _write_header(self, head_msg=None):
        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        header = "\n%s\nDateTime: %s \nMessage: %s \n" % ("*" * 100, now, head_msg)

        return header

    def get_terraform_install_log_file(self):
        return self.terraform_install_log

    def get_terraform_destroy_log_file(self):
        return self.terraform_destroy_log
