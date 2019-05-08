/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :kkumar28
  Modified Date: Dec 26, 2018
  
**/
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
package com.tmobile.pacman.reactors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.integrations.slack.SlackMessageRelay;
import com.tmobile.pacman.reactors.commons.PacEventStatus;
import com.tmobile.pacman.reactors.commons.ReactorCommonValues;
import com.tmobile.pacman.reactors.dto.PacEvent;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ESUtils;
import com.tmobile.pacman.util.ReflectionUtils;



/**
 * @author kkumar28
 *
 */
public class PacEventHandler {
    
    
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(PacEventHandler.class);
    
    private SlackMessageRelay slack;

    /**
     * @param args 
     * @return true if invocation source is an event
     */
    public static Boolean isInvocationSourceAnEvent(String eventData) {
        try{
                Gson gson = new GsonBuilder().create();
                PacEvent event = gson.fromJson(eventData, PacEvent.class);
                return !Strings.isNullOrEmpty(event.getEventName());
        }catch(Exception e){
            return Boolean.FALSE;
        }
    }

    /**
     * @param args
     */
    public void handleEvent(String eventId,String eventData) {
        
        Gson gson = new GsonBuilder().create();
        PacEvent event = gson.fromJson(eventData, PacEvent.class);
        Map<String,Object> eventActionLog = new HashMap<>();
        final String jobId = CommonUtils.getEnvVariableValue(PacmanSdkConstants.JOB_ID);// this is applicable when the job is running in aws batch
      //register event
        registerEvent(eventId, event);
        
        
        List<Reaction> reactions = new ArrayList<>();
        String message = new StringBuilder("received event -> ") .append(event.getEventName()).append(" \n event id assigned --> ").append(eventId).append(" \n processing event using job id -->").append(jobId).toString();
        logger.info(message);
        slack = new SlackMessageRelay();
        slack.sendMessage(CommonUtils.getPropValue(PacmanSdkConstants.SQUARE_ONE_SLACK_CHANNEL), message);
        
        String eventName = event.getEventName();
        //find reactor matching event name
        Set<ReactorShell> allMatchingReactors = ReflectionUtils.findEventHandlers(eventName);
        eventActionLog.put("totalMatchingReactors", allMatchingReactors.size());
        if(!Strings.isNullOrEmpty(jobId)){
            eventActionLog.put("assignedJobId", jobId);
        }
        
        //remove all the reactors which is not white labeled for account number found in event
        
        allMatchingReactors.removeIf(obj->!IswhiteLabelledForAccount(obj.getReactorClassName(),event.getAccountId()));
        
        if(!(allMatchingReactors.size()>0)){
            String msg = "event cannot be processed, found no reactor whitelisted for this account " + event.getAccountId();
            logger.info(msg);
            eventActionLog.put("reason", msg);
            updateEventStatus(eventId, PacEventStatus.WILL_NOT_PROCESS,eventActionLog);
            return;
        }
        
        String resourceType = ReactorCommonValues.getResourceType(event.getEventData());
        eventActionLog.put("resourceType", resourceType);
        //call all reactor methods 
        allMatchingReactors.parallelStream().forEach(reactorShell->{
            try {
                    if((Boolean)reactorShell.backup(event.getEventData())){
                        Reaction reaction = reactorShell.react(event.getEventData());
                        reaction.getAdditionalInfo().put(PacmanSdkConstants.REACTOR_CATEGORY, reactorShell.getReactorCategory());
                        reaction.setReactorName(reactorShell.getReactorClassName());
                        reactions.add(reaction);
                    }else{
                        HashMap<String,String> additionalInfo = new HashMap<>();
                        additionalInfo.put("reason", "backup method returned false");
                        reactions.add(new Reaction(reactorShell.getReactorClassName(),PacEventStatus.WILL_NOT_PROCESS,additionalInfo));
                    }
            } catch (IllegalAccessException e) {
                logger.error("error while invoking reactors",e);
            } catch (IllegalArgumentException e) {
                logger.error("error while invoking reactors",e);
            } catch (InvocationTargetException e) {
                logger.error("error while invoking reactors",e);
            }
        });
        List<String> resourceIds =   processReactions(reactions,eventActionLog);
        String resources = "";
        Iterables.removeIf(resourceIds, Predicates.isNull()); // remove all nulls
        if(resourceIds!=null&&resourceIds.size()>0){
            resources = Joiner.on(",").join(resourceIds);
        }
        //update event status as processed
        updateEventStatus(eventId, PacEventStatus.PROCESSED,eventActionLog);
        slack.sendMessage(CommonUtils.getPropValue(PacmanSdkConstants.SQUARE_ONE_SLACK_CHANNEL), "event with id " + eventId + " processed successfully for resourceId/Ids : "+ resources + "  \n check reactors log for more details..! ");
    }
    
   

   

