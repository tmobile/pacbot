from threading import Thread
import time
import importlib
import sys
import inspect
import os


class RedeployV1(BaseCommand):
    """
    This calss is Older version defined to redeploy PacBot which is already installed by Installer command

    Attributes:
        validation_class (class): This validate the input and resources
        input_class (class): Main class to read input from user
        install_class (class): Provider based install class
        need_complete_install (boolean): True if complete installation is required else False

    """
    def __init__(self, args):
        args.append((K.CATEGORY_FIELD_NAME, "deploy"))
        args.append((K.CATEGORY_FIELD_NAME, "roles"))
        args.append((K.CATEGORY_FIELD_NAME, "all_read_role"))
        args.append((K.CATEGORY_FIELD_NAME, "batch-ecr"))
        args.append((K.CATEGORY_FIELD_NAME, "batch-job"))
        args.append((K.CATEGORY_FIELD_NAME, "submit-job"))
        args.append((K.CATEGORY_FIELD_NAME, "rule-engine-job"))
        args.append((K.CATEGORY_FIELD_NAME, "upload_tf"))

        self.need_complete_install = self._need_complete_installation()
        Settings.set('SKIP_RESOURCE_EXISTENCE_CHECK', True)
        super().__init__(args)

    def _need_complete_installation(self):
        need_complete_install = False

        redshift_cluster_file_tf = os.path.join(Settings.TERRAFORM_DIR, "datastore_redshift_RedshiftCluster.tf")
        redshift_cluster_file_tf_json = os.path.join(Settings.TERRAFORM_DIR, "datastore_redshift_RedshiftCluster.tf.json")

        if os.path.exists(redshift_cluster_file) or os.path.exists(redshift_cluster_file_tf_json):
            need_complete_install = True

        return need_complete_install

    def execute(self, provider):
        """
        Command execution starting point

        Args:
            provider (string): Provider name like AWS or Azure etc
        """
        self.initialize_install_classes(provider)

        if self.check_pre_requisites() is False:
            self.exit_system_with_pre_requisites_fail()

        input_instance = self.read_input()
        self.re_deploy_pacbot(input_instance)

    def initialize_install_classes(self, provider):
        """
        Initialise classes based on the provider

        Args:
            provider (string): Provider name like AWS or Azure etc
        """
        self.validation_class = getattr(importlib.import_module(
            provider.provider_module + '.validate'), 'SystemInstallValidation')
        self.input_class = getattr(importlib.import_module(
            provider.provider_module + '.input'), 'SystemInstallInput')
        self.install_class = getattr(importlib.import_module(
            provider.provider_module + '.install'), 'Install')

    def re_deploy_pacbot(self, input_instance):
        """
        Start method for redeploy

        Args:
            input_instance (Input object): User input values
        """
        resources_to_process = self.get_resources_to_process(self.resource_tags_list, input_instance)
        try:
            resources_to_taint = self.get_resources_with_given_tags(input_instance, ["deploy"])
            resources_to_taint = [resource for resource in resources_to_taint if resource.PROCESS is True]
            response = PyTerraform().terraform_taint(resources_to_taint)  # If tainted or destroyed already then skip it
        except Exception as e:
            pass

        terraform_with_targets = False if self.need_complete_install else True
        resources_to_process = self.get_complete_resources(input_instance) if self.need_complete_install else resources_to_process

        self.run_pre_deployment_process(resources_to_process)
        self.run_real_deployment(input_instance, resources_to_process, terraform_with_targets)

    def run_pre_deployment_process(self, resources_to_process):
        """
        Before redeploy get started do predeployment activities

        Args:
            resources_to_process (list): List of resources to be created/updated
        """
        if not self.dry_run:
            elb.delete_all_listeners_of_alb(
                ApplicationLoadBalancer.get_input_attr('name'),
                Settings.AWS_AUTH_CRED)

            tg_resources = self._get_resources_of_a_given_class_type(resources_to_process, ALBTargetGroupResource)
            tg_names = [resource.get_input_attr('name') for resource in tg_resources]
            elb.delete_alltarget_groups(
                tg_names,
                Settings.AWS_AUTH_CRED)

    def inactivate_required_services_for_redeploy(self, resources_to_process, dry_run):
        """
        Before redeploy get started or on redeploy happens stop the tasks and deregister task definition

        Args:
            resources_to_process (list): List of resources to be created/updated
            only_tasks (boolean): This flasg decides whther to deregister task definition or not
        """
        if dry_run:
            return

        for resource in resources_to_process:
            if self.terraform_thread.isAlive():
                resource_base_classes = inspect.getmro(resource.__class__)

                if ECSTaskDefinitionResource in resource_base_classes:
                    try:
                        deregister_task_definition(
                            resource.get_input_attr('family'),
                            Settings.AWS_AUTH_CRED,
                        )
                    except:
                        pass
                elif ECSClusterResource in resource_base_classes:
                    cluster_name = resource.get_input_attr('name')
            else:
                return

        for i in range(3):
            if self.terraform_thread.isAlive():
                try:
                    stop_all_tasks_in_a_cluster(
                        cluster_name,
                        Settings.AWS_ACCESS_KEY,
                        Settings.AWS_SECRET_KEY,
                        Settings.AWS_REGION
                    )
                except:
                    pass
                time.sleep(20)
            else:
                return

    def run_real_deployment(self, input_instance, resources_to_process, terraform_with_targets):
        """
        Main thread method which invokes the 2 thread: one for actual execution and another for displaying status

        Args:
            input_instance (Input obj): Input object with values read from user
            resources_to_process (list): List of resources to be created/updated
            terraform_with_targets (boolean): This is True since redeployment is happening
        """
        self.terraform_thread = Thread(target=self.run_tf_apply, args=(input_instance, list(resources_to_process), terraform_with_targets))
        # Dt-run variable is passed as it is rquired otherwise argument parsing issue will occur
        stop_related_task_thread = Thread(target=self.inactivate_required_services_for_redeploy, args=(list(resources_to_process), self.dry_run))

        self.terraform_thread.start()
        stop_related_task_thread.start()

        self.terraform_thread.join()
        stop_related_task_thread.join()

    def run_tf_apply(self, input_instance, resources_to_process, terraform_with_targets):
        """
        Execute the installation of resources by invoking the execute method of provider class

        Args:
            input_instance (Input obj): Input object with values read from user
            resources_to_process (list): List of resources to be created/updated
            terraform_with_targets (boolean): This is True since redeployment is happening
        """
        self.install_class(
            self.args,
            input_instance,
            check_dependent_resources=False
        ).execute(
            resources_to_process,
            terraform_with_targets,
            self.dry_run
        )
