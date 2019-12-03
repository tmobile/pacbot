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
 * Utility functions for ASGC Rules
 */
package com.tmobile.cloud.awsrules.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.util.CollectionUtils;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;

public class PacmanUtils {
    private static final Logger logger = LoggerFactory.getLogger(PacmanUtils.class);

    private PacmanUtils() {

    }

    public static Set<String> getMissingTags(List<String> tags, List<String> mandatoryTags) {

        Set<String> mandatoryTagSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        mandatoryTagSet.addAll(mandatoryTags);

        Set<String> missedTagSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        missedTagSet.addAll(tags);

        // Remove all common Strings ignoring case
        mandatoryTagSet.removeAll(missedTagSet);
        return mandatoryTagSet;
    }

    public static Annotation createAnnotaion(Map<String, String> ruleParam, String missingTagsStr,
            List<String> mandatoryTagsList, String description, String severity, String category) {
        Annotation annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
        annotation.put(PacmanRuleConstants.MANDATORY_TAGS_MISSING_FLG, PacmanRuleConstants.YES);
        annotation.put(PacmanSdkConstants.DESCRIPTION, description);
        annotation.put(PacmanRuleConstants.MISSING_TAGS, missingTagsStr);
        annotation.put(PacmanRuleConstants.SEVERITY, severity);
        annotation.put(PacmanRuleConstants.CATEGORY, category);
        for (String tag : mandatoryTagsList) {
            if (org.apache.commons.lang.StringUtils.contains(missingTagsStr, tag)) {
                annotation.put(tag, PacmanRuleConstants.NOTFOUND);
            } else {
                annotation.put(tag, PacmanRuleConstants.FOUND);
            }
        }
        return annotation;
    }

