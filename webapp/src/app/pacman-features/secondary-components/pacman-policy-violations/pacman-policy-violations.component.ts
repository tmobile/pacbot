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
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { ActivatedRoute, Router } from '@angular/router';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { WorkflowService } from '../../../core/services/workflow.service';
import { RefactorFieldsService } from './../../../shared/services/refactor-fields.service';

@Component({
  selector: 'app-pacman-policy-violations',
  templateUrl: './pacman-policy-violations.component.html',
  styleUrls: ['./pacman-policy-violations.component.css'],
  providers: [CommonResponseService, AutorefreshService]
})

export class PacmanPolicyViolationsComponent implements OnInit, OnDestroy {
  public somedata: any;
  public outerArr: any;
  public allColumns: any;
  selectedAssetGroup: string;
  public apiData: any;
  public applicationValue: any;
  public errorMessage: any;
  public dataComing = true;
  public showLoader = true;
  public tableHeaderData: any;
  private subscriptionToAssetGroup: Subscription;
  private dataSubscription: Subscription;
  public seekdata = false;
  durationParams: any;
  autoRefresh: boolean;
  totalRows = 0;
  bucketNumber = 0;
  paginatorSize = 10;
  currentBucket: any = [];
  firstPaginator = 1;
  dataTableData: any = [];
  tableDataLoaded = false;
  lastPaginator: number;
  currentPointer = 0;
  errorValue = 0;
  searchTxt = '';
  showGenericMessage = false;
  firstTimeLoad = true;

  private urlToRedirect: string;
  @Input() pageLevel: number;
  @Input() resourceId = '';
  @Input() resourceType = 'ec2';
  @Output() errorOccured = new EventEmitter<any>();

