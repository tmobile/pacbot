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
 Purpose: This rule check for the elastic search exposed to public
 Author :Kkambal1
 Modified Date: Oct 4, 2018

 **/
package com.tmobile.cloud.awsrules.elasticsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-elastic-search-public-access", desc = "This rule check for es which is exposed to public", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class ElasticSearchPublicAccessRule extends BaseRule {
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchPublicAccessRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 * ************* Following are the Rule Parameters********* <br><br>
	 * ruleKey : check-for-elastic-search-public-access<br><br>
	 * 
	 * internetGateWay : The value 'igw' is used to identify the security group with Internet gateway <br><br>
	 *
     * esRoutetableAssociationsURL : Enter the route table association ES URL <br><br>
     * 
     * esRoutetableRoutesURL : Enter the route table routes ES URL <br><br>
     * 
     * esRoutetableURL : Enter the route table ES URL <br><br>
     * 
     * esSgRulesUrl : Enter the SG rules ES URL <br><br>
     * 
     * cidrIp : Enter the ip as 0.0.0.0/0 <br><br>
     * 
     * cidripv6 : Enter the ip as ::/0 <br><br>
	 * 
	 * severity : Enter the value of severity <br><br>
	 * 
	 * ruleCategory : Enter the value of category <br><br>
	 * 
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========ElasticSearchPublicAccessRule started=========");
		 JsonParser jsonParser = new JsonParser();
		 Set<String> routeTableIdSet = new HashSet<>();
		 Set<GroupIdentifier> securityGroupsSet = new HashSet<>();
		 Map<String, Boolean> openPortsMap = new HashMap<>();
		 LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
		 Gson gson = new Gson();
		 Annotation annotation = null;
		 Boolean isIgwExists = false;
         String routetableAssociationsEsURL = null;
         String routetableRoutesEsURL = null;
         String routetableEsURL = null;
         String sgRulesUrl = null;
		 String endPoint = resourceAttributes.get(PacmanRuleConstants.END_POINT); 
		 String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		 String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		 String vpcId = resourceAttributes.get(PacmanRuleConstants.VPC_ID);
		 String subnetId = resourceAttributes.get(PacmanRuleConstants.SUBNETID);
		 String securityGroupId = resourceAttributes.get(PacmanRuleConstants.EC2_WITH_SECURITYGROUP_ID);
		 String internetGateWay = ruleParam.get(PacmanRuleConstants.INTERNET_GATEWAY);
		 String cidrIp = ruleParam.get(PacmanRuleConstants.CIDR_IP);
		 String cidrIpv6 = ruleParam.get(PacmanRuleConstants.CIDRIPV6);
		 String targetType = resourceAttributes.get(PacmanRuleConstants.ENTITY_TYPE);
		 String description = targetType + " has publicly accessible ports";

        String pacmanHost = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
        logger.debug("========pacmanHost {}  =========", pacmanHost);

        if (!StringUtils.isNullOrEmpty(pacmanHost)) {
            routetableAssociationsEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_ASSOCIATIONS_URL);
            routetableRoutesEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_ROUTES_URL);
            routetableEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_URL);
            sgRulesUrl = ruleParam.get(PacmanRuleConstants.ES_SG_RULES_URL);

            routetableAssociationsEsURL = pacmanHost + routetableAssociationsEsURL;
            routetableRoutesEsURL = pacmanHost + routetableRoutesEsURL;
            routetableEsURL = pacmanHost + routetableEsURL;
            sgRulesUrl = pacmanHost + sgRulesUrl;
        } 
		

		MDC.put("executionId", ruleParam.get("executionId"));
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

		if (!PacmanUtils.doesAllHaveValue(cidrIpv6,internetGateWay, severity, category, routetableAssociationsEsURL, routetableRoutesEsURL, routetableEsURL, sgRulesUrl, cidrIp)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		try {
			
			if (!StringUtils.isNullOrEmpty(endPoint)) {
				if (resourceAttributes.containsKey(PacmanRuleConstants.ACCESS_POLICIES)) {
					JsonObject accessPoliciesJson = (JsonObject) jsonParser.parse(resourceAttributes.get(PacmanRuleConstants.ACCESS_POLICIES));
					if (accessPoliciesJson.has(PacmanRuleConstants.STATEMENT)) {
						JsonArray statments = accessPoliciesJson.get(PacmanRuleConstants.STATEMENT).getAsJsonArray();
						if (PacmanUtils.isHavingPublicAccess(statments,"http://"+endPoint)) {
							accessPoliciesJson.add("endPoint", gson.fromJson(endPoint, JsonElement.class));
							description = "Elastic search is open to internet " + accessPoliciesJson ;
							annotation = PacmanUtils.createAnnotation(null, ruleParam, description, severity, category);
							if(null!=annotation){
							annotation.put(PacmanRuleConstants.RESOURCE_DISPLAY_ID, resourceAttributes.get("domainname"));
							return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE,annotation);
							}
						} else {
							logger.debug("========ElasticSearchPublicAccessRule ended=========");
							return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
						}

					}
				}
			} else {
				if (!StringUtils.isNullOrEmpty(subnetId)) {
					routeTableIdSet = PacmanUtils.getRouteTableId(subnetId, vpcId, routetableAssociationsEsURL, "subnet");
					logger.debug("======routeTableId : {}", routeTableIdSet);
					 if(!routeTableIdSet.isEmpty()){
					isIgwExists = PacmanUtils.isIgwFound(cidrIp, subnetId, "Subnet", issue, routeTableIdSet, routetableRoutesEsURL, internetGateWay,cidrIpv6);
					 }
				}
				if (!isIgwExists && routeTableIdSet.isEmpty() && (!StringUtils.isNullOrEmpty(vpcId))) {
					routeTableIdSet = PacmanUtils.getRouteTableId(subnetId, vpcId, routetableEsURL, "vpc");
					logger.debug("======routeTableId : {}", routeTableIdSet);
					 if(!routeTableIdSet.isEmpty()){
					isIgwExists = PacmanUtils.isIgwFound(cidrIp, vpcId, "VPC", issue, routeTableIdSet, routetableRoutesEsURL, internetGateWay,cidrIpv6);
					 }

				}
				if (isIgwExists) {
					List<GroupIdentifier> listSecurityGroupID = new ArrayList<>();
					listSecurityGroupID = PacmanUtils.getSecurityGrouplist(securityGroupId, ",", listSecurityGroupID);
					securityGroupsSet.addAll(listSecurityGroupID);
					issue.put(PacmanRuleConstants.SEC_GRP,org.apache.commons.lang3.StringUtils.join(listSecurityGroupID, "/"));
				} else {
					logger.info("Elasticsearch is not publically accessble");
				}
				logger.info("calling Global IP method");
				if (!securityGroupsSet.isEmpty()) {
					openPortsMap =  PacmanUtils.checkAccessibleToAll(securityGroupsSet,
	                         "ANY", sgRulesUrl, cidrIp,cidrIpv6,"");
				}

				if (!openPortsMap.isEmpty()) {
					annotation = PacmanUtils.setAnnotation(openPortsMap,ruleParam, subnetId, description, issue);
					if (null != annotation) {
						annotation.put("endpoint", endPoint);
						annotation.put(PacmanRuleConstants.RESOURCE_DISPLAY_ID, resourceAttributes.get("domainname"));
						return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
            throw new RuleExecutionFailedExeption(e.getMessage());
        }
		logger.debug("========ElasticSearchPublicAccessRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	@Override
	public String getHelpText() {
		return "This rule check for es which is exposed to public";
	}
}
