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

import { Component, OnInit, OnDestroy, ViewChild, trigger, state, style, transition, animate } from '@angular/core';
import { environment } from './../../../../../../environments/environment';

import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import * as _ from 'lodash';
import * as frLocale from 'date-fns/locale/en';
import {WorkflowService} from '../../../../../core/services/workflow.service';
import * as moment from 'moment';
import { UtilsService } from '../../../../../shared/services/utils.service';
import { LoggerService } from '../../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../../shared/services/error-handling.service';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/pairwise';
import { RouterUtilityService } from '../../../../../shared/services/router-utility.service';
import { AdminService } from '../../../../services/all-admin.service';
import {  FormGroup, FormControl, Validators } from '@angular/forms';
import { SelectComponent } from 'ng2-select';
import { UploadFileService } from '../../../../services/upload-file-service';
import { DatepickerOptions } from 'ng2-datepicker';

@Component({
  selector: 'app-admin-create-sticky-exceptions',
  templateUrl: './create-sticky-exceptions.component.html',
  styleUrls: ['./create-sticky-exceptions.component.css'],
  animations: [
    trigger('slideInOut', [
      state('in', style({
        transform: 'translate3d(0, 0, 0)'
      })),
      state('out', style({
        transform: 'translate3d(100%, 0, 0)'
      })),
      transition('in => out', animate('400ms ease-in-out')),
      transition('out => in', animate('400ms ease-in-out'))
    ]),
    trigger('fadeInOut', [
      state('open', style({ 'z-index': 2, opacity: 1 })),
      state('closed', style({ 'z-index': -1, opacity: 0 })),
      transition('open <=> closed', animate('500ms')),
    ])
  ],
  providers: [
    LoggerService,
    ErrorHandlingService,
    UploadFileService,
    AdminService
  ]
})
export class CreateStickyExceptionsComponent implements OnInit, OnDestroy {
  @ViewChild('targetTypeRuleSelect') targetTypeRuleSelectComponent: SelectComponent;
  pageTitle: String = '';
  breadcrumbArray: any = ['Admin', 'Sticky Exceptions'];
  breadcrumbLinks: any = ['policies', 'sticky-exceptions'];
  breadcrumbPresent: any;
  outerArr: any = [];
  filters: any = [];
  date = new Date();
  dateToday = this.date.getFullYear() + '-' + (this.date.getMonth() + 1) + '-' + this.date.getDate();
  exceptionDetailsForm: any = {
    name: '',
    reason: '',
    expiry: this.dateToday,
    assetGroup: []
  }

  isExceptionNameValid: any = -1;
  allAssetGroupNames: Array<string>;
  selectedAssetGroup: string;
  dataForm: FormGroup;
  user: FormGroup;

  assetLoaderTitle: string = '';
  assetLoader: boolean = false;
  assetLoaderFailure: boolean = false;
  attributeName: any = [];
  attributeValue: string = '';
  selectedRules: any = [];

  allOptionalRuleParams: any = [];
  isRuleInvokeFailed: boolean = false;
  isRuleInvokeSuccess: boolean = false;
  ruleContentLoader: boolean = true;
  ruleLoader: boolean = false;
  invocationId: String = '';
  paginatorSize: number = 25;
  isLastPage: boolean;
  isFirstPage: boolean;
  totalPages: number;
  pageNumber: number = 0;
  showLoader: boolean = true;
  errorMessage: any;
  searchTerm: String = '';

  hideContent: boolean = false;
  pageContent: any = [
    { title: 'Enter Exception Details', hide: false },
    { title: 'Exempt Target Types', hide: true }
  ];

  options: DatepickerOptions = {
    minYear: this.date.getFullYear(),
    maxYear: 2030,
    displayFormat: 'DD/MM/YYYY',
    barTitleFormat: 'DD/MM/YYYY',
    firstCalendarDay: 0, 
    locale: frLocale
  };

