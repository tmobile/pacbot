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
package com.tmobile.pacman.api.compliance.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;

/**
 * The Class DownloadRepositoryImpl.
 */
@Repository
public class DownloadRepositoryImpl implements DownloadRepository, Constants {
    
    /** The rdsepository. */
    @Autowired
    private PacmanRdsRepository rdsepository;

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.repository.DownloadRepository#getFiltersFromDb(int)
     */
    @Override
    public List<Map<String, Object>> getFiltersFromDb(int serviceId)
            throws DataException {
        String ruleIdWithTargetTypeQuery = "SELECT serviceName,serviceEndpoint FROM pac_v2_ui_download_filters where serviceId = "
                + serviceId + "";
        return rdsepository.getDataFromPacman(ruleIdWithTargetTypeQuery);
    }
    
}
