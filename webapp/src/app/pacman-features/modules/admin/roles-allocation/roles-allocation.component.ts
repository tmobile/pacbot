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

import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { UtilsService } from '../../../../shared/services/utils.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/pairwise';
import { RefactorFieldsService } from './../../../../shared/services/refactor-fields.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { RouterUtilityService } from '../../../../shared/services/router-utility.service';
import { AdminService } from '../../../services/all-admin.service';

@Component({
  selector: 'app-roles-allocation',
  templateUrl: './roles-allocation.component.html',
  styleUrls: ['./roles-allocation.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,
    AdminService
  ]
})
export class RolesAllocationComponent implements OnInit, OnDestroy {
  pageTitle = 'User Role Allocations';
  allPolicies = [];
  breadcrumbArray = ['Admin'];
  breadcrumbLinks = ['policies'];
  breadcrumbPresent;
  outerArr = [];
  dataLoaded = false;
  errorMessage;
  showingArr = ['policyName', 'policyId', 'policyDesc'];
  allColumns = [];
  totalRows = 0;
  currentBucket = [];
  bucketNumber = 0;
  firstPaginator = 1;
  lastPaginator;
  currentPointer = 0;
  seekdata = false;
  showLoader = true;

  paginatorSize = 25;
  isLastPage;
  isFirstPage;
  totalPages;
  pageNumber = 0;

  searchTxt = '';
  dataTableData = [];
  tableDataLoaded = false;
  filters = [];
  searchCriteria;
  filterText = {};
  errorValue = 0;
  showGenericMessage = false;
  dataTableDesc = '';
  urlID = '';
  public labels;
  FullQueryParams;
  queryParamsWithoutFilter;
  private previousUrl = '';
  urlToRedirect = '';
  private pageLevel = 0;
  public backButtonRequired;
  mandatory;
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
    this.breadcrumbPresent = 'User Role Allocations';
    this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
      this.pageLevel
    );
  }

  nextPage() {
    try {
      if (!this.isLastPage) {
        this.pageNumber++;
        this.showLoader = true;
        this.getPolicyDetails();
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
        this.getPolicyDetails();
      }

    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  getPolicyDetails() {
    const url = environment.rolesAllocation.url;
    const method = environment.rolesAllocation.method;

    const queryParams = {
      page: this.pageNumber,
      size: this.paginatorSize
    };

    if (this.searchTxt !== undefined && this.searchTxt !== '') {
      queryParams['searchTerm'] = this.searchTxt;
    }

    this.adminService.executeHttpAction(url, method, {}, queryParams).subscribe(reponse => {
      this.showLoader = false;
      if (reponse[0].content !== undefined) {
        this.allPolicies = reponse[0].content;
        this.errorValue = 1;
        this.searchCriteria = undefined;
        const data = reponse[0];
        this.tableDataLoaded = true;
        this.dataTableData = reponse[0].content;
        this.dataLoaded = true;
        if (reponse[0].content.length === 0) {
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
          const updatedResponse = this.massageData(data.content);
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
      });
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
    this.getPolicyDetails();
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  massageData(data) {
    const refactoredService = this.refactorFieldsService;
    const newData = [];
    const formattedFilters = data.map(function (datam) {
      const keysTobeChanged = Object.keys(datam);
      let newObj = {};
      keysTobeChanged.forEach(element => {
        const elementnew =
          refactoredService.getDisplayNameForAKey(
            element
          ) || element;
        newObj = Object.assign(newObj, { [elementnew]: datam[element] });
      });
      newObj['No of Roles'] = '';
      newData.push(newObj);
    });
    return newData;
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
          if (getCols[col].toLowerCase() === 'no of roles') {
            cellObj = {
              link: '',
              properties: {
                color: ''
              },
              colName: getCols[col],
              hasPreImg: false,
              imgLink: '',
              text: getData[row]['Roles'].length,
              valText: getData[row]['Roles'].length
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
      this.allColumns = ['User Id', 'No of Roles', 'Roles'];
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  goToDetails(row) {

    if (row.col === 'Actions') {
      try {
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        this.router.navigate(['../create-edit-policy'], {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'merge',
          queryParams: {
            policyId: row.row['Policy Id'].text
          }
        });
      } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.logger.log('error', error);
      }
    } else if (row.col === 'Edit') {
      try {
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        this.router.navigate(['../update-rule'], {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'merge',
          queryParams: {
            ruleId: row.row['Rule Id'].text
          }
        });
      } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.logger.log('error', error);
      }
    } else if (row.col === 'Invoke') {
      try {
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        this.router.navigate(['../invoke-rule'], {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'merge',
          queryParams: {
            ruleId: row.row['Rule Id'].text
          }
        });
      } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.logger.log('error', error);
      }
    } else if (row.col === 'Enable' || row.col === 'Disable') {
      try {
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        this.router.navigate(['../enable-disable-rule'], {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'merge',
          queryParams: {
            ruleId: row.row['Rule Id'].text,
            action: row.col
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
    this.getPolicyDetails();
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
