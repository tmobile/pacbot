/*
 *Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not use
 * this file except in compliance with the License. A copy of the License is located at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../../shared/shared.module';
import { AssetsRoutingModule } from './assets-routing.module';
import { AssetsComponent } from './assets.component';
import { AssetDashboardComponent } from './asset-dashboard/asset-dashboard.component';
import { AssetTrackerComponent } from './../../secondary-components/asset-tracker/asset-tracker.component';
import { TrackerBarComponent } from './../../secondary-components/tracker-bar/tracker-bar.component';
import { AwsAppTileComponent } from './../../secondary-components/aws-app-tile/aws-app-tile.component';
import { AwsResourceDetailsComponent } from './../../secondary-components/aws-resource-details/aws-resource-details.component';
import { ViewAllResourcesComponent } from './../../secondary-components/view-all-resources/view-all-resources.component';
import { MultilineChartComponent } from './../../secondary-components/multiline-chart/multiline-chart.component';
import { InventoryContainerComponent } from './../../secondary-components/inventory-container/inventory-container.component';
import { UtilizationContainerComponent } from './../../secondary-components/utilization-container/utilization-container.component';
import { AssetInfoComponent } from './../../secondary-components/asset-info/asset-info.component';
import { AssetTypeComponent } from './../../secondary-components/asset-type/asset-type.component';
import { AssetApplicationComponent } from './../../secondary-components/asset-application/asset-application.component';
import { AssetWafComponent } from './../../secondary-components/asset-waf/asset-waf.component';
import { AssetCropComponent } from './../../secondary-components/asset-crop/asset-crop.component';
import { AssetCertificateComponent } from './../../secondary-components/asset-certificate/asset-certificate.component';
import { RecommendationComponent } from './../../secondary-components/recommendation/recommendation.component';
import { AssetDetailsComponent } from './asset-details/asset-details.component';
import { OpenPortsComponent } from './../../secondary-components/open-ports/open-ports.component';
import { AssetSummaryComponent } from './../../secondary-components/asset-summary/asset-summary.component';
import { AssetContentsComponent } from './../../secondary-components/asset-contents/asset-contents.component';
import { PacmanPolicyViolationsComponent } from './../../secondary-components/pacman-policy-violations/pacman-policy-violations.component';
import { AssetListComponent } from './asset-list/asset-list.component';
import { AttributeComponent } from './../../secondary-components/attribute/attribute.component';
import { AccessGroupsComponent } from './../../secondary-components/access-groups/access-groups.component';
import { HostVulnerabilitiesComponent } from './../../secondary-components/host-vulnerabilities/host-vulnerabilities.component';
import { InstalledSoftwaresComponent } from './../../secondary-components/installed-softwares/installed-softwares.component';
import { OnpremAssetsComponent } from './onprem-assets/onprem-assets.component';
import { AgGridModule } from 'ag-grid-angular/main';
import { AwsNotificationsComponent } from './aws-notifications/aws-notifications.component';

@NgModule({
  imports: [
    CommonModule,
    AssetsRoutingModule,
    SharedModule,
    AgGridModule.withComponents(
        [OnpremAssetsComponent]
    )
  ],
  declarations: [
    AssetsComponent,
    AssetDashboardComponent,
    TrackerBarComponent,
    AssetTrackerComponent,
    AwsAppTileComponent,
    AwsResourceDetailsComponent,
    InventoryContainerComponent,
    ViewAllResourcesComponent,
    MultilineChartComponent,
    UtilizationContainerComponent,
    AssetInfoComponent,
    AssetTypeComponent,
    AssetApplicationComponent,
    AssetWafComponent,
    AssetCropComponent,
    AssetCertificateComponent,
    RecommendationComponent,
    AssetDetailsComponent,
    OpenPortsComponent,
    AssetSummaryComponent,
    AssetContentsComponent,
    PacmanPolicyViolationsComponent,
    AssetListComponent,
    AttributeComponent,
    AccessGroupsComponent,
    HostVulnerabilitiesComponent,
    InstalledSoftwaresComponent,
    OnpremAssetsComponent,
    AwsNotificationsComponent
  ]
})
export class AssetsModule { }
