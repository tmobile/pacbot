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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;

/**
 * @author kkumar
 *
 */
@Service
public class RecommendedActionServiceImpl implements RecommendedActionService,
        Constants {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    private static final String INDEX_RECOMMENDED_ACTIONS = "recommended_actions";
    private static final String TYPE_RECOMMENDED_ACTIONS = "action";

    /**
	 *
	 */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendedActionServiceImpl.class);

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.api.compliance.service.RecommendedActionService#
     * getRecommendedActions(java.lang.String)
     */
    @Override
    public List<Map<String, Object>> getRecommendedActions(String dataSource,
            String targetType, String ruleId) throws ServiceException {

        Map<String, Object> mustFilter = new HashMap<>();
        if (!Strings.isNullOrEmpty(dataSource)) {
            mustFilter.put("dataSource.keyword", dataSource);
        }
        if (Strings.isNullOrEmpty(targetType)) {
            throw new ServiceException("target type cannot be empty");
        }
        if (Strings.isNullOrEmpty(ruleId)) {
            throw new ServiceException("ruleId cannot be empty");
        }
        mustFilter.put("actsOn.keyword", targetType);
        mustFilter.put("ruleId.keyword", ruleId);
        try{
        return elasticSearchRepository.getSortedDataFromES(INDEX_RECOMMENDED_ACTIONS,
                TYPE_RECOMMENDED_ACTIONS, mustFilter, null, null, null, null,null);
        }catch(Exception e){
            throw new ServiceException(e);
        }
    }

}
