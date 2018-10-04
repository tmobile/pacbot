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
  Author :Nidhish
  Modified Date: Nov 27, 2017
**/
package com.tmobile.pacman.api.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
//@EnableResourceServer
/*@EnableHystrix
@EnableHystrixDashboard*/
@EnableFeignClients
@EnableConfigurationProperties
@Configuration
@EnableResourceServer
@ComponentScan(basePackages="com.tmobile.pacman")
public class AuthApplication {

	public static void main(String[] args) {
		//disable DNS caching


		System.setProperty("com.unboundid.ldap.sdk.debug.enabled", "true");
		System.setProperty("com.unboundid.ldap.sdk.debug.level",  "ALL");
		//java.security.Security.setProperty("networkaddress.cache.ttl" , "0");
		SpringApplication.run(AuthApplication.class, args);
	}
}
