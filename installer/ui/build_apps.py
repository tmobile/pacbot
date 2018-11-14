import os
import subprocess
from git import Repo  # This require python package gitpython
import shutil
import time
from os import walk
import boto3


class Buildpacbot(object):
    git_repo_url = "git@github.com:tmobile/pacbot.git"  # This should be changed based on the Repo
    mvn_build_command = "mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V"
    mvn_clean_command = "mvn clean"
    npm_install = "npm install"
    bower_install = "bower install --allow-root"
    type_script_install = "npm install typescript@'>=2.1.0 <2.4.0'"
    ng_build = "ng build --env=prod --output-hashing=all"
    archive_type = "zip"  # What type of archive is required
    html_handlebars_uri = ''

    def __init__(self, api_domain_url, upload_dir, log_file):
        self.api_domain_url = api_domain_url
        self.cwd = os.getcwd()
        self.codebase_root_dir = os.path.abspath(os.path.join(os.getcwd(), os.pardir))

        self.log_file = log_file
        self.upload_dir = upload_dir

    def _clean_up_all(self):
        os.chdir(self.cwd)

    def build_api_and_ui_apps(self, aws_access_key, aws_secret_key, region, bucket):
        print("Execution started...............\n")

        self.upload_ui_files_to_s3(aws_access_key, aws_secret_key, region, bucket)

        self.build_jar_and_ui_from_code()

        self.archive_ui_app_build()

        self._clean_up_all()

    def upload_ui_files_to_s3(self, aws_access_key, aws_secret_key, region, bucket):
        print("Uploading Email templates to S3...............\n")

        folder_to_upload = "pacman-v2-email-template"
        local_folder_path = os.path.join(self.codebase_root_dir, 'emailTemplates', folder_to_upload)

        for (dirpath, dirnames, file_names) in walk(local_folder_path):
            files_to_upload = file_names

        s3_client = s3 = boto3.client('s3', region_name=region, aws_access_key_id=aws_access_key, aws_secret_access_key=aws_secret_key)
        for file_name in files_to_upload:
            file_path = os.path.join(local_folder_path, file_name)
            extra_args = {}
            key = folder_to_upload + '/' + file_name

            if file_name == 'html.handlebars':
                extra_args = {'ACL': 'public-read'}  # To make this public
                self.html_handlebars_uri = '%s/%s/%s' % (s3_client.meta.endpoint_url, bucket, key)  # To be added in config.ts

            s3_client.upload_file(file_path, bucket, key, ExtraArgs=extra_args)

        print("Email templates upload to S3 completed...............\n")

    def run_bash_command(self, command, exec_dir):
        '''
        This method runs the bash command provided as argument
        '''
        command = command + ' &>>' + self.log_file
        os.chdir(exec_dir)
        p = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE)
        time.sleep(5)
        stdout, stderr = p.communicate()

        return stdout, stderr

    def build_jar_and_ui_from_code(self):
        print("Started building the jar...............\n")
        webapp_dir = self._get_web_app_directory()

        self._update_variables_in_ui_config(webapp_dir)
        self.build_api_job_jars(self.codebase_root_dir)
        self.copy_jars_to_upload_dir(self.codebase_root_dir)

    def build_api_job_jars(self, working_dir):
        stdout, stderr = self.run_bash_command(self.mvn_clean_command, working_dir)
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

    def _get_web_app_directory(self):
        return os.path.join(self.codebase_root_dir, "webapp")

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

    def archive_ui_app_build(self):
        time.sleep(5)
        zip_file_name = self.upload_dir + "/dist"
        print("Started creating zip file...")
        dir_to_archive = os.path.join(self.codebase_root_dir, "dist/pacmanspa")
        shutil.make_archive(zip_file_name, self.archive_type, dir_to_archive)
        time.sleep(5)
