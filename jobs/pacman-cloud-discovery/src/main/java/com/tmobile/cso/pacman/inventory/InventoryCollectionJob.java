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
package com.tmobile.cso.pacman.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.tmobile.pacman.commons.jobs.PacmanJob;

/**
 * The Class InventoryCollectionJob.
 */
@PacmanJob(methodToexecute="execute",jobName="AWS Data Collector", desc="Job to fetch aws info and load to Redshift" ,priority=5)
public class InventoryCollectionJob {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		Map<String,String> params = new HashMap<>();
		Arrays.asList(args).stream().forEach(obj-> {
			 for(String param :obj.split("[*]")){
				String[] paramTemp = param.split("=");
				params.put(paramTemp[0], paramTemp[1]);
			 }
		});
		execute(params);
	}
	
	/**
	 * Execute.
	 *
	 * @param params the params
	 */
	public static void execute(Map<String,String> params){
		if( !(params==null || params.isEmpty())){
			params.forEach((k,v) -> System.setProperty(k, v));
		}
		InventoryFetchApplication.main( new String[]{});
	}
}
