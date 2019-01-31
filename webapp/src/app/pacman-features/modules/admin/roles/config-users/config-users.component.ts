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

import {  Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import * as _ from 'lodash';
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
  selector: 'app-admin-config-users',
  templateUrl: './config-users.component.html',
  styleUrls: ['./config-users.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,
    UploadFileService,
    AdminService,
    CommonResponseService
  ]
})
export class ConfigUsersComponent implements OnInit, OnDestroy {
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

  isCreate = false;
  successTitle = '';
  failedTitle = '';
  successSubTitle = '';
  availChoosedItems = {};
  availChoosedSelectedItems = {};
  availChoosedItemsCount = 0;
  selectedRoleName = '';

  selectChoosedItems = {};
  selectChoosedSelectedItems = {};
  selectChoosedItemsCount = 0;

  availableItems = [];
  selectedItems = [];

  availableItemsBackUp = [];
  selectedItemsBackUp = [];

  availableItemsCopy = [];
  selectedItemsCopy = [];

  searchSelectedUsersTerms = '';
  searchAvailableUsersTerms = '';
  isRoleCreationUpdationFailed = false;
  isRoleCreationUpdationSuccess = false;
  highlightName = '';
  allRDetails: any = [];
  allCategoryDetails: any = [];
  allAllocatedUsers: any = [];
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
  errorValue = 0;

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

