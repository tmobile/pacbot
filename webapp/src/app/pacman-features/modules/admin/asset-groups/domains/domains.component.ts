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
import { environment } from './../../../../../../environments/environment';

import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { UtilsService } from '../../../../../shared/services/utils.service';
import { LoggerService } from '../../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../../shared/services/error-handling.service';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/pairwise';
import { RefactorFieldsService } from './../../../../../shared/services/refactor-fields.service';
import { WorkflowService } from '../../../../../core/services/workflow.service';
import { RouterUtilityService } from '../../../../../shared/services/router-utility.service';
import { AdminService } from '../../../../services/all-admin.service';

@Component({
  selector: 'app-admin-domains',
  templateUrl: './domains.component.html',
  styleUrls: ['./domains.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,

    AdminService
  ]
})
export class DomainsComponent implements OnInit, OnDestroy {
  pageTitle: String = 'Domains';
  allDomains: any = [];


  breadcrumbArray: any = ['Admin'];
  breadcrumbLinks: any = ['policies'];
  breadcrumbPresent: any;
  outerArr: any = [];
  dataLoaded: boolean = false;
  errorMessage: any;
  showingArr: any = ['policyName', 'policyId', 'policyDesc'];
  allColumns: any = [];
  totalRows: number = 0;
  currentBucket: any = [];
  bucketNumber: number = 0;
  firstPaginator: number = 1;
  lastPaginator: number;
  currentPointer: number = 0;
  seekdata: boolean = false;
  showLoader: boolean = true;

  paginatorSize: number = 25;
  isLastPage: boolean;
  isFirstPage: boolean;
  totalPages: number;
  pageNumber: number = 0;

