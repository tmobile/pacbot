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
package com.tmobile.cso.pacbot.recommendation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.cso.pacbot.recommendation.entity.RecommendationCollector;
import com.tmobile.cso.pacbot.recommendation.util.Constants;
import com.tmobile.cso.pacbot.recommendation.util.ErrorManageUtil;
import com.tmobile.pacman.commons.jobs.PacmanJob;


/**
 * The Class Main.
 */
@PacmanJob(methodToexecute = "shipData", jobName = "Recommendation-Enricher", desc = "", priority = 5)
public class Main implements Constants {

    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        Map<String, String> params = new HashMap<>();
        Arrays.asList(args).stream().forEach(obj -> {
                String[] paramArray = obj.split("[:]");
                params.put(paramArray[0], paramArray[1]);
        });
        shipData(params);
        System.exit(0);
    }

    /**
     * Ship data.
     *
     * @param params            the params
     * @return the map
     */
    public static Map<String, Object> shipData(Map<String, String> params) {
    	String jobName  = params.get("jobName");
    	if(jobName.isEmpty()){
    		jobName = "aws-recommendations-collector";
    	}
        List<Map<String,String>> errorList = new ArrayList<>();
        try {
			MainUtil.setup(params);
		} catch (Exception e) {
			  Map<String,String> errorMap = new HashMap<>();
              errorMap.put(ERROR, "Exception in setting up Job ");
              errorMap.put(ERROR_TYPE, WARN);
              errorMap.put(EXCEPTION, e.getMessage());
              errorList.add(errorMap);
              return ErrorManageUtil.formErrorCode(jobName, errorList);
		}
        errorList.addAll(new RecommendationCollector().uploadRecommendationData());
        Map<String, Object> status = ErrorManageUtil.formErrorCode(jobName, errorList);
        LOGGER.info("Job Return Status {} ",status);
        return status;
    }

}
