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
package com.tmobile.pacman.api.compliance.service;

import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.compliance.domain.PatchingProgressResponse;
import com.tmobile.pacman.api.compliance.domain.ProjectionRequest;
import com.tmobile.pacman.api.compliance.domain.ProjectionResponse;

/**
 * The Interface ProjectionService.
 */
public interface ProjectionService {

    /**
     * If service receives resourceType,year and quarter as request parameter,
     * it will save the db with week and projection as zero for those weeks in
     * that given quarter and year. If service receives resourceType,year
     * quarter and projectionByWeek as request parameter, it will update the db
     * with projection as mentioned value for those weeks in that given quarter
     * and year.
     *
     * @param projectionRequest the projection request
     * @return Boolean
     * @throws ServiceException the service exception
     */
    public Boolean updateProjection(ProjectionRequest projectionRequest)
            throws ServiceException;

    /**
     * list of projection for the given quarter,year
     *              and the target type from DB.
     *
     * @param targetType the target type
     * @param year the year
     * @param quarter the quarter
     * @return ProjectionResponse
     * @throws ServiceException the service exception
     */
    public ProjectionResponse getProjection(String targetType, int year,
            int quarter) throws ServiceException;

    /**
     * Gets the list of weekly based patching and projection data for the given
     * quarter,year and the target type.
     *
     * @param assetGroup the asset group
     * @return ProjectionResponse
     * @throws ServiceException the service exception
     */
    public ProjectionResponse getPatchingAndProjectionByWeek(String assetGroup)
            throws ServiceException;

    /**
     * Gets the list of patching progress by director.
     *
     * @param assetGroup the asset group
     * @return PatchingProgressResponse
     * @throws ServiceException the service exception
     */
    public PatchingProgressResponse getPatchingProgressByDirector(
            String assetGroup) throws ServiceException;

    /**
     * Gets the patching progress by executive sponsors.
     *
     * @param assetGroup the asset group
     * @return PatchingProgressResponse
     * @throws ServiceException the service exception
     */
    public PatchingProgressResponse patchProgByExSponsor(String assetGroup)
            throws ServiceException;
}
