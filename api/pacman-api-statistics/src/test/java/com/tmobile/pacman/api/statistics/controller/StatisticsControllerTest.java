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
package com.tmobile.pacman.api.statistics.controller;

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
import com.tmobile.pacman.api.statistics.service.StatisticsService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class })
public class StatisticsControllerTest {

    @Mock
    private StatisticsService statsService;
    
    /** The statistics service. */
    private StatisticsController statisticsController = new StatisticsController();
    
    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        ReflectionTestUtils.setField(statisticsController, "statsService", statsService);
    }
    
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
        when(statsService.getCPUUtilization(anyString())).thenReturn(utlisationList);
        
        ResponseEntity<Object> response =  statisticsController.getCPUUtilization("aws-all");
      
        assert(response.getStatusCodeValue()==200);
    }
    
    @Test
    public void getCPUUtilizationExceptionTest() throws Exception {
       
        when(statsService.getCPUUtilization(anyString())).thenThrow( new ServiceException());
        ResponseEntity<Object> response =  statisticsController.getCPUUtilization("aws-all");
        assert(response.getStatusCodeValue()!=200);
    }
    
    @Test
    public void getCPUUtilizationNoAGTest() throws Exception {
       
        when(statsService.getCPUUtilization(anyString())).thenThrow( new ServiceException());
        ResponseEntity<Object> response =  statisticsController.getCPUUtilization(null);
        assert(response.getStatusCodeValue()!=200);
    }
    
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
        when(statsService.getNetworkUtilization(anyString())).thenReturn(utlisationList);
        
        ResponseEntity<Object> response =  statisticsController.getNetworkUtilization("aws-all");
      
        assert(response.getStatusCodeValue()==200);
    }
    
    @Test
    public void getNetworkUtilizationExceptionTest() throws Exception {
       
        when(statsService.getNetworkUtilization(anyString())).thenThrow( new ServiceException());
        ResponseEntity<Object> response =  statisticsController.getNetworkUtilization("aws-all");
        assert(response.getStatusCodeValue()!=200);
    }
    
    @Test
    public void getNetworkUtilizationNoAGTest() throws Exception {
       
        when(statsService.getNetworkUtilization(anyString())).thenThrow( new ServiceException());
        ResponseEntity<Object> response =  statisticsController.getNetworkUtilization(null);
        assert(response.getStatusCodeValue()!=200);
    }
    
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
        when(statsService.getDiskUtilization(anyString())).thenReturn(utlisationList);
        
        ResponseEntity<Object> response =  statisticsController.getDiskUtilization("aws-all");
      
        assert(response.getStatusCodeValue()==200);
    }
    
    @Test
    public void getDiskUtilizationExceptionTest() throws Exception {
        
        when(statsService.getDiskUtilization(anyString())).thenThrow( new ServiceException());
        ResponseEntity<Object> response =  statisticsController.getDiskUtilization("aws-all");
        assert(response.getStatusCodeValue()!=200);
    }
    
    @Test
    public void getDiskUtilizationNoAGTest() throws Exception {
        
        when(statsService.getDiskUtilization(anyString())).thenThrow( new ServiceException());
        ResponseEntity<Object> response =  statisticsController.getDiskUtilization(null);
        assert(response.getStatusCodeValue()!=200);
    }
    
    @Test
    public void getStatsDetailsTest() throws Exception {
        
        when(statsService.getStats()).thenReturn(new ArrayList<Map<String,Object>>());
        ResponseEntity<Object> response =  statisticsController.getStatsDetails();
      
        assert(response.getStatusCodeValue()==200);
    }
    
    @Test
    public void getStatsDetailsExceptionTest() throws Exception {
        
        when(statsService.getStats()).thenThrow( new ServiceException());
        ResponseEntity<Object> response =  statisticsController.getStatsDetails();
        assert(response.getStatusCodeValue()!=200);
    }
    
    
    @Test
    public void getAutofixStatsTest() throws Exception {
        
        when(statsService.getAutofixStats()).thenReturn(new ArrayList<Map<String,Object>>());
        ResponseEntity<Object> response =  statisticsController.getAutofixStats();
      
        assert(response.getStatusCodeValue()==200);
    }
    
    @Test
    public void getAutofixStatsExceptionTest() throws Exception {
        
        when(statsService.getAutofixStats()).thenThrow( new ServiceException());
        ResponseEntity<Object> response =  statisticsController.getAutofixStats();
        assert(response.getStatusCodeValue()!=200);
    }
    
}
