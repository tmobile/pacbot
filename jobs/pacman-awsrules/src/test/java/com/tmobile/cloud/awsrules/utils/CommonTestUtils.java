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
package com.tmobile.cloud.awsrules.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;

public class CommonTestUtils {

    public static Map<String, String> getMapString(String passRuleResourceId) {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("iamPriviliges","iamPriviliges");
        commonMap.put(",lambda:*,*",",lambda:*,*");
        commonMap.put(",ec2:*,*",",ec2:*,*");
        commonMap.put("lambda","lambda");
        commonMap.put(",ec2:*,*,s3:*,s3:put*",",ec2:*,*,s3:*,s3:put*");

        commonMap.put("cidripv6", "cidripv6");
        commonMap.put("username", "svc_123");
        commonMap.put("associationid", "associationid");
        commonMap.put("domainname", "domainname");
        commonMap.put("accesspolicies", "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"*\"},\"Action\":\"es:*\",\"Resource\":\"123/*\"}]}");
        commonMap.put("scheme", "internet-facing");
        commonMap.put("subnets", "subnets");
        commonMap.put("esElbWithSGUrl", "esElbWithSGUrl");
        commonMap.put("esEc2SgURL", "esEc2SgURL");
        commonMap.put("endpoint", "endpoint");
        commonMap.put("esRoutetableAssociationsURL", "esRoutetableAssociationsURL");
        commonMap.put("esRoutetableRoutesURL", "esRoutetableRoutesURL");
        commonMap.put("esRoutetableURL", "esRoutetableURL");
        commonMap.put("esSgRulesUrl", "esSgRulesUrl");
        commonMap.put("esSubnetURL", "esSubnetURL");
        commonMap.put("identifiableKey", "identifiableKey");
        commonMap.put("subnetEsURL", "subnetEsURL");
        commonMap.put("esSubnetURL", "esSubnetURL");
        commonMap.put("awsSearch", "awsSearch");
        commonMap.put("kernelInfoApi", "kernelInfoApi");
        commonMap.put("esNonAdminAccntsWithIAMFullAccessUrl", "esNonAdminAccntsWithIAMFullAccessUrl");
        commonMap.put("esLdapUrl", "esLdapUrl");
        commonMap.put("esQualysUrl", "esQualysUrl");
        commonMap.put("esSatAndSpacewalkUrl", "esSatAndSpacewalkUrl");
        commonMap.put("esServiceURL", "esServiceURL");
        commonMap.put("esAdGroupURL", "esAdGroupURL");
        commonMap.put("esEbsWithInstanceUrl", "esEbsWithInstanceUrl");
        commonMap.put("esAppTagURL", "esAppTagURL");
        commonMap.put("esEc2SgURL", "esEc2SgURL");
        commonMap.put("esEc2WithVulnInfoForS5Url", "esEc2WithVulnInfoForS5Url");
        commonMap.put("esEc2PubAccessPortUrl", "esEc2PubAccessPortUrl");
        commonMap.put("esSsmWithInstanceUrl", "esSsmWithInstanceUrl");
        commonMap.put("esElasticIpUrl", "esElasticIpUrl");
        commonMap.put("esAppElbWithInstanceUrl", "esAppElbWithInstanceUrl");
        commonMap.put("esClassicElbWithInstanceUrl", "esClassicElbWithInstanceUrl");
        commonMap.put("esGuardDutyUrl", "esGuardDutyUrl");
        commonMap.put("esNonAdminAccntsWithIAMFullAccessUrl", "esNonAdminAccntsWithIAMFullAccessUrl");
        commonMap.put("esSgRulesUrl", "esSgRulesUrl");
        commonMap.put("esServiceWithSgUrl", "esServiceWithSgUrl");
        commonMap.put("ES_URI", "ES_URI");
        commonMap.put("executionId", "1234");
        commonMap.put("_resourceid", passRuleResourceId);
        commonMap.put("severity", "low");
        commonMap.put("ruleCategory", "security");
        commonMap.put("type", "Task");
        commonMap.put("accountid", "12345");
        commonMap.put("checkId", "1234567");
        commonMap.put("serviceEsURL", "url");
        commonMap.put("serviceAccountEsURL", "serviceAccountEsURL");
        commonMap.put("description", "R FND");
        commonMap.put("elasticIpEsUrl", "elasticIpEsUrl");
        commonMap.put("region", "us-east-1");
        commonMap.put("authType", "authType");
        commonMap.put("splitterChar", ",");
        commonMap.put("roleIdentifyingString", "roleIdentifyingString");
        commonMap.put("ldapApi", "ldapApi");
        commonMap.put("satAndSpacewalkApi", "satAndSpacewalkApi");
        commonMap.put("qualysApi", "qualysApi");
        commonMap.put("kernelVersionByInstanceIdUrl",
                "kernelVersionByInstanceIdUrl");
        commonMap.put("defaultKernelCriteriaUrl", "defaultKernelCriteriaUrl");
        commonMap.put("accountNames", "accountNames");
        commonMap.put("sourceType", "sourceType");
        commonMap.put("statename", "running");
        commonMap.put("ebsWithInstanceUrl", "ebsWithInstanceUrl");
        commonMap.put("volumeid", "volumeid");
        commonMap.put("loadbalancername", "loadbalancername");
        commonMap.put("targetExpireDuration", "150");
        commonMap.put("validto", "12/10/2018 23:33");
        commonMap.put("appElbWithInstanceUrl", "appElbWithInstanceUrl");
        commonMap.put("loadbalancerarn", "loadbalancerarn");
        commonMap.put("classicElbWithInstanceUrl", "classicElbWithInstanceUrl");
        commonMap.put("guardDutyEsUrl", "guardDutyEsUrl");
        commonMap.put("dbinstanceidentifier", "dbinstanceidentifier");
        commonMap.put("dbsnapshotarn", "dbsnapshotarn");
        commonMap.put("publiclyaccessible", "true");
        commonMap.put("apiGWURL", "apiGWURL");
        commonMap.put("portToCheck", "22");
        commonMap.put("sgRulesUrl", "sgRulesUrl");
        commonMap.put("cidrIp", "cidrIp");
        commonMap.put("serviceWithSgUrl", "serviceWithSgUrl");
        commonMap.put("esUrl", "esUrl");
        commonMap.put("groupid", "groupid");
        commonMap.put("adGroupEsURL", "adGroupEsURL");
        commonMap.put("target", "30");
        commonMap.put("inScope", "true");
        commonMap.put("role", "role");
        commonMap.put("passwordlastused", "2018-07-16 12:16:38+00");
        commonMap.put("pwdInactiveDuration", "1");
        commonMap.put("status_RED", "status_RED");
        commonMap.put("tags.Application", "identifiableKey");
        commonMap.put("_entitytype", "elasticache");
        commonMap.put("appTagEsURL", "appTagEsURL");
        commonMap.put("heimdallESURL", "heimdallESURL");
        commonMap.put("deprecatedInstanceType", "deprecatedInstanceType");
        commonMap.put("instancetype", "xyz");
        commonMap.put("running", "running");
        commonMap.put("instanceid", "instanceid");
        commonMap.put("ec2PubAccessPortUrl", "ec2PubAccessPortUrl");
        commonMap.put("ec2WithVulnInfoForS5Url", "ec2WithVulnInfoForS5Url");
        commonMap.put("ec2PortRuleId", "ec2PortRuleId");
        commonMap.put("severityVulnValue", "severityVulnValue");
        commonMap.put("publicipaddress", "publicipaddress");
        commonMap.put("Stopped", "Stopped");
        commonMap.put("statetransitionreason",
                "User initiated (2017-10-20 11:36:20 GMT)");
        commonMap.put("targetstoppedDuration", "30");
        commonMap.put("privateipaddress", "privateipaddress");
        commonMap.put("port", "22");
        commonMap.put("ssmWithInstanceUrl", "ssmWithInstanceUrl");
        commonMap.put("mandatoryTags", "mandatoryTags");
        commonMap.put("targetType", "targetType");
        commonMap.put("internetGateWay", "internetGateWay");
        commonMap.put("ec2SgEsURL", "ec2SgEsURL");
        commonMap.put("routetableAssociationsEsURL",
                "routetableAssociationsEsURL");
        commonMap.put("routetableRoutesEsURL", "routetableRoutesEsURL");
        commonMap.put("routetableEsURL", "routetableEsURL");
        commonMap.put("target", "30");
        commonMap.put("sgRulesUrl", "sgRulesUrl");
        commonMap.put("cidrIp", "cidrIp");
        commonMap.put("subnetid", "subnetid");
        commonMap.put("vpcid", "vpcid");
        commonMap.put("accountname", "accountname");
        commonMap.put("client", "client");
        commonMap.put("platform", "platform");
        commonMap.put("ruleName", "ruleName");
        commonMap.put("functionname", "functionname");
        commonMap.put("timePeriodInHours", "30");
        commonMap.put("threshold", "30");
        commonMap.put("rolename", "rolename");
        commonMap.put("adminRolesToCompare", "adminRolesToCompare");
        commonMap.put("kernelversionForComparision.x86_64",
                "kernelversionForComparision.x86_64");
        commonMap.put("reponse", "success");
        commonMap.put("lucene_version", "success");
        commonMap.put("final_u_last_patched", "2018-08-01 00:00:00.000000");
        commonMap.put("final_kernel_release", "123");
        commonMap.put("firstdiscoveredon", "2018-08-03 10:00:00+00");
        commonMap.put("discoveredDaysRange", "7");
        commonMap.put("vpc", "vpc");
        commonMap.put("securitygroups", "securitygroups");
        return commonMap;
    }

