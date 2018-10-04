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
package com.tmobile.pacman.api.compliance.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class })
public class CertificateRepositoryTest {

    @InjectMocks
    private CertificateRepository certificateRepository;
    
    @Mock
    private ComplianceRepository complianceRepository;
    
    @Mock
    private ElasticSearchRepository elasticSearchRepository;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void getCerticatesExpiryByApplicationTest() throws Exception {
        
        String response = "{\"aggregations\":{\"apps\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"APP1\",\"doc_count\":33,\"certs\":{\"doc_count\":44,\"openfilter\":{\"doc_count\":12,\"rules\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"PacMan_certificate-expiry-policy_version-1_certificates-expiry-next-30days_cert\",\"doc_count\":6},{\"key\":\"PacMan_certificate-expiry-policy_version-1_SSLcertificatesexpirywithin45days_cert\",\"doc_count\":6}]}}}}]}}}";
    
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        
        Map<String, Object> cert = certificateRepository.getCertificatesExpiryByApplication("ag");
        assertTrue(cert.size() == 1);
    }
    
    @Test
    public void getCerticatesExpiryByApplicationTest1_Without30And45Rule() throws Exception {
        
        String response = "{\"aggregations\":{\"apps\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"APP1\",\"doc_count\":33,\"certs\":{\"doc_count\":44,\"openfilter\":{\"doc_count\":12,\"rules\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"PacMan_certificate-expiry-policy_version-1_SSL-certificates-expiry-within-60-days_cert\",\"doc_count\":6}]}}}}]}}}";
    
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        
        Map<String, Object> cert = certificateRepository.getCertificatesExpiryByApplication("ag");
        assertTrue(cert.size() == 1);
    }
    
    @Test
    public void getCerticatesExpiryByApplicationTest_NoRules() throws Exception {
        
        String response = "{\"aggregations\":{\"apps\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"APP1\",\"doc_count\":33,\"certs\":{\"doc_count\":44,\"openfilter\":{\"doc_count\":12,\"rules\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[]}}}}]}}}";
    
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        
        Map<String, Object> cert = certificateRepository.getCertificatesExpiryByApplication("ag");
        assertTrue(cert.size() == 1);
    }
    
    @Test
    public void getCerticatesExpiryByApplicationTest_NoApps() throws Exception {
        
        String response = "{\"aggregations\":{\"apps\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[]}}}";
    
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        
        Map<String, Object> cert = certificateRepository.getCertificatesExpiryByApplication("ag");
        assertTrue(cert.size() == 0);
    }
    
    @Test
    public void getCerticatesExpiryByApplicationTest_NoAppName() throws Exception {
        
        String response = "{\"aggregations\":{\"apps\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"\",\"doc_count\":33,\"certs\":{\"doc_count\":44,\"openfilter\":{\"doc_count\":12,\"rules\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[]}}}}]}}}";
        
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        
        Map<String, Object> cert = certificateRepository.getCertificatesExpiryByApplication("ag");
        assertTrue(cert.size() == 0);
    }
    
