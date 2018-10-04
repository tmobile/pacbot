from __future__ import print_function

import json
import boto3
import os


batch = boto3.client('batch')
jobQueue = os.environ.get('JOB_QUEUE') #JOB_QUEUE:pacman-rule-engine
jobDefinition = os.environ.get('JOB_DEFINITION') #JOB_DEFINITION:pacman-rule-engine-job:15


def lambda_handler(event, context):
    # Log the received event
    print("Received event: " + json.dumps(event, indent=2))
    if jobQueue is None or jobDefinition is None:
        print("job definition or environment not found")
        return
    return processJsonInput(event,context)

def submit_to_batch(jobQueue,jobName,jobDefinition,containerOverrides,parameters):

    try:
            # Submit a Batch Job
            response = batch.submit_job(jobQueue=jobQueue, jobName=jobName, jobDefinition=jobDefinition,
                                        containerOverrides=containerOverrides, parameters=parameters)
            # Log response from AWS Batch
            print("Response: " + json.dumps(response, indent=2))
            # Return the jobId
            jobId = response['jobId']
            return {
                'jobId': jobId
            }
    except Exception as e:
            print(e)
            message = 'Error submitting Batch Job'
            print(message)
            raise Exception(message)

## funcation name: processJsonInput
## Author : kkumar
## process json input from CW and submit a job with Batch
##
def processJsonInput(event,context):
    # job name cannot be more then 128 characters for AWS batch
    jobName =  event['ruleId'][0:123] + '-job'

    executableName = "pac-managed-rules"

    if event.get('containerOverrides'):
        containerOverrides = event['containerOverrides']
    else:
        containerOverrides = {}

    if event.get('parameters'):
        parameters = event['parameters']
    else:
         if('ruleType' in event and event['ruleType']=="Serverless"):
            executableName = "rule-engine"

    parameters = {"executableName":executableName +".jar",
                 "params":json.dumps(event),
                 "jvmMemParams":os.getenv('JVM_HEAP_SIZE',"-Xms1024m -Xmx4g"),
                 "ruleEngineExecutableName":"rule-engine.jar",
                 "entryPoint":"com.tmobile.pacman.executor.RuleExecutor"}

    return submit_to_batch(jobQueue,jobName,jobDefinition,containerOverrides,parameters)
