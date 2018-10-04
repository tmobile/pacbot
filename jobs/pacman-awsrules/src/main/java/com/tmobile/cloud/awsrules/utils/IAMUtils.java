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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysRequest;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysResult;

public class IAMUtils {
    
    private static final Logger logger = LoggerFactory
            .getLogger(IAMUtils.class);

    private IAMUtils() {

    }

    /**
     * This method will fetch the access key information of a particular user.
     * 
     * @param userName
     * @param iamClient
     * @return list of access key meta data
     */
    public static List<AccessKeyMetadata> getAccessKeyInformationForUser(
            final String userName, AmazonIdentityManagementClient iamClient) {
        ListAccessKeysRequest accessKeysRequest = new ListAccessKeysRequest();
        accessKeysRequest.setUserName(userName);
        logger.debug("userName {} ",userName);
        List<AccessKeyMetadata> accessKeyMetadatas = new ArrayList<>();
        ListAccessKeysResult keysResult = null;
        do {
            keysResult = iamClient.listAccessKeys(accessKeysRequest);
            accessKeyMetadatas.addAll(keysResult.getAccessKeyMetadata());
            accessKeysRequest.setMarker(keysResult.getMarker());
        } while (keysResult.isTruncated());

        return accessKeyMetadatas;
    }
}
