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
import { UtilsService } from '../../../../shared/services/utils.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import 'rxjs/add/operator/filter';
import {RefactorFieldsService} from './../../../../shared/services/refactor-fields.service';
import {WorkflowService} from '../../../../core/services/workflow.service';


@Component({
  selector: 'app-aws-notifications',
  templateUrl: './aws-notifications.component.html',
  styleUrls: ['./aws-notifications.component.css'],
  providers: [
    IssueListingService,
    LoggerService,
    ErrorHandlingService
  ]
})

export class AwsNotificationsComponent implements OnInit, OnDestroy {

  pageTitle = 'AWS Notifications';
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
  private urlParams: any;
  queryParamsWithoutFilter: any;
  resourceId: string;
  resourceType: string;
  statuscode: string;
  public decodedResourceId: string;
  private assetGroupSubscription: Subscription;
  private routeSubscription: Subscription;
  private issueListingSubscription: Subscription;
  private querySubscription: Subscription;
  public pageLevel = 0;
  public backButtonRequired;

  constructor(
    private assetGroupObservableService: AssetGroupObservableService,
    private activatedRoute: ActivatedRoute,
    private issueListingService: IssueListingService,
    private router: Router,
    private utils: UtilsService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private refactorFieldsService: RefactorFieldsService,
    private workflowService: WorkflowService
  ) {
    this.getRuleId();
    this.assetGroupSubscription = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(this.pageLevel);
        this.selectedAssetGroup = assetGroupName;
        this.routerParam();
        this.updateComponent();
        this.deleteFilters();
        this.getFilterArray();
      });
  }

  getRuleId() {
    /*
    * this funtion stores the URL params
    */
    this.routeSubscription = this.activatedRoute.params.subscribe(params => {
        this.urlParams = params; // <<-- This urlParams is used while calling the api
        this.resourceId = this.urlParams.resourceId;
        this.decodedResourceId = decodeURIComponent(this.resourceId);
        this.resourceType = this.urlParams.resourceType;
    });
    this.querySubscription = this.activatedRoute.queryParams.subscribe(queryParams => {
          this.statuscode = queryParams['statuscode'];
    });
  }

  ngOnInit() {
    this.breadcrumbPresent = 'AWS Notifications';
    // gets the current page url,which is used to come back to the same page after navigate
  }

  /*
    * This function gets the urlparameter and queryObj
    *based on that different apis are being hit with different queryparams
    */
  routerParam() {
        try {
      // this.filterText saves the queryparam
      if (this.activatedRoute.snapshot.queryParams) {
        /**
         * FullQueryParams hold the entire queryobj(filter obj + the other obj)
         * queryParamsWithoutFilter holds only the part without the filter,
         * queryParamsWithoutFilter is used so that while deleting the filter we can append the remaining part
         * which is not part of filterobj(check in deleteFilters function)
         */
        if (this.activatedRoute.snapshot.queryParams.filter !== '' && this.activatedRoute.snapshot.queryParams.filter !== undefined) {
          this.FullQueryParams = this.activatedRoute.snapshot.queryParams;
          this.queryParamsWithoutFilter = Object.keys(this.FullQueryParams).slice(1).map(key => ({[key]: this.FullQueryParams[key]}));
          const obj = {};
          this.queryParamsWithoutFilter.forEach(element => {
          const localKeys = Object.keys(element);
          obj[localKeys[0]] = element[localKeys[0]];
        });
        this.queryParamsWithoutFilter = obj;
        }

        /**
         * The below code is added to get URLparameter and queryparameter
         * when the page loads ,only then this function runs and hits the api with the
         * filterText obj processed through processFilterObj function
         */
        this.filterText = this.utils.processFilterObj(this.activatedRoute.snapshot.queryParams);
      }
      // this.urlID saves the urlparameter
      this.routeSubscription = this.activatedRoute.params.subscribe(params => {
        this.urlID = params['id'];
      });
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
          key: formattedFilters[i].name,
          value: this.filterText[filterObjKeys[i]],
          filterkey: filterObjKeys[i]
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
      let queryParams;

      if (this.issueListingSubscription) {
        this.issueListingSubscription.unsubscribe();
      }

      queryParams = {
        filter: {'statuscode.keyword': this.statuscode},
        from: this.bucketNumber * this.paginatorSize,
        searchtext: this.searchTxt,
        size: this.paginatorSize
      };
      this.errorValue = 0;
      const url = environment.awsNotificationsDetails.url;
      const method = environment.awsNotificationsDetails.method;
      const newUrl = this.replaceUrl(url);
      this.issueListingSubscription = this.issueListingService
        .getData(queryParams, newUrl, method)
        .subscribe(
          response => {
            this.showGenericMessage = false;
            try {
              this.errorValue = 1;
              this.searchCriteria = undefined;
              this.tableDataLoaded = true;
              this.dataTableData = response[0];
              const data = response[0];
              this.showLoader = false;
              this.dataLoaded = true;
              if (response[0].length === 0) {
                this.errorValue = -1;
                this.outerArr = [];
                this.allColumns = [];
              }
              if (data.length > 0) {
                this.issueListingdata = data;

                this.seekdata = false;

                this.totalRows = data.total;
                if (response.hasOwnProperty('total')) {
                        this.totalRows = response.total;
                    } else {
                        this.totalRows = response.length;
                }

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



  callNewSearch() {
    this.bucketNumber = 0;
    this.currentBucket = [];
    this.getData();
  }

  replaceUrl(url) {
    let replacedUrl = url.replace('{resourceId}', this.resourceId.toString());
    replacedUrl = replacedUrl.replace('{assetGroup}', this.selectedAssetGroup.toString());
    replacedUrl = replacedUrl.replace('{resourceType}', this.resourceType.toString());
    return replacedUrl;
  }

  ngOnDestroy() {
    try {
      if (this.assetGroupSubscription) {
        this.assetGroupSubscription.unsubscribe();
      }
      if (this.routeSubscription) {
        this.routeSubscription.unsubscribe();
      }
      if (this.issueListingSubscription) {
        this.issueListingSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }
}