  isCreate: boolean = false;
  successTitle: String = '';
  failedTitle: string = '';
  successSubTitle: String = '';
  isAssetGroupExceptionCreationUpdationFailed: boolean = false;
  isAssetGroupExceptionCreationUpdationSuccess: boolean = false;
  loadingContent: string = '';
  assetGroupExceptionLoader: boolean = false;
  highlightName: string = '';

  availChoosedItems: any = {};
  availChoosedSelectedItems = {};
  availChoosedItemsCount = 0;

  selectChoosedItems: any = {};
  selectChoosedSelectedItems = {};
  selectChoosedItemsCount = 0;

  availableItems: any = [];
  selectedItems: any = [];

  availableItemsBackUp: any = [];
  selectedItemsBackUp: any = [];

  availableItemsCopy: any = [];
  selectedItemsCopy: any = [];

  searchSelectedDomainTerms: any = '';
  searchAvailableDomainTerms = '';


  // Target Details //
  availTdChoosedItems: any = {};
  availTdChoosedSelectedItems = {};
  availTdChoosedItemsCount = 0;

  selectTdChoosedItems: any = {};
  selectTdChoosedSelectedItems = {};
  selectTdChoosedItemsCount = 0;

  availableTdItems: any = [];
  selectedTdItems: any = [];

  availableTdItemsBackUp: any = [];
  selectedTdItemsBackUp: any = [];

  availableTdItemsCopy: any = [];
  selectedTdItemsCopy: any = [];

  searchSelectedTargetTerms: any = '';
  searchAvailableTargetTerms = '';

  stepIndex: number = 0;
  stepTitle: any = this.pageContent[this.stepIndex].title;
  allAttributeDetails: any = [];
  allAttributeDetailsCopy: any = [];

