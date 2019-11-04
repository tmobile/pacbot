from core.config import Settings
from core.terraform.utils import get_terraform_resource_path, get_all_resource_tags
from core.terraform.utils import get_formatted_resource_attr_value, get_resource_created_status_op_file
from core.log import SysLog
from abc import ABCMeta
import json
import os
import sys
import re


class BaseTerraformResource(metaclass=ABCMeta):
    """
    Abstract Base class for all resource type (terraform resource, data resource etc, variables)

    Attributes:
        DEPENDS_ON (list): This defines what are the resources on which the current resource depends on
        OUTPUT_LIST (list): List of attributes to be output in terraform
        VARIABLES (list): List of variables used in the resource
        PROCESS (boolean): Whether to create/destroy resource or not. If true then resource will be created else resource will not be created
                            and is used as dummy resource which doesn't dod anything
        input (input instacne): Input object for install or destroy
    """
    DEPENDS_ON = []
    OUTPUT_LIST = []
    VARIABLES = []
    PROCESS = True  # Whether to process this resource or not

    def __init__(self, input):
        self.input = input

    @classmethod
    def get_resource_id(cls):
        """
        This method generate the resource id of the current resource from the class name path

        Returns:
            resource_id (str): Resource ID string generated from the class name
        """
        # return getattr(cls, 'resource_id', '_'.join(cls.__module__.title().lower().split('.')[1:]) + '_' + cls.__name__)
        return '_'.join(cls.__module__.title().lower().split('.')[1:]) + '_' + cls.__name__

    @classmethod
    def get_input_attr(cls, key):
        """
        Find the value of the given attribute of a resource

        Args:
            key (str): Attribute name

        Returns:
            value (str): Formatted argument value of the given key
        """
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
        """
        Generate terraform output string(reference string) format of the given key. If count is greater than 1 then index is used.

        Args:
            key (str): Attribute name
            index (int): Index of the resource if the resource count is greater than 0

        Returns:
            output_attr_ref (str): Terraform output reference of the given attribute
        """
        if getattr(cls, "count", None):
            output_attr_ref = "%s.*.%s" % (cls.get_terraform_resource_path(), key)
            output_attr_ref += "" if index is False else "[count.index]"
            output_attr_ref = "${%s}" % output_attr_ref
        else:
            output_attr_ref = "${%s.%s}" % (cls.get_terraform_resource_path(), key)

        return output_attr_ref

    @classmethod
    def get_output_attr_name(cls, name):
        """
        Output key identifier of the given output attribute name

        Args:
            name (str): Output Attribute name

        Returns:
            key (str): Key identifier for the given output name
        """
        return "-".join([cls.get_resource_id(), name])

    @classmethod
    def get_terraform_resource_path(cls):
        """
        Get path of the resource class

        Returns:
            path (str): path of the resource class
        """
        return get_terraform_resource_path(cls)

    def get_resource_terraform_file(self):
        """
        Get the path of the terraform file created(to be created) for the current resource

        Returns:
            path (path): path of terraform file
        """
        return os.path.join(
            Settings.TERRAFORM_DIR,
            self.get_resource_id() + "." + self.tf_file_extension
        )

    def get_resource_dependency_list(self):
        """
        Find the dependency resources for the given resource

        Returns:
            dependency_list (list): dependency resources path list
        """
        dependency_list = []

        for resource_class in self.DEPENDS_ON:
            dependency_list.append(
                resource_class.get_terraform_resource_path())

        return dependency_list

    def generate_terraform_script(self, terraform_args_dict):
        """
        Build terraform resource configuration as dict to be used to generate json

        Args:
            terraform_args_dict (dict): Input resource class args dict supplied

        Returns:
            terraform_script_dict (dict): terraform resource configurations
        """
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
        """
        Find the terraform variables added for the current resource

        Returns:
            variables (dict): variables as dict
        """
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
        """This creates terraform resource"""
        if self.PROCESS:  # Generate the resource terraform file only if the resource is to be processed
            try:
                terraform_args_dict = self.get_terraform_resource_args_dict()
                self.create_terraform_resource_file(terraform_args_dict)
            except Exception as e:
                msg = 'Error occured in Terraform file generation. Resource: %s' % self.__class__.__name__
                print(msg)
                SysLog().write_error_log(str(e) + '\n' + msg)
                sys.exit()
        else:
            self.remove_terraform()

    def remove_terraform(self):
        """Delete the terraform file of the current resource from terraform directory"""
        file = self.get_resource_terraform_file()
        if os.path.isfile(file):
            os.remove(file)

    def _get_resource_argument_value(self, arg, attrs):
        """
        Find the terraform resource configuration from the class attributes

        Args:
            arg (str): Argument name
            attrs (dict): attributes of the argument

        Returns:
            value (str/None): Formated resource attr value if exists else None
        """
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
        """
        Find the terraform configuration key from the class attributes ie. if tf_arg_key is supplied then use that else the given attr

        Args:
            key (str): Argument name
            attrs (dict): attributes of the argument

        Returns:
            value (str): argument name
        """
        return attrs.get('tf_arg_key', key)

    def get_terraform_resource_args_dict(self):
        """
        Generate terraform configuration dict. Iterate over each configuration analyse the attributes and create configuration key with value as dict

        Returns:
            terraform_args_dict (dict): Terraform configuration dict
        """
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
        """
        This creates terraform resource file in terraform directory and add the configurations as json

        Args:
            terraform_args_dict (dict): Terraform resource configurations
        """
        terraform_script_dict = self.generate_terraform_script(terraform_args_dict)

        with open(self.get_resource_terraform_file(), "w") as jsonfile:
            json.dump(terraform_script_dict, jsonfile, indent=4)

    def validate_input_args(self):
        """
        Validate arguments supplied to the terrafomr resource

        Returns:
            success (boolean): Validation is success or not
            msg_list (list): List of error messages if there is any error
        """
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
        """
        Find all the mandatory configurations required by checking required attribute of attributes dict

        Returns:
            required_arguments (list): List of arguments/configurations
        """
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
        """List of all provisioners hook method for the current resource"""
        return []

    def get_mandatory_provisioners(self):
        """List of all mandatory provisioners hook method for the current resource"""
        return []

    def pre_generate_terraform(self):
        """Hook method called before terraform generation"""
        pass

    def pre_terraform_apply(self):
        """Hook method called before terraform apply"""
        pass

    def pre_terraform_destroy(self):
        """Hook method called before terraform destroy"""
        pass

    def post_terraform_apply(self):
        """Hook method called after terraform apply"""
        pass

    def post_terraform_destroy(self):
        """Hook method called after terraform destroy"""
        pass

    def render_output(self, outputs):
        """Hook method called to render output"""
        pass


