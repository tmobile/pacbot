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

package com.tmobile.pacman.commons.autofix.manager;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.CollectionUtils;
import com.google.common.base.Strings;
import com.tmobile.pacman.common.AutoFixAction;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.integrations.slack.SlackMessageRelay;
import com.tmobile.pacman.util.CommonUtils;

import java.time.format.DateTimeFormatter;

// TODO: Auto-generated Javadoc
/**
 * The Class NextStepManager.
 *
 * @author kkumar
 */
public class NextStepManager {

    /** The tagging manager. */
    ResourceTaggingManager taggingManager;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(NextStepManager.class);
    
    
    /** API response date time format **/
    
    private static final String DATE_TIME_FORMAT="yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * Instantiates a new next step manager.
     */
    public NextStepManager() {

        taggingManager = new ResourceTaggingManager();
    }

    /**
     * Gets the next step.
     *
     * @param ruleId the rule id
     * @param resourceId the resource id
     * @param resourceId 
     * @param clientMap the client map
     * @param serviceType the service type
     * @return the next step
     */
    @SuppressWarnings("unchecked")
    public AutoFixAction getNextStep(Map<String, String> ruleParam , String normalizedResourceId,  String resourceId, Map<String, Object> clientMap, 
            AWSService serviceType) {

        
        String ruleId = ruleParam.get(PacmanSdkConstants.RULE_ID);
        
        try {
            
           //silent fix can only be aplied to tagging rules , where exception does not makes much sense 
           if(isSilentFixEnabledForRule(ruleId)){
                return AutoFixAction.AUTOFIX_ACTION_FIX;
            }
            // if the resource was ever exempted we will send mail to CSR and
            // Exception Owner
            if (isServiceTaggable(serviceType) && null != wasResourceEverExempted(normalizedResourceId, clientMap, serviceType)) {
                return AutoFixAction.AUTOFIX_ACTION_EMAIL_REMIND_EXCEPTION_EXPIRY;
            }
            String url = CommonUtils.getPropValue(PacmanSdkConstants.RESOURCE_GET_LASTACTION);
            url = url.concat("?resourceId=").concat(resourceId);
            String response;
            try{
                response = CommonUtils.doHttpGet(url);
            }catch (Exception e) {
                // this is an api failure
                logger.error("uable to call API",e);
                new SlackMessageRelay().sendMessage(CommonUtils.getPropValue(PacmanSdkConstants.AUTH_API_OWNER_SLACK_HANDLE), e.getMessage());
                return AutoFixAction.UNABLE_TO_DETERMINE; 
            }
            Map<String, Object> resourceDetailsMap = (Map<String, Object>) CommonUtils.deSerializeToObject(response);
            Double responseCode = Double.valueOf((resourceDetailsMap.get("responseCode").toString()));
            int autoFixDelay = getAutoFixDelay(ruleId);
            int maxEmails = getMaxNotifications(ruleId);
           
            List<String> lastActions = (List<String>) resourceDetailsMap.get("lastActions");
            
            if(CollectionUtils.isNullOrEmpty(lastActions)){
                    //no action taken yet, and silent fix is not enabled , first action should be email
                    return AutoFixAction.AUTOFIX_ACTION_EMAIL;
            }else{
                    Collections.sort(lastActions);//sort based on date and find the first action time
                    //LocalDateTime lastActionTime =  LocalDateTime.parse(lastActions.get(lastActions.size() - 1), DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
                    LocalDateTime firstActionTime =  LocalDateTime.parse(lastActions.get(0), DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
                    LocalDateTime currentTime = LocalDateTime.now();
                    long elapsedHours = ChronoUnit.HOURS.between(firstActionTime, currentTime);

                    if (lastActions.size() >= maxEmails) {
                        
                        if (elapsedHours >= autoFixDelay) {
                            return AutoFixAction.AUTOFIX_ACTION_FIX;
                        } else {
                            return AutoFixAction.DO_NOTHING;
                        }
                    }else{
                        long nextActionTime = getNextActionTime(maxEmails,autoFixDelay,lastActions.size());
                        if(elapsedHours>=nextActionTime){
                            return AutoFixAction.AUTOFIX_ACTION_EMAIL;
                        }else{
                                return AutoFixAction.DO_NOTHING;
                        }
                    }
                }
        } catch (Exception exception) {
            logger.error("Exception in getNextStep:" + exception.getMessage());
            return AutoFixAction.UNABLE_TO_DETERMINE; 
        }
    }
    
    
    /**
     * default or rule specific # of notifications
     * @param ruleId
     * @return
     */
    public static int getMaxNotifications(String ruleId) {

        String ruleSpecificValue = CommonUtils.getPropValue(PacmanSdkConstants.AUTOFIX_MAX_EMAILS + "." + ruleId);
        if(Strings.isNullOrEmpty(ruleSpecificValue)){
            return Integer.parseInt(
                    CommonUtils.getPropValue(PacmanSdkConstants.AUTOFIX_MAX_EMAILS + "." + PacmanSdkConstants.PAC_DEFAULT));
        }else{
            return Integer.parseInt(ruleSpecificValue);
            }
        }

    /**
     * calculates the next action time based on actions already taken
     * @param maxEmails
     * @param autoFixDelay
     * @param size
     * @return
     */
    private long getNextActionTime(int maxEmails, long autoFixDelay, int noOfActionsTakenAlready) {
        if(noOfActionsTakenAlready>=maxEmails){
            return -1;
        }
        int interval = Math.toIntExact(autoFixDelay/maxEmails);
        ArrayList<Integer> intervals= new ArrayList<>();
        int index = 0;
        while(index<(autoFixDelay/interval)){
            intervals.add(index*interval);
            index++;
        }
        return intervals.get(noOfActionsTakenAlready);
    }

    /**
     * @param ruleId 
     * @return
     */
    public static int getAutoFixDelay(String ruleId) {
        Integer delay = 24;// to be safe this is initialized with 24 and not 0 , though this will be overridden by config property
        try{
            String delayForRule = CommonUtils.getPropValue(new StringBuilder(PacmanSdkConstants.PAC_AUTO_FIX_DELAY_KEY).append(".").append(ruleId).toString());
            if(Strings.isNullOrEmpty(delayForRule)){
                //get default delay
                delayForRule =  CommonUtils.getPropValue(new StringBuilder(PacmanSdkConstants.PAC_AUTO_FIX_DELAY_KEY).append(".").append(PacmanSdkConstants.PAC_DEFAULT).toString());
            }
                delay =  Integer.parseInt(delayForRule);
        }catch (NumberFormatException nfe) {
            logger.error("unable to find delay param will not execute fix");
            throw nfe;
        }
        return delay;
    }

    /**
     * Checks if is silent fix enabled for rule.
     *
     * @param ruleId the rule id
     * @return true, if is silent fix enabled for rule
     */
    public boolean isSilentFixEnabledForRule(String ruleId) {
        String fixType = CommonUtils.getPropValue(PacmanSdkConstants.AUTO_FIX_TYPE + "." +ruleId );
        return !Strings.isNullOrEmpty(fixType) && PacmanSdkConstants.AUTO_FIX_TYPE_SILENT.equals(fixType);
    }

    /**
     * Checks if is service taggable.
     *
     * @param serviceType the service type
     * @return true, if is service taggable
     */
    private boolean isServiceTaggable(AWSService serviceType) {
        try {
            List<String> nonTaggableServices = Arrays.asList(
                    CommonUtils.getPropValue(PacmanSdkConstants.PAC_AUTO_TAG_NON_TAGGABLE_SERVICES).split("\\s*,\\s*"));
            return !nonTaggableServices.contains(serviceType.toString());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Was resource ever exempted.
     *
     * @param resourceId the resource id
     * @param clientMap the client map
     * @param serviceType the service type
     * @return the string
     */
    private String wasResourceEverExempted(String resourceId, Map<String, Object> clientMap, AWSService serviceType) {

        String exceptionExpiry = taggingManager.getPacmanTagValue(resourceId, clientMap, serviceType);
        if (!Strings.isNullOrEmpty(exceptionExpiry)) {
            // dcrypt tag value
            try {
                exceptionExpiry = CommonUtils.decryptB64(exceptionExpiry);
                return exceptionExpiry;
            } catch (IOException e) {
                logger.error("error decrypting pacman tag value", e);
                return null;
            }

        } else {
            return null;
        }
    }

    /**
     * Post fix action.
     *
     * @param resourceId the resource id
     * @param action the action
     * @throws Exception the exception
     */
    public void postFixAction(final String resourceId, final AutoFixAction action) throws Exception {
        try {
            String url = CommonUtils
                    .getPropValue(PacmanSdkConstants.RESOURCE_POST_LASTACTION);
            url = url.concat("?resourceId=").concat(resourceId).concat("&action=").concat(action.toString());
            CommonUtils.doHttpPost(url, "", new HashMap<>());
        } catch (Exception exception) {
            logger.error("Exception in getNextStep:" + exception.getMessage(), exception);
            throw exception;
        }
    }
    
    /**
     * Sets the tagging manager.
     *
     * @param taggingManager the new tagging manager
     */
    public void setTaggingManager(ResourceTaggingManager taggingManager) {
        this.taggingManager = taggingManager;
    }

    /**
     * @return
     */
    public long getAutoFixExpirationTimeInHours(String ruleId,String resourceId) {
        String url = CommonUtils.getPropValue(PacmanSdkConstants.RESOURCE_GET_LASTACTION);
        url = url.concat("?resourceId=").concat(resourceId);
        String response=null;
        try{
            response = CommonUtils.doHttpGet(url);
        }catch (Exception e) {
            // this is an api failure
            logger.error("uable to call API",e);
                   }
        Map<String, Object> resourceDetailsMap = (Map<String, Object>) CommonUtils.deSerializeToObject(response);
        Double responseCode = Double.valueOf((resourceDetailsMap.get("responseCode").toString()));
        long autoFixDelay = getAutoFixDelay(ruleId);
        List<String> lastActions = (List<String>) resourceDetailsMap.get("lastActions");
        Collections.sort(lastActions);//sort based on date and find the first action time
        long elapsedHours=0l;
        if(lastActions.size()>0){
            LocalDateTime firstActionTime =  LocalDateTime.parse(lastActions.get(0), DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    
        LocalDateTime currentTime = LocalDateTime.now();
        elapsedHours = ChronoUnit.HOURS.between(firstActionTime, currentTime);
        }
        if(lastActions.size()>0&autoFixDelay>=elapsedHours){
            return autoFixDelay-elapsedHours;
        }else if(lastActions.size()==0){
            return autoFixDelay;
        }else return 0;
               
    }



//    public static void main(String[] args) {
//        int totalActions=2;
//        for(int noOfActionsAlreadyTaken=0;noOfActionsAlreadyTaken<=totalActions;noOfActionsAlreadyTaken++){
//            System.out.println("*******"+new NextStepManager().getNextActionTime(totalActions, 24, noOfActionsAlreadyTaken));
//        }
//    }
    
    
    
}
