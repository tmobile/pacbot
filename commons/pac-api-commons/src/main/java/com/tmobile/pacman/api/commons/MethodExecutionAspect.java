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

package com.tmobile.pacman.api.commons;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
@ConditionalOnExpression("'${logging.esLoggingLevel}'=='TRACE'")
public class MethodExecutionAspect {

    private static final Logger log = LoggerFactory.getLogger(MethodExecutionAspect.class);

    /*
     * This is a pointcut which fires on method entry and exit. Some classes
     * from config package, Spring Security, Swagger etc. are excluded by making
     * use of the below pointcut wildcard expression. The config property
     * logging.esLoggingLevel should be set to TRACE for the required
     * environment if we need to see the entry and exit messages.
     */
    @Around("execution(* com.tmobile.pacman..*.*(..))  &&!execution(* com.tmobile.pacman..*.*config..*.*(..)) && !execution(* com.tmobile.pacman..*.*commons..*.*(..))")
    public void around(ProceedingJoinPoint pjp) throws Throwable {
        String entryMessage = "Entered " + pjp.getSignature().getDeclaringTypeName() + "::"
                + pjp.getSignature().getName();
        log.info(entryMessage);
        long start = System.currentTimeMillis();
        // We logged the start. Now, we will let the method do its course by
        // calling proceed()
        pjp.proceed();
        String exitMessage = "Exited " + pjp.getSignature().getDeclaringTypeName() + "::" + pjp.getSignature().getName()
                + "(). Took " + (System.currentTimeMillis() - start) + " ms.";
        log.info(exitMessage);
    }
}
