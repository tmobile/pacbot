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
package com.tmobile.cso.pacman.inventory.util;

import java.util.Base64;

/**
 * The Class Util.
 */
public class Util {
    
    /**
     * Instantiates a new util.
     */
    private Util() {
        
    }

	/**
	 * Base 64 decode.
	 *
	 * @param encodedStr the encoded str
	 * @return the string
	 */
	public static String base64Decode(String encodedStr) {
		return new String(Base64.getDecoder().decode(encodedStr));
	}
}
