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
package com.tmobile.pacman.api.admin.controller;

import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tmobile.pacman.api.admin.repository.service.DatasourceService;

@RunWith(MockitoJUnitRunner.class)
public class DatasourceControllerTest {
	private MockMvc mockMvc;

	@Mock
	private DatasourceService datasourceService;

	@InjectMocks
	private DatasourceController datasourceController;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(datasourceController)
				/* .addFilters(new CORSFilter()) */
				.build();
	}

	@Test
	public void getAllDatasourceDetailsTest() throws Exception {
		when(datasourceService.getAllDatasourceDetails()).thenReturn(new ArrayList<>());
		mockMvc.perform(get("/datasource/list")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.data", hasSize(0)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllDatasourceDetailsExceptionTest() throws Exception {
		when(datasourceService.getAllDatasourceDetails()).thenThrow(Exception.class);
		mockMvc.perform(get("/datasource/list").contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isExpectationFailed()).andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
}
