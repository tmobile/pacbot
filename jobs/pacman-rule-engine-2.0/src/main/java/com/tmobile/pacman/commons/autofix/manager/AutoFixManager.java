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

package com.tmobile.pacman.commons.autofix.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.regions.Regions;
import com.amazonaws.util.CollectionUtils;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmobile.pacman.common.AutoFixAction;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.commons.autofix.FixResult;
import com.tmobile.pacman.commons.aws.clients.AWSClientManager;
import com.tmobile.pacman.commons.aws.clients.impl.AWSClientManagerImpl;
import com.tmobile.pacman.commons.exception.UnableToCreateClientException;
import com.tmobile.pacman.dto.AutoFixTransaction;
import com.tmobile.pacman.dto.IssueException;
import com.tmobile.pacman.dto.ResourceOwner;
import com.tmobile.pacman.publisher.impl.ElasticSearchDataPublisher;
import com.tmobile.pacman.service.ExceptionManager;
import com.tmobile.pacman.service.ExceptionManagerImpl;
import com.tmobile.pacman.service.ResourceOwnerService;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ESUtils;
import com.tmobile.pacman.util.MailUtils;
import com.tmobile.pacman.util.ReflectionUtils;

/**
 * The Class AutoFixManager.
 *
 * @author kkumar
 */
public class AutoFixManager {

    /** The Constant logger. */

    private static final Logger logger = LoggerFactory.getLogger(AutoFixManager.class);

    
    Map<String,String> targetTypeAlias;
    
    /**
     * Perform auto fixs.
     *
     * @param ruleParam the rule param
     * @param exemptedResourcesForRule the exempted resources for rule
     * @param individuallyExcemptedIssues the individually excempted issues
     * @return the map
     * @throws Exception the exception
     */

    
    /**
     * 
     */
    public AutoFixManager() {
        targetTypeAlias = new HashMap<>();
        String alias = CommonUtils.getPropValue(PacmanSdkConstants.TARGET_TYPE_ALIAS);
        if(!Strings.isNullOrEmpty(alias))
         {
            targetTypeAlias = Splitter.on(",").withKeyValueSeparator("=").split(alias);
         }
             }
        
