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

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	 private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
	            "classpath:/META-INF/resources/", "classpath:/resources/",
	            "classpath:/static/"
	    };

	    @Override
	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	        registry.addResourceHandler("/**")
	                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
	    }

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/logout").setViewName("logout");
		registry.addViewController("/oauth/confirm_access").setViewName("authorize");


	}

	/*@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//super.addResourceHandlers(registry);
		registry.addResourceHandler("/resources/**")
        .addResourceLocations("/resources/");
	}*/
}
