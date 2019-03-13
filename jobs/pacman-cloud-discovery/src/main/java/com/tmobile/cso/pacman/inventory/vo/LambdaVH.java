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
package com.tmobile.cso.pacman.inventory.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.workspaces.model.Tag;



/**
 * The Class LambdaVH.
 */
public class LambdaVH {
	
	/** The lambda. */
	private FunctionConfiguration lambda;
	
	/** The tags. */
	private List<Tag> tags;
	
	/**
	 * Instantiates a new lambda VH.
	 *
	 * @param lambda the lambda
	 * @param tagsList the tags list
	 */
	public LambdaVH(FunctionConfiguration lambda,Map<String,String> tagsList){
		this.lambda = lambda;
		this.tags = new ArrayList<>();
		Iterator<Entry<String, String>> it = tagsList.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, String> entry = it.next();
			Tag tag = new Tag();
			tag.setKey(entry.getKey());
			tag.setValue(entry.getValue());
			tags.add(tag);
		}
	}
}
