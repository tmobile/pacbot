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
  Purpose:
  Author :santoshi
  Modified Date: Sep 7, 2017

 **/
package com.tmobile.cloud.awsrules.lambda;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tmobile.cloud.constants.PacmanRuleConstants;

public class CheckAccessToHost implements
        RequestHandler<Map<String, String>, String> {
    private static final Logger logger = LoggerFactory.getLogger(CheckAccessToHost.class);

    /**
     * The method will get triggered from Lambda Function with following
     * parameters
     *
     * @param input
     *
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     *
     *            host : Value of the Ip Address<br>
     * <br>
     *
     *            port : Value of the port <br>
     * <br>
     *
     * @param context
     *            null
     *
     */

    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        String host = input.get("host");
        String port = input.get("port");
        Socket socket = null;
        try {
             socket = new Socket();
            socket.connect(new InetSocketAddress(host, Integer.parseInt(port)),
                    1000);

        } catch (Exception e) {
            logger.error(PacmanRuleConstants.FAILED_MESSAGE,e);
            return PacmanRuleConstants.FAILED_MESSAGE;
        }finally{

                if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                   logger.error(PacmanRuleConstants.FAILED_MESSAGE,e);

                }
                }

        }

        return "success";
    }

}
