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

import {
  Component,
  OnInit,
  ViewChild,
  ElementRef,
  Input,
  OnDestroy,
  HostListener
} from '@angular/core';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { ActivatedRoute, UrlSegment, Router } from '@angular/router';
import { ICONS } from './../../../../shared/constants/icons-mapping';
import { DataCacheService } from '../../../../core/services/data-cache.service';
import {
  FormControl,
  FormGroup,
  FormBuilder,
  Validators
} from '@angular/forms';
import { AutorefreshService } from '../../../services/autorefresh.service';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { IssueAuditService } from '../../../services/issue-audit.service';
import { environment } from './../../../../../environments/environment';
import { Subscription } from 'rxjs/Subscription';
import { LoggerService } from '../../../../shared/services/logger.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { UtilsService } from '../../../../shared/services/utils.service';
import { DomainTypeObservableService } from '../../../../core/services/domain-type-observable.service';
import { PolicyViolationDescComponent } from '../../../secondary-components/policy-violation-desc/policy-violation-desc.component';
import { CONFIGURATIONS } from '../../../../../config/configurations';
import { PermissionGuardService } from '../../../../core/services/permission-guard.service';
const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;

@Component({
  selector: 'app-issue-details',
  templateUrl: './issue-details.component.html',
  styleUrls: ['./issue-details.component.css'],
  providers: [
    CommonResponseService,
    AutorefreshService,
    LoggerService,
    IssueAuditService
  ]
})


export class IssueDetailsComponent implements OnInit, OnDestroy {
  /* global variables for email template and add exception*/
  @ViewChild(PolicyViolationDescComponent) policyViolationDescComponent: PolicyViolationDescComponent;

  public queryValue = '';
  public filteredList = [];
  public idDetailsName = [];
  public elementRef;
  public emailArray = [];
  public users;
  public endDateValue: any;
  public grantedDateValue: any;
  searchTxt = '';

  @ViewChild('query') vc: ElementRef;

  dataForm: FormGroup;
  user: FormGroup;
  userEmail: FormGroup;

  /*variables for breadcrumb data*/

  breadcrumbArray: any = ['Compliance', 'Policy Violations'];
  breadcrumbLinks: any = ['compliance-dashboard', 'issue-listing'];
  breadcrumbPresent: any;

  /* variables for handling data*/

  issueBlocks: any;
  search: any;
  entity: any;
  tagsData: any;
  totalRows = 0;
  bucketNumber = 0;
  currentBucket: any = [];
  firstPaginator = 1;
  lastPaginator: number;
  currentPointer = 0;
  errorValue = 0;
  errorMessage: any;
  resourceDetails: any;
  issueAudit: any;
  outerArr: any = [];
  issueTopblocks: any = [];
  endDate: any;
  allColumns: any = [];
  descname: any;
  accountIdname: any;
  sevname: any;
  regionname: any;
  policyname: any;
  issuename: any;
  categoryname: any;
  modifiedname: any;
  rulename: any;
  paginatorSize = 10;
  dataTableData: any = [];
  tableDataLoaded = false;
  resourceIdname: any;
  awsAccountname: any;
  createdname: any;
  accountname: any;
  policyNameVal: any;
  targetname: any;
  recommedData: any;
  numberOfButtons: any = [];
  actionData: any = [];
  arrowkeyLocation = 0;
  issueIdValue: any;
  keysValue: any;
  issueKey: any;

  /*Boolean variables for setting property*/

  showNone = true;
  showOpposite = false;
  showOppositeEmail = false;
  showLoader = true;
  seekdata = false;
  showTransaction = false;
  showTransactionEmail = false;
  showLoadcomplete = false;
  showLoadcompleteEmail = false;
  check = false;
  checkEmail = false;
  checkRecommend = false;
  emailObj = {
    'to': {
      'required': true,
      'validFormat': true
    },
    'from': {
      'required': true,
      'validFormat': true
    }
  };
  showTopSection = false;
  showRecommendantions = false;
  exceptionAdded = false;
  showOppositeRecommend = false;
  showLoadcompleteRecommend = false;
  showRecommend = true;
  showJira = true;
  adminAccess = false;
  showRevoke = true;
  showOppositeJira = false;
  showOppositeRevoke = false;
  showLoadcompleteJira = false;
  showLoadcompleteRevoke = false;
  checkJira = false;
  checkRevoke = false;
  userAdmin = false;
  showJiraData = false;
  showJiraButton = false;
  viewJira = false;
  private previousUrl: any = '';
  selectedDomain: any = '';
  selectedAssetGroup: string;
  public GLOBAL_CONFIG;
  fromEmailID: any;
  private policyViolationId;

