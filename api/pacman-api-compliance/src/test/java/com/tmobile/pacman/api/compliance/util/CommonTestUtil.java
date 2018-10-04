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
package com.tmobile.pacman.api.compliance.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tmobile.pacman.api.compliance.domain.AssetCountDTO;
import com.tmobile.pacman.api.compliance.domain.CompliantTrendRequest;
import com.tmobile.pacman.api.compliance.domain.IssueAuditLogRequest;
import com.tmobile.pacman.api.compliance.domain.IssueResponse;
import com.tmobile.pacman.api.compliance.domain.KernelVersion;
import com.tmobile.pacman.api.compliance.domain.PatchingProgressResponse;
import com.tmobile.pacman.api.compliance.domain.PatchingRequest;
import com.tmobile.pacman.api.compliance.domain.ProjectionResponse;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.ResponseWithCount;
import com.tmobile.pacman.api.compliance.domain.ResponseWithOrder;
import com.tmobile.pacman.api.compliance.domain.RuleDetails;
import com.tmobile.pacman.api.compliance.domain.RuleTrendRequest;

public class CommonTestUtil {
    public static ResponseWithCount getResponseWithCount() {
        List<Map<String, Object>> responseList = new ArrayList<>();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("passed", 1000);
        responseMap.put("failed", 500);
        responseMap.put("assetsScanned", 1500);
        responseMap.put("compliance_percent", 25);
        responseMap.put("ruleCategory", "tagging");
        responseList.add(responseMap);
        ResponseWithCount responseWithCount = new ResponseWithCount(
                responseList, 1000);
        return responseWithCount;
    }

    public static Map<String, Object> getMapObject() {
        Map<String,Object> distSeverirtMap = new HashMap<>();
        distSeverirtMap.put("abc", "xyz");
        Map<String, Object> commonMap = new HashMap<>();
        commonMap.put("vulnerabilities", 100);
        commonMap.put("hosts", 500);
        commonMap.put("totalVulnerableAssets", 100);
        commonMap.put("compliantpercent", 50);
        commonMap.put("status", 200);
        commonMap.put("serviceName", "serviceName");
        commonMap.put("serviceEndpoint", "serviceEndpoint");
        commonMap.put("noncompliant", 2114l);
        commonMap.put("total", 2123l);
        commonMap.put("compliant", 9l);
        commonMap.put("overall", 73l);
        commonMap.put("total_issues", 1500l);
        commonMap.put("ec2", 1500l);
        commonMap.put("_id", "_id");
        commonMap.put("severity", "Severity");
        commonMap.put("issueId", "issueId");
        commonMap
                .put("ruleId",
                        "PacMan_AmazonRDSIdleDBInstancesRule_version-1_AmazonRDSIdleDBInstancesRule_rdsdb");
        commonMap.put("_resourceid", "123");
        commonMap.put("targetType", "ec2");
        commonMap.put("issueStatus", "open");

        commonMap.put("severity", "low");
        commonMap.put("ruleCategory", "security");
        commonMap.put("issueReason", "dont know");
        commonMap.put("_source",new HashMap());
        commonMap.put("createdDate", "createdDate");
        commonMap.put("modifiedDate", "modifiedDate");
        commonMap.put("costOptimization", 100);
        commonMap.put("policyId",
                "PacMan_AmazonRDSIdleDBInstancesRule_version-1");
        commonMap
                .put("issueDetails",
                        "[{violationReason=Amazon RDS idle DB instance found, check_id=Ti39halfu8, sources_verified=trusted advisor}]");
        commonMap.put("date", "2018-06-26");
        commonMap.put("noncompliant", 2114.0);
        commonMap.put("ruleId", "PacMan_TaggingRule_version-1");
        commonMap.put("overall", 73);
        commonMap.put("distribution_by_severity", distSeverirtMap);
        commonMap.put("total_issues", 1500);
        commonMap.put("ruleCategory", "security");
        commonMap.put("displayName", "displayName");
        return commonMap;
    }

