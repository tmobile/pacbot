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
  Modified Date: Jun 11, 2018

**/
/*
 *Copyright 2016-2017 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 *Licensed under the Amazon Software License (the "License"). You may not use
 * this file except in compliance with the License. A copy of the License is located at
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tmobile.pacman.api.commons.exception;

/**
 * @author kkumar
 * This is the Generic data exception to be thrown when no specific exceptions matches, usually a replacement of Exception class
 * when used in Repo class
 */
public class DataException extends Exception {

	/**
	 *
	 */
	public DataException() {
		// TODO Auto-generated constructor stub
	}


	/**
	 *
	 */
	public DataException(String msg) {
		super(msg);
	}

	/**
	 *
	 * @param th
	 */
	public DataException(Throwable th){
		super(th);
	}

	/**
	 *
	 * @param msg
	 * @param th
	 */
	public DataException(String msg,Throwable th){
		super(msg,th);
	}



}
