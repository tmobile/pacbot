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

import { Component, OnInit, OnDestroy , Input} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { DownloadService } from '../../../shared/services/download.service';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import {LoggerService} from '../../../shared/services/logger.service';
import {ErrorHandlingService} from '../../../shared/services/error-handling.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastObservableService} from '../../../post-login-app/common/services/toast-observable.service';
import {WorkflowService} from '../../../core/services/workflow.service';
import {RefactorFieldsService} from '../../../shared/services/refactor-fields.service';
import {DomainTypeObservableService} from '../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-all-policy-violations',
  templateUrl: './all-policy-violations.component.html',
  styleUrls: ['./all-policy-violations.component.css'],
  providers: [CommonResponseService, AutorefreshService]
})

export class AllPolicyViolationsComponent implements OnInit, OnDestroy {

  public outerArr: any;
  public allColumns: any;

  selectedAssetGroup: string;
  selectedDomain: string;
  public apiData: any;
  public applicationValue: any;
  public errorMessage: any;
  public dataComing = true;
  public showLoader = true;
  public tableHeaderData: any;
  private subscriptionToAssetGroup: Subscription;
  private domainSubscription: Subscription;
  private dataSubscription: Subscription;
  private downloadSubscription: Subscription;
  public seekdata = false;
  durationParams: any;
  autoRefresh: boolean;
  totalRows = 0;
  bucketNumber = 0;
  popRows: any = ['Download Data'];
  dataTableData: any = [];
  tableDataLoaded = false;
  currentBucket: any = [];
  paginatorSize = 10;
  private urlToRedirect: string;
  @Input() pageLevel = 0;
  firstPaginator = 1;
  lastPaginator: number;
  currentPointer = 0;
  searchTxt = '';
  errorValue = 0;
  showGenericMessage = false;
  pageTitle = 'List of Violations';

  @Input() ruleID: any;
  constructor(	private commonResponseService: CommonResponseService,
                  private assetGroupObservableService: AssetGroupObservableService,
                  private autorefreshService: AutorefreshService,
                  private logger: LoggerService,
                  private errorHandling: ErrorHandlingService,
                  private router: Router,
                  private activatedRoute: ActivatedRoute,
                  private downloadService: DownloadService,
                  private toastObservableService: ToastObservableService,
                  private workflowService: WorkflowService,
                  private refactorFieldsService: RefactorFieldsService,
                  private domainObservableService: DomainTypeObservableService ) {


        this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroupName => {
            this.selectedAssetGroup = assetGroupName;
        });

        this.domainSubscription = this.domainObservableService.getDomainType().subscribe(domain => {
            this.selectedDomain = domain;
            this.updateComponent();
        });

        this.durationParams = this.autorefreshService.getDuration();
        this.durationParams = parseInt(this.durationParams, 10);
        this.autoRefresh = this.autorefreshService.autoRefresh;
}


ngOnInit() {
  this.urlToRedirect = this.router.routerState.snapshot.url;
  this.updateComponent();
}

updateComponent() {

    /* All functions variables which are required to be set for component to be reloaded should go here */
    this.outerArr = [];
    this.searchTxt = '';
    this.currentBucket = [];
    this.bucketNumber = 0;
    this.firstPaginator = 1;
    this.currentPointer = 0;
    this.dataTableData = [];
    this.tableDataLoaded = false;
    this.showLoader = true;
    this.dataComing = false;
    this.seekdata = false;
    this.errorValue = 0;
    this.showGenericMessage = false;
    this.getData();
}

getData() {
    /* All functions to get data should go here */
    this.getAllPatchingDetails();
}

handlePopClick(rowText) {
        const fileType = 'csv';

        try {

            let queryParams;

            queryParams = {
                'fileFormat': 'csv',
                'serviceId': 1,
                'fileType': fileType
            };

            const downloadRequest =  {
                  'ag': this.selectedAssetGroup,
                  'filter': {'ruleId.keyword': this.ruleID, 'domain': this.selectedDomain},
                  'from': 0,
                  'searchtext': this.searchTxt,
                  'size': this.totalRows
            };

            const downloadUrl = environment.download.url;
            const downloadMethod = environment.download.method;

            this.downloadService.requestForDownload(
                queryParams,
                downloadUrl,
                downloadMethod,
                downloadRequest,
                this.pageTitle,
                this.totalRows);

        } catch (error) {
            this.logger.log('error', error);
        }
    }

