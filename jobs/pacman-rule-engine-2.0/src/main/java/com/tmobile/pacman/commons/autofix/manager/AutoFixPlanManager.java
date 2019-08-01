/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :kkumar28
  Modified Date: Jun 19, 2019
  
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
package com.tmobile.pacman.commons.autofix.manager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.tmobile.pacman.common.AutoFixAction;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.autofix.AutoFixPlan;
import com.tmobile.pacman.commons.autofix.PlanItem;
import com.tmobile.pacman.commons.autofix.Status;
import com.tmobile.pacman.publisher.impl.ElasticSearchDataPublisher;
import com.tmobile.pacman.publisher.impl.ElasticSearchDataReader;
import com.tmobile.pacman.util.CommonUtils;

/**
 * @author kkumar28
 * This class will be used to manage auto fix plan
 * A typical plan looks like a series of notifications followed by an auto fix action
 */
public class AutoFixPlanManager{

    
    /**
     * 
     */
    private static final String AUTO_FIX_PLAN_TYPE = "autofixplan";
    /**
     * 
     */
    private static final Logger logger = LoggerFactory.getLogger(AutoFixPlanManager.class);
    
    
    
    private ElasticSearchDataPublisher dp;
    
    private ElasticSearchDataReader dr;
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        dp.close();
        super.finalize();
    }
    
    /**
     * 
     * @param ruleId
     * @param issueId
     * @param resourceId : id of the resource as specified in cloud
     * @param docId id of the resource as specified in pacbot es index
     * @param resourceType
     * @param numberOfNotificatons
     * @param waitTimeBeforeAutoFix
     * @return
     */
    public AutoFixPlan createPlan(String ruleId, String issueId, String resourceId,String docId, String resourceType,int numberOfNotificatons, int waitTimeBeforeAutoFix)
    {
        AutoFixPlan autoFixPlan = createTransientPlan(ruleId,  issueId,  resourceId,docId,resourceType,numberOfNotificatons, waitTimeBeforeAutoFix);
        return autoFixPlan;
    }
    
    
    /**
     *     
     * @param numberOfNotificatons
     * @param waitTimeBeforeAutoFix
     * @return AutoFixPlan
     */
    public AutoFixPlan createTransientPlan(String ruleId, String issueId, String resourceId,String docId,String resourceType,int numberOfNotificatons, int waitTimeBeforeAutoFix)
    {
        AutoFixPlan autoFixPlan = new AutoFixPlan(ruleId, issueId, resourceId,docId,resourceType);
        IntStream.range(0, numberOfNotificatons).forEach(
                index->{
                    autoFixPlan.addPlanItem(new PlanItem(index,AutoFixAction.AUTOFIX_ACTION_EMAIL,new DateTime().plusHours(index*(waitTimeBeforeAutoFix/numberOfNotificatons)).toDateTimeISO().toString(),Status.SCHEDULED));
                }
                );  
        autoFixPlan.addPlanItem(new PlanItem(numberOfNotificatons,AutoFixAction.AUTOFIX_ACTION_FIX, new DateTime().plusHours(numberOfNotificatons*(waitTimeBeforeAutoFix/numberOfNotificatons)).toDateTimeISO().toString(),Status.SCHEDULED));
        return autoFixPlan;
    }
    
     /**
      * 
      * @param plan
     * @throws MalformedURLException 
      */
     public void publishPlan(Map<String, String> ruleParam,AutoFixPlan plan) throws MalformedURLException{
         if(dp==null){
             dp = new ElasticSearchDataPublisher();
         }
         dp.postDocAsChildOfType(plan, plan.getPlanId(), ruleParam, AUTO_FIX_PLAN_TYPE, plan.getResourceType(), plan.getDocId());
     }
    
     /**
      * 
      * @param planId
      * @param itemIndex
     * @throws Exception 
      */
     public void updatePlanItemStatusAndPublish(String planId,int itemIndex,Map<String, String> ruleParam , String parentId, Status status) throws Exception{
         AutoFixPlan plan = getAutoFixPlan(CommonUtils.getIndexNameFromRuleParam(ruleParam), AUTO_FIX_PLAN_TYPE, planId,parentId);
       //1: getPlan()
       //2: Update item at itemIndex
         plan.markPlanItemStatus(itemIndex,status);
       // publish plan again
         publishPlan(ruleParam, plan);
     }
     
     
     /**
      * 
      * @param planId
      * @param itemIndex
     * @throws Exception 
      */
     public AutoFixPlan updatePlanItemStatus(AutoFixPlan plan,Integer itemIndex,Status status){
         plan.markPlanItemStatus(itemIndex,status);
         return plan;
     }
     
     
     /**
      * 
      * @param resourceId
      * @return
     * @throws Exception 
     * @throws IOException 
      */
     public AutoFixPlan getAutoFixPalnForResource(String resourceId,Map<String, String> ruleParam) throws IOException, Exception{
         AutoFixPlan plan =  getAutoFixPlan(resourceId,ElasticSearchDataPublisher.getIndexName(ruleParam));
         return plan;
     }
     
     
     /**
     * @param resourceId
     * @param indexNameFromRuleParam
     * @return
     * @throws Exception 
     * @throws IOException 
     */
    private AutoFixPlan getAutoFixPlan(String resourceId, String indexNameFromRuleParam) throws IOException, Exception {
        
        if(dr==null) {
            dr = new ElasticSearchDataReader();
        }
        String doc =dr.searchDocument(indexNameFromRuleParam,AUTO_FIX_PLAN_TYPE,resourceId);
        Gson gson = new Gson();
        return gson.fromJson(doc, AutoFixPlan.class);
    }


    /**
      * 
      * @param index
      * @param type
      * @param planId
      * @param parentId
      * @return
     * @throws Exception 
      */
     private AutoFixPlan getAutoFixPlan(String index, String type, String planId , String parentId) throws Exception{
         String doc = new ElasticSearchDataReader().getDocumentById(index, type, planId,parentId);
         Gson gson = new Gson();
         return gson.fromJson(doc, AutoFixPlan.class);
     }
     
     
     /**
      * 
      * @param resourceId
      * @return
     * @throws Exception 
     * @throws IOException 
      */
     private AutoFixPlan synchronizeAutoFixPlan(AutoFixPlan plan,String resourceId,Map<String, String> ruleParam) throws IOException, Exception{
         
         String url = CommonUtils.getPropValue(PacmanSdkConstants.RESOURCE_GET_LASTACTION);
         url = url.concat("?resourceId=").concat(resourceId);
         String response=null;
         try{
             response = CommonUtils.doHttpGet(url);
         }catch (Exception e) {
             logger.error("uable to call API",e);
             throw e;
                    }
         Map<String, Object> resourceDetailsMap = (Map<String, Object>) CommonUtils.deSerializeToObject(response);
         List<String> lastActions = (List<String>) resourceDetailsMap.get("lastActions");
         
         // update the plan
         int index = 0;
         for(String action : lastActions){
             updatePlanItemStatus(plan, index++, Status.COMPLETED);
         }
         // return updated plan
        return plan;
     }
     
     /**
      * @param resourceId
      * @param ruleParam
     * @throws Exception 
     * @throws IOException 
      */
     public void suspendPlan(AutoFixPlan plan, Map<String, String> ruleParam) throws IOException, Exception {
         plan.setPlanStatus(Status.SUSPENDED);
         publishPlan(ruleParam, plan);
     }
     
     /**
      * 
      * @param resourceId
      * @return
     * @throws Exception 
     * @throws IOException 
      */
     public void synchronizeAndRepublishAutoFixPlan(AutoFixPlan plan,String resourceId , Map<String, String> ruleParam) throws IOException, Exception{
         if(null==plan)
         {
             String msg = "null plan";
             logger.error(msg);
             throw new Exception(msg);
         }
         AutoFixPlan syncPlan = synchronizeAutoFixPlan(plan,resourceId,ruleParam);
         if(syncPlan!=null) {
             publishPlan(ruleParam, syncPlan);
         }
     }
     
     
     public void releaseResourfes(){
         if(this.dp!=null) dp.close();
         if(this.dr!=null) dr.close();
     }
}
