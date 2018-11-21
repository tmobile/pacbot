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
  Modified Date: Dec 27, 2017
  
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
package com.tmobile.pacman.api.commons.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Strings;
import com.tmobile.pacman.api.commons.Constants;

/**
 * @author kkumar
 *
 */
public class DateUtils {

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List getAllDatesBetweenDates(Date startDate, Date endDate, String dateFormat) {
		Date begin = new Date(startDate.getTime());
		LinkedList list = new LinkedList();
		DateFormat df = null;
		if (!Strings.isNullOrEmpty(dateFormat))
			df = new SimpleDateFormat(dateFormat);
		else{
			return list;
		}

		if (Strings.isNullOrEmpty(dateFormat)) {
			list.add(new Date(begin.getTime()));
		} else {
			list.add(df.format(begin.getTime()));
		}
		while (begin.compareTo(endDate) < 0) {
			begin = new Date(begin.getTime() + Constants.MILLIS_ONE_DAY);
			if (Strings.isNullOrEmpty(dateFormat)) {
				list.add(new Date(begin.getTime()));
			} else {
				list.add(df.format(begin.getTime()));
			}

		}

		return list;
	}

//	public static void main(String[] args) {
//
//		Date endDate = new Date(System.currentTimeMillis());
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DATE, -15);
//		Date startDate = cal.getTime();
//		List dates = getAllDatesBetweenDates(startDate, endDate, "MM-dd-yyyy");
//		dates.stream().forEach(System.out::println);
//
//	}
}
