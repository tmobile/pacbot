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

import java.io.Serializable;

import com.tmobile.pacman.common.AutoFixAction;

/**
 * @author kkumar28
 *
 */
public class PlanItem implements Serializable {

    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    Integer index;
    private AutoFixAction action;
    private String plannedActionTime;
    private Status status;
    private String actualActiontime;
    
    /**
     * 
     */
    public PlanItem() {
    }
    
    /**
     * 
     * @param action
     * @param triggerTime
     */
    public PlanItem(Integer index ,AutoFixAction action, String plannedActionTime,Status status) {
        super();
        this.index = index;
        this.action = action;
        this.plannedActionTime = plannedActionTime;
        this.status=status;
    }

    /**
     * 
     * @return
     */
    public AutoFixAction getAction() {
        return action;
    }
    /**
     * 
     * @param action
     */
    public void setAction(AutoFixAction action) {
        this.action = action;
    }
    

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getIndex() {
        return index;
    }

    public String getPlannedActionTime() {
        return plannedActionTime;
    }

    public void setPlannedActionTime(String plannedActionTime) {
        this.plannedActionTime = plannedActionTime;
    }

    public String getActualActiontime() {
        return actualActiontime;
    }

    public void setActualActiontime(String actualActiontime) {
        this.actualActiontime = actualActiontime;
    }
    
}
