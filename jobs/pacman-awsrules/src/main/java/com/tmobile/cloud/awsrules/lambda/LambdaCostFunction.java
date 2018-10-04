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
  Modified Date: Sep 15, 2017

 **/
package com.tmobile.cloud.awsrules.lambda;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;

public class LambdaCostFunction
        implements
        RequestHandler<Map<String, Map<String, String>>, Map<String, String>> {
    private static final Logger logger = LoggerFactory
            .getLogger(LambdaCostFunction.class);

    /**
     * The method will get triggered from Lambda Function with following
     * parameters
     *
     * @param input
     *
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     *
     *            ruleParam : Value of the rule param<br>
     * <br>
     *
     *            resource : Value of the resource<br>
     * <br>
     *
     * @param context
     *            null
     *
     */

    @Override
    public Map<String, String> handleRequest(
            Map<String, Map<String, String>> input, Context context) {
        Map<String, String> resource = input.get("resource");
        String functionarn = resource.get("functionarn");
        Map<String, String> funResponse = new HashMap<>();
        String startDate = null;
        String endDate = null;
        LocalDate todayDate = LocalDate.now();
        LocalDate yesterdayDate = todayDate.minusDays(30);

        startDate = yesterdayDate + " " + "08:00:00";// "2017-09-11 08:00:00"
        endDate = todayDate + " " + "07:59:59"; // "2017-09-12 07:59:59"
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("start_date", startDate);
        jsonObj.addProperty("end_date", endDate);
        JsonArray jsonArray = new JsonArray();
        jsonObj.add("applications", jsonArray);
        jsonObj.add("services", jsonArray);
        jsonObj.add("accounts", jsonArray);
        jsonObj.add("environments", jsonArray);
        JsonArray jsonArrayFunction = new JsonArray();
        jsonArrayFunction.add(functionarn);
        jsonObj.add("resource_id", jsonArrayFunction);
        jsonObj.addProperty("interval", "Monthly");
        JsonArray jsonArraySvc = new JsonArray();
        jsonArraySvc.add("services");
        jsonObj.add("group_by", jsonArraySvc);
        int cost = 0;// zero
        String costStr = null;

        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(
                    "{COST_API_HOST}/api/get-cloud-cost");
            StringEntity jsonString;
            jsonString = new StringEntity(jsonObj.toString());
            request.addHeader("content-type", "application/json");
            request.addHeader("cache-control", "no-cache");
            request.addHeader("Accept", "application/json");
            request.setEntity(jsonString);
            HttpResponse response = httpClient.execute(request);
            logger.info("calling api to get cost");
            if (response.getStatusLine().getStatusCode() == 200) {
                logger.info("cost api executed successfully");
                httpClient.close();
                String responseEntity = EntityUtils.toString(response
                        .getEntity());
                JsonParser jsonParser = new JsonParser();
                JsonObject responseJson = (JsonObject) jsonParser
                        .parse(responseEntity);
                Object dataObj = responseJson.get("data");
                String dataStr = dataObj.toString();
                JsonObject dataJson = (JsonObject) jsonParser.parse(dataStr);
                Object bucketsObj = dataJson.get("buckets");
                String bucketsStr = bucketsObj.toString();
                JsonArray bucketsJsonArray = (JsonArray) jsonParser
                        .parse(bucketsStr);
                String bucketVal = bucketsJsonArray.toString();
                bucketVal = (bucketVal.substring(1, bucketVal.length() - 1));
                JsonObject bucketJson = (JsonObject) jsonParser
                        .parse(bucketVal);
                JsonElement costElement = bucketJson.get("cost");
                cost = costElement.getAsInt();
                costStr = costElement.getAsString();
                logger.info(costStr," : cost");

            }
        } catch (Exception e) {
           logger.error(e.getMessage());
           throw new RuleExecutionFailedExeption(e.getMessage());
        }
        if (cost < 5) {

            funResponse.put("httpStatus", "555");
            Map<String, Object> errorPayload = new HashMap();
            errorPayload.put("errorType", "InternalServerError");
            errorPayload.put("httpStatus", 555);
            errorPayload.put("requestId", context.getAwsRequestId());
            errorPayload.put("message",
                    "An unknown error has occurred. Please try again.");


            String message = errorPayload.toString();
            throw new RuleExecutionFailedExeption(message);

        }

        return funResponse;

    }


}