    public static Map<String, Long> getMapLong() {
        Map<String, Long> vulnInfoMap = new HashMap<>();
        vulnInfoMap.put("vulnerabilities", 100l);
        vulnInfoMap.put("hosts", 500l);
        vulnInfoMap.put("totalVulnerableAssets", 100l);
        vulnInfoMap.put("compliantpercent", 50l);
        vulnInfoMap.put("noncompliant", 2114l);
        vulnInfoMap.put("total", 2123l);
        vulnInfoMap.put("compliant", 9l);
        vulnInfoMap.put("overall", 73l);
        vulnInfoMap.put("total_issues", 1500l);
        vulnInfoMap.put("ec2", 5900l);
        vulnInfoMap.put("rdsdb", 500l);
        vulnInfoMap
                .put("PacMan_AmazonRDSIdleDBInstancesRule_version-1_AmazonRDSIdleDBInstancesRule_rdsdb",
                        1500l);
        return vulnInfoMap;
    }

    public static List<Map<String, Object>> getListMapObject() {
        List<Map<String, Object>> vulnInfoMapList = new ArrayList();
        vulnInfoMapList.add(getMapObject());
        return vulnInfoMapList;
    }

    public static List<Map<String, Long>> getListMapLong() {
        List<Map<String, Long>> vulnInfoMapList = new ArrayList();
        vulnInfoMapList.add(getMapLong());
        return vulnInfoMapList;
    }

    public static Map<String, String> getFilters() {
        Map<String, String> filter = new HashMap<>();
        filter.put("domain", "Infra & Platforms");
        filter.put("ruleCategory.keyword", "security");
        filter.put("ruleCategory", "security");
        filter.put("targetType.keyword", "ec2");
        return filter;
    }
    
    public static Map<String, String> getOnlyIssueIdFilters() {
        Map<String, String> filter = new HashMap<>();
        filter.put("domain", "domain");
        filter.put("issueId.keyword", "security");
        filter.put("ruleId.keyword", "PacMan_TaggingRule_version-1");
        filter.put("targetType.keyword", "ec2");
        return filter;
    }

    public static CompliantTrendRequest getCompliantTrendRequest() {
        CompliantTrendRequest compliantTrendRequest = new CompliantTrendRequest();
        compliantTrendRequest.setAg("aws-all");
        compliantTrendRequest.setFilters(getFilters());
        return compliantTrendRequest;
    }

    public static CompliantTrendRequest getCompliantTrendEmptyRequest() {
        return new CompliantTrendRequest();
    }

    public static RuleTrendRequest getRuleTrendRequest() {
        RuleTrendRequest ruleTrendRequest = new RuleTrendRequest();
        ruleTrendRequest.setAg("aws-all");
        ruleTrendRequest
                .setRuleid("PacMan_TaggingRule_version-1_Ec2TaggingRule_ec2");
        ruleTrendRequest.setFilters(getFilters());
        return ruleTrendRequest;
    }

    public static RuleTrendRequest getRuleTrendRequestEmpty() {
        return new RuleTrendRequest();
    }

    public static ResponseWithOrder getResponseWithOrder() {
        List<LinkedHashMap<String, Object>> responseList = new ArrayList<>();
        LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("passed", 1000);
        responseMap.put("failed", 500);
        responseMap.put("assetsScanned", 1500);
        responseMap.put("compliance_percent", 25);
        responseMap.put("ruleCategory", "tagging");
        responseList.add(responseMap);
        ResponseWithOrder responseWithOrder = new ResponseWithOrder();
        responseWithOrder.setTotal(1000);
        responseWithOrder.setResponse(responseList);
        return responseWithOrder;
    }

    public static ProjectionResponse getProjectionResponse() {
        List<Map<String, Object>> responseList = new ArrayList<>();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("passed", 1000);
        responseMap.put("failed", 500);
        responseMap.put("assetsScanned", 1500);
        responseMap.put("compliance_percent", 25);
        responseMap.put("ruleCategory", "tagging");
        responseList.add(responseMap);
        ProjectionResponse projectionResponse = new ProjectionResponse(
                "onprem-vm", "onpremserver", 1000, 2018, 3, responseList);
        return projectionResponse;
    }

    public static PatchingProgressResponse getPatchingProgressResponse() {
        List<Map<String, Object>> responseList = new ArrayList<>();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("passed", 1000);
        responseMap.put("failed", 500);
        responseMap.put("assetsScanned", 1500);
        responseMap.put("compliance_percent", 25);
        responseMap.put("ruleCategory", "tagging");
        responseList.add(responseMap);
        PatchingProgressResponse projectionResponse = new PatchingProgressResponse(
                "onprem-vm", "onpremserver", 1000, 2018, 3, responseList);
        return projectionResponse;
    }

