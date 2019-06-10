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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.tmobile.pacman.api.admin.domain.ConfigPropertyItem;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyRequest;
import com.tmobile.pacman.api.admin.repository.ConfigPropertyAuditRepository;
import com.tmobile.pacman.api.admin.repository.ConfigPropertyMetadataRepository;
import com.tmobile.pacman.api.admin.repository.ConfigPropertyRelationRepository;
import com.tmobile.pacman.api.admin.repository.ConfigPropertyRepository;
import com.tmobile.pacman.api.admin.repository.model.ConfigProperty;
import com.tmobile.pacman.api.admin.repository.model.ConfigPropertyAudit;
import com.tmobile.pacman.api.admin.repository.model.ConfigPropertyKey;
import com.tmobile.pacman.api.admin.repository.model.ConfigPropertyMetadata;
import com.tmobile.pacman.api.admin.repository.model.ConfigPropertyRelation;

@RunWith(MockitoJUnitRunner.class)
public class ConfigPropertyServiceImplTest {

	@InjectMocks
	private ConfigPropertyServiceImpl configPropertyService;

	@Mock
	private ConfigPropertyRepository configPropertyRepository;

	@Mock
	private ConfigPropertyRelationRepository configRelationRepository;

	@Mock
	private ConfigPropertyMetadataRepository configPropertyMetadataRepository;

	@Mock
	private ConfigPropertyAuditRepository configPropertyAuditRepository;

