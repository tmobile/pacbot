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

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.compliance.repository.model.SystemConfiguration;
import com.tmobile.pacman.api.compliance.repository.model.SystemConfigurationIdentity;

/**
 * The Interface SystemConfigurationRepository.
 */
@Repository
public interface SystemConfigurationRepository extends
        CrudRepository<SystemConfiguration, SystemConfigurationIdentity> {

    /**
     * Find system configuration by keyname.
     *
     * @param keyname the keyname
     * @return the system configuration
     */
    public SystemConfiguration findSystemConfigurationByKeyname(
            final String keyname);
}
