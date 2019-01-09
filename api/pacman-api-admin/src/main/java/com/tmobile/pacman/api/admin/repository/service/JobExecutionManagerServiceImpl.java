/*******************************************************************************
 * Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.tmobile.pacman.api.admin.repository.service;

import static com.tmobile.pacman.api.admin.common.AdminConstants.CLOUDWATCH_RULE_DELETION_FAILURE;
import static com.tmobile.pacman.api.admin.common.AdminConstants.DELETE_RULE_TARGET_FAILED;
import static com.tmobile.pacman.api.admin.common.AdminConstants.ENABLED_CAPS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.INVALID_JOB_FREQUENCY;
import static com.tmobile.pacman.api.admin.common.AdminConstants.JAR_FILE_MISSING;
import static com.tmobile.pacman.api.admin.common.AdminConstants.JOB_CREATION_SUCCESS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.JOB_ID_ALREADY_EXITS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.JOB_ID_NOT_EXITS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.JOB_UPDATION_SUCCESS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.cloudwatchevents.model.DeleteRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.DeleteRuleResult;
import com.amazonaws.services.cloudwatchevents.model.PutRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.PutRuleResult;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsRequest;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsResult;
import com.amazonaws.services.cloudwatchevents.model.RemoveTargetsRequest;
import com.amazonaws.services.cloudwatchevents.model.RemoveTargetsResult;
import com.amazonaws.services.cloudwatchevents.model.RuleState;
import com.amazonaws.services.cloudwatchevents.model.Target;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.AddPermissionRequest;
import com.amazonaws.services.lambda.model.AddPermissionResult;
import com.amazonaws.services.lambda.model.GetPolicyRequest;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ResourceNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.config.PacmanConfiguration;
import com.tmobile.pacman.api.admin.domain.JobDetails;
import com.tmobile.pacman.api.admin.domain.JobExecutionManagerListProjections;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.JobExecutionManagerRepository;
import com.tmobile.pacman.api.admin.repository.model.JobExecutionManager;
import com.tmobile.pacman.api.admin.service.AmazonClientBuilderService;
import com.tmobile.pacman.api.admin.service.AwsS3BucketService;

/**
 * JobExecution Manager Service Implementations
 */
@Service
public class JobExecutionManagerServiceImpl implements JobExecutionManagerService {

	private static final Logger log = LoggerFactory.getLogger(JobExecutionManagerServiceImpl.class);
	
	@Autowired
	private PacmanConfiguration config;
	
	@Autowired
	private AmazonClientBuilderService amazonClient; 
	
	@Autowired
	private AwsS3BucketService awsS3BucketService;
	
	@Autowired
	private JobExecutionManagerRepository jobExecutionManagerRepository;

	@Autowired
	private ObjectMapper mapper;
	
	@Override
	public Page<JobExecutionManagerListProjections> getAllJobExecutionManagers(final Integer page, final Integer size, final String searchTerm) {
		return jobExecutionManagerRepository.findAllJobExecutionManagers(searchTerm.toLowerCase(), PageRequest.of(page, size));
	}

	@Override
	public String createJob(final MultipartFile fileToUpload, final JobDetails jobDetails, final String userId) throws PacManException {
		return addJobExecutionManager(fileToUpload, jobDetails, userId);
	}
	
	@Override
	public String updateJob(final MultipartFile fileToUpload, final JobDetails jobDetails, final String userId) throws PacManException {
		return updateJobExecutionManager(fileToUpload, jobDetails, userId);
	}

	@Override
	public Collection<String> getAllJobIds() {
		return jobExecutionManagerRepository.getAllJobIds();
	}
	
	@Override
	public JobExecutionManager getByJobId(final String jobId) throws PacManException {
		boolean isJobExits = jobExecutionManagerRepository.existsById(jobId);
		if(isJobExits) {
			return jobExecutionManagerRepository.findById(jobId).get();	
		} else {
			throw new PacManException(String.format(JOB_ID_ALREADY_EXITS, jobId));
		}
	}
	
	@Override
	public String enableDisableJob(final String jobId, final String action, final String userId) throws PacManException {
		if(jobExecutionManagerRepository.existsById(jobId)) {
			JobExecutionManager existingJob = jobExecutionManagerRepository.findById(jobId).get();
			if(action.equalsIgnoreCase("enable")) {
				return enableAndCreateCloudWatchRule(existingJob, userId, RuleState.ENABLED);
			} else {
				return disableAndCreateCloudWatchRule(existingJob, userId, RuleState.DISABLED);
			}
		} else {
			throw new PacManException(String.format(AdminConstants.JOB_ID_NOT_EXITS, jobId));
		}
	}
	