    /**
     * @param reactorClassName
     * @param accountId
     * @return
     */
    private Boolean IswhiteLabelledForAccount(String reactorClassName, String accountId) {
        String whiteListAccountIds = CommonUtils.getPropValue(reactorClassName+  PacmanSdkConstants.WHITELIST);
        List<String> whiteListAccountList;
        if(!Strings.isNullOrEmpty(whiteListAccountIds)){
            whiteListAccountList = Arrays.asList(whiteListAccountIds.split("\\s*,\\s*"));
            return whiteListAccountList.contains(accountId);
        }
        logger.info( reactorClassName +" reactor is not white labelle  for " + accountId);
        logger.info( reactorClassName + " will not be fired");
        return false;
    }

    /**
     * @param reactions
     * @param eventActionLog
     */
    private List<String> processReactions(List<Reaction> reactions, Map<String, Object> eventActionLog) {
        eventActionLog.put("reactorsLog", reactions);
        List<String> resourceIds = 
        reactions.stream().map(reaction->reaction.getAdditionalInfo().get(PacmanSdkConstants.RESOURCE_ID)).collect(Collectors.toList());
        return resourceIds;
    }

    /**
     * registers the event to an elastic search index named pac-reactors under the type event_log
     * @param resourceId
     * @param eventData
     * @return
     */
    private Boolean registerEvent(String eventId , PacEvent event){
        String indexName = CommonUtils.getPropValue(PacmanSdkConstants.EVENTS_INDEX_NAME_KEY);// "fre-stats";
        Map<String,Object> eventDoc = new HashMap<>();
        Gson gson = new GsonBuilder().create();
        eventDoc.put(PacmanSdkConstants.EVENT_ID, eventId);
        eventDoc.put(PacmanSdkConstants.STATUS_KEY, PacEventStatus.PROCESSING);
        Map<String, String> eventDataMap = gson.fromJson(event.getEventData().toString(), HashMap.class);
        eventDoc.put(PacmanSdkConstants.EVENT_DATA_KEY, eventDataMap);
        eventDoc.put(PacmanSdkConstants.EVENT_NAME, event.getEventName());
        eventDoc.put(PacmanSdkConstants.EXECUTION_ID, eventId);
        eventDoc.put(PacmanSdkConstants.EVENT_RECEIVE_TIME, CommonUtils.getCurrentDateStringWithFormat(PacmanSdkConstants.PAC_TIME_ZONE,
                PacmanSdkConstants.DATE_FORMAT));
        return ESUtils.doESPublish(eventDoc, indexName, CommonUtils.getPropValue(PacmanSdkConstants.EVENTS_REGISTRY_KEY));
    }
    
    /**
     * 
     * @param eventId
     * @param eventStatus
     * @return
     */
    private Boolean updateEventStatus(String eventId,PacEventStatus eventStatus, Map<String,Object> eventActionLog){
        String indexName = CommonUtils.getPropValue(PacmanSdkConstants.EVENTS_INDEX_NAME_KEY);
        Map<String,Object> eventDoc = new HashMap<>();
        Map<String,Object> partialDoc = new HashMap<>();
        partialDoc.put(PacmanSdkConstants.STATUS_KEY, eventStatus.toString());
        partialDoc.put(PacmanSdkConstants.EVENT_PROCESSED_TIME, CommonUtils.getCurrentDateStringWithFormat(PacmanSdkConstants.PAC_TIME_ZONE,
                PacmanSdkConstants.DATE_FORMAT));
        partialDoc.put("eventActionLog", eventActionLog);
        eventDoc.put("doc",partialDoc);
        return ESUtils.doESUpdate(eventId,eventDoc, indexName, CommonUtils.getPropValue(PacmanSdkConstants.EVENTS_REGISTRY_KEY));
    }
    
    
    /****/
    public static void main(String[] args) {
        String eventData= "{\"eventName\":\"runInstance\",\"eventData\":{\"a\":\"b\"}}";
       new PacEventHandler().handleEvent(UUID.randomUUID().toString(), eventData);
       
    }

}
