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
package com.tmobile.cso.pacman.inventory.vo;

import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentResourceDescription;


/**
 * The Class EbsVH.
 */
public class EbsVH {

	/** The app. */
	ApplicationDescription app;
	
	/** The env. */
	EnvironmentDescription env;
	
	/** The env resource. */
	EnvironmentResourceDescription envResource;
	
	/**
	 * Gets the app.
	 *
	 * @return the app
	 */
	public ApplicationDescription getApp() {
		return app;
	}
	
	/**
	 * Sets the app.
	 *
	 * @param app the new app
	 */
	public void setApp(ApplicationDescription app) {
		this.app = app;
	}
	
	/**
	 * Gets the env.
	 *
	 * @return the env
	 */
	public EnvironmentDescription getEnv() {
		return env;
	}
	
	/**
	 * Sets the env.
	 *
	 * @param env the new env
	 */
	public void setEnv(EnvironmentDescription env) {
		this.env = env;
	}
	
	/**
	 * Gets the env resource.
	 *
	 * @return the env resource
	 */
	public EnvironmentResourceDescription getEnvResource() {
		return envResource;
	}
	
	/**
	 * Sets the env resource.
	 *
	 * @param envResource the new env resource
	 */
	public void setEnvResource(EnvironmentResourceDescription envResource) {
		this.envResource = envResource;
	}
	
}
