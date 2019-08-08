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
package com.tmobile.pacman.autofix.elasticsearch;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.elasticsearch.AWSElasticsearch;
import com.amazonaws.services.elasticsearch.model.DescribeElasticsearchDomainResult;
import com.amazonaws.services.elasticsearch.model.ElasticsearchDomainStatus;
import com.amazonaws.services.elasticsearch.model.VPCDerivedInfo;
import com.tmobile.pacman.autofix.publicaccess.PublicAccessAutoFix;
import com.tmobile.pacman.commons.autofix.BaseFix;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.util.CommonTestUtils;
@PowerMockIgnore({"javax.net.ssl.*","javax.management.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PublicAccessAutoFix.class,BaseRule.class})
public class ElasticSearchPublicAccessAutoFixTest {

    @InjectMocks
    ElasticSearchPublicAccessAutoFix searchPublicAccessAutoFix;
    
    
    @Mock
    AmazonEC2 amazonEC2;
    
    @Mock
    AWSElasticsearch elasticsearch;
    

    @Before
    public void setUp() throws Exception{
    	amazonEC2 = PowerMockito.mock(AmazonEC2.class);
    	elasticsearch = PowerMockito.mock(AWSElasticsearch.class); 
    }
    @Test
    public void executeFixTest()throws Exception{
        
    	
    	ElasticsearchDomainStatus emptyDomainStatus = new ElasticsearchDomainStatus();
    	emptyDomainStatus.setDomainId("123");
    	emptyDomainStatus.setAccessPolicies("{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"*\"},\"Action\":\"es:*\",\"Resource\":\"123\"}]}");
    	
    	DescribeElasticsearchDomainResult domainResult = new DescribeElasticsearchDomainResult();
    	domainResult.setDomainStatus(CommonTestUtils.getElasticsearchDomainStatus());
    	
    	DescribeElasticsearchDomainResult emptyDdomainResult = new DescribeElasticsearchDomainResult();
    	emptyDdomainResult.setDomainStatus(CommonTestUtils.getElasticsearchDomainStatus());
    	mockStatic(PublicAccessAutoFix.class);
        Map<String,Object> ec2Map=new HashMap<String, Object>();
        ec2Map.put("client", amazonEC2);
        when(PublicAccessAutoFix.getAWSClient(anyString(),anyObject(),anyString())).thenReturn(ec2Map);
    	
        Map<String,Object> elasticSearch=new HashMap<String, Object>();
        elasticSearch.put("client", elasticsearch);
        ElasticSearchPublicAccessAutoFix spy = Mockito.spy(new ElasticSearchPublicAccessAutoFix());
         
        Mockito.doReturn(elasticSearch).when((BaseFix)spy).getClientFor(anyObject(), anyString(), anyObject());
        when(PublicAccessAutoFix.getDomainStatusForEsResource(anyObject(),anyObject())).thenReturn(CommonTestUtils.getElasticsearchDomainStatus());
        when(elasticsearch.describeElasticsearchDomain(anyObject())).thenReturn(domainResult);
        when(PublicAccessAutoFix.isEsHavingPublicAccess(anyObject(),anyString())).thenReturn(true);
        spy.executeFix(CommonTestUtils.getMapString("r_123 "), elasticSearch, CommonTestUtils.getMapString("r_123 "));
    	
        VPCDerivedInfo vPCOptions = new VPCDerivedInfo();
        vPCOptions.setSecurityGroupIds(Arrays.asList("123"));
        
        ElasticsearchDomainStatus domainStatusEmpty = new ElasticsearchDomainStatus();
    	domainStatusEmpty.setDomainId("123");
    	domainStatusEmpty.setVPCOptions(vPCOptions);
    	domainStatusEmpty.setAccessPolicies("{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"*\"},\"Action\":\"es:*\",\"Resource\":\"123\"}]}");
    	
    	DescribeElasticsearchDomainResult domainEmptyResult = new DescribeElasticsearchDomainResult();
    	domainEmptyResult.setDomainStatus(domainStatusEmpty);
        
    	when(PublicAccessAutoFix.getDomainStatusForEsResource(anyObject(),anyObject())).thenReturn(CommonTestUtils.getWithoutEndPOintElasticsearchDomainStatus());
        when(PublicAccessAutoFix.getExistingSecurityGroupDetails(anyObject(),anyObject())).thenReturn(CommonTestUtils.getSecurityGroupIdList());
        when(PublicAccessAutoFix.nestedSecurityGroupDetails(anyString(),anyObject(),anyObject(),anyObject(),anyObject(),anyInt())).thenReturn(CommonTestUtils.getSetString("123"));
        when(PublicAccessAutoFix.createSecurityGroup(anyString(),anyString(),anyObject(),anyObject(),anyString(),anyString(),anyObject())).thenReturn("123");
        when(PublicAccessAutoFix.applySecurityGroupsToAppELB(anyObject(),anyObject(),anyString())).thenReturn(true);
        spy.executeFix(CommonTestUtils.getMapString("r_123 "), elasticSearch, CommonTestUtils.getMapString("r_123 "));
        when(PublicAccessAutoFix.getAWSClient(anyString(),anyObject(),anyString())).thenThrow(new Exception());
       
       /* assertThatThrownBy( 
                () -> searchPublicAccessAutoFix.executeFix(CommonTestUtils.getMapString("r_123 "),elasticSearch,CommonTestUtils.getMapString("r_123 "))).isInstanceOf(Exception.class);*/
        
        
    }
    
    
   /* @Test
    public void backupExistingConfigForResourceTest()throws Exception{
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("client", elasticsearch);
        ElasticSearchPublicAccessAutoFix spy = Mockito.spy(new ElasticSearchPublicAccessAutoFix());
        
        Mockito.doReturn(map).when((BaseFix)spy).getClientFor(anyObject(), anyString(), anyObject());
        mockStatic(PublicAccessAutoFix.class);
        when(PublicAccessAutoFix.getDomainStatusForEsResource(anyObject(),anyObject())).thenReturn(CommonTestUtils.getElasticsearchDomainStatus());
        spy.backupExistingConfigForResource("123","123",map,CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        when(PublicAccessAutoFix.getDomainStatusForEsResource(anyObject(),anyObject())).thenReturn(CommonTestUtils.getWithoutEndPOintElasticsearchDomainStatus());
        spy.backupExistingConfigForResource("123","123",map,CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "));
        
        
        when(PublicAccessAutoFix.getDomainStatusForEsResource(anyObject(),anyString())).thenThrow(new Exception());
        assertThatThrownBy( 
                () -> searchPublicAccessAutoFix.backupExistingConfigForResource("123","123",map,CommonTestUtils.getMapString("r_123 "),CommonTestUtils.getMapString("r_123 "))).isInstanceOf(Exception.class);
    }*/

}