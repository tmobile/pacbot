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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tmobile.pacman.api.auth.services.CustomUserDetailsService;
import com.tmobile.pacman.api.auth.services.PacmanAuthenticationProvider;

/**
 * @author 	NidhishKrishnan
 * @purpose Custom implementation of WebSecurityConfigurerAdapter
 * @since	November 10, 2018
 * @version	1.0 
**/
@Order(1)
@EnableWebSecurity
@Configuration("WebSecurityConfig")
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new CustomUserDetailsService();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
    	web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    	web.ignoring().antMatchers("/auth/api.html", "/actuator/**", "/imgs/**", "/css/**", "/hystrix/monitor/**", "/hystrix/**", "/public/**", "/swagger-ui.html", "/api.html", "/js/swagger-oauth.js", "/images/pacman_logo.svg", "/js/swagger.js", "/js/swagger-ui.js", "/images/favicon-32x32.png", "/images/favicon-16x16.png", "/images/favicon.ico", "/swagger-resources/**", "/v2/api-docs/**", "/webjars/**",
				"/v1/auth/**", "/client-auth/**", "/user/login/**", "/auth/refresh/**", "/user/authorize/**", "/user/refresh/**");
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(pacmanAuthenticationProvider).userDetailsService(userDetailsServiceBean())
        .passwordEncoder(passwordEncoder());
    }

	@Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Autowired
	private PacmanAuthenticationProvider pacmanAuthenticationProvider;

	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(pacmanAuthenticationProvider);
	}


    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.authorizeRequests().anyRequest().authenticated()
    	.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll().
        antMatchers("/actuator/**").permitAll().anyRequest().authenticated();
		http.formLogin().loginPage("/login").permitAll();
		http.logout().clearAuthentication(true)
        .logoutSuccessUrl("/")
        .logoutUrl("/logout-session")
        .deleteCookies("JSESSIONID")
        .invalidateHttpSession(true);
		http.requestMatchers().antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access", "/implicit/redirect");
		http.authorizeRequests().antMatchers("/oauth/authorize").authenticated().anyRequest().authenticated();
		http.csrf().ignoringAntMatchers("/instances", "/actuator/**");
    }
}
