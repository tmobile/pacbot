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
  Author : NidhishKrishnan
  Modified Date: Jan 28, 2018
  
**/
package com.tmobile.pacman.api.notification.service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.notification.client.ComplianceServiceClient;
import com.tmobile.pacman.api.notification.client.StatisticsServiceClient;
import com.tmobile.pacman.api.notification.domain.Response;
@Service
public class AssetGroupEmailService implements Constants{

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private ComplianceServiceClient complianceServiceClient;
	
	@Autowired
	private StatisticsServiceClient statisticsServiceClient;
	
	
	@Value("${template.digest-mail.url}")
	private String mailTemplateUrl;

	private final Logger log = LoggerFactory.getLogger(getClass());

	public void executeEmailServiceForAssetGroup() {
	    log.info(EMAIL_SERVICE_STARTED);
		List<Map<String, Object>> ownerEmailDetails = notificationService.getAllAssetGroupOwnerEmailDetails();

        ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
        String domainUrl = System.getenv("DOMAIN_URL");
	
		for(Map<String, Object> ownerEmailDetail : ownerEmailDetails) {
			emailExecutor.execute(new Runnable() {
	            @Override
	            public void run() {
	            	String ownnerName = ownerEmailDetail.get("ownerName").toString();
	                String assetGroupName = ownerEmailDetail.get("assetGroup").toString();
					String ownerEmail = ownerEmailDetail.get("ownerEmail").toString();
					sendDigestMail(assetGroupName, ownnerName, ownerEmail, mailTemplateUrl, domainUrl);
	            }
	        });
		}
		emailExecutor.shutdown();
		log.info(EMAIL_SERVICE_COMPLETED);
	}
	
