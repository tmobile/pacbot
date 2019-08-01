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

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.pacman.commons.autofix.manager.NextStepManager;
import com.tmobile.pacman.util.ESUtils;

/**
 * @author kkumar28
 *
 */
public class ElasticSearchDataInterface implements AutoCloseable{
    
    
    
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchDataInterface.class);

    
    /** The client. */
    protected RestHighLevelClient client;
    
   /**  rest client will be used to create RestHighLevelClient. */
    protected RestClient restClient;
    
    
    /**
     * Instantiates a new elastic search data publisher.
     * @throws MalformedURLException 
     */
    public ElasticSearchDataInterface() throws MalformedURLException {
        restClient = RestClient.builder(new HttpHost(ESUtils.getESHost(), ESUtils.getESPort())).build();
        client = new RestHighLevelClient(restClient);
    }

    
    /* (non-Javadoc)
     * @see java.lang.AutoCloseable#close()
     */
    @Override
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
