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
import { environment } from "./../../../../../environments/environment";

import { ActivatedRoute, Router } from "@angular/router";
import { Subscription } from "rxjs/Subscription";
import * as _ from "lodash";
import { UtilsService } from "../../../../shared/services/utils.service";
import { LoggerService } from "../../../../shared/services/logger.service";
import { ErrorHandlingService } from "../../../../shared/services/error-handling.service";
import { NavigationStart } from "@angular/router";
import { Event, NavigationEnd } from "@angular/router";
import "rxjs/add/operator/filter";
import "rxjs/add/operator/pairwise";
import { RoutesRecognized } from "@angular/router";
import { RefactorFieldsService } from "./../../../../shared/services/refactor-fields.service";
import { WorkflowService } from "../../../../core/services/workflow.service";
import { RouterUtilityService } from "../../../../shared/services/router-utility.service";
import { AdminService } from "../../../services/all-admin.service";
import { NgForm } from "@angular/forms";
import { SelectComponent } from "ng2-select";
import { UploadFileService } from "../../../services/upload-file-service";

@Component({
  selector: 'app-admin-create-rule',
  templateUrl: './create-rule.component.html',
  styleUrls: ['./create-rule.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,
    UploadFileService,
    AdminService
  ]
})
export class CreateRuleComponent implements OnInit {
  @ViewChild('targetType') targetTypeSelectComponent: SelectComponent;
  @ViewChild('ruleFrequencyMonthDay') ruleFrequencyMonthDayComponent: SelectComponent;
  ruleLoader: boolean = false;
  pageTitle: String = "Create Rule";
  isRuleIdValid: any = -1;
  allPolicies: any = [];
  breadcrumbArray: any = ["Admin", "Rules"];
  breadcrumbLinks: any = ["policies", "rules"];
  breadcrumbPresent: any;
  outerArr: any = [];
  dataLoaded: boolean = false;
  errorMessage: any;
  showingArr: any = ["policyName", "policyId", "policyDesc"];
  allColumns: any = [];
  totalRows: number = 0;
  currentBucket: any = [];
  bucketNumber: number = 0;
  firstPaginator: number = 1;
  lastPaginator: number;
  currentPointer: number = 0;
  seekdata: boolean = false;
  showLoader: boolean = true;
  allMonthDays: any = [];
  allEnvironments: any = [];
  allRuleParamKeys = ["severity","ruleCategory"];
  allEnvParamKeys: any = [];
  allRuleParams: any = [];
  hideContent: boolean = false;
  isRuleCreationFailed: boolean = false;
  isRuleCreationSuccess: boolean = false;
  rulePolicyLoader: boolean = false;
  rulePolicyLoaderFailure: boolean = false;
  ruleDisplayName: String = '';

  paginatorSize: number = 25;
  isLastPage: boolean;
  isFirstPage: boolean;
  totalPages: number;
  pageNumber: number = 0;

  searchTxt: String = "";
  dataTableData: any = [];
  initVals: any = [];
  tableDataLoaded: boolean = false;
  filters: any = [];
  searchCriteria: any;
  filterText: any = {};
  errorValue: number = 0;
  showGenericMessage: boolean = false;
  dataTableDesc: String = "";
  urlID: String = "";

  FullQueryParams: any;
  queryParamsWithoutFilter: any;
  urlToRedirect: any = "";
  mandatory: any;
  activePolicy: any = [];
  parametersInput: any = { ruleKey: '', ruleValue: '', envKey: '', envValue: '' };
  alexaKeywords: any = [];
  assetGroupNames: any = [];
  datasourceDetails: any = [];
  targetTypesNames: any = [];
  ruleCategories = [];
  ruleSeverities = ["critical","high","medium","low"];
  allPolicyIds: any = [];
  allFrequencies: any = ["Daily", "Hourly", "Minutes", "Monthly", "Weekly", "Yearly"];
  allMonths: any = [
    { text: 'January', id: 0 },
    { text: 'February', id: 1 },
    { text: 'March', id: 2 },
    { text: 'April', id: 3 },
    { text: 'May', id: 4 },
    { text: 'June', id: 5 },
    { text: 'July', id: 6 },
    { text: 'August', id: 7 },
    { text: 'September', id: 8 },
    { text: 'October', id: 9 },
    { text: 'November', id: 10 },
    { text: 'December', id: 11 }
  ];
  isAlexaKeywordValid: any = -1;
  ruleJarFile: any;
  currentFileUpload: File;
  selectedFiles: FileList;

