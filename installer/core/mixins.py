from abc import ABCMeta
from core.log import SysLog
from core.config import Settings
from time import sleep
from datetime import datetime
from core import constants as K
import math
import sys
import os


class MsgMixin(metaclass=ABCMeta):
    column_length = 115
    TITLE_ANSI = "\033[95m"
    BOLD_ANSI = "\033[1m"
    UNDERLINE_ANSI = "\033[4m"
    GREEN_ANSI = "\u001b[32m"
    WARN_ANSI = "\033[93m"
    ERROR_ANSI = "\033[91m"
    BERROR_ANSI = "\u001b[31;1m"
    BLUE_ANSI = "\033[94m"
    CYAN_ANSI = "\u001b[36m"
    END_ANSI = "\033[0m"
    RESET_ANSI = "\u001b[0m"
    BGREEN_ANSI = "\u001b[32;1m"
    BCYAN_ANSI = "\u001b[36;1m"
    BMAGENTA = "\u001b[35;1m"

    def show_loading_messsage(self):
        print(self.BMAGENTA)
        if os.path.exists(Settings.LOADER_FILE_PATH):
            with open(Settings.LOADER_FILE_PATH, "r") as f:
                lines = f.readlines()
            for line in lines:
                sys.stdout.write(line)
                sys.stdout.flush()
        else:
            surrounding_char = "#"
            column_length = self.column_length + 0 if len(Settings.SETUP_TITLE) < self.column_length else 20

            print(surrounding_char * column_length)
            pre_hash_count, post_hash_count = self._get_pre_and_post_char_length(Settings.SETUP_TITLE, column_length)
            print("%s %s %s" % (surrounding_char * pre_hash_count, Settings.SETUP_TITLE, surrounding_char * post_hash_count))
            if Settings.get('SETUP_DESCRIPTION', None):
                pre_hash_count, post_hash_count = self._get_pre_and_post_char_length(Settings.SETUP_DESCRIPTION, column_length)
                print("%s %s %s" % (surrounding_char * pre_hash_count, Settings.SETUP_DESCRIPTION, surrounding_char * post_hash_count))
            print(surrounding_char * column_length)

        print(self.RESET_ANSI)

    def _get_pre_and_post_char_length(self, message, column_length):
        pre_hash_count = math.ceil(int(column_length - len(message) - 2) / 2)
        post_hash_count = math.floor(int(column_length - len(message) - 2) / 2)

        return pre_hash_count, post_hash_count

    def exit_with_provider_not_found(self):
        print("Error: %s " % PROVIDER_NOT_FOUND)

    def exit_safely(self):
        sys.exit()

    def exit_with_validation_errors(self, errors):
        for key, error_list in errors.items():
            msg = '\nValidation Error. Resource %s\n' % key
            msg = msg + '\n'.join(error_list)
            print(msg)
            SysLog().write_error_log(msg, with_trace=False)

    def show_step_heading(self, heading, write_log=True):
        if write_log:
            SysLog().write_debug_log(heading)
        step_count_num = Settings.get('step_count_num', 1)
        print(self._get_heading_message_in_color("\nStep %s: %s" % (str(step_count_num), heading), self.BCYAN_ANSI))
        step_count_num = Settings.set('step_count_num', step_count_num + 1)

    def show_step_finish(self, end_heading, write_log=True, color=""):
        if write_log:
            SysLog().write_debug_log(end_heading)

        end_heading = "\t%s" % end_heading
        print(color + end_heading + self.RESET_ANSI)

    def show_step_inner_messaage(self, message, status, error_msg=None):
        dot_len = self.column_length - len(message) - 30
        print_message = "\t%s %s [%s]" % (message, self._get_line_dots_in_color(dot_len), self._get_status_in_color(status))
        SysLog().write_debug_log(print_message)
        print(print_message)

        if error_msg:
            print("\t\t%s" % self._get_error_msg_in_color(error_msg))
            SysLog().write_error_log(error_msg)

    def show_inner_inline_message(self, message, color=""):
        message = "\t%s" % message
        print(color + message + self.RESET_ANSI)

    def _get_status_in_color(self, status):
        color = ""
        if status in [K.VALID, K.FOUND, K.PRESENT, K.EXISTS]:
            color = self.GREEN_ANSI
        elif status in [K.NOT_VALID, K.NOT_FOUND, K.NOT_PRESENT, K.NOT_EXISTS]:
            color = self.ERROR_ANSI

        return color + status + self.RESET_ANSI

    def _get_line_dots_in_color(self, dot_len):
        return self.CYAN_ANSI + "." * dot_len + self.RESET_ANSI

    def _get_error_msg_in_color(self, error_msg):
        return self.ERROR_ANSI + error_msg + self.RESET_ANSI

    def _get_heading_message_in_color(self, message, color=None):
        color = self.CYAN_ANSI if color is None else color
        return color + message + self.RESET_ANSI

    def _input_message_in_color(self, message):
        return self.WARN_ANSI + message + self.RESET_ANSI

    def show_step_inner_error(self, message):
        print_message = "\t%s" % self._get_error_msg_in_color(message)
        SysLog().write_error_log(print_message)
        print(print_message)

    def show_step_inner_warning(self, message):
        print_message = "\t%s" % message
        SysLog().write_debug_log(print_message)
        print(self.WARN_ANSI + print_message + self.RESET_ANSI)

    def show_progress_start_message(self, message):
        progress_bracket = self.BGREEN_ANSI + "[.   ]" + self.RESET_ANSI
        sys.stdout.write("\r\t%s %s\b\b\b\b" % (message, progress_bracket))

    def erase_printed_line(self):
        sys.stdout.flush()
        blank_line = " " * self.column_length
        sys.stdout.write("\r%s\r" % blank_line)
        sys.stdout.flush()

    def show_progress_message(self, message, time_delay):
        self.erase_printed_line()
        self.show_progress_start_message(message)
        sys.stdout.write(self.BGREEN_ANSI)
        sys.stdout.flush()
        sleep(time_delay)
        sys.stdout.write(".")
        sys.stdout.flush()
        sleep(time_delay)
        sys.stdout.write(".")
        sys.stdout.flush()
        sleep(time_delay)
        sys.stdout.write("." + self.RESET_ANSI)
        sys.stdout.flush()
        sleep(time_delay)

    def display_op_msg(self, display_op_list):
        if display_op_list:
            result_title = "OUTPUT"
            column_length = self.column_length - 10
            pre_star_count = math.ceil(int(column_length - len(result_title) - 2) / 2)
            post_star_count = math.floor(int(column_length - len(result_title) - 2) / 2)

            heading = "\n\t%s %s %s" % ("*" * pre_star_count, result_title, "*" * post_star_count)
            print(self._get_heading_message_in_color(heading, self.BCYAN_ANSI))

            debug_log_msg = ""
            for op in display_op_list:
                for key, val in op.items():
                    key_value_msg = "\t%20s: %s" % (key, val)
                    debug_log_msg += key_value_msg + "\n"
                    print(self.GREEN_ANSI + key_value_msg + self.RESET_ANSI)

            SysLog().write_debug_log(heading + "\n" + debug_log_msg)

            end = "\t" + "*" * column_length
            print(self._get_heading_message_in_color(end, self.BCYAN_ANSI))
        print("\n")

    def stdout_flush(self):
        sys.stdout.flush()

    def get_duration(self, time_delta):
        duration = datetime(1, 1, 1) + time_delta
        return "%sm %ss" % (duration.minute, duration.second)

    def display_process_duration(self, start_time, end_time, step=True):
        time_delta = end_time - start_time
        duration = self.get_duration(time_delta)
        message = "\t" if step else ""
        message = self.CYAN_ANSI + message + "Completed in %s" % duration + self.RESET_ANSI
        print(message)

    def warn_another_process_running(self):
        message = self.BERROR_ANSI + K.ANOTHER_PROCESS_RUNNING + self.RESET_ANSI

        print("\t%s\n" % message)