    public static XmlRpcClient getXMLRPCClient(String rpcUrl) throws MalformedURLException {

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(rpcUrl));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        return client;
    }

    public static Boolean establishTrustForAllCertificates() {

        // accept any certificate, regardless of issuer and host start

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // do nothing
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // do nothing
            }
        } };
        SSLContext sc = null;
        // Install the all-trusting trust manager
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e1) {
            return Boolean.FALSE;
        }

        
        // Create empty HostnameVerifier
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e1) {
            return Boolean.FALSE;
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(hv);

        // accept any certificate, regardless of issuer and host end
        return Boolean.TRUE;
    }

    public static Set<String> getMissingTagsfromResourceAttribute(List<String> mandatoryTags,
            Map<String, String> attributes) {
        List<String> keyList = new ArrayList<>();
        Set<String> mandatoryTagSetFinal = new HashSet<>();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            keyList.add(entry.getKey().trim());
        }

        Set<String> mandatoryTagSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        mandatoryTagSet.addAll(appendStringOnElements(mandatoryTags));

        Set<String> missedTagSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        missedTagSet.addAll(keyList);

        mandatoryTagSet.removeAll(missedTagSet);
        if (!mandatoryTagSet.isEmpty()) {
            for (String tag : mandatoryTagSet) {
                mandatoryTagSetFinal.add(tag.substring(5));
            }
        }
        return mandatoryTagSetFinal;

    }

    private static List<String> appendStringOnElements(List<String> mandatoryTags) {
        List<String> newList = new ArrayList<>();
        String tagsDot = "tags.";
        for (String tag : mandatoryTags) {
            newList.add(tagsDot + tag);
        }
        return newList;
    }

    public static Boolean checkIsCompliant(String version, Set<String> criteriaMapkeys,
            Map<String, String> kernelCriteriaMapDetails) {

        String criteria = getComplianceCriteriaFor(version, criteriaMapkeys, kernelCriteriaMapDetails);
        if (StringUtils.isBlank(criteria)) {
            return Boolean.FALSE;
        }
        DefaultArtifactVersion minVersion = new DefaultArtifactVersion(criteria);
        return minVersion.compareTo(new DefaultArtifactVersion(version)) <= 0;
    }

    private static String getComplianceCriteriaFor(String version, Set<String> criteriaMapkeys,
            Map<String, String> kernelCriteriaMapDetails) {
        for (String key : criteriaMapkeys) {
            if ("el6.x".equals(key) && version.contains("el6")) {
                if (version.contains("el6uek")) {
                    key = "el6uek";
                } else {
                    key = "el6";
                }
            }
            if (version.contains(key)) {
                if ("el6".equals(key)) {
                    key = "el6.x";
                }
                String quaterlyVersion = kernelCriteriaMapDetails.get(key);
                return removeSequence(quaterlyVersion);
            }
        }
        return StringUtils.EMPTY;
    }

    private static String removeSequence(String version) {
        String searchStr1 = ".x86_64";
        String searchStr2 = "-x86_64";
        String versionStr = null;
        String[] arr;
        if (StringUtils.containsIgnoreCase(version, searchStr1)) {
            arr = version.split(searchStr1);
            versionStr = arr[0];
        } else if (StringUtils.containsIgnoreCase(version, searchStr2)) {
            arr = version.split(searchStr2);
            versionStr = arr[0];
        }
        return versionStr;
    }

    public static Map<String, String> getBody(Map<String, String> resourceAttributes) {

        Map<String, String> body = new HashMap<>();
        body.put("host", resourceAttributes.get(PacmanRuleConstants.ENDPOINT_ADDR));
        body.put("port", resourceAttributes.get(PacmanRuleConstants.ENDPOINT_PORT));
        return body;
    }

    public static String getResponse(Map<String, String> body, String apiGWurl) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        Gson gson = new Gson();
        String bodyString = gson.toJson(body);
        CloseableHttpResponse response = null;
        String result = null;
        int rcode = 0;

        try {
            HttpPost httpPost = new HttpPost(apiGWurl);
            StringEntity entity = new StringEntity(bodyString);
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, PacmanRuleConstants.APPLICATION_JSON));
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            rcode = response.getStatusLine().getStatusCode();
            if (rcode == 200) {
                result = EntityUtils.toString(response.getEntity());

            }
            httpClient.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuleExecutionFailedExeption(e.getMessage());
        }
        return result;

    }

    public static boolean isAccountExists(List<String> accountNames, String resourceAttrAccName) {
        Set<String> accntNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        accntNames.addAll(accountNames);
        return accntNames.contains(resourceAttrAccName);
    }

    public static boolean isGuardDutyFindingsExists(String id, String esUrl, String attribute) {
        JsonParser jsonParser = new JsonParser();

        try {
            HttpClient client = HttpClientBuilder.create().build();

            URL url = new URL(esUrl);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
                    url.getQuery(), url.getRef());

            // prepare Json pay load for GET query.
            JsonObject innerJson = new JsonObject();
            JsonObject matchPhrase = new JsonObject();
            JsonObject must = new JsonObject();
            JsonObject bool = new JsonObject();
            JsonObject query = new JsonObject();

            innerJson.addProperty(attribute, id);
            matchPhrase.add(PacmanRuleConstants.MATCH_PHRASE, innerJson);
            must.add("must", matchPhrase);
            bool.add("bool", must);
            query.add(PacmanRuleConstants.QUERY, bool);
            StringEntity strjson = new StringEntity(query.toString());

            // Qurying the ES
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(uri);
            httpPost.setEntity(strjson);
            httpPost.setHeader(PacmanRuleConstants.CONTENT_TYPE, PacmanRuleConstants.APPLICATION_JSON);
            HttpResponse response = client.execute(httpPost);

            String jsonString = EntityUtils.toString(response.getEntity());
            JsonObject resultJson = (JsonObject) jsonParser.parse(jsonString);
            String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
            JsonObject hitsJson = (JsonObject) jsonParser.parse(hitsJsonString);
            if (hitsJson != null) {
                JsonElement total = hitsJson.getAsJsonObject().get(PacmanRuleConstants.TOTAL);
                if (total.getAsInt() > 0) {
                    return true;
                } else {
                    logger.info("no records found in ElasticSearch");
                }
            }
        } catch (Exception me) {
            logger.error(me.getMessage());
        }

        return false;
    }

    public static boolean checkResourceIdFromElasticSearch(String id, String esUrl, String attributeName, String region)
            throws Exception {
        JsonParser jsonParser = new JsonParser();

        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(convertAttributetoKeyword(attributeName), id);
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.REGION), region);

        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(esUrl, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);
        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
            JsonObject hitsJson = (JsonObject) jsonParser.parse(hitsJsonString);
            JsonArray jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            return isInstanceExists(jsonArray);

        }
        return false;
    }

    private static boolean isInstanceExists(JsonArray jsonArray) {
        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject firstObject = (JsonObject) jsonArray.get(i);
                JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                if (sourceJson != null
                        && (sourceJson.get(PacmanRuleConstants.INSTANCEID) != null && !sourceJson.get(
                                PacmanRuleConstants.INSTANCEID).isJsonNull())) {
                    String instanceId = sourceJson.get(PacmanRuleConstants.INSTANCEID).getAsString();
                    if (!org.apache.commons.lang.StringUtils.isEmpty(instanceId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Map<String, String> getSeviceLimit(String id, String accountId, String esUrl) {
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = new JsonArray();
        Map<String, String> data = new HashMap<>();
        try {
            HttpClient client = HttpClientBuilder.create().build();

            URL url = new URL(esUrl);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
                    url.getQuery(), url.getRef());

            // prepare Json pay load for GET query.
            JsonObject innerJson = new JsonObject();
            JsonObject innerJson1 = new JsonObject();
            JsonObject matchPhrase = new JsonObject();
            JsonObject matchPhrase1 = new JsonObject();
            JsonObject mustObj = new JsonObject();
            JsonArray mustArray = new JsonArray();
            JsonObject bool = new JsonObject();
            JsonObject query = new JsonObject();

            innerJson.addProperty(PacmanRuleConstants.CHECK_ID_KEYWORD, id);
            innerJson1.addProperty(PacmanRuleConstants.ACCOUNTID, accountId);
            matchPhrase.add("match", innerJson);
            matchPhrase1.add("match", innerJson1);
            mustArray.add(matchPhrase);
            mustArray.add(matchPhrase1);
            mustObj.add("must", mustArray);
            bool.add("bool", mustObj);
            query.add(PacmanRuleConstants.QUERY, bool);
            StringEntity strjson = new StringEntity(query.toString());

            // Qurying the ES
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(uri);
            httpPost.setEntity(strjson);
            httpPost.setHeader(PacmanRuleConstants.CONTENT_TYPE, PacmanRuleConstants.APPLICATION_JSON);
            HttpResponse response = client.execute(httpPost);

            String jsonString = EntityUtils.toString(response.getEntity());
            JsonObject resultJson = (JsonObject) jsonParser.parse(jsonString);
            String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
            JsonObject hitsJson = (JsonObject) jsonParser.parse(hitsJsonString);
            jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            if (jsonArray.size() > 0) {

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject firstObject = (JsonObject) jsonArray.get(i);
                    JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                    if (sourceJson != null) {
                        String resourceinfo = sourceJson.get(PacmanRuleConstants.RESOURCE_INFO).getAsString();
                        JsonObject resourceinfoJson = (JsonObject) jsonParser.parse(resourceinfo);
                        String service = resourceinfoJson.get("Service").getAsString();
                        String status = resourceinfoJson.get(PacmanRuleConstants.STATUS_CAP).getAsString();
                        String cUsage = resourceinfoJson.get("Current Usage").getAsString();
                        String lAmount = resourceinfoJson.get("Limit Amount").getAsString();

                        if (cUsage != null && lAmount != null && !"null".equalsIgnoreCase(cUsage)
                                && !"null".equalsIgnoreCase(lAmount)) {
                            Double percentage = (Double.parseDouble(cUsage) / Double.parseDouble(lAmount)) * 100;
                            if (percentage >= 80 && status.equalsIgnoreCase(PacmanRuleConstants.STATUS_RED)) {
                                data.put(service, percentage.toString());
                                data.put("status_" + status, "RED");
                            } else if (percentage >= 80 && status.equalsIgnoreCase(PacmanRuleConstants.STATUS_YELLOW)) {
                                data.put(service, percentage.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception me) {
            logger.error(me.getMessage());
        }

        return data;
    }

    public static List<String> getVolumeIdFromElasticSearch(String id, String esUrl, String attributeName) {
        JsonParser jsonParser = new JsonParser();
        List<String> volList = new ArrayList<>();
        try {
            HttpClient client = HttpClientBuilder.create().build();

            URL url = new URL(esUrl);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
                    url.getQuery(), url.getRef());

            // prepare Json pay load for GET query.
            JsonObject innerJson = new JsonObject();
            JsonObject matchPhrase = new JsonObject();
            JsonObject must = new JsonObject();
            JsonObject bool = new JsonObject();
            JsonObject query = new JsonObject();

            innerJson.addProperty(attributeName, id);
            matchPhrase.add(PacmanRuleConstants.MATCH_PHRASE, innerJson);
            must.add("must", matchPhrase);
            bool.add("bool", must);
            query.add(PacmanRuleConstants.QUERY, bool);
            StringEntity strjson = new StringEntity(query.toString());

            // Qurying the ES
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(uri);
            httpPost.setEntity(strjson);
            httpPost.setHeader(PacmanRuleConstants.CONTENT_TYPE, PacmanRuleConstants.APPLICATION_JSON);
            HttpResponse response = client.execute(httpPost);

            String jsonString = EntityUtils.toString(response.getEntity());
            JsonObject resultJson = (JsonObject) jsonParser.parse(jsonString);
            String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
            JsonObject hitsJson = (JsonObject) jsonParser.parse(hitsJsonString);
            JsonArray jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            volList = getVolumeList(jsonArray);
        } catch (Exception me) {
            logger.error(me.getMessage());
        }

        return volList;
    }

    private static List<String> getVolumeList(JsonArray jsonArray) {
        List<String> volList = new ArrayList<>();
        if (jsonArray.size() > 0) {

            for (int i = 0; i < jsonArray.size(); i++) {

                JsonObject firstObject = (JsonObject) jsonArray.get(i);
                JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                if (sourceJson != null && ("true".equalsIgnoreCase(sourceJson.get("latest").getAsString()))) {
                    JsonElement volumeid = sourceJson.get("volumeid");
                    if (volumeid != null && !volumeid.isJsonNull()) {
                        volList.add(volumeid.getAsString());
                    }
                }
            }
        }
        return volList;
    }

    public static Map<String, Boolean> getPolicyPublicAccess(AmazonS3Client awsS3Client, String s3BucketName,
            String accessType) {
        Map<String, Boolean> map = new HashMap<>();
        JsonParser jsonParser = new JsonParser();
        BucketPolicy bucketPolicy = awsS3Client.getBucketPolicy(s3BucketName);

        JsonArray jsonArray = new JsonArray();
        if (!com.amazonaws.util.StringUtils.isNullOrEmpty(bucketPolicy.getPolicyText())) {
            JsonObject resultJson = (JsonObject) jsonParser.parse(bucketPolicy.getPolicyText());
            jsonArray = resultJson.get("Statement").getAsJsonArray();
        }
        String action = null;
        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject firstObject = (JsonObject) jsonArray.get(i);

                try {
                    String principal = firstObject.get(PacmanRuleConstants.PRINCIPAL).getAsString();
                    String effect = firstObject.get(PacmanRuleConstants.EFFECT).getAsString();

                    if ("*".equals(principal)) {
                        try {
                            action = firstObject.get(PacmanRuleConstants.ACTION).getAsString();
                            if ((action.startsWith(PacmanRuleConstants.S3_PUT) || action.startsWith("s3:*"))
                                    && accessType.equalsIgnoreCase(PacmanRuleConstants.WRITE_ACCESS)
                                    && (PacmanRuleConstants.ALLOW.equalsIgnoreCase(effect))) {
                                map.put(PacmanRuleConstants.WRITE, true);
                            } else if ((action.startsWith(PacmanRuleConstants.S3_GET) || action.startsWith("s3:*"))
                                    && accessType.equalsIgnoreCase(PacmanRuleConstants.READ_ACCESS)
                                    && (PacmanRuleConstants.ALLOW.equalsIgnoreCase(effect))) {
                                map.put("Read", true);
                            }
                        } catch (Exception e3) {
                            JsonArray array1 = firstObject.get(PacmanRuleConstants.ACTION).getAsJsonArray();
                            if (array1.size() > 0) {
                                for (int k = 0; k < array1.size(); k++) {
                                    String actionS3 = array1.get(k).getAsString();

                                    if ((actionS3.startsWith(PacmanRuleConstants.S3_PUT) || actionS3.startsWith("s3:*"))
                                            && accessType.equalsIgnoreCase(PacmanRuleConstants.WRITE_ACCESS)
                                            && (PacmanRuleConstants.ALLOW.equalsIgnoreCase(effect))) {
                                        map.put(PacmanRuleConstants.WRITE, true);
                                    } else if ((actionS3.startsWith(PacmanRuleConstants.S3_GET) || actionS3
                                            .startsWith("s3:*"))
                                            && accessType.equalsIgnoreCase(PacmanRuleConstants.READ_ACCESS)
                                            && (PacmanRuleConstants.ALLOW.equalsIgnoreCase(effect))) {
                                        map.put("Read", true);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    JsonObject principal = firstObject.get(PacmanRuleConstants.PRINCIPAL).getAsJsonObject();
                    JsonObject conditionJsonObject = new JsonObject();
                    JsonArray conditionJsonArray = new JsonArray();
                    JsonParser parser = new JsonParser();
                    JsonElement publicIp = parser.parse(PacmanRuleConstants.CIDR_FILTERVALUE);
                    try {
                        if (principal.has("AWS")) {
                            String aws = principal.get("AWS").getAsString();
                            action = firstObject.get(PacmanRuleConstants.ACTION).getAsString();
                            String effect = firstObject.get(PacmanRuleConstants.EFFECT).getAsString();
                            if ("*".equals(aws)) {
                                if (firstObject.has(PacmanRuleConstants.CONDITION)
                                        && (firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                                .has(PacmanRuleConstants.IP_ADDRESS_CAP))
                                        && (firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                                .get(PacmanRuleConstants.IP_ADDRESS_CAP).getAsJsonObject()
                                                .has(PacmanRuleConstants.SOURCE_IP))) {
                                    if (firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                            .get(PacmanRuleConstants.IP_ADDRESS_CAP).getAsJsonObject()
                                            .get(PacmanRuleConstants.SOURCE_IP).isJsonObject()) {
                                        conditionJsonObject = firstObject.get(PacmanRuleConstants.CONDITION)
                                                .getAsJsonObject().get(PacmanRuleConstants.IP_ADDRESS_CAP)
                                                .getAsJsonObject().get(PacmanRuleConstants.SOURCE_IP).getAsJsonObject();
                                    } else if (firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                            .get(PacmanRuleConstants.IP_ADDRESS_CAP).getAsJsonObject()
                                            .get(PacmanRuleConstants.SOURCE_IP).isJsonArray()) {
                                        conditionJsonArray = firstObject.get(PacmanRuleConstants.CONDITION)
                                                .getAsJsonObject().get(PacmanRuleConstants.IP_ADDRESS_CAP)
                                                .getAsJsonObject().get(PacmanRuleConstants.SOURCE_IP).getAsJsonArray();
                                    }
                                }

                                if ((action.startsWith(PacmanRuleConstants.S3_PUT) || action.startsWith("s3:*"))
                                        && accessType.equalsIgnoreCase(PacmanRuleConstants.WRITE_ACCESS)
                                        && (PacmanRuleConstants.ALLOW.equalsIgnoreCase(effect))) {

                                    if (conditionJsonObject.equals(publicIp) || conditionJsonArray.contains(publicIp)) {
                                        map.put(PacmanRuleConstants.WRITE, true);
                                    }

                                } else if ((action.startsWith(PacmanRuleConstants.S3_GET) || action.startsWith("s3:*"))
                                        && accessType.equalsIgnoreCase(PacmanRuleConstants.READ_ACCESS)
                                        && (PacmanRuleConstants.ALLOW.equalsIgnoreCase(effect))) {
                                    if (conditionJsonObject.equals(publicIp) || conditionJsonArray.contains(publicIp)) {
                                        map.put("Read", true);
                                    }
                                }
                            }
                        }
                    } catch (Exception e1) {
                        JsonArray array = firstObject.get(PacmanRuleConstants.ACTION).getAsJsonArray();
                        String effect = firstObject.get(PacmanRuleConstants.EFFECT).getAsString();
                        if (array.size() > 0) {
                            for (int j = 0; j < array.size(); j++) {
                                String actionS3 = array.get(j).getAsString();

                                if ((actionS3.startsWith(PacmanRuleConstants.S3_PUT) || actionS3.startsWith("s3:*"))
                                        && accessType.equalsIgnoreCase(PacmanRuleConstants.WRITE_ACCESS)
                                        && (PacmanRuleConstants.ALLOW.equalsIgnoreCase(effect))) {
                                    map.put(PacmanRuleConstants.WRITE, true);
                                } else if ((actionS3.startsWith(PacmanRuleConstants.S3_GET) || actionS3
                                        .startsWith("s3:*"))
                                        && accessType.equalsIgnoreCase(PacmanRuleConstants.READ_ACCESS)
                                        && (PacmanRuleConstants.ALLOW.equalsIgnoreCase(effect))) {
                                    map.put("Read", true);
                                }
                            }
                        }
                    }
                }

            }
        }
        return map;
    }

    public static Map<String, Double> getVolumeCost(List<String> idList, String costServiceUrl) {
        Map<String, Double> dataMap = new HashMap<>();
        LocalDate todayDate = LocalDate.now();
        LocalDate yesterdayDate = todayDate.minusDays(30);
        String startDate = yesterdayDate + " " + "08:00:00";// "2017-09-11 08:00:00"
        String endDate = todayDate + " " + "07:59:59"; // "2017-09-12 07:59:59"
        for (String id : idList) {
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("start_date", startDate);
            jsonObj.addProperty("end_date", endDate);
            JsonArray jsonArray = new JsonArray();
            jsonObj.add("applications", jsonArray);
            jsonObj.add(PacmanRuleConstants.SERVICES, jsonArray);
            jsonObj.add("accounts", jsonArray);
            jsonObj.add("environments", jsonArray);

            JsonArray jsonArrayFunction = new JsonArray();
            jsonArrayFunction.add(id);
            jsonObj.add("resource_id", jsonArrayFunction);
            jsonObj.addProperty("interval", "Monthly");
            JsonArray jsonArraySvc = new JsonArray();
            jsonArraySvc.add(PacmanRuleConstants.SERVICES);
            jsonObj.add("group_by", jsonArraySvc);
            double costStr = 0.0;

            try {
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost request = new HttpPost(costServiceUrl);
                StringEntity jsonString;
                jsonString = new StringEntity(jsonObj.toString());
                request.addHeader("content-type", PacmanRuleConstants.APPLICATION_JSON);
                request.addHeader("cache-control", "no-cache");
                request.addHeader("Accept", PacmanRuleConstants.APPLICATION_JSON);
                request.setEntity(jsonString);
                logger.info("calling api to get cost");
                HttpResponse response = httpClient.execute(request);
                if (response.getStatusLine().getStatusCode() == 200) {
                    logger.info("cost api executed successfully");

                    String responseEntity = EntityUtils.toString(response.getEntity());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject responseJson = (JsonObject) jsonParser.parse(responseEntity);
                    Object dataObj = responseJson.get("data");
                    String dataStr = dataObj.toString();
                    JsonObject dataJson = null;
                    if (!"".equals(dataStr)) {
                        dataJson = (JsonObject) jsonParser.parse(dataStr);
                        Object bucketsObj = dataJson.get("buckets");

                        String bucketsStr = bucketsObj.toString();
                        JsonArray bucketsJsonArray = (JsonArray) jsonParser.parse(bucketsStr);
                        String bucketVal = bucketsJsonArray.toString();
                        bucketVal = (bucketVal.substring(1, bucketVal.length() - 1));
                        JsonObject bucketJson = (JsonObject) jsonParser.parse(bucketVal);
                        JsonElement costElement = bucketJson.get("cost");
                        costStr = costElement.getAsDouble();
                    }
                    httpClient.close();
                }
                costStr = Math.floor(costStr * 100) / 100;
                dataMap.put(id, costStr);

            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuleExecutionFailedExeption(e.getMessage());
            }
        }
        return dataMap;
    }

    public static Annotation createAnnotaion(Map<String, String> ruleParam, String missingTagsStr,
            List<String> mandatoryTagsList, String description, String severity, String category, String targetType) {
        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
        Gson gson = new Gson();

        Map<String, String> tagsMap = new HashMap<>();
        Annotation annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
        annotation.put(PacmanRuleConstants.MANDATORY_TAGS_MISSING_FLG, PacmanRuleConstants.YES);
        annotation.put(PacmanSdkConstants.DESCRIPTION, description);
        annotation.put(PacmanRuleConstants.MISSING_TAGS, missingTagsStr);
        annotation.put(PacmanRuleConstants.SEVERITY, severity);
        annotation.put(PacmanRuleConstants.CATEGORY, category);
        for (String tag : mandatoryTagsList) {
            if (org.apache.commons.lang.StringUtils.contains(missingTagsStr, tag)) {
                annotation.put(tag, PacmanRuleConstants.NOTFOUND);
                tagsMap.put(tag, PacmanRuleConstants.NOTFOUND);
            } else {
                annotation.put(tag, PacmanRuleConstants.FOUND);
                tagsMap.put(tag, PacmanRuleConstants.FOUND);
            }
        }
        issue.put(PacmanRuleConstants.VIOLATION_REASON, "Mandatory tags missed for " + targetType + " target type!");
        issue.put("tags_associated", gson.toJson(tagsMap));
        issueList.add(issue);

        annotation.put(PacmanRuleConstants.ISSUE_DETAILS, issueList.toString());
        logger.debug("========TaggingRule ended with an annotation {} : =========", annotation);
        return annotation;
    }

    public static Map<String, Boolean> getQueryFromElasticSearch(String securityGroupId,
            List<String> serviceWithSgEsUrl, String esUrlParam) {
        JsonParser jsonParser = new JsonParser();
        Map<String, Boolean> instanceMap = new HashMap<>();
        String securityGroupAttribute = null;
        String servicesWithSgurl = null;
        for (String esUrl : serviceWithSgEsUrl) {
            servicesWithSgurl = esUrlParam + esUrl;
            if (esUrl.contains("ec2") || esUrl.contains("lambda") || esUrl.contains("appelb")
                    || esUrl.contains("classicelb")) {
                securityGroupAttribute = PacmanRuleConstants.EC2_WITH_SECURITYGROUP_ID;
            } else {
                securityGroupAttribute = PacmanRuleConstants.SECURITYGROUP_ID_ATTRIBUTE;
            }

            try {
                HttpClient client = HttpClientBuilder.create().build();

                URL url = new URL(servicesWithSgurl);
                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
                        url.getQuery(), url.getRef());

                // prepare Json pay load for GET query.
                JsonObject innerJson = new JsonObject();
                JsonObject matchPhrase = new JsonObject();
                JsonObject must = new JsonObject();
                JsonObject bool = new JsonObject();
                JsonObject query = new JsonObject();

                innerJson.addProperty(securityGroupAttribute, securityGroupId);
                matchPhrase.add(PacmanRuleConstants.MATCH_PHRASE, innerJson);
                must.add("must", matchPhrase);
                bool.add("bool", must);
                query.add(PacmanRuleConstants.QUERY, bool);
                StringEntity strjson = new StringEntity(query.toString());

                // Qurying the ES
                HttpPost httpPost = new HttpPost();
                httpPost.setURI(uri);
                httpPost.setEntity(strjson);
                httpPost.setHeader(PacmanRuleConstants.CONTENT_TYPE, PacmanRuleConstants.APPLICATION_JSON);
                HttpResponse response = client.execute(httpPost);
                instanceMap = getInstanceMap(response, jsonParser, instanceMap, esUrl);
            } catch (Exception me) {
                logger.error(me.getMessage());
            }
        }
        return instanceMap;
    }

    private static Map<String, Boolean> getInstanceMap(HttpResponse response, JsonParser jsonParser,
            Map<String, Boolean> instanceMap, String esUrl) throws IOException {

        String jsonString = EntityUtils.toString(response.getEntity());
        JsonObject resultJson = (JsonObject) jsonParser.parse(jsonString);
        String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
        JsonObject hitsJson = (JsonObject) jsonParser.parse(hitsJsonString);
        if (hitsJson != null) {
            JsonElement total = hitsJson.getAsJsonObject().get(PacmanRuleConstants.TOTAL);
            if (total.getAsInt() > 0) {
                instanceMap.put(esUrl, true);
            } else {
                logger.info("no records found in ElasticSearch");
            }
        }
        return instanceMap;
    }

    public static String getAppTagMappedToOU(String appId, String esUrl, String attributeName) {
        JsonArray jsonArray;
        jsonArray = getAppTagInfoFromES(appId, esUrl, attributeName);
        String appname=null;
        String workload=null;
        if (jsonArray.size() == 1) {
            JsonObject firstObject = (JsonObject) jsonArray.get(0);
            JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
            if (sourceJson != null) {
                return sourceJson.get(PacmanRuleConstants.APP_TAG).getAsString();

            }
        } else if (jsonArray.size() > 1) {
            for (JsonElement apptagDetails : jsonArray) {
               appname=(null!=apptagDetails.getAsJsonObject().get(PacmanRuleConstants.SOURCE).getAsJsonObject().get("appName")?apptagDetails.getAsJsonObject().get(PacmanRuleConstants.SOURCE).getAsJsonObject().get("appName").getAsString():null);
               workload=(null!=apptagDetails.getAsJsonObject().get(PacmanRuleConstants.SOURCE).getAsJsonObject().get("workload")?apptagDetails.getAsJsonObject().get(PacmanRuleConstants.SOURCE).getAsJsonObject().get("workload").getAsString():null);
                if (((appname!=null && workload!=null))&&appname.equalsIgnoreCase(workload)) {
                    return apptagDetails.getAsJsonObject().get(PacmanRuleConstants.SOURCE).getAsJsonObject().get(PacmanRuleConstants.APP_TAG).getAsString();
                }

            }
        }
        return null;
    }

    private static JsonArray getAppTagInfoFromES(String appId, String esUrl, String attributeName) {
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = new JsonArray();
        try {
            HttpClient client = HttpClientBuilder.create().build();

            URL url = new URL(esUrl);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
                    url.getQuery(), url.getRef());
            // prepare Json pay load for GET query.
            JsonObject innerJson = new JsonObject();
            JsonObject matchPhrase = new JsonObject();
            JsonObject must = new JsonObject();
            JsonObject bool = new JsonObject();
            JsonObject query = new JsonObject();

            innerJson.addProperty(attributeName, appId);
            matchPhrase.add(PacmanRuleConstants.MATCH_PHRASE, innerJson);
            must.add("must", matchPhrase);
            bool.add("bool", must);
            query.add(PacmanRuleConstants.QUERY, bool);
            StringEntity strjson = new StringEntity(query.toString());

            // Qurying the ES
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(uri);
            httpPost.setEntity(strjson);
            httpPost.setHeader(PacmanRuleConstants.CONTENT_TYPE, PacmanRuleConstants.APPLICATION_JSON);
            HttpResponse response = client.execute(httpPost);

            String jsonString = EntityUtils.toString(response.getEntity());
            JsonObject resultJson = (JsonObject) jsonParser.parse(jsonString);
            String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
            JsonObject hitsJson = (JsonObject) jsonParser.parse(hitsJsonString);
            jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();

        } catch (Exception me) {
            logger.error(me.getMessage());
        }
        return jsonArray;
    }

    public static boolean checkAppTagMappedtoOUMatchesCurrentAppTag(String appId, String esUrl, String attributeName,
            String currentApptag) {
        JsonArray jsonArray = getAppTagInfoFromES(appId, esUrl, attributeName);
        if (jsonArray.size() == 1) {
            JsonObject firstObject = (JsonObject) jsonArray.get(0);
            JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
            if (sourceJson != null && currentApptag.equalsIgnoreCase(sourceJson.get("appTag").getAsString())) {
                return true;
            }
        } else if (jsonArray.size() > 1) {
            // multiple apptag found for AppId
            for (JsonElement apptagDetails : jsonArray) {
                if (apptagDetails.getAsJsonObject().get(PacmanRuleConstants.SOURCE).getAsJsonObject().get("appTag").getAsString().equalsIgnoreCase(currentApptag)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Map<String, Boolean> checkS3HasOpenAccess(String checkId, String accountId, String esUrl,
            String resourceInfo) throws Exception {
        JsonParser jsonParser = new JsonParser();
        String policyAllowsAccess = null;
        String aclAllowsAccess = null;
        JsonObject resourceInfoJson;
        Map<String, Boolean> publicAccess = new HashMap<>();

        String resourceinfo = getQueryDataForCheckid(checkId, esUrl, resourceInfo, null, accountId);

        if (org.apache.commons.lang.StringUtils.isNotEmpty(resourceinfo)) {

            resourceInfoJson = (JsonObject) jsonParser.parse(resourceinfo);
            policyAllowsAccess = resourceInfoJson.get("Policy Allows Access").getAsString();
            aclAllowsAccess = resourceInfoJson.get("ACL Allows List").getAsString();
            if (!com.amazonaws.util.StringUtils.isNullOrEmpty(policyAllowsAccess)
                    && "Yes".equalsIgnoreCase(policyAllowsAccess)) {
                publicAccess.put("bucketPolicy_found", true);
            } else {
                publicAccess.put("bucketPolicy_found", false);
            }

            if (!com.amazonaws.util.StringUtils.isNullOrEmpty(aclAllowsAccess)
                    && "Yes".equalsIgnoreCase(aclAllowsAccess)) {

                publicAccess.put("acl_found", true);

            } else {

                publicAccess.put("acl_found", false);
            }

        }
        return publicAccess;
    }

    public static String doHttpPost(final String url, final String requestBody) throws Exception {
        try {

            HttpClient client = HttpClientBuilder.create().build();
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader(PacmanRuleConstants.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
            StringEntity jsonEntity = new StringEntity(requestBody);
            httppost.setEntity(jsonEntity);
            HttpResponse httpresponse = client.execute(httppost);
            int statusCode = httpresponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                return EntityUtils.toString(httpresponse.getEntity());
            } else {
                throw new Exception("unable to execute post request because "
                        + httpresponse.getStatusLine().getReasonPhrase());
            }
        } catch (Exception e) {
            throw new RuleExecutionFailedExeption(e.getMessage());
        }
    }

    public static String doHttpGet(final String url, Map<String, String> headers) throws Exception {
        try {

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(PacmanRuleConstants.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
            Set<Entry<String, String>> entrySet = headers.entrySet();
            for (Entry<String, String> entry : entrySet) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
            HttpResponse httpresponse = client.execute(httpGet);
            int statusCode = httpresponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                return EntityUtils.toString(httpresponse.getEntity());
            } else {
                throw new Exception("unable to execute post request because "
                        + httpresponse.getStatusLine().getReasonPhrase());
            }
        } catch (ParseException parseException) {
            throw parseException;
        } catch (Exception exception) {
            throw exception;
        }
    }

    public static List<GroupIdentifier> getSecurityGroupsByInstanceId(String instanceId, String esUrl) throws Exception {
        List<GroupIdentifier> list = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.INSTANCEID), instanceId);
        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(esUrl, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);
        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(PacmanRuleConstants.HITS).toString());
            JsonArray hitsArray = hitsJson.getAsJsonArray(PacmanRuleConstants.HITS);
            for (int i = 0; i < hitsArray.size(); i++) {
                JsonObject source = hitsArray.get(i).getAsJsonObject().get(PacmanRuleConstants.SOURCE)
                        .getAsJsonObject();
                String securitygroupid = source.get(PacmanRuleConstants.EC2_WITH_SECURITYGROUP_ID).getAsString();
                GroupIdentifier groupIdentifier = new GroupIdentifier();
                if (!com.amazonaws.util.StringUtils.isNullOrEmpty(securitygroupid)) {
                    groupIdentifier.setGroupId(securitygroupid);
                    list.add(groupIdentifier);
                }
            }
        }
        return list;
    }

    public static Set<String> getRouteTableId(String subnetId, String vpcId, String routetableEsURL, String type)
            throws Exception {
        String routetableid = null;
        JsonParser jsonParser = new JsonParser();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        if (type.equals(PacmanRuleConstants.SUBNET)) {
            mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.SUBNETID), subnetId);
        } else {
            mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.VPC_ID), vpcId);
            mustFilter.put(PacmanRuleConstants.LATEST, true);
        }
        
        Set<String> routeTableIdList = new HashSet<>();
        
        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(routetableEsURL, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);
        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(PacmanRuleConstants.HITS).toString());
            JsonArray hitsArray = hitsJson.getAsJsonArray(PacmanRuleConstants.HITS);
            for (int i = 0; i < hitsArray.size(); i++) {
                JsonObject source = hitsArray.get(i).getAsJsonObject().get(PacmanRuleConstants.SOURCE)
                        .getAsJsonObject();
                routetableid = source.get(PacmanRuleConstants.ROUTE_TABLE_ID).getAsString();
                if (!org.apache.commons.lang.StringUtils.isEmpty(routetableid)) {
                    routeTableIdList.add(routetableid);
                   // return routetableid;
                }
            }
        }
        return routeTableIdList;
    }

    public static boolean getRouteTableRoutesId(List<String> routeTableIdList,Set<String> routeTableIdSet, String routetableRoutesEsURL,
            String cidrfilterValue, String internetGateWay) throws Exception {
        String gatewayid = null;
        JsonParser jsonParser = new JsonParser();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustTermsFilter.put(convertAttributetoKeyword(PacmanRuleConstants.ROUTE_TABLE_ID), routeTableIdSet);
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.DEST_CIDR_BLOCK), cidrfilterValue);
        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(routetableRoutesEsURL,
                mustFilter, mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);

        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(PacmanRuleConstants.HITS).toString());

            JsonArray hitsArray = hitsJson.getAsJsonArray(PacmanRuleConstants.HITS);
            for (int i = 0; i < hitsArray.size(); i++) {
                JsonObject source = hitsArray.get(i).getAsJsonObject().get(PacmanRuleConstants.SOURCE)
                        .getAsJsonObject();
                gatewayid = source.get(PacmanRuleConstants.GATE_WAY_ID).getAsString();
                if (!org.apache.commons.lang.StringUtils.isEmpty(gatewayid)
                        && gatewayid.toLowerCase().startsWith(internetGateWay)) {
                    routeTableIdList.add(source.get(PacmanRuleConstants.ROUTE_TABLE_ID).getAsString());
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkInstanceIdForPortRuleInES(String instanceId, String ec2PortUrl, String ruleId)
            throws Exception {
        JsonParser jsonParser = new JsonParser();
        String resourceid = null;
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.ISSUE_STATUS_KEY), PacmanSdkConstants.STATUS_OPEN);
        mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.RULE_ID), ruleId);
        mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.RESOURCE_ID), instanceId);

        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(ec2PortUrl, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);

        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(PacmanRuleConstants.HITS).toString());
            JsonArray hitsArray = hitsJson.getAsJsonArray(PacmanRuleConstants.HITS);
            for (int i = 0; i < hitsArray.size(); i++) {
                JsonObject source = hitsArray.get(i).getAsJsonObject().get(PacmanRuleConstants.SOURCE)
                        .getAsJsonObject();
                resourceid = source.get(PacmanSdkConstants.RESOURCE_ID).getAsString();
                if (!org.apache.commons.lang.StringUtils.isEmpty(resourceid)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<String> getSeverityVulnerabilitiesByInstanceId(String instanceId, String ec2WithVulnUrl,
            String severityVulnValue) throws Exception {
        List<String> severityList = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(PacmanRuleConstants.LATEST, PacmanRuleConstants.TRUE_VAL);
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.SEVERITY), severityVulnValue);
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.RESOURCE_ID), instanceId);
        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(ec2WithVulnUrl, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);
        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(PacmanRuleConstants.HITS).toString());

            JsonArray hitsArray = hitsJson.getAsJsonArray(PacmanRuleConstants.HITS);
            for (int i = 0; i < hitsArray.size(); i++) {
                JsonObject source = hitsArray.get(i).getAsJsonObject().get(PacmanRuleConstants.SOURCE)
                        .getAsJsonObject();
                severityList.add(source.get("title").getAsString());
            }

        }
        return severityList;
    }

    public static boolean checkACLAccess(AmazonS3Client awsS3Client, String s3BucketName, String accessType) {
        logger.info("inside the checkACLAccess method");
        Boolean openAcces = false;
        AccessControlList bucketAcl;
        List<Permission> permissionList = null;
        try {
            bucketAcl = awsS3Client.getBucketAcl(s3BucketName);

            List<Grant> grants = bucketAcl.getGrantsAsList();

            // Check grants has which permission
            if (!CollectionUtils.isNullOrEmpty(grants)) {

                permissionList = checkAnyGrantHasOpenToReadOrWriteAccess(grants, accessType);
                if (!CollectionUtils.isNullOrEmpty(permissionList)) {
                    openAcces = true;
                }
            }

        } catch (AmazonS3Exception s3Exception) {
            logger.error("error : ", s3Exception);
            throw new RuleExecutionFailedExeption(s3Exception.getMessage());
        }
        return openAcces;
    }

    public static Annotation createS3Annotation(Map<String, String> ruleParam, String description, String severity,
            String category, String accessType, List<String> sourcesVerified, Map<String, Object> accessLevels,
            String resourceId) {
        Annotation annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
        annotation.put(PacmanSdkConstants.DESCRIPTION, description);
        annotation.put(PacmanRuleConstants.SEVERITY, severity);
        annotation.put(PacmanRuleConstants.CATEGORY, category);
        Gson gson = new Gson();
        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
        issue.put(PacmanRuleConstants.SOURCE_VERIFIED, String.join(",", sourcesVerified));
        issue.put("access_levels", gson.toJson(accessLevels));
        issue.put(PacmanRuleConstants.VIOLATION_REASON, "ResourceId " + resourceId + " has " + accessType + "access");
        issueList.add(issue);
        annotation.put(PacmanRuleConstants.ISSUE_DETAILS, issueList.toString());
        logger.debug("AccessRule ended with an annotation : {} =========", annotation);
        return annotation;
    }

    /**
     * Check accessible to all.
     *
     * @param secuityGroups the secuity groups
     * @param portToCheck the port to check
     * @param sgRulesUrl the sg rules url
     * @param cidrIp the cidr ip
     * @param cidripv6 the cidripv 6
     * @param target the target
     * @return the map
     * @throws Exception the exception
     */
    public static Map<String, Boolean> checkAccessibleToAll(Set<GroupIdentifier> secuityGroups, String portToCheck,
            String sgRulesUrl, String cidrIp, String cidripv6,String target) throws Exception {
        JsonObject resultJsonCidrip = null;
        LinkedHashMap<String, Boolean> openPorts = new LinkedHashMap<>();
        for (GroupIdentifier securityGrp : secuityGroups) {
            Map<String, Object> mustFilter = new HashMap<>();
            Map<String, Object> mustNotFilter = new HashMap<>();
            HashMultimap<String, Object> shouldFilter = HashMultimap.create();
            Map<String, Object> mustTermsFilter = new HashMap<>();
            mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.GROUP_ID), securityGrp.getGroupId());
            shouldFilter.put(convertAttributetoKeyword(PacmanRuleConstants.CIDRIP), cidrIp);
            shouldFilter.put(convertAttributetoKeyword(PacmanRuleConstants.CIDRIPV6), cidripv6);
            mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.TYPE), PacmanRuleConstants.INBOUND);
            resultJsonCidrip = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(sgRulesUrl, mustFilter,
                    mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);
            proccessCidrIpOrCidrIpv6Data(resultJsonCidrip, portToCheck, openPorts,target);
        }

        return openPorts;
    }

    public static CloseableHttpClient getCloseableHttpClient() {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    }).build()).build();
        } catch (KeyManagementException e) {
            logger.error("KeyManagementException in creating http client instance", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException in creating http client instance", e);
        } catch (KeyStoreException e) {
            logger.error("KeyStoreException in creating http client instance", e);
        }
        return httpClient;
    }

    public static Map<String, Boolean> getFromAndToPorts(String fromPort, String toPort, String ipprotocol,
            Map<String, Boolean> openPorts) {
        String fPort = "FromPort_" + fromPort + "_" + ipprotocol;
        String tPort = "ToPort_" + toPort + "_" + ipprotocol;
        openPorts.put(fPort + "-" + tPort, true);
        return openPorts;
    }

    public static Map<String, Boolean> isAccessbleToAll(Set<GroupIdentifier> secuityGroupSet, int target,
            String sgRulesUrl, String cidrIp) throws Exception {
        String fromPort = null;
        String toPort = null;
        String ipprotocol = null;
        JsonObject resultJson = null;
        LinkedHashMap<String, Boolean> openPorts = new LinkedHashMap<>();

        for (GroupIdentifier securityGrp : secuityGroupSet) {
            JsonParser jsonParser = new JsonParser();
            Map<String, Object> mustFilter = new HashMap<>();
            Map<String, Object> mustNotFilter = new HashMap<>();
            HashMultimap<String, Object> shouldFilter = HashMultimap.create();
            Map<String, Object> mustTermsFilter = new HashMap<>();
            mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.GROUP_ID), securityGrp.getGroupId());
            mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.CIDRIP), cidrIp);
            mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.TYPE), PacmanRuleConstants.INBOUND);
            resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(sgRulesUrl, mustFilter, mustNotFilter,
                    shouldFilter, null, 0, mustTermsFilter, null,null);
            if (resultJson != null) {
                JsonObject hitsJson = (JsonObject) jsonParser
                        .parse(resultJson.get(PacmanRuleConstants.HITS).toString());
                JsonArray hitsArray = hitsJson.getAsJsonArray(PacmanRuleConstants.HITS);
                logger.info(sgRulesUrl);
                for (int i = 0; i < hitsArray.size(); i++) {
                    JsonObject source = hitsArray.get(i).getAsJsonObject().get(PacmanRuleConstants.SOURCE)
                            .getAsJsonObject();
                    fromPort = source.get("fromport").getAsString();
                    toPort = source.get("toport").getAsString();
                    ipprotocol = source.get("ipprotocol").getAsString();
                    logger.info(fromPort);
                    logger.info(toPort);
                    logger.info(ipprotocol);
                    if (!org.apache.commons.lang.StringUtils.isEmpty(fromPort)
                            && !org.apache.commons.lang.StringUtils.isEmpty(toPort)) {

                        if (!"All".equalsIgnoreCase(fromPort)) {

                            if (Long.parseLong(fromPort) <= target) {
                                getFromAndToPorts(fromPort, toPort, ipprotocol, openPorts);
                            }
                        } else {
                            getFromAndToPorts(fromPort, toPort, ipprotocol, openPorts);
                        }
                    }
                }
            }
        }
        return openPorts;
    }

    public static boolean checkResourceIdForRuleInES(String resourceId, String url, String ruleId, String accountid)
            throws Exception {
        JsonParser jsonParser = new JsonParser();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.ISSUE_STATUS_KEY), PacmanSdkConstants.STATUS_OPEN);
        mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.TYPE), PacmanRuleConstants.ISSUE);
        mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.ACCOUNT_ID), accountid);
        mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.RULE_ID), ruleId);
        mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.RESOURCE_ID), resourceId);

        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(url, mustFilter, mustNotFilter,
                shouldFilter, null, 0, mustTermsFilter, null,null);

        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(PacmanRuleConstants.HITS).toString());
            Long total = hitsJson.get(PacmanRuleConstants.TOTAL).getAsLong();
            if (total > 0) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, String> getIdleLoadBalancerDetails(String checkId, String id, String esUrl,
            String region, String accountId) throws Exception {
        JsonParser jsonParser = new JsonParser();
        Map<String, String> data = new HashMap<>();
        String resourceinfo = getQueryDataForCheckid(checkId, esUrl, id, region, accountId);
        if (resourceinfo != null) {
            JsonObject resourceinfoJson = (JsonObject) jsonParser.parse(resourceinfo);
            String loadBalancerName = resourceinfoJson.get("Load Balancer Name").getAsString();
            if (!Strings.isNullOrEmpty(loadBalancerName) && loadBalancerName.equals(id)) {
                String reason = resourceinfoJson.get(PacmanRuleConstants.ISSUE_REASON).getAsString();
                String estimatedMonthlySavings = resourceinfoJson.get(PacmanRuleConstants.ESTIMATED_MONTHLY_SAVINGS)
                        .getAsString();

                if (!Strings.isNullOrEmpty(reason) || !Strings.isNullOrEmpty(estimatedMonthlySavings)
                        || !Strings.isNullOrEmpty(loadBalancerName)) {
                    data.put(PacmanRuleConstants.REASON, reason);
                    data.put(PacmanRuleConstants.EST_MONTHLY_SAVINGS, estimatedMonthlySavings);
                }
            }
        }

        return data;
    }

    public static boolean getUnownedAdGroup(String resourceId, String url) throws Exception {
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(PacmanRuleConstants.LATEST, true);
        mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.RESOURCE_ID), resourceId);

        JsonObject ownedAdGroupsJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(url, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);
        if (ownedAdGroupsJson != null && ownedAdGroupsJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = ownedAdGroupsJson.get(PacmanRuleConstants.HITS).getAsJsonObject();
            JsonArray jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            if (jsonArray.size() > 0) {

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject firstObject = (JsonObject) jsonArray.get(i);
                    JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                    if (sourceJson != null) {
                        return isManagedBy(sourceJson);
                    }
                }
            }
        }

        return false;
    }

    private static boolean isManagedBy(JsonObject sourceJson) {

        JsonElement managedBy = sourceJson.get(PacmanRuleConstants.MANAGED_BY);

        return (org.apache.commons.lang.StringUtils.isEmpty(managedBy.getAsString()) || managedBy.isJsonNull() || managedBy
                .getAsString().equals(PacmanRuleConstants.SVC_ADDS_UNOWNED));

    }

    public static boolean getNestedRoles(String resourceId, String url, String type) throws Exception {

        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(PacmanRuleConstants.LATEST, true);
        mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.RESOURCE_ID), resourceId);

        JsonObject nestedRolesJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(url, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);
        if (nestedRolesJson != null && nestedRolesJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = nestedRolesJson.get(PacmanRuleConstants.HITS).getAsJsonObject();
            JsonArray jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            if (jsonArray.size() > 0) {

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject firstObject = (JsonObject) jsonArray.get(i);
                    JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                    if (sourceJson != null) {
                        JsonArray memberOf = sourceJson.get(PacmanRuleConstants.MEMBER_OF).getAsJsonArray();
                        if (memberOf.size() > 0) {
                            for (int j = 0; j < memberOf.size(); j++) {
                                String memberOfObject = memberOf.get(j).getAsString();
                                if (memberOfObject.startsWith(PacmanRuleConstants.ROLE)) {
                                    return true;
                                }
                            }
                        } else {
                            return "nested".equals(type);
                        }
                    }
                }
            }
        }
        return false;
    }

    public static JsonArray getMemberOf(String resourceId, String url) throws Exception {
        JsonArray memberOf = new JsonArray();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(PacmanRuleConstants.LATEST, true);
        mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.RESOURCE_ID), resourceId);

        JsonObject nestedRolesJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(url, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);
        if (nestedRolesJson != null && nestedRolesJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = nestedRolesJson.get(PacmanRuleConstants.HITS).getAsJsonObject();
            JsonArray jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            if (jsonArray.size() > 0) {

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject firstObject = (JsonObject) jsonArray.get(i);
                    JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                    if (sourceJson != null) {
                        memberOf = sourceJson.get(PacmanRuleConstants.MEMBER_OF).getAsJsonArray();

                    }
                }
            }
        }
        return memberOf;
    }

    public static Map<String, Object> checkInstanceIdFromElasticSearchForQualys(String id, String esUrl,
            String attributeName, String target) throws Exception {
        JsonParser jsonParser = new JsonParser();
        Map<String, Object> resourceVerified = new HashMap<>();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(convertAttributetoKeyword(attributeName), id);
        mustFilter.put(PacmanRuleConstants.LATEST, true);

        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(esUrl, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);
        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
            JsonObject hitsJson = (JsonObject) jsonParser.parse(hitsJsonString);
            JsonArray jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            if (jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject firstObject = (JsonObject) jsonArray.get(i);
                    JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);

                    if ((null != sourceJson) && (null != sourceJson.get(PacmanRuleConstants.RESOURCE_ID))
                            && (!sourceJson.get(PacmanRuleConstants.RESOURCE_ID).isJsonNull())) {
                        String instanceId = sourceJson.get(PacmanRuleConstants.RESOURCE_ID).getAsString();
                        if (!sourceJson.has(PacmanRuleConstants.LAST_VULN_SCAN)
                                || sourceJson.get(PacmanRuleConstants.LAST_VULN_SCAN).isJsonNull()) {
                            resourceVerified.put(PacmanRuleConstants.FAILED_REASON,
                                    "unable to determine as last scanned date is not available!!");
                            return resourceVerified;
                        } else {

                            if (!org.apache.commons.lang.StringUtils.isEmpty(instanceId)) {
                                String lastVulnScan = sourceJson.get(PacmanRuleConstants.LAST_VULN_SCAN).getAsString();
                                if (calculateDuration(lastVulnScan) < Long.parseLong(target)) {
                                    return resourceVerified;
                                } else {
                                    resourceVerified.put(PacmanRuleConstants.FAILED_REASON, "qualys not scanned since "
                                            + target + " days!!");
                                    return resourceVerified;
                                }
                            }
                        }
                    } else {
                        resourceVerified.put(PacmanRuleConstants.FAILED_REASON, "qualys agent not found!!");
                        return resourceVerified;
                    }
                }
            } else {
                resourceVerified.put(PacmanRuleConstants.FAILED_REASON, "qualys agent not found!!");
                return resourceVerified;
            }
        }
        return resourceVerified;
    }

    public static Map<String, String> getLowUtilizationEc2Details(String checkId, String id, String esUrl,
            String region, String accountId) throws Exception {
        String cpuUtilization = null;
        String day = null;
        String networkIO = null;
        JsonParser jsonParser = new JsonParser();
        Map<String, String> data = new HashMap<>();
        String resourceinfo = getQueryDataForCheckid(checkId, esUrl, id, region, accountId);

        if (resourceinfo != null) {
            JsonObject resourceinfoJson = (JsonObject) jsonParser.parse(resourceinfo);
            String instanceId = resourceinfoJson.get("Instance ID").getAsString();
            if (!Strings.isNullOrEmpty(instanceId) && instanceId.equals(id)) {

                String estimatedMonthlySavings = resourceinfoJson.get(PacmanRuleConstants.ESTIMATED_MONTHLY_SAVINGS)
                        .getAsString();
                String days = resourceinfoJson.get("Number of Days Low Utilization").getAsString();
                int count = 1;

                if (!StringUtils.isEmpty(days) && "14 days".equals(days)) {
                    for (int k = 1; k <= 14; k++) {

                        day = resourceinfoJson.get("Day " + k).getAsString();
                        cpuUtilization = StringUtils.substringBefore(day, "%");
                        Double cpuUtilizationD = Double.parseDouble(cpuUtilization);
                        networkIO = StringUtils.substringBetween(day, "%", "MB").trim();
                        Double networkUtilizationD = Double.parseDouble(networkIO);
                        if (cpuUtilizationD <= 10 && networkUtilizationD <= 5) {
                            if (count == 4) {
                                data.put(PacmanRuleConstants.EST_MONTHLY_SAVINGS, estimatedMonthlySavings);
                                data.put("noOfDaysLowUtilization", days);
                                return data;
                            }
                            count++;
                        }
                    }
                }
            }
        }
        return data;
    }

    private static Long calculateDuration(String date) throws java.text.ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("M/d/yyyy H:m");
        Date givenDate = null;
        String givenDateStr = null;
        try {
            givenDate = dateFormat.parse(date);
            givenDateStr = dateFormat1.format(givenDate);
        } catch (ParseException e) {
            logger.error("Parse exception occured : ", e);
            throw new RuleExecutionFailedExeption("Parse exception occured : " + e);
        }
        LocalDate givenLoacalDate = LocalDateTime.parse(givenDateStr, DateTimeFormatter.ofPattern("M/d/yyyy H:m"))
                .toLocalDate();
        LocalDate today = LocalDateTime.now().toLocalDate();
        return java.time.temporal.ChronoUnit.DAYS.between(givenLoacalDate, today);

    }

    public static Map<String, String> getDetailsForCheckId(String checkId, String id, String esUrl, String region,
            String accountId) throws Exception {
        JsonParser jsonParser = new JsonParser();
        String resourceinfo = null;
        Map<String, String> data = new HashMap<>();

        resourceinfo = getQueryDataForCheckid(checkId, esUrl, id, region, accountId);
        if (resourceinfo != null) {
            JsonObject resourceinfoJson = (JsonObject) jsonParser.parse(resourceinfo);
            String cluster = resourceinfoJson.get("Cluster").getAsString();
            if (!Strings.isNullOrEmpty(cluster) && cluster.equals(id)) {
                String reason = resourceinfoJson.get(PacmanRuleConstants.ISSUE_REASON).getAsString();
                String estimatedMonthlySavings = resourceinfoJson.get(PacmanRuleConstants.ESTIMATED_MONTHLY_SAVINGS)
                        .getAsString();
                String status = resourceinfoJson.get(PacmanRuleConstants.STATUS_CAP).getAsString();

                if (!Strings.isNullOrEmpty(status) && "Yellow".equals(status)) {
                    if (!Strings.isNullOrEmpty(reason) || !Strings.isNullOrEmpty(estimatedMonthlySavings)
                            || !Strings.isNullOrEmpty(cluster)) {
                        String instanceType = resourceinfoJson.get(PacmanRuleConstants.INSTANCE_TYPE_CAP).getAsString();
                        data.put(PacmanRuleConstants.REASON, reason);
                        data.put(PacmanRuleConstants.INSTANCETYPE, instanceType);
                        data.put(PacmanRuleConstants.STATUS, status);
                        data.put(PacmanRuleConstants.EST_MONTHLY_SAVINGS, estimatedMonthlySavings);
                    }
                }
            }
        }

        return data;
    }

    public static String getQueryDataForCheckid(String checkId, String esUrl, String id, String region, String accountId)
            throws Exception {
        JsonParser jsonParser = new JsonParser();
        String resourceinfo = null;

        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        Map<String, List<String>> matchPhrasePrefix = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(PacmanRuleConstants.CHECK_ID_KEYWORD, checkId);
        mustFilter.put(PacmanRuleConstants.ACCOUNT_ID_KEYWORD, accountId);
        List<String> resourceInfoList = new ArrayList<>();
        if (region != null) {
            resourceInfoList.add(region);
        }
        resourceInfoList.add(id);
        matchPhrasePrefix.put(PacmanRuleConstants.RESOURCE_INFO, resourceInfoList);

        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(esUrl, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, matchPhrasePrefix,null);

        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(PacmanRuleConstants.HITS).toString());
            JsonArray jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            if (jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject firstObject = (JsonObject) jsonArray.get(i);
                    JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                    if (sourceJson != null && sourceJson.has(PacmanRuleConstants.RESOURCE_INFO)) {
                        if (sourceJson.get(PacmanRuleConstants.RESOURCE_INFO).isJsonObject()) {
                            resourceinfo = sourceJson.get(PacmanRuleConstants.RESOURCE_INFO).getAsJsonObject()
                                    .toString();
                        } else {
                            resourceinfo = sourceJson.get(PacmanRuleConstants.RESOURCE_INFO).getAsString();
                        }
                    }
                }
            }
        }
        return resourceinfo;
    }

    public static Map<String, String> getRDSDetailsForCheckId(String checkId, String id, String esUrl, String region,
            String accountId) throws Exception {
        JsonParser jsonParser = new JsonParser();
        Map<String, String> data = new HashMap<>();

        String resourceinfo = getQueryDataForCheckid(checkId, esUrl, id, region, accountId);
        if (resourceinfo != null) {
            JsonObject resourceinfoJson = (JsonObject) jsonParser.parse(resourceinfo);
            String dbInstanceName = resourceinfoJson.get("DB Instance Name").getAsString();
            if (!Strings.isNullOrEmpty(dbInstanceName) && dbInstanceName.equals(id)) {
                String multiAZ = resourceinfoJson.get("Multi-AZ").getAsString();
                String estimatedMonthlySavings = resourceinfoJson.get("Estimated Monthly Savings (On Demand)")
                        .getAsString();

                if (!Strings.isNullOrEmpty(estimatedMonthlySavings)) {
                    String instanceType = resourceinfoJson.get(PacmanRuleConstants.INSTANCE_TYPE_CAP).getAsString();
                    String daysSinceLastConnection = resourceinfoJson.get("Days Since Last Connection").getAsString();
                    String storageProvisioned = resourceinfoJson.get("Storage Provisioned (GB)").getAsString();
                    data.put("multi-AZ", multiAZ);
                    data.put(PacmanRuleConstants.INSTANCETYPE, instanceType);
                    data.put("storageProvisioned", storageProvisioned);
                    data.put("daysSinceLastConnection", daysSinceLastConnection);
                    data.put(PacmanRuleConstants.EST_MONTHLY_SAVINGS, estimatedMonthlySavings);
                }
            }
        }
        return data;
    }

    public static Map<String, String> getAmazonEC2ReservedInstanceLeaseExpiration(String checkId, String id,
            String esUrl, String region, String accountId) throws Exception {
        JsonParser jsonParser = new JsonParser();
        Map<String, String> data = new HashMap<>();
        String resourceinfo = getQueryDataForCheckid(checkId, esUrl, id, region, accountId);

        if (resourceinfo != null) {
            JsonObject resourceinfoJson = (JsonObject) jsonParser.parse(resourceinfo);
            String reservedInstanceId = resourceinfoJson.get("Reserved Instance ID").getAsString();
            if (!Strings.isNullOrEmpty(reservedInstanceId) && reservedInstanceId.equals(id)) {
                data.put(PacmanRuleConstants.EST_MONTHLY_SAVINGS,
                        resourceinfoJson.get(PacmanRuleConstants.ESTIMATED_MONTHLY_SAVINGS).getAsString());
                data.put(PacmanRuleConstants.REASON, resourceinfoJson.get(PacmanRuleConstants.ISSUE_REASON)
                        .getAsString());
                data.put("instanceCount", resourceinfoJson.get("Instance Count").getAsString());
                data.put(PacmanRuleConstants.STATUS, resourceinfoJson.get(PacmanRuleConstants.STATUS_CAP).getAsString());
                data.put("platform", resourceinfoJson.get("Platform").getAsString());
                data.put("currentMonthlyCost", resourceinfoJson.get("Current Monthly Cost").getAsString());
                data.put("expirationDate", resourceinfoJson.get("Expiration Date").getAsString());
                data.put(PacmanRuleConstants.INSTANCETYPE, resourceinfoJson.get(PacmanRuleConstants.INSTANCE_TYPE_CAP)
                        .getAsString());
                data.put("zone", resourceinfoJson.get("Zone").getAsString());
            }
        }
        return data;
    }

    public static Map<String, Boolean> getPublicAccessPolicy(AmazonS3Client awsS3Client, String s3BucketName,
            String accessType) {
        Map<String, Boolean> map = new HashMap<>();

        JsonParser jsonParser = new JsonParser();
        JsonObject conditionJsonObject = new JsonObject();
        JsonArray conditionJsonArray = new JsonArray();
        String conditionStr = null;
        BucketPolicy bucketPolicy = awsS3Client.getBucketPolicy(s3BucketName);

        JsonArray jsonArray = new JsonArray();
        if (!com.amazonaws.util.StringUtils.isNullOrEmpty(bucketPolicy.getPolicyText())) {
            JsonObject resultJson = (JsonObject) jsonParser.parse(bucketPolicy.getPolicyText());
            jsonArray = resultJson.get("Statement").getAsJsonArray();
        }
        String actionString = null;
        JsonArray actionJsonArray = new JsonArray();
        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject firstObject = (JsonObject) jsonArray.get(i);
                if (firstObject.has(PacmanRuleConstants.PRINCIPAL)
                        && firstObject.get(PacmanRuleConstants.PRINCIPAL).isJsonObject()) {
                    JsonObject principal = firstObject.get(PacmanRuleConstants.PRINCIPAL).getAsJsonObject();

                    try {
                        if (principal.has("AWS")) {
                            String aws = null;
                            JsonArray awsArray = null;
                            if (principal.get("AWS").isJsonArray()) {
                                awsArray = principal.get("AWS").getAsJsonArray();
                                if (awsArray.size() > 0) {
                                    logger.debug(
                                            "Not checking the s3 read/write public access for principal array values : {}",
                                            awsArray);
                                }
                            } else {
                                aws = principal.get("AWS").getAsString();
                            }

                            if (firstObject.get(PacmanRuleConstants.ACTION).isJsonObject()) {
                                JsonObject actionJson = firstObject.get(PacmanRuleConstants.ACTION).getAsJsonObject();
                                actionString = actionJson.getAsString();
                            } else if (firstObject.get(PacmanRuleConstants.ACTION).isJsonArray()) {
                                actionJsonArray = firstObject.get(PacmanRuleConstants.ACTION).getAsJsonArray();
                            } else {
                                actionString = firstObject.get(PacmanRuleConstants.ACTION).getAsString();
                            }

                            String effect = firstObject.get(PacmanRuleConstants.EFFECT).getAsString();
                            if ("*".equals(aws)) {
                                if (firstObject.has(PacmanRuleConstants.CONDITION)
                                        && (firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                                .has(PacmanRuleConstants.IP_ADDRESS_CAP))
                                        && (firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                                .get(PacmanRuleConstants.IP_ADDRESS_CAP).getAsJsonObject()
                                                .has(PacmanRuleConstants.SOURCE_IP))) {
                                    if (firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                            .get(PacmanRuleConstants.IP_ADDRESS_CAP).getAsJsonObject()
                                            .get(PacmanRuleConstants.SOURCE_IP).isJsonObject()) {
                                        conditionJsonObject = firstObject.get(PacmanRuleConstants.CONDITION)
                                                .getAsJsonObject().get(PacmanRuleConstants.IP_ADDRESS_CAP)
                                                .getAsJsonObject().get(PacmanRuleConstants.SOURCE_IP).getAsJsonObject();
                                    } else if (firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                            .get(PacmanRuleConstants.IP_ADDRESS_CAP).getAsJsonObject()
                                            .get(PacmanRuleConstants.SOURCE_IP).isJsonArray()) {
                                        conditionJsonArray = firstObject.get(PacmanRuleConstants.CONDITION)
                                                .getAsJsonObject().get(PacmanRuleConstants.IP_ADDRESS_CAP)
                                                .getAsJsonObject().get(PacmanRuleConstants.SOURCE_IP).getAsJsonArray();
                                    } else {
                                        conditionStr = firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                                .get(PacmanRuleConstants.IP_ADDRESS_CAP).getAsJsonObject()
                                                .get(PacmanRuleConstants.SOURCE_IP).getAsString();
                                    }
                                }

                                JsonElement cJson = conditionJsonArray;
                                Type listType = new TypeToken<List<String>>() {
                                }.getType();

                                List<String> conditionList = new Gson().fromJson(cJson, listType);
                                if (!org.apache.commons.lang.StringUtils.isEmpty(actionString)) {
                                    map = getReadWriteAccess(actionString, accessType, effect, conditionJsonObject,
                                            conditionList, conditionStr, map);
                                }
                                if (actionJsonArray.size() > 0) {
                                    for (int j = 0; j < actionJsonArray.size(); j++) {
                                        actionString = actionJsonArray.get(j).getAsString();
                                        map = getReadWriteAccess(actionString, accessType, effect, conditionJsonObject,
                                                conditionList, conditionStr, map);
                                    }
                                }
                            }
                        }
                    } catch (Exception e1) {
                        logger.error("error", e1);
                        throw new RuleExecutionFailedExeption(e1.getMessage());
                    }
                }
            }
        }
        return map;
    }

    private static Map<String, Boolean> getReadWriteAccess(String actionString, String accessType, String effect,
            JsonObject conditionJsonObject, List<String> conditionList, String conditionStr,
            Map<String, Boolean> accessMap) {
        if ((actionString.startsWith(PacmanRuleConstants.S3_PUT) || actionString.startsWith("s3:*"))
                && accessType.equalsIgnoreCase(PacmanRuleConstants.WRITE_ACCESS)
                && (PacmanRuleConstants.ALLOW.equalsIgnoreCase(effect))) {
            getReadOrWriteAccessDetails(PacmanRuleConstants.WRITE, accessMap, PacmanRuleConstants.CIDR_FILTERVALUE,
                    conditionStr, conditionJsonObject, conditionList);

        } else if ((actionString.startsWith(PacmanRuleConstants.S3_GET) || actionString.startsWith("s3:*"))
                && accessType.equalsIgnoreCase(PacmanRuleConstants.READ_ACCESS)
                && (PacmanRuleConstants.ALLOW.equalsIgnoreCase(effect))) {
            getReadOrWriteAccessDetails("Read", accessMap, PacmanRuleConstants.CIDR_FILTERVALUE, conditionStr,
                    conditionJsonObject, conditionList);

        }
        return accessMap;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getEBSVolumeWithCheckId(String checkId, String id, String esUrl, String region,
            String accountId) throws Exception {
        JsonParser jsonParser = new JsonParser();
        Map<String, String> resourceInfoMap = new HashMap<>();

        String resourceinfo = getQueryDataForCheckid(checkId, esUrl, id, region, accountId);
        if (resourceinfo != null) {
            JsonObject resourceinfoJson = (JsonObject) jsonParser.parse(resourceinfo);
            resourceInfoMap = new Gson().fromJson(resourceinfoJson, Map.class);

        }
        return resourceInfoMap;
    }

    public static Map<String, String> getEBSSnapshotWithCheckId(String checkId, String id, String esUrl, String region,
            String accountId) throws Exception {
        JsonParser jsonParser = new JsonParser();
        Map<String, String> data = new HashMap<>();

        String resourceinfo = getQueryDataForCheckid(checkId, esUrl, id, region, accountId);
        if (resourceinfo != null) {
            JsonObject resourceinfoJson = (JsonObject) jsonParser.parse(resourceinfo);
            String snapshotId = resourceinfoJson.get("Snapshot ID").getAsString();
            if (!Strings.isNullOrEmpty(snapshotId) && snapshotId.equals(id)) {

                String status = resourceinfoJson.get(PacmanRuleConstants.STATUS_CAP).getAsString();
                String volumeId = resourceinfoJson.get("Volume ID").getAsString();
                String description = resourceinfoJson.get("Description").getAsString();

                data.put("status", status);
                data.put("volumeId", volumeId);
                data.put("description", description);
            }
        }
        return data;
    }

    public static Map<String, String> getRDSSnapshotWithCheckId(String checkId, String id, String esUrl, String region,
            String accountId) throws Exception {
        JsonParser jsonParser = new JsonParser();
        Map<String, String> data = new HashMap<>();

        String resourceinfo = getQueryDataForCheckid(checkId, esUrl, id, region, accountId);
        if (resourceinfo != null) {
            JsonObject resourceinfoJson = (JsonObject) jsonParser.parse(resourceinfo);
            String snapshotId = resourceinfoJson.get("Snapshot ID").getAsString();
            if (!Strings.isNullOrEmpty(snapshotId) && snapshotId.equals(id)) {
                String status = resourceinfoJson.get(PacmanRuleConstants.STATUS_CAP).getAsString();
                String clusterId = resourceinfoJson.get("DB Instance or Cluster ID").getAsString();

                data.put("status", status);
                data.put("dbInstanceOrClusterId", clusterId);
            }
        }
        return data;
    }

    public static boolean checkSSMAgent(String id, String esUrl, String attributeName, String region, String accountId,
            String online) throws Exception {
        JsonArray hits;
        JsonParser parser = new JsonParser();
        boolean isSSMAgentOnline = false;
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(convertAttributetoKeyword(attributeName), id);
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.REGION), region);
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.ACCOUNTID), accountId);
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.PING_STATUS), online);
        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(esUrl, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);
        if (null != resultJson && resultJson.has(PacmanRuleConstants.HITS)) {
            String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
            JsonObject hitsJson = (JsonObject) parser.parse(hitsJsonString);
            hits = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            if (hits.size() > 0) {
                JsonObject firstObject = (JsonObject) hits.get(0);
                JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                if ((null != sourceJson) && (null != sourceJson.get(PacmanRuleConstants.INSTANCEID))
                        && (!sourceJson.get(PacmanRuleConstants.INSTANCEID).isJsonNull())
                        && (!StringUtils.isEmpty(sourceJson.get(PacmanRuleConstants.INSTANCEID).getAsString())))
                    isSSMAgentOnline = true;

            }
        }
        return isSSMAgentOnline;
    }

    public static Map<String, Object> getResourceCreatedDetails(String reSourceId, String eventType,
            String heimdallESURL) throws Exception {
        String subUserName = null;
        String ou = null;
        Map<String, Object> resourceDetails = new HashMap<>();
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put("resourceid", reSourceId);
        mustFilter.put("event_type", eventType);
        JsonParser parser = new JsonParser();
        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(heimdallESURL, mustFilter, null,
                null, null, 0, null, null,null);
        if (null != resultJson && resultJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = (JsonObject) parser.parse(resultJson.get(PacmanRuleConstants.HITS).toString());
            JsonArray hits = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            if (hits.size() > 0) {
                resourceDetails.put("created_event_found", true);
                JsonObject firstObject = (JsonObject) hits.get(0);
                JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                JsonObject detailJson = (JsonObject) sourceJson.get("detail");
                JsonObject userIdentity = (JsonObject) detailJson.get("userIdentity");
                String userName = getUserName(userIdentity);

                resourceDetails.put("created_type", userIdentity.get("type").getAsString());
                resourceDetails.put(PacmanRuleConstants.USER_NAME, userName);
                if (userName != null) {
                    subUserName = userName.substring(0, 4);
                    if (subUserName.endsWith("_")) {
                        ou = subUserName.substring(0, 3);
                    }
                }

                resourceDetails.put("OU", ou);

            }

            else {
                resourceDetails.put("created_event_found", false);
            }
        }
        return resourceDetails;
    }

    private static String getUserName(JsonObject userIdentity) {
        String userName = null;
        if ("Root".equalsIgnoreCase(userIdentity.get("type").getAsString())
                || "IAMUser".equalsIgnoreCase(userIdentity.get("type").getAsString())) {
            userName = userIdentity.get(PacmanRuleConstants.USER_NAME).getAsString();
        } else if ("AssumedRole".equalsIgnoreCase(userIdentity.get("type").getAsString())) {
            JsonObject sessionContext = (JsonObject) userIdentity.get("sessionContext");
            JsonObject sessionIssuer = (JsonObject) sessionContext.get("sessionIssuer");
            userName = sessionIssuer.get(PacmanRuleConstants.USER_NAME).getAsString();
        }
        return userName;
    }

    private static Map<String, Boolean> getReadOrWriteAccessDetails(String type, Map<String, Boolean> accessMap,
            String publicIp, String conditionStr, JsonObject conditionJsonObject, List<String> conditionList) {
        if ((conditionJsonObject.size() == 0) && (conditionList.isEmpty()) && null == conditionStr) {
            accessMap.put(type, true);
        }
        if (!conditionJsonObject.isJsonNull()) {
            if (conditionJsonObject.toString().equals(publicIp)) {
                accessMap.put(type, true);
            }
        } else if (null != conditionStr && conditionStr.contains(publicIp)) {
            accessMap.put(type, true);
        } else if (conditionList.contains(publicIp)) {
            accessMap.put(type, true);
        }
        return accessMap;
    }

    /**
     * This method is to check whether s3 bucket has read/write/full control
     * 
     * @param grants
     * @param accessTypeToCheck
     * @return List<Permission>, if permissions found else empty
     */
    private static List<Permission> checkAnyGrantHasOpenToReadOrWriteAccess(List<Grant> grants, String accessTypeToCheck) {

        List<Permission> permissions = new ArrayList<>();
        for (Grant grant : grants) {
            if ((PacmanRuleConstants.ANY_S3_AUTHENTICATED_USER_URI.equalsIgnoreCase(grant.getGrantee().getIdentifier()) || PacmanRuleConstants.ALL_S3_USER_URI
                    .equalsIgnoreCase(grant.getGrantee().getIdentifier()))

                    &&

                    (grant.getPermission().toString().contains(accessTypeToCheck) || grant.getPermission().toString()
                            .equalsIgnoreCase(PacmanRuleConstants.FULL_CONTROL))) {
                permissions.add(grant.getPermission());
            }
        }
        return permissions;
    }

    public static String convertAttributetoKeyword(String attributeName) {
        return attributeName + ".keyword";
    }

    public static Annotation createELBAnnotation(String elbType, Map<String, String> ruleParam,
             String description, String severity, String category) {
        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

        Annotation annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
        annotation.put(PacmanSdkConstants.DESCRIPTION, description);
        annotation.put(PacmanRuleConstants.SEVERITY, severity);
        annotation.put(PacmanRuleConstants.SUBTYPE, Annotation.Type.RECOMMENDATION.toString());
        annotation.put(PacmanRuleConstants.CATEGORY, category);

        issue.put(PacmanRuleConstants.VIOLATION_REASON, description);
        issueList.add(issue);
        annotation.put(PacmanRuleConstants.ISSUE_DETAILS, issueList.toString());
        logger.debug("========Unused ElbRule ended with annotation {} :=========", annotation);
        return annotation;
    }

    
    /**
     * 
     * @param envVariableName
     * @return
     */

    public static String getPacmanHost(String envVariableName) {
        String pacmanHost = null;
        if (!Strings.isNullOrEmpty(envVariableName)) {
            pacmanHost = System.getenv(envVariableName);
        }
        return pacmanHost;
    }
    
    public static String formatUrl(Map<String, String> ruleParam, String param) {
        String pacmanHost = getPacmanHost(PacmanRuleConstants.ES_URI);
        if (!StringUtils.isEmpty(pacmanHost)) {
            return pacmanHost + ruleParam.get(param);
        }
        return null;
    }
    
    public static String formatUrl(Map<String, String> ruleParam, String param,String host) {
        String pacmanHost = getPacmanHost(host);
        if (!StringUtils.isEmpty(pacmanHost)) {
            return pacmanHost + ruleParam.get(param);
        }
        return null;
    }
    
    
    /**
     * Does all have value.
     *
     * @param strings the strings
     * @return the boolean
     */
    public static Boolean doesAllHaveValue(String...strings ){
        if(null==strings || strings.length==0){
            return Boolean.FALSE;
        }
        for(String str:strings){
            if(Strings.isNullOrEmpty(str)){
                logger.error("Blank value found for param" + str);
                return Boolean.FALSE; 
            }
        }
        return Boolean.TRUE;
    }
    
    /**
     * splits a string with separator into List.
     *
     * @param toSplit the to split
     * @param separator the separator
     * @return the list
     */
    public static List<String> splitStringToAList(String toSplit, String separator){
        return Lists.newArrayList(Splitter.on(separator).split(toSplit));
    }
    
    /**
     * Calculates the difference between the given date and current date.
     *
     * @param formattedDateString the formatted date string
     * @return the long
     */
    public static Long calculateLaunchedDuration(String formattedDateString) {
        if(formattedDateString!=null){
        LocalDate expiryDate = LocalDate.parse(formattedDateString);
        LocalDate today = LocalDate.now();
        return java.time.temporal.ChronoUnit.DAYS.between(expiryDate, today);
        }else{
            return 0l;
        }
    }
    
    public static boolean isNonStandardRegion(List<String> standardRegions,
            String region) {
        return !standardRegions.isEmpty() && standardRegions.contains(region);
    }

    /**
     * Checks if is igw found.
     *
     * @param cidrIp the cidr ip
     * @param id the id
     * @param type the type
     * @param issue the issue
     * @param routeTableIdSet the route table id set
     * @param routetableRoutesEsURL the routetable routes es URL
     * @param internetGateWay the internet gate way
     * @param cidrIpv6 the cidr ipv 6
     * @return the boolean
     * @throws Exception the exception
     */
    public static Boolean isIgwFound(String cidrIp, String id, String type, Map<String, Object> issue,
            Set<String> routeTableIdSet, String routetableRoutesEsURL, String internetGateWay,String cidrIpv6) throws Exception {
        Boolean isIgwExists = false;
        List<String> routeTableIdList = new ArrayList<>();
        if (!CollectionUtils.isNullOrEmpty(routeTableIdSet)) {
            isIgwExists = getRouteTableRoutesId(routeTableIdList, routeTableIdSet, routetableRoutesEsURL,
            		cidrIp, internetGateWay,cidrIpv6);
            if ("VPC".equals(type)) {
                issue.put(PacmanRuleConstants.VPCID, id);
            } else {
                issue.put(PacmanRuleConstants.SUBID, id);
            }
            if (isIgwExists) {
                issue.put(PacmanRuleConstants.IGW_OPENED, type);
                issue.put(PacmanRuleConstants.ROUTE_TABLEID, String.join(",", routeTableIdList));
                return isIgwExists;
            }

        }
        return isIgwExists;
    }
    
    public static String getKernelInfoFromElasticSearchBySource(
            String instanceId, String kernelInfoApi, String source)
            throws Exception {
        JsonArray hits;
        JsonParser parser = new JsonParser();
        String kernelVersion = null;
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.RESOURCE_ID),instanceId);
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.SOURCE_FIELD),source);
        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(kernelInfoApi, mustFilter,mustNotFilter, shouldFilter, null, 0, mustTermsFilter,null,null);
        if (null != resultJson && resultJson.has(PacmanRuleConstants.HITS)) {
            String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
            JsonObject hitsJson = (JsonObject) parser.parse(hitsJsonString);
            hits = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            if (hits.size() > 0) {
                JsonObject firstObject = (JsonObject) hits.get(0);
                JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                if (null != sourceJson && sourceJson.has(PacmanRuleConstants.KERNEL_FIELD)) {
                    kernelVersion = sourceJson.get(PacmanRuleConstants.KERNEL_FIELD).getAsString();
                }
            }
        }
        return kernelVersion;
    }
    
    /**
     * Creates the annotation.
     *
     * @param resourceType the resource type
     * @param ruleParam the rule param
     * @param description the description
     * @param severity the severity
     * @param category the category
     * @return the annotation
     */
    public static Annotation createAnnotation(String resourceType, Map<String, String> ruleParam, String description,
            String severity, String category) {
        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

        Annotation annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
        annotation.put(PacmanSdkConstants.DESCRIPTION, description);
        annotation.put(PacmanRuleConstants.SEVERITY, severity);
        if (!StringUtils.isEmpty(resourceType)) {
            annotation.put(PacmanRuleConstants.SUBTYPE, Annotation.Type.RECOMMENDATION.toString());
        }
        annotation.put(PacmanRuleConstants.CATEGORY, category);

        issue.put(PacmanRuleConstants.VIOLATION_REASON, description);
        issueList.add(issue);
        annotation.put(PacmanRuleConstants.ISSUE_DETAILS, issueList.toString());
        return annotation;
    }
    
    /**
     * Gets the route table routes id.
     *
     * @param routeTableIdList the route table id list
     * @param routeTableIdSet the route table id set
     * @param routetableRoutesEsURL the routetable routes es URL
     * @param cidrIp the cidr ip
     * @param internetGateWay the internet gate way
     * @param cidrIpv6 the cidr ipv 6
     * @return the route table routes id
     * @throws Exception the exception
     */
    public static boolean getRouteTableRoutesId(List<String> routeTableIdList, Set<String> routeTableIdSet,
            String routetableRoutesEsURL, String cidrIp, String internetGateWay,String cidrIpv6) throws Exception {
        String gatewayid = null;
        JsonParser jsonParser = new JsonParser();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustTermsFilter.put(convertAttributetoKeyword(PacmanRuleConstants.ROUTE_TABLE_ID), routeTableIdSet);
        shouldFilter.put(convertAttributetoKeyword(PacmanRuleConstants.DEST_CIDR_BLOCK), cidrIp);
        shouldFilter.put(convertAttributetoKeyword(PacmanRuleConstants.DEST_CIDR_IPV6_BLOCK), cidrIpv6);
        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(routetableRoutesEsURL,mustFilter, mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);

        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(PacmanRuleConstants.HITS).toString());

            JsonArray hitsArray = hitsJson.getAsJsonArray(PacmanRuleConstants.HITS);
            for (int i = 0; i < hitsArray.size(); i++) {
                JsonObject source = hitsArray.get(i).getAsJsonObject().get(PacmanRuleConstants.SOURCE)
                        .getAsJsonObject();
                gatewayid = source.get(PacmanRuleConstants.GATE_WAY_ID).getAsString();
                if (!org.apache.commons.lang.StringUtils.isEmpty(gatewayid)
                        && gatewayid.toLowerCase().startsWith(internetGateWay)) {
                    routeTableIdList.add(source.get(PacmanRuleConstants.ROUTE_TABLE_ID).getAsString());
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Proccess cidr ip or cidr ipv 6 data.
     *
     * @param resultJson the result json
     * @param portToCheck the port to check
     * @param openPorts the open ports
     * @param target the target
     * @return the map
     */
    private static Map<String, Boolean> proccessCidrIpOrCidrIpv6Data(JsonObject resultJson, String portToCheck,
            LinkedHashMap<String, Boolean> openPorts,String target) {
        String fromPort = null;
        String toPort = null;
        String ipprotocol = null;
        JsonParser jsonParser = new JsonParser();
        if (resultJson != null) {
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(PacmanRuleConstants.HITS).toString());
            JsonArray hitsArray = hitsJson.getAsJsonArray(PacmanRuleConstants.HITS);
            if (hitsArray.size() > 0 && portToCheck.equalsIgnoreCase("any")) {
                return getFromAndToPorts(PacmanRuleConstants.ANY_PORT, PacmanRuleConstants.ANY_PORT, PacmanRuleConstants.ANY_PORT, openPorts);
            }
            for (int i = 0; i < hitsArray.size(); i++) {
                JsonObject source = hitsArray.get(i).getAsJsonObject().get(PacmanRuleConstants.SOURCE)
                        .getAsJsonObject();
                fromPort = source.get("fromport").getAsString();
                toPort = source.get("toport").getAsString();
                ipprotocol = source.get("ipprotocol").getAsString();
                logger.info(fromPort);
                logger.info(toPort);
                logger.info(ipprotocol);

                if ((!org.apache.commons.lang.StringUtils.isEmpty(fromPort) && !org.apache.commons.lang.StringUtils
                        .isEmpty(toPort) && !"icmp".equalsIgnoreCase(ipprotocol))) {
                	if(StringUtils.isEmpty(target)){
                    if (!"All".equalsIgnoreCase(toPort) && !"All".equalsIgnoreCase(fromPort)) {

                    	if (PacmanRuleConstants.SSH_PORT.equals(portToCheck)) {
							if (portToCheck.equals(fromPort) || (Long.parseLong(fromPort) == Long.parseLong("0") && Long.parseLong(toPort) == Long.parseLong("1024"))) {
								getFromAndToPorts(fromPort, toPort, ipprotocol, openPorts);
							} 
						} else if (PacmanRuleConstants.RDP_PORT.equals(portToCheck)) {
							if (portToCheck.equals(fromPort) || (Long.parseLong(fromPort) == Long.parseLong("1024") && Long.parseLong(toPort) == Long.parseLong("4098"))) {
								getFromAndToPorts(fromPort, toPort, ipprotocol, openPorts);
							} 
						}else if (portToCheck.equals(fromPort) || (Long.parseLong(portToCheck) >= Long.parseLong(fromPort) && Long.parseLong(portToCheck) <= Long.parseLong(toPort))) {
							getFromAndToPorts(fromPort, toPort, ipprotocol, openPorts);
						}
                    } else {
                        if (!"All".equalsIgnoreCase(fromPort)) {

                            if ( portToCheck.equals(fromPort)
                                    || (Long.parseLong(portToCheck) >= Long.parseLong(fromPort) && "All"
                                            .equalsIgnoreCase(toPort))) {
                                getFromAndToPorts(fromPort, toPort, ipprotocol, openPorts);
                            }
                        } else {
                            getFromAndToPorts(fromPort, toPort, ipprotocol, openPorts);
                        }
                    }
                }else{
                	   if (!"All".equalsIgnoreCase(fromPort)) {

                           if (Long.parseLong(fromPort) <= Long.parseLong(target)) {
                               getFromAndToPorts(fromPort, toPort, ipprotocol, openPorts);
                           }
                       } else {
                           getFromAndToPorts(fromPort, toPort, ipprotocol, openPorts);
                       }
                }}
                }
            
        }
        return openPorts;
    }
    
    /**
     * Checks if is having public access.
     *
     * @param jsonArray the json array
     * @param endPoint the end point
     * @return true, if is having public access
     */
    public static boolean isHavingPublicAccess(JsonArray jsonArray, String endPoint) {
        boolean isPublicAccess = false;
        JsonObject conditionJsonObject = new JsonObject();
        JsonArray conditionJsonArray = new JsonArray();
        String conditionStr = null;
        JsonObject principal = new JsonObject();
        String effect = null;
        String principalStr = null;
        String aws = null;
        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject firstObject = (JsonObject) jsonArray.get(i);

                if (firstObject.has(PacmanRuleConstants.PRINCIPAL)
                        && firstObject.get(PacmanRuleConstants.PRINCIPAL).isJsonObject()) {
                    principal = firstObject.get(PacmanRuleConstants.PRINCIPAL).getAsJsonObject();
                } else {
                    principalStr = firstObject.get(PacmanRuleConstants.PRINCIPAL).getAsString();
                }
                try {
                    if (principal.has("AWS") || "*".equals(principalStr)) {
                        JsonArray awsArray = null;
                        effect = firstObject.get(PacmanRuleConstants.EFFECT).getAsString();
                        if (principal.has("AWS") && principal.get("AWS").isJsonArray()) {
                            awsArray = principal.get("AWS").getAsJsonArray();
                            if (awsArray.size() > 0) {
                                logger.debug(
                                        "Not checking the s3 read/write public access for principal array values : {}",
                                        awsArray);
                            }
                        }

                        if (principal.has("AWS") && !principal.get("AWS").isJsonArray()) {
                            aws = principal.get("AWS").getAsString();
                        }
                        if ("*".equals(principalStr)) {
                            aws = firstObject.get(PacmanRuleConstants.PRINCIPAL).getAsString();
                        }

                        if ("*".equals(aws) && !firstObject.has(PacmanRuleConstants.CONDITION)) {
                            if (effect.equals(PacmanRuleConstants.ALLOW)) {
                                isPublicAccess = true;
                            }
                        } else if ("*".equals(aws) && firstObject.has(PacmanRuleConstants.CONDITION)
                                && effect.equals(PacmanRuleConstants.ALLOW)) {
                            if (firstObject.has(PacmanRuleConstants.CONDITION)
                                    && (firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                            .has(PacmanRuleConstants.IP_ADDRESS_CAP))
                                    && (firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                            .get(PacmanRuleConstants.IP_ADDRESS_CAP).getAsJsonObject()
                                            .has(PacmanRuleConstants.SOURCE_IP))) {
                                if (firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                        .get(PacmanRuleConstants.IP_ADDRESS_CAP).getAsJsonObject()
                                        .get(PacmanRuleConstants.SOURCE_IP).isJsonObject()) {
                                    conditionJsonObject = firstObject.get(PacmanRuleConstants.CONDITION)
                                            .getAsJsonObject().get(PacmanRuleConstants.IP_ADDRESS_CAP)
                                            .getAsJsonObject().get(PacmanRuleConstants.SOURCE_IP).getAsJsonObject();
                                } else if (firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                        .get(PacmanRuleConstants.IP_ADDRESS_CAP).getAsJsonObject()
                                        .get(PacmanRuleConstants.SOURCE_IP).isJsonArray()) {
                                    conditionJsonArray = firstObject.get(PacmanRuleConstants.CONDITION)
                                            .getAsJsonObject().get(PacmanRuleConstants.IP_ADDRESS_CAP)
                                            .getAsJsonObject().get(PacmanRuleConstants.SOURCE_IP).getAsJsonArray();
                                } else {
                                    conditionStr = firstObject.get(PacmanRuleConstants.CONDITION).getAsJsonObject()
                                            .get(PacmanRuleConstants.IP_ADDRESS_CAP).getAsJsonObject()
                                            .get(PacmanRuleConstants.SOURCE_IP).getAsString();
                                }
                            }

                            JsonElement cJson = conditionJsonArray;
                            Type listType = new TypeToken<List<String>>() {
                            }.getType();

                            List<String> conditionList = new Gson().fromJson(cJson, listType);
                            if (!conditionJsonObject.isJsonNull()
                                    && conditionJsonObject.toString().equals(PacmanRuleConstants.CIDR_FILTERVALUE)) {
                                isPublicAccess = true;
                            }

                            if (null != conditionStr && conditionStr.contains(PacmanRuleConstants.CIDR_FILTERVALUE)) {
                                isPublicAccess = true;
                            }
                            if (conditionList.contains(PacmanRuleConstants.CIDR_FILTERVALUE)) {
                                isPublicAccess = true;
                            }

                        }
                    }
                } catch (Exception e1) {
                    logger.error("error", e1);
                    throw new RuleExecutionFailedExeption(e1.getMessage());
                }
            }
        }
        return isPublicAccess;
    }
    
    /**
     * Gets the security grouplist.
     *
     * @param securityGroupId the security group id
     * @param delimeter the delimeter
     * @param securityGrouplist the security grouplist
     * @return the security grouplist
     */
    public static List<GroupIdentifier> getSecurityGrouplist(String securityGroupId, String delimeter,
            List<GroupIdentifier> securityGrouplist) {
        List<String> sgList = new ArrayList(Arrays.asList(securityGroupId.split(delimeter)));
        for (String sg : sgList) {
            GroupIdentifier groupIdentifier = new GroupIdentifier();
            groupIdentifier.setGroupId(sg);
            securityGrouplist.add(groupIdentifier);
        }
        return securityGrouplist;
    }
    
    /**
     * Sets the annotation.
     *
     * @param openPortsMap the open ports map
     * @param ruleParam the rule param
     * @param subnetId the subnet id
     * @param descrition the descrition
     * @param issue the issue
     * @return the annotation
     */
    public static Annotation setAnnotation(Map<String, Boolean> openPortsMap, Map<String, String> ruleParam,
            String subnetId, String descrition, LinkedHashMap<String, Object> issue) {
        Annotation annotation = null;
        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        List<String> portsSet = new ArrayList<>();
        for (Map.Entry<String, Boolean> ports : openPortsMap.entrySet()) {
            portsSet.add(ports.getKey());
        }

        annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
        annotation.put(PacmanSdkConstants.DESCRIPTION, descrition);
        annotation.put(PacmanRuleConstants.SEVERITY, ruleParam.get(PacmanRuleConstants.SEVERITY));
        annotation.put(PacmanRuleConstants.CATEGORY, ruleParam.get(PacmanRuleConstants.CATEGORY));
        annotation.put(PacmanRuleConstants.VPC_ID, ruleParam.get(PacmanRuleConstants.VPC_ID));
        annotation.put(PacmanRuleConstants.SUBNETID, subnetId);
        issue.put(PacmanRuleConstants.VIOLATION_REASON, descrition);
        issueList.add(issue);
        annotation.put("issueDetails", issueList.toString());
        logger.debug("========ApplicationElbPublicAccessRule ended with an annotation {} : =========", annotation);
        return annotation;
    }
    
    /**
     * Gets the security broup id by elb.
     *
     * @param resourceId the resource id
     * @param elbSecurityApi the elb security api
     * @param accountId the account id
     * @param region the region
     * @return the security broup id by elb
     * @throws Exception the exception
     */
    public static List<GroupIdentifier> getSecurityBroupIdByElb(String resourceId, String elbSecurityApi,
            String accountId, String region) throws Exception {
        JsonArray hits;
        JsonParser parser = new JsonParser();
        String securityGroupId = null;
        List<GroupIdentifier> securityGrouplist = new ArrayList<>();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.LOAD_BALANCER_ID_ATTRIBUTE), resourceId);
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.ACCOUNTID), accountId);
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.REGION_ATTR), region);
        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(elbSecurityApi, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);
        if (null != resultJson && resultJson.has(PacmanRuleConstants.HITS)) {
            String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
            JsonObject hitsJson = (JsonObject) parser.parse(hitsJsonString);
            hits = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            if (hits.size() > 0) {
                JsonObject firstObject = (JsonObject) hits.get(0);
                JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                if (null != sourceJson && sourceJson.has(PacmanRuleConstants.EC2_WITH_SECURITYGROUP_ID)) {
                    securityGroupId = sourceJson.get(PacmanRuleConstants.EC2_WITH_SECURITYGROUP_ID).getAsString();
                    getSecurityGrouplist(securityGroupId, ":;", securityGrouplist);
                }
            }
        }
        return securityGrouplist;
    }
    
    /**
     * Creates the S3 annotation.
     *
     * @param ruleParam the rule param
     * @param description the description
     * @return the annotation
     */
    public static Annotation createS3Annotation(Map<String, String> ruleParam, String description) {
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		Annotation annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
		annotation.put(PacmanSdkConstants.DESCRIPTION, description);
		annotation.put(PacmanRuleConstants.SEVERITY, severity);
		annotation.put(PacmanRuleConstants.CATEGORY, category);
		List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
		LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
		issue.put(PacmanRuleConstants.VIOLATION_REASON, description);
		issueList.add(issue);
		annotation.put(PacmanRuleConstants.ISSUE_DETAILS, issueList.toString());
		logger.debug("S3HostsWebsiteRule ended with an annotation : {} =========", annotation);
		return annotation;
	}
    
    /**
     * Gets the security groups by resource id.
     *
     * @param resourceId the resourceId
     * @param esUrl the es url
     * @param resourceField the resource field
     * @param sgField the sgField
     * @param sgStatusField the sgStatusField
     * @return the security groups by resource id
     * @throws Exception the exception
     */
    public static List<GroupIdentifier> getSecurityGroupsByResourceId(String resourceId, String esUrl,String resourceField,String sgField,String sgStatusField) throws Exception {
        List<GroupIdentifier> list = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put(convertAttributetoKeyword(resourceField), resourceId);
        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(esUrl, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,null);
        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(PacmanRuleConstants.HITS).toString());
            JsonArray hitsArray = hitsJson.getAsJsonArray(PacmanRuleConstants.HITS);
            for (int i = 0; i < hitsArray.size(); i++) {
                JsonObject source = hitsArray.get(i).getAsJsonObject().get(PacmanRuleConstants.SOURCE)
                        .getAsJsonObject();
                String securitygroupid = source.get(sgField).getAsString();
                String vpcSecuritygroupStatus = source.get(sgStatusField).getAsString();
                if("active".equals(vpcSecuritygroupStatus)){
                GroupIdentifier groupIdentifier = new GroupIdentifier();
                if (!com.amazonaws.util.StringUtils.isNullOrEmpty(securitygroupid)) {
                    groupIdentifier.setGroupId(securitygroupid);
                    list.add(groupIdentifier);
                }
            }
            }
        }
        return list;
    }
    
    /**
     * Gets the environment variable.
     *
     * @param envVar the env var
     * @return the environment variable
     */
    public static String getEnvironmentVariable(String envVar){
    	return System.getenv(envVar);
    }
    
    /**
     * Gets the header.
     *
     * @param base64Creds the base 64 creds
     * @return the header
     */
    public static Map<String,String> getHeader(String base64Creds){
        Map<String,String> authToken = new HashMap<>();
        authToken.put("Content-Type", ContentType.APPLICATION_JSON.toString());
        authToken.put("Authorization", "Basic "+base64Creds);
        return authToken;
    }
    
    /**
     * Method for getting the configurations via PACMAN API
     *  
     *
     * @param url the url
     * @param headers the headers
     * @return configurations JsonObject
     */
	public static JsonObject getConfigurationsFromConfigApi(String url,Map<String,String> headers) {
		String resultStringPost = null;
		Gson gson = new Gson();
		try {
			resultStringPost = doHttpGet(url,headers);
			if (!StringUtils.isEmpty(resultStringPost)) {
				return gson.fromJson(resultStringPost, JsonObject.class);
			}

		} catch (Exception e) {
			logger.error("Exceptions occured in getConfigurationsFromConfigApi========",e);
			return null;
		}
		return null;
	}
	
	 /**
     * Gets the value from elastic search as set.
     *
     * @param esUrl the es url
     * @param mustFilterMap the must filter map
     * @param shouldFilterMap the should filter map
     * @param mustTermsFilterMap the must terms filter map
     * @param fieldKey the field key
     * @param matchPhrase the match phrase
     * @return the value from elastic search as set
     * @throws Exception the exception
     */
    public static Set<String> getValueFromElasticSearchAsSet(String esUrl, Map<String,Object> mustFilterMap,HashMultimap<String, Object> shouldFilterMap,Map<String, Object> mustTermsFilterMap,String fieldKey,Map<String, List<String>> matchPhrase)
            throws Exception {
        JsonParser jsonParser = new JsonParser();

        Map<String, Object> mustFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustNotFilter = new HashMap<>();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        
		if (!mustFilterMap.isEmpty()) {
			for(Map.Entry<String,Object> mustFilMap: mustFilterMap.entrySet()){
			mustFilter.put(convertAttributetoKeyword(mustFilMap.getKey()), mustFilMap.getValue());
			}
		}
		
		if (!shouldFilterMap.isEmpty()) {
			for(Map.Entry<String,Object> shouldFilMap: shouldFilterMap.entries()){
				shouldFilter.put(convertAttributetoKeyword(shouldFilMap.getKey()), shouldFilMap.getValue());
			}
		}
		
		if (!mustTermsFilterMap.isEmpty()) {
			for(Map.Entry<String,Object> mustTermsFilMap: mustTermsFilterMap.entrySet()){
				mustTermsFilter.put(convertAttributetoKeyword(mustTermsFilMap.getKey()), mustTermsFilMap.getValue());
			}
		}
        
        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(esUrl+"?size=10000", mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,matchPhrase);
        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
            JsonObject hitsJson = (JsonObject) jsonParser.parse(hitsJsonString);
            JsonArray jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            return returnFieldValueAsSet(jsonArray,fieldKey);

        }
        return null;
    }
    
    /**
     * Checks if is field exists.
     *
     * @param jsonArray the json array
     * @param fieldKey the field key
     * @return String, if is instance exists
     */
    private static Set<String> returnFieldValueAsSet(JsonArray jsonArray,String fieldKey) {
    	 Set<String> fieldValueList = new HashSet<>();
        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject firstObject = (JsonObject) jsonArray.get(i);
                JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                if (sourceJson != null
                        && (sourceJson.get(fieldKey) != null && !sourceJson.get(fieldKey).isJsonNull())) {
                    String fieldValue = sourceJson.get(fieldKey).getAsString();
                    if (!org.apache.commons.lang.StringUtils.isEmpty(fieldValue)) {
                    	fieldValueList.add(fieldValue);
                    	
                    }
                }
            }
        }
        return fieldValueList;
    }
    
    /**
     * Gets the query from elastic search.
     *
     * @param securityGroupId the security group id
     * @param serviceWithSgEsUrl the service with sg es url
     * @param esUrlParam the es url param
     * @param ruleParams the rule params
     * @return the query from elastic search
     * @throws Exception the exception
     */
    public static String getQueryFromElasticSearch(String securityGroupId,
            List<String> serviceWithSgEsUrl, String esUrlParam,Map<String,String> ruleParams) throws Exception {
        String securityGroupAttribute = null;
        String servicesWithSgurl = null;
        String returnedValue = null;
        String latest = "";
        for (String esUrl : serviceWithSgEsUrl) {
            servicesWithSgurl = esUrlParam + esUrl;
            if (esUrl.contains("ec2") || esUrl.contains("lambda") || esUrl.contains("appelb")
                    || esUrl.contains("classicelb") || esUrl.contains("elasticsearch")) {
                securityGroupAttribute = PacmanRuleConstants.EC2_WITH_SECURITYGROUP_ID;
                if(esUrl.contains("elasticsearch")){
                	latest = "true";
                }
            } else {
                securityGroupAttribute = PacmanRuleConstants.SECURITYGROUP_ID_ATTRIBUTE;
            }
            Map<String, List<String>> matchPhrase = new HashMap<>();
            
            List<String> ids = new ArrayList<>();
            ids.add(securityGroupId);
            matchPhrase.put(securityGroupAttribute, ids);
            	 returnedValue =  getValueFromElasticSearch(ruleParams.get("accountid"),"", servicesWithSgurl, securityGroupAttribute, ruleParams.get("region"), securityGroupAttribute, latest,matchPhrase);
			if (!StringUtils.isEmpty(returnedValue)) {
				List<GroupIdentifier> listSecurityGroupID = new ArrayList<>();
				getSecurityGrouplist(returnedValue, ":;", listSecurityGroupID);
				for(GroupIdentifier sgId:listSecurityGroupID){
					if(sgId.getGroupId().equals(securityGroupId)){
						return securityGroupId;
					}
				}
				
			}
           
        }
		return returnedValue;
    }
    
    /**
     * get value from elastic search.
     *
     * @param accountId the account id
     * @param id the id
     * @param esUrl the es url
     * @param attributeName the attribute name
     * @param region the region
     * @param fieldKey the field key
     * @param latest the latest
     * @param matchPhrase the match phrase
     * @return String, if successful
     * @throws Exception the exception
     */
    public static String getValueFromElasticSearch(String accountId,String id, String esUrl, String attributeName, String region,String fieldKey,String latest,Map<String, List<String>> matchPhrase)
            throws Exception {
        JsonParser jsonParser = new JsonParser();

        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustNotFilter = new HashMap<>();
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        
		if (!StringUtils.isEmpty(id)) {
			mustFilter.put(convertAttributetoKeyword(attributeName), id);
		}
        
        if(!StringUtils.isEmpty(region)){
        mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.REGION), region);
        }
        
        if(!StringUtils.isEmpty(latest)){
        	mustFilter.put(PacmanRuleConstants.LATEST, "true");
        }
        
        if(!StringUtils.isEmpty(accountId)){
        	mustFilter.put(convertAttributetoKeyword(PacmanRuleConstants.ACCOUNTID), accountId);
        }

        JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(esUrl, mustFilter,
                mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null,matchPhrase);
        if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
            String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
            JsonObject hitsJson = (JsonObject) jsonParser.parse(hitsJsonString);
            JsonArray jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
            return returnFieldValue(jsonArray,fieldKey);

        }
        return null;
    }
    
    /**
     * Checks if is field exists.
     *
     * @param jsonArray the json array
     * @param fieldKey the field key
     * @return String, if is instance exists
     */
    private static String returnFieldValue(JsonArray jsonArray,String fieldKey) {
        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject firstObject = (JsonObject) jsonArray.get(i);
                JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
                if (sourceJson != null
                        && (sourceJson.get(fieldKey) != null && !sourceJson.get(fieldKey).isJsonNull())) {
                    String fieldValue = sourceJson.get(fieldKey).getAsString();
                    if (!org.apache.commons.lang.StringUtils.isEmpty(fieldValue)) {
                        return fieldValue;
                    }
                }
            }
        }
        return null;
    }
    
	/**
	 * Check instance id for port rule in ES.
	 *
	 * @param instanceId
	 *            the instance id
	 * @param ec2PortUrl
	 *            the ec 2 port url
	 * @param ruleId
	 *            the rule id
	 * @param type
	 *            the type
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	public static boolean checkInstanceIdForPortRuleInES(String instanceId, String ec2PortUrl, String ruleId,
			String type) throws Exception {
		JsonParser jsonParser = new JsonParser();
		String resourceid = null;
		Map<String, Object> mustFilter = new HashMap<>();
		Map<String, Object> mustNotFilter = new HashMap<>();
		HashMultimap<String, Object> shouldFilter = HashMultimap.create();
		Map<String, Object> mustTermsFilter = new HashMap<>();
		if (StringUtils.isEmpty(type)) {
			shouldFilter.put(convertAttributetoKeyword(PacmanSdkConstants.ISSUE_STATUS_KEY),
					PacmanSdkConstants.STATUS_OPEN);
		} else {
			shouldFilter.put(convertAttributetoKeyword(PacmanSdkConstants.ISSUE_STATUS_KEY),
					PacmanSdkConstants.STATUS_OPEN);
			shouldFilter.put(convertAttributetoKeyword(PacmanSdkConstants.ISSUE_STATUS_KEY),
					PacmanRuleConstants.STATUS_EXEMPTED);
		}

		mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.RULE_ID), ruleId);
		mustFilter.put(convertAttributetoKeyword(PacmanSdkConstants.RESOURCE_ID), instanceId);

		JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(ec2PortUrl, mustFilter,
				mustNotFilter, shouldFilter, null, 0, mustTermsFilter, null, null);

		if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
			JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(PacmanRuleConstants.HITS).toString());
			JsonArray hitsArray = hitsJson.getAsJsonArray(PacmanRuleConstants.HITS);
			for (int i = 0; i < hitsArray.size(); i++) {
				JsonObject source = hitsArray.get(i).getAsJsonObject().get(PacmanRuleConstants.SOURCE)
						.getAsJsonObject();
				resourceid = source.get(PacmanSdkConstants.RESOURCE_ID).getAsString();
				if (!org.apache.commons.lang.StringUtils.isEmpty(resourceid)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Check Azure Security center rules.
	 *
	 * @param esUrl
	 *            the es url
	 * @param mustfilter
	 *            the must filter map
	 * @throws Exception
	 *             the exception
	 */
	public static Map<String, Object> checkResourceIdBypolicyName(String esUrl, Map<String, Object> mustFilter)
			throws Exception {
		JsonParser jsonParser = new JsonParser();
		Map<String, Object> mustNotFilter = new HashMap<>();
		HashMultimap<String, Object> shouldFilter = HashMultimap.create();
		Map<String, Object> mustTermsFilter = new HashMap<>();
		Map<String, Object> secMap = new HashMap<>();

		JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(esUrl, mustFilter, mustNotFilter,
				shouldFilter, null, 0, mustTermsFilter, null, null);
		if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
			String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
			JsonObject hitsJson = (JsonObject) jsonParser.parse(hitsJsonString);
			JsonArray jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
			if (jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JsonObject firstObject = (JsonObject) jsonArray.get(i);
					JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
					if (null != sourceJson) {
						JsonObject recomendationJson = (JsonObject) sourceJson.get(PacmanRuleConstants.RECOMMENDATION);
						if ((null != recomendationJson.get(PacmanRuleConstants.RESOURCEID))
								&& (!recomendationJson.get(PacmanRuleConstants.RESOURCEID).isJsonNull())) {
							secMap.put(PacmanRuleConstants.RESOURCEID,
									recomendationJson.get(PacmanRuleConstants.RESOURCEID).getAsString());
							if (null != recomendationJson.get(PacmanRuleConstants.DETAILS)) {
								JsonObject detailJson = (JsonObject) sourceJson.get(PacmanRuleConstants.RECOMMENDATION);
								secMap.put(PacmanRuleConstants.DETAILS, detailJson.get(PacmanRuleConstants.DETAILS));
							}
						}

					}

				}
			}
		}
		return secMap;
	}
	
	/**
	 * Function for creating the rule list of a particular virtual machine with
	 * resource id
	 * 
	 * @param esUrl
	 * @param resourceId
	 * @param policyDefinitionName
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getAzurePolicyEvaluationResults(String esUrl, String resourceId,
			String policyDefinitionName) throws Exception {

		JsonParser jsonParser = new JsonParser();
		Map<String, Object> policyEvaluationResultsMap = new HashMap<>();
		Map<String, Object> mustFilter = new HashMap<String, Object>();
		mustFilter.put(convertAttributetoKeyword("resourceIdLower"), resourceId);
		mustFilter.put(convertAttributetoKeyword("policyDefinitionName"), policyDefinitionName);
		mustFilter.put(PacmanRuleConstants.LATEST, "true");
		JsonObject resultJson = RulesElasticSearchRepositoryUtil.getQueryDetailsFromES(esUrl, mustFilter, null, null,
				null, 0, null, null, null);
		if (resultJson != null && resultJson.has(PacmanRuleConstants.HITS)) {
			String hitsJsonString = resultJson.get(PacmanRuleConstants.HITS).toString();
			JsonObject hitsJson = (JsonObject) jsonParser.parse(hitsJsonString);
			JsonArray jsonArray = hitsJson.getAsJsonObject().get(PacmanRuleConstants.HITS).getAsJsonArray();
			if (jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JsonObject firstObject = (JsonObject) jsonArray.get(i);
					JsonObject sourceJson = (JsonObject) firstObject.get(PacmanRuleConstants.SOURCE);
					if (null != sourceJson) {
						boolean isCompliant = sourceJson.get("isCompliant").getAsBoolean();
						policyEvaluationResultsMap.put("isCompliant", isCompliant);
						policyEvaluationResultsMap.put("policyName", sourceJson.get("policyName").getAsString());
						policyEvaluationResultsMap.put("policyDescription",
								sourceJson.get("policyDescription"));

					}

				}
			}
		}
		return policyEvaluationResultsMap;
	}


}
