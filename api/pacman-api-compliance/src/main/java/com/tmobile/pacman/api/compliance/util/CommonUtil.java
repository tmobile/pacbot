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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;

/**
 * The Class CommonUtil.
 */
public class CommonUtil implements Constants {
    private CommonUtil() {

    }
    
    /** The logger. */
    private final static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    private static final String KERNEL_CRITERIA_KEY = "pacman.kernel.compliance.map";

	/**
     * Decode aws cron exp.
     *
     * @param cron the cron
     * @return the string
     */
    public static String decodeAwsCronExp(String cron) {
        String[] fields = cron.split(" ");
        String hour = fields[1];
        String dayofmonth = fields[TWO];

        if (hour.contains("/")) {
            String[] hours = hour.split("/");
            if (!"23".equals(hours[1].trim())) {
                return "Every " + hours[1] + " hours";
            } else {
                return "Daily";
            }
        }
        if (dayofmonth.contains("/")) {
            String[] split = dayofmonth.split("/");
            if (!"1".equals(split[1])) {
                return "Every " + split[1] + " days";
            } else {
                return "Daily";
            }
        }
        return cron;
    }

    /**
     * Gets the rule severity from parms.
     *
     * @param ruleParamjson the rule paramjson
     * @return the rule severity from parms
     */
    public static String getRuleSeverityFromParms(String ruleParamjson) {
        JsonParser parser = new JsonParser();

        JsonObject ruleParamsJson = (JsonObject) parser.parse(ruleParamjson);
        JsonArray params = ruleParamsJson.get("params").getAsJsonArray();

        for (JsonElement param : params) {
            JsonObject pramObj = (JsonObject) param;
            String key = pramObj.get("key").getAsString();
            if ("severity".equals(key)) {
                return pramObj.get("value").getAsString();
            }
        }
        return "";
    }

    /**
     * Encode url.
     *
     * @param toBeEncoded the to be encoded
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public static String encodeUrl(String toBeEncoded)
            throws UnsupportedEncodingException {
        String encoded = toBeEncoded;
        try {
            encoded = URLEncoder.encode(toBeEncoded, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            logger.error("error",e1);
            throw new UnsupportedEncodingException(e1.getMessage());
        }
        return encoded;
    }

    /**
     * Decode url.
     *
     * @param toBeDecoded the to be decoded
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public static String decodeUrl(String toBeDecoded)
            throws UnsupportedEncodingException {
        String decoded = toBeDecoded;
        try {
            decoded = URLDecoder.decode(toBeDecoded, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            logger.error("error",e1);
            throw new UnsupportedEncodingException(e1.getMessage());
        }
        return decoded;
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
    
    public static String getCurrentQuarterCriteriaKey() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int currentQuarter = (month / 3) + 1;
		return KERNEL_CRITERIA_KEY.concat(".")
				.concat(String.valueOf(year)).concat(".q")
				.concat(String.valueOf(currentQuarter));
	}
}
