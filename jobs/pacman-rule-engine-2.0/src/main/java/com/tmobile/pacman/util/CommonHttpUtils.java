package com.tmobile.pacman.util;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CommonHttpUtils {

	/** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonHttpUtils.class);

    
    /**
     * Gets the header.
     *
     * @param base64Creds the base 64 creds
     * @return the header
     */
    public static Map<String,Object> getHeader(String base64Creds){
        Map<String,Object> authToken = new HashMap<>();
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
	public static JsonObject getConfigurationsFromConfigApi(String url,Map<String,Object> headers) {
		String resultStringPost = null;
		Gson gson = new Gson();
		try {
			resultStringPost = httpGetMethodWithHeaders(url,headers);
			if (!StringUtils.isEmpty(resultStringPost)) {
				return gson.fromJson(resultStringPost, JsonObject.class);
			}

		} catch (Exception e) {
			LOGGER.error("Exceptions occured in getConfigurationsFromConfigApi========",e);
			return null;
		}
		return null;
	}
	
	/**
     * Http get method with headers.
     *
     * @param url the url
     * @param headers the headers
     * @return the string
     * @throws Exception the exception
     */
    private static String httpGetMethodWithHeaders(String url,Map<String, Object> headers) throws Exception {
        String json = null;
        // Some custom method to craete HTTP post object
        HttpGet get = new HttpGet(url);
        CloseableHttpClient httpClient = null;
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
            	get.setHeader(entry.getKey(), entry.getValue().toString());
            }
        }
        try {
            // Get http client
            httpClient = getCloseableHttpClient();

            // Execute HTTP method
            CloseableHttpResponse res = httpClient.execute(get);

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
    
    /**
     * Gets the closeable http client.
     *
     * @return the closeable http client
     */
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
        	LOGGER.error("KeyManagementException in creating http client instance", e);
        } catch (NoSuchAlgorithmException e) {
        	LOGGER.error("NoSuchAlgorithmException in creating http client instance", e);
        } catch (KeyStoreException e) {
        	LOGGER.error("KeyStoreException in creating http client instance", e);
        }
        return httpClient;
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
    
    

}
