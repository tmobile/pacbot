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

import { Component, ViewChild, OnInit, OnDestroy } from '@angular/core';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import { AdminService } from '../../../services/all-admin.service';
import { environment } from './../../../../../environments/environment';
import { NgForm } from '@angular/forms';
import { Subscription } from 'rxjs/Subscription';
import { UtilsService } from '../../../../shared/services/utils.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { RouterUtilityService } from '../../../../shared/services/router-utility.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-create-edit-policy',
  templateUrl: './create-edit-policy.component.html',
  styleUrls: ['./create-edit-policy.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,
    AdminService
  ]
})

export class CreateEditPolicyComponent implements OnInit, OnDestroy {
  @ViewChild('policyForm') policyForm: NgForm;
  policyId: any;
  isPolicyIdValid: any = -1;
  policyUrl: any;
  policyVersion: String;
  policyName: any;
  policyDesc: any;
  policyResolution: any;
  policyDetails: any;
  loadingStatus: any;
  pageTitle: String = 'Policies';
  issueListingdata: any;
  selectedAssetGroup: String;
  breadcrumbArray: any = ['Admin'];
  breadcrumbLinks: any = ['asset-dashboard'];

  breadcrumbPresent: any;
  outerArr: any = [];
  filters: any = [];
  successTitle: String = '';
  failedTitle: String = '';
  successSubTitle: String = '';
  isPolicyCreationFailed: boolean = false;
  isPolicyCreationSuccess: boolean = false;
  ruleContentLoader: boolean = true;
  policyLoader: boolean = false;
  invocationId: String = '';
  paginatorSize: number = 25;
  isLastPage: boolean;
  isFirstPage: boolean;
  totalPages: number;
  pageNumber: number = 0;
  showLoader: boolean = true;
  errorMessage: any;

  hideContent: boolean = false;


  filterText: any = {};
  errorValue: number = 0;
  urlID: String = '';
  isCreate: boolean = true;

  FullQueryParams: any;
  queryParamsWithoutFilter: any;
  urlToRedirect: any = '';
  mandatory: any;

  public labels: any;
  private previousUrl: any = '';
  private pageLevel = 0;
  public backButtonRequired;
  private routeSubscription: Subscription;
  private getKeywords: Subscription;
  private previousUrlSubscription: Subscription;
  private downloadSubscription: Subscription;