    public static Map<String, String> getAnotherMapString(
            String passRuleResourceId) {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("vpcid", "12");
        commonMap.put("_resourceid", passRuleResourceId);
        commonMap.put("severity", "low");
        commonMap.put("ruleCategory", "security");
        commonMap.put("accountid", "12345");
        commonMap.put("serviceEsURL", "url");
        commonMap.put("serviceAccountEsURL", "serviceAccountEsURL");
        commonMap.put("elasticIpEsUrl", "elasticIpEsUrl");
        commonMap.put("region", "us-east-1");
        commonMap.put("splitterChar", ",");
        commonMap.put("roleIdentifyingString", "roleIdentifyingString");
        commonMap.put("statename", "stopped");
        commonMap.put("ebsWithInstanceUrl", "ebsWithInstanceUrl");
        commonMap.put("passwordlastused", "2018-07-16 12:16:38+00");
        commonMap.put("pwdInactiveDuration", "1");
        commonMap.put("deprecatedInstanceType", "deprecatedInstanceType");
        commonMap.put("instancetype", "xyz");
        commonMap.put("running", "running");
        commonMap.put("instanceid", "instanceid");
        commonMap.put("statetransitionreason",
                "User initiated (2017-10-20 11:36:20 GMT)");
        commonMap.put("targetstoppedDuration", "30");
        commonMap.put("functionname", "functionname");
        commonMap.put("timePeriodInHours", "timePeriodInHours");
        commonMap.put("threshold", "threshold");
        commonMap.put("apiKeyName", "apiKeyName");
        commonMap.put("apiKeyValue", "apiKeyValue");
        commonMap.put("apiGWURL", "apiGWURL");
        commonMap.put("checkEsUrl", "checkEsUrl");
        commonMap.put("inScope", "true");
        commonMap.put("adminRolesToCompare", "adminRolesToCompare");
        commonMap.put("rolename", "abc");
        commonMap.put("endpoints", "{vpc=abc}");
        return commonMap;
    }
    
