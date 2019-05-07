package com.tmobile.cso.pacman.datashipper.entity;


import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.cso.pacman.datashipper.es.ESManager;
import com.tmobile.cso.pacman.datashipper.util.AssetGroupUtil;
import com.tmobile.cso.pacman.datashipper.util.AuthManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AssetGroupUtil.class, ESManager.class,AuthManager.class})
public class AssetGroupStatsCollectorTest {
    
    AssetGroupStatsCollector assetGroupStatsCollector = new AssetGroupStatsCollector();
    @Before
    public void setup() throws Exception{
        PowerMockito.mockStatic(AuthManager.class);
        when(AuthManager.getToken()).thenReturn("");
    }
    
  

  
    @SuppressWarnings("unchecked")
    @Test
    public void testUploadAssetGroupTagCompliance() throws Exception{
        PowerMockito.mockStatic(AssetGroupUtil.class);
        Map<String,Object> comSummaryMap = new HashMap<>();
        comSummaryMap.put("total", 1345l);
        comSummaryMap.put("compliant", 1000l);
        comSummaryMap.put("noncompliant", 345l);
        when(AssetGroupUtil.fetchTaggingSummary(anyString(),anyString(),anyString())).thenReturn(comSummaryMap);
        
        PowerMockito.mockStatic(ESManager.class);
        doNothing().when(ESManager.class);
        ESManager.uploadData(anyString(), anyString(), anyList(), anyString(), anyBoolean());
        
        assetGroupStatsCollector.uploadAssetGroupTagCompliance(Arrays.asList("pacman"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUploadAssetGroupRuleCompliance() throws Exception{
        PowerMockito.mockStatic(AssetGroupUtil.class);
        List<Map<String, Object>> ruleInfoList = new ArrayList<>();
        Map<String,Object> ruleInfo = new HashMap<>();
        ruleInfo.put("domain", "infra");
        ruleInfo.put("ruleId", "testruleid1");
        ruleInfo.put("compliance_percent", 55);
        ruleInfo.put("total", 1345l);
        ruleInfo.put("compliant", 1000l);
        ruleInfo.put("noncompliant",  345l);
        ruleInfo.put("contribution_percent", 66);
        ruleInfoList.add(ruleInfo);
        when(AssetGroupUtil.fetchRuleComplianceInfo(anyString(),anyString(),anyList(),anyString())).thenReturn(ruleInfoList);
        
        PowerMockito.mockStatic(ESManager.class);
        doNothing().when(ESManager.class);
        ESManager.uploadData(anyString(), anyString(), anyList(), anyString(), anyBoolean());
        
        Map<String, List<String>> assetGroups = new HashMap<>();
        List<String> domains = new ArrayList<>();
        domains.add("infra");
        assetGroups.put("pacman", domains);
        assetGroupStatsCollector.uploadAssetGroupRuleCompliance(assetGroups);
    }
    
  
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUploadAssetGroupCompliance() throws Exception{
        PowerMockito.mockStatic(AssetGroupUtil.class);
        List<Map<String, Object>> ruleInfoList = new ArrayList<>();
        Map<String,Object> complnInfo = new HashMap<>();
        complnInfo.put("domain", "infra");
        complnInfo.put("tagging", 60);
        complnInfo.put("security", 89);
        complnInfo.put("costOptimization", 66);
        complnInfo.put("governance", 83);
        complnInfo.put("overall",  74);
        ruleInfoList.add(complnInfo);
        when(AssetGroupUtil.fetchRuleComplianceInfo(anyString(),anyString(),anyList(),anyString())).thenReturn(ruleInfoList);
        
        PowerMockito.mockStatic(ESManager.class);
        doNothing().when(ESManager.class);
        ESManager.uploadData(anyString(), anyString(), anyList(), anyString(), anyBoolean());
        
        Map<String, List<String>> assetGroups = new HashMap<>();
        List<String> domains = new ArrayList<>();
        domains.add("infra");
        assetGroups.put("pacman", domains);
        assetGroupStatsCollector.uploadAssetGroupCompliance(assetGroups);
    }
    
 
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUploadAssetGroupCountStats() throws Exception{
        PowerMockito.mockStatic(AssetGroupUtil.class);
        List<Map<String, Object>> typeCounts  = new ArrayList<>();
        Map<String, Object> typeCount = new HashMap<>();
        typeCount.put("type", "ec2");
        typeCount.put("count", 125l);
        typeCounts.add(typeCount);
        when(AssetGroupUtil.fetchTypeCounts(anyString(),anyString(),anyString())).thenReturn(typeCounts);
        

        Map<String, Map<String, Map<String, Object>>> currentInfo = new HashMap<>();
        Map<String, Object> minMax = new HashMap<>();
        minMax.put("min", 100);
        minMax.put("max", 120);        
        Map<String, Map<String, Object>> typeMap = new HashMap<>();
        typeMap.put("ec2", minMax);
        currentInfo.put("pacman", typeMap);
        PowerMockito.mockStatic(ESManager.class);
        when(ESManager.fetchCurrentCountStatsForAssetGroups(anyString())).thenReturn(currentInfo);
        doNothing().when(ESManager.class);
        ESManager.uploadData(anyString(), anyString(), anyList(), anyString(), anyBoolean());
        
        Map<String, List<String>> assetGroups = new HashMap<>();
        List<String> domains = new ArrayList<>();
        domains.add("infra");
        assetGroups.put("pacman", domains);
        assetGroupStatsCollector.uploadAssetGroupCountStats(Arrays.asList("pacman"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUploadAssetGroupIssues() throws Exception{
        PowerMockito.mockStatic(AssetGroupUtil.class);
        List<Map<String,Object>>  returnList = new ArrayList<>();
        Map<String,Object> issuesInfo = new HashMap<>();
        issuesInfo.put("domain", "infra");
        issuesInfo.put("total", 123l);
        returnList.add(issuesInfo);
        when(AssetGroupUtil.fetchIssuesInfo(anyString(),anyString(),anyList(),anyString())).thenReturn(returnList);
        
        PowerMockito.mockStatic(ESManager.class);
        doNothing().when(ESManager.class);
        ESManager.uploadData(anyString(), anyString(), anyList(), anyString(), anyBoolean());
        
        Map<String, List<String>> assetGroups = new HashMap<>();
        List<String> domains = new ArrayList<>();
        domains.add("infra");
        assetGroups.put("pacman", domains);
        assetGroupStatsCollector.uploadAssetGroupIssues(assetGroups);
    }
    
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCollectAssetGroupStats() throws Exception{
        
        Map<String, List<String>> assetGroups = new HashMap<>();
        List<String> domains = new ArrayList<>();
        domains.add("infra");
        assetGroups.put("pacman", domains);
        
        PowerMockito.mockStatic(AssetGroupUtil.class);
        when(AssetGroupUtil.fetchAssetGroups(anyString(),anyString())).thenReturn(assetGroups);
      
        
        PowerMockito.mockStatic(ESManager.class);
        doNothing().when(ESManager.class);
        ESManager.createIndex(anyString(),anyList());
        ESManager.createType(anyString(),anyString(),anyList());
        
        assetGroupStatsCollector = PowerMockito.spy(assetGroupStatsCollector);
        doNothing().when(assetGroupStatsCollector).uploadAssetGroupRuleCompliance(anyMap());
        doNothing().when(assetGroupStatsCollector).uploadAssetGroupCountStats(anyList());
        doNothing().when(assetGroupStatsCollector).uploadAssetGroupCompliance(anyMap());
        doNothing().when(assetGroupStatsCollector).uploadAssetGroupTagCompliance(anyList());
        doNothing().when(assetGroupStatsCollector).uploadAssetGroupIssues(anyMap());
        assetGroupStatsCollector.collectAssetGroupStats();
      
    }
}