   /**
    *  
    * @param ruleParam
    * @param exemptedResourcesForRule
    * @param individuallyExcemptedIssues
    * @return
    * @throws Exception
    */
    public Map<String, Object> performAutoFixs(Map<String, String> ruleParam,
            Map<String, List<IssueException>> exemptedResourcesForRule,
            Map<String, IssueException> individuallyExcemptedIssues) throws Exception {

        List<Map<String, String>> existingIssues = null;
        String ruleId = ruleParam.get(PacmanSdkConstants.RULE_ID);
        ResourceOwnerService ownerService = new ResourceOwnerService();
        NextStepManager nextStepManager = new NextStepManager();
        ResourceTaggingManager taggingManager = new ResourceTaggingManager();
        AutoFixAction autoFixAction;
        List<FixResult> fixResults = new ArrayList<>();
        AWSService serviceType = null;
        ResourceOwner resourceOwner = null;
        String resourceId = null;
        String targetType = null;
        String annotationId = null;
        String exceptionExpiryDate = null;
        Class<?> fixClass = null;
        Object fixObject = null;
        Method executeMethod = null;
        Method backupMethod = null;
        Method isFixCandidateMethod=null;
        Method addDetailsToTransactionLogMethod=null;
        String fixKey = null;
        Map<String, Object> autoFixStats = new HashMap<>();
        List<AutoFixTransaction> autoFixTrans = new ArrayList<>();
        List<AutoFixTransaction> silentautoFixTrans = new ArrayList<>();
        Map<String, Object> clientMap = null;

        Integer resourcesTaggedCounter = 0;
        Integer notificationSentCounter = 0;
        Integer autoFixCounter = 0;
        Integer errorWhileTaggingCounter = 0;
        Integer resourceOwnerNotFoundCounter = 0;
        Integer didNothingCounter = 0;
        Integer backupConfigCounter = 0;

        String executionId = ruleParam.get(PacmanSdkConstants.EXECUTION_ID);
        String transactionId = null;

        MDC.put("executionId", executionId);
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
        
        String type = "autofix";

        // check fix exists for rule
        try {
                fixKey = ruleParam.get("fixKey");
                fixClass = ReflectionUtils.findFixClass(fixKey);
                fixObject = fixClass.newInstance();
                executeMethod = ReflectionUtils.findAssociatedMethod(fixObject, "executeFix");
                backupMethod = ReflectionUtils.findAssociatedMethod(fixObject, "backupExistingConfigForResource");
                isFixCandidateMethod = findIsFixCandidateMethod(fixObject, isFixCandidateMethod);
                try{
                    addDetailsToTransactionLogMethod = ReflectionUtils.findAssociatedMethod(fixObject, "addDetailsToTransactionLog");
                }catch (NoSuchMethodException e) {
                    logger.debug("addDetailsToTransactionLog method not implemented",e);
                }
        } catch (Exception e) {
            logger.error(String.format("Please check the rule class complies to implemetation contract, fix key= %s" , fixKey), e);
            autoFixStats.put("auto-fix-enabled", true);
            autoFixStats.put("auto-fix-error", "error finding fix class - >" + e.getMessage());
            return autoFixStats;
        }

        
        try {
            existingIssues = getOpenAndExcepmtedAnnotationForRule(ruleParam);
        } catch (Exception e) {
            logger.error("unable to get open issue for rule" + ruleId);
            autoFixStats.put("auto-fix-error", "unable to get open issue for rule" + ruleId + "-- >" + e.getMessage());
            return autoFixStats;
        }

        for (Map<String, String> annotation : existingIssues) {
        	List<AutoFixTransaction> addDetailsToLogTrans = new ArrayList<>();
            targetType = annotation.get("targetType");
            resourceId =annotation.get("_resourceid");
            transactionId = CommonUtils.getUniqueIdForString(resourceId);
               annotationId = annotation.get(PacmanSdkConstants.ES_DOC_ID_KEY); // this
                                                                             // will
                                                                             // be
                                                                             // used
                                                                             // to
                                                                             // identify
                                                                             // the
                                                                             // exception
                                                                             // for
                                                                             // the
                                                                             // resource
            targetType =  getTargetTypeAlias(targetType);
            
            
            serviceType = AWSService.valueOf(targetType.toUpperCase());
            
            // create client
            if(isAccountWhiteListedForAutoFix(annotation.get(PacmanSdkConstants.ACCOUNT_ID),ruleParam.get(PacmanSdkConstants.RULE_ID))){
            	clientMap = getAWSClient(targetType, annotation, CommonUtils.getPropValue(PacmanSdkConstants.AUTO_FIX_ROLE_NAME));
            }else{
            	 logger.info("Account id is not whitelisted {}" , annotation.get(PacmanSdkConstants.ACCOUNT_ID));
            	continue;
            }
            logger.debug(String.format("processing for %s " , resourceId));
            if ((!isResourceTypeExemptedFromCutOfDateCriteria(targetType)
                       && resourceCreatedBeforeCutoffData(resourceId, targetType))
                    || !isresourceIdMatchesCriteria(resourceId) ||!isAFixCandidate(isFixCandidateMethod,fixObject, resourceId, targetType, clientMap, ruleParam,annotation)) {
                logger.debug(String.format("exempted by various conditions --> %s " , resourceId));
                continue;
            }
            logger.debug(String.format("not exempted by conditions --> %s " , resourceId));
          
            // if resource is exempted tag the resource
            String issueStatus = annotation.get("issueStatus");

            // find resource owner
            resourceOwner = ownerService.findResourceOwnerByIdAndType(resourceId, serviceType);
            autoFixAction = nextStepManager.getNextStep(ruleParam.get(PacmanSdkConstants.RULE_ID),resourceId, clientMap, serviceType);
            if(AutoFixAction.UNABLE_TO_DETERMINE==autoFixAction){
                autoFixTrans.add(new AutoFixTransaction(AutoFixAction.UNABLE_TO_DETERMINE, resourceId,ruleId,
                        executionId, transactionId, "unable to determine the next set of action , not processing for this pass",type,annotation.get("targetType"),annotationId,annotation.get(PacmanSdkConstants.ACCOUNT_ID),annotation.get(PacmanSdkConstants.REGION)));
                continue;
            }
            
            if (PacmanSdkConstants.ISSUE_STATUS_EXEMPTED_VALUE.equals(issueStatus)) {
                try {
                    // get the exception
                    // 1: check if individual exception exists
                    // individuallyExcemptedIssues.get(annotation.get(key));
                    // notify resource owner about exemption if he was already
                    // notified for violation
                    // check the next step

                    exceptionExpiryDate = getMaxExceptionExpiry(annotationId, resourceId, exemptedResourcesForRule,
                            individuallyExcemptedIssues);

                    if (AutoFixAction.AUTOFIX_ACTION_FIX == autoFixAction) {
                        Map<String, String> pacTag = createPacTag(exceptionExpiryDate);
                        taggingManager.tagResource(resourceId, clientMap, serviceType, pacTag);
                        autoFixTrans.add(new AutoFixTransaction(AutoFixAction.AUTOFIX_ACTION_TAG, resourceId,ruleId,
                                executionId, transactionId, "resource tagged",type,annotation.get("targetType"),annotationId,annotation.get(PacmanSdkConstants.ACCOUNT_ID),annotation.get(PacmanSdkConstants.REGION)));
                        resourcesTaggedCounter++;
                        // this means this resource was exempted after sending
                        // the violation emails and exempted afterwards
                        if (!nextStepManager.isSilentFixEnabledForRule(ruleId) && !MailUtils.sendAutoFixNotification(ruleParam, resourceOwner, targetType, resourceId,
                                exceptionExpiryDate, AutoFixAction.AUTOFIX_ACTION_EXEMPTED,addDetailsToLogTrans,annotation)) {
                            logger.error(String.format("unable to send email to %s" ,resourceOwner.toString()));
                        }
                    }

                    // should be removed for deployment
                    // throw new Exception("in case you run it by mistake it
                    // will tag all buckets , hence checking in with this guard
                    // rail");
                    continue;
                } catch (Exception e) {
                    logger.error("error while tagging the resource", e);
                    errorWhileTaggingCounter++;
                    continue;
                }
            } else {
                try {

                    logger.debug(String.format("found the resource Owner %s" , resourceOwner.toString()));
                    if (Strings.isNullOrEmpty(resourceOwner.getEmailId())
                            && !Strings.isNullOrEmpty(resourceOwner.getName())
                            && resourceOwner.getName().contains("@")) { // case
                                                                        // when
                                                                        // name
                                                                        // contains
                                                                        // email
                        resourceOwner.setEmailId(resourceOwner.getName());
                    }

                    if (!resourceOwner.getEmailId().contains("@")) { // service
                                                                     // account
                                                                     // case, in
                                                                     // this
                                                                     // case it
                                                                     // is a
                                                                     // service
                                                                     // account
                                                                     // name
                        resourceOwner
                                .setEmailId(CommonUtils.getPropValue(PacmanSdkConstants.ORPHAN_RESOURCE_OWNER_EMAIL));
                    }

                    
                } catch (Exception e) {
                    logger.error(String.format("unable to find the resource owner for %s " , resourceId));
                    resourceOwner = new ResourceOwner("CSO",
                            CommonUtils.getPropValue(PacmanSdkConstants.ORPHAN_RESOURCE_OWNER_EMAIL));
                    resourceOwnerNotFoundCounter++;
                }

                if (AutoFixAction.DO_NOTHING == autoFixAction) {
                    didNothingCounter++;
                    autoFixTrans.add(new AutoFixTransaction(AutoFixAction.DO_NOTHING, resourceId,ruleId, executionId,
                            transactionId, "waiting for 24 hours before fixing the violation",type,annotation.get("targetType"),annotationId,annotation.get(PacmanSdkConstants.ACCOUNT_ID),annotation.get(PacmanSdkConstants.REGION)));

                    continue;
                }

                if (AutoFixAction.AUTOFIX_ACTION_EMAIL == autoFixAction
                        && isAccountWhiteListedForAutoFix(annotation.get(PacmanSdkConstants.ACCOUNT_ID), ruleId)) {
                    long autofixExpiring=nextStepManager.getAutoFixExpirationTimeInHours(ruleParam.get(PacmanSdkConstants.RULE_ID),resourceId);
                	/*ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Los_Angeles")).plusHours(Integer.parseInt(CommonUtils.getPropValue(PacmanSdkConstants.PAC_AUTO_FIX_DELAY_KEY
                            +"."+ ruleParam.get(PacmanSdkConstants.RULE_ID))));*/
                    ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Los_Angeles")).plusHours(autofixExpiring);
                    String expiringTime = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    if (!MailUtils.sendAutoFixNotification(ruleParam, resourceOwner, targetType, resourceId,
                            expiringTime, AutoFixAction.AUTOFIX_ACTION_EMAIL,addDetailsToLogTrans,annotation)) {
                        logger.error(String.format("unable to send email to %s" , resourceOwner.toString()));
                        continue; // notification was not sent, skip further
                                  // execution
                    }
                    logger.debug(String.format("email sent to %s" , resourceOwner.toString()));
                    autoFixTrans.add(new AutoFixTransaction(AutoFixAction.AUTOFIX_ACTION_EMAIL, resourceId,ruleId, executionId,
                            transactionId, "email sent to " + resourceOwner.getEmailId(),type,annotation.get("targetType"),annotationId,annotation.get(PacmanSdkConstants.ACCOUNT_ID),annotation.get(PacmanSdkConstants.REGION)));
                    notificationSentCounter++;
                    try {
                        nextStepManager.postFixAction(resourceId, AutoFixAction.EMAIL);
                    } catch (Exception e) {
                        logger.error(String.format("unable to post email action for %s ", resourceId));
                    }

                    fixResults.add(new FixResult(PacmanSdkConstants.STATUS_SUCCESS_CODE,
                            String.format("email sent to owner of resource %s" , resourceId)));
                    continue;
                } else {
                    if (AutoFixAction.AUTOFIX_ACTION_FIX == autoFixAction) {
                        try {
                                try {
                                       backupMethod.invoke(fixObject, resourceId, targetType, clientMap, ruleParam,annotation);
                                } catch (Exception e) {
                                    logger.error(String.format(
                                            "unable to backup the configuration for %s and %s" , targetType ,resourceId));
                                    continue;
                                }
                                autoFixTrans.add(new AutoFixTransaction(AutoFixAction.AUTOFIX_ACTION_BACKUP, resourceId,ruleId,
                                        executionId, transactionId, "resource aconfig backedup",type,annotation.get("targetType"),annotationId,annotation.get(PacmanSdkConstants.ACCOUNT_ID),annotation.get(PacmanSdkConstants.REGION)));
                                backupConfigCounter++;
                                FixResult result = (FixResult) executeMethod.invoke(fixObject, annotation, clientMap, ruleParam);
                                fixResults
                                        .add(result);
                                autoFixTrans.add(new AutoFixTransaction(AutoFixAction.AUTOFIX_ACTION_FIX, resourceId,ruleId,
                                        executionId, transactionId, result.toString(),type,annotation.get("targetType"),annotationId,annotation.get(PacmanSdkConstants.ACCOUNT_ID),annotation.get(PacmanSdkConstants.REGION)));
                                if (!nextStepManager.isSilentFixEnabledForRule(ruleId)){
                                	
                                	if(null!=addDetailsToTransactionLogMethod){
                                		addDetailsToLogTrans.add((AutoFixTransaction) addDetailsToTransactionLogMethod.invoke(fixObject,annotation));
                                    }
                                	MailUtils.sendAutoFixNotification(ruleParam, resourceOwner, targetType, resourceId, "",
                                        AutoFixAction.AUTOFIX_ACTION_FIX,addDetailsToLogTrans,annotation);
                                    logger.debug(String.format("autofixed the resource %s and email sent to  %s",
                                            resourceId, resourceOwner.toString()));
                                }
//                                    if(annotation.get("policyId").equalsIgnoreCase("PacMan_ApplicationTagsShouldBeValid_version-1")){
//                                    silentautoFixTrans.add(new AutoFixTransaction(resourceId, ruleId,annotation.get("accountid") , annotation.get("region"),annotation.get("correct_application_tag")));
//                                    }
                                    else{
                                    logger.error(String.format("unable to send email to %s " , resourceOwner.toString()));
                                   
                                    }
                                if(nextStepManager.isSilentFixEnabledForRule(ruleId) && null!=addDetailsToTransactionLogMethod){
                                    silentautoFixTrans.add((AutoFixTransaction) addDetailsToTransactionLogMethod.invoke(fixObject,annotation));
                                }
                                                                
                                autoFixCounter++;
                        } catch (Exception e) {
                            logger.error(String.format("unable to execute auto fix for %s  will not fix at this time",resourceId),
                                    e);
                            // continue with next bucket
                            continue;
                        }
                    } else if (AutoFixAction.AUTOFIX_ACTION_EMAIL_REMIND_EXCEPTION_EXPIRY == autoFixAction) {

                        if (!MailUtils.sendAutoFixNotification(ruleParam, resourceOwner, targetType, resourceId, "",
                                AutoFixAction.AUTOFIX_ACTION_EMAIL_REMIND_EXCEPTION_EXPIRY,addDetailsToLogTrans,annotation)) {
                            logger.error(String.format("unable to send email to %s" , resourceOwner.toString()));
                        }
                    }
                }
            } // if issue open
        
}// for
        //Silent fix send Digest email
        if(!silentautoFixTrans.isEmpty() && nextStepManager.isSilentFixEnabledForRule(ruleId)){
            
            MailUtils.sendCommonFixNotification(silentautoFixTrans, ruleParam, resourceOwner, targetType);
        }
        // publish the transactions here
        // if any transaction exists post it
        if (autoFixTrans != null && autoFixTrans.size() > 0) {
            ElasticSearchDataPublisher dataPublisher =  new ElasticSearchDataPublisher();
            dataPublisher.publishAutoFixTransactions(autoFixTrans,ruleParam);
            dataPublisher.close();
        }
        
        autoFixStats.put("autoFixCounter", autoFixCounter);
        autoFixStats.put("resourcesTaggedCounter", resourcesTaggedCounter);
        autoFixStats.put("notificationSentCounter", notificationSentCounter);
        autoFixStats.put("errorWhileTagging", errorWhileTaggingCounter);
        autoFixStats.put("resourceOwnerNotFound", resourceOwnerNotFoundCounter);
        autoFixStats.put("didNothingCounter", didNothingCounter);
        autoFixStats.put("backupConfigCounter", backupConfigCounter);

        return autoFixStats;
    }
    
   
    /**
     * @param targetType
     * @return
     */
    private String getTargetTypeAlias(String targetType) {
        return targetTypeAlias.get(targetType)==null?targetType:targetTypeAlias.get(targetType);
    }


