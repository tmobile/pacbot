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

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.compliance.repository.CertificateRepository;

/**
 * The Class CertificateService.
 */
@Service
public class CertificateService {

    /** The certificate repository. */
    @Autowired
    private CertificateRepository certificateRepository;

    /**
     * Gets the certificates expiry by application.
     *
     * @param assetGroup the asset group
     * @return the certificates expiry by application
     * @throws ServiceException the service exception
     */
    public Map<String, Object> getCerticatesExpiryByApplication(
            String assetGroup) throws ServiceException {
      
        try{return certificateRepository
                .getCertificatesExpiryByApplication(assetGroup);
        }catch(DataException e){
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the certificates details.
     *
     * @param assetGroup the asset group
     * @param searchText the search text
     * @param filter the filter
     * @return the certificates details
     * @throws ServiceException the service exception
     */
    public List<Map<String, Object>> getCerticatesDetails(String assetGroup,
            String searchText, Map<String, String> filter) throws ServiceException {
        try {
            return certificateRepository.getCertificatesDetails(assetGroup,
                    searchText, filter);
        } catch (DataException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the certificates summary.
     *
     * @param assetGroup the asset group
     * @return the certificates summary
     * @throws ServiceException the service exception
     */
    public Map<String, Object> getCerticatesSummary(String assetGroup)
            throws ServiceException {
        try{
        return certificateRepository.getCertificatesSummary(assetGroup);
        }catch(DataException e){
            throw new ServiceException(e);
        }
    }

}