  nextPage() {
    try {
      if (!this.isLastPage) {
        this.pageNumber++;
        this.showLoader = true;
        // this.getPolicyDetails();
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


  onClickAvailableItem(index, availableItem, key) {
    if (this.availChoosedItems.hasOwnProperty(index)) {
      this.availChoosedItems[index] = !this.availChoosedItems[index];
      if (this.availChoosedItems[index]) {
        this.availChoosedSelectedItems[key] = availableItem;
      } else {
        delete this.availChoosedSelectedItems[key];
      }

    } else {
      this.availChoosedItems[index] = true;
      this.availChoosedSelectedItems[key] = availableItem;
    }
    this.availChoosedItemsCount = Object.keys(this.availChoosedSelectedItems).length;
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
      const userUrl = environment.listUsers.url;
      const userMethod = environment.listUsers.method;
      const queryparams = {};
      this.hideContent = true;
      this.roleLoader = true;
      this.loadingContent = 'loading';
      this.isRoleCreationUpdationFailed = false;
      this.isRoleCreationUpdationSuccess = false;
      this.getUserSubscription = this.commonResponseService.getData(userUrl, userMethod, {}, queryparams)
        .subscribe(
          response => {
            this.emailArray = [];
            this.idDetailsName = [];
            this.hideContent = false;
            this.roleLoader = false;
            this.users = response;
            for (let i = 0; i < this.users.length; i++) {
              const userdetails =
                this.users[i].userName +
                ' ' +
                '(' +
                this.users[i].userId +
                ')';
              this.users[i]['user'] = userdetails;
              if (this.allAllocatedUsers.indexOf(this.users[i].userId.toLowerCase()) === -1) {
                this.idDetailsName.push(this.users[i]);
              } else {
                this.emailArray.push(this.users[i]);
              }
            }

            const availableItems = _.cloneDeep(this.idDetailsName);
            const selectedItems = _.cloneDeep(this.emailArray);

            this.availableItems = _.cloneDeep(availableItems);
            this.selectedItems = _.cloneDeep(selectedItems);
            this.availableItemsBackUp = _.cloneDeep(availableItems);
            this.selectedItemsBackUp = _.cloneDeep(selectedItems);
            this.availableItemsCopy = _.cloneDeep(availableItems);
            this.selectedItemsCopy = _.cloneDeep(selectedItems);
            this.searchAvailableUsers();
            this.searchSelectedUsers();
          },
          error => {
            this.errorValue = -1;
            this.outerArr = [];
            this.errorMessage = 'apiResponseError';
            this.showLoader = false;
            this.failedTitle = 'Loading Failed';
            this.loadingContent = 'Loading';
            this.highlightName = 'Role Details';
            this.isRoleCreationUpdationFailed = true;
            this.roleLoader = false;
          }
        );
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  onClickSelectedItem(index, selectedItem, key) {
    if (this.selectChoosedItems.hasOwnProperty(index)) {
      this.selectChoosedItems[index] = !this.selectChoosedItems[index];
      if (this.selectChoosedItems[index]) {
        this.selectChoosedSelectedItems[key] = selectedItem;
      } else {
        delete this.selectChoosedSelectedItems[key];
      }
    } else {
      this.selectChoosedItems[index] = true;
      this.selectChoosedSelectedItems[key] = selectedItem;
    }
    this.selectChoosedItemsCount = Object.keys(this.selectChoosedSelectedItems).length;
  }

  moveAllItemsToLeft() {
    if (this.searchSelectedUsersTerms.length === 0) {
      this.availableItems = _.cloneDeep(this.availableItemsBackUp);
      this.availableItemsCopy = _.cloneDeep(this.availableItemsBackUp);
      this.selectedItems = [];
      this.selectedItemsCopy = [];
      this.selectChoosedItems = {};
      this.selectChoosedSelectedItems = {};
      this.selectChoosedItemsCount = 0;
      this.searchAvailableUsers();
      this.searchSelectedUsers();


    } else {
      this.selectChoosedSelectedItems = {};
      this.selectedItems.forEach((element) => {

        this.selectChoosedSelectedItems[element.user] = element;
      });
      this.moveItemToLeft();
    }
  }

  moveAllItemsToRight() {
    if (this.searchAvailableUsersTerms.length === 0) {
      this.selectedItems = _.cloneDeep(this.availableItemsBackUp);
      this.selectedItemsCopy = _.cloneDeep(this.availableItemsBackUp);
      this.availableItemsCopy = [];
      this.availableItems = [];
      this.availChoosedItems = {};
      this.availChoosedSelectedItems = {};
      this.availChoosedItemsCount = 0;
      this.searchAvailableUsers();
      this.searchSelectedUsers();
    } else {
      this.availChoosedSelectedItems = {};
      this.availableItems.forEach((element) => {
        this.availChoosedSelectedItems[element.user] = element;
      });
      this.moveItemToRight();
    }
  }

  moveItemToRight() {

    const selectedItemsCopy = this.selectedItemsCopy;
    const availableItemsCopy = this.availableItemsCopy;
    for (const choosedSelectedKey in this.availChoosedSelectedItems) {
      if (this.availChoosedSelectedItems.hasOwnProperty(choosedSelectedKey)) {
        selectedItemsCopy.push(this.availChoosedSelectedItems[choosedSelectedKey]);
        const filterIndex = availableItemsCopy.indexOf(this.availChoosedSelectedItems[choosedSelectedKey]);
        availableItemsCopy.splice(filterIndex, 1);
      }
    }

    this.availableItems = availableItemsCopy;
    if (this.searchAvailableUsersTerms.length !== 0) {
      this.searchAvailableUsers();
    }

    this.selectedItems = selectedItemsCopy;
    if (this.searchSelectedUsersTerms.length !== 0) {
      this.searchSelectedUsers();
    }

    this.availChoosedItems = {};
    this.availChoosedSelectedItems = {};
    this.availChoosedItemsCount = 0;
  }

  moveItemToLeft() {
    const selectedItemsCopy = this.selectedItemsCopy;
    const availableItemsCopy = this.availableItemsCopy;
    for (const choosedSelectedKey in this.selectChoosedSelectedItems) {
      if (this.selectChoosedSelectedItems.hasOwnProperty(choosedSelectedKey)) {
        availableItemsCopy.push(this.selectChoosedSelectedItems[choosedSelectedKey]);
        const filterIndex = selectedItemsCopy.indexOf(this.selectChoosedSelectedItems[choosedSelectedKey]);
        selectedItemsCopy.splice(filterIndex, 1);
      }
    }

    this.availableItems = availableItemsCopy;
    if (this.searchAvailableUsersTerms.length !== 0) {
      this.searchAvailableUsers();
    }

    this.selectedItems = selectedItemsCopy;
    if (this.searchSelectedUsersTerms.length !== 0) {
      this.searchSelectedUsers();
    }

    this.selectChoosedItems = {};
    this.selectChoosedSelectedItems = {};
    this.selectChoosedItemsCount = 0;
  }


  searchAvailableUsers() {
    const term = this.searchAvailableUsersTerms;
    this.availableItems = this.availableItemsCopy.filter(function (tag) {
      return tag.user.toLowerCase().indexOf(term.toLowerCase()) >= 0;
    });
  }

  searchSelectedUsers() {
    const term = this.searchSelectedUsersTerms;
    this.selectedItems = this.selectedItemsCopy.filter(function (tag) {
      return tag.user.toLowerCase().indexOf(term.toLowerCase()) >= 0;
    });
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
      this.successTitle = 'Role Created !!!';
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

  configureUsers() {
    this.loadingContent = 'configuring';
    this.selectedRoleName = this.highlightName;
    this.hideContent = true;
    this.roleLoader = true;
    this.isRoleCreationUpdationFailed = false;
    this.isRoleCreationUpdationSuccess = false;
    const userRoleConfigRequest = {};
    userRoleConfigRequest['roleId'] = this.roleId;
    const userId = [];
    userRoleConfigRequest['userDetails'] = this.selectedItems;
    const url = environment.configUserRolesAllocation.url;
    const method = environment.configUserRolesAllocation.method;
    this.adminService.executeHttpAction(url, method, userRoleConfigRequest, {}).subscribe(reponse => {
      this.successTitle = 'User Roles Configured !!!';
      this.isRoleCreationUpdationSuccess = true;
      this.roleLoader = false;
      this.roles = {
        roleName: '',
        description: '',
        writePermission: false
      };
    },
      error => {
        this.failedTitle = 'Roles Updation Failed !!!';
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
          this.pageTitle = 'Config Users';
          this.breadcrumbPresent = 'Config Users';
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
        const userNames = userRoleReponse[0].users.map(user => user.userId);
        this.allAllocatedUsers = _.uniq(userNames);
        this.getUsers();
        this.highlightName = userRoleReponse[0].roleName;
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
        this.highlightName = 'Role Details';
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
   // this.getUsers();
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