	private String disableAndCreateCloudWatchRule(JobExecutionManager existingJob, String userId, RuleState ruleState) throws PacManException {
		boolean isRemoveTargetSuccess = removeTargetWithRule(config.getJob().getLambda().getTargetId(), existingJob.getJobUUID());
		if(isRemoveTargetSuccess) {
			DeleteRuleRequest deleteRuleRequest = new DeleteRuleRequest()
	    	.withName(existingJob.getJobUUID());
			DeleteRuleResult deleteRuleResult = amazonClient.getAmazonCloudWatchEvents(config.getJob().getLambda().getRegion()).deleteRule(deleteRuleRequest);
			if (deleteRuleResult.getSdkHttpMetadata() != null) {
				if(deleteRuleResult.getSdkHttpMetadata().getHttpStatusCode() == 200) {
					existingJob.setUserId(userId);
					existingJob.setModifiedDate(new Date());
					existingJob.setStatus(ruleState.name());
					jobExecutionManagerRepository.save(existingJob);
					return String.format(AdminConstants.JOB_DISABLE_ENABLE_SUCCESS, ruleState.name().toLowerCase());
				} else {
					linkTargetWithRule(config.getJob().getLambda().getTargetId(), config.getJob().getLambda().getFunctionArn(), existingJob.getJobParams(), existingJob.getJobUUID());
					throw new PacManException(DELETE_RULE_TARGET_FAILED);
				}
			} else {
				throw new PacManException(CLOUDWATCH_RULE_DELETION_FAILURE);
			}
		} else {
			linkTargetWithRule(config.getJob().getLambda().getTargetId(), config.getJob().getLambda().getFunctionArn(), existingJob.getJobParams(), existingJob.getJobUUID());
			throw new PacManException(DELETE_RULE_TARGET_FAILED);
		}
	}

	private String enableAndCreateCloudWatchRule(JobExecutionManager existingJob, String userId, RuleState ruleState) throws PacManException {
		AWSLambda awsLambdaClient = amazonClient.getAWSLambdaClient(config.getJob().getLambda().getRegion());
		if (!checkIfPolicyAvailableForLambda(config.getRule().getLambda().getFunctionName(), awsLambdaClient)) {
			createPolicyForLambda(config.getRule().getLambda().getFunctionName(), awsLambdaClient);
		}
		
		PutRuleRequest ruleRequest = new PutRuleRequest()
    	.withName(existingJob.getJobUUID())
    	.withDescription(existingJob.getJobId())
    	.withState(ruleState);
		ruleRequest.setState(ruleState);
		ruleRequest.setScheduleExpression("cron(".concat(existingJob.getJobFrequency()).concat(")"));
		PutRuleResult ruleResult = amazonClient.getAmazonCloudWatchEvents(config.getJob().getLambda().getRegion()).putRule(ruleRequest);
		
		existingJob.setUserId(userId);
		existingJob.setModifiedDate(new Date());
		existingJob.setStatus(ruleState.name());

		if (ruleResult.getRuleArn() != null) {
			existingJob.setJobArn(ruleResult.getRuleArn());
			boolean isLambdaFunctionLinked = linkTargetWithRule(config.getJob().getLambda().getTargetId(), config.getJob().getLambda().getFunctionArn(), existingJob.getJobParams(), existingJob.getJobUUID());
			if(!isLambdaFunctionLinked) { 
				throw new PacManException(String.format(AdminConstants.LAMBDA_LINKING_EXCEPTION, existingJob.getJobId()));
			} else {
				jobExecutionManagerRepository.save(existingJob);
			}
		} else {
			throw new PacManException(String.format(AdminConstants.UNEXPECTED_ERROR_OCCURRED, existingJob.getJobId()));
		}
		return String.format(AdminConstants.JOB_DISABLE_ENABLE_SUCCESS, ruleState.name().toLowerCase());
	}

