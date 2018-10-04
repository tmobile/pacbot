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
package com.tmobile.cloud.awsrules.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysResult;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacmanUtils.class})
public class IAMUtilsTest {

    @InjectMocks
    IAMUtils iamUtils;
    
    @Mock
    AmazonIdentityManagementClient iamClient;
    
    @Before
    public void setUp() throws Exception{
        iamClient = PowerMockito.mock(AmazonIdentityManagementClient.class); 
    }
 
    @SuppressWarnings("static-access")
    @Test
    public void getAccessKeyInformationForUserTest() throws Exception {
        
        ListAccessKeysResult keysResult = new ListAccessKeysResult();
        keysResult.setIsTruncated(false);
        
        when(iamClient.listAccessKeys(anyObject())).thenReturn(keysResult);
        assertThat(iamUtils.getAccessKeyInformationForUser("user",iamClient),is(notNullValue()));
        
    }
    
}
