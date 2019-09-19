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
  Modified Date: Jul 6, 2018

 **/
package com.tmobile.pacman.api.compliance.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

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

import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.HeimdallElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
import com.tmobile.pacman.api.compliance.client.AssetServiceClient;
import com.tmobile.pacman.api.compliance.domain.IssueResponse;
import com.tmobile.pacman.api.compliance.domain.IssuesException;
import com.tmobile.pacman.api.compliance.domain.KernelVersion;
import com.tmobile.pacman.api.compliance.domain.RuleDetails;
import com.tmobile.pacman.api.compliance.repository.model.RhnSystemDetails;
import com.tmobile.pacman.api.compliance.util.CommonTestUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class ComplianceRepositoryImplTest implements Constants {
    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    @Mock
    PacmanRdsRepository rdsepository;

    @Mock
    AssetServiceClient assetServiceClient;

    @Mock
    FilterRepository filterRepository;

    @Mock
    RhnSystemDetailsRepository rhnSystemDetailsRepository;

    @Mock
    PatchingRepository patchingRepository;

    @Mock
    HeimdallElasticSearchRepository heimdallElasticSearchRepository;
    @InjectMocks
    ComplianceRepositoryImpl complianceRepositoryImpl;

    @Test
    public void closeIssuesByRuleTest() throws Exception {
        List<Map<String, Object>> issueDetails = new ArrayList<>();
        Map<String, Object> issueDetailMap = new HashMap<>();
        issueDetailMap.put("resourceType", "ec2");
        issueDetailMap.put("status", "open");
        issueDetailMap.put("severity", "high");
        issueDetailMap.put(PAC_DS, AWS);
        issueDetailMap.put(PAC_DS, AWS);
        issueDetailMap.put(TYPE, "issue");
        issueDetailMap.put(ES_DOC_ROUTING_KEY, "12345");
        issueDetailMap.put(ES_DOC_PARENT_KEY, "ec2");
        issueDetailMap.put(ES_DOC_ID_KEY, "678");
        issueDetails.add(issueDetailMap);

        RuleDetails ruleDetails = new RuleDetails();
        ruleDetails.setRuleId("Kernel Compliance Rule");
        ruleDetails.setReason("kernel Version Non-Compliant");
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(issueDetails);
        when(complianceRepositoryImpl.getOpenIssueDetails(ruleDetails.getRuleId())).thenReturn(issueDetails);
        complianceRepositoryImpl.closeIssuesByRule(ruleDetails);

    }

    @Test
    public void exemptAndUpdateIssueDetailsTest() throws Exception {
        List<Map<String, Object>> issueDetails = new ArrayList<>();
        Map<String, Object> issueDetailMap = new HashMap<>();
        issueDetailMap.put("resourceType", "ec2");
        issueDetailMap.put("status", "open");
        issueDetailMap.put("severity", "high");
        issueDetailMap.put(PAC_DS, AWS);
        issueDetailMap.put(PAC_DS, AWS);
        issueDetailMap.put(TYPE, "issue");
        issueDetailMap.put(ES_DOC_ROUTING_KEY, "12345");
        issueDetailMap.put(ES_DOC_PARENT_KEY, "ec2");
        issueDetailMap.put(ES_DOC_ID_KEY, "678");
        issueDetails.add(issueDetailMap);

        IssueResponse issueReason = new IssueResponse();
        issueReason.setExceptionReason("exempted");
        issueReason.setIssueId("1234");
        RuleDetails ruleDetails = new RuleDetails();
        ruleDetails.setRuleId("Kernel Compliance Rule");
        ruleDetails.setReason("kernel Version Non-Compliant");
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(issueDetails);
        when(complianceRepositoryImpl.getOpenIssueDetails(ruleDetails.getRuleId())).thenReturn(issueDetails);
        complianceRepositoryImpl.exemptAndUpdateIssueDetails(issueReason);

    }

    @Test
    public void revokeAndUpdateIssueDetailsTest() throws Exception {
        List<Map<String, Object>> issueDetails = new ArrayList<>();
        Map<String, Object> issueDetailMap = new HashMap<>();
        issueDetailMap.put("resourceType", "ec2");
        issueDetailMap.put("status", "open");
        issueDetailMap.put("severity", "high");
        issueDetailMap.put(PAC_DS, AWS);
        issueDetailMap.put(PAC_DS, AWS);
        issueDetailMap.put(TYPE, "issue");
        issueDetailMap.put(ES_DOC_ROUTING_KEY, "12345");
        issueDetailMap.put(ES_DOC_PARENT_KEY, "ec2");
        issueDetailMap.put(ES_DOC_ID_KEY, "678");
        issueDetails.add(issueDetailMap);

        IssueResponse issueReason = new IssueResponse();
        issueReason.setExceptionReason("exempted");
        issueReason.setIssueId("1234");
        RuleDetails ruleDetails = new RuleDetails();
        ruleDetails.setRuleId("Kernel Compliance Rule");
        ruleDetails.setReason("kernel Version Non-Compliant");
        when(
                elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(issueDetails);
        when(complianceRepositoryImpl.getExemptedIssueDetails(issueReason.getIssueId())).thenReturn(issueDetails);
        when(
                elasticSearchRepository.updatePartialDataToES(anyString(), anyString(), anyString(), anyString(),
                        anyString(), anyObject())).thenReturn(true);
        ReflectionTestUtils.setField(complianceRepositoryImpl, "esUrl", "dummyEsURL");
        complianceRepositoryImpl.revokeAndUpdateIssueDetails("1234");

    }

    @Test
    public void getRuleDetailsByEnvironmentFromESTest() throws Exception {
        String response = "{\"took\":469,\"timed_out\":false,\"_shards\":{\"total\":176,\"successful\":176,\"failed\":0},\"hits\":{\"total\":115456,\"max_score\":0,\"hits\":[]},\"aggregations\":{\"NAME\":{\"doc_count_error_upper_bound\":691,\"sum_other_doc_count\":34323,\"buckets\":["
        		+ "{\"key\":\"key\",\"doc_count\":6258},{\"key\":\"key\",\"doc_count\":3339}]}}}";
        ReflectionTestUtils.setField(complianceRepositoryImpl, "esUrl", "dummyEsURL");
        ReflectionTestUtils.setField(complianceRepositoryImpl, "mandatoryTags", "Application,Environment");
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        complianceRepositoryImpl.getRuleDetailsByEnvironmentFromES("aws-all", "tagging-rule", "app1", null,"test");
        complianceRepositoryImpl.getRuleDetailsByEnvironmentFromES("aws-all", "tagging-rule", "app1", "dev","test");
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new RuntimeException());
        assertThatThrownBy(
                () -> complianceRepositoryImpl.getRuleDetailsByEnvironmentFromES("aws-all", "tagging-rule", "app1",
                        null,"test")).isInstanceOf(DataException.class);
    }

    @Test
    public void getRuleDetailsByApplicationFromESTest() throws Exception {
        String response = "{\"took\":469,\"timed_out\":false,\"_shards\":{\"total\":176,\"successful\":176,\"failed\":0},\"hits\":{\"total\":115456,\"max_score\":0,\"hits\":[]},\"aggregations\":{\"NAME\":{\"doc_count_error_upper_bound\":691,\"sum_other_doc_count\":34323,\"buckets\":["
        		+ "{\"key\":\"key\",\"doc_count\":6258},{\"key\":\"key\",\"doc_count\":3339}]}}}";
        ReflectionTestUtils.setField(complianceRepositoryImpl, "esUrl", "dummyEsURL");
        ReflectionTestUtils.setField(complianceRepositoryImpl, "mandatoryTags", "Application,Environment");
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        complianceRepositoryImpl.getRuleDetailsByApplicationFromES("aws-all", "tagging-rule", "app1");
        complianceRepositoryImpl.getRuleDetailsByApplicationFromES("aws-all", "tagging-rule", null);
        complianceRepositoryImpl.getRuleDetailsByApplicationFromES("aws-all", TAGGING_POLICY, null);
        complianceRepositoryImpl.getRuleDetailsByApplicationFromES("aws-all", EC2_KERNEL_COMPLIANCE_RULE, null);

        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new RuntimeException());
        assertThatThrownBy(
                () -> complianceRepositoryImpl.getRuleDetailsByApplicationFromES("aws-all", "tagging-rule", "app1"))
                .isInstanceOf(DataException.class);
    }

    @Test
    public void updateKernelVersionTest() throws Exception {
        KernelVersion kernelVersion = new KernelVersion();
        kernelVersion.setInstanceId("12345");
        kernelVersion.setKernelVersionId("12345");
        String response = "{\"count\":0,\"_shards\":{\"total\":3,\"successful\":3,\"failed\":0}}";
        String responsewithcount = "{\"count\":10,\"_shards\":{\"total\":3,\"successful\":3,\"failed\":0}}";
        ReflectionTestUtils.setField(complianceRepositoryImpl, "esUrl", "dummyEsURL");
        String kernelCriteria = "el6.x#2.6333.32-696.23.1.el6.x86_64|el7#3.10.0-6933333.231.1.el7.x86_64|el6uek#3.8.13-133318.20.3.el6uek-x86_64|amzn1#4.9.85-38.55558.amzn1.x86_64";

        when(rdsepository.queryForString(anyString())).thenReturn(kernelCriteria);
        mockStatic(PacHttpUtils.class);

        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(responsewithcount);
        complianceRepositoryImpl.updateKernelVersion(kernelVersion);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        complianceRepositoryImpl.updateKernelVersion(kernelVersion);

        RhnSystemDetails systemDetails = new RhnSystemDetails();
        systemDetails.setCompanyId(123l);
        when(rhnSystemDetailsRepository.findRhnSystemDetailsByInstanceId(kernelVersion.getInstanceId())).thenReturn(
                systemDetails);
        complianceRepositoryImpl.updateKernelVersion(kernelVersion);
        KernelVersion emptyKernalVersion = new KernelVersion();
        complianceRepositoryImpl.updateKernelVersion(emptyKernalVersion);
    }

    @Test
    public void getPolicyViolationDetailsByIssueIdTest() throws Exception {
        String response = "{\"took\":391,\"timed_out\":false,\"_shards\":{\"total\":176,\"successful\":176,\"failed\":0},\"hits\":{\"total\":5427,\"max_score\":4.344331,\"hits\":[{\"_index\":\"_index\",\"_type\":\"_type\",\"_id\":\"_id\",\"_score\":4.344331,\"_routing\":\"_routing\",\"_parent\":\"_parent\",\"_source\":{\"severity\":\"high\",\"_resourceid\":\"_resourceid\",\"ruleCategory\":\"security\",\"_docid\":\"_docid\",\"targetType\":\"ec2\",\"type\":\"issue\",\"issueDetails\":\"[{violationReason=Default target kernel criteria not maintained}]\",\"accountid\":\"123456789\",\"executionId\":\"executionId\",\"issueStatus\":\"open\",\"createdDate\":\"2018-07-03T01:00:50.820Z\",\"policyId\":\"cloud-kernel-compliance_version-1\",\"accountname\":\"account1\",\"tags.Environment\":\"env\",\"tags.Application\":\"appl\",\"pac_ds\":\"aws\",\"modifiedDate\":\"2018-07-09T23:00:50.788Z\",\"ruleId\":\"kernelcomplianceRule\",\"region\":\"region\",\"desc\":\"Target Kernerl Criteria not maintained\"}}]}}";
        ReflectionTestUtils.setField(complianceRepositoryImpl, "esUrl", "dummyEsURL");
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        complianceRepositoryImpl.getPolicyViolationDetailsByIssueId("test", "1234");
    }
    @Test
    public void getPatchableAssetsByApplicationTest() throws Exception {
        Map<String, Long> patchableassetsByApp = new HashMap<>();
        patchableassetsByApp.put("app1", 200l);

       when( elasticSearchRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(patchableassetsByApp);
        complianceRepositoryImpl.getPatchableAssetsByApplication("test", null, "ec2");
        complianceRepositoryImpl.getPatchableAssetsByApplication("test", "app1", "ec2");
        complianceRepositoryImpl.getPatchableAssetsByApplication("test", null, ONPREMSERVER);
        complianceRepositoryImpl.getPatchableAssetsByApplication("test", null, "s3");
    }

    @SuppressWarnings("unchecked")
    public void exemptAndUpdateMultipleIssueDetailsTest() throws Exception {
        List<Map<String, Object>> issueDetails = new ArrayList<>();
        Map<String, Object> issueDetailMap = new HashMap<>();
        issueDetailMap.put("resourceType", "ec2");
        issueDetailMap.put("status", "open");
        issueDetailMap.put("severity", "high");
        issueDetailMap.put(PAC_DS, AWS);
        issueDetailMap.put(PAC_DS, AWS);
        issueDetailMap.put(TYPE, "issue");
        issueDetailMap.put(ES_DOC_ROUTING_KEY, "12345");
        issueDetailMap.put(ES_DOC_PARENT_KEY, "ec2");
        issueDetailMap.put(ES_DOC_ID_KEY, "678");
        issueDetails.add(issueDetailMap);

        IssuesException issueException = new IssuesException();
        issueException.setExceptionReason("exempted");
        List<String> issueIds = new ArrayList<>();
        issueIds.add("1234");
        issueException.setIssueIds(issueIds);
        when(elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(issueDetails);
        when(elasticSearchRepository.updatePartialDataToES(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyObject())).thenReturn(true);
        when(elasticSearchRepository.saveExceptionDataToES(anyString(), anyString(), anyMap())).thenReturn(true);
        assertTrue(complianceRepositoryImpl.exemptAndUpdateMultipleIssueDetails(issueException).getStatus().equals("Success"));

    }

    public void revokeAndUpdateIssueMultipleDetailsTest() throws Exception {
        List<Map<String, Object>> issueDetails = new ArrayList<>();
        Map<String, Object> issueDetailMap = new HashMap<>();
        issueDetailMap.put("resourceType", "ec2");
        issueDetailMap.put("status", "open");
        issueDetailMap.put("severity", "high");
        issueDetailMap.put(PAC_DS, AWS);
        issueDetailMap.put(PAC_DS, AWS);
        issueDetailMap.put(TYPE, "issue");
        issueDetailMap.put(ES_DOC_ROUTING_KEY, "12345");
        issueDetailMap.put(ES_DOC_PARENT_KEY, "ec2");
        issueDetailMap.put(ES_DOC_ID_KEY, "678");
        issueDetails.add(issueDetailMap);

        when(elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(issueDetails);
        when(elasticSearchRepository.updatePartialDataToES(anyString(), anyString(), anyString(), anyString(),
                        anyString(), anyObject())).thenReturn(true);
        ReflectionTestUtils.setField(complianceRepositoryImpl, "esUrl", "dummyEsURL");

        List<String> issueIds = new ArrayList<>();
        issueIds.add("1234");
        assertTrue(complianceRepositoryImpl.revokeAndUpdateMultipleIssueDetails(issueIds).getStatus().equals("Success"));
    }

   /* @Test
    public void getExemptedIssuesForTaggingRuleTest() throws Exception {
        String response = "{\"took\":469,\"timed_out\":false,\"_shards\":{\"count\":176,\"successful\":176,\"failed\":0},\"hits\":{\"total\":115456,\"max_score\":0,\"hits\":[]},\"aggregations\":{\"NAME\":{\"doc_count_error_upper_bound\":691,\"sum_other_doc_count\":34323,\"buckets\":["
        		+ "{\"key\":\"key\",\"doc_count\":6258},{\"key\":\"key\",\"doc_count\":3339}]}}}";
        ReflectionTestUtils.setField(complianceRepositoryImpl, "esUrl", "dummyEsURL");
        ReflectionTestUtils.setField(complianceRepositoryImpl, "mandatoryTags", "mandatoryTags");
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        complianceRepositoryImpl.getExemptedIssuesForTaggingRule(CommonTestUtil.getRequest(), "tagging-rule", "app1", "targetType");
        complianceRepositoryImpl.getExemptedIssuesForTaggingRule(CommonTestUtil.getRequest(), "tagging-rule", "app1", null);
        complianceRepositoryImpl.getExemptedIssuesForTaggingRule(CommonTestUtil.getWithoutSizeRequest(), "tagging-rule", "app1", null);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new DataException());
        assertThatThrownBy(
                () -> complianceRepositoryImpl.getExemptedIssuesForTaggingRule(CommonTestUtil.getRequest(), "tagging-rule", "app1",
                        null)).isInstanceOf(DataException.class);
    }

    @Test
    public void getExemptedResourceDetailsTest() throws Exception {
        when(elasticSearchRepository.getSortedDataFromES(anyString(),anyString(),anyObject(),anyObject(),anyObject(),anyObject(),anyObject(),anyObject())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(complianceRepositoryImpl.getExemptedResourceDetails("test","test"),
                is(notNullValue()));

        when(elasticSearchRepository.getSortedDataFromES(anyString(),anyString(),anyObject(),anyObject(),anyObject(),anyObject(),anyObject(),anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(
                () -> complianceRepositoryImpl.getExemptedResourceDetails("tagging-rule", "app1"))
                .isInstanceOf(DataException.class);
    }*/

}
