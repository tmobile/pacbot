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
  Author :santoshi
  Modified Date: Jul 10, 2018

**/
package com.tmobile.pacman.api.notification.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.api.notification.client.ComplianceServiceClient;
import com.tmobile.pacman.api.notification.client.StatisticsServiceClient;
import com.tmobile.pacman.api.notification.domain.Response;
@RunWith(PowerMockRunner.class)
public class AssetGroupEmailServiceTest {
    @InjectMocks
    AssetGroupEmailService assetGroupEmailService;
    @Mock
    private NotificationService notificationService;

 @Mock
    private MailService mailService;

  @Mock
    private ComplianceServiceClient complianceServiceClient;
  @Mock
    @Autowired
    private StatisticsServiceClient statisticsServiceClient;


    @Value("${template.digest-mail.url}")
    private String mailTemplateUrl;
    @Test
    public void executeEmailServiceForAssetGroupTest(){
        List<Map<String, Object>> ownerEmailDetails=new ArrayList<Map<String,Object>>();
        Map<String,Object>ownerDetails=new HashMap<>();
        ownerDetails.put("ownerName", "jack");
        ownerDetails.put("assetGroup", "aws-all");
        ownerDetails.put("ownerEmail", "test@mail.com");
        ownerDetails.put("ownerName", "jack");
        ownerEmailDetails.add(ownerDetails);
        Map<String,Object>patchingDetails=new HashMap<>();
        patchingDetails.put("unpatched_instances", 0);
        patchingDetails.put("patched_instances", 5816);
        patchingDetails.put("total_instances", 5816);
        patchingDetails.put("patching_percentage", 100);
        Response patchingResponse = new Response();
        patchingResponse.setData(patchingDetails);
        Map<String,Object>certificateDetails=new HashMap<>();
        certificateDetails.put("certificates", 1284);
        certificateDetails.put("certificates_expiring", 0);
        Response CertificateResponse = new Response();
        CertificateResponse.setData(certificateDetails);
        Map<String,Object> taggingDetails=new HashMap<>();
        taggingDetails.put("assets", 122083);
        taggingDetails.put("untagged", 47744);
        taggingDetails.put("tagged", 74339);
        taggingDetails.put("compliance", 60);
        Response taggingResponse = new Response();
        taggingResponse.setData(taggingDetails);
        Map<String,Object> vulnerabilityDetails=new HashMap<>();
        vulnerabilityDetails.put("hosts", 6964);
        vulnerabilityDetails.put("vulnerabilities", 94258);
        vulnerabilityDetails.put("totalVulnerableAssets", 5961);
        Response vulnerabilityResponse = new Response();
        vulnerabilityResponse.setData(taggingDetails);
        Map<String,Object> complianceStats=new HashMap<>();
        complianceStats.put("hosts", 6964);
        complianceStats.put("vulnerabilities", 94258);
        complianceStats.put("totalVulnerableAssets", 5961);
        Response complianceStatsResponse = new Response();
        complianceStatsResponse.setData(complianceStats);


        Map<String,Object> topNonCompliant = new HashMap<>();
        topNonCompliant.put("hosts", 6964);
        topNonCompliant.put("vulnerabilities", 94258);
        topNonCompliant.put("totalVulnerableAssets", 5961);
        Response topNonCompliantAppsResponse = new Response();
        topNonCompliantAppsResponse.setData(topNonCompliant);

        Map<String, Object> responseDetails = Maps.newHashMap();
        responseDetails.put("application", "application123");
        List<Map<String, Object>> response = Lists.newArrayList();
        response.add(responseDetails);
        Map<String,Object> applicationNames = new HashMap<>();
        applicationNames.put("response", response);
        applicationNames.put("vulnerabilities", 94258);
        applicationNames.put("totalVulnerableAssets", 5961);
        Response applicationNamesResponse = new Response();
        applicationNamesResponse.setData(applicationNames);


        Map<String,Object> issueDetails = new HashMap<>();
        issueDetails.put("hosts", 6964);
        issueDetails.put("vulnerabilities", 94258);
        issueDetails.put("totalVulnerableAssets", 5961);
        Response issueDetailsResponse = new Response();
        issueDetailsResponse.setData(issueDetails);
        issueDetailsResponse.setMessage("message");
        assertTrue(issueDetailsResponse.getMessage().equals("message"));

        ReflectionTestUtils.setField(assetGroupEmailService, "mailTemplateUrl", mailTemplateUrl);
        when(complianceServiceClient.getPatching(anyString())).thenReturn(patchingResponse);
        when(complianceServiceClient.getCertificates(anyString())).thenReturn(CertificateResponse);
        when(complianceServiceClient.getTagging(anyString())).thenReturn(taggingResponse);
        when(statisticsServiceClient.getComplianceStats(anyString())).thenReturn(complianceStatsResponse);
        when(complianceServiceClient.getVulnerabilities(anyString())).thenReturn(vulnerabilityResponse);

        when(complianceServiceClient.getTopNonCompliantApps(anyString())).thenReturn(topNonCompliantAppsResponse);
        when(complianceServiceClient.getVulnerabilityByApplications(anyString())).thenReturn(applicationNamesResponse);
        when(complianceServiceClient.getDistribution(anyString())).thenReturn(issueDetailsResponse);

        when(notificationService.getAllAssetGroupOwnerEmailDetails()).thenReturn(ownerEmailDetails);
        assetGroupEmailService.executeEmailServiceForAssetGroup();

    }

