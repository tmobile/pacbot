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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { RouterUtilityService } from './../../../shared/services/router-utility.service';
import { LoggerService } from '../../../shared/services/logger.service';
import { WorkflowService } from '../../../core/services/workflow.service';
import { DomainMappingService } from '../../../core/services/domain-mapping.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-compliance',
  templateUrl: './compliance.component.html',
  styleUrls: ['./compliance.component.css'],
  providers: []
})

export class ComplianceComponent implements OnInit, OnDestroy {

  private moduleName = 'compliance';
  private subscribeForRouteChange: Subscription;
  private subscriptionToDomainType: Subscription;
  private selectedDomainName = '';
  pageTitle: string;
  issueId: string;
  listOfContextualMenuItems: any[];
  querySubscription: Subscription;
  tvState: any;
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private routerUtilityService: RouterUtilityService,
    private loggerService: LoggerService,
    private workflowService: WorkflowService,
    private domainMappingService: DomainMappingService,
    private domainTypeObservableService: DomainTypeObservableService) {
  }

  ngOnInit() {
    try {
      this.pageTitle = 'Compliance';

      this.updateMenuListOnDomainUpdate();
      this.subscribeToDomainType();
      this.getRoute();

      this.querySubscription = this.activatedRoute.queryParams.subscribe(queryParams => {
        this.tvState = queryParams['tv'];
      });
    } catch (error) {
      this.loggerService.log('JS error', error);
    }
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

  updateMenuListOnDomainUpdate() {
    try {
      const moduleName = 'compliance';
      const domainName = this.selectedDomainName || '';
      this.listOfContextualMenuItems = this.domainMappingService.getDashboardsApplicableForADomain(domainName, moduleName);
    } catch (error) {
      this.loggerService.log('JS Error ', error);
    }
  }

  /**
   *This is the subscribtion function for domain selection
   */
  subscribeToDomainType() {
    try {
      this.subscriptionToDomainType = this.domainTypeObservableService
          .getDomainType()
          .subscribe(DomainName => {
            if (DomainName) {
              this.selectedDomainName = DomainName;
              this.updateMenuListOnDomainUpdate();
            }
          });
    } catch (error) {
      this.loggerService.log('error', error);
    }
  }

  public getRouterOutletState(outlet) {
    return outlet.isActivated ? outlet.activatedRoute : '';
  }

  ngOnDestroy() {
    try {
      if (this.subscribeForRouteChange) {
        this.subscribeForRouteChange.unsubscribe();
      }
      if (this.subscriptionToDomainType) {
        this.subscriptionToDomainType.unsubscribe();
      }
      if (this.querySubscription) {
        this.querySubscription.unsubscribe();
      }
    } catch (error) {
      this.loggerService.log('error', error);
    }
  }
}
