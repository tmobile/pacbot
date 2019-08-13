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
import { Subscription } from 'rxjs/Subscription';
import { DataCacheService } from '../../../../core/services/data-cache.service';
import { ActivatedRoute, UrlSegment, Router } from '@angular/router';
import { AutorefreshService } from '../../../services/autorefresh.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import { environment } from './../../../../../environments/environment';
import { OmniSearchDataService } from '../../../services/omni-search-data.service';
import { UtilsService } from '../../../../shared/services/utils.service';
import { DomainTypeObservableService } from '../../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-omni-search-page',
  templateUrl: './omni-search-page.component.html',
  styleUrls: ['./omni-search-page.component.css'],
  providers: [
    OmniSearchDataService,
    AutorefreshService,
    LoggerService,
    ErrorHandlingService
  ]
})
export class OmniSearchPageComponent implements OnInit, OnDestroy {
  /*
    ***************  Component details  **********************
    * @author Trinanjan
   * @desc add description here about the component
    ************ Component details ends here  ******************
   */
  private dropdownData = [];
  public errorMessage: any;
  public dataComing = true;
  public showLoader = true;
  datacoming;
  public seekdata = false;
  private dataSubscription: Subscription;
  private subscriptionDomain: Subscription;
  selectedDomain: any;
  durationParams: any;
  autoRefresh: boolean;
  private autorefreshInterval;
  constructor(
    private dataStore: DataCacheService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private omniSearchDataService: OmniSearchDataService,
    private autorefreshService: AutorefreshService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private utils: UtilsService,
    private domainObservableService: DomainTypeObservableService
  ) {
    // domain subscription
    this.subscriptionDomain = this.domainObservableService
      .getDomainType()
      .subscribe(domain => {
        this.selectedDomain = domain;
        this.updateComponent();
      });
  }

  ngOnInit() {
    try {
      this.durationParams = this.autorefreshService.getDuration();
      this.durationParams = parseInt(this.durationParams, 10);
      this.autoRefresh = this.autorefreshService.autoRefresh;

      const afterLoad = this;
      if (this.autoRefresh !== undefined) {
        if (
          this.autoRefresh === true ||
          this.autoRefresh.toString() === 'true'
        ) {
          this.autorefreshInterval = setInterval(function() {
            afterLoad.omniSearchData();
          }, this.durationParams);
        }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  updateComponent() {
    /* All functions variables which are required to be set for component to be reloaded should go here */
    this.showLoader = true;
    this.dataComing = false;
    this.seekdata = false;
    this.getData();
  }
  /* Function to get Data */
  getData() {
    /* All functions to get data should go here */
    this.omniSearchData();
  }
  omniSearchData() {
    try {
      if (this.dataSubscription) {
        this.dataSubscription.unsubscribe();
      }

      const omniSearchomniSearchCategoriesUrl =
        environment.omniSearchCategories.url;
      const omniSearchCategoriesMethod = environment.omniSearchCategories.method;

      const queryParam = {
        domain: this.selectedDomain
      };

      this.dataSubscription = this.omniSearchDataService.getOmniSearchCategories(
        omniSearchomniSearchCategoriesUrl,
        omniSearchCategoriesMethod,
        queryParam
      ).subscribe(
        response => {
          try {
            if (response.length === 0) {
              this.getErrorValues();
              this.errorMessage = 'noDataAvailable';
            } else {
              this.showLoader = false;
              this.seekdata = false;
              this.dataComing = true;
              this.dropdownData = response;
              // this.dropdownData.splice(0, 0, "All"); /* Commented by Puneet for now, All will be added later */
              this.dropdownData.splice(0, 0);
              this.dataStore.set(
                'OmniSearchCategories',
                JSON.stringify(response)
              );
            }
          } catch (e) {
            this.errorMessage = this.errorHandling.handleJavascriptError(e);
            this.getErrorValues();
          }
        },
        error => {
          this.errorMessage = error;
          this.getErrorValues();
        }
      );
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  // assign error values...

  getErrorValues(): void {
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
  }

  navigatePage(event) {
    try {
      this.dataStore.clear('omnisearchLastAppliedFilter');
      this.dataStore.clear('OmniSearchFirstLevelIndex');
      this.dataStore.clear('OmniSearchSecondLevelIndex');
      this.dataStore.clear('omniSearchFilterRefineByCount');
      // If Dropdown is not selected By Default send first option in the queryparams
      if (this.utils.isObjectEmpty(event.searchValue)) {
        event.searchValue = {
          id: this.dropdownData[0],
          value: this.dropdownData[0]
        };
      }
      const queryData = {
        filterValue: event.searchValue.value.toString(),
        searchText: event.filterValue.toString()
      };
      this.router.navigate(
        [
          '../',
          'omni-search-details',
          queryData.filterValue,
          queryData.searchText
        ],
        {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'merge'
        }
      );
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  ngOnDestroy() {
    try {
      if (this.dataSubscription) {
        this.dataSubscription.unsubscribe();
      }
      if (this.subscriptionDomain) {
        this.subscriptionDomain.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }
}