  constructor(
    private router: Router,
    private utils: UtilsService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private workflowService: WorkflowService,
    private routerUtilityService: RouterUtilityService,
    private adminService: AdminService
  ) {

    this.routerParam();
    this.updateComponent();
  }

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.breadcrumbPresent = 'Create Policy';
    this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
      this.pageLevel
    );
  }

  nextPage() {
    try {
      if (!this.isLastPage) {
        this.pageNumber++;
        this.showLoader = true;
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
      }

    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  allpolicyId: any = [];
  isPolicyIdAvailable(policyIdValidKeyword) {
    if (policyIdValidKeyword.trim().length == 0) {
      this.isPolicyIdValid = -1;
    } else {
        policyIdValidKeyword = 'PacMan_'+policyIdValidKeyword+'_'+this.policyVersion;
        let isKeywordExits = this.allpolicyId.findIndex(item => policyIdValidKeyword.trim().toLowerCase() === item.trim().toLowerCase());
        if (isKeywordExits === -1) {
          this.isPolicyIdValid = 1;
        } else {
          this.isPolicyIdValid = 0;
        }
    }
  }

  getAllPolicyIds() {
    this.hideContent = true;
    this.loadingStatus = 'Existing policies is been loading'
    this.isPolicyCreationSuccess = false;
    this.isPolicyCreationFailed = false;
    this.policyLoader = true;
    const url = environment.allPolicyIds.url;
    const method = environment.allPolicyIds.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.policyLoader = false;
      this.hideContent = false;
      this.allpolicyId =  reponse[0];
    },
      error => {
        this.policyLoader = false;
        this.failedTitle = 'Loading Failed !!';
        this.isPolicyCreationFailed = true;
      })
  }

  createOrUpdatePolicy(policyForm: NgForm) {
    let policyDetails = policyForm.form.value;
    
    let url: String = '';
    let method: String = '';
    let formData = Object();
    if (this.isCreate) {
      url = environment.createPolicy.url;
      method = environment.createPolicy.method;
      formData.policyId = 'PacMan_' + policyDetails.policyName + '_' + this.policyVersion;
      this.policyId = formData.policyId;
      formData.policyName = policyDetails.policyName;
      formData.policyDesc = policyDetails.policyDesc;
      formData.resolution = policyDetails.policyResolution;
      formData.policyUrl = policyDetails.policyUrl;
      formData.policyVersion = this.policyVersion;
      formData.status = 'ENABLED';
      this.loadingStatus = 'details is been creating'
    } else {
      url = environment.updatePolicy.url;
      method = environment.updatePolicy.method;
      formData.policyId = this.policyId
      formData.policyDesc = policyDetails.policyDesc;
      formData.resolution = policyDetails.policyResolution;
      formData.policyUrl = policyDetails.policyUrl;
      formData.policyVersion = this.policyVersion;
      this.loadingStatus = 'details is been updating'
    }

    this.isPolicyCreationSuccess = false;
    this.isPolicyCreationFailed = false;
    this.policyLoader = true;
    this.hideContent = true;
    this.adminService.executeHttpAction(url, method, formData, {}).subscribe(reponse => {
        if(this.isCreate) {
          this.successTitle = 'Policy Created';
          this.successSubTitle = 'created';
        } else {
          this.successTitle = 'Policy Updated';
          this.successSubTitle = 'updated';
        }
        this.policyLoader = false;
        this.isPolicyCreationSuccess = true;
    },
    error => {
      this.policyLoader = false;
      this.isPolicyCreationFailed = true;
      if(this.isCreate) {
        this.failedTitle = 'Creation Failed !!';
      } else {
        this.failedTitle = 'Updation Failed !!';
      }
    });
  }

  closeErrorMessage() {
    this.policyLoader = false;
    this.isPolicyCreationFailed = false;
    this.hideContent = false;
  }

  getData() {
    //this.getAllPolicyIds();
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
        this.policyId = this.queryParamsWithoutFilter.policyId;
        delete this.queryParamsWithoutFilter['filter'];
        if (this.policyId) {
          this.pageTitle = 'Edit Policy';
          this.breadcrumbPresent = 'Edit Policy';
          this.isCreate = false;
          this.hideContent = true;
          this.isPolicyIdValid = 1;
          this.getPolicyDetails(this.policyId);
        } else {
          this.pageTitle = 'Create New Policy';
          this.breadcrumbPresent = 'Create Policy';
          this.policyVersion = 'version-1';
          this.isCreate = true;
          this.getAllPolicyIds();
        }
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

  getPolicyDetails(policyId) {
    this.policyLoader = true;
    let url = environment.getPolicyById.url;
    let method = environment.getPolicyById.method;
    this.loadingStatus = 'details is been loading'
    this.adminService.executeHttpAction(url, method, {}, {policyId: policyId}).subscribe(reponse => {
      this.policyLoader = false;
      this.policyDetails = reponse[0];
      this.policyId = this.policyDetails.policyId;
      this.policyUrl = this.policyDetails.policyUrl;
      this.policyVersion = this.policyDetails.policyVersion;
      this.policyName = this.policyDetails.policyName;
      this.policyDesc = this.policyDetails.policyDesc;
      this.policyResolution = this.policyDetails.resolution;
      this.hideContent = false;
    },
    error => {
      this.policyLoader = false;
    });
  }

  /**
   * This function get calls the keyword service before initializing
   * the filter array ,so that filter keynames are changed
   */

  updateComponent() {
    this.outerArr = [];
    this.showLoader = true;
    this.errorValue = 0;
    this.getData();
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
