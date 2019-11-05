from core.config import Settings
import os
import json


def get_terraform_provider_file():
    """
    Return terraform provider file path

    Returns:
        path: terraform provider file path
    """
    return os.path.join(
        Settings.TERRAFORM_DIR,
        'provider.tf.json'
    )


def get_terraform_scripts_and_files_dir():
    """
    Return path of the terraform supported scripts & file directory

    Returns:
        path: Scripts and files directory path
    """
    return os.path.join(
        Settings.TERRAFORM_DIR,
        'scripts_and_files'
    )


def get_terraform_scripts_dir():
    """
    Return path of the terraform supported scripts directory

    Returns:
        path: Scripts directory path
    """
    return os.path.join(
        get_terraform_scripts_and_files_dir(),
        'scripts'
    )


def get_terraform_resource_path(resource_class):
    """
    File path without extension of the resource terraform file

    Args:
        resource_class (class/instance): Resource Base class

    Returns:
        resource_path (path): terraform file path
    """
    resource_path = ".".join(
        [resource_class.resource_instance_name, resource_class.get_resource_id()])

    return resource_path


def get_formatted_resource_attr_value(arg_value, attrs):
    """
    Terraform resource attribute formation

    Args:
        arg_value (str): Attribute name
        attrs (dict): Attribute dictionary

    Returns:
        arg_value (str): Value of the resource attribute
    """
    field_type = attrs.get('type', None)
    if field_type == 'json':
        arg_value = json.dumps(arg_value)
    elif arg_value is True or arg_value is False:
        arg_value = "true" if arg_value is True else "false"
    else:
        arg_value = get_prefix_added_attr_value(arg_value, attrs)

    return arg_value


def get_prefix_added_attr_value(arg_value, attrs):
    """
    Prefix added attribute value for resource attribute if required

    Args:
        arg_value (str): Attribute name
        attrs (dict): Attribute dictionary

    Returns:
        arg_value (str): Value of the resource attribute with prefix
    """
    if attrs.get('prefix', False):
        trail_value = "" if arg_value.strip() == "" else arg_value
        prefix_sep = "" if (Settings.RESOURCE_NAME_PREFIX.strip() == "" or trail_value == "") else attrs.get('sep', "")

        arg_value = Settings.RESOURCE_NAME_PREFIX.strip() + prefix_sep + trail_value

    return arg_value


def get_terraform_latest_output_file():
    """
    Terraform output file where terraform execution output is stored

    Returns:
        path: Path of the output file
    """
    return os.path.join(Settings.OUTPUT_DIR, 'output.json')


def get_terraform_status_file():
    """
    Terraform status file where terraform execution status is stored

    Returns:
        path: Path of the status file
    """
    return os.path.join(Settings.OUTPUT_DIR, 'status.json')


def _get_resource_status_file_name(resource_id, status):
    """
    Resource completion status file name path

    Args:
        resource_id (str): Resource ID of the resource
        status (str): Extension to be provided

    Returns:
        str: Abs path of the status file as string
    """
    filename = "op." + resource_id + ".pyform." + str(status)
    file_path = os.path.join(Settings.OUTPUT_STATUS_DIR, filename)

    return str(file_path)


def get_resource_creating_status_op_file(resource_id):
    """
    Resource initialization started status file name path

    Args:
        resource_id (str): Resource ID of the resource

    Returns:
        str: Abs path of the status file as string
    """
    return _get_resource_status_file_name(resource_id, '0')


def get_resource_created_status_op_file(resource_id):
    """
    Resource creation completed status file name path

    Args:
        resource_id (str): Resource ID of the resource

    Returns:
        str: Abs path of the status file as string
    """
    return _get_resource_status_file_name(resource_id, '1')


def get_type_corrected_tags(tags):
    """
    Get tags type corrected since earlier version used list and now changed to dict for terraform compatibility

    Args:
        tags (List/Dict): Tags

    Returns:
        type_corrected_tags (dict): Dict of tags
    """
    type_corrected_tags = {}

    if isinstance(tags, list):  # To make tags compatible with earlier version
        for tag in tags:
            for key, value in tag.items():
                type_corrected_tags[key] = value
    else:
        return tags

    return type_corrected_tags


def get_system_default_resource_tags():
    """
    Get all tags required for resources

    Returns:
        tags (list): List of tags
    """
    type_corrected_tags = get_type_corrected_tags(Settings.DEFAULT_RESOURCE_TAG)

    return type_corrected_tags


def get_user_defined_resource_tags():
    """
    Get all user defined custom tags required for resources

    Returns:
        tags (list): List of tags
    """
    type_corrected_tags = get_type_corrected_tags(Settings.CUSTOM_RESOURCE_TAGS)

    return type_corrected_tags


def get_all_resource_tags():
    """
    Get all tags required for resources

    Returns:
        tags (list): List of tags
    """
    default_tags = get_system_default_resource_tags()
    user_defined_tags = get_user_defined_resource_tags()
    default_tags.update(user_defined_tags)

    return default_tags
