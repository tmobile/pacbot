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
import { environment } from './../../../../../../environments/environment';

import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { UtilsService } from '../../../../../shared/services/utils.service';
import { LoggerService } from '../../../../../shared/services/logger.service';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/pairwise';
import { FilterManagementService } from '../../../../../shared/services/filter-management.service';
import { WorkflowService } from '../../../../../core/services/workflow.service';
import { RouterUtilityService } from '../../../../../shared/services/router-utility.service';
import { AdminService } from '../../../../services/all-admin.service';
import { NgForm } from '@angular/forms';
import { SelectComponent } from 'ng2-select';
import { UploadFileService } from '../../../../services/upload-file-service';
import { ErrorHandlingService } from '../../../../../shared/services/error-handling.service';

@Component({
  selector: 'app-admin-create-job-execution-manager',
  templateUrl: './create-job-execution-manager.component.html',
  styleUrls: ['./create-job-execution-manager.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,
    UploadFileService,
    AdminService
  ]
})
export class CreateJobExecutionManagerComponent implements OnInit, OnDestroy {
  @ViewChild('targetType') targetTypeSelectComponent: SelectComponent;
  @ViewChild('jobFrequencyMonthDay') jobFrequencyMonthDayComponent: SelectComponent;

  pageTitle = 'Create Job Execution Manager';
  allJobNames = [];
  breadcrumbArray = ['Admin', 'Job Execution Manager'];
  breadcrumbLinks = ['policies', 'job-execution-manager'];
  breadcrumbPresent;
  outerArr = [];
  dataLoaded = false;
  errorMessage;
  isCreate;
  showingArr = ['policyName', 'policyId', 'policyDesc'];
  allColumns = [];
  totalRows = 0;
  currentBucket = [];
  bucketNumber = 0;
  firstPaginator = 1;
  lastPaginator;
  currentPointer = 0;
  seekdata = false;
  showLoader = true;
  jobDetailsLoader = true;
  hideContent = false;
  contentHidden = false;
  allMonthDays = [];
  allEnvironments = [];
  allJobParams = [];
  loadingContent = '';
  isJobCreationUpdationFailed = false;
  isJobCreationUpdationSuccess = false;
  jobLoaderFailure = false;

  paginatorSize = 25;
  isLastPage;
  isFirstPage;
  totalPages;
  pageNumber = 0;

  allJobParamKeys = [];
  allEnvParamKeys = [];
  searchTxt = '';
  dataTableData = [];
  initVals = [];
  tableDataLoaded = false;
  filters = [];
  searchCriteria;
  jobName;
  filterText = {};
  errorValue = 0;
  showGenericMessage = false;
  dataTableDesc = '';
  urlID = '';

  FullQueryParams;
  queryParamsWithoutFilter;
  urlToRedirect = '';
  mandatory;
  parametersInput = { jobKey: '', jobValue: '', envKey: '', envValue: '' };
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
  jobJarFile;
  currentFileUpload: File;
  selectedFiles: FileList;
  jobLoader = false;
  isJobSuccess = false;
  isJobFailed = false;
  isJobNameValid = -1;
  jobNames = [];

  jobType = 'jar';
  selectedFrequency = '';
  jobJarFileName = '';

