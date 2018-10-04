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
package com.tmobile.pacman.api.compliance.domain;

/**
 * 
 * Medal for achieving compliance. For e.g. patching
 *
 */
public class Medal {

	/**
	 * Gold , silver, etc.
	 */
	private String medalType;

	/**
	 * Pending, Confirmed etc.
	 */
	private String medalStatus;

	public String getMedalType() {
		return medalType;
	}

	public void setMedalType(String medalType) {
		this.medalType = medalType;
	}

	public String getMedalStatus() {
		return medalStatus;
	}

	public void setMedalStatus(String medalStatus) {
		this.medalStatus = medalStatus;
	}

}
