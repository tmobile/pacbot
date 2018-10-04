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
/**
  Copyright (C) 2017 T Mobile Inc - All Rights Reserve
  Purpose: This rule check for the EC2 private IP address accessible with PORT 9200 to the public
  Author :santoshi
  Modified Date: Aug 14, 2017

 **/
package com.tmobile.cloud.awsrules.ec2;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.util.StringUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

@PacmanRule(key = "check-for-es-internal-access", desc = "This rule check for the EC2 private IP adress accessble with PORT 9200 to the public", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class ElasticSearchInternalAccessRule extends BaseRule {
    private static final Logger logger = LoggerFactory
            .getLogger(ElasticSearchInternalAccessRule.class);

    /**
     * The method will get triggered from Rule Engine with following parameters
     *
     * @param ruleParam
     *
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     *
     *            port : The port value of the elastic search <br>
     * <br>
     *
     *            ruleKey : check-for-es-internal-access<br>
     * <br>
     *
     *            threadsafe : if true , rule will be executed on multiple
     *            threads <br>
     * <br>
     *
     *            severity : Enter the value of severity <br>
     * <br>
     *
     *            ruleCategory : Enter the value of category <br>
     * <br>
     *
     * @param resourceAttributes
     *            this is a resource in context which needs to be scanned this
     *            is provided by execution engine
     *
     */

    public RuleResult execute(final Map<String, String> ruleParam,
            Map<String, String> resourceAttributes) {
        logger.debug("========ElasticSearchInternalAccessRule started=========");
        String privateIPAddress = resourceAttributes
                .get(PacmanRuleConstants.PRIVATE_IP_ADDRESS);
        String portNum = ruleParam.get(PacmanRuleConstants.PORT);

        String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

        MDC.put("executionId", ruleParam.get("executionId"));
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

        List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();

        if (!PacmanUtils.doesAllHaveValue(severity, category,portNum)) {
            logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
        }

        if ((!StringUtils.isNullOrEmpty(privateIPAddress))
                && (!StringUtils.isNullOrEmpty(portNum))) {
            String urlString = null;
            Annotation annotation = null;
            urlString = "http://" + privateIPAddress + ":" + portNum;
            CloseableHttpClient httpClient = null;
            try {
                URL url = new URL(urlString);
                URI uri = new URI(url.getProtocol(), url.getUserInfo(),
                        url.getHost(), url.getPort(), url.getPath(),
                        url.getQuery(), url.getRef());

                httpClient = HttpClientBuilder.create()
                        .setConnectionTimeToLive(1, TimeUnit.SECONDS).build();
                HttpGet httpGet = new HttpGet(uri);
                HttpResponse response = httpClient.execute(httpGet);
                String responseJson = EntityUtils
                        .toString(response.getEntity());
                if (responseJson.contains("lucene_version")) {
                    logger.info("ES end point can be accessed internally");
                    annotation = Annotation.buildAnnotation(ruleParam,
                            Annotation.Type.ISSUE);
                    annotation.put(PacmanSdkConstants.DESCRIPTION,
                            "Private IP public accessble with port 9200");
                    annotation.put(PacmanRuleConstants.SEVERITY, severity);
                    annotation.put(PacmanRuleConstants.CATEGORY, category);

                    issue.put(PacmanRuleConstants.VIOLATION_REASON,
                            "Private IP public accessble with port 9200");
                    issue.put("private_ip", privateIPAddress);
                    issue.put("port_number", portNum);
                    issueList.add(issue);
                    annotation.put("issueDetails", issueList.toString());

                    logger.debug("========ElasticSearchInternalAccessRule ended with an annotation {} :=========", annotation);
                    return new RuleResult(PacmanSdkConstants.STATUS_FAILURE,
                            PacmanRuleConstants.FAILURE_MESSAGE, annotation);
                }
            } catch (Exception mue) {
                logger.info(mue.toString());
            }  finally {
                if (null != httpClient)
                    try {
                        httpClient.close();
                    } catch (IOException e) {
                        logger.error("unable to close http client");
                    }
            }

        } else {
            logger.info("Private IP/Port#  value is null");
            throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);

        }
        logger.debug("========ElasticSearchInternalAccessRule ended=========");
        return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS,
                PacmanRuleConstants.SUCCESS_MESSAGE);
    }

    @Override
    public String getHelpText() {
        return "This rule check for the EC2 private IP address accessble with PORT 9200 to the public";
    }
}
