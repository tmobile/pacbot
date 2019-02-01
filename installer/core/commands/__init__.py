from abc import ABCMeta
from core.config import Settings
from core.terraform.resources import BaseTerraformResource
from core import constants as K
import inspect
import importlib


class BaseCommand(metaclass=ABCMeta):
    OPTIONAL_ARGS = {}
    MANDATORY_ARGS = {}
    category_field_name = K.CATEGORY_FIELD_NAME
    terraform_with_targets = False
    dry_run = False

    def __init__(self, args):
        self.args = args
        self.resource_tags_list = [v for (k, v) in args if k == self.category_field_name]

        if self.resource_tags_list:
            self.terraform_with_targets = True

        self.dry_run = True if any([x[1] for x in args if x[0] == "dry-run"]) else self.dry_run

    def get_resources_to_process(self, input_instance, need_instance=True):
        resources_to_process = []
        resource_keys_to_process = self.get_resource_keys_to_process()

        for resource in resource_keys_to_process:
            try:
                resource = Settings.RESOURCES_FOLDER + '.' + resource
                resource_module = importlib.import_module(resource)
            except ImportError:
                print("Resource classes could not be found for module: %s" % str(resource))
                raise Exception("Resource classes could not be found for module, %s", str(resource))
            except Exception as e:
                print("Error: %s" % str(e))
                raise Exception("Error: %s" % str(e))

            for name, obj_class in inspect.getmembers(resource_module, inspect.isclass):
                if obj_class.__module__ == resource:  # To collect Resource Classes defined only in the resource file
                    if BaseTerraformResource in inspect.getmro(obj_class):
                        if need_instance:
                            resources_to_process.append(obj_class(input_instance))  # Create instance of that class
                        else:
                            resources_to_process.append(obj_class)

        return resources_to_process

    def get_resource_keys_to_process(self):
        resource_keys_to_process = []
        if self.resource_tags_list:
            for resource, attrs in Settings.PROCESS_RESOURCES.items():
                for attr, val in attrs.items():
                    if attr == self.category_field_name:
                        if any(x in val for x in self.resource_tags_list):
                            resource_keys_to_process.append(resource)
        else:
            resource_keys_to_process = Settings.PROCESS_RESOURCES.keys()

        return resource_keys_to_process

    def initialize_and_validate(self):
        return self.validation_class().validate()

    def check_pre_requisites(self):
        pass

    def read_input(self):
        input_instancce = self.input_class()
        input_instancce.read_input()

        return input_instancce
