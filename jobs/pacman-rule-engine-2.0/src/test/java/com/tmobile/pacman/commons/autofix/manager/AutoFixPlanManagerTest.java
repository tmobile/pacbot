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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.pacman.common.AutoFixAction;
import com.tmobile.pacman.commons.autofix.AutoFixPlan;
import com.tmobile.pacman.commons.autofix.PlanItem;

/**
 * @author kkumar28
 *
 */

@RunWith(PowerMockRunner.class)
public class AutoFixPlanManagerTest {
    
    
    final static Integer numberOfNotifications = 4;
    
    @Test
    public void testcreateTransientPlanNotNull(){
        AutoFixPlan plan = new AutoFixPlanManager().createPlan( "ruleId", "issueId", "resourceId","docId","resourcetype", numberOfNotifications, 48);
        assertNotNull(plan);
    }
    
    
    @Test
    public void testcreateTransientPlanVerify(){
        
        AutoFixPlan plan = new AutoFixPlanManager().createPlan( "ruleId", "issueId", "resourceId","docId","resourcetype", numberOfNotifications, 48);
        assertEquals(numberOfNotifications.intValue(),plan.getPlanItems().size() - 1); // reducing by one as one of them will be action and not notification signal
    }
    
    @Test
    public void testcreateTransientPlanVerifyAction(){
        AutoFixPlan plan = new AutoFixPlanManager().createPlan( "ruleId", "issueId", "resourceId","docId","resourcetype", numberOfNotifications, 48);
        List<PlanItem> items = plan.getPlanItems().stream().filter(obj->obj.getAction().equals(AutoFixAction.AUTOFIX_ACTION_FIX)).collect(Collectors.toList());
        assertTrue(items.get(0).getAction().equals(AutoFixAction.AUTOFIX_ACTION_FIX));
    }
    
    
}
