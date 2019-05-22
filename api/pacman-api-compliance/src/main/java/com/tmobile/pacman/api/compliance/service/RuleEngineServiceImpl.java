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
package com.tmobile.pacman.api.compliance.service;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.AWSLambdaAsyncClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.compliance.repository.PacRuleEngineAutofixActionsRepository;
import com.tmobile.pacman.api.compliance.repository.model.PacRuleEngineAutofixActions;
import com.tmobile.pacman.api.compliance.repository.model.RuleInstance;
import com.tmobile.pacman.api.compliance.util.CommonUtil;

/**
 * The Class RuleEngineServiceImpl.
 */
@Service
public class RuleEngineServiceImpl implements RuleEngineService, Constants {
    
    /** The log. */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /** The rule lambda function name. */
    @Value("${rule-engine.invoke.url}")
    private String ruleLambdaFunctionName;
    
    /** The rule aws access key. */
    private String ruleAwsAccessKey = "pacman.rule.access.keyA";
    
    /** The rule aws secret key. */
    private String ruleAwsSecretKey = "pacman.rule.secret.keyA";
    
    /** The additional params. */
    private String additionalParams = "additionalParams";

    /** The system config service. */
    @Autowired
    private SystemConfigurationService systemConfigService;

    /** The rule instance service. */
    @Autowired
    private RuleInstanceService ruleInstanceService;

    /** The rule engine autofix repository. */
    @Autowired
    private PacRuleEngineAutofixActionsRepository ruleEngineAutofixRepository;

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.RuleEngineService#runRule(java.lang.String, java.util.Map)
     */
    @Override
    public void runRule(final String ruleId, Map<String, String> runTimeParams)
            throws ServiceException {
        Boolean isRuleInvocationSuccess = invokeRule(ruleId, runTimeParams);
        if (!isRuleInvocationSuccess) {
            throw new ServiceException("Rule Invocation Failed");
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.RuleEngineService#getLastAction(java.lang.String)
     */
    @Override
    public Map<String, Object> getLastAction(final String resourceId) {
        Map<String, Object> response = Maps.newHashMap();
        SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    	dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            List<String> lastActions = Lists.newArrayList();
            List<PacRuleEngineAutofixActions> pacRuleEngineAutofixActions = ruleEngineAutofixRepository
                    .findLastActionByResourceId(resourceId);
            pacRuleEngineAutofixActions.forEach(autofixLastAction -> {
			lastActions.add(dateFormatUTC.format(autofixLastAction.getLastActionTime()));
            });
            if (lastActions.isEmpty()) {
                response.put(RESPONSE_CODE, 0);
                response.put(LAST_ACTIONS, Lists.newArrayList());
                response.put(MESSAGE_KEY, "Last action not found!!!");
            } else {
                response.put(RESPONSE_CODE, 1);
                response.put(MESSAGE_KEY, "Last action found!!!");
                response.put(LAST_ACTIONS, lastActions);
            }
        } catch (Exception e) {
            response.put(RESPONSE_CODE, 0);
            response.put(LAST_ACTIONS, Lists.newArrayList());
            response.put(MESSAGE_KEY, "Unexpected error occurred!!!");
        }
        return response;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.RuleEngineService#postAction(java.lang.String, java.lang.String)
     */
    @Override
    public void postAction(final String resourceId, final String action)
            throws ServiceException {
    	SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    	dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        PacRuleEngineAutofixActions autofixActions = new PacRuleEngineAutofixActions();
        autofixActions.setAction(action);
        autofixActions.setResourceId(resourceId);
        try {
			autofixActions.setLastActionTime(dateFormatUTC.parse(dateFormatUTC.format(new Date())));
		} catch (ParseException e) {
			throw new ServiceException("error parsing date");
		}
        ruleEngineAutofixRepository.save(autofixActions);
    }

    /**
     * Invoke rule.
     *
     * @param ruleId the rule id
     * @param runTimeParams the run time params
     * @return true, if successful
     */
    @SuppressWarnings("unchecked")
    private boolean invokeRule(final String ruleId,
            Map<String, String> runTimeParams) {
        RuleInstance ruleInstance = ruleInstanceService
                .getRuleInstanceByRuleId(ruleId);
        String ruleParams = ruleInstance.getRuleParams();
        Map<String, Object> ruleParamDetails = (Map<String, Object>) CommonUtil
                .deSerializeToObject(ruleParams);
        if (runTimeParams != null) {
            ruleParamDetails.put(additionalParams,
                    formatAdditionalParameters(runTimeParams));
        }
        ruleParams = CommonUtil.serializeToString(ruleParamDetails);
        AWSLambdaAsyncClient awsLambdaClient = getAWSLambdaAsyncClient();
        InvokeRequest invokeRequest = new InvokeRequest().withFunctionName(
                ruleLambdaFunctionName).withPayload(
                ByteBuffer.wrap(ruleParams.getBytes()));
        InvokeResult invokeResult = awsLambdaClient.invoke(invokeRequest);
        if (invokeResult.getStatusCode() == TWO_HUNDRED) {
            ByteBuffer responsePayload = invokeResult.getPayload();
            log.error("Return Value :" + new String(responsePayload.array()));
            return true;
        } else {
            log.error("Received a non-OK response from AWS: "
                    + invokeResult.getStatusCode());
            return false;
        }
    }

    /**
     * Format additional parameters.
     *
     * @param runTimeParams the run time params
     * @return the list
     */
    private List<Map<String, Object>> formatAdditionalParameters(
            Map<String, String> runTimeParams) {
        List<Map<String, Object>> additionalParamsList = Lists.newArrayList();
        runTimeParams.forEach((key, value) -> {
            Map<String, Object> additionalParam = Maps.newHashMap();
            additionalParam.put("key", key);
            additionalParam.put("value", value);
            additionalParam.put("encrypt", false);
            additionalParamsList.add(additionalParam);
        });
        return additionalParamsList;
    }

    /**
     * Gets the AWS lambda async client.
     *
     * @return the AWS lambda async client
     */
    @SuppressWarnings("deprecation")
    public AWSLambdaAsyncClient getAWSLambdaAsyncClient() {
        BasicAWSCredentials creds = new BasicAWSCredentials(
                systemConfigService.getConfigValue(ruleAwsAccessKey),
                systemConfigService.getConfigValue(ruleAwsSecretKey));
        return new AWSLambdaAsyncClient(creds);
    }
}
