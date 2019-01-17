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

import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.config.PacmanConfiguration;
import com.tmobile.pacman.api.admin.domain.AssetGroupTargetTypes;
import com.tmobile.pacman.api.admin.domain.AttributeValuesRequest;
import com.tmobile.pacman.api.admin.domain.CreateUpdateTargetTypeDetailsRequest;
import com.tmobile.pacman.api.admin.domain.TargetTypeAttribute;
import com.tmobile.pacman.api.admin.domain.TargetTypesDetails;
import com.tmobile.pacman.api.admin.domain.TargetTypesProjections;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.TargetTypesRepository;
import com.tmobile.pacman.api.admin.repository.model.TargetTypes;

/**
 * TargetTypes Service Implementations
 */
@Service
public class TargetTypesServiceImpl implements TargetTypesService {

	private static final Logger log = LoggerFactory.getLogger(TargetTypesServiceImpl.class);
	
	@Autowired
	private PacmanConfiguration config;
	
	@Autowired
	private TargetTypesRepository targetTypesRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	private static RestClient restClient;

	@Override
	public Collection<String> getTargetTypesNamesByDataSourceName(final String dataSourceName) {
		return targetTypesRepository.findByDataSourceName(dataSourceName);
	}

	@Override
	public List<TargetTypes> getAllTargetTypesByDomainList(final List<String> domains) {
		return targetTypesRepository.findByDomainIn(domains);
	}

	@Override
	public Page<TargetTypesProjections> getAllTargetTypeDetails(String searchTerm, int page, int size) {
		return targetTypesRepository.findAllTargetTypeDetails(searchTerm.toLowerCase(), new PageRequest(page, size));
	}

	@Override
	public List<String> getAllTargetTypesCategories() {
		return config.getTargetTypes().getCategories();
	}

	@Override
	public List<TargetTypesDetails> getAllTargetTypes(List<AssetGroupTargetTypes> selectedTargetTypes) {
		Map<String, Integer> selectedTargetTypeIndex = buildSelectedTargetTypesIndex(selectedTargetTypes);
		List<TargetTypesDetails> attributes = Lists.newArrayList(); 
		List<TargetTypes> allTargets = targetTypesRepository.findAll();
		for (TargetTypes targetType : allTargets) {
			String targetName = targetType.getTargetName().trim();
			Integer index = selectedTargetTypeIndex.get(targetName.toLowerCase());
			if(index==null) {
				String dataSourceName = targetType.getDataSourceName().trim();
				TargetTypesDetails targetTypeAttribute = new TargetTypesDetails();
				targetTypeAttribute.setAttributes(Lists.newArrayList());
				targetTypeAttribute.setTargetName(targetType.getTargetName().trim());
				targetTypeAttribute.setAllAttributesName(getFieldNames(dataSourceName + "_" + targetName, targetName));
				targetTypeAttribute.setIncludeAll(false);
				attributes.add(targetTypeAttribute);
			} else {
				String dataSourceName = targetType.getDataSourceName().trim();
				TargetTypesDetails targetTypeAttribute = new TargetTypesDetails();
				targetTypeAttribute.setTargetName(targetType.getTargetName().trim());
				targetTypeAttribute.setAllAttributesName(getFieldNames(dataSourceName + "_" + targetName, targetName));
				attributes.add(targetTypeAttribute);
			}
		}
		return attributes;
	}

	private Map<String, Integer> buildSelectedTargetTypesIndex(List<AssetGroupTargetTypes> selectedTargetTypes) {
		Map<String, Integer> selectedTargetTypeIndex = Maps.newHashMap();
		for (int index = 0; index < selectedTargetTypes.size(); index++) {
			selectedTargetTypeIndex.put(selectedTargetTypes.get(index).getTargetType().toLowerCase(), index);
		}
		return selectedTargetTypeIndex;
	}

	@Override
	public List<TargetTypeAttribute> getTargetTypeAttributes(final List<TargetTypes> targetTypes) {
		List<TargetTypeAttribute> attributes = Lists.newArrayList();
		for (TargetTypes targetType : targetTypes) {
			String targetName = targetType.getTargetName().trim();
			String dataSourceName = targetType.getDataSourceName().trim();
			TargetTypeAttribute targetTypeAttribute = new TargetTypeAttribute();
			targetTypeAttribute.setAttributes(Lists.newArrayList());
			targetTypeAttribute.setTargetName(targetType.getTargetName().trim());
			targetTypeAttribute.setAllAttributesName(getFieldNames(dataSourceName + "_" + targetName, targetName));
			targetTypeAttribute.setIncludeAll(false);
			targetTypeAttribute.setIndex("/"+dataSourceName + "_" + targetName);
			attributes.add(targetTypeAttribute);
		}
		return attributes;
	}

