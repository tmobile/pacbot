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

import com.amazonaws.util.StringUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmobile.pacman.common.AutoFixAction;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.autofix.manager.NextStepManager;
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
                        gson.toJson(mailDetails),new HashMap<>());
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
     * @param ruleParam            the rule param
     * @param resourceOwner            the resource owner
     * @param targetType            the target type
     * @param resourceid            the resourceid
     * @param expiringTime            the expiring time
     * @param autofixActionEmail            the autofix action email
     * @param addDetailsToLogTrans the add details to log trans
     * @param annotation the annotation
     * @return true, if successful
     */
    public static boolean sendAutoFixNotification(Map<String, String> ruleParam, final ResourceOwner resourceOwner,
            final String targetType, final String resourceid, final String expiringTime,
            AutoFixAction autofixActionEmail,List<AutoFixTransaction> addDetailsToLogTrans,Map<String,String> annotation) {
        try {
        	
        	String accountId = annotation.get("accountid");
        	String accountName = annotation.get("accountname");
        	String region = annotation.get("region");
            List<String> toRecipients = Lists.newArrayList();
            if (resourceOwner != null && !Strings.isNullOrEmpty(resourceOwner.getEmailId())
                    && resourceOwner.getEmailId().contains("@")) {
                toRecipients.add(resourceOwner.getEmailId());
            } else {
                toRecipients.add(CommonUtils.getPropValue(PacmanSdkConstants.ORPHAN_RESOURCE_OWNER_EMAIL));
            }
            String policyUrl = getPolicyKnowledgeBasePathURL(ruleParam);
            String violationMessage = CommonUtils.getPropValue(PacmanSdkConstants.EMAIL_VIOLATION_MESSAGE_PREFIX
                    + ruleParam.get(PacmanSdkConstants.RULE_ID));
            String postFixMessage = CommonUtils.getPropValue(PacmanSdkConstants.EMAIL_FIX_MESSAGE_PREFIX
                    + ruleParam.get(PacmanSdkConstants.RULE_ID));
            if (!Strings.isNullOrEmpty(violationMessage)) {
                Map<String, String> data = new HashMap<>();
                data.put("RESOURCE_ID", resourceid);
                data.put("ACCOUNT_ID", accountId);
                data.put("REGION", region);
                violationMessage = StrSubstitutor.replace(violationMessage, data);
                postFixMessage = StrSubstitutor.replace(postFixMessage, data);
            }
            String warning = CommonUtils.getPropValue(PacmanSdkConstants.EMAIL_WARNING_MESSAGE_PREFIX
                    + ruleParam.get(PacmanSdkConstants.RULE_ID));
            Integer autoFixDealy = NextStepManager.getAutoFixDelay(ruleParam.get(PacmanSdkConstants.RULE_ID));
            if(autoFixDealy!=null){
               warning = warning.replace("{days}", "" + Math.toIntExact(autoFixDealy/24));
            }
            String emailCCList = CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_CC_KEY);
            toRecipients.addAll(Arrays.asList(emailCCList.split("\\s*,\\s*")));
            String templateName = "";
            Map<String, Object> placeholderValues = Maps.newHashMap();
            placeholderValues.put("NAME", resourceOwner != null ? resourceOwner.getName() : "");
            placeholderValues.put("POLICY_URL", policyUrl);
            placeholderValues.put("RESOURCE_ID", resourceid);
            placeholderValues.put("ACCOUNT_ID", accountId);
            placeholderValues.put("REGION", region);
            placeholderValues.put("TIME", expiringTime);
            placeholderValues.put("RULE_VIOLATION_MESSAGE", violationMessage);
            placeholderValues.put("AUTOFIX_WARNING_MESSAGE", warning);
            placeholderValues.put("AUTOFIX_POST_FIX_MESSAGE", postFixMessage);
            placeholderValues.put("AUTOFIX_EXPIRY_TIME", expiringTime);
            placeholderValues.put("EMAIL_BANNER", CommonUtils.getPropValue(PacmanSdkConstants.EMAIL_BANNER));
            
            String emailSubject = "Pacman AutoFix Reminder";
            if (autofixActionEmail == AutoFixAction.AUTOFIX_ACTION_EMAIL && "Sandbox".equalsIgnoreCase(accountName)) {
                templateName = "autofix-user-notification-info";
                emailSubject = "(Sandbox) : "+ CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_WARNING_SUBJECT_PREFIX
                        + ruleParam.get(PacmanSdkConstants.RULE_ID));
            }else if (autofixActionEmail == AutoFixAction.AUTOFIX_ACTION_EMAIL) {
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
            if((null!=CommonUtils.getPropValue("pacman.auto.fix.common.email.notifications."
                        + ruleParam.get(PacmanSdkConstants.RULE_ID)))&&  CommonUtils.getPropValue("pacman.auto.fix.common.email.notifications."
                                + ruleParam.get(PacmanSdkConstants.RULE_ID)).equals("commonTemplate") && autofixActionEmail == AutoFixAction.AUTOFIX_ACTION_FIX ){
            	return sendCommonFixNotification(addDetailsToLogTrans, ruleParam, resourceOwner, targetType);
            }else{
            return sendPlainTextMail(toRecipients, CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_FROM),
                    emailSubject, placeholderValues, templateName);
            }
        } catch (Exception e) {
            logger.error("error sending email", e);
        }
        return false;
    }

    
    /**
     * Send common fix notification.
     *
     * @param silentautoFixTrans the silentauto fix trans
     * @param ruleParam the rule param
     * @param resourceOwner the resource owner
     * @param targetType the target type
     * @return true, if successful
     */
     public static boolean sendCommonFixNotification(List<AutoFixTransaction> silentautoFixTrans, Map<String, String> ruleParam,
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
                 mailDetails.put("mailBodyAsString", formateCommonFixBody(silentautoFixTrans, ruleParam,resourceOwner));
                 mailDetails.put("placeholderValues", Maps.newHashMap());
                 mailDetails.put("subject", emailSubject);
                 mailDetails.put("to",toRecipients );
                 CommonUtils.doHttpPost(CommonUtils.getPropValue(PacmanSdkConstants.EMAIL_SERVICE_URL),
                         gson.toJson(mailDetails),new HashMap<>());
             }
         } catch (Exception e) {
             logger.error("error sending email", e);
         }
         return true;
     }
     
     /**
      * Formate common fix body.
      *
      * @param silentautoFixTrans the silentauto fix trans
      * @param ruleParam the rule param
      * @param resourceOwner the resource owner
      * @return the string
      */
      public static String formateCommonFixBody(List<AutoFixTransaction> silentautoFixTrans,Map<String, String> ruleParam,ResourceOwner resourceOwner) {
          TemplateEngine templateEngine = new TemplateEngine();
          ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
          templateResolver.setTemplateMode("HTML");
          templateResolver.setSuffix(".html");
          templateEngine.setTemplateResolver(templateResolver);
          
          List<String> columnsList = Arrays.asList(CommonUtils.getPropValue(PacmanSdkConstants.PACMAN_MAIL_TEMPLATE_COLUMNS
                  + ruleParam.get(PacmanSdkConstants.RULE_ID)).split("\\s*,\\s*"));
         
          Context context = new Context(); 
  
          context.setVariable("columns", columnsList);
          context.setVariable("resources", silentautoFixTrans);
          String policyUrl = getPolicyKnowledgeBasePathURL(ruleParam);
          String name =CommonUtils.getPropValue(PacmanSdkConstants.SEND_EMAIL_SILENT_FIX_ADMIN
                  + ruleParam.get(PacmanSdkConstants.RULE_ID));
          
          if(StringUtils.isNullOrEmpty(name)){
        	 name = resourceOwner.getName(); 
          }
          String postFixMessage = CommonUtils.getPropValue(PacmanSdkConstants.EMAIL_FIX_MESSAGE_PREFIX
                  + ruleParam.get(PacmanSdkConstants.RULE_ID));
          context.setVariable("AUTOFIX_POST_FIX_MESSAGE", postFixMessage);
          context.setVariable("POLICY_URL", policyUrl);
          context.setVariable("NAME", "Hello "+name);
          context.setVariable("RESOURCE_TYPE", " Resource Type : "+ruleParam.get(PacmanSdkConstants.TARGET_TYPE));
          context.setVariable("AUTO_FIX_APPLIED", "Total AutoFixs Applied : "+silentautoFixTrans.size());
          StringWriter writer = new StringWriter();
          
        if(CommonUtils.getPropValue("pacman.auto.fix.common.email.notifications."
                  + ruleParam.get(PacmanSdkConstants.RULE_ID)).equals("commonTemplate")){
          	templateEngine.process("/template/autofix-user-notification-action-common.html", context, writer);
      }else{
          	templateEngine.process("/template/autofix-silent-autodelete-usernotification-info.html", context, writer);
          }
          return writer.toString();
          
      }
      
      /**
       * Gets the policy knowledge base path URL.
       *
       * @param ruleParam the rule param
       * @return the policy knowledge base path URL
       */
      private static String getPolicyKnowledgeBasePathURL(Map<String,String> ruleParam){
    	  String policyUrl = CommonUtils.getPropValue(PacmanSdkConstants.POLICY_URL_PATH);
          Map<String, String> policyUrlMap = new HashMap<>();
          policyUrlMap.put("RULE_ID", ruleParam.get(PacmanSdkConstants.RULE_ID));
          policyUrl = StrSubstitutor.replace(policyUrl, policyUrlMap);
          return policyUrl;
      }

}