    public static Request getRequest() {
        Request request = new Request();
        request.setAg("aws-all");
        request.setSearchtext("low");
        request.setFilter(getFilters());
        request.setSize(7);
        return request;
    }
    
    public static Request getRequestWithIssueId() {
        Request request = new Request();
        request.setAg("aws-all");
        request.setSearchtext("low");
        request.setFilter(getOnlyIssueIdFilters());
        request.setSize(7);
        return request;
    }
    
    public static Request getWithoutSizeRequest() {
        Request request = new Request();
        request.setAg("aws-all");
        request.setSearchtext("low");
        request.setFilter(getFilters());
        return request;
    }

    public static Request getRequestEmpty() {
        return new Request();
    }

    public static Request getRequestFromLessThanZero() {
        Request request = new Request();
        request.setAg("aws-all");
        request.setFrom(-1);
        request.setFilter(getFilters());
        return request;
    }
    
    public static Request getRequestFromgreaterThanMasterList() {
        Request request = new Request();
        request.setAg("aws-all");
        request.setFrom(100);
        request.setFilter(getFilters());
        return request;
    }
    
    public static IssueAuditLogRequest getIssueAuditLogRequest() {
        IssueAuditLogRequest issueAuditLogReq = new IssueAuditLogRequest();
        issueAuditLogReq.setIssueId("123");
        issueAuditLogReq.setFrom(2);
        issueAuditLogReq.setSize(3);
        issueAuditLogReq.setSearchText("ec2");
        issueAuditLogReq.setTargetType("ec2");
        return issueAuditLogReq;
    }

    public static IssueAuditLogRequest getIssueAuditLogRequestEmpty() {
        return new IssueAuditLogRequest();
    }

    public static RuleDetails getRuleDetails() {
        RuleDetails details = new RuleDetails();
        details.setRuleId("PacMan_TaggingRule_version-1_Ec2TaggingRule_ec2");
        return new RuleDetails();
    }

    public static IssueResponse getIssueResponse() {
        IssueResponse issueResponse =  new IssueResponse();
        issueResponse.setExceptionReason("reason");
        return issueResponse;
    }

    public static KernelVersion getKernelVersion() {
            KernelVersion kernelVersion = new KernelVersion();
            kernelVersion.setInstanceId("i-123");
            kernelVersion.setKernelVersionId("2.446.32-696222.30.1.el6.x86_64");
            return kernelVersion;
    }
    
    public static PatchingRequest getPatchingRequest() {
        PatchingRequest request = new PatchingRequest();
        request.setAg("aws-all");
        request.setQuarter(3);
        request.setYear(2018);
        return request;
    }

    public static PatchingRequest getPatchingRequestEmpty() {
        return new PatchingRequest();
    }
    
    public static PatchingRequest getPatchingRequestYearAndQuarterEmpty() {
        PatchingRequest request = new PatchingRequest();
        request.setAg("aws-all");
        return request;
    }
    
    public static PatchingRequest getPatchingRequestMoreYear() {
        PatchingRequest request = new PatchingRequest();
        request.setAg("aws-all");
        request.setQuarter(3);
        request.setYear(2101);
        return request;
    }
    
    public static JsonArray getJsonArray() {
        Gson gson = new Gson();
        JsonObject obj = new JsonObject();
        obj.add("doc_count", gson.fromJson("500", JsonElement.class));
        obj.add("key", gson.fromJson("ec2", JsonElement.class));
        JsonArray array = new JsonArray();
        array.add(obj);
        return array;
    }
    
    public static LinkedHashMap<String, Object> getLinkedHashMapObject() {
        LinkedHashMap<String, Object> ruleMap = new LinkedHashMap<>();
        ruleMap.put("datasource", "aws");
        ruleMap.put("targetType", "ec2");
        ruleMap.put("annotationid", "annotationid");
        ruleMap.put("status", "open");
        ruleMap.put("auditdate", "2017-11-09T18:03:30.263Z");
        ruleMap.put("_auditdate", "2017-11-09");
        ruleMap.put("ruleId", "PacMan_TaggingRule_version-1_Ec2TaggingRule_ec2");
        return ruleMap;
    }
    
