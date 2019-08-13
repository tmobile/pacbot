import { Component, OnInit, OnDestroy } from '@angular/core';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { Subscription } from 'rxjs/Subscription';
import { environment } from './../../../../../environments/environment';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { UtilsService } from '../../../../shared/services/utils.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { ExceptionManagementService } from '../../../../shared/services/exception-management.service';
import { Exception } from '../../../../shared/models/exception.model';
import { ExceptionInput } from '../../../../shared/models/exception-input.model';

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.css']
})
export class EventDetailsComponent implements OnInit, OnDestroy {

  constructor(
    private activatedRoute: ActivatedRoute,
    private assetGroupObservableService: AssetGroupObservableService,
    private logger: LoggerService,
    private commonResponseService: CommonResponseService,
    private exceptionMangamentService: ExceptionManagementService,
    private router: Router,
    private utils: UtilsService,
    private workflowService: WorkflowService
  ) {
      this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroupName => {
        this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
          this.pageLevel
        );
        this.selectedAssetGroup = assetGroupName;
      });
  }

  routeSubscription: Subscription;
  assetGroupSubscription: Subscription;
  getDescriptionSubscription: Subscription;
  getAutofixSubscription: Subscription;
  getDetailsSubscription: Subscription;
  selectedAssetGroup;
  breadcrumbArray: any = ['Health Notifications'];
  breadcrumbLinks: any = ['health-notifications'];
  breadcrumbPresent = 'Event Details';
  backButtonRequired;
  responseStatusInfo = {
    loadState: 0,
    errorMessage: 'apiResponseError'
  };
  responseStatusDetails = {
    loadState: 0,
    errorMessage: 'apiResponseError'
  };
  arnId;
  descData;
  pageLevel = 0;
  global;
  titleVal = 'Request Exception';
  description = 'Choose policies below to be exempted against selected assets';
  showExceptionalModal = false;
  cbprocessData: Exception;
  inputToException: any = {};
  paginatorSize = 25;
  totalRows = 0;
  firstPaginator = 1;
  lastPaginator: number;
  outerArr = [];
  allColumns = [];
  searchTxt = '';
  searchPassed = '';
  autofix = false;
  autofixData;

  ngOnInit() {
    this.routeSubscription = this.activatedRoute.params.subscribe(params => {
      const urlParams = params;
      this.arnId = decodeURIComponent(urlParams.arn);
    });
    this.activatedRoute.queryParams.subscribe(params => {
      this.autofix = params.autofix === 'true' || params.autofix === true;
      this.global = params.global === true || params.global === 'true';
    });
    this.updateComponent();
  }

  updateComponent() {
    this.outerArr = [];
    this.firstPaginator = 1;
    this.allColumns = [];
    if (!this.autofix) {
      this.getDescription();
      if (!this.global || this.global === 'false') {
        this.getDetails();
      }
    } else {
      this.getAutofixDetails();
    }
  }

  requestNewException() {
    this.inputToException['disablePolicy'] = true;
    const data = {
      ruleId: this.autofixData.ruleId,
      ruleName: this.autofixData.ruleName,
      expiringIn: 0,
      exceptionReason: '',
      exceptionEndDate: null,
      allPolicyIds: [],
      common: this.inputToException
    };

    const exceptionRawInput: ExceptionInput = data;
    this.cbprocessData = this.exceptionMangamentService.createDataToAddOrModifyException(exceptionRawInput);

  }

  getDescription() {
    this.responseStatusInfo.loadState = 0;
    if (this.getDescriptionSubscription) {
      this.getDescriptionSubscription.unsubscribe();
    }

    const Url = environment.getEventDescription.url;
    const Method = environment.getEventDescription.method;
    const queryParams = {
        'ag': this.selectedAssetGroup,
        'eventArn': this.arnId,
        'global': this.global
    };

    try {
          this.getDescriptionSubscription = this.commonResponseService.getData(Url, Method, {}, queryParams).subscribe(
            response => {
                try {
                    this.descData = response;
                    this.responseStatusInfo.loadState = 1;
                } catch (e) {
                    this.responseStatusInfo.errorMessage = 'jsError';
                    this.responseStatusInfo.loadState = -1;
                    this.logger.log('error', e);
                }
            },
            error => {
                this.responseStatusInfo.errorMessage = 'apiResponseError';
                this.responseStatusInfo.loadState = -1;
                this.logger.log('error', error);
            });
        } catch (error) {
            this.responseStatusInfo.errorMessage = 'jsError';
            this.responseStatusInfo.loadState = -1;
            this.logger.log('error', error);
        }
  }

  getAutofixDetails() {
    this.responseStatusInfo.loadState = 0;
    if (this.getAutofixSubscription) {
      this.getAutofixSubscription.unsubscribe();
    }

    const Url = environment.getAutofixDetails.url;
    const Method = environment.getAutofixDetails.method;
    const queryParams = {
    };

    const payload = {
      ag: this.selectedAssetGroup,
      filter: {
        resourceId: this.arnId
      },
      from: 0,
      size: 0
    };

    try {
          this.getAutofixSubscription = this.commonResponseService.getData(Url, Method, payload, queryParams).subscribe(
            response => {
                try {
                    this.autofixData = response.data;
                    this.inputToException['allResourceIds'] = [this.autofixData.resourceId];
                    this.inputToException['resourceType'] = this.autofixData.resourceType;
                    this.inputToException['allTargetTypes'] = [this.autofixData.resourceType];
                    this.inputToException['disablePolicy'] = true;
                    this.responseStatusInfo.loadState = 1;
                } catch (e) {
                    this.responseStatusInfo.errorMessage = 'jsError';
                    this.responseStatusInfo.loadState = -1;
                    this.logger.log('error', e);
                }
            },
            error => {
                this.responseStatusInfo.errorMessage = 'apiResponseError';
                this.responseStatusInfo.loadState = -1;
                this.logger.log('error', error);
            });
        } catch (error) {
            this.responseStatusInfo.errorMessage = 'jsError';
            this.responseStatusInfo.loadState = -1;
            this.logger.log('error', error);
        }
  }

  getDetails() {
    this.responseStatusDetails.loadState = 0;
    if (this.getDetailsSubscription) {
      this.getDetailsSubscription.unsubscribe();
    }

    const Url = environment.getEventDetails.url;
    const Method = environment.getEventDetails.method;
    const queryParams = {
        'ag': this.selectedAssetGroup,
        'eventArn': this.arnId,
        'global': this.global
    };

    try {
          this.getDetailsSubscription = this.commonResponseService.getData(Url, Method, {}, queryParams).subscribe(
            response => {
              this.responseStatusDetails.loadState = 1;
              if (response.length === 0) {
                this.responseStatusDetails.loadState = -1;
                this.responseStatusDetails.errorMessage = 'noDataAvailable';
              }
              this.totalRows = response.length;
              if (this.totalRows > 0) {
                this.lastPaginator = this.totalRows;
                const updatedResponse = this.utils.massageTableData(response);
                this.processData(updatedResponse);
              }
                },
                error => {
                    this.responseStatusDetails.errorMessage = 'apiResponseError';
                    this.responseStatusDetails.loadState = -1;
                    this.logger.log('error', error);
                });
            } catch (error) {
                this.responseStatusDetails.errorMessage = 'jsError';
                this.responseStatusDetails.loadState = -1;
                this.logger.log('error', error);
            }
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  processData(data) {
    let innerArr = {};
    const totalVariablesObj = {};
    let cellObj = {};
    this.outerArr = [];
    const datainString = JSON.stringify(data);
    const getData = JSON.parse(datainString);
    const getCols = Object.keys(getData[0]);

    for (let row = 0 ; row < getData.length ; row++) {
      innerArr = {};
      for (let col = 0; col < getCols.length; col++) {
        if (getCols[col].toLowerCase() === 'resource id') {
          cellObj = {
            'link': 'View Asset Details',
            'properties':
              {
                  'color': ''
              },
            'colName': getCols[col],
            'hasPreImg': false,
            'imgLink': '',
            'text': getData[row][getCols[col]],
            'valText': getData[row][getCols[col]]
          };
        } else {
          cellObj = {
            'link': '',
            'properties':
              {
                'color': ''
              },
            'colName': getCols[col],
            'hasPreImg': false,
            'imgLink': '',
            'text': getData[row][getCols[col]],
            'valText': getData[row][getCols[col]]
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
  }

  goToDetails(row) {
    try {
      if (row.col.toLowerCase() === 'resource id') {
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        const targetType = row.row['Asset Type'].text;
        const resourceId = encodeURI(row.row['Resource ID'].text);
        this.router.navigate(
          ['../../../', 'assets', 'assets-details', targetType, resourceId],
          {
            relativeTo: this.activatedRoute,
            queryParamsHandling: 'merge'
          }
        ).catch(error => {
            this.logger.log('error', 'Error in navigation - ' + error);
        });
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  goToLinkDetails(module, page, p1?, p2?) {
    const arr = ['../../../', module, page];
    if (p1) {
      arr.push(p1);
    }
    if (p2) {
      arr.push(encodeURIComponent(p2));
    }
    this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
    this.router.navigate(
      arr,
      { queryParamsHandling: 'merge', relativeTo: this.activatedRoute }
    ).catch(error => {
      this.logger.log('error', 'Error in navigation - ' + error);
    });
  }

  searchCalled(search) {
    this.searchTxt = search;
    if (this.searchTxt === '') {
        this.searchPassed = this.searchTxt;
    }
  }

  callNewSearch() {
    this.searchPassed = this.searchTxt;
  }

  ngOnDestroy() {
    this.routeSubscription.unsubscribe();
    this.assetGroupSubscription.unsubscribe();
    if (this.getDescriptionSubscription) {
      this.getDescriptionSubscription.unsubscribe();
    }
    if (this.getDetailsSubscription) {
      this.getDetailsSubscription.unsubscribe();
    }
  }

}
