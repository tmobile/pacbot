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
package com.tmobile.pacman.api.compliance.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;

import com.google.common.base.Strings;

/**
 * 
 *
 */
public class PacHttpUtils {

	/**
	 * 
	 */
	private static final String CONTENT_TYPE = "Content-Type";
	
	 /** The Constant APPLICATION_JSON. */
    private static final String APPLICATION_JSON = "application/json";
    
	static final Log LOGGER = LogFactory.getLog(PacHttpUtils.class);

	/**
	 * 
	 * @param rest
	 *            URL for POST method
	 * @return String
	 * @throws Exception
	 */
	public static String doHttpPost(final String url, final String requestBody) throws Exception {
		try {

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost httppost = new HttpPost(url);
			httppost.setHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
			StringEntity jsonEntity = new StringEntity(requestBody);
			httppost.setEntity(jsonEntity);
			HttpResponse httpresponse = client.execute(httppost);
			int statusCode = httpresponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
				return EntityUtils.toString(httpresponse.getEntity());
			} else {
				LOGGER.error(requestBody);
				throw new Exception(
						"unable to execute post request because " + httpresponse.getStatusLine().getReasonPhrase());
			}
		} catch (ParseException parseException) {
			LOGGER.error("error closing issue" + parseException);
			throw parseException;
		} catch (Exception exception) {
			LOGGER.error("error closing issue" + exception.getMessage());
			throw exception;
		}
	}

	/**
     * 
     * @param serviceEndpoint
     * @param urlParameters
     * @return
     * @throws Exception
     */
    public static String doHttpsPost(final String serviceEndpoint, final String urlParameters) throws Exception {
        return getResponse(serviceEndpoint, urlParameters, null);
    }

	/**
	 * 
	 * @param rest
	 *            URL for HTTP GET method
	 * @param rest
	 *            headers for HTTPS GET method
	 * @return String
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String getHttpGet(final String url, final Map<String, String> headers)
			throws ParseException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(url);
		if (headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				httpGet.setHeader(entry.getKey(), entry.getValue());
			}
		}
		HttpResponse httpresponse = client.execute(httpGet);
		return EntityUtils.toString(httpresponse.getEntity());
	}

	/**
	 * 
	 * @param rest
	 *            URL for HTTPS POST method
	 * @param rest
	 *            URL Parameters for HTTPS POST method
	 * @return String
	 * @throws Exception
	 */
	public static String doHttpSecureFormPost(final String url, final String urlParameters) throws Exception {
		byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;
		if (Strings.isNullOrEmpty(url)) {
			throw new Exception("service endpoint cannot be blank");
		}
		URL serviceUrl = null;
		try {
			serviceUrl = new URL(url);
		} catch (MalformedURLException malformedURLException) {
			LOGGER.error(malformedURLException.getMessage());
			throw malformedURLException;
		}

		HttpsURLConnection.setDefaultSSLSocketFactory(CommonUtils.createNoSSLContext().getSocketFactory());
		HttpsURLConnection con = null;
		try {
			con = (HttpsURLConnection) serviceUrl.openConnection();
			con.setDoOutput(true);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("POST");
			con.setRequestProperty(CONTENT_TYPE, "application/x-www-form-urlencoded");
			con.setRequestProperty("cache-control", "no-cache");
			con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(postData);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage());
			throw ioException;
		}
		StringBuilder response = new StringBuilder();
		if (con != null) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String input;
				while ((input = br.readLine()) != null) {
					response.append(input);
				}
				br.close();
				con.disconnect();
			} catch (IOException ioException) {
				LOGGER.error(ioException.getMessage());
				throw ioException;
			}
		}
		return response.toString();
	}
	
	 /**
     * 
     * @param serviceEndpoint
     * @param urlParameters
     * @param headers
     * @return
     * @throws Exception
     */
    public static String doHttpsPost(final String serviceEndpoint, final String urlParameters,Map<String, String> headers) throws Exception {
        return getResponse(serviceEndpoint, urlParameters, headers);
    }
	
	public static String getBase64AuthorizationHeader(final HttpServletRequest request){
        final String authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String base64AuthorizationHeader = Optional.ofNullable(authorizationHeaderValue).map(headerValue->{
            if(headerValue.startsWith("B")){
                return headerValue.substring("Bearer ".length());
            }else{
                return headerValue.substring("bearer ".length()); 
            }
            }).orElse(StringUtils.EMPTY);
        return base64AuthorizationHeader;
    }
	
	
    /**
     * Do http post.
     *
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return the string
     */
    public static String getResponse(final String url, final String requestBody, final Map<String, String> headers) {
        CloseableHttpClient httpclient = null;
        if(Strings.isNullOrEmpty(url)){
            return "";
        }
        
        try {
            if (url.contains("https")) {

                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(CommonUtils.createNoSSLContext());
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
            LOGGER.debug(url + " service with input" + requestBody +" returned " + responseStr);
            return responseStr;
        } catch (org.apache.http.ParseException parseException) {
            LOGGER.error("ParseException : " + parseException.getMessage());
        } catch (IOException ioException) {
            LOGGER.error("IOException : " + ioException.getMessage());
        }
        return null;
    }
	
}