    public static List<LinkedHashMap<String, Object>> getLinkedHashMapObjectList() {
        List<LinkedHashMap<String, Object>> ruleList = new ArrayList<>();
        ruleList.add(getLinkedHashMapObject());
        return ruleList;
    }
    
    public static AssetCountDTO[] getAssetCountByApps() {

        AssetCountDTO assetCountDTO = new AssetCountDTO();
        assetCountDTO.setName("Intelligent Management System");
        assetCountDTO.setType("ec2");
        AssetCountDTO assetCountDTO1 = new AssetCountDTO();
        assetCountDTO1.setName("Intelligent Management System");
        assetCountDTO1.setType("onpremserver");
        AssetCountDTO[] assetCountDTOs = new AssetCountDTO[] { assetCountDTO,
                assetCountDTO1 };
        return assetCountDTOs;
    }
    
    public static String getTargetTypes() {
        return "'ec2','s3','appelb','asg','classicelb','stack','dynamodb','efs','emr','lambda','nat','eni','rdscluster','rdsdb','redshift','sg','snapshot','subnet','targetgroup','volume','vpc','api','iamuser','iamrole','rdssnapshot','account','checks','kms','phd','cloudfront','cert','wafdomain','corpdomain','elasticip','routetable','internetgateway','launchconfig','networkacl','vpngateway','asgpolicy','snstopic','dhcpoption','peeringconnection','customergateway','vpnconnection','directconnect','virtualinterface','elasticsearch'";
    }
    
