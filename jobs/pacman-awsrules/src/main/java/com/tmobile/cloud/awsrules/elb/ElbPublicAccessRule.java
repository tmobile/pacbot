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
package com.tmobile.cloud.awsrules.elb;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-elb-public-access", desc = "This rule checks for application/classic elb which is exposed to public", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class ElbPublicAccessRule extends BaseRule {
	private static final Logger logger = LoggerFactory.getLogger(ElbPublicAccessRule.class);

	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 ************** Following are the Rule Parameters********* <br><br>
	 *
	 *ruleKey : check-for-elb-public-access<br><br>
	 *
	 *internetGateWay : The value 'igw' is used to identify the security group with Internet gateway <br><br>
	 *
	 *esElbWithSGUrl : Enter the appELB/classicELB with SG URL <br><br>
     * 
     *esRoutetableAssociationsURL : Enter the route table association ES URL <br><br>
     * 
     *esRoutetableRoutesURL : Enter the route table routes ES URL <br><br>
     * 
     *esRoutetableURL : Enter the route table ES URL <br><br>
     * 
     *esSgRulesUrl : Enter the SG rules ES URL <br><br>
     * 
     *cidrIp : Enter the ip as 0.0.0.0/0 <br><br>
     *
     *cidripv6 : Enter the ip as ::/0 <br><br>
	 * 
	 *severity : Enter the value of severity <br><br>
	 * 
	 *ruleCategory : Enter the value of category <br><br>
	 * 
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(final Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========ElbPublicAccessRule started=========");
		Annotation annotation = null;
		String subnet = null;
		String routetableAssociationsEsURL = null;
		String routetableRoutesEsURL = null;
		String routetableEsURL = null;
		String sgRulesUrl = null;
		String elbSgUrl = null;
		Set<String> routeTableIdSet = new HashSet<>();
		Boolean isIgwExists = false;
		Set<GroupIdentifier> securityGroupsSet = new HashSet<>();
		LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
		Map<String, Boolean> openPortsMap = new HashMap<>();

		String scheme = resourceAttributes.get(PacmanRuleConstants.SCHEME);
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String internetGateWay = ruleParam.get(PacmanRuleConstants.INTERNET_GATEWAY);
		String loadBalncerId = ruleParam.get(PacmanRuleConstants.RESOURCE_ID);
		String region = resourceAttributes.get(PacmanRuleConstants.REGION_ATTR);
		String accountId = resourceAttributes.get(PacmanRuleConstants.ACCOUNTID);
		String vpcId = resourceAttributes.get(PacmanRuleConstants.VPC_ID);
		String cidrIp = ruleParam.get(PacmanRuleConstants.CIDR_IP);
		String cidrIpv6 = ruleParam.get(PacmanRuleConstants.CIDRIPV6);
		String targetType = resourceAttributes.get(PacmanRuleConstants.ENTITY_TYPE);
		String description = targetType+" Elb has publicly accessible ports";
		String elbType = resourceAttributes.get(PacmanRuleConstants.ELB_TYPE);

		String pacmanHost = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
		logger.debug("========pacmanHost {}  =========", pacmanHost);

		if (!StringUtils.isNullOrEmpty(pacmanHost)) {
			routetableAssociationsEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_ASSOCIATIONS_URL);
			routetableRoutesEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_ROUTES_URL);
			routetableEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_URL);
			sgRulesUrl = ruleParam.get(PacmanRuleConstants.ES_SG_RULES_URL);
			elbSgUrl = ruleParam.get(PacmanRuleConstants.ES_ELB_WITH_SECURITYGROUP_URL);

			routetableAssociationsEsURL = pacmanHost + routetableAssociationsEsURL;
			routetableRoutesEsURL = pacmanHost + routetableRoutesEsURL;
			routetableEsURL = pacmanHost + routetableEsURL;
			sgRulesUrl = pacmanHost + sgRulesUrl;
			elbSgUrl = pacmanHost + elbSgUrl;
		}

		MDC.put("executionId", ruleParam.get("executionId"));
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

		if (!PacmanUtils.doesAllHaveValue(cidrIpv6,internetGateWay, severity, category, elbSgUrl,routetableAssociationsEsURL, routetableRoutesEsURL, routetableEsURL, sgRulesUrl, cidrIp)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		try {
			if (!StringUtils.isNullOrEmpty(scheme) && scheme.equals(PacmanRuleConstants.INTERNET_FACING)) {
				String subnets = resourceAttributes.get(PacmanRuleConstants.SUBNETS_LIST);
				if(!StringUtils.isNullOrEmpty(subnets)){
				List<String> subnetsList = new ArrayList(Arrays.asList(subnets.split(":;")));
				for (String subnetId : subnetsList) {
					routeTableIdSet = PacmanUtils.getRouteTableId(subnetId,null, routetableAssociationsEsURL, "subnet");
					logger.debug("======routeTableId : {}", routeTableIdSet);
					isIgwExists = PacmanUtils.isIgwFound(cidrIp, subnetId, "Subnet", issue, routeTableIdSet, routetableRoutesEsURL, internetGateWay,cidrIpv6);
					if (isIgwExists) {
						subnet = subnetId;
						break;
					}
				}
			}

				if (!isIgwExists && routeTableIdSet.isEmpty() && (!StringUtils.isNullOrEmpty(vpcId))) {
					routeTableIdSet = PacmanUtils.getRouteTableId(null, vpcId, routetableEsURL, "vpc");
					logger.debug("======routeTableId : {}", routeTableIdSet);
					isIgwExists = PacmanUtils.isIgwFound(cidrIp, vpcId, "VPC", issue, routeTableIdSet,routetableRoutesEsURL, internetGateWay,cidrIpv6);
				}

				if(isIgwExists) {
					logger.debug("======loadBalncerId : {}", loadBalncerId);
					List<GroupIdentifier> listSecurityGroupID = PacmanUtils.getSecurityBroupIdByElb(loadBalncerId, elbSgUrl, accountId, region);
					securityGroupsSet.addAll(listSecurityGroupID);
					logger.info("calling Global IP method");
					if(!securityGroupsSet.isEmpty()){
						openPortsMap =  PacmanUtils.checkAccessibleToAll(securityGroupsSet,"ANY", sgRulesUrl, cidrIp,cidrIpv6,"");
					}else{
						logger.error("sg not associated to the resource");
						throw new RuleExecutionFailedExeption("sg not associated to the resource");
					}
					
					issue.put(PacmanRuleConstants.SEC_GRP,org.apache.commons.lang3.StringUtils.join(listSecurityGroupID, "/"));
				} else {
					logger.info("publicly accessible elb {}"+ targetType);
				}
			
				if (!openPortsMap.isEmpty()) {
					annotation = PacmanUtils.setAnnotation(openPortsMap, ruleParam,subnet,description, issue);
					annotation.put(PacmanRuleConstants.SCHEME,scheme);
					if (null != annotation) {
						if("appelb".equals(targetType)){
							annotation.put(PacmanRuleConstants.TYPE_OF_ELB,elbType);
							annotation.put(PacmanRuleConstants.RESOURCE_DISPLAY_ID, resourceAttributes.get("loadbalancerarn"));
						}
						return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuleExecutionFailedExeption(e.getMessage());
		}
		logger.debug("========ElbPublicAccessRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	@Override
	public String getHelpText() {
		return "This rule check for application/classic elb which is exposed to public";
	}
}
