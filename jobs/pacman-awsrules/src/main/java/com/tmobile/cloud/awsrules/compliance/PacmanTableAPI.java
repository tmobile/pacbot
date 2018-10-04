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

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;

public class PacmanTableAPI {

    private PacmanTableAPI(){
        
    }
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PacmanTableAPI.class);

    /**
     * Method for getting the quarterly kernel versions via PACMAN API
     *  
     * @return Quarterly Kernel versions as JSONObject
     */
    public static JsonObject getKernelVersionFromPacmanTable(String url) {
        JsonObject versions = null;
        try {
            HttpEntity resultString = null;
            HttpResponse httpResponse = httpGenericMethod(url);
            if(httpResponse!=null){
             resultString = httpResponse.getEntity();
            }
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(
                    EntityUtils.toString(resultString), JsonObject.class);
            if (!jsonObject.has("exception")) {
                versions = jsonObject;
            }
        } catch (Exception e) {
            LOGGER.error("Exceptions occured getKernelVersionFromPacmanTable==========", e);
        }
        return versions;
    }

    public static String getKernelVersionFromRHNSystemDetails(
            String instanceId, String kernelVersionByInstanceIdUrl) {
        String url = kernelVersionByInstanceIdUrl + instanceId;
        String jsonKernelVersion = null;
        String resultStringPost = null;
        JsonObject jsonObject = null;
        JsonObject jsonData = null;
        JsonObject jsonResponse = null;
        Gson gson = new Gson();
        try {

            resultStringPost = httpGetMethod(url);
            if (!StringUtils.isEmpty(resultStringPost)) {
                jsonObject = gson.fromJson(resultStringPost, JsonObject.class);

                jsonData = (JsonObject) jsonObject.get("data");
                if(jsonData.get("response").isJsonObject()){
                jsonResponse = (JsonObject) jsonData.get("response");
                if (jsonResponse.size() > 0) {
                    jsonKernelVersion = jsonResponse.get("kernelVersion")
                            .getAsString();
                }
                }
            }

        } catch (Exception e) {
            LOGGER.error("Exceptions occured in getKernelVersionFromRHNSystemDetails========",e);
            return null;
        }
        return jsonKernelVersion;
    }

    public static HttpResponse httpGenericMethod(String url) {
        HttpResponse response = null;
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf).build();
            HttpGet httpGet = new HttpGet(url);
            response = httpclient.execute(httpGet);

        } catch (Exception e) {
            LOGGER.error("Error occured while http GET" , e);
        }
        return response;
    }

    public static String httpGetMethod(String url) throws Exception {
        String json = null;
        // Some custom method to craete HTTP post object
        HttpGet post = new HttpGet(url);
        CloseableHttpClient httpClient = null;
        post.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        try {
            // Get http client
            httpClient = PacmanUtils.getCloseableHttpClient();

            // Execute HTTP method
            CloseableHttpResponse res = httpClient.execute(post);

            // Verify response
            if (res.getStatusLine().getStatusCode() == 200) {
                json = EntityUtils.toString(res.getEntity());
            }
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
        return json;
    }
}
