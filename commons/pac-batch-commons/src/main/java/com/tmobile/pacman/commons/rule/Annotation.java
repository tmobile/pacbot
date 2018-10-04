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
  Author :kkumar
  Modified Date: Jun 14, 2017

**/

package com.tmobile.pacman.commons.rule;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.tmobile.pacman.commons.PacmanSdkConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class Annotation.
 */
public class Annotation extends HashMap<String, String> {




    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Annotation.class);
	/**
	 * Instantiates a new annotation.
	 *
	 * @param annotation the annotation
	 */
	public Annotation(Annotation annotation) {
		Annotation annotationNew = new Annotation();
		annotationNew.putAll(annotation);
	}

	/**
	 * Instantiates a new annotation.
	 */
	public Annotation() {
	}

	/**
	 * Builds the annotation.
	 *
	 * @param ruleParam the rule param
	 * @param type the type
	 * @return the annotation
	 */
	public static Annotation buildAnnotation(Map<String, String> ruleParam,Annotation.Type type){
		Annotation annotation = new Annotation();
		annotation.put(PacmanSdkConstants.RULE_ID, ruleParam.get(PacmanSdkConstants.RULE_ID));
		annotation.put(PacmanSdkConstants.POLICY_ID, ruleParam.get(PacmanSdkConstants.POLICY_ID));
		annotation.put(PacmanSdkConstants.POLICY_VERSION, ruleParam.get(PacmanSdkConstants.POLICY_VERSION));
		annotation.put(PacmanSdkConstants.RESOURCE_ID, ruleParam.get(PacmanSdkConstants.RESOURCE_ID));
		annotation.put(PacmanSdkConstants.TYPE, type.value());
		return annotation;
	}


	/* (non-Javadoc)
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public String put(String key, String value) {
		if(!Strings.isNullOrEmpty(value)){
			key = CharMatcher.WHITESPACE.removeFrom(key);
			return super.put(key, value);
		}else{
		    logger.error(String.format("Null value not allowed %s = %s "  ,key, value));
			return null;
		}
	}


	/**
	 * The Enum Type.
	 */
	public enum Type {

		/** The issue. */
		ISSUE("issue"),

		/** The info. */
		INFO("info"),

		/** The recommendation. */
		RECOMMENDATION("recommendation"),

		/** The error. */
		ERROR("error");

		/** The value. */
		private String value;

		/**
		 * Instantiates a new type.
		 *
		 * @param value the value
		 */
		private Type(String value) {
			this.value=value;
		}

		 /**
 		 * Value.
 		 *
 		 * @return the string
 		 */
 		public String value() {
		        return value;
		    }
	}

}
