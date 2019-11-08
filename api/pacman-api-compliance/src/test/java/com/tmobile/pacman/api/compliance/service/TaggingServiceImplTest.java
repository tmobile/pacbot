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
package com.tmobile.pacman.api.compliance.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.UntaggedTargetTypeRequest;
import com.tmobile.pacman.api.compliance.repository.ComplianceRepository;
import com.tmobile.pacman.api.compliance.repository.TaggingRepository;

@RunWith(MockitoJUnitRunner.class)
public class TaggingServiceImplTest {

    @InjectMocks
    private TaggingServiceImpl taggingServiceImpl;

    @Mock
    private TaggingRepository repository;

    @Mock
    private ComplianceRepository complainceRepository;

    Request request = new Request();
    
    UntaggedTargetTypeRequest untaggedTargetTypeRequest = new UntaggedTargetTypeRequest();

    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void taggingByApplicationTest() throws Exception {
        String reponse = "{\"count\":1000,\"took\":9157,\"timed_out\":false,\"_shards\":{\"total\":176,\"successful\":176,\"failed\":0},\"hits\":{\"total\":280654,\"max_score\":0,\"hits\":[]},\"aggregations\":{\"NAME\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"snapshot\",\"doc_count\":22930},{\"key\":\"eni\",\"doc_count\":11927},{\"key\":\"sg\",\"doc_count\":5337},{\"key\":\"rdssnapshot\",\"doc_count\":3658},{\"key\":\"volume\",\"doc_count\":3581},{\"key\":\"stack\",\"doc_count\":3539},{\"key\":\"lambda\",\"doc_count\":2843},{\"key\":\"iamrole\",\"doc_count\":2768},{\"key\":\"s3\",\"doc_count\":1537},{\"key\":\"subnet\",\"doc_count\":1447},{\"key\":\"phd\",\"doc_count\":1221},{\"key\":\"launchconfig\",\"doc_count\":1192},{\"key\":\"iamuser\",\"doc_count\":1187},{\"key\":\"snstopic\",\"doc_count\":932},{\"key\":\"routetable\",\"doc_count\":727},{\"key\":\"asgpolicy\",\"doc_count\":691},{\"key\":\"networkacl\",\"doc_count\":617},{\"key\":\"corpdomain\",\"doc_count\":572},{\"key\":\"checks\",\"doc_count\":521},{\"key\":\"dhcpoption\",\"doc_count\":451},{\"key\":\"internetgateway\",\"doc_count\":435},{\"key\":\"vpc\",\"doc_count\":432},{\"key\":\"kms\",\"doc_count\":402},{\"key\":\"dynamodb\",\"doc_count\":385},{\"key\":\"ec2\",\"doc_count\":338},{\"key\":\"elasticip\",\"doc_count\":328},{\"key\":\"targetgroup\",\"doc_count\":300},{\"key\":\"virtualinterface\",\"doc_count\":286},{\"key\":\"peeringconnection\",\"doc_count\":269},{\"key\":\"api\",\"doc_count\":258},{\"key\":\"asg\",\"doc_count\":209},{\"key\":\"cloudfront\",\"doc_count\":175},{\"key\":\"classicelb\",\"doc_count\":159},{\"key\":\"rdsdb\",\"doc_count\":139},{\"key\":\"nat\",\"doc_count\":122},{\"key\":\"vpngateway\",\"doc_count\":88},{\"key\":\"rdscluster\",\"doc_count\":69},{\"key\":\"customergateway\",\"doc_count\":63},{\"key\":\"appelb\",\"doc_count\":51},{\"key\":\"vpnconnection\",\"doc_count\":51},{\"key\":\"efs\",\"doc_count\":38},{\"key\":\"elasticsearch\",\"doc_count\":38},{\"key\":\"wafdomain\",\"doc_count\":29},{\"key\":\"account\",\"doc_count\":25},{\"key\":\"emr\",\"doc_count\":20},{\"key\":\"directconnect\",\"doc_count\":4},{\"key\":\"redshift\",\"doc_count\":4}]}}}";
        String targetTypes = "'ec2','s3','volume'";
        Map<String, Long> tagMap = new HashMap<>();
        tagMap.put("ec2", 100l);
        tagMap.put("volume", 200l);
        tagMap.put("assets", 200l);
        when(complainceRepository.getTargetTypeForAG(anyString(), anyString()))
                .thenReturn(targetTypes);
        when( 
                elasticSearchRepository
                        .getTotalDocumentCountForIndexAndTypeWithMustNotTermsFilter(
                                anyString(), anyString(), anyObject(),
                                anyObject(), anyObject(), anyObject(),
                                anyObject(), anyObject(), anyObject()))
                .thenReturn(5000l);
        when( 
                elasticSearchRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyString(), anyInt(), anyObject()))
                .thenReturn(tagMap);
        when(repository.getTaggingByApplication(anyString(), anyString()))
                .thenReturn(reponse);
        assertThat(taggingServiceImpl.taggingByApplication("aws-all", "s3"),
                is(notNullValue()));