    @Test
    public void sendDigestMail1(){
    	List<Map<String, Object>> ownerEmailDetails=new ArrayList<Map<String,Object>>();
        Map<String,Object>ownerDetails=new HashMap<>();
        ownerDetails.put("ownerName", "jack");
        ownerDetails.put("assetGroup", "aws-all");
        ownerDetails.put("ownerEmail", "test@mail.com");
        ownerDetails.put("ownerName", "jack");
        ownerEmailDetails.add(ownerDetails);
        Map<String,Object>patchingDetails=new HashMap<>();
        patchingDetails.put("unpatched_instances", 0);
        patchingDetails.put("patched_instances", 5816);
        patchingDetails.put("total_instances", 5816);
        patchingDetails.put("patching_percentage", 100);
        patchingDetails.put("output", getOutput());
        Response patchingResponse = new Response();
        patchingResponse.setData(patchingDetails);

        Map<String,Object>certificateDetails=new HashMap<>();
        certificateDetails.put("certificates", 1284);
        certificateDetails.put("certificates_expiring", 0);
        certificateDetails.put("output", getOutput());
        Response CertificateResponse = new Response();
        CertificateResponse.setData(certificateDetails);

        Map<String,Object> taggingDetails=new HashMap<>();
        taggingDetails.put("assets", 122083);
        taggingDetails.put("untagged", 47744);
        taggingDetails.put("tagged", 74339);
        taggingDetails.put("compliance", 60);
        taggingDetails.put("output", getOutput());
        Response taggingResponse = new Response();
        taggingResponse.setData(taggingDetails);

        Map<String,Object> vulnerabilityDetails=new HashMap<>();
        vulnerabilityDetails.put("hosts", 6964);
        vulnerabilityDetails.put("vulnerabilities", 94258);
        vulnerabilityDetails.put("totalVulnerableAssets", 5961);
        vulnerabilityDetails.put("output", getOutput());
        Response vulnerabilityResponse = new Response();
        vulnerabilityResponse.setData(taggingDetails);

        Map<String,Object> complianceStats=new HashMap<>();
        complianceStats.put("hosts", 6964);
        complianceStats.put("vulnerabilities", 94258);
        complianceStats.put("totalVulnerableAssets", 5961);
        complianceStats.put("output", getOutput());

        Map<String, Double> compliant = Maps.newHashMap();
        compliant.put("overall_compliance", 1234d);
        compliant.put("patch_compliance", 1234d);
        compliant.put("vuln_compliance", 1234d);
        compliant.put("tag_compliance", 1234d);
        compliant.put("cert_compliance", 1234d);

        complianceStats.put("compliance_stats", compliant);

        Response complianceStatsResponse = new Response();
        complianceStatsResponse.setData(complianceStats);



        Map<String, Object> responsetopNonCompliantDetails = Maps.newHashMap();
        responsetopNonCompliantDetails.put("application", "application123");
        List<Map<String, Object>> topNonCompliantResponse = Lists.newArrayList();
        topNonCompliantResponse.add(responsetopNonCompliantDetails);
        Map<String,Object> topNonCompliant = new HashMap<>();
        topNonCompliant.put("response", topNonCompliantResponse);
        topNonCompliant.put("hosts", 6964);
        topNonCompliant.put("vulnerabilities", 94258);
        topNonCompliant.put("totalVulnerableAssets", 5961);
        Response topNonCompliantAppsResponse = new Response();
        topNonCompliantAppsResponse.setData(topNonCompliant);


        Map<String, Object> responseDetails = Maps.newHashMap();
        responseDetails.put("application", "application123");
        List<Map<String, Object>> response = Lists.newArrayList();
        response.add(responseDetails);
        Map<String,Object> applicationNames = new HashMap<>();
        applicationNames.put("response", response);
        applicationNames.put("hosts", 6964);
        applicationNames.put("vulnerabilities", 94258);
        applicationNames.put("totalVulnerableAssets", 5961);
        Response applicationNamesResponse = new Response();
        applicationNamesResponse.setData(applicationNames);


        Map<String,Object> issueDetails = new HashMap<>();
        issueDetails.put("hosts", 6964);
        issueDetails.put("vulnerabilities", 94258);
        issueDetails.put("totalVulnerableAssets", 5961);
        Map<String, Object> issueDistributionDetails = Maps.newHashMap();
        Map<String, Integer> distributionBySeverity = Maps.newHashMap();
        distributionBySeverity.put("critical", 1233);

        issueDistributionDetails.put("distribution_by_severity", Maps.newHashMap());
        issueDetails.put("distribution", Maps.newHashMap());

        Response issueDetailsResponse = new Response();
        issueDetailsResponse.setData(issueDetails);
        issueDetailsResponse.setMessage("message");
        assertTrue(issueDetailsResponse.getMessage().equals("message"));

        ReflectionTestUtils.setField(assetGroupEmailService, "mailTemplateUrl", mailTemplateUrl);
        when(complianceServiceClient.getPatching(anyString())).thenReturn(patchingResponse);
        when(complianceServiceClient.getCertificates(anyString())).thenReturn(CertificateResponse);
        when(complianceServiceClient.getTagging(anyString())).thenReturn(taggingResponse);
        when(statisticsServiceClient.getComplianceStats(anyString())).thenReturn(complianceStatsResponse);
        when(complianceServiceClient.getVulnerabilities(anyString())).thenReturn(vulnerabilityResponse);

        when(complianceServiceClient.getTopNonCompliantApps(anyString())).thenReturn(topNonCompliantAppsResponse);
        when(complianceServiceClient.getVulnerabilityByApplications(anyString())).thenReturn(applicationNamesResponse);
        when(complianceServiceClient.getDistribution(anyString())).thenReturn(issueDetailsResponse);

        when(notificationService.getAllAssetGroupOwnerEmailDetails()).thenReturn(ownerEmailDetails);
    	assetGroupEmailService.sendDigestMail("assetGroupName", "ownerName", "ownerEmail", "mainTemplateUrl", "domainUrl");
    }

