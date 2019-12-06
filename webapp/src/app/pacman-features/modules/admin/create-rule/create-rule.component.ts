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

import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { environment } from './../../../../../environments/environment';

import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { UtilsService } from '../../../../shared/services/utils.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/pairwise';
import { RefactorFieldsService } from './../../../../shared/services/refactor-fields.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { RouterUtilityService } from '../../../../shared/services/router-utility.service';
import { AdminService } from '../../../services/all-admin.service';
import { NgForm } from '@angular/forms';
import { SelectComponent } from 'ng2-select';
import { UploadFileService } from '../../../services/upload-file-service';

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
export class CreateRuleComponent implements OnInit, OnDestroy {
  @ViewChild('targetType') targetTypeSelectComponent: SelectComponent;
  @ViewChild('ruleFrequencyMonthDay') ruleFrequencyMonthDayComponent: SelectComponent;
  ruleLoader = false;
  pageTitle = 'Create Rule';
  isRuleIdValid = -1;
  isCreate;
  allPolicies = [];
  breadcrumbArray = ['Admin', 'Rules'];
  breadcrumbLinks = ['policies', 'rules'];
  breadcrumbPresent;
  outerArr = [];
  dataLoaded = false;
  errorMessage;
  showingArr = ['policyName', 'policyId', 'policyDesc'];
  allColumns = [];
  totalRows = 0;
  currentBucket = [];
  bucketNumber= 0;
  firstPaginator= 1;
  lastPaginator;
  currentPointer= 0;
  seekdata = false;
  showLoader = true;
  allMonthDays = [];
  ruleIds = [];
  allEnvironments = [];
  allRuleParamKeys = [];
  allEnvParamKeys = [];
  allRuleParams = [];
  hideContent = false;
  isRuleCreationFailed = false;
  isRuleCreationSuccess = false;
  rulePolicyLoader = false;
  rulePolicyLoaderFailure = false;
  ruleDisplayName = '';

  paginatorSize = 25;
  isLastPage;
  isFirstPage;
  totalPages: number;
  pageNumber= 0;

  searchTxt = '';
  dataTableData = [];
  initVals = [];
  tableDataLoaded = false;
  filters = [];
  searchCriteria;
  filterText = {};
  errorValue= 0;
  showGenericMessage = false;
  dataTableDesc = '';
  urlID = '';

