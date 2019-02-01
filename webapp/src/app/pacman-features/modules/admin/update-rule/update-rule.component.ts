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

import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { environment } from './../../../../../environments/environment';

import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { UtilsService } from '../../../../shared/services/utils.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/pairwise';
import * as _ from "lodash";
import { RefactorFieldsService } from './../../../../shared/services/refactor-fields.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { RouterUtilityService } from '../../../../shared/services/router-utility.service';
import { AdminService } from '../../../services/all-admin.service';
import { NgForm } from '@angular/forms';
import { SelectComponent , SelectItem} from 'ng2-select';
import { UploadFileService } from '../../../services/upload-file-service';

@Component({
  selector: 'app-admin-update-rule',
  templateUrl: './update-rule.component.html',
  styleUrls: ['./update-rule.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,
    UploadFileService,
    AdminService
  ]
})
export class UpdateRuleComponent implements OnInit, OnDestroy {
  @ViewChild('targetType') targetTypeSelectComponent: SelectComponent;
  // @ViewChild('ruleFrequencyMonthDay') ruleFrequencyMonthDayComponent: SelectComponent;
  @ViewChild('ruleFrequency') ruleFrequencyComponent: SelectComponent;
  @ViewChild('ruleSeverity') ruleSeverityComponent: SelectComponent;
  @ViewChild('ruleCategory') ruleCategoryComponent: SelectComponent;

  ruleFrequencyMonth: any;
  ruleFrequencyDay: any;

  ruleFrequencyMonths: any;
  ruleFrequencyDays: any;
  ruleFrequencyModeValue: any;
  ruleDisplayName: String = '';
  weekName: any;
  ruleRestUrl: any;
  alexaKeywords: any;
  assetGroup: any = [];

  pageTitle: String = 'Update Rule';
  allPolicies: any = [];
  breadcrumbArray: any = ['Admin', 'Rules'];
  breadcrumbLinks: any = ['policies', 'rules'];
  breadcrumbPresent: any;
  outerArr: any = [];
  dataLoaded: boolean = false;
  errorMessage: any;
  showingArr: any = ['policyName', 'policyId', 'policyDesc'];
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
  allRuleParams: any = [];
  hideContent: boolean = true;
  isRuleUpdationFailed: boolean = false;
  isRuleUpdationSuccess: boolean = false;
  ruleLoader: boolean = false;
  ruleContentLoader: boolean = true;
  ruleDetails: any = { ruleId: '', policyId: '', ruleName: '', assetGroup: '', dataSource: '', targetType: '' };
  ruleFrequency: any = []
  paginatorSize: number = 25;
  isLastPage: boolean;
  isFirstPage: boolean;
  totalPages: number;
  pageNumber: number = 0;

  searchTxt: String = '';
  dataTableData: any = [];
  initVals: any = [];
  tableDataLoaded: boolean = false;
  filters: any = [];
  searchCriteria: any;
  filterText: any = {};
  ruleId = '';
  errorValue: number = 0;
  showGenericMessage: boolean = false;
  dataTableDesc: String = '';
  urlID: String = '';

  FullQueryParams: any;
  queryParamsWithoutFilter: any;
  urlToRedirect: any = '';
  mandatory: any;
  allRuleParamKeys: any = [];
  allEnvParamKeys: any = [];
  activePolicy: any = [];
  parametersInput: any = { ruleKey: '', ruleValue: '', envKey: '', envValue: '' };
  allAlexaKeywords: any = [];
  assetGroupNames: any = [];
  datasourceDetails: any = [];
  targetTypesNames: any = [];
  ruleCategories = [];
  ruleSeverities = ["critical","high","medium","low"];
  allPolicyIds: any = [];
  allFrequencies: any = ['Daily', 'Hourly', 'Minutes', 'Monthly', 'Weekly', 'Yearly'];
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

  ruleType: any = 'Classic';
  selectedFrequency: any = '';
  selectedSeverity:any = '';
  selectedCategory:any = '';
  ruleJarFileName: any = '';
  selectedPolicyId: any = '';
  selectedTargetType: any = '';
  isAutofixEnabled: boolean = false;
  isFileChanged: boolean = false;

