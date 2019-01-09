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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.google.common.collect.Lists;
import com.tmobile.pacman.api.commons.Constants;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ImplicitGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.LoginEndpoint;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger Config Class
 * 
 * @author Nidhish
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig implements WebMvcConfigurer, Constants {

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/docs/v1/");
		registry.addResourceHandler("**/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Value("${auth.active}")
	private String dataSource;
	
	@Value("${spring.application.title}")
	private String serviceName;
	
	@Value("${spring.application.description}")
	private String serviceDesc;
	
	@Value("${azure.authorizeEndpoint:}")
	private String authorizeEndpoint;

	@Value("${azure.activedirectory.client-id:}")
	private String clientId;
	
	@Value("${azure.activedirectory.scope:}")
	private String scope;
	
	@Value("${azure.activedirectory.scopeDesc:}")
	private String scopeDesc;
	
	@Value("${azure.activedirectory.state:}")
	private String state;
	
	@Value("${CONFIG_SERVER_URL:}")
	private String configUrl;

	@Bean
	public Docket userApi() {

		List<ResponseMessage> list = new java.util.ArrayList<>();
		list.add(new ResponseMessageBuilder().code(500).message("500 message").responseModel(new ModelRef("Result"))
				.build());
		list.add(new ResponseMessageBuilder().code(401).message("Unauthorized").responseModel(new ModelRef("Result"))
				.build());
		list.add(new ResponseMessageBuilder().code(406).message("Not Acceptable").responseModel(new ModelRef("Result"))
				.build());

		return new Docket(DocumentationType.SWAGGER_2)
			.apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.tmobile.pacman"))
			.paths(PathSelectors.any()).build()
			.securitySchemes(chooseSecuritSchema())
			.securityContexts(chooseSecurityContext())
			.globalResponseMessage(RequestMethod.GET, list).globalResponseMessage(RequestMethod.POST, list);
	}

	@Bean
	SecurityScheme oauth() {
		return new OAuthBuilder().name(state).grantTypes(grantTypes()).scopes(scopes()).build();
	}

	List<AuthorizationScope> scopes() {
		List<AuthorizationScope> scopes = Lists.<AuthorizationScope>newArrayList();
		scopes.add(new AuthorizationScope(scope, scopeDesc));
		return scopes;
	}

	List<GrantType> grantTypes() {
		GrantType grantType = new ImplicitGrantBuilder()
				.loginEndpoint(new LoginEndpoint(authorizeEndpoint))
				.build();
		return newArrayList(grantType);
	}
	
	@Bean
    public SecurityConfiguration securityInfo() {
		String hostName = configUrl.split("/api")[0];
		if (dataSource.equalsIgnoreCase("azuread")) {
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

	@Bean
	SecurityContext securityContext() {
		AuthorizationScope readScope = new AuthorizationScope(scope, scopeDesc);
		AuthorizationScope[] scopes = new AuthorizationScope[1];
		scopes[0] = readScope;
		SecurityReference securityReference = SecurityReference.builder().reference(state).scopes(scopes)
				.build();

		return SecurityContext.builder().securityReferences(newArrayList(securityReference)).forPaths(PathSelectors.any())
				.build();
	}

	private List<SecurityContext> chooseSecurityContext() {
		if (dataSource.equalsIgnoreCase("azuread")) {
			return newArrayList(securityContext());
		}
		return newArrayList();
	}
	private List<? extends SecurityScheme> chooseSecuritSchema() {
		if (dataSource.equalsIgnoreCase("db") || dataSource.equalsIgnoreCase("ldap")) {
			return newArrayList(new BasicAuth(BASIC_AUTH));
		} else if (dataSource.equalsIgnoreCase("azuread")) {
			return newArrayList(oauth());
		}
		return newArrayList(apiKey());
	}

	private ApiKey apiKey() {
		return new ApiKey(AUTHORIZATION, AUTHORIZATION, "header");
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(serviceName).description(serviceDesc)
				.termsOfServiceUrl("http://pacman.com/service").contact(contact()).license("Apache License Version 2.0")
				.licenseUrl("http://pacman.com/service/LICENSE").version("2.0").build();
	}

	private Contact contact() {
		return new Contact("Team PacBot", "http://pacbot.com", "pacbot@t-mobile.com");
	}
}
