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
  Modified Date: Oct 18, 2017

**/
package com.tmobile.pacman.api.commons.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Strings;

/**
 *
 * @author kkumar
 *
 */
public class PacHttpUtils {

	/**
	 *
	 */
	private static final String CONTENT_TYPE = "Content-Type";
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
		byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;

		if (Strings.isNullOrEmpty(serviceEndpoint)) {
			throw new Exception("service endpoint cannot be blank");
		}
		URL url = null;
		try {
			url = new URL(serviceEndpoint);
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}

		HttpsURLConnection.setDefaultSSLSocketFactory(CommonUtils.createNoSSLContext().getSocketFactory());
		HttpsURLConnection con = null;
		try {
			con = (HttpsURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("POST");
			con.setRequestProperty(CONTENT_TYPE, "application/json");
			con.setRequestProperty("cache-control", "no-cache");
			con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(postData);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);
			throw e;
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
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				throw e;
			}
		}
		return response.toString();
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
}
