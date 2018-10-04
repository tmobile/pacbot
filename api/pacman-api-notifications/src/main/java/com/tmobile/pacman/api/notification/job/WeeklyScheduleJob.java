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
/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author : NidhishKrishnan
  Modified Date: Jan 29, 2018
  
**/
package com.tmobile.pacman.api.notification.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.tmobile.pacman.api.notification.service.AssetGroupEmailService;

@DisallowConcurrentExecution
public class WeeklyScheduleJob extends QuartzJobBean implements InterruptableJob {
	
	@Autowired
	private AssetGroupEmailService assetGroupEmailService;
	
	@SuppressWarnings("unused")
	private volatile boolean toStopFlag = true;
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		//String environment = System.getenv("ENVIRONMENT");
		//if(environment.equalsIgnoreCase("dev")) {
			assetGroupEmailService.executeEmailServiceForAssetGroup();
		//}
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		toStopFlag = false;
	}
}
