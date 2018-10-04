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
package com.tmobile.pacman.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmobile.pacman.common.AutoFixAction;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.dto.AutoFixTransaction;
import com.tmobile.pacman.dto.ResourceOwner;

// TODO: Auto-generated Javadoc
/**
 * The Class MailUtils.
 */
public class MailUtils {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(MailUtils.class);

    /**
     * Send plain text mail.
     *
     * @param toRecipients
     *            the to recipients
     * @param from
     *            the from
     * @param subject
     *            the subject
     * @param placeholderValues
     *            the placeholder values
     * @param templateName
     *            the template name
     * @return true, if successful
     */
    public static boolean sendPlainTextMail(final List<String> toRecipients, final String from, final String subject,
            final Map<String, Object> placeholderValues, final String templateName) {
        try {
            Gson gson = new Gson();
            if (toRecipients != null && toRecipients.size() > 0) {
                logger.debug("sending email to-->");
                toRecipients.stream().forEach(logger::debug);

                String templateContent = CommonUtils.getTemplateContent(templateName);
                Map<String, Object> mailDetails = Maps.newHashMap();
                mailDetails.put("attachmentUrl", "");
                mailDetails.put("from", from);
                mailDetails.put("mailBodyAsString", templateContent);
                mailDetails.put("placeholderValues", placeholderValues);
                mailDetails.put("subject", subject);
                mailDetails.put("to", toRecipients);
                CommonUtils.doHttpPost(CommonUtils.getPropValue(PacmanSdkConstants.EMAIL_SERVICE_URL),
                        gson.toJson(mailDetails));
            }
            return true;
        } catch (Exception e) {
            logger.error("error sending email", e);
        }
        return false;
    }

