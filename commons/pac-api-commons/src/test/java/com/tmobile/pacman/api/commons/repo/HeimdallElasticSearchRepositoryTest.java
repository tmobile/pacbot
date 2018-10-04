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
  Author :Nidhish
  Modified Date: June 27, 2018

**/
/*
 *Copyright 2016-2017 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 *Licensed under the Amazon Software License (the "License"). You may not use
 * this file except in compliance with the License. A copy of the License is located at
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tmobile.pacman.api.commons.repo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

/**
 * @author Nidhish
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ByteBuffer.class, StringBuilder.class, PacHttpUtils.class, Strings.class, String.class })
public class HeimdallElasticSearchRepositoryTest {

	private String esHost = "elastic-search.host";

	private int esPort = 9090;

	final static String protocol = "http";

	@SuppressWarnings("unused")
	private String esUrl = protocol + "://" + esHost + ":" + esPort;

	@Test
	public void getDataFromESTest() throws Exception {
		esUrl = "esUrl";
		final HeimdallElasticSearchRepository classUnderTest = PowerMockito.spy(new HeimdallElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(StringBuilder.class);
		mockStatic(PacHttpUtils.class);
		String response = "{\"aggregations\" :{\"events-per-day\": { \"buckets\": [{\"accountname\":{\"buckets\" :[{\"key\":\"key678\"}]},\"key\":\"key123\"},{\"accountname\":{\"buckets\" :[{\"key\":\"key566\"}]},\"key\":\"key455\"}]}}}";
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(response);
		final StringBuilder mock = PowerMockito.spy(new StringBuilder());
		PowerMockito.whenNew(StringBuilder.class).withAnyArguments().thenReturn(mock);
		assertThat(classUnderTest.getEventsProcessed().size(), is(2));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getDataFromESTest1() throws Exception {
		esUrl = "esUrl";
		final HeimdallElasticSearchRepository classUnderTest = PowerMockito.spy(new HeimdallElasticSearchRepository());
		ReflectionTestUtils.setField(classUnderTest, "esUrl", "esUrl123");
		mockStatic(StringBuilder.class);
		mockStatic(PacHttpUtils.class);
		PowerMockito.when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(Exception.class);
		final StringBuilder mock = PowerMockito.spy(new StringBuilder());
		PowerMockito.whenNew(StringBuilder.class).withAnyArguments().thenReturn(mock);
		assertThatThrownBy(() -> classUnderTest.getEventsProcessed()).isInstanceOf(Exception.class);
	}

	@Before
	public void before() {
		esUrl = "esUrl";
	    MockitoAnnotations.initMocks(esHost);
	}
}
