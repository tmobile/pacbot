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
  Modified Date: Jun 29, 2018

 **/
package com.tmobile.pacman.api.compliance.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
import com.tmobile.pacman.api.compliance.domain.AssetCountDTO;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class PatchingRepositoryImplTest {
    @InjectMocks
    PatchingRepositoryImpl patchingRepositoryImpl;
    @Mock
    ElasticSearchRepository elasticSearchRepository;

    @Mock
    PacmanRdsRepository rdsrepository;

    @Mock
    FilterRepository filterRepository;

    @Test
    public void initTest() {
        patchingRepositoryImpl.init();
    }

    @Test
    public void addParentConditionPatchingTest() {
        patchingRepositoryImpl.addParentConditionPatching("ec2");
        patchingRepositoryImpl.addParentConditionPatching("onpremserver");
        patchingRepositoryImpl.addParentConditionPatching("s3");
    }

    @Test
    public void getAmilAvailDateTest() {
        when(rdsrepository.queryForString(anyString())).thenReturn("07-02-2018");
        patchingRepositoryImpl.getAmilAvailDate(2018, 2);
        when(rdsrepository.queryForString(anyString())).thenThrow(new RuntimeException());
      assertTrue(null== patchingRepositoryImpl.getAmilAvailDate(2018, 2));
    }

    @Test
    public void getClosedIssueInfoTest() throws Exception {
        List<Map<String, Object>> closedIssueInfo = new ArrayList<>();
        Map<String, Object> issueinfo = new HashMap<>();
        issueinfo.put("resourceid", "i-12344");
        issueinfo.put("issueStatus", "closed");
        issueinfo.put("reason-to-close", "Kernel version compliant");
        closedIssueInfo.add(issueinfo);
        ReflectionTestUtils.setField(patchingRepositoryImpl, "esUrl", "dummyEsURL");
		String response = "{\"took\":2205,\"timed_out\":false,\"_shards\":{\"total\":3,\"successful\":3,\"failed\":0},\"hits\":{\"total\":4151,\"max_score\":3.7548497,"
				+ "\"hits\":[{\"_index\":\"aws_ec2\",\"_type\":\"_type\",\"_id\":\"_id\",\"_score\":3.7548497,\"_routing\":\"_routing\",\"_parent\":\"_parent\",\"_source\":{\"severity\":\"high\",\"_resourceid\":\"_resourceid\",\"ruleCategory\":\"security\",\"closeddate\":\"2018-02-15T05:38:34.533Z\",\"_docid\":\"_docid\",\"failedTypes\":\"[spaceandsat, ldap, rhnsystemdetails, ssh]\",\"targetType\":\"ec2\",\"type\":\"issue\",\"accountid\":\"accountid\",\"executionId\":\"executionId\",\"issueStatus\":\"closed\",\"createdDate\":\"2018-02-15T04:38:23.754Z\",\"policyId\":\"PacMan_cloud-kernel-compliance_version-1\",\"accountname\":\"test\",\"pac_ds\":\"aws\",\"modifiedDate\":\"2018-02-15T04:38:23.754Z\",\"reason-to-close\":\"{sourceType=spaceandsat, kernelVersion=kernelVersion, description=Kernel version is compliant}\",\"ruleId\":\"PacMan_cloud-kernel-compliance_version-1_Ec2-Kernel-Compliance-Rule_ec2\",\"region\":\"region\",\"desc\":\"Kernel version not found.validated using:spaceandsat/ldap/rhnsystemdetails/ssh\"}}]}}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        patchingRepositoryImpl.getClosedIssueInfo("aws-all", 100);
        // test response with hits
        String dummyResponse = "{\"took\":2205,\"timed_out\":false,\"_shards\":{\"total\":3,\"successful\":3,\"failed\":0}}";
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(dummyResponse);
        patchingRepositoryImpl.getClosedIssueInfo("aws-all", 100);
        String emptyHits = "{\"took\":2205,\"timed_out\":false,\"_shards\":{\"total\":3,\"successful\":3,\"failed\":0},\"hits\":{\"total\":4151,\"max_score\":3.7548497}}";
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(emptyHits);
        patchingRepositoryImpl.getClosedIssueInfo("aws-all", 100);

        // test Data Exception
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> patchingRepositoryImpl.getClosedIssueInfo("aws-all", 100)).isInstanceOf(
                DataException.class);
    }

    @Test
    public void getDirectorsAndExcutiveSponsersTest() throws Exception {
		String response = "{\"took\":195,\"timed_out\":false,\"_shards\":{\"total\":5,\"successful\":5,\"failed\":0},"
				+ "\"hits\":{\"total\":1,\"max_score\":14.612631,\"hits\":[{\"_index\":\"_index\",\"_type\":\"_type\",\"_id\":\"test\",\"_score\":14.612631,"
				+ "\"_source\":{\"_appType\":\"_appType\",\"issueID\":\"issueID\",\"businessUnit\":\"billing\",\"discoverydate\":\"2018-06-26 14:00:00-0700\","
				+ "\"_docid\":\"_docid\",\"description\":\"app1\",\"tier\":\"Tier II\",\"cloudProvider\":\"AWS\",\"@id\":\"test\",\"appTag\":\"appTag\",\"latest\":true,"
				+ "\"summary\":\"App: summary\",\"_resourceid\":\"_resourceid\",\"projectLead\":\"abcd@test.com\",\"hiddenFlag\":\"No\",\"appName\":\"appName\","
				+ "\"director\":\"director1\",\"intakeDate\":\"2018-06-13\",\"workload\":\"workload\",\"parentOf\":[],\"executiveSponsor\":\"executive1\","
				+ "\"priority\":\"P2 - Critical\",\"issueType\":\"issueType\",\"stage\":\"On-Boarding\",\"appID\":\"appID\",\"internalFlag\":\"No\","
				+ "\"status\":\"Open\"}}]}}";
        mockStatic(PacHttpUtils.class);
        ReflectionTestUtils.setField(patchingRepositoryImpl, "esUrl", "dummyEsURL");

        // test without Direector & executive sponsor in the repose.
        String emptyDirresponse = "{\"took\":195,\"timed_out\":false,\"_shards\":{\"total\":5,\"successful\":5,\"failed\":0},\"hits\":{\"total\":1,\"max_score\":14.612631,"
        		+ "\"hits\":[{\"_index\":\"_index\",\"_type\":\"_type\",\"_id\":\"test\",\"_score\":14.612631,\"_source\":{\"_appType\":\"_appType\","
        		+ "\"issueID\":\"issueID\",\"businessUnit\":\"billing\",\"discoverydate\":\"2018-06-26 14:00:00-0700\",\"_docid\":\"_docid\",\"description\":"
        		+ "\"app1\",\"tier\":\"Tier II\",\"cloudProvider\":\"AWS\",\"@id\":\"test\",\"appTag\":\"TibcoBW\",\"latest\":true,\"summary\":\"App: summary\","
        		+ "\"_resourceid\":\"_resourceid\",\"projectLead\":\"abcd@test.com\",\"hiddenFlag\":\"No\",\"appName\":\"appName\",\"director\":\"\",\"director\":"
        		+ "\"2018-06-13\",\"executiveSponsor\":\"\",\"issueType\":\"issueType\",\"stage\":\"stage\",\"appID\":\"TBW\",\"internalFlag\":\"No\",\"status\":\"Open\"}}]}}";
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(emptyDirresponse);
        // ReflectionTestUtils.setField(patchingRepositoryImpl, "esUrl",
        // "dummyEsURL");
        patchingRepositoryImpl.getDirectorsAndExcutiveSponsers("app1", "Cloud");
        String responsewithempty = "{\"took\":195,\"timed_out\":false,\"_shards\":{\"total\":5,\"successful\":5,\"failed\":0},\"hits\":{\"total\":1,"
        		+ "\"max_score\":14.612631,\"hits\":[{\"_index\":\"aws_apps\",\"_type\":\"apps\",\"_id\":\"test\",\"_score\":14.612631,"
        		+ "\"_source\":{\"_appType\":\"_appType\",\"issueID\":\"issueID\",\"businessUnit\":\"billing\",\"discoverydate\":\"2018-06-26 14:00:00-0700\","
        		+ "\"_docid\":\"_docid\",\"description\":\"app1\",\"tier\":\"Tier II\",\"cloudProvider\":\"AWS\",\"@id\":\"test\",\"appTag\":\"appTag\","
        		+ "\"latest\":true,\"summary\":\"App: summary\",\"_resourceid\":\"_resourceid\",\"projectLead\":\"abcd@test.com\",\"hiddenFlag\":\"No\","
        		+ "\"appName\":\"appName\",\"intakeDate\":\"2018-06-13\",\"issueType\":\"issueType\",\"stage\":\"On-Boarding\",\"appID\":\"appID\","
        		+ "\"internalFlag\":\"No\",\"status\":\"Open\"}}]}}";
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(responsewithempty);
        patchingRepositoryImpl.getDirectorsAndExcutiveSponsers("app1", "Cloud");
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(patchingRepositoryImpl, "esUrl", "dummyEsURL");
        patchingRepositoryImpl.getDirectorsAndExcutiveSponsers("app1", "Cloud");
        // test Data Exception
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> patchingRepositoryImpl.getDirectorsAndExcutiveSponsers("app1", "Cloud")).isInstanceOf(
                DataException.class);
    }

    @Test
    public void getExecAndDirectorInfoTest() throws Exception {
        List<Map<String, Object>> executvesAndDirectorsInfo = new ArrayList<>();
        Map<String, Object> executiveInfo = new HashMap<>();
        executiveInfo.put("apptag", "app1");
        executiveInfo.put("executive", "Mark");
        executiveInfo.put("director", "Mats");
        executvesAndDirectorsInfo.add(executiveInfo);
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(executvesAndDirectorsInfo);
        patchingRepositoryImpl.getExecAndDirectorInfo();

        // test issyeDetails throws DataException
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> patchingRepositoryImpl.getExecAndDirectorInfo()).isInstanceOf(DataException.class);
    }

    @Test
    public void getInstanceInfoTest() throws Exception {
        List<Map<String, Object>> instancesInfo = new ArrayList<>();
        Map<String, Object> instanceInfo = new HashMap<>();
        instanceInfo.put("_resourceid", "i-1234");
        instanceInfo.put("accountName", "testAccount");
        instanceInfo.put("vpc", "test1234");
        instanceInfo.put("application", "app1");
        instancesInfo.add(instanceInfo);
        Map<String, String> filters = new HashMap<>();
        filters.put("_resourceid", "i-1234");
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(instancesInfo);
        Map<String, String> emptyfilters = new HashMap<>();
        patchingRepositoryImpl.getInstanceInfo("aws-all", null);
        patchingRepositoryImpl.getInstanceInfo("aws-all", emptyfilters);
        patchingRepositoryImpl.getInstanceInfo("aws-all", filters);

        // test issyeDetails throws DataException
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> patchingRepositoryImpl.getInstanceInfo("aws-all", filters)).isInstanceOf(
                DataException.class);
    }

    @Test
    public void getInstanceInfoCountTest() throws Exception {
        when(
                elasticSearchRepository.getTotalDocumentCountForIndexAndType(anyString(), anyString(), anyObject(),
                        anyObject(), anyObject(), anyString(), anyObject())).thenReturn(10l);
        patchingRepositoryImpl.getInstanceInfoCount("aws-all", null, null);
        Map<String, String> filters = new HashMap<>();
        patchingRepositoryImpl.getInstanceInfoCount("aws-all", filters, null);
        filters.put("tags.application", "app1");
        patchingRepositoryImpl.getInstanceInfoCount("aws-all", filters, null);
        when(
                elasticSearchRepository.getTotalDocumentCountForIndexAndType(anyString(), anyString(), anyObject(),
                        anyObject(), anyObject(), anyString(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> patchingRepositoryImpl.getInstanceInfoCount("aws-all", filters, null)).isInstanceOf(
                DataException.class);

    }

    @Test
    public void getIssueInfoTest() throws Exception {
        List<Map<String, Object>> issuesInfo = new ArrayList<>();
        Map<String, Object> issueinfo = new HashMap<>();
        issueinfo.put("resourceid", "i-12344");
        issueinfo.put("issueStatus", "open");
        issuesInfo.add(issueinfo);
        // test issueDetails
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(issuesInfo);
        patchingRepositoryImpl.getIssueInfo("aws-all");
        // test issyeDetails throws DataException
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> patchingRepositoryImpl.getIssueInfo("aws-all")).isInstanceOf(DataException.class);
    }

    @Test
    public void getNonCompliantNumberForAGTest() throws Exception {
        List<Map<String, Long>> nonCompliantAseetsByApp = new ArrayList<>();
        Map<String, Long> application = new HashMap<>();
        application.put("app1", 100l);
        application.put("app1", 500l);
        nonCompliantAseetsByApp.add(application);
        AssetCountDTO assetcountByTypes = new AssetCountDTO();
        assetcountByTypes.setType("ec2");
        AssetCountDTO[] ec2TargetType = new AssetCountDTO[] { assetcountByTypes };
        when(
                elasticSearchRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(application);
        // test nonCompliant assetcount for Ec2
        when(filterRepository.getListOfTargetTypes(anyString(), anyString())).thenReturn(ec2TargetType);

    }

    @Test
    public void getNonCompliantNumberForAgAndResourceTypeTest() throws Exception {
        Map<String, Long> nonCompliant = new HashMap<>();
        nonCompliant.put("app1", 100l);
        nonCompliant.put("app2", 200l);
        AssetCountDTO assetcountByTypes = new AssetCountDTO();
        assetcountByTypes.setType("ec2");
        AssetCountDTO[] ec2TargetType = new AssetCountDTO[] { assetcountByTypes };
        when(
                elasticSearchRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(nonCompliant);
        // test nonCompliant assetcount for Ec2
        when(filterRepository.getListOfTargetTypes(anyString(), anyString())).thenReturn(ec2TargetType);
        patchingRepositoryImpl.getNonCompliantNumberForAgAndResourceType("aws-all", "ec2");
        // test non-compliant assetcount onpremserver
        assetcountByTypes = new AssetCountDTO();
        assetcountByTypes.setType("onpremserver");
        AssetCountDTO[] onpremTargetType = new AssetCountDTO[] { assetcountByTypes };
        when(filterRepository.getListOfTargetTypes(anyString(), anyString())).thenReturn(onpremTargetType);
        patchingRepositoryImpl.getNonCompliantNumberForAgAndResourceType("aws-all", "onpremserver");
        // test non-compliant other than ec2/s3
        assetcountByTypes = new AssetCountDTO();
        assetcountByTypes.setType("s3");
        AssetCountDTO[] s3TargetType = new AssetCountDTO[] { assetcountByTypes };
        when(filterRepository.getListOfTargetTypes(anyString(), anyString())).thenReturn(s3TargetType);
        patchingRepositoryImpl.getNonCompliantNumberForAgAndResourceType("aws-all", "ec2");
        patchingRepositoryImpl.getNonCompliantNumberForAgAndResourceType("aws-all", "onpremserver");
        // test when non compliant when targetType is empty
        assetcountByTypes = new AssetCountDTO();
        AssetCountDTO[] emptyTargetType = new AssetCountDTO[] { assetcountByTypes };
        when(filterRepository.getListOfTargetTypes(anyString(), anyString())).thenReturn(emptyTargetType);
        patchingRepositoryImpl.getNonCompliantNumberForAgAndResourceType("aws-all", "ec2");

        // test DataException Scenario
        when(
                elasticSearchRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyInt(), anyObject()))
                .thenThrow(new RuntimeException());
        // test nonCompliant asset count for Ec2
        when(filterRepository.getListOfTargetTypes(anyString(), anyString())).thenReturn(ec2TargetType);
        assertThatThrownBy(() -> patchingRepositoryImpl.getNonCompliantNumberForAgAndResourceType("aws-all", "ec2"))
                .isInstanceOf(DataException.class);

    }

    @Test
    public void getOnpremIssueInfoTest() throws Exception {
        List<Map<String, Object>> onpremIssueInfo = new ArrayList<>();
        Map<String, Object> issueInfo = new HashMap<>();
        issueInfo.put("_resourceId", 1234);
        issueInfo.put("issueStatus", "open");
        onpremIssueInfo.add(issueInfo);
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(onpremIssueInfo);
        patchingRepositoryImpl.getOnpremIssueInfo("aws-all");
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> patchingRepositoryImpl.getOnpremIssueInfo("aws-all"))
                .isInstanceOf(DataException.class);

    }

    @Test
    public void getOnpremResourceInfoTest() throws Exception {
        List<Map<String, Object>> onpremResourceInfo = new ArrayList<>();
        Map<String, Object> resourceInfo = new HashMap<String, Object>();
        resourceInfo.put("_resourceId", 12345);
        resourceInfo.put("application", "appName1");
        onpremResourceInfo.add(resourceInfo);
        Map<String, String> filters = new HashMap<>();
        filters.put("resourceType", "onpremserver");
        // test method returning onprem resourceinfo
        when(
                elasticSearchRepository.getSortedDataFromESWithMustNotTermsFilter(anyString(), anyString(),
                        anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                        anyObject())).thenReturn(onpremResourceInfo);
        patchingRepositoryImpl.getOnpremResourceInfo("aws-all", filters);
        // test when filters are empty scenario.
        Map<String, String> emptyFilter = new HashMap<>();
        patchingRepositoryImpl.getOnpremResourceInfo("aws-all", emptyFilter);
        patchingRepositoryImpl.getOnpremResourceInfo("aws-all", null);
        // test method returning DataException
        when(
                elasticSearchRepository.getSortedDataFromESWithMustNotTermsFilter(anyString(), anyString(),
                        anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
                        anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> patchingRepositoryImpl.getOnpremResourceInfo("aws-all", filters)).isInstanceOf(
                DataException.class);

    }

    @Test
    public void getPatchingProgressTest() throws Exception {
        List<Map<String, Object>> pathingProgress = new ArrayList<>();
        Map<String, Object> patching = new HashMap<String, Object>();
        patching.put("week", 1);
        patching.put("compliance", 10);
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(pathingProgress);
        patchingRepositoryImpl.getPatchingProgress("aws-all", LocalDate.now(), LocalDate.now());
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(
                () -> patchingRepositoryImpl.getPatchingProgress("aws-all", LocalDate.now(), LocalDate.now()))
                .isInstanceOf(DataException.class);

    }

    @Test
    public void getPatchingWindowTest() {
        // test patching window method
        when(rdsrepository.count(anyString())).thenReturn(2);
        int count = patchingRepositoryImpl.getPatchingWindow();
        assertTrue(count > 0);
        // test DataException
        when(rdsrepository.count(anyString())).thenThrow(new RuntimeException());
        count = patchingRepositoryImpl.getPatchingWindow();
        assertTrue(count < 0);

    }

    @Test
    public void getQuartersWithPatchingData() throws Exception {
        Map<String, Long> patchingDataByQuarter = new HashMap<>();
        patchingDataByQuarter.put("1", 100l);
        patchingDataByQuarter.put("2", 200l);
        // test method returning patching databy quarter
        when(
                elasticSearchRepository.getDateHistogramForIndexAndTypeByInterval(anyString(), anyString(),
                        anyObject(), anyObject(), anyObject(), anyString(), anyString())).thenReturn(
                patchingDataByQuarter);
        patchingRepositoryImpl.getQuartersWithPatchingData("aws-all");
        // test asset group null scenario
        patchingRepositoryImpl.getQuartersWithPatchingData(null);
        // test DataException scenario
        when(
                elasticSearchRepository.getDateHistogramForIndexAndTypeByInterval(anyString(), anyString(),
                        anyObject(), anyObject(), anyObject(), anyString(), anyString())).thenThrow(
                new RuntimeException());
        assertThatThrownBy(() -> patchingRepositoryImpl.getQuartersWithPatchingData("aws-all")).isInstanceOf(
                DataException.class);

    }

    @Test
    public void getPatchingPercentForDateRangeTest() throws Exception{

    	 List<Map<String, Object>> patchingInfoList = new ArrayList<>();
         Map<String, Object> patchingInfoMap = new HashMap<>();
         patchingInfoMap.put("patching_percentage", "20");
         patchingInfoList.add(patchingInfoMap);
         when(
                 elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                         anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(patchingInfoList);
         List<Map<String, Object>> result = patchingRepositoryImpl.getPatchingPercentForDateRange( "aws-all", LocalDate.now(),
        		 LocalDate.now());
         assertTrue(result.size()==1);

    }
}
