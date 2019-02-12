from core.commands import BaseCommand
from core.config import Settings
from core import constants as K
from threading import Thread
from core.terraform import PyTerraform
from core.providers.aws.boto3.ecs import stop_all_tasks_in_a_cluster
import time
import importlib
import sys
import pprint
pp = pprint.PrettyPrinter(indent=2)


class Redeploy(BaseCommand):
    def __init__(self, args):
        args.append((K.CATEGORY_FIELD_NAME, "deploy"))
        super().__init__(args)

    def execute(self, provider):
        self.initialize_install_classes(provider)

        if self.check_pre_requisites() is False:
            self.exit_system_with_pre_requisites_fail()

        input_instance = self.read_input()
        self.re_deploy_pacbot(input_instance)

    def initialize_install_classes(self, provider):
        self.validation_class = getattr(importlib.import_module(
            provider.provider_module + '.validate'), 'SystemInstallValidation')
        self.input_class = getattr(importlib.import_module(
            provider.provider_module + '.input'), 'SystemInstallInput')
        self.install_class = getattr(importlib.import_module(
            provider.provider_module + '.install'), 'Install')

    def re_deploy_pacbot(self, input_instance):
        resources_to_process = self.get_resources_to_process(input_instance)
        response = PyTerraform().terraform_taint(resources_to_process)
        terraform_with_targets = True  # THis should be set as false otherwise dependent resources will not be built
        # self.stop_ecs_tasks()

        self.install_class(
            self.args,
            input_instance,
            check_dependent_resources=False
        ).execute(
            resources_to_process,
            terraform_with_targets,
            self.dry_run
        )

    # def stop_ecs_tasks(self):
    #     stop_all_tasks_in_a_cluster(
    #         Settings.RESOURCE_NAME_PREFIX,
    #         Settings.AWS_ACCESS_KEY,
    #         Settings.AWS_SECRET_KEY,
    #         Settings.AWS_REGION
    #     )
