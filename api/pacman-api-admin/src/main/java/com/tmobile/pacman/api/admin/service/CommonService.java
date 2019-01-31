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
package com.tmobile.pacman.api.admin.service;

import static com.tmobile.pacman.api.admin.common.AdminConstants.QUERY;
import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.config.PacmanConfiguration;

/**
 * Common Service Implementations
 */
@Service
public class CommonService {
	
	private static final Logger log = LoggerFactory.getLogger(CommonService.class);
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private PacmanConfiguration config;
	
	private RestClient restClient;
	
	public List<String> getFieldNames(String index, String type){
		String response;
		List<String> attributes = Lists.newArrayList();
		Response responseDetails = invokeAPI("GET", index+"/_mapping/"+type, null);
		try {
			if(Objects.nonNull(responseDetails)) {
				response = EntityUtils.toString(responseDetails.getEntity());
				JsonNode properties = mapper.readTree(response).at("/"+index+"/mappings/"+type+"/properties");
				JsonNode tags = mapper.readTree(response).at("/"+index+"/mappings/"+type+"/properties/tags/properties");
				
				Iterator<String> it = properties.fieldNames();
				List<String> ignored = Arrays.asList( "tags", QUERY,"_resourceid","latest");
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
	
	public Response invokeAPI(String method, String endpoint, String payLoad) {
		HttpEntity entity = null;
		if (payLoad != null) {
            entity = new NStringEntity(payLoad, ContentType.APPLICATION_JSON);
        }
		if(!endpoint.startsWith("/")) {
        	endpoint = "/"+endpoint;
        }
        try {
            return getRestClient().performRequest(method, endpoint, Collections.<String, String>emptyMap(), entity);
        } catch (IOException exception) {
        	log.error(UNEXPECTED_ERROR_OCCURRED, exception);
        } finally {
        	if(entity != null) {
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
			RequestConfigCallback requestConfigCallback = requestConfigBuilder -> requestConfigBuilder
					.setConnectionRequestTimeout(0);
			builder.setRequestConfigCallback(requestConfigCallback);
			restClient = builder.build();
		}
		return restClient;
	}
}