    @Test
    public void sendDigestMail(){
    	List<Map<String, Object>> ownerEmailDetails=new ArrayList<Map<String,Object>>();
        Map<String,Object>ownerDetails=new HashMap<>();
        ownerDetails.put("ownerName", "jack");
        ownerDetails.put("assetGroup", "aws-all");
        ownerDetails.put("ownerEmail", "test@mail.com");
        ownerDetails.put("ownerName", "jack");
        ownerEmailDetails.add(ownerDetails);
        Map<String,Object>patchingDetails=new HashMap<>();
        patchingDetails.put("unpatched_instances", 0);
        patchingDetails.put("patched_instances", 5816);
        patchingDetails.put("total_instances", 5816);
        patchingDetails.put("patching_percentage", 100);
        patchingDetails.put("output", getOutput());
        Response patchingResponse = new Response();
        patchingResponse.setData(patchingDetails);

        Map<String,Object>certificateDetails=new HashMap<>();
        certificateDetails.put("certificates", 1284);
        certificateDetails.put("certificates_expiring", 0);
        certificateDetails.put("output", getOutput());
        Response CertificateResponse = new Response();
        CertificateResponse.setData(certificateDetails);

        Map<String,Object> taggingDetails=new HashMap<>();
        taggingDetails.put("assets", 122083);
        taggingDetails.put("untagged", 47744);
        taggingDetails.put("tagged", 74339);
        taggingDetails.put("compliance", 60);
        taggingDetails.put("output", getOutput());
        Response taggingResponse = new Response();
        taggingResponse.setData(taggingDetails);

        Map<String,Object> vulnerabilityDetails=new HashMap<>();
        vulnerabilityDetails.put("hosts", 6964);
        vulnerabilityDetails.put("vulnerabilities", 94258);
        vulnerabilityDetails.put("totalVulnerableAssets", 5961);
        vulnerabilityDetails.put("output", getOutput());
        Response vulnerabilityResponse = new Response();
        vulnerabilityResponse.setData(taggingDetails);

        Map<String,Object> complianceStats=new HashMap<>();
        complianceStats.put("hosts", 6964);
        complianceStats.put("vulnerabilities", 94258);
        complianceStats.put("totalVulnerableAssets", 5961);
        complianceStats.put("output", getOutput());

        Map<String, Double> compliant = Maps.newHashMap();
        compliant.put("overall_compliance", 1234d);
        compliant.put("patch_compliance", 1234d);
        compliant.put("vuln_compliance", 1234d);
        compliant.put("tag_compliance", 1234d);
        compliant.put("cert_compliance", 1234d);

        complianceStats.put("compliance_stats", compliant);

        Response complianceStatsResponse = new Response();
        complianceStatsResponse.setData(complianceStats);



        Map<String, Object> responsetopNonCompliantDetails = Maps.newHashMap();
        responsetopNonCompliantDetails.put("application", "application123");
        List<Map<String, Object>> topNonCompliantResponse = Lists.newArrayList();
        topNonCompliantResponse.add(responsetopNonCompliantDetails);
        topNonCompliantResponse.add(responsetopNonCompliantDetails);
        topNonCompliantResponse.add(responsetopNonCompliantDetails);
        topNonCompliantResponse.add(responsetopNonCompliantDetails);
        topNonCompliantResponse.add(responsetopNonCompliantDetails);
        topNonCompliantResponse.add(responsetopNonCompliantDetails);
        topNonCompliantResponse.add(responsetopNonCompliantDetails);
        topNonCompliantResponse.add(responsetopNonCompliantDetails);
        topNonCompliantResponse.add(responsetopNonCompliantDetails);

        Map<String,Object> topNonCompliant = new HashMap<>();
        topNonCompliant.put("response", topNonCompliantResponse);
        topNonCompliant.put("hosts", 6964);
        topNonCompliant.put("vulnerabilities", 94258);
        topNonCompliant.put("totalVulnerableAssets", 5961);
        Response topNonCompliantAppsResponse = new Response();
        topNonCompliantAppsResponse.setData(topNonCompliant);


        Map<String, Object> responseDetails = Maps.newHashMap();
        responseDetails.put("application", "application123");
        List<Map<String, Object>> response = Lists.newArrayList();
        response.add(responseDetails);
        Map<String,Object> applicationNames = new HashMap<>();
        applicationNames.put("response", response);
        applicationNames.put("hosts", 6964);
        applicationNames.put("vulnerabilities", 94258);
        applicationNames.put("totalVulnerableAssets", 5961);
        Response applicationNamesResponse = new Response();
        applicationNamesResponse.setData(applicationNames);


        Map<String,Object> issueDetails = new HashMap<>();
        issueDetails.put("hosts", 6964);
        issueDetails.put("vulnerabilities", 94258);
        issueDetails.put("totalVulnerableAssets", 5961);
        Map<String, Object> issueDistributionDetails = Maps.newHashMap();
        Map<String, Integer> distributionBySeverity = Maps.newHashMap();
        distributionBySeverity.put("critical", 1233);

        issueDistributionDetails.put("distribution_by_severity", distributionBySeverity);
        issueDetails.put("distribution", issueDistributionDetails);

        Response issueDetailsResponse = new Response();
        issueDetailsResponse.setData(issueDetails);
        issueDetailsResponse.setMessage("message");
        assertTrue(issueDetailsResponse.getMessage().equals("message"));

        ReflectionTestUtils.setField(assetGroupEmailService, "mailTemplateUrl", mailTemplateUrl);
        when(complianceServiceClient.getPatching(anyString())).thenReturn(patchingResponse);
        when(complianceServiceClient.getCertificates(anyString())).thenReturn(CertificateResponse);
        when(complianceServiceClient.getTagging(anyString())).thenReturn(taggingResponse);
        when(statisticsServiceClient.getComplianceStats(anyString())).thenReturn(complianceStatsResponse);
        when(complianceServiceClient.getVulnerabilities(anyString())).thenReturn(vulnerabilityResponse);

        when(complianceServiceClient.getTopNonCompliantApps(anyString())).thenReturn(topNonCompliantAppsResponse);
        when(complianceServiceClient.getVulnerabilityByApplications(anyString())).thenReturn(applicationNamesResponse);
        when(complianceServiceClient.getDistribution(anyString())).thenReturn(issueDetailsResponse);

        when(notificationService.getAllAssetGroupOwnerEmailDetails()).thenReturn(ownerEmailDetails);
    	assetGroupEmailService.sendDigestMail("assetGroupName", "ownerName", "ownerEmail", "mainTemplateUrl", "domainUrl");
    }




    private  Map<String, Integer> getOutput() {
    	Map<String, Integer> output = Maps.newHashMap();
    	output.put("unpatched_instances", 123);
    	output.put("total_instances", 123);
    	output.put("assets", 123);
    	output.put("hosts", 123);
    	output.put("certificates", 123);
    	output.put("vulnerabilities", 123);
    	output.put("untagged", 123);
    	output.put("certificates_expiring", 123);
		return output;

    }
}
