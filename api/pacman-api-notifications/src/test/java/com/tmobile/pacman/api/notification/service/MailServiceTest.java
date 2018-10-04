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
/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :santoshi
  Modified Date: Jul 10, 2018

**/
package com.tmobile.pacman.api.notification.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.notification.domain.TimePeriod;

@RunWith(PowerMockRunner.class)
/* @PrepareForTest(MessagePreparer.class) */
public class MailServiceTest {

	@Mock
	private JavaMailSender mailSender;
	@Mock
	private MailContentBuilderService mailContentBuilderService;

	@Mock
	private ElasticSearchRepository elasticSearchRepository;

	MailService mailService = new MailService(mailSender, mailContentBuilderService);

	@Test
	public void getFileContent() throws Exception {
		URL myURL = new URL("http://example.com/");
		Whitebox.invokeMethod(mailService, "getFileContent", myURL);
	}

	@Test
	public void isHttpUrlTest() throws Exception {
		Whitebox.invokeMethod(mailService, "isHttpUrl", "www.google.com");
	}

	@Test
	public void processTemplate1() throws Exception {
		mailService = PowerMockito.spy(new MailService(mailSender, mailContentBuilderService));
		String templateUrl = "templateUrl";
		when(mailContentBuilderService.getRemoteMailContent(anyString())).thenReturn("TEST");
		assertNotNull(mailService.processTemplate(templateUrl, Maps.newHashMap()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void processTemplate2() throws Exception {
		mailService = PowerMockito.spy(new MailService(mailSender, mailContentBuilderService));
		String templateUrl = "templateUrl";
		when(mailContentBuilderService.getRemoteMailContent(anyString())).thenThrow(Exception.class);
		assertNull(mailService.processTemplate(templateUrl, Maps.newHashMap()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void buildMimeMessagePreparator() throws Exception {
		//MimeMessage mockMessage = PowerMockito.mock(MimeMessage.class);
		MimeMessageHelper mockMessageHelper = PowerMockito.mock(MimeMessageHelper.class);
		PowerMockito.whenNew(MimeMessageHelper.class).withArguments(any(MimeMessage.class), anyBoolean()).thenReturn(mockMessageHelper);

		when(mailContentBuilderService.buildPlainTextMail(anyString(), anyMap())).thenReturn("content");
		mailService = PowerMockito.spy(new MailService(mailSender, mailContentBuilderService));
		String from = "from";
		List<String> to = Lists.newArrayList();
		to.add("test@gmail.com");
		String subject = "subject";
		String mailMessageUrlOrBody = "mailMessageUrlOrBody";
		Map<String, Object> placeholderValues = Maps.newHashMap();
		placeholderValues.put("name", "134");
		final String attachmentUrl = "www.test.com";
		final Boolean isPlainMessage = true;
		final MailService classUnderTest = PowerMockito.spy(new MailService(mailSender, mailContentBuilderService));

		Whitebox.invokeMethod(classUnderTest, "buildMimeMessagePreparator", from, to, subject, mailMessageUrlOrBody,
				placeholderValues, attachmentUrl, isPlainMessage);
	}

	@Test
	public void timePeriodTest() throws Exception {
		assertEquals(TimePeriod.DAY, TimePeriod.DAY);
	}
}
