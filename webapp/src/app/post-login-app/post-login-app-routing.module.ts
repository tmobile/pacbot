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
import { Routes, RouterModule } from '@angular/router';
import { PostLoginAppComponent } from './post-login-app.component';
import { ChangeDefaultAssetGroupComponent } from './change-default-asset-group/change-default-asset-group.component';
import { FirstTimeUserJourneyComponent } from './first-time-user-journey/first-time-user-journey.component';
import { KnowYourDashboardComponent } from './know-your-dashboard/know-your-dashboard.component';
import { ComplianceComponent } from './../pacman-features/modules/compliance/compliance.component';
import { AssetsComponent } from './../pacman-features/modules/assets/assets.component';
import { AuthGuardService } from './../shared/services/auth-guard.service';
import { ToolsComponent } from '../pacman-features/modules/tools/tools.component';
import { StatsOverlayComponent } from './stats-overlay/stats-overlay.component';
import { DomainOverlayComponent } from './domain-overlay/domain-overlay.component';
import { HelpTextComponent } from '../shared/help-text/help-text.component';
import { OmnisearchComponent } from '../pacman-features/modules/omnisearch/omnisearch.component';
import { AdminComponent } from '../pacman-features/modules/admin/admin.component';
import { VulnerabilityReportComponent } from './vulnerability-report/vulnerability-report.component';
import { RecommendationsDetailsComponent } from '../pacman-features/modules/compliance/recommendations-details/recommendations-details.component';
import {AssetDetailsComponent} from '../pacman-features/modules/assets/asset-details/asset-details.component';

const routes: Routes = [
    {
        path: 'pl',
        component: PostLoginAppComponent,
        children: [
            {
                path: 'first-time-user-journey',
                component: FirstTimeUserJourneyComponent
            },
        ],
        canActivate: [AuthGuardService]
    },
    {
        path: 'pl',
        component: PostLoginAppComponent,
        children: [
            {
                path: 'compliance',
                component: ComplianceComponent,
                data: {sequence: 1},
                loadChildren: './../pacman-features/modules/compliance/compliance.module#ComplianceModule',
            },
            {
                path: 'assets',
                component: AssetsComponent,
                data: {sequence: 2},
                loadChildren: './../pacman-features/modules/assets/assets.module#AssetsModule'
            },
            {
                path: 'tools',
                component: ToolsComponent,
                data: {sequence: 3},
                loadChildren: './../pacman-features/modules/tools/tools.module#ToolsModule'
            },
            {
                path: 'omnisearch',
                component: OmnisearchComponent,
                data: {sequence: 4},
                loadChildren: './../pacman-features/modules/omnisearch/omnisearch.module#OmnisearchModule'
            },
            {
                path: 'admin',
                component: AdminComponent,
                data: {sequence: 5},
                loadChildren: './../pacman-features/modules/admin/admin.module#AdminModule'
            },
            {
                path: 'change-default-asset-group',
                component: ChangeDefaultAssetGroupComponent,
                outlet: 'modal'
            },
            {
                path: 'stats-overlay',
                component: StatsOverlayComponent,
                outlet: 'modalBGMenu'
            },
            {
                path: 'domain-overlay',
                component: DomainOverlayComponent,
                outlet: 'modalBGMenu'
            },
            {
                path: 'vulnerability-report',
                component: VulnerabilityReportComponent,
                outlet: 'modalBGMenu'
            },
            {
                path: 'know-your-dashboard',
                component: KnowYourDashboardComponent,
                outlet: 'kydModal'
            },
            {
                path: 'help-text',
                component: HelpTextComponent,
                outlet: 'helpTextModal'
            },
        ],
        canActivate: [AuthGuardService]
    },
    {
        path: '**',
        redirectTo: '/home'
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class PostLoginAppRoutingModule {}
