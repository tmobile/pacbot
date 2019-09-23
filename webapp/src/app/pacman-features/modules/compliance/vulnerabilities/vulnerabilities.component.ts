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
import { DataCacheService } from '../../../../core/services/data-cache.service';
import { Subscription } from 'rxjs/Subscription';
import { IssueListingService } from '../../../services/issue-listing.service';
import { IssueFilterService } from '../../../services/issue-filter.service';
import * as _ from 'lodash';
import { UtilsService } from '../../../../shared/services/utils.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/pairwise';
import { DownloadService } from '../../../../shared/services/download.service';
import { RefactorFieldsService } from './../../../../shared/services/refactor-fields.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { RouterUtilityService } from '../../../../shared/services/router-utility.service';

@Component({
  selector: 'app-vulnerabilities',
  templateUrl: './vulnerabilities.component.html',
  styleUrls: ['./vulnerabilities.component.css'],
  providers: [
    IssueListingService,
    IssueFilterService,
    LoggerService,
    ErrorHandlingService
  ]
})
export class VulnerabilitiesComponent implements OnInit, OnDestroy {
  pageTitle = 'Vulnerabilities';
  issueListingdata: any;
  selectedAssetGroup: string;
  breadcrumbArray: any = ['Compliance'];
  breadcrumbLinks: any = ['compliance-dashboard'];
  breadcrumbPresent: any;
  outerArr: any = [];
  dataLoaded = false;
  errorMessage: any;
  showingArr: any = ['severity', 'owner', 'executionId'];
  allColumns: any = [];
  totalRows = 0;
  currentBucket: any = [];
  bucketNumber = 0;
  firstPaginator = 1;
  lastPaginator: number;
  currentPointer = 0;
  seekdata = false;
  showLoader = true;
  paginatorSize = 25;
  searchTxt = '';
  popRows: any = ['Vulnerability list', 'Vulnerability list with asset details'];
  dataTableData: any = [];
  tableDataLoaded = false;
  filters: any = [];
  searchCriteria: any;
  filterText: any = {};
  errorValue = 0;
  showGenericMessage = false;
  urlID = '';
  public labels: any;
  FullQueryParams: any;
  queryParamsWithoutFilter: any;
  urlToRedirect: any = '';
  backButtonRequired;
  pageLevel = 0;

  private assetGroupSubscription: Subscription;
  private routeSubscription: Subscription;
  private complianceDropdownSubscription: Subscription;
  private issueListingSubscription: Subscription;
  private issueFilterSubscription: Subscription;
  private downloadSubscription: Subscription;

