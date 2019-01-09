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
package com.tmobile.pacman.api.auth.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.tmobile.pacman.api.auth.common.Constants;
import com.tmobile.pacman.api.commons.config.SwaggerConfig;

import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 	NidhishKrishnan
 * @purpose Custom implementation of SwaggerConfig
 * @since	November 10, 2018
 * @version	1.0 
**/
@EnableSwagger2
@Configuration
@Order(1)
public class AuthSwaggerConfig extends SwaggerConfig implements Constants {
	 
	@Value("${auth.active}")
	private String dataSource;
	
	@Value("${azure.activedirectory.client-id:}")
	private String clientId;
	
	@Value("${CONFIG_SERVER_URL:}")
	private String configUrl;
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	@Bean
    public SecurityConfiguration securityInfo() {
		String hostName;
		if(applicationName.equalsIgnoreCase(AUTH_SERVICE)) {
			String url = System.getenv(DOMAIN_URL);
			hostName = url.split(API)[0];
		} else {
			hostName = configUrl.split(API)[0];
		}
		if (dataSource.equalsIgnoreCase(AZURE_READ)) {
			return new SecurityConfiguration(
					clientId, 
					StringUtils.EMPTY, 
					hostName, 
					clientId, 
					BEARER, 
					ApiKeyVehicle.HEADER, 
					AUTHORIZATION, 
					StringUtils.EMPTY
			);
		} else {
			return new SecurityConfiguration(
					null, 
					null, 
					hostName,
					null,
					null,
					ApiKeyVehicle.HEADER, 
					AUTHORIZATION,
					null
			);
		}
	}
}
