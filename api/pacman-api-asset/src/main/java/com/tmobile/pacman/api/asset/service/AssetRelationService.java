package com.tmobile.pacman.api.asset.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.repository.AssetRelationRepository;
import com.tmobile.pacman.api.asset.repository.AssetRepository;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;

@Service
public class AssetRelationService {
	
	@Autowired
	private AssetRepository assetRepository;
	
	@Autowired
	private AssetRelationRepository assetRelationRepository;
	
	private static final Log LOGGER = LogFactory.getLog(AssetRelationService.class);

	@Cacheable(value = "resultList", key = "{#resourceType, #relation, #fields}")
	public List<Map<String, Object>> getRelationAssets(String resourceType,String relation,String fields) throws ServiceException {
		if(assetRelationRepository.isTargetTypeExists(resourceType)) {
			String datasource = assetRepository.getDataSourceForTargetTypes(Lists.newArrayList(resourceType)).get(0).get(Constants.PROVIDER).toString();
			if(isRelatedTypeExists(datasource+"_"+resourceType,resourceType+"_"+relation)) {
				List<String> fieldsList = new ArrayList<>();
				if(StringUtils.isNotBlank(fields)) {
					fieldsList = Arrays.asList(fields.split(","));
				}
				
				List<Map<String, Object>> relationAssetsTemp = new ArrayList<>();
				try {
					relationAssetsTemp = assetRelationRepository.getRelationAssets(datasource, resourceType, relation, fieldsList);
				} catch (DataException e) {
					LOGGER.error("Error while getting related assets");
					throw new ServiceException("Error while getting related assets");
				}
				List<Map<String, Object>> relationAssets = new ArrayList<>();
				relationAssetsTemp.parallelStream().forEach(relationAsset -> {
					if(!relationAsset.isEmpty()) {
						relationAsset.remove(Constants._ID);
						relationAsset.remove(Constants.ES_DOC_PARENT_KEY);
						relationAsset.remove(Constants.ES_DOC_ROUTING_KEY);
						relationAsset.remove(AssetConstants.DISCOVERY_DATE);
						relationAsset.remove(AssetConstants.UNDERSCORE_LOADDATE);
						relationAsset.remove("_cloudType");
						relationAssets.add(relationAsset);
					}
				});
				return relationAssets;
			} else {
				throw new ServiceException("Related type is not valid. Valid types - "+ 
						StringUtils.join(getValidRelatedTypes(datasource, resourceType), ","));
			}
		} else {
			throw new ServiceException("Resource type does not exist");
		}
	}
	
	public List<String> getRelatedTypes(String resourceType) throws ServiceException {
		if(assetRelationRepository.isTargetTypeExists(resourceType)) {
			String datasource = assetRepository.getDataSourceForTargetTypes(Lists.newArrayList(resourceType)).get(0).get(Constants.PROVIDER).toString();
			return getValidRelatedTypes(datasource, resourceType);
		} else {
			throw new ServiceException("Resource type does not exist");
		}
	}
	
	private List<String> getValidRelatedTypes(String datasource, String resourceType) throws ServiceException {
		
		List<String> relatedTypes = new ArrayList<>();
		String index = datasource+"_"+resourceType;
		Response response = assetRepository.invokeAPI("GET", index + "/_mappings", null);
		try {
			String responseStr = "";
			if (null != response) {
				responseStr = EntityUtils.toString(response.getEntity());
			}
			if (responseStr.contains(AssetConstants.RESPONSE_ERROR)) {
				throw new ServiceException("Error while fetching Related Types");
			} else {
				JsonParser parser = new JsonParser();
		        JsonObject responseDetailsjson = parser.parse(responseStr).getAsJsonObject();
		        Set<String> mappings = responseDetailsjson.getAsJsonObject(index).getAsJsonObject("mappings").keySet();
		        for(String mapping : mappings) {
		        	if(mapping.startsWith(resourceType+"_")) {
		        		relatedTypes.add(mapping.replace(resourceType+"_", ""));
		        	}
		        }
			}
		} catch (Exception e) {
			LOGGER.error("Error while fetching Related Types", e);
			throw new ServiceException("Error while fetching Related Types");
		}
		return relatedTypes;
	}
	
	private boolean isRelatedTypeExists(String indexName, String type) {
	    Response response = assetRepository.invokeAPI("HEAD", indexName + "/_mapping/" + type, null);
		if (response != null) {
		    return response.getStatusLine().getStatusCode() == 200 ? true : false;
		}
	    return false;
	}
}
