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
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.config.PacmanConfiguration;
import com.tmobile.pacman.api.admin.domain.AssetGroupTargetTypes;
import com.tmobile.pacman.api.admin.domain.AttributeDetails;
import com.tmobile.pacman.api.admin.domain.AttributeValuesRequest;
import com.tmobile.pacman.api.admin.domain.CreateUpdateTargetTypeDetailsRequest;
import com.tmobile.pacman.api.admin.domain.ElasticSearchProperty;
import com.tmobile.pacman.api.admin.domain.TargetTypeAttribute;
import com.tmobile.pacman.api.admin.domain.TargetTypesDetails;
import com.tmobile.pacman.api.admin.domain.TargetTypesProjections;
import com.tmobile.pacman.api.admin.domain.TargetTypesProperty;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.AssetGroupTargetDetailsRepository;
import com.tmobile.pacman.api.admin.repository.TargetTypesRepository;
import com.tmobile.pacman.api.admin.repository.model.TargetTypes;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class TargetTypesServiceImplTest {

	@InjectMocks
	private TargetTypesServiceImpl targetTypesService;

	@Mock
	private PacmanConfiguration config ;
	
	@Mock
	private RestClient restClient;
	
	@Mock
	private Response response;
	
	@Mock
	private ObjectMapper mapper;
	
	@Mock
	private StatusLine sl;
	
	@Mock
	private TargetTypesRepository targetTypesRepository;
	
	@Mock
	private AssetGroupTargetDetailsRepository assetGroupTargetDetailsRepository;

	private TargetTypesServiceImpl targetTypesServiceImpl = new TargetTypesServiceImpl();
	@Before
	public void setUp() throws Exception {
		//MockitoAnnotations.initMocks(this);
		 PowerMockito.whenNew(PacmanConfiguration.class).withAnyArguments().thenReturn(config);  
		 PowerMockito.whenNew(RestClient.class).withAnyArguments().thenReturn(restClient);  
	}

	
	@Test
	public void getAllAssetGroupNamesTest() {
		Collection<String> allTargetTypesNames = Lists.newArrayList();
		allTargetTypesNames.add("TargetTypesName123");
		when(targetTypesService.getTargetTypesNamesByDataSourceName(anyString())).thenReturn(allTargetTypesNames);
		assertThat(targetTypesRepository.findByDataSourceName(StringUtils.EMPTY), is(notNullValue()));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getTargetTypesNamesByDataSourceNameTest() {
		when(targetTypesService.getAllTargetTypesByDomainList(anyList())).thenReturn(getTargetTypesDetailsList());
		assertThat(targetTypesRepository.findByDomainIn(Lists.newArrayList()), is(notNullValue()));
	}
	
	@Test
	public void getAllTargetTypeDetailsTest() {
		List<TargetTypesProjections> targetTypesDetails = new ArrayList<TargetTypesProjections>();
		targetTypesDetails.add(getTargetTypesProjections());
		Page<TargetTypesProjections> allTargetTypesDetails = new PageImpl<TargetTypesProjections>(targetTypesDetails,new PageRequest(0, 1), targetTypesDetails.size());
		when(targetTypesService.getAllTargetTypeDetails(StringUtils.EMPTY, 0,  1)).thenReturn(allTargetTypesDetails);
		assertThat(targetTypesRepository.findAllTargetTypeDetails(StringUtils.EMPTY, new PageRequest(0, 1)), is(notNullValue()));
	}
	
	@Test
	public void getTargetTypesByNameTest() throws PacManException {
		when(targetTypesRepository.existsById("targetTypeName123")).thenReturn(true);
		when(targetTypesRepository.findById(anyString())).thenReturn(getTargetTypesValues());
		assertThat(targetTypesService.getTargetTypesByName("targetTypeName123").getCategory(), is(getTargetTypesValues().get().getCategory()));
	}
	
	@Test
	public void getTargetTypesByNameExceptionTest() throws PacManException {
		when(targetTypesRepository.existsById("targetTypeName123")).thenReturn(false);
		assertThatThrownBy(() -> targetTypesService.getTargetTypesByName("targetTypeName123")).isInstanceOf(PacManException.class);
	}

	@Test
	public void getAllTargetTypesCategoriesTest() {
		List<String> categories = Lists.newArrayList();
		categories.add("Category123");
		TargetTypesProperty targetTypesProperty = new TargetTypesProperty();
		targetTypesProperty.setCategories(categories);
		when(config.getTargetTypes()).thenReturn(targetTypesProperty);
		assertThat(targetTypesService.getAllTargetTypesCategories().get(0), is("Category123"));
	}

	@Test
	public void updateTargetTypeDetailsTest() throws PacManException {
		when(targetTypesRepository.existsById("targettypename123")).thenReturn(true);
		when(targetTypesRepository.findById("targettypename123")).thenReturn(getTargetTypesValues());
		when(config.getElasticSearch()).thenReturn(getElasticSearchProperty());
		assertThat(targetTypesService.updateTargetTypeDetails(getTargetTypeDetailsRequest(), "userId"), is(AdminConstants.TARGET_TYPE_UPDATION_SUCCESS));
	}

	@Test
	public void updateTargetTypeDetailsExceptionTest() throws PacManException {
		when(targetTypesRepository.existsById("targetTypeName123")).thenReturn(false);
		assertThatThrownBy(() -> targetTypesService.updateTargetTypeDetails(getTargetTypeDetailsRequest(), "userId")).isInstanceOf(PacManException.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getAllTargetTypesTest() throws Exception {
		when(targetTypesRepository.findAll()).thenReturn(getTargetTypesDetailsList());
		List<AssetGroupTargetTypes> selectedTargetTypes = getSelectedTargetTypes();
		when(config.getElasticSearch()).thenReturn(getElasticSearchProperty());
		
		HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
		when(targetTypesRepository.existsById(anyString())).thenReturn(false);
        when(response.getEntity()).thenReturn(jsonEntity);

        when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(targetTypesServiceImpl, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
 	    when(response.getStatusLine()).thenReturn(sl);
 	    
 	    
 	    String jsonString = "{\"k1\":\"v1\",\"k2\":\"v2\"}";
 	    JsonNode actualObj = new ObjectMapper().readTree(jsonString);
 	    when(mapper.readTree(anyString())).thenReturn(actualObj);
 	    
		assertThat(targetTypesService.getAllTargetTypes(selectedTargetTypes), is(notNullValue()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getAttributeValuesTest() throws IOException {
		AttributeValuesRequest attributeValuesRequest = getAttributeValuesRequest();
		when(config.getElasticSearch()).thenReturn(getElasticSearchProperty());
		HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(targetTypesServiceImpl, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
 	    when(response.getStatusLine()).thenReturn(sl);
 	    Map<String, Object> ruleParamDetails = Maps.newHashMap();
        when(mapper.readValue(anyString(), any(TypeReference.class))).thenReturn(ruleParamDetails);
		assertThat(targetTypesService.getAttributeValues(attributeValuesRequest), is(notNullValue()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getAllTargetTypesNoValueTest() throws IOException {
		when(targetTypesRepository.findAll()).thenReturn(getTargetTypesDetailsList());
		when(config.getElasticSearch()).thenReturn(getElasticSearchProperty());
		
		HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
		when(targetTypesRepository.existsById(anyString())).thenReturn(false);
        when(response.getEntity()).thenReturn(jsonEntity);

        when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(targetTypesServiceImpl, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
 	    when(response.getStatusLine()).thenReturn(sl);

 	    String jsonString = "{\"k1\":\"v1\",\"k2\":\"v2\"}";
 	    JsonNode actualObj = new ObjectMapper().readTree(jsonString);
 	    when(mapper.readTree(anyString())).thenReturn(actualObj);
		assertThat(targetTypesService.getAllTargetTypes(Lists.newArrayList()), is(notNullValue()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getTargetTypeAttributesTest() throws IOException {
		when(config.getElasticSearch()).thenReturn(getElasticSearchProperty());
		
		HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
		when(targetTypesRepository.existsById(anyString())).thenReturn(false);
        when(response.getEntity()).thenReturn(jsonEntity);

        when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(targetTypesServiceImpl, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
 	    when(response.getStatusLine()).thenReturn(sl);

 	    String jsonString = "{\"k1\":\"v1\",\"k2\":\"v2\"}";
 	    JsonNode actualObj = new ObjectMapper().readTree(jsonString);
 	    when(mapper.readTree(anyString())).thenReturn(actualObj);
		assertThat(targetTypesService.getTargetTypeAttributes(getTargetTypes()), is(notNullValue()));
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Test
	public void addTargetTypeDetailsTest() throws Exception {
		TargetTypesServiceImpl mock = PowerMockito.spy(new TargetTypesServiceImpl());

		when(targetTypesRepository.existsById("targetTypeName123")).thenReturn(true);
		when(targetTypesRepository.findById("targetTypeName123")).thenReturn(getTargetTypesValues());
		when(config.getElasticSearch()).thenReturn(getElasticSearchProperty());

		ElasticSearchProperty elasticSearchProperty = getElasticSearchProperty();
 
		HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);

		when(targetTypesRepository.existsById(anyString())).thenReturn(false);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(config.getElasticSearch()).thenReturn(elasticSearchProperty);
        when(restClient.performRequest(anyString(), anyString(), any(Map.class), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(targetTypesServiceImpl, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
 	    when(response.getStatusLine()).thenReturn(sl);
 	    assertThatThrownBy(() -> targetTypesService.addTargetTypeDetails(getTargetTypeDetailsRequest(), "userId")).isInstanceOf(PacManException.class);
	}
	
	private AttributeValuesRequest getAttributeValuesRequest() {
		AttributeValuesRequest attributeValuesRequest = new AttributeValuesRequest();
		attributeValuesRequest.setIndex("endpoint123");
		attributeValuesRequest.setPayload("payload123");
		return attributeValuesRequest;
	}
	
	private List<AssetGroupTargetTypes> getSelectedTargetTypes() {
		AssetGroupTargetTypes assetGroupTargetTypes = new AssetGroupTargetTypes();
		assetGroupTargetTypes.setAttributeName("attributeName123");
		assetGroupTargetTypes.setAttributeValue("attributeValue123");
		assetGroupTargetTypes.setGroupId("groupId123");
		assetGroupTargetTypes.setId("id123");
		assetGroupTargetTypes.setTargetType("targetTypeName123");
		List<AssetGroupTargetTypes> allAssetGroupTargetTypes = Lists.newArrayList();
		allAssetGroupTargetTypes.add(assetGroupTargetTypes);
		return allAssetGroupTargetTypes;
	}

	private List<TargetTypes> getTargetTypes() {
		TargetTypes targetTypes = new TargetTypes();
		targetTypes.setCategory("category");
		targetTypes.setTargetName("targetName");
		targetTypes.setCreatedDate(new Date());
		targetTypes.setDataSourceName("dataSourceName");
		List<TargetTypes> targetTypesList = Lists.newArrayList();
		targetTypesList.add(targetTypes);
		return targetTypesList;
	}

	@SuppressWarnings("unused")
	private List<TargetTypeAttribute> getAllAttributesList() {
		List<TargetTypeAttribute> attributes = Lists.newArrayList();
		String targetName = "targetName123";;
		TargetTypeAttribute targetTypeAttribute = new TargetTypeAttribute();
		targetTypeAttribute.setAttributes(Lists.newArrayList());
		targetTypeAttribute.setTargetName(targetName.trim());
		targetTypeAttribute.setAllAttributesName(Lists.newArrayList());
		targetTypeAttribute.setIncludeAll(false);
		attributes.add(targetTypeAttribute);
		return attributes;
	}

	private ElasticSearchProperty getElasticSearchProperty() {
		ElasticSearchProperty elasticSearchProperty = new ElasticSearchProperty();
		elasticSearchProperty.setDevIngestHost("dev-ingest.pacman.corporate.t-mobile.com");
		elasticSearchProperty.setDevIngestPort(9090);
		elasticSearchProperty.setHost("dev-ingest.pacman.corporate.t-mobile.com");
		elasticSearchProperty.setPort(9090);
		assertEquals(elasticSearchProperty.getPort(), 9090);
		assertEquals(elasticSearchProperty.getHost(), "dev-ingest.pacman.corporate.t-mobile.com");
		return elasticSearchProperty;
	}

	private CreateUpdateTargetTypeDetailsRequest getTargetTypeDetailsRequest() {
		CreateUpdateTargetTypeDetailsRequest targetTypeDetailsRequest = new CreateUpdateTargetTypeDetailsRequest();
		targetTypeDetailsRequest.setCategory("targetTypeCategory123");
		targetTypeDetailsRequest.setConfig("targetTypeConfig123");
		targetTypeDetailsRequest.setDataSource("targetTypeDataSource123");
		targetTypeDetailsRequest.setDesc("targetTypeDesc123");
		targetTypeDetailsRequest.setDomain("targetTypeDomain123");
		targetTypeDetailsRequest.setName("targetTypeName123");
		return targetTypeDetailsRequest;
	}

	private List<TargetTypes> getTargetTypesDetailsList() {
		List<TargetTypes> allTargetTypes = Lists.newArrayList();
		allTargetTypes.add(getTargetTypesValues().get());
		return allTargetTypes;
	}
	
	private Optional<TargetTypes> getTargetTypesValues() {
		TargetTypes targetTypes = new TargetTypes();
		targetTypes.setCategory("targetTypeCategory123");
		targetTypes.setTargetConfig("targetTypeConfig123");
		targetTypes.setDataSourceName("targetTypeDataSource123");
		targetTypes.setTargetDesc("targetTypeDesc123");
		targetTypes.setDomain("targetTypeDomain123");
		targetTypes.setTargetName("targetTypeName123");
		
		assertEquals(targetTypes.getCategory(), "targetTypeCategory123");
		assertEquals(targetTypes.getTargetConfig(), "targetTypeConfig123");
		assertEquals(targetTypes.getDataSourceName(), "targetTypeDataSource123");
		assertEquals(targetTypes.getTargetDesc(), "targetTypeDesc123");
		assertEquals(targetTypes.getDomain(), "targetTypeDomain123");
		assertEquals(targetTypes.getTargetName(), "targetTypeName123");
		
		return Optional.of(targetTypes);
	}
	
	@SuppressWarnings("unused")
	private List<TargetTypesDetails> getTargetTypesDetails() {
		List<TargetTypesDetails> allTargetTypesDetails = Lists.newArrayList();
		List<String> allAttributesName = Lists.newArrayList();
		allAttributesName.add("ABC");
		List<AttributeDetails> attributes = Lists.newArrayList();
		AttributeDetails attributeDetails = new AttributeDetails();
		attributeDetails.setName("name123");
		attributeDetails.setValue("value123");
		attributes.add(attributeDetails);
		TargetTypesDetails targetTypesDetails = new TargetTypesDetails();
		targetTypesDetails.setAdded(false);
		targetTypesDetails.setIncludeAll(false);
		targetTypesDetails.setTargetName("targetName");
		targetTypesDetails.setAllAttributesName(allAttributesName);
		targetTypesDetails.setAttributes(attributes);
		allTargetTypesDetails.add(targetTypesDetails);
		return allTargetTypesDetails;
	}
	
	
	private TargetTypesProjections getTargetTypesProjections() {
		return new TargetTypesProjections() {
			@Override
			public String getTargetName() {
				return "TargetName123";
			}

			@Override
			public String getTargetDesc() {
				return "TargetDesc123";
			}

			@Override
			public String getTargetConfig() {
				return "TargetConfig123";
			}

			@Override
			public String getEndpoint() {
				return "Endpoint123";
			}

			@Override
			public String getDomain() {
				return "Domain123";
			}

			@Override
			public String getDataSourceName() {
				return "DataSourceName123";
			}

			@Override
			public String getCategory() {
				return "Category123";
			}
		};
	}
}
