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
package com.tmobile.pacman.cloud;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.pacman.cloud.util.ConfigUtil;
import com.tmobile.pacman.cloud.util.Constants;
import com.tmobile.pacman.cloud.util.Util;
import com.tmobile.pacman.commons.jobs.PacmanJob;


/**
 * The Class Cloud Notification enricher.
 */
@PacmanJob(methodToexecute = "execute", jobName = "Cloud-Notification-DataCollection", desc = "Job to collect the cloud data from phd index ", priority = 5)
public class CloudNotificationJob implements Constants {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudNotificationJob.class);
    /**
     * The main method.
     * @param args 
     * the arguments
     */
    public static void main(String[] args){
    	 Map<String, String> params = new HashMap<>();
         Arrays.asList(args).stream().forEach(obj -> {
                 String[] paramArray = obj.split("[:]");
                 params.put(paramArray[0], paramArray[1]);
                 System.setProperty(paramArray[0], paramArray[1]);
         });
         execute(params);
         System.exit(0);
    }

    /**
     * execute.
     * @param params the params
     * @return 
     * @return the map
     */
    public static void execute(Map<String, String> params){
        try {
        	ConfigUtil.setConfigProperties(params);
		} catch (Exception e) {
			LOGGER.error("Unexpected ERROR in execute method"+Util.getStackTrace(e));
		}
       CloudNotificationApplication.main( new String[]{});
    }
}
