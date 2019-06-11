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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.RuleResult;

/**
 * Purpose: This test checks for cloudfront cdn exposing content without
 * approval.
 * 
 * Author: pavankumarchaitanya
 * 
 * Reviewers: Kamal, Kanchana
 * 
 * Modified Date: April 11th, 2019
 * 
 */
public class CloudfrontAuthorizedContentDistributionRuleTest {

	@Test
	public void testExecute() {
		CloudfrontAuthorizedContentDistributionRule cloudfrontAuthorizedContentDistributionRule = new CloudfrontAuthorizedContentDistributionRule();

		Map<String, String> ruleParam = new HashMap<>();
		Map<String, String> resourceAttributes = new HashMap<>();

		resourceAttributes.put(PacmanSdkConstants.RESOURCE_ID, "test-resource-id");

		ruleParam.put("executionId", "test-execution-Id");
		ruleParam.put(PacmanSdkConstants.RULE_ID, "test-rule-Id");

		RuleResult ruleResult = cloudfrontAuthorizedContentDistributionRule.execute(ruleParam, resourceAttributes);
		assertTrue(ruleResult.getStatus().equals(PacmanSdkConstants.STATUS_FAILURE));
	}
}
