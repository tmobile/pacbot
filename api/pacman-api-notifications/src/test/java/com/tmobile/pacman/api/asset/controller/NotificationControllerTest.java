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

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang.StringUtils;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.api.notification.controller.NotificationController;
import com.tmobile.pacman.api.notification.dto.MailMessageRequestBody;
import com.tmobile.pacman.api.notification.dto.MailTemplateRequestBody;
import com.tmobile.pacman.api.notification.service.MailService;
import com.tmobile.pacman.api.notification.service.NotificationService;

@RunWith(MockitoJUnitRunner.class)
public class NotificationControllerTest {

	private MockMvc mockMvc;

	@Mock
	private MailService mailService;

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private NotificationController notificationController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void prepareAndSendMailTest() throws Exception {
		MailMessageRequestBody mailMessageRequestBody = buildMailMessageRequestBody();
		byte[] mailMessageRequestBodyContent = toJson(mailMessageRequestBody);
		doNothing().when(mailService).prepareAndSendMail(anyString(),anyString(), anyList(), anyString(), anyString(), anyMap(),
				anyString(), anyBoolean());
		mockMvc.perform(post("/send-plain-text-mail").content(mailMessageRequestBodyContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		verify(mailService, times(1)).prepareAndSendMail("",mailMessageRequestBody.getFrom(),
				mailMessageRequestBody.getTo(), mailMessageRequestBody.getSubject(),
				mailMessageRequestBody.getMailBodyAsString(), mailMessageRequestBody.getPlaceholderValues(),
				mailMessageRequestBody.getAttachmentUrl(), true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void prepareAndSendMailExceptionTest() throws Exception {
		MailMessageRequestBody mailMessageRequestBody = buildMailMessageRequestBody();
		byte[] mailMessageRequestBodyContent = toJson(mailMessageRequestBody);
		doThrow(Exception.class).when(mailService).prepareAndSendMail(anyString(),anyString(), anyList(), anyString(), anyString(),
				anyMap(), anyString(), anyBoolean());
		mockMvc.perform(post("/send-plain-text-mail").content(mailMessageRequestBodyContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void sendMailWithTemplateTest() throws Exception {
		MailTemplateRequestBody mailTemplateRequestBody = buildMailTemplateRequestBody();
		byte[] mailTemplateRequestBodyContent = toJson(mailTemplateRequestBody);
		doNothing().when(mailService).prepareAndSendMail(anyString(),anyString(), anyList(), anyString(), anyString(), anyMap(),
				anyString(), anyBoolean());
		mockMvc.perform(post("/send-mail-with-template").content(mailTemplateRequestBodyContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		verify(mailService, times(1)).prepareAndSendMail("",mailTemplateRequestBody.getFrom(),
				mailTemplateRequestBody.getTo(), mailTemplateRequestBody.getSubject(),
				mailTemplateRequestBody.getMailBodyAsString(), mailTemplateRequestBody.getPlaceholderValues(),
				mailTemplateRequestBody.getAttachmentUrl(), false);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void sendMailWithTemplateExceptionTest() throws Exception {
		MailTemplateRequestBody mailTemplateRequestBody = buildMailTemplateRequestBody();
		byte[] mailTemplateRequestBodyContent = toJson(mailTemplateRequestBody);
		doThrow(Exception.class).when(mailService).prepareAndSendMail(anyString(),anyString(), anyList(), anyString(), anyString(),
				anyMap(), anyString(), anyBoolean());
		mockMvc.perform(post("/send-mail-with-template").content(mailTemplateRequestBodyContent)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}
	
	@Test
	public void unsubscribeDigestMailTest() throws Exception {
		when(notificationService.unsubscribeDigestMail(anyString())).thenReturn(Maps.newHashMap());
		mockMvc.perform(get("/unsubscribe-digest-mail")
				.param("mailId", StringUtils.EMPTY))
				.andExpect(status().isOk());
	}
	
	
	@Test
	public void sendTextMailFallBack() throws Exception {
		//doNothing().when(notificationController).sendTextMailFallBack(anyMap());
		notificationController.sendTextMailFallBack(Maps.newHashMap());
		//verify(notificationController, times(1)).sendTextMailFallBack(Maps.newHashMap());
	}
	
	@Test
	public void getDeviceDetailsNotFoundTest() throws Exception {
		when(notificationService.getDeviceDetails(eq("device123"))).thenReturn(Maps.newHashMap());
		mockMvc.perform(get("/cache")
				.param("deviceId", StringUtils.EMPTY))
				.andExpect(status().isExpectationFailed());
	}
	
	@Test
	public void getDeviceDetailsTest() throws Exception {
		when(notificationService.getDeviceDetails(eq("device123"))).thenReturn(Maps.newHashMap());
		mockMvc.perform(get("/cache")
				.param("deviceId", "deviceId123"))
				.andExpect(status().isOk());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getDeviceDetailsExceptionTest() throws Exception {
		when(notificationService.getDeviceDetails("device123")).thenThrow(Exception.class);
		mockMvc.perform(get("/cache")
				.param("deviceId", "device123"))
				.andExpect(status().isExpectationFailed());
	}

	
	
	

	private MailTemplateRequestBody buildMailTemplateRequestBody() {
		MailTemplateRequestBody mailTemplateRequestBody = new MailTemplateRequestBody();
		mailTemplateRequestBody.setAttachmentUrl("attachmentUrl");
		mailTemplateRequestBody.setFrom("from");
		mailTemplateRequestBody.setMailBodyAsString("mailBodyAsString");
		mailTemplateRequestBody.setPlaceholderValues(Maps.newHashMap());
		mailTemplateRequestBody.setSubject("subject");
		mailTemplateRequestBody.setTo(Lists.newArrayList());
		return mailTemplateRequestBody;
	}

	private MailMessageRequestBody buildMailMessageRequestBody() {
		MailMessageRequestBody mailMessageRequestBody = new MailMessageRequestBody();
		mailMessageRequestBody.setAttachmentUrl("attachmentUrl");
		mailMessageRequestBody.setFrom("from");
		mailMessageRequestBody.setMailBodyAsString("mailBodyAsString");
		mailMessageRequestBody.setPlaceholderValues(Maps.newHashMap());
		mailMessageRequestBody.setSubject("subject");
		mailMessageRequestBody.setTo(Lists.newArrayList());
		return mailMessageRequestBody;
	}

	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}
}
