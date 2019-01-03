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
package com.tmobile.pacman.api.admin.controller;

import static com.tmobile.pacman.api.admin.common.AdminConstants.UNEXPECTED_ERROR_OCCURRED;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.domain.JobDetails;
import com.tmobile.pacman.api.admin.domain.JobExecutionManagerListProjections;
import com.tmobile.pacman.api.admin.repository.service.JobExecutionManagerService;

@RunWith(MockitoJUnitRunner.class)
public class JobExecutionManagerControllerTest
{
	private MockMvc mockMvc;

	private Principal principal;

	@Mock
	private JobExecutionManagerService jobExecutionManagerService;

	@InjectMocks
	private JobExecutionManagerController jobExecutionManagerController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(jobExecutionManagerController)
				/* .addFilters(new CORSFilter()) */
				.build();
		principal = Mockito.mock(Principal.class);
	}

	@Test
	public void getPoliciesTest() throws Exception {
		List<JobExecutionManagerListProjections> jobExecutionManagerDetails = Lists.newArrayList();
		jobExecutionManagerDetails.add(getjobExecutionManagerDetailsProjections());
		Page<JobExecutionManagerListProjections> allJobExecutionManagerDetails = new PageImpl<JobExecutionManagerListProjections>(jobExecutionManagerDetails,new PageRequest(0, 1), jobExecutionManagerDetails.size());

		when(jobExecutionManagerService.getAllJobExecutionManagers(anyInt(), anyInt(), anyString())).thenReturn(allJobExecutionManagerDetails);
		mockMvc.perform(get("/job-execution-manager/list").principal(principal)
				.param("page", "0")
				.param("size", "1")
				.param("searchTerm", StringUtils.EMPTY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.message", is("success")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getPoliciesExceptionTest() throws Exception {
		when(jobExecutionManagerService.getAllJobExecutionManagers(anyInt(), anyInt(), anyString())).thenThrow(Exception.class);
		mockMvc.perform(get("/job-execution-manager/list")
				.param("page", "0")
				.param("size", "1")
				.param("searchTerm", StringUtils.EMPTY))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
	@Test
	public void createJobTest() throws Exception {
		byte[] jobDetailsContent = toJson(getCreateJobDetailsRequest());
		MultipartFile firstFile = getMockMultipartFile();
		when(jobExecutionManagerService.createJob(any(), any(), any())).thenReturn(AdminConstants.JOB_CREATION_SUCCESS);
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/job-execution-manager/create")
				.file("file", firstFile.getBytes())
				.principal(principal)
				.content(jobDetailsContent)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is("success")))
				.andExpect(jsonPath("$.data", is(AdminConstants.JOB_CREATION_SUCCESS)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createJobExceptionTest() throws Exception {
		byte[] jobDetailsContent = toJson(getCreateJobDetailsRequest());
		MultipartFile firstFile = getMockMultipartFile();
		when(jobExecutionManagerService.createJob(any(), any(), any())).thenThrow(Exception.class);
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/job-execution-manager/create")
				.file("file", firstFile.getBytes())
				.principal(principal)
				.content(jobDetailsContent)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
				.andExpect(status().isExpectationFailed())
				.andExpect(jsonPath("$.message", is(UNEXPECTED_ERROR_OCCURRED)));
	}
	
	private MultipartFile getMockMultipartFile() {
		return new MockMultipartFile("data", "job.jar", "multipart/form-data", "job content".getBytes());
	}

	private JobDetails getCreateJobDetailsRequest() {
		JobDetails jobDetails = new JobDetails();
		jobDetails.setIsFileChanged(false);
		jobDetails.setJobDesc("jobDesc");
		jobDetails.setJobExecutable("jobExecutable123");
		jobDetails.setJobFrequency("jobFrequency123");
		jobDetails.setJobName("jobName123");
		jobDetails.setJobParams("jobParams123");
		jobDetails.setJobType("jobType123");
		return jobDetails;
	}
	
	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}
	
	private JobExecutionManagerListProjections getjobExecutionManagerDetailsProjections() {
		return new JobExecutionManagerListProjections() {
			
			@Override
			public Date getModifiedDate() {
				return new Date();
			}
			
			@Override
			public String getJobType() {
				return "JobType123";
			}
			
			@Override
			public String getJobParams() {
				return "JobParams123";
			}
			
			@Override
			public String getJobName() {
				return "JobName123";
			}
			
			@Override
			public String getJobId() {
				return "JobId123";
			}
			
			@Override
			public String getJobFrequency() {
				return "JobFrequency123";
			}
			
			@Override
			public String getJobExecutable() {
				return "JobExecutable123";
			}
			
			@Override
			public Date getCreatedDate() {
				return new Date();
			}

			@Override
			public String getStatus() {
				return "ENABLED";
			}
		};
	}
}

