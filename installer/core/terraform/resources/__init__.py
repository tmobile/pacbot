from core.config import Settings
from core.terraform.utils import get_terraform_resource_path
from core.terraform.utils import get_formatted_resource_attr_value, get_resource_created_status_op_file
from core.log import SysLog
from abc import ABCMeta
import json
import os
import sys
import re


class BaseTerraformResource(metaclass=ABCMeta):
    DEPENDS_ON = []
    OUTPUT_LIST = []
    VARIABLES = []
    PROCESS = True  # Whether to process this resource or not

    def __init__(self, input):
        self.input = input

    @classmethod
    def get_resource_id(cls):
        # return getattr(cls, 'resource_id', '_'.join(cls.__module__.title().lower().split('.')[1:]) + '_' + cls.__name__)
        return '_'.join(cls.__module__.title().lower().split('.')[1:]) + '_' + cls.__name__

    @classmethod
    def get_input_attr(cls, key):
        attrs = cls.available_args.get(key, None)
        arg_value = getattr(cls, key, None)

        # If the key present in inline argument
        if arg_value is not None and attrs is None:
            for main_arg_nname, main_arg in cls.available_args.items():
                if 'inline_args' in main_arg:
                    attrs = main_arg['inline_args'].get(key, None)

        return get_formatted_resource_attr_value(arg_value, attrs)

    @classmethod
    def get_output_attr(cls, key, index=False):
        if getattr(cls, "count", None):
            output_attr_ref = "%s.*.%s" % (cls.get_terraform_resource_path(), key)
            output_attr_ref += "" if index is False else "[count.index]"
            output_attr_ref = "${%s}" % output_attr_ref
        else:
            output_attr_ref = "${%s.%s}" % (cls.get_terraform_resource_path(), key)

        return output_attr_ref

    @classmethod
    def get_output_attr_name(cls, name):
        return "-".join([cls.get_resource_id(), name])

    @classmethod
    def get_terraform_resource_path(cls):
        return get_terraform_resource_path(cls)

    def get_resource_terraform_file(self):
        return os.path.join(
            Settings.TERRAFORM_DIR,
            self.get_resource_id() + "." + self.tf_file_extension
        )

    def get_resource_dependency_list(self):
        dependency_list = []

        for resource_class in self.DEPENDS_ON:
            dependency_list.append(
                resource_class.get_terraform_resource_path())

        return dependency_list

    def generate_terraform_script(self, terraform_args_dict):
        terraform_script_dict = {
            self.terraform_type: {
                self.resource_instance_name: {
                    self.get_resource_id(): terraform_args_dict
                }
            }
        }

        output = self.get_terraform_output_list()
        if output:
            terraform_script_dict['output'] = output

        variables = self.get_terraform_variables()
        if variables:
            terraform_script_dict['variable'] = variables

        return terraform_script_dict

    def get_terraform_variables(self):
        variables = {}

        for variable_class in self.VARIABLES:
            variable_dict = {}
            variable_name = variable_class.get_input_attr('variable_name')

            default_value = variable_class.get_input_attr('default_value')
            variable_dict = {'default': default_value}

            variable_type = variable_class.get_input_attr('variable_type')
            if variable_type:
                variable_dict['type'] = variable_type

            variables[variable_name] = variable_dict

        return variables

    def generate_terraform(self):
        if self.PROCESS:  # Generate the resource terraform file only if the resource is to be processed
            try:
                terraform_args_dict = self.get_terraform_resource_args_dict()
                self.create_terraform_resource_file(terraform_args_dict)
            except Exception as e:
                msg = 'Error occured in Terraform file generation. Resource: %s' % self.__class__.__name__
                print(msg)
                SysLog().write_error_log(str(e) + '\n' + msg)
                sys.exit()

    def remove_terraform(self):
        file = self.get_resource_terraform_file()
        if os.path.isfile(file):
            os.remove(file)

    def _get_resource_argument_value(self, arg, attrs):
        if attrs.get('inline_args', False):
            arg_dict_values = {}
            for inline_arg, inline_arg_attrs in attrs.get('inline_args', {}).items():
                inline_arg_value = getattr(self, inline_arg, None)
                if inline_arg_value is not None:
                    tf_arg_key = self._get_terraform_argument_key(inline_arg, inline_arg_attrs)
                    arg_dict_values[tf_arg_key] = get_formatted_resource_attr_value(inline_arg_value, inline_arg_attrs)

            if arg_dict_values:
                return arg_dict_values

        else:
            arg_value = getattr(self, arg, None)
            if arg_value is not None:
                return get_formatted_resource_attr_value(arg_value, attrs)

        return None

    def _get_terraform_argument_key(self, key, attrs):
        return attrs.get('tf_arg_key', key)

    def get_terraform_resource_args_dict(self):
        self.set_default_available_arguments()
        terraform_args_dict = {}
        for arg, attrs in self.available_args.items():
            arg_value = self._get_resource_argument_value(arg, attrs)
            if arg_value is not None:
                tf_arg_key = self._get_terraform_argument_key(arg, attrs)
                terraform_args_dict[tf_arg_key] = arg_value

        dependency_list = self.get_resource_dependency_list()
        if len(dependency_list) > 0:
            terraform_args_dict['depends_on'] = dependency_list

        provisioners = self.get_provisioners() + self.get_mandatory_provisioners()
        if provisioners:
            terraform_args_dict['provisioner'] = provisioners

        return terraform_args_dict

    def create_terraform_resource_file(self, terraform_args_dict):
        terraform_script_dict = self.generate_terraform_script(terraform_args_dict)

        with open(self.get_resource_terraform_file(), "w") as jsonfile:
            json.dump(terraform_script_dict, jsonfile, indent=4)

    def validate_input_args(self):
        success = True
        msg_list = []
        for arg in self._get_required_arguments():
            if getattr(self, arg, None) is None:
                msg_list.append("Required argument are not provided. Argument: %s" % arg)
                success = False

        if self.get_resource_id() is None:
            success = False
            msg_list.append("Resource ID is not assigned to this resource.")

        return success, msg_list

    def _get_required_arguments(self):
        required_arguments = []

        for arg, attrs in self.available_args.items():
            if attrs['required'] is True:
                if 'inline_args' in attrs:
                    for inline_arg, inline_arg_attrs in attrs.get('inline_args', {}).items():
                        if inline_arg_attrs['required'] is True:
                            required_arguments.append(inline_arg)
                else:
                    required_arguments.append(arg)

        return required_arguments

    def set_default_available_arguments(self):
        # Set count argument to all resources and use it if requires. So making it optional
        self.available_args['count'] = {'required': False}

    def get_provisioners(self):
        return []

    def get_mandatory_provisioners(self):
        return []

    def pre_generate_terraform(self):
        pass

    def pre_terraform_apply(self):
        pass

    def pre_terraform_destroy(self):
        pass

    def post_terraform_apply(self):
        pass

    def post_terraform_destroy(self):
        pass

    def render_output(self, outputs):
        pass


