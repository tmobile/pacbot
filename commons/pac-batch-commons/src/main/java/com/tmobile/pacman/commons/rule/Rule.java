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
Purpose: * A PacMan rule written in java shall implement this interface to be properly executed by the runtime
Author :kkumar
Modified Date: Jun 14, 2017

**/
package com.tmobile.pacman.commons.rule;

import java.util.Map;
import java.util.concurrent.Callable;

// TODO: Auto-generated Javadoc
/**
 * The Interface Rule.
 */
public interface Rule extends Callable<RuleResult> {

	/**
	 * Tests the resource based on ruleParam.
	 *
	 * @param ruleParam the rule param
	 * @param resourceAttributes TODO
	 * @return RuleResult
	 */
	public RuleResult execute(final Map<String,String> ruleParam, Map<String, String> resourceAttributes);

	/**
	 * build and json string defining the purpose of the rule and how it works.
	 *
	 * @return String
	 */
	public String getHelpText();

}