	@Override
	public Map<String, Object> getAttributeValues(AttributeValuesRequest attributeValuesRequest) {
		try {
			Response response = invokeAPI("GET", attributeValuesRequest.getIndex(), attributeValuesRequest.getPayload());
			if(response != null) {
				String attributeValues = EntityUtils.toString(response.getEntity());
				return mapper.readValue(attributeValues, new TypeReference<Map<String, Object>>(){});
			}
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
		}
		return Maps.newHashMap();
	}
	

	@Override
	public TargetTypes getTargetTypesByName(String targetTypeName) throws PacManException {
		boolean isTargetTypeExits = targetTypesRepository.existsById(targetTypeName);
		if (!isTargetTypeExits) {
			throw new PacManException(AdminConstants.TARGET_TYPE_NAME_NOT_EXITS);
		} else {
			return targetTypesRepository.findById(targetTypeName).get();
		}
	}
	

	@Override
	public String updateTargetTypeDetails(CreateUpdateTargetTypeDetailsRequest targetTypesDetails, final String userId) throws PacManException {
		targetTypesDetails.setName(targetTypesDetails.getName().toLowerCase().trim().replaceAll(" ", "-"));
		boolean isTargetTypeExits = targetTypesRepository.existsById(targetTypesDetails.getName());
		if (isTargetTypeExits) {
			Date currentDate = new Date();
			TargetTypes existingTargetType = targetTypesRepository.findById(targetTypesDetails.getName().trim()).get();
			existingTargetType.setTargetDesc(targetTypesDetails.getDesc());
			existingTargetType.setCategory(targetTypesDetails.getCategory());
			existingTargetType.setDataSourceName(targetTypesDetails.getDataSource());
			existingTargetType.setTargetConfig(targetTypesDetails.getConfig());
			existingTargetType.setUserId(userId);
			String endpoint = config.getElasticSearch().getDevIngestHost() + ":" + config.getElasticSearch().getDevIngestPort() + "/" + targetTypesDetails.getDataSource() + "_"+ targetTypesDetails.getName() + "/" + targetTypesDetails.getName();
			existingTargetType.setEndpoint(endpoint);
			existingTargetType.setModifiedDate(currentDate);
			existingTargetType.setDomain(targetTypesDetails.getDomain());
			targetTypesRepository.save(existingTargetType);
			return AdminConstants.TARGET_TYPE_UPDATION_SUCCESS;
		} else {
			throw new PacManException(AdminConstants.TARGET_TYPE_NAME_NOT_EXITS);
		}
	}
	
	@Override
	public String addTargetTypeDetails(final CreateUpdateTargetTypeDetailsRequest targetTypeDetailsRequest, final String userId) throws PacManException {
		try { 
			String dataSource = targetTypeDetailsRequest.getDataSource().toLowerCase().trim();
			String type = targetTypeDetailsRequest.getName().toLowerCase().trim().replaceAll(" ", "-");
			String indexName = dataSource+"_"+type;
			StringBuilder payLoad = new StringBuilder("{ \"mappings\": {");
			payLoad.append("\""+type+"\":{},\"issue_"+type+"\": { \"_parent\": {\"type\": \""+type+"\"}},\"recommendation_"+type+"\": { \"_parent\": {\"type\": \""+type+"\"}},\"issue_"+type+"_audit\": { \"_parent\": {\"type\": \"issue_"+type+"\"}},\"issue_"+type+"_comment\": { \"_parent\": {\"type\": \"issue_"+type+"\"}},\"issue_"+type+"_exception\": { \"_parent\": {\"type\": \"issue_"+type+"\"}}");
	        payLoad.append("}}");
	        if(!indexExists(indexName)) {
	        	Response indexResponse = invokeAPI("PUT", indexName, payLoad.toString());
		        Response aliasResponse = invokeAPI("PUT", "/"+indexName+"/_alias/"+dataSource,null);
				if ((aliasResponse != null) && (indexResponse != null)) {
					if ((indexResponse.getStatusLine().getStatusCode() == 200) && (aliasResponse.getStatusLine().getStatusCode() == 200)) {
						return processTargetTypeCreation(targetTypeDetailsRequest, userId);
					} else {
						throw new PacManException(AdminConstants.TARGET_TYPE_CREATION_FAILURE);
					}
				} else {
					throw new PacManException(AdminConstants.TARGET_TYPE_CREATION_FAILURE);
				}
	        } else {
	        	throw new PacManException(AdminConstants.TARGET_TYPE_INDEX_EXITS);
	        }
		} catch(Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			throw new PacManException(AdminConstants.TARGET_TYPE_CREATION_FAILURE);
		}
	}
	
