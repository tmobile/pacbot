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

import static com.tmobile.pacman.api.admin.common.AdminConstants.DOMAIN_CREATION_SUCCESS;
import static com.tmobile.pacman.api.admin.common.AdminConstants.DOMAIN_UPDATION_SUCCESS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.domain.CreateUpdateDomain;
import com.tmobile.pacman.api.admin.exceptions.PacManException;
import com.tmobile.pacman.api.admin.repository.DomainRepository;
import com.tmobile.pacman.api.admin.repository.model.Domain;

@RunWith(MockitoJUnitRunner.class)
public class DomainServiceImplTest {

	@InjectMocks
	private DomainServiceImpl domainService;

	@Mock
	private DomainRepository domainRepository;

	@Test
	public void getAllDomainsTest() {
		List<Domain> allDomains = getAllDomainRespone();
		when(domainRepository.findAll()).thenReturn(allDomains);
		assertThat(domainService.getAllDomains().size(), is(1));
	}

	@Test
	public void getDomainByNameTest() throws PacManException {
		when(domainRepository.existsById(anyString())).thenReturn(true);
		when(domainRepository.findById(anyString())).thenReturn(getDomainDetails());
		assertThat(domainService.getDomainByName(anyString()).getConfig(), is("config123"));
	}

	@Test
	public void getAllDomainDetailsTest() {
		Object[] domain = { "DomainName123", "DomainDesc123" };
		List<Object[]> domains = Lists.newArrayList();
		domains.add(domain);
		Page<Object[]> allDomains = new PageImpl<Object[]>(domains, new PageRequest(0, 1), domains.size());
		when(domainRepository.findAllDomainDetails(anyString(), any(PageRequest.class))).thenReturn(allDomains);
		assertThat(domainService.getAllDomainDetails(StringUtils.EMPTY, 0, 1).getContent().size(), is(1));
	}

	@Test
	public void createDomainTest() throws PacManException {
		when(domainRepository.existsById(anyString())).thenReturn(false);
		assertThat(domainService.createDomain(getDomainDetailsRequest(), "user123"), is(DOMAIN_CREATION_SUCCESS));
	}

	@Test
	public void createDomainExceptionTest() throws PacManException {
		when(domainRepository.existsById(anyString())).thenReturn(true);
		assertThatThrownBy(() -> domainService.createDomain(getDomainDetailsRequest(), "user123"))
				.isInstanceOf(PacManException.class);
	}

	@Test
	public void updateDomainTest() throws PacManException {
		when(domainRepository.existsById(anyString())).thenReturn(true);
		when(domainRepository.findById(anyString())).thenReturn(getDomainDetails());
		assertThat(domainService.updateDomain(getDomainDetailsRequest(), "user123"), is(DOMAIN_UPDATION_SUCCESS));
	}

	@Test
	public void updateDomainExceptionTest() throws PacManException {
		when(domainRepository.existsById(anyString())).thenReturn(false);
		assertThatThrownBy(() -> domainService.updateDomain(getDomainDetailsRequest(), "user123"))
				.isInstanceOf(PacManException.class);
	}

	private CreateUpdateDomain getDomainDetailsRequest() {
		CreateUpdateDomain domainDetails = new CreateUpdateDomain();
		domainDetails.setName("domainName123");
		domainDetails.setConfig("domainConfig123");
		domainDetails.setDesc("domainDesc123");
		return domainDetails;
	}

	private List<Domain> getAllDomainRespone() {
		List<Domain> allDomains = Lists.newArrayList();
		Domain domain = getDomainDetails().get();
		allDomains.add(domain);
		return allDomains;
	}

	private Optional<Domain> getDomainDetails() {
		Domain domain = new Domain();
		domain.setConfig("config123");
		domain.setCreatedDate(new Date());
		domain.setDomainDesc("domainDesc123");
		domain.setDomainName("domainName123");
		domain.setModifiedDate(new Date());
		domain.setUserId("userId123");
		return Optional.of(domain);
	}
}
