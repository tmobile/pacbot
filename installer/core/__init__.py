import sys


def autoload(config_path, sys_args):
    """
    This is the initial method which checks for python version and starts execution.
    If python version is less than 3.4 then it stops the execution and warn the user

    Args:
        config_path (str): This is the path to the main configuration/settings file
        sys_args (dict): CLI Arguments supplied to the command
    """
    python_version_info = sys.version_info

    if python_version_info.major >= 3 and python_version_info.minor >= 4:
        from core.kernel import Kernel
        # Run the command from command list
        Kernel(config_path).run(sys_args)

    else:
        print("#" * 60)
        print("%s!!!!!!! WARNING !!!!!!!" % (" " * 10))
        print("#" * 60)
        print("This system works with python version 3.5 or greater.")
        print("Please create virtualenv with python3 or upgrade python\n\n")
