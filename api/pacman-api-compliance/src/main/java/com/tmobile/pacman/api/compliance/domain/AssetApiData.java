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
  Modified Date: Oct 26, 2017

 **/
package com.tmobile.pacman.api.compliance.domain;
/**
 * The Class AssetApiData.
 */
public class AssetApiData {
    private String ag;

    private String datasource;

    private AssetCountDTO[] applications;

    private AssetCountDTO[] environments;

    private AssetCountDTO[] targettypes;

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public AssetCountDTO[] getTargettypes() {
        return targettypes;
    }

    public void setTargettypes(AssetCountDTO[] targettypes) {
        this.targettypes = targettypes;
    }

    public AssetCountDTO[] getEnvironments() {
        return environments;
    }

    public void setEnvironments(AssetCountDTO[] environments) {
        this.environments = environments;
    }

    public String getAg() {
        return ag;
    }

    public void setAg(String ag) {
        this.ag = ag;
    }

    public AssetCountDTO[] getApplications() {
        return applications;
    }

    public void setApplications(AssetCountDTO[] applications) {
        this.applications = applications;
    }

    @Override
    public String toString() {
        return "ClassPojo [applications = " + applications + ", ag = " + ag
                + ", environments = " + environments + ", targettypes = "
                + targettypes + ",datasource = " + datasource + "]";
    }
}
