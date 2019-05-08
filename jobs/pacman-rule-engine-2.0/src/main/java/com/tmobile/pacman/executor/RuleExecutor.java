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


package com.tmobile.pacman.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.autofix.manager.AutoFixManager;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;
import com.tmobile.pacman.dto.IssueException;
import com.tmobile.pacman.integrations.slack.SlackMessageRelay;
import com.tmobile.pacman.publisher.impl.AnnotationPublisher;
import com.tmobile.pacman.reactors.PacEventHandler;
import com.tmobile.pacman.service.ExceptionManager;
import com.tmobile.pacman.service.ExceptionManagerImpl;
import com.tmobile.pacman.util.AuditUtils;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ESUtils;
import com.tmobile.pacman.util.ProgramExitUtils;
import com.tmobile.pacman.util.ReflectionUtils;
import com.tmobile.pacman.util.RuleExecutionUtils;


// TODO: Auto-generated Javadoc
/**
 * This class is responsible for firing the execute method of the rule.
 *
 * @author kkumar
 */
public class RuleExecutor {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(RuleExecutor.class);
    
    /** The is resource filter exists. */
    private Boolean isResourceFilterExists = Boolean.FALSE;
    
    /**  Annotation Publisher *. */
    AnnotationPublisher annotationPublisher;

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        // File f = new File("rule.jar-jar-with-dependencies.jar");
        // URLClassLoader cl = new URLClassLoader(new URL[]{f.toURI().toURL(),
        // null});
        //
        // Class<?> clazz =
        // cl.loadClass("com.tmobile.cloud.awsrules.ec2.CheckForNamingConvention");
        // Method main = clazz.getMethod("main", String[].class);
        // main.invoke(null, new Object[]{args});
        // if(1==1) return;
        
        
        String executionId = UUID.randomUUID().toString(); // this is the unique
        // id for this pass
        // of execution
        