getAllPatchingDetails() {

    /* comment ends here */
    if (this.ruleID !== undefined) {
      const payload = {
        'ag': this.selectedAssetGroup,
        'filter': {'ruleId.keyword': this.ruleID, 'domain': this.selectedDomain},
        'from': (this.bucketNumber) * this.paginatorSize,
        'searchtext': this.searchTxt,
        'size': this.paginatorSize
      };
      this.errorValue = 0;
      const policyViolationUrl = environment.policyViolation.url;
      const policyViolationMethod = environment.policyViolation.method;

      if (this.dataSubscription) {
        this.dataSubscription.unsubscribe();
      }


      this.dataSubscription = this.commonResponseService.getData( policyViolationUrl, policyViolationMethod, payload, {}).subscribe(
        response => {
          this.showGenericMessage = false;
          try {
            this.errorValue = 1;
            this.tableDataLoaded = true;
            this.showLoader = false;
            this.seekdata = false;
            this.dataTableData = response.data.response;
            this.dataComing = true;
            this.totalRows = response.data.total;
            this.firstPaginator = (this.bucketNumber * this.paginatorSize) + 1;
            this.lastPaginator = (this.bucketNumber * this.paginatorSize) + this.paginatorSize;

            this.currentPointer = this.bucketNumber;

            if (this.lastPaginator > this.totalRows) {
              this.lastPaginator = this.totalRows;
            }
            const updatedResponse = this.massageData(response.data.response);
            this.currentBucket[this.bucketNumber] = updatedResponse;
            this.processData(updatedResponse);
          }catch (e) {
              this.errorValue = 0;
              this.errorMessage = this.errorHandling.handleJavascriptError(e);
              this.getErrorValues();
          }
      },
      error => {
        this.showGenericMessage = true;
        this.outerArr = [];
        this.errorMessage = error;
        this.getErrorValues();
      });
    }
}

getErrorValues(): void {
  this.errorValue = -1;
  this.showLoader = false;
  this.dataComing = false;
  this.seekdata = true;
  this.totalRows = 0;
}