	@Test
	public void listPropertiesTest() throws Exception {

		List<ConfigProperty> cfList = new ArrayList<>();

		ConfigProperty cf1 = new ConfigProperty();
		ConfigPropertyKey cfkey1 = new ConfigPropertyKey();
		cfkey1.setApplication("application");
		cfkey1.setCfkey("applevelkey1");
		cfkey1.setProfile("dev");
		cf1.setConfigKeyParams(cfkey1);
		cf1.setValue("applevelvalue");
		cfList.add(cf1);

		ConfigProperty cf2 = new ConfigProperty();
		ConfigPropertyKey cfkey2 = new ConfigPropertyKey();
		cfkey2.setApplication("application");
		cfkey2.setCfkey("applevelkey2");
		cfkey2.setProfile("dev");
		cf2.setConfigKeyParams(cfkey2);
		cf2.setValue("applevelvalue");
		cfList.add(cf2);

		ConfigProperty cf3 = new ConfigProperty();
		ConfigPropertyKey cfkey3 = new ConfigPropertyKey();
		cfkey3.setApplication("api");
		cfkey3.setCfkey("applevelkey3");
		cfkey3.setProfile("dev");
		cf3.setConfigKeyParams(cfkey3);
		cf3.setValue("applevelvalue");
		cfList.add(cf3);

		ConfigProperty cf4 = new ConfigProperty();
		ConfigPropertyKey cfkey4 = new ConfigPropertyKey();
		cfkey4.setApplication("api");
		cfkey4.setCfkey("applevelkey4");
		cfkey4.setProfile("dev");
		cf4.setConfigKeyParams(cfkey4);
		cf4.setValue("applevelvalue");
		cfList.add(cf4);

		ConfigProperty cf5 = new ConfigProperty();
		ConfigPropertyKey cfkey5 = new ConfigPropertyKey();
		cfkey5.setApplication("api");
		cfkey5.setCfkey("applevelkey5");
		cfkey5.setProfile("dev");
		cf5.setConfigKeyParams(cfkey5);
		cf5.setValue("applevelvalue");
		cfList.add(cf5);

		when(configPropertyRepository.findAll()).thenReturn(cfList);

		List<ConfigPropertyRelation> relationlist = new ArrayList<>();

		ConfigPropertyRelation relation1 = new ConfigPropertyRelation();
		relation1.setApplication("application");
		relation1.setParent("root");
		relationlist.add(relation1);

		ConfigPropertyRelation relation2 = new ConfigPropertyRelation();
		relation2.setApplication("api");
		relation2.setParent("application");
		relationlist.add(relation2);

		ConfigPropertyRelation relation3 = new ConfigPropertyRelation();
		relation3.setApplication("asset-service");
		relation3.setParent("api");
		relationlist.add(relation3);

		when(configRelationRepository.findAll()).thenReturn(relationlist);

		List<ConfigPropertyMetadata> cfMetaList = new ArrayList<>();

		ConfigPropertyMetadata meta1 = new ConfigPropertyMetadata();
		meta1.setCfkey("applevelkey1");
		meta1.setDescription("desc");
		cfMetaList.add(meta1);

		ConfigPropertyMetadata meta2 = new ConfigPropertyMetadata();
		meta2.setCfkey("applevelkey2");
		meta2.setDescription("desc");
		cfMetaList.add(meta2);

		ConfigPropertyMetadata meta3 = new ConfigPropertyMetadata();
		meta3.setCfkey("applevelkey3");
		meta3.setDescription("desc");
		cfMetaList.add(meta3);

		ConfigPropertyMetadata meta4 = new ConfigPropertyMetadata();
		meta4.setCfkey("applevelkey4");
		meta4.setDescription("desc");
		cfMetaList.add(meta4);

		ConfigPropertyMetadata meta5 = new ConfigPropertyMetadata();
		meta5.setCfkey("applevelkey5");
		meta5.setDescription("desc");
		cfMetaList.add(meta5);

		when(configPropertyMetadataRepository.findAll()).thenReturn(cfMetaList);

		assertTrue((configPropertyService.listProperties().getProperties().size() == 2));
		assertTrue((configPropertyService.listProperties().getChildren().size() == 1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void addUpdatePropertiesTest() throws Exception {
		ConfigPropertyRequest request = new ConfigPropertyRequest();
		List<ConfigPropertyItem> configPropertyItemList = new ArrayList<ConfigPropertyItem>();
		List<ConfigProperty> cfList = new ArrayList<>();

		ConfigPropertyItem configPropertyItem = new ConfigPropertyItem();
		configPropertyItem.setApplication("application");
		configPropertyItem.setConfigKey("key");
		configPropertyItem.setConfigKey("value");

		configPropertyItemList.add(configPropertyItem);
		request.setConfigProperties(configPropertyItemList);

		when(configPropertyRepository.existsByConfigKeyParams(any(ConfigPropertyKey.class))).thenReturn(false);

		ConfigProperty cf1 = new ConfigProperty();
		ConfigPropertyKey cfkey1 = new ConfigPropertyKey();
		cfkey1.setApplication("application");
		cfkey1.setCfkey("applevelkey1");
		cfkey1.setProfile("dev");
		cf1.setConfigKeyParams(cfkey1);
		cf1.setValue("applevelvalue");
		cfList.add(cf1);

		when(configPropertyRepository.saveAll(anyCollection())).thenReturn(cfList);
		when(configPropertyAuditRepository.saveAll(anyCollection())).thenReturn(new ArrayList<>());
		assertTrue(configPropertyService.addUpdateProperties(request, "user", "", "10/10/2018 10:10:10", false)
				.size() == 1);

		when(configPropertyRepository.existsByConfigKeyParams(any(ConfigPropertyKey.class))).thenReturn(true);
		when(configPropertyRepository.findByConfigKeyParams(any(ConfigPropertyKey.class))).thenReturn(cf1);
		when(configPropertyRepository.saveAll(anyCollection())).thenReturn(cfList);
		when(configPropertyAuditRepository.saveAll(anyCollection())).thenReturn(new ArrayList<>());
		assertTrue(configPropertyService.addUpdateProperties(request, "user", "", "10/10/2018 10:10:10", false)
				.size() == 1);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deletePropertiesTest() throws Exception {
		ConfigPropertyItem configPropertyRequest = new ConfigPropertyItem();
		configPropertyRequest.setApplication("application");
		configPropertyRequest.setConfigKey("key");
		configPropertyRequest.setConfigKey("value");

		List<ConfigProperty> cfList = new ArrayList<>();

		ConfigProperty cf1 = new ConfigProperty();
		ConfigPropertyKey cfkey1 = new ConfigPropertyKey();
		cfkey1.setApplication("application");
		cfkey1.setCfkey("applevelkey1");
		cfkey1.setProfile("dev");
		cf1.setConfigKeyParams(cfkey1);
		cf1.setValue("applevelvalue");
		cfList.add(cf1);

		when(configPropertyRepository.findByConfigKeyParams(any(ConfigPropertyKey.class))).thenReturn(cf1);

		Mockito.doNothing().when(configPropertyRepository).deleteAll(anyCollection());
		when(configPropertyAuditRepository.saveAll(anyCollection())).thenReturn(new ArrayList<>());
		assertTrue(configPropertyService.deleteProperty(configPropertyRequest, "user", "", "10/10/2018 10:10:10", false)
				.equals("Success"));
	}

	@Test
	public void isPropertiesExistingTest() throws Exception {
		ConfigPropertyItem configPropertyRequest = new ConfigPropertyItem();
		configPropertyRequest.setApplication("application");
		configPropertyRequest.setConfigKey("applevelkey2");
		configPropertyRequest.setConfigValue("applevelvalue");

		List<ConfigProperty> cfList = new ArrayList<>();
		ConfigProperty cf1 = new ConfigProperty();
		ConfigPropertyKey cfkey1 = new ConfigPropertyKey();
		cfkey1.setApplication("application");
		cfkey1.setCfkey("applevelkey1");
		cfkey1.setProfile("dev");
		cf1.setConfigKeyParams(cfkey1);
		cf1.setValue("applevelvalue");
		cfList.add(cf1);

		when(configPropertyRepository.findAll()).thenReturn(cfList);

		assertFalse(configPropertyService.isPropertyExisting(configPropertyRequest));
	}

	@Test
	public void iscfkeyExistingTest() throws Exception {
		List<ConfigPropertyMetadata> cfMetaList = new ArrayList<>();

		ConfigPropertyMetadata meta1 = new ConfigPropertyMetadata();
		meta1.setCfkey("applevelkey1");
		meta1.setDescription("desc");
		cfMetaList.add(meta1);

		ConfigPropertyMetadata meta2 = new ConfigPropertyMetadata();
		meta2.setCfkey("applevelkey2");
		meta2.setDescription("desc");
		cfMetaList.add(meta2);

		when(configPropertyMetadataRepository.findAll()).thenReturn(cfMetaList);

		assertTrue(configPropertyService.isCfkeyExisting("applevelkey2"));
	}

	@Test
	public void isApplicationExistingTest() throws Exception {
		List<ConfigPropertyRelation> relationlist = new ArrayList<>();

		ConfigPropertyRelation relation1 = new ConfigPropertyRelation();
		relation1.setApplication("application");
		relation1.setParent("root");
		relationlist.add(relation1);

		ConfigPropertyRelation relation2 = new ConfigPropertyRelation();
		relation2.setApplication("api");
		relation2.setParent("application");
		relationlist.add(relation2);

		ConfigPropertyRelation relation3 = new ConfigPropertyRelation();
		relation3.setApplication("asset-service");
		relation3.setParent("api");
		relationlist.add(relation3);

		when(configRelationRepository.findAll()).thenReturn(relationlist);
		assertTrue(configPropertyService.isApplicationExisting("api"));
	}

	@Test
	public void testlistAllConfigPropertyAudits() throws Exception {
		List<ConfigPropertyAudit> configPropertyAuditList = new ArrayList<>();
		ConfigPropertyAudit auditItem1 = new ConfigPropertyAudit();
		auditItem1.setApplication("application");
		auditItem1.setCfkey("logging.esHost");
		auditItem1.setLabel("latest");
		auditItem1.setOldvalue("old");
		auditItem1.setNewvalue("new");
		auditItem1.setModifiedDate("01/01/2013 01:01:01");
		configPropertyAuditList.add(auditItem1);

		ConfigPropertyAudit auditItem2 = new ConfigPropertyAudit();
		auditItem2.setApplication("application");
		auditItem2.setCfkey("logging.esHost");
		auditItem2.setLabel("latest");
		auditItem2.setOldvalue("new");
		auditItem2.setNewvalue("newest");
		auditItem2.setModifiedDate("02/02/2013 01:01:01");
		configPropertyAuditList.add(auditItem2);

		when(configPropertyAuditRepository.findAllByOrderByModifiedDateDescModifiedByAsc())
				.thenReturn(configPropertyAuditList);
		assertTrue(configPropertyService.listAllConfigPropertyAudits("01/01/2012 01:01:01").getConfigPropertyAudit()
				.size() > 0);

	}

	@Test
	public void testgetRollbackPreview() throws Exception {
		List<ConfigPropertyAudit> configPropertyAuditList = new ArrayList<>();
		ConfigPropertyAudit auditItem1 = new ConfigPropertyAudit();
		auditItem1.setApplication("application");
		auditItem1.setCfkey("logging.esHost");
		auditItem1.setLabel("latest");
		auditItem1.setOldvalue("old");
		auditItem1.setNewvalue("new");
		auditItem1.setModifiedDate("01/01/2013 01:01:01");
		configPropertyAuditList.add(auditItem1);

		ConfigPropertyAudit auditItem2 = new ConfigPropertyAudit();
		auditItem2.setApplication("application");
		auditItem2.setCfkey("logging.esHost");
		auditItem2.setLabel("latest");
		auditItem2.setOldvalue("new");
		auditItem2.setNewvalue("newest");
		auditItem2.setModifiedDate("02/02/2013 01:01:01");
		configPropertyAuditList.add(auditItem2);

		when(configPropertyAuditRepository.findAllByOrderByModifiedDateDescModifiedByAsc())
				.thenReturn(configPropertyAuditList);
		assertTrue(configPropertyService.getRollbackPreview("01/01/2012 01:01:01").getRollbackChangeSet().size() > 0);
	}

	@Test
	public void testisAllPropertiesExisting() throws Exception {
		List<ConfigProperty> cfList = new ArrayList<>();

		when(configPropertyRepository.findAll()).thenReturn(cfList);

		ConfigPropertyRequest configPropertyRequest = new ConfigPropertyRequest();
		List<ConfigPropertyItem> configPropertyItemList = new ArrayList<>();
		ConfigPropertyItem item = new ConfigPropertyItem();
		item.setApplication("dummy");
		configPropertyItemList.add(item);
		configPropertyRequest.setConfigProperties(configPropertyItemList);

		assertTrue(configPropertyService.isAllPropertiesExisting(configPropertyRequest) == false);
	}

	@Test
	public void testisAllApplicationsExisting() throws Exception {

		ConfigPropertyRequest configPropertyRequest = new ConfigPropertyRequest();
		List<ConfigPropertyItem> configPropertyItemList = new ArrayList<>();
		ConfigPropertyItem item = new ConfigPropertyItem();
		item.setApplication("dummy");
		configPropertyItemList.add(item);
		configPropertyRequest.setConfigProperties(configPropertyItemList);
		
		List<ConfigPropertyRelation> relList = new ArrayList<>();
		ConfigPropertyRelation rel = new ConfigPropertyRelation();
		rel.setApplication("dummy");
		rel.setParent("dummy");
		relList.add(rel);
		
		when(configRelationRepository.findAll()).thenReturn(relList );
		assertTrue(configPropertyService.isAllApplicationsExisting(configPropertyRequest) == true);

	}
}
