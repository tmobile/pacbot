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

import { Component, OnInit, OnDestroy, ChangeDetectorRef, ViewChild, ElementRef } from "@angular/core";
import { environment } from "./../../../../../../environments/environment";

import { ActivatedRoute, Router } from "@angular/router";
import { Subscription } from "rxjs/Subscription";
import * as _ from "lodash";
import { UtilsService } from "../../../../../shared/services/utils.service";
import { LoggerService } from "../../../../../shared/services/logger.service";
import { ErrorHandlingService } from "../../../../../shared/services/error-handling.service";
import { NavigationStart } from "@angular/router";
import { Event, NavigationEnd } from "@angular/router";
import "rxjs/add/operator/filter";
import "rxjs/add/operator/pairwise";
import { RoutesRecognized } from "@angular/router";
import { RefactorFieldsService } from "./../../../../../shared/services/refactor-fields.service";
import { WorkflowService } from "../../../../../core/services/workflow.service";
import { RouterUtilityService } from "../../../../../shared/services/router-utility.service";
import { AdminService } from "../../../../services/all-admin.service";
import { NgForm } from "@angular/forms";
import { SelectComponent } from "ng2-select";
import { UploadFileService } from "../../../../services/upload-file-service";

@Component({
  selector: 'app-admin-delete-asset-groups',
  templateUrl: './delete-asset-groups.component.html',
  styleUrls: ['./delete-asset-groups.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,
    UploadFileService,
    AdminService
  ]
})
export class DeleteAssetGroupsComponent implements OnInit {
  pageTitle: String = "Delete Asset Groups";
  breadcrumbArray: any = ["Admin", "Asset Groups"];
  breadcrumbLinks: any = ["policies", "asset-groups"];
  breadcrumbPresent: any;
  outerArr: any = [];
  filters: any = [];
  isAssetGroupDeletionFailed: boolean = false;
  isAssetGroupDeletionSuccess: boolean = false;
  ruleContentLoader: boolean = true;
  assetGroupLoader: boolean = false;
  invocationId: String = "";
  showLoader: boolean = true;
  errorMessage: any;

  hideContent: boolean = false;

  filterText: any = {};
  errorValue: number = 0;
  urlID: String = "";
  groupId: string = "";
  groupName: string = "";

  FullQueryParams: any;
  queryParamsWithoutFilter: any;
  urlToRedirect: any = "";
  mandatory: any;

  public labels: any;
  private previousUrl: any = "";
  private pageLevel = 0;
  public backButtonRequired;
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
    private ref: ChangeDetectorRef,
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
    this.breadcrumbPresent = "Delete Asset Groups";
    this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
      this.pageLevel
    );
  }
  
  deleteAssetGroup() {
    this.hideContent = true;
    this.assetGroupLoader = true;
    let url = environment.deleteAssetGroups.url; 
    let method = environment.deleteAssetGroups.method; 
    this.adminService.executeHttpAction(url, method, {groupId: this.groupId}, {}).subscribe(reponse => {
      this.assetGroupLoader = false;
      this.isAssetGroupDeletionSuccess = true;
    },
    error => {
      this.isAssetGroupDeletionFailed = true;
      this.assetGroupLoader = false;
    })
  }

  closeErrorMessage() {
    this.isAssetGroupDeletionFailed = false;
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
        this.groupId = this.FullQueryParams.groupId;
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
      this.logger.log("error", error);
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
      this.logger.log("error", error);
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
      this.logger.log("error", "--- Error while unsubscribing ---");
    }
  }
}