    /**
     * find the method isfixCandidate.
     *
     * @param fixObject the fix object
     * @param isFixCandidateMethod the is fix candidate method
     * @return the method
     */
    private Method findIsFixCandidateMethod(Object fixObject, Method isFixCandidateMethod) {
        try{
                isFixCandidateMethod =  ReflectionUtils.findAssociatedMethod(fixObject, "isFixCandidate");
        }catch(Exception e){
            logger.debug("isFixCandidateMethod not implemented will use the default value true",e);
        }
        return isFixCandidateMethod;
    }

    /**
     * Checks if is a fix candidate.
     *
     * @param isFixCandidateMethod the is fix candidate method
     * @param fixObject the fix object
     * @param resourceId the resource id
     * @param targetType the target type
     * @param clientMap the client map
     * @param ruleParam the rule param
     * @param annotation the annotation
     * @return true, if is a fix candidate
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     */
    private boolean isAFixCandidate(Method isFixCandidateMethod, Object fixObject, String resourceId, String targetType,
            Map<String, Object> clientMap, Map<String, String> ruleParam, Map<String, String> annotation) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        logger.debug("is fix candidate ==> " + (null==isFixCandidateMethod?true:(Boolean)isFixCandidateMethod.invoke(fixObject, resourceId, targetType, clientMap, ruleParam,annotation)));
       return null==isFixCandidateMethod?true:(Boolean)isFixCandidateMethod.invoke(fixObject, resourceId, targetType, clientMap, ruleParam,annotation);
    }

    /**
     * Checks if is resource type exempted from cut of date criteria.
     *
     * @param targetType the target type
     * @return true, if is resource type exempted from cut of date criteria
     */
    private boolean isResourceTypeExemptedFromCutOfDateCriteria(String targetType) {

        try {
            List<String> exemptedtypes = Arrays
                    .asList(CommonUtils.getPropValue(PacmanSdkConstants.AUTOFIX_EXEMPTED_TYPES_KEY).split("\\s*,\\s*"));
            return exemptedtypes.contains(targetType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the max exception expiry.
     *
     * @param annotationId the annotation id
     * @param resourceId the resource id
     * @param exemptedResourcesForRule the exempted resources for rule
     * @param individuallyExcemptedIssues the individually excempted issues
     * @return the max exception expiry
     * @throws Exception the exception
     */
    private String getMaxExceptionExpiry(String annotationId, String resourceId,
            Map<String, List<IssueException>> exemptedResourcesForRule,
            Map<String, IssueException> individuallyExcemptedIssues) throws Exception {

        // check if resource exempted using sticky exception
        List<IssueException> issueExceptions = exemptedResourcesForRule.get(resourceId);
        if (!CollectionUtils.isNullOrEmpty(issueExceptions)) {
            // get the max expiry date exception
        }
        // get individual exception details
        IssueException issueException = individuallyExcemptedIssues.get(annotationId);
        if (issueException != null) {
            return issueException.getExpiryDate();
        } else {
            throw new Exception("unable to find expiry date");
        }
    }

    /**
     * This will help testing the auto fix function.
     *
     * @param resourceId the resource id
     * @return true, if is resource id matches criteria
     */
    private boolean isresourceIdMatchesCriteria(String resourceId) {

        Pattern p;
        if (Strings.isNullOrEmpty(resourceId) || Strings
                .isNullOrEmpty(CommonUtils.getPropValue(PacmanSdkConstants.FIX_ONLY_MATCHING_RESOURCE_PATTERN))) {
            // resource with no name, this method has no responsibility to fix
            // this
            return true;
        }
        try {
            p = Pattern.compile(CommonUtils.getPropValue(PacmanSdkConstants.FIX_ONLY_MATCHING_RESOURCE_PATTERN));
        } catch (Exception e) {
            logger.info("no resource filter pattern defined");
            return true;
        }
        Matcher m = p.matcher(resourceId.toLowerCase());
        return m.find();

    }

    /**
     * Gets the AWS client.
     *
     * @param targetType the target type
     * @param annotation the annotation
     * @param ruleIdentifyingString the rule identifying string
     * @return the AWS client
     * @throws Exception the exception
     */
    private Map<String, Object> getAWSClient(String targetType, Map<String, String> annotation,
            String ruleIdentifyingString) throws Exception {

        StringBuilder roleArn = new StringBuilder();
        Map<String, Object> clientMap = null;
        roleArn.append(PacmanSdkConstants.ROLE_ARN_PREFIX).append(annotation.get(PacmanSdkConstants.ACCOUNT_ID))
                .append(":").append(ruleIdentifyingString);

        AWSClientManager awsClientManager = new AWSClientManagerImpl();
        try {
            clientMap = awsClientManager.getClient(annotation.get(PacmanSdkConstants.ACCOUNT_ID), roleArn.toString(),
                    AWSService.valueOf(targetType.toUpperCase()), Regions.fromName(
                            annotation.get(PacmanSdkConstants.REGION) == null ? Regions.DEFAULT_REGION.getName()
                                    : annotation.get(PacmanSdkConstants.REGION)),
                    ruleIdentifyingString);
        } catch (UnableToCreateClientException e1) {
            String msg = String.format("unable to create client for account %s  and region %s" , annotation.get(PacmanSdkConstants.ACCOUNT_ID),annotation.get(PacmanSdkConstants.REGION));
            logger.error(msg);
            throw new Exception(msg);
        }
        return clientMap;
    }

    /**
     * Resource created before cutoff data.
     *
     * @param resourceid the resourceid
     * @param resourceType the resource type
     * @return true, if successful
     */
    private boolean resourceCreatedBeforeCutoffData(final String resourceid, String resourceType) {

        // Call service to find the resource creation date and check from cutoff
        // date defined in properties file return false if unable to determine
        try {
            return CommonUtils.resourceCreatedBeforeCutoffData(getResourceCreatedDate(resourceid, resourceType));
        } catch (Exception e) {
            // cannot find using heimdall, the fix shall expose a method to get
            // the resource specific creation date, call that method here and
            // get the creation date
            // for now returning true to indicate resource was created befroe
            // cutoff
            return true;
        }
    }

    /**
     * Gets the resource created date.
     *
     * @param resourceId the resource id
     * @param resourceType the resource type
     * @return the resource created date
     * @throws Exception the exception
     */
    @SuppressWarnings("unchecked")
    private Date getResourceCreatedDate(final String resourceId, String resourceType) throws Exception {
        String response = "";
        try {
            String url = CommonUtils.getPropValue(PacmanSdkConstants.RESOURCE_CREATIONDATE);
            url = url.concat("?resourceId=").concat(resourceId).concat("&resourceType=").concat(resourceType);
            response = CommonUtils.doHttpGet(url);
            if (!Strings.isNullOrEmpty(response)) {
                Gson serializer = new GsonBuilder().setLenient().create();
                Map<String, Object> resourceDetailsMap = (Map<String, Object>) serializer.fromJson(response,
                        Object.class);
                String resourceCreationDateString = resourceDetailsMap.get("data").toString();
                if ("Resource Not Found".equals(resourceCreationDateString))
                    throw new Exception("resource not found in heimdal");
                return CommonUtils.dateFormat(resourceCreationDateString, PacmanSdkConstants.YYYY_MM_DD_T_HH_MM_SS_Z,
                        PacmanSdkConstants.MM_DD_YYYY);
            }
            throw new Exception("unable to find resource creation date");
        } catch (Exception exception) {
            logger.error(String.format("Cannot find resource creation data " + " response from service--> %s" , response), exception);
            throw exception;
        }
    }

    /**
     * Checks if is account white listed for auto fix.
     *
     * @param account the account
     * @param ruleId the rule id
     * @return true, if is account white listed for auto fix
     */
    private boolean isAccountWhiteListedForAutoFix(String account, String ruleId) {
        try {
            String whitelistStr = CommonUtils
                    .getPropValue(PacmanSdkConstants.AUTOFIX_WHITELIST_ACCOUNTS_PREFIX + ruleId);
            List<String> whitelist = Arrays.asList(whitelistStr.split("\\s*,\\s*"));
            return whitelist.contains(account);
        } catch (Exception e) {
            logger.error(String.format("account not whitelisted for autofix for ruleId %s" , ruleId));
            return Boolean.FALSE;
        }
    }

    /**
     * Gets the open and excepmted annotation for rule.
     *
     * @param ruleParam the rule param
     * @return the open and excepmted annotation for rule
     * @throws Exception the exception
     */
    private List<Map<String, String>> getOpenAndExcepmtedAnnotationForRule(Map<String, String> ruleParam)
            throws Exception {

        String esUrl = ESUtils.getEsUrl();
        String ruleId = ruleParam.get(PacmanSdkConstants.RULE_ID);
        String indexName = CommonUtils.getIndexNameFromRuleParam(ruleParam);
        String attributeToQuery = ESUtils.convertAttributetoKeyword(PacmanSdkConstants.RULE_ID);
        Map<String, Object> mustFilter = new HashMap<>();
        mustFilter.put(attributeToQuery, ruleId);
        mustFilter.put("type.keyword", "issue");
        HashMultimap<String, Object> shouldFilter = HashMultimap.create();
        shouldFilter.put(ESUtils.convertAttributetoKeyword(PacmanSdkConstants.ISSUE_STATUS_KEY),
                PacmanSdkConstants.STATUS_OPEN);
        shouldFilter.put(ESUtils.convertAttributetoKeyword(PacmanSdkConstants.ISSUE_STATUS_KEY),
                PacmanSdkConstants.STATUS_EXEMPTED);
        List<String> fields = new ArrayList<String>();
        Long totalDocs = ESUtils.getTotalDocumentCountForIndexAndType(esUrl, indexName, null, mustFilter, null,
                shouldFilter);
        // get all the issues for this ruleId
        List<Map<String, String>> existingIssues = ESUtils.getDataFromES(esUrl, indexName.toLowerCase(), null,
                mustFilter, null, shouldFilter, fields, 0, totalDocs);
        return existingIssues;
    }

    /**
     * creates a tag for resource.
     *
     * @param exceptionDetails the exception details
     * @return the map
     * @throws Exception the exception
     */
    private Map<String, String> createPacTag(String exceptionDetails) throws Exception {
        String pacTagName = CommonUtils.getPropValue(PacmanSdkConstants.PACMAN_AUTO_FIX_TAG_NAME);
        // String pacTagValue = CommonUtils.encrypt(exceptionDetails,
        // CommonUtils.getPropValue(PacmanSdkConstants.PAC_AUTO_TAG_SALT_KEY));
        String pacTagValue = CommonUtils.encryptB64(exceptionDetails);
        Map<String, String> tagMap = new HashMap<>();
        tagMap.put(pacTagName, pacTagValue);
        return tagMap;
    }
    
    

    /**
     * test the code locally.
     *
     * @param args the arguments

     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        CommonUtils.getPropValue(PacmanSdkConstants.ORPHAN_RESOURCE_OWNER_EMAIL);

        Map<String, String> ruleParam = CommonUtils.createParamMap(args[0]);
        ExceptionManager exceptionManager = new ExceptionManagerImpl();
        Map<String, List<IssueException>> excemptedResourcesForRule = exceptionManager.getStickyExceptions(
                ruleParam.get(PacmanSdkConstants.RULE_ID), ruleParam.get(PacmanSdkConstants.TARGET_TYPE));
        Map<String, IssueException> individuallyExcemptedIssues = exceptionManager
                .getIndividualExceptions(ruleParam.get(PacmanSdkConstants.TARGET_TYPE));
       AutoFixManager autoFixManager = new AutoFixManager();
       autoFixManager.performAutoFixs(ruleParam, excemptedResourcesForRule, individuallyExcemptedIssues);


}
}
