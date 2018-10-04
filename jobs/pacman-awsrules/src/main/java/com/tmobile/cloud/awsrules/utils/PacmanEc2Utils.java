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
 * Utility functions for ASGC Rules
 */
package com.tmobile.cloud.awsrules.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeFlowLogsRequest;
import com.amazonaws.services.ec2.model.DescribeFlowLogsResult;
import com.amazonaws.services.ec2.model.DescribeVolumesRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.FlowLog;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Volume;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;

public class PacmanEc2Utils {
    private static final Logger logger = LoggerFactory
            .getLogger(PacmanEc2Utils.class);

    private PacmanEc2Utils() {

    }

    /**
     * 
     * @param ec2ServiceClient
     * @param request
     * @return
     */
    public static List<Volume> collectAllVolumes(AmazonEC2 ec2ServiceClient,
            DescribeVolumesRequest request) {
        DescribeVolumesResult result;
        String nextToken;
        List<Volume> volumes = new ArrayList<>();
        do {
            result = ec2ServiceClient.describeVolumes(request);
            volumes.addAll(result.getVolumes());
            nextToken = result.getNextToken();
            request.setNextToken(nextToken);
        } while (null != nextToken);
        return volumes;
    }


    public static List<FlowLog> getFlowLogs(AmazonEC2 ec2ServiceClient,
            DescribeFlowLogsRequest describeFlowLogsRequest) {
        List<FlowLog> flowLogs = new ArrayList<>();
        DescribeFlowLogsResult flowLogsResult;
        String nextToken = null;
        do {
            try {
                flowLogsResult = ec2ServiceClient
                        .describeFlowLogs(describeFlowLogsRequest);
            } catch (RuleExecutionFailedExeption exception) {
                logger.error(exception.getMessage()); 
                nextToken = null;
                continue;
                
            }
            if(null!=flowLogsResult){
            flowLogs.addAll(flowLogsResult.getFlowLogs());
            nextToken = flowLogsResult.getNextToken();
            }
           
            describeFlowLogsRequest.setNextToken(nextToken);

        } while (null != nextToken);

        return flowLogs;
    }

    public static Filter setFilters(String filterName, String filterValue) {

        Filter filter = new Filter();
        filter.setName(filterName);
        filter.setValues(Arrays.asList(filterValue));
        return filter;
    }

    public static Map<String, Boolean> checkAccessibleToAll(
            Set<GroupIdentifier> secuityGroups, String portToCheck,
            String sgRulesUrl, String cidrIp) throws Exception {
        String responseJson = null;
        String fromPort = null;
        String toPort = null;
        String ipprotocol = null;
        JsonParser jsonParser;
        JsonObject resultJson;
        HashMap<String, Boolean> openPorts = new HashMap<>();
        StringBuilder urlToQueryBuffer = new StringBuilder(sgRulesUrl);
        for (GroupIdentifier securityGrp : secuityGroups) {
            StringBuilder requestBody = new StringBuilder(
                    "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"groupid.keyword\":{\"value\":\""
                            + securityGrp.getGroupId()
                            + "\"}}},{\"term\":{\"cidrip.keyword\":{\"value\":\""
                            + cidrIp
                            + "\"}}},{\"term\":{\"type.keyword\":{\"value\":\"inbound\"}}}]}}}");
            try {
                responseJson = PacmanUtils.doHttpPost(
                        urlToQueryBuffer.toString(), requestBody.toString());
            } catch (RuleExecutionFailedExeption e) {
                throw new RuleExecutionFailedExeption(e.getMessage());
            }
            jsonParser = new JsonParser();
            resultJson = (JsonObject) jsonParser.parse(responseJson);

            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get(
                    "hits").toString());
            JsonArray hitsArray = hitsJson.getAsJsonArray("hits");
            logger.info(sgRulesUrl);
            logger.info(securityGrp.getGroupId());
            logger.info(portToCheck);
            for (int i = 0; i < hitsArray.size(); i++) {
                JsonObject source = hitsArray.get(i).getAsJsonObject()
                        .get("_source").getAsJsonObject();
                fromPort = source.get("fromport").getAsString();
                toPort = source.get("toport").getAsString();
                ipprotocol = source.get("ipprotocol").getAsString();
                logger.info(fromPort);
                logger.info(toPort);
                logger.info(ipprotocol);
                if (!org.apache.commons.lang.StringUtils.isEmpty(fromPort)
                        && !org.apache.commons.lang.StringUtils.isEmpty(toPort)) {
                    String fPort = "FromPort_" + fromPort + "_" + ipprotocol;
                    String tPort = "ToPort_" + toPort + "_" + ipprotocol;
                    openPorts.put(fPort + "-" + tPort, true);
                }
            }
        }
        return openPorts;
    }
    
}
