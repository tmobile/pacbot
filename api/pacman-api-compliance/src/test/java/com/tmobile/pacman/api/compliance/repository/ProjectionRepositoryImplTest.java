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
  Modified Date: Jun 28, 2018

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
import com.tmobile.pacman.api.compliance.domain.ProjectionRequest;
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class ProjectionRepositoryImplTest {
    @InjectMocks
    ProjectionRepositoryImpl projectionRepositoryImpl;
    @Mock
    ElasticSearchRepository esRepository;

    @Mock
    PacmanRdsRepository rdsRepository;

    @Test
    public void intTest() {
        projectionRepositoryImpl.init();
    }

    @Test
    public void updateProjectionByTargetTypeTest() throws Exception {
        List<LocalDate>numberofWeeks=projectionRepositoryImpl.getListOfLastWeekDateOfQuarter();

        ProjectionRequest projectionRequest = new ProjectionRequest();
        projectionRequest.setYear(2018);
        projectionRequest.setQuarter(2);
        List<Map<String, Object>> projectionByweeksList = new ArrayList<>();
        Map<String, Object> projectionByweek = new HashMap<>();
        for(int i=0;i<numberofWeeks.size();i++)
        {
            projectionByweek.put("week",i);
            projectionByweek.put("projection", 100);
            projectionByweeksList.add(projectionByweek);
        }
        projectionRequest.setProjectionByWeek(projectionByweeksList);
        int[] count = { 1 };
        when(rdsRepository.executeQuery(anyString())).thenReturn(count);
        //Test update projections
        projectionRepositoryImpl.updateProjectionByTargetType(projectionRequest);
        //test update projections into DB Fails
        int[] counttemp = { 0 };
       when(rdsRepository.executeQuery(anyString())).thenReturn(counttemp);
       projectionRepositoryImpl.updateProjectionByTargetType(projectionRequest);
       //test projection & week different names
       ProjectionRequest projectionRequestTemp = new ProjectionRequest();
       List<Map<String, Object>> projectionByweeksListTemp = new ArrayList<>();
       for(int i=0;i<numberofWeeks.size();i++)
       {
           projectionByweek.put("weekdummy",i);
           projectionByweek.put("projectiondmmy", 100);
           projectionByweeksListTemp.add(projectionByweek);
       }

       projectionRequestTemp.setProjectionByWeek(projectionByweeksListTemp);
       projectionRepositoryImpl.updateProjectionByTargetType(projectionRequestTemp);
       //test update projections fails when we pass more projections of quarter than expected
       projectionByweek.put("week",100);
       projectionByweek.put("projection", 100);
       projectionByweeksList.add(projectionByweek);
              assertThatThrownBy(() ->projectionRepositoryImpl.updateProjectionByTargetType(projectionRequest)).isInstanceOf(
               DataException.class);



    }

    @Test
    public void getProjectionDetailsFromDbTest() throws Exception {
        List<Map<String, Object>> projectionsByWeek = new ArrayList<>();
        Map<String, Object> projections = new HashMap<>();
        projections.put("week", 1);
        projections.put("projection", 100l);
        projections.put("week", 2);
        projections.put("projection", 400l);
        projectionsByWeek.add(projections);
        when(rdsRepository.getDataFromPacman(anyString())).thenReturn(projectionsByWeek);
        projectionRepositoryImpl.getProjectionDetailsFromDb("onpremserver", 2018, 2);
    }

    @Test
    public void getTotalAssetCountByTargetTypeTest() throws Exception {
        // test method returning asset count for Target Type
        when(
                esRepository.getTotalDocumentCountForIndexAndType(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyString(), anyObject())).thenReturn(100l);
        projectionRepositoryImpl.getTotalAssetCountByTargetType("ec2");
        // test method returning Data Exception
        when(
                esRepository.getTotalDocumentCountForIndexAndType(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyString(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> projectionRepositoryImpl.getTotalAssetCountByTargetType("ec2")).isInstanceOf(
                DataException.class);
    }

    @Test
    public void getListOfLastWeekDateOfQuarterTest() {
        List<LocalDate> lastdaysweekofQuarter = projectionRepositoryImpl.getListOfLastWeekDateOfQuarter();
        assertTrue(lastdaysweekofQuarter.size() > 0);
    }


    @Test
    public void getWeekNoByDateTest() {
        LocalDate date = LocalDate.now();
         projectionRepositoryImpl.getWeekNoByDate(date);


    }
    @Test
    public void getPatchingSnapshotTest()throws Exception {

      String response = "{\"took\":134,\"timed_out\":false,\"_shards\":{\"total\":5,\"successful\":5,\"failed\":0},\"hits\":{\"total\":39703,\"max_score\":1,\"hits\":[{\"_index\":\"assetgroup_stats\",\"_type\":\"patching\",\"_id\":\"EAB576835F943FE515EE98EC4CED313A\",\"_score\":1,\"_source\":{\"date\":\"2018-01-11\",\"unpatched_instances\":36,\"ag\":\"adapt\",\"@id\":\"EAB576835F943FE515EE98EC4CED313A\",\"patched_instances\":5,\"total_instances\":41,\"patching_percentage\":12}}]}}";
      mockStatic(PacHttpUtils.class);
      ReflectionTestUtils.setField(projectionRepositoryImpl, "esUrl", "dummyEsURL");
      when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
      projectionRepositoryImpl.getPatchingSnapshot("aws-all");
      //test data exception
      when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new RuntimeException());
      assertThatThrownBy(() -> projectionRepositoryImpl.getPatchingSnapshot("aws-all")).isInstanceOf(
              DataException.class);

    }
    @Test
    public void getUnPatchedDetailsByApplicationTest() throws Exception {
        Map<String, Long> unpatchedAssetCountApp = new HashMap<>();
        unpatchedAssetCountApp.put("app1", 100l);
        unpatchedAssetCountApp.put("app2", 150l);
        // test method returning un-patched asset count by Application
        when(
                esRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(unpatchedAssetCountApp);
        projectionRepositoryImpl.getUnPatchedDetailsByApplication("aws-all", "ec2");
        projectionRepositoryImpl.getUnPatchedDetailsByApplication("aws-all", "s3");
        projectionRepositoryImpl.getUnPatchedDetailsByApplication("aws-all", "onpremserver");
        when(
                esRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyInt(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> projectionRepositoryImpl.getUnPatchedDetailsByApplication("aws-all", "ec2"))
                .isInstanceOf(DataException.class);

    }

    @Test
    public void getAssetDetailsByApplicationTest() throws Exception {
        Map<String, Long> assetcountByApp = new HashMap<>();
        assetcountByApp.put("app1", 120l);
        assetcountByApp.put("app2", 150l);
        // test method returning asset count by Application
        when(
                esRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyInt(), anyObject())).thenReturn(assetcountByApp);
        projectionRepositoryImpl.getAssetDetailsByApplication("aws-all", "ec2");
        // test asset count when targetType is onpremserver
        projectionRepositoryImpl.getAssetDetailsByApplication("aws-all", "onpremserver");
        // test method returning Data Exception
        when(
                esRepository.getTotalDistributionForIndexAndType(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyObject(), anyInt(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> projectionRepositoryImpl.getAssetDetailsByApplication("aws-all", "ec2")).isInstanceOf(
                DataException.class);
    }

    @Test
    public void getAssetCountByAgTest() throws Exception {
        // test method returning asset count
        when(
                esRepository.getTotalDocumentCountForIndexAndType(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyString(), anyObject())).thenReturn(100l);
        projectionRepositoryImpl.getAssetCountByAg("aws-all", "ec2");
        // test method returning Data Exception
        when(
                esRepository.getTotalDocumentCountForIndexAndType(anyString(), anyString(), anyObject(), anyObject(),
                        anyObject(), anyString(), anyObject())).thenThrow(new RuntimeException());
        assertThatThrownBy(() -> projectionRepositoryImpl.getAssetCountByAg("aws-all", "ec2")).isInstanceOf(
                DataException.class);
    }

    @Test
    public void getAppsDetailsTest() throws Exception {
        List<Map<String, Object>> appdetails = new ArrayList<>();
        Map<String, Object> apps = new HashMap<>();
        apps.put("appTag", "app1");
        apps.put("director", "Mark");
        apps.put("executiveSponsor", "Matt");
        appdetails.add(apps);
        // test method returning apps details
        when(
                esRepository.getSortedDataFromESBySize(anyString(), anyString(), anyObject(), anyObject(), anyObject(),
                        anyObject(), anyInt(), anyInt(), anyString(), anyObject(), anyObject())).thenReturn(appdetails);
        projectionRepositoryImpl.getAppsDetails("cloud");
        // test method returning Data Exception
        when(
                esRepository.getSortedDataFromESBySize(anyString(), anyString(), anyObject(), anyObject(), anyObject(),
                        anyObject(), anyInt(), anyInt(), anyString(), anyObject(), anyObject())).thenThrow(
                new RuntimeException());
        assertThatThrownBy(() -> projectionRepositoryImpl.getAppsDetails("cloud")).isInstanceOf(DataException.class);
    }
}
