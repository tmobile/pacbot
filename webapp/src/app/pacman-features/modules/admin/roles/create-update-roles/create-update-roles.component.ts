/*
 *Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License'); You may not use
 * this file except in compliance with the License. A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the 'license' file accompanying this file. This file is distributed on
 * an 'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
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
import { CommonResponseService } from '../../../../../shared/services/common-response.service';

@Component({
  selector: 'app-admin-create-update-roles',
  templateUrl: './create-update-roles.component.html',
  styleUrls: ['./create-update-roles.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,
    UploadFileService,
    AdminService,
    CommonResponseService
  ]
})
export class CreateUpdateRolesComponent implements OnInit, OnDestroy {
  pageTitle = '';
  breadcrumbArray = ['Admin', 'Roles'];
  breadcrumbLinks = ['policies', 'roles'];
  breadcrumbPresent;
  outerArr = [];
  filters = [];

  public queryValue = '';
  public filteredList = [];
  public emailArray = [];
  public users;
  public idDetailsName = [];
  private getUserSubscription: Subscription;
  invalid = true;
  arrowkeyLocation = 0;

  roles = {
    roleName: '',
    description: '',
    writePermission: false
  };
  selectedRoleName = '';

  isCreate = false;
  successTitle = '';
  failedTitle = '';
  highlightName = '';
  allRDetails = [];
  allCategoryDetails = [];
  successSubTitle = '';
  isRoleCreationUpdationFailed = false;
  isRoleCreationUpdationSuccess = false;
  loadingContent = '';
  roleLoader = false;

  roleId = '';
  paginatorSize = 25;
  isLastPage;
  isFirstPage;
  totalPages;
  pageNumber = 0;
  showLoader = true;
  errorMessage;

  hideContent = false;

  filterText = {};
  errorValue= 0;

  FullQueryParams;
  queryParamsWithoutFilter;
  urlToRedirect = '';
  mandatory;

  public labels;
  private previousUrl = '';
  private pageLevel = 0;
  public backButtonRequired;
  private routeSubscription: Subscription;
  private getKeywords: Subscription;
  private previousUrlSubscription: Subscription;

  constructor(
    private router: Router,
    private utils: UtilsService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private workflowService: WorkflowService,
    private routerUtilityService: RouterUtilityService,
    private adminService: AdminService,
    private commonResponseService: CommonResponseService,
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

  filter() {
    try {
      if (this.queryValue !== '') {
        this.filteredList = this.idDetailsName.filter(
          function(el) {
            return el.toLowerCase().indexOf(this.queryValue.toLowerCase()) > -1;
          }.bind(this)
        );
      } else {
        this.filteredList = [];
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  removeData(index): any {
    try {
      this.emailArray.splice(index, 1);
      if (this.emailArray.length < 1) {
        this.invalid = false;
      } else {
        this.invalid = true;
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  select(item) {
    try {
      this.queryValue = item;
      this.filteredList = [];
      this.emailArray.push(item);
      this.queryValue = '';
      if (this.emailArray.length < 1) {
        this.invalid = false;
      } else {
        this.invalid = true;
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  keyDown(event: KeyboardEvent) {
    try {
      switch (event.keyCode) {
        case 38: // this is the ascii of arrow up
          this.arrowkeyLocation--;
          break;
        case 40: // this is the ascii of arrow down
          this.arrowkeyLocation++;
          break;
        case 13: // this is the ascii of enter
          this.queryValue = this.filteredList[this.arrowkeyLocation];
          this.filteredList = [];
          this.emailArray.push(this.queryValue);
          this.queryValue = '';
          if (this.emailArray.length < 1) {
            this.invalid = false;
          } else {
            this.invalid = true;
          }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  keyEvent(event: KeyboardEvent, item) {
    try {
      switch (event.keyCode) {
        case 13: // this is the ascii of enter
          this.queryValue = item;
          this.filteredList = [];
          this.emailArray.push(item);
          this.queryValue = '';
          if (this.emailArray.length < 1) {
            this.invalid = false;
          } else {
            this.invalid = true;
          }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  getUsers(): any {
    try {
      const userUrl = environment.users.url;
      const userMethod = environment.users.method;
      const queryparams = {};
      this.getUserSubscription = this.commonResponseService
        .getData(userUrl, userMethod, {}, queryparams)
        .subscribe(
          response => {
            this.users = response.values;
            for (let i = 0; i < this.users.length; i++) {
              const userdetails =
                this.users[i].displayName +
                ' ' +
                '(' +
                this.users[i].userId +
                ')';
              this.idDetailsName.push(userdetails);
            }
          },
          error => {}
        );
    } catch (e) {
      this.logger.log('error', e);
    }
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
        // this.getPolicyDetails();
      }

    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }


  createRole(roleDetails) {
    this.loadingContent = 'creation';
    this.hideContent = true;
    this.roleLoader = true;
    this.isRoleCreationUpdationFailed = false;
    this.isRoleCreationUpdationSuccess = false;
    this.selectedRoleName = roleDetails.roleName;
    this.highlightName = roleDetails.roleName;
    const url = environment.createRole.url;
    const method = environment.createRole.method;
    this.adminService.executeHttpAction(url, method, roleDetails, {}).subscribe(reponse => {
      this.successTitle = 'Role Created';
      this.isRoleCreationUpdationSuccess = true;
      this.roleLoader = false;
      this.roles = {
        roleName: '',
        description: '',
        writePermission: false
      };
    },
      error => {
        this.failedTitle = 'Creation Failed';
        this.roleLoader = false;
        this.isRoleCreationUpdationFailed = true;
      });
  }

  updateRole(roleDetails) {
    this.loadingContent = 'updating';
    this.hideContent = true;
    this.roleLoader = true;
    this.isRoleCreationUpdationFailed = false;
    this.isRoleCreationUpdationSuccess = false;
    this.selectedRoleName = roleDetails.roleName;
    this.highlightName = roleDetails.roleName;
    const url = environment.updateRole.url;
    const method = environment.updateRole.method;
    roleDetails.roleId = this.roleId;
    this.adminService.executeHttpAction(url, method, roleDetails, {}).subscribe(reponse => {
      this.successTitle = 'Role Updated';
      this.isRoleCreationUpdationSuccess = true;
      this.roleLoader = false;
      this.roles = {
        roleName: '',
        description: '',
        writePermission: false
      };
    },
      error => {
        this.failedTitle = 'Updation Failed';
        this.roleLoader = false;
        this.isRoleCreationUpdationFailed = true;
      });
  }

  closeErrorMessage() {
    if (this.failedTitle === 'Loading Failed') {
      //  this.getDomainAndCategoryDetails();
    } else {
      this.hideContent = false;
    }
    this.isRoleCreationUpdationFailed = false;
    this.isRoleCreationUpdationSuccess = false;
  }

  getData() {
    // this.getAllPolicyIds();
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
        this.roleId = this.queryParamsWithoutFilter.roleId;
        const selectedRoleName = this.queryParamsWithoutFilter.roleName;
        delete this.queryParamsWithoutFilter['filter'];
        if (this.roleId) {
          this.pageTitle = 'Edit Role';
          this.breadcrumbPresent = 'Edit Role';
          this.isCreate = false;
          this.highlightName = selectedRoleName;
          this.getRoleDetails();
        } else {
          this.pageTitle = 'Create New Role';
          this.breadcrumbPresent = 'Create Role';
          this.isCreate = true;
        }

        /**
         * The below code is added to get URLparameter and queryparameter
         * when the page loads ,only then this function runs and hits the api with the
         * filterText obj processed through processFilterObj function
         */
        this.filterText = this.utils.processFilterObj(
          this.FullQueryParams
        );

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

  getRoleDetails() {
    this.hideContent = true;
    this.roleLoader = true;
    this.loadingContent = 'loading';
    this.isRoleCreationUpdationFailed = false;
    this.isRoleCreationUpdationSuccess = false;
    const url = environment.getRoleById.url;
    const method = environment.getRoleById.method;
    this.adminService.executeHttpAction(url, method, {}, {roleId: this.roleId}).subscribe(userRoleReponse => {
      if (!this.isCreate) {
        this.hideContent = false;
        this.roleLoader = false;
        this.roles.roleName = userRoleReponse[0].roleName;
        this.roles.description = userRoleReponse[0].roleDesc;
        this.roles.writePermission = userRoleReponse[0].writePermission;
      }
    },
      error => {
        this.errorValue = -1;
        this.outerArr = [];
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
        this.failedTitle = 'Loading Failed';
        this.loadingContent = 'Loading';
        this.highlightName = 'Domain and Category';
        this.isRoleCreationUpdationFailed = true;
        this.roleLoader = false;
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
    this.getUsers();
    this.invalid = true;
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