  /*Subscription variables*/

  private getRuleDescSubscription: Subscription;
  private getEmailSubscription: Subscription;
  private getResourceDetailsSubscription: Subscription;
  private getEntityDetailsSubscription: Subscription;
  private getIssueAuditSubscription: Subscription;
  private assetGroupSubscription: Subscription;
  private routeSubscription: Subscription;
  private getUserSubscription: Subscription;
  private getExceptionSubscription: Subscription;
  private getRecommendSubscription: Subscription;
  private getActionDataSubscription: Subscription;
  private getJiraSubscription: Subscription;
  private getRevokeSubscription: Subscription;
  private findJiraSubscription: Subscription;
  private subscriptionDomain: Subscription;

  emailIcon: any = {
    icon: '../assets/icons/email.svg'
  };
  jiraIcon: any = {
    icon: '../assets/icons/jira.svg'
  };
  downIcon: any = {
    icon: '../assets/png/down.png'
  };
  viewMore: any = {
    icon: '../assets/icons/front-arrow.svg'
  };
  public pageLevel = 0;
  public backButtonRequired;

  @HostListener('document:click', ['$event']) handleClick(event) {
    try {
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
    } catch (e) {
      this.logger.log('error', e);
    }
  }
  constructor(
    private activatedRoute: ActivatedRoute,
    private assetGroupObservableService: AssetGroupObservableService,
    private dataStore: DataCacheService,
    private formBuilder: FormBuilder,
    private issueAuditService: IssueAuditService,
    private commonResponseService: CommonResponseService,
    private router: Router,
    private myElement: ElementRef,
    private logger: LoggerService,
    private workflowService: WorkflowService,
    private utilityService: UtilsService,
    private domainObservableService: DomainTypeObservableService,
    private permissions: PermissionGuardService
  ) {
    try {
      this.elementRef = this.myElement;
      this.GLOBAL_CONFIG = CONFIGURATIONS;
      this.fromEmailID = this.GLOBAL_CONFIG && this.GLOBAL_CONFIG.optional && this.GLOBAL_CONFIG.optional.pacmanIssue && this.GLOBAL_CONFIG.optional.pacmanIssue.emailPacManIssue && this.GLOBAL_CONFIG.optional.pacmanIssue.emailPacManIssue.ISSUE_EMAIL_FROM_ID;
      this.routeSubscription = this.activatedRoute.params.subscribe(params => {
        this.policyViolationId = params['issueId'];
      });

      this.assetGroupSubscription = this.assetGroupObservableService
        .getAssetGroup()
        .subscribe(assetGroupName => {
          this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
            this.pageLevel
          );
          this.selectedAssetGroup = assetGroupName;
        });

      this.subscriptionDomain = this.domainObservableService
        .getDomainType()
        .subscribe(domain => {
          this.selectedDomain = domain;
          this.updateComponent();
        });
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  ngOnInit() {
    try {
      this.adminAccess = this.permissions.checkAdminPermission();

      this.dataForm = this.formBuilder.group({
        date: ''
      });

      this.user = new FormGroup({
        name: new FormControl('', [
          Validators.required,
          Validators.minLength(1)
        ])
      });

      this.userEmail = new FormGroup({
        ename: new FormControl('', [
          Validators.required,
          Validators.minLength(6)
        ]),
        fname: new FormControl('', [Validators.required, Validators.minLength(6)])
      });

      this.breadcrumbPresent = 'Policy Violations Details';

      // this.getData();
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /* Function to repaint component */

  updateComponent() {
    this.showNone = true;
    this.outerArr = [];
    this.showOpposite = false;
    this.showOppositeEmail = false;
    this.showLoader = true;
    this.seekdata = false;
    this.showTransaction = false;
    this.showTransactionEmail = false;
    this.showLoadcomplete = false;
    this.showLoadcompleteEmail = false;
    this.showOppositeRecommend = false;
    this.showLoadcompleteRecommend = false;
    this.check = false;
    this.checkEmail = false;
    this.dataTableData = [];
    this.tableDataLoaded = false;
    this.checkRecommend = false;
    this.emailObj = {
      'to': {
        'required': true,
        'validFormat': true
      },
      'from': {
        'required': true,
        'validFormat': true
      }
    };
    this.showTopSection = false;
    this.issueBlocks = false;
    this.showRecommendantions = false;
    this.showJira = true;
    this.showRevoke = true;
    this.showOppositeJira = false;
    this.showLoadcompleteJira = false;
    this.showLoadcompleteRevoke = false;
    this.checkJira = false;
    this.checkRevoke = false;
    this.showOppositeRevoke = false;
    this.errorValue = 0;
    this.showJiraData = false;
    this.showJiraButton = false;
    this.viewJira = false;
    this.getData();
  }

  getData() {
    this.getRuleDesc();
    this.getUsers();
    this.findJiraExist();
  }

  getRuleDesc(): any {
    try {
      this.issueTopblocks = [];

      if (this.policyViolationId) {
        const queryParams = {
          ag: this.selectedAssetGroup,
          issueId: this.policyViolationId
        };

        const ruleDescUrl = environment.ruleDesc.url;
        const ruleDescMethod = environment.ruleDesc.method;

        this.getRuleDescSubscription = this.commonResponseService
          .getData(ruleDescUrl, ruleDescMethod, {}, queryParams)
          .subscribe(
            response => {
              try {
                this.showLoader = false;
                if (!this.utilityService.isObjectEmpty(response)) {
                  this.issueBlocks = response;
                  // changing the time using utils func
                  if (this.issueBlocks['violationCreatedDate']) {
                    this.issueBlocks[
                      'violationCreatedDate'
                    ] = this.utilityService.calculateDate(
                      this.issueBlocks['violationCreatedDate']
                    );
                  }
                  if (this.issueBlocks['violationModifiedDate']) {
                    this.issueBlocks[
                      'violationModifiedDate'
                    ] = this.utilityService.calculateDate(
                      this.issueBlocks['violationModifiedDate']
                    );
                  }

                  this.issueIdValue = this.issueBlocks.resouceViolatedPolicy;

                  if (this.issueBlocks.status !== undefined) {
                    this.exceptionAdded = (this.issueBlocks.status === 'exempted');
                    const obj = {
                      header: 'Status',
                      footer: this.issueBlocks.status,
                      img: '../assets/icons/Lock-Open.svg'
                    };
                    this.issueTopblocks.push(obj);
                  }

                  if (this.issueBlocks.severity !== undefined) {
                    const obj = {
                      header: 'Severity',
                      footer: this.issueBlocks.severity,
                      img: '../assets/icons/Flag-Critical.svg'
                    };
                    this.issueTopblocks.push(obj);
                  }

                  if (this.issueBlocks.resourceType !== undefined) {
                    let obj;
                    const iconKeys = Object.keys(ICONS.awsResources);
                    if (iconKeys.indexOf(this.issueBlocks.resourceType) > -1) {
                      obj = {
                        header: 'Target Type',
                        footer: this.issueBlocks.resourceType,
                        img: ICONS.awsResources[this.issueBlocks.resourceType]
                      };
                    } else {
                      obj = {
                        header: 'Target Type',
                        footer: this.issueBlocks.resourceType,
                        img: ICONS.awsResources[`unknown`]
                      };
                    }
                    this.issueTopblocks.push(obj);
                  }

                  if (this.issueBlocks.ruleCategory !== undefined) {
                    let obj;
                    if (
                      this.issueBlocks.ruleCategory === 'governance' ||
                      this.issueBlocks.ruleCategory === 'Governance'
                    ) {
                      obj = {
                        header: 'Rule Category',
                        footer: this.issueBlocks.ruleCategory,
                        img: '../assets/icons/Governance.svg'
                      };
                    } else {
                      obj = {
                        header: 'Rule Category',
                        footer: this.issueBlocks.ruleCategory,
                        img: '../assets/icons/Security.svg'
                      };
                    }
                    this.issueTopblocks.push(obj);
                  }

                  this.showTopSection = true;
                  this.getEntityDetails(this.issueBlocks);
                  this.getIssueAudit(this.issueBlocks);
                } else {
                  this.showLoader = false;
                  this.seekdata = true;
                  this.issueBlocks = false;
                  this.errorMessage = 'noDataAvailable';
                }
              } catch (e) {
                this.showLoader = false;
                this.seekdata = true;
                this.issueBlocks = false;
                this.errorMessage = 'noDataAvailable';
              }

              // this.getRecommend();
            },
            error => {
              this.showLoader = false;
              this.seekdata = true;
              this.issueBlocks = false;
              this.errorMessage = 'apiResponseError';
            }
          );
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(
        this.router.routerState.snapshot.root
      );
    } catch (error) {
      this.logger.log('error', error);
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
        this.emailObj.to.required = false;
      } else {
        this.emailObj.to.required = true;
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  // TODO: Remove unused variables
  // getRecommend() {
  //   try {
  //     const Url = environment.recommendDetails.url;
  //     const Method = environment.recommendDetails.method;
  //     let queryparams;
  //     if (this.issueBlocks) {
  //       queryparams = {
  //         targetType: this.issueBlocks.resourceType,
  //         ruleId: this.issueBlocks.policyId
  //       };
  //     }

  //     this.getRecommendSubscription = this.commonResponseService
  //       .getData(Url, Method, {}, queryparams)
  //       .subscribe(
  //         response => {
  //           const self = this;
  //           setTimeout(() => {
  //             self.checkRecommend = true;
  //             self.showLoadcompleteRecommend = true;
  //           }, 4500);

  //           this.numberOfButtons = response;
  //           this.recommedData = response[0];
  //           for (let i = 0; i < this.numberOfButtons.length; i++) {
  //             this.actionData.push(this.numberOfButtons[i].actionApiUrl);
  //           }
  //           if (this.recommedData !== undefined) {
  //             this.showRecommendantions = true;
  //           }
  //         },
  //         error => {
  //           const self = this;
  //           setTimeout(() => {
  //             self.checkRecommend = false;
  //             self.showLoadcompleteRecommend = true;
  //           }, 4500);

  //         }
  //       );
  //   } catch (e) {
  //     this.logger.log('error', e);
  //   }
  // }

  showButtons(index) {
    try {
      const Method = 'GET';
      const Url = this.actionData[index];

      this.getActionDataSubscription = this.commonResponseService
        .getData(Url, Method, {}, {})
        .subscribe(response => {}, error => {});
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  getEntityDetails(data): any {
    try {
      const resourceId = data.resouceViolatedPolicy;
      const queryParams = {
        ag: this.selectedAssetGroup,
        resourceId: resourceId
      };
      const resourceDetailsUrl = environment.resourceDetails.url;
      const resourceDetailsMethod = environment.resourceDetails.method;
      this.getEntityDetailsSubscription = this.commonResponseService
        .getData(resourceDetailsUrl, resourceDetailsMethod, {}, queryParams)
        .subscribe(
          response => {
            if (
              !this.utilityService.checkIfAPIReturnedDataIsEmpty(
                response.response
              )
            ) {
              const enityData = response.response[0];
              this.chunckTags(enityData);
            } else {
              this.errorMessage = 'noDataAvailable';
            }
          },
          error => {
            this.errorMessage = 'apiResponseError';
          }
        );
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  callNewSearch() {
    try {
      this.bucketNumber = 0;
      this.currentBucket = [];
      this.getIssueAudit();
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  chunckTags(data) {
    try {
      const dataObj = JSON.parse(JSON.stringify(data));
      const dataValue = JSON.parse(JSON.stringify(data));
      const keys = Object.keys(dataValue);
      keys.forEach(element => {
        if (element.indexOf('tags') > -1) {
          delete dataValue[element];
        }
      });
      this.entity = dataValue;
      this.tagsData = dataObj;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  getIssueAudit(data?: any): any {
    try {
      let issueId;
      this.routeSubscription = this.activatedRoute.params.subscribe(params => {
        issueId = params['issueId'];
      });

      const payload = {
        from: this.bucketNumber * this.paginatorSize,
        issueId: issueId,
        size: this.paginatorSize,
        targetType: this.issueBlocks.resourceType,
        searchtext: this.searchTxt,
        filter: { domain: this.selectedDomain }
      };
      this.errorValue = 0;
      const issueAuditUrl = environment.issueAudit.url;
      const issueAuditMethod = environment.issueAudit.method;
      this.getIssueAuditSubscription = this.issueAuditService
        .getData(payload, issueAuditUrl, issueAuditMethod)
        .subscribe(
          response => {
            this.errorValue = 1;
            this.tableDataLoaded = true;
            this.issueAudit = response[0].data.response;
            this.dataTableData = this.issueAudit;
            this.totalRows = response[0].data.total;
            this.firstPaginator = this.bucketNumber * this.paginatorSize + 1;
            this.lastPaginator =
              this.bucketNumber * this.paginatorSize + this.paginatorSize;
            this.currentPointer = this.bucketNumber;
            if (this.lastPaginator > this.totalRows) {
              this.lastPaginator = this.totalRows;
            }
            data = this.massageData(this.issueAudit);
            this.currentBucket[this.bucketNumber] = data;
            this.processData(this.issueAudit);
          },
          error => {
            this.errorValue = -1;
          }
        );
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  massageData(data) {
    try {
      for (let i = 0; i < data.length; i++) {
        data[i][`Date`] = data[i].auditdate;
        data[i][`Source`] = data[i].datasource;
        data[i][`Status`] = data[i].status;

        delete data[i].auditdate;
        delete data[i].datasource;
        delete data[i].status;
        delete data[i]._id;
      }

      return data;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  bodyClicked() {
    this.policyViolationDescComponent.closePopup();
  }

  processData(data) {
    try {
      this.outerArr = [];
      let innerArr = {};
      let cellObj = {};
      const totalVariablesObj = {};
      const getCols = Object.keys(data[0]);
      for (let row = 0; row < data.length; row++) {
        innerArr = {};
        for (let col = 0; col < getCols.length; col++) {
          if (getCols[col].toLowerCase() === 'date') {
            cellObj = {
              link: '',
              properties: {},
              colName: getCols[col],
              hasPreImg: false,
              imgLink: '',
              text: this.utilityService.calculateDateAndTime(
                data[row][getCols[col]]
              ),
              valText: this.utilityService.calculateDateAndTime(
                data[row][getCols[col]]
              )
            };
          } else {
            cellObj = {
              link: '',
              properties: {},
              colName: getCols[col],
              hasPreImg: false,
              imgLink: '',
              text: data[row][getCols[col]],
              valText: data[row][getCols[col]]
            };
          }

          innerArr[getCols[col]] = cellObj;
          totalVariablesObj[getCols[col]] = '';
        }
        this.outerArr.push(innerArr);
      }

      if (this.outerArr.length > data.length) {
        const halfLength = this.outerArr.length / 2;
        this.outerArr.splice(halfLength);
      }
      this.allColumns = Object.keys(totalVariablesObj);
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  calculateDateEmail(_JSDate) {
    try {
      const date = new Date(_JSDate);
      const year = date.getFullYear().toString();
      const month = date.getMonth() + 1;
      let monthString;
      if (month < 10) {
        monthString = '0' + month.toString();
      } else {
        monthString = month.toString();
      }
      const day = date.getDate();
      let dayString;
      if (day < 10) {
        dayString = '0' + day.toString();
      } else {
        dayString = day.toString();
      }
      return year + '-' + monthString + '-' + dayString;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  // below fns are related to RHS of page // not to be removed

  removeData(index): any {
    try {
      this.emailArray.splice(index, 1);
      if (this.emailArray.length < 1) {
        this.emailObj.to.required = false;
      } else {
        this.emailObj.to.required = true;
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  showOtherDivJira() {
    try {
      this.showJira = !this.showJira;
      this.showOppositeJira = !this.showOppositeJira;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  revokeException() {
    try {
      const Url = environment.revokeIssueException.url;
      const Method = environment.revokeIssueException.method;
      const payload = {
        issueIds: [this.policyViolationId]
      };

      this.getRevokeSubscription = this.commonResponseService
        .getData(Url, Method, payload, {})
        .subscribe(
          response => {
            this.upateStatusOnAddOrRevokeException('Open');
            setTimeout(() => {
              this.exceptionAdded = !this.exceptionAdded;
              this.checkRevoke = false;
              this.showLoadcompleteRevoke = true;
            }, 100);
          },
          error => {
            setTimeout(() => {
              this.checkRevoke = true;
              this.showLoadcompleteRevoke = true;
            }, 100);
          }
        );
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  createJira() {
    try {
      let Data = this.dataStore.getUserDetailsValue();
      Data = Data.getEmail();
      const Url = environment.createJira.url;
      const Method = environment.createJira.method;
      const payload = {
        email: Data,
        title: 'JIRA ticket created with issueId ' + this.issueIdValue,
        description: [
          {
            heading: 'Issue Details',
            details: {},
            highlight: 'true'
          },
          {
            heading: 'Entity Details',
            details: {}
          },
          {
            heading: 'Rule Parameters',
            details: {}
          }
        ],
        issue_id: this.issueIdValue
      };

      this.keysValue = Object.keys(this.issueBlocks);
      let count = 0;
      for (let i = 0; i < this.keysValue.length; i++) {
        if (i >= 0 && i < 6) {
          count = 0;
        } else if (i >= 6 && i < 12) {
          count = 1;
        } else {
          count = 2;
        }
        if (this.keysValue[i] !== 'nonDisplayableAttributes') {
          if (this.keysValue[i] === 'description') {
            payload.description[0].details[
              this.keysValue[i]
            ] = this.issueBlocks[this.keysValue[i]];
          } else if (this.keysValue[i] === 'RuleCategory') {
            payload.description[2].details[
              this.keysValue[i]
            ] = this.issueBlocks[this.keysValue[i]];
          } else {
            payload.description[count].details[
              this.keysValue[i]
            ] = this.issueBlocks[this.keysValue[i]];
          }
        }
      }

      this.getJiraSubscription = this.commonResponseService
        .getData(Url, Method, payload, {})
        .subscribe(
          response => {
            this.issueKey = response.data.issueKey;
            if (response.data.exists === true) {
              this.viewJira = true;
            }
            setTimeout(() => {
              return this.hideJira();
            }, 4500);
          },
          error => {
            this.viewJira = false;
            const self = this;
            setTimeout(() => {
              self.checkJira = true;
              self.showLoadcompleteJira = true;
            }, 4500);
          }
        );
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  onSubmit({ value, valid }: { value; valid: boolean }) {
    try {
      const date = new Date();
      const endDateValue = this.utilityService.getUTCDate(this.endDate);
      const grantedDateValue = this.utilityService.getUTCDate(date);
      const payload = {
        createdBy: this.dataStore.getUserDetailsValue().getUserId(),
        exceptionEndDate: endDateValue,
        exceptionGrantedDate: grantedDateValue,
        exceptionReason: value.name,
        issueIds: [ this.policyViolationId ]
      };
      const exceptionUrl = environment.addIssueException.url;
      const exceptionMethod = environment.addIssueException.method;
      this.getExceptionSubscription = this.commonResponseService
        .getData(exceptionUrl, exceptionMethod, payload, {})
        .subscribe(
          response => {
            this.check = true;
            this.showLoadcomplete = true;
            this.showTopSection = false;
            this.exceptionAdded = !this.exceptionAdded;
            this.upateStatusOnAddOrRevokeException('Exempted');
          },
          error => {
            this.check = false;
            this.showLoadcomplete = true;
          }
        );

      this.user.reset();
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
            this.emailObj.to.required = false;
          } else {
            this.emailObj.to.required = true;
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
          item = this.retrieveEmailFromSelectedItem(item);
          this.emailArray.push(item);
          this.queryValue = '';
          if (this.emailArray.length < 1) {
            this.emailObj.to.required = false;
          } else {
            this.emailObj.to.required = true;
          }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  upateStatusOnAddOrRevokeException(status) {
    try {

      let statusIcon;

      if (status && status.toLowerCase() === 'exempted') {
        statusIcon = '../assets/icons/Lock-Closed.svg';
      } else if (status && status.toLowerCase() === 'open') {
        statusIcon = '../assets/icons/Lock-Open.svg';
      }

      let obj;
      this.issueTopblocks = [];
      obj = {
        header: 'Status',
        footer: status, // 'Exempted' | 'Open'
        img: statusIcon
      };
      this.issueTopblocks.push(obj);

      if (this.issueBlocks.severity !== undefined) {
        obj = {
          header: 'Severity',
          footer: this.issueBlocks.severity,
          img: '../assets/icons/Flag-Critical.svg'
        };
        this.issueTopblocks.push(obj);
      }

      if (this.issueBlocks.resourceType !== undefined) {
        const iconKeys = Object.keys(ICONS.awsResources);
        if (iconKeys.indexOf(this.issueBlocks.resourceType) > -1) {
          obj = {
            header: 'Target Type',
            footer: this.issueBlocks.resourceType,
            img: ICONS.awsResources[this.issueBlocks.resourceType]
          };
        } else {
          obj = {
            header: 'Target Type',
            footer: this.issueBlocks.resourceType,
            img: ICONS.awsResources[`unknown`]
          };
        }
        this.issueTopblocks.push(obj);
      }

      if (this.issueBlocks.ruleCategory !== undefined) {
        if (
          this.issueBlocks.ruleCategory === 'governance' ||
          this.issueBlocks.ruleCategory === 'Governance'
        ) {
          obj = {
            header: 'Rule Category',
            footer: this.issueBlocks.ruleCategory,
            img: '../assets/icons/Governance.svg'
          };
        } else {
          obj = {
            header: 'Rule Category',
            footer: this.issueBlocks.ruleCategory,
            img: '../assets/icons/Security.svg'
          };
        }
        this.issueTopblocks.push(obj);
      }
      if (this.issueTopblocks.length) {
        this.showTopSection = true;
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  onSubmitemail() {
    try {
      // reset values
      this.emailObj = {
        'to': {
          'required': true,
          'validFormat': true
        },
        'from': {
          'required': true,
          'validFormat': true
        }
      };
      // to address validation
      if (this.emailArray.length < 1 && this.queryValue.length <= 0) {
        this.emailObj.to.required = false;
        return;
      } else {
        this.emailObj.to.required = true;
        if (this.emailArray.length < 1 && this.queryValue.length > 0) {
          if (this.validateEmailInput(this.queryValue)) {
            this.emailArray.push(this.queryValue);
          } else {
            this.emailObj.to.validFormat = false;
            return;
          }
        }
      }
      // from address validation
      if (this.fromEmailID.length > 0) {
        if (!this.validateEmailInput(this.fromEmailID)) {
          this.emailObj.from.validFormat = false;
          return;
        }
      } else {
        this.emailObj.from.required = false;
        return;
      }

      this.showTransactionEmail = true;

      this.postEmail(this.emailArray);

      this.emailArray = [];
      this.userEmail.reset();
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  postEmail(emailArrayList): any {
    try {
      const locationValue =
        window.location.href + '?ag=' + this.selectedAssetGroup;

      const emailUrl = environment.email.url;
      const emailMethod = environment.email.method;
      const payload = {
        attachmentUrl: this.GLOBAL_CONFIG.optional.pacmanIssue.emailPacManIssue.ISSUE_MAIL_TEMPLATE_URL + '/html.handlebars',
        from: this.fromEmailID,
        mailTemplateUrl: this.GLOBAL_CONFIG.optional.pacmanIssue.emailPacManIssue.ISSUE_MAIL_TEMPLATE_URL + '/html.handlebars',
        placeholderValues: {
          link: locationValue,
          name: name,
          statusName: 'Status',
          statusFooter: this.issueBlocks.status,
          severityName: 'Severity',
          severityFooter: this.issueBlocks.severity,
          targetTypeName: 'Target Type',
          targetTypeFooter: this.issueBlocks.resourceType,
          ruleCategoryName: 'Rule Category',
          ruleCategoryFooter: this.issueBlocks.ruleCategory,
          policyViolated: this.issueBlocks.policyViolated,
          policyDescription: this.issueBlocks.policyDescription,
          violationReason: this.issueBlocks.violationReason,
          resourceId: this.issueBlocks.resouceViolatedPolicy,
          createdOn: this.issueBlocks.violationModifiedDate,
          lastModifiedDate: this.issueBlocks.violationModifiedDate,
          templatePath: this.GLOBAL_CONFIG.optional.pacmanIssue.emailPacManIssue.ISSUE_MAIL_TEMPLATE_URL
        },
        subject: 'Issue Details',
        to: emailArrayList
      };
      this.getEmailSubscription = this.commonResponseService
        .getData(emailUrl, emailMethod, payload, {}, {
          responseType: 'text'
        })
        .subscribe(
          response => {
            this.showLoadcompleteEmail = true;
            this.checkEmail = true;
          },
          error => {
            this.showLoadcompleteEmail = true;
            this.checkEmail = false;
          }
        );
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  clearContents(element): any {
      this.showTransaction = true;
  }

  showOtherDivRecommend(): any {
    try {
      this.showRecommend = !this.showRecommend;
      this.showOppositeRecommend = !this.showOppositeRecommend;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  searchCalled(search) {
    try {
      this.searchTxt = search;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  getDateData(date: any): any {
    try {
      this.endDate = date;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  showOtherDiv(): any {
    try {
      this.showOpposite = !this.showOpposite;
      this.showNone = !this.showNone;
      if (this.showOpposite === false) {
        this.showTransaction = false;
        this.showLoadcomplete = false;
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  showOtherDivRevoke(): any {
    try {
      this.showRevoke = !this.showRevoke;
      this.showOppositeRevoke = !this.showOppositeRevoke;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  showOtherDivEmail(): any {
    try {
      this.showOppositeEmail = !this.showOppositeEmail;
      this.showNone = !this.showNone;
      if (this.showOppositeEmail === false) {
        this.showTransactionEmail = false;
        this.showLoadcompleteEmail = false;
        this.queryValue = '';
        this.filteredList = [];
        this.fromEmailID = this.GLOBAL_CONFIG && this.GLOBAL_CONFIG.optional && this.GLOBAL_CONFIG.optional.pacmanIssue && this.GLOBAL_CONFIG.optional.pacmanIssue.emailPacManIssue && this.GLOBAL_CONFIG.optional.pacmanIssue.emailPacManIssue.ISSUE_EMAIL_FROM_ID;
        this.emailObj = {
          'to': {
            'required': true,
            'validFormat': true
          },
          'from': {
            'required': true,
            'validFormat': true
          }
        };
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  prevPg() {
    try {
      this.currentPointer--;
      this.processData(this.currentBucket[this.currentPointer]);
      this.firstPaginator = this.currentPointer * this.paginatorSize + 1;
      this.lastPaginator =
        this.currentPointer * this.paginatorSize + this.paginatorSize;
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  nextPg() {
    try {
      if (this.currentPointer < this.bucketNumber) {
        this.currentPointer++;
        this.processData(this.currentBucket[this.currentPointer]);
        this.firstPaginator = this.currentPointer * this.paginatorSize + 1;
        this.lastPaginator =
          this.currentPointer * this.paginatorSize + this.paginatorSize;
        if (this.lastPaginator > this.totalRows) {
          this.lastPaginator = this.totalRows;
        }
      } else {
        this.bucketNumber++;
        this.getData();
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  showJiraTicket(): any {
    try {
      this.viewJira = true;
      setTimeout(() => {
        return this.hideJira();
      }, 10);
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  findJiraExist(): any {
    try {
      const url = environment.findJira.url;
      const method = environment.findJira.method;
      let issueValue;
      this.routeSubscription = this.activatedRoute.params.subscribe(params => {
        issueValue = params['issueId'];
      });

      const payload = {
        issue_id: issueValue
      };

      this.findJiraSubscription = this.commonResponseService
        .getData(url, method, payload, {})
        .subscribe(
          response => {
            this.showJiraButton = true;
            if (response.data.exists === false) {
              this.showJiraData = true;
            } else {
              this.showJiraData = false;
            }
          },
          error => {
            this.showJiraButton = false;
          }
        );
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
                this.users[i].userEmail +
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

  hideJira() {
    try {
      this.checkJira = false;
      this.showLoadcompleteJira = true;
      this.showJiraData = false;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  // function to check whether input is matching email pattern
  validateEmailInput(inputValue) {
    if (!emailPattern.test(inputValue)) {
      return false;
    }
    return true;
  }

  // function to retrieve email id from selected list user item
  retrieveEmailFromSelectedItem(selectedItem) {
    return selectedItem.split(' (')[1].replace(')', '');
  }

  ngOnDestroy() {
    try {
      // pushes the current url to datastore
      if (this.getRuleDescSubscription) {
        this.getRuleDescSubscription.unsubscribe();
      }
      if (this.getEmailSubscription) {
        this.getEmailSubscription.unsubscribe();
      }
      if (this.getResourceDetailsSubscription) {
        this.getResourceDetailsSubscription.unsubscribe();
      }
      if (this.getEntityDetailsSubscription) {
        this.getEntityDetailsSubscription.unsubscribe();
      }
      if (this.getIssueAuditSubscription) {
        this.getIssueAuditSubscription.unsubscribe();
      }
      if (this.assetGroupSubscription) {
        this.assetGroupSubscription.unsubscribe();
      }
      if (this.routeSubscription) {
        this.routeSubscription.unsubscribe();
      }
      if (this.subscriptionDomain) {
        this.subscriptionDomain.unsubscribe();
      }
      if (this.getUserSubscription) {
        this.getUserSubscription.unsubscribe();
      }
      if (this.getExceptionSubscription) {
        this.getExceptionSubscription.unsubscribe();
      }
      if (this.getRecommendSubscription) {
        this.getRecommendSubscription.unsubscribe();
      }
      if (this.getActionDataSubscription) {
        this.getActionDataSubscription.unsubscribe();
      }
      if (this.getJiraSubscription) {
        this.getJiraSubscription.unsubscribe();
      }
      if (this.getRevokeSubscription) {
        this.getRevokeSubscription.unsubscribe();
      }
      if (this.findJiraSubscription) {
        this.findJiraSubscription.unsubscribe();
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }
}
