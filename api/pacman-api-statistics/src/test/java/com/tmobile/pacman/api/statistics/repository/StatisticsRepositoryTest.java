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
  Author :kkumar
  Modified Date: May 15, 2018

**/

package com.tmobile.pacman.api.statistics.repository;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.gson.JsonArray;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
import com.tmobile.pacman.api.statistics.client.AssetServiceClient;
import com.tmobile.pacman.api.statistics.domain.AssetApi;
import com.tmobile.pacman.api.statistics.domain.AssetApiData;
import com.tmobile.pacman.api.statistics.domain.AssetApiName;

// TODO: Auto-generated Javadoc
/**
 * The Class StatisticsRepositoryTest.
 *
 * @author kkumar
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class })
public class StatisticsRepositoryTest {

    /** The rdsepository. */
    @Mock
    private PacmanRdsRepository rdsepository;

    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    /** The asset service client. */
    @Mock
    private AssetServiceClient assetServiceClient;

    /** The statistics repository. */
    private StatisticsRepository statisticsRepository = new StatisticsRepositoryImpl();

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {

        ReflectionTestUtils.setField(statisticsRepository, "rdsepository", rdsepository);
        ReflectionTestUtils.setField(statisticsRepository, "assetServiceClient", assetServiceClient);
        ReflectionTestUtils.setField(statisticsRepository, "esUrl", "esUrl");
        ReflectionTestUtils.setField(statisticsRepository, "elasticSearchRepository", elasticSearchRepository);
    }

    /**
     * Gets the target type for AG test.
     *
     * @return the target type for AG test
     * @throws Exception the exception
     */
    @Test
    public void getTargetTypeForAGTest() throws Exception {

        AssetApi assetApi = new AssetApi();
        AssetApiData data = new AssetApiData();
        AssetApiName[] targettypes = new AssetApiName[5];
        for (int i = 0; i < 5; i++) {
            targettypes[i] = new AssetApiName();
        }
        targettypes[0].setType("ec2");
        targettypes[1].setType("s3");
        targettypes[2].setType("api");
        targettypes[3].setType("asg");
        targettypes[4].setType("redshift");
        data.setTargettypes(targettypes);
        assetApi.setData(data);

        when(assetServiceClient.getTargetTypeList(anyString(), anyString())).thenReturn(assetApi);

        String targetTypes = statisticsRepository.getTargetTypeForAG(anyString(), anyString());
        assert (!targetTypes.isEmpty());
    }

    /**
     * Gets the number of policies evaluated test.
     *
     * @return the number of policies evaluated test
     * @throws Exception the exception
     */
    @Test
    public void getNumberOfPoliciesEvaluatedTest() throws Exception {
        String response = "{\"aggregations\":{\"total_evals\":{\"value\":13133091}}}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

        String evaluations = statisticsRepository.getNumberOfPoliciesEvaluated();
        assert (!evaluations.isEmpty());
    }

    /**
     * Gets the total violations test.
     *
     * @return the total violations test
     * @throws Exception the exception
     */
    @Test
    public void getTotalViolationsTest() throws Exception {
        String response = "{\"aggregations\":{\"severity\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"low\",\"doc_count\":68782},{\"key\":\"high\",\"doc_count\":36569},{\"key\":\"medium\",\"doc_count\":4063},{\"key\":\"critical\",\"doc_count\":1839}]}}}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

        JsonArray violations = statisticsRepository.getTotalViolations();
        assert (violations.isJsonArray() && violations.size() > 0);
    }

    /**
     * Gets the autofix stats test.
     *
     * @return the autofix stats test
     * @throws Exception the exception
     */
    @Test
    public void getAutofixStatsTest() throws Exception {
        String response = "{\"aggregations\":{\"RULEID\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"DO_NOTHING\",\"doc_count\":2149},{\"key\":\"AUTOFIX_ACTION_TAG\",\"doc_count\":673},{\"key\":\"AUTOFIX_ACTION_EMAIL\",\"doc_count\":550},{\"key\":\"AUTOFIX_ACTION_BACKUP\",\"doc_count\":117},{\"key\":\"AUTOFIX_ACTION_FIX\",\"doc_count\":50}]}}}";
        when(elasticSearchRepository.getTotalDocumentCountForIndexAndType(anyString(), anyString(),anyObject(),anyObject(),anyObject(),anyString(),anyObject())).thenReturn(1233l);
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

        List<Map<String, Object>> autofixStats = statisticsRepository.getAutofixActionCountByRule();
        assert (!autofixStats.isEmpty());
    }

    /**
     * Gets the number of accounts test.
     *
     * @return the number of accounts test
     * @throws Exception the exception
     */
    @Test
    public void getNumberOfAccountsTest() throws Exception {
        String response = "{\"aggregations\":{\"accounts\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"abc\",\"doc_count\":944295},{\"key\":\"xyz\",\"doc_count\":864199},{\"key\":\"123\",\"doc_count\":728022},{\"key\":\"789\",\"doc_count\":429529},{\"key\":\"sshsh\",\"doc_count\":272781}]}}}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);

        JsonArray totalAccounts = statisticsRepository.getNumberOfAccounts();
        assert (totalAccounts.size() == 5);
    }

    /**
     * Gets the rule id with target type query test.
     *
     * @return the rule id with target type query test
     * @throws Exception the exception
     */
    @Test
    public void getRuleIdWithTargetTypeQueryTest() throws Exception {

        List<Map<String, Object>> ruleTargetList = new ArrayList<>();
        Map<String, Object> rule = new HashMap<>();
        rule.put("rule1", "ec2");
        ruleTargetList.add(rule);

        rule = new HashMap<>();
        rule.put("rule2", "ec2");
        ruleTargetList.add(rule);

        rule = new HashMap<>();
        rule.put("rule3", "ec2");
        ruleTargetList.add(rule);

        when(rdsepository.getDataFromPacman(anyString())).thenReturn(ruleTargetList);

        List<Map<String, Object>> rulInfo = statisticsRepository.getRuleIdWithTargetTypeQuery("ec2");
        assert (!rulInfo.isEmpty());

    }
}