        assertThat(taggingServiceImpl.taggingByApplication("aws-all", ""),
                is(notNullValue()));
    }
    
    @Test
    public void getTaggingSummaryTest() throws Exception {
        Map<String, Long> tagMap = new HashMap<>();
        tagMap.put("ec2", 100l);
        tagMap.put("s3", 200l);
        tagMap.put("assets", 200l);
        Gson gson = new Gson();
        List<JsonObject> paramsList = new ArrayList<>();
        JsonObject paramsKeyValue = new JsonObject();
        paramsKeyValue.add("key",
                gson.fromJson("mandatoryTags", JsonElement.class));
        paramsKeyValue.add("value",
                gson.fromJson("Application", JsonElement.class));
        paramsList.add(paramsKeyValue);
        JsonObject ruleParam = new JsonObject();
        ruleParam.add("params",
                gson.fromJson(paramsList.toString(), JsonElement.class));

        Map<String, Object> mainRuleParams = new HashMap<>();
        mainRuleParams.put("ruleParams", ruleParam);

        List<Map<String, Object>> mainRuleParamsList = new ArrayList<>();
        mainRuleParamsList.add(mainRuleParams);
        when(complainceRepository.getTagging(anyString(), anyString()))
                .thenReturn(tagMap);
        when(repository.getRuleParamsFromDbByPolicyId(anyString())).thenReturn(
                mainRuleParamsList);
        assertThat(taggingServiceImpl.getTaggingSummary("test"),
                is(notNullValue())); 
          //test service Exception when it throws DataException
            when(complainceRepository.getTagging(anyString(),
                    anyString()))
            .thenThrow(new DataException());
            
            assertThatThrownBy(() -> taggingServiceImpl.getTaggingSummary("test"))
            .isInstanceOf(ServiceException.class);
    }

    @Test
    public void getUntaggingByTargetTypesTest() throws Exception {
        Map<String, String> filter = new HashMap<>();
        filter.put("targetType", "ec2");
        untaggedTargetTypeRequest.setAg("aws-all");
        untaggedTargetTypeRequest.setFilter(filter);
        Map<String, Object> targetTypesMap = new HashMap<>();
        targetTypesMap.put("targetType", "ec2");

        List<Map<String, Object>> maintargetTypesList = new ArrayList<>();
        maintargetTypesList.add(targetTypesMap);

        Map<String, Long> assetCountMap = new HashMap<>();
        assetCountMap.put("ec2", 100l);
        assetCountMap.put("s3", 200l);

        String response = "{\"took\":806,\"timed_out\":false,\"_shards\":{\"total\":176,\"successful\":176,\"failed\":0},\"hits\":{\"total\":45266,\"max_score\":9.880274,\"hits\":["
        		+ "{\"_index\":\"aws_ec2\",\"_type\":\"issue_ec2\",\"_id\":\"_id\",\"_score\":9.880274,\"_routing\":\"_routing\",\"_parent\":\"_parent\",\"_source\":{\"missingTags\":\"Role, Environment, Application, Stack\","
        		+ "\"_docid\":\"_docid\",\"type\":\"issue\",\"accountid\":\"accountid\",\"issueStatus\":\"open\",\"accountname\":\"accountname\","
        		+ "\"ruleId\":\"PacMan_TaggingRule_version-1_Ec2TaggingRule_ec2\",\"severity\":\"high\",\"_resourceid\":\"_resourceid\",\"ruleCategory\":\"tagging\","
        		+ "\"targetType\":\"ec2\",\"issueDetails\":\"[{violationReason\\u003dMandatory tags missed for ec2 target type!, tags_associated\\u003d{\\\"Role\\\":\\\"Not Found\\\",\\\"Environment\\\":\\\"Not Found\\\",\\\"Application\\\":\\\"Not Found\\\",\\\"Stack\\\":\\\"Not Found\\\"}}]\",\"Stack\":\"Not Found\",\"Role\":\"Not Found\","
        		+ "\"executionId\":\"executionId\",\"mandatoryTagsMissingFlg\":\"YES\",\"createdDate\":\"2018-06-22T21:01:01.603Z\",\"policyId\":\"PacMan_TaggingRule_version-1\",\"pac_ds\":\"aws\",\"modifiedDate\":\"2018-06-26T11:00:47.956Z\",\"Environment\":\"Not Found\",\"region\":\"region\",\"Application\":\"Not Found\",\"desc\":\"Missed tags for ec2 are Role, Environment, Application, Stack\"}}]},"
        		+ "\"aggregations\":{\"NAME\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":["
        		+ "{\"key\":\"key\",\"doc_count\":25032},{\"key\":\"key\",\"doc_count\":5589},"
        		+ "{\"key\":\"key\",\"doc_count\":1797},{\"key\":\"key\",\"doc_count\":9}]}}}";

        when(repository.getRuleTargetTypesFromDbByPolicyId(anyString()))
                .thenReturn(maintargetTypesList);
        ReflectionTestUtils.setField(taggingServiceImpl, "mandatoryTags",
                "Application,Environment");
        when(complainceRepository.getTotalAssetCount(anyString(), anyString(),anyString(),anyString()))
                .thenReturn(assetCountMap);
 
        when(repository.getUntaggedTargetTypeIssues(anyObject(), anyObject()))
                .thenReturn(response);

        assertThat(
                taggingServiceImpl
                        .getUntaggingByTargetTypes(untaggedTargetTypeRequest),
                is(notNullValue()));
        //test service Exception when it throws DataException
        when(repository.getRuleTargetTypesFromDbByPolicyId(anyString()))
        .thenThrow(new DataException());
  
        assertThatThrownBy(() -> taggingServiceImpl.getUntaggingByTargetTypes(untaggedTargetTypeRequest))
        .isInstanceOf(ServiceException.class);

      
    }
    @Test
    public void getUntaggingByTargetTypesTestExeptions() throws Exception {
        //test serviceException when targetTypes empty
        List<Map<String, Object>>  emptyList = new ArrayList<>();
        when(repository.getRuleTargetTypesFromDbByPolicyId(anyString()))
        .thenReturn(emptyList);
        ReflectionTestUtils.setField(taggingServiceImpl, "mandatoryTags",
                "Application,Environment");
        assertThatThrownBy(() -> taggingServiceImpl
                .getUntaggingByTargetTypes(untaggedTargetTypeRequest))
        .isInstanceOf(ServiceException.class);
    }

    @Test
    public void getUnTaggedAssetsByApplicationTest() throws Exception {
        request.setAg("aws-all1");
        Gson gson = new Gson();
        JsonArray array = new JsonArray();
        JsonObject tagsJson = new JsonObject(); 
        JsonObject oneTagsJson = new JsonObject();
        JsonObject twoTagsJson = new JsonObject();
        JsonObject threeTagsJson = new JsonObject();
        threeTagsJson.add("doc_count", gson.fromJson("165", JsonElement.class)); 
        twoTagsJson.add("Environment", threeTagsJson);
        oneTagsJson.add("buckets", twoTagsJson);
        tagsJson.add("key", gson.fromJson("My-TMO", JsonElement.class));
        tagsJson.add("doc_count", gson.fromJson("3066", JsonElement.class));
        tagsJson.add("tags", oneTagsJson);
        array.add(tagsJson);
        when(
                repository.getUntaggedIssuesByapplicationFromES(anyString(),
                        anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(array);

        assertThat(taggingServiceImpl.getUnTaggedAssetsByApplication(request),
                is(notNullValue()));
        
      //test service Exception when it throws DataException
        when(repository.getUntaggedIssuesByapplicationFromES(anyString(),
                anyString(), anyString(), anyInt(), anyInt()))
        .thenThrow(new DataException());
        
        assertThatThrownBy(() -> taggingServiceImpl.getUnTaggedAssetsByApplication(request))
        .isInstanceOf(ServiceException.class);
    }
    
    
}
