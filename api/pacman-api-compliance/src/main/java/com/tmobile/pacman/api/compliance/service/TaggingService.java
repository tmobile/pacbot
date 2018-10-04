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
  Modified Date: Dec 4, 2017

 **/
package com.tmobile.pacman.api.compliance.service;

import java.util.List;
import java.util.Map;

import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.compliance.domain.Request;
import com.tmobile.pacman.api.compliance.domain.ResponseWithCount;
import com.tmobile.pacman.api.compliance.domain.UntaggedTargetTypeRequest;

/**
 * The Interface TaggingService.
 */
public interface TaggingService {

    /**
     * Gets the list of un tagged assets by application.
     *
     * @param request the request
     * @return ResponseWithCount
     * @throws ServiceException the service exception
     */
    public ResponseWithCount getUnTaggedAssetsByApplication(Request request)
            throws ServiceException;

    /**
     * Gets the tagging summary.
     *
     * @param assetGroup the asset group
     * @return Map<String, Object>
     * @throws ServiceException the service exception
     */
    public Map<String, Object> getTaggingSummary(String assetGroup)
            throws ServiceException;

    /**
     * Gets the un tagging by target types.
     *
     * @param request the request
     * @return List<Map<String, Object>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getUntaggingByTargetTypes(
            UntaggedTargetTypeRequest request) throws ServiceException;

    /**
     * If method receives assetGroup as request parameter, it gives
     *              the list of target types which has addition of no and
     *              unknown applications. If method receives assetGroup and
     *              targetType as request parameter, it gives the addition of no
     *              and unknown applications for that target type.
     *
     * @param assetGroup the asset group
     * @param targetType the target type
     * @return List<Map<String, Long>>
     * @throws ServiceException the service exception
     */
    public List<Map<String, Long>> taggingByApplication(String assetGroup,
            String targetType) throws ServiceException;

}