    public static List<Map<String, Object>> getMapList() {
        List<JsonObject> innerList = new ArrayList<>();
        Gson gson = new Gson();
        JsonObject innerMap = new JsonObject();
        innerMap.add("encrypt", gson.fromJson("false", JsonElement.class));
        innerMap.add("value", gson.fromJson("Ti39halfu8", JsonElement.class));
        innerMap.add("key", gson.fromJson("checkId", JsonElement.class));
        innerList.add(innerMap);
        innerMap = new JsonObject();
        innerMap.add("encrypt", gson.fromJson("false", JsonElement.class));
        innerMap.add("value", gson.fromJson(
                "check-for-amazon-RDS-idle-DB-instances", JsonElement.class));
        innerMap.add("key", gson.fromJson("ruleKey", JsonElement.class));
        innerList.add(innerMap);
        innerMap = new JsonObject();
        innerMap.add("encrypt", gson.fromJson("false", JsonElement.class));
        innerMap.add("value", gson.fromJson("http", JsonElement.class));
        innerMap.add("key", gson.fromJson("serviceEsURL", JsonElement.class));
        innerList.add(innerMap);
        innerMap = new JsonObject();
        innerMap.add("encrypt", gson.fromJson("false", JsonElement.class));
        innerMap.add("value", gson.fromJson("low", JsonElement.class));
        innerMap.add("key", gson.fromJson("severity", JsonElement.class));
        innerList.add(innerMap);
        innerMap = new JsonObject();
        innerMap.add("encrypt", gson.fromJson("false", JsonElement.class));
        innerMap.add("value",
                gson.fromJson("costOptimization", JsonElement.class));
        innerMap.add("key", gson.fromJson("ruleCategory", JsonElement.class));
        innerList.add(innerMap);
        Map<String, Object> distSeverirtMap = new HashMap<>();
        distSeverirtMap.put("assetGroup", "aws-all");
        distSeverirtMap.put("policyId",
                "PacMan_AmazonRDSIdleDBInstancesRule_version-1");
        
        distSeverirtMap.put("params", innerList);

        List<Map<String, Object>> trendProgressInfoList = new ArrayList<>();
        Map<String, Object> commonMap = new HashMap<>();
        commonMap.put("date", "2018-06-26");
        commonMap.put("modifiedDate", "2018-06-26");
        commonMap.put("noncompliant", 2114.0);
        commonMap.put("total", 2123.0);
        commonMap.put("compliant", 9.0);
        commonMap.put("ruleId", "abc_ruleId");
        commonMap.put("severity", "high");
        commonMap.put("overall", 73);
        commonMap.put("distribution_by_severity", distSeverirtMap);
        commonMap.put("total_issues", 1500);
        commonMap.put("ruleCategory", "security");
        commonMap
                .put("ruleId",
                        "PacMan_AmazonRDSIdleDBInstancesRule_version-1_AmazonRDSIdleDBInstancesRule_rdsdb");
        commonMap.put("displayName",
                "Amazon RDS DB instances should not be idle");
        commonMap.put("targetType", "rdsdb");
        commonMap.put("ruleParams", distSeverirtMap);
        commonMap.put("resolution", "test resolution");
        commonMap.put("kernelVersion", "kernelVersion");
        commonMap.put("tagging", "tagging");
        trendProgressInfoList.add(commonMap);

        commonMap = new HashMap<>();
        commonMap.put("date", "2018-06-26");
        commonMap.put("modifiedDate", "2018-06-26");
        commonMap.put("noncompliant", 2114.0);
        commonMap.put("total", 2123.0);
        commonMap.put("compliant", 9.0);
        commonMap.put("ruleId", "abc_ruleId");
        commonMap.put("severity", "high");
        commonMap.put("overall", 73);
        commonMap.put("distribution_by_severity", distSeverirtMap);
        commonMap.put("total_issues", 1500);
        commonMap.put("ruleCategory", "security");
        commonMap
                .put("ruleId",
                        "PacMan_cloud-kernel-compliance_version-1_Ec2-Kernel-Compliance-Rule_ec2");
        commonMap.put("displayName", "Kernel compliance rule");
        commonMap.put("targetType", "rdsdb");
        commonMap.put("ruleParams", distSeverirtMap);
        commonMap.put("resolution", "test resolution");
        trendProgressInfoList.add(commonMap);
        commonMap = new HashMap<>();
        commonMap.put("date", "2018-06-26");
        commonMap.put("modifiedDate", "2018-06-26");
        commonMap.put("noncompliant", 2114.0);
        commonMap.put("total", 2123.0);
        commonMap.put("compliant", 9.0);
        commonMap.put("ruleId", "abc_ruleId");
        commonMap.put("severity", "high");
        commonMap.put("overall", 73);
        commonMap.put("distribution_by_severity", distSeverirtMap);
        commonMap.put("total_issues", 1500);
        commonMap.put("ruleCategory", "security");
        commonMap
                .put("ruleId",
                        "PacMan_onpremisekernelversion_version-1_onpremKernelVersionRule_onpremserver");
        commonMap
                .put("displayName", "Onprem Kernel compliance rule");
        commonMap.put("targetType", "rdsdb");
        commonMap.put("ruleParams", distSeverirtMap);
        commonMap.put("resolution", "test resolution");
        commonMap.put("kernelVersion", "kernelVersion");
        commonMap.put("tagging", "tagging");
        trendProgressInfoList.add(commonMap);

        commonMap = new HashMap<>();
        commonMap.put("date", "2018-06-26");
        commonMap.put("modifiedDate", "2018-06-26");
        commonMap.put("noncompliant", 2114.0);
        commonMap.put("total", 2123.0);
        commonMap.put("compliant", 9.0);
        commonMap.put("ruleId", "abc_ruleId");
        commonMap.put("severity", "low");
        commonMap.put("overall", 73);
        commonMap.put("distribution_by_severity", distSeverirtMap);
        commonMap.put("total_issues", 1500);
        commonMap.put("ruleCategory", "security");
        commonMap.put("ruleId", "PacMan_TaggingRule_version-1");
        commonMap
                .put("displayName", "Onprem Kernel compliance rule");
        commonMap.put("targetType", "ec2");
        commonMap.put("ruleParams", distSeverirtMap);
        commonMap.put("resolution", "test resolution");
        commonMap.put("kernelVersion", "kernelVersion");
        commonMap.put("tagging", "tagging");
        trendProgressInfoList.add(commonMap);

        return trendProgressInfoList;
    }
    
    public static List<Object> getRules() {
        List<Object> rules = new ArrayList<>();
        rules.add("PacMan_TaggingRule_version-1_Ec2TaggingRule_ec2");
        return rules;
    }

    
    public static Map<String,Map<String,Object>> getMapOfMapObject() {
       Map<String,Map<String,Object>> map = new HashMap<>();
       map.put("test", getMapObject());
       return map;
    }
    
}
