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

package com.tmobile.pacman.common.exception;

// TODO: Auto-generated Javadoc
/**
 * The Class RuleEngineRunTImeException.
 *
 * @author kkumar
 */
public class RuleEngineRunTimeException extends RuntimeException {


    /** The status code. */
    protected final int statusCode;

    /** The msg. */
    protected final String msg;


    /**
     * Instantiates a new rule engine run T ime exception.
     *
     * @param e the e
     */
    public RuleEngineRunTimeException(Exception e) {
        super(e);
        this.statusCode=-1;
        this.msg=e.getMessage();
    }

    /**
     * Instantiates a new rule engine run T ime exception.
     *
     * @param statusCode the status code
     * @param msg the msg
     */
    public RuleEngineRunTimeException(int statusCode,String msg) {
        this.statusCode=statusCode;
        this.msg=msg;
    }


    /**
     * Gets the status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return msg;
    }

}
