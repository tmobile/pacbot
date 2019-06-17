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
  Author :Kanchana
  Modified Date: Jun 17, 2019
  
 **/
package com.tmobile.cloud.awsrules.rds;

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
import com.tmobile.cloud.awsrules.redshift.RedShiftPublicAccessRule;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-rds-db-public-access", desc = "This rule checks for RDS DB is publicaly accessble, if yes then it creates an issue", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class RDSDBPublicAcessRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(RedShiftPublicAccessRule.class);
	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 **************Following are the Rule Parameters********* <br><br>
	 * 
	 * ruleKey : check-for-rds-db-public-access <br><br>
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
	 * esRdsDbSgUrl : Enter the rdsDb with SG URL <br><br>
	 * 
	 * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
	 *
	 */

	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========RDSDBPublicAcessRule started=========");Annotation annotation = null;
		String subnet = null;
		String routetableAssociationsEsURL = null;
		String rdsdbSgEsURL = null;
		String routetableRoutesEsURL = null;
		String routetableEsURL = null;
		String sgRulesUrl = null;
		
		Boolean isIgwExists = false;
		Set<String> routeTableIdSet = new HashSet<>();
		Map<String, Boolean> openPortsMap = new HashMap<>();
		Set<GroupIdentifier> securityGroupsSet = new HashSet<>();
		LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
		
		String resourceId = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String vpcId = resourceAttributes.get("vpcid");
		String cidrIp = ruleParam.get(PacmanRuleConstants.CIDR_IP);
		String cidrIpv6 = ruleParam.get(PacmanRuleConstants.CIDRIPV6);
		String internetGateWay = ruleParam.get(PacmanRuleConstants.INTERNET_GATEWAY);
		String securityGroups = resourceAttributes.get(PacmanRuleConstants.SECURITY_GROUPS);
		String dbInstanceIdentifier = resourceAttributes.get(PacmanRuleConstants.DB_INSTANCE_IDENTIFIER);
		String endPointPort = resourceAttributes.get(PacmanRuleConstants.ENDPOINT_PORT);
		
		if(!resourceAttributes.containsKey(PacmanRuleConstants.ENDPOINT_PORT) && StringUtils.isNullOrEmpty(endPointPort)){
			endPointPort = "0";
		}
		
		String description ="RdsDb has publicly accessible ports" + endPointPort;
		MDC.put("executionId", ruleParam.get("executionId"));
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

		String pacmanHost = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
		logger.debug("========pacmanHost {}  =========", pacmanHost);

		if (!StringUtils.isNullOrEmpty(pacmanHost)) {
			routetableAssociationsEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_ASSOCIATIONS_URL);
			rdsdbSgEsURL = ruleParam.get(PacmanRuleConstants.ES_RDSDB_SG_URL);
			routetableRoutesEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_ROUTES_URL);
			routetableEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_URL);
			sgRulesUrl = ruleParam.get(PacmanRuleConstants.ES_SG_RULES_URL);

			routetableAssociationsEsURL = pacmanHost + routetableAssociationsEsURL;
			rdsdbSgEsURL = pacmanHost + rdsdbSgEsURL;
			routetableRoutesEsURL = pacmanHost + routetableRoutesEsURL;
			routetableEsURL = pacmanHost + routetableEsURL;
			sgRulesUrl = pacmanHost + sgRulesUrl;
		}
		
		if (!PacmanUtils.doesAllHaveValue(cidrIpv6,internetGateWay, severity, category, rdsdbSgEsURL,routetableAssociationsEsURL, routetableRoutesEsURL, routetableEsURL, sgRulesUrl, cidrIp)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}
		
		try {
			if (!StringUtils.isNullOrEmpty(resourceAttributes.get(PacmanRuleConstants.PUBLIC_ACCESS)) && Boolean.parseBoolean(resourceAttributes.get(PacmanRuleConstants.PUBLIC_ACCESS))) {	
				String subnets = resourceAttributes.get(PacmanRuleConstants.SUBNETS_LIST);
				if(!StringUtils.isNullOrEmpty(subnets)){
				List<String> subnetsList = new ArrayList(Arrays.asList(subnets.split(",")));
				for (String subnetId : subnetsList) {
					routeTableIdSet = PacmanUtils.getRouteTableId(subnetId,null, routetableAssociationsEsURL, "subnet");
					logger.debug("======routeTableId : {}", routeTableIdSet);
					if(!routeTableIdSet.isEmpty()){
					isIgwExists = PacmanUtils.isIgwFound(cidrIp, subnetId, "Subnet", issue, routeTableIdSet, routetableRoutesEsURL, internetGateWay,cidrIpv6);
					}
					if (isIgwExists) {
						subnet = subnetId;
						break;
					}
				}
			}

				if (!isIgwExists && routeTableIdSet.isEmpty() && (!StringUtils.isNullOrEmpty(vpcId))) {
					routeTableIdSet = PacmanUtils.getRouteTableId(null, vpcId, routetableEsURL, "vpc");
					logger.debug("======routeTableId : {}", routeTableIdSet);
					if(!routeTableIdSet.isEmpty()){
					isIgwExists = PacmanUtils.isIgwFound(cidrIp, vpcId, "VPC", issue, routeTableIdSet,routetableRoutesEsURL, internetGateWay,cidrIpv6);
					}
				}

				if(isIgwExists) {
					logger.debug("======Redshiftcluster id : {}", resourceId);
					List<GroupIdentifier> listSecurityGroupID = new ArrayList<>();
					listSecurityGroupID =  PacmanUtils.getSecurityGrouplist(securityGroups.replace(":active", ""), ",",listSecurityGroupID);
					securityGroupsSet.addAll(listSecurityGroupID);
					logger.info("calling Global IP method");
					if(!securityGroupsSet.isEmpty()){
						openPortsMap =  PacmanUtils.checkAccessibleToAll(securityGroupsSet,endPointPort, sgRulesUrl, cidrIp,cidrIpv6,"");
					}else{
						logger.error("sg not associated to the resource");
						throw new RuleExecutionFailedExeption("sg not associated to the resource");
					}
					
					issue.put(PacmanRuleConstants.SEC_GRP,org.apache.commons.lang3.StringUtils.join(listSecurityGroupID, "/"));
				} else {
					logger.info("not a publicly accessible rdsdb");
				}
			
				if (!openPortsMap.isEmpty()) {
					annotation = PacmanUtils.setAnnotation(openPortsMap, ruleParam,subnet,description, issue);
					if (null != annotation) {
						annotation.put(PacmanRuleConstants.RESOURCE_DISPLAY_ID, dbInstanceIdentifier);
						return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,PacmanRuleConstants.FAILURE_MESSAGE, annotation);
					}
				}
			}
		} catch (Exception exception) {
			logger.error("error: ", exception);
			throw new RuleExecutionFailedExeption(exception.getMessage());
		}
		logger.debug("========RDSDBPublicAcessRule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
		
	}

	public String getHelpText() {
		return "This rule checks rdsdb has public access";
	}
	
}
