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
import { environment } from './../../../../../../../environments/environment';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { UtilsService } from '../../../../../../shared/services/utils.service';
import { LoggerService } from '../../../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../../../shared/services/error-handling.service';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/pairwise';
import { WorkflowService } from '../../../../../../core/services/workflow.service';
import { RouterUtilityService } from '../../../../../../shared/services/router-utility.service';
import { AdminService } from '../../../../../services/all-admin.service';

@Component({
  selector: 'app-admin-create-update-domain',
  templateUrl: './create-update-domain.component.html',
  styleUrls: ['./create-update-domain.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,
    AdminService
  ]
})
export class CreateUpdateDomainComponent implements OnInit, OnDestroy {
  pageTitle = '';
  breadcrumbArray: any = ['Admin', 'Domains'];
  breadcrumbLinks: any = ['policies', 'domains'];
  breadcrumbPresent: any;
  outerArr: any = [];
  filters: any = [];
  domainName = '';
  isCreate = false;
  successTitle = '';
  failedTitle = '';
  successSubTitle = '';
  isDomainCreationUpdationFailed = false;
  isDomainCreationUpdationSuccess = false;
  loadingContent = '';
  domainId = '';

  isDomainNameValid: any = -1;
  paginatorSize = 25;
  isLastPage: boolean;
  isFirstPage: boolean;
  totalPages: number;
  pageNumber = 0;
  showLoader = true;
  domainLoader = false;
  errorMessage: any;

  domain: any = {
    name: '',
    desc: '',
    config: ''
  };

  hideContent = false;

  filterText: any = {};
  errorValue = 0;

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

  createDomain(domain) {
    domain.name = domain.name.trim();
    domain.desc = domain.desc.trim();
    domain.config = domain.config.trim();

    this.loadingContent = 'creating';
    this.hideContent = true;
    this.domainLoader = true;
    this.isDomainCreationUpdationFailed = false;
    this.isDomainCreationUpdationSuccess = false;
    const url = environment.createDomain.url;
    const method = environment.createDomain.method;
    this.domainName = domain.name;
    this.adminService.executeHttpAction(url, method, domain, {}).subscribe(reponse => {
      this.successTitle = 'Domain Created';
      this.isDomainCreationUpdationSuccess = true;
      this.domainLoader = false;
      this.domain.name = '';
      this.domain.desc = '';
      this.domain.config = '';
    },
      error => {
        this.failedTitle = 'Creation Failed';
        this.domainLoader = false;
        this.isDomainCreationUpdationFailed = true;
      });
  }

  updateDomain(domain) {
    domain.name = domain.name.trim();
    domain.desc = domain.desc.trim();
    domain.config = domain.config.trim();
    this.loadingContent = 'updating';
    this.hideContent = true;
    this.domainLoader = true;
    this.isDomainCreationUpdationFailed = false;
    this.isDomainCreationUpdationSuccess = false;
    const url = environment.updateDomain.url;
    const method = environment.updateDomain.method;
    this.domainName = domain.name;
    this.adminService.executeHttpAction(url, method, domain, {}).subscribe(reponse => {
      this.successTitle = 'Domain Updated';
      this.isDomainCreationUpdationSuccess = true;
      this.domainLoader = false;
      this.domain.name = '';
      this.domain.desc = '';
      this.domain.config = '';
    },
      error => {
        this.failedTitle = 'Updation Failed';
        this.domainLoader = false;
        this.isDomainCreationUpdationFailed = true;
      });
  }

  closeErrorMessage() {
    if (this.failedTitle === 'Loading Failed') {
      this.getDomainDetails(this.domainName);
    } else {
      this.hideContent = false;
    }
    this.isDomainCreationUpdationFailed = false;
    this.isDomainCreationUpdationSuccess = false;
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
        this.domainName = this.queryParamsWithoutFilter.domainName;
        delete this.queryParamsWithoutFilter['filter'];
        if (this.domainName) {
          this.pageTitle = 'Edit Domain';
          this.breadcrumbPresent = 'Edit Domain';
          this.isCreate = false;
          this.getDomainDetails(this.domainName);
        } else {
          this.pageTitle = 'Create New Domain';
          this.breadcrumbPresent = 'Create Domain';
          this.isCreate = true;
          this.getAllDomainNames();
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

  domainNames: any = [];
  isDomainNameAvailable(domainNameKeyword) {
    if (domainNameKeyword.trim().length == 0) {
      this.isDomainNameValid = -1;
    } else {
        let isKeywordExits = this.domainNames.findIndex(item => domainNameKeyword.trim().toLowerCase() === item.trim().toLowerCase());
        if (isKeywordExits === -1) {
          this.isDomainNameValid = 1;
        } else {
          this.isDomainNameValid = 0;
        }
    }
  }

  getAllDomainNames() {
    this.hideContent = true;
    this.domainLoader = true;
    this.loadingContent = 'loading';
    this.isDomainCreationUpdationFailed = false;
    this.isDomainCreationUpdationSuccess= false;
    const url = environment.getAllDomainNames.url;
    const method = environment.getAllDomainNames.method;
    this.adminService.executeHttpAction(url, method, {}, {domainName: this.domainName}).subscribe(reponse => {
      this.domainLoader = false;
      this.hideContent = false;
      this.domainNames =  reponse[0];
    },
      error => {
        this.loadingContent = 'loading';
        this.failedTitle = 'Loading Failed';
        this.domainLoader = false;
        this.isDomainCreationUpdationFailed = true;
      })
  }

  getDomainDetails(domainName) {
    this.hideContent = true;
    this.domainLoader = true;
    this.loadingContent = 'loading';
    this.isDomainCreationUpdationFailed = false;
    this.isDomainCreationUpdationSuccess= false;
    const url = environment.domainDetailsByName.url;
    const method = environment.domainDetailsByName.method;
    this.adminService.executeHttpAction(url, method, {}, {domainName: this.domainName}).subscribe(reponse => {
      this.domainLoader = false;
      this.hideContent = false;
      const domainDetails =  reponse[0];
      this.domain.name = domainDetails.domainName;
      this.domain.desc = domainDetails.domainDesc;
      this.domain.config = domainDetails.config;
    },
      error => {
        this.loadingContent = 'loading';
        this.failedTitle = 'Loading Failed';
        this.domainLoader = false;
        this.isDomainCreationUpdationFailed = true;
      })
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

  ngOnDestroy () {
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
