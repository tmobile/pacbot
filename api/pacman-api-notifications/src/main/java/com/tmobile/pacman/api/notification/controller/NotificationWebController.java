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

import io.swagger.annotations.ApiOperation;

import java.util.Map;

import javax.validation.Valid;

import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tmobile.pacman.api.notification.service.NotificationService;

@Controller
public class NotificationWebController
{
	@Autowired
	private NotificationService notificationService;
	
	@ApiOperation(value = "Un Subscribe Weekly Status Email")
	@RequestMapping(value = "/un-subscribe", method = RequestMethod.GET)
	public String unSubscribe(Model model, @Email @Valid @RequestParam(value = "email", required = true) String email) {
		model.addAttribute("email", email);
		Map<String, Object> response = notificationService.unsubscribeDigestMail(email.toLowerCase());
		model.addAttribute("model", response);
		return "un-subscribe";
	}
	
	@ApiOperation(value = "Subscribe Weekly Status Email")
	@RequestMapping(value = "/subscribe", method = RequestMethod.GET)
	public String subscribe(Model model, @Email @Valid @RequestParam(value = "email", required = true) String email) {
		model.addAttribute("email", email);
		Map<String, Object> response = notificationService.subscribeDigestMail(email.toLowerCase());
		model.addAttribute("model", response);
		return "subscribe";
	}
}

