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
  Author :kkumar
  Modified Date: Dec 18, 2017

**/
/*
 *Copyright 2016-2017 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 *Licensed under the Amazon Software License (the "License"). You may not use
 * this file except in compliance with the License. A copy of the License is located at
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tmobile.pacman.api.commons.repo;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

/**
 * @author kkumar
 *
 */
@Repository
@ConditionalOnProperty(prefix="elastic-search",name="host-heimdall")
public class HeimdallElasticSearchRepository {


	@Value("${elastic-search.host-heimdall}")
	private String		esHost;
	@Value("${elastic-search.port-heimdall}")
	private int			esPort;

	final String		protocol	= "http";

	private String		esUrl;

	protected final Log	logger		= LogFactory.getLog(getClass());

	@PostConstruct
	void init()
	{
		esUrl = protocol + "://" + esHost + ":" + esPort;
	}

	public JsonArray getEventsProcessed() throws Exception
	{
		String responseJson = null;
		JsonParser jsonParser;
		JsonObject resultJson;
		StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append("blackbox").append("/").append("_search");

		StringBuilder requestBody = new StringBuilder(
				"{\"size\":0,\"query\":{\"range\":{\"eventTime\":{\"gte\":\"now-1d/d\"}}},\"aggs\":{\"events-per-day\":{\"date_histogram\":{\"field\":\"eventTime\",\"interval\":\"day\",\"order\":{\"_key\":\"desc\"}}}}}");

		try
		{
			responseJson = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
		} catch (Exception e)
		{
			logger.error("error retrieving inventory from ES", e);
			throw new Exception(e);
		}
		jsonParser = new JsonParser();
		resultJson = (JsonObject) jsonParser.parse(responseJson);
		JsonObject aggsJson = (JsonObject) jsonParser.parse(resultJson.get("aggregations").toString());
		return aggsJson.getAsJsonObject("events-per-day").getAsJsonArray("buckets");
	}

}
