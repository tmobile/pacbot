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

import { Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { UtilsService } from '../../../../../shared/services/utils.service';
import { LoggerService } from '../../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../../shared/services/error-handling.service';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/pairwise';
import { WorkflowService } from '../../../../../core/services/workflow.service';
import { RouterUtilityService } from '../../../../../shared/services/router-utility.service';
import { AdminService } from '../../../../services/all-admin.service';
import { UploadFileService } from '../../../../services/upload-file-service';

@Component({
  selector: 'app-admin-delete-sticky-exceptions',
  templateUrl: './delete-sticky-exceptions.component.html',
  styleUrls: ['./delete-sticky-exceptions.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,
    UploadFileService,
    AdminService
  ]
})
export class DeleteStickyExceptionsComponent implements OnInit, OnDestroy {
  pageTitle: String = 'Delete Sticky Exception';
  breadcrumbArray: any = ['Admin', 'Sticky Exceptions'];
  breadcrumbLinks: any = ['policies', 'sticky-exceptions'];
  breadcrumbPresent: any;
  outerArr: any = [];
  filters: any = [];
  isExceptionDeletionFailed: boolean = false;
  isExceptionDeletionSuccess: boolean = false;
  ruleContentLoader: boolean = true;
  exceptionLoader: boolean = false;
  invocationId: String = '';
  showLoader: boolean = true;
  errorMessage: any;

  hideContent: boolean = false;

  filterText: any = {};
  errorValue: number = 0;
  urlID: String = '';
  exceptionName: string = '';
  groupName: string = '';

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
    this.breadcrumbPresent = 'Delete Sticky Exception';
    this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
      this.pageLevel
    );
  }
  
  deleteException() {
    this.hideContent = true;
    this.exceptionLoader = true;
    let url = environment.deleteStickyException.url; 
    let method = environment.deleteStickyException.method; 
    this.adminService.executeHttpAction(url, method, {exceptionName: this.exceptionName, groupName: this.groupName}, {}).subscribe(reponse => {
      this.exceptionLoader = false;
      this.isExceptionDeletionSuccess = true;
    },
    error => {
      this.isExceptionDeletionFailed = true;
      this.exceptionLoader = false;
    })
  }

  closeErrorMessage() {
    this.isExceptionDeletionFailed = false;
    this.hideContent = false;
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
        this.exceptionName = this.FullQueryParams.exceptionName;
        this.groupName = this.FullQueryParams.groupName;
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
