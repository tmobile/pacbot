import sys


def autoload(config_path, sys_args):
    python_version_info = sys.version_info
    if python_version_info.major >= 3 and python_version_info.minor >= 5:
        from core.kernel import Kernel
        # Run the command from command list
        Kernel(config_path).run(sys_args)

    else:
        print("#" * 60)
        print("%s!!!!!!! WARNING !!!!!!!" % (" " * 10))
        print("#" * 60)
        print("This system works with python version 3.5 or greater.")
        print("Please create virtualenv with python3 or upgrade python\n\n")