    @Test
    public void getCerticatesExpiryByApplicationTest_Exception() throws Exception {
        
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new Exception());
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        
        assertThatThrownBy(() -> certificateRepository.getCertificatesExpiryByApplication("ag"))
        .isInstanceOf(DataException.class);
    }
    
    @Test
    public void getCerticatesSummaryTest() throws Exception {
        
        String response = "{\"aggregations\":{\"certs\":{\"doc_count\":269,\"openfilter\":{\"doc_count\":23,\"rules\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,"
                + "\"buckets\":[{\"key\":\"PacMan_certificate-expiry-policy_version-1_SSLcertificatesexpirywithin45days_cert\",\"doc_count\":23},"
                + "{\"key\":\"PacMan_certificate-expiry-policy_version-1_certificates-expiry-next-30days_cert\",\"doc_count\":35}]}}}}}";
        
        Map<String, Long> certificates = new HashMap<>();
        certificates.put("certificates", 15L);
        certificates.put("certificates_expiring", 0L);
        
        when(complianceRepository.getCertificates(anyString())).thenReturn(certificates);
        ReflectionTestUtils.setField(certificateRepository, "complianceRepository", complianceRepository);
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        Map<String, Object> certificateSummary = certificateRepository.getCertificatesSummary("ag");
        
        assertTrue(Integer.valueOf(certificateSummary.get("totalCertificates").toString()).equals(15));
        assertTrue(Integer.valueOf(certificateSummary.get("expiry45Days").toString()).equals(23));
        assertTrue(Integer.valueOf(certificateSummary.get("expiry30Days").toString()).equals(35));
    }
    
    @Test
    public void getCerticatesSummaryTest_NoRules() throws Exception {
        
        String response = "{\"aggregations\":{\"certs\":{\"doc_count\":269,\"openfilter\":{\"doc_count\":23,\"rules\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[]}}}}}";
        
        Map<String, Long> certificates = new HashMap<>();
        certificates.put("certificates", 15L);
        certificates.put("certificates_expiring", 0L);
        
        when(complianceRepository.getCertificates(anyString())).thenReturn(certificates);
        ReflectionTestUtils.setField(certificateRepository, "complianceRepository", complianceRepository);
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        Map<String, Object> certificateSummary = certificateRepository.getCertificatesSummary("ag");
        
        assertTrue(Integer.valueOf(certificateSummary.get("totalCertificates").toString()).equals(15));
        assertTrue(Integer.valueOf(certificateSummary.get("expiry45Days").toString()).equals(0));
        
    }
    
    @Test
    public void getCerticatesSummaryTest_NoCertificates() throws Exception {
        
        String response = "{\"aggregations\":{\"certs\":{\"doc_count\":269,\"openfilter\":{\"doc_count\":23,\"rules\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[]}}}}}";
        
        Map<String, Long> certificates = new HashMap<>();
        certificates.put("certificates", 0L);
        certificates.put("certificates_expiring", 0L);
        
        when(complianceRepository.getCertificates(anyString())).thenReturn(certificates);
        ReflectionTestUtils.setField(certificateRepository, "complianceRepository", complianceRepository);
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        Map<String, Object> certificateSummary = certificateRepository.getCertificatesSummary("ag");
        
        assertTrue(Integer.valueOf(certificateSummary.get("totalCertificates").toString()).equals(0));
        assertTrue(Float.valueOf(certificateSummary.get("compliantPercent").toString()) == 100.0);
        
    }
    
    @Test
    public void getCerticatesSummaryTest_Exception() throws Exception {
        
        when(complianceRepository.getCertificates(anyString())).thenReturn(new HashMap<>());
        ReflectionTestUtils.setField(certificateRepository, "complianceRepository", complianceRepository);
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new Exception());
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        
        assertThatThrownBy(() -> certificateRepository.getCertificatesSummary("ag"))
        .isInstanceOf(DataException.class);
        
        when(complianceRepository.getCertificates(anyString())).thenThrow(new DataException());
        ReflectionTestUtils.setField(certificateRepository, "complianceRepository", complianceRepository);
        
        assertThatThrownBy(() -> certificateRepository.getCertificatesSummary("ag"))
        .isInstanceOf(DataException.class);
    }
    
    @Test
    public void getCerticatesDetailsTest() throws Exception {
        
        String response = "{\"_scroll_id\":\"scrollid\",\"hits\":{\"hits\":[{\"_index\":\"aws_cert\",\"_type\":\"cert\",\"_id\":\"tst.domain_Public\",\"_score\":0.2211614,\"_source\":{\"certType\":\"Public\",\"tags.Environment\":\"\",\"tags.Application\":\"APP1\",\"tags.Owner\":\"\",\"status\":\"active\",\"validto\":\"9/11/2018 10:44\","
                + "\"commonname\":\"tst.domain\",\"validfrom\":\"9/11/2017 10:14\"}},{\"_index\":\"aws_cert\",\"_type\":\"cert\",\"_id\":\"dev1r2.rbl_Internal\",\"_score\":0.2211614,\"_source\":{\"certType\":\"Internal\",\"tags.Environment\":\"Non-Prod\",\"tags.Application\":\"APP1\","
                + "\"issuerdn\":\"CN=T-Mobile USA Enterprise Issuing CA 01, DC=gsm1900, DC=org\",\"tags.Owner\":\"\",\"status\":\"active\",\"validto\":\"9/15/2018 10:06\",\"commonname\":\"dev1r2.rbl\",\"validfrom\":\"9/15/2017 10:06\"}}]}}";
        
        when(elasticSearchRepository.getTotalDocumentCountForIndexAndType(
                anyString(),anyString(),anyObject(),anyObject(),anyObject(),anyObject(),anyObject())).thenReturn(11000L);
        when(elasticSearchRepository.buildScrollRequest(anyString(),anyString())).thenReturn("");
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        when(elasticSearchRepository.processResponseAndSendTheScrollBack(anyString(), anyObject())).thenCallRealMethod();
    
        List<Map<String, Object>> certificatesDetails = certificateRepository.getCertificatesDetails("APP1", null, new HashMap<>());
        assertTrue(certificatesDetails.size() == 4);
        assertTrue(certificatesDetails.get(0).get("name").equals("tst.domain"));
    }
    
    @Test
    public void getCerticatesDetailsTest_WithFiltersExpiringIn30Days() throws Exception {
        
        String response = "{\"_scroll_id\":\"scrollid\",\"hits\":{\"hits\":[{\"_index\":\"aws_cert\",\"_type\":\"cert\",\"_id\":\"tst.domain_Public\",\"_score\":0.2211614,\"_source\":{\"certType\":\"Public\","
                + "\"tags.Environment\":\"\",\"tags.Application\":\"APP1\",\"tags.Owner\":\"\",\"status\":\"active\",\"validto\":\"9/11/2018 10:44\",\"commonname\":\"tst.domain\",\"validfrom\":\"9/11/2017 10:14\"}}]}}";
        
        when(elasticSearchRepository.getTotalDocumentCountForIndexAndType(
                anyString(),anyString(),anyObject(),anyObject(),anyObject(),anyObject(),anyObject())).thenReturn(10L);
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        when(elasticSearchRepository.processResponseAndSendTheScrollBack(anyString(), anyObject())).thenCallRealMethod();
    
        Map<String, String> filter = new HashMap<>();
        filter.put("tags.Application.keyword", "app");
        filter.put("tags.Environment.keyword", "env");
        filter.put("expiringIn", "30");
        
        List<Map<String, Object>> certificatesDetails = certificateRepository.getCertificatesDetails("APP1", null, filter);
        assertTrue(certificatesDetails.size() == 1);
        assertTrue(certificatesDetails.get(0).get("name").equals("tst.domain"));
    }
    
    @Test
    public void getCerticatesDetailsTest_WithFiltersExpiringIn45Days() throws Exception {
        
        String response = "{\"_scroll_id\":\"scrollid\",\"hits\":{\"hits\":[{\"_index\":\"aws_cert\",\"_type\":\"cert\",\"_id\":\"tst.domain_Public\",\"_score\":0.2211614,\"_source\":{\"certType\":\"Public\","
                + "\"tags.Environment\":\"\",\"tags.Application\":\"APP1\",\"tags.Owner\":\"\",\"status\":\"active\",\"validto\":\"9/11/2018 10:44\",\"commonname\":\"tst.domain\",\"validfrom\":\"9/11/2017 10:14\"}}]}}";
        
        when(elasticSearchRepository.getTotalDocumentCountForIndexAndType(
                anyString(),anyString(),anyObject(),anyObject(),anyObject(),anyObject(),anyObject())).thenReturn(10L);
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
        ReflectionTestUtils.setField(certificateRepository, "esUrl", "dummyEsURL");
        when(elasticSearchRepository.processResponseAndSendTheScrollBack(anyString(), anyObject())).thenCallRealMethod();
    
        Map<String, String> filter = new HashMap<>();
        filter.put("expiringIn", "45");
        
        List<Map<String, Object>> certificatesDetails = certificateRepository.getCertificatesDetails("APP1", "tst.domain", filter);
        assertTrue(certificatesDetails.size() == 1);
        assertTrue(certificatesDetails.get(0).get("name").equals("tst.domain"));
    }
    
    public void getCerticatesDetailsTest_WithException() throws Exception {
        
        when(elasticSearchRepository.getTotalDocumentCountForIndexAndType(
                anyString(),anyString(),anyObject(),anyObject(),anyObject(),anyObject(),anyObject())).thenReturn(10L);
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new Exception());
        
        assertThatThrownBy(() -> certificateRepository.getCertificatesDetails("APP1", null, null))
        .isInstanceOf(DataException.class);
    }
}
