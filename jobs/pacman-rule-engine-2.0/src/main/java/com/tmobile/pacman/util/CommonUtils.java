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

package com.tmobile.pacman.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.autofix.manager.AuthManager;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.config.ConfigManager;

// TODO: Auto-generated Javadoc
/**
 * The Class CommonUtils.
 */
public class CommonUtils {

    /** The Constant TLS. */
    private static final String TLS = "TLS";

    /** The Constant BOOL. */
    private static final String BOOL = "bool";

    /** The Constant SHOULD. */
    private static final String SHOULD = "should";

    /**
     *
     */
    private static final String MINIMUM_SHOULD_MATCH = "minimum_should_match";
    /**
     *
     */

    private static final String MUST_NOT = "must_not";

    /** The Constant MUST. */
    private static final String MUST = "must";

    /** The Constant APPLICATION_JSON. */
    private static final String APPLICATION_JSON = "application/json";

    /** The Constant CONTENT_TYPE. */
    private static final String CONTENT_TYPE = "Content-Type";

    /** The Constant HTTPS. */
    private static final String HTTPS = "https";

    /** The Constant LOGGER. */
    static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    /** The prop. */
    static Properties prop;
    static {
    	prop = new Properties();
    	Hashtable<String, Object> configMap = ConfigManager.getConfigurationsMap();
    	if (configMap != null && !configMap.isEmpty()) {
    	   prop.putAll(configMap);
    	}else{
    	          LOGGER.info("unable to load configuration, exiting now");
    	          throw new RuntimeException("unable to load configuration");
    	      }
    	  }



    /**
     * Checks if is env variable exists.
     *
     * @param envVariableName the env variable name
     * @return the boolean
     */
    public static Boolean isEnvVariableExists(String envVariableName) {
        return !Strings.isNullOrEmpty(System.getenv(envVariableName));
    }

    /**
     * Gets the env variable value.
     *
     * @param envVariableName the env variable name
     * @return the env variable value
     */
    public static String getEnvVariableValue(String envVariableName) {
        return System.getenv(envVariableName);
    }

