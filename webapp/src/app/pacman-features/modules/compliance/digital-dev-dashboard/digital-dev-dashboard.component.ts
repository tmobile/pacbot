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

import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';
import {WorkflowService} from '../../../../core/services/workflow.service';
import {LoggerService} from '../../../../shared/services/logger.service';
import {FilterManagementService} from '../../../../shared/services/filter-management.service';
import {ErrorHandlingService} from '../../../../shared/services/error-handling.service';
import {AssetGroupObservableService} from '../../../../core/services/asset-group-observable.service';
import {DomainTypeObservableService} from '../../../../core/services/domain-type-observable.service';
import {UtilsService} from '../../../../shared/services/utils.service';
import {RouterUtilityService} from '../../../../shared/services/router-utility.service';

@Component({
  selector: 'app-digital-dev-dashboard',
  templateUrl: './digital-dev-dashboard.component.html',
  styleUrls: ['./digital-dev-dashboard.component.css']
})

export class DigitalDevDashboardComponent implements OnInit, OnDestroy {

  assetGroupSubscription: Subscription;
  domainSubscription: Subscription;

  pageTitle = 'Digital Dev';
  breadcrumbDetails = {
    breadcrumbArray: ['Compliance'],
    breadcrumbLinks: ['compliance-dashboard'],
    breadcrumbPresent: 'Digital Dev Dashboard'
  };
  backButtonRequired: boolean;
  pageLevel = 0;
  errorMessage: string;
  agAndDomain = {};
  applicationFilter;
  isFilterRquiredOnPage = true;
  appliedFilters = {
    queryParamsWithoutFilter: {}, /* Stores the query parameter ibject without filter */
    pageLevelAppliedFilters: {} /* Stores the query parameter ibject without filter */
  };
  filterArray = []; /* Stores the page applied filter array */

  @Input() filter: any;

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private workflowService: WorkflowService,
              private logger: LoggerService,
              private filterManagementService: FilterManagementService,
              private errorHandling: ErrorHandlingService,
              private assetGroupObservableService: AssetGroupObservableService,
              private domainObservableService: DomainTypeObservableService,
              private utils: UtilsService,
              private routerUtilityService: RouterUtilityService) {
    this.subscribeToAssetGroup();
    this.subscribeToDomain();
  }

  ngOnInit() {
  }

  subscribeToAssetGroup() {
    this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroup => {
      if (assetGroup) {
        this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(this.pageLevel);
        this.agAndDomain['ag'] = assetGroup;
      }
    });
  }

  subscribeToDomain() {
    this.domainSubscription = this.domainObservableService.getDomainType().subscribe(domain => {
     if (domain) {
       this.agAndDomain['domain'] = domain;
     }
     this.reset();
     this.init();
     this.updateComponent();
    });
  }

  reset() {
    /* Reset the page */
    this.filterArray = [];
  }

  init() {
    /* Initialize */
    this.routerParam();
  }

  updateComponent() {
    /* Updates the whole component */
  }

  routerParam() {
    try {
      const currentQueryParams = this.routerUtilityService.getQueryParametersFromSnapshot(this.router.routerState.snapshot.root);

      if (currentQueryParams) {

        this.appliedFilters.queryParamsWithoutFilter = JSON.parse(JSON.stringify(currentQueryParams));
        delete this.appliedFilters.queryParamsWithoutFilter['filter'];

        this.appliedFilters.pageLevelAppliedFilters = this.utils.processFilterObj(currentQueryParams);

        this.filterArray = this.filterManagementService.getFilterArray(this.appliedFilters.pageLevelAppliedFilters);
        if (this.filterArray && this.filterArray.length) {
          for (let i = 0 ; i < this.filterArray.length; i++ ) {
            if (this.filterArray[i].key.toString().toLowerCase() === 'application') {
              this.applicationFilter = this.filterArray[i].value;
              break;
            }
          }
        } else {
          this.applicationFilter = undefined;
        }
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  updateUrlWithNewFilters(filterArr) {
    try {
      if (filterArr.length) {
        this.appliedFilters.pageLevelAppliedFilters = this.utils.arrayToObject(
        this.filterArray,
        'filterkey',
        'value'
      ); // <-- TO update the queryparam which is passed in the filter of the api
      this.appliedFilters.pageLevelAppliedFilters = this.utils.makeFilterObj(this.appliedFilters.pageLevelAppliedFilters);
    } else {
      this.appliedFilters.pageLevelAppliedFilters = {};
    }
    /**
     * To change the url
     * with the deleted filter value along with the other existing paramter(ex-->tv:true)
     */

    const updatedFilters = Object.assign(
        this.appliedFilters.pageLevelAppliedFilters,
        this.appliedFilters.queryParamsWithoutFilter
    );

    /*
      Update url with new filters
     */

    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: updatedFilters
    }).then(success => {
      this.routerParam();
    });

  } catch (error) {
      this.logger.log('error', error);
    }
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }

  }

  ngOnDestroy() {
    try {
      if (this.assetGroupSubscription) {
        this.assetGroupSubscription.unsubscribe();
      }
      if (this.domainSubscription) {
        this.domainSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', 'JS Error - ' + error);
    }
  }

}
