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

package com.tmobile.pacman.autofix.rds;

import java.util.Map;

import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.ModifyDBInstanceRequest;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.autofix.BaseFix;
import com.tmobile.pacman.commons.autofix.FixResult;

// TODO: Auto-generated Javadoc
/**
 * The Class RDSGlobalAccessAutoFix.
 *
 * @author kkumar
 */
public class RDSGlobalAccessAutoFix extends BaseFix {

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.commons.autofix.BaseFix#executeFix(java.util.Map,
     * java.util.Map, java.util.Map)
     */
    @Override
    public FixResult executeFix(Map<String, String> issue, Map<String, Object> clientMap,
            Map<String, String> ruleParams) {

        String dbInstanceIdentifier = issue.get(PacmanSdkConstants.RESOURCE_ID);
        AmazonRDS amazonRdsClient = (AmazonRDSClient) clientMap.get(PacmanSdkConstants.CLIENT);
        ModifyDBInstanceRequest modifyDBInstanceRequest = new ModifyDBInstanceRequest();
        modifyDBInstanceRequest.setDBInstanceIdentifier(dbInstanceIdentifier);
        modifyDBInstanceRequest.setApplyImmediately(Boolean.FALSE);
        modifyDBInstanceRequest.setPubliclyAccessible(Boolean.FALSE);
        amazonRdsClient.modifyDBInstance(modifyDBInstanceRequest);
        return new FixResult(PacmanSdkConstants.STATUS_SUCCESS_CODE, "the rds db instance is now fixed");

    }

    /*
     * (non-Javadoc)
     *
     * @see com.tmobile.pacman.commons.autofix.BaseFix#
     * backupExistingConfigForResource(java.lang.String, java.lang.String,
     * java.util.Map, java.util.Map)
     */
    @Override
    public boolean backupExistingConfigForResource(String resourceId, String resourceType,
            Map<String, Object> clientMap, Map<String, String> ruleParams,Map<String, String> issue) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

}
