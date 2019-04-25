/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :kkumar28
  Modified Date: Dec 24, 2018
  
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

import java.util.HashMap;
import java.util.Map;

import com.tmobile.pacman.reactors.commons.PacEventStatus;

/**
 * @author kkumar28
 *
 */
public class Reaction {

    
    private String reactorName;
    
    
    /**
     * 
     */
    private PacEventStatus statusCode;
    
    /**
     * 
     */
    private Map<String,String> additionalInfo;

    /**
     *     
     * @param statusCode
     * @param additionalInfo
     */
    public Reaction(PacEventStatus statusCode, Map<String, String> additionalInfo) {
        super();
        this.statusCode = statusCode;
        this.additionalInfo = additionalInfo;
    }
    
    /**
     *     
     * @param statusCode
     * @param additionalInfo
     */
    public Reaction(String reactorName,PacEventStatus statusCode, Map<String, String> additionalInfo) {
        super();
        this.reactorName=reactorName;
        this.statusCode = statusCode;
        this.additionalInfo = additionalInfo;
    }
    
    /**
     *     
     * @param statusCode
     */
    public Reaction(PacEventStatus statusCode) {
        this(statusCode,new HashMap<>());
    }
    
    /**
     * 
     * @return
     */
    public PacEventStatus getStatusCode() {
        return statusCode;
    }

    /**
     * 
     * @return
     */
    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new StringBuilder(statusCode.toString()).toString();
    }

    public String getReactorName() {
        return reactorName;
    }

    public void setReactorName(String reactorName) {
        this.reactorName = reactorName;
    }
    
    
    
}
