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

package com.tmobile.pacman.util;

// TODO: Auto-generated Javadoc
/**
 * The Class ProgramExitUtils.
 *
 * @author kkumar
 */
public class ProgramExitUtils {


    /**
     * Exit with error.
     */
    public static void exitWithError() {
        exitWithCode(-1);
        ;
    }
    /**
     * Exit sucessfully.
     */
    public static void exitSucessfully() {
        exitWithCode(0);
    }
    /**
     * Exit with code.
     *
     * @param statusCode the status code
     */
    public static void exitWithCode(Integer statusCode) {
        System.exit(statusCode);
    }

}
