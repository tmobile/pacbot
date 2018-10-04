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

package com.tmobile.pacman.commons;

// TODO: Auto-generated Javadoc
/**
 * The Enum AWSService.
 */
public enum AWSService {

	/** The ec2. */
	EC2("ec2"),

	/** The rds. */
	RDS("rds"),

	/** The s3. */
	S3("s3"),

	/** The iam. */
	IAM("iam"),

	/** The elb classic. */
	ELB_CLASSIC("elbc"),

	/** The elb app. */
	ELB_APP("elbapp"),

	/** The config. */
	CONFIG("config"),

	/** The lambda. */
	LAMBDA("lambda"),

	/** The apigtw. */
	APIGTW("apigtw"),

	/** The dyndb. */
	DYNDB("dynamodb"),

	/** The cloudtrl. */
	CLOUDTRL("cloudctrl"),

	/** The cloudwatch. */
	CLOUDWATCH("cloudwatch"),

	/** The guard duty. */
	GUARD_DUTY("guardduty"),

	/** The cloudwatch events. */
	CLOUDWATCH_EVENTS("cwevents"),

	/** The route53. */
	ROUTE53("route53"),

	/** The ses. */
	SES("ses"),

	VOLUME("volume"),

	SNAPSHOT("snapshot"),

	/** The rdsdb. */
    RDSDB("rdsdb"),

    /** The elasticsearch. */
    ELASTICSEARCH("elasticsearch"),

    /** The elasticfilesystem. */
    EFS("efs"),

    /** The redshift. */
    REDSHIFT("redshift");

	/** The service name. */
	String serviceName;

	/**
	 * Instantiates a new AWS service.
	 *
	 * @param serviceName the service name
	 */
	private AWSService(String serviceName) {
		this.serviceName=serviceName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.serviceName.toString();
	}
}
