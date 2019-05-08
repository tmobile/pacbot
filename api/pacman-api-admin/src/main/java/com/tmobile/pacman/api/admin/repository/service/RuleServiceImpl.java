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

import static com.tmobile.pacman.api.admin.common.AdminConstants.CLOUDWATCH_RULE_DISABLE_FAILURE;
import static com.tmobile.pacman.api.admin.common.AdminConstants.CLOUDWATCH_RULE_ENABLE_FAILURE;
import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.cloudwatchevents.model.DisableRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.DisableRuleResult;
import com.amazonaws.services.cloudwatchevents.model.EnableRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.EnableRuleResult;
import com.amazonaws.services.cloudwatchevents.model.PutRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.PutRuleResult;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsRequest;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsResult;
import com.amazonaws.services.cloudwatchevents.model.RuleState;
import com.amazonaws.services.cloudwatchevents.model.Target;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.AddPermissionRequest;
import com.amazonaws.services.lambda.model.GetPolicyRequest;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ResourceNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.config.PacmanConfiguration;
import com.tmobile.pacman.api.admin.domain.CreateUpdateRuleDetails;
import com.tmobile.pacman.api.admin.domain.RuleProjection;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.RuleCategoryRepository;
import com.tmobile.pacman.api.admin.repository.RuleRepository;
import com.tmobile.pacman.api.admin.repository.model.Rule;
import com.tmobile.pacman.api.admin.repository.model.RuleCategory;
import com.tmobile.pacman.api.admin.service.AmazonClientBuilderService;
import com.tmobile.pacman.api.admin.service.AwsS3BucketService;
import com.tmobile.pacman.api.admin.util.AdminUtils;

/**
 * Rule Service Implementations
 */
@Service
public class RuleServiceImpl implements RuleService {

	private static final Logger log = LoggerFactory.getLogger(RuleServiceImpl.class);
	
	@Autowired
	private PacmanConfiguration config;
	
	@Autowired
	private AmazonClientBuilderService amazonClient; 
	
	@Autowired
	private AwsS3BucketService awsS3BucketService;
	
	@Autowired
	private RuleRepository ruleRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private RuleCategoryRepository ruleCategoryRepository;
	
	@Override
	public List<Rule> getAllRulesByTargetType(String targetType) {
		return ruleRepository.findByTargetTypeIgnoreCase(targetType);
	}
	
	@Override
	public List<RuleProjection> getAllRulesByTargetTypeName(String targetType) {
		return ruleRepository.findByTargetType(targetType);
	}
	
	@Override
	public List<RuleProjection> getAllRulesByTargetTypeAndNotInRuleIdList(final String targetType, final List<String> ruleIdList) {
		return ruleRepository.findByTargetTypeAndRuleIdNotIn(targetType, ruleIdList);
	}

	@Override
	public List<RuleProjection> getAllRulesByTargetTypeAndRuleIdList(final String targetType, final List<String> ruleIdList) {
		return ruleRepository.findByTargetTypeAndRuleIdIn(targetType, ruleIdList);
	}
	
	@Override
	public Rule getByRuleId(String ruleId) {
		return ruleRepository.findByRuleId(ruleId);
	}

	@Override
	public Page<Rule> getRules(final String searchTerm, final int page, final int size) {
		return ruleRepository.findAll(searchTerm.toLowerCase(), PageRequest.of(page, size));
	}
	
	@Override
	public Collection<String> getAllAlexaKeywords() {
		return ruleRepository.getAllAlexaKeywords();
	}
	
	@Override
	public Collection<String> getAllRuleIds() {
		return ruleRepository.getAllRuleIds();
	}

	@Override
	public String createRule(final MultipartFile fileToUpload, final CreateUpdateRuleDetails ruleDetails, final String userId) throws PacManException {
		checkRuleTypeNotServerlessOrManaged(ruleDetails, fileToUpload);
		return addRuleInstance(fileToUpload, ruleDetails, userId);
	}
	
