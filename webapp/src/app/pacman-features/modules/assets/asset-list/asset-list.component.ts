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
import { environment } from './../../../../../environments/environment';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { IssueListingService } from '../../../services/issue-listing.service';
import { IssueFilterService } from '../../../services/issue-filter.service';
import * as _ from 'lodash';
import { UtilsService } from '../../../../shared/services/utils.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import 'rxjs/add/operator/filter';
import { ToastObservableService } from '../../../../post-login-app/common/services/toast-observable.service';
import { DownloadService } from '../../../../shared/services/download.service';
import { RefactorFieldsService } from './../../../../shared/services/refactor-fields.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { DomainTypeObservableService } from '../../../../core/services/domain-type-observable.service';
import { RouterUtilityService } from '../../../../shared/services/router-utility.service';


@Component({
  selector: 'app-asset-list',
  templateUrl: './asset-list.component.html',
  styleUrls: ['./asset-list.component.css'],
  providers: [
    IssueListingService,
    IssueFilterService,
    LoggerService,
    ErrorHandlingService
  ]
})
export class AssetListComponent implements OnInit, OnDestroy {

  pageTitle = 'Asset List';
  issueListingdata: any;
  selectedAssetGroup: string;
  breadcrumbArray: any = ['Assets'];
  breadcrumbLinks: any = ['asset-dashboard'];
  breadcrumbPresent: any;
  outerArr: any = [];
  dataLoaded = false;
  errorMessage: any;
  showingArr: any = ['severity', 'owner', 'executionId'];
  allColumns: any = [];
  totalRows = 0;
  currentBucket: any = [];
  popRows = ['Download Data'];
  bucketNumber = 0;
  firstPaginator = 1;
  lastPaginator: number;
  currentPointer = 0;
  seekdata = false;
  showLoader = true;
  paginatorSize = 25;
  searchTxt = '';
  filterTypeOptions: any = [];
  filterTagOptions: any = [];
  currentFilterType;
  filterTypeLabels = [];
  filterTagLabels = [];
  dataTableData: any = [];
  tableDataLoaded = false;
  filters: any = [];
  searchCriteria: any;
  filterText: any = {};
  errorValue = 0;
  showGenericMessage = false;
  dataTableDesc = '';
  urlID = '';
  public labels: any;
  FullQueryParams: any;
  queryParamsWithoutFilter: any;
  private previousUrl: any = '';
  selectedDomain: any = '';
  urlToRedirect: any = '';
  serviceId = 7;
  tableDownloadName = 'All Assets';
  private pageLevel = 0;
  public backButtonRequired;
  mandatory: any;

  private assetGroupSubscription: Subscription;
  private routeSubscription: Subscription;
  private complianceDropdownSubscription: Subscription;
  private issueListingSubscription: Subscription;
  private issueFilterSubscription: Subscription;
  private previousUrlSubscription: Subscription;
  private downloadSubscription: Subscription;
  private subscriptionDomain: Subscription;

