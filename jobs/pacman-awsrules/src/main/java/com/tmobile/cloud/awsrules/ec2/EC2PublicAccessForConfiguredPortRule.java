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
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-ec2-with-public-access-for-configured-port", desc = "checks for EC2 instance which has IP address and looks for any of SG group has CIDR IP to 0.0.0.0 for configured port", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class EC2PublicAccessForConfiguredPortRule extends BaseRule {
    private static final Logger logger = LoggerFactory.getLogger(EC2PublicAccessForConfiguredPortRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     * ************* Following are the Rule Parameters********* <br><br>
     * 
     * internetGateWay : The value 'igw' is used to identify the security group with Internet gateway <br><br>
     * 
     * portToCheck : The port value of the security group <br><br>
     * 
     * ruleKey : check-for-ec2-with-public-access-for-configured-port <br><br>
     * 
     * severity : Enter the value of severity <br><br>
     * 
     * ruleCategory : Enter the value of category <br><br>
     * 
     * esEc2SgURL : Enter the EC2 with SG URL <br><br>
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
     * threadsafe : if true , rule will be executed on multiple threads <br><br>
     * 
     * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
     *
     */

    @Override
    public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
        logger.debug("========EC2PublicAccessForConfiguredPortRule started=========");
        Boolean isIgwExists = false;
        String ec2SgEsURL = null;
        String routetableAssociationsEsURL = null;
        String routetableRoutesEsURL = null;
        String routetableEsURL = null;
        String sgRulesUrl = null;
        Set<GroupIdentifier> securityGroupsSet = new HashSet<>();
        if (resourceAttributes.get("statename").equals(PacmanRuleConstants.RUNNING_STATE)) {
           
            String portToCheck = ruleParam.get(PacmanRuleConstants.PORT_TO_CHECK);
            String internetGateWay = ruleParam.get(PacmanRuleConstants.INTERNET_GATEWAY);
            String resourceId = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
            String publicipaddress = resourceAttributes.get(PacmanRuleConstants.PUBLIC_IP_ADDR);
            String subnetid = resourceAttributes.get("subnetid");
            String vpcid = resourceAttributes.get("vpcid");

            logger.debug("======publicipaddress : {}", publicipaddress);
            logger.debug("======subnetid : {}", subnetid);
            logger.debug("======vpcid : {}", vpcid);

            String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
            String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
            String cidrIp = ruleParam.get(PacmanRuleConstants.CIDR_IP);
            String cidrIpv6 = "::/0";

            String pacmanHost = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
            logger.debug("========pacmanHost {}  =========", pacmanHost);

            if (!StringUtils.isNullOrEmpty(pacmanHost)) {
                ec2SgEsURL = ruleParam.get(PacmanRuleConstants.ES_EC2_SG_URL);
                routetableAssociationsEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_ASSOCIATIONS_URL);
                routetableRoutesEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_ROUTES_URL);
                routetableEsURL = ruleParam.get(PacmanRuleConstants.ES_ROUTE_TABLE_URL);
                sgRulesUrl = ruleParam.get(PacmanRuleConstants.ES_SG_RULES_URL);

                ec2SgEsURL = pacmanHost + ec2SgEsURL;
                routetableAssociationsEsURL = pacmanHost + routetableAssociationsEsURL;
                routetableRoutesEsURL = pacmanHost + routetableRoutesEsURL;
                routetableEsURL = pacmanHost + routetableEsURL;
                sgRulesUrl = pacmanHost + sgRulesUrl;
            }
            logger.debug("========ec2SgEsURL URL after concatination param {}  =========", ec2SgEsURL);
            logger.debug("========routetableAssociationsEsURL URL after concatination param {}  =========",
                    routetableAssociationsEsURL);
            logger.debug("========routetableRoutesEsURL URL after concatination param {}  =========",
                    routetableRoutesEsURL);
            logger.debug("========routetableEsURL URL after concatination param {}  =========", routetableEsURL);
            logger.debug("========sgRulesUrl URL after concatination param {}  =========", sgRulesUrl);

            MDC.put("executionId", ruleParam.get("executionId"));
            MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
            List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
            LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
            if (!PacmanUtils.doesAllHaveValue(portToCheck, internetGateWay, severity, category, ec2SgEsURL, routetableAssociationsEsURL, routetableRoutesEsURL, routetableEsURL, sgRulesUrl, cidrIp)) {
                logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
                throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
            }

            try {
                if (!StringUtils.isNullOrEmpty(publicipaddress) ) {
                    issue.put(PacmanRuleConstants.PUBLICIP, publicipaddress);

                    Set<String> routeTableIdSet = PacmanUtils.getRouteTableId(subnetid, vpcid, routetableAssociationsEsURL, "subnet");
                    logger.debug("======routeTableId : {}", routeTableIdSet);
                    if(!routeTableIdSet.isEmpty()){
                    isIgwExists = PacmanUtils.isIgwFound(cidrIp, subnetid, "Subnet", issue, routeTableIdSet, routetableRoutesEsURL, internetGateWay,cidrIpv6);
                    }
                if (!isIgwExists && routeTableIdSet.isEmpty() && (!StringUtils.isNullOrEmpty(vpcid))) {
                        routeTableIdSet = PacmanUtils.getRouteTableId(subnetid, vpcid, routetableEsURL, "vpc");
                       logger.debug("======routeTableId : {}", routeTableIdSet);
                       if(!routeTableIdSet.isEmpty()){
                       isIgwExists = PacmanUtils.isIgwFound(cidrIp, vpcid, "VPC", issue, routeTableIdSet, routetableRoutesEsURL, internetGateWay,cidrIpv6);
                       }
                    }
                    if (isIgwExists) {
                        logger.debug("======entityId : {}", resourceId);
                        List<GroupIdentifier> listSecurityGroupID = PacmanUtils.getSecurityGroupsByInstanceId(resourceId, ec2SgEsURL);
                        securityGroupsSet.addAll(listSecurityGroupID);
                        issue.put(PacmanRuleConstants.SEC_GRP,org.apache.commons.lang3.StringUtils.join(listSecurityGroupID, "/"));
                    } else {
                        logger.info("EC2 is not publically accessble");
                    }
                    Annotation annotation = createAnnotation(resourceAttributes, securityGroupsSet, ruleParam, issueList, issue, sgRulesUrl);
                    if (null != annotation) {
                        return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE, annotation);
                    }

                }

            } catch (Exception exception) {
                logger.error("error: ", exception);
                throw new RuleExecutionFailedExeption(exception.getMessage());
            }

        }

        logger.debug("========EC2PublicAccessForConfiguredPortRule ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    @Override
    public String getHelpText() {
        return "checks for EC2 instance which has IP address and looks for any of SG group has CIDR IP to 0.0.0.0 for any configured port";
    }

    private Annotation createAnnotation(Map<String, String> resourceAttributes, Set<GroupIdentifier> securityGroupsSet, Map<String, String> ruleParam, List<LinkedHashMap<String, Object>> issueList, LinkedHashMap<String, Object> issue, String sgRulesUrl) {
        Annotation annotation = null;
        try {
            Map<String, Boolean> sgOpen = PacmanUtils.checkAccessibleToAll(securityGroupsSet,ruleParam.get(PacmanRuleConstants.PORT_TO_CHECK), sgRulesUrl, ruleParam.get(PacmanRuleConstants.CIDR_IP),"::/0","");

            if (!sgOpen.isEmpty()) {
                annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
                annotation.put(PacmanSdkConstants.DESCRIPTION,"EC2 has port : " + ruleParam.get(PacmanRuleConstants.PORT_TO_CHECK) + " publicly open");
                issue.put(PacmanRuleConstants.VIOLATION_REASON,"ResourceId " + ruleParam.get(PacmanSdkConstants.RESOURCE_ID)+ " has public access through port " + ruleParam.get(PacmanRuleConstants.PORT_TO_CHECK));
                issue.put(PacmanRuleConstants.PORTS_VIOLATED, ruleParam.get(PacmanRuleConstants.PORT_TO_CHECK));
                issueList.add(issue);
                annotation.put("issueDetails", issueList.toString());
                annotation.put("EC2PublicIP", resourceAttributes.get(PacmanRuleConstants.PUBLIC_IP_ADDR));
                annotation.put(PacmanRuleConstants.SEVERITY, ruleParam.get(PacmanRuleConstants.SEVERITY));
                annotation.put(PacmanRuleConstants.CATEGORY, ruleParam.get(PacmanRuleConstants.CATEGORY));
                annotation.put(PacmanRuleConstants.VPC_ID, resourceAttributes.get(PacmanRuleConstants.VPC_ID));
                annotation.put(PacmanRuleConstants.SUBNETID, resourceAttributes.get(PacmanRuleConstants.SUBNETID));
                logger.debug("========EC2PublicAccessForConfiguredPortRule ended with an annotation {} : =========", annotation);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuleExecutionFailedExeption(e.getMessage());
        }
        return annotation;
    }
}