	@Override
	public String updateRule(MultipartFile fileToUpload, CreateUpdateRuleDetails updateRuleDetails, String userId) throws PacManException {
		checkRuleTypeNotServerlessOrManaged(updateRuleDetails, fileToUpload);
		return updateRuleInstance(fileToUpload, updateRuleDetails, userId);
	}
	
	@Override
	public String invokeRule(String ruleId, List<Map<String, Object>> ruleOptionalParams) {
		Rule ruleDetails = ruleRepository.findById(ruleId).get();
		AWSLambda awsLambdaClient = amazonClient.getAWSLambdaClient(config.getRule().getLambda().getRegion());
		String invocationId = AdminUtils.getReferenceId();
		boolean invokeStatus = invokeRule(awsLambdaClient, ruleDetails, invocationId, ruleOptionalParams);
		if(invokeStatus) {
			return invocationId;
		} else {
			return null;
		}
	}
	
	@Override
	public String enableDisableRule(final String ruleId, final String action, final String userId) throws PacManException {
		if(ruleRepository.existsById(ruleId)) {
			Rule existingRule = ruleRepository.findById(ruleId).get();
			if(action.equalsIgnoreCase("enable")) {
				return enableCloudWatchRule(existingRule, userId, RuleState.ENABLED);
			} else {
				return disableCloudWatchRule(existingRule, userId, RuleState.DISABLED);
			}
		} else {
			throw new PacManException(String.format(AdminConstants.RULE_ID_NOT_EXITS, ruleId));
		}
	}
	
	private String disableCloudWatchRule(Rule existingRule, String userId, RuleState ruleState) throws PacManException {
		DisableRuleRequest disableRuleRequest = new DisableRuleRequest().withName(existingRule.getRuleUUID());
		DisableRuleResult disableRuleResult = amazonClient.getAmazonCloudWatchEvents(config.getRule().getLambda().getRegion()).disableRule(disableRuleRequest);
		if (disableRuleResult.getSdkHttpMetadata() != null) {
			if(disableRuleResult.getSdkHttpMetadata().getHttpStatusCode() == 200) {
				existingRule.setUserId(userId);
				existingRule.setModifiedDate(new Date());
				existingRule.setStatus(ruleState.name());
				ruleRepository.save(existingRule);
				return String.format(AdminConstants.RULE_DISABLE_ENABLE_SUCCESS, ruleState.name().toLowerCase());
			} else {
				throw new PacManException(CLOUDWATCH_RULE_DISABLE_FAILURE);
			}
		} else {
			throw new PacManException(CLOUDWATCH_RULE_DISABLE_FAILURE);
		}
	}

	private String enableCloudWatchRule(Rule existingRule, String userId, RuleState ruleState) throws PacManException {
		AWSLambda awsLambdaClient = amazonClient.getAWSLambdaClient(config.getRule().getLambda().getRegion());
		if (!checkIfPolicyAvailableForLambda(config.getRule().getLambda().getFunctionName(), awsLambdaClient)) {
			createPolicyForLambda(config.getRule().getLambda().getFunctionName(), awsLambdaClient);
		}
		
		EnableRuleRequest enableRuleRequest = new EnableRuleRequest().withName(existingRule.getRuleUUID());
		EnableRuleResult enableRuleResult = amazonClient.getAmazonCloudWatchEvents(config.getRule().getLambda().getRegion()).enableRule(enableRuleRequest);
		if (enableRuleResult.getSdkHttpMetadata() != null) {
			if(enableRuleResult.getSdkHttpMetadata().getHttpStatusCode() == 200) {
				existingRule.setUserId(userId);
				existingRule.setModifiedDate(new Date());
				existingRule.setStatus(ruleState.name());
				ruleRepository.save(existingRule);
				invokeRule(awsLambdaClient, existingRule, null, null);
				return String.format(AdminConstants.RULE_DISABLE_ENABLE_SUCCESS, ruleState.name().toLowerCase());
			}else {
				throw new PacManException(CLOUDWATCH_RULE_ENABLE_FAILURE);
			}
		} else {
			throw new PacManException(CLOUDWATCH_RULE_ENABLE_FAILURE);
		}
	}

