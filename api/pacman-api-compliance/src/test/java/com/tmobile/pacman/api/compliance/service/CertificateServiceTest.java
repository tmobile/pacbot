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
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.compliance.repository.CertificateRepository;

@RunWith(PowerMockRunner.class)
public class CertificateServiceTest {

    @InjectMocks
    private CertificateService certificateService;
    
    @Mock
    private CertificateRepository certificateRepository;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void getCerticatesExpiryByApplicationTest() throws Exception {
        when(certificateRepository.getCertificatesExpiryByApplication(anyString())).thenReturn(new HashMap<>());
        assertThat(certificateService.getCerticatesExpiryByApplication("ag"),
                is(notNullValue()));
    }
    
    @Test
    public void getCerticatesExpiryByApplicationTest_Exception() throws Exception {
        when(certificateRepository.getCertificatesExpiryByApplication(anyString())).thenThrow(new DataException());
        assertThatThrownBy(() ->certificateService.getCerticatesExpiryByApplication("ag"))
        .isInstanceOf(ServiceException.class);
    }
    
    @Test
    public void getCerticatesDetailsTest() throws Exception {
        when(certificateRepository.getCertificatesDetails(anyString(), anyString(), anyObject())).thenReturn(new ArrayList<>());
        assertThat(certificateService.getCerticatesDetails("ag","",new HashMap<>()),
                is(notNullValue()));
    }
    
    @Test
    public void getCerticatesDetailsTest_Exception() throws Exception {
        when(certificateRepository.getCertificatesDetails(anyString(), anyString(), anyObject())).thenThrow(new DataException());
        assertThatThrownBy(() -> certificateService.getCerticatesDetails("ag","",new HashMap<>()))
        .isInstanceOf(ServiceException.class);
    }
    
    @Test
    public void getCerticatesSummaryTest() throws Exception {
        when(certificateRepository.getCertificatesSummary(anyString())).thenReturn(new HashMap<>());
        assertThat(certificateService.getCerticatesSummary("ag"),
                is(notNullValue()));
    }
    
    @Test
    public void getCerticatesSummaryTest_Exception() throws Exception {
        when(certificateRepository.getCertificatesSummary(anyString())).thenThrow(new DataException());
        assertThatThrownBy(() ->certificateService.getCerticatesSummary("ag"))
        .isInstanceOf(ServiceException.class);
    }
}