  isFilterRquiredOnPage = false;
  appliedFilters = {
    queryParamsWithoutFilter: {}, /* Stores the query parameter ibject without filter */
    pageLevelAppliedFilters: {} /* Stores the query parameter ibject without filter */
  };
  filterArray = []; /* Stores the page applied filter array */

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
    private filterManagementService: FilterManagementService,
    private workflowService: WorkflowService,
    private routerUtilityService: RouterUtilityService,
    private adminService: AdminService
  ) {
    /* Check route parameter */
    this.routeSubscription = this.activatedRoute.params.subscribe(params => {
    // Fetch the required params from this object.
    });
    this.routerParam();
    this.updateComponent();
  }

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.breadcrumbPresent = 'Create Job Execution Manager';
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

  createNewJob(form: NgForm) {
    this.hideContent = true;
    this.jobLoader = true;
    const newRuleModel = this.buildCreateJobModel(form.value);
  }
  private buildCreateJobModel(jobForm) {
    const newJobModel = Object();
    this.jobName = jobForm.jobName;
    newJobModel.jobName = jobForm.jobName;
    newJobModel.jobDesc = jobForm.jobDesc;
    newJobModel.jobFrequency = this.buildRuleFrequencyCronJob(jobForm);
    newJobModel.jobType = jobForm.jobType;
    newJobModel.jobParams = this.buildJobParams();
    newJobModel.jobExecutable = this.jobJarFileName;
    newJobModel.isFileChanged = true;

    const url = environment.createJob.url;
    const method = environment.createJob.method;
    this.currentFileUpload = this.selectedFiles.item(0);
    this.uploadService.pushFileToStorage(url, method, this.currentFileUpload, newJobModel).subscribe(event => {
      this.jobLoader = false;
      this.isJobSuccess = true;
    },
    error => {
      this.isJobFailed = true;
      this.showGenericMessage = true;
      this.errorValue = -1;
      this.outerArr = [];
      this.dataLoaded = true;
      this.seekdata = true;
      this.errorMessage = 'apiResponseError';
      this.showLoader = false;
      this.jobLoader = false;
    });
    // this.selectedFiles = undefined
  }

  private buildJobParams() {
    const jobParms = Object();
    jobParms.params = this.allJobParams;
    jobParms.environmentVariables = this.allEnvironments;
    return JSON.stringify(jobParms);
  }

  private getRuleRestUrl(jobForm) {
    const jobType = jobForm.jobType;
    if (jobType === 'Serverless') {
      return jobForm.jobRestUrl;
    } else {
      return '';
    }
  }

  private buildRuleFrequencyCronJob(jobForm) {
    const selectedFrequencyType = jobForm.jobFrequency[0].text;
    const cronDetails = Object();
    cronDetails.interval = selectedFrequencyType;
    if (selectedFrequencyType === 'Yearly') {
      cronDetails.day = jobForm.jobFrequencyMonth[0].id;
      cronDetails.month = (jobForm.jobFrequencyMonth[0].id + 1);
    } else if (selectedFrequencyType === 'Monthly') {
      cronDetails.duration = parseInt(jobForm.jobFrequencyMonths, 10);
      cronDetails.day = parseInt(jobForm.jobFrequencyDays, 10);
    } else if (selectedFrequencyType === 'Weekly') {
      cronDetails.week = jobForm.weekName;
    } else {
      cronDetails.duration = parseInt(jobForm.jobFrequencyModeValue, 10);
    }

    return this.generateExpression(cronDetails);
  }

  private generateExpression(cronDetails) {

    const getCronExpression = function (cronObjIns) {
      if (cronObjIns === undefined || cronObjIns === null) {
        return undefined;
      } else {
        const cronObjFields = ['minutes', 'hours', 'dayOfMonth', 'month', 'dayOfWeek', 'year'];
        let cronExpression = cronObjIns.minutes;
        for (let index = 1; index < cronObjFields.length; index++) {
          cronExpression = cronExpression + ' ' + cronObjIns[cronObjFields[index]];
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

  onJarFileChange(event) {
    this.selectedFiles = event.target.files;
    this.jobJarFileName = this.selectedFiles[0].name;
    const extension = this.jobJarFileName.substring(this.jobJarFileName.lastIndexOf('.') + 1);
    if (extension !== 'jar') {
      this.removeJarFileName();
    }
  }

  removeJarFileName() {
    this.jobJarFileName = '';
    this.jobJarFile = '';
  }

  closeErrorMessage() {
    this.isJobFailed = false;
    this.hideContent = false;
  }

  openJarFileBrowser(event) {
    const element: HTMLElement = document.getElementById('selectJarFile') as HTMLElement;
    element.click();
  }


  isJobNameAvailable(jobNameKeyword) {
    if (jobNameKeyword.trim().length === 0) {
      this.isJobNameValid = -1;
    } else {
      const isKeywordExits = this.jobNames.findIndex(item => jobNameKeyword.trim().toLowerCase() === item.trim().toLowerCase());
        if (isKeywordExits === -1) {
          this.isJobNameValid = 1;
        } else {
          this.isJobNameValid = 0;
        }
    }
  }

  addEnvironmentParameters(parametersInput: any, isEncrypted: any) {
    if (parametersInput.envKey.trim() !== '' && parametersInput.envValue.trim() !== '') {
      this.allEnvironments.push({ name: parametersInput.envKey.trim(), value: parametersInput.envValue.trim(), encrypt: isEncrypted.checked });
      this.allEnvParamKeys.push(parametersInput.envKey.trim());
      parametersInput.envKey = '';
      parametersInput.envValue = '';
      isEncrypted.checked = false;
    }
  }

  addJobParameters(parametersInput: any, isEncrypted: any) {
    if (parametersInput.jobKey.trim() !== '' && parametersInput.jobValue.trim() !== '') {
      this.allJobParams.push({ key: parametersInput.jobKey.trim(), value: parametersInput.jobValue.trim(), encrypt: isEncrypted.checked });
      this.allJobParamKeys.push(parametersInput.jobKey.trim());
      parametersInput.jobKey = '';
      parametersInput.jobValue = '';
      isEncrypted.checked = false;
    }
  }

  removeJobParameters(index: number): void {
    this.allJobParamKeys.splice(index, 1);
    this.allJobParams.splice(index, 1);
  }

  removeEnvironmentParameters(index: number): void {
    this.allEnvParamKeys.splice(index, 1);
    this.allEnvironments.splice(index, 1);
  }

  getAllJobNames() {
    this.contentHidden = true;
    this.jobLoader = false;
    this.jobDetailsLoader = true;
    this.loadingContent = 'loading';
    this.isJobCreationUpdationFailed = false;
    this.isJobCreationUpdationSuccess = false;
    const url = environment.allJobIdList.url;
    const method = environment.allJobIdList.method;
    this.adminService.executeHttpAction(url, method, {}, {jobName: this.jobName}).subscribe(reponse => {
      this.jobDetailsLoader = false;
      this.contentHidden = false;
      this.jobNames =  reponse[0];
    },
      error => {
        this.jobDetailsLoader = false;
        this.jobLoaderFailure = true;
        this.loadingContent = 'loading';
      });
  }

  onSelectFrequency(frequencyType) {
    this.selectedFrequency = frequencyType.text;
  }

  onSelectFrequencyMonth(selectedMonth) {
    this.jobFrequencyMonthDayComponent.placeholder = 'Select Day';
    if (this.jobFrequencyMonthDayComponent.active) {
      this.jobFrequencyMonthDayComponent.active.length = 0;
    }
    const monthDays: any = [];
    const daysCount = this.getNumberOfDays(selectedMonth.id);
    for (let dayNo = 1; dayNo <= daysCount; dayNo++) {
      monthDays.push({ id: dayNo, text: dayNo.toString() });
    }
    this.allMonthDays = monthDays;
    this.jobFrequencyMonthDayComponent.items = monthDays;
  }


  private getNumberOfDays = function (month) {
    const year = new Date().getFullYear();
    const isLeap = ((year % 4) === 0 && ((year % 100) !== 0 || (year % 400) === 0));
    return [31, (isLeap ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
  };

  /*
    * This function gets the urlparameter and queryObj
    *based on that different apis are being hit with different queryparams
    */
   routerParam() {
    try {

      const currentQueryParams = this.routerUtilityService.getQueryParametersFromSnapshot(this.router.routerState.snapshot.root);

      if (currentQueryParams) {

        this.appliedFilters.queryParamsWithoutFilter = JSON.parse(JSON.stringify(currentQueryParams));
        delete this.appliedFilters.queryParamsWithoutFilter['filter'];

        this.appliedFilters.pageLevelAppliedFilters = this.utils.processFilterObj(currentQueryParams);

        this.filterArray = this.filterManagementService.getFilterArray(this.appliedFilters.pageLevelAppliedFilters);
      }
    } catch (error) {
      this.errorMessage = 'jsError';
      this.logger.log('error', error);
    }
  }

  updateUrlWithNewFilters(filterArr) {
    this.appliedFilters.pageLevelAppliedFilters = this.utils.arrayToObject(
        this.filterArray,
        'filterkey',
        'value'
    ); // <-- TO update the queryparam which is passed in the filter of the api
    this.appliedFilters.pageLevelAppliedFilters = this.utils.makeFilterObj(this.appliedFilters.pageLevelAppliedFilters);

    /**
     * To change the url
     * with the deleted filter value along with the other existing paramter(ex-->tv:true)
     */

    const updatedFilters = Object.assign(
        this.appliedFilters.pageLevelAppliedFilters,
        this.appliedFilters.queryParamsWithoutFilter
    );

    /*
     Update url with new filters
     */

    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: updatedFilters
    }).then(success => {
      this.routerParam();
    });
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
    this.jobDetailsLoader = true;
    this.currentPointer = 0;
    this.dataTableData = [];
    this.tableDataLoaded = false;
    this.dataLoaded = false;
    this.seekdata = false;
    this.errorValue = 0;
    this.showGenericMessage = false;
    this.getAllJobNames();
  }

  navigateBack() {
    this.router.navigate(['../job-execution-manager'], {
      relativeTo: this.activatedRoute,
      queryParamsHandling: 'merge',
      queryParams: {
      }
    });
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
