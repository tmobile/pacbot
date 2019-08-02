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
package com.tmobile.pacman.cloud.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class Util.
 */
public class Util {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);
    
    /**
     * Instantiates a new util.
     */
    private Util(){
        
    }
    /**
     * Gets the stack trace.
     *
     * @param e
     *            the e
     * @return the stack trace
     */
    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();

    }

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
     * Gets the intersect list.
     *
     * @param List1 the List1
     * @param List2 the list2
     * @return the intersect list
     */
	public static List<String> compareLst(List<String> list1, List<String> list2) {
		
		return list1.stream()
				.filter(list2::contains)
				.collect(Collectors.toList());
		
	}
	/**
     * Gets the String.
     *
     * @param List the List
     * @return the String with the list of elements appended with Quote. 
     */
	public static String appendQuotesForList (List<String> list) {
		return  list.stream()
				  .map(s -> "\"" + s + "\"")
				  .collect(Collectors.joining(", "));
	}
	/**
     * Alter the Key namae in the Map.
     * @param inputMap the InputMap.
     * @param oldkey the oldkey
     * @param newkey the newkey
     * @param keyVal the key value.
     * @return the Map with the altered keys. 
     */
	public static Map<String, Object> alterKey(Map<String, Object> inputMap, String oldKey, String newKey, String keyVal){
		try {
		inputMap.put(newKey, keyVal);
		inputMap.remove(oldKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputMap;
		
	}
	
	/**
     * Gets the String.
     *
     * @param OrgStr the original String.
     * @param repStr the replace String.
     * @return the replaced String. 
     */
	public static String strReplace(String orgStr, String repStr) {
		return orgStr.replaceAll(repStr, "");
	}
	
	/**
     * Gets the String.
     * @param orgStr the originalString
     * @param index the index
     * @return the Sub String by index. 
     */
	public static String strSub(String orgStr, int index) {
		return orgStr.substring(index);
	}
	
	public static List<String> split(String str, String repStr){
	    return Stream.of(str.split(repStr))
	      .map (elem -> new String(elem))
	      .collect(Collectors.toList());
	}
	
	 /**
     * Concatenate.
     *
     * @param map
     *            the map
     * @param keys
     *            the keys
     * @param delimiter
     *            the delimiter
     * @return the string
     */
    public static String concatenate(Map<String, String> map, String[] keys, String delimiter) {
        List<String> values = new ArrayList<>();
        for (String key : keys) {
            values.add(map.get(key));
        }
        return values.stream().collect(Collectors.joining(delimiter));
    }
    
    /**
     * Gets the unique ID.
     *
     * @param idstring
     *            the idstring
     * @return the unique ID
     */
    public static String getUniqueID(String idstring) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return (new HexBinaryAdapter()).marshal(md5.digest(idstring.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error in getUniqueID",e);
        }
        return "";
    }
}