    public static Map<String, String> getWithOutEndPointMoreMapString(String passRuleResourceId) {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("scheme", "internet-facing");
        commonMap.put("subnets", "subnets");
        commonMap.put("esElbWithSGUrl", "esElbWithSGUrl");
        commonMap.put("esEc2SgURL", "esEc2SgURL");
        commonMap.put("esRoutetableAssociationsURL", "esRoutetableAssociationsURL");
        commonMap.put("esRoutetableRoutesURL", "esRoutetableRoutesURL");
        commonMap.put("esRoutetableURL", "esRoutetableURL");
        commonMap.put("esSgRulesUrl", "esSgRulesUrl");
        commonMap.put("esSubnetURL", "esSubnetURL");
        
        commonMap.put("subnetEsURL", "subnetEsURL");
        commonMap.put("esSubnetURL", "esSubnetURL");
        commonMap.put("awsSearch", "awsSearch");
        commonMap.put("kernelInfoApi", "kernelInfoApi");
        commonMap.put("esNonAdminAccntsWithIAMFullAccessUrl", "esNonAdminAccntsWithIAMFullAccessUrl");
        commonMap.put("esLdapUrl", "esLdapUrl");
        commonMap.put("esQualysUrl", "esQualysUrl");
        commonMap.put("esSatAndSpacewalkUrl", "esSatAndSpacewalkUrl");
        commonMap.put("esServiceURL", "esServiceURL");
        commonMap.put("esAdGroupURL", "esAdGroupURL");
        commonMap.put("esEbsWithInstanceUrl", "esEbsWithInstanceUrl");
        commonMap.put("esAppTagURL", "esAppTagURL");
        commonMap.put("esEc2SgURL", "esEc2SgURL");
        commonMap.put("esEc2WithVulnInfoForS5Url", "esEc2WithVulnInfoForS5Url");
        commonMap.put("esEc2PubAccessPortUrl", "esEc2PubAccessPortUrl");
        commonMap.put("esSsmWithInstanceUrl", "esSsmWithInstanceUrl");
        commonMap.put("esElasticIpUrl", "esElasticIpUrl");
        commonMap.put("esAppElbWithInstanceUrl", "esAppElbWithInstanceUrl");
        commonMap.put("esClassicElbWithInstanceUrl", "esClassicElbWithInstanceUrl");
        commonMap.put("esGuardDutyUrl", "esGuardDutyUrl");
        commonMap.put("esNonAdminAccntsWithIAMFullAccessUrl", "esNonAdminAccntsWithIAMFullAccessUrl");
        commonMap.put("esSgRulesUrl", "esSgRulesUrl");
        commonMap.put("esServiceWithSgUrl", "esServiceWithSgUrl");
        commonMap.put("ES_URI", "ES_URI");
        commonMap.put("executionId", "1234");
        commonMap.put("_resourceid", passRuleResourceId);
        commonMap.put("severity", "low");
        commonMap.put("ruleCategory", "security");
        commonMap.put("type", "Task");
        commonMap.put("accountid", "12345");
        commonMap.put("checkId", "1234567");
        commonMap.put("serviceEsURL", "url");
        commonMap.put("serviceAccountEsURL", "serviceAccountEsURL");
        commonMap.put("description", "R FND");
        commonMap.put("elasticIpEsUrl", "elasticIpEsUrl");
        commonMap.put("region", "us-east-1");
        commonMap.put("authType", "authType");
        commonMap.put("splitterChar", ",");
        commonMap.put("roleIdentifyingString", "roleIdentifyingString");
        commonMap.put("ldapApi", "ldapApi");
        commonMap.put("satAndSpacewalkApi", "satAndSpacewalkApi");
        commonMap.put("qualysApi", "qualysApi");
        commonMap.put("kernelVersionByInstanceIdUrl",
                "kernelVersionByInstanceIdUrl");
        commonMap.put("defaultKernelCriteriaUrl", "defaultKernelCriteriaUrl");
        commonMap.put("accountNames", "accountNames");
        commonMap.put("sourceType", "sourceType");
        commonMap.put("statename", "running");
        commonMap.put("ebsWithInstanceUrl", "ebsWithInstanceUrl");
        commonMap.put("volumeid", "volumeid");
        commonMap.put("loadbalancername", "loadbalancername");
        commonMap.put("targetExpireDuration", "150");
        commonMap.put("validto", "12/10/2018 23:33");
        commonMap.put("appElbWithInstanceUrl", "appElbWithInstanceUrl");
        commonMap.put("loadbalancerarn", "loadbalancerarn");
        commonMap.put("classicElbWithInstanceUrl", "classicElbWithInstanceUrl");
        commonMap.put("guardDutyEsUrl", "guardDutyEsUrl");
        commonMap.put("dbinstanceidentifier", "dbinstanceidentifier");
        commonMap.put("dbsnapshotarn", "dbsnapshotarn");
        commonMap.put("publiclyaccessible", "true");
        commonMap.put("apiGWURL", "apiGWURL");
        commonMap.put("portToCheck", "22");
        commonMap.put("sgRulesUrl", "sgRulesUrl");
        commonMap.put("cidrIp", "cidrIp");
        commonMap.put("serviceWithSgUrl", "serviceWithSgUrl");
        commonMap.put("esUrl", "esUrl");
        commonMap.put("groupid", "groupid");
        commonMap.put("adGroupEsURL", "adGroupEsURL");
        commonMap.put("target", "30");
        commonMap.put("inScope", "true");
        commonMap.put("role", "role");
        commonMap.put("passwordlastused", "2018-07-16 12:16:38+00");
        commonMap.put("pwdInactiveDuration", "1");
        commonMap.put("status_RED", "status_RED");
        commonMap.put("tags.Application", "tags.Application");
        commonMap.put("_entitytype", "ec2");
        commonMap.put("appTagEsURL", "appTagEsURL");
        commonMap.put("heimdallESURL", "heimdallESURL");
        commonMap.put("deprecatedInstanceType", "deprecatedInstanceType");
        commonMap.put("instancetype", "xyz");
        commonMap.put("running", "running");
        commonMap.put("instanceid", "instanceid");
        commonMap.put("ec2PubAccessPortUrl", "ec2PubAccessPortUrl");
        commonMap.put("ec2WithVulnInfoForS5Url", "ec2WithVulnInfoForS5Url");
        commonMap.put("ec2PortRuleId", "ec2PortRuleId");
        commonMap.put("severityVulnValue", "severityVulnValue");
        commonMap.put("publicipaddress", "publicipaddress");
        commonMap.put("Stopped", "Stopped");
        commonMap.put("statetransitionreason",
                "User initiated (2017-10-20 11:36:20 GMT)");
        commonMap.put("targetstoppedDuration", "30");
        commonMap.put("privateipaddress", "privateipaddress");
        commonMap.put("port", "22");
        commonMap.put("ssmWithInstanceUrl", "ssmWithInstanceUrl");
        commonMap.put("mandatoryTags", "mandatoryTags");
        commonMap.put("targetType", "targetType");
        commonMap.put("internetGateWay", "internetGateWay");
        commonMap.put("ec2SgEsURL", "ec2SgEsURL");
        commonMap.put("routetableAssociationsEsURL",
                "routetableAssociationsEsURL");
        commonMap.put("routetableRoutesEsURL", "routetableRoutesEsURL");
        commonMap.put("routetableEsURL", "routetableEsURL");
        commonMap.put("target", "30");
        commonMap.put("sgRulesUrl", "sgRulesUrl");
        commonMap.put("cidrIp", "cidrIp");
        commonMap.put("subnetid", "subnetid");
        commonMap.put("vpcid", "vpcid");
        commonMap.put("accountname", "accountname");
        commonMap.put("client", "client");
        commonMap.put("platform", "platform");
        commonMap.put("ruleName", "ruleName");
        commonMap.put("functionname", "functionname");
        commonMap.put("timePeriodInHours", "30");
        commonMap.put("threshold", "30");
        commonMap.put("rolename", "rolename");
        commonMap.put("adminRolesToCompare", "adminRolesToCompare");
        commonMap.put("kernelversionForComparision.x86_64",
                "kernelversionForComparision.x86_64");
        commonMap.put("reponse", "success");
        commonMap.put("lucene_version", "success");
        commonMap.put("final_u_last_patched", "2018-08-01 00:00:00.000000");
        commonMap.put("final_kernel_release", "123");
        commonMap.put("firstdiscoveredon", "2018-08-03 10:00:00+00");
        commonMap.put("discoveredDaysRange", "7");
        commonMap.put("vpc", "vpc");
        commonMap.put("securitygroups", "securitygroups");
        return commonMap;
    }
    
