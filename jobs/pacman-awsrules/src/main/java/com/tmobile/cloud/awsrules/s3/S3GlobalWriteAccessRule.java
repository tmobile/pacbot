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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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

@PacmanRule(key = "check-for-s3-global-write-access", desc = "checks entirely for S3 Buckets With Global Write Permission", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class S3GlobalWriteAccessRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(S3GlobalWriteAccessRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 *            ************* Following are the Rule Parameters********* <br><br>
	 * 
	 *            apiKeyName : Value of API key <br><br>
	 * 
	 *            apiKeyValue : Value of the API key name <br><br>
	 * 
	 *            apiGWURL : API gateway URL <br><br>
	 * 
	 *            ruleKey : check-for-s3-global-write-access <br><br>
	 *            severity : Enter the value of severity <br><br>
	 * 
	 *            ruleCategory : Enter the value of category <br><br>
	 *            
	 *            roleIdentifyingString : Configure it as role/pac_ro <br><br>
	 *            
	 *            esServiceURL : Enter the Es url <br><br>
	 * 
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========S3GlobalWriteAccessRule started=========");
		Map<String, Object> map = null;
		AmazonS3Client awsS3Client = null;
		String roleIdentifyingString = ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
		String s3BucketName = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
		String apiKeyName = ruleParam.get(PacmanRuleConstants.API_KEY_NAME);
		String apiKeyValue = ruleParam.get(PacmanRuleConstants.API_KEY_VALUE);
		String apiGWURL = ruleParam.get(PacmanRuleConstants.APIGW_URL);

		String checkEsUrl = null;
		
		String formattedUrl = PacmanUtils.formatUrl(ruleParam,PacmanRuleConstants.ES_CHECK_SERVICE_SEARCH_URL_PARAM);
        
        if(!StringUtils.isEmpty(formattedUrl)){
            checkEsUrl =  formattedUrl;
        }
        Map<String, Boolean> s3HasOpenAccess;
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String checkId = ruleParam.get(PacmanRuleConstants.CHECK_ID);
		boolean aclFound = false;
		boolean bucketPolicyFound = false;
		String description = "Global write access detected";
		List<String> sourcesverified = new ArrayList<>();
        LinkedHashMap<String,Object>accessLevels=new LinkedHashMap<>();
		MDC.put("executionId", ruleParam.get("executionId")); 
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); 
		
		if (!PacmanUtils.doesAllHaveValue(apiGWURL, apiKeyValue, apiKeyName, severity, category, checkEsUrl)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
       
		if (!resourceAttributes.isEmpty()) {
			try {

				// create client to describe bucket policies/acl
				try {
					map = getClientFor(AWSService.S3, roleIdentifyingString, ruleParam);
					awsS3Client = (AmazonS3Client) map.get(PacmanSdkConstants.CLIENT);
				} catch (UnableToCreateClientException e) {
					logger.error("unable to get client for following input", e);
					throw new InvalidInputException(e.toString());
				}
				logger.info("checking bucket has write access through ACL");
				if (PacmanUtils.checkACLAccess(awsS3Client, s3BucketName,PacmanRuleConstants.WRITE_ACCESS)) {
					description = description + " through ACL";
					sourcesverified.add("ACL");
					accessLevels.put("ACL", PacmanRuleConstants.PUBLIC);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,PacmanUtils.createS3Annotation(ruleParam,description, severity, category,PacmanRuleConstants.WRITE_ACCESS,sourcesverified,accessLevels,resourceAttributes.get("_resourceid")));

				} else if (isPolicyTrue(awsS3Client, s3BucketName,PacmanRuleConstants.WRITE_ACCESS)) {
					sourcesverified.add("ACL");
					accessLevels.put("ACL", "private");
					logger.info("checking bucket has write access through Bucket Policy");
					description = description + PacmanRuleConstants.THROUGH_BUCKET_POLICY;
					sourcesverified.add("BucketPolicy");
					accessLevels.put("BucketPolicy",  PacmanRuleConstants.PUBLIC);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE, PacmanUtils.createS3Annotation(ruleParam, description, severity, category,PacmanRuleConstants.WRITE_ACCESS,sourcesverified,accessLevels,resourceAttributes.get("_resourceid")));
				} else {

                    // check bucket is opened through TA
                    logger.info("checking S3 bucket has public access from Trusted Advisor");
                    String accountId = StringUtils.trim(resourceAttributes.get(PacmanRuleConstants.ACCOUNTID));
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
                                        PacmanRuleConstants.WRITE_ACCESS, sourcesverified, accessLevels,
                                        resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID)));
                        }
                    }
                
				}

			} catch (Exception e1) {
				logger.error("unable to get the details", e1);
				throw new InvalidInputException(e1.getMessage());
			}
		}

		logger.debug("========S3GlobalWriteAccessRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);

	}

	private boolean isPolicyTrue(AmazonS3Client awsS3Client,String s3BucketName, String accessType) {
		boolean isWriteAccess = false;
		Map<String, Boolean> checkPolicyMap = PacmanUtils.getPublicAccessPolicy(awsS3Client, s3BucketName, accessType);
		
		if (!checkPolicyMap.isEmpty()) {
			isWriteAccess = checkPolicyMap.get("Write");
		}
		return isWriteAccess;
	}

	public String getHelpText() {
		return "This rule checks s3 bucket name with the global write access";
	}
}
