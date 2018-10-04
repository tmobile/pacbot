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
package com.tmobile.cloud.awsrules.compliance;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SpacewalkAndSatelliteManager {
    private static final Logger logger = LoggerFactory
            .getLogger(SpacewalkAndSatelliteManager.class);

    private SpacewalkAndSatelliteManager() {

    }

    /**
     * This method used to get the Kernel Version of an instance.
     * 
     * @param instanceId
     * @return String, if kernel version available else null
     */

    public static String getQueryfromRhnElasticSearch(String instanceId,
            String satAndSpacewalkApi) {
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = new JsonArray();

        try {
            HttpClient client = HttpClientBuilder.create().build();

            URL url = new URL(satAndSpacewalkApi);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(),
                    url.getHost(), url.getPort(), url.getPath(),
                    url.getQuery(), url.getRef());

            // prepare Json pay load for GET query.
            JsonObject innerJson = new JsonObject();
            JsonObject matchPhrase = new JsonObject();
            JsonObject must = new JsonObject();
            JsonObject bool = new JsonObject();
            JsonObject query = new JsonObject();

            innerJson.addProperty("instanceid", instanceId);
            matchPhrase.add("match_phrase", innerJson);
            must.add("must", matchPhrase);
            bool.add("bool", must);
            query.add("query", bool);
            StringEntity strjson = new StringEntity(query.toString());

            // Qurying the ES
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(uri);
            httpPost.setEntity(strjson);
            httpPost.setHeader("Content-Type", "application/json");
            HttpResponse response = client.execute(httpPost);

            String jsonString = EntityUtils.toString(response.getEntity());
            JsonObject resultJson = (JsonObject) jsonParser.parse(jsonString);
            String hitsJsonString = resultJson.get("hits").toString();
            JsonObject hitsJson = (JsonObject) jsonParser.parse(hitsJsonString);
            jsonArray = hitsJson.getAsJsonObject().get("hits").getAsJsonArray();
            if (jsonArray.size() > 0) {
                JsonObject firstObject = (JsonObject) jsonArray.get(0);
                JsonObject sourceJson = (JsonObject) firstObject.get("_source");
                if (sourceJson != null) {
                    JsonElement osVersion = sourceJson.get("kernelid");
                    if (osVersion != null) {
                        return osVersion.toString().substring(1,
                                osVersion.toString().length() - 1);
                    }
                }
            } else {
                logger.info("no records found in ElasticSearch");
            }
        } catch (MalformedURLException me) {
            logger.error(me.getMessage());
        } catch (UnsupportedEncodingException ue) {
            logger.error(ue.getMessage());
        } catch (ClientProtocolException ce) {
            logger.error(ce.getMessage());
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
        } catch (URISyntaxException use) {
            logger.error(use.getMessage());
        }

        return null;
    }
}
