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
  Modified Date: Jun 14, 2017

**/

package com.tmobile.pacman.commons.rule;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Regions;
import com.google.common.base.Strings;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.aws.clients.AWSClientManager;
import com.tmobile.pacman.commons.aws.clients.impl.AWSClientManagerImpl;
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseRule.
 */
public abstract class BaseRule implements Rule {


    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(BaseRule.class);

    /** The rule param map. */
    transient Map<String, String> ruleParamMap;

    /** The resource attribute map. */
    transient Map<String, String> resourceAttributeMap;

	/**
	 * logs the message to appropriate log media.
	 *
	 * @param message the message
	 */
	public void log(String message) {
		logger.info(message);
	}

	/**
	 * Instantiates a new base rule.
	 */
	public BaseRule() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new base rule.
	 *
	 * @param ruleParamMap the rule param map
	 * @param resourceAttributeMap the resource attribute map
	 */
	public BaseRule(Map<String, String> ruleParamMap, Map<String, String> resourceAttributeMap) {
		super();
		this.ruleParamMap = ruleParamMap;
		this.resourceAttributeMap = resourceAttributeMap;
	}

	/**
	 * Gets the client for.
	 *
	 * @param service the service
	 * @param roleIdentifierString the role identifier string
	 * @param ruleParam the rule param
	 * @return the client for
	 * @throws UnableToCreateClientException the unable to create client exception
	 */
	public Map<String, Object> getClientFor(AWSService service,String roleIdentifierString,Map<String, String> ruleParam) throws UnableToCreateClientException{
		AWSClientManager awsClientManager = new AWSClientManagerImpl();
		StringBuilder roleArn= new StringBuilder();
		String accountId=ruleParam.get(PacmanSdkConstants.ACCOUNT_ID);
		String roleIdentifier=ruleParam.get(PacmanSdkConstants.Role_IDENTIFYING_STRING);
		try {
				if(Strings.isNullOrEmpty(accountId) ||  Strings.isNullOrEmpty(roleIdentifier)){
					throw new UnableToCreateClientException("missing account id or role arn identifier");
				}
				roleArn.append(PacmanSdkConstants.ROLE_ARN_PREFIX).append(ruleParam.get(PacmanSdkConstants.ACCOUNT_ID)).append(":").append(roleIdentifierString);
				if(null!=ruleParam.get(PacmanSdkConstants.REGION)){
					return awsClientManager.getClient(accountId,roleArn.toString(), service, Regions.fromName(ruleParam.get(PacmanSdkConstants.REGION)),roleIdentifierString);
				}else {
					return awsClientManager.getClient(accountId,roleArn.toString(), service, null,roleIdentifierString);
				}
		} catch (UnableToCreateClientException e) {
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	public RuleResult call() throws Exception {
		if(ruleParamMap!=null && resourceAttributeMap!=null){
			RuleResult result = execute(ruleParamMap, resourceAttributeMap);
			result.setResource(resourceAttributeMap);// in case rule has modified this , overwrite the resource as it was sent
			return result;
		}
		else{
			throw new Exception("rule parameters or resource attributes cannot be null, exiting now");
		}
	}

	/**
	 * Gets the rule param map.
	 *
	 * @return the rule param map
	 */
	public Map<String, String> getRuleParamMap() {
		return ruleParamMap;
	}

	/**
	 * Sets the rule param map.
	 *
	 * @param ruleParamMap the rule param map
	 */
	public void setRuleParamMap(Map<String, String> ruleParamMap) {
		this.ruleParamMap = ruleParamMap;
	}

	/**
	 * Gets the resource attribute map.
	 *
	 * @return the resource attribute map
	 */
	public Map<String, String> getResourceAttributeMap() {
		return resourceAttributeMap;
	}

	/**
	 * Sets the resource attribute map.
	 *
	 * @param resourceAttributeMap the resource attribute map
	 */
	public void setResourceAttributeMap(Map<String, String> resourceAttributeMap) {
		this.resourceAttributeMap = resourceAttributeMap;
	}

}
