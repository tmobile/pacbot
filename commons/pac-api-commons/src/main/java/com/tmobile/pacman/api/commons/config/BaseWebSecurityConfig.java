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
package com.tmobile.pacman.api.commons.config;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.tmobile.pacman.api.commons.conditions.CheckSecurityConfigCondition;

/**
 * Base Security Configurer Adapter Class
 *
 * @author Nidhish
 */

@Configuration
@Conditional(CheckSecurityConfigCondition.class)
@EnableWebSecurity
public class BaseWebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(
				"/swagger-ui.html",
				"/api.html",
				"/css/styles.js",
				"/js/swagger.js",
				"/js/swagger-ui.js",
				"/js/swagger-oauth.js",
				"/images/pacman_logo.svg",
				"/images/favicon-32x32.png",
				"/images/favicon-16x16.png",
				"/images/favicon.ico",
				"/docs/v1/api.html",
				"/swagger-resources/**",
				"/v2/api-docs/**",
				"/v2/swagger.json");
	}
	/**
     * Base Security Configurer Adapter
     *
     * @author Nidhish
     * @param http - valid HttpSecurity
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/**").permitAll();
    }
}