    public static Map<String, String> getFinalKernelReleaseAnotherMapString(
            String passRuleResourceId) {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("_resourceid", passRuleResourceId);
        commonMap.put("severity", "low");
        commonMap.put("ruleCategory", "security");
        commonMap.put("accountid", "12345");
        commonMap.put("serviceEsURL", "url");
        commonMap.put("serviceAccountEsURL", "serviceAccountEsURL");
        commonMap.put("elasticIpEsUrl", "elasticIpEsUrl");
        commonMap.put("region", "us-east-1");
        commonMap.put("splitterChar", ",");
        commonMap.put("roleIdentifyingString", "roleIdentifyingString");
        commonMap.put("statename", "running");
        commonMap.put("ebsWithInstanceUrl", "ebsWithInstanceUrl");
        commonMap.put("passwordlastused", "2018-07-16 12:16:38+00");
        commonMap.put("pwdInactiveDuration", "1");
        commonMap.put("deprecatedInstanceType", "deprecatedInstanceType");
        commonMap.put("instancetype", "xyz");
        commonMap.put("running", "running");
        commonMap.put("instanceid", "instanceid");
        commonMap.put("statetransitionreason",
                "User initiated (2017-10-20 11:36:20 GMT)");
        commonMap.put("targetstoppedDuration", "30");
        commonMap.put("functionname", "functionname");
        commonMap.put("timePeriodInHours", "timePeriodInHours");
        commonMap.put("threshold", "threshold");
        commonMap.put("apiKeyName", "apiKeyName");
        commonMap.put("apiKeyValue", "apiKeyValue");
        commonMap.put("apiGWURL", "apiGWURL");
        commonMap.put("checkEsUrl", "checkEsUrl");
        commonMap.put("inScope", "true");
        commonMap.put("final_kernel_release", "23323");
        commonMap.put("defaultKernelCriteriaUrl", "defaultKernelCriteriaUrl");
        commonMap.put("firstdiscoveredon", "2018-07-03 10:00:00+00");
        return commonMap;
    }

    public static Map<String, String> getEmptyMapString() {
        return new HashMap<>();
    }

    public static JsonArray getJsonArray() {
        Gson gson = new Gson();
        JsonArray array = new JsonArray();
        array.add(gson.fromJson("r_win_abc_admin", JsonElement.class));
        array.add(gson.fromJson("r_rhel_abc_admin", JsonElement.class));
        return array;
    }

    public static JsonObject getJsonObject() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("abc",
                gson.fromJson("r_win_abc__admin", JsonElement.class));
        jsonObject.add("hits", getHitsJson());
        
