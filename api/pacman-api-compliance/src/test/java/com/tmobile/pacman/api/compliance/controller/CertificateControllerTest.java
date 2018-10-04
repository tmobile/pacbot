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
package com.tmobile.pacman.api.compliance.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.service.CertificateService;

@RunWith(PowerMockRunner.class)
public class CertificateControllerTest {

    @InjectMocks
    CertificateController certificateController;
    
    @Mock
    CertificateService certificateService;
    
    @Test
    public void getCerticatesExpiryByApplicationTest() throws Exception {
     
        when(certificateService.getCerticatesExpiryByApplication(anyString())).thenReturn(new HashMap<>());
        
        ResponseEntity<Object> responseObj = certificateController.getCerticatesExpiryByApplication("ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
    }
    
    @Test
    public void getCerticatesExpiryByApplicationTest_NoAg() throws Exception {
     
        ResponseEntity<Object> responseObj = certificateController.getCerticatesExpiryByApplication("");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getCerticatesExpiryByApplicationTest_Exception() throws Exception {
     
        when(certificateService.getCerticatesExpiryByApplication(anyString())).thenThrow(new ServiceException());
        
        ResponseEntity<Object> responseObj = certificateController.getCerticatesExpiryByApplication("ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getCertificatesDetailsTest() throws Exception {
     
        Request request = new Request();
        request.setAg("ag");
        
        List<Map<String,Object>> certDetails = new ArrayList<>();
        
        when(certificateService.getCerticatesDetails(anyString(), anyString(), anyObject())).thenReturn(certDetails);
        assertTrue(certificateController.getCertificatesDetails(request).getStatusCode() == HttpStatus.OK);
        
        request.setFrom(0);
        certDetails.add(new HashMap<>());
        certDetails.add(new HashMap<>());
        when(certificateService.getCerticatesDetails(anyString(), anyString(), anyObject())).thenReturn(certDetails);
        assertTrue(certificateController.getCertificatesDetails(request).getStatusCode() == HttpStatus.OK);
        
        request.setSize(1);
        request.setFilter(new HashMap<>());
        when(certificateService.getCerticatesDetails(anyString(), anyString(), anyObject())).thenReturn(certDetails);
        assertTrue(certificateController.getCertificatesDetails(request).getStatusCode() == HttpStatus.OK);
        
        request.setSize(3);
        when(certificateService.getCerticatesDetails(anyString(), anyString(), anyObject())).thenReturn(certDetails);
        assertTrue(certificateController.getCertificatesDetails(request).getStatusCode() == HttpStatus.OK);
    }
    
    @Test
    public void getCertificatesDetailsTest_Failure() throws Exception {
     
        List<Map<String,Object>> certDetails = new ArrayList<>();
        
        Request request = new Request();
        assertTrue(certificateController.getCertificatesDetails(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        request.setAg("ag");
        request.setFrom(-1);
        assertTrue(certificateController.getCertificatesDetails(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        certDetails.add(new HashMap<>());
        request.setFrom(2);
        when(certificateService.getCerticatesDetails(anyString(), anyString(), anyObject())).thenReturn(certDetails);
        assertTrue(certificateController.getCertificatesDetails(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getCertificatesDetailsTest_Exception() throws Exception {
     
        Request request = new Request();
        when(certificateService.getCerticatesDetails(anyString(), anyString(), anyObject())).thenThrow(new ServiceException());
        
        assertTrue(certificateController.getCertificatesDetails(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getCertificatesSummaryTest() throws Exception {
     
        when(certificateService.getCerticatesSummary(anyString())).thenReturn(new HashMap<>());
        
        ResponseEntity<Object> responseObj = certificateController.getCertificatesSummary("ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.OK);
    }
    
    @Test
    public void getCertificatesSummaryTest_NoAg() throws Exception {
     
        ResponseEntity<Object> responseObj = certificateController.getCertificatesSummary("");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getCertificatesSummaryTest_Exception() throws Exception {
     
        when(certificateService.getCerticatesSummary(anyString())).thenThrow(new ServiceException());
        
        ResponseEntity<Object> responseObj = certificateController.getCertificatesSummary("ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
}
