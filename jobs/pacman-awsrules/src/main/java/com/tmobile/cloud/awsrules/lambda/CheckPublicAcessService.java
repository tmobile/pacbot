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
  Modified Date: Jul 27, 2017

 **/
package com.tmobile.cloud.awsrules.lambda;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class CheckPublicAcessService implements RequestHandler<String, String> {
    private static final Logger logger = LoggerFactory
            .getLogger(CheckPublicAcessService.class);

    /**
     * The method will get triggered from Lambda Function with following
     * parameters
     *
     * @param s3bucketURL
     *
     *            ************* Following are the Rule Parameters********* <br>
     * <br>
     *
     *            s3bucketURL : URL of the s3 bucket<br>
     * <br>
     *
     * @param context
     *            null
     *
     */

    @Override
    public String handleRequest(String s3bucketURL, Context context) {
        String publicAccess = null;
        try {
            URL s3BucketURL = new URL(s3bucketURL);
            HttpURLConnection connection = (HttpURLConnection) s3BucketURL
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();
            if (code == 200) {
                publicAccess = "YES";
            } else {
                publicAccess = "NO";
            }

        } catch (MalformedURLException malexp) {
            logger.error("error", malexp);
        } catch (IOException ioex) {
            logger.error("IOException", ioex);
        }

        return publicAccess;
    }
}