	private String processTargetTypeCreation(final CreateUpdateTargetTypeDetailsRequest targetTypeRequest, final String userId) throws PacManException {
		String targetName = targetTypeRequest.getName().toLowerCase().trim().replaceAll(" ", "-");
		targetTypeRequest.setName(targetName);
		boolean isTargetTypeExits = targetTypesRepository.existsById(targetName);
		if (!isTargetTypeExits) {
			Date currentDate = new Date();
			TargetTypes newTargetType = new TargetTypes();
			newTargetType.setTargetName(targetName);
			newTargetType.setTargetDesc(targetTypeRequest.getDesc());
			newTargetType.setCategory(targetTypeRequest.getCategory());
			newTargetType.setDataSourceName(targetTypeRequest.getDataSource());
			newTargetType.setTargetConfig(targetTypeRequest.getConfig());
			newTargetType.setStatus("");
			newTargetType.setUserId(userId);
			String endpoint = config.getElasticSearch().getDevIngestHost() + ":" + config.getElasticSearch().getDevIngestPort() + "/" + targetTypeRequest.getDataSource() + "_"+ targetName + "/" + targetName;
			newTargetType.setEndpoint(endpoint);
			newTargetType.setCreatedDate(currentDate);
			newTargetType.setModifiedDate(currentDate);
			newTargetType.setDomain(targetTypeRequest.getDomain());
			targetTypesRepository.save(newTargetType);
			return AdminConstants.TARGET_TYPE_CREATION_SUCCESS;
		} else {
			try {
				invokeAPI("DELETE", "aws_"+targetName, null);
			} catch (Exception exception) {
				log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			}
			throw new PacManException(AdminConstants.TARGET_TYPE_NAME_EXITS);
		}
	}

	private boolean indexExists(String indexName){
		Response response = invokeAPI("HEAD",indexName,null);
		if(response!=null){
			return response.getStatusLine().getStatusCode() == 200?true:false;
		}
		return false;
	}

	private List<String> getFieldNames(String index, String type){
		String response;
		List<String> attributes = Lists.newArrayList();
		try {
			Response responseDetails = invokeAPI("GET", index+"/_mapping/"+type, null);
			if(responseDetails != null) {
				response = EntityUtils.toString(responseDetails.getEntity());
				JsonNode properties = mapper.readTree(response).at("/"+index+"/mappings/"+type+"/properties");
				JsonNode tags = mapper.readTree(response).at("/"+index+"/mappings/"+type+"/properties/tags/properties");
				
				Iterator<String> it = properties.fieldNames();
				List<String> ignored = Arrays.asList( "tags","query","_resourceid","latest");
				while(it.hasNext()){
					String attribute = it.next();
					if(!ignored.contains(attribute))
						attributes.add(attribute);
				}
				
				it = tags.fieldNames();
				while(it.hasNext()){
					String attribute = it.next();
					attributes.add("tags."+attribute);
				}
			}
		} catch (Exception exception) {
			log.error(UNEXPECTED_ERROR_OCCURRED, exception);
		}
		return attributes;
	}
	
	private Response invokeAPI(String method, String endpoint, String payLoad) {
		HttpEntity entity = null;
        try {
            if (payLoad != null) {
                entity = new NStringEntity(payLoad, ContentType.APPLICATION_JSON);
            }
            if(!endpoint.startsWith("/")) {
            	endpoint = "/"+endpoint;
            }
            return getRestClient().performRequest(method, endpoint, Collections.<String, String>emptyMap(), entity);
        } catch (IOException exception) {
        	log.error(UNEXPECTED_ERROR_OCCURRED, exception);
		} finally {
			if (entity != null) {
				try {
					EntityUtils.consume(entity);
				} catch (IOException exception) {
					log.error(UNEXPECTED_ERROR_OCCURRED, exception);
				}
			}
		}
        return null; 
	}
	
	private RestClient getRestClient() {
        if (restClient == null) {
        	String esHost = config.getElasticSearch().getDevIngestHost();
    		int esPort = config.getElasticSearch().getDevIngestPort();
            RestClientBuilder builder = RestClient.builder(new HttpHost(esHost, esPort));
            builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                    return requestConfigBuilder.setConnectionRequestTimeout(0);
                }
            });
            restClient = builder.build();
        }
        return restClient;
    }

	@Override
	public void deleteIndex(String targetTypeIndex) {
		invokeAPI("DELETE", "aws_"+targetTypeIndex, null);
	}
}
