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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.tmobile.pacman.commons.autofix.PacmanFix;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.reactors.PacReactor;
import com.tmobile.pacman.reactors.ReactorShell;

// TODO: Auto-generated Javadoc
/**
 * The Class ReflectionUtils.
 */
public class ReflectionUtils {
    
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);
    

    /**
     * Find associate class.
     *
     * @param ruleKey the rule key
     * @return the class
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws ClassNotFoundException the class not found exception
     */
    public static Class<?> findAssociateClass(String ruleKey)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Reflections reflections = new Reflections("com.tmobile");
        Set<Class<?>> allRules = reflections.getTypesAnnotatedWith(PacmanRule.class);
        for (Class<?> ruleClass : allRules) {
            PacmanRule rule = ruleClass.getAnnotation(PacmanRule.class);
            if (rule.key().equals(ruleKey)) {
                return ruleClass;
            }
        }
        // if code reached here , this means no class found associated to this
        // key
        throw new ClassNotFoundException("cannot find class associated to rule");
    }

    /**
     * Find fix class.
     *
     * @param ruleKey the rule key
     * @return the class
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws ClassNotFoundException the class not found exception
     */
    public static Class<?> findFixClass(String ruleKey)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Reflections reflections = new Reflections("com.tmobile");
        Set<Class<?>> allRules = reflections.getTypesAnnotatedWith(PacmanFix.class);
        for (Class<?> ruleClass : allRules) {
            PacmanFix rule = ruleClass.getAnnotation(PacmanFix.class);
            if (rule.key().equals(ruleKey)) {
                return ruleClass;
            }
        }
        // if code reached here , this means no class found associated to this
        // key
        throw new ClassNotFoundException("cannot find class associated to rule");
    }

    /**
     * Find associate class.
     *
     * @param annotationClass the annotation class
     * @param hintPackage the hint package
     * @return the class
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws ClassNotFoundException the class not found exception
     */
    public static Class<?> findAssociateClass(Class annotationClass, String hintPackage)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Reflections reflections;
        if (!Strings.isNullOrEmpty(hintPackage))
            reflections = new Reflections(hintPackage);
        else {
            reflections = new Reflections();
        }
        Set<Class<?>> allClass = reflections.getTypesAnnotatedWith(annotationClass);
        if(allClass.size()>1){
            logger.error("multiple classes found with @PacmanJob annotation, will pick first in the classpath");
        }
        for (Class<?> _class : allClass) {
            return _class;
        }
        // if code reached here , this means no class found associated to this
        // key
        throw new ClassNotFoundException("cannot find associated class");
    }

    /**
     * Find associated method.
     *
     * @param ruleClass the rule class
     * @param methodName the method name
     * @return the method
     * @throws NoSuchMethodException the no such method exception
     */
    public static Method findAssociatedMethod(Object ruleClass, String methodName) throws NoSuchMethodException {
        Method[] methodArr = ruleClass.getClass().getDeclaredMethods();
        for (Method method : methodArr) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        // if control is here that means no execute method found in the class
        throw new NoSuchMethodException("unable to find  "+ methodName +" method");
    }

    /**
     * Find entry method.
     *
     * @param ruleClass the rule class
     * @param entryAnnotation the entry annotation
     * @return the method
     * @throws NoSuchMethodException the no such method exception
     */
    public static Method findEntryMethod(Object ruleClass, Class entryAnnotation) throws NoSuchMethodException {
        Method[] methodArr = ruleClass.getClass().getDeclaredMethods();
        for (Method method : methodArr) {
            if (method.isAnnotationPresent(entryAnnotation)) {
                return method;
            }
        }
        // if control is here that means no execute method found in the class
        throw new NoSuchMethodException("unable to find the execute method");
    }

    /**
     * @param eventName
     * @return
     */
    public static Set<ReactorShell> findEventHandlers(String eventName) {
        Reflections reflections = new Reflections("com.tmobile");
        Set<Class<?>> allReactors = reflections.getTypesAnnotatedWith(PacReactor.class);
        Object reactorObject;
        Method reactMethod = null;
        Method backupMethod = null;
        Set<ReactorShell>  reactors = new HashSet();
        for (Class<?> reactor : allReactors) {
            PacReactor pacReactor = reactor.getAnnotation(PacReactor.class);
            if (isAMatchingEvent(eventName, pacReactor.eventsofInterest())) {
                try {
                        reactorObject = reactor.newInstance();
                } catch (InstantiationException e) {
                    logger.error("unable to create reactor" + e.getMessage());continue;
                } catch (IllegalAccessException e) {
                    logger.error("unable to create reactor" + e.getMessage());
                    continue;
                }
                // executeMethod =
                // ReflectionUtils.findEntryMethod(ruleObject,PacmanExecute.class);
                try {
                        reactMethod = findAssociatedMethod(reactorObject, "react");
                        backupMethod = findAssociatedMethod(reactorObject, "backup");
                        
                } catch (NoSuchMethodException e) {
                    logger.error("unable to find method in reactor" + reactor);
                    continue;
                }
                reactors.add(new ReactorShell(pacReactor,reactorObject, reactMethod, backupMethod));
            }
        }
        return reactors;
    }
    
    /**
     * 
     * @param eventName
     * @param events
     * @return
     */
    private static boolean isAMatchingEvent(String eventName, String events) {
        List<String> eventsofIntrestList = Arrays.asList(events.split("\\s*,\\s*"));// convert comma separated string to array list 
        return eventsofIntrestList.contains(eventName);
    }

}
