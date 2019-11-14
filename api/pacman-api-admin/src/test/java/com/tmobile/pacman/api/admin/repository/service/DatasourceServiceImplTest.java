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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.tmobile.pacman.api.admin.repository.DatasourceRepository;

@RunWith(MockitoJUnitRunner.class)
public class DatasourceServiceImplTest {

	@InjectMocks
	private DatasourceServiceImpl datasourceService;
	
	@Mock
	private DatasourceRepository datasourceRepository;

	@Test
	public void getAllDatasourceDetailsTest() throws Exception {
		when(datasourceRepository.findAll()).thenReturn(new ArrayList<>());
		assertThat(datasourceService.getAllDatasourceDetails().size(), is(0));
	}
}
