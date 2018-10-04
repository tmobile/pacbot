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
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.dto.AutoFixTransaction;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ESUtils;

// TODO: Auto-generated Javadoc
// not using the old way , this is the new class to publish data to ES , all old code will be refactored to use this one

/**
 * The Class ElasticSearchDataPublisher.
 */
public class ElasticSearchDataPublisher {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchDataPublisher.class);
    
    /** The client. */
    private RestHighLevelClient client;
    
   /**  rest client will be used to create RestHighLevelClient. */
    private RestClient restClient;
    
    /**  test mode flag *. */
    boolean testMode = false;

    /**
     * Instantiates a new elastic search data publisher.
     */
    public ElasticSearchDataPublisher() {
        restClient = RestClient.builder(new HttpHost(ESUtils.getESHost(), ESUtils.getESPort())).build();
        client = new RestHighLevelClient(restClient);
    }
    
    /**
     * Instantiates a new elastic search data publisher.
     *
     * @param testMode the test mode
     */
    public ElasticSearchDataPublisher(Boolean testMode) {
       this.testMode = testMode;
    }

    /**
     * Publish auto fix transactions.
     *
     * @param autoFixTrans the auto fix trans
     * @return the int
     */
    public int publishAutoFixTransactions(List<AutoFixTransaction> autoFixTrans) {

        if (autoFixTrans != null && autoFixTrans.size() == 0) {
            return 0;
        }

        BulkRequest bulkRequest = new BulkRequest();
        Gson gson = new Gson();

        for (AutoFixTransaction autoFixTransaction : autoFixTrans) {
            IndexRequest indexRequest = new IndexRequest(
                    CommonUtils.getPropValue(PacmanSdkConstants.AUTO_FIX_TRAN_INDEX_NAME_KEY),
                    CommonUtils.getPropValue(PacmanSdkConstants.AUTO_FIX_TRAN_TYPE_NAME_KEY));
            indexRequest.source(gson.toJson(autoFixTransaction), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        try {
            if(null!=client){
                   BulkResponse bulkResponse = client.bulk(bulkRequest);
                   if (bulkResponse.hasFailures()) {
                       if (!isIndexAvaialble(bulkResponse.getItems())) {
                           logger.info("index not found to write the transaction logs, creating one");
                            // version 5.6 does not support index creation via API,
                            // hence executing a post
                            try {
                                CommonUtils
                                    .doHttpPut(
                                        ESUtils.getEsUrl() + "/"
                                                + CommonUtils
                                                        .getPropValue(PacmanSdkConstants.AUTO_FIX_TRAN_INDEX_NAME_KEY),
                                        "");
                                publishAutoFixTransactions(autoFixTrans); // index should be created now
                            } catch (Exception e) {

                        logger.error("error creating index", e);
                        }
                       }
                   }
            }
        } catch (IOException e) {
            logger.error("error posting auto fix transaction log", e);
            return -1;
        }
        return 0;
    }

    /**
     * Checks if is index avaialble.
     *
     * @param bulkItemResponses the bulk item responses
     * @return the boolean
     */
    private Boolean isIndexAvaialble(BulkItemResponse[] bulkItemResponses) {
        // System.out.println(bulkItemResponses[0].getFailureMessage());
        // System.out.println(bulkItemResponses[0].getFailure().getMessage());
        return null == Arrays.stream(bulkItemResponses)
                .filter(x -> x.getFailure().getCause().getMessage().contains("no such index")).findAny().orElse(null);
    }
    
    
    /**
     * Close.
     */
    public void close(){
        if(null!=restClient)
            try {
                restClient.close();
            } catch (IOException e) {
                logger.error("error closing rest client" ,e);
            }
        
        client = null;
    }

}
