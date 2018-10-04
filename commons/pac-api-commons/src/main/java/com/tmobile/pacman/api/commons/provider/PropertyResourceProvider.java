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
package com.tmobile.pacman.api.commons.provider;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.tmobile.pacman.api.commons.config.SwaggerServicesConfig;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

/**
 * Swagger Services Config Class
 *
 * @author Nidhish
 */
@Component
@Primary
public class PropertyResourceProvider implements SwaggerResourcesProvider {

	@Autowired
	private SwaggerServicesConfig config;

	/**
	 * Build and return list of Swagger Resources (i.e. each PacMan MicroService api-docs info)
	 */
	@Override
	public List<SwaggerResource> get() {
		return config.services.stream().map(service -> {
			SwaggerResource swaggerResource = new SwaggerResource();
			swaggerResource.setName(service.getName());
			swaggerResource.setLocation(service.getUrl());
			swaggerResource.setSwaggerVersion(service.getVersion());
			return swaggerResource;
		}).collect(Collectors.toList());
	}
}