massageData(data) {
  /*
 * added by Trinanjan 14/02/2017
 * the funciton replaces keys of the table header data to a readable format
*/
  const refactoredService = this.refactorFieldsService;
  const newData = [];
  data.map(function(rowObj) {
    const KeysTobeChanged = Object.keys(rowObj);
    let newObj = {};
    KeysTobeChanged.forEach(element => {
      const elementnew =
        refactoredService.getDisplayNameForAKey(
          element.toLocaleLowerCase()
        ) || element;
      newObj = Object.assign(newObj, { [elementnew]: rowObj[element] });
    });
    newData.push(newObj);
  });
  return newData;
}
processData(data) {
  let innerArr = {};
  const totalVariablesObj = {};
  let cellObj = {};
  this.outerArr = [];
  const getData = data;

  const getCols = Object.keys(getData[0]);

  for (let row = 0 ; row < getData.length ; row++) {
    innerArr = {};
    for (let col = 0; col < getCols.length; col++) {



      if (getCols[col].toLowerCase() === '_resourceid' || getCols[col].toLowerCase() === 'resourceid' || getCols[col].toLowerCase() === 'resource id') {
                        cellObj = {

                            'link': 'true',
                            'properties':
                                {
                                    'text-shadow': '0.1px 0',
                                    'text-transform': 'lowercase'
                                },
                            'colName': getCols[col],
                            'hasPreImg': false,
                            'imgLink': '',
                            'valText': getData[row][getCols[col]],
                            'text': getData[row][getCols[col]]
                        };
                    } else if (getCols[col].toLowerCase() === 'issue id' || getCols[col].toLowerCase() === 'issueid' || getCols[col].toLowerCase() === 'policy name') {
                        cellObj = {

                            'link': 'true',
                            'properties':
                                {
                                    'text-transform': 'lowercase',
                                    'text-shadow': '0.1px 0'
                                },
                            'colName': getCols[col],
                            'hasPreImg': false,
                            'imgLink': '',
                            'valText': getData[row][getCols[col]],
                            'text': getData[row][getCols[col]]
                        };
                    } else if (getCols[col].toLowerCase() === 'created on' || getCols[col].toLowerCase() === 'modified on') {
                        cellObj = {
                          'link': '',
                          'properties':
                            {
                                'color': ''
                            },
                          'colName': getCols[col],
                          'hasPreImg': false,
                          'imgLink': '',
                          'text': this.calculateDate(getData[row][getCols[col]]),
                          'valText': (new Date(getData[row][getCols[col]])).getTime()
                        };
                    } else if (getCols[col].toLowerCase() === 'severity') {
                        if (getData[row][getCols[col]].toLowerCase() === 'low') {
                            cellObj = {
                                'link': '',
                                'properties':
                                    {
                                        'color': '',
                                        'text-transform': 'capitalize'
                                    },
                                'colName': getCols[col],
                                'hasPreImg': true,
                                'valText': 1,
                                'imgLink': '',
                                'text': getData[row][getCols[col]],
                                'statusProp':
                                    {
                                        'background-color': '#ffe00d',
                                        'height': '8px',
                                        'width': '8px',
                                        'border-radius': '4px'
                                    }
                            };
                        } else if (getData[row][getCols[col]].toLowerCase() === 'medium') {
                            cellObj = {
                                'link': '',
                                'properties':
                                    {
                                        'color': '',
                                        'text-transform': 'capitalize'
                                    },
                                'colName': getCols[col],
                                'valText': 2,
                                'hasPreImg': true,
                                'imgLink': '',
                                'text': getData[row][getCols[col]],
                                'statusProp': {
                                    'background-color': '#ffb00d',
                                    'height': '8px',
                                    'width': '8px',
                                    'border-radius': '4px'
                                }
                            };
                        } else if (getData[row][getCols[col]].toLowerCase() === 'high') {
                            cellObj = {
                                'link': '',
                                'properties':
                                    {
                                        'color': '',
                                        'text-transform': 'capitalize'
                                    },
                                'colName': getCols[col],
                                'hasPreImg': true,
                                'imgLink': '',
                                'valText': 3,
                                'text': getData[row][getCols[col]],
                                'statusProp': {
                                    'background-color': '#f75303',
                                    'height': '8px',
                                    'width': '8px',
                                    'border-radius': '4px'
                                }
                            };
                        } else {
                            cellObj = {
                                'link': '',
                                'valText': 4,
                                'properties':
                                    {
                                        'color': '',
                                        'text-transform': 'capitalize'
                                    },
                                'colName': getCols[col],
                                'hasPreImg': true,
                                'imgLink': '',
                                'text': getData[row][getCols[col]],
                                'statusProp': {
                                    'background-color': '#d40325',
                                    'height': '8px',
                                    'width': '8px',
                                    'border-radius': '4px'
                                }
                            };
                        }

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

calculateDate(_JSDate) {
        if (!_JSDate) {
            return 'No Data';
        }
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
        return monthString + '-' + dayString + '-' + year ;
    }

goToDetails(row) {
        try {
            this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
            if (row.col.toLowerCase() === 'resource id') {
              this.router.navigate(['../../../', 'assets' , 'assets-details', row.row['Asset Type'].text, encodeURIComponent(row.row['Resource ID'].text)], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
            } else if (row.col.toLowerCase() === 'issue id' || row.col.toLowerCase() === 'issueid') {
              this.router.navigate(['../../issue-details', row.row['Issue ID'].text ]  , {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
            } else if (row.col.toLowerCase() === 'policy name') {
                this.router.navigate(['../../policy-knowledgebase-details', row.row.nonDisplayableAttributes.text.RuleId], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
            }
        } catch (error) {
            this.errorMessage = this.errorHandling.handleJavascriptError(error);
            this.logger.log('error', error);
        }
    }

prevPg() {

  this.currentPointer--;
  this.processData(this.currentBucket[this.currentPointer]);
  this.firstPaginator = (this.currentPointer * this.paginatorSize) + 1;
  this.lastPaginator = (this.currentPointer * this.paginatorSize) + this.paginatorSize;

}

nextPg() {

  if (this.currentPointer < this.bucketNumber) {
      this.currentPointer++;
      this.processData(this.currentBucket[this.currentPointer]);
      this.firstPaginator = (this.currentPointer * this.paginatorSize) + 1;
      this.lastPaginator = (this.currentPointer * this.paginatorSize) + this.paginatorSize;
      if (this.lastPaginator > this.totalRows) {
          this.lastPaginator = this.totalRows;
        }
  } else {
    this.bucketNumber++;
    this.getData();
  }
}

searchCalled(search) {
  this.searchTxt = search;
}

callNewSearch() {
  this.bucketNumber = 0;
  this.currentBucket = [];
  this.getData();
}

ngOnDestroy() {
  try {
    this.subscriptionToAssetGroup.unsubscribe();
    this.domainSubscription.unsubscribe();
    this.dataSubscription.unsubscribe();
  } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.getErrorValues();
  }
}

}

