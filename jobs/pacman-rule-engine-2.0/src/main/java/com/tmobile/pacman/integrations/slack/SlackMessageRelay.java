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

package com.tmobile.pacman.integrations.slack;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.pacman.util.CommonUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class SlackMessageRelay.
 *
 * @author kkumar
 */
public class SlackMessageRelay {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SlackMessageRelay.class);

    /** The slack message template. */
    private String SLACK_MESSAGE_TEMPLATE="{\"channel\": \"@%s\", \"text\": \"%s.\" }";

    /** The Constant SLACK_WEBHOOK. */
    private static final String SLACK_WEBHOOK="pacman.integrations.slack.webhook.url";

    /**
     * Send message.
     *
     * @param userId the user id
     * @param message the message
     * @return the boolean
     */
    public Boolean sendMessage(String userId,String message){

        String payload =  String.format(SLACK_MESSAGE_TEMPLATE,userId,message);
        String response="";
        LOGGER.debug(payload);
        String slackUrl = CommonUtils.getPropValue(SLACK_WEBHOOK);
        LOGGER.debug(slackUrl);

        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(slackUrl);
        post.addParameter("payload", payload);
        post.getParams().setContentCharset("UTF-8");
        Integer responseCode=null;
        try {
            responseCode = client.executeMethod(post);
        } catch (IOException e) {

           LOGGER.error("error sending message to slack",e);return Boolean.FALSE;
        }
        try {
                 response = post.getResponseBodyAsString();
        } catch (IOException e) {
            LOGGER.error("error getting response from slack",e);return Boolean.FALSE;
        }
        if (responseCode != HttpStatus.SC_OK) {
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }

    }



    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        new SlackMessageRelay().sendMessage("anil", "hello world");
    }

}