    /**
     * Do http post.
     *
     * @param url the url
     * @param requestBody the request body
     * @return String
     * @throws Exception the exception
     */
    public static String doHttpPost(final String url,  String requestBody) throws Exception {
        CloseableHttpClient httpclient = null;
        try {

            if (url.contains(HTTPS)) {

                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(createNoSSLContext());
                httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            } else {
                httpclient = HttpClients.custom().build();
            }
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
            StringEntity jsonEntity = new StringEntity(requestBody);
            httppost.setEntity(jsonEntity);
            HttpResponse httpresponse = httpclient.execute(httppost);
            int statusCode = httpresponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                return EntityUtils.toString(httpresponse.getEntity());
            } else {/*
                LOGGER.error(requestBody);
                throw new Exception(
                        "unable to execute post request because " + httpresponse.getStatusLine().getReasonPhrase());
            */}
            
            try {

                if (url.contains(HTTPS)) {

                    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(createNoSSLContext());
                    httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
                } else {
                    httpclient = HttpClients.custom().build();
                }
                HttpPost httppost1 = new HttpPost(url);
                if(AuthManager.getToken()!=null){
                    String accessToken =  AuthManager.getToken();
                    if(!Strings.isNullOrEmpty(accessToken))
                    {
                    	httppost1.setHeader(PacmanSdkConstants.AUTH_HEADER, "Bearer " + accessToken);
                    }
                }
                httppost1.setHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
                StringEntity jsonEntity1 = new StringEntity(requestBody);
                httppost1.setEntity(jsonEntity1);
                HttpResponse httpresponse1 = httpclient.execute(httppost1);
                int statusCode1 = httpresponse1.getStatusLine().getStatusCode();
                if (statusCode1 == HttpStatus.SC_OK || statusCode1 == HttpStatus.SC_CREATED) {
                    return EntityUtils.toString(httpresponse1.getEntity());
                } else {
                    LOGGER.error(requestBody);
                    throw new Exception(
                            "unable to execute post request because " + httpresponse1.getStatusLine().getReasonPhrase());
                }
            } catch (ParseException parseException) {
                LOGGER.error("error closing issue" + parseException);
                throw parseException;
            } catch (Exception exception) {
                LOGGER.error("error closing issue" + exception.getMessage());
                throw exception;
            } finally {
                if (null != httpclient)
                    httpclient.close();
            }
        } catch (ParseException parseException) {
            LOGGER.error("error closing issue" + parseException);
            throw parseException;
        } catch (Exception exception) {
            LOGGER.error("error closing issue" + exception.getMessage());
            throw exception;
        } finally {
            if (null != httpclient)
                httpclient.close();
        }
    }

    /**
     * Do http put.
     *
     * @param url the url
     * @param requestBody the request body
     * @return String
     * @throws Exception the exception
     */
    public static String doHttpPut(final String url, final String requestBody) throws Exception {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPut httpPut = new HttpPut(url);
            httpPut.setHeader(CONTENT_TYPE, APPLICATION_JSON);

            StringEntity jsonEntity = null;
            if (requestBody != null) {
                jsonEntity = new StringEntity(requestBody);
            }

            httpPut.setEntity(jsonEntity);
            HttpResponse httpresponse = client.execute(httpPut);
            if (httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(httpresponse.getEntity());
            } else {
            	 if(AuthManager.getToken()!=null){
                     String accessToken =  AuthManager.getToken();
                     if(!Strings.isNullOrEmpty(accessToken))
                     {
                     	httpPut.setHeader(PacmanSdkConstants.AUTH_HEADER, "Bearer " + accessToken);
                     }
                 }
                 httpPut.setEntity(jsonEntity);
                 HttpResponse httpresponse1 = client.execute(httpPut);
                 if (httpresponse1.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                     return EntityUtils.toString(httpresponse1.getEntity());
                 } else {
                     throw new Exception(
                             "unable to execute put request caused by" + EntityUtils.toString(httpresponse1.getEntity()));
                 }
            }
        } catch (ParseException parseException) {
            LOGGER.error("ParseException in getHttpPut :" + parseException.getMessage());
        } catch (IOException ioException) {
            LOGGER.error("IOException in getHttpPut :" + ioException.getMessage());
        }
        return null;
    }

    /**
     * Checks if is valid resource.
     *
     * @param esUrl the es url
     * @return boolean
     */
    public static boolean isValidResource(String esUrl) {
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpHead httpHead = new HttpHead(esUrl);
        HttpResponse response;
        try {
            response = httpclient.execute(httpHead);
            return HttpStatus.SC_OK == response.getStatusLine().getStatusCode();
        } catch (ClientProtocolException clientProtocolException) {
            LOGGER.error("ClientProtocolException in getHttpHead:" + clientProtocolException);
        } catch (IOException ioException) {
            LOGGER.error("IOException in getHttpHead:" + ioException);
        }
        return false;
    }

    /**
     * Builds the query.
     *
     * @param mustFilter the must filter
     * @param mustNotFilter the must not filter
     * @param shouldFilter the should filter
     * @return elastic search query details
     */
    static Map<String, Object> buildQuery(final Map<String, Object> mustFilter, final Map<String, Object> mustNotFilter,
            final HashMultimap<String, Object> shouldFilter) {
        Map<String, Object> queryFilters = Maps.newHashMap();
        Map<String, Object> boolFilters = Maps.newHashMap();
        if (isNotNullOrEmpty(mustFilter)) {
            boolFilters.put(MUST, getFilter(mustFilter));
        }
        if (isNotNullOrEmpty(mustNotFilter)) {

            boolFilters.put(MUST_NOT, getFilter(mustNotFilter));
        }
        if (isNotNullOrEmpty(shouldFilter)) {
            boolFilters.put(SHOULD, getFilter(shouldFilter));
            boolFilters.put(MINIMUM_SHOULD_MATCH, 1);
        }
        queryFilters.put(BOOL, boolFilters);
        return queryFilters;
    }

    /**
     * Checks if is not null or empty.
     *
     * @param shouldFilter the should filter
     * @return true, if is not null or empty
     */
    private static boolean isNotNullOrEmpty(HashMultimap<String, Object> shouldFilter) {

        return shouldFilter != null && shouldFilter.size() > 0;
    }

    /**
     * Checks if is not null or empty.
     *
     * @param collection the collection
     * @return true, if is not null or empty
     */
    private static boolean isNotNullOrEmpty(Map<String, Object> collection) {

        return collection != null && collection.size() > 0;
    }

    /**
     * Gets the filter.
     *
     * @param filter the filter
     * @return the filter
     */
    private static List<Map<String, Object>> getFilter(final HashMultimap<String, Object> filter) {
        List<Map<String, Object>> finalFilter = Lists.newArrayList();
        for (Map.Entry<String, Object> entry : filter.entries()) {
            Map<String, Object> term = Maps.newHashMap();
            Map<String, Object> termDetails = Maps.newHashMap();
            termDetails.put(entry.getKey(), entry.getValue());
            term.put("term", termDetails);
            finalFilter.add(term);
        }
        return finalFilter;
    }

    /**
     * Gets the filter.
     *
     * @param filter the filter
     * @return the filter
     */
    private static List<Map<String, Object>> getFilter(final Map<String, Object> filter) {
        List<Map<String, Object>> finalFilter = Lists.newArrayList();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            Map<String, Object> term = Maps.newHashMap();
            Map<String, Object> termDetails = Maps.newHashMap();
            termDetails.put(entry.getKey(), entry.getValue());
            if ("range".equals(entry.getKey())) {
                term.put("range", entry.getValue());
            } else {
                term.put("term", termDetails);
            }
            finalFilter.add(term);
        }
        return finalFilter;
    }

    /**
     * Builds the query for existing issues.
     *
     * @param filter the filter
     * @return the object
     */
    public static Object buildQueryForExistingIssues(Map<String, Object> filter) {
        Map<String, Object> queryFilters = Maps.newHashMap();
        Map<String, Object> boolFilters = Maps.newHashMap();
        List<Map<String, Object>> should = getFilter(filter);
        boolFilters.put(MUST, should);
        should = Lists.newArrayList();
        Map<String, Object> term = Maps.newHashMap();
        Map<String, Object> termDetails = Maps.newHashMap();
        termDetails.put("issueStatus.keyword", "closed");
        term.put("term", termDetails);
        should.add(term);
        boolFilters.put(MUST_NOT, should);
        should = Lists.newArrayList();
        term = Maps.newHashMap();
        termDetails = Maps.newHashMap();
        termDetails.put("type.keyword", "issue");
        term.put("term", termDetails);
        should.add(term);
        boolFilters.put(SHOULD, should);
        term = Maps.newHashMap();
        termDetails = Maps.newHashMap();
        termDetails.put("type.keyword", "recommendation");
        term.put("term", termDetails);
        should.add(term);
        boolFilters.put(SHOULD, should);
        queryFilters.put(BOOL, boolFilters);
        return queryFilters;
    }

    /**
     * Gets the index name from rule param.
     *
     * @param ruleParam the rule param
     * @return the index name from rule param
     */
    public static String getIndexNameFromRuleParam(Map<String, String> ruleParam) {
        if (ruleParam.containsKey(PacmanSdkConstants.ASSET_GROUP_KEY)) {
            return ruleParam.get(PacmanSdkConstants.ASSET_GROUP_KEY);
        } else {
            return ruleParam.get(PacmanSdkConstants.DATA_SOURCE_KEY) + "_"
                    + ruleParam.get(PacmanSdkConstants.TARGET_TYPE);
        }
    }

    /**
     * Flat nested map.
     *
     * @param notation the notation
     * @param nestedMap the nested map
     * @return nestedMap
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> flatNestedMap(String notation, Map<String, Object> nestedMap) {
        Map<String, String> flatNestedMap = new HashMap<String, String>();
        String prefixKey = notation != null ? notation + "." : "";
        for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
            if (entry.getValue() instanceof String) {
                flatNestedMap.put(prefixKey + entry.getKey(), (String) entry.getValue());
            }
            if (entry.getValue() instanceof Long || entry.getValue() instanceof Integer
                    || entry.getValue() instanceof Boolean || entry.getValue() instanceof Float) {
                flatNestedMap.put(prefixKey + entry.getKey(), String.valueOf(entry.getValue()));
            }
            // Gson converts Double to Exponential notation, hence converting
            // them back to long here
            if (entry.getValue() instanceof Double) {
                flatNestedMap.put(prefixKey + entry.getKey(),
                        String.valueOf(new BigDecimal(String.valueOf(entry.getValue())).longValue()));
            }
            if (entry.getValue() instanceof Map) {
                flatNestedMap.putAll(flatNestedMap(prefixKey + entry.getKey(), (Map<String, Object>) entry.getValue()));
            }
        }
        return flatNestedMap;
    }

    /**
     * Gets the unique annotation id.
     *
     * @param annotation the annotation
     * @return the unique annotation id
     */
    public static String getUniqueAnnotationId(Annotation annotation) {
        return getUniqueAnnotationId(annotation.get(PacmanSdkConstants.DOC_ID),
                annotation.get(PacmanSdkConstants.RULE_ID));
    }

    /**
     * Gets the unique annotation id.
     *
     * @param parentId the parent id
     * @param ruleId the rule id
     * @return the unique annotation id
     */
    public static String getUniqueAnnotationId(String parentId, String ruleId) {
        return getUniqueIdForString(parentId + ruleId);
    }

    // In order to avoid collision 100%, you need a prime number that
    // is bigger than the wider difference between your characters. So for 7-bit
    // ASCII,
    // you need something higher than 128. So instead of 31, use 131 (the next
    // prime number after 128).
    /**
     * This is inspired by java hash function.
     *
     * @param inStr the in str
     * @return the unique id for string
     */
    public static String getUniqueIdForString(String inStr) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // if algorithm does not exist, fall back and try to generate unique
            // hash
            LOGGER.error("unable to generate has usnig Md5", e);
            LOGGER.error("falling back to hash generation");
            return hash(inStr);
        }
        md.update(inStr.getBytes());
        byte byteData[] = md.digest();
        // convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            String hex = Integer.toHexString(0xff & byteData[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Hash.
     *
     * @param s the s
     * @return the string
     */
    public static String hash(String s) {
        long h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = 131 * h + s.charAt(i);
        }
        return Long.toString(h);
    }

    /**
     * Creates the param map.
     *
     * @param ruleParams the rule params
     * @return the map
     */
    public static Map<String, String> createParamMap(String ruleParams) {
       /* // return Splitter.on("#").withKeyValueSeparator("=").split(ruleParams);
        if (ruleParams.contains("*")) // this is for backward compatibility
            return buildMapFromString(ruleParams, "*", "=");
        else {*/
            return buildMapFromJson(ruleParams);
       // }
    }

    /**
     * Builds the map from json.
     *
     * @param json the json
     * @return the map
     */
    private static Map<String, String> buildMapFromJson(String json) {
        JsonParser parser = new JsonParser();
        String ruleUUID = "";
        JsonElement element = parser.parse(json);
        JsonObject obj = element.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
        if (obj.has(PacmanSdkConstants.RULE_UUID_KEY)) {
            ruleUUID = obj.get(PacmanSdkConstants.RULE_UUID_KEY).getAsString();
        }
        Map<String, String> toReturn = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : entries) {
            if (entry.getValue().isJsonArray()) {
                toReturn.putAll(getMapFromArray(entry.getValue().getAsJsonArray(), ruleUUID));
            } else {
                toReturn.put(entry.getKey(), entry.getValue().getAsString());
            }
        }

        return toReturn;

    }

    /**
     * Decrypt.
     *
     * @param encryptedText the encrypted text
     * @return the string
     */
    public static String decrypt(String encryptedText) {
        // have to implement this based on input encryption
        return encryptedText;
    }

    /**
     * Gets the map from array.
     *
     * @param jsonArray the json array
     * @param ruleUUID the rule UUID
     * @return the map from array
     */
    private static Map<String, String> getMapFromArray(JsonArray jsonArray, String ruleUUID) {
        Map<String, String> toReturn = new HashMap<>();
        jsonArray.forEach(e -> {
            if (e.getAsJsonObject().get("encrypt").getAsBoolean())
                try {
                    toReturn.put(e.getAsJsonObject().get("key").getAsString(),
                            decrypt(e.getAsJsonObject().get("value").getAsString(), ruleUUID));
                } catch (Exception e1) {
                    LOGGER.error("unable to decrypt", e);
                }
            else
                toReturn.put(e.getAsJsonObject().get("key").getAsString(),
                        e.getAsJsonObject().get("value").getAsString());
        });
        return toReturn;
    }

    /**
     * Builds the map from string.
     *
     * @param input the input
     * @param splitOn the split on
     * @param keyValueSeparator the key value separator
     * @return the map
     */
    public static Map<String, String> buildMapFromString(String input, String splitOn, String keyValueSeparator) {
        return Splitter.on(splitOn).omitEmptyStrings().trimResults().withKeyValueSeparator(keyValueSeparator)
                .split(input);
    }

    /**
     * Gets the elapse time since.
     *
     * @param startTime the start time
     * @return the elapse time since
     */
    public static Long getElapseTimeSince(long startTime) {
        return TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
    }

    /**
     * Gets the current date string with format.
     *
     * @param timeZone the time zone
     * @param format the format
     * @return the current date string with format
     */
    public static String getCurrentDateStringWithFormat(String timeZone, String format) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
        if (!Strings.isNullOrEmpty(timeZone))
            dateFormatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        else
            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormatter.format(new Date());
    }

    /**
     * Gets the date from string.
     *
     * @param dateInString the date in string
     * @param format the format
     * @return the date from string
     * @throws ParseException the parse exception
     */
    public static Date getDateFromString(final String dateInString, final String format)
            throws java.text.ParseException {
        String dateDormatter = "MM/dd/yyyy";
        if (!StringUtils.isNullOrEmpty(format)) {
            dateDormatter = format;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(dateDormatter);
        return formatter.parse(dateInString);
    }

    /**
     * Date format.
     *
     * @param dateInString the date in string
     * @param formatFrom the format from
     * @param formatTo the format to
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date dateFormat(final String dateInString, String formatFrom, String formatTo)
            throws java.text.ParseException {
        String dateDormatter = "MM/dd/yyyy";
        if (StringUtils.isNullOrEmpty(formatFrom)) {
            formatFrom = dateDormatter;
        }
        if (StringUtils.isNullOrEmpty(formatTo)) {
            formatTo = dateDormatter;
        }
        DateFormat dateFromFormater = new SimpleDateFormat(formatFrom);
        DateFormat dateToFormater = new SimpleDateFormat(formatTo);
        return dateToFormater.parse(dateToFormater.format(dateFromFormater.parse(dateInString)));
    }

    /**
     * Compare date.
     *
     * @param firstDate the first date
     * @param lastDate the last date
     * @return the int
     */
    public static int compareDate(final Date firstDate, final Date lastDate) {
        return firstDate.compareTo(lastDate);
    }

    /**
     * Resource created before cutoff data.
     *
     * @param resourceCreationDate the resource creation date
     * @return true, if successful
     */
    public static boolean resourceCreatedBeforeCutoffData(final Date resourceCreationDate) {
        try {
            if (null != resourceCreationDate) {
                String cutoffDateString = CommonUtils.getPropValue(PacmanSdkConstants.AUTOFIX_CUTOFF_DATE);

                Date cutoffDate = getDateFromString(cutoffDateString, PacmanSdkConstants.MM_DD_YYYY);
                if (resourceCreationDate.before(cutoffDate) || resourceCreationDate.equals(cutoffDate)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Exception in isResourceDateExpired: " + exception.getMessage());
        }
        return Boolean.FALSE;
    }

    /**
     * Do http post.
     *
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return the string
     */
    public static String doHttpPost(final String url, final String requestBody, final Map<String, String> headers) {
        CloseableHttpClient httpclient = null;
        if(Strings.isNullOrEmpty(url)){
            return "";
        }
        try {
            if (url.contains(HTTPS)) {

                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(createNoSSLContext());
                httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            } else {
                httpclient = HttpClients.custom().build();
            }

            HttpPost httppost = new HttpPost(url);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httppost.addHeader(entry.getKey(), entry.getValue());
            }
            httppost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            StringEntity jsonEntity = new StringEntity(requestBody);
            httppost.setEntity(jsonEntity);
            HttpResponse httpresponse = httpclient.execute(httppost);
           if(httpresponse.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
               throw new IOException("non 200 code from rest call--->" + url);
           }
            String responseStr = EntityUtils.toString(httpresponse.getEntity());
            return responseStr;
        } catch (org.apache.http.ParseException parseException) {
            LOGGER.error("ParseException : " + parseException.getMessage());
        } catch (IOException ioException) {
        	try{
        		if(AuthManager.getToken()!=null){
                    String accessToken =  AuthManager.getToken();
                 if(!Strings.isNullOrEmpty(accessToken))
                 {
                     headers.put(PacmanSdkConstants.AUTH_HEADER, "Bearer " + accessToken);
                 }
             }
        	 if (url.contains(HTTPS)) {

                 SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(createNoSSLContext());
                 httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
             } else {
                 httpclient = HttpClients.custom().build();
             }

             HttpPost httppost = new HttpPost(url);
             for (Map.Entry<String, String> entry : headers.entrySet()) {
                 httppost.addHeader(entry.getKey(), entry.getValue());
             }
             httppost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
             StringEntity jsonEntity = new StringEntity(requestBody);
             httppost.setEntity(jsonEntity);
             HttpResponse httpresponse = httpclient.execute(httppost);
            if(httpresponse.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
                throw new IOException("non 200 code from rest call--->" + url);
            }
             String responseStr = EntityUtils.toString(httpresponse.getEntity());
             return responseStr;
        }catch(Exception e){
        	LOGGER.error("Exception in isResourceDateExpired: " + e.getMessage());
        }
        }
        return null;
    }

    /**
     * Do http get.
     *
     * @param url the url
     * @return the string
     */
    public static String doHttpGet(final String url) {
        CloseableHttpClient httpclient = null;
        try {

            if (url.contains(HTTPS)) {

                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(createNoSSLContext());
                httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            } else {
                httpclient = HttpClients.custom().build();
            }
            HttpGet httpGet = new HttpGet(url);
            if(AuthManager.getToken()!=null){
                String accessToken =  AuthManager.getToken();
                if(!Strings.isNullOrEmpty(accessToken))
                {
                    httpGet.setHeader(PacmanSdkConstants.AUTH_HEADER, "Bearer " + accessToken);
                }
            }
            
            httpGet.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception exception) {
            LOGGER.error("Exception in getHttpGet: " + exception.getMessage());
        } finally {
            if (null != httpclient) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    LOGGER.error("error closing http client", e);
                    httpclient = null;
                }
            }
        }

        return null;
    }

    /**
     * Creates the no SSL context.
     *
     * @return the SSL context
     */
    public static SSLContext createNoSSLContext() {
        SSLContext ssl_ctx = null;
        try {
            ssl_ctx = SSLContext.getInstance(TLS);
        } catch (NoSuchAlgorithmException e) {
        }
        TrustManager[] trust_mgr = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String t) {
                /**
                 * no implementation required
                 * **/
            }

            public void checkServerTrusted(X509Certificate[] certs, String t) {
                /**
                 * no implementation required
                 * **/
            }
        } };
        try {
            if(null!=ssl_ctx){
                ssl_ctx.init(null, trust_mgr, new SecureRandom());
            }
        } catch (KeyManagementException e) {
        }
        return ssl_ctx;
    }

    /**
     * Gets the json string.
     *
     * @param annotation the annotation
     * @return the json string
     */
    public static String getJsonString(final Object annotation) {
        try {
            return new ObjectMapper().writeValueAsString(annotation);
        } catch (JsonProcessingException jsonProcessingException) {
            LOGGER.error("JsonProcessingException : " + jsonProcessingException.getMessage());
        }
        return null;
    }

    /** The random. */
    private static Random random = new Random((new Date()).getTime());

    /**
     * Encrypt B 64.
     *
     * @param plainText the plain text
     * @return the string
     */
    public static String encryptB64(String plainText) {
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + Base64.getEncoder().encodeToString(plainText.getBytes());
    }

    /**
     * Decrypt B 64.
     *
     * @param text the text
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static String decryptB64(String text) throws IOException {

        // remove random salt, length will be always 12
        // Each base64 digit represents exactly 6 bits of data. Three 8-bit
        // bytes (i.e., a total of 24 bits) can therefore be represented by four
        // 6-bit base64 digits.
        String cipher = text.substring(12);
        return new String(Base64.getDecoder().decode(cipher));
    }

    /**
     * Encrypt.
     *
     * @param plainText the plain text
     * @param key the key
     * @return the string
     * @throws Exception the exception
     */
    public static String encrypt(String plainText, final String key) throws Exception {
        SecretKey secretKey = getSecretKey(key);
        byte[] plainTextByte = plainText.getBytes();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        // return new BASE64Encoder().encode(encryptedByte);
        return new String(encryptedByte, StandardCharsets.UTF_8);
    }

    /**
     * Decrypt.
     *
     * @param encryptedText the encrypted text
     * @param key the key
     * @return the string
     * @throws Exception the exception
     */
    public static String decrypt(String encryptedText, final String key) throws Exception {
        SecretKey secretKey = getSecretKey(key);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedText.getBytes(StandardCharsets.UTF_8));
        return new String(decryptedByte);
    }

    /**
     * Gets the secret key.
     *
     * @param baseKey the base key
     * @return the secret key
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    private static SecretKeySpec getSecretKey(final String baseKey) throws UnsupportedEncodingException {
        String secretKeyValue = Base64.getEncoder().encodeToString(baseKey.substring(0, 16).getBytes()).substring(0, 16);
        return new SecretKeySpec(secretKeyValue.getBytes(StandardCharsets.UTF_8), "AES");
    }

    /**
     * Gets the iv parameter spec.
     *
     * @return the iv parameter spec
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    private static IvParameterSpec getIvParameterSpec() throws UnsupportedEncodingException {
        return new IvParameterSpec("RandomInitVector".getBytes("UTF-8"));
    }

    /**
     * Gets the prop value.
     *
     * @param keyname the keyname
     * @return the prop value
     */
    public static String getPropValue(final String keyname) {

        return prop.getProperty(keyname);
    }

    /**
     * Gets the template content.
     *
     * @param templateName the template name
     * @return the template content
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static String getTemplateContent(final String templateName) throws IOException {
        InputStream inputStream = CommonUtils.class.getClassLoader()
                .getResourceAsStream("template/" + templateName + ".html");
        return readContent(inputStream);
    }

    /**
     * Read content.
     *
     * @param input the input
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static String readContent(final InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    /**
     * Serialize to string.
     *
     * @param object the object
     * @return the string
     */
    public static String serializeToString(Object object) {
        Gson serializer = new GsonBuilder().create();
        return serializer.toJson(object);
    }

    /**
     * De serialize to object.
     *
     * @param jsonString the json string
     * @return the object
     */
    public static Object deSerializeToObject(String jsonString) {
        Gson serializer = new GsonBuilder().create();
        return serializer.fromJson(jsonString, Object.class);
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
                LOGGER.error("Blank value found for param" + str);
                return Boolean.FALSE; 
            }
        }
        return Boolean.TRUE;
    }


}
