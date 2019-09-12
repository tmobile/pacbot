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
  Author :Kanchana
  Modified Date: 12th September, 2019
  
**/
package com.tmobile.pacman.autofix.elasticip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.ReleaseAddressRequest;
import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.common.exception.AutoFixException;
import com.tmobile.pacman.commons.autofix.BaseFix;
import com.tmobile.pacman.commons.autofix.FixResult;
import com.tmobile.pacman.commons.autofix.PacmanFix;
import com.tmobile.pacman.dto.AutoFixTransaction;
import com.tmobile.pacman.util.CommonUtils;

/**
 * UnusedElasticIPAutofix class executes fix by releasing unused elastic ip by
 * allocation-id.
 */
@PacmanFix(key = "unused-elastic-ip-fix", desc = "Auto fixes by releasing the unused elastic ip")
public class UnusedElasticIPAutofix extends BaseFix {
	private static final String ELASTICIPDETAILS = "elasticIPDetails";
	private static final Integer AUTOFIX_DEFAULT_INTERVAL = 72;
	private static final Logger LOGGER = LoggerFactory.getLogger(UnusedElasticIPAutofix.class);
	private static final String ELASTIC_IP_ALLOCATION_ID_RELEASED = "The Elastic ip with allocation id [{}] [{}] released";
	private static final String ELASTIC_IP_ALLOCATION_ID_RELEASE_FAILED = "The Elastic ip with allocation id [{}] [{}] release failed [{}]";

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	private static final String ISSUE_CREATION_TIME_ELAPSED = "pacman.autofix.issue.creation.time.elapsed";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tmobile.pacman.commons.autofix.BaseFix#executeFix(java.util.Map,
	 * java.util.Map, java.util.Map)
	 */
	@Override
	public FixResult executeFix(Map<String, String> issue, Map<String, Object> clientMap,
			Map<String, String> ruleParams) {

		try {
			AmazonEC2 amazonEC2 = (AmazonEC2) clientMap.get(PacmanSdkConstants.CLIENT);

			ReleaseAddressRequest request = new ReleaseAddressRequest().withAllocationId(issue.get(PacmanSdkConstants.ALLOCATION_ID));
			amazonEC2.releaseAddress(request);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(String.format(ELASTIC_IP_ALLOCATION_ID_RELEASED, issue.get(PacmanSdkConstants.RESOURCE_ID),
						issue.get(PacmanSdkConstants.ALLOCATION_ID)));
			return new FixResult(PacmanSdkConstants.STATUS_SUCCESS_CODE,
					String.format(ELASTIC_IP_ALLOCATION_ID_RELEASED, issue.get(PacmanSdkConstants.RESOURCE_ID),
							issue.get(PacmanSdkConstants.ALLOCATION_ID)));
		} catch (Exception e) {
			LOGGER.error(String.format(ELASTIC_IP_ALLOCATION_ID_RELEASE_FAILED,
					issue.get(PacmanSdkConstants.RESOURCE_ID), issue.get(PacmanSdkConstants.ALLOCATION_ID)), e.getMessage());
			return new FixResult(PacmanSdkConstants.STATUS_FAILURE_CODE,
					String.format(ELASTIC_IP_ALLOCATION_ID_RELEASE_FAILED, issue.get(PacmanSdkConstants.RESOURCE_ID),
							issue.get(PacmanSdkConstants.ALLOCATION_ID), e.getMessage()));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tmobile.pacman.commons.autofix.BaseFix#backupExistingConfigForResource(
	 * java.lang.String, java.lang.String, java.util.Map, java.util.Map)
	 */
	@Override
	public boolean backupExistingConfigForResource(final String resourceId, final String resourceType,
			Map<String, Object> clientMap, Map<String, String> ruleParams, Map<String, String> issue)
			throws AutoFixException {

		Gson gson = new GsonBuilder().create();
		return backupOldConfig(resourceId, ELASTICIPDETAILS, gson.toJson(issue));
	}

	@Override
	public boolean isFixCandidate(String resourceId, String resourceType, Map<String, Object> clientMap,
			Map<String, String> ruleParams, Map<String, String> issue) throws AutoFixException {
		int hours = AUTOFIX_DEFAULT_INTERVAL;// Default
		try {
			hours = Integer.parseInt(CommonUtils
					.getPropValue(ISSUE_CREATION_TIME_ELAPSED + "." + ruleParams.get(PacmanSdkConstants.RULE_ID)));
		} catch (Exception e) {
			LOGGER.error("Exception retrieving autofix configuration[{}]", e.getMessage());
		}
		return isCreateDateDaysOld(issue.get("createdDate"), hours);
	}

	static boolean isCreateDateDaysOld(String createDate, int hours) {

		LocalDateTime firstActionTime = LocalDateTime.parse(createDate, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
		LocalDateTime currentTime = LocalDateTime.now();
		long elapsedHours = ChronoUnit.HOURS.between(firstActionTime, currentTime);

		return elapsedHours > hours;
	}
	
	/* (non-Javadoc)
     * @see com.tmobile.pacman.commons.autofix.BaseFix#addDetailsToTransactionLog()
     */
    @Override
    public AutoFixTransaction addDetailsToTransactionLog(Map<String, String> annotation) {
    	LinkedHashMap<String,String> transactionParams = new LinkedHashMap();
    	if(!StringUtils.isNullOrEmpty(annotation.get("_resourceid"))){
		transactionParams.put("resourceId", annotation.get("_resourceid"));
    	}else{
    		transactionParams.put("resourceId","No Data");	
    	}
    	if(!StringUtils.isNullOrEmpty(annotation.get("accountid"))){
		transactionParams.put("accountId", annotation.get("accountid"));
    	}else{
    		transactionParams.put("accountId", "No Data");
    	}
    	
    	if(!StringUtils.isNullOrEmpty(annotation.get("region"))){
		transactionParams.put("region", annotation.get("region"));
    	}else{
    		transactionParams.put("region", "No Data");	
    	}
    	
    	if(!StringUtils.isNullOrEmpty(annotation.get("allocationId"))){
		transactionParams.put("allocationId", annotation.get(PacmanSdkConstants.ALLOCATION_ID));
    	}else{
    		transactionParams.put("allocationId", "No Data");	
    	}
		return new AutoFixTransaction(null,transactionParams);
    }
}
