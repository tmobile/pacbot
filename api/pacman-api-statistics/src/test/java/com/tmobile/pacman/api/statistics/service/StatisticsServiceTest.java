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

package com.tmobile.pacman.api.statistics.service;

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.HeimdallElasticSearchRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
import com.tmobile.pacman.api.statistics.client.AssetServiceClient;
import com.tmobile.pacman.api.statistics.client.ComplianceServiceClient;
import com.tmobile.pacman.api.statistics.repository.StatisticsRepository;

/**
 * The Class StatisticsServiceTest.
 *
 * @author kkumar
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class })
public class StatisticsServiceTest {

    /** The elastic search repository. */
    @Mock
    private ElasticSearchRepository elasticSearchRepository;

    /** The repository. */
    @Mock
    private StatisticsRepository repository;

    /** The heimdall elastic search repository. */
    @Mock
    private HeimdallElasticSearchRepository heimdallElasticSearchRepository;

    /** The asset client. */
    @Mock
    private AssetServiceClient assetClient;

    /** The compliance client. */
    @Mock
    private ComplianceServiceClient complianceClient;


    /** The statistics service. */
    private StatisticsService statisticsService = new StatisticsServiceImpl();

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        ReflectionTestUtils.setField(statisticsService, "assetClient", assetClient);
        ReflectionTestUtils.setField(statisticsService, "elasticSearchRepository", elasticSearchRepository);
        ReflectionTestUtils.setField(statisticsService, "repository", repository);
        ReflectionTestUtils.setField(statisticsService, "complianceClient", complianceClient);
        ReflectionTestUtils.setField(statisticsService, "heimdallElasticSearchRepository", heimdallElasticSearchRepository);
    }

    /**
     * Gets the stats test.
     *
     * @return the stats test
     * @throws Exception the exception
     */
    @Test
    public void getStatsTest() throws Exception {

        String countJson= "{\"data\":{\"ag\":\"aws-all\",\"assetcount\":[{\"count\":1930,\"type\":\"subnet\"},{\"count\":5586,\"type\":\"stack\"},{\"count\":689,\"type\":\"asgpolicy\"},{\"count\":3652,\"type\":\"rdssnapshot\"},{\"count\":78,\"type\":\"rdscluster\"},{\"count\":1243,\"type\":\"cert\"}]},\"message\":\"success\"}";

        when(assetClient.getTypeCounts(anyString(), anyString(), anyString())).thenReturn(new Gson().fromJson(countJson, new TypeToken<Map<String, Object>>(){}.getType()));

        String response = "[{\"key_as_string\":%s,\"key\":15232391200000,\"doc_count\":176774115}]";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String prevDate = dateFormat.format(new Date().getTime() - 1000*60*60*24);
        response = String.format(response, prevDate);

        when(heimdallElasticSearchRepository.getEventsProcessed()).thenReturn(new JsonParser().parse(response).getAsJsonArray());

        String issueDistribution = "{\"data\":{\"distribution\":{\"total_issues\":108317,\"distribution_by_severity\":{\"high\":31943,\"critical\":2047,\"low\":70360,\"medium\":3967}}},\"message\":\"success\"}";
        when(complianceClient.getDistributionAsJson(anyString(),anyString())).thenReturn(issueDistribution);

        String accountResponse = "[{\"key\":\"abc\",\"doc_count\":944295},{\"key\":\"xyz\",\"doc_count\":864199},{\"key\":\"123\",\"doc_count\":728022},{\"key\":\"789\",\"doc_count\":429529},{\"key\":\"sshsh\",\"doc_count\":272781}]";
        when(repository.getNumberOfAccounts()).thenReturn(new JsonParser().parse(accountResponse).getAsJsonArray());

        when(repository.getTargetTypeForAG(anyString(),anyString())).thenReturn("ec2,s3,api");

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

        when(repository.getRuleIdWithTargetTypeQuery(anyString())).thenReturn(ruleTargetList);

        when(repository.getNumberOfPoliciesEvaluated()).thenReturn("105");

        String overallCompResponse ="{\"data\":{\"distribution\":{\"tagging\":59,\"security\":88,\"costOptimization\":61,\"governance\":82,\"overall\":72}},\"message\":\"success\"}";
        when(complianceClient.getOverallCompliance(anyString(),anyString())).thenReturn(new Gson().fromJson(overallCompResponse, new TypeToken<Map<String, Object>>(){}.getType()));

        List<Map<String, Object>> stats = statisticsService.getStats();

        assert(!stats.isEmpty());
        assert("13178".equals(stats.get(0).get("totalNumberOfAssets").toString()));

    }

    /**
     * Gets the autofix stats test.
     *
     * @return the autofix stats test
     * @throws Exception the exception
     */
    @Test
    public void getAutofixStatsTest() throws Exception {
        List<Map<String,Object>> autoStatsList = new ArrayList<>();
        Map<String,Object> autoStatsMap = new HashMap<>();
        List<Map<String,Object>> docList = new ArrayList<>();
        Map<String,Object> docMap = new HashMap<>();
        docMap.put("key", "12");
        docMap.put("doc_count", "12");
        docList.add(docMap);
        Map<String,Object> bucketStatsMap = new HashMap<>();
        bucketStatsMap.put("buckets", docList);
        autoStatsMap.put("RESOURCEID", bucketStatsMap);
        autoStatsList.add(autoStatsMap);
        when(repository.getAutofixActionCountByRule()).thenReturn(autoStatsList);

        List<Map<String, Object>> stats = statisticsService.getAutofixStats();
        assert(!stats.isEmpty());
    }

    /**
     * Gets the autofix stats test exception.
     *
     * @return the autofix stats test exception
     * @throws Exception the exception
     */
    @Test(expected=ServiceException.class)
    public void getAutofixStatsTestException() throws Exception {
        when(repository.getAutofixActionCountByRule()).thenThrow(new DataException());
        statisticsService.getAutofixStats();
    }

    /**
     * Gets the CPU utilization test.
     *
     * @return the CPU utilization test
     * @throws Exception the exception
     */
    @Test
    public void getCPUUtilizationTest() throws Exception {
        LocalDate currDate = LocalDate.now();
        Map<String,Object> utilisation;
        List< Map<String,Object>> utlisationList = new ArrayList<>();
        for(int i=0;i<7;i++){
            LocalDate temp = currDate.minusDays(i);
            String date = temp.format(DateTimeFormatter.ISO_DATE);
            utilisation = new HashMap<>();
            utilisation.put("date",date);
            utilisation.put("cpu-utilization",Math.random() * 75 + 10);
            utlisationList.add(utilisation);
        }
        System.out.println(utlisationList);
        when(elasticSearchRepository.getUtilizationByAssetGroup(anyString())).thenReturn(utlisationList);

        List<Map<String, Object>> _utlisationList = statisticsService.getCPUUtilization(anyString());
        System.out.println(_utlisationList);
        assert(_utlisationList.size()==utlisationList.size());
    }

    /**
     * Gets the network utilization test.
     *
     * @return the network utilization test
     * @throws Exception the exception
     */
    @Test
    public void getNetworkUtilizationTest() throws Exception {
        LocalDate currDate = LocalDate.now();
        Map<String,Object> utilisation;
        List< Map<String,Object>> utlisationList = new ArrayList<>();
        for(int i=0;i<7;i++){
            LocalDate temp = currDate.minusDays(i);
            String date = temp.format(DateTimeFormatter.ISO_DATE);
            utilisation = new HashMap<>();
            utilisation.put("date",date);
            utilisation.put("networkIn",Math.random() * 1000 + 26);
            utilisation.put("networkOut",Math.random() * 5000 + 12);
            utlisationList.add(utilisation);
        }
        System.out.println(utlisationList);
        when(elasticSearchRepository.getUtilizationByAssetGroup(anyString())).thenReturn(utlisationList);

        List<Map<String, Object>> _utlisationList = statisticsService.getNetworkUtilization(anyString());
        System.out.println(_utlisationList);
        assert(_utlisationList.size()==utlisationList.size());
    }

    /**
     * Gets the disk utilization test.
     *
     * @return the disk utilization test
     * @throws Exception the exception
     */
    @Test
    public void getDiskUtilizationTest() throws Exception {
        LocalDate currDate = LocalDate.now();
        Map<String,Object> utilisation;
        List< Map<String,Object>> utlisationList = new ArrayList<>();
        for(int i=0;i<7;i++){
            LocalDate temp = currDate.minusDays(i);
            String date = temp.format(DateTimeFormatter.ISO_DATE);
            utilisation = new HashMap<>();
            utilisation.put("date",date);
            utilisation.put("diskReadinBytes",Math.random() * 1000 + 26);
            utilisation.put("diskWriteinBytes",Math.random() * 5000 + 12);
            utlisationList.add(utilisation);
        }
        System.out.println(utlisationList);
        when(elasticSearchRepository.getUtilizationByAssetGroup(anyString())).thenReturn(utlisationList);

        List<Map<String, Object>> _utlisationList = statisticsService.getDiskUtilization(anyString());
        System.out.println(_utlisationList);
        assert(_utlisationList.size()==utlisationList.size());
    }

}
