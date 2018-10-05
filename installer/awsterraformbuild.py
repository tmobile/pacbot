#!/usr/bin/env python
import subprocess
from python_terraform import *
import network
import json
import container.handler
import filecreator
import os
import varsdata
import jsonRead
import checkresources
import warnings
from threading import Thread

pacman_installation = filecreator.create_pacman_log_file_handler()


def _create_aws_resources(aws_access_key, aws_secret_key, region):
    warnings.simplefilter("ignore")
    warnings.simplefilter("error")
    varsmap = {}
    threads = []
    rc = 0
    filecreator.flush_logfile()
    for count, resource in enumerate(varsdata._get_execution_order(), start=0):
        if resource == "build-ui":  # For build ui just call the function and dont do anything else
            jsonRead._build_ui_apps(aws_access_key, aws_secret_key, region)
            continue

        varsmap = varsdata._get_terraform_map(resource, "")
        if varsmap is None:
            continue
        print "Creating ", resource
        varsmap.update({'aws_access_key': aws_access_key, 'aws_secret_key': aws_secret_key, 'region': region})
        terraform = ''
        to_be_created = False
        try:
            terraform = Terraform(working_dir='./terraform/' + resource)
            terraform.init()
        except ValueError as ve:
            os.remove("./terraform/" + resource + "/terraform.tfstate")
            terraform = Terraform(working_dir='./terraform/'+resource)
            terraform.init()
        if resource in ("es", "rds", "redshift"):
            rc += 1
        if count != 2 and checkresources._check_resource(aws_access_key, aws_secret_key, region, resource, terraform) is True:
            if resource in ("es", "rds", "redshift"):
                if len(threads) == 0:
                    continue
            else:
                continue
        else:
            to_be_created = True
        if resource == "batch":
            network.create_KeyPair(
                region, aws_access_key, aws_secret_key, jsonRead._get_key_name(), jsonRead._get_file_name()
            )
            filecreator.file_replace(jsonRead._get_base_accountid())
            container.handler._create_ecr_image_push(
                region, aws_access_key, aws_secret_key, './container', jsonRead._get_batch_repo(), pacman_installation
            )
        elif resource == "oss-api":
            filecreator._api_file_replace(jsonRead._get_base_accountid())
            container.handler._create_ecr_image_push(
                region, aws_access_key, aws_secret_key, './container/api', jsonRead._get_api_repo(), pacman_installation
            )
        elif resource == "oss-ui":
            filecreator._ui_file_replace(jsonRead._get_base_accountid())
            container.handler._create_ecr_image_push(
                region, aws_access_key, aws_secret_key, './container/ui', jsonRead._get_ui_repo(), pacman_installation
            )
        response = terraform.plan(refresh=False, capture_output=True, input=False, var=varsmap)
        if count == 2:
            varsmap.update({'check': 0})
        approve = {"auto_approve": True, "var": varsmap}
        if resource in ("es", "rds", "redshift"):
            if to_be_created:
                threads.append(Thread(target=_create_or_destroy, args=(terraform, "install", resource, approve,)))
                to_be_created = False
            if rc == 3:
                for thread in threads:
                    thread.start()
                for thread in threads:
                    thread.join()
        else:
            response = terraform.apply(**approve)
            _logs_display(response)
            print resource, " creation completed"
            response = terraform.output()
            try:
                value = response['pacman']['value']
                if response is not None:
                    jsonRead._write_json(resource, value)
            except TypeError as e:
                continue
            except KeyError as ke:
                continue

    append_ui_url_and_auth_details_to_log()


def _create_or_destroy(terraform, action, resource, approve):
    if action == "install":
        response = terraform.apply(**approve)
        print resource, " creation completed"
        _logs_display(response)
        try:
            response = terraform.output()
            value = response['pacman']['value']
            if response is not None:
                jsonRead._write_json(resource, value)
        except TypeError as e:
            pass
        except KeyError as ke:
            pass
    elif action == "destroy":
        response = terraform.destroy(**approve)
        _logs_display(response)
        try:
            os.remove("./terraform/"+resource+"/terraform.tfstate")
            os.remove("./terraform/"+resource+"/terraform.tfstate.backup")
        except OSError as ose:
            pass


def _destroy_aws_resources(aws_access_key, aws_secret_key, region):
    filecreator.flush_logfile()
    threads = []
    for resource in list(reversed(varsdata._get_execution_order())):
        varsmap = varsdata._get_terraform_map(resource, "destroy")
        if varsmap is None:
            continue
        if resource == "build-ui":
            continue
        print "Deleting ", resource
        varsmap.update({'aws_access_key': aws_access_key, 'aws_secret_key': aws_secret_key, 'region': region})
        terraform = Terraform(working_dir='./terraform/' + resource)
        terraform.init()
        approve = {"auto_approve": True, "var": varsmap}
        if resource in ("es", "rds", "redshift"):
            threads.append(Thread(target=_create_or_destroy, args=(terraform, "destroy", resource, approve,)))
            if len(threads) == 3:
                for thread in threads:
                    thread.start()
                for thread in threads:
                    thread.join()
        else:
            response = terraform.destroy(**approve)
            _logs_display(response)
            if resource == "batch":
                container.handler.delete_repo(
                    region, aws_access_key, aws_secret_key, jsonRead._get_batch_repo(), pacman_installation
                )
            elif resource == "oss-api":
                container.handler.delete_repo(
                    region, aws_access_key, aws_secret_key, jsonRead._get_api_repo(), pacman_installation
                )
            elif resource == "oss-ui":
                container.handler.delete_repo(
                    region, aws_access_key, aws_secret_key, jsonRead._get_ui_repo(), pacman_installation
                )
            try:
                os.remove("./terraform/"+resource+"/terraform.tfstate")
                os.remove("./terraform/"+resource+"/terraform.tfstate.backup")
            except OSError as ose:
                continue


def add_quote(string):
    return '"{0}"'.format(string)


def _logs_display(logdetail):
    _json_output_ = ""
    if len(logdetail) > 1:
        _info = logdetail[0]
        _log_info = logdetail[1]
        if len(logdetail[1]) == 0 and len(logdetail) == 3:
            _log_info = logdetail[2]

        pacman_installation.write(_log_info)


def append_ui_url_and_auth_details_to_log():
    ui_url = jsonRead.get_value_from_output_json('oss-ui')
    credentials = jsonRead.get_app_auth_credentials()

    cred1 = ("%s %s: %s" % ("*" * 17, 'Admin', credentials['admin']))
    cred2 = ("%s %s: %s" % ("*" * 18, 'User', credentials['user']))
    ui_url = ("%s %s: %s") % ("*" * 10, 'Login Domain', ui_url)

    info = "\n%s\n%s\n%s\n%s\n%s" % ("*" * 120, ui_url, cred1, cred2, "*" * 120)

    _logs_display(['index', info])
