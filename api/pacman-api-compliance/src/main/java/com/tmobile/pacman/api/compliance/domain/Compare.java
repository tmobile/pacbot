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
  Author :santoshi
  Modified Date: Dec 8, 2017

 **/
package com.tmobile.pacman.api.compliance.domain;

import java.util.Comparator;
import java.util.Map;

import com.tmobile.pacman.api.commons.Constants;
/**
 * The Class Compare.
 */
public class Compare implements Comparator<Map<String, Object>>,
        Constants {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Map<String, Object> map1,
            Map<String, Object> map2) {

        Long.parseLong(map1.get(FAILED).toString());
        return Long.compare(Long.parseLong(map1.get(FAILED).toString()),
                Long.parseLong(map2.get(FAILED).toString()));
    }

}
