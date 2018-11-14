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
package com.tmobile.pacman.api.admin.repository.service;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;

import com.tmobile.pacman.api.admin.domain.CreateUpdateDomain;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.model.Domain;

/**
 * Domain Service Functionalities
 */
public interface DomainService {

	/**
     * Service to get all domains
     *
     * @author NidhishKrishnan
     * @return All Domains in List
     */
	public List<Domain> getAllDomains();

	/**
     * Service to get all domains details
     *
     * @author NidhishKrishnan
     * @param searchTerm - searchTerm to be searched.
     * @param page - zero-based page index.
     * @param size - the size of the page to be returned.
     * @return All Domains details list
     */
	public Page<Object[]> getAllDomainDetails(final String searchTerm, final int page, final int size);

	/**
     * Service to create new domain
     * 
     * @author NidhishKrishnan
     * @param createUpdateDomain - details for creating new domain
	 * @param userId - valid user id
     * @return Success or Failure response
     * @throws PacManException
     */
	public String createDomain(final CreateUpdateDomain createUpdateDomain, String userId) throws PacManException;
	
	/**
     * Service to update domain
     * 
     * @author NidhishKrishnan
     * @param createUpdateDomain - details for updating existing domain
	 * @param userId - valid user id
     * @return Success or Failure response
     * @throws PacManException
     */
	public String updateDomain(final CreateUpdateDomain createUpdateDomain, String userId) throws PacManException;

	/**
     * Service to get domain details by name
     *
     * @author NidhishKrishnan
     * @param domainName - name of the domain
     * @return Domains Details
	 * @throws PacManException 
     */
	public Domain getDomainByName(final String domainName) throws PacManException;

	/**
     * Service to get all domain names
     *
     * @author NidhishKrishnan
     * @return List of Domain Names
     */
	public Collection<String> getAllDomainNames();
}