  ruleType: any = "Classic";
  selectedFrequency: any = "";
  ruleJarFileName: any = "";
  selectedPolicyId: any = "";
  selectedRuleName: any = "";
  selectedTargetType: any = "";
  isAutofixEnabled: boolean = false;

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
    private uploadService: UploadFileService,
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
    this.breadcrumbPresent = "Create Rule";
    this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
      this.pageLevel
    );
  }

  dataMarshalling(dataToMarshall) {
    let fullPolicies = [];
    for (var index = 0; index < dataToMarshall.length; index++) {
      let policyItem = {};
      policyItem['createdDate'] = dataToMarshall[index][0];
      policyItem['modifiedDate'] = dataToMarshall[index][1];
      policyItem['resolution'] = dataToMarshall[index][2];
      policyItem['policyDesc'] = dataToMarshall[index][3];
      policyItem['policyId'] = dataToMarshall[index][4];
      policyItem['policyUrl'] = dataToMarshall[index][5];
      policyItem['policyVersion'] = dataToMarshall[index][6];
      policyItem['policyName'] = dataToMarshall[index][7];
      policyItem['numberOfRules'] = dataToMarshall[index][8];
      fullPolicies.push(policyItem);
    }
    return fullPolicies;
  }

  nextPage() {
    try {
      if (!this.isLastPage) {
        this.pageNumber++;
        this.showLoader = true;
        //this.getPolicyDetails();
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log("error", error);
    }
  }

  prevPage() {
    try {
      if (!this.isFirstPage) {
        this.pageNumber--;
        this.showLoader = true;
        //this.getPolicyDetails();
      }

    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log("error", error);
    }
  }

  getAlexaKeywords() {
    this.rulePolicyLoader = true;
    this.contentHidden = true;
    this.rulePolicyLoaderFailure = false;
    var url = environment.allAlexaKeywords.url;
    var method = environment.allAlexaKeywords.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.alexaKeywords = reponse[0];
      this.getDatasourceDetails();
    },
      error => {
        this.alexaKeywords = [];
        this.rulePolicyLoader = false;
        this.contentHidden = true;
        this.rulePolicyLoaderFailure = true;
        this.errorMessage = "apiResponseError";
        this.showLoader = false;
      });
  }

  getDatasourceDetails() {
    this.rulePolicyLoader = true;
    this.contentHidden = true;
    this.rulePolicyLoaderFailure = false
    var url = environment.datasourceDetails.url;
    var method = environment.datasourceDetails.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      var fullDatasourceNames = [];
      for (var index = 0; index < reponse[0].length; index++) {
        var datasourceDetail = reponse[0][index];
        fullDatasourceNames.push(datasourceDetail[0]);
      }
      this.datasourceDetails = fullDatasourceNames;
      this.getAllAssetGroupNames();
    },
      error => {
        this.rulePolicyLoader = false;
        this.contentHidden = true;
        this.rulePolicyLoaderFailure = true
        this.datasourceDetails = [];
        this.errorMessage = "apiResponseError";
        this.showLoader = false;
      });
  }
  
  getRuleCategoryDetails() {
    this.rulePolicyLoader = true;
    this.contentHidden = true;
    this.rulePolicyLoaderFailure = false;
    const url = environment.ruleCategory.url;
    const method = environment.ruleCategory.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      const categories = [];
      for (let index = 0; index < reponse[0].length; index++) {
        const categoryDetail = reponse[0][index];
        categories.push(categoryDetail.ruleCategory);
      }
      this.ruleCategories = categories;
      this.showLoader = false;
      this.contentHidden = false;
      this.rulePolicyLoaderFailure = false;
      this.rulePolicyLoader = false;
    },
      error => {
        this.rulePolicyLoader = false;
        this.contentHidden = true;
        this.rulePolicyLoaderFailure = true;
        this.ruleCategories = [];
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      });
  }

  getAllAssetGroupNames() {
    this.rulePolicyLoader = true;
    this.contentHidden = true;
    this.rulePolicyLoaderFailure = false;
    var url = environment.assetGroupNames.url;
    var method = environment.assetGroupNames.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.assetGroupNames = reponse[0];
      this.getAllRuleIds();
    },
      error => {
        this.rulePolicyLoader = false;
        this.contentHidden = true;
        this.rulePolicyLoaderFailure = true;
        this.assetGroupNames = [];
        this.errorMessage = "apiResponseError";
        this.showLoader = false;
      });
  }

  createNewRule(form: NgForm) {
    this.hideContent = true;
    this.ruleLoader = true;
    let newRuleModel = this.buildCreateRuleModel(form.value);
  }

  isRuleIdAvailable(ruleIdKeyword) {
    if (ruleIdKeyword.trim().length == 0) {
      this.isRuleIdValid = -1;
    } else {
        let isKeywordExits = this.ruleIds.findIndex(item => ruleIdKeyword.trim().toLowerCase() === item.trim().toLowerCase());
        if (isKeywordExits === -1) {
          this.isRuleIdValid = 1;
        } else {
          this.isRuleIdValid = 0;
        }
    }
  }

  ruleIds: any = [];
  getAllRuleIds() {
    this.rulePolicyLoader = true;
    this.contentHidden = true;
    this.rulePolicyLoaderFailure = false;
    var url = environment.getAllRuleIds.url;
    var method = environment.getAllRuleIds.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.ruleIds = reponse[0];
      this.getRuleCategoryDetails();
    },
      error => {
        this.contentHidden = true;
        this.rulePolicyLoader = false;
        this.rulePolicyLoaderFailure = true;
        this.ruleIds = [];
        this.errorMessage = "apiResponseError";
        this.showLoader = false;
      });
  }

  private buildCreateRuleModel(ruleForm) {
    let newRuleModel = Object();
    newRuleModel.assetGroup = ruleForm.assetGroup[0].text;
    newRuleModel.ruleId = ruleForm.policyId[0].text + '_' + ruleForm.ruleName + '_' + ruleForm.targetType[0].text;
    newRuleModel.policyId = ruleForm.policyId[0].text;
    newRuleModel.ruleName = ruleForm.ruleName;
    newRuleModel.targetType = ruleForm.targetType[0].text;
    newRuleModel.assetGroup = ruleForm.assetGroup[0].text;
    newRuleModel.alexaKeyword = ruleForm.alexaKeywords;
    newRuleModel.ruleFrequency = this.buildRuleFrequencyCronJob(ruleForm);
    newRuleModel.ruleExecutable = this.ruleJarFileName;
    newRuleModel.ruleRestUrl = this.getRuleRestUrl(ruleForm);
    newRuleModel.ruleType = ruleForm.ruleType;
    newRuleModel.isFileChanged = true;
    newRuleModel.dataSource = ruleForm.dataSource[0].text;
    newRuleModel.ruleParams = this.buildRuleParams();
    newRuleModel.isAutofixEnabled = ruleForm.isAutofixEnabled;
    newRuleModel.displayName = ruleForm.ruleDisplayName;
    newRuleModel.severity = ruleForm.ruleSeverity[0].text;
    newRuleModel.category = ruleForm.ruleCategory[0].text;
    var url = environment.createRule.url; 
    var method = environment.createRule.method; 
    if(ruleForm.ruleType === 'Classic') {
      this.currentFileUpload = this.selectedFiles.item(0);
    } else {
      this.currentFileUpload = new File([""], "");
    }
    this.uploadService.pushFileToStorage(url, method, this.currentFileUpload, newRuleModel).subscribe(event => {
      this.ruleLoader = false;
      this.isRuleCreationSuccess = true;
    },
    error => {
      this.isRuleCreationFailed = true;
      this.ruleLoader = false;
    })
  }

  private buildRuleParams() {
    let ruleParms = Object();
    ruleParms.params = this.allRuleParams;
    ruleParms.environmentVariables = this.allEnvironments;
    return JSON.stringify(ruleParms);
  }

  private getRuleRestUrl(ruleForm) {
    let ruleType = ruleForm.ruleType;
    if (ruleType === 'Serverless') {
      return ruleForm.ruleRestUrl;
    } else {
      return '';
    }
  }

  private buildRuleFrequencyCronJob(ruleForm) {
    let selectedFrequencyType = ruleForm.ruleFrequency[0].text;
    let cronDetails = Object();
    cronDetails.interval = selectedFrequencyType;
    if (selectedFrequencyType === 'Yearly') {
      cronDetails.day = ruleForm.ruleFrequencyMonth[0].id;
      cronDetails.month = (ruleForm.ruleFrequencyMonth[0].id + 1);
    } else if (selectedFrequencyType === 'Monthly') {
      cronDetails.duration = parseInt(ruleForm.ruleFrequencyMonths);
      cronDetails.day = parseInt(ruleForm.ruleFrequencyDays);
    } else if (selectedFrequencyType === 'Weekly') {
      cronDetails.week = ruleForm.weekName;
    } else {
      cronDetails.duration = parseInt(ruleForm.ruleFrequencyModeValue);
    }

    return this.generateExpression(cronDetails);
  }

  private generateExpression(cronDetails) {

    let getCronExpression = function (cronObj) {
      if (cronObj === undefined || cronObj === null) {
        return undefined;
      } else {
        let cronObjFields = ['minutes', 'hours', 'dayOfMonth', 'month', 'dayOfWeek', 'year'];
        let cronExpression = cronObj.minutes;
        for (let index = 1; index < cronObjFields.length; index++) {
          cronExpression = cronExpression + ' ' + cronObj[cronObjFields[index]];
        }
        return cronExpression;
      }
    };

    let isValid = function (cronValidity) {
      if (cronValidity.minutes && cronValidity.hours && cronValidity.dayOfMonth && cronValidity.month && cronValidity.dayOfWeek && cronValidity.year) {
        return true;
      }
      return false;
    };

    let cronObj = {};
    if (cronDetails.interval == 'Minutes') {
      cronObj = {
        minutes: '0/' + cronDetails.duration,
        hours: '*',
        dayOfMonth: '*',
        month: '*',
        dayOfWeek: '?',
        year: '*'
      };
    } else if (cronDetails.interval == 'Hourly') {
      cronObj = {
        minutes: '0',
        hours: '0/' + cronDetails.duration,
        dayOfMonth: '*',
        month: '*',
        dayOfWeek: '?',
        year: '*'
      };
    } else if (cronDetails.interval == 'Daily') {
      cronObj = {
        minutes: '0',
        hours: '0',
        dayOfMonth: '1/' + cronDetails.duration,
        month: '*',
        dayOfWeek: '?',
        year: '*'
      };
    } else if (cronDetails.interval == 'Weekly') {
      cronObj = {
        minutes: '0',
        hours: '0',
        dayOfMonth: '?',
        month: '*',
        dayOfWeek: cronDetails.week,
        year: '*'
      };
    } else if (cronDetails.interval == 'Monthly') {
      cronObj = {
        minutes: '0',
        hours: '0',
        dayOfMonth: cronDetails.day,
        month: '1/' + cronDetails.duration,
        dayOfWeek: '?',
        year: '*'
      };
    } else if (cronDetails.interval == 'Yearly') {
      cronObj = {
        minutes: '0',
        hours: '0',
        dayOfMonth: cronDetails.day,
        month: cronDetails.month,
        dayOfWeek: '?',
        year: '*'
      };
    }
    return getCronExpression(cronObj);
  };

  closeErrorMessage() {
    this.isRuleCreationFailed = false;
    this.hideContent = false;
  }

  onJarFileChange(event) {
    this.selectedFiles = event.target.files;
    this.ruleJarFileName = this.selectedFiles[0].name;
    let extension = this.ruleJarFileName.substring(this.ruleJarFileName.lastIndexOf(".")+1);
    if(extension!=='jar') {
      this.removeJarFileName();
    }
  }

  removeRuleParameters(index: number): void {
    this.allRuleParamKeys.splice(index, 1);
    this.allRuleParams.splice(index, 1);
  }

  removeEnvironmentParameters(index: number): void {
    this.allEnvParamKeys.splice(index, 1);
    this.allEnvironments.splice(index, 1);
  }

  removeJarFileName() {
    this.ruleJarFileName = "";
    this.ruleJarFile = "";
  }

  openJarFileBrowser(event) {
    let element: HTMLElement = document.getElementById('selectJarFile') as HTMLElement;
    element.click();
  }

  getTargetTypeNamesByDatasourceName(datasourceName) {
    var url = environment.targetTypesByDatasource.url;
    var method = environment.targetTypesByDatasource.method;
    this.adminService.executeHttpAction(url, method, {}, { dataSourceName: datasourceName }).subscribe(reponse => {
      this.showLoader = false;
      this.targetTypesNames = reponse[0];
      if (this.targetTypesNames.length > 0) {
        this.targetTypeSelectComponent.disabled = false;
        this.targetTypeSelectComponent.placeholder = 'Select Target Type';
      } else {
        this.targetTypeSelectComponent.placeholder = 'No Target Available';
      }
    },
      error => {
        this.allPolicyIds = [];
        this.errorMessage = "apiResponseError";
        this.showLoader = false;
      });
  }
  contentHidden : boolean = true;
  getAllPolicyIds() {
    this.rulePolicyLoader = true;
    this.contentHidden = true;
    this.rulePolicyLoaderFailure = false;
    var url = environment.allPolicyIds.url;
    var method = environment.allPolicyIds.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.allPolicyIds = reponse[0];
      this.getAlexaKeywords();
    },
      error => {
        this.contentHidden = true;
        this.rulePolicyLoader = false;
        this.rulePolicyLoaderFailure = true;
        this.allPolicyIds = [];
        this.errorMessage = "apiResponseError";
        this.showLoader = false;
      });
  }

  public onSelectDatasource(datasourceName: any): void {
    this.targetTypeSelectComponent.items = [];
    this.targetTypeSelectComponent.disabled = true;
    if (this.targetTypeSelectComponent.active) {
      this.targetTypeSelectComponent.active.length = 0;
    }
    this.getTargetTypeNamesByDatasourceName(datasourceName.text);
  }

  addEnvironmentParameters(parametersInput: any, isEncrypted: any) {
    if (parametersInput.envKey !== '' && parametersInput.envValue !== '') {
      this.allEnvironments.push({ key: parametersInput.envKey.trim(), value: parametersInput.envValue.trim(), encrypt: isEncrypted.checked });
      this.allEnvParamKeys.push(parametersInput.envKey);
      parametersInput.envKey = '';
      parametersInput.envValue = '';
      isEncrypted.checked = false;
    }
  }

  addRuleParameters(parametersInput: any, isEncrypted: any) {
    if (parametersInput.ruleKey !== '' && parametersInput.ruleValue !== '') {
      this.allRuleParams.push({ key: parametersInput.ruleKey.trim(), value: parametersInput.ruleValue.trim(), encrypt: isEncrypted.checked });
      this.allRuleParamKeys.push(parametersInput.ruleKey);
      parametersInput.ruleKey = '';
      parametersInput.ruleValue = '';
      isEncrypted.checked = false;
    }
  }

  isAlexaKeywordAvailable(alexaKeyword) {
    if (alexaKeyword.trim().length == 0) {
      this.isAlexaKeywordValid = -1;
    } else {
        let isKeywordExits = this.alexaKeywords.findIndex(item => alexaKeyword.trim().toLowerCase() === item.trim().toLowerCase());
        if (isKeywordExits === -1) {
          this.isAlexaKeywordValid = 1;
        } else {
          this.isAlexaKeywordValid = 0;
        }
    }
  }

  onSelectPolicyId(policyId: any) {
    this.selectedPolicyId = policyId.text;
    this.isRuleIdAvailable(this.selectedPolicyId + '_' + this.selectedRuleName + '_' + this.selectedTargetType);
  }
  onSelectTargetType(targetType: any) {
    this.selectedTargetType = targetType.text;
    this.isRuleIdAvailable(this.selectedPolicyId + '_' + this.selectedRuleName + '_' + this.selectedTargetType);
  }
  onSelectFrequency(frequencyType) {
    this.selectedFrequency = frequencyType.text;
  }

  onSelectFrequencyMonthDay(selectedMonthDay) {

  }

  onSelectFrequencyMonth(selectedMonth) {

    this.targetTypeSelectComponent.placeholder = 'Select Day';
    if (this.ruleFrequencyMonthDayComponent.active) {
      this.ruleFrequencyMonthDayComponent.active.length = 0;
    }
    // this.ruleFrequencyMonthDayComponent.items = [];
    let monthDays: any = [];
    let daysCount = this.getNumberOfDays(selectedMonth.id);
    for (let dayNo = 1; dayNo <= daysCount; dayNo++) {
      monthDays.push({ id: dayNo, text: dayNo.toString() });
    }
    this.allMonthDays = monthDays;
    this.ruleFrequencyMonthDayComponent.items = monthDays;
  }


  private getNumberOfDays = function (month) {
    var year = new Date().getFullYear();
    var isLeap = ((year % 4) == 0 && ((year % 100) != 0 || (year % 400) == 0));
    return [31, (isLeap ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
  }


  getData() {
    this.getAllPolicyIds();
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
    this.searchTxt = "";
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
    this.getData();
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log("error", error);
    }
  }

  massageData(data) {
    let refactoredService = this.refactorFieldsService;
    let newData = [];
    let formattedFilters = data.map(function (data) {
      let keysTobeChanged = Object.keys(data);
      let newObj = {};
      keysTobeChanged.forEach(element => {
        var elementnew =
          refactoredService.getDisplayNameForAKey(
            element
          ) || element;
        newObj = Object.assign(newObj, { [elementnew]: data[element] });
      });
      newObj['Actions'] = '';
      newData.push(newObj);
    });
    return newData;
  }

  processData(data) {
    try {
      var innerArr = {};
      var totalVariablesObj = {};
      var cellObj = {};
      var magenta = "#e20074";
      var green = "#26ba9d";
      var red = "#f2425f";
      var orange = "#ffb00d";
      var yellow = "yellow";
      this.outerArr = [];
      var getData = data;

      if (getData.length) {
        var getCols = Object.keys(getData[0]);
      } else {
        this.seekdata = true;
      }

      for (var row = 0; row < getData.length; row++) {
        innerArr = {};
        for (var col = 0; col < getCols.length; col++) {
          if (getCols[col].toLowerCase() == "actions") {
            cellObj = {
              link: true,
              properties: {
                "text-shadow": "0.33px 0",
                "color": "#ed0295"
              },
              colName: getCols[col],
              hasPreImg: false,
              valText: "Edit",
              imgLink: "",
              text: 'Edit',
              statusProp: {
                "color": "#ed0295"
              }
            };
          } else {
            cellObj = {
              link: "",
              properties: {
                color: ""
              },
              colName: getCols[col],
              hasPreImg: false,
              imgLink: "",
              text: getData[row][getCols[col]],
              valText: getData[row][getCols[col]]
            };
          }
          innerArr[getCols[col]] = cellObj;
          totalVariablesObj[getCols[col]] = "";
        }
        this.outerArr.push(innerArr);
      }
      if (this.outerArr.length > getData.length) {
        var halfLength = this.outerArr.length / 2;
        this.outerArr = this.outerArr.splice(halfLength);
      }
      this.allColumns = Object.keys(totalVariablesObj);
      this.allColumns = ["Policy Id", "Policy Name", "Policy Description", "Policy Version", "No of Rules", "Actions"];
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log("error", error);
    }
  }

  goToCreatePolicy() {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      this.router.navigate(["../create-edit-policy"], {
        relativeTo: this.activatedRoute,
        queryParamsHandling: 'merge',
        queryParams: {
        }
      });
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log("error", error);
    }
  }

  goToDetails(row) {
    if (row.col === 'Actions') {
      try {
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        this.router.navigate(["../create-edit-policy"], {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'merge',
          queryParams: {
            policyId: row.row['Policy Id'].text
          }
        });
      } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.logger.log("error", error);
      }
    }
  }

  searchCalled(search) {
    this.searchTxt = search;
  }

  callNewSearch() {
    this.bucketNumber = 0;
    this.currentBucket = [];
    // this.getPolicyDetails();
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
