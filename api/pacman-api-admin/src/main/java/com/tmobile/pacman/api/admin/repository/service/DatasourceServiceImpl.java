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
package com.tmobile.pacman.api.admin.repository.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.admin.repository.DatasourceRepository;
import com.tmobile.pacman.api.admin.repository.model.Datasource;
import com.tmobile.pacman.api.commons.Constants;

/**
 * DataSource Service Implementations
 */
@Service
public class DatasourceServiceImpl implements DatasourceService, Constants {

	@Autowired
	private DatasourceRepository datasourceRepository;

	@Override
	public List<Datasource> getAllDatasourceDetails() {
		return datasourceRepository.findAll();
	}
}
