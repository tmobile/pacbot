/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose:
  Author :Nidhish
  Modified Date: Nov 27, 2017
**/
package com.tmobile.pacman.api.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfigurerAdapterConfig extends ResourceServerConfigurerAdapter {

	private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void configure(final ResourceServerSecurityConfigurer resources) throws Exception {
        resources
            .accessDeniedHandler(accessDeniedHandler())
            .authenticationEntryPoint(authenticationEntryPoint());
    }

    /** Define your custom exception translator bean here */
    @Bean
    public WebResponseExceptionTranslator exceptionTranslator() {
        return new ApiErrorWebResponseExceptionTranslator();
    }

    /**
     * Inject your custom exception translator into the OAuth2 {@link AuthenticationEntryPoint}.
     *
     * @return AuthenticationEntryPoint
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        final OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
        entryPoint.setExceptionTranslator(exceptionTranslator());
        return entryPoint;
    }

    /**
     * Classic Spring Security stuff, defining how to handle {@link AccessDeniedException}s.
     * Inject your custom exception translator into the OAuth2AccessDeniedHandler.
     * (if you don't add this access denied exceptions may use a different format)
     *
     * @return AccessDeniedHandler
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        final OAuth2AccessDeniedHandler handler = new OAuth2AccessDeniedHandler();
        handler.setExceptionTranslator(exceptionTranslator());
        return handler;
    }

    class ApiErrorWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

        /** The default WebResponseExceptionTranslator. */
        private WebResponseExceptionTranslator defaultTranslator = new DefaultWebResponseExceptionTranslator();

        // Constructor omitted

        @Override
        public ResponseEntity<OAuth2Exception> translate(final Exception e) throws Exception {
            // Translate the exception with the default translator
            ResponseEntity<OAuth2Exception> defaultResponse = this.defaultTranslator.translate(e);
            // Build your own error object
            String errorCode = defaultResponse.getBody().getOAuth2ErrorCode();
            OAuth2Exception excBody = defaultResponse.getBody();
            log.info("Came here==>"+errorCode);
            if(errorCode.equals("unauthorized")) {
            	excBody.addAdditionalInformation("error_description", "Authentication required to access this resource");
            }
            return new ResponseEntity<OAuth2Exception>(excBody, defaultResponse.getStatusCode()) ;
        }
    }
}
