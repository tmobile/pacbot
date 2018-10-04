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
package com.tmobile.pacman.api.admin.repository.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.config.PacmanConfiguration;
import com.tmobile.pacman.api.admin.domain.AssetGroupExceptionProjections;
import com.tmobile.pacman.api.admin.domain.CreateAssetGroupExceptionDetailsRequest;
import com.tmobile.pacman.api.admin.domain.DeleteAssetGroupExceptionRequest;
import com.tmobile.pacman.api.admin.domain.ElasticSearchProperty;
import com.tmobile.pacman.api.admin.domain.RuleDetails;
import com.tmobile.pacman.api.admin.domain.TargetTypeRuleDetails;
import com.tmobile.pacman.api.admin.domain.TargetTypeRuleViewDetails;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.AssetGroupExceptionRepository;
import com.tmobile.pacman.api.admin.repository.model.AssetGroupException;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class AssetGroupExceptionServiceImplTest {

	@InjectMocks
	private AssetGroupExceptionServiceImpl assetGroupExceptionService;
	
	@Mock
	private RestClient restClient;
	
	@Mock
	private Response response;
	
	@Mock
	private StatusLine sl;
	 
	@Mock
	private PacmanConfiguration config;
	
	@Mock
	private RuleService ruleService;
	
	@Mock
	private ObjectMapper mapper;
	 
	@Mock
	private AssetGroupExceptionRepository assetGroupExceptionRepository;
	
	@Mock
	private AssetGroupTargetDetailsService assetGroupTargetDetailsService;
	
	private AssetGroupExceptionServiceImpl assetGroupExceptionServiceImpl = new AssetGroupExceptionServiceImpl();

/*	@Before
    public void setUp() throws Exception{
		//MockitoAnnotations.initMocks(this);
		restClient = PowerMockito.mock(RestClient.class);
	}
	
	@Before
    public  void initClient() {
        restClient = mock(RestClient.class);
    }*/
	
	@Test
	public void getAllAssetGroupDetailsTest() {
		List<AssetGroupExceptionProjections> assetGroupExceptionDetails = Lists.newArrayList();
		assetGroupExceptionDetails.add(getAssetGroupExceptionProjections());
		Page<AssetGroupExceptionProjections> allAssetGroupExceptionProjections = new PageImpl<AssetGroupExceptionProjections>(assetGroupExceptionDetails, new PageRequest(0, 1), assetGroupExceptionDetails.size());
		when(assetGroupExceptionRepository.findAllAssetGroupExceptions(anyString(), any(PageRequest.class))).thenReturn(allAssetGroupExceptionProjections);
		assertThat(assetGroupExceptionService.getAllAssetGroupExceptions(StringUtils.EMPTY, 0, 1), is(notNullValue()));
	}
	
	@Test
	public void getAllTargetTypesByExceptionNameAndDataSourceTest() throws PacManException {
		List<AssetGroupExceptionProjections> assetGroupExceptionDetails = Lists.newArrayList();
		assetGroupExceptionDetails.add(getAssetGroupExceptionProjections());
		when(assetGroupExceptionRepository.findAllAssetGroupExceptions(anyString(), anyString())).thenReturn(assetGroupExceptionDetails);
		List<TargetTypeRuleViewDetails> allTargetTypeRuleViewDetails = Lists.newArrayList();
		TargetTypeRuleViewDetails targetTypeRuleViewDetails = new TargetTypeRuleViewDetails();
		targetTypeRuleViewDetails.setAdded(false);
		targetTypeRuleViewDetails.setAllRules("[]");
		targetTypeRuleViewDetails.setRules("[]");
		targetTypeRuleViewDetails.setTargetName("targetName123");
		allTargetTypeRuleViewDetails.add(targetTypeRuleViewDetails);
		when(assetGroupTargetDetailsService.getTargetTypesByAssetGroupIdAndTargetTypeNotIn(anyString(), any())).thenReturn(allTargetTypeRuleViewDetails);
		assertThat(assetGroupExceptionService.getAllTargetTypesByExceptionNameAndDataSource("exceptionName123", "dataSource123"), is(notNullValue()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void createAssetGroupExceptionsTest() throws PacManException, IOException {
		AssetGroupException assetGroupExceptionDetails = buildAssetGroupException();
		when(assetGroupExceptionRepository.save(any(AssetGroupException.class))).thenReturn(assetGroupExceptionDetails);
		ElasticSearchProperty elasticSearchProperty = buildElasticSearchProperty();
	    when(config.getElasticSearch()).thenReturn(elasticSearchProperty);
        when(response.getEntity()).thenReturn(null);
        when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(assetGroupExceptionServiceImpl, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
 	    when(response.getStatusLine()).thenReturn(sl);
		assertThat(assetGroupExceptionService.createAssetGroupExceptions(getCreateAssetGroupExceptionDetailsRequest(), "userId123"), is(notNullValue()));
	}

/*	@SuppressWarnings("unchecked")
	@Test
	public void updateAssetGroupExceptionsTest() throws PacManException, IOException {
		String json = "{\"aggregations\":{\"severity\":{\"buckets\":[{\"key\":\"low\",\"doc_count\":2158},{\"key\":\"high\",\"doc_count\":1998}]}}}";
		AssetGroupException assetGroupExceptionDetails = buildAssetGroupException();
		List<AssetGroupException> allAssetGroupException = Lists.newArrayList();
		allAssetGroupException.add(assetGroupExceptionDetails);
		when(assetGroupExceptionRepository.findByGroupNameAndExceptionName(anyString(), anyString())).thenReturn(allAssetGroupException);
		HttpEntity jsonEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
		when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(assetGroupExceptionServiceImpl, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
 	    when(response.getStatusLine()).thenReturn(sl);
 	    doNothing().when(assetGroupExceptionRepository).delete(anyLong());
		AssetGroupException assetGroupException = buildAssetGroupException();
		when(assetGroupExceptionRepository.save(any(AssetGroupException.class))).thenReturn(assetGroupException);
		ElasticSearchProperty elasticSearchProperty = buildElasticSearchProperty();
	    when(config.getElasticSearch()).thenReturn(elasticSearchProperty);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(assetGroupExceptionServiceImpl, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
 	    when(response.getStatusLine()).thenReturn(sl);
 	    when(assetGroupExceptionService.deleteAssetGroupExceptions(any(), any())).thenReturn(AdminConstants.EXCEPTION_DELETEION_SUCCESS);

 	    assertThat(assetGroupExceptionService.updateAssetGroupExceptions(getCreateAssetGroupExceptionDetailsRequest(), "userId123"), is(notNullValue()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updateAssetGroupExceptionsExceptionTest() throws PacManException, IOException {
		AssetGroupException assetGroupExceptionDetails = buildAssetGroupException();
		List<AssetGroupException> allAssetGroupException = Lists.newArrayList();
		allAssetGroupException.add(assetGroupExceptionDetails);
		when(response.getEntity()).thenThrow(Exception.class);
	    when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenThrow(Exception.class);
        ReflectionTestUtils.setField(assetGroupExceptionServiceImpl, "restClient", restClient);
        when(sl.getStatusCode()).thenThrow(Exception.class);
 	    when(response.getStatusLine()).thenReturn(sl);
		//when(assetGroupExceptionRepository.findByGroupNameAndExceptionName(anyString(), anyString())).thenThrow(Exception.class);
 	   	assertThatThrownBy(() -> assetGroupExceptionService.updateAssetGroupExceptions(getCreateAssetGroupExceptionDetailsRequest(), "userId123")).isInstanceOf(PacManException.class);
	}*/
	
	@SuppressWarnings("unchecked")
	@Test
	public void deleteAssetGroupExceptionsExceptionTest() throws PacManException, IOException {
		String json = "{\"aggregations\":{\"severity\":{\"buckets\":[{\"key\":\"low\",\"doc_count\":2158},{\"key\":\"high\",\"doc_count\":1998}]}}}";
		AssetGroupException assetGroupExceptionDetails = buildAssetGroupException();
		List<AssetGroupException> allAssetGroupException = Lists.newArrayList();
		allAssetGroupException.add(assetGroupExceptionDetails);
		HttpEntity jsonEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
		when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(assetGroupExceptionServiceImpl, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
 	    when(response.getStatusLine()).thenReturn(sl);
		when(assetGroupExceptionRepository.findByGroupNameAndExceptionName(anyString(), anyString())).thenThrow(Exception.class);
		assertThatThrownBy(() -> assetGroupExceptionService.deleteAssetGroupExceptions(geDeleteAssetGroupExceptionDetailsRequest(), "userId123")).isInstanceOf(PacManException.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deleteAssetGroupExceptionsTest() throws PacManException, IOException {
		String json = "{\"aggregations\":{\"severity\":{\"buckets\":[{\"key\":\"low\",\"doc_count\":2158},{\"key\":\"high\",\"doc_count\":1998}]}}}";
		AssetGroupException assetGroupExceptionDetails = buildAssetGroupException();
		List<AssetGroupException> allAssetGroupException = Lists.newArrayList();
		allAssetGroupException.add(assetGroupExceptionDetails);
		when(assetGroupExceptionRepository.findByGroupNameAndExceptionName(anyString(), anyString())).thenReturn(allAssetGroupException);
		HttpEntity jsonEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
		when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(assetGroupExceptionServiceImpl, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
 	    when(response.getStatusLine()).thenReturn(sl);
		assertThat(assetGroupExceptionService.deleteAssetGroupExceptions(geDeleteAssetGroupExceptionDetailsRequest(), "userId123"), is(notNullValue()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deleteAssetGroupExceptionsExceptionThrowsTest1() throws PacManException, IOException {
		when(assetGroupExceptionRepository.findByGroupNameAndExceptionName(anyString(), anyString())).thenThrow(Exception.class);
		assertThatThrownBy(() -> assetGroupExceptionService.deleteAssetGroupExceptions(geDeleteAssetGroupExceptionDetailsRequest(), "userId123")).isInstanceOf(PacManException.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deleteAssetGroupExceptionsExceptionThrowsTest2() throws PacManException, IOException {
		when(assetGroupExceptionRepository.findByGroupNameAndExceptionName(anyString(), anyString())).thenThrow(Exception.class);
		assertThatThrownBy(() -> assetGroupExceptionService.deleteAssetGroupExceptions(geDeleteAssetGroupExceptionDetailsRequest(), "userId123")).isInstanceOf(PacManException.class);
	}

	private DeleteAssetGroupExceptionRequest geDeleteAssetGroupExceptionDetailsRequest() {
		DeleteAssetGroupExceptionRequest deleteAssetGroupExceptionRequest = new DeleteAssetGroupExceptionRequest();
		deleteAssetGroupExceptionRequest.setExceptionName("exceptionName123");
		deleteAssetGroupExceptionRequest.setGroupName("groupName123");
		return deleteAssetGroupExceptionRequest;
	}

	private ElasticSearchProperty buildElasticSearchProperty() {
		ElasticSearchProperty elasticSearchProperty = new ElasticSearchProperty();
		elasticSearchProperty.setDevIngestHost("test.es.com");
		elasticSearchProperty.setDevIngestPort(9090);
		elasticSearchProperty.setHost("test.es.com");
		elasticSearchProperty.setPort(9090);
		return elasticSearchProperty;
	}

	private CreateAssetGroupExceptionDetailsRequest getCreateAssetGroupExceptionDetailsRequest() {
		List<TargetTypeRuleDetails> allTargetTypeRuleDetails = Lists.newArrayList();
		TargetTypeRuleDetails targetTypeRuleDetails = new TargetTypeRuleDetails();
		targetTypeRuleDetails.setAllRules("[]");
		List<RuleDetails> allRuleDetails = getAllRuleDetails();
		targetTypeRuleDetails.setRules(allRuleDetails);
		targetTypeRuleDetails.setTargetName("targetName123");
		allTargetTypeRuleDetails.add(targetTypeRuleDetails);
		
		CreateAssetGroupExceptionDetailsRequest createAssetGroupExceptionDetailsRequest = new CreateAssetGroupExceptionDetailsRequest();
		createAssetGroupExceptionDetailsRequest.setAssetGroup("assetGroup123");
		createAssetGroupExceptionDetailsRequest.setDataSource("dataSource123");
		createAssetGroupExceptionDetailsRequest.setExceptionName("exceptionName123");
		createAssetGroupExceptionDetailsRequest.setExceptionReason("exceptionReason123");
		createAssetGroupExceptionDetailsRequest.setExpiryDate("12/12/2018");
		createAssetGroupExceptionDetailsRequest.setTargetTypes(allTargetTypeRuleDetails);
		return createAssetGroupExceptionDetailsRequest;
	}

	private List<RuleDetails> getAllRuleDetails() {
		List<RuleDetails> allRuleDetails = Lists.newArrayList();
		RuleDetails ruleDetails = new RuleDetails();
		ruleDetails.setId("id123");
		ruleDetails.setPolicyId("policyId123");
		ruleDetails.setStatus("status123");
		ruleDetails.setText("text123");
		ruleDetails.setType("type123");
		allRuleDetails.add(ruleDetails);
		return allRuleDetails;
	}

	private AssetGroupException buildAssetGroupException() {
		Date date = new Date();
		AssetGroupException assetGroupException = new AssetGroupException();
		assetGroupException.setDataSource("dataSource123");
		assetGroupException.setExceptionName("exceptionName123");
		assetGroupException.setExceptionReason("exceptionReason123");
		assetGroupException.setExpiryDate(date);
		assetGroupException.setGroupName("groupName123");
		assetGroupException.setId(123l);
		assetGroupException.setRuleId("ruleId123");
		assetGroupException.setRuleName("ruleName123");
		assetGroupException.setTargetType("targetType123"); 
		assertEquals(assetGroupException.getDataSource(), "dataSource123");
		assertEquals(assetGroupException.getExceptionName(), "exceptionName123");
		assertEquals(assetGroupException.getExceptionReason(), "exceptionReason123");
		assertEquals(assetGroupException.getExpiryDate(), date);
		assertEquals(assetGroupException.getGroupName(), "groupName123");
		assertEquals(assetGroupException.getId(), 123l);
		assertEquals(assetGroupException.getRuleId(), "ruleId123");
		assertEquals(assetGroupException.getRuleName(), "ruleName123");
		assertEquals(assetGroupException.getTargetType(), "targetType123"); 
		return assetGroupException;
	}

	private AssetGroupExceptionProjections getAssetGroupExceptionProjections() {
		return new AssetGroupExceptionProjections() {

			@Override
			public String getTargetType() {
				return "TargetType123";
			}

			@Override
			public String getRuleName() {
				return "RuleName123";
			}

			@Override
			public String getRuleId() {
				return "RuleId123";
			}

			@Override
			public long getId() {
				return 123l;
			}

			@Override
			public String getExpiryDate() {
				return "ExpiryDate123";
			}

			@Override
			public String getExceptionReason() {
				return "ExceptionReason123";
			}

			@Override
			public String getExceptionName() {
				return "ExceptionName123";
			}

			@Override
			public String getDataSource() {
				return "DataSource123";
			}

			@Override
			public String getAssetGroup() {
				return "AssetGroup123";
			}
		};
	}
}
