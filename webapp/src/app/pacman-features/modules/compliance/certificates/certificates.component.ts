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
import 'rxjs/add/operator/pairwise';
import {ToastObservableService} from '../../../../post-login-app/common/services/toast-observable.service';
import { DownloadService } from '../../../../shared/services/download.service';
import {RefactorFieldsService} from './../../../../shared/services/refactor-fields.service';
import {WorkflowService} from '../../../../core/services/workflow.service';
import {RouterUtilityService} from '../../../../shared/services/router-utility.service';

@Component({
  selector: 'app-certificates',
  templateUrl: './certificates.component.html',
  styleUrls: ['./certificates.component.css'],
  providers: [
    IssueListingService,
    IssueFilterService,
    LoggerService,
    ErrorHandlingService
  ]
})
export class CertificatesComponent implements OnInit, OnDestroy {
  pageTitle = 'All Certificates';
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
  popRows: any = ['Download Data'];
  filterTypeOptions: any = [];
  filterTagOptions: any = [];
  currentFilterType;
  filterTypeLabels = [];
  dataTableData: any = [];
  tableDataLoaded = false;
  filterTagLabels = [];
  filters: any = [];
  searchCriteria: any;
  filterText: any = {};
  errorValue = 0;
  showGenericMessage = false;
  urlID = '';
  public labels: any;
  FullQueryParams: any;
  queryParamsWithoutFilter: any;
  private previousUrl: any = '';
  private assetGroupSubscription: Subscription;
  private routeSubscription: Subscription;
  private complianceDropdownSubscription: Subscription;
  private issueListingSubscription: Subscription;
  private issueFilterSubscription: Subscription;
  private downloadSubscription: Subscription;
  public pageLevel = 0;
  public backButtonRequired;

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
    private routerUtilityService: RouterUtilityService
  ) {
    this.assetGroupSubscription = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(this.pageLevel);
        this.selectedAssetGroup = assetGroupName;
        this.getFilters();
        this.routerParam();
        this.updateComponent();
        this.deleteFilters();
        this.getFilterArray();
      });
  }

  ngOnInit() {
    this.breadcrumbPresent = 'All Certificates';
    // gets the current page url,which is used to come back to the same page after navigate
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
        this.filterText = this.utils.processFilterObj(this.FullQueryParams);
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


        const updatedFilters = Object.assign(this.filterText , this.queryParamsWithoutFilter);
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
     * this functin passes query params to filter component to show filter
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
          'name': filterObjKeys[i]
        };
        dataArray.push(obj);
      }

      const filterValues = dataArray;
      const refactoredService = this.refactorFieldsService;
      const formattedFilters = dataArray.map(function(data){
          data.name = refactoredService.getDisplayNameForAKey(data.name) || data.name;
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
    this.dataTableData = [];
    this.tableDataLoaded = false;
    this.showLoader = true;
    this.currentPointer = 0;
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
  /***********************************************************************************/

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
      const allcertificateTableUrl = environment.certificateTable.url;
      const allcertificateTableMethod = environment.certificateTable.method;
      this.issueListingSubscription = this.issueListingService
        .getData(queryParams, allcertificateTableUrl, allcertificateTableMethod)
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
                // replace below this.issueListingdata to massaged data in case massage() is implemented
                this.currentBucket[this.bucketNumber] = this.issueListingdata;
                this.processData(this.issueListingdata);
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
          if (
            getCols[col].toLowerCase() === 'expiringin') {
              cellObj = {
                link: '',
                properties: {
                  color: ''
                },
                colName: getCols[col],
                hasPreImg: false,
                imgLink: '',
                text: getData[row][getCols[col]],
                valText: parseInt(getData[row][getCols[col]], 10)
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
                'fileFormat': 'csv',
                'serviceId': 5,
                'fileType': fileType
            };
            const downloadRequest =  {
                  'ag': this.selectedAssetGroup,
                  'filter': this.filterText,
                  'from': 0,
                  'searchtext': this.searchTxt,
                  'size': this.totalRows
            };

            const downloadUrl = environment.download.url;
            const downloadMethod = environment.download.method;

            this.downloadService.requestForDownload(
              queryParams,
              downloadUrl,
              downloadMethod,
              downloadRequest,
              'All Certificates',
              this.totalRows);

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
            this.issueFilterSubscription = this.issueFilterService.getFilters(
                {filterId: 2},
                environment.issueFilter.url,
                environment.issueFilter.method)
                .subscribe((response) => {
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
            this.currentFilterType = _.find(this.filterTypeOptions, {optionName: value.value});
            this.issueFilterSubscription = this.issueFilterService.getFilters({
                'ag': this.selectedAssetGroup
            }, environment.base + this.utils.getParamsFromUrlSnippet(this.currentFilterType.optionURL).url, 'GET').subscribe(
                (response) => {
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
                const filterTag = _.find(this.filterTagOptions, {name: value.value});
                this.utils.addOrReplaceElement(this.filters, {
                        key: this.currentFilterType.optionName,
                        value: filterTag['id'],
                        filterkey: this.currentFilterType.optionValue.trim(),
                        compareKey : this.currentFilterType.optionValue.toLowerCase().trim()
                    },
                    (el) => {
                      return el.compareKey === this.currentFilterType.optionValue.toLowerCase().trim();
                    });
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


        const updatedFilters = Object.assign(this.filterText , this.queryParamsWithoutFilter);
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
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }
}
