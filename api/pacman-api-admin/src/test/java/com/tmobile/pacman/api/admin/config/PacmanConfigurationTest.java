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
package com.tmobile.pacman.api.admin.config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Lists;
import com.tmobile.pacman.api.admin.domain.Client;
import com.tmobile.pacman.api.admin.domain.ElasticSearchProperty;
import com.tmobile.pacman.api.admin.domain.JobProperty;
import com.tmobile.pacman.api.admin.domain.LambdaProperty;
import com.tmobile.pacman.api.admin.domain.Oauth2;
import com.tmobile.pacman.api.admin.domain.RuleProperty;
import com.tmobile.pacman.api.admin.domain.S3Property;
import com.tmobile.pacman.api.admin.domain.SecurityProperty;
import com.tmobile.pacman.api.admin.domain.TargetTypesProperty;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ })
public class PacmanConfigurationTest {

	@InjectMocks
	private PacmanConfiguration config;
	
	@Test
	public void getAllPacmanConfigurationTest() {
		PacmanConfiguration pacmanConfiguration = new PacmanConfiguration();
		pacmanConfiguration.setJob(buildJobProperty());
		pacmanConfiguration.setRule(buildRuleProperty());
		pacmanConfiguration.setSecurity(buildSecurityProperty());
		pacmanConfiguration.setElasticSearch(buildElasticSearchProperty());
		pacmanConfiguration.setTargetTypes(buildTargetTypesProperty());
		assertThat(pacmanConfiguration.getJob().getLambda().getFunctionArn(), is("functionArn123"));
		assertThat(pacmanConfiguration.getRule().getLambda().getFunctionArn(), is("functionArn123"));
		
		assertThat(pacmanConfiguration.getSecurity().getOauth2().getClient().getUserAuthorizationUri(), is("userAuthorizationUri"));
		assertThat(pacmanConfiguration.getElasticSearch().getDevIngestHost(), is("devIngestHost"));
		assertThat(pacmanConfiguration.getTargetTypes().getCategories().size(), is(0));
	}
	
	private JobProperty buildJobProperty() {
		JobProperty jobProperty = new JobProperty();
		LambdaProperty lambdaProperty = new LambdaProperty();
		lambdaProperty.setActionDisabled("actionDisabled123");
		lambdaProperty.setActionEnabled("actionEnabled123");
		lambdaProperty.setFunctionArn("functionArn123");
		lambdaProperty.setFunctionName("functionName123");
		lambdaProperty.setPrincipal("principal123");
		lambdaProperty.setTargetId("targetId123");
		S3Property s3Property = new S3Property();
		s3Property.setBucketName("bucketName123");
		jobProperty.setLambda(lambdaProperty);
		jobProperty.setS3(s3Property);
		return jobProperty;
	}
	
	private RuleProperty buildRuleProperty() {
		RuleProperty ruleProperty = new RuleProperty();
		S3Property s3Property = new S3Property();
		s3Property.setBucketName("job-execution-manager-executables");
		LambdaProperty lambdaProperty = new LambdaProperty();
		lambdaProperty.setActionDisabled("actionDisabled123");
		lambdaProperty.setActionEnabled("actionEnabled123");
		lambdaProperty.setFunctionArn("functionArn123");
		lambdaProperty.setFunctionName("functionName123");
		lambdaProperty.setPrincipal("principal123");
		lambdaProperty.setTargetId("targetId123");
		ruleProperty.setLambda(lambdaProperty);
		ruleProperty.setS3(s3Property);
		return ruleProperty;
	}
	
	private SecurityProperty buildSecurityProperty() {
		Client client = new Client();
		client.setUserAuthorizationUri("userAuthorizationUri");
		Oauth2 oauth2 = new Oauth2();
		oauth2.setClient(client);
		SecurityProperty securityProperty = new SecurityProperty();
		securityProperty.setOauth2(oauth2);
		return securityProperty;
	}
	
	private ElasticSearchProperty buildElasticSearchProperty() {
		ElasticSearchProperty elasticSearchProperty = new ElasticSearchProperty();
		elasticSearchProperty.setDevIngestHost("devIngestHost");
		elasticSearchProperty.setDevIngestPort(9090);
		elasticSearchProperty.setHost("host");
		elasticSearchProperty.setPort(9090);
		return elasticSearchProperty;
	}
	
	private TargetTypesProperty buildTargetTypesProperty() {
		TargetTypesProperty targetTypesProperty = new TargetTypesProperty();
		targetTypesProperty.setCategories(Lists.newArrayList());
		return targetTypesProperty;
	}
}
