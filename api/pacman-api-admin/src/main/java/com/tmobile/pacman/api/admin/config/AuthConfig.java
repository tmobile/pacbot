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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author Nidhish
 */
@Order(1)
@Configuration("WebSecurityConfig")
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AuthConfig extends WebSecurityConfigurerAdapter {
	
	@Value("${swagger.auth.whitelist:}")
	private String[] AUTH_WHITELIST;

	/**
	 * Constructor disables the default security settings
	 **/
	public AuthConfig() {
		super(true);
	}
    
	@Bean
	public RequestInterceptor requestTokenBearerInterceptor() {
		return new RequestInterceptor() {
			@Override
			public void apply(RequestTemplate requestTemplate) {
				OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext()
						.getAuthentication().getDetails();
				requestTemplate.header("Authorization", "bearer " + details.getTokenValue());
			}
		};
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(AUTH_WHITELIST);
		 web.ignoring()
         .antMatchers("/actuator/**");
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**", "/actuator/**");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.anonymous().and().antMatcher("/user").authorizeRequests()
		
		.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll().
         antMatchers(AUTH_WHITELIST).permitAll().
         antMatchers("/actuator/**").permitAll().
         anyRequest().authenticated()
		.and()
        .csrf()
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .ignoringAntMatchers("/instances", "/actuator/**");
	}
}
