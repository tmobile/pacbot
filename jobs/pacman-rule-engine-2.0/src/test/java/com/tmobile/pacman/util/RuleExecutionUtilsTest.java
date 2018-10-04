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
/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :kkumar
  Modified Date: Jul 16, 2018

**/
/*
 *Copyright 2016-2017 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 *Licensed under the Amazon Software License (the "License"). You may not use
 * this file except in compliance with the License. A copy of the License is located at
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tmobile.pacman.util;

import static org.junit.Assert.*;

import java.util.Map;

import javax.net.ssl.SSLContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Maps;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;
import com.tmobile.pacman.commons.rule.Annotation.Type;


// TODO: Auto-generated Javadoc
/**
 * The Class RuleExecutionUtilsTest.
 */
@PowerMockIgnore("org.apache.http.conn.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SSLContext.class })
public class RuleExecutionUtilsTest {

	/**
	 * Post audit trail.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void postAuditTrail() throws Exception {
		Map<String, String> param = Maps.newHashMap();
		param.put(PacmanSdkConstants.ACCOUNT_ID, "acc123");
		param.put(PacmanSdkConstants.REGION, "region123");
		param.put(PacmanSdkConstants.RESOURCE_ID, "resou123");
		param.put(PacmanSdkConstants.REGION, "region");
		boolean response = RuleExecutionUtils.ifFilterMatchesTheCurrentResource(param, param);
		assertTrue(response);
	}

	/**
	 * Gets the local rule param.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void getLocalRuleParam() throws Exception {
		Map<String, String> param = Maps.newHashMap();
		param.put(PacmanSdkConstants.ACCOUNT_ID, "acc123");
		param.put(PacmanSdkConstants.REGION, "region123");
		param.put(PacmanSdkConstants.RESOURCE_ID, "resou123");
		param.put(PacmanSdkConstants.REGION, "region");
		Map<String, String> response = RuleExecutionUtils.getLocalRuleParam(param, param);
		assertNotNull(response);
	}

	/**
	 * Builds the annotation.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void buildAnnotation() throws Exception {
        Type annotationType = Type.INFO;
		Map<String, String> param = Maps.newHashMap();
		param.put(PacmanSdkConstants.ACCOUNT_ID, "acc123");
		param.put(PacmanSdkConstants.APPLICATION_TAG_KEY, "acc123");
		param.put(PacmanSdkConstants.INVOCATION_ID, "acc123");
		param.put(PacmanSdkConstants.RULE_SEVERITY, "acc123");
		param.put(PacmanSdkConstants.REGION, "region123");
		param.put(PacmanSdkConstants.RESOURCE_ID, "resou123");
		param.put(PacmanSdkConstants.REGION, "region");
		Map<String, String> response = RuleExecutionUtils.buildAnnotation(param, param, "executionId123",annotationType, getPacmanRule());
		assertNotNull(response);
	}



	/**
	 * Gets the rule attribute.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void getRuleAttribute() throws Exception {
		Map<String, String> param = Maps.newHashMap();
		param.put(PacmanSdkConstants.ACCOUNT_ID, "acc123");
		param.put(PacmanSdkConstants.REGION, "region123");
		param.put(PacmanSdkConstants.RESOURCE_ID, "resou123");
		param.put(PacmanSdkConstants.REGION, "region");
		Annotation annotation = new Annotation();
        annotation.put(PacmanSdkConstants.RULE_ID, "ruleId123");
        annotation.put(PacmanSdkConstants.DOC_ID, "docId123");
		annotation.put(PacmanSdkConstants.DATA_SOURCE_KEY, "sKey123");
		annotation.put(PacmanSdkConstants.TARGET_TYPE, "target123");
		RuleResult result = new RuleResult();
		result.setAnnotation(annotation);
		result.setDesc("desc");
		result.setResource(param);
		result.setStatus("status");
		PacmanRule ruleAnnotation = getPacmanRule();

		String response = RuleExecutionUtils.getRuleAttribute(result, param, ruleAnnotation, "attribute");
		assertNotNull(response);
		response = RuleExecutionUtils.getRuleAttribute(result, null, null, "attribute");
		assertNull(response);
	}

	/**
	 * Gets the pacman rule.
	 *
	 */
	private PacmanRule getPacmanRule() {
		return new PacmanRule() {

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return null;
			}

			@Override
			public String severity() {
				return "high";
			}

			@Override
			public String key() {
				return "key";
			}

			@Override
			public String desc() {
				return "desc";
			}

			@Override
			public String category() {
				return "category";
			}
		};
	}
}
