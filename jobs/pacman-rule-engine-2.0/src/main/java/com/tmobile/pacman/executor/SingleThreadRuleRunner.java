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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;
import com.tmobile.pacman.util.ReflectionUtils;
import com.tmobile.pacman.util.RuleExecutionUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class SingleThreadRuleRunner.
 */
public class SingleThreadRuleRunner implements RuleRunner {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SingleThreadRuleRunner.class);

    /* (non-Javadoc)
     * @see com.tmobile.pacman.executor.RuleRunner#runRules(java.util.List, java.util.Map, java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public List<RuleResult> runRules(List<Map<String, String>> resources, Map<String, String> ruleParam,
            String executionId) throws Exception {
        String ruleKey = null;
        Class<?> ruleClass = null;
        Object ruleObject = null;
        Method executeMethod = null;
        List<RuleResult> evaluations = new ArrayList<RuleResult>();
        HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        if (!PacmanSdkConstants.RULE_TYPE_SERVERLESS.equals(ruleParam.get(PacmanSdkConstants.RULE_TYPE))) {
            try {
                ruleKey = ruleParam.get(PacmanSdkConstants.RULE_KEY);
                ruleClass = ReflectionUtils.findAssociateClass(ruleKey);
                ruleObject = ruleClass.newInstance();
                // executeMethod =
                // ReflectionUtils.findEntryMethod(ruleObject,PacmanExecute.class);
                executeMethod = ReflectionUtils.findAssociatedMethod(ruleObject, "execute");
            } catch (Exception e) {
                logger.error("Please check the rule class complies to implemetation contract, rule key=" + ruleKey, e);
                throw e;
            }
        }
        logger.info(
                "----------------------------------------------------scan start------------------------------------------------------------------");

        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager()); // create
                                                                               // this
                                                                               // outside
                                                                               // the
                                                                               // loop
                                                                               // below

        for (Map<String, String> resource : resources) {
            try {
                Map<String, String> localRuleParam = RuleExecutionUtils.getLocalRuleParam(ruleParam, resource);
                logger.debug("Resource-->: " + Joiner.on("#").withKeyValueSeparator("=").join(resource));
                RuleResult result = null;
                // RuleResult result =
                // (RuleResult)executeMethod.invoke(ruleObject,
                // Collections.unmodifiableMap(ruleParam),null); // let rule not
                // allow modify input
                PacmanRule ruleAnnotation = null;
                if (PacmanSdkConstants.RULE_TYPE_SERVERLESS.equals(localRuleParam.get(PacmanSdkConstants.RULE_TYPE))) {
                    result = new ServerlessRuleHandler(httpClient).handleRule(ruleParam, resource);
                } else {
                    try {
                        result = (RuleResult) executeMethod.invoke(ruleObject,
                                Collections.unmodifiableMap(localRuleParam), Collections.unmodifiableMap(resource)); // let
                                                                                                                     // rule
                                                                                                                     // not
                                                                                                                     // allow
                                                                                                                     // modify
                                                                                                                     // input
                    } catch (Exception e) {
                        // in case not able to evaluate the result :
                        // RuleExecutor class will detect this by taking the
                        // delta between resource in and result out
                        logger.error(String.format("unable to evaluvate for this resource %s" , resource), e); // this will be handled as missing evaluation at RuleEcecutor
                    }
                    ruleAnnotation = ruleClass.getAnnotation(PacmanRule.class);
                }
                // if fail issue will get logged to database, hence update the
                // category and severity
                if (result!= null && (PacmanSdkConstants.STATUS_FAILURE.equalsIgnoreCase(result.getStatus())
                        || PacmanSdkConstants.STATUS_UNKNOWN.equalsIgnoreCase(result.getStatus()))) {
                    if (ruleParam.containsKey(PacmanSdkConstants.INVOCATION_ID)) {
                        result.getAnnotation().put(PacmanSdkConstants.INVOCATION_ID,
                                ruleParam.get(PacmanSdkConstants.INVOCATION_ID));
                    }
                    result.getAnnotation().put(PacmanSdkConstants.RESOURCE_ID,
                            resource.get(PacmanSdkConstants.RESOURCE_ID_COL_NAME_FROM_ES));
                    result.getAnnotation().put(PacmanSdkConstants.ACCOUNT_ID, resource.get("accountid"));
                    result.getAnnotation().put(PacmanSdkConstants.REGION, resource.get("region"));
                    result.getAnnotation().put(PacmanSdkConstants.RULE_CATEGORY, RuleExecutionUtils
                            .getRuleAttribute(result, ruleParam, ruleAnnotation, PacmanSdkConstants.RULE_CATEGORY));
                    result.getAnnotation().put(PacmanSdkConstants.RULE_SEVERITY, RuleExecutionUtils
                            .getRuleAttribute(result, ruleParam, ruleAnnotation, PacmanSdkConstants.RULE_SEVERITY));
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
                }
                else {
                            Annotation annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
                            annotation.put(PacmanSdkConstants.DATA_SOURCE_KEY,
                                    ruleParam.get(PacmanSdkConstants.DATA_SOURCE_KEY));
                            annotation.put(PacmanSdkConstants.TARGET_TYPE, ruleParam.get(PacmanSdkConstants.TARGET_TYPE));
                            if(null!=result){
                                annotation.put(PacmanSdkConstants.REASON_TO_CLOSE_KEY, result.getDesc());
                            }
                            annotation.put(PacmanSdkConstants.RULE_ID, ruleParam.get(PacmanSdkConstants.RULE_ID));
                            if (ruleParam.containsKey(PacmanSdkConstants.INVOCATION_ID)) {
                                annotation.put(PacmanSdkConstants.INVOCATION_ID,
                                        ruleParam.get(PacmanSdkConstants.INVOCATION_ID));
                            }
                            annotation.put(PacmanSdkConstants.RESOURCE_ID,
                                    resource.get(PacmanSdkConstants.RESOURCE_ID_COL_NAME_FROM_ES));
                            annotation.put(PacmanSdkConstants.ACCOUNT_ID, resource.get("accountid"));
                            annotation.put(PacmanSdkConstants.DOC_ID, resource.get(PacmanSdkConstants.DOC_ID)); // this is important to close the issue
                            if (resource.containsKey(PacmanSdkConstants.APPLICATION_TAG_KEY)) {
                                annotation.put(PacmanSdkConstants.APPLICATION_TAG_KEY,
                                        resource.get(PacmanSdkConstants.APPLICATION_TAG_KEY));
                            }
                            if (resource.containsKey(PacmanSdkConstants.ENV_TAG_KEY)) {
                                annotation.put(PacmanSdkConstants.ENV_TAG_KEY, resource.get(PacmanSdkConstants.ENV_TAG_KEY));
                            }
                            if(null!=result){
                                result.setAnnotation(annotation);
                            }else{
                                continue;
                            }
                }
                evaluations.add(result);
            } catch (Exception e) {
                logger.debug("rule execution for resource " + resource.get("id") + " failed due to " + e.getMessage(),
                        e);
            }
        }
        logger.info(
                "----------------------------------------------------scan complete------------------------------------------------------------------");
        return evaluations;
    }

}
