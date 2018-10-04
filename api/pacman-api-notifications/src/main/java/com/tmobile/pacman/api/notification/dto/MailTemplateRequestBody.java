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
package com.tmobile.pacman.api.notification.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailTemplateRequestBody  {
	
	private String from;
	private List<String> to;
	private String subject;
	@JsonProperty("mailTemplateUrl")
	private String mailBodyAsString;
	private String attachmentUrl;
	private Map<String, Object> placeholderValues;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public List<String> getTo() {
		return to;
	}
	public void setTo(List<String> to) {
		this.to = to;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMailBodyAsString() {
		return mailBodyAsString;
	}
	public void setMailBodyAsString(String mailBodyAsString) {
		this.mailBodyAsString = mailBodyAsString;
	}
	public Map<String, Object> getPlaceholderValues() {
		return placeholderValues;
	}
	public void setPlaceholderValues(Map<String, Object> placeholderValues) {
		this.placeholderValues = placeholderValues;
	}
	public String getAttachmentUrl() {
		return attachmentUrl;
	}
	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}
}
