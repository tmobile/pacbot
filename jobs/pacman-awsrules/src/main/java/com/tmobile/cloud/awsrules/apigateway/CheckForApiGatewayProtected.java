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
package com.tmobile.cloud.awsrules.apigateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.apigateway.AmazonApiGatewayClient;
import com.amazonaws.services.apigateway.model.GetMethodRequest;
import com.amazonaws.services.apigateway.model.GetMethodResult;
import com.amazonaws.services.apigateway.model.GetResourcesRequest;
import com.amazonaws.services.apigateway.model.GetResourcesResult;
import com.amazonaws.services.apigateway.model.Method;
import com.amazonaws.services.apigateway.model.Resource;
import com.google.gson.Gson;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-api-gateway-protected", desc = "checks entirely for API gateway is protected or not", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class CheckForApiGatewayProtected extends BaseRule {
    private static final Logger logger = LoggerFactory
            .getLogger(CheckForApiGatewayProtected.class);
    String apiMethodsDes = null;

    /**
     * The method will get triggered from Rule Engine with following parameters
     *  
     * @param ruleParam
     * 
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     * 
     *            authType : Comma separated list of AWS Auth types <br>
     * <br>
     * 
     *            splitterChar : The splitter character used to split the auth
     *            type <br>
     * <br>
     * 
     *            ruleKey : check-for-api-gateway-protected <br>
     * <br>
     * 
     *            severity : Enter the value of severity <br>
     * <br>
     * 
     *            ruleCategory : Enter the value of category <br>
     * <br>
     * 
     *            roleIdentifyingString : Configure it as role/pacbot_ro <br>
     * <br>
     * 
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided y execution engine
     *
     */

    @Override
    public RuleResult execute(Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {
        logger.debug("========CheckForApiGatewayProtected started=========");

        Annotation annotation = null;
        String roleIdentifyingString = ruleParam
                .get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
        String resourceId = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
        String authType = ruleParam.get(PacmanRuleConstants.AUTH_TYPE);
        String tagsSplitter = ruleParam.get(PacmanSdkConstants.SPLITTER_CHAR);

        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

        MDC.put("executionId", ruleParam.get("executionId"));
        // this is the logback Mapped Diagnostic Contex
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); 
        // this is the logback Mapped Diagnostic Contex

        Gson gson = new Gson();
        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
        Map<String, Object> authTypeMap = new HashMap<>();
        if (!PacmanUtils.doesAllHaveValue(authType, tagsSplitter, severity,
                category)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }
        AmazonApiGatewayClient apiGatewayClient = getClient(
                roleIdentifyingString, ruleParam);

        List<String> authTypeList = PacmanUtils.splitStringToAList(authType,
                tagsSplitter);
        try {
            for (Resource resource : getResourceList(resourceId,
                    apiGatewayClient)) {
                Map<String, Method> httpMethodMap = resource
                        .getResourceMethods();

                if (httpMethodMap != null) {
                    for (Map.Entry<String, Method> httpMethod : httpMethodMap
                            .entrySet()) {
                        GetMethodResult methodResult = getGetMethodResult(
                                resource, resourceId, httpMethod,
                                apiGatewayClient);
                        String authorisationType = methodResult
                                .getAuthorizationType();
                        boolean isApiKeyRequired = methodResult
                                .getApiKeyRequired();
                        boolean isAuthType = false;
                        logger.info("========Checking is auth type true=========");
                        for (String authorType : authTypeList) {
                            if (authorisationType.equalsIgnoreCase(authorType)) {
                                isAuthType = true;
                            } else {
                                authTypeMap.put(authorType, "Not found");
                            }
                        }
                        if (!(isAuthType || isApiKeyRequired)) {

                            // create annotations now
                            if (annotation == null) { 
                                // annotation will build for first method only for 2nd method it just adds method details.
                                annotation = Annotation.buildAnnotation(
                                        ruleParam, Annotation.Type.ISSUE);
                                annotation.put(PacmanRuleConstants.SEVERITY,
                                        severity);
                                annotation.put(PacmanRuleConstants.CATEGORY,
                                        category);
                            }

                            formateAPIMethodDetails(resource.getPath(),
                                    httpMethod.getKey(),
                                    String.valueOf(isApiKeyRequired));
                        }
                    }
                }
            }

        } catch (Exception exception) {
            logger.error("error: ", exception);
            throw new RuleExecutionFailedExeption(exception.getMessage());
        }
        if (annotation == null) {
            logger.debug("========CheckForApiGatewayProtected ended=========");
            return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                    PacmanRuleConstants.SUCCESS_MESSAGE);
        } else {
            annotation.put("APIMethods", apiMethodsDes);
            annotation.put(PacmanSdkConstants.DESCRIPTION,
                    "API gateway is not protected.And the API methods description are "
                            + apiMethodsDes);

            issue.put(PacmanRuleConstants.VIOLATION_REASON,
                    "API gateway is not protected");
            issue.put("apiMethods", apiMethodsDes);
            issue.put("authType", gson.toJson(authTypeMap));
            issueList.add(issue);
            annotation.put("issueDetails", issueList.toString());

            apiMethodsDes = null; // clear api description
            logger.debug("========CheckForApiGatewayProtected ended with annotation : {}=========",annotation);
            return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
                    PacmanRuleConstants.FAILURE_MESSAGE, annotation);
        }
    }

    /**
     * This method appends the apiMethodDes using below params.
     * 
     * @param path
     * @param method
     * @param apiKey
     */
    private void formateAPIMethodDetails(String path, String method,
            String apiKey) {
        if (apiMethodsDes == null) {
            apiMethodsDes = "Path:" + path + " " + "Method:" + method + " "
                    + "API KEY:" + apiKey + " \n";
        } else {
            apiMethodsDes = apiMethodsDes + "Path:" + path + " " + "Method:"
                    + method + " " + "API KEY:" + apiKey + " \n";
        }

    }

    private List<Resource> getResourceList(String resourceId,
            AmazonApiGatewayClient apiGatewayClient) {
        GetResourcesRequest resourcesRequest = new GetResourcesRequest();
        resourcesRequest.setRestApiId(resourceId);
        GetResourcesResult resourceResult = apiGatewayClient
                .getResources(resourcesRequest);
        return resourceResult.getItems();
    }

    @Override
    public String getHelpText() {
        return "This rule checks id the mandatory parameters are preset in Ec2 instance";
    }

    private AmazonApiGatewayClient getClient(String roleIdentifyingString,
            Map<String, String> ruleParam) {
        Map<String, Object> map = null;
        AmazonApiGatewayClient apiGatewayClient = null;
        try {
            map = getClientFor(AWSService.APIGTW, roleIdentifyingString,
                    ruleParam);
            apiGatewayClient = (AmazonApiGatewayClient) map
                    .get(PacmanSdkConstants.CLIENT);
        } catch (UnableToCreateClientException e) {
            logger.error("unable to get client for following input", e);
            throw new InvalidInputException(e.getMessage());
        }
        return apiGatewayClient;
    }

    private GetMethodResult getGetMethodResult(Resource resource,
            String resourceId, Map.Entry<String, Method> httpMethod,
            AmazonApiGatewayClient apiGatewayClient) {
        GetMethodRequest methodRequest = new GetMethodRequest();
        methodRequest.setResourceId(resource.getId());
        methodRequest.setRestApiId(resourceId);
        methodRequest.setHttpMethod(httpMethod.getKey());
        return apiGatewayClient.getMethod(methodRequest);
    }
}
