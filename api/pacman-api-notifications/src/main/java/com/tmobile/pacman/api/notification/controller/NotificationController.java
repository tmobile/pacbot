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
package com.tmobile.pacman.api.notification.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import com.tmobile.pacman.api.notification.dto.MailMessageRequestBody;
import com.tmobile.pacman.api.notification.dto.MailTemplateRequestBody;
import com.tmobile.pacman.api.notification.service.MailService;
import com.tmobile.pacman.api.notification.service.NotificationService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
public class NotificationController implements Constants
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/** The from mail address. */
    @Value("${spring.mail.username}")
    private String fromAddress;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private NotificationService notificationService;
	
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully sent the email"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 417, message = "Expectation Failed"),
            @ApiResponse(code = 408, message = "operation timed out")
    }
    )
	
	@ApiOperation(value = "Send a Text Email", response = ResponseEntity.class)
	@RequestMapping(value = "/send-plain-text-mail", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<Void> sendTextMail(@ApiParam(value = "Provide Mail Message Request Body", required = true) final @RequestBody MailMessageRequestBody mailMessageRequestBody) {
		try {
			log.info("fromAddress==sendMailWithTemplate from config {}",fromAddress);
			log.info("mailTemplateRequestBody.getFrom()===sendMailWithTemplate from param {}",mailMessageRequestBody.getFrom());
			
			mailService.prepareAndSendMail("",mailMessageRequestBody.getFrom(), 
					mailMessageRequestBody.getTo(),
					mailMessageRequestBody.getSubject(), 
					mailMessageRequestBody.getMailBodyAsString(),
					mailMessageRequestBody.getPlaceholderValues(), mailMessageRequestBody.getAttachmentUrl(), true);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception exception) {
			log.error(EXE_EMAIL_SEND, exception);
			try {
				log.info("fromAddress==sendMailWithTemplate from catch block from config {}",fromAddress);
				log.info("mailTemplateRequestBody.getFrom() from catch block===sendMailWithTemplate from param {}",mailMessageRequestBody.getFrom());
				
				mailService.prepareAndSendMail(mailMessageRequestBody.getFrom(),fromAddress, 
						mailMessageRequestBody.getTo(),
						mailMessageRequestBody.getSubject(), 
						mailMessageRequestBody.getMailBodyAsString(),
						mailMessageRequestBody.getPlaceholderValues(), mailMessageRequestBody.getAttachmentUrl(), true);
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
				log.error(EXE_EMAIL_SEND, e);
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		}
	}
	
	@ApiOperation(value = "Send an Email with Template", response = ResponseEntity.class)
	@RequestMapping(value = "/send-mail-with-template", method = RequestMethod.POST)
	public ResponseEntity<Void> sendMailWithTemplate(@ApiParam(value = "Provide Mail Template Request Body", required = true) final @RequestBody MailTemplateRequestBody mailTemplateRequestBody) {
		try {
			
			log.info("fromAddress==sendMailWithTemplate from config {}",fromAddress);
			log.info("mailTemplateRequestBody.getFrom()===sendMailWithTemplate from param {}",mailTemplateRequestBody.getFrom());
			
			mailService.prepareAndSendMail("",mailTemplateRequestBody.getFrom(), 
					mailTemplateRequestBody.getTo(),
					mailTemplateRequestBody.getSubject(), 
					mailTemplateRequestBody.getMailBodyAsString(),
					mailTemplateRequestBody.getPlaceholderValues(), mailTemplateRequestBody.getAttachmentUrl(), false);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception exception) {
			log.error(EXE_EMAIL_SEND , exception);
			try {
				
				log.info("fromAddress in catch block==sendMailWithTemplate from config {}",fromAddress);
				log.info("mailTemplateRequestBody.getFrom() in catch block===sendMailWithTemplate from param {}",mailTemplateRequestBody.getFrom());
				
				mailService.prepareAndSendMail(mailTemplateRequestBody.getFrom(),fromAddress, 
						mailTemplateRequestBody.getTo(),
						mailTemplateRequestBody.getSubject(), 
						mailTemplateRequestBody.getMailBodyAsString(),
						mailTemplateRequestBody.getPlaceholderValues(), mailTemplateRequestBody.getAttachmentUrl(), false);
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
				log.error(EXE_EMAIL_SEND , e);
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		}
	}
	
	
	@ApiOperation(value = "Unsubscribe Digest Mail Report", response = ResponseEntity.class)
	@RequestMapping(value = "/unsubscribe-digest-mail", method = RequestMethod.GET)
	public ResponseEntity<Object> unsubscribeDigestMail(@RequestParam(name="mailId") String mailId) {
		Map<String, Object> response = notificationService.unsubscribeDigestMail(mailId.toLowerCase());
		return ResponseUtils.buildSucessResponse(response); 
	}

	/**
	 * 
	 * @param mailDetails
	 * @return
	 */
	public ResponseEntity<Void> sendTextMailFallBack(final @RequestBody Map<String, Object> mailDetails) {
		return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
	}
	
	
	/**
	 * 
	 * @param assetGroup
	 * @param ruleId
	 * @return
	 * API description: asssetGroup is mandatory & ruleId is optional
	 * If API receives assetGroup as request parameter, it gives issues count of all open issues and unknown issues for all the rules.
	 * If API receives both assetGroup and ruleId as request parameter,it gives issues count of all open issues and unknown issues for that rule.
	 */
	@RequestMapping(path = "/cache", method = RequestMethod.GET)
	public ResponseEntity<Object> getDeviceDetails(@RequestParam(name="deviceId") String deviceId)
	{
		if (Strings.isNullOrEmpty(deviceId))
		{
			return ResponseUtils.buildFailureResponse(new Exception("Device Id is mandatory"));
		}
		try {
			    return ResponseUtils.buildSucessResponse(notificationService.getDeviceDetails(deviceId));
		} catch (Exception e) {
			return ResponseUtils.buildFailureResponse(e);
		}

	} 
	
	
	
}