	private void checkRuleTypeNotServerlessOrManaged(CreateUpdateRuleDetails ruleDetails, MultipartFile fileToUpload) throws PacManException {
		if (isRuleTypeNotServerlessOrManaged(ruleDetails.getRuleType()) && ruleDetails.getIsFileChanged()) {
			if(fileToUpload.isEmpty()) {
				throw new PacManException(AdminConstants.JAR_FILE_MISSING);
			}
		}
	}

	private String updateRuleInstance(final MultipartFile fileToUpload, CreateUpdateRuleDetails ruleDetails, String userId) throws PacManException {
		if(ruleDetails != null) {
			if(isRuleIdExits(ruleDetails.getRuleId())) {		
				Date currentDate = new Date();
				Rule updateRuleDetails = ruleRepository.findById(ruleDetails.getRuleId()).get();
				ruleDetails.setTargetType(updateRuleDetails.getTargetType());
				ruleDetails.setPolicyId(updateRuleDetails.getPolicyId());
				ruleDetails.setDataSource(retrieveDataSource(updateRuleDetails));
				String ruleParams = buildAndGetRuleParams(ruleDetails, updateRuleDetails.getRuleUUID(), false);
				updateRuleDetails.setRuleParams(ruleParams);
				updateRuleDetails.setRuleFrequency(ruleDetails.getRuleFrequency());
				updateRuleDetails.setRuleExecutable(ruleDetails.getRuleExecutable());
				updateRuleDetails.setUserId(userId);
				updateRuleDetails.setDisplayName(ruleDetails.getDisplayName());
				updateRuleDetails.setAssetGroup(ruleDetails.getAssetGroup());
				updateRuleDetails.setAlexaKeyword(ruleDetails.getAlexaKeyword());
				updateRuleDetails.setModifiedDate(currentDate);
				updateRuleDetails.setRuleType(ruleDetails.getRuleType());
				updateRuleDetails.setRuleRestUrl(ruleDetails.getRuleRestUrl());
				updateRuleDetails.setSeverity(ruleDetails.getSeverity());
				updateRuleDetails.setCategory(ruleDetails.getCategory());
				createUpdateCloudWatchEventRule(updateRuleDetails);
				if (ruleDetails.getIsFileChanged() && ruleDetails.getRuleType().equalsIgnoreCase("Classic")) {
					createUpdateRuleJartoS3Bucket(fileToUpload, updateRuleDetails.getRuleUUID());
				}
			} else {
				throw new PacManException(String.format(AdminConstants.RULE_ID_NOT_EXITS, (ruleDetails.getRuleId() == null ? "given" : ruleDetails.getRuleId())));
			}
		} else {
			throw new PacManException("Invalid Rule Instance, please provide valid details.");
		}
		return AdminConstants.RULE_CREATION_SUCCESS;
	}
	
	private String retrieveDataSource(final Rule updateRuleDetails) {
		Map<String, Object> ruleParams;
		try {
			ruleParams = mapper.readValue(updateRuleDetails.getRuleParams(), new TypeReference<Map<String, Object>>(){});
			return String.valueOf(ruleParams.get("pac_ds"));
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			return StringUtils.EMPTY;
		} 
	}

