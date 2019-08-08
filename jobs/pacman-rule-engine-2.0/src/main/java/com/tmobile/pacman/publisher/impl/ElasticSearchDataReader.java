/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :kkumar28
  Modified Date: Jul 2, 2019
  
**/
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
package com.tmobile.pacman.publisher.impl;

import java.io.IOException;
import java.net.MalformedURLException;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.tmobile.pacman.commons.autofix.AutoFixPlan;
import com.tmobile.pacman.util.ESUtils;


/**
 * @author kkumar28
 *
 */
public class ElasticSearchDataReader extends ElasticSearchDataInterface {

    
    
    /**
     * @throws MalformedURLException 
     * 
     */
    public ElasticSearchDataReader() throws Exception {
        super();
    }
    
    
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchDataReader.class);

    
    /**
     * 
     * @param index
     * @param type
     * @param id
     * @return
     * @throws IOException
     */
    public String getDocumentById(String index,String type, String id,String parentId) throws IOException{
        GetRequest req = new GetRequest(index,type,id);
        req.routing(parentId);
        GetResponse response =    client.get(req);
        return response.getSourceAsString();
    }
    
    
    /**
     * @param indexNameFromRuleParam
     * @param autoFixPlanType
     * @param resourceId
     * @return
     * @throws IOException 
     */
    public String searchDocument(String indexNameFromRuleParam, String autoFixPlanType, String resourceId) throws IOException {
        
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
        sourceBuilder.query(QueryBuilders.termQuery(ESUtils.convertAttributetoKeyword("resourceId"), resourceId)); 
        SearchRequest searchRequest = new SearchRequest(indexNameFromRuleParam);
        searchRequest.types(autoFixPlanType);
        searchRequest.source(sourceBuilder);
       
        logger.debug("searching auto fix plan with query " ,searchRequest.toString());

        SearchResponse response  = client.search(searchRequest);
        SearchHits hits = response.getHits();
        if(RestStatus.OK==response.status() && hits.totalHits>0){
          return  hits.getAt(0).getSourceAsString();
        }else{
            throw new IOException(String.format("no plan found for resource %s",resourceId));
        }
    }
    
}
