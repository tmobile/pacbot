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
package com.tmobile.cloud.awsrules.s3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.s3.AmazonS3Client;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-s3-global-read-access", desc = "checks entirely for S3 Buckets With Global Read Permission", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class S3GlobalReadAccessRule extends BaseRule {
    private static final Logger logger = LoggerFactory.getLogger(S3GlobalReadAccessRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * ************* Following are the Rule Parameters********* <br>
     * <br>
     * 
     * apiKeyName : Value of API key <br>
     * <br>
     * 
     * apiKeyValue : Value of the API key name <br>
     * <br>
     * 
     * apiGWURL : API gateway URL <br>
     * <br>
     * 
     * ruleKey : check-for-s3-global-read-access <br>
     * <br>
     * 
     * severity : Enter the value of severity <br>
     * <br>
     * 
     * ruleCategory : Enter the value of category <br>
     * <br>
     * 
     * roleIdentifyingString : Configure it as role/pac_ro <br>
     * <br>
     * 
     * esServiceURL : Enter the Es url <br>
     * <br>
     * 
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided by execution engine
     *
     */

    public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
        logger.debug("========S3GlobalReadAccessRule started=========");
        Map<String, Object> map = null;
        AmazonS3Client awsS3Client = null;
        String roleIdentifyingString = ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
        String s3BucketName = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
        String apiKeyName = ruleParam.get(PacmanRuleConstants.API_KEY_NAME);
        String apiKeyValue = ruleParam.get(PacmanRuleConstants.API_KEY_VALUE);
        String apiGWURL = ruleParam.get(PacmanRuleConstants.APIGW_URL);
        String checkEsUrl = null;

        String formattedUrl = PacmanUtils.formatUrl(ruleParam, PacmanRuleConstants.ES_CHECK_SERVICE_SEARCH_URL_PARAM);

        if (!StringUtils.isEmpty(formattedUrl)) {
            checkEsUrl = formattedUrl;
        }

        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        String description = "Global read access detected";
        boolean aclFound = false;
        boolean bucketPolicyFound = false;
        Map<String, Boolean> s3HasOpenAccess;
        String checkId = ruleParam.get(PacmanRuleConstants.CHECK_ID);
        List<String> sourcesverified = new ArrayList<>();
        LinkedHashMap<String, Object> accessLevels = new LinkedHashMap<>();
        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

        /* check rule received all required values for rule execution */
        if (!PacmanUtils.doesAllHaveValue(apiGWURL, apiKeyValue, apiKeyName, severity, category, checkEsUrl)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }

        if (!resourceAttributes.isEmpty()) {

            logger.debug(resourceAttributes.get("region") + "=========================region");
            try {
                try {
                    map = getClientFor(AWSService.S3, roleIdentifyingString, ruleParam);
                    awsS3Client = (AmazonS3Client) map.get(PacmanSdkConstants.CLIENT);
                } catch (UnableToCreateClientException e) {
                    logger.error("unable to get client for following input", e);
                    throw new InvalidInputException(e.toString());
                }

                logger.info("check brute force , hit the url and check the response ");
                try {
                    String response = hitUrlUsingProxyAndGetResponse(s3BucketName,
                            ruleParam.get(PacmanRuleConstants.APIGW_URL),
                            ruleParam.get(PacmanRuleConstants.API_KEY_NAME),
                            ruleParam.get(PacmanRuleConstants.API_KEY_VALUE));
                    if (sniffPublicAccess(response)) {
                        description = description + " hitting url from outside vpc";
                        sourcesverified.add("HTTP Get-From Public IP");
                        accessLevels.put("HTTP Get-From Public IP", PacmanRuleConstants.PUBLIC);
                        return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,
                                PacmanUtils.createS3Annotation(ruleParam, description, severity, category,
                                        PacmanRuleConstants.READ_ACCESS, sourcesverified, accessLevels,
                                        resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID)));
                    } else {
                        sourcesverified.add("API Gateway");
                        accessLevels.put("APIGW", "private");
                    }
                } catch (Exception e) {
                    logger.error("unable to use brute force on resource " + s3BucketName, e);
                }

                logger.info("checking bucket has read access through ACL");
                if (PacmanUtils.checkACLAccess(awsS3Client, s3BucketName, PacmanRuleConstants.READ_ACCESS)) {
                    description = description + " through ACL";
                    sourcesverified.add("ACL");
                    accessLevels.put("ACL", PacmanRuleConstants.PUBLIC);

                    return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,
                            PacmanUtils.createS3Annotation(ruleParam, description, severity, category,
                                    PacmanRuleConstants.READ_ACCESS, sourcesverified, accessLevels,
                                    resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID)));

                } else if (isPolicyTrue(awsS3Client, s3BucketName, PacmanRuleConstants.READ_ACCESS)) {
                    sourcesverified.add("ACL");
                    accessLevels.put("ACL", "private");
                    description = description + PacmanRuleConstants.THROUGH_BUCKET_POLICY;
                    sourcesverified.add("BucketPolicy");
                    accessLevels.put("Bucket Policy", PacmanRuleConstants.PUBLIC);
                    return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,
                            PacmanUtils.createS3Annotation(ruleParam, description, severity, category,
                                    PacmanRuleConstants.READ_ACCESS, sourcesverified, accessLevels,
                                    resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID)));
                } else {
                    // check bucket is opened through TA
                    logger.info("checking S3 bucket has public access from Trusted Advisor");
                    String accountId = StringUtils.trim(resourceAttributes.get(PacmanRuleConstants.ACCOUNTID));

                    /*
                     * Check1 - From Trusted advisor check bucket has read
                     * access through ACL/BucketPolicy
                     */
                    s3HasOpenAccess = PacmanUtils.checkS3HasOpenAccess(checkId, accountId, checkEsUrl, s3BucketName);
                    if (!s3HasOpenAccess.isEmpty() && s3HasOpenAccess != null) {
                        aclFound = s3HasOpenAccess.get("acl_found");
                        bucketPolicyFound = s3HasOpenAccess.get("bucketPolicy_found");
                        description = description + "through Trusted Advisor";
                        if (aclFound) {
                            accessLevels.put("ACL", PacmanRuleConstants.PUBLIC);
                        } else if(bucketPolicyFound) {
                            accessLevels.put("Bucket Policy", PacmanRuleConstants.PUBLIC);
                        }
                        if(aclFound ||bucketPolicyFound){
                        sourcesverified.add("Trusted advisor");
                        return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,
                                PacmanUtils.createS3Annotation(ruleParam, description, severity, category,
                                        PacmanRuleConstants.READ_ACCESS, sourcesverified, accessLevels,
                                        resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID)));
                        }
                    }
                }
                /*
                 * if (aclFound) {
                 * logger.info("checking bucket has read access through ACL");
                 * if (PacmanUtils.checkACLAccess(awsS3Client, s3BucketName,
                 * PacmanRuleConstants.READ_ACCESS)) { description = description
                 * + " through ACL"; return new RuleResult(
                 * PacmanSdkConstants.STATUS_FAILURE,
                 * PacmanRuleConstants.FAILURE_MESSAGE,
                 * PacmanUtils.createS3Annotation(ruleParam, description,
                 * severity, category, PacmanRuleConstants.READ_ACCESS,
                 * sourcesverified, accessLevels, resourceAttributes
                 * .get(PacmanRuleConstants.RESOURCE_ID)));
                 * 
                 * } else if (bucketPolicyFound && isPolicyTrue(awsS3Client,
                 * s3BucketName, PacmanRuleConstants.READ_ACCESS)) { description
                 * = description + PacmanRuleConstants.THROUGH_BUCKET_POLICY;
                 * return new RuleResult( PacmanSdkConstants.STATUS_FAILURE,
                 * PacmanRuleConstants.FAILURE_MESSAGE, PacmanUtils
                 * .createS3Annotation( ruleParam, description, severity,
                 * category, PacmanRuleConstants.READ_ACCESS, sourcesverified,
                 * accessLevels, resourceAttributes
                 * .get(PacmanRuleConstants.RESOURCE_ID))); } } else { if
                 * (isPolicyTrue(awsS3Client, s3BucketName,
                 * PacmanRuleConstants.READ_ACCESS)) { description = description
                 * + PacmanRuleConstants.THROUGH_BUCKET_POLICY; return new
                 * RuleResult( PacmanSdkConstants.STATUS_FAILURE,
                 * PacmanRuleConstants.FAILURE_MESSAGE,
                 * PacmanUtils.createS3Annotation(ruleParam, description,
                 * severity, category, PacmanRuleConstants.READ_ACCESS,
                 * sourcesverified, accessLevels, resourceAttributes
                 * .get(PacmanRuleConstants.RESOURCE_ID))); }
                 */
                // }
                // }
            } catch (Exception e1) {
                logger.error("unable to get the details", e1);
                throw new InvalidInputException(e1.getMessage());
            }
        }
        logger.info(s3BucketName, "This Bucket is not publicly accessable");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);

    }

    /**
     * @param response
     * @return
     */
    private boolean sniffPublicAccess(String response) {
        JSONObject jsonObject = XML.toJSONObject(response);
        return "NoSuchKey".equals(jsonObject.getJSONObject("Error").get("Code"));
    }

    /**
     * @param s3BucketName
     * @param string
     * @param string2
     * @param string3
     * @return
     * @throws Exception
     */
    private String hitUrlUsingProxyAndGetResponse(String s3BucketName, String url, String headerName, String headerValue)
            throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put(headerName, headerValue);
        return PacmanUtils.doHttpGet(String.format(url, s3BucketName), headers);
    }

    private boolean isPolicyTrue(AmazonS3Client awsS3Client, String s3BucketName, String accessType) {
        boolean isReadAccess = false;
        Map<String, Boolean> checkPolicyMap = PacmanUtils.getPublicAccessPolicy(awsS3Client, s3BucketName, accessType);
        if (!checkPolicyMap.isEmpty()) {
            isReadAccess = checkPolicyMap.get("Read");
        }
        return isReadAccess;
    }

    public String getHelpText() {
        return "This rule checks s3 bucket name with the global read access";
    }

}
