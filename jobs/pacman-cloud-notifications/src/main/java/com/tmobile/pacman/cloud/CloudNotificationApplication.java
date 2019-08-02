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
package com.tmobile.pacman.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * The Class CloudNotificationApplication.
 */
@Configuration
@ComponentScan
public class CloudNotificationApplication {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CloudNotificationApplication.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @return
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(CloudNotificationApplication.class);
		LOGGER.info("****INVENTORY JOB STARTED RUNNING****");
		// Reads the data from raw-data index,
		CloudNotificationDataCollectionOrchestrator dataOrchestrator = context
				.getBean(CloudNotificationDataCollectionOrchestrator.class);
		dataOrchestrator.orchestrate();

	}

}
