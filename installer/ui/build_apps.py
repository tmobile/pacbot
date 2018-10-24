import os
import subprocess
from git import Repo  # This require python package gitpython
import shutil
import time
from os import walk
import boto3

class BuildPacman(object):
    git_repo_url = "https://github.com/tmobile/pacbot.git"  # This should be changed based on the Repo
    pacman_clone_path = "pacman_cloned_dir_"  # This should be changed based on the system

    mvn_build_command = "mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V"
    npm_install = "npm install"
    bower_install = "bower install --allow-root"
    type_script_install = "npm install typescript@'>=2.1.0 <2.4.0'"
    ng_build = "ng build --env=prod --output-hashing=all"
    archive_type = "zip"  # What type of archive is required
    html_handlebars_uri = ''

    def __init__(self, api_domain_url, upload_dir, log_file):
        self.api_domain_url = api_domain_url
        self.cwd = os.getcwd()

        self.pacman_clone_path = "/tmp/" + self.pacman_clone_path + str(time.time())
        self.log_file = log_file
        self.upload_dir = upload_dir

    def _clean_up_all(self):
        shutil.rmtree(self.pacman_clone_path, ignore_errors=True)
        os.chdir(self.cwd)

    def build_api_and_ui_apps(self, aws_access_key, aws_secret_key, region, bucket):
        print("Execution started...............\n")
        cloned_repo = self.clone_pacman_code()
        self.upload_ui_files_to_s3(cloned_repo, aws_access_key, aws_secret_key, region, bucket)

        self.build_jar_and_ui_from_code(cloned_repo)

        self.archive_ui_app_build(cloned_repo.working_dir)

        self._clean_up_all()

    def upload_ui_files_to_s3(self, cloned_repo, aws_access_key, aws_secret_key, region, bucket):
        print("Uploading Email templates to S3...............\n")

        folder_to_upload = "pacman-v2-email-template"
        local_folder_path = os.path.join(cloned_repo.working_dir , 'emailTemplates', folder_to_upload)

        for (dirpath, dirnames, file_names) in walk(local_folder_path):
            files_to_upload = file_names

        s3_client = s3 = boto3.client('s3', region_name=region, aws_access_key_id=aws_access_key, aws_secret_access_key=aws_secret_key)
        for file_name in files_to_upload:
            file_path = os.path.join(local_folder_path, file_name)
            extra_args = {}
            key = folder_to_upload + '/' + file_name

            if file_name == 'html.handlebars':
                 extra_args = {'ACL':'public-read'}  # To make this public
                 self.html_handlebars_uri = '%s/%s/%s' % (s3_client.meta.endpoint_url, bucket, key)  # To be added in config.ts

            s3_client.upload_file(file_path, bucket, key, ExtraArgs=extra_args)

        print("Email templates upload to S3 completed...............\n")

    def run_bash_command(self, command, exec_dir):
        '''
        This method runs the bash command provided as argument
        '''
        command = command + ' &>>' + self.log_file
        os.chdir(exec_dir)
        p = subprocess.Popen(command, shell=True, stdout = subprocess.PIPE)
        time.sleep(5)
        stdout, stderr = p.communicate()

        return stdout, stderr

    def clone_pacman_code(self):
        '''
        Clone pacman code from the repository
        '''
        print("Cloning the repository...............\n")
        shutil.rmtree(self.pacman_clone_path, ignore_errors=True)  # Remove the directory if already exists for fresh build
        time.sleep(10)
        cloned_repo = Repo.clone_from(
            self.git_repo_url,
            self.pacman_clone_path,
            env={'GIT_SSH_COMMAND': "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no"}
        )
        print("Cloned repository to path: " + cloned_repo.working_dir + "\n")
        return cloned_repo

    def build_jar_and_ui_from_code(self, cloned_repo):
        print("Started building the jar...............\n")
        working_dir = cloned_repo.working_dir
        webapp_dir = self._get_web_app_directory(cloned_repo)

        self._update_variables_in_ui_config(webapp_dir)
        self.build_api_job_jars(working_dir)
        self.copy_jars_to_upload_dir(working_dir)

    def build_api_job_jars(self, working_dir):
        stdout, stderr = self.run_bash_command(self.mvn_build_command, working_dir)

    def copy_jars_to_upload_dir(self, working_dir):
        api_folders = [
            "pacman-api-admin.jar",
            "pacman-api-asset.jar",
            "pacman-api-compliance.jar",
            "config.jar",
            "pacman-api-notification.jar",
            "pacman-api-statistics.jar",
            "pacman-api-auth.jar"
        ]
        jobs_folder = [
            "pacman-aws-inventory-jar-with-dependencies.jar",
            "data-shipper-jar-with-dependencies.jar",
            "rule-engine.jar",
            "pac-managed-rules.jar"
        ]

        for jarfile in api_folders:
            copy_file_from = working_dir + "/dist/api/" + jarfile
            shutil.copy2(copy_file_from, self.upload_dir)

        for jarfile in jobs_folder:
            copy_file_from = working_dir + "/dist/jobs/" + jarfile
            shutil.copy2(copy_file_from, self.upload_dir)

    def _get_web_app_directory(self, cloned_repo):
        return os.path.join(cloned_repo.working_dir, "webapp")

    def _update_variables_in_ui_config(self, webapp_dir):
        config_file = os.path.join(webapp_dir, 'src/config/configurations.ts')
        with open(config_file, 'r') as f:
            lines = f.readlines()

        for idx, line in enumerate(lines):
            if "DEV_BASE_URL: ''" in line or "STG_BASE_URL: ''" in line or "PROD_BASE_URL: ''" in line:
                lines[idx] = lines[idx].replace("_BASE_URL: ''", "_BASE_URL: '" + self.api_domain_url + "/api'")

            if "AD_AUTHENTICATION: false" in line:
                lines[idx] = lines[idx].replace("AD_AUTHENTICATION: false", "AD_AUTHENTICATION: true")

            if "ISSUE_MAIL_TEMPLATE_URL: ''" in line:
                lines[idx] = lines[idx].replace("ISSUE_MAIL_TEMPLATE_URL: ''", "ISSUE_MAIL_TEMPLATE_URL: '" + self.html_handlebars_uri + "'")

        with open(config_file, 'w') as f:
            f.writelines(lines)

    def archive_ui_app_build(self, working_dir):
        time.sleep(5)
        zip_file_name = self.upload_dir + "/dist"
        print("Started creating zip file...")
        dir_to_archive = os.path.join(working_dir, "dist/pacmanspa")
        shutil.make_archive(zip_file_name, self.archive_type, dir_to_archive)
        time.sleep(5)
