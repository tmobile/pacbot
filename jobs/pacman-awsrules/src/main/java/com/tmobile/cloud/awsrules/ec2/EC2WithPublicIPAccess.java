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
package com.tmobile.cloud.awsrules.ec2;

import java.util.ArrayList;
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
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.tmobile.cloud.awsrules.utils.PacmanEc2Utils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-ec2-public-access", desc = "checks for EC2 instance which has IP address and looks for any of SG group has CIDR IP to 0.0.0.0", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class EC2WithPublicIPAccess extends BaseRule {
    private static final Logger logger = LoggerFactory
            .getLogger(EC2WithPublicIPAccess.class);
    String cidrfilterValue = PacmanRuleConstants.CIDR_FILTERVALUE;
    String internetGateway = PacmanRuleConstants.INTERNET_GATEWAY;

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     * 
     *            internetGateWay : The value 'igw' is used to identify the
     *            security group with internet gateway <br>
     * <br>
     * 
     *            ec2SgEsURL : The ES URL of the security group <br>
     * <br>
     * 
     *            ruleKey : check-for-ec2-public-access <br>
     * <br>
     * 
     *            severity : Enter the value of severity <br>
     * <br>
     * 
     *            ruleCategory : Enter the value of category <br>
     * <br>
     * 
     *            esEc2SgURL : Enter the EC2 with SG URL <br>
     * <br>
     * 
     *            esRoutetableAssociationsURL : Enter the route table
     *            association ES URL <br>
     * <br>
     * 
     *            esRoutetableRoutesURL : Enter the route table routes ES URL <br>
     * <br>
     * 
     *            esRoutetableURL : Enter the route table ES URL <br>
     * <br>
     * 
     *            esSubnetURL: Enter the subnet ES URL <br>
	 * <br>
     * 
     *            esSgRulesUrl : Enter the SG rules ES URL <br>
     * <br>
     * 
     *            cidrIp : Enter the ip as 0.0.0.0/0 <br>
     * <br>
     * 
     *            threadsafe : if true , rule will be executed on multiple
     *            threads <br>
     * <br>
     * 
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided by execution engine
     *
     */

    @Override
    public RuleResult execute(Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {
        logger.debug("========EC2WithPublicIPAccess started=========");
        Annotation annotation = null;

        Set<String> routeTableIdSet = new HashSet<>();
        Boolean isIgwExists = false;
        if (resourceAttributes.get("statename").equals(
                PacmanRuleConstants.RUNNING_STATE)) {
            Set<GroupIdentifier> securityGroupsSet = new HashSet<>();
            String internetGateWay = ruleParam
                    .get(PacmanRuleConstants.INTERNET_GATEWAY);
            String entityId = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);

            String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
            String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
            String ec2SgEsURL = null;
            String routetableAssociationsEsURL = null;
            String routetableRoutesEsURL = null;
            String routetableEsURL = null;
            String sgRulesUrl = null;
            String subnetEsURL = null;
            String cidrIp = ruleParam.get(PacmanRuleConstants.CIDR_IP);
            
            String pacmanHost = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
            logger.debug("========pacmanHost {}  =========",pacmanHost);
            if(!StringUtils.isNullOrEmpty(pacmanHost)){
                ec2SgEsURL = ruleParam.get(PacmanRuleConstants.ES_EC2_SG_URL);
                routetableAssociationsEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_ASSOCIATIONS_URL);
                routetableRoutesEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_ROUTES_URL);
                routetableEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_URL);
                sgRulesUrl = ruleParam.get(PacmanRuleConstants.ES_SG_RULES_URL);
                
                ec2SgEsURL = pacmanHost+ec2SgEsURL;
                routetableAssociationsEsURL = pacmanHost+routetableAssociationsEsURL;
                routetableRoutesEsURL = pacmanHost+routetableRoutesEsURL;
                routetableEsURL = pacmanHost+routetableEsURL;
                sgRulesUrl = pacmanHost+sgRulesUrl;
                subnetEsURL = pacmanHost+subnetEsURL;
            }
            
            logger.debug("========ec2SgEsURL URL after concatination param {}  =========",ec2SgEsURL);
            logger.debug("========routetableAssociationsEsURL URL after concatination param {}  =========",routetableAssociationsEsURL);
            logger.debug("========routetableRoutesEsURL URL after concatination param {}  =========",routetableRoutesEsURL);
            logger.debug("========routetableEsURL URL after concatination param {}  =========",routetableEsURL);
            logger.debug("========sgRulesUrl URL after concatination param {}  =========",sgRulesUrl);

            String publicipaddress = resourceAttributes.get("publicipaddress");
            String subnetid = resourceAttributes.get("subnetid");
            String vpcid = resourceAttributes.get("vpcid");

            MDC.put("executionId", ruleParam.get("executionId")); 
            MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); 

            List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
            LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

            if (!PacmanUtils.doesAllHaveValue(internetGateWay, severity,
                    category, ec2SgEsURL, routetableAssociationsEsURL,
                    routetableRoutesEsURL, routetableEsURL, sgRulesUrl, cidrIp,subnetEsURL)) {
                logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
                throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
            }

            try {

                if (!StringUtils.isNullOrEmpty(publicipaddress)) {
                    issue.put(PacmanRuleConstants.PUBLICIP, publicipaddress);
                    routeTableIdSet = PacmanUtils.getRouteTableId(subnetid, vpcid,
                            routetableAssociationsEsURL, "subnet");
                    
                    isIgwExists = PacmanUtils.isIgwFound(cidrfilterValue,subnetid, "Subnet", issue, routeTableIdSet, routetableRoutesEsURL, internetGateWay);
                    
                    if (!isIgwExists && !StringUtils.isNullOrEmpty(vpcid)) {
                        routeTableIdSet = PacmanUtils.getRouteTableId(
                                    subnetid, vpcid, routetableEsURL, "vpc");
                        isIgwExists = PacmanUtils.isIgwFound(cidrfilterValue,vpcid, "VPC", issue, routeTableIdSet, routetableRoutesEsURL, internetGateWay);

                    }

                    if (isIgwExists) {
                        List<GroupIdentifier> listSecurityGroupID = PacmanUtils
                                .getSecurityGroupsByInstanceId(entityId,
                                        ec2SgEsURL);
                        securityGroupsSet.addAll(listSecurityGroupID);
                        issue.put(PacmanRuleConstants.SEC_GRP,
                                org.apache.commons.lang3.StringUtils.join(
                                        listSecurityGroupID, "/"));
                    } else {
                        logger.info("EC2 is not publicly accessble");

                    }

                    logger.info("calling Global IP method");
                    Map<String, Boolean> openPortsMap = getOpenPorts(securityGroupsSet, null, sgRulesUrl, cidrIp);
                    
                    List<String> portsSet = new ArrayList<>();
                    for (Map.Entry<String, Boolean> ports : openPortsMap
                            .entrySet()) {
                        portsSet.add(ports.getKey());
                    }

                    String ports = Joiner.on(", ").join(portsSet);
                    if (!openPortsMap.isEmpty()) {
                        Gson gson = new Gson();
                        String openPortsJson = gson.toJson(ports);
                        annotation = Annotation.buildAnnotation(ruleParam,
                                Annotation.Type.ISSUE);
                        annotation.put(PacmanSdkConstants.DESCRIPTION,
                                "EC2 with publicly accessible ports: " + ports);
                        annotation.put("EC2PublicIP", publicipaddress);
                        annotation.put("openPorts", openPortsJson);
                        annotation.put(PacmanRuleConstants.SEVERITY, severity);
                        annotation.put(PacmanRuleConstants.CATEGORY, category);
                        annotation.put(PacmanRuleConstants.VPC_ID,vpcid);
                        annotation.put(PacmanRuleConstants.SUBNETID,subnetid);

                        issue.put(PacmanRuleConstants.VIOLATION_REASON,
                                "EC2 with publicly accessible ports found");
                        issue.put(PacmanRuleConstants.PORTS_VIOLATED,
                                String.join(",", portsSet));
                        issueList.add(issue);
                        annotation.put("issueDetails", issueList.toString());
                        logger.debug("========EC2WithPublicIPAccess ended with an annotation {} : =========",annotation);
                        return new RuleResult(
                                PacmanSdkConstants.STATUS_FAILURE,
                                PacmanRuleConstants.FAILURE_MESSAGE, annotation);
                    }
                
            }
            } catch (Exception exception) {
                logger.error("error: ", exception);
                throw new RuleExecutionFailedExeption(exception.getMessage());
            }
        }
        logger.debug("========EC2WithPublicIPAccess ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);

       }

    @SuppressWarnings("unused")
    private Map<String, Boolean> getOpenPorts(
            Set<GroupIdentifier> securityGroupsSet, String portToCheck,
            String sgRulesUrl, String cidrIp){
    Map<String, Boolean> openPortsMap;
    logger.info("calling Global IP method");
    try {
        openPortsMap = PacmanEc2Utils.checkAccessibleToAll(
                securityGroupsSet, null, sgRulesUrl, cidrIp);
    } catch (Exception e) {
        logger.error("error: ", e);
        throw new RuleExecutionFailedExeption(e.getMessage());
    }
    return openPortsMap;
}
    
    @Override
    public String getHelpText() {

        return "checks entirely for ec2 instance with public access of security group";
    }

}
