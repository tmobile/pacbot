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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.compliance.domain.IssueExceptionResponse;
import com.tmobile.pacman.api.compliance.domain.IssuesException;
import com.tmobile.pacman.api.compliance.domain.PolicyViolationDetails;
import com.tmobile.pacman.api.compliance.domain.RevokeIssuesException;
import com.tmobile.pacman.api.compliance.service.ComplianceService;
import com.tmobile.pacman.api.compliance.util.CommonTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class ComplianceControllerTest {

    @InjectMocks
    ComplianceController complianceController;
    
    @Mock
    ComplianceService complianceService;
    
    @Test
    public void getIssuesTest() throws Exception {
        when(complianceService.getIssues(anyObject())).thenReturn(CommonTestUtil.getResponseWithOrder());

        assertThat(complianceController.getIssues(CommonTestUtil.getRequest()), is(notNullValue()));

        when(complianceService.getIssues(anyObject())).thenReturn(
                CommonTestUtil.getResponseWithOrder());

        assertThat(complianceController.getIssues(CommonTestUtil.getRequestEmpty()), is(notNullValue()));
        
        when(complianceService.getIssues(anyObject())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getIssues(CommonTestUtil.getRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getIssuesCountTest() throws Exception {
        when(complianceService.getIssuesCount(anyString(),anyString(),anyString())).thenReturn(1000l);
        assertThat(complianceController.getIssuesCount("ag","ruleId","domain"), is(notNullValue()));
        assertThat(complianceController.getIssuesCount("","",""), is(notNullValue()));
        
        when(complianceService.getIssuesCount(anyString(),anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getIssuesCount("ag","ruleId","domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getDistributionTest() throws Exception {
        when(complianceService.getDistribution(anyString(),anyString())).thenReturn(new HashMap<>());
        assertThat(complianceController.getDistribution("ag","domain"), is(notNullValue()));
        assertThat(complianceController.getDistribution("",""), is(notNullValue()));
        
        when(complianceService.getDistribution(anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getDistribution("ag","domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getTaggingTest() throws Exception {
        when(complianceService.getTagging(anyString(),anyString())).thenReturn(new HashMap<>());
        assertThat(complianceController.getTagging("ag","targettype"), is(notNullValue()));
        assertThat(complianceController.getTagging("",""), is(notNullValue()));
        
        when(complianceService.getTagging(anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getTagging("ag","targettype");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getCertificatesTest() throws Exception {
        when(complianceService.getCertificates(anyString())).thenReturn(CommonTestUtil.getMapLong());
        assertThat(complianceController.getCertificates("ag"), is(notNullValue()));
        assertThat(complianceController.getCertificates(""), is(notNullValue()));
        
        when(complianceService.getCertificates(anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getCertificates("ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getPatchingTest() throws Exception {
        when(complianceService.getPatching(anyString(),anyString(),anyString())).thenReturn(CommonTestUtil.getMapLong());
        assertThat(complianceController.getPatching("ag"), is(notNullValue()));
        assertThat(complianceController.getPatching(""), is(notNullValue()));
        
        when(complianceService.getPatching(anyString(),anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getPatching("ag");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getRecommendationsTest() throws Exception {
        when(complianceService.getRecommendations(anyString(),anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(complianceController.getRecommendations("ag","targetType"), is(notNullValue()));
        assertThat(complianceController.getRecommendations("",""), is(notNullValue()));
        
        when(complianceService.getRecommendations(anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getRecommendations("ag","targetType");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getResourceDetailsTest() throws Exception {
        when(complianceService.getResourceDetails(anyString(),anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(complianceController.getResourceDetails("ag","targetType"), is(notNullValue()));
        assertThat(complianceController.getResourceDetails("",""), is(notNullValue()));
        
        when(complianceService.getResourceDetails(anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getResourceDetails("ag","targetType");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
    }
    
    @Test
    public void closeIssuesTest() throws Exception {
        when(complianceService.closeIssuesByRule(anyObject())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(complianceController.closeIssues(CommonTestUtil.getRuleDetails()), is(notNullValue()));
    }
    
    @Test
    public void getIssueAuditTest() throws Exception {
        when(complianceService.getIssueAuditLog(anyString(),anyString(),anyInt(),anyInt(),anyString())).thenReturn(CommonTestUtil.getResponseWithOrder());
        assertThat(complianceController.getIssueAudit(CommonTestUtil.getIssueAuditLogRequest()), is(notNullValue()));
        assertThat(complianceController.getIssueAudit(CommonTestUtil.getIssueAuditLogRequestEmpty()), is(notNullValue()));
        
        when(complianceService.getIssueAuditLog(anyString(),anyString(),anyInt(),anyInt(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getIssueAudit(CommonTestUtil.getIssueAuditLogRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void addIssueExceptionTest() throws Exception {
        when(complianceService.addIssueException(anyObject())).thenReturn(true);
        assertThat(complianceController.addIssueException(CommonTestUtil.getIssueResponse()), is(notNullValue()));
        
        when(complianceService.addIssueException(anyObject())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.addIssueException(CommonTestUtil.getIssueResponse());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void addIssueExceptionFalseTest() throws Exception {
        when(complianceService.addIssueException(anyObject())).thenReturn(false);
        assertThat(complianceController.addIssueException(CommonTestUtil.getIssueResponse()), is(notNullValue()));
    }
    
    @Test
    public void revokeIssueExceptionTest() throws Exception {
        when(complianceService.revokeIssueException(anyString())).thenReturn(true);
        assertThat(complianceController.revokeIssueException("issueId"), is(notNullValue()));
        
        when(complianceService.revokeIssueException(anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.revokeIssueException("issueId");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void revokeIssueExceptionFalseTest() throws Exception {
        when(complianceService.revokeIssueException(anyString())).thenReturn(false);
        assertThat(complianceController.revokeIssueException("issueId"), is(notNullValue()));
    }
    
    @Test
    public void getNonCompliancePolicyByRuleTest() throws Exception {
        when(complianceService.getRulecompliance(anyObject())).thenReturn(CommonTestUtil.getResponseWithOrder());
        assertThat(complianceController.getNonCompliancePolicyByRule(CommonTestUtil.getRequest()), is(notNullValue()));
        assertThat(complianceController.getNonCompliancePolicyByRule(CommonTestUtil.getRequestEmpty()), is(notNullValue()));
        
        when(complianceService.getRulecompliance(anyObject())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getNonCompliancePolicyByRule(CommonTestUtil.getRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getPolicydetailsbyApplicationTest() throws Exception {
        when(complianceService.getRuleDetailsbyApplication(anyString(),anyString(),anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(complianceController.getPolicydetailsbyApplication("ag","ruleId","searchText"), is(notNullValue()));
        assertThat(complianceController.getPolicydetailsbyApplication("","",""), is(notNullValue()));
        
        when(complianceService.getRuleDetailsbyApplication(anyString(),anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getPolicydetailsbyApplication("ag","ruleId","searchText");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getpolicydetailsbyEnvironmentTest() throws Exception {
        when(complianceService.getRuleDetailsbyEnvironment(anyString(),anyString(),anyString(),anyString())).thenReturn(CommonTestUtil.getListMapObject());
        assertThat(complianceController.getpolicydetailsbyEnvironment("ag","ruleId","application","searchText"), is(notNullValue()));
        assertThat(complianceController.getpolicydetailsbyEnvironment("","","",""), is(notNullValue()));
        
        when(complianceService.getRuleDetailsbyEnvironment(anyString(),anyString(),anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getpolicydetailsbyEnvironment("ag","ruleId","application","searchText");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getPolicyDescriptionTest() throws Exception {
        when(complianceService.getRuleDescription(anyString())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(complianceController.getPolicyDescription("ruleId"), is(notNullValue()));
        assertThat(complianceController.getPolicyDescription(""), is(notNullValue()));
        
        when(complianceService.getRuleDescription(anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getPolicyDescription("ruleId");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getKernelComplianceByInstanceIdTest() throws Exception {
        when(complianceService.getKernelComplianceByInstanceIdFromDb(anyString())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(complianceController.getKernelComplianceByInstanceId("instanceId"), is(notNullValue()));
        assertThat(complianceController.getKernelComplianceByInstanceId(""), is(notNullValue()));
        
        when(complianceService.getKernelComplianceByInstanceIdFromDb(anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getKernelComplianceByInstanceId("instanceId");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void updateKernelVersionTest() throws Exception {
        when(complianceService.updateKernelVersion(anyObject())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(complianceController.updateKernelVersion(CommonTestUtil.getKernelVersion()), is(notNullValue()));
    }
    
    @Test
    public void getOverallComplianceTest() throws Exception {
        when(complianceService.getOverallComplianceByDomain(anyString(),anyString())).thenReturn(CommonTestUtil.getMapObject());
        assertThat(complianceController.getOverallCompliance("ag","domain"), is(notNullValue()));
        assertThat(complianceController.getOverallCompliance("",""), is(notNullValue()));
        
        when(complianceService.getOverallComplianceByDomain(anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getOverallCompliance("ag","domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void getTargetTypeTest() throws Exception {
        when(complianceService.getResourceType(anyString(),anyString())).thenReturn(new ArrayList<>());
        assertThat(complianceController.getTargetType("ag","domain"), is(notNullValue()));
        assertThat(complianceController.getTargetType("",""), is(notNullValue()));
        
        when(complianceService.getResourceType(anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getTargetType("ag","domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void policyViolationReasonTest() throws Exception {
        when(complianceService.getPolicyViolationDetailsByIssueId(anyString(),anyString())).thenReturn(new PolicyViolationDetails());
        assertThat(complianceController.policyViolationReason("ag","domain"), is(notNullValue()));
        assertThat(complianceController.policyViolationReason("",""), is(notNullValue()));
        
        when(complianceService.getPolicyViolationDetailsByIssueId(anyString(),anyString())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.policyViolationReason("ag","domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void addIssuesExceptionExceptionTest() throws Exception {
        
        when(complianceService.addMultipleIssueException(anyObject())).thenReturn(new IssueExceptionResponse());
        assertThat(complianceController.addIssuesException(new IssuesException()), is(notNullValue()));
        
        when(complianceService.addMultipleIssueException(anyObject())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.addIssuesException(new IssuesException());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
    @Test
    public void revokeIssuesExceptionTest() throws Exception {
        
        RevokeIssuesException revokeIssuesException = new RevokeIssuesException();
        revokeIssuesException.setIssueIds(Arrays.asList("123"));
        when(complianceService.revokeMultipleIssueException(anyObject())).thenReturn(new IssueExceptionResponse());
        assertThat(complianceController.revokeIssuesException(revokeIssuesException), is(notNullValue()));
        
        when(complianceService.revokeMultipleIssueException(anyObject())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.revokeIssuesException(revokeIssuesException);
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }
    
   /* @Test
    public void getExemptedIssuesTest() throws Exception {
        when(complianceService.getExemptedIssues(anyObject())).thenReturn(CommonTestUtil.getResponseWithOrder());

        assertThat(complianceController.getExemptedIssues(CommonTestUtil.getRequest()), is(notNullValue()));

        when(complianceService.getExemptedIssues(anyObject())).thenReturn(
                CommonTestUtil.getResponseWithOrder());

        assertThat(complianceController.getExemptedIssues(CommonTestUtil.getRequestEmpty()), is(notNullValue()));
        
        when(complianceService.getExemptedIssues(anyObject())).thenThrow(new ServiceException());
        when(complianceService.formatException(anyObject())).thenReturn(ResponseUtils.buildFailureResponse(new ServiceException()));
        ResponseEntity<Object> responseObj = complianceController.getExemptedIssues(CommonTestUtil.getRequest());
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
    }*/
    
}
