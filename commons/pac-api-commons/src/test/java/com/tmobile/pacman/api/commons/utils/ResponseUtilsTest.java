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
package com.tmobile.pacman.api.commons.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;


@PowerMockIgnore("org.apache.http.conn.ssl.*")
@RunWith(PowerMockRunner.class)
public class ResponseUtilsTest {

	private ForTestUtils forTest;

	@Before
    public void setUp() throws Exception{
		forTest = PowerMockito.spy(new ForTestUtils());
    }

	@Test
	public void buildSucessResponse() {
		assertEquals(forTest.buildSucessResponse(), false);
	}

	@Test
	public void buildFailureResponse() {
		assertEquals(forTest.buildFailureResponse(), false);
	}

	@Test
	public void buildFailureResponse2() {
		assertEquals(forTest.buildFailureResponse2(), false);
	}
}