  searchTxt: String = '';
  dataTableData: any = [];
  tableDataLoaded: boolean = false;
  filters: any = [];
  searchCriteria: any;
  filterText: any = {};
  errorValue: number = 0;
  showGenericMessage: boolean = false;
  dataTableDesc: String = '';
  urlID: String = '';
  public labels: any;
  FullQueryParams: any;
  queryParamsWithoutFilter: any;
  private previousUrl: any = '';
  urlToRedirect: any = '';
  private pageLevel = 0;
  public backButtonRequired;
  mandatory: any;
  private routeSubscription: Subscription;
  private getKeywords: Subscription;
  private previousUrlSubscription: Subscription;
  private downloadSubscription: Subscription;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private utils: UtilsService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private refactorFieldsService: RefactorFieldsService,
    private workflowService: WorkflowService,
    private routerUtilityService: RouterUtilityService,
    private adminService: AdminService
  ) {

    this.routerParam();
    this.updateComponent();
  }


  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.breadcrumbPresent = 'Domains';
    this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
      this.pageLevel
    );
  }

  nextPage() {
    try {
      if (!this.isLastPage) {
        this.pageNumber++;
        this.showLoader = true;
        this.getAssetGroupsDetails();
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  prevPage() {
    try {
      if (!this.isFirstPage) {
        this.pageNumber--;
        this.showLoader = true;
        this.getAssetGroupsDetails();
      }

    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  getAssetGroupsDetails() {
    var url = environment.domainsDetails.url;
    var method = environment.domainsDetails.method;

    var queryParams = {
      page: this.pageNumber,
      size: this.paginatorSize
    };

    if (this.searchTxt !== undefined && this.searchTxt !== '') {
      queryParams['searchTerm'] = this.searchTxt;
    }

    this.adminService.executeHttpAction(url, method, {}, queryParams).subscribe(reponse => {
      this.showLoader = false;
      if (reponse[0].content !== undefined) {
        reponse[0].content= this.dataMarshalling(reponse[0].content);
        this.allDomains= reponse[0].content;
        this.errorValue = 1;
        this.searchCriteria = undefined;
        var data = reponse[0];
        this.tableDataLoaded = true;
        this.dataTableData = reponse[0].content;
        this.dataLoaded = true;
        if (reponse[0].content.length == 0) {
          this.errorValue = -1;
          this.outerArr = [];
          this.allColumns = [];
        }
      
        if (data.content.length > 0) {
          this.isLastPage = data.last;
          this.isFirstPage = data.first;
          this.totalPages = data.totalPages;
          this.pageNumber = data.number;

          this.seekdata = false;

          this.totalRows = data.totalElements;

          this.firstPaginator = data.number * this.paginatorSize + 1;
          this.lastPaginator = data.number * this.paginatorSize + this.paginatorSize;

          this.currentPointer = data.number;

          if (this.lastPaginator > this.totalRows) {
            this.lastPaginator = this.totalRows;
          }
          
          let updatedResponse = this.massageData(data.content);
          this.processData(updatedResponse);
        }
      }
    },
      error => {
        this.showGenericMessage = true;
        this.errorValue = -1;
        this.outerArr = [];
        this.dataLoaded = true;
        this.seekdata = true;
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      })
  }

  /*
    * This function gets the urlparameter and queryObj 
    *based on that different apis are being hit with different queryparams
    */
  routerParam() {
    try {
      // this.filterText saves the queryparam
      let currentQueryParams = this.routerUtilityService.getQueryParametersFromSnapshot(this.router.routerState.snapshot.root);
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
        //check for mandatory filters.
        if (this.FullQueryParams.mandatory) {
          this.mandatory = this.FullQueryParams.mandatory;
        }

      }
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
    this.getAssetGroupsDetails();
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  dataMarshalling(dataToMarshall) {
    let fullPolicies = [];
    for (var index = 0; index < dataToMarshall.length; index++) {
      let policyItem = {};
      policyItem['domainDesc'] = dataToMarshall[index][0];
      policyItem['domainName'] = dataToMarshall[index][1];
      policyItem['config'] = dataToMarshall[index][2];
      policyItem['targetTypesCount'] = dataToMarshall[index][3];
      fullPolicies.push(policyItem);
    }
    return fullPolicies;
  }

  massageData(data) {
    let refactoredService = this.refactorFieldsService;
    let newData = [];
    let formattedFilters = data.map(function (data) {
      let keysTobeChanged = Object.keys(data);
      let newObj = {};
      keysTobeChanged.forEach(element => {
        var elementnew =
          refactoredService.getDisplayNameForAKey(
            element
          ) || element;
        newObj = Object.assign(newObj, { [elementnew]: data[element] });
      });
      newObj['Actions'] = '';
      newData.push(newObj);
    });
    return newData;
  }

  processData(data) {
    try {
      var innerArr = {};
      var totalVariablesObj = {};
      var cellObj = {};
      var magenta = '#e20074';
      var green = '#26ba9d';
      var red = '#f2425f';
      var orange = '#ffb00d';
      var yellow = 'yellow';
      this.outerArr = [];
      var getData = data;

      if (getData.length) {
        var getCols = Object.keys(getData[0]);
      } else {
        this.seekdata = true;
      }

      for (var row = 0; row < getData.length; row++) {
        innerArr = {};
        for (var col = 0; col < getCols.length; col++) {
          if (getCols[col].toLowerCase() == 'actions') {
            cellObj = {
              link: true,
              properties: {
                'text-shadow': '0.33px 0',
                'color': '#ed0295'
              },
              colName: getCols[col],
              hasPreImg: false,
              valText: 'Edit',
              imgLink: '',
              text: 'Edit',
              statusProp: {
                'color': '#ed0295'
              }
            };
          }  else if (getCols[col].toLowerCase() == 'target types') {
            let targetTypeName = getData[row][getCols[col]].map(target => target.targetType); 
            cellObj = {
              link: '',
              properties: {
                color: ''
              },
              colName: getCols[col],
              hasPreImg: false,
              imgLink: '',
              text: targetTypeName,
              valText: targetTypeName,
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
        var halfLength = this.outerArr.length / 2;
        this.outerArr = this.outerArr.splice(halfLength);
      }
      this.allColumns = Object.keys(totalVariablesObj);
      this.allColumns = ['Domain Name', 'Domain Desc', 'Asset Types Count', 'Config', 'Actions'];
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  goToCreateUpdateDomain() {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      this.router.navigate(['../create-update-domain'], {
        relativeTo: this.activatedRoute,
        queryParamsHandling: 'merge',
        queryParams: {
        }
      });
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  goToDetails(row) {
    if(row.col === 'Actions') {
      try {
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        this.router.navigate(['../create-update-domain'], {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'merge',
          queryParams: {
            domainName : row.row['Domain Name'].text
          }
        });
      } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.logger.log('error', error);
      }
    }
  }

  searchCalled(search) {
    this.searchTxt = search;
  }

  callNewSearch() {
    this.bucketNumber = 0;
    this.currentBucket = [];
    this.pageNumber = 0;
    this.paginatorSize = 25;
    this.getAssetGroupsDetails();
  }

  ngOnDestroy() {
    try {
      if (this.routeSubscription) {
        this.routeSubscription.unsubscribe();
      }
      if (this.previousUrlSubscription) {
        this.previousUrlSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }
}
