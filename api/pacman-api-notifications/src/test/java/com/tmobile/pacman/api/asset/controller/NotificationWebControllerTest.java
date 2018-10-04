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
package com.tmobile.pacman.api.asset.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.google.common.collect.Maps;
import com.tmobile.pacman.api.notification.controller.NotificationWebController;
import com.tmobile.pacman.api.notification.service.NotificationService;

@RunWith(MockitoJUnitRunner.class)
public class NotificationWebControllerTest {
	private MockMvc mockMvc;

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private NotificationWebController notificationWebController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(notificationWebController).setViewResolvers(viewResolver()).build();
	}

	
	@Test
	public void unSubscribeTest() throws Exception {
		Map<String, Object> response = Maps.newHashMap();
		response.put("email", "value");
		when(notificationService.unsubscribeDigestMail(anyString())).thenReturn(response);
		mockMvc.perform(get("/un-subscribe").param("email", StringUtils.EMPTY)).andExpect(status().isOk())
				.andExpect(view().name("un-subscribe")).andExpect(forwardedUrl("classpath:templates/un-subscribe.html"))
				.andExpect(model().attribute("email", is("")));

	}
	
	@Test
	public void subscribeTest() throws Exception {
		Map<String, Object> response = Maps.newHashMap();
		response.put("email", "value");
		when(notificationService.subscribeDigestMail(anyString())).thenReturn(response);
		mockMvc.perform(get("/subscribe").param("email", StringUtils.EMPTY)).andExpect(status().isOk())
				.andExpect(view().name("subscribe")).andExpect(forwardedUrl("classpath:templates/subscribe.html"))
				.andExpect(model().attribute("email", is("")));

	}
	
	private ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("classpath:templates/");
		viewResolver.setSuffix(".html");
		return viewResolver;
	}

}
