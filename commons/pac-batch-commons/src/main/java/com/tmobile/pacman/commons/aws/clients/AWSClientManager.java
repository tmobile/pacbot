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
  Author :kkumar
  Modified Date: Jun 14, 2017

**/

package com.tmobile.pacman.commons.aws.clients;

import java.util.Map;

import com.amazonaws.regions.Regions;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;

// TODO: Auto-generated Javadoc
/**
 * The Interface AWSClientManager.
 */
public interface AWSClientManager {

	/**
	 * Gets the client.
	 *
	 * @param awsAccount the aws account
	 * @param roleArnWithAdequateAccess the role arn with adequate access
	 * @param serviceType the service type
	 * @param region the region
	 * @param roleIdentifierString the role identifier string
	 * @return the client
	 * @throws UnableToCreateClientException the unable to create client exception
	 */
	public Map<String, Object> getClient(String awsAccount,String roleArnWithAdequateAccess, AWSService serviceType, Regions region, String roleIdentifierString)
			throws UnableToCreateClientException;
//	/**
//	 *
//	 * @param serviceType
//	 * @param roleArnsWithAdequateAccess
//	 * @return
//	 * @throws UnableToCreateClientException
//	 */
//	public Map<String, Object> getClientForAllTheRegions(String awsAccount,AWSService serviceType, String... roleArnsWithAdequateAccess)
//			throws UnableToCreateClientException;
//
//	/**
//	 *
//	 * @param serviceType
//	 * @param awsAccount
//	 * @param region
//	 * @param roleArnsForAccountWithAdequateAccess
//	 * @return
//	 * @throws UnableToCreateClientException
//	 */
//	public Map<String, Object> getClientForAccountAndRegion(AWSService serviceType, String awsAccount, Regions region,String roleArnsForAccountWithAdequateAccess)
//			throws UnableToCreateClientException;


}
