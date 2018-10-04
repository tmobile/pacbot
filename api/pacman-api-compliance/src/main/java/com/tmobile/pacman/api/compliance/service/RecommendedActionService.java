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
  Modified Date: Dec 19, 2017

 **/
/*
 *Copyright 2016-2017 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 *Licensed under the Amazon Software License (the "License"). You may not use
 * this file except in compliance with the License. A copy of the License is located at
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tmobile.pacman.api.compliance.service;

import java.util.List;
import java.util.Map;

/**
 * The Interface RecommendedActionService.
 *
 * @author kkumar
 */
public interface RecommendedActionService {

    /**
     * list of recommended actions for targetType.
     *
     * @param dataSource the data source
     * @param targetType the target type
     * @param ruleId the rule id
     * @return the recommended actions
     * @throws Exception the exception
     */
    public List<Map<String, Object>> getRecommendedActions(String dataSource,
            String targetType, String ruleId) throws Exception;

}
