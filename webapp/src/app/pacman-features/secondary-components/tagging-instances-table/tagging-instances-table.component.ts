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

import { Component, ViewEncapsulation, OnInit, OnDestroy } from '@angular/core';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { Subscription } from 'rxjs/Subscription';
import { DownloadService } from '../../../shared/services/download.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import {ToastObservableService} from '../../../post-login-app/common/services/toast-observable.service';
import {RefactorFieldsService} from '../../../shared/services/refactor-fields.service';
import {WorkflowService} from '../../../core/services/workflow.service';
import {ActivatedRoute, Router} from '@angular/router';
import {UtilsService} from '../../../shared/services/utils.service';

@Component({
  selector: 'app-tagging-instances-table',
  templateUrl: './tagging-instances-table.component.html',
  styleUrls: ['./tagging-instances-table.component.css'],
  providers: [CommonResponseService, AutorefreshService],
  encapsulation: ViewEncapsulation.None
})
export class TaggingInstancesTableComponent implements OnInit, OnDestroy {
  public somedata: any;
  public outerArr: any;
  public allColumns: any;

  selectedAssetGroup: string;
  public apiData: any;
  public applicationValue: any;
  public errorMessage: any;
  public tableHeaderData: any;

  private subscriptionToAssetGroup: Subscription;
  private dataSubscription: Subscription;
  private downloadSubscription: Subscription;

  public seekdata = false;
  durationParams: any;
  autoRefresh: boolean;
  totalRows = 0;
  bucketNumber = 0;
  currentBucket: any = [];
  firstPaginator = 1;
  popRows: any = ['Download Data'];
  lastPaginator: number;
  currentPointer = 0;
  paginatorSize = 10;
  dataTableData: any = [];
  errorValue = 0;
  tableDataLoaded = false;
  searchTxt = '';
  showGenericMessage = false;
  public pageLevel = 0;
  public backButtonRequired;

  pageTitle = 'Tagging Untagged Assets';

