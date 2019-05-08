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

package com.tmobile.pacman.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.dto.ResourceOwner;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ESUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ResourceOwnerService.
 *
 * @author kkumar
 */
public class ResourceOwnerService {

    /** The Constant DETAIL_FIELD_NAME. */
    private static final String DETAIL_FIELD_NAME = "detail";

    /** The Constant HEIMDALL_PORT. */
    private static final String HEIMDALL_PORT = "heimdall-port";

    /** The Constant PROTOCOL. */
    private static final String PROTOCOL = "http";

    /** The Constant EMAIL. */
    private static final String EMAIL = "email";

    /** The Constant USER. */
    private static final String USER = "user";

    /** The Constant HEIMDALL_HOST. */
    private static final String HEIMDALL_HOST = "heimdall-host";

    /** The Constant HEIMDALL_RESOURCE_INDEX. */
    private static final String HEIMDALL_RESOURCE_INDEX = "pacman-resource-claim";

    /**
     * Find resource owner by id and type.
     *
     * @param resourceId the resource id
     * @param serviceType the service type
     * @return the resource owner
     * @throws Exception the exception
     */
    public ResourceOwner findResourceOwnerByIdAndType(final String resourceId, final AWSService serviceType)
            throws Exception {
        return fetchAndBuildResourceOwnerDetails(resourceId);
    }

    /**
     * find the owner of the resource identified by resourceId.
     *
     * @param resourceId the resource id
     * @return the resource owner
     * @throws Exception the exception
     */
    private ResourceOwner fetchAndBuildResourceOwnerDetails(final String resourceId) throws Exception {
        String heimdallUrl = PROTOCOL + "://" + CommonUtils.getPropValue(HEIMDALL_HOST) + ":"
                + CommonUtils.getPropValue(HEIMDALL_PORT);
        ResourceOwner resourceOwner = new ResourceOwner();
        List<Map<String, String>> resourceDetails = new ArrayList<>();
        List<String> fields = Lists.newArrayList();
        fields.add(EMAIL);
        fields.add(USER);
        fields.add(DETAIL_FIELD_NAME);
        Map<String, Object> mustFilter = Maps.newHashMap();
        mustFilter.put(ESUtils.createKeyword("resourceid"), resourceId);
        HashMultimap<String, Object> shouldFilter = null;
        try{
        resourceDetails = ESUtils.getDataFromES(heimdallUrl, HEIMDALL_RESOURCE_INDEX, "",
                mustFilter, Maps.newHashMap(), shouldFilter, fields, 0, 10);
        
        if (resourceDetails.size() > 0) {
            resourceOwner.setEmailId(findEmail(resourceDetails));
            resourceOwner.setName(resourceDetails.get(0).get(USER));
        }
        }catch(Exception e){
        	resourceOwner.setEmailId(CommonUtils.getPropValue(PacmanSdkConstants.PACBOT_AUTOFIX_RESOURCE_OWNER_FALLBACK_MAIL));
        	resourceOwner.setName("Team");
        }
        return resourceOwner;
    }

    /**
     * Find email.
     *
     * @param resourceDetails the resource details
     * @return the string
     */
    private String findEmail(List<Map<String, String>> resourceDetails) {
        if (null != resourceDetails.get(0).get(EMAIL) && !"null".equals(resourceDetails.get(0).get(EMAIL))) {
            return resourceDetails.get(0).get(EMAIL);
        } else {
            // try to detect from ARN
            String arn = resourceDetails.get(0).get("detail.userIdentity.arn");
            return arn.substring(arn.indexOf("/") + 1);
        }
    }
}
