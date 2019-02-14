from core.commands import BaseCommand
from core.config import Settings
from core import constants as K
from core.terraform.resources.aws.ecs import ECSTaskDefinitionResource, ECSClusterResource
from core.terraform import PyTerraform
from core.providers.aws.boto3.ecs import stop_all_tasks_in_a_cluster, deregister_task_definition
from threading import Thread
import time
import importlib
import sys
import inspect


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
        try:
            response = PyTerraform().terraform_taint(resources_to_process)  # If tainted or destroyed already then skip it
        except:
            pass

        terraform_with_targets = True  # THis should be set as false otherwise dependent resources will not be built
        self.inactivate_required_services_for_redeploy(resources_to_process)
        self.run_real_deployment(input_instance, resources_to_process, terraform_with_targets)

    def inactivate_required_services_for_redeploy(self, resources_to_process, only_tasks=False):
        for resource in resources_to_process:
            resource_base_classes = inspect.getmro(resource.__class__)

            if ECSTaskDefinitionResource in resource_base_classes and not only_tasks:
                try:
                    deregister_task_definition(
                        Settings.AWS_ACCESS_KEY,
                        Settings.AWS_SECRET_KEY,
                        Settings.AWS_REGION,
                        resource.get_input_attr('family'),
                    )
                except:
                    pass
            elif ECSClusterResource in resource_base_classes:
                cluster_name = resource.get_input_attr('name')
                if only_tasks:
                    time.sleep(60)
                    break

        try:
            stop_all_tasks_in_a_cluster(
                cluster_name,
                Settings.AWS_ACCESS_KEY,
                Settings.AWS_SECRET_KEY,
                Settings.AWS_REGION
            )
        except:
            pass

    def run_real_deployment(self, input_instance, resources_to_process, terraform_with_targets):
        self.terraform_thread = Thread(target=self.run_tf_apply, args=(input_instance, list(resources_to_process), terraform_with_targets))
        stop_related_task_thread = Thread(target=self.inactivate_required_services_for_redeploy, args=(list(resources_to_process), True))

        self.terraform_thread.start()
        stop_related_task_thread.start()

        self.terraform_thread.join()
        stop_related_task_thread.join()

    def run_tf_apply(self, input_instance, resources_to_process, terraform_with_targets):
        self.install_class(
            self.args,
            input_instance,
            check_dependent_resources=False
        ).execute(
            resources_to_process,
            terraform_with_targets,
            self.dry_run
        )
