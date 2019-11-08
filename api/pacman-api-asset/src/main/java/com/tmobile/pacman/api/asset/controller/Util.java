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
package com.tmobile.pacman.api.asset.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.Constants;

/**
 * This class contains common methods to be used across other classes 
 */
@Component
public class Util {

    private static final Log LOGGER = LogFactory.getLog(Util.class);
   
    @Autowired(required = true)
    private static AssetService assetService;
    
    /**
     * Sets the asset service.
     *
     * @param assetService the new asset service
     */
    @Autowired(required = true)
    public void setassetService(AssetService assetService) {
        Util.assetService = assetService;
    }

    /**
     * Checks if is valid target type.
     *
     * @param ag the ag
     * @param type the type
     * @return true, if is valid target type
     */
    public static boolean isValidTargetType(String ag, String type) {
        try {
            List<Map<String, Object>> targetTypes = assetService.getTargetTypesForAssetGroup(ag, null, null);
            return targetTypes.stream().filter(obj -> type.equals(obj.get("type"))).count() > 0 ? true : false;
        } catch (Exception e) {
            LOGGER.error("Error in isValidTargetType ",e);
            return false;
        }
    }

    /**
     * Checks if is valid asset group.
     *
     * @param ag the ag
     * @return true, if is valid asset group
     */
    public static boolean isValidAssetGroup(String ag) {
        List<Map<String, Object>> agList = assetService.getAllAssetGroups();
        return agList.stream().filter(obj -> ag.equals(obj.get("name"))).count() > 0 ? true : false;
    }

    /**
     * Gets the utilisation score.
     *
     * @param cpuUtilisation the cpu utilisation
     * @return the utilisation score
     */
    public static int getUtilisationScore(double cpuUtilisation) {

        return cpuUtilisation > AssetConstants.ZERO && cpuUtilisation <= Constants.FIVE ? Constants.ONE
                : cpuUtilisation > Constants.FIVE && cpuUtilisation <= Constants.TEN ? Constants.TWO
                        : cpuUtilisation > Constants.TEN && cpuUtilisation <= AssetConstants.FIFTEEN ? Constants.THREE
                                : cpuUtilisation > AssetConstants.FIFTEEN && cpuUtilisation <= AssetConstants.TWENTY ? Constants.FOUR
                                        : cpuUtilisation > AssetConstants.TWENTY_FIVE
                                                && cpuUtilisation <= AssetConstants.TWENTY_FIVE ? Constants.FIVE
                                                : cpuUtilisation > AssetConstants.TWENTY_FIVE
                                                        && cpuUtilisation <= AssetConstants.THIRTY ? Constants.SIX
                                                        : cpuUtilisation > AssetConstants.THIRTY
                                                                && cpuUtilisation <= AssetConstants.FORTY ? Constants.SEVEN
                                                                : cpuUtilisation > AssetConstants.FORTY
                                                                        && cpuUtilisation <= AssetConstants.FIFTY ? AssetConstants.EIGHT
                                                                        : cpuUtilisation > AssetConstants.FIFTY
                                                                                && cpuUtilisation <= AssetConstants.SIXTY ? AssetConstants.NINE
                                                                                : Constants.TEN;
    }

    /**
     * Encode url.
     *
     * @param toBeEncoded the to be encoded
     * @return the string
     */
    public static String encodeUrl(String toBeEncoded) {
        String encoded = toBeEncoded;
        try {
            encoded = URLEncoder.encode(toBeEncoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error in encodeUrl ", e);
        }
        return encoded;
    }

}
