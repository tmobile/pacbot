from core.config import Settings
import os
import sys
import subprocess


def get_sub_dir_names(directory_path):
    try:
        dirs = [dir.path.split(os.sep)[-1]
                for dir in os.scandir(directory_path) if dir.is_dir()]
    except BaseException:
        # Fall back to python2 compatibility
        dirs = os.walk(directory_path).next()[1]

    return dirs


def get_dir_file_names(directory_path):
    files_only = [f for f in os.listdir(directory_path) if os.path.isfile(
        os.path.join(directory_path, f))]

    return files_only


def exit_system_safely():
    sys.exit()


def run_command(command):
    '''
    This method runs the bash command provided as argument
    '''
    stderr = subprocess.PIPE
    stdout = subprocess.PIPE
    command_list = command.split(' ')

    try:
        p = subprocess.Popen(command_list, stdout=stdout, stderr=stderr)
        out, err = p.communicate()

        return p.returncode, out, err
    except Exception as e:
        return 1, "Command not found: %s" % command, None


def get_terraform_lock_file():
    return os.path.join(Settings.TERRAFORM_DIR, ".terraform.tfstate.lock.info")


def exists_teraform_lock():
    lock_file = get_terraform_lock_file()

    return os.path.exists(lock_file)
