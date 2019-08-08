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
package com.tmobile.pacman.commons.autofix;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author kkumar28
 *
 */
public class AutoFixPlan implements Serializable , PropertyChangeListener  {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<PlanItem> planItems;
    private String planId;
    private String ruleId;
    private String issueId;
    private String resourceId;
    private String docId; // system wide unique id of the resource
    private String resourceType;
    private Status planStatus;
    /*this property will be used to monitor the state of line items , when all the line items are completed, plan status 
     * should also update*/
    private transient final PropertyChangeSupport pcs; // transient is required else Gson will throw StackOverflow Exception while serializing the plan for posting

    /**
     * 
     */
    private AutoFixPlan() {
        super();
        pcs = new PropertyChangeSupport(this);
        this.pcs.addPropertyChangeListener("planItems", this);
        planItems = new ArrayList<>();
    }
    
    /**
     * 
     * @param planId
     * @param ruleId
     * @param issueId
     * @param resourceId
     */
    public AutoFixPlan(String ruleId, String issueId, String resourceId,String docId,String resourceType) {
        this();
        this.planId = issueId;
        this.ruleId = ruleId;
        this.issueId = issueId;
        this.resourceId = resourceId;
        this.docId=docId;
        this.resourceType=resourceType;
        this.planStatus = Status.SCHEDULED;
    }
    
    
    /**
     * 
     * @param index
     * @return
     */
    public Boolean markPlanItemStatus(Integer index,Status status){
       Optional<PlanItem> pi =  this.getPlanItems().stream().filter(item->item.getIndex().equals(index)).findFirst();
       if(pi.isPresent()) {
           pi.get().setStatus(status);
       }
       this.pcs.firePropertyChange("planItems", "", status);
       return Status.COMPLETED.equals(pi.get().getStatus());
       
    }
    
    /**
     * 
     * @param pi
     * @return
     */
    public Boolean addPlanItem(PlanItem pi){
        return planItems.add(pi);
    }

    public List<PlanItem> getPlanItems() {
        return planItems;
    }

    public String getPlanId() {
        return planId;
    }
    
    /**
     * 
     * @param index
     * @return
     */
    public PlanItem getPlanItemByIndex(Integer index){
        return planItems.get(index);
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getDocId() {
        return docId;
    }

    public Status getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(Status planStatus) {
        this.planStatus = planStatus;
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     * this method will set the final plan status if all the plan items are completed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        List<PlanItem> pItems = this.planItems.stream().filter(planItem->!planItem.getStatus().equals(Status.COMPLETED)).collect(Collectors.toList());
        if(!(pItems.size()>0)){
            this .planStatus=Status.COMPLETED;
    }

}
}
