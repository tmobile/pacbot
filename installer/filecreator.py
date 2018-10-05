#!/usr/bin/env python
import jsonRead
import re
import os
import sys


def file_replace(accountid):
    with open("fetch_and_run.input", "rt") as input_sh:
        with open("./container/fetch_and_run.sh", "wt") as output_sh:
            for line in input_sh:
                line = line.replace("pacman-data072018", jsonRead._get_s3_bucket_name() + "-" + accountid)
                output_sh.write(line)
        output_sh.close()
        input_sh.close()


def _api_file_replace(accountid):
    with open("entrypoint.input", "rt") as input_sh:
        with open("./container/api/entrypoint.sh", "wt") as output_sh:
            for line in input_sh:
                line = line.replace("pacman-data072018", jsonRead._get_s3_bucket_name() + "-" + accountid)
                output_sh.write(line)
        output_sh.close()
        input_sh.close()


def _ui_file_replace(accountid):
    with open("entrypoint-ui.input", "rt") as input_sh:
        with open("./container/ui/entrypoint.sh", "wt") as output_sh:
            for line in input_sh:
                line = line.replace("pacman-data072018", jsonRead._get_s3_bucket_name() + "-" + accountid)
                output_sh.write(line)
        output_sh.close()
        input_sh.close()


def _create_tfvars_file():
    with open("rule_engine_cloudwatch_rule.json", "r") as input_file:
        with open("terraform.tfvars", "w") as output_file:
            for line in input_file:
                line = line.replace(": ", "=", 1)
                if re.match("^\[", line):
                    line = line.replace("[", "rules = [")
                if "ruleParams" in line:
                    line = line.replace("role/pacman_ro", "role/" + jsonRead._get_client_account_role_name())
                output_file.write(line)


def get_logfile_path():
    log_dir = "/var/log/pacman"
    try:
        log_filename = "pacman-" + sys.argv[1].strip().lower() + ".log"
    except:
        log_filename = "pacman-test.log"

    if not os.path.exists(log_dir):
        os.mkdir(log_dir)

    return os.path.join(log_dir, log_filename)


def create_pacman_log_file_handler():
    return open(get_logfile_path(), "a+")


def flush_logfile():
    open(get_logfile_path(), "w").close()
