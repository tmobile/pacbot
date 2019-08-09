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

import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { Subscription } from 'rxjs/Subscription';
import { DownloadService } from '../../../shared/services/download.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { ToastObservableService } from '../../../post-login-app/common/services/toast-observable.service';
import { WorkflowService } from '../../../core/services/workflow.service';
import { RefactorFieldsService } from '../../../shared/services/refactor-fields.service';
import { UtilsService } from '../../../shared/services/utils.service';

@Component({
  selector: 'app-all-patching-table',
  templateUrl: './all-patching-table.component.html',
  styleUrls: ['./all-patching-table.component.css'],
  providers: [ CommonResponseService, AutorefreshService ]
})
export class AllPatchingTableComponent implements OnInit, OnDestroy {

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
  private downloadSubscription: Subscription;
  private urlToRedirect: string;
  @Input() pageLevel = 0;


  public seekdata = false;
  durationParams: any;
  autoRefresh: boolean;
  totalRows = 0;
  bucketNumber = 0;
  paginatorSize = 10;
  popRows: any = [ 'Download Data'];
  currentBucket: any = [];
  firstPaginator = 1;
  lastPaginator: number;
  currentPointer = 0;
  dataTableData: any = [];
  tableDataLoaded = false;
  errorValue = 0;
  searchTxt = '';
  showGenericMessage = false;
  pageTitle = 'Patching Compliance Details';

  constructor(	private commonResponseService: CommonResponseService,
          private assetGroupObservableService: AssetGroupObservableService,
          private autorefreshService: AutorefreshService,
          private logger: LoggerService,
          private errorHandling: ErrorHandlingService,
          private activatedRoute: ActivatedRoute,
          private router: Router,
          private downloadService: DownloadService,
          private toastObservableService: ToastObservableService,
          private workflowService: WorkflowService,
          private refactorFieldsService: RefactorFieldsService,
          private utilityService: UtilsService ) {


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
    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.updateComponent();
  }

  updateComponent() {

      /* All functions variables which are required to be set for component to be reloaded should go here */
      this.outerArr = [];
      this.searchTxt = '';
      this.currentBucket = [];
      this.dataTableData = [];
      this.bucketNumber = 0;
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

  getData() {
      /* All functions to get data should go here */
      this.getAllPatchingDetails();
  }

   getAllPatchingDetails() {

     if (this.dataSubscription) {
       this.dataSubscription.unsubscribe();
     }

    const payload = {
      'ag': this.selectedAssetGroup,
      'filter': {},
      'from': (this.bucketNumber) * this.paginatorSize,
      'searchtext': this.searchTxt,
      'size': this.paginatorSize
    };
    this.errorValue = 0;
    const allPatchingTableUrl = environment.patchingTable.url;
    const allPatchingTableMethod = environment.patchingTable.method;


    this.dataSubscription = this.commonResponseService.getData( allPatchingTableUrl, allPatchingTableMethod, payload, {}).subscribe(
      response => {
        try {
            this.showGenericMessage = false;
            this.errorValue = 1;
            this.showLoader = false;
            this.seekdata = false;
            this.tableDataLoaded = true;
            this.dataComing = true;
            if (!this.utilityService.checkIfAPIReturnedDataIsEmpty(response.data)) {
              this.dataTableData = response.data.response;
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
            } else {
              this.getErrorValues();
            }
        } catch (e) {
            this.errorMessage = this.errorHandling.handleJavascriptError(e);
            this.getErrorValues();
        }
    },
    error => {
      this.showGenericMessage = true;
      this.errorMessage = error;
      this.getErrorValues();
    });
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
      const newObj = {};
      KeysTobeChanged.forEach(element => {
        const elementnew = refactoredService.getDisplayNameForAKey(element.toLocaleLowerCase()) || element;
        newObj[elementnew] = rowObj[element];
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

    const datainString = JSON.stringify(data);

    const getData = JSON.parse(datainString);

    const getCols = Object.keys(getData[0]);

    for (let row = 0 ; row < getData.length ; row++ ) {
      innerArr = {};
      for (let col = 0; col < getCols.length; col++) {
         if (getCols[col] === 'status') {
            if (getData[row][getCols[col]] === 'Non Compliant') {
              cellObj = {
                'link': '',
                'properties':
                  {
                      'color': '#d40325'
                  },
                'colName': getCols[col],
                'hasPreImg': false,
                'imgLink': '',
                'text': getData[row][getCols[col]],
                'valText': 2
              };
            } else if (getData[row][getCols[col]] === 'Compliant') {
               cellObj = {
                'link': '',
                'properties':
                  {
                      'color': '#00b946'
                  },
                'colName': getCols[col],
                'hasPreImg': false,
                'valText': 1,
                'imgLink': '',
                'text': getData[row][getCols[col]]
              };
           } else if (getData[row][getCols[col]] === 'Exempted') {

              cellObj = {
              'link': '',
              'properties':
                {
                    'color': '#ffb00d'
                },
              'colName': getCols[col],
              'hasPreImg': false,
              'valText': 3,
              'imgLink': '',
              'text': getData[row][getCols[col]]
              };
           } else  {
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

        } else if (getCols[col].toLowerCase() === 'instance id' || getCols[col].toLowerCase() === 'resourceid' || getCols[col].toLowerCase() === '_resourceid' || getCols[col].toLowerCase() === 'resource id') {
          cellObj = {

              'link': 'true',
              // 'link': '',
              'properties':
                  {
                      'text-transform': 'lowercase',
                      'text-shadow': '0.1px 0'
                  },
              'colName': getCols[col],
              'hasPreImg': false,
              'imgLink': '',
              'text': getData[row][getCols[col]],
              'valText' : getData[row][getCols[col]]
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
    if (this.outerArr.length > getData.length ) {
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

  handlePopClick(rowText) {
        const fileType = 'csv';

        try {

            let queryParams;

            queryParams = {
                'fileFormat': 'csv',
                'serviceId': 3,
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

  goToDetails(row) {
      try {
          this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
          this.router.navigate(['../../', 'assets' , 'assets-details', row.row['Asset Type'].text, row.row['Resource ID'].text], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
      } catch (error) {
          this.errorMessage = this.errorHandling.handleJavascriptError(error);
          this.logger.log('error', error);
      }
  }

  callNewSearch() {
    this.bucketNumber = 0;
    this.currentBucket = [];
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