  constructor(
    private commonResponseService: CommonResponseService,
    private assetGroupObservableService: AssetGroupObservableService,
    private autorefreshService: AutorefreshService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private workflowService: WorkflowService,
    private refactorFieldsService: RefactorFieldsService,
  ) {
    this.durationParams = this.autorefreshService.getDuration();
    this.durationParams = parseInt(this.durationParams, 10);
    this.autoRefresh = this.autorefreshService.autoRefresh;
    this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
          this.selectedAssetGroup = assetGroupName;
          this.updateComponent();
    });
  }

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.updateComponent();
  }

  updateComponent() {
    if (this.resourceId) {
      /* All functions variables which are required to be set for component to be reloaded should go here */
      this.outerArr = [];
      this.searchTxt = '';
      this.currentBucket = [];
      this.bucketNumber = 0;
      this.dataTableData = [];
      this.tableDataLoaded = false;
      this.firstPaginator = 1;
      this.currentPointer = 0;
      this.showLoader = true;
      this.dataComing = false;
      this.seekdata = false;
      this.errorValue = 0;
      this.showGenericMessage = false;
      this.getData();
    }
  }

  goToDetails(row) {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      if (row.col.toLowerCase() === 'policy name') {
        this.router.navigate(
          [
            '../../../../',
            'compliance',
            'policy-knowledgebase-details',
            row.row['Rule Id'].text
          ],
          { relativeTo: this.activatedRoute, queryParamsHandling: 'merge' }
        );
      } else if (row.col.toLowerCase() === 'last scanned') {
        this.router.navigate(
          ['../../../../', 'compliance', 'issue-details', row.row['Issue ID'].text],
          { relativeTo: this.activatedRoute, queryParamsHandling: 'merge' }
        );
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  getData() {
    /* All functions to get data should go here */
    this.getAllPatchingDetails();
  }

  getAllPatchingDetails() {

    if (this.dataSubscription) {
      this.dataSubscription.unsubscribe();
    }
    const payload = {
      ag: this.selectedAssetGroup,
      filter: {},
      from: this.bucketNumber * this.paginatorSize,
      searchtext: this.searchTxt,
      size: this.paginatorSize
    };

    const queryParam = {
      from: this.bucketNumber * this.paginatorSize,
      searchtext: this.searchTxt,
      size: this.paginatorSize
    };

    this.errorValue = 0;
    const pacmanPolicyViolationsUrl = environment.pacmanPolicyViolations.url;
    const newUrl = this.replaceUrl(pacmanPolicyViolationsUrl);
    const pacmanPolicyViolationsMethod =
      environment.pacmanPolicyViolations.method;

    this.dataSubscription = this.commonResponseService
      .getData( newUrl, pacmanPolicyViolationsMethod, payload, queryParam)
      .subscribe(
        response => {
          this.showGenericMessage = false;
          try {

            this.errorValue = 1;
            this.showLoader = false;
            this.seekdata = false;
            this.dataComing = true;
            this.dataTableData = response.response;
            this.dataComing = true;

            if (response.response.length === 0 && this.firstTimeLoad) {
              this.errorMessage = 'noPolicyFound';
            }
            this.firstTimeLoad = false;
            this.totalRows = response.total;

            this.firstPaginator = this.bucketNumber * this.paginatorSize + 1;
            this.lastPaginator =
              this.bucketNumber * this.paginatorSize + this.paginatorSize;

            this.currentPointer = this.bucketNumber;

            if (this.lastPaginator > this.totalRows) {
              this.lastPaginator = this.totalRows;
            }

            if (response.response.length > 0) {
              const updatedResponse = this.massageData(response.response);
              this.currentBucket[this.bucketNumber] = updatedResponse;
              this.processData(updatedResponse);
            }

          } catch (e) {
            this.errorValue = 0;
            this.errorMessage = this.errorHandling.handleJavascriptError(e);
            this.getErrorValues();
            this.errorOccured.emit();
          }
        },
        error => {
          this.showGenericMessage = true;
          this.errorMessage = error;
          this.getErrorValues();
          this.errorOccured.emit();
        }
      );
  }

  replaceUrl(url) {
    let replacedUrl = url.replace('{resourceId}', this.resourceId.toString());
    replacedUrl = replacedUrl.replace(
      '{assetGroup}',
      this.selectedAssetGroup.toString()
    );
    replacedUrl = replacedUrl.replace(
      '{resourceType}',
      this.resourceType.toString()
    );
    return replacedUrl;
  }

  getErrorValues(): void {
    this.errorValue = -1;
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
  }
  massageData(data) {
    /*
   * added by Trinanjan 14/02/2017
   * the funciton replaces keys of the table header data to a readable format
 */
    const refactoredService = this.refactorFieldsService;
    const newData = [];
    const formattedFilters = data.map(function(eachRow) {
      const KeysTobeChanged = Object.keys(eachRow);
      let newObj = {};
      KeysTobeChanged.forEach(element => {
        const elementnew =
          refactoredService.getDisplayNameForAKey(
            element.toLocaleLowerCase()
          ) || element;
        newObj = Object.assign(newObj, { [elementnew]: eachRow[element] });
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

    if (!this.totalRows) {
      this.totalRows = getData.length;
      this.lastPaginator = this.totalRows;
    }

    const getCols = Object.keys(getData[0]);

    for ( let row = 0; row < getData.length; row++) {
      innerArr = {};
      for ( let col = 0; col < getCols.length; col++) {
        if (getCols[col].toLowerCase() === 'last scanned') {
          if (getData[row][getCols[col]].toLowerCase() === 'pass') {
            cellObj = {
              link: '',
              properties: {
                color: 'rgb(0, 185, 70)',
                'font-family': 'ex2-medium',
                'text-transform': 'capitalize'
              },
              colName: getCols[col],
              hasPreImg: false,
              imgLink: '',
              valText: getData[row][getCols[col]],
              text: getData[row][getCols[col]]
            };
          } else {
            cellObj = {
              link: 'true',
              properties: {
                color: '#d40325',
                'font-family': 'ex2-medium',
                'text-transform': 'capitalize'
              },
              imgProp: { cursor: 'pointer' },
              colName: getCols[col],
              hasPostImg: true,
              hasPreImg: false,
              imgLink: '../assets/icons/left-arrow.svg',
              valText: getData[row][getCols[col]],
              text: getData[row][getCols[col]]
            };
          }
        } else if (getCols[col].toLowerCase() === 'scan history') {
          const arr = [];
          for ( let ele = 0; ele < getData[row][getCols[col]].length; ele++) {
            let obj = {};
            if (getData[row][getCols[col]][ele] === 'pass') {
              obj = {
                text: getData[row][getCols[col]][ele],
                styling: {
                  height: '0.66em',
                  width: '0.66em',
                  background: 'rgb(0, 185, 70)',
                  'border-radius': '50%',
                  margin: '0em 1em 0em 0em'
                }
              };
            } else {
              obj = {
                text: getData[row][getCols[col]][ele],
                styling: {
                  height: '0.66em',
                  width: '0.66em',
                  background: 'rgb(212, 3, 37)',
                  'border-radius': '50%',
                  margin: '0em 1em 0em 0em'
                }
              };
            }
            arr.push(obj);
          }

          cellObj = {
            link: '',
            properties: {
              color: ''
            },
            colName: getCols[col],
            isArray: true,
            hasPreImg: false,
            imgLink: '',
            valText: getData[row][getCols[col]],
            text: arr
          };
        } else if (getCols[col].toLowerCase() === 'policy name') {
          cellObj = {
            link: 'true',
            properties: {
              'text-transform': 'lowercase',
              'text-shadow': '0.1px 0'
            },
            colName: getCols[col],
            hasPreImg: false,
            imgLink: '',
            text: getData[row][getCols[col]],
            valText: getData[row][getCols[col]]
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
            valText: getData[row][getCols[col]],
            text: getData[row][getCols[col]]
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

  prevPg() {
    this.currentPointer--;
    this.processData(this.currentBucket[this.currentPointer]);

    this.firstPaginator = this.currentPointer * this.paginatorSize + 1;
    this.lastPaginator =
      this.currentPointer * this.paginatorSize + this.paginatorSize;
  }

  nextPg() {
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
  }
  searchCalled(search) {
    this.searchTxt = search;
  }

  callNewSearch() {
    this.bucketNumber = 0;
    this.currentBucket = [];
    this.outerArr = [];
    this.currentBucket = [];
    this.firstPaginator = 1;
    this.currentPointer = 0;
    this.showLoader = true;
    this.getData();
  }
  ngOnDestroy() {
    try {
      this.subscriptionToAssetGroup.unsubscribe();
      this.dataSubscription.unsubscribe();
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.getErrorValues();
    }
  }
}