	private String addRuleInstance(final MultipartFile fileToUpload, CreateUpdateRuleDetails ruleDetails, String userId) throws PacManException {
		if(ruleDetails != null) {
			Date currentDate = new Date();
			if(!isRuleIdExits(ruleDetails.getRuleId())) {		
				Rule newRuleDetails = new Rule();
				String ruleUUID = UUID.randomUUID().toString();
				newRuleDetails.setRuleId(ruleDetails.getRuleId());
				newRuleDetails.setPolicyId(ruleDetails.getPolicyId());
				newRuleDetails.setRuleName(ruleDetails.getRuleName());
				newRuleDetails.setTargetType(ruleDetails.getTargetType());
				String ruleParams = buildAndGetRuleParams(ruleDetails, ruleUUID, true);
				newRuleDetails.setRuleParams(ruleParams);
				newRuleDetails.setRuleFrequency(ruleDetails.getRuleFrequency());
				newRuleDetails.setRuleExecutable(ruleDetails.getRuleExecutable());
				newRuleDetails.setDisplayName(ruleDetails.getDisplayName());
				newRuleDetails.setUserId(userId);
				newRuleDetails.setStatus(RuleState.ENABLED.name().toUpperCase());
				newRuleDetails.setAssetGroup(ruleDetails.getAssetGroup());
				newRuleDetails.setAlexaKeyword(ruleDetails.getAlexaKeyword());
				newRuleDetails.setCreatedDate(currentDate);
				newRuleDetails.setModifiedDate(currentDate);
				newRuleDetails.setRuleUUID(ruleUUID);
				newRuleDetails.setRuleType(ruleDetails.getRuleType());
				newRuleDetails.setRuleRestUrl(ruleDetails.getRuleRestUrl());
				newRuleDetails.setSeverity(ruleDetails.getSeverity());
				newRuleDetails.setCategory(ruleDetails.getCategory());
				createUpdateCloudWatchEventRule(newRuleDetails);
				if (ruleDetails.getIsFileChanged() && ruleDetails.getRuleType().equalsIgnoreCase("Classic")) {
					createUpdateRuleJartoS3Bucket(fileToUpload, ruleUUID);
				}
			} else {
				throw new PacManException(String.format(AdminConstants.RULE_ID_EXITS,  (ruleDetails.getRuleId() == null ? "given" : ruleDetails.getRuleId())));
			}
		} else {
			throw new PacManException("Invalid Rule Instance, please provide valid details.");
		}
		return AdminConstants.RULE_CREATION_SUCCESS;
	}

	private void createUpdateCloudWatchEventRule(final Rule ruleDetails) {	
		try {
			PutRuleRequest ruleRequest = new PutRuleRequest()
				.withName(ruleDetails.getRuleUUID())
		    	.withDescription(ruleDetails.getRuleId());
				 ruleRequest.withScheduleExpression("cron(".concat(ruleDetails.getRuleFrequency()).concat(")"));

			AWSLambda awsLambdaClient = amazonClient.getAWSLambdaClient(config.getRule().getLambda().getRegion());
			
			if (!checkIfPolicyAvailableForLambda(config.getRule().getLambda().getFunctionName(), awsLambdaClient)) {
				createPolicyForLambda(config.getRule().getLambda().getFunctionName(), awsLambdaClient);
			}
		
			if (ruleDetails.getStatus().equalsIgnoreCase(RuleState.ENABLED.name())) {
				ruleRequest.setState(RuleState.ENABLED);
			} else {
				ruleRequest.setState(RuleState.DISABLED);
			}

			PutRuleResult ruleResult = amazonClient.getAmazonCloudWatchEvents(config.getRule().getLambda().getRegion()).putRule(ruleRequest);
			if (ruleResult.getRuleArn() != null) {
				ruleDetails.setRuleArn(ruleResult.getRuleArn());
				boolean isLambdaFunctionLinked = linkTargetWithRule(ruleDetails);
				if(!isLambdaFunctionLinked) { 
					//message.put(RuleConst.SUCCESS.getName(), false);
					//message.put(RuleConst.MESSAGE.getName(), "Unexpected Error Occured!");
				} else {
					ruleRepository.save(ruleDetails);
					invokeRule(awsLambdaClient, ruleDetails, null, null);
				}
			} else {
				//message.put(RuleConst.SUCCESS.getName(), false);
				//message.put(RuleConst.MESSAGE.getName(), "Unexpected Error Occured!");
			}
		} catch(Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
		}
	}
	
