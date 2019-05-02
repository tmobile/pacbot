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
package com.tmobile.cloud.awsrules.cloudfront;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

/**
 * Purpose: This rule checks for cloudfront resources serving HTML content
 * without authorization
 * 
 * Author: pavankumarchaitanya
 * 
 * Reviewers: Kamal, Kanchana
 * 
 * Modified Date: April 22nd, 2019
 */
@PacmanRule(key = "check-for-unauthorized-html-cloudfront-distribution", desc = "checks for unauthorized HTML cloudfront distribution", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class CloudfrontAuthorizedHTMLContentDistributionRule extends BaseRule {
	private static final Logger logger = LoggerFactory.getLogger(CloudfrontAuthorizedHTMLContentDistributionRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * ************* Following are the Rule Parameters********* <br>
	 * 
	 * ruleKey : check-for-unauthorized-html-cloudfront-distribution <br>
	 * <br>
	 * 
	 * @param resourceAttributes
	 *            this is a resource in context which needs to be scanned this is
	 *            provided by execution engine
	 *
	 */

	private final String HTTP_PROTOCOL_PREFIX = "http://";

	private final String SLASH = "/";

	private final String INDEX_HTML = "index.html";

	private final String INDEX_HTM = "index.htm";

	@Override
	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========CloudfrontAuthorizedHTMLContentDistributionRule started=========");
		String cloudFrontResourceID = resourceAttributes.get(PacmanSdkConstants.RESOURCE_ID);

		MDC.put("executionId", ruleParam.get("executionId"));
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
		boolean isWebsiteHosted = false;
		String domainName = resourceAttributes.get("domainName");
		String rootObject = resourceAttributes.get("deafultRootObject");
		String enabled = resourceAttributes.get("enabled");
		if (enabled != null && enabled.equalsIgnoreCase("true")) {
			List<String> urlListToCheck = new ArrayList<>();
			if (rootObject != null && rootObject.contains("htm")) {
				urlListToCheck.add(HTTP_PROTOCOL_PREFIX + domainName + SLASH + rootObject);
			}
			urlListToCheck.add(HTTP_PROTOCOL_PREFIX + domainName);
			urlListToCheck.add(HTTP_PROTOCOL_PREFIX + domainName + SLASH + INDEX_HTML);
			urlListToCheck.add(HTTP_PROTOCOL_PREFIX + domainName + SLASH + INDEX_HTM);

			for (String url : urlListToCheck) {
				try {
					isWebsiteHosted = isWebSiteHosted(url);
					if (isWebsiteHosted) {
						String description = "CloudFront instance: " + cloudFrontResourceID
								+ " is unauthorized for html content distribution. Content hosted on url : " + url;
						logger.debug(description);
						return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,
								PacmanUtils.createAnnotation("", ruleParam, description,
										PacmanSdkConstants.SEV_HIGH, PacmanSdkConstants.SECURITY));
					}
				} catch (Exception e) {
					logger.error("Exception getting from url  :[{}],[{}] ", url, e.getMessage());
				}
			}
		}
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);

	}

	public boolean isWebSiteHosted(String url) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("content-type", "text/html");
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		if (httpClient != null) {
			HttpResponse httpResponse;
			try {
				httpResponse = httpClient.execute(httpGet);
				if (httpResponse.getStatusLine().getStatusCode() >= 400) {
					return false;
				}
			} catch (Exception e) {
				logger.error("Exception getting from url  :[{}],[{}] ", url, e.getMessage());
				throw e;
			}
		}
		return true;
	}

	@Override
	public String getHelpText() {
		return "This rule checks for unauthorized html content on cloudfront distribution.";
	}

}