  constructor(
    private assetGroupObservableService: AssetGroupObservableService,
    private activatedRoute: ActivatedRoute,
    private dataStore: DataCacheService,
    private issueListingService: IssueListingService,
    private issueFilterService: IssueFilterService,
    private router: Router,
    private workflowService: WorkflowService,
    private utils: UtilsService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private downloadService: DownloadService,
    private refactorFieldsService: RefactorFieldsService,
    private routerUtilityService: RouterUtilityService
  ) {
    this.assetGroupSubscription = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        this.selectedAssetGroup = assetGroupName;
        this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
          this.pageLevel
        );
        this.routerParam();
        this.deleteFilters();
        this.getFilterArray();
        this.updateComponent();
      });
  }


  ngOnInit() {
    this.breadcrumbPresent = 'All Vulnerabilities';
  }

  /*
    * This function gets the urlparameter and queryObj
    *based on that urlparameter different apis are being hit with different queryparams
    */
  routerParam() {
    try {
      // this.filterText saves the queryparam
      const currentQueryParams = this.routerUtilityService.getQueryParametersFromSnapshot(this.router.routerState.snapshot.root);
      if (currentQueryParams) {
        /**
         * FullQueryParams hold the entire queryobj(filter obj + the other obj)
         * queryParamsWithoutFilter holds only the part without the filter,
         * queryParamsWithoutFilter is used so that while deleting the filter we can append the remaining part
         * which is not part of filterobj(check in deleteFilters function)
         */
        this.FullQueryParams = currentQueryParams;
        this.queryParamsWithoutFilter = JSON.parse(JSON.stringify(this.FullQueryParams));
        delete this.queryParamsWithoutFilter['filter'];
        /**
         * The below code is added to get URLparameter and queryparameter
         * when the page loads ,only then this function runs and hits the api with the
         * filterText obj processed through processFilterObj function
         */
        this.filterText = this.utils.processFilterObj(currentQueryParams);
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  updatePaginator(event) {
    if (event !== this.paginatorSize) {
      this.paginatorSize = event;
      this.updateComponent();
    }
  }

  deleteFilters(event?) {
    try {
      if (!event) {
        this.filters = [];
      } else {
        if (event.clearAll) {
          this.filters = [];
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
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /*
     * this functin passes query params to filter component to show filter
   */
  getFilterArray() {
    try {
      const localFilters = []; // <<-- this filter is used to store data for filter
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
      // add By trinanjan
      const formattedFilters = dataArray.map(function(data) {
        data.name =
          refactoredService.getDisplayNameForAKey(data.name.toLowerCase()) || data.name;
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
    this.dataLoaded = false;
    this.dataTableData = [];
    this.tableDataLoaded = false;
    this.seekdata = false;
    this.errorValue = 0;
    this.showGenericMessage = false;
    this.getData();
  }

  getData() {
    try {
      if (this.issueListingSubscription) {
        this.issueListingSubscription.unsubscribe();
      }
      let queryParams;
      queryParams = {
        ag: this.selectedAssetGroup,
        filter: this.filterText,
        from: this.bucketNumber * this.paginatorSize,
        searchtext: this.searchTxt,
        size: this.paginatorSize
      };
      this.errorValue = 0;
      const allVulnerabilityUrl = environment.allVulnerability.url;
      const allVulnerabilityMethod = environment.allVulnerability.method;
      this.issueListingSubscription = this.issueListingService
        .getData(queryParams, allVulnerabilityUrl, allVulnerabilityMethod)
        .subscribe(
          response => {
            this.showGenericMessage = false;
            try {
              this.errorValue = 1;
              this.searchCriteria = undefined;
              this.tableDataLoaded = true;
              this.dataTableData = response[0].response;
              const data = response[0];
              this.showLoader = false;
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
                this.firstPaginator =
                  this.bucketNumber * this.paginatorSize + 1;
                this.lastPaginator =
                  this.bucketNumber * this.paginatorSize + this.paginatorSize;
                this.currentPointer = this.bucketNumber;
                if (this.lastPaginator > this.totalRows) {
                  this.lastPaginator = this.totalRows;
                }
                const updatedResponse = this.utils.massageTableData(this.issueListingdata);
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
    } catch (error) {
      this.showLoader = false;
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  processData(data) {
    try {
      let innerArr = {};
      const totalVariablesObj = {};
      let cellObj = {};

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
          if (getCols[col].toLowerCase() === 'title' ||
              getCols[col].toLowerCase() === 'qid') {
            cellObj = {
              link: 'View Vulnerability Details',
              properties: {
                color: '',
                'text-shadow': '0.1px 0'
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
          } else if (
            getCols[col].toLowerCase() === 'assets affected' || getCols[col].toLowerCase() === 'assetsaffected') {
            cellObj = {
              link: 'View Asset List',
              properties: {
                color: '',
                'text-decoration': 'underline #383C4D'
              },
              colName: getCols[col],
              hasPreImg: false,
              imgLink: '',
              text: getData[row][getCols[col]],
              valText: getData[row][getCols[col]]
            };
          } else if (
            getCols[col].toLowerCase() === 'createdon' ||
            getCols[col].toLowerCase() === 'created on' ||
            getCols[col].toLowerCase() === 'modifiedon' ||
            getCols[col].toLowerCase() === 'modified on'
          ) {
            cellObj = {
              link: '',
              properties: {
                color: ''
              },
              colName: getCols[col],
              hasPreImg: false,
              imgLink: '',
              text: this.utils.calculateDate(getData[row][getCols[col]]),
              valText: new Date(getData[row][getCols[col]]).getTime()
            };
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
    console.log(row);
    try {
      const apiTarget = { TypeAsset: 'vulnerable' };
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      if (row.col.toLowerCase() === 'assets affected' || row.col.toLowerCase() === 'assetsaffected') {
        const applicationFilterValue = this.filterText['tags.Application.keyword'];
        const environmentFilterValue = this.filterText['tags.Environment.keyword'];
        const eachParams = { qid: row.row.qid.valText, application: applicationFilterValue, environment:  environmentFilterValue};
        let newParams = this.utils.makeFilterObj(eachParams);
        newParams = Object.assign(newParams, apiTarget);
        newParams['mandatory'] = 'qid';
        this.router.navigate(['../../../assets', 'asset-list'],
        {
          relativeTo: this.activatedRoute,
          queryParams: newParams,
          queryParamsHandling: 'merge'
        });
      } else if (row.col.toLowerCase() === 'qid' || row.col.toLowerCase() === 'title') {
        this.router.navigate(['../../vulnerabilities/vulnerability-details', row.row.qid.valText ], {
          relativeTo: this.activatedRoute,
          queryParams: this.queryParamsWithoutFilter,
          queryParamsHandling: 'merge'
        });
      }
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

  callNewSearch() {
    this.bucketNumber = 0;
    this.currentBucket = [];
    this.getData();
  }

  vulnerabilitiesCSV(serviceName) {
    const fileType = 'csv';
    let downloadCsvName;
    let downloadSize = 0;

    try {
        let queryParams;

        queryParams = {
          fileFormat: 'csv',
          fileType: fileType
        };
        if (serviceName === 'Vulnerability list') {
          queryParams.serviceId = 6;
          downloadCsvName = 'All Vulnerabilities';
          downloadSize = this.totalRows;
        } else if (serviceName === 'Vulnerability list with asset details') {
          queryParams.serviceId = 19;
          downloadCsvName = 'All Vulnerabilities with Details';
        }

        const downloadRequest = {
          ag: this.selectedAssetGroup,
          filter: this.filterText,
          from: 0,
          searchtext: this.searchTxt,
          size: downloadSize
        };

        const downloadUrl = environment.download.url;
        const downloadMethod = environment.download.method;
        this.downloadService
          .requestForDownload(
            queryParams,
            downloadUrl,
            downloadMethod,
            downloadRequest,
            downloadCsvName,
            this.totalRows
          );
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  updateUrlWithNewFilters(filterArr) {
    this.filters = filterArr;
    this.getUpdatedUrl();
    this.updateComponent();
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

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }
  }


  ngOnDestroy() {
    try {
      // pushes the current url to datastore
      this.dataStore.set('urlToRedirect', this.urlToRedirect);
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
      if (this.issueFilterSubscription) {
        this.issueFilterSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }
}
