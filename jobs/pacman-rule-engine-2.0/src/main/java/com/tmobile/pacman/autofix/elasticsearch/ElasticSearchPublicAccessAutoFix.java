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
package com.tmobile.pacman.autofix.elasticsearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.elasticsearch.AWSElasticsearch;
import com.amazonaws.services.elasticsearch.model.ElasticsearchDomainStatus;
import com.amazonaws.services.elasticsearch.model.UpdateElasticsearchDomainConfigRequest;
import com.amazonaws.services.elasticsearch.model.VPCDerivedInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tmobile.pacman.autofix.publicaccess.PublicAccessAutoFix;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.common.exception.AutoFixException;
import com.tmobile.pacman.commons.autofix.BaseFix;
import com.tmobile.pacman.commons.autofix.FixResult;
import com.tmobile.pacman.commons.autofix.PacmanFix;
import com.tmobile.pacman.dto.AutoFixTransaction;
import com.tmobile.pacman.util.CommonUtils;

@PacmanFix(key = "publicly-accessible-elasticsearch", desc = "Elastic Search Applies Security Group without public access or It will deny the policy access")
public class ElasticSearchPublicAccessAutoFix extends BaseFix {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchPublicAccessAutoFix.class);
	
	private static final String EXISTING_GROUPS = "existingESGroups";
	private static final String EXISTING_POLICY = "existingESPolicy";

	public static String afterAutoFixSg = null;
	public static String beforeAutoFixSg = null;
	public static String afterPolicyAutoFix = null;
	@Override
	public FixResult executeFix(Map<String, String> issue, Map<String, Object> clientMap, Map<String, String> ruleParams) {
		afterAutoFixSg = null;
		String resourceId = issue.get("resourceDisplayId");
		String defaultCidrIp = ruleParams.get("defaultCidrIp");
		String cidrIp = ruleParams.get("cidrIp");
		String accessPolicies = null;
		String denyAccessPolicy = null;
		JsonArray statments = new JsonArray();
		Map<String, Object> ec2ClinetMap = null;
		List<SecurityGroup> securityGroupsDetails = null;
		AmazonEC2 ec2Client = null;
		String securityGroupId = null;
		Collection<IpPermission> ipPermissionsToBeAdded = null;
		Gson serializer = new GsonBuilder().create();
		try {
			UpdateElasticsearchDomainConfigRequest domainConfigRequest = new UpdateElasticsearchDomainConfigRequest();
			AWSElasticsearch awsElasticsearch = (AWSElasticsearch) clientMap.get("client");
			
			ElasticsearchDomainStatus domainStatus = PublicAccessAutoFix.getDomainStatusForEsResource(clientMap, resourceId);
			String endPoint = domainStatus.getEndpoint();
			if (!StringUtils.isEmpty(endPoint)) {
				accessPolicies = domainStatus.getAccessPolicies();
				JsonObject accessPoliciesJson = serializer.fromJson(accessPolicies, JsonObject.class);
				if (accessPoliciesJson.has("Statement")) {
					statments = accessPoliciesJson.get("Statement").getAsJsonArray();
					if (PublicAccessAutoFix.isEsHavingPublicAccess(statments,cidrIp)) {
						denyAccessPolicy = accessPolicies.replace("Allow", "Deny");
						
						domainConfigRequest.setAccessPolicies(denyAccessPolicy);
						domainConfigRequest.setDomainName(resourceId);
						awsElasticsearch.updateElasticsearchDomainConfig(domainConfigRequest);
						afterPolicyAutoFix = denyAccessPolicy;
					}
				}
			}else{
					ec2ClinetMap = PublicAccessAutoFix.getAWSClient("ec2", issue, CommonUtils.getPropValue(PacmanSdkConstants.AUTO_FIX_ROLE_NAME));
					List<String> securityGroupsTobeApplied = new ArrayList<>();
					VPCDerivedInfo vpcDerivedInfo = domainStatus.getVPCOptions();
					List<String> securityGroupNames = vpcDerivedInfo.getSecurityGroupIds();
					Set<String> securityGroupsSet = new HashSet<>(securityGroupNames);
					Set<String> alreadyCheckedSgSet = new HashSet<>();
					if (ec2ClinetMap != null) {
						ec2Client = (AmazonEC2) ec2ClinetMap.get("client");
						securityGroupsDetails = PublicAccessAutoFix.getExistingSecurityGroupDetails(securityGroupsSet, ec2Client);
					}
					String vpcid;
					Set<String> publiclyAccessible = new HashSet<>();
					boolean isSgApplied = false;

					for (SecurityGroup securityGroup : securityGroupsDetails) {
						ipPermissionsToBeAdded = new ArrayList<>();
						publiclyAccessible = new HashSet<>();
						vpcid = securityGroup.getVpcId();
						securityGroupId = securityGroup.getGroupId();
						PublicAccessAutoFix.nestedSecurityGroupDetails(securityGroupId, ipPermissionsToBeAdded, ec2Client, publiclyAccessible,alreadyCheckedSgSet,0);

						if (!publiclyAccessible.isEmpty()) {
							// copy the security group and remove in bound rules
							String createdSgId = PublicAccessAutoFix.createSecurityGroup(securityGroupId, vpcid,ec2Client, ipPermissionsToBeAdded,resourceId,defaultCidrIp,securityGroup.getIpPermissions());
							if(!StringUtils.isEmpty(createdSgId)){
							securityGroupsTobeApplied.add(createdSgId);
							}
						} else {
							securityGroupsTobeApplied.add(securityGroupId);
						}
					}
					if (!securityGroupsTobeApplied.isEmpty()) {
						isSgApplied = PublicAccessAutoFix.applySecurityGroupsToElacticSearch(awsElasticsearch, securityGroupsTobeApplied, resourceId, domainConfigRequest);
						afterAutoFixSg = securityGroupsTobeApplied.toString();
					}

					if (isSgApplied) {
						LOGGER.info("{} sg's successfully applied for the resource {}",securityGroupsTobeApplied,  resourceId);
					}
				
			}

		} catch (Exception e) {
			LOGGER.error("Error in elastic public access autofix",e);
			throw new RuntimeException(e.getMessage());
		}
		return new FixResult(PacmanSdkConstants.STATUS_SUCCESS_CODE,"the elasticsearch "+resourceId+" is now fixed");
	}

	
	/* * (non-Javadoc)
	 * 
	 * @see
	 * com.tmobile.pacman.commons.autofix.BaseFix#backupExistingConfigForResource
	 * (java.lang.String, java.lang.String, java.util.Map, java.util.Map,
	 * java.util.Map)*/
	 
	@Override
	public boolean backupExistingConfigForResource(final String resourceId, final String resourceType, Map<String, Object> clientMap, Map<String, String> ruleParams, Map<String, String> issue)throws AutoFixException {
		beforeAutoFixSg = null;
	    try {
	    String accessPolicies = null;
	    Gson serializer = new GsonBuilder().create();
	    String rId = issue.get("resourceDisplayId");
	    ElasticsearchDomainStatus domainStatus = PublicAccessAutoFix.getDomainStatusForEsResource(clientMap,rId);
	    String endPoint = domainStatus.getEndpoint();
        if (!StringUtils.isEmpty(endPoint)) {
        	
            accessPolicies = domainStatus.getAccessPolicies();
            JsonObject accessPoliciesJson = serializer.fromJson(accessPolicies, JsonObject.class);
            backupOldConfig(resourceId, EXISTING_POLICY, accessPoliciesJson.toString());
        }else{
        List<String> originalSgMembers;
        Set<String> oldConfigSet = new HashSet<>();
            VPCDerivedInfo vpcDerivedInfo = domainStatus.getVPCOptions();
            originalSgMembers = vpcDerivedInfo.getSecurityGroupIds();

            	oldConfigSet.addAll(originalSgMembers);
            	beforeAutoFixSg = oldConfigSet.toString();
            backupOldConfig(resourceId, EXISTING_GROUPS, oldConfigSet.toString());
        } 
        
	    }catch (Exception e) {
            LOGGER.error("back up failed", e);
            throw new AutoFixException("backup failed");
        }
            LOGGER.debug("backup complete for {}" , resourceId);
            return true;
	}

	
	/* * (non-Javadoc)
	 * 
	 * @see
	 * com.tmobile.pacman.commons.autofix.BaseFix#isFixCandidate(java.lang.String
	 * , java.lang.String, java.util.Map, java.util.Map, java.util.Map)*/
	 
	@Override
	public boolean isFixCandidate(String resourceId, String resourceType, Map<String, Object> clientMap, Map<String, String> ruleParams, Map<String, String> issue) throws AutoFixException {

		afterAutoFixSg = null;
		String rId = issue.get("resourceDisplayId");
		String cidrIp = ruleParams.get("cidrIp");
		String accessPolicies = null;
		JsonArray statments = new JsonArray();
		Map<String, Object> ec2ClinetMap = null;
		List<SecurityGroup> securityGroupsDetails = null;
		AmazonEC2 ec2Client = null;
		String securityGroupId = null;
		Collection<IpPermission> ipPermissionsToBeAdded = null;
		Gson serializer = new GsonBuilder().create();
		Boolean hasPublicAccess= false;
		try {
			
			ElasticsearchDomainStatus domainStatus = PublicAccessAutoFix.getDomainStatusForEsResource(clientMap, rId);
			String endPoint = domainStatus.getEndpoint();
			if (!StringUtils.isEmpty(endPoint)) {
				accessPolicies = domainStatus.getAccessPolicies();
				JsonObject accessPoliciesJson = serializer.fromJson(accessPolicies, JsonObject.class);
				if (accessPoliciesJson.has("Statement")) {
					statments = accessPoliciesJson.get("Statement").getAsJsonArray();
					if (PublicAccessAutoFix.isEsHavingPublicAccess(statments,cidrIp)) {
						hasPublicAccess = true;
					}
				}
			}else{
					ec2ClinetMap = PublicAccessAutoFix.getAWSClient("ec2", issue, CommonUtils.getPropValue(PacmanSdkConstants.AUTO_FIX_ROLE_NAME));
					VPCDerivedInfo vpcDerivedInfo = domainStatus.getVPCOptions();
					List<String> securityGroupNames = vpcDerivedInfo.getSecurityGroupIds();
					Set<String> securityGroupsSet = new HashSet<>(securityGroupNames);
					Set<String> alreadyCheckedSgSet = new HashSet<>();
					if (ec2ClinetMap != null) {
						ec2Client = (AmazonEC2) ec2ClinetMap.get("client");
						securityGroupsDetails = PublicAccessAutoFix.getExistingSecurityGroupDetails(securityGroupsSet, ec2Client);
					}
					Set<String> publiclyAccessible = new HashSet<>();

					for (SecurityGroup securityGroup : securityGroupsDetails) {
						ipPermissionsToBeAdded = new ArrayList<>();
						publiclyAccessible = new HashSet<>();
						securityGroupId = securityGroup.getGroupId();
						PublicAccessAutoFix.nestedSecurityGroupDetails(securityGroupId, ipPermissionsToBeAdded, ec2Client, publiclyAccessible,alreadyCheckedSgSet,0);

						if (!publiclyAccessible.isEmpty()) {
							hasPublicAccess = true;
						} 
					}
					
				
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		return hasPublicAccess;
	
	}

	
	 /** (non-Javadoc)
	 * 
	 * @see
	 * com.tmobile.pacman.commons.autofix.BaseFix#addDetailsToTransactionLog()*/
	 
	@Override
	public AutoFixTransaction addDetailsToTransactionLog(Map<String, String> annotation) {
		
		LinkedHashMap<String,String> transactionParams = new LinkedHashMap();
    	if(!StringUtils.isEmpty(annotation.get("_resourceid"))){
		transactionParams.put("resourceId", annotation.get("_resourceid"));
    	}else{
    		transactionParams.put("resourceId","No Data");	
    	}
    	if(!StringUtils.isEmpty(annotation.get("accountid"))){
		transactionParams.put("accountId", annotation.get("accountid"));
    	}else{
    		transactionParams.put("accountId", "No Data");
    	}
    	
    	if(!StringUtils.isEmpty(annotation.get("region"))){
		transactionParams.put("region", annotation.get("region"));
    	}else{
    		transactionParams.put("region", "No Data");	
    	}
    	
    	if(!StringUtils.isEmpty(afterPolicyAutoFix)){
    		transactionParams.put("policy", afterPolicyAutoFix);
        	}else{
        		transactionParams.put("policy", "No Data");	
        	}
    	
    	if (!StringUtils.isEmpty(afterAutoFixSg)) {
			transactionParams.put("attachedSg", afterAutoFixSg);
		}else{
			transactionParams.put("attachedSg", "No Data");
		}
		if (!StringUtils.isEmpty(beforeAutoFixSg)) {
			transactionParams.put("detachedSg", beforeAutoFixSg);
		} else {
			transactionParams.put("detachedSg", "No Data");
		}
		afterAutoFixSg = null;
		beforeAutoFixSg = null;
		afterPolicyAutoFix= null;
		return new AutoFixTransaction(null,transactionParams);
	}
}