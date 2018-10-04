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
package com.tmobile.pacman.api.notification.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Sets;

@PowerMockIgnore("org.apache.http.conn.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SSLContext.class })
public class NotificationUtilTest {
	
	@SuppressWarnings("static-access")
	@Test
    public void isObjectEmpty1() {
		NotificationUtil notificationUtil = mock(NotificationUtil.class);
		assertFalse(notificationUtil.isObjectEmpty(new NotificationUtilTest()));
	}
	
	@SuppressWarnings("static-access")
	@Test
    public void isObjectEmpty2() {
		NotificationUtil notificationUtil = mock(NotificationUtil.class);
		assertTrue(notificationUtil.isObjectEmpty(null));
	}
	
	@SuppressWarnings("static-access")
	@Test
    public void isObjectEmpty3() {
		NotificationUtil notificationUtil = mock(NotificationUtil.class);
		assertFalse(notificationUtil.isObjectEmpty("Test"));
		assertTrue(notificationUtil.isObjectEmpty(new String("")));
		Set<String> items = Sets.newHashSet();
		assertTrue(notificationUtil.isObjectEmpty(items));
		Set<String> items1 = null;
		assertTrue(notificationUtil.isObjectEmpty(items1));
		items.add("addd");
		assertFalse(notificationUtil.isObjectEmpty(items));
		
		
	}

	/*@Test
	private void isCollectionEmpty() {
		boolean collectionEmpty=false;
	    if (collection == null || collection.isEmpty()) {
	        collectionEmpty= true;
		}
		return collectionEmpty;
	}*/
}
