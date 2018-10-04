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
  Author :santoshi
  Modified Date: Jul 10, 2018

**/
package com.tmobile.pacman.api.notification.service;

import static org.mockito.Mockito.doNothing;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.quartz.Calendar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.notification.job.WeeklyScheduleJob;

@RunWith(PowerMockRunner.class)
public class WeeklyScheduleJobTest {

	@InjectMocks
	private WeeklyScheduleJob weeklyScheduleJob;

	@Mock
	private AssetGroupEmailService assetGroupEmailService;

	@Before
	public void setUp() {
		final WeeklyScheduleJob classUnderTest = PowerMockito.spy(new WeeklyScheduleJob());
		ReflectionTestUtils.setField(classUnderTest, "toStopFlag", true);
	}

	@Test
	public void interruptTest() throws Exception {
		//doNothing().when(weeklyScheduleJob).interrupt();
		weeklyScheduleJob.interrupt();
	}

	@Test
	public void executeInternalTest() throws Exception {
		final WeeklyScheduleJob classUnderTest = PowerMockito.spy(new WeeklyScheduleJob());
		assetGroupEmailService = PowerMockito.mock(AssetGroupEmailService.class);
		ReflectionTestUtils.setField(classUnderTest, "assetGroupEmailService", assetGroupEmailService);
		doNothing().when(assetGroupEmailService).executeEmailServiceForAssetGroup();
		JobExecutionContext JobExecutionContext = getJobExecutionContext();
		Whitebox.invokeMethod(classUnderTest, "executeInternal", JobExecutionContext);
	}

	private org.quartz.JobExecutionContext getJobExecutionContext() {
		return new JobExecutionContext(){

			@Override
			public Scheduler getScheduler() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Trigger getTrigger() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Calendar getCalendar() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isRecovering() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public TriggerKey getRecoveringTriggerKey() throws IllegalStateException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getRefireCount() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public JobDataMap getMergedJobDataMap() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public JobDetail getJobDetail() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Job getJobInstance() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Date getFireTime() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Date getScheduledFireTime() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Date getPreviousFireTime() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Date getNextFireTime() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getFireInstanceId() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getResult() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setResult(Object result) {
				// TODO Auto-generated method stub

			}

			@Override
			public long getJobRunTime() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void put(Object key, Object value) {
				// TODO Auto-generated method stub

			}

			@Override
			public Object get(Object key) {
				// TODO Auto-generated method stub
				return null;
			}};
	}

}
