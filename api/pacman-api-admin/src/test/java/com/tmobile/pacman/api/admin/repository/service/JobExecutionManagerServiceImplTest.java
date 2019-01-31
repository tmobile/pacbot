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

import static com.tmobile.pacman.api.admin.common.AdminConstants.JOB_CREATION_SUCCESS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.model.PutRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.PutRuleResult;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsRequest;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsResult;
import com.amazonaws.services.cloudwatchevents.model.RuleState;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.api.admin.config.PacmanConfiguration;
import com.tmobile.pacman.api.admin.domain.AWSCredentials;
import com.tmobile.pacman.api.admin.domain.JobDetails;
import com.tmobile.pacman.api.admin.domain.JobExecutionManagerListProjections;
import com.tmobile.pacman.api.admin.domain.JobProperty;
import com.tmobile.pacman.api.admin.domain.LambdaProperty;
import com.tmobile.pacman.api.admin.domain.S3Property;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.JobExecutionManagerRepository;
import com.tmobile.pacman.api.admin.service.AmazonClientBuilderService;
import com.tmobile.pacman.api.admin.service.AwsS3BucketService;
import com.tmobile.pacman.api.admin.util.AdminUtils;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AdminUtils.class, ByteBuffer.class, RuleState.class, PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class JobExecutionManagerServiceImplTest {

	@InjectMocks
	private JobExecutionManagerServiceImpl jobExecutionManagerService;

	@Mock
	private AwsS3BucketService awsS3BucketService;
	
	@Mock
	private JobExecutionManagerRepository jobExecutionManagerRepository;

	@Mock
	private PacmanConfiguration config;
	
	@Mock
	private AmazonClientBuilderService amazonClient;
	
	@Mock
	private ObjectMapper mapper;

	private AWSLambdaClient awsLambdaClient;
	
	private AmazonCloudWatchEvents amazonCloudWatchEvents;
	
	@Mock
	private InvokeRequest invokeRequest;
	
	@Mock
	private PutRuleRequest putRuleRequest;
	
	@Mock
	private PutTargetsRequest putTargetsRequest;
	
	@Mock
	private PutRuleResult putRuleResult;
	
	@Mock
	private PutTargetsResult putTargetsResult;
	
	@Mock
	private InvokeResult invokeResult;

	@Before
    public void setUp() throws Exception{
        awsLambdaClient = mock(AWSLambdaClient.class);
        amazonCloudWatchEvents = mock(AmazonCloudWatchEvents.class);
        invokeResult = mock(InvokeResult.class);
        PowerMockito.whenNew(ObjectMapper.class).withNoArguments().thenReturn(mapper);
        invokeRequest = Mockito.spy(new InvokeRequest());
        putRuleRequest = Mockito.spy(new PutRuleRequest());
        putRuleResult = Mockito.spy(new PutRuleResult());
        putTargetsResult = Mockito.spy(new PutTargetsResult());
        putTargetsRequest = Mockito.spy(new PutTargetsRequest());
        AWSCredentials awsCredentialsProperty = buildAWSCredentials();
		when(config.getAws()).thenReturn(awsCredentialsProperty);
        PowerMockito.whenNew(AWSLambdaClient.class).withAnyArguments().thenReturn(awsLambdaClient);  
        when(amazonClient.getAWSLambdaClient(anyString())).thenReturn(awsLambdaClient);
		when(amazonClient.getAmazonCloudWatchEvents(anyString())).thenReturn(amazonCloudWatchEvents);
    }

	@Test
	public void getAllJobExecutionManagersTest() {
		List<JobExecutionManagerListProjections> jobExecutionManagerDetails = Lists.newArrayList();
		jobExecutionManagerDetails.add(getjobExecutionManagerDetailsProjections());
		Page<JobExecutionManagerListProjections> allJobExecutionManagerDetails = new PageImpl<JobExecutionManagerListProjections>(jobExecutionManagerDetails,new PageRequest(0, 1), jobExecutionManagerDetails.size());
		when(jobExecutionManagerService.getAllJobExecutionManagers(0, 1, StringUtils.EMPTY)).thenReturn(allJobExecutionManagerDetails);
		assertThat(jobExecutionManagerRepository.findAllJobExecutionManagers(StringUtils.EMPTY, new PageRequest(0, 1)).getContent().size(), is(1));
	}
	
	@Test
	public void createJobTest() throws PacManException, JsonParseException, JsonMappingException, IOException {
		JobDetails createJobDetails = getCreateJobDetailsRequest();
		MultipartFile firstFile = getMockMultipartFile();
		mockStatic(ByteBuffer.class);
		Map<String, Object> newJobParams = Maps.newHashMap();
		newJobParams.put("jobName", "jobName123");
		newJobParams.put("jobUuid", "jobUuid123");
		newJobParams.put("jobType", "jobType123");
		newJobParams.put("jobDesc", "jobDesc123");
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(newJobParams);
        when(mapper.writeValueAsString(any())).thenReturn("[]");
		JobProperty jobProperty = buildJobProperty();
        when(config.getJob()).thenReturn(jobProperty);
		when(putRuleRequest.withName(anyString()).withDescription(anyString()).withState(anyString())).thenReturn(putRuleRequest);
        when(amazonCloudWatchEvents.putRule(any())).thenReturn(putRuleResult);
        String ruleArn = "ruleArn123";
		when(putRuleResult.getRuleArn()).thenReturn(ruleArn);
        when(amazonCloudWatchEvents.putTargets(any())).thenReturn(putTargetsResult);
        ByteBuffer params = ByteBuffer.wrap(createJobDetails.getJobParams().getBytes());
        when(ByteBuffer.wrap(any())).thenReturn(params);   
        when(awsLambdaClient.invoke(any())).thenReturn(invokeResult);
        when(invokeResult.getStatusCode()).thenReturn(200);
        when(invokeRequest.withFunctionName(anyString()).withPayload(any(ByteBuffer.class))).thenReturn(invokeRequest);
        
		int count = 0;
        when(putTargetsResult.getFailedEntryCount()).thenReturn(count);
		
		Map<String, Object> ruleParamDetails = Maps.newHashMap();
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(ruleParamDetails);
		assertThat(jobExecutionManagerService.createJob(firstFile, createJobDetails, "user123"), is(JOB_CREATION_SUCCESS));
	}
	
	
	
	@Test
	public void createJobFileChangedTest() throws PacManException, JsonParseException, JsonMappingException, IOException {
		JobDetails createJobDetails = getCreateJobDetailsRequest();
		createJobDetails.setIsFileChanged(true);
		MultipartFile firstFile = getMockMultipartFile();
		mockStatic(ByteBuffer.class);
		Map<String, Object> newJobParams = Maps.newHashMap();
		newJobParams.put("jobName", "jobName123");
		newJobParams.put("jobUuid", "jobUuid123");
		newJobParams.put("jobType", "jobType123");
		newJobParams.put("jobDesc", "jobDesc123");
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(newJobParams);
        when(mapper.writeValueAsString(any())).thenReturn("[]");
		JobProperty jobProperty = buildJobProperty();
        when(config.getJob()).thenReturn(jobProperty);
		when(putRuleRequest.withName(anyString()).withDescription(anyString()).withState(anyString())).thenReturn(putRuleRequest);
        when(amazonCloudWatchEvents.putRule(any())).thenReturn(putRuleResult);
        String ruleArn = "ruleArn123";
		when(putRuleResult.getRuleArn()).thenReturn(ruleArn);
        when(amazonCloudWatchEvents.putTargets(any())).thenReturn(putTargetsResult);
        ByteBuffer params = ByteBuffer.wrap(createJobDetails.getJobParams().getBytes());
        when(ByteBuffer.wrap(any())).thenReturn(params);   
        when(awsLambdaClient.invoke(any())).thenReturn(invokeResult);
        when(invokeResult.getStatusCode()).thenReturn(200);
        when(invokeRequest.withFunctionName(anyString()).withPayload(any(ByteBuffer.class))).thenReturn(invokeRequest);
        
		int count = 0;
        when(putTargetsResult.getFailedEntryCount()).thenReturn(count);
		
		Map<String, Object> ruleParamDetails = Maps.newHashMap();
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(ruleParamDetails);
		assertThat(jobExecutionManagerService.createJob(firstFile, createJobDetails, "user123"), is(JOB_CREATION_SUCCESS));
	}
	
	@Test
	public void createJobFileMissingTest() {
		assertThatThrownBy(() -> jobExecutionManagerService.createJob(getMockEmptyMultipartFile(), getCreateJobDetailsRequest(), "user123")).isInstanceOf(PacManException.class);
	}
	
	@Test
	public void createJobExceptionTest() throws PacManException, JsonParseException, JsonMappingException, IOException {
		JobDetails createJobDetails = getCreateJobDetailsRequest();
		MultipartFile firstFile = getMockMultipartFile();
		when(putRuleRequest.withName(anyString()).withDescription(anyString()).withState(anyString())).thenReturn(putRuleRequest);
		when(putRuleResult.getRuleArn()).thenReturn(null);
        when(amazonCloudWatchEvents.putRule(any())).thenReturn(putRuleResult);
		JobProperty jobProperty = buildJobProperty();
        when(config.getJob()).thenReturn(jobProperty);
		Map<String, Object> ruleParamDetails = Maps.newHashMap();
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(ruleParamDetails);
		assertThatThrownBy(() -> jobExecutionManagerService.createJob(firstFile, createJobDetails, "user123")).isInstanceOf(PacManException.class);
	}

	private JobProperty buildJobProperty() {
		JobProperty jobProperty = new JobProperty();
		LambdaProperty lambdaProperty = new LambdaProperty();
		lambdaProperty.setActionDisabled("actionDisabled123");
		lambdaProperty.setActionEnabled("actionEnabled123");
		lambdaProperty.setFunctionArn("functionArn123");
		lambdaProperty.setFunctionName("functionName123");
		lambdaProperty.setPrincipal("principal123");
		lambdaProperty.setTargetId("targetId123");
		S3Property s3Property = new S3Property();
		s3Property.setBucketName("bucketName123");
		jobProperty.setLambda(lambdaProperty);
		jobProperty.setS3(s3Property);
		return jobProperty;
	}

	private JobDetails getCreateJobDetailsRequest() {
		JobDetails jobDetails = new JobDetails();
		jobDetails.setIsFileChanged(false);
		jobDetails.setJobDesc("jobDesc");
		jobDetails.setJobExecutable("jobExecutable123");
		jobDetails.setJobFrequency("jobFrequency123");
		jobDetails.setJobName("jobName123");
		jobDetails.setJobParams("jobParams123");
		jobDetails.setJobType("jobType123");
		return jobDetails;
	}
	
	private MultipartFile getMockMultipartFile() {
		return new MockMultipartFile("data", "rule.jar", "multipart/form-data", "rule content".getBytes());
	}
	
	private MultipartFile getMockEmptyMultipartFile() {
		byte [] conents = new byte[0];
		return new MockMultipartFile("data", "rule.jar", "multipart/form-data", conents);
	}

	private JobExecutionManagerListProjections getjobExecutionManagerDetailsProjections() {
		return new JobExecutionManagerListProjections() {
			
			@Override
			public Date getModifiedDate() {
				return new Date();
			}
			
			@Override
			public String getJobType() {
				return "JobType123";
			}
			
			@Override
			public String getJobParams() {
				return "JobParams123";
			}
			
			@Override
			public String getJobName() {
				return "JobName123";
			}
			
			@Override
			public String getJobId() {
				return "JobId123";
			}
			
			@Override
			public String getJobFrequency() {
				return "JobFrequency123";
			}
			
			@Override
			public String getJobExecutable() {
				return "JobExecutable123";
			}
			
			@Override
			public Date getCreatedDate() {
				return new Date();
			}

			@Override
			public String getStatus() {
				return "ENABLED";
			}
		};
	}
	
	private AWSCredentials buildAWSCredentials() {
		AWSCredentials awsCredentials = new AWSCredentials();
		awsCredentials.setAccessKey("accessKey");
		awsCredentials.setSecretKey("secretKey");
		return awsCredentials;
	}
}