  public labels: any;
  private previousUrl: any = '';
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
    this.breadcrumbPresent = 'Update Rule';
    this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
      this.pageLevel
    );
  }

  dataMarshalling(dataToMarshall) {
    let fullPolicies = [];
    for (let index = 0; index < dataToMarshall.length; index++) {
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

  getAlexaKeywords() {
    let url = environment.allAlexaKeywords.url;
    let method = environment.allAlexaKeywords.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.showLoader = false;
      this.allAlexaKeywords = reponse[0];
      this.getRuleCategoryDetails();
    },
      error => {
        this.allAlexaKeywords = [];
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      });
  }

  getDatasourceDetails() {
    let url = environment.datasourceDetails.url;
    let method = environment.datasourceDetails.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.showLoader = false;
      let fullDatasourceNames = [];
      for (let index = 0; index < reponse[0].length; index++) {
        let datasourceDetail = reponse[0][index];
        fullDatasourceNames.push(datasourceDetail[0]);
      }
      this.datasourceDetails = fullDatasourceNames;
    },
      error => {
        this.datasourceDetails = [];
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      });
  }
  
  getRuleCategoryDetails() {
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
    },
      error => {
        this.ruleCategories = [];
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      });
  }

  getAllAssetGroupNames() {
    let url = environment.assetGroupNames.url;
    let method = environment.assetGroupNames.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.showLoader = false;
      this.assetGroupNames = reponse[0];
    },
      error => {
        this.assetGroupNames = [];
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      });
  }

  updateRule(form: NgForm) {
    this.hideContent = true;
    this.ruleLoader = true;
    this.buildAndUpdateRuleModel(form.value);
  }

  private buildAndUpdateRuleModel(ruleForm) {
    let newRuleModel = Object();
    newRuleModel.assetGroup = ruleForm.assetGroup[0].text;
    newRuleModel.ruleId = this.FullQueryParams.ruleId;
    newRuleModel.assetGroup = ruleForm.assetGroup[0].text;
    newRuleModel.alexaKeyword = ruleForm.alexaKeywords;
    newRuleModel.ruleFrequency = this.buildRuleFrequencyCronJob(ruleForm);
    newRuleModel.ruleExecutable = this.ruleJarFileName;
    newRuleModel.ruleRestUrl = this.getRuleRestUrl(ruleForm);
    newRuleModel.ruleType = ruleForm.ruleType;
    newRuleModel.isFileChanged = this.isFileChanged;
    newRuleModel.displayName = ruleForm.ruleDisplayName;
    newRuleModel.ruleParams = this.buildRuleParams();
    newRuleModel.isAutofixEnabled = ruleForm.isAutofixEnabled;
    newRuleModel.severity = this.selectedSeverity;
    newRuleModel.category = this.selectedCategory;

    if (this.isFileChanged) {
      this.currentFileUpload = this.selectedFiles.item(0);
    } else {
      this.currentFileUpload = null;
    }

    let url = environment.updateRule.url;
    let method = environment.updateRule.method;
    this.uploadService.pushFileToStorage(url, method, this.currentFileUpload, newRuleModel).subscribe(event => {
      this.ruleLoader = false;
      this.isRuleUpdationSuccess = true;
    },
      error => {
        this.isRuleUpdationFailed = true;
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
      cronDetails.day = ruleForm.ruleFrequencyDay[0].id;
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
    this.isRuleUpdationFailed = false;
    this.hideContent = false;
  }

  onJarFileChange(event) {
    this.selectedFiles = event.target.files;
    this.ruleJarFileName = this.selectedFiles[0].name;
    let extension = this.ruleJarFileName.substring(this.ruleJarFileName.lastIndexOf(".")+1);
    if(extension!=='jar') {
      this.removeJarFileName();
    }
    this.isFileChanged = true;
  }

  removeJarFileName() {
    this.ruleJarFileName = '';
    this.ruleJarFile = '';
    this.isFileChanged = true;
  }

  openJarFileBrowser(event) {
    let element: HTMLElement = document.getElementById('selectJarFile') as HTMLElement;
    element.click();
  }

  getTargetTypeNamesByDatasourceName(datasourceName) {
    let url = environment.targetTypesByDatasource.url;
    let method = environment.targetTypesByDatasource.method;
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
    let url = environment.allPolicyIds.url;
    let method = environment.allPolicyIds.method;
    this.adminService.executeHttpAction(url, method, {}, {}).subscribe(reponse => {
      this.showLoader = false;
      this.allPolicyIds = reponse[0];
    },
      error => {
        this.allPolicyIds = [];
        this.errorMessage = 'apiResponseError';
        this.showLoader = false;
      });
  }

  getRuleDetails() {
    let url = environment.getRuleById.url;
    let method = environment.getRuleById.method;
    let ruleId: string = this.FullQueryParams.ruleId;
    this.adminService.executeHttpAction(url, method, {}, { ruleId: ruleId }).subscribe(reponse => {
      this.allRuleParamKeys = [];
      this.allEnvParamKeys = [];
      this.ruleDetails = reponse[0];
      let ruleParams = Object();
      this.ruleDetails.dataSource = 'N/A';
      this.allEnvironments = [];
      this.allRuleParams = [];
      this.isAutofixEnabled = false;
      this.ruleDisplayName =  this.ruleDetails.displayName;
      ruleParams = JSON.parse(this.ruleDetails.ruleParams);

      if (ruleParams.hasOwnProperty('pac_ds')) {
        this.ruleDetails.dataSource = ruleParams.pac_ds;
      }

      if (ruleParams.hasOwnProperty('environmentVariables')) {
        this.allEnvironments = ruleParams.environmentVariables;
        this.allEnvParamKeys = _.map(ruleParams.environmentVariables, 'key');
      }
      if (ruleParams.hasOwnProperty('params')) {
        if (ruleParams.params instanceof Array) {
          for (let i = ruleParams.params.length - 1; i >= 0; i -= 1) {
            if(ruleParams.params[i].key == 'severity') {
              this.selectedSeverity = ruleParams.params[i].value;
              this.ruleSeverityComponent.active.push(new SelectItem(ruleParams.params[i].value));
              ruleParams.params.splice(i,1);
            } else if(ruleParams.params[i].key == 'ruleCategory') {
              this.selectedCategory = ruleParams.params[i].value;
              this.ruleCategoryComponent.active.push(new SelectItem(ruleParams.params[i].value));
              ruleParams.params.splice(i,1);
            }
          }
          this.allRuleParams = ruleParams.params;
          this.allRuleParamKeys = _.map(ruleParams.params, 'key');
        }
      }
      if (ruleParams.hasOwnProperty('autofix')) {
        this.isAutofixEnabled = ruleParams.autofix;
      }

      this.ruleType = this.ruleDetails.ruleType;
      this.alexaKeywords = this.ruleDetails.alexaKeyword;
      this.isAlexaKeywordValid = 1;
      if (this.ruleDetails.assetGroup !== '') {
        this.assetGroup = [{ 'text': this.ruleDetails.assetGroup, 'id': this.ruleDetails.assetGroup }];;
      }
      if (this.ruleType === 'Classic') {
        this.ruleJarFileName = this.ruleDetails.ruleExecutable;
      } else if (this.ruleType === 'Serverless') {
        this.ruleRestUrl = this.ruleDetails.ruleRestUrl;
      }

      let frequencyforEdit = this.decodeCronExpression(this.ruleDetails.ruleFrequency);
      this.ruleFrequency = [{ 'text': frequencyforEdit.interval, 'id': frequencyforEdit.interval }];
      
      this.onSelectFrequency(frequencyforEdit.interval);
      if (frequencyforEdit.interval.toLowerCase() === 'yearly') {
        this.ruleFrequencyDay = [{ text: frequencyforEdit.day, id: frequencyforEdit.day }];
        this.ruleFrequencyMonth = [this.allMonths[parseInt(frequencyforEdit.month) - 1]];
      } else if (frequencyforEdit.interval.toLowerCase() === 'monthly') {
        this.ruleFrequencyMonths = frequencyforEdit.duration;
        this.ruleFrequencyDays = frequencyforEdit.day;
      } else if (frequencyforEdit.interval.toLowerCase() === 'weekly') {
        this.weekName = frequencyforEdit.week;
      } else {
        this.ruleFrequencyModeValue = frequencyforEdit.duration;
      }

      this.hideContent = false;
      this.ruleContentLoader = false;
    },
      error => {
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

  checkForDuration(cronValue) {
    let arr = cronValue.split('/');
    if (arr.length > 1) {
      return arr[1];
    } else {
      return arr[0];
    }
  }

  checkIfCronValueIsForMonthly(cronValue) {
    let arr = cronValue.split('/');
    if (arr.length > 1) {
      return true;
    } else {
      return false;
    }
  }

  checkForSpecialCharactersInCron(cronValue) {
    return (cronValue != '*' && cronValue != '?' && cronValue != '0')
  }

  decodeCronExpression(expression) {
    let intervals = ['Minutes', 'Hourly', 'Daily', 'Monthly', 'Weekly', 'Yearly'];
    let decodedObject = {
      day: '',
      duration: '',
      interval: '',
      month: '',
      week: ''
    };

    let expressionSplitArr = expression.split(' ');
    for (let i = 0; i < expressionSplitArr.length; i++) {
      if (i === 0 || i === 1) {
        if (this.checkForSpecialCharactersInCron(expressionSplitArr[i])) {
          decodedObject.duration = this.checkForDuration(expressionSplitArr[i]);
          decodedObject.interval = intervals[i];
          break;
        }
      } else if (i === 2) {
        if (this.checkForSpecialCharactersInCron(expressionSplitArr[i])) {
          let j = i + 1;
          let monthlyExpressionValue = this.checkForSpecialCharactersInCron(expressionSplitArr[j]);

          if (monthlyExpressionValue) {
            decodedObject.day = this.checkForDuration(expressionSplitArr[i]);
          } else {
            decodedObject.duration = this.checkForDuration(expressionSplitArr[i]);
            decodedObject.interval = intervals[i];

            break;
          }
        }
      } else if (i === 3) {
        if (this.checkForSpecialCharactersInCron(expressionSplitArr[i])) {
          if (this.checkIfCronValueIsForMonthly(expressionSplitArr[i])) {
            decodedObject.duration = this.checkForDuration(expressionSplitArr[i]);
            decodedObject.interval = intervals[i];
          } else {
            decodedObject.month = this.checkForDuration(expressionSplitArr[i]);
            decodedObject.interval = intervals[intervals.length - 1];
          }
          break;
        }
      } else if (i === 4) {
        if (this.checkForSpecialCharactersInCron(expressionSplitArr[i])) {
          decodedObject.week = this.checkForDuration(expressionSplitArr[i]);
          decodedObject.interval = intervals[i];

          break;
        }
      }
    }

    return decodedObject;
  }

  addEnvironmentParameters(parametersInput: any, isEncrypted: any) {
    if (parametersInput.envKey !== '' && parametersInput.envValue !== '') {
      this.allEnvironments.push({ key: parametersInput.envKey.trim(), value: parametersInput.envValue.trim(), isValueNew: true, encrypt: isEncrypted.checked });
      this.allEnvParamKeys.push(parametersInput.envKey.trim());
      parametersInput.envKey = '';
      parametersInput.envValue = '';
      isEncrypted.checked = false;
    }
  }

  addRuleParameters(parametersInput: any, isEncrypted: any) {
    if (parametersInput.ruleKey !== '' && parametersInput.ruleValue !== '') {
      this.allRuleParams.push({ key: parametersInput.ruleKey.trim(), value: parametersInput.ruleValue.trim(), isValueNew: true, encrypt: isEncrypted.checked });
      this.allRuleParamKeys.push(parametersInput.ruleKey.trim());
      parametersInput.ruleKey = '';
      parametersInput.ruleValue = '';
      isEncrypted.checked = false;
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

  isAlexaKeywordAvailable(alexaKeyword) {
    if (alexaKeyword.length == 0) {
      this.isAlexaKeywordValid = -1;
    } else {
      if (alexaKeyword.toLowerCase() === this.ruleDetails.alexaKeyword.toLowerCase()) {
        this.isAlexaKeywordValid = 1;
      } else {
        let isKeywordExits = this.allAlexaKeywords.findIndex(item => alexaKeyword.toLowerCase() === item.toLowerCase());
        if (isKeywordExits === -1) {
          this.isAlexaKeywordValid = 1;
        } else {
          this.isAlexaKeywordValid = 0;
        }
      }
    }
  }

  onSelectPolicyId(policyId: any) {
    this.selectedPolicyId = policyId.text;
  }
  onSelectTargetType(targetType: any) {
    this.selectedTargetType = targetType.text;
  }
  onSelectFrequency(selectedFrequency) {
    this.selectedFrequency = selectedFrequency;
  }

  onSelectFrequencyMonthDay(selectedMonthDay) {

  }
  
  onSelectSeverity(selectedSeverity) {
    this.selectedSeverity = selectedSeverity;
  }

  onSelectCategory(selectedCategory) {
    this.selectedCategory = selectedCategory;
  }

  onSelectFrequencyMonth(selectedMonth) {
    let monthDays: any = [];
    let daysCount = this.getNumberOfDays(selectedMonth.id);
    for (let dayNo = 1; dayNo <= daysCount; dayNo++) {
      monthDays.push({ id: dayNo, text: dayNo.toString() });
    }
    this.allMonthDays = monthDays;
  }


  private getNumberOfDays = function (month) {
    let year = new Date().getFullYear();
    let isLeap = ((year % 4) == 0 && ((year % 100) != 0 || (year % 400) == 0));
    return [31, (isLeap ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
  }


  getData() {
    this.getAllPolicyIds();
    this.getAlexaKeywords();
    this.getDatasourceDetails();
    this.getAllAssetGroupNames();
    this.getRuleDetails();
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
        this.ruleId = this.FullQueryParams.ruleId;
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
    let refactoredService = this.refactorFieldsService;
    let newData = [];
    let formattedFilters = data.map(function (data) {
      let keysTobeChanged = Object.keys(data);
      let newObj = {};
      keysTobeChanged.forEach(element => {
        let elementnew =
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
      let innerArr = {};
      let totalVariablesObj = {};
      let cellObj = {};
      this.outerArr = [];
      let getData = data;
      let getCols = Array();
      if (getData.length) {
        getCols = Object.keys(getData[0]);
      } else {
        this.seekdata = true;
      }

      for (let row = 0; row < getData.length; row++) {
        innerArr = {};
        for (let col = 0; col < getCols.length; col++) {
          if (getCols[col].toLowerCase() == 'actions') {
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
        let halfLength = this.outerArr.length / 2;
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
