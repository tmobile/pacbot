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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.anyObject;
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
import org.springframework.jdbc.core.JdbcTemplate;

import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
@RunWith(PowerMockRunner.class)
public class NotificationServiceImplTest {
    @Mock
    ElasticSearchRepository elasticSearchRepository;

    @Mock
    JdbcTemplate jdbcTemplate;
    @InjectMocks
    NotificationServiceImpl notificationImpl;

    @Test
    public void getAllAssetGroupOwnerEmailDetailsTest(){
        List<Map<String,Object>> agOwnerDetails=new ArrayList<>();
        Map<String,Object>ownerDetails=new HashMap<>();
        ownerDetails.put("ownerName", "Matt");
        ownerDetails.put("assetGroup", "ag1");
        ownerDetails.put("ownerEmail", "Matt@testmail.com");
        agOwnerDetails.add(ownerDetails);
        when(jdbcTemplate.queryForList(anyString())).thenReturn(agOwnerDetails);
        notificationImpl.getAllAssetGroupOwnerEmailDetails();
    }
    @Test
    public void getApiRolesTest(){
        List<Map<String, Object>>apiroles=new ArrayList<>();
        Map<String,Object>roleDetails =new HashMap<>();
        roleDetails.put("roles", "admin");
        roleDetails.put("urls", "/test");
        apiroles.add(roleDetails);
       when(jdbcTemplate.queryForList(anyString())).thenReturn(apiroles);
       notificationImpl.getApiRoles("test", "dev");

    }
    @Test
    public void getDeviceDetailsTest()throws Exception{
        List<Map<String,Object>> response=new ArrayList<>();
        List<Map<String,Object>> emptyResponse=null;
        Map<String,Object>deviceDetails = new HashMap<>();
        deviceDetails.put("_id", "amz1123444");
        deviceDetails.put("name", "Nicholas Criss");
        deviceDetails.put("callme", "Nick");
        deviceDetails.put("email", "nick@testemail.com");
        response.add(deviceDetails);
        when(elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(response);
        notificationImpl.getDeviceDetails("1");
        when(elasticSearchRepository.getSortedDataFromES(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(emptyResponse);
        assertThatThrownBy(
                () ->   notificationImpl.getDeviceDetails("1")).isInstanceOf(Exception.class);


    }
    @Test
    public void subscribeDigestMailTest(){

        when(jdbcTemplate.update(anyString(), anyString())).thenReturn(1);
        notificationImpl. subscribeDigestMail("test@mail.com");
        when(jdbcTemplate.update(anyString(), anyString())).thenReturn(2);
        notificationImpl. subscribeDigestMail("test@mail.com");

    }
    @Test
    public void unsubscribeDigestMailTest(){
      //  when(jdbcTemplate.update(anyString(), anyObject())).thenReturn(1);
       // notificationImpl. subscribeDigestMail("test@mail.com");
        when(jdbcTemplate.update(anyString(), anyString())).thenReturn(2);
        notificationImpl. unsubscribeDigestMail("test@mail.com");
    }
}