	@Override
	public Map<String, Object> invokeAllRules(List<String> ruleIds) {
		AWSLambda awsLambdaClient = amazonClient.getAWSLambdaClient(config.getRule().getLambda().getRegion());
		Map<String, Object> responseLists = Maps.newHashMap();
		List<String> successList = Lists.newArrayList();
		List<String> failedList = Lists.newArrayList();
		for(String ruleId: ruleIds) {
			Rule ruleInstance = ruleRepository.findById(ruleId).get();
			boolean isInvoked = invokeRule(awsLambdaClient, ruleInstance, null, Lists.newArrayList());
			if(isInvoked) {
				successList.add(ruleId);
			} else {
				failedList.add(ruleId);
			}
		}
		responseLists.put("successList", successList);
		responseLists.put("failedList", failedList);
		return responseLists;
	}
	
	private boolean invokeRule(AWSLambda awsLambdaClient, Rule ruleDetails, String invocationId, List<Map<String, Object>> additionalRuleParams) {
		String ruleParams = ruleDetails.getRuleParams();
		if(invocationId != null) {
			Map<String, Object> ruleParamDetails;
			try {
				ruleParamDetails = mapper.readValue(ruleDetails.getRuleParams(), new TypeReference<Map<String, Object>>(){});
				ruleParamDetails.put("invocationId", invocationId);
				ruleParamDetails.put("additionalParams", mapper.writeValueAsString(additionalRuleParams));
				ruleParams = mapper.writeValueAsString(ruleParamDetails);
			} catch (Exception exception) {
				log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			} 
		}
		String functionName = config.getRule().getLambda().getFunctionName();
		ByteBuffer payload = ByteBuffer.wrap(ruleParams.getBytes());
		InvokeRequest invokeRequest = new InvokeRequest().withFunctionName(functionName).withPayload(payload);
		InvokeResult invokeResult = awsLambdaClient.invoke(invokeRequest);
		if (invokeResult.getStatusCode() == 200) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean linkTargetWithRule(final Rule rule) {
		Target target = new Target()
	    .withId(config.getRule().getLambda().getTargetId())
		.withArn(config.getRule().getLambda().getFunctionArn())
		.withInput(rule.getRuleParams());
		
		PutTargetsRequest targetsRequest = new PutTargetsRequest()
	    .withTargets(target)
	    .withRule(rule.getRuleUUID());
		
		try {
			PutTargetsResult targetsResult = amazonClient.getAmazonCloudWatchEvents(config.getRule().getLambda().getRegion()).putTargets(targetsRequest);
			return (targetsResult.getFailedEntryCount()==0);
		} catch(Exception exception) {
			return false;
		}
	}
	
	private void createPolicyForLambda(final String lambdaFunctionName, final AWSLambda lambdaClient) {
		AddPermissionRequest addPermissionRequest = new AddPermissionRequest()
		.withFunctionName(lambdaFunctionName)
		.withPrincipal(config.getRule().getLambda().getPrincipal())
		.withStatementId("sid-".concat(config.getRule().getLambda().getTargetId()))
		.withAction(config.getRule().getLambda().getActionEnabled());
		lambdaClient.addPermission(addPermissionRequest);
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
	
	private boolean isRuleTypeNotServerlessOrManaged(final String ruleType) {
		String ruleTypeToCheck = ruleType.replace(" ", StringUtils.EMPTY);
		return (!ruleTypeToCheck.equalsIgnoreCase(AdminConstants.SERVERLESS_RULE_TYPE) && !ruleTypeToCheck.equalsIgnoreCase(AdminConstants.MANAGED_RULE_TYPE));
	}

	private boolean createUpdateRuleJartoS3Bucket(MultipartFile fileToUpload, String ruleUUID) {
		return awsS3BucketService.uploadFile(amazonClient.getAmazonS3(config.getRule().getS3().getBucketRegion()), fileToUpload, config.getJob().getS3().getBucketName()+"/pacbot", ruleUUID.concat(".jar"));
	}

	public boolean isRuleIdExits(String ruleId) {
		return ruleRepository.findByRuleId(ruleId) != null;
	}

	@SuppressWarnings("unchecked")
	private String buildAndGetRuleParams(final CreateUpdateRuleDetails ruleDetails, final String ruleUUID, final boolean isCreatedNew) {
		Map<String, Object> newJobParams;
		try {
			newJobParams = mapper.readValue(ruleDetails.getRuleParams(), new TypeReference<Map<String, Object>>(){});
			newJobParams.put("ruleId", ruleDetails.getRuleId());
			newJobParams.put("autofix", ruleDetails.isAutofixEnabled());
			newJobParams.put("alexaKeyword", ruleDetails.getAlexaKeyword());
			newJobParams.put("ruleRestUrl", ruleDetails.getRuleRestUrl());
			newJobParams.put("targetType", ruleDetails.getTargetType());
			newJobParams.put("pac_ds", ruleDetails.getDataSource());
			newJobParams.put("policyId", ruleDetails.getPolicyId());
			newJobParams.put("assetGroup", ruleDetails.getAssetGroup());
			newJobParams.put("ruleUUID", ruleUUID);
			newJobParams.put("ruleType", ruleDetails.getRuleType());
			Map<String, Object> severity = new HashMap<>();
			severity.put("key", "severity");
			severity.put("value", ruleDetails.getSeverity());
			severity.put("encrypt", false);
			Map<String, Object> category = new HashMap<>();
			category.put("key", "ruleCategory");
			category.put("value", ruleDetails.getCategory());
			category.put("encrypt", false);
			List<Map<String, Object>> environmentVariables = (List<Map<String, Object>>) newJobParams.get("environmentVariables");
			List<Map<String, Object>> params = (List<Map<String, Object>>) newJobParams.get("params");
			params.add(severity);
			params.add(category);
			newJobParams.put("environmentVariables", encryptDecryptValues(environmentVariables, ruleUUID, isCreatedNew));
			newJobParams.put("params", encryptDecryptValues(params, ruleUUID, isCreatedNew));
			return mapper.writeValueAsString(newJobParams);
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
		}
		return ruleDetails.getRuleParams();
	}

	private List<Map<String, Object>> encryptDecryptValues(List<Map<String, Object>> ruleParams, String ruleUUID, boolean isCreatedNew) {
		for (int index = 0; index < ruleParams.size(); index++) {
			Map<String, Object> keyValue = ruleParams.get(index);
			if (isCreatedNew) {
				String isToBeEncrypted = keyValue.get("encrypt").toString();
				if (StringUtils.isNotBlank(isToBeEncrypted) && Boolean.parseBoolean(isToBeEncrypted)) {
					try {
						keyValue.put("value", AdminUtils.encrypt(keyValue.get("value").toString(), ruleUUID));
					} catch (Exception exception) {
						keyValue.put("value", keyValue.get("value").toString());
					}
				}
			} else {
				if (keyValue.get("isValueNew") != null) {
					String isValueNew = keyValue.get("isValueNew").toString();
					String isToBeEncrypted = keyValue.get("encrypt").toString();
					if (StringUtils.isNotBlank(isValueNew) && Boolean.parseBoolean(isValueNew)) {
						if (StringUtils.isNotBlank(isToBeEncrypted) && Boolean.parseBoolean(isToBeEncrypted)) {
							try {
								keyValue.put("value", AdminUtils.encrypt(keyValue.get("value").toString(), ruleUUID));
							} catch (Exception exception) {
								keyValue.put("value", keyValue.get("value").toString());
							}
						}
					}
				}
			}
		}
		return ruleParams;
	}
	
	@Override
	public List<RuleCategory> getAllRuleCategories() throws PacManException{
		return ruleCategoryRepository.findAll();
	}
}
