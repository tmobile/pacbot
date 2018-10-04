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

import { Component, OnInit, OnDestroy } from '@angular/core';
import { fadeAnimation } from '../../../shared/animations/animations';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { LoggerService } from '../../../shared/services/logger.service';
import { Subscription } from 'rxjs/Subscription';
import { RouterUtilityService } from '../../../shared/services/router-utility.service';
import { WorkflowService } from '../../../core/services/workflow.service';

@Component({
  selector: 'app-omnisearch',
  templateUrl: './omnisearch.component.html',
  styleUrls: ['./omnisearch.component.css'],
  animations: [fadeAnimation]
})

export class OmnisearchComponent implements OnInit, OnDestroy {
  private subscribeForRouteChange: Subscription;
  private moduleName = 'omnisearch';
  pageTitle: string;
  querySubscription: Subscription;
  detailsOfCurrentPage: any;
  tvState: any;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private routerUtilityService: RouterUtilityService,
    private loggerService: LoggerService,
    private workflowService: WorkflowService
  ) {}

  ngOnInit() {
    this.getRoute();
    this.querySubscription = this.activatedRoute.queryParams.subscribe(
      queryParams => {
        this.tvState = queryParams['tv'];
      }
    );

    this.pageTitle = 'Omnisearch';
  }

  getRoute() {
    this.subscribeForRouteChange = this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {

        const urlPath = this.routerUtilityService.getFullUrlFromSnapshopt(this.router.routerState.snapshot.root);
        const queryParams = this.routerUtilityService.getQueryParametersFromSnapshot(this.router.routerState.snapshot.root);

        this.workflowService.addPageToModuleTracker(this.moduleName, urlPath);
        this.workflowService.addQueryParamsToModuleTracker(this.moduleName, JSON.stringify(queryParams));
      }
    });
  }

  getRouterOutletState(outlet) {
    return outlet.isActivated ? outlet.activatedRoute : '';
  }

  ngOnDestroy() {
    try {
      if (this.querySubscription) {
        this.querySubscription.unsubscribe();
      }
      if (this.subscribeForRouteChange) {
        this.subscribeForRouteChange.unsubscribe();
      }
    } catch (error) {
      this.loggerService.log('error', error);
    }
  }
}
