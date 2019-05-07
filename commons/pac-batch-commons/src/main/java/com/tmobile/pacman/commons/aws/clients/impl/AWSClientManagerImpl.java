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

package com.tmobile.pacman.commons.aws.clients.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.cloudtrail.AWSCloudTrail;
import com.amazonaws.services.cloudtrail.AWSCloudTrailClientBuilder;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.config.AmazonConfig;
import com.amazonaws.services.config.AmazonConfigClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.elasticfilesystem.AmazonElasticFileSystem;
import com.amazonaws.services.elasticfilesystem.AmazonElasticFileSystemClientBuilder;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticsearch.AWSElasticsearch;
import com.amazonaws.services.elasticsearch.AWSElasticsearchClientBuilder;
import com.amazonaws.services.guardduty.AmazonGuardDuty;
import com.amazonaws.services.guardduty.AmazonGuardDutyClientBuilder;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.redshift.AmazonRedshift;
import com.amazonaws.services.redshift.AmazonRedshiftClientBuilder;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.aws.clients.AWSClientManager;
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;
import com.tmobile.pacman.commons.utils.CommonUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class AWSClientManagerImpl.
 */
public class AWSClientManagerImpl implements AWSClientManager {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(AWSClientManagerImpl.class);

    /** The aws client cache. */
    private static Cache<Object, Object> awsClientCache;
    static {
        logger.info("cache initiated...");
        awsClientCache = CacheBuilder.newBuilder().maximumSize(1000)
                .expireAfterWrite(PacmanSdkConstants.TEMPORARY_CREDS_VALID_SECONDS - 200, TimeUnit.SECONDS) // to
                                                                                                            // be
                                                                                                            // on
                                                                                                            // safer
                                                                                                            // side,
                                                                                                            // connections
                                                                                                            // are
                                                                                                            // removed
                                                                                                            // 200
                                                                                                            // seconds
                                                                                                            // before
                                                                                                            // they
                                                                                                            // expire
                .build();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.tmobile.pacman.commons.aws.clients.AWSClientManager#getClient(java
     * .lang.String, java.lang.String, com.tmobile.pacman.commons.AWSService,
     * com.amazonaws.regions.Regions, java.lang.String)
     */
    public Map<String, Object> getClient(String awsAccount, String roleArnWithAdequateAccess, AWSService serviceType,
            Regions region, String roleIdentifierString) throws UnableToCreateClientException {
        Map<String, Object> toReturn;
        BasicSessionCredentials temporaryCredentials = null;
        String clientKey = awsAccount + serviceType.toString() + region.toString() + roleArnWithAdequateAccess;
        if (null != awsClientCache.getIfPresent(clientKey)) {
            logger.info("found connection in cache , not going to create one.");
            return (Map<String, Object>) awsClientCache.getIfPresent(clientKey);
        }
        try {
            temporaryCredentials = getTempCredentials(roleArnWithAdequateAccess, region, roleIdentifierString);
            logger.info("temporaryCredentials {}", temporaryCredentials);
        } catch (Exception e) {
            logger.error("error creating client", e);
            throw new UnableToCreateClientException(e);
        }
        try {
            switch (serviceType) {
            case EC2:
                toReturn = new HashMap<String, Object>();
                AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region)
                        .build();
                toReturn.put(PacmanSdkConstants.CLIENT, ec2Client);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case S3:
                toReturn = new HashMap<String, Object>();
                AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonS3Client);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;
            case RDS:
                toReturn = new HashMap<String, Object>();
                AmazonRDS amazonRdsClient = AmazonRDSClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonRdsClient);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case IAM:
                toReturn = new HashMap<String, Object>();
                AmazonIdentityManagement amazonIdentityManagement = AmazonIdentityManagementClientBuilder.standard()
                        .withRegion(region).withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials))
                        .build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonIdentityManagement);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case ELB_CLASSIC:
                toReturn = new HashMap<String, Object>();
                AmazonElasticLoadBalancing amazonElasticLoadBalancing = AmazonElasticLoadBalancingClientBuilder
                        .standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonElasticLoadBalancing);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case ELB_APP:
                toReturn = new HashMap<String, Object>();
                com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing amazonElasticLoadBalancingV2 = com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder
                        .standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonElasticLoadBalancingV2);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case CONFIG:
                toReturn = new HashMap<String, Object>();
                AmazonConfig amazonConfig = AmazonConfigClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonConfig);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case LAMBDA:
                toReturn = new HashMap<String, Object>();
                AWSLambda awsLambda = AWSLambdaClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, awsLambda);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case APIGTW:
                toReturn = new HashMap<String, Object>();
                AmazonApiGateway amazonApiGatewayClient = AmazonApiGatewayClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonApiGatewayClient);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case DYNDB:
                toReturn = new HashMap<String, Object>();
                AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonDynamoDB);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case CLOUDTRL:
                toReturn = new HashMap<String, Object>();
                // here region is trivial hence not including that
                AWSCloudTrail awsCloudTrail = AWSCloudTrailClientBuilder.standard().withRegion(Regions.DEFAULT_REGION)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, awsCloudTrail);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case CLOUDWATCH:
                toReturn = new HashMap<String, Object>();
                AmazonCloudWatch amazonCloudWatchClient = AmazonCloudWatchClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonCloudWatchClient);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case GUARD_DUTY:
                toReturn = new HashMap<String, Object>();
                AmazonGuardDuty amazonGuardDutyClient = AmazonGuardDutyClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonGuardDutyClient);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case CLOUDWATCH_EVENTS:
                toReturn = new HashMap<String, Object>();
                AmazonCloudWatchEvents amazonCloudWatchEventsClient = AmazonCloudWatchEventsClientBuilder.standard()
                        .withRegion(region).withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials))
                        .build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonCloudWatchEventsClient);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case ROUTE53:
                toReturn = new HashMap<String, Object>();
                AmazonRoute53 amazonRoute53 = AmazonRoute53ClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonRoute53);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case SES:
                toReturn = new HashMap<String, Object>();
                AmazonSimpleEmailService amazonSimpleEmailService = AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(region).withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials))
                        .build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonSimpleEmailService);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;
            case ELASTICSEARCH:
                toReturn = new HashMap<String, Object>();
                AWSElasticsearch awsElasticsearch = AWSElasticsearchClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, awsElasticsearch);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case EFS:
                toReturn = new HashMap<String, Object>();
                AmazonElasticFileSystem fileSystem = AmazonElasticFileSystemClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, fileSystem);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;

            case REDSHIFT:
                toReturn = new HashMap<String, Object>();
                AmazonRedshift amazonRedshift = AmazonRedshiftClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).build();
                toReturn.put(PacmanSdkConstants.CLIENT, amazonRedshift);
                awsClientCache.put(clientKey, toReturn);
                return toReturn;
            }

        } catch (Exception e) {
            logger.error("error creating client", e);
            throw new UnableToCreateClientException(e);
        }

        throw new UnableToCreateClientException("Unknown service type");
    }

    /**
     * get the map of app the clients for all the regions.
     *
     * @param roleArnWithAdequateAccess
     *            the role arn with adequate access
     * @return the string
     */
    // public Map<String, Object> getClientForAllTheRegions(String
    // awsAccount,AWSService serviceType, String... roleArnsWithAdequateAccess)
    // throws UnableToCreateClientException {
    // Map<String, Object> allRegionClients = new HashMap<String, Object>();
    // Map<String, Object> client = null;
    // for (String roleArnWithAdequateAccess : roleArnsWithAdequateAccess) {
    // String accountNumber = detectAccountFromArn(roleArnWithAdequateAccess);
    // for (Regions region : Regions.values()) {
    // try {
    // client = getClient(awsAccount,roleArnWithAdequateAccess, serviceType,
    // region);
    // } catch (Exception e) {
    // logger.error("unable to create client for arn==>" +
    // roleArnWithAdequateAccess + " and region==>"
    // + region.toString(), e);
    // }
    // if (client != null) {
    // allRegionClients.put("client_" + accountNumber + "_" + region.toString(),
    // client.get(PacmanSdkConstants.CLIENT));
    // }
    // }
    // }
    // return allRegionClients;
    // }

    // /**
    // * {@inheritDoc}
    // * @throws UnableToCreateClientException
    // */
    // public Map<String, Object> getClientForAccountAndRegion(AWSService
    // serviceType, String awsAccount, Regions region, String
    // roleArnsForAccountWithAdequateAccess) throws
    // UnableToCreateClientException{
    // return getClient(awsAccount,roleArnsForAccountWithAdequateAccess,
    // serviceType, region);
    // }

    /**
     *
     * @param roleArnWithAdequateAccess
     * @return
     */
    private String detectAccountFromArn(String roleArnWithAdequateAccess) {
        try {
            return roleArnWithAdequateAccess.substring(roleArnWithAdequateAccess.indexOf("::") + "::".length(),
                    roleArnWithAdequateAccess.indexOf(":", roleArnWithAdequateAccess.indexOf("::") + "::".length()));
        } catch (IndexOutOfBoundsException e) {
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Gets the temp credentials.
     *
     * @param roleArnWithAdequateAccess
     *            the role arn with adequate access
     * @param region
     *            the region
     * @param roleIdentifierString
     *            the role identifier string
     * @return the temp credentials
     * @throws Exception
     *             the exception
     */
    private BasicSessionCredentials getTempCredentials(String roleArnWithAdequateAccess, Regions region,
			String roleIdentifierString) throws Exception {
		logger.debug("roleIdentifierString {}", roleIdentifierString);
		logger.debug("region {}", region.getName());
		logger.debug("roleArnWithAdequateAccess {}", roleArnWithAdequateAccess);

		AWSCredentialsProvider acp;
		try {

			acp = new ProfileCredentialsProvider(PacmanSdkConstants.PACMAN_DEV_PROFILE_NAME);

			acp.getCredentials();// to make sure profile exists

			logger.info("Dev environment detected, due to presense of aws credentials profile named -- >"+ PacmanSdkConstants.PACMAN_DEV_PROFILE_NAME);

		} catch (Exception e) {

			logger.info("non dev environment detected, will use default provider chain");

			acp = new DefaultAWSCredentialsProviderChain();

		}
		logger.debug("base ac#-->"+ CommonUtils.getEnvVariableValue(PacmanSdkConstants.BASE_AWS_ACCOUNT_ENV_VAR_NAME));

		String baseAccountRoleArn = "arn:aws:iam::"+ CommonUtils.getEnvVariableValue(PacmanSdkConstants.BASE_AWS_ACCOUNT_ENV_VAR_NAME)+ ":"

				+ roleIdentifierString; // get it from Env. variable

		logger.debug("container role is going to assume " + baseAccountRoleArn);

		BasicSessionCredentials temporaryCredentialsForBaseAccount = getTempCredentialsUsingCredProvider(

		baseAccountRoleArn, Regions.DEFAULT_REGION, acp,PacmanSdkConstants.TEMPORARY_CREDS_VALID_SECONDS);

		logger.debug("container role is going to assume " + baseAccountRoleArn + " success");

		logger.debug("now pac ro now going to assume role specific to account");

		// now we have base account role, assume required account role now,

		// reducing the TTL by 15 secs , assuming parent credentials will expire

		// 15 secs earlier as created earlier

		// do this only if target account is not same as base account

		if (!roleArnWithAdequateAccess.contains(CommonUtils.getEnvVariableValue(PacmanSdkConstants.BASE_AWS_ACCOUNT_ENV_VAR_NAME))){

			temporaryCredentialsForBaseAccount = getTempCredentialsUsingCredProvider(
					roleArnWithAdequateAccess, region,

					new AWSStaticCredentialsProvider(temporaryCredentialsForBaseAccount),

					PacmanSdkConstants.TEMPORARY_CREDS_VALID_SECONDS - 15);

			logger.debug("now pac ro now going to assume role specific to account success");

		} else {
			logger.debug("role already present for this account, not going to assume again.");
		}

		return temporaryCredentialsForBaseAccount;

	}

    /**
     * Gets the temp credentials using cred provider.
     *
     * @param roleArnWithAdequateAccess
     *            the role arn with adequate access
     * @param region
     *            the region
     * @param acp
     *            the acp
     * @param validForSeconds
     *            the valid for seconds
     * @return the temp credentials using cred provider
     */
    private BasicSessionCredentials getTempCredentialsUsingCredProvider(String roleArnWithAdequateAccess,
            Regions region, AWSCredentialsProvider acp, Integer validForSeconds) {
        if (null == region) { // cloud trail case
            region = Regions.DEFAULT_REGION;
        }
        AWSSecurityTokenServiceClientBuilder stsBuilder = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(acp).withRegion(region);
        AWSSecurityTokenService sts = stsBuilder.build();
        AssumeRoleRequest assumeRequest = new AssumeRoleRequest().withRoleArn(roleArnWithAdequateAccess)
                .withDurationSeconds(validForSeconds).withRoleSessionName(PacmanSdkConstants.DEFAULT_SESSION_NAME);
        logger.debug("assume role request " + assumeRequest.toString());
        AssumeRoleResult assumeResult = sts.assumeRole(assumeRequest);
        logger.debug("assume role response " + assumeResult.toString());
        BasicSessionCredentials temporaryCredentials = new BasicSessionCredentials(assumeResult.getCredentials()
                .getAccessKeyId(), assumeResult.getCredentials().getSecretAccessKey(), assumeResult.getCredentials()
                .getSessionToken());

        return temporaryCredentials;
    }

    /**
     * detects the presense of an env variable called with name PACMAN_DEV
     *
     * @return
     */
    // private boolean detectDevEnv() {
    // Map<String, String> env = System.getenv();
    // //return Boolean.TRUE;
    // return env.containsKey(PacmanSdkConstants.PACMAN_DEV_ENV_VARIABLE) ||
    // System.getProperty(PacmanSdkConstants.PACMAN_DEV_ENV_VARIABLE)!=null;
    // }

}
