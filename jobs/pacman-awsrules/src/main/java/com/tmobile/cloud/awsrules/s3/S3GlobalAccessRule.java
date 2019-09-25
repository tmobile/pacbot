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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetPublicAccessBlockRequest;
import com.amazonaws.services.s3.model.GetPublicAccessBlockResult;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.awsrules.utils.S3PacbotUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-s3-global-access", desc = "checks entirely for S3 Buckets With Global Access Permission", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class S3GlobalAccessRule extends BaseRule {
    private static final Logger logger = LoggerFactory.getLogger(S3GlobalAccessRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * ************* Following are the Rule Parameters********* <br>
     * ruleKey : check-for-s3-global-access <br>
     * <br>
     * 
     * severity : Enter the value of severity <br>
     * <br>
     * 
     * ruleCategory : Enter the value of category <br>
     * <br>
     * 
     * roleIdentifyingString : Configure it as role/pacbot_ro <br>
     * <br>
     * 
     * esServiceURL : Enter the Elastic search URL <br>
     * <br>
     * 
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided by execution engine
     *
     */
    @Override
    public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
        logger.debug("========S3GlobalAccessRule started=========");
        Map<String, Object> map = null;
        AmazonS3Client awsS3Client = null;
        Map<String, Boolean> checkPolicyMap = new HashMap();
        String roleIdentifyingString = ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
        String s3BucketName = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
        String checkEsUrl = null;

        String formattedUrl = PacmanUtils.formatUrl(ruleParam, PacmanRuleConstants.ES_CHECK_SERVICE_SEARCH_URL_PARAM);

        if (!StringUtils.isEmpty(formattedUrl)) {
            checkEsUrl = formattedUrl;
        }

        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        String description = "Global read/write access detected";

        boolean aclFound = false;
        boolean bucketPolicyFound = false;
        Boolean isRequiredAclCheck = true;
		Boolean isRequiredPublicPolicyCheck = true;
		Boolean isRequiredTrustedAdvisorCheck = true;
        Map<String, Boolean> s3HasOpenAccess = new HashMap<>();
        String checkId = ruleParam.get(PacmanRuleConstants.CHECK_ID);
        List<String> sourcesverified = new ArrayList<>();
        LinkedHashMap<String, Object> accessLevels = new LinkedHashMap<>();
        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
        /* check rule received all required values for rule execution */
        if (!PacmanUtils.doesAllHaveValue(severity, category, checkEsUrl)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }
        if (!resourceAttributes.isEmpty()) {
            logger.info("=========================region {}",resourceAttributes.get("region"));
            try {
                map = getClientFor(AWSService.S3, roleIdentifyingString, ruleParam);
                awsS3Client = (AmazonS3Client) map.get(PacmanSdkConstants.CLIENT);
            } catch (UnableToCreateClientException e) {
                logger.error("unable to get client for following input", e);
                throw new InvalidInputException(e.toString());
            }
        }
		       
			if (null != awsS3Client) {
				GetPublicAccessBlockRequest publicAccessBlockRequest = new GetPublicAccessBlockRequest();
				publicAccessBlockRequest.setBucketName(s3BucketName);
				try {
					GetPublicAccessBlockResult accessBlockResult = awsS3Client.getPublicAccessBlock(publicAccessBlockRequest);
					PublicAccessBlockConfiguration accessBlockConfiguration = accessBlockResult.getPublicAccessBlockConfiguration();
					
					if (accessBlockConfiguration.getBlockPublicAcls() && accessBlockConfiguration.getIgnorePublicAcls() && accessBlockConfiguration.getBlockPublicPolicy() && accessBlockConfiguration.getRestrictPublicBuckets()) {
						logger.debug(s3BucketName,"This Bucket is not publicly accessible");
						return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
					}
					if(accessBlockConfiguration.getBlockPublicAcls() || accessBlockConfiguration.getIgnorePublicAcls()){
						isRequiredAclCheck = false;
					}
					if(accessBlockConfiguration.getBlockPublicPolicy() || accessBlockConfiguration.getRestrictPublicBuckets()){
						isRequiredPublicPolicyCheck = false;
					}
				
				} catch (Exception e) {
					if(e.getMessage().contains("Access Denied")){
						logger.debug(s3BucketName,"This Bucket is not publicly accessable");
						return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
					}
					logger.debug("no PublicAccessBlockConfiguration found, proceeding with ACL, policy and trusted advisor check {}",e);
				}
			}
        
        
        
        logger.info("checking bucket has public access through ACL");
        String accessType = "READ,WRITE,READ_ACP";
        Set<Permission> permissions = new HashSet<>();
        if(isRequiredAclCheck){
        	permissions = S3PacbotUtils.checkACLPermissions(awsS3Client, s3BucketName, accessType);
        }
  		if (!permissions.isEmpty()) {

            description = description + " through ACL";
            sourcesverified.add("ACL");
            accessLevels.put("ACL", PacmanRuleConstants.PUBLIC);
            accessLevels.put("permisssions", permissions.toString());
            return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,
                    PacmanUtils.createS3Annotation(ruleParam, description, severity, category,
                            PacmanRuleConstants.GLOBAL_ACCESS, sourcesverified, accessLevels,
                            resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID)));
            
        } else if (isRequiredPublicPolicyCheck && isPolicyTrue(awsS3Client, s3BucketName, accessType,checkPolicyMap)) {
        	List<String> policyTypeList = new ArrayList<>();
        	for(Map.Entry<String, Boolean> policyType : checkPolicyMap.entrySet()){
            	policyTypeList.add(policyType.getKey());
            }
            logger.info("checking bucket has public access through BucketPolicy");
            sourcesverified.add("ACL");
            accessLevels.put("ACL", "private");
            description = description + PacmanRuleConstants.THROUGH_BUCKET_POLICY;
            sourcesverified.add("BucketPolicy");
            accessLevels.put("Bucket Policy", PacmanRuleConstants.PUBLIC);
            accessLevels.put("permisssions",  String.join(",", sourcesverified));
            return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,
                    PacmanUtils.createS3Annotation(ruleParam, description, severity, category,
                            PacmanRuleConstants.GLOBAL_ACCESS, sourcesverified, accessLevels,
                            resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID)));
            
        }else if(isRequiredTrustedAdvisorCheck){

            // check bucket is opened through TA
            logger.info("checking S3 bucket has public access from Trusted Advisor");
            String accountId = StringUtils.trim(resourceAttributes.get(PacmanRuleConstants.ACCOUNTID));

            /*
             * Check1 - From Trusted advisor check bucket has read
             * access through ACL/BucketPolicy
             */
            try {
				s3HasOpenAccess = S3PacbotUtils.checkS3HasOpenAccess(checkId, accountId, checkEsUrl, s3BucketName);
			} catch (Exception e) {
				logger.error("unable to get the details", e);
				throw new RuleExecutionFailedExeption(e.getMessage());
			}
            if (!s3HasOpenAccess.isEmpty()) {
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
        logger.info(s3BucketName, "This Bucket is not publicly accessable");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tmobile.pacman.commons.rule.Rule#getHelpText()
     */
    @Override
    public String getHelpText() {
        return null;
    }

    private boolean isPolicyTrue(AmazonS3Client awsS3Client, String s3BucketName, String accessType,Map<String,Boolean> checkPolicyMap) {
        Map<String, Boolean> checkPolicy = S3PacbotUtils.getPublicAccessPolicy(awsS3Client, s3BucketName, accessType);
        if (!checkPolicy.isEmpty()) {
        	checkPolicyMap.putAll(checkPolicy);
          return (checkPolicy.containsKey("Read") || checkPolicy.containsKey("Write"));
        }
        return false;
    }
}