  FullQueryParams;
  queryParamsWithoutFilter;
  urlToRedirect = '';
  mandatory;
  activePolicy = [];
  parametersInput = { ruleKey: '', ruleValue: '', envKey: '', envValue: '' };
  alexaKeywords = [];
  assetGroupNames = [];
  datasourceDetails = [];
  targetTypesNames = [];
  ruleCategories = [];
  ruleSeverities = ["critical","high","medium","low"];
  allPolicyIds = [];
  allFrequencies = ['Daily', 'Hourly', 'Minutes', 'Monthly', 'Weekly', 'Yearly'];
  allMonths = [
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
  isAlexaKeywordValid = -1;
  ruleJarFile;
  currentFileUpload: File;
  selectedFiles: FileList;

  ruleType = 'Classic';
  selectedFrequency = '';
  ruleJarFileName = '';
  selectedPolicyId = '';
  contentHidden = true;
  selectedRuleName = '';
  selectedTargetType = '';
  isAutofixEnabled = false;

  public labels;
  private previousUrl = '';
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
    this.breadcrumbPresent = 'Create Rule';
    this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
      this.pageLevel
    );
  }

  dataMarshalling(dataToMarshall) {
    const fullPolicies = [];
    for (let index = 0; index < dataToMarshall.length; index++) {
      const policyItem = {};
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

  getAlexaKeywords() {
    this.rulePolicyLoader = true;
    this.contentHidden = true;
    this.rulePolicyLoaderFailure = false;
    const url = environment.allAlexaKeywords.url;
    const method = environment.allAlexaKeywords.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.alexaKeywords = reponse[0];
      this.getDatasourceDetails();
    },
      error => {
        this.alexaKeywords = [];
        this.rulePolicyLoader = false;
        this.contentHidden = true;
        this.rulePolicyLoaderFailure = true;
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      });
  }

  getDatasourceDetails() {
    this.rulePolicyLoader = true;
    this.contentHidden = true;
    this.rulePolicyLoaderFailure = false;
    const url = environment.datasourceDetails.url;
    const method = environment.datasourceDetails.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      const fullDatasourceNames = [];
      for (let index = 0; index < reponse[0].length; index++) {
        fullDatasourceNames.push(reponse[0][index].dataSourceName);
      }
      this.datasourceDetails = fullDatasourceNames;
      this.getAllAssetGroupNames();
    },
      error => {
        this.rulePolicyLoader = false;
        this.contentHidden = true;
        this.rulePolicyLoaderFailure = true;
        this.datasourceDetails = [];
        this.errorMessage = 'apiResponseError';
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
    const url = environment.assetGroupNames.url;
    const method = environment.assetGroupNames.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.assetGroupNames = reponse[0];
      this.getAllRuleIds();
    },
      error => {
        this.rulePolicyLoader = false;
        this.contentHidden = true;
        this.rulePolicyLoaderFailure = true;
        this.assetGroupNames = [];
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      });
  }

  createNewRule(form: NgForm) {
    this.hideContent = true;
    this.ruleLoader = true;
    const newRuleModel = this.buildCreateRuleModel(form.value);
  }

  isRuleIdAvailable(ruleIdKeyword) {
    if (ruleIdKeyword.trim().length === 0) {
      this.isRuleIdValid = -1;
    } else {
      const isKeywordExits = this.ruleIds.findIndex(item => ruleIdKeyword.trim().toLowerCase() === item.trim().toLowerCase());
        if (isKeywordExits === -1) {
          this.isRuleIdValid = 1;
        } else {
          this.isRuleIdValid = 0;
        }
    }
  }

  getAllRuleIds() {
    this.rulePolicyLoader = true;
    this.contentHidden = true;
    this.rulePolicyLoaderFailure = false;
    const url = environment.getAllRuleIds.url;
    const method = environment.getAllRuleIds.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.ruleIds = reponse[0];
      this.getRuleCategoryDetails();
    },
      error => {
        this.contentHidden = true;
        this.rulePolicyLoader = false;
        this.rulePolicyLoaderFailure = true;
        this.ruleIds = [];
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      });
  }

  private buildCreateRuleModel(ruleForm) {
    const newRuleModel = Object();
    newRuleModel.assetGroup = ruleForm.assetGroup[0].text;
    newRuleModel.ruleId = ruleForm.policyId[0].text + '_' + ruleForm.ruleName + '_' + ruleForm.targetType[0].text;
    newRuleModel.ruleId = newRuleModel.ruleId.replace(/\s/g, '-');
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
    const url = environment.createRule.url;
    const method = environment.createRule.method;
    if (ruleForm.ruleType === 'Classic') {
      this.currentFileUpload = this.selectedFiles.item(0);
    } else {
      this.currentFileUpload = new File([''], '');
    }
    this.uploadService.pushFileToStorage(url, method, this.currentFileUpload, newRuleModel).subscribe(event => {
      this.ruleLoader = false;
      this.isRuleCreationSuccess = true;
    },
    error => {
      this.isRuleCreationFailed = true;
      this.ruleLoader = false;
    });
  }

  private buildRuleParams() {
    const ruleParms = Object();
    ruleParms.params = this.allRuleParams;
    ruleParms.environmentVariables = this.allEnvironments;
    return JSON.stringify(ruleParms);
  }

  private getRuleRestUrl(ruleForm) {
    const ruleType = ruleForm.ruleType;
    if (ruleType === 'Serverless') {
      return ruleForm.ruleRestUrl;
    } else {
      return '';
    }
  }

  private buildRuleFrequencyCronJob(ruleForm) {
    const selectedFrequencyType = ruleForm.ruleFrequency[0].text;
    const cronDetails = Object();
    cronDetails.interval = selectedFrequencyType;
    if (selectedFrequencyType === 'Yearly') {
      cronDetails.day = ruleForm.ruleFrequencyMonth[0].id;
      cronDetails.month = (ruleForm.ruleFrequencyMonth[0].id + 1);
    } else if (selectedFrequencyType === 'Monthly') {
      cronDetails.duration = parseInt(ruleForm.ruleFrequencyMonths, 10);
      cronDetails.day = parseInt(ruleForm.ruleFrequencyDays, 10);
    } else if (selectedFrequencyType === 'Weekly') {
      cronDetails.week = ruleForm.weekName;
    } else {
      cronDetails.duration = parseInt(ruleForm.ruleFrequencyModeValue, 10);
    }

    return this.generateExpression(cronDetails);
  }

  private generateExpression(cronDetails) {

    const getCronExpression = function(cronObjInst) {
      if (cronObjInst === undefined || cronObjInst === null) {
        return undefined;
      } else {
        const cronObjFields = ['minutes', 'hours', 'dayOfMonth', 'month', 'dayOfWeek', 'year'];
        let cronExpression = cronObjInst.minutes;
        for (let index = 1; index < cronObjFields.length; index++) {
          cronExpression = cronExpression + ' ' + cronObjInst[cronObjFields[index]];
        }
        return cronExpression;
      }
    };

    const isValid = function (cronValidity) {
      if (cronValidity.minutes && cronValidity.hours && cronValidity.dayOfMonth && cronValidity.month && cronValidity.dayOfWeek && cronValidity.year) {
        return true;
      }
      return false;
    };

    let cronObj = {};
    if (cronDetails.interval === 'Minutes') {
      cronObj = {
        minutes: '0/' + cronDetails.duration,
        hours: '*',
        dayOfMonth: '*',
        month: '*',
        dayOfWeek: '?',
        year: '*'
      };
    } else if (cronDetails.interval === 'Hourly') {
      cronObj = {
        minutes: '0',
        hours: '0/' + cronDetails.duration,
        dayOfMonth: '*',
        month: '*',
        dayOfWeek: '?',
        year: '*'
      };
    } else if (cronDetails.interval === 'Daily') {
      cronObj = {
        minutes: '0',
        hours: '0',
        dayOfMonth: '1/' + cronDetails.duration,
        month: '*',
        dayOfWeek: '?',
        year: '*'
      };
    } else if (cronDetails.interval === 'Weekly') {
      cronObj = {
        minutes: '0',
        hours: '0',
        dayOfMonth: '?',
        month: '*',
        dayOfWeek: cronDetails.week,
        year: '*'
      };
    } else if (cronDetails.interval === 'Monthly') {
      cronObj = {
        minutes: '0',
        hours: '0',
        dayOfMonth: cronDetails.day,
        month: '1/' + cronDetails.duration,
        dayOfWeek: '?',
        year: '*'
      };
    } else if (cronDetails.interval === 'Yearly') {
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
  }

  closeErrorMessage() {
    this.isRuleCreationFailed = false;
    this.hideContent = false;
  }

  onJarFileChange(event) {
    this.selectedFiles = event.target.files;
    this.ruleJarFileName = this.selectedFiles[0].name;
    const extension = this.ruleJarFileName.substring(this.ruleJarFileName.lastIndexOf('.') + 1);
    if (extension !== 'jar') {
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
    this.ruleJarFileName = '';
    this.ruleJarFile = '';
  }

  openJarFileBrowser(event) {
    const element: HTMLElement = document.getElementById('selectJarFile') as HTMLElement;
    element.click();
  }

  getTargetTypeNamesByDatasourceName(datasourceName) {
    const url = environment.targetTypesByDatasource.url;
    const method = environment.targetTypesByDatasource.method;
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
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      });
  }

  getAllPolicyIds() {
    this.rulePolicyLoader = true;
    this.contentHidden = true;
    this.rulePolicyLoaderFailure = false;
    const url = environment.allPolicyIds.url;
    const method = environment.allPolicyIds.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.allPolicyIds = reponse[0];
      this.getAlexaKeywords();
    },
      error => {
        this.contentHidden = true;
        this.rulePolicyLoader = false;
        this.rulePolicyLoaderFailure = true;
        this.allPolicyIds = [];
        this.errorMessage = 'apiResponseError';
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
      this.allRuleParamKeys.push(parametersInput.ruleKey.trim());
      parametersInput.ruleKey = '';
      parametersInput.ruleValue = '';
      isEncrypted.checked = false;
    }
  }

  isAlexaKeywordAvailable(alexaKeyword) {
    if (alexaKeyword.trim().length === 0) {
      this.isAlexaKeywordValid = -1;
    } else {
      const isKeywordExits = this.alexaKeywords.findIndex(item => alexaKeyword.trim().toLowerCase() === item.trim().toLowerCase());
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

  onSelectFrequencyMonth(selectedMonth) {
    this.targetTypeSelectComponent.placeholder = 'Select Day';
    if (this.ruleFrequencyMonthDayComponent.active) {
      this.ruleFrequencyMonthDayComponent.active.length = 0;
    }
    const monthDays: any = [];
    const daysCount = this.getNumberOfDays(selectedMonth.id);
    for (let dayNo = 1; dayNo <= daysCount; dayNo++) {
      monthDays.push({ id: dayNo, text: dayNo.toString() });
    }
    this.allMonthDays = monthDays;
    this.ruleFrequencyMonthDayComponent.items = monthDays;
  }


  private getNumberOfDays = function (month) {
    const year = new Date().getFullYear();
    const isLeap = ((year % 4) === 0 && ((year % 100) !== 0 || (year % 400) === 0));
    return [31, (isLeap ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
  };


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
      const currentQueryParams = this.routerUtilityService.getQueryParametersFromSnapshot(this.router.routerState.snapshot.root);
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
    this.searchTxt = '';
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
      this.logger.log('error', error);
    }
  }

  massageData(data) {
    const refactoredService = this.refactorFieldsService;
    const newData = [];
    const formattedFilters = data.map(function (datam) {
      const keysTobeChanged = Object.keys(datam);
      let newObj = {};
      keysTobeChanged.forEach(element => {
        const elementnew =
          refactoredService.getDisplayNameForAKey(
            element
          ) || element;
        newObj = Object.assign(newObj, { [elementnew]: datam[element] });
      });
      newObj['Actions'] = '';
      newData.push(newObj);
    });
    return newData;
  }

  processData(data) {
    try {
      let innerArr = {};
      const totalVariablesObj = {};
      let cellObj = {};
      this.outerArr = [];
      const getData = data;
      let getCols;

      if (getData.length) {
        getCols = Object.keys(getData[0]);
      } else {
        this.seekdata = true;
      }

      for (let row = 0; row < getData.length; row++) {
        innerArr = {};
        for (let col = 0; col < getCols.length; col++) {
          if (getCols[col].toLowerCase() === 'actions') {
            cellObj = {
              link: true,
              properties: {
                'text-shadow': '0.33px 0',
                'color': '#ed0295'
              },
              colName: getCols[col],
              hasPreImg: false,
              valText: 'Edit',
              imgLink: '',
              text: 'Edit',
              statusProp: {
                'color': '#ed0295'
              }
            };
          } else {
            cellObj = {
              link: '',
              properties: {
                color: ''
              },
              colName: getCols[col],
              hasPreImg: false,
              imgLink: '',
              text: getData[row][getCols[col]],
              valText: getData[row][getCols[col]]
            };
          }
          innerArr[getCols[col]] = cellObj;
          totalVariablesObj[getCols[col]] = '';
        }
        this.outerArr.push(innerArr);
      }
      if (this.outerArr.length > getData.length) {
        const halfLength = this.outerArr.length / 2;
        this.outerArr = this.outerArr.splice(halfLength);
      }
      this.allColumns = Object.keys(totalVariablesObj);
      this.allColumns = ['Policy Id', 'Policy Name', 'Policy Description', 'Policy Version', 'No of Rules', 'Actions'];
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  goToCreatePolicy() {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      this.router.navigate(['../create-edit-policy'], {
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

  goToDetails(row) {
    if (row.col === 'Actions') {
      try {
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        this.router.navigate(['../create-edit-policy'], {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'merge',
          queryParams: {
            policyId: row.row['Policy Id'].text
          }
        });
      } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.logger.log('error', error);
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
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }
}