class TerraformResource(BaseTerraformResource, metaclass=ABCMeta):
    terraform_type = 'resource'
    MANDATORY_OUTPUT = 'id'
    tf_file_extension = 'tf'
    tags = [
        {Settings.RESOURCE_DEFAULT_TAG_NAME: Settings.RESOURCE_DEFAULT_TAG_VALUE}
    ]

    def check_exists_before(self, input, outputs):
        return False, {'attr': None, 'value': None}

    def resource_in_tf_output(self, tf_outputs):
        return True if tf_outputs.get(self.get_resource_id(), None) else False

    def get_terraform_output_list(self):
        outputs = {}
        if getattr(self, 'count', 1) != 0:
            self.OUTPUT_LIST.append(self.MANDATORY_OUTPUT)

            for output_name in self.OUTPUT_LIST:
                key = self.get_output_attr_name(output_name)
                outputs[key] = {'value': self.get_output_attr(output_name)}

        return outputs

    def get_mandatory_provisioners(self):
        id_reference = self.get_output_attr('id')
        resource_created_status_file = get_resource_created_status_op_file(self.get_resource_id())

        local_execs = [
            {
                'local-exec': {
                    'command': "echo 1 > %s" % resource_created_status_file
                }
            }
        ]

        return local_execs


class TerraformData(BaseTerraformResource, metaclass=ABCMeta):
    terraform_type = 'data'
    tf_file_extension = 'tf'

    @classmethod
    def get_output_attr(cls, key):
        return "${%s.%s.%s}" % ("data", cls.get_terraform_resource_path(), key)

    def get_terraform_output_list(self):
        return None


class BaseTerraformVariable(BaseTerraformResource):
    tf_file_extension = 'auto.tfvars'

    def generate_terraform(self):
        if self.variable_dict_input:
            lines = json.dumps(self.variable_dict_input, indent=4).split('\n')
            self.create_terraform_tfvars_file(lines)

    def create_terraform_tfvars_file(self, lines):
        file = self.get_resource_terraform_file()
        output_lines = []
        with open(file, "w") as fp:
            for line in lines:
                line = line.replace(": ", "=", 1)

                if re.match("^\[", line):
                    line = line.replace("[", self.variable_name + " = [")
                output_lines.append("%s\n" % line)
            fp.writelines(output_lines)

    def get_resource_terraform_file(self):
        return os.path.join(
            Settings.TERRAFORM_DIR,
            self.get_resource_id() + "." + self.tf_file_extension
        )

    @classmethod
    def length(cls):
        return "${length(var.%s)}" % cls.variable_name

    @classmethod
    def lookup(cls, key):
        return '${lookup(var.%s[count.index], "%s")}' % (cls.variable_name, key)
