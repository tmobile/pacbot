from core.config import Settings
import os
import json


def get_terraform_provider_file():
    return os.path.join(
        Settings.TERRAFORM_DIR,
        'provider.tf'
    )


def get_terraform_scripts_and_files_dir():
    return os.path.join(
        Settings.TERRAFORM_DIR,
        'scripts_and_files'
    )


def get_terraform_scripts_dir():
    return os.path.join(
        get_terraform_scripts_and_files_dir(),
        'scripts'
    )


def get_terraform_resource_path(resource_class):
    resource_path = ".".join(
        [resource_class.resource_instance_name, resource_class.get_resource_id()])

    return resource_path


def get_formatted_resource_attr_value(arg_value, attrs):
    field_type = attrs.get('type', None)
    if field_type == 'json':
        arg_value = json.dumps(arg_value)
    elif arg_value is True or arg_value is False:
        arg_value = "true" if arg_value is True else "false"
    else:
        arg_value = get_prefix_added_attr_value(arg_value, attrs)

    return arg_value


def get_prefix_added_attr_value(arg_value, attrs):
    if attrs.get('prefix', False):
        trail_value = "" if arg_value.strip() == "" else arg_value
        prefix_sep = "" if (Settings.RESOURCE_NAME_PREFIX.strip() == "" or trail_value == "") else attrs.get('sep', "")

        arg_value = Settings.RESOURCE_NAME_PREFIX.strip() + prefix_sep + trail_value

    return arg_value


def get_terraform_latest_output_file():
    return os.path.join(Settings.OUTPUT_DIR, 'output.json')


def get_terraform_status_file():
    return os.path.join(Settings.OUTPUT_DIR, 'status.json')
