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
package com.tmobile.pacman.reactors.commons;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tmobile.pacman.common.PacmanSdkConstants;

/**
 * @author SGorle
 *
 */
public class ReactorCommonValues {
    public static String getAccountId(JsonObject event) {
        return event.get(PacmanSdkConstants.ACCOUNT).getAsString();
    }

    public static String getRegion(JsonObject event) {
        return event.get(PacmanSdkConstants.REGION).getAsString();
    }

    public static String getResourceID(JsonObject event) {
        JsonArray instancesList = event.get("detail").getAsJsonObject().get("responseElements").getAsJsonObject()
                .get("instancesSet").getAsJsonObject().get("items").getAsJsonArray();
        JsonObject firstInstance = instancesList.get(0).getAsJsonObject();
        return firstInstance.get("instanceId").getAsString();

    }

    /**
     * Gets the reactor info.
     *
     * @param resourceId the resource id
     * @param accountId the account id
     * @param region the region
     * @param reactorMessage the reactor message
     * @return reactorInfo
     */
    public static Map<String, String> getReactorInfo(String resourceId, String accountId, String region,
            String reactorMessage) {
        Map<String, String> reactionInfo = new HashMap<String, String>();
        reactionInfo.put("Message", "Run Instance/Start Instance event created in non-standandard region");
        reactionInfo.put(PacmanSdkConstants.ACCOUNT_ID, accountId);
        reactionInfo.put(PacmanSdkConstants.RESOURCE_ID, resourceId);
        reactionInfo.put(PacmanSdkConstants.REGION, region);
        return reactionInfo;
    }

    /**
     * @param event
     * @return eventName
     */
    public static String getEventName(JsonObject event) {
        return event.get("detail").getAsJsonObject().get("eventName").getAsString();
    }

    /**
     * @param event
     * @return resourceType
     */
    public static String getResourceType(JsonObject event) {
       String resourceType = event.get("source").getAsString().substring(4);
   return resourceType;
   
    }

    /**
     * @param event
     * @return roleName
     */
    public static String getRoleName(JsonObject event) {
        return(null!= event.get("detail").getAsJsonObject().get("requestParameters").getAsJsonObject().get("roleName"))?event.get("detail").getAsJsonObject().get("requestParameters").getAsJsonObject().get("roleName").getAsString():null;
    }

    /**
     * @param event
     * @return policyName
     */
    public static String getPolicyName(JsonObject event) {
        String PolicyName;
    if (  null!= event.get("detail").getAsJsonObject().get("requestParameters").getAsJsonObject().get("policyName")){
        PolicyName = event.get("detail").getAsJsonObject().get("requestParameters").getAsJsonObject().get("policyName").getAsString();
    }else{
      String   policyARN = event.get("detail").getAsJsonObject().get("requestParameters").getAsJsonObject().get("policyArn").getAsString();
      PolicyName=policyARN.substring(policyARN.lastIndexOf("/") + 1);
       
    }
    return PolicyName;
    }

    /**
     * @param policyName
     * @return
     */
    public static String getConfigValue(String configName) {
       //getConfiguration from Database;
         String dbquery = "SELECT * FROM pac_v2_reactors_configs WHERE configName='" + configName.trim()+ "'";
   //      List<Map<String, String>>configValue= RDSManager.executeQuery(dbquery);
         String confiValue= "";
         return confiValue;
    }

    /**
     * @param event
     * @return
     */
    public static String getUserName(JsonObject event) {
        return(null!= event.get("detail").getAsJsonObject().get("requestParameters").getAsJsonObject().get("userName"))?event.get("detail").getAsJsonObject().get("requestParameters").getAsJsonObject().get("userName").getAsString():null;
      
    }

    /**
     * @param event
     * @return CWRuleName
     */
    public static String getCloudWatchRuleName(JsonObject event) {
        return((null!= event.get("detail").getAsJsonObject().get("requestParameters").getAsJsonObject().get("name"))?event.get("detail").getAsJsonObject().get("requestParameters").getAsJsonObject().get("name").getAsString():null);  
    }

    /**
     * @param policyDocument
     * @return
     */
    public static Boolean checkValidJsonString(String policyDocument) {
         Gson gson = new Gson();
        try {
            gson.fromJson(policyDocument, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) { 
            return false;
        }
      
        
    
    }
}
