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

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { DownloadService } from './../../../shared/services/download.service';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { ToastObservableService } from '../../../post-login-app/common/services/toast-observable.service';
import { RefactorFieldsService } from '../../../shared/services/refactor-fields.service';

@Component({
  selector: 'app-all-certificate-table',
  templateUrl: './all-certificate-table.component.html',
  styleUrls: ['./all-certificate-table.component.css'],
  providers: [CommonResponseService, AutorefreshService]
})
export class AllCertificateTableComponent implements OnInit, OnDestroy {
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
    pageTitle = 'All Certificates';

    private subscriptionToAssetGroup: Subscription;
    private dataSubscription: Subscription;
    private downloadSubscription: Subscription;

    public seekdata = false;
    durationParams: any;
    autoRefresh: boolean;
    paginatorSize = 10;
    totalRows = 0;
    dataTableData: any = [];
    tableDataLoaded = false;
    bucketNumber = 0;
    popRows: any = ['Download Data'];
    currentBucket: any = [];
    firstPaginator = 1;
    lastPaginator: number;
    currentPointer = 0;
    errorValue = 0;
    searchTxt = '';
    showGenericMessage = false;
    constructor(	private commonResponseService: CommonResponseService,
      private assetGroupObservableService: AssetGroupObservableService,
      private autorefreshService: AutorefreshService,
      private logger: LoggerService,
      private errorHandling: ErrorHandlingService,
      private downloadService: DownloadService,
      private toastObservableService: ToastObservableService,
      private refactorFieldsService: RefactorFieldsService ) {


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
    // this.updateComponent();
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
      if (this.selectedAssetGroup !== undefined) {
        /* All functions to get data should go here */
        this.getAllPatchingDetails();
      }
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
    const allcertificateTableUrl = environment.certificateTable.url;
    const allcertificateTableMethod = environment.certificateTable.method;


    this.dataSubscription = this.commonResponseService.getData( allcertificateTableUrl, allcertificateTableMethod, payload, {}).subscribe(
      response => {
          this.showGenericMessage = false;
        try {
          this.errorValue = 1;
          this.showLoader = false;
          this.tableDataLoaded = true;
          this.seekdata = false;
          this.dataTableData = response.data.response;
          this.dataComing = true;
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
      this.errorMessage = error;
      this.getErrorValues();
    });
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
    data.map(function(rowObj) {
      const KeysTobeChanged = Object.keys(rowObj);
      let newObj = {};
      KeysTobeChanged.forEach(element => {
        const elementnew =
          refactoredService.getDisplayNameForAKey(element.toLocaleLowerCase()) || element;
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

    const datainString = JSON.stringify(data);
    const getData = JSON.parse(datainString);

    const getCols = Object.keys(getData[0]);

    for (let row = 0 ; row < getData.length ; row++) {
      innerArr = {};
      for (let col = 0; col < getCols.length; col++) {

        if (getCols[col].toLowerCase() === 'expiringin' || getCols[col].toLowerCase() === 'expiring in') {
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
              'valText': parseInt(getData[row][getCols[col]], 10)
            };
        } else if (getCols[col].toLowerCase() === 'validfrom'  || getCols[col].toLowerCase() === 'valid from' || getCols[col].toLowerCase() === 'validuntil' || getCols[col].toLowerCase() === 'valid until' ) {
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

          // innerArr.push(cellObj);
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

  handlePopClick(rowText) {
        const fileType = 'csv';

        try {

            let queryParams;

            queryParams = {
                'fileFormat': 'csv',
                'serviceId': 5,
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
      this.dataSubscription.unsubscribe();
    } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.getErrorValues();
    }
  }
}
