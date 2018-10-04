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
package com.tmobile.pacman.api.admin.util;

import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin Utility Class
 */
public class AdminUtils {

	private AdminUtils() {
	}

	private static final Logger log = LoggerFactory.getLogger(AdminUtils.class);

	/**
     * Utility function to convert MultipartFile to File
     *
     * @author Nidhish
     * @param file - valid MultipartFile file
     * @return File response
     * @throws IOException
     */
	public static File convert(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		boolean created = convFile.createNewFile();
		if (created) {
			try (OutputStream stream = new FileOutputStream(convFile)) {
				stream.write(file.getBytes());
			} catch (Exception exception) {
				log.error(UNEXPECTED_ERROR_OCCURRED, exception);
			}
		}
		return convFile;
	}

	/**
     * Utility function to get reference Id
     *
     * @author Nidhish
     * @return Four character code
     */
	public static String getReferenceId() {
		Random random = new Random();
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < 4; index++) {
			builder.append(characters.charAt(random.nextInt(characters.length())));
		}
		return builder.toString();
	}

	/**
     * Utility function to encrypt a plainText with a baseKey
     *
     * @author Nidhish
     * @param plainText - valid plain text
     * @param baseKey - valid base key
     * @return Encrypted string value
     * @throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
     */
	public static String encrypt(String plainText, final String baseKey) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		SecretKey secretKey = getSecretKey(baseKey);
		byte[] plainTextByte = plainText.getBytes();
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, getIvParameterSpec());
		byte[] encryptedByte = cipher.doFinal(plainTextByte);
		return new String(Base64.encodeBase64URLSafe(encryptedByte));
	}

	/**
     * Utility function to deCrypt a plainText with a baseKey
     *
     * @author Nidhish
     * @param encryptedText - valid encrypted text
     * @param baseKey - valid base key
     * @return deCrypted string value
     * @throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
     */
	public static String decrypt(String encryptedText, final String baseKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException  {
		SecretKey secretKey = getSecretKey(baseKey);
		byte[] encryptedTextByte = Base64.decodeBase64(encryptedText);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, getIvParameterSpec());
		byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
		return new String(decryptedByte);
	}

	/**
     * Utility function to perform doHttpPut
     *
     * @author Nidhish
     * @param url - valid URL string
     * @param requestBody - valid request body
     * @return response string
	 * @throws IOException
     */
	public static String doHttpPut(final String url, final String requestBody) throws IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPut httpput = new HttpPut(url);
		httpput.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
		StringEntity jsonEntity = new StringEntity(requestBody);
		httpput.setEntity(jsonEntity);
		HttpResponse httpresponse = client.execute(httpput);
		int statusCode = httpresponse.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
			return EntityUtils.toString(httpresponse.getEntity());
		} else {
			throw new IOException("unable to execute put request because " + httpresponse.getStatusLine().getReasonPhrase());
		}
	}

	/**
     * Utility function to get formated string date
     *
     * @author Nidhish
     * @param format - valid date string format
     * @param date - valid date object
     * @return formatted date string
     */
	public static String getFormatedStringDate(final String format, final Date date) {
		return new SimpleDateFormat(format).format(date);
	}

	/**
     * Utility function to get formated date object
     *
     * @author Nidhish
     * @param format - valid date string format
     * @param date - valid date string
     * @return formatted date object
     */
	public static Date getFormatedDate(final String format, final String date) throws ParseException {
		return new SimpleDateFormat(format).parse(date);
	}

	private static SecretKeySpec getSecretKey(final String baseKey) throws UnsupportedEncodingException {
		String secretKeyValue = Base64Utils.encodeToString(baseKey.substring(0, 16).getBytes()).substring(0, 16);
		return new SecretKeySpec(secretKeyValue.getBytes("UTF-8"), "AES");
	}

	private static IvParameterSpec getIvParameterSpec() throws UnsupportedEncodingException {
		return new IvParameterSpec("RandomInitVector".getBytes("UTF-8"));
	}
}
