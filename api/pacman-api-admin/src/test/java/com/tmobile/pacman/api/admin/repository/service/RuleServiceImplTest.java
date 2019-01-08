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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.amazonaws.services.lambda.model.GetPolicyRequest;
import com.amazonaws.services.lambda.model.GetPolicyResult;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.config.PacmanConfiguration;
import com.tmobile.pacman.api.admin.domain.AWSCredentials;
import com.tmobile.pacman.api.admin.domain.CreateUpdateRuleDetails;
import com.tmobile.pacman.api.admin.domain.LambdaProperty;
import com.tmobile.pacman.api.admin.domain.RuleProjection;
import com.tmobile.pacman.api.admin.domain.RuleProperty;
import com.tmobile.pacman.api.admin.domain.S3Property;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.RuleCategoryRepository;
import com.tmobile.pacman.api.admin.repository.RuleRepository;
import com.tmobile.pacman.api.admin.repository.model.Rule;
import com.tmobile.pacman.api.admin.service.AmazonClientBuilderService;
import com.tmobile.pacman.api.admin.util.AdminUtils;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AdminUtils.class, ByteBuffer.class, RuleState.class, PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class RuleServiceImplTest {

	@InjectMocks
	private RuleServiceImpl ruleService;

	@Mock
	private PacmanConfiguration config;
	
	@Mock
	private AmazonClientBuilderService amazonClient;
	
	@Mock
	private ObjectMapper mapper;

	@Mock
	private RuleRepository ruleRepository;
	
	@Mock
	private PutRuleResult putRuleResult;
	
	private AWSLambdaClient awsLambdaClient;
	
	private AmazonCloudWatchEvents amazonCloudWatchEvents;
	
	@Mock
	private InvokeRequest invokeRequest;
	
	@Mock
	private PutTargetsRequest putTargetsRequest;
	
	@Mock
	private PutRuleRequest putRuleRequest;
	
	@Mock
	private PutTargetsResult putTargetsResult;
	
	@Mock
	private GetPolicyResult getPolicyResult;
	
	@Mock
	private InvokeResult invokeResult;
	
	@Mock
	private RuleCategoryRepository ruleCategoryRepository;

	@Before
    public void setUp() throws Exception{
        awsLambdaClient = mock(AWSLambdaClient.class);
        amazonCloudWatchEvents = mock(AmazonCloudWatchEvents.class);
       // invokeRequest = mock(InvokeRequest.class);
        invokeResult = mock(InvokeResult.class);
        PowerMockito.whenNew(ObjectMapper.class).withNoArguments().thenReturn(mapper);
        invokeRequest = Mockito.spy(new InvokeRequest());
        getPolicyResult = Mockito.spy(new GetPolicyResult());
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
	public void getAllRulesByTargetTypeTest() {
		List<Rule> ruleDetails = Lists.newArrayList();
		Optional<Rule> rule = getRuleDetailsResponse();
		ruleDetails.add(rule.get());
		when(ruleRepository.findByTargetTypeIgnoreCase(anyString())).thenReturn(ruleDetails);
		assertThat(ruleService.getAllRulesByTargetType(anyString()).size(), is(1));
	}

	@Test
	public void getAllRulesByTargetTypeNameTest() {
		List<RuleProjection> ruleDetails = Lists.newArrayList();
		ruleDetails.add(getRuleProjection());
		when(ruleRepository.findByTargetType(anyString())).thenReturn(ruleDetails);
		assertThat(ruleService.getAllRulesByTargetTypeName(anyString()).size(), is(1));
	}

	@Test
	public void getAllRulesByTargetTypeAndNotInRuleIdListTest() {
		List<RuleProjection> ruleDetails = Lists.newArrayList();
		ruleDetails.add(getRuleProjection());
		when(ruleRepository.findByTargetTypeAndRuleIdNotIn(anyString(), any())).thenReturn(ruleDetails);
		assertThat(ruleService.getAllRulesByTargetTypeAndNotInRuleIdList(anyString(), any()).size(), is(1));
	}

	@Test
	public void getAllRulesByTargetTypeAndRuleIdListTest() {
		List<RuleProjection> ruleDetails = Lists.newArrayList();
		ruleDetails.add(getRuleProjection());
		when(ruleRepository.findByTargetTypeAndRuleIdIn(anyString(), any())).thenReturn(ruleDetails);
		assertThat(ruleService.getAllRulesByTargetTypeAndRuleIdList(anyString(), any()).size(), is(1));
	}

	@Test
	public void getByRuleIdTest() {
		Optional<Rule> ruleDetails = getRuleDetailsResponse();
		when(ruleRepository.findByRuleId(anyString())).thenReturn(ruleDetails.get());
		assertThat(ruleService.getByRuleId(anyString()).getRuleId(), is("ruleId123"));
	}

	@Test
	public void getRulesTest() {
		List<Rule> ruleDetails = Lists.newArrayList();
		Optional<Rule> rule = getRuleDetailsResponse();
		ruleDetails.add(rule.get());
		Page<Rule> allPoliciesDetails = new PageImpl<Rule>(ruleDetails, new PageRequest(0, 1), ruleDetails.size());
		when(ruleService.getRules(StringUtils.EMPTY, 0, 1)).thenReturn(allPoliciesDetails);
		assertThat(ruleRepository.findAll(StringUtils.EMPTY, new PageRequest(0, 1)).getContent().size(), is(1));
	}

	@Test
	public void getAllAlexaKeywordsTest() {
		Collection<String> alexaKeywords = Lists.newArrayList();
		alexaKeywords.add("Alexa1");
		alexaKeywords.add("Alexa2");
		when(ruleService.getAllAlexaKeywords()).thenReturn(alexaKeywords);
		assertThat(ruleRepository.getAllAlexaKeywords().size(), is(2));
	}
	
	@Test
	public void invokeRuleTest() throws Exception {
		Optional<Rule> rule = getRuleDetailsResponse();
		when(ruleRepository.findById(anyString())).thenReturn(rule);
		List<Map<String, Object>> additionalRuleParams = Lists.newArrayList();
		String referenceId = "S4FA";
        mockStatic(AdminUtils.class);
        mockStatic(ByteBuffer.class);
        when(AdminUtils.getReferenceId()).thenReturn(referenceId);
        Map<String, Object> ruleParamDetails = Maps.newHashMap();
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(ruleParamDetails);
        when(mapper.writeValueAsString(any())).thenReturn("[]");
        RuleProperty ruleProperty = buildRuleProperty();
        when(config.getRule()).thenReturn(ruleProperty);
        ByteBuffer params = ByteBuffer.wrap(rule.get().getRuleParams().getBytes());
        when(ByteBuffer.wrap(any())).thenReturn(params);   
        when(awsLambdaClient.invoke(any())).thenReturn(invokeResult);
        when(invokeResult.getStatusCode()).thenReturn(200);
        when(invokeRequest.withFunctionName(anyString()).withPayload(any(ByteBuffer.class))).thenReturn(invokeRequest);
        assertThat(ruleService.invokeRule("ruleId123", additionalRuleParams), is("S4FA"));
	}

	@Test
	public void invokeRuleCheckInvokeStatusFalseTest() throws Exception {
		Optional<Rule> rule = getRuleDetailsResponse();
		when(ruleRepository.findById(anyString())).thenReturn(rule);
		List<Map<String, Object>> additionalRuleParams = Lists.newArrayList();
		String referenceId = null;
        mockStatic(AdminUtils.class);
        mockStatic(ByteBuffer.class);
        when(AdminUtils.getReferenceId()).thenReturn(referenceId);
      
        RuleProperty ruleProperty = buildRuleProperty();
        when(config.getRule()).thenReturn(ruleProperty);
        ByteBuffer params = ByteBuffer.wrap(rule.get().getRuleParams().getBytes());
        when(ByteBuffer.wrap(any())).thenReturn(params);   
        when(awsLambdaClient.invoke(any())).thenReturn(invokeResult);
        when(invokeResult.getStatusCode()).thenReturn(500);
        when(invokeRequest.withFunctionName(anyString()).withPayload(any(ByteBuffer.class))).thenReturn(invokeRequest);
        assertNull(ruleService.invokeRule("ruleId123", additionalRuleParams));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void enableDisableRuleNotFoundExceptionTest() throws Exception {
		String action = "disable";
		mockStatic(RuleState.class);
		when(ruleRepository.findById(anyString())).thenThrow(PacManException.class);
	    assertThatThrownBy(() -> ruleService.enableDisableRule("ruleId123", action, "userId123")).isInstanceOf(PacManException.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void enableDisableRuleCreatePolicyForLambdaExceptionTest() throws Exception {
		String action = "enable";
		Optional<Rule> rule = getRuleDetailsResponse();
		mockStatic(RuleState.class);
		when(ruleRepository.findById(anyString())).thenReturn(rule);
		when(putRuleRequest.withName(anyString()).withDescription(anyString()).withState(anyString())).thenReturn(putRuleRequest);
		when(amazonClient.getAWSLambdaClient(anyString())).thenThrow(Exception.class);
	    RuleProperty ruleProperty = buildRuleProperty();
        when(config.getRule()).thenReturn(ruleProperty);
	    when(ruleRepository.save(rule.get())).thenReturn(rule.get()); 
	    assertThatThrownBy(() -> ruleService.enableDisableRule("ruleId123", action, "userId123")).isInstanceOf(PacManException.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void enableDisableRuleResourceNotFoundExceptionTest() throws Exception {
		String action = "enable";
		String lambdaFunctionName = "lambdaFunctionName123";
		GetPolicyRequest getPolicyRequest = new GetPolicyRequest();
		getPolicyRequest.setFunctionName(lambdaFunctionName);
		PowerMockito.whenNew(GetPolicyRequest.class).withNoArguments().thenThrow(Exception.class);
		Optional<Rule> rule = getRuleDetailsResponse();
		mockStatic(RuleState.class);
		when(ruleRepository.findById(anyString())).thenReturn(rule);
		when(putRuleRequest.withName(anyString()).withDescription(anyString()).withState(anyString())).thenReturn(putRuleRequest);
		when(amazonClient.getAWSLambdaClient(anyString())).thenThrow(Exception.class);
	    RuleProperty ruleProperty = buildRuleProperty();
        when(config.getRule()).thenReturn(ruleProperty);
	    when(ruleRepository.save(rule.get())).thenReturn(rule.get());
	    assertThatThrownBy(() -> ruleService.enableDisableRule("ruleId123", action, "userId123")).isInstanceOf(PacManException.class);
	}

/*	
	@Test
	public void enableDisableRuleTest() throws Exception {
		String action = "enable";
		Optional<Rule> rule = getRuleDetailsResponse();
		mockStatic(RuleState.class);
		when(ruleRepository.existsById(anyString())).thenReturn(true);
		when(ruleRepository.findById(anyString())).thenReturn(rule);
		when(putRuleRequest.withName(anyString()).withDescription(anyString()).withState(anyString())).thenReturn(putRuleRequest);
	    RuleProperty ruleProperty = buildRuleProperty();
        when(config.getRule()).thenReturn(ruleProperty);
	    when(ruleRepository.save(rule.get())).thenReturn(rule.get());
	    assertThat(ruleService.enableDisableRule("ruleId123", action, "userId123"), is(String.format(AdminConstants.RULE_DISABLE_ENABLE_SUCCESS, action)));
	}*/
	
	
	@Test
	public void createRuleTest() throws Exception {
		CreateUpdateRuleDetails createRuleDetails = getCreateUpdateRuleDetailsRequest();
		MultipartFile firstFile = getMockMultipartFile();
		Map<String, Object> ruleParamDetails = Maps.newHashMap();
		List<Map<String, Object>> params = Lists.newArrayList();
		Map<String, Object> param = Maps.newHashMap();
		param.put("name", "name123");
		param.put("value", "name123");
		param.put("encrypt", true);
		params.add(param);
		param = Maps.newHashMap();
		param.put("name", "name123");
		param.put("value", "name123");
		param.put("encrypt", false);
		param.put("isValueNew", true);
		params.add(param);
		ruleParamDetails.put("environmentVariables", params);
		ruleParamDetails.put("params", params);
		
		when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(ruleParamDetails);
	    when(mapper.writeValueAsString(any())).thenReturn("[]");
		Optional<Rule> rule = getRuleDetailsResponse();
		RuleProperty ruleProperty = buildRuleProperty();
        when(config.getRule()).thenReturn(ruleProperty);
	    when(ruleRepository.save(rule.get())).thenReturn(rule.get());
	    mockStatic(ByteBuffer.class);
	    mockStatic(RuleState.class);
	    when(amazonCloudWatchEvents.putRule(any())).thenReturn(putRuleResult);
	    String ruleArn = "ruleArn123";
		when(putRuleResult.getRuleArn()).thenReturn(ruleArn);
		when(amazonCloudWatchEvents.putTargets(any())).thenReturn(putTargetsResult);
        ByteBuffer byteBuffer = ByteBuffer.wrap(createRuleDetails.getRuleParams().getBytes());
        when(ByteBuffer.wrap(any())).thenReturn(byteBuffer);   
        when(awsLambdaClient.invoke(any())).thenReturn(invokeResult);
        when(awsLambdaClient.getPolicy(any())).thenReturn(getPolicyResult);
        when(invokeResult.getStatusCode()).thenReturn(200);
        when(invokeRequest.withFunctionName(anyString()).withPayload(any(ByteBuffer.class))).thenReturn(invokeRequest);
        int count = 0;
        when(putTargetsResult.getFailedEntryCount()).thenReturn(count);
		assertThat(ruleService.createRule(firstFile, createRuleDetails, "userId123"), is(String.format(AdminConstants.RULE_CREATION_SUCCESS)));
	}
	
	@Test
	public void updateRuleTest() throws Exception {
		
		Optional<Rule> rule = getRuleDetailsResponse();
		when(ruleRepository.findById(anyString())).thenReturn(rule);
		
		MultipartFile firstFile = getMockMultipartFile();
		Map<String, Object> ruleParamDetails = Maps.newHashMap();
		List<Map<String, Object>> params = Lists.newArrayList();
		Map<String, Object> param = Maps.newHashMap();
		param.put("name", "name123");
		param.put("value", "name123");
		param.put("encrypt", true);
		params.add(param);
		param = Maps.newHashMap();
		param.put("name", "name123");
		param.put("value", "name123");
		param.put("encrypt", false);
		param.put("isValueNew", true);
		params.add(param);
		ruleParamDetails.put("environmentVariables", params);
		ruleParamDetails.put("params", params);
		RuleProperty ruleProperty = buildRuleProperty();
        when(config.getRule()).thenReturn(ruleProperty);
        when(ruleRepository.findByRuleId(anyString())).thenReturn(rule.get());
	    when(ruleRepository.save(rule.get())).thenReturn(rule.get());
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(ruleParamDetails);
        when(mapper.writeValueAsString(any())).thenReturn("[]");
		assertThat(ruleService.updateRule(firstFile, getCreateUpdateRuleDetailsRequest(), "userId123"), is(String.format(AdminConstants.RULE_CREATION_SUCCESS)));
	}
	
	@Test
	public void getAllRuleCategoriesTest() throws PacManException {
		
		when(ruleCategoryRepository.findAll()).thenReturn(new ArrayList<>());
		assertThat(ruleService.getAllRuleCategories(), is(notNullValue()));
	}
	
	
	private RuleProperty buildRuleProperty() {
		RuleProperty ruleProperty = new RuleProperty();
		S3Property s3Property = new S3Property();
		s3Property.setBucketName("job-execution-manager-executables");
		LambdaProperty lambdaProperty = new LambdaProperty();
		lambdaProperty.setActionDisabled("actionDisabled123");
		lambdaProperty.setActionEnabled("actionEnabled123");
		lambdaProperty.setFunctionArn("functionArn123");
		lambdaProperty.setFunctionName("functionName123");
		lambdaProperty.setPrincipal("principal123");
		lambdaProperty.setTargetId("targetId123");
		ruleProperty.setLambda(lambdaProperty);
		ruleProperty.setS3(s3Property);
		return ruleProperty;
	}
	
	private MultipartFile getMockMultipartFile() {
		return new MockMultipartFile("data", "rule.jar", "multipart/form-data", "rule content".getBytes());
	}

	private CreateUpdateRuleDetails getCreateUpdateRuleDetailsRequest() {
		CreateUpdateRuleDetails ruleDetails = new CreateUpdateRuleDetails();
		ruleDetails.setRuleId("ruleId123");
		ruleDetails.setPolicyId("policyId123");
		ruleDetails.setRuleName("ruleName123");
		ruleDetails.setTargetType("targetType123");
		ruleDetails.setAssetGroup("assetGroup123");
		ruleDetails.setAlexaKeyword("alexaKeyword123");
		ruleDetails.setRuleParams("ruleParams123");
		ruleDetails.setRuleFrequency("ruleFrequency123");
		ruleDetails.setRuleExecutable("ruleExecutable123");
		ruleDetails.setRuleRestUrl("ruleRestUrl123");
		ruleDetails.setRuleType("ruleType123");
		ruleDetails.setStatus("status123");
		ruleDetails.setDisplayName("displayName123");
		ruleDetails.setDataSource("dataSource123");
		ruleDetails.setIsAutofixEnabled(false);
		ruleDetails.setIsFileChanged(false);
		return ruleDetails;
	}

	private RuleProjection getRuleProjection() {
		return new RuleProjection() {
			@Override
			public String getType() {
				return "Type123";
			}

			@Override
			public String getText() {
				return "Text123";
			}

			@Override
			public String getStatus() {
				return "Status123";
			}

			@Override
			public String getPolicyId() {
				return "PolicyId123";
			}

			@Override
			public String getId() {
				return "RuleId123";
			}
		};
	}

	private Optional<Rule> getRuleDetailsResponse() {
		Rule rule = new Rule();
		rule.setRuleId("ruleId123");
		rule.setRuleUUID("ruleUUID123");
		rule.setPolicyId("policyId123");
		rule.setRuleName("ruleName123");
		rule.setTargetType("targetType123");
		rule.setAssetGroup("assetGroup123");
		rule.setAlexaKeyword("alexaKeyword123");
		rule.setRuleParams("{\"assetGroup\":\"aws\",\"policyId\":\"PacMan_AWSCloudTrailConfig_version-1\",\"environmentVariables\":[{\"encrypt\":false,\"value\":\"123\",\"key\":\"abc\"}],\"ruleUUID\":\"22ce851c-7b6c-4986-9ba9-db97803b363a\",\"ruleType\":\"ManageRule\",\"pac_ds\":\"aws\",\"targetType\":\"cloudtrl\",\"params\":[{\"encrypt\":\"false\",\"value\":\"role/pac_ro\",\"key\":\"roleIdentifyingString\"},{\"encrypt\":\"false\",\"value\":\"check-for-aws-cloudtrail-config\",\"key\":\"ruleKey\"},{\"encrypt\":false,\"value\":\"critical\",\"key\":\"severity\"},{\"encrypt\":false,\"value\":\"security\",\"key\":\"ruleCategory\"}],\"ruleId\":\"PacMan_AWSCloudTrailConfig_version-1_AWSCloudTrailConfig_cloudtrl\",\"autofix\":false,\"alexaKeyword\":\"AWSCloudTrailConfig\",\"ruleRestUrl\":\"\"}");
		rule.setRuleFrequency("ruleFrequency123");
		rule.setRuleExecutable("ruleExecutable123");
		rule.setRuleRestUrl("ruleRestUrl123");
		rule.setRuleType("ruleType123");
		rule.setRuleArn("ruleArn123");
		rule.setStatus("status123");
		rule.setUserId("userId123");
		rule.setDisplayName("displayName123");
		rule.setCreatedDate(new Date());
		rule.setModifiedDate(new Date());
		return Optional.of(rule);
	}
	
	private AWSCredentials buildAWSCredentials() {
		AWSCredentials awsCredentials = new AWSCredentials();
		awsCredentials.setAccessKey("accessKey");
		awsCredentials.setSecretKey("secretKey");
		return awsCredentials;
	}
}