	private String addJobExecutionManager(final MultipartFile fileToUpload, final JobDetails jobDetails, final String userId) throws PacManException {
		boolean isJobExits = jobExecutionManagerRepository.existsById(jobDetails.getJobName());
		Date currentDate = new Date();
		if(!isJobExits) {
			JobExecutionManager newJobDetails = new JobExecutionManager();
			String jobUUID = UUID.randomUUID().toString();
			newJobDetails.setJobId(jobDetails.getJobName());
			newJobDetails.setJobName(jobDetails.getJobName());
			newJobDetails.setJobUUID(jobUUID);
			newJobDetails.setJobType(jobDetails.getJobType());
			newJobDetails.setJobExecutable(jobDetails.getJobExecutable());
			newJobDetails.setJobFrequency(jobDetails.getJobFrequency());
			newJobDetails.setJobParams(buildAndGetJobParams(jobDetails.getJobParams(), jobDetails, jobUUID));
			newJobDetails.setStatus(ENABLED_CAPS);
			newJobDetails.setUserId(userId);
			newJobDetails.setModifiedDate(currentDate);
			newJobDetails.setCreatedDate(currentDate);
			if(fileToUpload.isEmpty()) {
				throw new PacManException(JAR_FILE_MISSING);
			} else {
				createUpdateJobJartoS3Bucket(jobUUID, fileToUpload);
			}
			createUpdateCloudWatchEventJob(newJobDetails);
			return JOB_CREATION_SUCCESS;
		} else {
			throw new PacManException(String.format(JOB_ID_ALREADY_EXITS, jobDetails.getJobName()));
		}
	}

	private String updateJobExecutionManager(final MultipartFile fileToUpload, final JobDetails jobDetails, final String userId) throws PacManException {
		boolean isJobExits = jobExecutionManagerRepository.existsById(jobDetails.getJobName());
		Date currentDate = new Date();
		if(isJobExits) {
			JobExecutionManager newJobDetails = jobExecutionManagerRepository.findById(jobDetails.getJobName()).get();
			newJobDetails.setJobType(jobDetails.getJobType());
			newJobDetails.setJobFrequency(jobDetails.getJobFrequency());
			newJobDetails.setJobParams(buildAndGetJobParams(jobDetails.getJobParams(), jobDetails, newJobDetails.getJobUUID()));
			newJobDetails.setUserId(userId);
			newJobDetails.setModifiedDate(currentDate);
			if(jobDetails.getIsFileChanged()) {
				if(fileToUpload.isEmpty()) {
					throw new PacManException(JAR_FILE_MISSING);
				} else {
					newJobDetails.setJobExecutable(jobDetails.getJobExecutable());
					createUpdateJobJartoS3Bucket(newJobDetails.getJobUUID(), fileToUpload);
				}
			}
			createUpdateCloudWatchEventJob(newJobDetails);
			return JOB_UPDATION_SUCCESS;
		} else {
			throw new PacManException(String.format(JOB_ID_NOT_EXITS, jobDetails.getJobName()));
		}
	}
	
	private void createUpdateCloudWatchEventJob(final JobExecutionManager jobDetails) throws PacManException {	
		
		try {
			PutRuleRequest ruleRequest = new PutRuleRequest()
				.withName(jobDetails.getJobUUID())
		    	.withDescription(jobDetails.getJobId());
				 ruleRequest.withScheduleExpression("cron(".concat(jobDetails.getJobFrequency()).concat(")"))
		    	.withState(RuleState.ENABLED);
			PutRuleResult ruleResult = amazonClient.getAmazonCloudWatchEvents(config.getJob().getLambda().getRegion()).putRule(ruleRequest);
			AWSLambda awsLambdaClient = amazonClient.getAWSLambdaClient(config.getJob().getLambda().getRegion());
			
			if (!checkIfPolicyAvailableForLambda(config.getJob().getLambda().getFunctionName(), awsLambdaClient)) {
				createPolicyForLambda(config.getJob().getLambda().getFunctionName(), awsLambdaClient);
			}
			if (ruleResult.getRuleArn() != null) {
				jobDetails.setJobArn(ruleResult.getRuleArn());
				boolean isLambdaFunctionLinked = linkTargetWithRule(config.getJob().getLambda().getTargetId(), config.getJob().getLambda().getFunctionArn(), jobDetails.getJobParams(), jobDetails.getJobUUID());
				if(!isLambdaFunctionLinked) {
					
				} else {
					jobExecutionManagerRepository.save(jobDetails);
					invokeRule(awsLambdaClient, config.getJob().getLambda().getFunctionName(), jobDetails.getJobParams());
				}
			} else {
				throw new PacManException(UNEXPECTED_ERROR_OCCURRED);
			}
		} catch(Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			if(exception.getMessage().contains("ScheduleExpression is not valid")) {
				throw new PacManException(INVALID_JOB_FREQUENCY);
			} else {
				throw new PacManException(UNEXPECTED_ERROR_OCCURRED);
			}
		}
	}
	
