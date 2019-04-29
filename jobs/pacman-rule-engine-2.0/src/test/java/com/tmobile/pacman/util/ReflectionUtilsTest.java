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
  Author :kkumar
  Modified Date: Aug 4, 2017

**/
package com.tmobile.pacman.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.base.Strings;
import com.tmobile.pacman.commons.autofix.PacmanFix;
import com.tmobile.pacman.config.ConfigManager;

// TODO: Auto-generated Javadoc
/**
 * The Class ReflectionUtilsTest.
 */
@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ CommonUtils.class, StringBuilder.class, Strings.class,ConfigManager.class})

public class ReflectionUtilsTest {
	
	 /**
     * Setup.
     */
    @Before
    public void setup(){
    	mockStatic(ConfigManager.class);
        ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
    }

	/**
	 * Find fix class 1.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	@Test
	public void findFixClass1() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		assertThatThrownBy(() -> ReflectionUtils.findFixClass("ruleKey")).isInstanceOf(Exception.class);
	}

	/**
	 * Find fix class 2.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 *//*
	@Test
	public void findFixClass2() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		assertNotNull(ReflectionUtils.findFixClass("ec2-global-ssh-fix"));
	}*/

	/**
	 * Find associate class 1.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	@Test
	public void findAssociateClass1() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		assertThatThrownBy(() -> ReflectionUtils.findAssociateClass("ruleKey")).isInstanceOf(Exception.class);
	}

	/**
	 * Find associate class 2.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	@Test
	public void findAssociateClass2() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		assertNotNull(ReflectionUtils.findAssociateClass("test_key"));
	}

	/**
	 * Find associate class 3.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	@Test
	public void findAssociateClass3() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		assertThatThrownBy(() -> ReflectionUtils.findAssociateClass(PacmanFix.class, "ruleKey")).isInstanceOf(Exception.class);
	}

	/**
	 * Find associate class 4.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	@Test
	public void findAssociateClass4() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		assertNotNull(ReflectionUtils.findAssociateClass(PacmanFix.class, "com.tmobile"));
	}

	/*@Test
	public void findAssociateClass5() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		assertNotNull(ReflectionUtils.findAssociateClass(PacmanFix.class, null));
	}*/



	/**
	 * Find associated method.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 */
	@Test
	public void findAssociatedMethod() throws NoSuchMethodException {
		assertNotNull(ReflectionUtils.findAssociatedMethod(PacmanFix.class, "getFactory"));
	}

	/**
	 * Find associated method exception.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 */
	@Test
	public void findAssociatedMethodException() throws NoSuchMethodException {
		assertThatThrownBy(() -> ReflectionUtils.findAssociatedMethod(PacmanFix.class, "getFactory1")).isInstanceOf(Exception.class);
	}


	/*
	@Test
	public void findEntryMethod1() throws NoSuchMethodException {
		assertNotNull(ReflectionUtils.findEntryMethod(PacmanFix.class, PacmanFix.class));
	}*/

	/**
	 * Find entry method 2.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 */
	@Test
	public void findEntryMethod2() throws NoSuchMethodException {
		assertThatThrownBy(() -> ReflectionUtils.findEntryMethod(PacmanFix.class, PacmanFix.class)).isInstanceOf(Exception.class);
	}

}
