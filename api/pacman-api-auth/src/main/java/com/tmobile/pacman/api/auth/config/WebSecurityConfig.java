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
package com.tmobile.pacman.api.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.context.request.RequestContextListener;

import com.tmobile.pacman.api.auth.provider.PacmanAuthenticationProvider;

@Configuration("WebSecurityConfig")
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


	@Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

	@Bean
	public RequestContextListener requestContextListener(){
	    return new RequestContextListener();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated();
		http.formLogin().loginPage("/login").permitAll();
		http.logout().clearAuthentication(true)
        .logoutSuccessUrl("/")
        .logoutUrl("/logout-session")
        .deleteCookies("JSESSIONID")
        .invalidateHttpSession(true);
		http.requestMatchers().antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access", "/implicit/redirect");
		http.authorizeRequests().antMatchers("/oauth/authorize").authenticated().anyRequest().authenticated();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
		web.ignoring().antMatchers("/imgs/**");
		web.ignoring().antMatchers("/css/**");
		web.ignoring().antMatchers("/css/font/**");
        web.ignoring().antMatchers("/proxy*/**");
        web.ignoring().antMatchers("/hystrix/monitor/**");
        web.ignoring().antMatchers("/hystrix/**");
		web.ignoring().antMatchers("/public/**", "/swagger-ui.html", "/api.html", "/js/swagger-oauth.js", "/images/pacman_logo.svg", "/js/swagger.js", "/js/swagger-ui.js", "/images/favicon-32x32.png", "/images/favicon-16x16.png", "/images/favicon.ico", "/swagger-resources/**", "/v2/api-docs/**", "/webjars/**",
				"/v1/auth/**", "/client-auth/**", "/user/login/**", "/auth/refresh/**", "/user/authorize/**");
	}

	@Autowired
	private PacmanAuthenticationProvider pacmanAuthenticationProvider;

	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(pacmanAuthenticationProvider);
	}
}
