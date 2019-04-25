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

package com.tmobile.pacman.executor;

import java.util.HashMap;
import java.util.Map;

import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ESUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ShutDownHook.
 */
public class ShutDownHook implements Runnable {

    /** The rule engine stats. */
    Map<String, Object> ruleEngineStats;

    /**
     * Instantiates a new shut down hook.
     *
     * @param ruleEngineStats the rule engine stats
     */
    public ShutDownHook(Map<String, Object> ruleEngineStats) {
        super();
        this.ruleEngineStats = ruleEngineStats;
    }

    /**
     * Instantiates a new shut down hook.
     */
    public ShutDownHook() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if(null==ruleEngineStats)ruleEngineStats = new HashMap<>();
        ruleEngineStats.put("endTime", CommonUtils.getCurrentDateStringWithFormat(PacmanSdkConstants.PAC_TIME_ZONE,
                PacmanSdkConstants.DATE_FORMAT));
        ruleEngineStats.put(PacmanSdkConstants.STATUS_REASON, "SIGTERM"); 
        ruleEngineStats.put(PacmanSdkConstants.STATUS_KEY, PacmanSdkConstants.STATUS_FINISHED);
        ESUtils.publishMetrics(ruleEngineStats,CommonUtils.getPropValue(PacmanSdkConstants.STATS_TYPE_NAME_KEY));
    }

}