        JsonArray jsonArray = new JsonArray();
        JsonObject propertySource = new JsonObject();
        propertySource.add(PacmanSdkConstants.NAME, gson.fromJson("application", JsonElement.class));
        JsonObject source = new JsonObject();
        source.add("tag", gson.fromJson("tag", JsonElement.class));
        propertySource.add(PacmanSdkConstants.SOURCE, source);
        jsonArray.add(propertySource);
        jsonObject.add("propertySources", jsonArray);
        return jsonObject;
    }

    public static JsonObject getHitsJson() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("total", gson.fromJson("1", JsonElement.class));
        jsonObject.add("max_score",
                gson.fromJson("12.365102", JsonElement.class));
        jsonObject.add("hits", getHitsArrayJson());
        return jsonObject;
    }

    public static JsonObject getSourceJson() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("vpcsecuritygroupid", gson.fromJson("vpcsecuritygroupid", JsonElement.class));
        jsonObject.add("kernel", gson.fromJson("kernel", JsonElement.class));
        jsonObject.add("subnetid", gson.fromJson("subnetid", JsonElement.class));
        jsonObject.add("region", gson.fromJson("region", JsonElement.class));
        jsonObject.add("accountid",
                gson.fromJson("accountid", JsonElement.class));
        jsonObject.add("instanceid",
                gson.fromJson("instanceid", JsonElement.class));
        jsonObject.add("securitygroupid",
                gson.fromJson("securitygroupid", JsonElement.class));
        jsonObject.add("routetableid",
                gson.fromJson("routetableid", JsonElement.class));
        jsonObject.add("gatewayid",
                   gson.fromJson("igw_gatewayid", JsonElement.class));
        jsonObject.add("_resourceid",
                gson.fromJson("_resourceid", JsonElement.class));
        jsonObject.add("title",
                gson.fromJson("title", JsonElement.class));
        jsonObject.add("fromport",
                gson.fromJson("80", JsonElement.class));
        jsonObject.add("toport",
                gson.fromJson("All", JsonElement.class));
        jsonObject.add("ipprotocol",
                gson.fromJson("ipprotocol", JsonElement.class));
        jsonObject.add("total",
                gson.fromJson("total", JsonElement.class));
        jsonObject.add("resourceinfo",gson.fromJson("{\"IP Address\":\"us\",\"Region\":\"us\",\"Load Balancer Name\":\"rbl\",\"Reason\":\"Low request count\",\"Estimated Monthly Savings\":\"$18.00\",\"Instance ID\":\"i-09\",\"Instance Name\":\"alerts\",\"Instance Type\":\"c.xlarge\",\"Day 1\":\"0.1%  0.07MB\",\"Day 2\":\"0.1%  0.07MB\",\"Day 3\":\"0.1%  0.08MB\",\"Day 4\":\"0.1%  0.07MB\",\"Day 5\":\"0.1%  0.07MB\",\"Day 6\":\"0.1%  0.07MB\",\"Day 7\":\"0.1%  0.07MB\",\"Day 8\":\"0.1%  0.07MB\",\"Day 9\":\"0.1%  0.07MB\",\"Day 10\":\"0.1%  0.09MB\",\"Day 11\":\"0.1%  0.07MB\",\"Day 12\":\"0.1%  0.07MB\",\"Day 13\":\"0.1%  0.06MB\",\"Day 14\":\"0.1%  0.04MB\",\"14-Day Average CPU Utilization\":\"0.1%\",\"14-Day Average Network IO\":\"0.07MB\",\"Number of Days Low Utilization\":\"14 days\",\"Status\":\"Yellow\",\"Cluster\":\"redShift\",\"DB Instance Name\":\"prd\",\"Multi-AZ\":\"No\",\"Storage Provisioned (GB)\":\"1\",\"Days Since Last Connection\":\"14+\",\"Estimated Monthly Savings (On Demand)\":\"$209\",\"Volume ID\":\"prd\",\"Volume Name\":\"dev\",\"Volume Type\":\"General\",\"Volume Size\":\"1000\",\"Monthly Storage Cost\":\"$100.00\",\"Snapshot Name\":\"snap\",\"Snapshot Age\":\"23\",\"Snapshot ID\":\"snap\",\"Description\":\"Public Access Test Volume\",\"Zone\":\"null\",\"Platform\":\"Linux/UNIX\",\"Instance Count\":\"3\",\"Current Monthly Cost\":\"$258.14\",\"Expiration Date\":\"2018-07-19T23:59:59.000Z\",\"Reserved Instance ID\":\"24300dd4\",\"DB Instance or Cluster ID\":\"DB Instance or Cluster ID\"}", JsonElement.class));
        jsonObject.add("managedBy",
                gson.fromJson("managedBy", JsonElement.class));
        jsonObject.add("memberOf",getJsonArray());
        jsonObject.add("lastVulnScan",
                gson.fromJson("", JsonElement.class));
        jsonObject.add("Cluster",
                gson.fromJson("Cluster", JsonElement.class));
        jsonObject.add("Status",
                gson.fromJson("Yellow", JsonElement.class));
        jsonObject.add("detail",getAllSourceJson());
        
        return jsonObject;
    }

    public static JsonArray getHitsArrayJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("_source", getSourceJson());

        JsonArray array = new JsonArray();
        array.add(jsonObject);
        return array;
    }

    
    public static JsonArray getAllHitsArrayJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("_source", getAllSourceJson());

        JsonArray array = new JsonArray();
        array.add(jsonObject);
        return array;
    }
    public static JsonObject getEmptyJsonObject() {
        return new JsonObject();
    }
    
    public static JsonObject getAllJsonObject() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("abc",
                gson.fromJson("r_win_abc__admin", JsonElement.class));
        jsonObject.add("hits", getAllHitsJson());
        return jsonObject;
    }

    public static JsonObject getAllHitsJson() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("total", gson.fromJson("1", JsonElement.class));
        jsonObject.add("max_score",
                gson.fromJson("12.365102", JsonElement.class));
        jsonObject.add("hits", getAllHitsArrayJson());
        jsonObject.add("type",
                gson.fromJson("type", JsonElement.class));
        return jsonObject;
    }

    public static JsonObject getAllSourceJson() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("region", gson.fromJson("region", JsonElement.class));
        jsonObject.add("accountid",
                gson.fromJson("accountid", JsonElement.class));
        jsonObject.add("instanceid",
                gson.fromJson("instanceid", JsonElement.class));
        jsonObject.add("securitygroupid",
                gson.fromJson("securitygroupid", JsonElement.class));
        jsonObject.add("routetableid",
                gson.fromJson("routetableid", JsonElement.class));
        jsonObject.add("gatewayid",
                   gson.fromJson("igw_gatewayid", JsonElement.class));
        jsonObject.add("_resourceid",
                gson.fromJson("_resourceid", JsonElement.class));
        jsonObject.add("title",
                gson.fromJson("title", JsonElement.class));
        jsonObject.add("fromport",
                gson.fromJson("All", JsonElement.class));
        jsonObject.add("toport",
                gson.fromJson("80", JsonElement.class));
        jsonObject.add("ipprotocol",
                gson.fromJson("ipprotocol", JsonElement.class));
        jsonObject.add("memberOf",new JsonArray());
        jsonObject.add("userIdentity", getOneMoreJsonObject());
        return jsonObject;
    }
    
    public static JsonObject getOneMoreJsonObject() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("type",
                gson.fromJson("Root", JsonElement.class));
        jsonObject.add("userName",
                gson.fromJson("userName", JsonElement.class));
        return jsonObject;
    }
    
    public static JsonObject getWrongPortsEmptyJsonObject() {
        return new JsonObject();
    }

    public static List<String> getListString() {
        List<String> commonList = new ArrayList<>();
        commonList.add("abc");
        commonList.add("AuthorizationType1");
       commonList.add("ldap");
       commonList.add("ssh");
        commonList.add("spaceandsat");
        commonList.add("qualys");
        commonList.add("webservice");
        commonList.add("kernelversionForComparision");
        return commonList;
    }

    public static List<String> getEmptyListString() {
       return new ArrayList<>();
    }
    public static List<String> getOneMoreListString() {
        List<String> commonList = new ArrayList<>();
        commonList.add("xyz");
        return commonList;
    }

    public static Map<String, Boolean> getMapBoolean(String passRuleResourceId) {
        Map<String, Boolean> commonMap = new HashMap<>();
        commonMap.put("sgopen", true);
        commonMap.put("Read", true);
        commonMap.put("acl_found", true);
        commonMap.put("bucketPolicy_found", true);
        commonMap.put("Write", true);
        return commonMap;
    }

    public static Map<String, Boolean> getOneMoreMapBoolean(
            String passRuleResourceId) {
        Map<String, Boolean> commonMap = new HashMap<>();
        commonMap.put("sgopen", true);
        commonMap.put("Read", true);
        commonMap.put("acl_found", true);
        commonMap.put("bucketPolicy_found", true);
        commonMap.put("Write", true);
        return commonMap;
    }

    public static LinkedHashMap<String, Boolean> getLinkedHashMapBoolean(
            String passRuleResourceId) {
        LinkedHashMap<String, Boolean> commonMap = new LinkedHashMap<>();
        commonMap.put("sgopen", true);
        return commonMap;
    }

    public static Map<String, Boolean> getEmptyMapBoolean(
            String passRuleResourceId) {
        Map<String, Boolean> commonMap = new HashMap<>();
        return commonMap;
    }

    public static Map<String, Object> getMapObject(String passRuleResourceId) {
        Map<String, Object> commonMap = new HashMap<>();
        commonMap.put("region", "region");
        commonMap.put("_resourceid", passRuleResourceId);
        commonMap.put("issueCount", "issueCount");
        commonMap.put("OU", "OU");
        commonMap.put("created_event_found", true);
        return commonMap;
    }
    
    public static HashMultimap<String, Object> getMulHashMapObject(String passRuleResourceId) {
    	HashMultimap<String, Object> commonMap = HashMultimap.create();
        commonMap.put("region", "region");
        commonMap.put("_resourceid", passRuleResourceId);
        commonMap.put("issueCount", "issueCount");
        commonMap.put("OU", "OU");
        commonMap.put("created_event_found", true);
        return commonMap;
    }

    public static Map<String, Object> getEmptyMapObject() {
        return new HashMap<>();
    }

    public static List<Map<Object, Map<String, Object>>> getListMapOfMap(
            String resourceId) {
        List<Map<Object, Map<String, Object>>> list = new ArrayList<>();
        list.add(getMapOfMap(resourceId));
        return list;
    }

    public static List<Map<Object, Map<String, Object>>> getEmptyListMapOfMap(
            String resourceId) {
        List<Map<Object, Map<String, Object>>> list = new ArrayList<>();
        return list;
    }

    public static Map<Object, Map<String, Object>> getMapOfMap(String resourceId) {
        Map<Object, Map<String, Object>> mapOfMap = new HashMap<>();
        mapOfMap.put("regionAndCount", getMapObject("resourceId"));
        return mapOfMap;
    }

    public static Map<String, String> getVolumeMapString(
            String passRuleResourceId) {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("description", "RP FND");
        commonMap.put("passwordlastused", "passwordlastused");
        commonMap.put("statename", "Stopped");
        commonMap.put("targetType", "ec2");
        commonMap.put("_entitytype", "volume");
        return commonMap;
    }
    
    public static Map<String, String> getSMapString(
            String passRuleResourceId) {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("description", "RP FND");
        commonMap.put("passwordlastused", "passwordlastused");
        commonMap.put("statename", "Stopped");
        commonMap.put("targetType", "ec2");
        commonMap.put("_entitytype", "snapshot");
        return commonMap;
    }
    
    public static Map<String, String> getOneMoreMapString(
            String passRuleResourceId) {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("description", "RP FND");
        commonMap.put("passwordlastused", "passwordlastused");
        commonMap.put("statename", "Stopped");
        commonMap.put("targetType", "ec2");
        commonMap.put("_entitytype", "ec2");
        return commonMap;
    }
    
    public static Map<String, String> getSnapshotMapString(
            String passRuleResourceId) {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("description", "RP FND");
        commonMap.put("passwordlastused", "passwordlastused");
        commonMap.put("statename", "Stopped");
        commonMap.put("tags.Application", "identifiableKey");
        commonMap.put("volumeid", "");
        commonMap.put("firstdiscoveredon", "2018-08-03 10:00:00+00");
        commonMap.put("discoveredDaysRange", "7");
        commonMap.put("targetType", "ec2");
        commonMap.put("_entitytype", "snapshot");
        commonMap.put("_resourceid", "identifiableKey");
        return commonMap;
    }
    
    public static Map<String, String> getSnapMapString(
            String passRuleResourceId) {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("description", "RP FND");
        commonMap.put("passwordlastused", "passwordlastused");
        commonMap.put("statename", "Stopped");
        commonMap.put("volumeid", "");
        commonMap.put("firstdiscoveredon", "2018-08-03 10:00:00+00");
        commonMap.put("discoveredDaysRange", "7");
        commonMap.put("targetType", "ec2");
        commonMap.put("_entitytype", "snapshot");
        commonMap.put("_resourceid", "identifiableKey");
        return commonMap;
    }

    public static Map<String, String> getCommonMapString(
            String passRuleResourceId) {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("description", "RP FND");
        commonMap.put("passwordlastused", "passwordlastused");
        return commonMap;
    }

    public static Set<String> getSetString(String passRuleResourceId) {
        Set<String> commonSet = new HashSet<>();
        commonSet.add("description");
        commonSet.add("passwordlastused");
        commonSet.add("kernelversionForComparision");
        return commonSet;
    }
    
    public static Set<String> getEmptySetString() {
        Set<String> commonSet = new HashSet<>();
        return commonSet;
    }

    public static List<GroupIdentifier> getListSecurityGroupId() {
        List<GroupIdentifier> groupIdentifiers = new ArrayList<>();
        groupIdentifiers.add(getGroupIdentifier("123"));
        return groupIdentifiers;
    }

    public static Annotation getAnnotation(String passRuleResourceId) {
        Annotation annotation = Annotation.buildAnnotation(
                CommonTestUtils.getMapString(passRuleResourceId),
                Annotation.Type.ISSUE);
        annotation.put("description", "description");
        annotation.put("passwordlastused", "passwordlastused");
        return annotation;
    }

    public static JSONObject getJonObject(String str) {
        Map<String, Object> map = new HashMap<>();
        map.put("Code", "NoSuchKey");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Error", map);

        return jsonObject;
    }

    public static JSONObject getOneMoreJonObject(String str) {
        Map<String, Object> map = new HashMap<>();
        map.put("Code", "NoSuchKey1");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Error", map);
        return jsonObject;
    }
    
    public static Set<GroupIdentifier> getSetGroupIdentifier(String passRuleResourceId) {
        Set<GroupIdentifier> commonSet = new HashSet<>();
        commonSet.add(getGroupIdentifier(passRuleResourceId));
        return commonSet;
    }
    
    public static Set<GroupIdentifier> getEmptySetGroupIdentifier(String passRuleResourceId) {
        Set<GroupIdentifier> commonSet = new HashSet<>();
        return commonSet;
    }
    
    public static GroupIdentifier getGroupIdentifier(String groupId){
        GroupIdentifier groupIdentifier = new GroupIdentifier();
        groupIdentifier.setGroupId(groupId);
        return groupIdentifier;
    }
    
    
    public static Map<String, String> getLastPatchedMapString(String passRuleResourceId) {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("executionId", "1234");
        commonMap.put("_resourceid", passRuleResourceId);
        commonMap.put("severity", "low");
        commonMap.put("ruleCategory", "security");
        commonMap.put("type", "Task");
        commonMap.put("accountid", "12345");
        commonMap.put("checkId", "1234567");
        commonMap.put("serviceEsURL", "url");
        commonMap.put("serviceAccountEsURL", "serviceAccountEsURL");
        commonMap.put("description", "R FND");
        commonMap.put("elasticIpEsUrl", "elasticIpEsUrl");
        commonMap.put("region", "us-east-1");
        commonMap.put("authType", "authType");
        commonMap.put("splitterChar", ",");
        commonMap.put("roleIdentifyingString", "roleIdentifyingString");
        commonMap.put("ldapApi", "ldapApi");
        commonMap.put("satAndSpacewalkApi", "satAndSpacewalkApi");
        commonMap.put("qualysApi", "qualysApi");
        commonMap.put("kernelVersionByInstanceIdUrl",
                "kernelVersionByInstanceIdUrl");
        commonMap.put("defaultKernelCriteriaUrl", "defaultKernelCriteriaUrl");
        commonMap.put("accountNames", "accountNames");
        commonMap.put("sourceType", "sourceType");
        commonMap.put("statename", "running");
        commonMap.put("ebsWithInstanceUrl", "ebsWithInstanceUrl");
        commonMap.put("volumeid", "volumeid");
        commonMap.put("loadbalancername", "loadbalancername");
        commonMap.put("targetExpireDuration", "150");
        commonMap.put("validto", "12/10/2018 23:33");
        commonMap.put("appElbWithInstanceUrl", "appElbWithInstanceUrl");
        commonMap.put("loadbalancerarn", "loadbalancerarn");
        commonMap.put("classicElbWithInstanceUrl", "classicElbWithInstanceUrl");
        commonMap.put("guardDutyEsUrl", "guardDutyEsUrl");
        commonMap.put("dbinstanceidentifier", "dbinstanceidentifier");
        commonMap.put("dbsnapshotarn", "dbsnapshotarn");
        commonMap.put("publiclyaccessible", "true");
        commonMap.put("apiGWURL", "apiGWURL");
        commonMap.put("portToCheck", "22");
        commonMap.put("sgRulesUrl", "sgRulesUrl");
        commonMap.put("cidrIp", "cidrIp");
        commonMap.put("serviceWithSgUrl", "serviceWithSgUrl");
        commonMap.put("esUrl", "esUrl");
        commonMap.put("groupid", "groupid");
        commonMap.put("adGroupEsURL", "adGroupEsURL");
        commonMap.put("target", "30");
        commonMap.put("inScope", "true");
        commonMap.put("role", "role");
        commonMap.put("passwordlastused", "2018-07-16 12:16:38+00");
        commonMap.put("pwdInactiveDuration", "1");
        commonMap.put("status_RED", "status_RED");
        commonMap.put("tags.Application", "tags.Application");
        commonMap.put("_entitytype", "ec2");
        commonMap.put("appTagEsURL", "appTagEsURL");
        commonMap.put("heimdallESURL", "heimdallESURL");
        commonMap.put("deprecatedInstanceType", "deprecatedInstanceType");
        commonMap.put("instancetype", "xyz");
        commonMap.put("running", "running");
        commonMap.put("instanceid", "instanceid");
        commonMap.put("ec2PubAccessPortUrl", "ec2PubAccessPortUrl");
        commonMap.put("ec2WithVulnInfoForS5Url", "ec2WithVulnInfoForS5Url");
        commonMap.put("ec2PortRuleId", "ec2PortRuleId");
        commonMap.put("severityVulnValue", "severityVulnValue");
        commonMap.put("publicipaddress", "publicipaddress");
        commonMap.put("Stopped", "Stopped");
        commonMap.put("statetransitionreason",
                "User initiated (2017-10-20 11:36:20 GMT)");
        commonMap.put("targetstoppedDuration", "30");
        commonMap.put("privateipaddress", "privateipaddress");
        commonMap.put("port", "22");
        commonMap.put("ssmWithInstanceUrl", "ssmWithInstanceUrl");
        commonMap.put("mandatoryTags", "mandatoryTags");
        commonMap.put("targetType", "targetType");
        commonMap.put("internetGateWay", "internetGateWay");
        commonMap.put("ec2SgEsURL", "ec2SgEsURL");
        commonMap.put("routetableAssociationsEsURL",
                "routetableAssociationsEsURL");
        commonMap.put("routetableRoutesEsURL", "routetableRoutesEsURL");
        commonMap.put("routetableEsURL", "routetableEsURL");
        commonMap.put("target", "30");
        commonMap.put("sgRulesUrl", "sgRulesUrl");
        commonMap.put("cidrIp", "cidrIp");
        commonMap.put("subnetid", "subnetid");
        commonMap.put("vpcid", "vpcid");
        commonMap.put("accountname", "accountname");
        commonMap.put("client", "client");
        commonMap.put("platform", "platform");
        commonMap.put("ruleName", "ruleName");
        commonMap.put("functionname", "functionname");
        commonMap.put("timePeriodInHours", "30");
        commonMap.put("threshold", "30");
        commonMap.put("rolename", "rolename");
        commonMap.put("adminRolesToCompare", "adminRolesToCompare");
        commonMap.put("kernelversionForComparision.x86_64",
                "kernelversionForComparision.x86_64");
        commonMap.put("reponse", "success");
        commonMap.put("lucene_version", "success");
        commonMap.put("final_u_last_patched", "2017-08-01 00:00:00.000000");
        commonMap.put("final_kernel_release", "123");
        
        return commonMap;
    }
    
    public static JsonArray getOneJsonArray() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("Effect", gson.fromJson("Deny", JsonElement.class));
        jsonObject.add("Principal", gson.fromJson("12", JsonElement.class));
        JsonArray array = new JsonArray();
        array.add(jsonObject);
        JsonObject stmnt = new JsonObject();
        stmnt.add("Statement", array);
        array.add(gson.fromJson("r_win_abc_admin", JsonElement.class));
        array.add(gson.fromJson("r_rhel_abc_admin", JsonElement.class));
        array.add(stmnt);
        return array;
    }
    
    public static JsonArray getForDenyJsonArray() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("Effect", gson.fromJson("Deny", JsonElement.class));
        jsonObject.add("Principal", gson.fromJson("*", JsonElement.class));
        JsonArray array = new JsonArray();
        array.add(jsonObject);
        return array;
    }
    
    public static JsonArray getAllowJsonArray() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("Effect", gson.fromJson("Allow", JsonElement.class));
        jsonObject.add("Principal", gson.fromJson("*", JsonElement.class));
        JsonArray array = new JsonArray();
        array.add(jsonObject);
        JsonObject stmnt = new JsonObject();
        stmnt.add("Statement", array);
        return array;
    }
    
    public static JsonArray getAnotherJsonArray() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("Effect", gson.fromJson("Allow", JsonElement.class));
        JsonArray array = new JsonArray();
        array.add(jsonObject);
        JsonObject stmnt = new JsonObject();
        stmnt.add("Statement", array);
        array.add(gson.fromJson("r_win_abc_admin", JsonElement.class));
        array.add(gson.fromJson("r_rhel_abc_admin", JsonElement.class));
        array.add(stmnt);
        return array;
    }
    
    public static Map<String, List<String>> getMapStringList(
            String passRuleResourceId) {
        Map<String, List<String>> commonMap = new HashMap<>();
        List<String> list = new ArrayList<String>();
        list.add(passRuleResourceId);
        commonMap.put("description", list);
        return commonMap;
    }
    
    public static Map<String, String> getMapStringNLB(String passRuleResourceId) {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("iamPriviliges","iamPriviliges");
        commonMap.put(",lambda:*,*",",lambda:*,*");
        commonMap.put(",ec2:*,*",",ec2:*,*");
        commonMap.put("lambda","lambda");
        commonMap.put(",ec2:*,*,s3:*,s3:put*",",ec2:*,*,s3:*,s3:put*");
        commonMap.put("type","network");
        commonMap.put("destinationBucketForAutofix","destinationBucketForAutofix");

        commonMap.put("cidripv6", "cidripv6");
        commonMap.put("username", "svc_123");
        commonMap.put("associationid", "associationid");
        commonMap.put("domainname", "domainname");
        commonMap.put("accesspolicies", "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"*\"},\"Action\":\"es:*\",\"Resource\":\"123/*\"}]}");
        commonMap.put("scheme", "internet-facing");
        commonMap.put("subnets", "subnets");
        commonMap.put("esElbWithSGUrl", "esElbWithSGUrl");
        commonMap.put("esEc2SgURL", "esEc2SgURL");
        commonMap.put("endpoint", "endpoint");
        commonMap.put("esRoutetableAssociationsURL", "esRoutetableAssociationsURL");
        commonMap.put("esRoutetableRoutesURL", "esRoutetableRoutesURL");
        commonMap.put("esRoutetableURL", "esRoutetableURL");
        commonMap.put("esSgRulesUrl", "esSgRulesUrl");
        commonMap.put("esSubnetURL", "esSubnetURL");
        commonMap.put("identifiableKey", "identifiableKey");
        commonMap.put("subnetEsURL", "subnetEsURL");
        commonMap.put("esSubnetURL", "esSubnetURL");
        commonMap.put("awsSearch", "awsSearch");
        commonMap.put("kernelInfoApi", "kernelInfoApi");
        commonMap.put("esNonAdminAccntsWithIAMFullAccessUrl", "esNonAdminAccntsWithIAMFullAccessUrl");
        commonMap.put("esLdapUrl", "esLdapUrl");
        commonMap.put("esQualysUrl", "esQualysUrl");
        commonMap.put("esSatAndSpacewalkUrl", "esSatAndSpacewalkUrl");
        commonMap.put("esServiceURL", "esServiceURL");
        commonMap.put("esAdGroupURL", "esAdGroupURL");
        commonMap.put("esEbsWithInstanceUrl", "esEbsWithInstanceUrl");
        commonMap.put("esAppTagURL", "esAppTagURL");
        commonMap.put("esEc2SgURL", "esEc2SgURL");
        commonMap.put("esEc2WithVulnInfoForS5Url", "esEc2WithVulnInfoForS5Url");
        commonMap.put("esEc2PubAccessPortUrl", "esEc2PubAccessPortUrl");
        commonMap.put("esSsmWithInstanceUrl", "esSsmWithInstanceUrl");
        commonMap.put("esElasticIpUrl", "esElasticIpUrl");
        commonMap.put("esAppElbWithInstanceUrl", "esAppElbWithInstanceUrl");
        commonMap.put("esClassicElbWithInstanceUrl", "esClassicElbWithInstanceUrl");
        commonMap.put("esGuardDutyUrl", "esGuardDutyUrl");
        commonMap.put("esNonAdminAccntsWithIAMFullAccessUrl", "esNonAdminAccntsWithIAMFullAccessUrl");
        commonMap.put("esSgRulesUrl", "esSgRulesUrl");
        commonMap.put("esServiceWithSgUrl", "esServiceWithSgUrl");
        commonMap.put("ES_URI", "ES_URI");
        commonMap.put("executionId", "1234");
        commonMap.put("_resourceid", passRuleResourceId);
        commonMap.put("severity", "low");
        commonMap.put("ruleCategory", "security");
        commonMap.put("accountid", "12345");
        commonMap.put("checkId", "1234567");
        commonMap.put("serviceEsURL", "url");
        commonMap.put("serviceAccountEsURL", "serviceAccountEsURL");
        commonMap.put("description", "R FND");
        commonMap.put("elasticIpEsUrl", "elasticIpEsUrl");
        commonMap.put("region", "us-east-1");
        commonMap.put("authType", "authType");
        commonMap.put("splitterChar", ",");
        commonMap.put("roleIdentifyingString", "roleIdentifyingString");
        commonMap.put("ldapApi", "ldapApi");
        commonMap.put("satAndSpacewalkApi", "satAndSpacewalkApi");
        commonMap.put("qualysApi", "qualysApi");
        commonMap.put("kernelVersionByInstanceIdUrl",
                "kernelVersionByInstanceIdUrl");
        commonMap.put("defaultKernelCriteriaUrl", "defaultKernelCriteriaUrl");
        commonMap.put("accountNames", "accountNames");
        commonMap.put("sourceType", "sourceType");
        commonMap.put("statename", "running");
        commonMap.put("ebsWithInstanceUrl", "ebsWithInstanceUrl");
        commonMap.put("volumeid", "volumeid");
        commonMap.put("loadbalancername", "loadbalancername");
        commonMap.put("targetExpireDuration", "150");
        commonMap.put("validto", "12/10/2018 23:33");
        commonMap.put("appElbWithInstanceUrl", "appElbWithInstanceUrl");
        commonMap.put("loadbalancerarn", "loadbalancerarn");
        commonMap.put("classicElbWithInstanceUrl", "classicElbWithInstanceUrl");
        commonMap.put("guardDutyEsUrl", "guardDutyEsUrl");
        commonMap.put("dbinstanceidentifier", "dbinstanceidentifier");
        commonMap.put("dbsnapshotarn", "dbsnapshotarn");
        commonMap.put("publiclyaccessible", "true");
        commonMap.put("apiGWURL", "apiGWURL");
        commonMap.put("portToCheck", "22");
        commonMap.put("sgRulesUrl", "sgRulesUrl");
        commonMap.put("cidrIp", "cidrIp");
        commonMap.put("serviceWithSgUrl", "serviceWithSgUrl");
        commonMap.put("esUrl", "esUrl");
        commonMap.put("groupid", "groupid");
        commonMap.put("adGroupEsURL", "adGroupEsURL");
        commonMap.put("target", "30");
        commonMap.put("inScope", "true");
        commonMap.put("role", "role");
        commonMap.put("passwordlastused", "2018-07-16 12:16:38+00");
        commonMap.put("pwdInactiveDuration", "1");
        commonMap.put("status_RED", "status_RED");
        commonMap.put("tags.Application", "identifiableKey");
        commonMap.put("_entitytype", "elasticache");
        commonMap.put("appTagEsURL", "appTagEsURL");
        commonMap.put("heimdallESURL", "heimdallESURL");
        commonMap.put("deprecatedInstanceType", "deprecatedInstanceType");
        commonMap.put("instancetype", "xyz");
        commonMap.put("running", "running");
        commonMap.put("instanceid", "instanceid");
        commonMap.put("ec2PubAccessPortUrl", "ec2PubAccessPortUrl");
        commonMap.put("ec2WithVulnInfoForS5Url", "ec2WithVulnInfoForS5Url");
        commonMap.put("ec2PortRuleId", "ec2PortRuleId");
        commonMap.put("severityVulnValue", "severityVulnValue");
        commonMap.put("publicipaddress", "publicipaddress");
        commonMap.put("Stopped", "Stopped");
        commonMap.put("statetransitionreason",
                "User initiated (2017-10-20 11:36:20 GMT)");
        commonMap.put("targetstoppedDuration", "30");
        commonMap.put("privateipaddress", "privateipaddress");
        commonMap.put("port", "22");
        commonMap.put("ssmWithInstanceUrl", "ssmWithInstanceUrl");
        commonMap.put("mandatoryTags", "mandatoryTags");
        commonMap.put("targetType", "targetType");
        commonMap.put("internetGateWay", "internetGateWay");
        commonMap.put("ec2SgEsURL", "ec2SgEsURL");
        commonMap.put("routetableAssociationsEsURL",
                "routetableAssociationsEsURL");
        commonMap.put("routetableRoutesEsURL", "routetableRoutesEsURL");
        commonMap.put("routetableEsURL", "routetableEsURL");
        commonMap.put("target", "30");
        commonMap.put("sgRulesUrl", "sgRulesUrl");
        commonMap.put("cidrIp", "cidrIp");
        commonMap.put("subnetid", "subnetid");
        commonMap.put("vpcid", "vpcid");
        commonMap.put("accountname", "accountname");
        commonMap.put("client", "client");
        commonMap.put("platform", "platform");
        commonMap.put("ruleName", "ruleName");
        commonMap.put("functionname", "functionname");
        commonMap.put("timePeriodInHours", "30");
        commonMap.put("threshold", "30");
        commonMap.put("rolename", "rolename");
        commonMap.put("adminRolesToCompare", "adminRolesToCompare");
        commonMap.put("kernelversionForComparision.x86_64",
                "kernelversionForComparision.x86_64");
        commonMap.put("reponse", "success");
        commonMap.put("lucene_version", "success");
        commonMap.put("final_u_last_patched", "2018-08-01 00:00:00.000000");
        commonMap.put("final_kernel_release", "123");
        commonMap.put("firstdiscoveredon", "2018-08-03 10:00:00+00");
        commonMap.put("discoveredDaysRange", "7");
        commonMap.put("vpc", "vpc");
        commonMap.put("securitygroups", "securitygroups");
        return commonMap;
    }
}
