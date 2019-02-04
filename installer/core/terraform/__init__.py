from core.terraform.resources import BaseTerraformVariable, TerraformData, TerraformResource
from core.config import Settings
from core.terraform.utils import get_terraform_resource_path, get_terraform_latest_output_file, get_terraform_status_file
from core.log import SysLog
from core import constants as K
from core.lib.python_terraform import *
from datetime import datetime
from core.utils import exists_teraform_lock
import inspect
import json


class PyTerraform():
    log_obj = SysLog()

    def terraform_init(self):
        if exists_teraform_lock():
            raise Exception(K.ANOTHER_PROCESS_RUNNING)

        terraform = Terraform(
            working_dir=Settings.TERRAFORM_DIR,
        )
        self.log_obj.write_debug_log(K.TERRAFORM_INIT_STARTED)
        response = terraform.init()

        if response[0] == 1:
            self.log_obj.write_debug_log(K.TERRAFORM_INIT_ERROR)
            raise Exception(response[2])

        self.log_obj.write_terraform_init_log(response)

        return response

    def terraform_plan(self, resources=None):
        if exists_teraform_lock():
            raise Exception(K.ANOTHER_PROCESS_RUNNING)

        terraform = Terraform(
            working_dir=Settings.TERRAFORM_DIR,
            targets=self.get_target_resources(resources)
        )

        self.log_obj.write_debug_log(K.TERRAFORM_PLAN_STARTED)
        response = terraform.plan()

        if response[0] == 1:
            self.log_obj.write_debug_log(K.TERRAFORM_PLAN_ERROR)
            raise Exception(response[2])

        self.log_obj.write_terraform_plan_log(response)

        return response

    def terraform_apply(self, resources=None):
        if exists_teraform_lock():
            raise Exception(K.ANOTHER_PROCESS_RUNNING)

        CMD = Settings.get('running_command', "Terraform Apply")
        terraform = Terraform(
            working_dir=Settings.TERRAFORM_DIR,
            targets=self.get_target_resources(resources),
            stdout_log_file=self.log_obj.get_terraform_install_log_file()
        )

        self.log_obj.write_terraform_apply_log_header()
        # In order to -auto-approve we need to pass skip_plan=True for python3
        response = terraform.apply(skip_plan=True)

        if response[0] == 1:
            self.log_obj.write_debug_log(K.TERRAFORM_APPLY_ERROR)
            self.write_current_status(CMD, K.APPLY_STATUS_ERROR, response[2])
            raise Exception(response[2])

        self.write_current_status(CMD, K.APPLY_STATUS_COMPLETED, K.TERRAFORM_APPLY_COMPLETED)
        return response

    def terraform_destroy(self, resources=None):
        if exists_teraform_lock():
            raise Exception(K.ANOTHER_PROCESS_RUNNING)

        terraform = Terraform(
            working_dir=Settings.TERRAFORM_DIR,
            targets=self.get_target_resources(resources),
            stdout_log_file=self.log_obj.get_terraform_destroy_log_file()
        )

        self.log_obj.write_terraform_destroy_log_header()
        kwargs = {"auto_approve": True, 'synchronous': False}  # To run destroy on background
        p, out, err = terraform.destroy(**kwargs)

        return p

    def process_destroy_result(self, p):
        response = Terraform().return_process_result(p)
        CMD = Settings.get('running_command', "Terraform Destroy")

        if response[0] == 1:
            self.log_obj.write_debug_log(K.TERRAFORM_DESTROY_ERROR)
            self.write_current_status(CMD, K.DESTROY_STATUS_ERROR, response[2])
            raise Exception(response[2])

        self.write_current_status(CMD, K.DESTROY_STATUS_COMPLETED, K.TERRAFORM_DESTROY_COMPLETED)

    def terraform_taint(self, resources):
        if exists_teraform_lock():
            raise Exception(K.ANOTHER_PROCESS_RUNNING)

        terraform = Terraform(
            working_dir=Settings.TERRAFORM_DIR,
        )

        taint_resources = self.get_taint_resources(resources)

        self.log_obj.write_debug_log(K.TERRAFORM_TAINT_STARTED)

        for resource_name in taint_resources:
            response = terraform.cmd("taint", resource_name)
            if response[0] == 1:
                self.log_obj.write_debug_log(K.TERRAFORM_TAINT_ERROR)
                raise Exception(response[2])

        self.log_obj.write_debug_log(K.TERRAFORM_TAINT_COMPLETED)

        return response

    def get_target_resources(self, resources):
        if resources:
            targets = []
            for resource in resources:
                # DO NOT process this resource as its definiiton asked to skip
                if resource.PROCESS is False:
                    continue

                if BaseTerraformVariable not in inspect.getmro(resource.__class__) and TerraformData not in inspect.getmro(resource.__class__):
                    targets.append(get_terraform_resource_path(resource))

            return targets

        return None

    def get_taint_resources(self, resources):
        taint_resources = []
        for resource in resources:
            if TerraformResource in inspect.getmro(resource.__class__):
                taint_resources.append(get_terraform_resource_path(resource))

        return taint_resources

    @classmethod
    def save_terraform_output(cls):
        tf_output_file = get_terraform_latest_output_file()
        output_dict = cls.load_terraform_output()

        with open(tf_output_file, 'w') as jsonfile:
            json.dump(output_dict, jsonfile, indent=4)
        cls.log_obj.write_debug_log(K.TERRAFORM_OUTPUT_STORED)

        return output_dict

    @classmethod
    def load_terraform_output(cls):
        output_dict = {}

        terraform = Terraform(
            working_dir=Settings.TERRAFORM_DIR,
        )
        response = terraform.output()
        if response:
            for key, item in response.items():
                key_splitted = key.split('-')
                resource_key = '-'.join(key_splitted[0:-1])

                if resource_key in output_dict:
                    output_dict[resource_key][key_splitted[-1]] = item['value']
                else:
                    output_dict[resource_key] = {key_splitted[-1]: item['value']}

        return output_dict

    @classmethod
    def load_terraform_output_from_json_file(cls):
        tf_output_file = get_terraform_latest_output_file()
        output_dict = {}
        if os.path.exists(tf_output_file):
            with open(tf_output_file) as jsonfile:
                output_dict = json.load(jsonfile)

        return output_dict

    def write_current_status(self, command, status_code, description=""):
        current_status = self.get_current_status()
        prev_status = None

        if current_status:
            prev_status = {
                'status_code': current_status['status_code'],
                'description': current_status['description'],
                'last_exec_command': current_status['last_exec_command'],
                'executed_time': current_status['executed_time']
            }

        current_status['status_code'] = status_code
        current_status['description'] = description
        current_status['last_exec_command'] = command
        current_status['executed_time'] = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

        if prev_status:  # FIrst time previous status won't be available
            current_status[prev_status['executed_time']] = prev_status

        status_file = get_terraform_status_file()
        with open(status_file, 'w') as jsonfile:
            json.dump(current_status, jsonfile, indent=4)

    @classmethod
    def get_current_status(self):
        status_file = get_terraform_status_file()
        status_dict = {}
        if os.path.exists(status_file):
            with open(status_file) as jsonfile:
                status_dict = json.load(jsonfile)

        return status_dict
