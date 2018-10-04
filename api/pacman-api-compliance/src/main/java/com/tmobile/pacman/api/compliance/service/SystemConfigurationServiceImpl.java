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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.compliance.repository.SystemConfigurationRepository;
import com.tmobile.pacman.api.compliance.repository.model.SystemConfiguration;

/**
 * The Class SystemConfigurationServiceImpl.
 */
@Service
public class SystemConfigurationServiceImpl implements
        SystemConfigurationService, Constants {
    
    /** The system config repository. */
    @Autowired
    private SystemConfigurationRepository systemConfigRepository;

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.SystemConfigurationService#getConfigValue(java.lang.String)
     */
    @Override
    public String getConfigValue(final String keyname) {
        SystemConfiguration systemConfiguration = systemConfigRepository
                .findSystemConfigurationByKeyname(keyname);
        return systemConfiguration.getValue();
    }
}