  constructor(private commonResponseService: CommonResponseService,
    private assetGroupObservableService: AssetGroupObservableService,
    private autorefreshService: AutorefreshService,
    private downloadService: DownloadService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private toastObservableService: ToastObservableService,
    private refactorFieldsService: RefactorFieldsService,
    private workflowService: WorkflowService,
    private router: Router,
    private utils: UtilsService,
    private activatedRoute: ActivatedRoute) {


    this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
        this.selectedAssetGroup = assetGroupName;
        this.updateComponent();
      });
    this.durationParams = this.autorefreshService.getDuration();
    this.durationParams = parseInt(this.durationParams, 10);
    this.autoRefresh = this.autorefreshService.autoRefresh;
  }


  ngOnInit() {
  }

  updateComponent() {

    /* All functions variables which are required to be set for component to be reloaded should go here */
    this.outerArr = [];
    this.searchTxt = '';
    this.currentBucket = [];
    this.dataTableData = [];
    this.tableDataLoaded = false;
    this.bucketNumber = 0;
    this.firstPaginator = 1;
    this.currentPointer = 0;
    this.seekdata = false;
    this.errorValue = 0;
    this.showGenericMessage = false;
    this.getData();

  }

  getData() {
    if (this.selectedAssetGroup !== undefined) {
            /* All functions to get data should go here */
            this.getTaggingSummary();
        }
  }

  handlePopClick(rowText) {
        const fileType = 'csv';

        try {

            let queryParams;

            queryParams = {
                'fileFormat': 'csv',
                'serviceId': 4,
                'fileType': fileType
            };

            const downloadRequest =  {
                  'ag': this.selectedAssetGroup,
                  'filter': {},
                  'from': 0,
                  'searchtext': this.searchTxt,
                  'size': this.totalRows
            };

            const downloadUrl = environment.download.url;
            const downloadMethod = environment.download.method;

            this.downloadService.requestForDownload(queryParams, downloadUrl, downloadMethod, downloadRequest, this.pageTitle, this.totalRows);

        } catch (error) {
            this.logger.log('error', error);
        }
    }

  getTaggingSummary() {

    if (this.dataSubscription) {
          this.dataSubscription.unsubscribe();
        }

    const payload = {
      'ag': this.selectedAssetGroup,
      'from': (this.bucketNumber) * this.paginatorSize,
      'searchtext': this.searchTxt,
      'size': this.paginatorSize
    };

    const taggingSummaryUrl = environment.taggingSummaryByApplication.url;
    const taggingSummaryMethod = environment.taggingSummaryByApplication.method;

    this.errorValue = 0;
    this.dataSubscription = this.commonResponseService.getData(taggingSummaryUrl, taggingSummaryMethod, payload, {}).subscribe(
      response => {
        this.showGenericMessage = false;
        try {
          this.errorValue = 1;
          this.tableDataLoaded = true;
          this.dataTableData = response.data.response;
          this.seekdata = false;
          if (response.data.response.length === 0) {
            this.errorValue = -1;
            this.outerArr = [];
            this.allColumns = [];
            this.totalRows = 0;
          }
          if (response.data.response.length > 0) {
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
          }

        } catch (e) {
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

  getErrorValues(): void {
    this.errorValue = -1;
    this.seekdata = true;
  }

  massageData(data) {
    /*
       * added by Trinanjan 14/02/2017
       * the function replaces keys of the table header data to a readable format
     */
    const refactoredService = this.refactorFieldsService;
    const newData = [];
    data.map(function(eachRow) {
      const KeysTobeChanged = Object.keys(eachRow);
      let newObj = {};
      KeysTobeChanged.forEach(element => {
        const elementnew =
          refactoredService.getDisplayNameForAKey(element.toLocaleLowerCase()) || element;
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

    const getCols = Object.keys(getData[0]);

    for ( let row = 0; row < getData.length; row++) {
      innerArr = {};
      for ( let col = 0; col < getCols.length; col++) {
          if ((getCols[col].toLowerCase() === 'environment untagged' ||
                      getCols[col].toLowerCase() === 'role untagged' ||
                      getCols[col].toLowerCase() === 'stack untagged') && getData[row][getCols[col]] > 0 ) {
            cellObj = {
              'link': 'true',
              'properties':
                {
                  'color': '',
                  'text-decoration': 'underline #383C4D'
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

    goToDetails(row) {
        try {
          const apiTarget = {'TypeAsset' : 'taggable'};
            this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
            if (row.col.toLowerCase() === 'environment untagged' && row.row['environment Untagged'].text > 0) {
              const eachParams = {'tagName': 'Environment', 'tagged' : false};
              eachParams['application'] = row.row.Application.valText;
              let newParams = this.utils.makeFilterObj(eachParams);
              newParams = Object.assign(newParams, apiTarget);
              newParams['mandatory'] = 'tagged';
              this.router.navigate(['../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling : 'merge'});
            } else if (row.col.toLowerCase() === 'role untagged' && row.row['role Untagged'].text > 0) {
              const eachParams = {'tagName': 'Role', 'tagged' : false};
              eachParams['application'] = row.row.Application.valText;
              let newParams = this.utils.makeFilterObj(eachParams);
              newParams = Object.assign(newParams, apiTarget);
              newParams['mandatory'] = 'tagged';
              this.router.navigate(['../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling : 'merge'});
           } else if (row.col.toLowerCase() === 'stack untagged' && row.row['stack Untagged'].text > 0) {
            const eachParams = {'tagName': 'Stack', 'tagged' : false};
              eachParams['application'] = row.row.Application.valText;
              let newParams = this.utils.makeFilterObj(eachParams);
              newParams = Object.assign(newParams, apiTarget);
              newParams['mandatory'] = 'tagged';
              this.router.navigate(['../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling : 'merge'});
            }

        } catch (error) {
            this.errorMessage = this.errorHandling.handleJavascriptError(error);
            this.logger.log('error', error);
        }
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
