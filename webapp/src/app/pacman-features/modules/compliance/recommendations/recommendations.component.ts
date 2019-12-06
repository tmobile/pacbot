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

import { Component, OnInit, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { Subscription } from 'rxjs/Subscription';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { environment } from './../../../../../environments/environment';
import { Router, ActivatedRoute } from '@angular/router';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import { RouterUtilityService } from '../../../../shared/services/router-utility.service';
import { UtilsService } from '../../../../shared/services/utils.service';
import {RefactorFieldsService} from '../../../../shared/services/refactor-fields.service';
import { FilterManagementService } from '../../../../shared/services/filter-management.service';
import {DomainTypeObservableService} from '../../../../core/services/domain-type-observable.service';
import * as _ from 'lodash';
@Component({
  selector: 'app-recommendations',
  templateUrl: './recommendations.component.html',
  styleUrls: ['./recommendations.component.css'],
  providers: [CommonResponseService, LoggerService, ErrorHandlingService]
})
export class RecommendationsComponent implements OnInit, OnChanges, OnDestroy {
  selectedAssetGroup: string;
  selectedDomain: string;
  subscriptionToAssetGroup: Subscription;
  domainSubscription: Subscription;
  summarySubscription: Subscription;
  tableSubscription: Subscription;
  costSubscription: Subscription;
  currentPageLevel = 0;
  tabName = [
  ];
  selectedTabName;
  errorVal = {
    'summaryStatus': 0,
    'tableStatus': 0,
    'costStatus': 0,
    'savingsStatus': 0
  };
  summaryActiveTab;
  errorMessage = 'apiResponseError';
  tableListData: any = {};
  selectedApplication = 'Total';
  mandatoryfilter;
  queryParamsWithoutFilter: any;
  isFilterRquiredOnPage = true;
  agAndDomain = {};
  appliedFilters = {
    queryParamsWithoutFilter: {} /* Stores the query parameter ibject without filter */,
    pageLevelAppliedFilters: {} /* Stores the query parameter ibject without filter */
  };
  filterArray = []; /* Stores the page applied filter array */
  monthlySavingsTotal;
  FullQueryParams: any;
  totalCost = 0;
  clearSelectedFilterValue = false;
  general = false;
  toggleSelected = 'asset';
  constructor(private assetGroupObservableService: AssetGroupObservableService,
    private router: Router,
    private commonResponseService: CommonResponseService,
    private logger: LoggerService,
    private activatedRoute: ActivatedRoute,
    private errorHandling: ErrorHandlingService,
    private filterManagementService: FilterManagementService,
    private routerUtilityService: RouterUtilityService,
    private utils: UtilsService,
    private refactorFieldsService: RefactorFieldsService,
    private domainObservableService: DomainTypeObservableService) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroupName => {
      this.selectedAssetGroup = assetGroupName;
      if (assetGroupName) {
        this.agAndDomain['ag'] = assetGroupName;
      }
    });
    this.subscribeToDomain();
  }

  ngOnInit() {
  }

  subscribeToDomain() {
    this.domainSubscription = this.domainObservableService.getDomainType().subscribe(domain => {
      if (domain) {
        this.agAndDomain['domain'] = domain;
      }
      this.currentPageLevel = this.routerUtilityService.getpageLevel(this.router.routerState.snapshot.root);
      if (this.currentPageLevel === 0 && this.mandatoryfilter) {
         // reset selected filter value on ag change
         setTimeout(() => {
          this.clearSelectedFilterValue = !this.clearSelectedFilterValue;
          this.updateUrlWithNewFilters([]);
         }, 50);
      } else if (this.currentPageLevel === 1) {
        this.routerParam();
      }
      this.updateComponent();
    });
  }

  routerParam() {
    try {
      const currentQueryParams = this.routerUtilityService.getQueryParametersFromSnapshot(
        this.router.routerState.snapshot.root
      );

      if (currentQueryParams) {
        this.appliedFilters.queryParamsWithoutFilter = JSON.parse(
          JSON.stringify(currentQueryParams)
        );
        delete this.appliedFilters.queryParamsWithoutFilter['filter'];

        this.appliedFilters.pageLevelAppliedFilters = this.utils.processFilterObj(
          currentQueryParams
        );

        this.filterArray = this.filterManagementService.getFilterArray(
          this.appliedFilters.pageLevelAppliedFilters
        );
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  updateUrlWithNewFilters(filterArr) {
    // update mandatoryfilter key if available.
    this.mandatoryfilter = filterArr['mandatory'];
    delete filterArr['mandatory'];
    this.filterArray = filterArr;
    this.appliedFilters.pageLevelAppliedFilters = this.utils.arrayToObject(
      this.filterArray,
      'filterkey',
      'value'
    ); // <-- TO update the queryparam which is passed in the filter of the api
    this.appliedFilters.pageLevelAppliedFilters = this.utils.makeFilterObj(
      this.appliedFilters.pageLevelAppliedFilters
    );

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
    this.router
      .navigate([], {
        relativeTo: this.activatedRoute,
        queryParams: updatedFilters,
        queryParamsHandling: 'merge'
      })
      .then(success => {
        this.appliedFilters.pageLevelAppliedFilters = this.utils.processFilterObj(
          this.appliedFilters.pageLevelAppliedFilters
        );
        if (!this.general && this.selectedApplication !== this.appliedFilters.pageLevelAppliedFilters['tags.Application.keyword']) {
          this.updateComponent();
        }
        if (this.appliedFilters.pageLevelAppliedFilters['tags.Application.keyword']) {
          this.selectedApplication = this.appliedFilters.pageLevelAppliedFilters['tags.Application.keyword'];
        } else {
          this.selectedApplication = 'Total';
        }
      });
  }

  ngOnChanges(changes: SimpleChanges) {
    try {
      const filterChange = changes['filters'];
      if (filterChange) {
        const cur  = JSON.stringify(filterChange.currentValue);
        const prev = JSON.stringify(filterChange.previousValue);
        if (cur !== prev) {
          this.updateComponent();
          }
        }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  updateComponent() {
    this.reset();
    this.getSummaryData();
    this.getApplicationCost();
  }

  reset() {
    this.summaryActiveTab = '';
  }

  contextChange(val) {
    if (this.toggleSelected !== val) {
      this.toggleSelected = val;
      if (val === 'general') {
        this.general = true;
        this.clearSelectedFilterValue = !this.clearSelectedFilterValue;
        this.updateUrlWithNewFilters([]);
        this.updateComponent();
      } else {
        this.general = false;
        this.updateComponent();
      }
    }
  }

  toggleSlider() {
    if (this.toggleSelected === 'general') {
      return 'asset';
    } else {
      return 'general';
    }
  }

  selectTab(tab) {
      this.selectedTabName = tab;
      return tab.category;
  }

  navigateToTab(category) {
    this.changeTab(category);
    this.updateUrlCategory(category);
  }

  changeTab(category) {
    this.tabName.forEach((ele, i) => {
      if (ele.category === category) {
        this.selectedTabName = this.tabName[i];
      }
    });
  }

  updateUrlCategory(category) {
    this.appliedFilters.queryParamsWithoutFilter['category'] = category;
    const currentQueryParams = this.routerUtilityService.getQueryParametersFromSnapshot(
      this.router.routerState.snapshot.root
    );
    if (this.appliedFilters.pageLevelAppliedFilters['tags.Application.keyword']) {
      this.appliedFilters.pageLevelAppliedFilters = this.utils.makeFilterObj(
        this.appliedFilters.pageLevelAppliedFilters
      );
    }
    this.router
    .navigate([], {relativeTo: this.activatedRoute,
      queryParams:
      { 'ag': currentQueryParams['ag'],
        'domain': currentQueryParams['domain'],
        'category': this.appliedFilters.queryParamsWithoutFilter['category'],
        'filter': this.appliedFilters.pageLevelAppliedFilters['filter']
      }
    })
    .then(success => {
    });
  }

  navigateTo(category, appVal) {
    if (appVal) {
      this.updateApplicationFilter(appVal['Application'].text);
    }
    this.navigateToTab(category);
 }

  updateApplicationFilter(appName) {
    if (this.filterArray.length && this.filterArray[0].value === appName) {
      return;
    }
    const filterArr = [{
      compareKey: 'tags.application.keyword',
      filterkey: 'tags.Application.keyword',
      key: 'Application',
      value: appName
    }];
    this.updateUrlWithNewFilters(filterArr);
  }

  getSummaryData() {
    try {
      if (this.summarySubscription) {
        this.summarySubscription.unsubscribe();
      }
      const payload = {};
      const queryParam = {
        'ag': this.selectedAssetGroup,
        'general': this.general,
        'application': this.appliedFilters.pageLevelAppliedFilters['tags.Application.keyword']
      };
      this.errorVal.summaryStatus = 0;
      this.tabName = [];
      const url = environment.recommendationSummary.url;
      const method = environment.recommendationSummary.method;
      this.summarySubscription = this.commonResponseService.getData(url, method, payload, queryParam).subscribe(
        response => {
            try {
              if (this.utils.checkIfAPIReturnedDataIsEmpty(response)) {
                this.errorVal.summaryStatus = -1;
                this.errorMessage = 'noDataAvailable';
              } else {
                this.errorVal.summaryStatus = 1;
                this.processSummary(response);
              }
            } catch (e) {
              this.errorVal.summaryStatus = -1;
              this.errorMessage = 'jsError';
              this.logger.log('error', e);
            }
          },
        error => {
          this.errorVal.summaryStatus = -1;
          this.errorMessage = 'apiResponseError';
          this.logger.log('error', error);
          });
        } catch (error) {
          this.logger.log('error', error);
      }
  }

  processSummary(response) {
    let categoryType;

    this.tabName = [];
    const summaryTabsColor = {
      'cost_optimizing': '#50c17c',
      'security': '#f58544',
      'performance': '#645ec5',
      'service_limits': '#27b5a4',
      'fault_tolerance': '#289cf7'
    };
    const displayOrder = {
      'cost_optimizing': 2,
      'security': 4,
      'performance': 5,
      'service_limits': 6,
      'fault_tolerance': 3
    };
    this.tabName[0] = {
      'category': 'summary',
      'displayName': 'Summary',
      'order': 1
    };
    response.forEach((element) => {
      element['category'] = element['category'].trim();
      element['displayName'] = this.refactorFieldsService.getDisplayNameForAKey(element['category'].toLocaleLowerCase()) || element['category'];
      element['icon'] = '../../../../../assets/icons/recommand_' + element.category + '.svg';
      element['color'] = summaryTabsColor[element.category];
      element['order'] = displayOrder[element.category];
      this.tabName.push(element);
    });

    this.tabName = this.sortData(this.tabName, 'order', 'asc');
    let category;

    if (this.appliedFilters.queryParamsWithoutFilter['category'] || this.selectedTabName) {

      const currentCategory =  this.appliedFilters.queryParamsWithoutFilter['category'];

      category = this.checkSelectedCategoryAvailable(currentCategory) ? this.appliedFilters.queryParamsWithoutFilter['category'] : this.selectTab(this.tabName[0]);

    } else if (!this.selectedTabName) {

      this.activatedRoute.queryParams.subscribe(params => {
        categoryType = params['category'];
      });

      const selectedTabFromList = this.tabName.find(element => element.category === categoryType);

      if (selectedTabFromList) {
        category = this.selectTab(selectedTabFromList);
      }else {
        category = this.selectTab(this.tabName[0]);
      }
    }

    this.navigateToTab(category);
    this.getApplicationTableData(this.tabName[1]);
  }

  getApplicationTableData(selectedTab) {
    try {
      if (this.summaryActiveTab && this.summaryActiveTab.category === selectedTab.category) {
        return;
      } else {
        this.summaryActiveTab = selectedTab;
      }
      if (this.tableSubscription) {
        this.tableSubscription.unsubscribe();
      }
      const payload = {};
      const queryParam = {
        'ag': this.selectedAssetGroup,
        'category': selectedTab.category,
        'general': this.general
      };
      this.errorVal.tableStatus = 0;
      this.tableListData = {};
      const url = environment.recommendationApplication.url;
      const method = environment.recommendationApplication.method;
      this.tableSubscription = this.commonResponseService.getData(url, method, payload, queryParam).subscribe(
        response => {
        try {
          if (this.utils.checkIfAPIReturnedDataIsEmpty(response.applications)) {
            this.errorVal.tableStatus = -1;
            this.errorMessage = 'noDataAvailable';
          } else {
            this.errorVal.tableStatus = 1;
            this.processTableData(response);
          }
        } catch (e) {
          this.errorVal.tableStatus = -1;
          this.errorMessage = 'jsError';
          this.logger.log('error', e);
        }
      },
      error => {
        this.errorVal.tableStatus = -1;
        this.errorMessage = 'apiResponseError';
        this.logger.log('error', error);
      });
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  processTableData(response) {
    const header = Object.keys(response.applications[0]);
    this.tableListData['header'] = [];
    header.forEach( element => {
      this.tableListData['header'].push(this.refactorFieldsService.getDisplayNameForAKey(element.toLocaleLowerCase()) || element);
    });
    this.tableListData.detailsView = false;
    this.tableListData.searchBar = true;
    this.tableListData.getColumnNumWithColor = {0: '#ed0074'};
    this.tableListData.enableSearchandDownload = true;
    this.tableListData.firstRowClick = true;
    const dataArray = [];
    let sortedData;
    if (this.tableListData['header'].includes('potential monthly savings')) {
      sortedData = this.sortData(response.applications, 'monthlySavings', 'desc');
    } else {
     sortedData = this.sortData(response.applications, 'recommendations', 'desc');
    }
    sortedData.forEach(element => {
      const eachRow = {};
        eachRow['Application'] = {
          'text': element['application'],
          'valText': element['application']
        };
        eachRow['recommendations'] = {
          'text': element['recommendations'],
          'valText': element['recommendations']
        };
      if (element['monthlySavings'] || element['monthlySavings'] === 0) {
        eachRow['potential monthly savings'] = {
          'text': '$ ' + element['monthlySavings'].toLocaleString(),
          'valText': element['monthlySavings']
        };
      } else if (this.tableListData['header'].includes('potential monthly savings')) {
        eachRow['potential monthly savings'] = {
          'text': '',
          'valText': ''
        };
      }
      dataArray.push(eachRow);
    });
    this.tableListData.tableData = dataArray;
  }

  sortData(data, key, sortType) {
    return _.orderBy(data, [key], [sortType]);
  }

  getmonthlySavings(event) {
    this.monthlySavingsTotal = event.val;
    this.errorVal.savingsStatus = event.status;
  }

  getApplicationCost() {
    try {
      if (this.costSubscription) {
        this.costSubscription.unsubscribe();
      }
      this.errorVal.costStatus = 0;
      const payload = {};
      const queryParam = {
        'ag': this.selectedAssetGroup
      };
      this.totalCost = 0;
      if (this.appliedFilters.pageLevelAppliedFilters['tags.Application.keyword']) {
        this.selectedApplication = this.appliedFilters.pageLevelAppliedFilters['tags.Application.keyword'];
        queryParam['application'] = this.appliedFilters.pageLevelAppliedFilters['tags.Application.keyword'];
      }
      const url = environment.costApplications.url;
      const method = environment.costApplications.method;
      this.costSubscription = this.commonResponseService.getData(url, method, payload, queryParam).subscribe(
        response => {
        const applicationList = response.costByApplication;
        try {
          this.totalCost = this.processApplicationCost(applicationList);
          this.errorVal.costStatus = 1;
        } catch (e) {
          this.logger.log('error', e);
        }
      },
      error => {
        this.logger.log('error', error);
      });
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  processApplicationCost(data) {
    return data.reduce(function (a, b) {
        return a + b['applicationTotalCost'];
    }, 0);
  }

  checkSelectedCategoryAvailable(category) {
    return _.some(this.tabName, {'category': category });
  }

  showHelpContent(event) {
    const widgetId  = 'w9';
    const newParams = { widgetId: widgetId };
    this.router.navigate(
        ['/pl', { outlets: { helpTextModal: ['help-text'] } }],
        { queryParams: newParams, queryParamsHandling: 'merge' }
    );
}

  ngOnDestroy() {
    try {
      if (this.subscriptionToAssetGroup) {
        this.subscriptionToAssetGroup.unsubscribe();
      }
      if (this.domainSubscription) {
        this.domainSubscription.unsubscribe();
      }
      if (this.tableSubscription) {
        this.tableSubscription.unsubscribe();
      }
      if (this.costSubscription) {
        this.costSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }
}
