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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import org.apache.http.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.thymeleaf.TemplateEngine;

import com.google.common.collect.Maps;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TemplateEngine.class, PacHttpUtils.class})
public class MailContentBuilderServiceTest {
	@Mock
	private TemplateEngine templateEngine;

	@Mock
	private MailContentBuilderService mailContentService = new MailContentBuilderService(templateEngine);

	@Test
	public void buildTest() throws Exception {
		Map<String, String> values = Maps.newHashMap();
		values.put("key", "value");
		Whitebox.invokeMethod(mailContentService, "buildContext", values);
	}

	@Test
	public void buildPlainTextMailTest() {
		Map<String, Object> values = Maps.newHashMap();
		values.put("key", "value");
		assertThat(mailContentService.buildPlainTextMail("template", values), is(notNullValue()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRemoteMailContent() throws ParseException, IOException {
		PowerMockito.mockStatic(PacHttpUtils.class);
		when(PacHttpUtils.getHttpGet(anyString(), anyMap())).thenReturn("test");
		assertThat(mailContentService.getRemoteMailContent("template"), is(notNullValue()));
	}
}
