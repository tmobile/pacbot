
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.RuleResult;

/**
 * Purpose: This test checks for cloudfront resources serving HTML content
 * without authorization
 * 
 * Author: pavankumarchaitanya
 * 
 * Reviewers: Kamal, Kanchana
 * 
 * Modified Date: April 22nd, 2019
 */
// @PowerMockIgnore({ "javax.net.ssl.*", "javax.management.*" })
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacmanUtils.class, HttpClientBuilder.class })
public class CloudfrontAuthorizedHTMLContentDistributionRuleTest {
	CloudfrontAuthorizedHTMLContentDistributionRule cloudfrontAuthorizedHTMLContentDistributionRule = null;
	CloudfrontAuthorizedHTMLContentDistributionRule spy = null;

	@Before
	public void setup() throws Exception {
		HttpClientBuilder httpClientBuilder = PowerMockito.mock(HttpClientBuilder.class);
		PowerMockito.mockStatic(HttpClientBuilder.class);

		CloseableHttpClient closeableHttpClient = PowerMockito.mock(CloseableHttpClient.class);
		CloseableHttpResponse closeableHttpResponse = PowerMockito.mock(CloseableHttpResponse.class);
		StatusLine statusline = new BasicStatusLine(new org.apache.http.ProtocolVersion("test", 1, 1), 400, "test");
		PowerMockito.when(closeableHttpResponse.getStatusLine()).thenReturn(statusline);

		PowerMockito.when(closeableHttpClient.execute(any())).thenReturn(closeableHttpResponse);
		PowerMockito.when(HttpClientBuilder.create()).thenReturn(httpClientBuilder);
		PowerMockito.when(httpClientBuilder.build()).thenReturn(closeableHttpClient);
		CloudfrontAuthorizedHTMLContentDistributionRule cloudfrontAuthorizedHTMLContentDistributionRule = new CloudfrontAuthorizedHTMLContentDistributionRule();
		spy = PowerMockito.spy(cloudfrontAuthorizedHTMLContentDistributionRule);

		doReturn(true).when(spy).isWebSiteHosted(any());
	}

	@Test
	public void testDisabledCloudFrontForHTMLContent() {
		CloudfrontAuthorizedHTMLContentDistributionRule cloudfrontAuthorizedHTMLContentDistributionRule = new CloudfrontAuthorizedHTMLContentDistributionRule();

		Map<String, String> ruleParam = new HashMap<>();
		;
		Map<String, String> resourceAttributes = new HashMap<>();
		resourceAttributes.put("_resourceid", "test-resource-id");
		resourceAttributes.put("domainName", "test-domain-name");
		resourceAttributes.put("deafultRootObject", "default-root-object");

		resourceAttributes.put("enabled", "false");

		ruleParam.put("executionId", "test-execution-id");
		ruleParam.put(PacmanSdkConstants.RULE_ID, "test-rule-id");

		RuleResult ruleResult = cloudfrontAuthorizedHTMLContentDistributionRule.execute(ruleParam, resourceAttributes);
		assertTrue(ruleResult.getStatus().equals(PacmanSdkConstants.STATUS_SUCCESS));

	}

	@Test
	public void testEnabledCloudFrontForHTMLContent() {
		CloudfrontAuthorizedHTMLContentDistributionRule cloudfrontAuthorizedHTMLContentDistributionRule = new CloudfrontAuthorizedHTMLContentDistributionRule();

		Map<String, String> ruleParam = new HashMap<>();
		;
		Map<String, String> resourceAttributes = new HashMap<>();
		resourceAttributes.put("_resourceid", "test-resource-id");
		resourceAttributes.put("domainName", "test-domain-name");
		resourceAttributes.put("deafultRootObject", "default-root-object");

		resourceAttributes.put("enabled", "true");

		ruleParam.put("executionId", "test-execution-id");
		ruleParam.put(PacmanSdkConstants.RULE_ID, "test-rule-id");

		RuleResult ruleResult = cloudfrontAuthorizedHTMLContentDistributionRule.execute(ruleParam, resourceAttributes);
		assertTrue(ruleResult.getStatus().equals(PacmanSdkConstants.STATUS_SUCCESS));

	}

	@Test
	public void testCloudFrontForHTMLContent() {

		Map<String, String> ruleParam = new HashMap<>();
		;
		Map<String, String> resourceAttributes = new HashMap<>();
		resourceAttributes.put("_resourceid", "test-resource-id");
		resourceAttributes.put("domainName", "test-domain-name");
		resourceAttributes.put("deafultRootObject", "default-root-object");

		resourceAttributes.put("enabled", "true");

		ruleParam.put("executionId", "test-execution-id");
		ruleParam.put(PacmanSdkConstants.RULE_ID, "test-rule-id");

		RuleResult ruleResult = spy.execute(ruleParam, resourceAttributes);
		assertTrue(ruleResult.getStatus().equals(PacmanSdkConstants.STATUS_FAILURE));

	}
}
