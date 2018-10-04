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
  Modified Date: Aug 18, 2017

 **/
package com.tmobile.cloud.awsrules.compliance;

import com.google.gson.JsonObject;

public class DefaultTargetCriteriaDataProvider {

    private JsonObject kernelVersionFromPacmanTable;
    private static DefaultTargetCriteriaDataProvider criteriaDataProvider;

    /**
	 *
	 */
    private DefaultTargetCriteriaDataProvider(String defaultKernelCriteriaUrl) {
        kernelVersionFromPacmanTable = PacmanTableAPI
                .getKernelVersionFromPacmanTable(defaultKernelCriteriaUrl);
    }

    /**
     * Singleton thread safe method for caching the kernel complaince target
     * criteria data
     *
     * @return
     */
    public static synchronized DefaultTargetCriteriaDataProvider getInstance(
            String defaultKernelCriteriaUrl) {
        if (criteriaDataProvider == null) {
            synchronized (DefaultTargetCriteriaDataProvider.class) {
                criteriaDataProvider = new DefaultTargetCriteriaDataProvider(
                        defaultKernelCriteriaUrl);
            }
        }
        return criteriaDataProvider;
    }

    /**
     *
     * @return
     */
    public JsonObject getTargetCriterianData() {
        return kernelVersionFromPacmanTable;
    }

}
