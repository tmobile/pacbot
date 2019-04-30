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

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.Annotation.Type;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

// TODO: Auto-generated Javadoc
/**
 * The Class RuleExecutionUtils.
 */
public class RuleExecutionUtils {

    /**
     * If filter matches the current resource.
     *
     * @param ruleParam the rule param
     * @param resource the resource
     * @return true, if successful
     */
    public static boolean ifFilterMatchesTheCurrentResource(Map<String, String> ruleParam,
            Map<String, String> resource) {

        String ruleParam_account = !Strings.isNullOrEmpty(ruleParam.get(PacmanSdkConstants.ACCOUNT_ID))
                ? ruleParam.get(PacmanSdkConstants.ACCOUNT_ID) : resource.get(PacmanSdkConstants.ACCOUNT_ID);
        String ruleParam_region = !Strings.isNullOrEmpty(ruleParam.get(PacmanSdkConstants.REGION))
                ? ruleParam.get(PacmanSdkConstants.REGION) : resource.get(PacmanSdkConstants.REGION);
        String ruleParam_resourceId = !Strings.isNullOrEmpty(ruleParam.get(PacmanSdkConstants.RESOURCE_ID))
                ? ruleParam.get(PacmanSdkConstants.RESOURCE_ID) : resource.get(PacmanSdkConstants.RESOURCE_ID);

        String ruleString = new StringBuilder(ruleParam_account).append(ruleParam_region).append(ruleParam_resourceId)
                .toString();
        String resourceString = new StringBuilder(resource.get(PacmanSdkConstants.ACCOUNT_ID))
                .append(resource.get(PacmanSdkConstants.REGION)).append(resource.get(PacmanSdkConstants.RESOURCE_ID))
                .toString();
        ;

        return ruleString.equals(resourceString);

    }

    /**
     * Gets the local rule param.
     *
     * @param ruleParam the rule param
     * @param resource the resource
     * @return the local rule param
     */
    public static Map<String, String> getLocalRuleParam(Map<String, String> ruleParam, Map<String, String> resource) {
        Map<String, String> localRuleParam = new HashMap<>();
        localRuleParam.putAll(ruleParam);
        localRuleParam.put(PacmanSdkConstants.RESOURCE_ID, resource.get(PacmanSdkConstants.RESOURCE_ID));
        if (null != resource.get(PacmanSdkConstants.ACCOUNT_ID))
            localRuleParam.put(PacmanSdkConstants.ACCOUNT_ID, resource.get(PacmanSdkConstants.ACCOUNT_ID));
        if (null != resource.get(PacmanSdkConstants.ACCOUNT_NAME))
            localRuleParam.put(PacmanSdkConstants.ACCOUNT_NAME, resource.get(PacmanSdkConstants.ACCOUNT_NAME));
        if (null != resource.get(PacmanSdkConstants.REGION))
            localRuleParam.put(PacmanSdkConstants.REGION, resource.get(PacmanSdkConstants.REGION));
        return ImmutableMap.<String, String>builder().putAll(localRuleParam).build();
    }

    /**
     * Gets the rule attribute.
     *
     * @param result the result
     * @param ruleParam the rule param
     * @param ruleAnnotation the rule annotation
     * @param attribute the attribute
     * @return the attribute value from ruleParam--ruleAnnotation--RuleResult
     *         wherever found first, not_found otherwise
     */
    public static String getRuleAttribute(RuleResult result, Map<String, String> ruleParam, PacmanRule ruleAnnotation,
            String attribute) {
        if (ruleParam != null && ruleParam.containsKey(attribute)) {
            return ruleParam.get(attribute);
        }
        if (ruleAnnotation != null) {
            return ruleAnnotation.category();
        }
        return getValueFromResult(result, attribute);
    }

    /**
     * Gets the value from result.
     *
     * @param result the result
     * @param key the key
     * @return the value from result
     */
    private static String getValueFromResult(final RuleResult result, final String key) {
        Annotation annotation = null;
        if (result != null) {
            annotation = result.getAnnotation();
            if (null != annotation) {
                return annotation.get(key);
            }
        }
        return "NOT_FOUND";
    }

    /**
     * Builds the annotation.
     *
     * @param ruleParam the rule param
     * @param resource the resource
     * @param executionId the execution id
     * @param annotationType the annotation type
     * @param ruleAnnotation the rule annotation
     * @return the annotation
     */
    public static Annotation buildAnnotation(Map<String, String> ruleParam, Map<String, String> resource,
            String executionId, Type annotationType, PacmanRule ruleAnnotation) {

        Annotation annotation = Annotation.buildAnnotation(ruleParam, annotationType);
        annotation.put(PacmanSdkConstants.EXECUTION_ID, executionId);
        if (null != ruleAnnotation) {
            annotation.put(PacmanSdkConstants.RULE_CATEGORY, ruleAnnotation.category());
            annotation.put(PacmanSdkConstants.RULE_SEVERITY, ruleAnnotation.severity());
        }
        if (null != ruleParam) {
            annotation.put(PacmanSdkConstants.DATA_SOURCE_KEY, ruleParam.get(PacmanSdkConstants.DATA_SOURCE_KEY));
            annotation.put(PacmanSdkConstants.TARGET_TYPE, ruleParam.get(PacmanSdkConstants.TARGET_TYPE));
            annotation.put(PacmanSdkConstants.RULE_ID, ruleParam.get(PacmanSdkConstants.RULE_ID));
            if (ruleParam.containsKey(PacmanSdkConstants.INVOCATION_ID)) {
                annotation.put(PacmanSdkConstants.INVOCATION_ID, ruleParam.get(PacmanSdkConstants.INVOCATION_ID));
            }
            if (ruleParam.containsKey(PacmanSdkConstants.RULE_SEVERITY)) {
                annotation.put(PacmanSdkConstants.RULE_SEVERITY, ruleParam.get(PacmanSdkConstants.RULE_SEVERITY));
            }
        }
        if (null != resource) {
            annotation.put(PacmanSdkConstants.RESOURCE_ID, resource.get(PacmanSdkConstants.RESOURCE_ID));
            annotation.put(PacmanSdkConstants.ACCOUNT_ID, resource.get(PacmanSdkConstants.ACCOUNT_ID));
            annotation.put(PacmanSdkConstants.REGION, resource.get(PacmanSdkConstants.REGION));
            annotation.put(PacmanSdkConstants.ACCOUNT_NAME, resource.get(PacmanSdkConstants.ACCOUNT_NAME));
            annotation.put(PacmanSdkConstants.DOC_ID, resource.get(PacmanSdkConstants.DOC_ID));
            if (resource.containsKey(PacmanSdkConstants.APPLICATION_TAG_KEY)) {
                annotation.put(PacmanSdkConstants.APPLICATION_TAG_KEY,
                        resource.get(PacmanSdkConstants.APPLICATION_TAG_KEY));
            }
            if (resource.containsKey(PacmanSdkConstants.ENV_TAG_KEY)) {
                annotation.put(PacmanSdkConstants.ENV_TAG_KEY, resource.get(PacmanSdkConstants.ENV_TAG_KEY));
            }
        }

        return annotation;
    }

}
