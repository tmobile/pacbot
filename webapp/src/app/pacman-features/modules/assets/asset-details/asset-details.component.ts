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

import { Component, OnInit, ElementRef, ViewChild, OnDestroy, AfterViewInit, HostListener } from '@angular/core';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { Subscription } from 'rxjs/Subscription';
import { environment } from './../../../../../environments/environment';
import { ActivatedRoute, Router } from '@angular/router';
import { DataCacheService } from '../../../../core/services/data-cache.service';
import { FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { PolicyViolationSummaryService } from '../../../services/policy-violation-summary.service';
import { CpuUtilizationService } from '../../../services/cpu-utilization.service';
import { DiskUtilizationService } from '../../../services/disk-utilization.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import {UtilsService} from '../../../../shared/services/utils.service';
import {RefactorFieldsService} from './../../../../shared/services/refactor-fields.service';
import {AssetCostService} from '../../../services/asset-cost.service';
import {WorkflowService} from '../../../../core/services/workflow.service';
import { HostVulnerabilitiesSummaryService } from '../../../services/host-vulnerabilities-summary.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { CONFIGURATIONS } from '../../../../../config/configurations';

@Component({
  selector: 'app-asset-details',
  templateUrl: './asset-details.component.html',
  styleUrls: ['./asset-details.component.css'],
  providers: [ CommonResponseService, PolicyViolationSummaryService, HostVulnerabilitiesSummaryService, CpuUtilizationService, DiskUtilizationService, AssetCostService]
})
export class AssetDetailsComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild('widgetCpu') widgetContainer: ElementRef;

  emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
  resourceId: string;
  resourceType: string;
  public decodedResourceId: string;
  public emailArray = [];
  public users;
  widgetWidth = 180;
  widgetHeight = 185;
  widgetWidth1 = 120;
  widgetHeight1 = 125;
  graphWidth: any;
  userEmail: FormGroup;
  public elementRef;
  public queryValue = '';
  public filteredList = [];
  public idDetailsName = [];
  widgetWidth2 = 500;
  widgetHeight2 = 270;
  policyValue = false;
  hostValue = false;
  private routeSubscription: Subscription;

  /*variables for breadcrumb data*/
  breadcrumbArray: any= ['Assets', 'Asset List'];
  breadcrumbLinks: any= ['asset-dashboard', 'asset-list'];
  breadcrumbPresent: any;

  filteredData = false;
  tagsVariable = false;
  buttonDisable = false;
  showCpuData = false;
  unCategorizedDataVisible = false;
  showUnCategorizedData = false;
  showIpAddress = false;
  hidePolicyViolations: boolean;
  hideHostVulnerabilities: boolean;
  hideOpenPorts: boolean;
  installedSoftwares: boolean;
  showOppositeEmail = false;
  showNone = false;
  showTransactionEmail = false;
  showLoadcompleteEmail = false;
  invalid = true;
  arrowkeyLocation = 0;
  checkEmail = false;
  complianceDropdowns: any = [];
  searchDropdownData: any = {};
  selectedDD = '';
  currentObj: any = {};
  filterArr: any = [];
  tagsArray: any= {};
  labels: any;
  dataObj: any= {};
  genericAttributes: any= {};
  showEmail = false;
  private subscriptionToAssetGroup: Subscription;
  private getPolicyDataSubscription: Subscription;
  private getHostDataSubscription: Subscription;
  private getCpuDataSubscription: Subscription;
  private getDiskDataSubscription: Subscription;
  private assetDetailsSubscription: Subscription;
  private awsNotificationSubscription: Subscription;
  private getEmailSubscription: Subscription;
  private getSummaryData: Subscription;
  private assetCostSubscription: Subscription;
  private accessGroupSubscription: Subscription;
  private getUserSubscription: Subscription;
  selectedAssetGroup: string;
  selectedComplianceDropdown: any;

  policyData: any;
  hostData: any;
  cpuData: any;
  diskData: any;
  detailsData: any;
  systemInfo: any;
  relatedAssets: any;
  awsDetailsData: any;
  installedSoftwaresData: any;
  assetSummaryData: any;
  costData: any;
  accessGroupData: any;

  policyAvailable: any = [false, false, false, false];
  showLoader: any = [false, false, false, false, false, false, false, false, false, false, false, false];
  hideContainer: any= [false, false, false, false, false, false, false, false];
  summary: any= {
    'violation': false,
    'vulnerabilities': false
  };
  errorMessage: any = [];
  public targetType: any = '';
  strokeColor = '#fff';
  innerRadious: any = 65;
  outerRadious: any = 50;
  innerRadious1 = 60;
  outerRadious1 = 47;
  colorSetCpu = ['#26ba9d', '#26ba9d', '#645ec5'];
  MainTextcolor = '#000';
  filterText: any = {};
  private urlParams: any;
  urlToRedirect: any = '';
  private previousUrl: any = '';
  public pageLevel = 0;
  public backButtonRequired;
  configurations;

  @HostListener('document:click', ['$event']) handleClick(event) {
    let clickedComponent = event.target;
     let inside = false;
     do {
         if (clickedComponent === this.elementRef.nativeElement) {
             inside = true;
         }
        clickedComponent = clickedComponent.parentNode;
     } while (clickedComponent);
      if (!inside) {
          this.filteredList = [];
      }
  }

  @HostListener('window:resize', ['$event']) onResize(event) {
    const element_cpuUtilization = document.getElementById('cpuUtilization');
    if (element_cpuUtilization) {
        this.widgetWidth2 = parseInt((window.getComputedStyle(element_cpuUtilization, null).getPropertyValue('width')).split('px')[0], 10);
    }
    const element_statsDoughnut = document.getElementById('statsDoughnut');
    if (element_statsDoughnut) {
        let widthValue = parseInt((window.getComputedStyle(element_statsDoughnut, null).getPropertyValue('width')).split('px')[0], 10);
        widthValue = widthValue - 155;
        if (widthValue > 150) {
          this.widgetWidth = widthValue;
        }
    }
  }

  constructor(private assetGroupObservableService: AssetGroupObservableService,
              private activatedRoute: ActivatedRoute,
              private commonResponseService: CommonResponseService,
              private router: Router,
              private utilityService: UtilsService,
              private dataStore: DataCacheService,
              private policyViolationSummaryService: PolicyViolationSummaryService,
              private cpuUtilizationService: CpuUtilizationService,
              private diskUtilizationService: DiskUtilizationService,
              private errorHandling: ErrorHandlingService,
              private assetCostService: AssetCostService,
              private refactorFieldsService: RefactorFieldsService ,
              private workflowService: WorkflowService,
              private hostVulnerabilitiesSummaryService: HostVulnerabilitiesSummaryService,
              private loggerService: LoggerService,
              myElement: ElementRef) {

                this.configurations = CONFIGURATIONS;

                this.elementRef = myElement;
                this.getAssetGroup();
              }

    ngAfterViewInit() {
      try {
        setTimeout(() => {
          this.widgetWidth2 = parseInt((window.getComputedStyle(this.widgetContainer.nativeElement, null).getPropertyValue('width')).split('px')[0], 10);
        }, 0);
      } catch (error) {
          this.errorMessage[2] = this.errorHandling.handleJavascriptError(error);
      }
    }

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.breadcrumbPresent = 'Asset Details';
    this.userEmail = new FormGroup({
      ename: new FormControl('', [Validators.required, Validators.minLength(6)])
    });
  }

  getUsers(): any {
    const userUrl = environment.users.url;
    const userMethod = environment.users.method;
    const queryparams = {};
    this.getUserSubscription = this.commonResponseService.getData(userUrl, userMethod, {}, queryparams).subscribe(
        response => {
          this.users = response.values;
          for (let i = 0; i < this.users.length; i++) {
            const userdetails = this.users[i].displayName + ' ' + '(' + this.users[i].userEmail + ')';
            this.idDetailsName.push(userdetails);
          }
        },
        error => {
          this.loggerService.log('error', error);
        });
  }

  getAllData() {
    this.getUsers();
    this.getAssetSummary();
    this.getPolicyData();
    this.getHostData();
    this.getcpuData();
    this.getdiskData();
    this.getAssetDetailsData();
    this.getAwsNotificationData();
    this.getAssetCostData();
    this.getAccessGroupData();
  }

  updateComponent() {
    try {
      this.getAllData();
      this.assetSummaryData = undefined;
      this.awsDetailsData = undefined;
      this.costData = undefined;
      this.accessGroupData = undefined;
      this.dataObj.Creators = undefined;
      this.dataObj['AWS Metadata'] = undefined;
      this.dataObj['IP Address'] = undefined;
      this.showLoader = [false, false, false, false, false, false, false, false, false, false, false, false];
      this.policyAvailable = [false, false, false, false];
      this.summary = {
        'violation': false,
        'vulnerabilities': false
      };
      this.filteredData = false;
      this.tagsVariable = false;
      this.hostValue = false;
      this.policyValue = false;
      this.hideContainer = [false, false, false, false, false, false, false, false];
      this.unCategorizedDataVisible = false;
      this.showUnCategorizedData = false;
      this.showIpAddress = false;
      this.hidePolicyViolations = true;
      this.hideHostVulnerabilities = true;
      this.hideOpenPorts = true;
      this.installedSoftwares = true;
      this.invalid = true;
      this.checkEmail = false;
      setTimeout(() => {
        this.hidePolicyViolations = false;
        this.hideHostVulnerabilities = false;
        this.hideOpenPorts = false;
        this.installedSoftwares = false;
      }, 10);
    if (this.activatedRoute.snapshot.queryParams) {
      this.filterText = this.activatedRoute.snapshot.queryParams; /* <-- filterText is used to hit the api filter object */
      }
    } catch (error) {
      this.loggerService.log('errro', 'js error - ' + error);
    }
  }
  getRuleId() {
    /*
    * this funtion stores the URL params
    */
    this.routeSubscription = this.activatedRoute.params.subscribe(params => {
        this.urlParams = params; // <<-- This urlParams is used while calling the api
        this.resourceId = this.urlParams.resourceId; // Encoded resource id is used everywhere to pass to api's
        this.decodedResourceId = decodeURIComponent(this.resourceId); // This is used only for Title of the page
        this.resourceType = this.urlParams.resourceType;
        this.updateComponent();
    });
  }

  getAssetGroup() {
    this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
          this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(this.pageLevel);
          this.selectedAssetGroup = assetGroupName;
          this.dataStore.setCurrentSelectedAssetGroup(this.selectedAssetGroup);
          this.getRuleId();
    });
  }

  replaceUrl(url) {
    let replacedUrl = url.replace('{resourceId}', this.resourceId.toString());
    /*
     * Added by Trinanjan on 01/03/2018
     * When user navigates in the same page ,Only the router subscription gets called not the asset subscription
     * so assetgroup was going empty
     * Now asset group is taken from the localstorage when only router subscription is called
     */
    this.selectedAssetGroup = this.dataStore.getCurrentSelectedAssetGroup();
    replacedUrl = replacedUrl.replace('{assetGroup}', this.selectedAssetGroup.toString());
    replacedUrl = replacedUrl.replace('{resourceType}', this.resourceType.toString());
    return replacedUrl;
  }

  getAssetSummary() {
    const url = environment.assetSummary.url;
    const method = environment.assetSummary.method;
    const newUrl = this.replaceUrl(url);
    this.getSummaryData = this.commonResponseService.getData(newUrl, method, {}, {}).subscribe(
      response => {
        try {
            if (response.attributes.length > 0) {
              this.assetSummaryData = response.attributes;
              this.showLoader[4] = true;
            } else {
              this.hideContainer[0] = true;
              this.showLoader[4] = true;
            }
        } catch (error) {
            this.showLoader[4] = false;
            this.hideContainer[0] = true;
        }
      },
      error => {
        this.hideContainer[0] = true;
        this.errorMessage[0] = 'apiResponseError';
      }
    );
  }

  getAwsNotificationData() {
    const url = environment.awsNotifications.url;
    const method = environment.awsNotifications.method;
    const newUrl = this.replaceUrl(url);
    this.awsNotificationSubscription = this.commonResponseService.getData(newUrl, method, {}, {}).subscribe(
      response => {
        try {
          if (response.distribution.length > 0) {
            this.awsDetailsData = response.distribution;
            this.showLoader[5] = true;
          } else {
            this.hideContainer[1] = true;
            this.showLoader[5] = true;
          }
        } catch (error) {
            this.showLoader[5] = false;
            this.hideContainer[1] = true;
        }
      },
      error => {
        this.hideContainer[1] = true;
        this.errorMessage[0] = 'apiResponseError';
      }
    );

  }

  getAssetDetailsData() {
    const url = environment.assetDetails.url;
    const method = environment.assetDetails.method;
    const newUrl = this.replaceUrl(url);
    // check if this is fine
    this.assetDetailsSubscription = this.commonResponseService.getData(newUrl, method, {}, {}).subscribe(
      response => {
        try {
            this.detailsData = response;
            if (response.attributes.length > 0) {
              const attributes = response.attributes;
              const refactoredService = this.refactorFieldsService;
              const formattedAttributes = attributes.map(function(attribute){
                  attribute.name = refactoredService.getDisplayNameForAKey(attribute.name) || attribute.name;
                  return attribute;
              });
              this.relatedData(formattedAttributes);
              this.showLoader[6] = true;
              this.showLoader[7] = true;
              this.showLoader[9] = true;
            } else {
              this.hideContainer[2] = true;
              this.hideContainer[3] = true;
              this.hideContainer[4] = true;
              this.hideContainer[5] = true;
              this.showLoader[6] = true;
              this.showLoader[7] = true;
              this.showLoader[9] = true;
            }
            const keys = Object.keys(response.tags);
            if (keys.length > 0) {
              this.tagsData(response.tags);
              this.showLoader[8] = true;
            } else {
              this.showLoader[8] = true;
              this.hideContainer[4] = true;
            }
        } catch (error) {
             this.hideContainer[2] = true;
             this.hideContainer[3] = true;
             this.hideContainer[4] = true;
             this.hideContainer[5] = true;
             this.showLoader[6] = true;
             this.showLoader[7] = true;
             this.showLoader[8] = true;
             this.showLoader[9] = true;
        }
      },
      error => {
        this.showLoader[6] = true;
        this.showLoader[7] = true;
        this.showLoader[9] = true;
        this.hideContainer[2] = true;
        this.hideContainer[3] = true;
        this.hideContainer[4] = true;
        this.hideContainer[5] = true;
        this.errorMessage[0] = 'apiResponseError';
      }
    );
  }

  relatedData(data) {
    this.dataObj = this.filterAttributesByCategories(data);
    this.genericAttributes = this.filterGenericAttributes(this.dataObj);

    if (Object.keys(this.genericAttributes).length > 0) {
      this.filteredData = true;
    }
  }

  /* Function for filtering the raw data by categories*/

  filterAttributesByCategories(data) {
    const categorisedData = {};
      for (let i = 0; i < data.length; i++) {
          if (categorisedData[data[i].category]) {
              categorisedData[data[i].category].push(data[i]);
          } else {
              categorisedData[data[i].category] = [data[i]];
          }
      }
      return categorisedData;
  }

  /* Function for removing the RHS data from the dataObj in order to show it in the LHS
     main block*/

  filterGenericAttributes(data) {
    const genericAttributes = JSON.parse(JSON.stringify(data));
    if (genericAttributes.hasOwnProperty('AWS Metadata')) {
      delete genericAttributes[`AWS Metadata`];
    } else {
      this.hideContainer[3] = true;
    }
    if (genericAttributes.hasOwnProperty('Creators')) {
      delete genericAttributes[`Creators`];
    } else {
      this.hideContainer[2] = true;
    }

    if (genericAttributes.hasOwnProperty('IP Address')) {
      delete genericAttributes[`IP Address`];
    } else {
      this.hideContainer[5] = true;
    }
    return genericAttributes;
  }

  tagsData(data) {
    this.tagsArray = data;
    this.tagsVariable = true;
  }


  getPolicyData() {
    const url = environment.policyViolationGraph.url;
    const method = environment.policyViolationGraph.method;
    const newUrl = this.replaceUrl(url);
    this.getPolicyDataSubscription = this.policyViolationSummaryService.getData(newUrl, method).subscribe(
      response => {
        try {
            if (response !== undefined) {
              if (response.totalCount !== 0) {
                this.policyValue = false;
                this.policyData = response;
                this.policyAvailable[0] = true;
                this.showLoader[0] = true;
              } else {
                this.policyValue = false;
                this.showLoader[0] = true;
                this.summary.violation = true;
              }
            } else {
              this.policyValue = true;
            }
        } catch (error) {
            this.policyValue = true;
        }
      },
      error => {
        this.showLoader[0] = true;
        this.policyAvailable[0] = false;
        this.policyValue = true;
        this.errorMessage[0] = 'apiResponseError';
      }
    );
  }

  getHostData() {
    const url = environment.hostVulnerabilitiesGraph.url;
    const method = environment.hostVulnerabilitiesGraph.method;
    const newUrl = this.replaceUrl(url);
    this.getHostDataSubscription = this.hostVulnerabilitiesSummaryService.getData(newUrl, method).subscribe(
      response => {
        try {
            if (!this.utilityService.checkIfAPIReturnedDataIsEmpty(response) && (response.totalCount !== 0)) {
                this.hostValue = false;
                this.hostData = response;
                this.policyAvailable[1] = true;
                this.showLoader[1] = true;
            } else {
              this.hostValue = true;
              this.policyAvailable[1] = false;
            }
        } catch (error) {
            this.hostValue = true;
        }
      },
      error => {
        this.showLoader[1] = true;
        this.policyAvailable[1] = false;
        this.hostValue = true;
        this.errorMessage[1] = 'apiResponseError';
      }
    );
  }

  getcpuData() {
    this.cpuData = [];
    const url = environment.cpuUtilizationGraph.url;
    const method = environment.cpuUtilizationGraph.method;
    const newUrl = this.replaceUrl(url);
    this.getCpuDataSubscription = this.cpuUtilizationService.getData(newUrl, method).subscribe(
      response => {
        try {
            if (response[0].values.length > 0) {
              this.showCpuData = false;
              this.cpuData = response;
              this.policyAvailable[2] = true;
              this.showLoader[2] = true;
            } else {
              this.showCpuData = true;
            }
        } catch (error) {
            this.showCpuData = true;
        }
      },
      error => {
        this.showLoader[2] = true;
        this.policyAvailable[2] = false;
        this.showCpuData = true;
        this.errorMessage[2] = 'apiResponseError';
      }
    );
  }

  getdiskData() {
    this.diskData = undefined;
    const url = environment.diskUtilizationGraph.url;
    const method = environment.diskUtilizationGraph.method;
    const newUrl = this.replaceUrl(url);
    this.getDiskDataSubscription = this.diskUtilizationService.getData(newUrl, method).subscribe(
      response => {
        try {
            if (response[0].values.length > 0) {
              this.diskData = response[0].values;
              this.policyAvailable[3] = true;
            }
        } catch (error) {
            this.loggerService.log('error', error);
        }
      },
      error => {
        this.showLoader[3] = true;
        this.policyAvailable[3] = false;
        this.errorMessage[3] = 'apiResponseError';
      }
    );
  }

  getAssetCostData() {
    const url = environment.assetCost.url;
    const method = environment.assetCost.method;
    const newUrl = this.replaceUrl(url);
    this.assetCostSubscription = this.assetCostService.getData(newUrl, method).subscribe(
      response => {
        try {
          if (response.length > 0) {
            this.costData = response;
            this.showLoader[10] = true;
          } else {
            this.hideContainer[6] = true;
            this.showLoader[10] = true;
          }
        } catch (error) {
            this.showLoader[10] = false;
            this.hideContainer[6] = true;
        }
      },
      error => {
        this.hideContainer[6] = true;
        this.errorMessage[0] = 'apiResponseError';
      }
    );
  }

  getAccessGroupData() {
    const url = environment.accessGroup.url;
    const method = environment.accessGroup.method;
    const newUrl = this.replaceUrl(url);
    const payload = {};
    const queryParams = {};
      this.accessGroupSubscription = this.commonResponseService.getData(newUrl, method, payload, queryParams).subscribe(
        response => {
          try {
            if (response.length > 0) {
              this.accessGroupData = response;
              this.showLoader[11] = true;
            } else {
              this.hideContainer[7] = true;
              this.showLoader[11] = true;
            }
          } catch (error) {
              this.showLoader[11] = false;
              this.hideContainer[7] = true;
          }
        },
        error => {
          this.hideContainer[7] = true;
          this.errorMessage[0] = 'apiResponseError';
        }
      );
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.loggerService.log('error', error);
    }
  }

  /**
   * This function navigates the page mentioned in the routeTo variable with a querypareams
   */

  navigateDataTable(event) {
    this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
    try {
      const queryObj = event;
      // the api has to accept resoueceId and resourcetype ,,,,,,not accepting now
      const eachParams = { 'severity.keyword' : queryObj.toLowerCase(), '_resourceid.keyword': this.resourceId , 'targetType.keyword' : this.resourceType};
      const newParams = this.utilityService.makeFilterObj(eachParams);
      if ( (queryObj !== undefined) ) {
        this.router.navigate(['../../../../', 'compliance', 'issue-listing'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.loggerService.log('error', error);
    }
  }

  navigateToVulnerabilitiesList(event) {
    try {
      if (!event) {
        return;
      }
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      const queryObj = this.utilityService.extractNumbersFromString(event);
      const eachParams = { 'severitylevel' : queryObj, '_resourceid.keyword': this.resourceId};
      const newParams = this.utilityService.makeFilterObj(eachParams);
      if ((queryObj !== undefined)) {
        this.router.navigate(['../../../../', 'compliance', 'vulnerabilities'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.loggerService.log('error', error);
    }
  }

/* navigatePage, navigateDataTable function ends here */

  sendEmail() {
      this.showOppositeEmail = !this.showOppositeEmail;
      this.showNone = !this.showNone;
      if (this.showOppositeEmail === false) {
        this.showTransactionEmail = false;
        this.showLoadcompleteEmail = false;
        this.filteredList = [];
        this.queryValue = '';
      }
  }

  postEmail(emailArrayList) {
    const locationValue = window.location.href;
    const emailUrl = environment.email.url;
    const emailMethod = environment.email.method;
    const payload = {
      'attachmentUrl': this.configurations.optional.assetDetails.ASSET_DETAILS_TEMPLATE_URL,
      'from': this.configurations.optional.assetDetails.ASSET_DETAILS_FROM_ID,
      'mailTemplateUrl': this.configurations.optional.assetDetails.ASSET_DETAILS_TEMPLATE_URL,
      'placeholderValues': {'link': locationValue, 'resourceId': this.decodedResourceId, 'targetType': this.resourceType},
      'subject': 'Asset Details',
      'to': emailArrayList
    };
    this.getEmailSubscription = this.commonResponseService.getData(
      emailUrl,
      emailMethod,
      payload,
      {},
      {
        responseType: 'text'
      }
    ).subscribe(
      response => {
        this.checkEmail = true;
        this.showLoadcompleteEmail = true;
      },
      error => {
        this.checkEmail = false;
      });
  }

  onSubmitemail() {
    try {
      if (this.emailArray.length < 1 && this.queryValue.length < 0) {
        this.invalid = false;
        return;
      } else {
        this.invalid = true;
        if (this.queryValue.length > 0) {
          if (this.validateEmailInput(this.queryValue)) {
            this.emailArray.push(this.queryValue);
          } else {
            this.invalid = false;
            return;
          }
        }
      }

      this.showTransactionEmail = true;

      this.postEmail(this.emailArray);

      this.emailArray = [];
      this.userEmail.reset();
    } catch (e) {
      this.loggerService.log('error', e);
    }
  }


  removeData(index): any {
    this.emailArray.splice(index, 1);
    if (this.emailArray.length < 1) {
      this.invalid = false;
    } else {
      this.invalid = true;
    }
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
      this.loggerService.log('error', e);
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
          if (this.filteredList.length > 0) {
            this.queryValue = this.filteredList[this.arrowkeyLocation];
            this.filteredList = [];
            this.queryValue = this.retrieveEmailFromSelectedItem(this.queryValue);
            this.emailArray.push(this.queryValue);
          } else if (this.queryValue.length > 0) {
              if (this.validateEmailInput(this.queryValue)) {
                this.emailArray.push(this.queryValue);
              }
          }
          this.queryValue = '';
          if (this.emailArray.length < 1) {
            this.invalid = false;
          } else {
            this.invalid = true;
          }
      }
    } catch (e) {
      this.loggerService.log('error', e);
    }
  }

  keyEvent(event: KeyboardEvent, item) {
    try {
      switch (event.keyCode) {
        case 13: // this is the ascii of enter
          this.queryValue = item;
          this.filteredList = [];
          item = this.retrieveEmailFromSelectedItem(item);
          this.emailArray.push(item);
          this.queryValue = '';
          if (this.emailArray.length < 1) {
            this.invalid = false;
          } else {
            this.invalid = true;
          }
      }
    } catch (e) {
      this.loggerService.log('error', e);
    }
  }


  select(item) {
    try {
      this.queryValue = item;
      this.filteredList = [];
      item = this.retrieveEmailFromSelectedItem(this.queryValue);
      this.emailArray.push(item);
      this.queryValue = '';
      if (this.emailArray.length < 1) {
        this.invalid = false;
      } else {
        this.invalid = true;
      }
    } catch (e) {
      this.loggerService.log('error', e);
    }
  }

  // function to check whether input is matching email pattern
  validateEmailInput(inputValue) {
    if (!this.emailPattern.test(inputValue)) {
      return false;
    }
    return true;
  }

  // function to retrieve email id from selected list user item
  retrieveEmailFromSelectedItem(selectedItem) {
    return selectedItem.split(' (')[1].replace(')', '');
  }



  navigateToAWSNotifications(status) {
    this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
    const eachParams = {'eventstatus': status, '_resourceid': this.resourceId};
    this.router.navigate(['../../../../compliance/health-notifications'],
    { relativeTo: this.activatedRoute, queryParams: this.utilityService.makeFilterObj(eachParams), queryParamsHandling: 'merge'});
  }

  ngOnDestroy() {
    try {
       if (this.subscriptionToAssetGroup) {
        this.subscriptionToAssetGroup.unsubscribe();
      }
      if (this.getPolicyDataSubscription) {
        this.getPolicyDataSubscription.unsubscribe();
      }
      if (this.getHostDataSubscription) {
        this.getHostDataSubscription.unsubscribe();
      }
      if (this.getCpuDataSubscription) {
        this.getCpuDataSubscription.unsubscribe();
      }
      if (this.getDiskDataSubscription) {
        this.getDiskDataSubscription.unsubscribe();
      }
      if (this.assetDetailsSubscription) {
        this.assetDetailsSubscription.unsubscribe();
      }
      if (this.awsNotificationSubscription) {
        this.awsNotificationSubscription.unsubscribe();
      }
      if (this.getEmailSubscription) {
        this.getEmailSubscription.unsubscribe();
      }
      if (this.getSummaryData) {
        this.getSummaryData.unsubscribe();
      }
      if (this.assetCostSubscription) {
        this.assetCostSubscription.unsubscribe();
      }
      if (this.accessGroupSubscription) {
        this.accessGroupSubscription.unsubscribe();
      }
      if (this.getUserSubscription) {
        this.getUserSubscription.unsubscribe();
      }
    } catch (error) {
      this.loggerService.log('error', '--- Error while unsubscribing ---');
    }
  }

}
