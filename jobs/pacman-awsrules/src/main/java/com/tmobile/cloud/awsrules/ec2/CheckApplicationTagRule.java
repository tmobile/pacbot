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
package com.tmobile.cloud.awsrules.ec2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.util.StringUtils;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;


@PacmanRule(key = "check-for-application-tag-rule", desc = "checks for application tag rule's value is valid or not", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.GOVERNANCE)
public class CheckApplicationTagRule extends BaseRule {

    private static final Logger logger = LoggerFactory.getLogger(CheckApplicationTagRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     **************            Following are the Rule Parameters********* <br>
     * <br>
     * 
     *            ruleKey : check-for-application-tag-rule <br>
     * <br>
     *
     *            severity : Enter the value of severity <br>
     * <br>
     * 
     *            ruleCategory : Enter the value of category <br>
     * <br>
     *
     *            esAppTagURL : Enter the ES URL <br>
     * <br>
     *
     *            threadsafe : if true , rule will be executed on multiple
     *            threads <br>
     * <br>
     *
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided by execution engine
     *
     */

	public RuleResult execute(final Map<String, String> ruleParam,Map<String, String> resourceAttributes) {

		logger.debug("========CheckApplicationTagRule started=========");
		Annotation annotation = null;
		String appTag = null;
		boolean isApplicationTagExists = false;
		String appTagURL = null;
		String heimdallURL = null;
		String issueDescription = null;
		boolean createEventFound = false;
		String appTagMappedtoOU = null;
		Map<String, Object> resourceCreatedDetails = null;
		List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
		LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
		LinkedHashMap<String, Object> appTagDetails = new LinkedHashMap<>();
		Gson gson = new Gson();
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String reSourceId = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
		String eventType = PacmanRuleConstants.CREATED_EVENT_TYPE;
		MDC.put("executionId", ruleParam.get("executionId"));
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
		
		String formattedUrl = PacmanUtils.formatUrl(ruleParam,PacmanRuleConstants.ES_APPLICATION_TAG_URL);
		String heimdallFormattedUrl = PacmanUtils.formatUrl(ruleParam,PacmanRuleConstants.ES_HEIMDALL_URL,PacmanRuleConstants.HEIMDALL_URI);
		logger.debug("==formattedUrl {}==", formattedUrl);
		if (!StringUtils.isNullOrEmpty(formattedUrl)) {
			appTagURL = formattedUrl;
		}
		logger.debug("==appTagURL {}==", appTagURL);

		logger.debug("==heimdallFormattedUrl {}==",heimdallFormattedUrl);
		if (!StringUtils.isNullOrEmpty(heimdallFormattedUrl)) {
			heimdallURL = heimdallFormattedUrl;
		}
		logger.debug("==heimdallURL URL {}==", heimdallURL);

		if (!PacmanUtils.doesAllHaveValue(severity, category, appTagURL)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		if (!resourceAttributes.isEmpty()) {
			try {
				if (StringUtils.isNullOrEmpty(resourceAttributes.get(PacmanRuleConstants.TAGS_APP))) {
					issueDescription = "Application Tag does not exist";
					resourceCreatedDetails = PacmanUtils.getResourceCreatedDetails(reSourceId, eventType,heimdallURL);
					createEventFound = (Boolean) resourceCreatedDetails.get("created_event_found");
					if (createEventFound && null != (resourceCreatedDetails.get("OU"))) {
						String ou = resourceCreatedDetails.get("OU").toString();
						appTagMappedtoOU = PacmanUtils.getAppTagMappedToOU(ou, appTagURL, PacmanRuleConstants.APP_ID);
					}
				} else {
					appTag = StringUtils.trim(resourceAttributes.get(PacmanRuleConstants.TAGS_APP));
					// Check In the Application Inventory whether it is valid
					appTag = PacmanUtils.getAppTagMappedToOU(appTag, appTagURL,PacmanRuleConstants.APP_TAG_KEYWORD);

					if (!Strings.isNullOrEmpty(appTag)) {
						isApplicationTagExists = true;
					}

					if (isApplicationTagExists) {
						// get ResourceCreatedDetails
						resourceCreatedDetails = PacmanUtils.getResourceCreatedDetails(reSourceId,eventType, heimdallURL);
						createEventFound = (Boolean) resourceCreatedDetails.get("created_event_found");
						if (createEventFound && null != resourceCreatedDetails.get("OU")) {
							String ou = resourceCreatedDetails.get("OU").toString();
							if (!PacmanUtils.checkAppTagMappedtoOUMatchesCurrentAppTag(ou, appTagURL,PacmanRuleConstants.APP_ID, appTag)) {
								isApplicationTagExists = false;
								issueDescription = reSourceId+ " Application Tag tagged incorrectly";
								if (null != PacmanUtils.getAppTagMappedToOU(ou,appTagURL, PacmanRuleConstants.APP_ID)) {
									appTagMappedtoOU = PacmanUtils.getAppTagMappedToOU(ou, appTagURL,PacmanRuleConstants.APP_ID);
								}
							}
						}
					}

				}

			} catch (Exception e) {
				throw new RuleExecutionFailedExeption(e.getMessage());
			}
		}
		if (!isApplicationTagExists) {
			annotation = Annotation.buildAnnotation(ruleParam,Annotation.Type.ISSUE);
			annotation.put(PacmanSdkConstants.DESCRIPTION,"Application Tags value is not a valid!!");
			issue.put(PacmanRuleConstants.VIOLATION_REASON, issueDescription);
			appTagDetails.put(PacmanSdkConstants.CURRENT_APP_TAG_KEY, appTag);
			appTagDetails.put(PacmanSdkConstants.CORRECT_APP_TAG_KEY,appTagMappedtoOU);
			// adding current-apptag and correct-apptag to annotation
			annotation.put(PacmanSdkConstants.CURRENT_APP_TAG_KEY, appTag);
			annotation.put(PacmanSdkConstants.CORRECT_APP_TAG_KEY,appTagMappedtoOU);
			if (null != resourceCreatedDetails) {
				appTagDetails.put("oU", resourceCreatedDetails.get("OU"));
				appTagDetails.put("user_name",resourceCreatedDetails.get("userName"));
				appTagDetails.put("created_type",resourceCreatedDetails.get("created_type"));
			}
			issue.put("appTagDetails", gson.toJson(appTagDetails));
			issueList.add(issue);
			annotation.put("issueDetails", issueList.toString());
			logger.debug("========CheckApplicationTagRule ended with annotation {} :=========",annotation);
			return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);

		}
		logger.debug("========CheckApplicationTagRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

    public String getHelpText() {
        return "This rule checks unused classic elb which are not associated with any instance";
    }
}