        //check if triggered by event of square one project.
        logger.debug("received input-->" + args[0]);
        if(PacEventHandler.isInvocationSourceAnEvent(args[0]))
        {
            logger.info("input source detected as event, will process event now.");
            new PacEventHandler().handleEvent(executionId,args[0]);
        }else
        {
                try {   logger.info("input source detected as rule, will process rule now.");
                        new RuleExecutor().run(args, executionId);
                } catch (Exception e) {
                    logger.error("error while in run method for executionId ->" + executionId, e);
                }
        }
    }

    /**
     * Run.
     *
     * @param args the args
     * @param executionId the execution id
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws ClassNotFoundException the class not found exception
     */
    private void run(String[] args, String executionId)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Map<String, String> ruleParam = new HashMap<String, String>();
        String ruleParams = "";
        Boolean errorWhileProcessing = Boolean.FALSE;

        Map<String, Object> ruleEngineStats = new HashMap<>();
        
        //this is elastic search type to put rule engine stats in  
        final String type = CommonUtils.getPropValue(PacmanSdkConstants.STATS_TYPE_NAME_KEY); // "execution-stats";
        final String JOB_ID = CommonUtils.getEnvVariableValue(PacmanSdkConstants.JOB_ID);
        if (args.length > 0) {
            ruleParams = args[0];
            ruleParam = CommonUtils.createParamMap(ruleParams);
            ruleParam.put(PacmanSdkConstants.EXECUTION_ID, executionId);
            if (Strings.isNullOrEmpty(ruleParam.get(PacmanSdkConstants.DATA_SOURCE_KEY))) {
                logger.error(
                        "data source is missing, will not be able to figure out the target index to post the rule evaluvation, please check rule configuration");
                logger.error("exiting now..");
                ProgramExitUtils.exitWithError();
            }
            logger.debug("rule Param String " + ruleParams);
            logger.debug("target Type :" + ruleParam.get(PacmanSdkConstants.TARGET_TYPE));
            logger.debug("rule Key : " + ruleParam.get("ruleKey"));
        } else {
            logger.debug(
                    "No arguments available for rule execution, unable to identify the rule due to missing arguments");
            logger.debug("atlest rule key is required to identify the rule class");
            logger.debug("returning now.");
            return;
        }
        try{
            setLogLevel(ruleParam);
        }catch(Exception e){
            logger.info("no log level found in params , setting to ERROR");
        }
        setMappedDiagnosticContex(executionId, ruleParam.get(PacmanSdkConstants.RULE_ID));
        setUncaughtExceptionHandler();
        logger.debug("uncaught exception handler engaged.");
        setShutDownHook(ruleEngineStats);
        logger.debug("shutdown hook engaged.");
        ruleEngineStats.put(PacmanSdkConstants.JOB_ID, JOB_ID);
        ruleEngineStats.put(PacmanSdkConstants.STATUS_KEY, PacmanSdkConstants.STATUS_RUNNING);
        ruleEngineStats.put(PacmanSdkConstants.EXECUTION_ID, executionId);
        ruleEngineStats.put(PacmanSdkConstants.RULE_ID, ruleParam.get(PacmanSdkConstants.RULE_ID));
        long startTime = resetStartTime();
        ruleEngineStats.put("startTime", CommonUtils.getCurrentDateStringWithFormat(PacmanSdkConstants.PAC_TIME_ZONE,
                PacmanSdkConstants.DATE_FORMAT));
        // publish the stats once to let ES know rule engine has started.
        ESUtils.publishMetrics(ruleEngineStats,type);
        ruleEngineStats.put("timeTakenToFindExecutable", CommonUtils.getElapseTimeSince(startTime));
        // get the resources based on Type
        // List<Map<String, String>> resources =
        // getResources(ruleParam.get(PacmanSdkConstants.TARGET_TYPE));
        List<Map<String, String>> resources = new ArrayList<>();
        List<String> userFields = null;
        if (!Strings.isNullOrEmpty(ruleParam.get(PacmanSdkConstants.ES_SOURCE_FIELDS_KEY))) {
            userFields = Splitter.on("|").trimResults()
                    .splitToList(ruleParam.get(PacmanSdkConstants.ES_SOURCE_FIELDS_KEY));
        }
        String indexName = "".intern();
        startTime = resetStartTime();

        try {
            indexName = CommonUtils.getIndexNameFromRuleParam(ruleParam);
            Map<String, String> filter = new HashMap<>();
            if (!Strings.isNullOrEmpty(ruleParam.get(PacmanSdkConstants.ACCOUNT_ID)))
                filter.put(ESUtils.createKeyword(PacmanSdkConstants.ACCOUNT_ID),
                        ruleParam.get(PacmanSdkConstants.ACCOUNT_ID));
            if (!Strings.isNullOrEmpty(ruleParam.get(PacmanSdkConstants.REGION)))
                filter.put(ESUtils.createKeyword(PacmanSdkConstants.REGION), ruleParam.get(PacmanSdkConstants.REGION));
            if (!Strings.isNullOrEmpty(ruleParam.get(PacmanSdkConstants.RESOURCE_ID)))
                filter.put(ESUtils.createKeyword(PacmanSdkConstants.RESOURCE_ID),
                        ruleParam.get(PacmanSdkConstants.RESOURCE_ID));

            if (!filter.isEmpty()) {
                logger.debug("found filters in rule config, resources will be filtered");
                isResourceFilterExists = Boolean.TRUE;
                ruleEngineStats.put("resource filter", filter);
            }
            resources = ESUtils.getResourcesFromEs(indexName, ruleParam.get(PacmanSdkConstants.TARGET_TYPE), filter,
                    userFields);
            logger.debug("got resources for evaluvation, total resources = " + resources.size());
            ruleEngineStats.put("timeTakenToFetchInventory", CommonUtils.getElapseTimeSince(startTime));
            if(resources.isEmpty()){
                logger.info("no resources to evaluvate exiting now");
                ProgramExitUtils.exitSucessfully();
            }
        } catch (Exception e) {
            logger.error(
                    "unable to get inventory for " + indexName + "--" + ruleParam.get(PacmanSdkConstants.TARGET_TYPE),
                    e);
            ruleEngineStats.put("errorMessage", "unable to fetch inventory");
            ruleEngineStats.put("technicalErrorDetails", e.getMessage());
            ProgramExitUtils.exitWithError();
        }

        
        startTime = resetStartTime();

        logger.info("total objects received for rule " + resources.size());
        String ruleParamStr = Joiner.on("#").withKeyValueSeparator("=").join(ruleParam);
        ruleEngineStats.put("timeTakenToGetResources", CommonUtils.getElapseTimeSince(startTime));
        ruleEngineStats.put("totalResourcesForThisExecutionCycle", resources.size());
        ruleEngineStats.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));
        ruleEngineStats.put("ruleParams", ruleParamStr);
        startTime = System.nanoTime();
        // loop through resources and call rule execute method

        RuleRunner ruleRunner;
        if ("true".equals(ruleParam.get(PacmanSdkConstants.RUN_ON_MULTI_THREAD_KEY))) {
            ruleRunner = new MultiThreadedRuleRunner();
        } else {
            ruleRunner = new SingleThreadRuleRunner();
        }

        // collect all resource ids for a post execution check of how many
        // executions returned issues.
        Map<String, Map<String, String>> resourceIdToResourceMap = new HashMap<>();
        resources.stream().forEach(obj -> {
            resourceIdToResourceMap.put(obj.get(PacmanSdkConstants.DOC_ID), obj);
        });
        List<RuleResult> evaluations = new ArrayList<>();
        List<RuleResult> missingEvaluations = new ArrayList<>();

        try {
            evaluations = ruleRunner.runRules(resources, ruleParam, executionId);
            ruleEngineStats.put("totalEvaluvationsFromRuleRunner", evaluations.size());
            logger.debug("total evaluations received back from rule Runner" + evaluations.size());
        } catch (Exception e) {
            String msg = "error occured while executing";
            logger.error(msg, e);
            ruleEngineStats.put(msg, Strings.isNullOrEmpty(e.getMessage()) ? "" : e.getMessage());
            logger.error("exiting now..", e);
            ProgramExitUtils.exitWithError();
        }

        // if resources size is not equals to number of evaluations then we have
        // some exceptions during evaluation , those will be the intersection of
        // resource and evaluations
        List<String> missingResourceIds = new ArrayList<>();
        // *****************************************************************
        // handle missing evaluation start
        // **************************************************************************************
        if (resources.size() != evaluations.size()) {
            if(ruleParam.containsKey(PacmanSdkConstants.RULE_CONTACT))
            {
                String message = String.format("%s total resource -> %s , total results returned by rule-> %s",ruleParam.get(PacmanSdkConstants.RULE_ID), resources.size(),evaluations.size());
                //send  message about missing evaluations 
                if(notifyRuleOwner(ruleParam.get(PacmanSdkConstants.RULE_CONTACT),message)){
                    logger.trace(String.format("message sent to %s" ,ruleParam.get(PacmanSdkConstants.RULE_CONTACT)));
                }else{
                    logger.error(String.format("unable to send message to %s" ,ruleParam.get(PacmanSdkConstants.RULE_CONTACT)));
                }
            }
            
            List<String> allEvaluvatedResources = evaluations.stream()
                    .map(obj -> obj.getAnnotation().get(PacmanSdkConstants.DOC_ID)).collect(Collectors.toList());
            logger.debug("all evaluated resource count" + allEvaluvatedResources.size());
            allEvaluvatedResources.stream().forEach(obj -> {
                resourceIdToResourceMap.remove(obj);
            });

            // create all missing evaluations as unknown / unable to execute
            // type annotations
            logger.debug("total potential missing evaluations" + resourceIdToResourceMap.size());
            final Map<String, String> ruleParamCopy = ImmutableMap.<String, String>builder().putAll(ruleParam).build();
            String ruleKey = ruleParam.get("ruleKey");
            Class<?> ruleClass = null;
            ruleClass = ReflectionUtils.findAssociateClass(ruleKey);
            PacmanRule ruleAnnotation = ruleClass.getAnnotation(PacmanRule.class);
            if (resourceIdToResourceMap.size() > 0) {
                resourceIdToResourceMap.values().forEach(obj -> {
                    missingEvaluations.add(new RuleResult(PacmanSdkConstants.STATUS_UNKNOWN,
                            PacmanSdkConstants.STATUS_UNKNOWN_MESSAGE, RuleExecutionUtils.buildAnnotation(ruleParamCopy,
                                    obj, executionId, Annotation.Type.ISSUE, ruleAnnotation)));
                });
                ruleEngineStats.put("missingEvaluations", missingEvaluations.size());
                evaluations.addAll(missingEvaluations);
            }
        }

        // *********************************************************************
        // handle missing evaluation end
        // ***********************************************************************************

        logger.info("Elapsed time in minutes for evaluation: " + CommonUtils.getElapseTimeSince(startTime));
        ruleEngineStats.put("timeTakenToEvaluvate", CommonUtils.getElapseTimeSince(startTime));
        startTime = System.nanoTime();
        AutoFixManager autoFixManager = new AutoFixManager();
        // process rule evaluations the annotations based on result
        try {
            if (evaluations.size() > 0) {

                ExceptionManager exceptionManager = new ExceptionManagerImpl();
                Map<String, List<IssueException>> exemptedResourcesForRule = exceptionManager.getStickyExceptions(
                        ruleParam.get(PacmanSdkConstants.RULE_ID), ruleParam.get(PacmanSdkConstants.TARGET_TYPE));
                Map<String, IssueException> individuallyExcemptedIssues = exceptionManager
                        .getIndividualExceptions(ruleParam.get(PacmanSdkConstants.TARGET_TYPE));

                ruleEngineStats.putAll(processRuleEvaluations(resources, evaluations, ruleParam,
                        exemptedResourcesForRule, individuallyExcemptedIssues));
                try {
                    if (ruleParam.containsKey(PacmanSdkConstants.RULE_PARAM_AUTO_FIX_KEY_NAME) && Boolean
                            .parseBoolean(ruleParam.get(PacmanSdkConstants.RULE_PARAM_AUTO_FIX_KEY_NAME)) == true) {
                        ruleEngineStats.putAll(autoFixManager.performAutoFixs(ruleParam, exemptedResourcesForRule,
                                individuallyExcemptedIssues));
                    }
                } catch (Exception e) {
                    logger.error("unable to signal auto fix manager");
                }
            } else {
                logger.info("no evaluvation to process");
            }
        } catch (Exception e) {
            logger.error("error while processing evaluvations", e);
            ruleEngineStats.put("error-while-processing-evaluvations", e.getLocalizedMessage());
            errorWhileProcessing = true;
        }
        ruleEngineStats.put("timeTakenToProcessEvaluvations", CommonUtils.getElapseTimeSince(startTime));
        startTime = System.nanoTime();
        ruleEngineStats.put("endTime", CommonUtils.getCurrentDateStringWithFormat(PacmanSdkConstants.PAC_TIME_ZONE,
                PacmanSdkConstants.DATE_FORMAT));
        ruleEngineStats.put(PacmanSdkConstants.STATUS_KEY, PacmanSdkConstants.STATUS_FINISHED);
        try{
                ESUtils.publishMetrics(ruleEngineStats,type);
        }catch(Exception e) {
            logger.error("unable to publish metrices",e);
        }
        if (!errorWhileProcessing)
            ProgramExitUtils.exitSucessfully();
        else
            ProgramExitUtils.exitWithError();
    }

    /**
     * @param ruleParam
     */
    private void setLogLevel(Map<String, String> ruleParam) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(ch.qos.logback.classic.Level.toLevel(ruleParam.get("logLevel"),ch.qos.logback.classic.Level.ERROR));
        
    }

    /**
     * Notify rule owner.
     *
     * @param user the user
     * @param message the message
     * @return true, if successful
     */
    private boolean notifyRuleOwner(String user, String message) {
        SlackMessageRelay messageRelay = new SlackMessageRelay();
        if(!Strings.isNullOrEmpty(user)){
            return messageRelay.sendMessage(user, message);
        }
        return false;
    }

    /**
     * Sets the mapped diagnostic contex.
     *
     * @param executionId the execution id
     * @param ruleId the rule id
     */
    private void setMappedDiagnosticContex(String executionId, String ruleId) {
        MDC.put(PacmanSdkConstants.EXECUTION_ID, executionId); // this is the
                                                               // logback Mapped
                                                               // Diagnostic
                                                               // Contex
        MDC.put(PacmanSdkConstants.RULE_ID, ruleId); // this is the logback
                                                     // Mapped Diagnostic Contex
    }

    /**
     * Reset start time.
     *
     * @return the long
     */
    private long resetStartTime() {
        return System.nanoTime();
    }

    /**
     * Sets the shut down hook.
     *
     * @param ruleEngineStats the rule engine stats
     */
    private void setShutDownHook(Map<String, Object> ruleEngineStats) {
        // final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutDownHook(ruleEngineStats)));
    }

    /**
     * Process rule evaluations.
     *
     * @param resources the resources
     * @param evaluations the evaluations
     * @param ruleParam the rule param
     * @param exemptedResourcesForRule the exempted resources for rule
     * @param individuallyExcemptedIssues the individually excempted issues
     * @return the map
     * @throws Exception the exception
     */
    private Map<String, Object> processRuleEvaluations(List<Map<String, String>> resources,
            List<RuleResult> evaluations, Map<String, String> ruleParam,
            Map<String, List<IssueException>> exemptedResourcesForRule,
            Map<String, IssueException> individuallyExcemptedIssues) throws Exception {

        Map<String, Object> metrics = new HashMap();
        metrics.put("totalResourcesEvalauetd", evaluations.size());
        String evalDate = CommonUtils.getCurrentDateStringWithFormat(PacmanSdkConstants.PAC_TIME_ZONE,
                PacmanSdkConstants.DATE_FORMAT);
        Annotation annotation = null;
        annotationPublisher = new AnnotationPublisher();
        long exemptionCounter = 0;
        try {

            metrics.put("max-exemptible-resource-count", exemptedResourcesForRule.size());

            metrics.put("individual-exception-count-for-this-rule", individuallyExcemptedIssues.size());
        } catch (Exception e) {
            logger.error("unable to fetch exceptions", e);
        }
        Status status;
        int issueFoundCounter = 0;
        //Pre populate the existing issues
        annotationPublisher.populateExistingIssuesForType(ruleParam);
        
        for (RuleResult result : evaluations) {
            annotation = result.getAnnotation();
            if (PacmanSdkConstants.STATUS_SUCCESS.equals(result.getStatus())) {
                annotation.put(PacmanSdkConstants.REASON_TO_CLOSE_KEY, result.getDesc());
                annotationPublisher.submitToClose(annotation);
                // closeIssue(annotation); // close issue
            } else { // publish the issue to ES
                if (PacmanSdkConstants.STATUS_FAILURE.equals(result.getStatus())) {

                    status = adjustStatus(PacmanSdkConstants.STATUS_OPEN, exemptedResourcesForRule,
                            individuallyExcemptedIssues, annotation);
                    annotation.put(PacmanSdkConstants.ISSUE_STATUS_KEY, status.getStatus());

                    // if exempted add additional details
                    if (PacmanSdkConstants.STATUS_EXEMPTED.equals(status.getStatus())) {
                        exemptionCounter++;
                        annotation.put(PacmanSdkConstants.EXEMPTION_EXPIRING_ON, status.getExemptionExpiryDate());
                        annotation.put(PacmanSdkConstants.REASON_TO_EXEMPT_KEY, status.getReason());
                        annotation.put(PacmanSdkConstants.EXEMPTION_ID, status.getExceptionId());
                    }
                }
                if (PacmanSdkConstants.STATUS_UNKNOWN.equals(result.getStatus())) {
                    annotation.put(PacmanSdkConstants.ISSUE_STATUS_KEY, PacmanSdkConstants.STATUS_UNKNOWN);
                    annotation.put(PacmanSdkConstants.STATUS_REASON,
                            PacmanSdkConstants.STATUS_UNABLE_TO_DETERMINE);
                }

                annotation.put(PacmanSdkConstants.DATA_SOURCE_KEY, ruleParam.get(PacmanSdkConstants.DATA_SOURCE_KEY));
                // add created date if not an existing issue
                if(!annotationPublisher.getExistingIssuesMapWithAnnotationIdAsKey().containsKey(CommonUtils.getUniqueAnnotationId(annotation))){
                    annotation.put(PacmanSdkConstants.CREATED_DATE, evalDate);
                }
                annotation.put(PacmanSdkConstants.MODIFIED_DATE, evalDate);
                // annotationPublisher.publishAnnotationToEs(annotation);
                annotationPublisher.submitToPublish(annotation);
                issueFoundCounter++;
                logger.info("submitted annotaiton to publisher");
            }

        }
        metrics.put("totalExemptionAppliedForThisRun", exemptionCounter);
        annotationPublisher.setRuleParam(ImmutableMap.<String, String>builder().putAll(ruleParam).build());
        // annotation will contain the last annotation processed above
        
        if (!isResourceFilterExists) {
            annotationPublisher.setExistingResources(resources); // if resources
                                                                 // are not
                                                                 // filtered
                                                                 // then no need
                                                                 // to make
                                                                 // another
                                                                 // call.
        }
        // this will be used for closing issues if resources are filtered
        // already this will prevent actual issues to close
        else {
            annotationPublisher
                    .setExistingResources(ESUtils.getResourcesFromEs(CommonUtils.getIndexNameFromRuleParam(ruleParam),
                            ruleParam.get(PacmanSdkConstants.TARGET_TYPE), null, null));
        }
        annotationPublisher.publish();
        metrics.put("total-issues-found", issueFoundCounter);
        List<Annotation> closedIssues = annotationPublisher.processClosureEx();
        Integer danglisngIssues = annotationPublisher.closeDanglingIssues(annotation);
        metrics.put("dangling-issues-closed", danglisngIssues);
        metrics.put("total-issues-closed", closedIssues.size() + danglisngIssues);
        AuditUtils.postAuditTrail(annotationPublisher.getBulkUploadBucket(), PacmanSdkConstants.STATUS_OPEN);
        AuditUtils.postAuditTrail(closedIssues, PacmanSdkConstants.STATUS_CLOSE);
        return metrics;
    }

    /**
     * Adjust the status of issue based on exception.
     *
     * @param status the status
     * @param excemptedResourcesForRule the excempted resources for rule
     * @param individuallyExcemptedIssues the individually excempted issues
     * @param annotation the annotation
     * @return the status
     */
    private Status adjustStatus(String status, Map<String, List<IssueException>> excemptedResourcesForRule,
            Map<String, IssueException> individuallyExcemptedIssues, Annotation annotation) {

        List<IssueException> stickyExceptions = excemptedResourcesForRule
                .get(annotation.get(PacmanSdkConstants.RESOURCE_ID));
        IssueException exception;
        if (null != stickyExceptions) {
            // get the exemption with min expiry date and create the status for
            // now taking from 0 index
            exception = stickyExceptions.get(0);
            return new Status(PacmanSdkConstants.STATUS_EXEMPTED, exception.getExceptionReason(), exception.getId(),
                    exception.getExpiryDate());
        } else // check individual exception
        {
            exception = individuallyExcemptedIssues.get(CommonUtils.getUniqueAnnotationId(annotation));
            if (null != exception) {
                return new Status(PacmanSdkConstants.STATUS_EXEMPTED, exception.getExceptionReason(), exception.getId(),
                        exception.getExpiryDate());
            } else {
                return new Status(status); // return the same status as input
            }
        }
    }

    /**
     * in case any rule throws exception and it reaches main, this will make
     * sure the VM is terminated gracefully close all clients here.
     */
    private void setUncaughtExceptionHandler() {
        Thread.currentThread().setUncaughtExceptionHandler(new RuleEngineUncaughtExceptionHandler());
    }

    /**
     * The Class Status.
     */
    static class Status {
        
        /** The status. */
        String status;
        
        /** The reason. */
        String reason;
        
        /** The exemption id. */
        String exemptionId;
        
        /** The exemption expiry date. */
        String exemptionExpiryDate;

        /**
         * Instantiates a new status.
         *
         * @param status the status
         * @param reason the reason
         * @param exemptionId the exemption id
         * @param exemptionExpiryDate the exemption expiry date
         */
        public Status(String status, String reason, String exemptionId, String exemptionExpiryDate) {
            super();
            this.status = status;
            this.reason = reason;
            this.exemptionId = exemptionId;
            this.exemptionExpiryDate = exemptionExpiryDate;
        }

        /**
         * Instantiates a new status.
         *
         * @param status the status
         */
        public Status(String status) {
            this.status = status;
        }

        /**
         * Gets the status.
         *
         * @return the status
         */
        public String getStatus() {
            return status;
        }

        /**
         * Sets the status.
         *
         * @param status the new status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * Gets the reason.
         *
         * @return the reason
         */
        public String getReason() {
            return reason;
        }

        /**
         * Sets the reason.
         *
         * @param reason the new reason
         */
        public void setReason(String reason) {
            this.reason = reason;
        }

        /**
         * Gets the exception id.
         *
         * @return the exception id
         */
        public String getExceptionId() {
            return exemptionId;
        }

        /**
         * Sets the exception id.
         *
         * @param exceptionId the new exception id
         */
        public void setExceptionId(String exceptionId) {
            this.exemptionId = exceptionId;
        }

        /**
         * Gets the exemption id.
         *
         * @return the exemption id
         */
        public String getExemptionId() {
            return exemptionId;
        }

        /**
         * Sets the exemption id.
         *
         * @param exemptionId the new exemption id
         */
        public void setExemptionId(String exemptionId) {
            this.exemptionId = exemptionId;
        }

        /**
         * Gets the exemption expiry date.
         *
         * @return the exemption expiry date
         */
        public String getExemptionExpiryDate() {
            return exemptionExpiryDate;
        }

        /**
         * Sets the exemption expiry date.
         *
         * @param exemptionExpiryDate the new exemption expiry date
         */
        public void setExemptionExpiryDate(String exemptionExpiryDate) {
            this.exemptionExpiryDate = exemptionExpiryDate;
        }
    }

}
