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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.esotericsoftware.minlog.Log;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

@Service
public class MailService {

	
	private final Logger log = LoggerFactory.getLogger(getClass());
    private JavaMailSender mailSender;
    private MailContentBuilderService mailContentBuilderService;
    
    @Autowired
    public MailService(JavaMailSender mailSender, MailContentBuilderService mailContentBuilderService) {
        this.mailSender = mailSender;
        this.mailContentBuilderService = mailContentBuilderService;
    }

	public void prepareAndSendMail(String cc,String from, List<String> to, String subject, String mailMessageUrlOrBody, Map<String, Object> placeholderValues, final String attachmentUrl, final Boolean isPlainMessage) throws Exception {
		mailSender.send(buildMimeMessagePreparator(cc,from, to, subject, mailMessageUrlOrBody, placeholderValues, attachmentUrl, isPlainMessage));
	}

	public void prepareTemplateAndSendMail(String from, List<String> to, String subject, String mailMessageUrlOrBody, Map<String, Object> placeholderValues, final String attachmentUrl, final Boolean isPlainMessage) throws Exception {
		mailSender.send(prepareTemplateBuildMimeMessagePreparator(from, to, subject, mailMessageUrlOrBody, placeholderValues, attachmentUrl, isPlainMessage));
	}
	
	public void prepareTemplateAndSendMail(String from, List<String> to, String subject, String mailContent , final String attachmentUrl) throws Exception {
		mailSender.send(buildMimeMessagePreparator(from, to, subject, mailContent, attachmentUrl));
	}
	
	private MimeMessagePreparator buildMimeMessagePreparator(String cc,String from, List<String> to, String subject, String mailMessageUrlOrBody, Map<String, Object> placeholderValues, final String attachmentUrl, final Boolean isPlainMessage) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setFrom(from);
			
			if(!StringUtils.isEmpty(cc)){
			messageHelper.setCc(cc);
			}
			
			String[] toMailList = to.toArray(new String[to.size()]);
			messageHelper.setTo(toMailList);
			messageHelper.setSubject(subject);
			if(StringUtils.isNotEmpty(attachmentUrl) && isHttpUrl(attachmentUrl)) {
				URL url = new URL(attachmentUrl);
				String filename = url.getFile();
				byte fileContent [] = getFileContent(url);
				messageHelper.addAttachment(filename, new ByteArrayResource(fileContent));
			}
			String content = StringUtils.EMPTY;
			if(isPlainMessage) {
				content = mailContentBuilderService.buildPlainTextMail(mailMessageUrlOrBody, placeholderValues);
			} else {
				if(!isHttpUrl(mailMessageUrlOrBody)) {
					File template = new ClassPathResource("templates/".concat(mailMessageUrlOrBody).concat(".html")).getFile();
					content = mailContentBuilderService.buildPlainTextMail(FileUtils.readFileToString(template, "UTF-8"), placeholderValues);
				} else {
					String mailBody = mailContentBuilderService.getRemoteMailContent(mailMessageUrlOrBody);
					content = mailContentBuilderService.buildPlainTextMail(mailBody, placeholderValues);
				}
			}
			messageHelper.setText(content, true);
		};
		return messagePreparator;
	}
	
	private MimeMessagePreparator prepareTemplateBuildMimeMessagePreparator(String from, List<String> to, String subject, String mailMessageUrlOrBody, Map<String, Object> templateModelValues, final String attachmentUrl, final Boolean isPlainMessage) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setFrom(from);
			String[] toMailList = to.toArray(new String[to.size()]);
			messageHelper.setTo(toMailList);
			messageHelper.setSubject(subject);
			if(StringUtils.isNotEmpty(attachmentUrl) && isHttpUrl(attachmentUrl)) {
				URL url = new URL(attachmentUrl);
				String filename = url.getFile();
				byte fileContent [] = getFileContent(url);
				messageHelper.addAttachment(filename, new ByteArrayResource(fileContent));
			}
			String content = StringUtils.EMPTY;
			if(isPlainMessage) {
				content = mailContentBuilderService.buildPlainTextMail(mailMessageUrlOrBody, templateModelValues);
			} else {
				if(!isHttpUrl(mailMessageUrlOrBody)) {
					File template = new ClassPathResource("templates/".concat(mailMessageUrlOrBody).concat(".html")).getFile();
					content = mailContentBuilderService.buildPlainTextMail(FileUtils.readFileToString(template, "UTF-8"), templateModelValues);
				} else {
					content = processTemplate(mailMessageUrlOrBody, templateModelValues);
				}
			}
			messageHelper.setText(content, true);
		};
		return messagePreparator;
	}
	
	private MimeMessagePreparator buildMimeMessagePreparator(String from, List<String> to, String subject, String mailContent , final String attachmentUrl) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setFrom(from);
			String[] toMailList = to.toArray(new String[to.size()]);
			messageHelper.setTo(toMailList);
			messageHelper.setSubject(subject);
			messageHelper.setText(mailContent, true);
			if(StringUtils.isNotEmpty(attachmentUrl) && isHttpUrl(attachmentUrl)) {
				URL url = new URL(attachmentUrl);
				String filename = url.getFile();
				byte fileContent [] = getFileContent(url);
				messageHelper.addAttachment(filename, new ByteArrayResource(fileContent));
			}
		};
		return messagePreparator;
	}


	@SuppressWarnings("deprecation")
	public String processTemplate(String templateUrl, Map<String, Object> model) {
		try {
			if(templateUrl != null) {
				String mailBody = mailContentBuilderService.getRemoteMailContent(templateUrl);
				Configuration cfg = new Configuration();
				cfg.setObjectWrapper(new DefaultObjectWrapper());
				Template t = new Template(UUID.randomUUID().toString(), new StringReader(mailBody), cfg);
			    Writer out = new StringWriter();
				t.process(model, out);
				return out.toString();
			}
		} catch (Exception exception) {
			Log.error(exception.getMessage());
		}
		return null;
	}

	private byte[] getFileContent(URL attachmentUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) attachmentUrl.openConnection();
        InputStream inputStream = connection.getInputStream();
       /* String filename = attachmentUrl.getFile();
        filename = filename.substring(filename.lastIndexOf('/') + 1);*/
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, baos);
        inputStream.close();
        baos.close();
		return baos.toByteArray();
	}

	private boolean isHttpUrl(String url) {
		return new UrlValidator().isValid(url);
	}
}
