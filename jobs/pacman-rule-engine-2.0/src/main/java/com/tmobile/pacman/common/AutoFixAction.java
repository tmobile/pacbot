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

package com.tmobile.pacman.common;

// TODO: Auto-generated Javadoc
/**
 * The Enum AutoFixAction.
 *
 * @author kkumar
 */
public enum AutoFixAction {

    /** The email. */
    EMAIL("email"),
 /** The autofix action email. */
 AUTOFIX_ACTION_EMAIL("autofixEmail"),
 /** The autofix action fix. */
 AUTOFIX_ACTION_FIX("autofixAction"),
 /** The autofix action tag. */
 AUTOFIX_ACTION_TAG(
            "autofixTag"),
 /** The autofix action backup. */
 AUTOFIX_ACTION_BACKUP("autofixBackup"),
 /** The autofix action email remind exception expiry. */
 AUTOFIX_ACTION_EMAIL_REMIND_EXCEPTION_EXPIRY(
                    "remindExceptionExpiry"),
 /** The autofix action exempted. */
 AUTOFIX_ACTION_EXEMPTED("autofixExempted"),
 /** The do nothing. */
 DO_NOTHING("doNothing"),
/** unable to determine */
UNABLE_TO_DETERMINE("unableToDetermine"), CREATE_AUTO_FIX_PLAN("createAutoFixPlan"), SUSPEND_AUTO_FIX_PLAN("suspendAutoFixPlan"), SYNC_AUTO_FIX_PLAN("syncAutoFixPlan");


    /** The action. */
    String action;

    /**
     * Instantiates a new auto fix action.
     *
     * @param action the action
     */
    AutoFixAction(String action) {
        this.action = action;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.action.toString();
    }
}