  filterText: any = {};
  errorValue: number = 0;
  urlID: string = '';
  exceptionName: string = '';

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
    private activatedRoute: ActivatedRoute,
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
    this.expiryDate = moment(new Date()).format('DD/MM/YYYY');
    this.user = new FormGroup({
      name: new FormControl(moment('2018-07-14', 'YYYY-MM-DD').toDate(), [Validators.required, Validators.minLength(1)])
    });
  }

  state: string = 'closed';
  menuState: string = 'out';
  closeAttributeConfigure() {
    this.state = 'closed';
    this.menuState = 'out';
    this.searchAttribute();
  }
  selectedIndex: number = -1;
  selectedAllRules: Array<string> = [];

  openAttributeConfigure(attributeDetail, index) {
    this.attributeValue = '';
    this.attributeName = [];
    this.state = 'open';
    this.menuState = 'in';
    this.selectedAllRules = attributeDetail.allRules;
    this.selectedRules = attributeDetail.rules;
    this.selectedIndex = index;

    attributeDetail.rules;
    if(attributeDetail.allRules.length === 0) {
      this.targetTypeRuleSelectComponent.placeholder = 'No Rules Available';
    } else {
      this.targetTypeRuleSelectComponent.items = attributeDetail.allRules;
      this.targetTypeRuleSelectComponent.placeholder = 'Select Rule Name';
    }
  }

  addAttributes(attributeName, attributeValue) {
    let ruleDetails = _.find(this.allAttributeDetails[this.selectedIndex].allRules, { id: attributeName[0].id });
    this.allAttributeDetails[this.selectedIndex].rules.push(ruleDetails);
    let itemIndex = this.allAttributeDetails[this.selectedIndex].allRules.indexOf(ruleDetails);
    if (itemIndex !== -1) {
      this.allAttributeDetails[this.selectedIndex].allRules.splice(itemIndex, 1);
      this.selectedAllRules = this.allAttributeDetails[this.selectedIndex].allRules;
      this.targetTypeRuleSelectComponent.items = this.selectedAllRules;
    }
    this.attributeValue = '';
    this.attributeName = [];
    if(this.allAttributeDetails[this.selectedIndex].allRules.length === 0) {
      this.targetTypeRuleSelectComponent.placeholder = 'No Rules Available';
    } else {
      this.targetTypeRuleSelectComponent.placeholder = 'Select Rule Name';
    }
  }

  deleteAttributes(attributeName, itemIndex) {
    let ruleDetails = this.allAttributeDetails[this.selectedIndex].rules[itemIndex];
    this.allAttributeDetails[this.selectedIndex].rules.splice(itemIndex, 1);
    if (itemIndex !== -1) {
      this.allAttributeDetails[this.selectedIndex].allRules.push(ruleDetails);
      this.selectedAllRules = this.allAttributeDetails[this.selectedIndex].allRules;
      this.targetTypeRuleSelectComponent.items = this.selectedAllRules;
    }

    if(this.allAttributeDetails[this.selectedIndex].allRules.length === 0) {
      this.targetTypeRuleSelectComponent.placeholder = 'No Rules Available';
    } else {
      this.targetTypeRuleSelectComponent.placeholder = 'Select Rule Name';
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
      }

    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  exceptionNames: any = [];
  isExceptionNameAvailable(exceptionNameKeyword) {
    if (exceptionNameKeyword.trim().length == 0) {
      this.isExceptionNameValid = -1;
    } else {
        let isKeywordExits = this.exceptionNames.findIndex(item => exceptionNameKeyword.trim().toLowerCase() === item.trim().toLowerCase());
        if (isKeywordExits === -1) {
          this.isExceptionNameValid = 1;
        } else {
          this.isExceptionNameValid = 0;
        }
    }
  }

  getAllExceptionNames() {
    const url = environment.getAllStickyExceptionNames.url;
    const method = environment.getAllStickyExceptionNames.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.pageContent[0].hide = false;
      this.exceptionNames = reponse[0];
      this.assetLoader = false;
    },
      error => {
        this.assetLoaderFailure = true;
        this.allAssetGroupNames = [];
        this.errorMessage = 'apiResponseError';
        this.assetLoader = false;
        this.showLoader = false;
      })
  }

  getAllAssetGroupNames() {
    this.assetLoader = true;
    this.pageContent[0].hide = true;
    this.assetLoaderFailure = false;
    var url = environment.assetGroupNames.url;
    var method = environment.assetGroupNames.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.allAssetGroupNames = reponse[0];
      this.getAllExceptionNames();
    },
      error => {
        this.assetLoaderFailure = true;
        this.allAssetGroupNames = [];
        this.errorMessage = 'apiResponseError';
        this.assetLoader = false;
        this.showLoader = false;
      });
  }

  stickyExceptionDetails: any;
  getAllStickyExceptionDetails(exceptionName) {
    this.assetLoaderFailure = false;
    this.assetLoader = true;
    this.pageContent[0].hide = true;
    this.assetLoaderTitle = this.exceptionName;
    var url = environment.getAllStickyExceptionDetails.url;
    var method = environment.getAllStickyExceptionDetails.method;
    this.adminService.executeHttpAction(url, method, {}, {exceptionName: exceptionName, dataSource: 'aws'}).subscribe(reponse => {
      this.assetLoader = false;
      this.pageContent[0].hide = false;
      this.stickyExceptionDetails = reponse[0];
     
      this.exceptionDetailsForm = {
        name: reponse[0].exceptionName,
        reason: reponse[0].exceptionReason,
        expiry: moment(reponse[0].expiryDate).format('YYYY-MM-DD') ,
        assetGroup: [{text: reponse[0].groupName, id: reponse[0].groupName}]
      }
      this.selectedAssetGroup = reponse[0].groupName;
    },
      error => {
        this.assetLoaderFailure = true;
        this.allAssetGroupNames = [];
        this.errorMessage = 'apiResponseError';
        this.assetLoader = false;
        this.showLoader = false;
      });
  }

  private collectTargetTypes() {
    this.assetLoaderFailure = false;
    this.assetLoader = true;
    this.assetLoaderTitle = 'Target Types';
    this.pageContent[0].hide = true;
    let url = environment.getTargetTypesByAssetGroupName.url;
    let method = environment.getTargetTypesByAssetGroupName.method;
    let assetGroupName = '';
    if (this.exceptionDetailsForm.assetGroup.length > 0) {
      assetGroupName = this.exceptionDetailsForm.assetGroup[0].text;
    }
    this.adminService.executeHttpAction(url, method, {}, { assetGroupName: assetGroupName }).subscribe(reponse => {
      this.assetLoader = false;
      this.showLoader = false;
      if (reponse.length > 0) {
        reponse[0].sort(function(a, b){
          return b.rules.length - a.rules.length;
        });
        reponse[0] = _.orderBy(reponse[0], ['added'], ['desc']);
        this.allAttributeDetails = reponse[0];
        this.allAttributeDetailsCopy = reponse[0];
        this.goToNextStep();
      }
    },
      error => {
        this.assetLoader = false;
        this.assetLoaderFailure = true;
        this.errorValue = -1;
        this.outerArr = [];
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      });
  }

  nextStep() {
    if (this.stepIndex + 1 === 1) {
      if(this.isCreate) {
        this.collectTargetTypes();
      } else {
        let selectedAssetGroup = this.exceptionDetailsForm.assetGroup[0].text;
        if(this.stickyExceptionDetails.groupName === selectedAssetGroup) {
          this.allAttributeDetails = this.stickyExceptionDetails.targetTypes;
          this.allAttributeDetailsCopy = _.cloneDeep(this.allAttributeDetails);
          this.goToNextStep();
        } else {
          this.collectTargetTypes();
        }
      } 
      this.searchAttribute();
    } 
    else {
      this.goToNextStep();
    }
  }

  goToNextStep() {
    this.pageContent[this.stepIndex].hide = true;
    this.stepIndex++;
    this.stepTitle = this.pageContent[this.stepIndex].title;
    this.pageContent[this.stepIndex].hide = false;
  }

  prevStep() {
    this.pageContent[this.stepIndex].hide = true;
    this.stepIndex--;
    this.stepTitle = this.pageContent[this.stepIndex].title;
    this.pageContent[this.stepIndex].hide = false;
  }

  expiryDate: any;
  getDateData(date: any): any {
    this.expiryDate = moment(date).format('DD/MM/YYYY');
  }

  closeAssetErrorMessage() {
    this.assetLoaderFailure = false;
    this.assetLoader = false;
    this.pageContent[this.stepIndex].hide = false;
  }

  navigateToCreateAssetGroup() {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      this.router.navigate(['../create-asset-groups'], {
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

  createException(exceptionFormDetails) {
    let exceptionDetails = this.marshallingCreateExceptionData(exceptionFormDetails);
    this.loadingContent = 'creation';

    this.successTitle = 'Exception Created';
    this.highlightName = exceptionFormDetails.name;

    this.hideContent = true;
    this.assetGroupExceptionLoader = true;
    this.isAssetGroupExceptionCreationUpdationFailed = false;
    this.isAssetGroupExceptionCreationUpdationSuccess = false;
    //this.selectedAssetGroupExceptionName = assetGroupExceptionDetails.assetGroupExceptionName;
    //this.highlightName = assetGroupExceptionDetails.assetGroupExceptionName;
    let url = environment.configureStickyException.url;
    let method = environment.configureStickyException.method;
    this.adminService.executeHttpAction(url, method, exceptionDetails, {}).subscribe(reponse => {
      this.successTitle = 'Exception Created';
      this.isAssetGroupExceptionCreationUpdationSuccess = true;
      this.assetGroupExceptionLoader = false;
      /*this.assetGroupExceptions = {
        assetGroupExceptionName: '',
        description: '',
        writePermission: false
      };*/
    },
      error => {
        this.failedTitle = 'Creation Failed';
        this.assetGroupExceptionLoader = false;
        this.isAssetGroupExceptionCreationUpdationFailed = true;
      })
  }

  deleteException(exceptionDetails) {
    this.loadingContent = 'deletion';
    this.hideContent = true;
    this.highlightName = '';
    this.assetGroupExceptionLoader = true;
    this.isAssetGroupExceptionCreationUpdationFailed = false;
    this.isAssetGroupExceptionCreationUpdationSuccess = false;
    //this.selectedAssetGroupExceptionName = assetGroupExceptionDetails.assetGroupExceptionName;
    //this.highlightName = assetGroupExceptionDetails.assetGroupExceptionName;
    let url = environment.deleteStickyException.url;
    let method = environment.deleteStickyException.method;
    this.adminService.executeHttpAction(url, method, exceptionDetails, {}).subscribe(reponse => {
      this.successTitle = 'Exception Deleted';
      this.isAssetGroupExceptionCreationUpdationSuccess = true;
      this.assetGroupExceptionLoader = false;
      /*this.assetGroupExceptions = {
        assetGroupExceptionName: '',
        description: '',
        writePermission: false
      };*/
    },
      error => {
        this.failedTitle = 'Deletion Failed';
        this.assetGroupExceptionLoader = false;
        this.isAssetGroupExceptionCreationUpdationFailed = true;
      })
  }

  updateException(exceptionFormDetails) {
    let exceptionDetails = this.marshallingCreateExceptionData(exceptionFormDetails);
    this.loadingContent = 'updation';
    this.successTitle = 'Exception Updated';
    this.highlightName = exceptionFormDetails.name;
    this.hideContent = true;
    this.assetGroupExceptionLoader = true;
    this.isAssetGroupExceptionCreationUpdationFailed = false;
    this.isAssetGroupExceptionCreationUpdationSuccess = false;
    //this.selectedAssetGroupExceptionName = assetGroupExceptionDetails.assetGroupExceptionName;
    //this.highlightName = assetGroupExceptionDetails.assetGroupExceptionName;
    let url = environment.configureStickyException.url;
    let method = environment.configureStickyException.method;
    this.adminService.executeHttpAction(url, method, exceptionDetails, {}).subscribe(reponse => {
      this.successTitle = 'Exception Updated';
      this.isAssetGroupExceptionCreationUpdationSuccess = true;
      this.assetGroupExceptionLoader = false;
      /*this.assetGroupExceptions = {
        assetGroupExceptionName: '',
        description: '',
        writePermission: false
      };*/
    },
      error => {
        this.failedTitle = 'Updation Failed';
        this.assetGroupExceptionLoader = false;
        this.isAssetGroupExceptionCreationUpdationFailed = true;
      })
  }

  marshallingCreateExceptionData(exceptionFormDetails) {
    let exceptionDetails = {
      exceptionName: exceptionFormDetails.name,
      exceptionReason: exceptionFormDetails.reason,
      expiryDate: this.expiryDate,
      assetGroup: exceptionFormDetails.assetGroup[0].text,
      dataSource: 'aws',
      targetTypes: this.allAttributeDetails
    }
    return exceptionDetails;
  }

  closeErrorMessage() {
    this.isRuleInvokeFailed = false;
    this.hideContent = false;
  }

  searchAttribute() {
    let term = this.searchTerm;
    this.allAttributeDetails = this.allAttributeDetailsCopy.filter(function (tag) {
      return tag.targetName.indexOf(term) >= 0;
    });

    this.allAttributeDetails.sort(function(a, b){
      return b.rules.length - a.rules.length;
    });
    this.allAttributeDetails = _.orderBy(this.allAttributeDetails, ['added'], ['desc']);
  }

  getData() {
    //this.getAllPolicyIds();
    this.allAttributeDetails = [];
    this.allAttributeDetailsCopy = [];
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
        this.exceptionName = this.queryParamsWithoutFilter.exceptionName;
        delete this.queryParamsWithoutFilter['filter'];
        if (this.exceptionName) {
          this.assetLoaderTitle = this.exceptionName;
          this.pageTitle = 'Edit Sticky Exceptions';
          this.breadcrumbPresent = 'Edit Sticky Exceptions';
          this.isCreate = false;
          this.getAllStickyExceptionDetails(this.exceptionName);
        } else {
          this.assetLoaderTitle = 'Asset Groups';
          this.pageTitle = 'Create Sticky Exceptions';
          this.breadcrumbPresent = 'Create Sticky Exceptions';
          this.isCreate = true;
          this.getAllAssetGroupNames();
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
