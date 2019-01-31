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
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.domain.CreateUpdateDomain;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.DomainRepository;
import com.tmobile.pacman.api.admin.repository.model.Domain;
import com.tmobile.pacman.api.commons.Constants;

/**
 * Domain Service Implementations
 */
@Service
public class DomainServiceImpl implements DomainService, Constants {

	@Autowired
	private DomainRepository domainRepository;
	
	@Override
	public Collection<String> getAllDomainNames() {
		return domainRepository.getAllDomainNames();
	}
	
	@Override
	public List<Domain> getAllDomains() {
		return domainRepository.findAll();
	}
	
	@Override
	public Domain getDomainByName(final String domainName) throws PacManException {
		if(domainRepository.existsById(domainName)) {
			return domainRepository.findById(domainName).get();
		} else {
			throw new PacManException(AdminConstants.DOMAIN_NAME_EXITS);
		}
	}
	
	@Override
	public Page<Object[]> getAllDomainDetails(final String searchTerm, final int page, final int size) {
		return domainRepository.findAllDomainDetails(searchTerm,  PageRequest.of(page, size));
	}
	
	@Override
	public String createDomain(final CreateUpdateDomain createUpdateDomain, final String userId) throws PacManException {
		boolean isDomainExits = domainRepository.existsById(createUpdateDomain.getName());
		if(!isDomainExits) {
			Date currentDate = new Date();
			Domain domain = new Domain();
			domain.setDomainName(createUpdateDomain.getName());
			domain.setDomainDesc(createUpdateDomain.getDesc());
			domain.setConfig(createUpdateDomain.getConfig());
			domain.setModifiedDate(currentDate);
			domain.setCreatedDate(currentDate);
			domain.setUserId(userId);
			domainRepository.save(domain);
			return AdminConstants.DOMAIN_CREATION_SUCCESS;
			
		} else {
			throw new PacManException(AdminConstants.DOMAIN_NAME_EXITS);
		}
	}

	@Override
	public String updateDomain(final CreateUpdateDomain createUpdateDomain, final String userId) throws PacManException {
		boolean isDomainExits = domainRepository.existsById(createUpdateDomain.getName());
		if (isDomainExits) {
			Domain existingDomain = domainRepository.findById(createUpdateDomain.getName()).get();
			existingDomain.setDomainDesc(createUpdateDomain.getDesc());
			existingDomain.setConfig(createUpdateDomain.getConfig());
			existingDomain.setModifiedDate(new Date());
			existingDomain.setUserId(userId);
			domainRepository.save(existingDomain);
			return AdminConstants.DOMAIN_UPDATION_SUCCESS;
		} else {
			throw new PacManException(AdminConstants.DOMAIN_NAME_EXITS);
		}
	}
}