	private boolean createUpdateJobJartoS3Bucket(String jobUUID, MultipartFile fileToUpload) {
		return awsS3BucketService.uploadFile(amazonClient.getAmazonS3(config.getJob().getS3().getBucketRegion()), fileToUpload, config.getJob().getS3().getBucketName(), jobUUID.concat(".jar"));
	}
	
	private void createPolicyForLambda(final String lambdaFunctionName, final AWSLambda lambdaClient) {
		AddPermissionRequest addPermissionRequest = new AddPermissionRequest()
		.withFunctionName(lambdaFunctionName)
		.withPrincipal(config.getJob().getLambda().getPrincipal())
		.withStatementId("sid-".concat(config.getJob().getLambda().getTargetId()))
		.withAction(config.getJob().getLambda().getActionEnabled());
		AddPermissionResult result = lambdaClient.addPermission(addPermissionRequest);
		log.info("Successfully created Policy for Lambda Function:"+result.getStatement());
	}
	
	private static boolean checkIfPolicyAvailableForLambda(final String lambdaFunctionName, final AWSLambda lambdaClient) {
		try {
			GetPolicyRequest getPolicyRequest = new GetPolicyRequest();
			getPolicyRequest.setFunctionName(lambdaFunctionName);
			lambdaClient.getPolicy(getPolicyRequest);
			return true;
		} catch (ResourceNotFoundException resourceNotFoundException) {
			if (resourceNotFoundException.getStatusCode() == 404) {
				return false;
			}
		}
		return false;
	}
	
	private boolean linkTargetWithRule(final String targetId, String targetLambdaFunctionArn, String params, String uuid) {
		Target target = new Target().withId(targetId).withArn(targetLambdaFunctionArn).withInput(params);
		PutTargetsRequest targetsRequest = new PutTargetsRequest().withTargets(target).withRule(uuid);
		try {
			PutTargetsResult targetsResult = amazonClient.getAmazonCloudWatchEvents(config.getJob().getLambda().getRegion()).putTargets(targetsRequest);
			return (targetsResult.getFailedEntryCount() == 0);
		} catch (Exception exception) {
			return false;
		}
	}
	
	private boolean removeTargetWithRule(final String targetId, String jobUuid) {
		RemoveTargetsRequest removeTargetsRequest = new RemoveTargetsRequest()
		.withIds(targetId)
	    .withRule(jobUuid);
		try {
			RemoveTargetsResult targetsResult = amazonClient.getAmazonCloudWatchEvents(config.getJob().getLambda().getRegion()).removeTargets(removeTargetsRequest);
			return (targetsResult.getFailedEntryCount()==0);
		} catch(Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}
	
	private void invokeRule(AWSLambda awsLambdaClient, String lambdaFunctionName, String params) {
		InvokeRequest invokeRequest = new InvokeRequest().withFunctionName(lambdaFunctionName).withPayload(ByteBuffer.wrap(params.getBytes()));
		InvokeResult invokeResult = awsLambdaClient.invoke(invokeRequest);
		if (invokeResult.getStatusCode() == 200) {
			invokeResult.getPayload();
		} else {
			log.error("Received a non-OK response from AWS: "+invokeResult.getStatusCode());
		}
	}
	
	private String buildAndGetJobParams(final String jobParams, final JobDetails jobDetails, final String jobUUID) {
		Map<String, Object> newJobParams;
		try {
			newJobParams = mapper.readValue(jobParams, new TypeReference<Map<String, Object>>(){});
			newJobParams.put("jobName", jobDetails.getJobName());
			newJobParams.put("jobUuid", jobUUID);
			newJobParams.put("jobType", jobDetails.getJobType());
			newJobParams.put("jobDesc", jobDetails.getJobDesc());
			return mapper.writeValueAsString(newJobParams);
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
		}
		return jobParams;
	}
}
