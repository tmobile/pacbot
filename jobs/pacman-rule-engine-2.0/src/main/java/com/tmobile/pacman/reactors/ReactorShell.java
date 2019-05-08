/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :kkumar28
  Modified Date: Jan 16, 2019
  
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
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * @author kkumar28
 *
 */
public class ReactorShell {
    
    
    private static final Logger logger = LoggerFactory.getLogger(ReactorShell.class);

    
    private Object reactorObject;
    private Method reactMethod;
    private Method backupMethod;
    private String reactorClassName;
    private PacReactor annotation;
    
    /**
     * 
     * @param reactorObject
     * @param reactMethod
     * @param backupMethod
     */
    public ReactorShell(PacReactor annotation,Object reactorObject, Method reactMethod, Method backupMethod) {
        super();
        this.reactorObject = reactorObject;
        this.reactMethod = reactMethod;
        this.backupMethod = backupMethod;
        this.reactorClassName=reactorObject.getClass().getName();
        this.annotation=annotation;
    }
    
    /**
     * 
     * @param event
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public Reaction react(JsonObject event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        return (Reaction) reactMethod.invoke(reactorObject, event);
    }
    
    
    public Boolean backup(JsonObject event){
    
        try {
                return (Boolean)backupMethod.invoke(reactorObject, event);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.error("error caling backup method" ,e);
            return Boolean.FALSE;
        }
    }
    /**
     * 
     * @return
     */
    public String getReactorClassName() {
        return reactorClassName;
    }
    
    /**
     * 
     * @return
     */
    public String getReactorCategory(){
        return annotation.category();
    }

}