class TerraformResource(BaseTerraformResource, metaclass=ABCMeta):
    """
    Main terraform resource class that is used toc create resource in cloud

    Attributes:
        terraform_type (str): Terraform resource type
        MANDATORY_OUTPUT (str): Mandatory output to be generated
        tf_file_extension (str): File extension for the terraform file
        tags (list): List of tags to be added
    """
    terraform_type = 'resource'
    MANDATORY_OUTPUT = 'id'
    tf_file_extension = 'tf.json'
    tags = get_all_resource_tags()

    def check_exists_before(self, input, outputs):
        """
        Factory method to check the existence of a resource

        Returns:
            boolean, dict: True if already exists else false with details
        """
        return False, {'attr': None, 'value': None}

    def resource_in_tf_output(self, tf_outputs):
        """
        Check whether the resource is created as part of this installation

        Args:
            tf_outputs (dict): Dict of terraform output

        Returns:
            boolean: True if created else False
        """
        return True if tf_outputs.get(self.get_resource_id(), None) else False

    def get_terraform_output_list(self):
        """
        Output to be done at the terraform

        Returns:
            output (dict): Output Dict
        """
        outputs = {}
        if getattr(self, 'count', 1) != 0:
            self.OUTPUT_LIST.append(self.MANDATORY_OUTPUT)

            for output_name in self.OUTPUT_LIST:
                key = self.get_output_attr_name(output_name)
                outputs[key] = {'value': self.get_output_attr(output_name)}

        return outputs

    def get_mandatory_provisioners(self):
        """List of all mandatory provisioners hook method for the current resource"""
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
    """
    Terraform data resource Base class

    Attributes:
        terraform_type (str): Terraform resource type
        tf_file_extension (str): File extension for the terraform file
    """
    terraform_type = 'data'
    tf_file_extension = 'tf.json'

    @classmethod
    def get_output_attr(cls, key):
        """
        Generate terraform output string(reference string) format of the given key.

        Args:
            key (str): Attribute name

        Returns:
            output_attr_ref (str): Terraform output reference of the given attribute
        """
        return "${%s.%s.%s}" % ("data", cls.get_terraform_resource_path(), key)

    def get_terraform_output_list(self):
        return None


class BaseTerraformVariable(BaseTerraformResource):
    """
    Terraform variable base class

    Attributes:
        tf_file_extension (str): File extension for the terraform file
    """
    tf_file_extension = 'auto.tfvars'

    def generate_terraform(self):
        """This creates terraform variable"""
        if self.variable_dict_input:
            lines = json.dumps(self.variable_dict_input, indent=4).split('\n')
            self.create_terraform_tfvars_file(lines)

    def create_terraform_tfvars_file(self, lines):
        """
        Create terraform tfavars file from the class definition

        Args:
            lines (list): List of json content

        """
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
        """
        Get the path of the terraform tfvars file created(to be created) for the current variable

        Returns:
            path (path): path of tfvars file
        """
        return os.path.join(
            Settings.TERRAFORM_DIR,
            self.get_resource_id() + "." + self.tf_file_extension
        )

    @classmethod
    def length(cls):
        """find the number of items in terraform variable list"""
        return "${length(var.%s)}" % cls.variable_name

    @classmethod
    def lookup(cls, key):
        """
        Search for the key in the variable with current index

        Args:
            kye (str): key name in the var
        """
        return '${lookup(var.%s[count.index], "%s")}' % (cls.variable_name, key)