	@SuppressWarnings("unchecked")
	public void sendDigestMail(String assetGroupName, String ownerName, String ownerEmail, String mainTemplateUrl, String domainUrl) {
		try {
			Map<String, Object> report = Maps.newHashMap();
			Response patchingDetails =  complianceServiceClient.getPatching(assetGroupName);
			Response certificateDetails =  complianceServiceClient.getCertificates(assetGroupName);
			Response taggingDetails =  complianceServiceClient.getTagging(assetGroupName);
			Response vulnerabilityDetails =  complianceServiceClient.getVulnerabilities(assetGroupName);
			Response complianceStats = statisticsServiceClient.getComplianceStats(assetGroupName);
			Response topNonCompliantApps = complianceServiceClient.getTopNonCompliantApps(assetGroupName);
			Response applicationNames = complianceServiceClient.getVulnerabilityByApplications(assetGroupName);
			Response issueDetails = complianceServiceClient.getDistribution(assetGroupName);
			Map<String, Object> issueDistributionDetails = (Map<String, Object>) issueDetails.getData().get("distribution");
			List<Map<String, Object>> topAppsAndDirectors = (List<Map<String, Object>>) topNonCompliantApps.getData().get("response");
			List<Map<String, Object>> topApplicationNames = (List<Map<String, Object>>) applicationNames.getData().get("response");
			Set<String> appNames = getAppNames(topApplicationNames);
			report.put("appNames", appNames);
			report.put("appNamesCount", appNames.size());
			report.put("ownerName", ownerName);
			
			/**
			 * 
			 *  Top App's and Directors with Un_patched Assets Details
			 *  
			 **/
			if(!topAppsAndDirectors.isEmpty()){
				if(topAppsAndDirectors.size()>=5){
					report.put(APP_TAB_DETAILS, topAppsAndDirectors.subList(0, 5));
					report.put(TOP_APP_COUNT, 5);
				} else {
					report.put(APP_TAB_DETAILS, topAppsAndDirectors.subList(0, topAppsAndDirectors.size()));
					report.put(TOP_APP_COUNT, topAppsAndDirectors.size());
				}
			} else {
				report.put(TOP_APP_COUNT, 0);
				report.put(APP_TAB_DETAILS, Lists.newArrayList());
			}

			Map<String, Double> compliant = (Map<String, Double>) complianceStats.getData().get("compliance_stats");
			report.put("overallCompliance", compliant.get("overall_compliance"));
			/**
			 * 
			 *  Patching Details
			 *  
			 **/
			Map<String, Integer> output = (Map<String, Integer>) patchingDetails.getData().get(OUTPUT);
			int unpatchedCount = output.get("unpatched_instances");
			int unpatchedTotalInstances = getAssetGroupTotalCount("total_instances", output);
			int unpatchedCompliant = roundOf(compliant.get("patch_compliance"));
			report.put("unpatchedCount", unpatchedCount);
			report.put("unpatchedCompliant", unpatchedCompliant);
			report.put("unpatchedTotalInstances", unpatchedTotalInstances);
			
			/**
			 * 
			 *  Vulnerability Details
			 *  
			 **/
			output = (Map<String, Integer>) vulnerabilityDetails.getData().get(OUTPUT);
			int vulnerabilitiesCount = output.get("vulnerabilities");
			int vulnerabilitiesTotalHosts = getAssetGroupTotalCount("hosts", output);
			int vulnerabilitiesCompliant = roundOf(compliant.get("vuln_compliance"));
	        report.put("vulnerabilitiesCount", vulnerabilitiesCount);
			report.put("vulnerabilitiesCompliant", vulnerabilitiesCompliant);
			report.put("vulnerabilitiesTotalHosts", vulnerabilitiesTotalHosts);

			/**
			 * 
			 *  Tagging Details
			 *  
			 **/
			
			output = (Map<String, Integer>) taggingDetails.getData().get(OUTPUT);
			int untaggedCount = output.get("untagged");
			int untaggedTotalInstances = getAssetGroupTotalCount("assets", output);
			int untaggedCompliant = roundOf(compliant.get("tag_compliance"));
			report.put("untaggedCount", untaggedCount);
			report.put("untaggedCompliant", untaggedCompliant);
			report.put("untaggedTotalInstances", untaggedTotalInstances);
			/**
			 * 
			 *  Certificate Details
			 *  
			 **/
			output = (Map<String, Integer>) certificateDetails.getData().get(OUTPUT);
			int expiringCount = output.get("certificates_expiring");
			int expiringDays = getAssetGroupTotalCount("certificates", output);
			int expiringCompliant = roundOf(compliant.get("cert_compliance"));
			report.put("expiringCount", expiringCount);
			report.put("expiringCompliant", expiringCompliant == 0 ? 100: expiringCompliant);
			report.put("expiringDays", expiringDays);
			/**
			 * 
			 *  Critical Issue Details
			 *  
			 **/
			Map<String, Integer> criticalIssuesDetails = (Map<String, Integer>)issueDistributionDetails.get("distribution_by_severity"); 
			if(criticalIssuesDetails.isEmpty()) {
				report.put(CRITICAL_ISSUE_COUNT, 0);
			} else {
				Integer criticalIssuesCount = criticalIssuesDetails.get("critical");
				if(criticalIssuesCount != null) {
					report.put(CRITICAL_ISSUE_COUNT, criticalIssuesCount);	
				} else {
					report.put(CRITICAL_ISSUE_COUNT, 0);
				}
			}

			report.put("criticalIssuesCompliant", compliant.get("overall_compliance"));
			report.put("criticalIssuesTotalIssues", issueDistributionDetails.get("total_issues"));
			domainUrl = domainUrl+"/un-subscribe?email="+ownerEmail;
			report.put("subscriptionUrl", domainUrl);
			String attachmentUrl="";
			List<String> toDetails = Lists.newArrayList(); 
			toDetails.add(ownerEmail); 

	        String emailContent = mailService.processTemplate(mainTemplateUrl, report);
	        log.info(EXE_EMAIL_SEND,assetGroupName);
		    if(emailContent != null) {
		    	mailService.prepareTemplateAndSendMail("noreply@pacman.t-mobile.com", toDetails, "Asset Group Weekly Report - "+assetGroupName, emailContent, attachmentUrl);
		    }
		} catch(Exception exception) {
			log.info("Exception in sendDigestMail:",exception);
		}
	}
	
	private Set<String> getAppNames(List<Map<String, Object>> topApplicationNames) {
		Set<String> appNames = Sets.newLinkedHashSet();
		for (int index = 0; index < topApplicationNames.size(); index++) {
			Map<String, Object> topApplicationName = topApplicationNames.get(index);
			String applicationName = topApplicationName.get(com.tmobile.pacman.api.commons.Constants.APPS).toString();
			if(StringUtils.isBlank(applicationName)) {
				if(index+1 == topApplicationNames.size()) {
					appNames.add(topApplicationName.get(com.tmobile.pacman.api.commons.Constants.APPS).toString());
				} else {
					appNames.add(topApplicationName.get(com.tmobile.pacman.api.commons.Constants.APPS).toString().concat(", "));
				}
			}
		}
		return appNames;
	}

	private int roundOf(final Double value) {
		try {
			DecimalFormat df = new DecimalFormat("##0");
			return Integer.parseInt(df.format((Math.round(value * 100.0) / 100.0)));
		} catch (Exception exception) {
		    log.error(exception.getMessage());
			return 0;
		}
	}
	
	private Integer getAssetGroupTotalCount(final String key, final Map<String, Integer> output) {
		return output.get(key);
	}
}
