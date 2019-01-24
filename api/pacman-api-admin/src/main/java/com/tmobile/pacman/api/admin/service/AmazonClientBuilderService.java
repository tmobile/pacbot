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
package com.tmobile.pacman.api.admin.service;

import static com.tmobile.pacman.api.admin.common.AdminConstants.DEFAULT_SESSION_NAME;
import static com.tmobile.pacman.api.admin.common.AdminConstants.TEMPORARY_CREDS_VALID_SECONDS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;

/**
 * Amazon Client Builder Service
 */
@Service
public class AmazonClientBuilderService {

	@Value("${spring.application.title}")
	private String serviceName;

	/**
     * Service function to get RuleAmazonS3 Client
     *
     * @author Nidhish
	 * @param region 
     * @return AmazonS3 Client
     */
	public AmazonS3 getAmazonS3(final String region) {
		return AmazonS3ClientBuilder.standard().withRegion(region).build();
	}

	/**
     * Service function to get RuleAmazonCloudWatchEvents Client
     *
     * @author Nidhish
     * @return AmazonCloudWatchEvents Client
     */
	public AmazonCloudWatchEvents getAmazonCloudWatchEvents(final String region) {
		return AmazonCloudWatchEventsClientBuilder.standard().withRegion(region).build();
	}

	/**
     * Service function to get RuleAWSLambda Client
     *
     * @author Nidhish
     * @return AWSLambda Client
     */
	public AWSLambda getAWSLambdaClient(final String region) {
		return AWSLambdaClientBuilder.standard().withRegion(region).build();
	}

	private AWSCredentialsProvider buildCredentials() {
		AWSCredentialsProvider acp = new DefaultAWSCredentialsProviderChain();
		String baseAccountRoleArn = System.getenv("ROLE_ARN");
        BasicSessionCredentials temporaryCredentials = getTempCredentialsUsingCredProvider(baseAccountRoleArn, acp, TEMPORARY_CREDS_VALID_SECONDS);
		return new AWSStaticCredentialsProvider(temporaryCredentials);
	}

	private BasicSessionCredentials getTempCredentialsUsingCredProvider(String roleArnWithAdequateAccess, AWSCredentialsProvider acp, Integer validForSeconds) {
		AWSSecurityTokenServiceClientBuilder stsBuilder = AWSSecurityTokenServiceClientBuilder.standard().withCredentials(acp);
		AWSSecurityTokenService sts = stsBuilder.build();
		AssumeRoleRequest assumeRequest = new AssumeRoleRequest()
											.withRoleArn(roleArnWithAdequateAccess)
											.withDurationSeconds(validForSeconds)
											.withRoleSessionName(DEFAULT_SESSION_NAME);
		AssumeRoleResult assumeResult = sts.assumeRole(assumeRequest);
		BasicSessionCredentials temporaryCredentials = new BasicSessionCredentials(
				assumeResult.getCredentials().getAccessKeyId(),
				assumeResult.getCredentials().getSecretAccessKey(),
				assumeResult.getCredentials().getSessionToken());

		return temporaryCredentials;
	}
}
