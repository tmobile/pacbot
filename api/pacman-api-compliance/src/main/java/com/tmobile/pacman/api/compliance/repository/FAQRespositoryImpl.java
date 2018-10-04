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
/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :santoshi
  Modified Date: Jan 31, 2018

 **/
package com.tmobile.pacman.api.compliance.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

/**
 * The Class FAQRespositoryImpl.
 */
@Repository
public class FAQRespositoryImpl implements FAQRepository, Constants {

    /** The rdsepository. */
    @Autowired
    private PacmanRdsRepository rdsepository;

    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    /** The es host. */
    @Value("${elastic-search.host}")
    private String esHost;

    /** The es port. */
    @Value("${elastic-search.port}")
    private int esPort;

    /** The Constant PROTOCOL. */
    static final String PROTOCOL = "http";

    /** The es url. */
    private String esUrl;

    /** The logger. */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Inits the.
     */
    @PostConstruct
    void init() {
        esUrl = PROTOCOL + "://" + esHost + ":" + esPort;
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.FAQRepository#getFAQSFromEs(java.lang.String, java.lang.String)
     */
    @Override
    public List<Map<String, Object>> getFAQSFromEs(String widgetId,
            String domainId) throws DataException {
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(CommonUtils.convertAttributetoKeyword("widgetid"),
                widgetId);
        if (!Strings.isNullOrEmpty(domainId)) {
            mustFilter.put(CommonUtils.convertAttributetoKeyword("domainid"),
                    domainId);
        }
       try{
        return elasticSearchRepository.getSortedDataFromES("faqs", "faqinfo",
                mustFilter, null, null, null, null, null);
       }catch(Exception e){
           throw new DataException(e);
       }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.FAQRepository#getRelevantFAQSFromEs(java.lang.String, java.lang.String, java.util.List, java.util.List, java.util.List)
     */
    @Override
    public List<Map<String, Object>> getRelevantFAQSFromEs(String widgetId,
            String domainId, List<String> tag, List<String> faqid,
            List<Map<String, Object>> releventfaqs) throws DataException {
        // Get All FAQ's assigned to to this widgetId from ES.
        JsonParser jsonParser = new JsonParser();
        StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/")
                .append("faqs/faqinfo").append("/").append("_search");
        StringBuilder requestFaqBody = new StringBuilder(
                "{\"_source\":[\"faqname\",\"answer\"],\"query\":{\"bool\":{\"must\":[{\"terms\":{\"tag.keyword\":"
                        + tag
                        + "}},{\"match\":{\"domainid.keyword\":\""
                        + domainId
                        + "\"}}],\"must_not\":[{\"terms\":{\"faqid.keyword\":"
                        + faqid + "}}]}}}");
        String responseFaqJson ;
       try{
           responseFaqJson= PacHttpUtils.doHttpPost(

                urlToQueryBuffer.toString(), requestFaqBody.toString());
       }catch(Exception e){
           throw new DataException(e);
       }
        JsonObject resultFaqJson = (JsonObject) jsonParser
                .parse(responseFaqJson);
        JsonObject hitsFaq = (JsonObject) resultFaqJson.get("hits");
        JsonArray jsonArrayFaq = hitsFaq.get("hits").getAsJsonArray();
        for (int j = 0; j < jsonArrayFaq.size(); j++) {
            JsonObject sourceFaq = (JsonObject) jsonArrayFaq.get(j)
                    .getAsJsonObject().get("_source");
            ConcurrentHashMap<String, Object> relectfaqDetails = new ConcurrentHashMap<>();
            relectfaqDetails.put("faqName", sourceFaq.get("faqname")
                    .getAsString());
            relectfaqDetails.put("faqAnswer", sourceFaq.get("answer")
                    .getAsString());
            releventfaqs.add(relectfaqDetails);

        }
        return releventfaqs;
    }
}