  constructor(
    private assetGroupObservableService: AssetGroupObservableService,
    private activatedRoute: ActivatedRoute,
    private issueListingService: IssueListingService,
    private issueFilterService: IssueFilterService,
    private router: Router,
    private utils: UtilsService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private downloadService: DownloadService,
    private toastObservableService: ToastObservableService,
    private refactorFieldsService: RefactorFieldsService,
    private workflowService: WorkflowService,
    private domainObservableService: DomainTypeObservableService,
    private routerUtilityService: RouterUtilityService
  ) {
    /**************************************************** */
    this.assetGroupSubscription = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
          this.pageLevel
        );
        this.selectedAssetGroup = assetGroupName;
    });

    this.subscriptionDomain = this.domainObservableService.getDomainType().subscribe(domain => {
        this.selectedDomain = domain;
        this.routerParam();
        this.getFilters();
        this.deleteFilters();
        this.getFilterArray();
        this.updateComponent();
    });
  }

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.breadcrumbPresent = 'Asset List';
  }

  /*
    * This function gets the urlparameter and queryObj
    *based on that different apis are being hit with different queryparams
    */
  routerParam() {
    try {
      // this.filterText saves the queryparam
      const currentQueryParams = this.routerUtilityService.getQueryParametersFromSnapshot(this.router.routerState.snapshot.root);
      if (currentQueryParams) {
        this.FullQueryParams = currentQueryParams;
        this.queryParamsWithoutFilter = JSON.parse(JSON.stringify(this.FullQueryParams));
        delete this.queryParamsWithoutFilter['filter'];
        /**
         * The below code is added to get URLparameter and queryparameter
         * when the page loads ,only then this function runs and hits the api with the
         * filterText obj processed through processFilterObj function
         */
        this.filterText = this.utils.processFilterObj(
          this.FullQueryParams
        );
        this.urlID = this.FullQueryParams.TypeAsset;
        // check for mandatory filters.
        if (this.FullQueryParams.mandatory) {
          this.mandatory = this.FullQueryParams.mandatory;
        }
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }
  deleteFilters(event?) {
    try {
      if (!event) {
        this.filters = [];
      } else {
        if (event.clearAll) {
          this.filters = [];
          // Adding again Mandatory filters if found any.
          event.array.forEach(obj => {
            if (obj.hasOwnProperty('mandatoryFilter')) {
              this.filters.push(obj);
            }
          });
        } else {
          this.filters.splice(event.index, 1);
        }

        this.filterText = this.utils.arrayToObject(
          this.filters,
          'filterkey',
          'value'
        ); // <-- TO update the queryparam which is passed in the filter of the api
        this.filterText = this.utils.makeFilterObj(this.filterText);

        /**
         * To change the url
         * with the deleted filter value along with the other existing paramter(ex-->tv:true)
         */

        const updatedFilters = Object.assign(
          this.filterText,
          this.queryParamsWithoutFilter
        );
        this.router.navigate([], {
          relativeTo: this.activatedRoute,
          queryParams: updatedFilters
        });
        /**
         * Finally after changing URL Link
         * api is again called with the updated filter
         */
        this.filterText = this.utils.processFilterObj(this.filterText);
        this.updateComponent();
      }
    } catch (error) {}
    /* TODO: Aditya: Why are we not calling any updateCompliance function in observable to update the filters */
  }
  /*
     * this function passes query params to filter component to show filter
   */
  getFilterArray() {
    try {
      const localFilters = []; // <<-- this filter is used to store data for filter
      // let labelsKey = Object.keys(this.labels);
      const filterObjKeys = Object.keys(this.filterText);
      const dataArray = [];
      for (let i = 0; i < filterObjKeys.length; i++) {
        let obj = {};
        obj = {
          name: filterObjKeys[i]
        };
        dataArray.push(obj);
      }
      const filterValues = dataArray;
      const refactoredService = this.refactorFieldsService;
      const formattedFilters = dataArray.map(function(data) {
        data.name =
          refactoredService.getDisplayNameForAKey(data.name) || data.name;
        return data;
      });

      for (let i = 0; i < formattedFilters.length; i++) {
        const eachObj = {
          key: formattedFilters[i].name, // <-- displayKey-- Resource Type
          value: this.filterText[filterObjKeys[i]], // <<-- value to be shown in the filter UI-- S2
          filterkey: filterObjKeys[i].trim(), // <<-- filter key that to be passed -- "resourceType "
          compareKey : filterObjKeys[i].toLowerCase().trim()// <<-- key to compare whether a key is already present -- "resourcetype"
        };
        localFilters.push(eachObj);
      }
      this.filters = localFilters;
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  /**
   * This function get calls the keyword service before initializing
   * the filter array ,so that filter keynames are changed
   */

  updateComponent() {
    this.outerArr = [];
    this.searchTxt = '';
    this.currentBucket = [];
    this.bucketNumber = 0;
    this.firstPaginator = 1;
    this.showLoader = true;
    this.currentPointer = 0;
    this.dataTableData = [];
    this.tableDataLoaded = false;
    this.dataLoaded = false;
    this.seekdata = false;
    this.errorValue = 0;
    this.showGenericMessage = false;
    this.getData();
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  getData() {
    try {
      let queryParams;
      let assetListUrl;
      let assetListMethod;
      this.errorValue = 0;

      if (this.urlID) {
        if (this.urlID.toLowerCase() === 'taggable') {
          this.dataTableDesc = 'Note: This page shows all the taggable assets';
          // the url and method for tagging << -- defines url and method
          assetListUrl = environment.assetListTaggable.url;
          assetListMethod = environment.assetListTaggable.method;
          this.serviceId = 10;
          this.tableDownloadName = 'Taggable Assets';
        } else if (this.urlID.toLowerCase() === 'patchable') {
          this.dataTableDesc = 'Note: This page shows all the patchable assets';
          // patchable  asset list api
          // the url and method for patching << -- defines url and method
          assetListUrl = environment.assetListPatchable.url;
          assetListMethod = environment.assetListPatchable.method;
          this.serviceId = 8;
          this.tableDownloadName = 'Patchable Assets';
          this.filterText['resourceType'] = 'ec2';
        } else if (this.urlID.toLowerCase() === 'scanned') {
          this.dataTableDesc = 'Note: This page shows all the scanned assets';
          // patchable  asset list api
          // the url and method for patching << -- defines url and method
          assetListUrl = environment.assetListScanned.url;
          assetListMethod = environment.assetListScanned.method;
          this.serviceId = 9;
          this.tableDownloadName = 'Scanned Assets';
        } else if (this.urlID.toLowerCase() === 'vulnerable') {
          this.dataTableDesc = 'Note: This page shows all the vulnerable assets';
          // vulnerable  asset list api
          // the url and method for patching << -- defines url and method
          assetListUrl = environment.assetListVulnerable.url;
          assetListMethod = environment.assetListVulnerable.method;
          this.serviceId = 11;
          this.tableDownloadName = 'Vulnerable Assets';
        } else if (this.urlID.toLowerCase() === 'pull-request-trend') {
          if (this.filterText.prstate) {
          this.dataTableDesc =
            'Note: This page shows the ' + this.filterText.prstate.toString().toLowerCase() + ' pull request trend';
          } else {
            this.dataTableDesc =
            'Note: This page shows the pull request trend';
          }
          // vulnerable  asset list api
          // the url and method for patching << -- defines url and method
          assetListUrl = environment.PullReqLineTrend.url;
          assetListMethod = environment.PullReqLineTrend.method;
          this.serviceId = 12;
          this.tableDownloadName = 'Pull Request Trend';
        } else if (this.urlID.toLowerCase() === 'pull-request-age') {
          if (this.filterText.daysRange) {
          this.dataTableDesc =
            'Note: This page shows the pull request age (' + this.filterText.daysRange.toString().toLowerCase() + ' days)';
          } else {
            this.dataTableDesc =
            'Note: This page shows the pull request age';
          }
          assetListUrl = environment.PullReqAge.url;
          assetListMethod = environment.PullReqAge.method;
          this.serviceId = 13;
          this.tableDownloadName = 'Pull Request Age';
        } else if (this.urlID.toLowerCase() === 'branching-strategy') {
          if (this.filterText.strategyType) {
            this.dataTableDesc =
              'Note: This page shows the' + ' \'' + this.filterText.strategyType.toString().toLowerCase() + '\' ' + this.filterText.branchingStrategyType.toString().toLowerCase() + ' ' + 'distribution by branching strategies';
            } else {
              this.dataTableDesc =
              'Note: This page shows the' + ' ' + this.filterText.branchingStrategyType.toString().toLowerCase() + ' ' + 'distribution by branching strategies';
            }
            assetListUrl = environment.devDistribution.url;
            assetListMethod = environment.devDistribution.method;
            this.serviceId = 14;
            this.tableDownloadName = this.filterText.branchingStrategyType.toString() + ' ' + 'Distribution';
        } else {
          assetListUrl = environment.assetList.url;
          assetListMethod = environment.assetList.method;
          this.serviceId = 7;
          this.tableDownloadName = 'All Assets';
          this.filterText['domain'] = this.selectedDomain;
        }
      } else {
        assetListUrl = environment.assetList.url;
        assetListMethod = environment.assetList.method;
        this.filterText['domain'] = this.selectedDomain;
      }

      queryParams = {
        ag: this.selectedAssetGroup,
        filter: this.filterText,
        from: this.bucketNumber * this.paginatorSize,
        searchtext: this.searchTxt,
        size: this.paginatorSize
      };

      this.getDataForAParticularTypeOfAssets(
        queryParams,
        assetListUrl,
        assetListMethod
      );
    } catch (error) {
      this.showLoader = false;
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  getDataForAParticularTypeOfAssets(
    queryParams,
    assetListUrl,
    assetListMethod
  ) {
    if (this.issueListingSubscription) {
      this.issueListingSubscription.unsubscribe();
    }
    this.issueListingSubscription = this.issueListingService
      .getData(queryParams, assetListUrl, assetListMethod)
      .subscribe(
        response => {
          this.showGenericMessage = false;
          try {
            this.errorValue = 1;
            this.searchCriteria = undefined;
            const data = response[0];
            this.showLoader = false;
            this.tableDataLoaded = true;
            this.dataTableData = response[0].response;
            this.dataLoaded = true;
            if (response[0].response.length === 0) {
              this.errorValue = -1;
              this.outerArr = [];
              this.allColumns = [];
              this.totalRows = 0;
            }
            if (data.response.length > 0) {
              this.issueListingdata = data.response;
              this.seekdata = false;
              this.totalRows = data.total;
              this.firstPaginator = this.bucketNumber * this.paginatorSize + 1;
              this.lastPaginator =
                this.bucketNumber * this.paginatorSize + this.paginatorSize;
              this.currentPointer = this.bucketNumber;
              if (this.lastPaginator > this.totalRows) {
                this.lastPaginator = this.totalRows;
              }
              const updatedResponse = this.massageData(this.issueListingdata);
              this.currentBucket[this.bucketNumber] = updatedResponse;
              this.processData(updatedResponse);
            }
          } catch (e) {
            this.errorValue = 0;
            this.errorValue = -1;
            this.outerArr = [];
            this.dataLoaded = true;
            this.seekdata = true;
            this.errorMessage = this.errorHandling.handleJavascriptError(e);
          }
        },
        error => {
          this.showGenericMessage = true;
          this.errorValue = -1;
          this.outerArr = [];
          this.dataLoaded = true;
          this.seekdata = true;
          this.errorMessage = 'apiResponseError';
        }
      );
  }
  massageData(data) {
    /*
       * added by Trinanjan 14/02/2017
       * the funciton replaces keys of the table header data to a readable format
     */
    const refactoredService = this.refactorFieldsService;
    const newData = [];
    data.map(function(responseData){
      const KeysTobeChanged = Object.keys(responseData);
      let newObj = {};
      let entityType;
      KeysTobeChanged.forEach(element => {
        if ( element === '_entitytype') {
          entityType = responseData['_entitytype'];
        }
        const elementnew =
          refactoredService.getDisplayNameForAKey(
            element.toLocaleLowerCase()
          ) || element;
        newObj = Object.assign(newObj, { [elementnew]: responseData[element] });
      });
      if (entityType) {
        newObj['Asset Type'] = entityType;
      }
      newData.push(newObj);
    });
    return newData;
  }
  processData(data) {
    try {
      let innerArr = {};
      const totalVariablesObj = {};
      let cellObj = {};
      const magenta = '#e20074';
      const green = '#26ba9d';
      const red = '#f2425f';
      const orange = '#ffb00d';
      const yellow = 'yellow';
      this.outerArr = [];
      const getData = data;
      let getCols;
      if (getData.length) {
        getCols = Object.keys(getData[0]);

      } else {
        this.seekdata = true;
      }

      for (let row = 0; row < getData.length; row++) {
        innerArr = {};
        for (let col = 0; col < getCols.length; col++) {
          if (
            getCols[col].toLowerCase() === 'resourceid' ||
            getCols[col].toLowerCase() === 'resource id'
          ) {
            cellObj = {
              link: 'true',
              properties: {
                'text-shadow': '0.1px 0',
                'text-transform': 'lowercase'
              },
              colName: getCols[col],
              hasPreImg: false,
              imgLink: '',
              text: getData[row][getCols[col]],
              valText: getData[row][getCols[col]]
            };
          } else if (getCols[col].toLowerCase() === 'severity') {
            if (getData[row][getCols[col]] === 'low') {
              cellObj = {
                link: '',
                properties: {
                  color: '',
                  'text-transform': 'capitalize'
                },
                colName: getCols[col],
                hasPreImg: true,
                imgLink: '',
                text: getData[row][getCols[col]],
                valText: 1,
                statusProp: {
                  'background-color': '#ffe00d'
                }
              };
            } else if (getData[row][getCols[col]] === 'medium') {
              cellObj = {
                link: '',
                properties: {
                  color: '',
                  'text-transform': 'capitalize'
                },
                colName: getCols[col],
                hasPreImg: true,
                imgLink: '',
                valText: 2,
                text: getData[row][getCols[col]],
                statusProp: {
                  'background-color': '#ffb00d'
                }
              };
            } else if (getData[row][getCols[col]] === 'high') {
              cellObj = {
                link: '',
                properties: {
                  color: '',
                  'text-transform': 'capitalize'
                },
                colName: getCols[col],
                hasPreImg: true,
                valText: 3,
                imgLink: '',
                text: getData[row][getCols[col]],
                statusProp: {
                  'background-color': '#ed0295'
                }
              };
            } else {
              cellObj = {
                link: '',
                properties: {
                  color: '',
                  'text-transform': 'capitalize'
                },
                colName: getCols[col],
                hasPreImg: true,
                imgLink: '',
                valText: 4,
                text: getData[row][getCols[col]],
                statusProp: {
                  'background-color': '#e60127'
                }
              };
            }
          } else {
            cellObj = {
              link: '',
              properties: {
                color: ''
              },
              colName: getCols[col],
              hasPreImg: false,
              imgLink: '',
              text: getData[row][getCols[col]],
              valText: getData[row][getCols[col]]
            };
          }
          innerArr[getCols[col]] = cellObj;
          totalVariablesObj[getCols[col]] = '';
        }
        this.outerArr.push(innerArr);
      }
      if (this.outerArr.length > getData.length) {
        const halfLength = this.outerArr.length / 2;
        this.outerArr = this.outerArr.splice(halfLength);
      }
      this.allColumns = Object.keys(totalVariablesObj);

    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  goToDetails(row) {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      let resourceType;
      if (row.row['Asset Type']) {
        resourceType = row.row['Asset Type'].text;
      }

      if ( this.urlID && (this.urlID.toLowerCase() === 'pull-request-trend' || this.urlID.toLowerCase() === 'pull-request-age' || this.urlID.toLowerCase() === 'branching-strategy') ) {
        resourceType = this.filterText.resourceType;
      }
      const resourceID = encodeURIComponent(row.row['Resource ID'].text);
      this.router.navigate(['../assets-details', resourceType, resourceID], {
        relativeTo: this.activatedRoute,
        queryParamsHandling: 'merge'
      });
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  searchCalled(search) {
    this.searchTxt = search;
  }

  prevPg() {
    try {
      this.currentPointer--;
      this.processData(this.currentBucket[this.currentPointer]);
      this.firstPaginator = this.currentPointer * this.paginatorSize + 1;
      this.lastPaginator =
        this.currentPointer * this.paginatorSize + this.paginatorSize;
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  nextPg() {
    try {
      if (this.currentPointer < this.bucketNumber) {
        this.currentPointer++;
        this.processData(this.currentBucket[this.currentPointer]);
        this.firstPaginator = this.currentPointer * this.paginatorSize + 1;
        this.lastPaginator =
          this.currentPointer * this.paginatorSize + this.paginatorSize;
        if (this.lastPaginator > this.totalRows) {
          this.lastPaginator = this.totalRows;
        }
      } else {
        this.bucketNumber++;
        this.getData();
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  handlePopClick(rowText) {
    const fileType = 'csv';

    try {
        let queryParams;

        queryParams = {
          fileFormat: 'csv',
          serviceId: this.serviceId,
          fileType: fileType,
        };

        // temp code to send download domain filters only for dev page assets landing

        if (this.urlID && (this.urlID.toLowerCase() === 'taggable' ||
                this.urlID.toLowerCase() === 'patchable' ||
                this.urlID.toLowerCase() === 'scanned' ||
                this.urlID.toLowerCase() === 'vulnerable')) {
              // this.filterText['domain'] = this.selectedDomain;
        } else {
            this.filterText['domain'] = this.selectedDomain;
        }

        const downloadRequest = {
          ag: this.selectedAssetGroup,
          filter: this.filterText,
          from: 0,
          searchtext: this.searchTxt,
          size: this.totalRows
        };

        const downloadUrl = environment.download.url;
        const downloadMethod = environment.download.method;

      this.downloadService.requestForDownload(
        queryParams,
        downloadUrl,
        downloadMethod,
        downloadRequest,
        this.tableDownloadName, this.totalRows);

    } catch (error) {
      this.logger.log('error', error);
    }
  }

  callNewSearch() {
    this.bucketNumber = 0;
    this.currentBucket = [];
    this.getData();
  }

  /**
   * This function get calls the keyword service before initializing
   * the filter array ,so that filter keynames are changed
   */

  getFilters() {
    try {
      let filterId = 8;
      if ( this.urlID && (this.urlID.toLowerCase() === 'pull-request-trend' || this.urlID.toLowerCase() === 'pull-request-age' || this.urlID.toLowerCase() === 'branching-strategy')) {
        filterId = 9;
      }
      this.issueFilterSubscription = this.issueFilterService
        .getFilters(
          { filterId: filterId , domain: this.selectedDomain},
          environment.issueFilter.url,
          environment.issueFilter.method
        )
        .subscribe(response => {
          this.filterTypeLabels = _.map(response[0].response, 'optionName');
          this.filterTypeOptions = response[0].response;
        });
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  changeFilterType(value) {
    try {
      this.currentFilterType = _.find(this.filterTypeOptions, {
        optionName: value.value
      });
      this.issueFilterSubscription = this.issueFilterService
        .getFilters(
          {
            ag: this.selectedAssetGroup,
            domain: this.selectedDomain
          },
          environment.base +
            this.utils.getParamsFromUrlSnippet(this.currentFilterType.optionURL)
              .url,
          'GET'
        )
        .subscribe(response => {
          this.filterTagOptions = response[0].response;
          this.filterTagLabels = _.map(response[0].response, 'name');
        });

    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  changeFilterTags(value) {
    try {
      if (this.currentFilterType) {
        const filterTag = _.find(this.filterTagOptions, { name: value.value });
        this.utils.addOrReplaceElement(
          this.filters,
          {
            key: this.currentFilterType.optionName,
            value: filterTag['id'].trim(),
            filterkey: this.currentFilterType.optionValue.trim(),
            compareKey : this.currentFilterType.optionValue.toLowerCase().trim()
          },
          el => {
            return el.compareKey === this.currentFilterType.optionValue.toLowerCase().trim();
          }
        );
      }
      this.getUpdatedUrl();
      this.utils.clickClearDropdown();
      this.updateComponent();
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  getUpdatedUrl() {
    this.filterText = this.utils.arrayToObject(
      this.filters,
      'filterkey',
      'value'
    ); // <-- TO update the queryparam which is passed in the filter of the api
    this.filterText = this.utils.makeFilterObj(this.filterText);
    /**
     * To change the url
     * with the deleted filter value along with the other existing paramter(ex-->tv:true)
     */

    const updatedFilters = Object.assign(
      this.filterText,
      this.queryParamsWithoutFilter
    );
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: updatedFilters
    });

    /**
     * Finally after changing URL Link
     * api is again called with the updated filter
     */
    this.filterText = this.utils.processFilterObj(this.filterText);
  }
  navigateToCreate() {
    this.router.navigateByUrl('../assets/asset-list/create-account');
  }
  ngOnDestroy() {
    try {
      if (this.assetGroupSubscription) {
        this.assetGroupSubscription.unsubscribe();
      }
      if (this.routeSubscription) {
        this.routeSubscription.unsubscribe();
      }
      if (this.complianceDropdownSubscription) {
        this.complianceDropdownSubscription.unsubscribe();
      }
      if (this.issueListingSubscription) {
        this.issueListingSubscription.unsubscribe();
      }
      if (this.previousUrlSubscription) {
        this.previousUrlSubscription.unsubscribe();
      }
      if (this.subscriptionDomain) {
        this.subscriptionDomain.unsubscribe();
      }
      if (this.issueFilterSubscription) {
        this.issueFilterSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }
}
