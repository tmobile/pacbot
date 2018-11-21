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
package com.tmobile.pacman.api.admin.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.admin.domain.JobExecutionManagerListProjections;
import com.tmobile.pacman.api.admin.repository.model.JobExecutionManager;

/**
 * JobExecutionManager Repository Interface
 */
@Repository
public interface JobExecutionManagerRepository extends JpaRepository<JobExecutionManager, String> {

	/**
     * JobExecutionManager Repository function for to get all Jobs details
     *
     * @author Nidhish
     * @param searchTerm - searchTerm to be searched.
     * @param pageable - pagination information
     * @return All Job Execution Manager Details
     */
	@Query(value = "SELECT job.jobId AS jobId, job.jobName AS jobName, job.jobType AS jobType, job.jobParams AS jobParams, job.jobFrequency AS jobFrequency, job.jobExecutable AS jobExecutable, job.createdDate AS createdDate,  job.modifiedDate AS modifiedDate FROM JobExecutionManager job WHERE "
			+ "LOWER(job.jobId) LIKE %:searchTerm% OR "
			+ "LOWER(job.jobName) LIKE %:searchTerm% OR "
			+ "LOWER(job.jobType) LIKE %:searchTerm% OR "
			+ "LOWER(job.jobParams) LIKE %:searchTerm% OR "
			+ "LOWER(job.jobFrequency) LIKE %:searchTerm% OR "
			+ "LOWER(job.jobExecutable) LIKE %:searchTerm% GROUP BY job.jobId",

			countQuery = "SELECT COUNT(*) FROM JobExecutionManager job WHERE "
					+ "LOWER(job.jobId) LIKE %:searchTerm% OR "
					+ "LOWER(job.jobName) LIKE %:searchTerm% OR "
					+ "LOWER(job.jobType) LIKE %:searchTerm% OR "
					+ "LOWER(job.jobParams) LIKE %:searchTerm% OR "
					+ "LOWER(job.jobFrequency) LIKE %:searchTerm% OR "
					+ "LOWER(job.jobExecutable) LIKE %:searchTerm% GROUP BY job.jobId")
	public Page<JobExecutionManagerListProjections> findAllJobExecutionManagers(@Param("searchTerm") String searchTerm, Pageable pageable);

	@Query("SELECT job.jobId FROM JobExecutionManager job WHERE job.jobId != '' AND job.jobId != null GROUP BY job.jobId")
	public Collection<String> getAllJobIds();
}
