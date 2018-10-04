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

package com.tmobile.pacman.executor.jobs;

import java.util.Map;

import com.tmobile.pacman.commons.jobs.PacmanJob;

// TODO: Auto-generated Javadoc
/**
 * The Class TestJob.
 *
 * @author kkumar
 */

@PacmanJob(methodToexecute = "shipData", jobName = "Test Job", desc = "Job for unit testing", priority = 5)
public class TestJob {

    /**
     * Ship data.
     *
     * @param map the map
     */
    public void shipData(Map<String,String> map){
        return;
    }

}
