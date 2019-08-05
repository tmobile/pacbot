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
 Copyright (C) 2018 T Mobile Inc - All Rights Reserve
 Purpose: Application ELB's publicly accessible AWS resources
 Author :Santhoshi,Kanchana
 Modified Date: Oct 22, 2018
 **/
package com.tmobile.pacman.autofix.sg;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.util.StringUtils;
import com.tmobile.pacman.autofix.publicaccess.PublicAccessAutoFix;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.common.exception.AutoFixException;
import com.tmobile.pacman.commons.autofix.BaseFix;
import com.tmobile.pacman.commons.autofix.FixResult;
import com.tmobile.pacman.commons.autofix.PacmanFix;
import com.tmobile.pacman.dto.AutoFixTransaction;
import com.tmobile.pacman.util.CommonUtils;

@PacmanFix(key = "unused-sg-auto-fix", desc = "Unused Security Group will be removed")
public class UnusedSgAutoFix extends BaseFix {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(UnusedSgAutoFix.class);
	

	@Override
	public FixResult executeFix(Map<String, String> issue, Map<String, Object> clientMap, Map<String, String> ruleParams) {
        String resourceId = issue.get("_resourceid");
        Map<String, Object> ec2ClinetMap = null;
        
        AmazonEC2 ec2Client = null;
        try {

            ec2ClinetMap = PublicAccessAutoFix.getAWSClient("ec2",issue,CommonUtils.getPropValue(PacmanSdkConstants.AUTO_FIX_ROLE_NAME));
            if (ec2ClinetMap != null) {
                ec2Client = (AmazonEC2) ec2ClinetMap.get("client");
                if(PublicAccessAutoFix.deleteSecurityGroup(resourceId, ec2Client)){
                	LOGGER.info("{} unused sg successfully deleted", resourceId);
                }
            }
        }

        catch (Exception e) {
            LOGGER.error("error in unused ag autofix {}",e);
            throw new RuntimeException(e.getMessage());
        }
         return new FixResult(PacmanSdkConstants.STATUS_SUCCESS_CODE, "the unused sg "+resourceId+" is now deleted");
         
    }

	
	/* * (non-Javadoc)
	 * 
	 * @see
	 * com.tmobile.pacman.commons.autofix.BaseFix#backupExistingConfigForResource
	 * (java.lang.String, java.lang.String, java.util.Map, java.util.Map,
	 * java.util.Map)*/
	 
	@Override
	public boolean backupExistingConfigForResource(final String resourceId, final String resourceType, Map<String, Object> clientMap, Map<String, String> ruleParams, Map<String, String> issue)throws AutoFixException {
	            return true;
	    }

	
	/* * (non-Javadoc)
	 * 
	 * @see
	 * com.tmobile.pacman.commons.autofix.BaseFix#isFixCandidate(java.lang.String
	 * , java.lang.String, java.util.Map, java.util.Map, java.util.Map)*/
	 
	@Override
	public boolean isFixCandidate(String resourceId, String resourceType, Map<String, Object> clientMap, Map<String, String> ruleParams, Map<String, String> issue) throws AutoFixException {
		String groupName = issue.get("groupname");
		return !StringUtils.isNullOrEmpty(groupName) && groupName.startsWith(PacmanSdkConstants.PACBOT_CREATED_SG_DESC);
	}

	
	 /** (non-Javadoc)
	 * 
	 * @see
	 * com.tmobile.pacman.commons.autofix.BaseFix#addDetailsToTransactionLog()*/
	 
	@Override
	public AutoFixTransaction addDetailsToTransactionLog(Map<String, String> annotation) {
		LinkedHashMap<String,String> transactionParams = new LinkedHashMap();
		if (!StringUtils.isNullOrEmpty(annotation.get("_resourceid"))) {
			transactionParams.put("resourceId", annotation.get("_resourceid"));
		} else {
			transactionParams.put("resourceId", "No Data");
		}
		if (!StringUtils.isNullOrEmpty(annotation.get("accountid"))) {
			transactionParams.put("accountId", annotation.get("accountid"));
		} else {
			transactionParams.put("accountId", "No Data");
		}
		if (!StringUtils.isNullOrEmpty(annotation.get("region"))) {
			transactionParams.put("region", annotation.get("region"));
		} else {
			transactionParams.put("region", "No Data");
		}
		if (!StringUtils.isNullOrEmpty(annotation.get("groupname"))) {
		transactionParams.put("groupName", annotation.get("groupname"));
		}else{
			transactionParams.put("groupName", "No Data");
		}
		return new AutoFixTransaction(null,transactionParams);
	}
}
