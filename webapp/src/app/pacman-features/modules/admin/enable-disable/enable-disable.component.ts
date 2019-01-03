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

import {  Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { UtilsService } from '../../../../shared/services/utils.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/pairwise';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { RouterUtilityService } from '../../../../shared/services/router-utility.service';
import { AdminService } from '../../../services/all-admin.service';

@Component({
  selector: 'app-admin-enable-disable',
  templateUrl: './enable-disable.component.html',
  styleUrls: ['./enable-disable.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,
    AdminService
  ]
})
export class EnableDisableRuleJobComponent implements OnInit, OnDestroy {
  pageTitle = '';
  breadcrumbArray: any = [];
  breadcrumbLinks: any = [];
  breadcrumbPresent: any;
  outerArr: any = [];
  filters: any = [];
  action: any = '';
  ruleOrJobId: any = '';

  allOptionalRuleParams: any = [];
  isEnableDisableInvokeFailed = false;
  isEnableDisableInvokeSuccess = false;
  ruleContentLoader = true;
  ruleLoader = false;
  invocationId = '';
  paginatorSize= 25;
  isRule;
  isLastPage;
  isFirstPage;
  totalPages: number;
  pageNumber= 0;
  showLoader = true;
  errorMessage: any;
  errorMessageDetails: any;

  hideContent = false;


  filterText: any = {};
  errorValue= 0;
  urlID = '';
  ruleId = '';
  jobId = '';

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

  enableDisableRuleOrJob() {
    this.hideContent = true;
    this.ruleLoader = true;
    const url = environment.enableDisableRuleOrJob.url;
    const method = environment.enableDisableRuleOrJob.method;
    const params = {};
    if (this.isRule) {
      params['ruleId'] = this.ruleId;
    } else {
      params['jobId'] = this.jobId;
    }
    params['action'] = this.action;

    this.adminService.executeHttpAction(url, method, {}, params).subscribe(reponse => {
      this.ruleLoader = false;
      this.isEnableDisableInvokeSuccess = true;
      this.invocationId = reponse[0].data;
    },
      error => {
        this.isEnableDisableInvokeFailed = true;
        this.ruleLoader = false;
      });
  }

  removeOptionalRuleParameters(index: number): void {
    this.allOptionalRuleParams.splice(index, 1);
  }

  closeErrorMessage() {
    this.isEnableDisableInvokeFailed = false;
    this.hideContent = false;
  }

  addOptionalRuleParameters(ruleParamKey: any, ruleParamValue: any, isEncrypted: any) {
    if (ruleParamKey.value !== '' && ruleParamValue.value !== '') {
      this.allOptionalRuleParams.push({ key: ruleParamKey.value, value: ruleParamValue.value, encrypt: isEncrypted.checked });
      ruleParamKey.value = '';
      ruleParamValue.value = '';
      isEncrypted.checked = false;
    }
  }

  buildRuleOrJobInfo(action, isRule) {
    this.pageTitle = isRule ? 'Enable Rule' : 'Enable Job';
    this.breadcrumbPresent = this.pageTitle;
    if (isRule) {
      this.breadcrumbArray = ['Admin', 'Rules'];
      this.breadcrumbLinks = ['policies', 'rules'];
      this.ruleOrJobId = this.ruleId;
    } else {
      this.breadcrumbArray = ['Admin', 'Job Execution Manager'];
      this.breadcrumbLinks = ['policies', 'job-execution-manager'];
      this.ruleOrJobId = this.jobId;
    }

    if (action === 'enable') {
      this.pageTitle = isRule ? 'Enable Rule' : 'Enable Job';
    } else {
      this.pageTitle = isRule ? 'Disable Rule' : 'Disable Job';
    }
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
        this.ruleId = this.FullQueryParams.ruleId;
        this.jobId = this.FullQueryParams.jobId;
        this.action = this.FullQueryParams.action.toLowerCase();
        if (this.action === 'enable' || this.action === 'disable') {
          this.isRule = this.ruleId !== undefined;
          this.buildRuleOrJobInfo(this.action, this.isRule);
        } else {
          this.navigateBack();
        }

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
    this.showLoader = true;
    this.errorValue = 0;
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
