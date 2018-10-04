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
package com.tmobile.pacman.api.notification.service;

import java.io.IOException;
import java.util.Map;

import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.google.common.collect.Maps;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@Service
public class MailContentBuilderService {

    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilderService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

	public String build(final String templateNameOrUrl, final Map<String, String> details) {
		return templateEngine.process(templateNameOrUrl, buildContext(details));
	}
	
	public String buildPlainTextMail(String mailBody, final Map<String, Object> details) {
		for (Map.Entry<String, Object> entry : details.entrySet()) {
			mailBody = mailBody.replace("${".concat(entry.getKey()).concat("}"), entry.getValue().toString());
		}
		return mailBody;
	}

	private Context buildContext(Map<String, String> details) {
		Context context = new Context();
		for (Map.Entry<String, String> entry : details.entrySet()) {
			context.setVariable(entry.getKey(), entry.getValue());
		}
		return context;
	}

	public String getRemoteMailContent(String templateUrl) throws ParseException, IOException {
		Map<String, String> headers = Maps.newHashMap();
		headers.put("Conent-Type", "text/html");
		return PacHttpUtils.getHttpGet(templateUrl, headers);
	}
}
