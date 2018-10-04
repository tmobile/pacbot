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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Joiner;
import com.tmobile.pacman.commons.jobs.PacmanJob;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ProgramExitUtils;
import com.tmobile.pacman.util.ReflectionUtils;

// TODO: Auto-generated Javadoc
/**
 * This class is responsible for firing the execute method of the Job.
 *
 * @author kkumar
 */
public class JobExecutor {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);

    /**
     * The main method.
     *
     * @param args            job parameters
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws InvocationTargetException the invocation target exception
     * @throws JsonParseException the json parse exception
     * @throws JsonMappingException the json mapping exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws NoSuchMethodException the no such method exception
     * @throws ClassNotFoundException the class not found exception
     */
    public static void main(String[] args)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            JsonParseException, JsonMappingException, IOException, NoSuchMethodException, ClassNotFoundException {

        Map<String, String> jobParams = new HashMap<String, String>();
        String programArgs = "";

        if (args.length > 0) {
            programArgs = args[0];
            jobParams = CommonUtils.createParamMap(programArgs);
            logger.debug("job Param String " + programArgs);
        } else {
            logger.debug("No arguments available for job execution, expecting a no args method");
        }
        setUncaughtExceptionHandler();
        logger.debug("shutdown hook engaged.");
        Method executeMethod = null;
        Object jobObject = null;
        Class<?> jobClass = null;

        try {
            jobClass = ReflectionUtils.findAssociateClass(PacmanJob.class, jobParams.get("package_hint"));
            PacmanJob job = jobClass.getAnnotation(PacmanJob.class);
            String methodName = job.methodToexecute();
            jobObject = jobClass.newInstance();
            executeMethod = ReflectionUtils.findAssociatedMethod(jobObject, methodName);
        } catch (Exception e) {
            logger.error("Please check the job class complies to implemetation contract", e);
            ProgramExitUtils.exitWithError();
        }
        if(null==executeMethod){
            logger.error("unable to find execute method");
            ProgramExitUtils.exitWithError();
        }else
        {
            long startTime = System.nanoTime();
            // loop through resources and call rule execute method
            try {
                if (hasArgumentsOtherThenHints(jobParams))
                    executeMethod.invoke(jobObject, Collections.unmodifiableMap(jobParams)); // let rule not allow modify input
                else {
                    executeMethod.invoke(jobObject);
                }
            } catch (Exception e) {
                logger.debug("job execution failed", e);
                ProgramExitUtils.exitWithError();
            }
            long endTime = System.nanoTime();
            long timeTakenToExecute = TimeUnit.MINUTES.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            logger.info("Elapsed time in minutes for evaluation: " + timeTakenToExecute);
            startTime = System.nanoTime();
            // process rule evaluations the annotations based on result
            Map<String, String> evalResults = new HashMap<>();
            evalResults.put("time taken for job execution", timeTakenToExecute + "");
            publishMetrics(evalResults);
            ProgramExitUtils.exitSucessfully();
        }
    }

    /**
     * Checks for arguments other then hints.
     *
     * @param jobParams the job params
     * @return true, if successful
     */
    private static boolean hasArgumentsOtherThenHints(Map<String, String> jobParams) {
        for (Entry<String, String> jobParam : jobParams.entrySet()) {
            if (!"package_hint".equals(jobParam.getKey())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Publish metrics.
     *
     * @param evalResults the eval results
     * @return the boolean
     */
    private static Boolean publishMetrics(Map<String, String> evalResults) {
        logger.info(Joiner.on("#").withKeyValueSeparator("=").join(evalResults));
        return Boolean.TRUE;
    }

    /**
     * in case any rule throws exception and it reaches main, this will make
     * sure the VM is terminated gracefully close all clients here.
     */
    private static void setUncaughtExceptionHandler() {
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String stacktrace = sw.toString();
                logger.error(stacktrace);
            }
        });
    }

}