    /**
     * Send auto fix notification.
     *
     * @param ruleParam
     *            the rule param
     * @param resourceOwner
     *            the resource owner
     * @param targetType
     *            the target type
     * @param resourceid
     *            the resourceid
     * @param expiringTime
     *            the expiring time
     * @param autofixActionEmail
     *            the autofix action email
     * @return true, if successful
     */
    public static boolean sendAutoFixNotification(Map<String, String> ruleParam, final ResourceOwner resourceOwner,
            final String targetType, final String resourceid, final String expiringTime,
            AutoFixAction autofixActionEmail) {
        try {
            List<String> toRecipients = Lists.newArrayList();
            if (resourceOwner != null && !Strings.isNullOrEmpty(resourceOwner.getEmailId())
                    && resourceOwner.getEmailId().contains("@")) {
                toRecipients.add(resourceOwner.getEmailId());
            } else {
                toRecipients.add(CommonUtils.getPropValue(PacmanSdkConstants.ORPHAN_RESOURCE_OWNER_EMAIL));
            }
            String policyUrl = CommonUtils.getPropValue(PacmanSdkConstants.POLICY_URL_PREFIX_KEY
                    + ruleParam.get(PacmanSdkConstants.RULE_ID));
            String violationMessage = CommonUtils.getPropValue(PacmanSdkConstants.EMAIL_VIOLATION_MESSAGE_PREFIX
                    + ruleParam.get(PacmanSdkConstants.RULE_ID));
            String postFixMessage = CommonUtils.getPropValue(PacmanSdkConstants.EMAIL_FIX_MESSAGE_PREFIX
                    + ruleParam.get(PacmanSdkConstants.RULE_ID));
            if (!Strings.isNullOrEmpty(violationMessage)) {
                Map<String, String> data = new HashMap<String, String>();
                data.put("RESOURCE_ID", resourceid);
                violationMessage = StrSubstitutor.replace(violationMessage, data);
                postFixMessage = StrSubstitutor.replace(postFixMessage, data);
            }
            String warning = CommonUtils.getPropValue(PacmanSdkConstants.EMAIL_WARNING_MESSAGE_PREFIX
                    + ruleParam.get(PacmanSdkConstants.RULE_ID));
            String emailCCList = CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_CC_KEY);
            toRecipients.addAll(Arrays.asList(emailCCList.split("\\s*,\\s*")));
            String templateName = "";
            Map<String, Object> placeholderValues = Maps.newHashMap();
            placeholderValues.put("NAME", resourceOwner != null ? resourceOwner.getName() : "");
            placeholderValues.put("POLICY_URL", policyUrl);
            placeholderValues.put("RESOURCE_ID", resourceid);
            placeholderValues.put("TIME", expiringTime);
            placeholderValues.put("RULE_VIOLATION_MESSAGE", violationMessage);
            placeholderValues.put("AUTOFIX_WARNING_MESSAGE", warning);
            placeholderValues.put("AUTOFIX_POST_FIX_MESSAGE", postFixMessage);
            placeholderValues.put("AUTOFIX_EXPIRY_TIME", expiringTime);
            String emailSubject = "Pacman AutoFix Reminder";

            if (autofixActionEmail == AutoFixAction.AUTOFIX_ACTION_EMAIL) {
                templateName = "autofix-user-notification-info";
                emailSubject = CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_WARNING_SUBJECT_PREFIX
                        + ruleParam.get(PacmanSdkConstants.RULE_ID));
            } else if (autofixActionEmail == AutoFixAction.AUTOFIX_ACTION_FIX) {
                templateName = "autofix-user-notification-action";
                emailSubject = CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_FIX_SUBJECT_PREFIX
                        + ruleParam.get(PacmanSdkConstants.RULE_ID));
            } else if (autofixActionEmail == AutoFixAction.AUTOFIX_ACTION_EMAIL_REMIND_EXCEPTION_EXPIRY) {
                templateName = "autofix-user-notification-exception-expiry";
            } else if (autofixActionEmail == AutoFixAction.AUTOFIX_ACTION_EXEMPTED) {
                templateName = "autofix-user-notification-exemption-granted";
                emailSubject = CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_EXEMPTED_SUBJECT);
            }

            return sendPlainTextMail(toRecipients, CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_FROM),
                    emailSubject, placeholderValues, templateName);
        } catch (Exception e) {
            logger.error("error sending email", e);
        }
        return false;
    }

   /**
    * 
    * @param mailBody
    * @param ruleParam
    * @param resourceOwner
    * @param targetType
    * @return
    */
    public static boolean sendSilentFixNotification(List<AutoFixTransaction> silentautoFixTrans, Map<String, String> ruleParam,
            ResourceOwner resourceOwner, String targetType) {
        try {
            List<String> toRecipients = Lists.newArrayList();
            String emailCCList = CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_CC_KEY);
            toRecipients.addAll(Arrays.asList(emailCCList.split("\\s*,\\s*")));
            String  emailSubject = CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_FIX_SUBJECT_PREFIX
                    + ruleParam.get(PacmanSdkConstants.RULE_ID));
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            if (toRecipients != null && toRecipients.size() > 0) {
                logger.debug("sending email to-->");
                toRecipients.stream().forEach(logger::debug);
                Map<String, Object> mailDetails = Maps.newHashMap();
                mailDetails.put("attachmentUrl", "");
                mailDetails.put("from", CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_FROM));
                mailDetails.put("mailBodyAsString", formateSilentFixBody(silentautoFixTrans, ruleParam));
                mailDetails.put("placeholderValues", Maps.newHashMap());
                mailDetails.put("subject", emailSubject);
                mailDetails.put("to",toRecipients );
                CommonUtils.doHttpPost(CommonUtils.getPropValue(PacmanSdkConstants.EMAIL_SERVICE_URL),
                        gson.toJson(mailDetails));
            }
        } catch (Exception e) {
            logger.error("error sending email", e);
        }
        return true;
    }
   /**
    * 
    * @param silentautoFixTrans
    * @param ruleParam
    * @return
    */
    public static String formateSilentFixBody(List<AutoFixTransaction> silentautoFixTrans,Map<String, String> ruleParam) {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setSuffix(".html");
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context();
        context.setVariable("resources", silentautoFixTrans);
        String policyUrl = CommonUtils.getPropValue(PacmanSdkConstants.POLICY_URL_PREFIX_KEY
                + ruleParam.get(PacmanSdkConstants.RULE_ID));
        String name =CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_SILENT_FIX_ADMIN
                + ruleParam.get(PacmanSdkConstants.RULE_ID));
        String postFixMessage = CommonUtils.getPropValue(PacmanSdkConstants.EMAIL_FIX_MESSAGE_PREFIX
                + ruleParam.get(PacmanSdkConstants.RULE_ID));
        context.setVariable("AUTOFIX_POST_FIX_MESSAGE", postFixMessage);
        context.setVariable("POLICY_URL", policyUrl);
        context.setVariable("NAME", " Hello "+name);
        context.setVariable("RESOURCE_TYPE", " Resource Type : "+ruleParam.get(PacmanSdkConstants.TARGET_TYPE));
        context.setVariable("AUTO_FIX_APPLIED", "Total AutoFixs Applied : "+silentautoFixTrans.size());
        StringWriter writer = new StringWriter();
        templateEngine.process("/template/autofix-silent-autoapptag-usernotification-info.html", context, writer);
        return writer.toString();
        
    }
    
  
    
}
