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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ReflectionUtils;
import com.tmobile.pacman.util.RuleExecutionUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class MultiThreadedRuleRunner.
 *
 * @author kkumar Runs the rule on multiple threads
 */
public class MultiThreadedRuleRunner implements RuleRunner {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiThreadedRuleRunner.class);

    /* (non-Javadoc)
     * @see com.tmobile.pacman.executor.RuleRunner#runRules(java.util.List, java.util.Map, java.lang.String)
     */
    @Override
    public List<RuleResult> runRules(List<Map<String, String>> resources, Map<String, String> ruleParam,
            String executionId) throws Exception {

        String ruleKey = ruleParam.get("ruleKey");
        Class<?> ruleClass = null;
        List<RuleResult> evaluations = new ArrayList<RuleResult>();
        ThreadFactoryBuilder namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat(PacmanSdkConstants.THREAD_NAME_PREFIX + "-" + ruleKey)
                .setUncaughtExceptionHandler(new RuleEngineUncaughtExceptionHandler());
        Integer threadPoolSize = 0;
        try {
            // see if rule parameter preset to control number of threads
            if (ruleParam.containsKey(PacmanSdkConstants.WORKER_THREAD_COUNT)) {
                threadPoolSize = Integer.parseInt(ruleParam.get(PacmanSdkConstants.WORKER_THREAD_COUNT));
                LOGGER.info(PacmanSdkConstants.WORKER_THREAD_COUNT + " found, setting thread pool size to "
                        + threadPoolSize);
            } else {
                threadPoolSize = Integer
                        .parseInt(CommonUtils.getEnvVariableValue(PacmanSdkConstants.ENV_PAC_RE_MAX_WORKERS));
                LOGGER.info("Env vaiable " + PacmanSdkConstants.ENV_PAC_RE_MAX_WORKERS
                        + " found, setting thread pool size to " + threadPoolSize);
            }

        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            LOGGER.info("Env vaiable " + PacmanSdkConstants.ENV_PAC_RE_MAX_WORKERS
                    + " not found, setting default thread pool size to "
                    + PacmanSdkConstants.MAX_RULE_EXECUTOR_THREADS);
            threadPoolSize = PacmanSdkConstants.MAX_RULE_EXECUTOR_THREADS;
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize, namedThreadFactory.build());

        if (!PacmanSdkConstants.RULE_TYPE_SERVERLESS.equals(ruleParam.get(PacmanSdkConstants.RULE_TYPE))) {
            try {
                ruleClass = ReflectionUtils.findAssociateClass(ruleKey);
            } catch (Exception e) {
                LOGGER.error("Please check the rule class complies to implemetation contract, rule key=" + ruleKey, e);
                executor.shutdown();
                throw e;
            }
        }
        List<Future<RuleResult>> results = new ArrayList<Future<RuleResult>>();
        LOGGER.info(
                "----------------------------------------------------scan start------------------------------------------------------------------");

        BaseRule rule = null;
        PacmanRule ruleAnnotation = null;
        HttpConnectionManagerParams connParam = new HttpConnectionManagerParams();
        connParam.setMaxTotalConnections(PacmanSdkConstants.MAX_HTTP_CON);
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.setParams(connParam);
        HttpClient httpClient = new HttpClient(connectionManager);
        Boolean firstRun = Boolean.TRUE;
        for (Map<String, String> resource : resources) {
            try {
                Map<String, String> localRuleParam = RuleExecutionUtils.getLocalRuleParam(ruleParam, resource);
                LOGGER.debug("Resource-->: " + Joiner.on("#").withKeyValueSeparator("=").join(resource));
                // RuleResult result =
                // (RuleResult)executeMethod.invoke(ruleObject,
                // Collections.unmodifiableMap(ruleParam),null); // let rule not
                // allow modify input
                if (PacmanSdkConstants.RULE_TYPE_SERVERLESS.equals(localRuleParam.get(PacmanSdkConstants.RULE_TYPE))) {
                    results.add(executor.submit(new ServerlessRuleHandler(httpClient,
                            ImmutableMap.<String, String>builder().putAll(localRuleParam).build(),
                            ImmutableMap.<String, String>builder().putAll(resource).build())));
                } else {
                    rule = (BaseRule) ruleClass.newInstance();
                    rule.setRuleParamMap(localRuleParam);
                    rule.setResourceAttributeMap(ImmutableMap.<String, String>builder().putAll(resource).build());
                    results.add(executor.submit(rule));
                    ruleAnnotation = ruleClass.getAnnotation(PacmanRule.class);
                    // induce delay as configured in rule param
                    if (firstRun && localRuleParam.containsKey(PacmanSdkConstants.RESOURCE_INIT_DELAY)) {
                        try {
                            firstRun = Boolean.FALSE;
                            long dealy = Long.parseLong(localRuleParam.get(PacmanSdkConstants.RESOURCE_INIT_DELAY));
                            waitForResourceInitialization(dealy);
                        } catch (Exception e) {
                            LOGGER.error(
                                    "unable to parse resource_init_delay, will not delay after first exection of control",
                                    e);
                        }
                    }

                    // result = (RuleResult)executeMethod.invoke(ruleObject,
                    // Collections.unmodifiableMap(ruleParam),Collections.unmodifiableMap(resource));
                    // // let rule not allow modify input
                }
            } catch (Exception e) {
                LOGGER.debug("rule execution for resource " + resource.get("id") + " failed due to " + e.getMessage());
            }
        }
        Map<String, String> resource = null;
        RuleResult result = null;
        Annotation annotation = null;
        for (Future<RuleResult> future : results) {
            try {
                result = future.get(PacmanSdkConstants.SCAN_TIME_OUT, TimeUnit.SECONDS);
                resource = result.getResource();
            } catch (TimeoutException e) {
                LOGGER.error("rule timed out, waited for " + PacmanSdkConstants.SCAN_TIME_OUT + " "
                        + TimeUnit.SECONDS.toString(), e);
                future.cancel(Boolean.TRUE);
                continue;
                // DO NOT ANNOTATE HERE : RULE EXECUTOR WILL TAKE CARE BY
                // FINDING NON EVALUATED RESOURCES
            } catch (Exception e) {
                LOGGER.error("exception occured for this pass cancelling this pass", e);
                future.cancel(Boolean.TRUE);
                continue;
                // DO NOT ANNOTATE HERE : RULE EXECUTOR WILL TAKE CARE BY
                // FINDING NON EVALUATED RESOURCES
            }
            // if fail issue will get logged to database, hence update the
            // category and severity
            if (PacmanSdkConstants.STATUS_FAILURE.equalsIgnoreCase(result.getStatus())
                    || PacmanSdkConstants.STATUS_UNKNOWN.equalsIgnoreCase(result.getStatus())) {

                LOGGER.debug(
                        "non-compliant resource found" + resource.get(PacmanSdkConstants.RESOURCE_ID_COL_NAME_FROM_ES));

                if (ruleParam.containsKey(PacmanSdkConstants.INVOCATION_ID)) {
                    result.getAnnotation().put(PacmanSdkConstants.INVOCATION_ID,
                            ruleParam.get(PacmanSdkConstants.INVOCATION_ID));
                }
                result.getAnnotation().put(PacmanSdkConstants.RESOURCE_ID,
                        resource.get(PacmanSdkConstants.RESOURCE_ID_COL_NAME_FROM_ES));
                result.getAnnotation().put(PacmanSdkConstants.ACCOUNT_ID, resource.get("accountid"));
                result.getAnnotation().put(PacmanSdkConstants.REGION, resource.get("region"));
                result.getAnnotation().put(PacmanSdkConstants.RULE_CATEGORY, RuleExecutionUtils.getRuleAttribute(result,
                        ruleParam, ruleAnnotation, PacmanSdkConstants.RULE_CATEGORY));
                result.getAnnotation().put(PacmanSdkConstants.RULE_SEVERITY, RuleExecutionUtils.getRuleAttribute(result,
                        ruleParam, ruleAnnotation, PacmanSdkConstants.RULE_SEVERITY));
                result.getAnnotation().put(PacmanSdkConstants.TARGET_TYPE,
                        ruleParam.get(PacmanSdkConstants.TARGET_TYPE));
                result.getAnnotation().put(PacmanSdkConstants.DOC_ID, resource.get(PacmanSdkConstants.DOC_ID));
                result.getAnnotation().put(PacmanSdkConstants.EXECUTION_ID, executionId);
                result.getAnnotation().put(PacmanSdkConstants.ACCOUNT_NAME, resource.get("accountname"));
                if (resource.containsKey(PacmanSdkConstants.APPLICATION_TAG_KEY)) {
                    result.getAnnotation().put(PacmanSdkConstants.APPLICATION_TAG_KEY,
                            resource.get(PacmanSdkConstants.APPLICATION_TAG_KEY));
                }
                if (resource.containsKey(PacmanSdkConstants.ENV_TAG_KEY)) {
                    result.getAnnotation().put(PacmanSdkConstants.ENV_TAG_KEY,
                            resource.get(PacmanSdkConstants.ENV_TAG_KEY));
                }

            } else if (PacmanSdkConstants.STATUS_SUCCESS.equalsIgnoreCase(result.getStatus())) {

                annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
                annotation.put(PacmanSdkConstants.EXECUTION_ID, executionId);
                annotation.put(PacmanSdkConstants.REASON_TO_CLOSE_KEY, result.getDesc()); // this
                                                                                          // is
                                                                                          // the
                                                                                          // reason
                                                                                          // to
                                                                                          // close
                if (null != ruleParam) {
                    annotation.put(PacmanSdkConstants.DATA_SOURCE_KEY,
                            ruleParam.get(PacmanSdkConstants.DATA_SOURCE_KEY));
                    annotation.put(PacmanSdkConstants.TARGET_TYPE, ruleParam.get(PacmanSdkConstants.TARGET_TYPE));
                    annotation.put(PacmanSdkConstants.RULE_ID, ruleParam.get(PacmanSdkConstants.RULE_ID));
                    if (ruleParam.containsKey(PacmanSdkConstants.INVOCATION_ID)) {
                        annotation.put(PacmanSdkConstants.INVOCATION_ID,
                                ruleParam.get(PacmanSdkConstants.INVOCATION_ID));
                    }
                }
                if (null != resource) {

                    LOGGER.debug(
                            "compliant resource found" + resource.get(PacmanSdkConstants.RESOURCE_ID_COL_NAME_FROM_ES));

                    annotation.put(PacmanSdkConstants.RESOURCE_ID,
                            resource.get(PacmanSdkConstants.RESOURCE_ID_COL_NAME_FROM_ES));
                    annotation.put(PacmanSdkConstants.ACCOUNT_ID, resource.get("accountid"));
                    annotation.put(PacmanSdkConstants.DOC_ID, resource.get(PacmanSdkConstants.DOC_ID));
                    annotation.put(PacmanSdkConstants.REGION, resource.get("region"));
                    if (resource.containsKey(PacmanSdkConstants.APPLICATION_TAG_KEY)) {
                        annotation.put(PacmanSdkConstants.APPLICATION_TAG_KEY,
                                resource.get(PacmanSdkConstants.APPLICATION_TAG_KEY));
                    }
                    if (resource.containsKey(PacmanSdkConstants.ENV_TAG_KEY)) {
                        annotation.put(PacmanSdkConstants.ENV_TAG_KEY, resource.get(PacmanSdkConstants.ENV_TAG_KEY));
                    }
                }

                result.setAnnotation(annotation);
            } else {
                // do nothing this is kind of unknown status
                LOGGER.error("unknown annotation status returned by rule " + ruleParam.get(PacmanSdkConstants.RULE_ID)
                        + " ignoring this result");
                continue;
            }
            evaluations.add(result);
        }
        LOGGER.info(
                "----------------------------------------------------scan complete------------------------------------------------------------------");
        executor.shutdown();
        connectionManager.shutdown();
        MultiThreadedHttpConnectionManager.shutdownAll();

        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            LOGGER.error("unable to shutdown executor", e);
        }
        List<Runnable> tasksNotExecuted = executor.shutdownNow();
        if (tasksNotExecuted != null && !tasksNotExecuted.isEmpty()) {
            LOGGER.error("total tasks cancelled because rule did not return result in time" + tasksNotExecuted.size());
        }

        return evaluations;
    }

    /**
     * Wait for resource initialization.
     *
     * @param millis the millis
     */
    private void waitForResourceInitialization(Long millis) {
        try {
            LOGGER.info("waiting post first run for " + millis + " milliseconds");
            Thread.sleep(millis);
        } catch (Exception e) {
            LOGGER.warn(
                    "rule config needed a pause after first thread, but unable to pause the execution, continuing..",
                    e);
        }

    }

}
