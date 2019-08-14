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

import { Component, ViewEncapsulation, Input, OnInit, OnChanges, Output, EventEmitter, SimpleChanges, OnDestroy } from '@angular/core';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { Subscription } from 'rxjs/Subscription';
import { DownloadService } from '../../../shared/services/download.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import {RefactorFieldsService} from '../../../shared/services/refactor-fields.service';
import {WorkflowService} from '../../../core/services/workflow.service';
import {ActivatedRoute, Router} from '@angular/router';
import {UtilsService} from '../../../shared/services/utils.service';

@Component({
  selector: 'app-recommand-category',
  templateUrl: './recommand-category.component.html',
  styleUrls: ['./recommand-category.component.css'],
  providers: [CommonResponseService, AutorefreshService],
  encapsulation: ViewEncapsulation.None
})
export class RecommandCategoryComponent implements OnInit, OnChanges, OnDestroy {
  public somedata: any;
  public outerArr: any;
  public allColumns: any;

  selectedAssetGroup: string;
  public apiData: any;
  public applicationValue: any;
  public tableHeaderData: any;

  private subscriptionToAssetGroup: Subscription;
  private dataSubscription: Subscription;
  private downloadSubscription: Subscription;

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
  searchTxt = '';
  showGenericMessage = false;
  @Input() filters: any = {};
  @Input() selectedTab: any = {};
  @Input() general: boolean;
  @Output() monthlySavings = new EventEmitter<any>();
  monthlySavingsTotal = 0;
  errorMessage = 'apiResponseError';
  columnWhiteList = [ 'recommendation', 'Recommendation For', 'Asset Type', 'potential monthly savings'];
  storeRecommendations = {
    'category' : {
      'summary': '',
      'cost_optimizing': '',
      'security': '',
      'performance': '',
      'service_limits': '',
      'fault_tolerance': ''
    }
  };
  constructor(private commonResponseService: CommonResponseService,
    private assetGroupObservableService: AssetGroupObservableService,
    private autorefreshService: AutorefreshService,
    private downloadService: DownloadService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
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

  ngOnChanges(changes: SimpleChanges) {
    try {
      const filterChange = changes['filters'];
      const tabChange = changes['selectedTab'];
      if (filterChange) {
        const cur  = JSON.stringify(filterChange.currentValue);
        const prev = JSON.stringify(filterChange.previousValue);
        if (cur !== prev) {
          this.updateComponent();
          }
        }
        if (tabChange) {
          const cur  = JSON.stringify(tabChange.currentValue);
          const prev = JSON.stringify(tabChange.previousValue);
          if (cur !== prev) {
            this.updateComponent();
            }
          }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  updateComponent() {

    /* All functions variables which are required to be set for component to be reloaded should go here */
    this.outerArr = [];
    this.searchTxt = '';
    this.currentBucket = [];
    this.dataTableData = [];
    this.bucketNumber = 0;
    this.firstPaginator = 1;
    this.currentPointer = 0;
    this.errorValue = 0;
    this.showGenericMessage = false;
    this.monthlySavings.emit({val: 0, status: 0});
    this.getData();
  }

  getData() {
    if (this.selectedAssetGroup !== undefined) {
            /* All functions to get data should go here */
            this.getTableData();
        }
  }

  handlePopClick(rowText) {
        const fileType = 'csv';

        try {

            let queryParams;

            queryParams = {
                'fileFormat': 'csv',
                'serviceId': 16,
                'fileType': fileType
            };

            const downloadRequest =  {
              'ag': this.selectedAssetGroup,
              'filter': {
                'category': this.selectedTab.category,
                'general': this.general
              },
              'from': 0,
              'searchtext': this.searchTxt,
              'size': this.totalRows
            };
            if (this.filters['tags.Application.keyword']) {
              downloadRequest['filter']['application'] = this.filters['tags.Application.keyword'];
            } else if (this.filters['filter'] && this.filters['filter'].includes('Application')) {
                let arr = [];
                arr = this.filters.filter.split('=');
                downloadRequest['filter']['application'] = arr.length > 1 ? arr[1] : '';
            }

            const downloadUrl = environment.download.url;
            const downloadMethod = environment.download.method;

            this.downloadService.requestForDownload(queryParams, downloadUrl, downloadMethod, downloadRequest, this.selectedTab.displayName, this.totalRows);

        } catch (error) {
            this.logger.log('error', error);
        }
  }

    getTableData() {

    if (this.dataSubscription) {
          this.dataSubscription.unsubscribe();
        }
        this.errorValue = 0;
        this.dataTableData = [];
        this.allColumns = [];
      if (this.general) {
        this.columnWhiteList = [ 'recommendation', 'recommended'];
      }
    if (this.general && this.storeRecommendations['category'][this.selectedTab.category]) {
      this.errorValue = 1;
      const response = this.storeRecommendations['category'][this.selectedTab.category];
      this.dataTableData = response.data.response;
      this.processResponse(response);
      return;
    }
    const queryParam = {
      'ag': this.selectedAssetGroup,
      'filter': {
        'category': this.selectedTab.category,
        'general': this.general
      },
      'from': (this.bucketNumber) * this.paginatorSize,
      'searchtext': this.searchTxt,
      'size': this.paginatorSize
    };
    if (this.filters['tags.Application.keyword']) {
      queryParam['filter']['application'] = this.filters['tags.Application.keyword'];
    } else if (this.filters['filter'] && this.filters['filter'].includes('Application')) {
        let arr = [];
        arr = this.filters.filter.split('=');
        queryParam['filter']['application'] = arr.length > 1 ? arr[1] : '';
    }
    const url = environment.recommendations.url;
    const method = environment.recommendations.method;

    this.dataSubscription = this.commonResponseService.getData(url, method, queryParam, {}).subscribe(
      response => {
        this.showGenericMessage = false;
        try {
          this.errorValue = 1;
          this.dataTableData = response.data.response;
          if (this.general && this.bucketNumber === 0) {
            this.storeRecommendations['category'][this.selectedTab.category] = response;
          }
          this.processResponse(response);
        } catch (e) {
          this.errorValue = -1;
          this.errorMessage = 'jsError';
        }
      },
      error => {
        this.monthlySavings.emit({val: '', status: 1});
        this.showGenericMessage = true;
        this.outerArr = [];
        this.errorMessage = 'apiResponseError';
        this.errorValue = -1;
      });
  }

  processResponse(response) {
    if (this.dataTableData.length === 0) {
      this.errorValue = -1;
      this.outerArr = [];
      this.allColumns = [];
      this.totalRows = 0;
      this.errorMessage = 'noDataAvailable';
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
      if (response.data.totalMonthlySavings) {
        this.monthlySavings.emit({val: response.data.totalMonthlySavings, status: 1});
      }else {
        this.monthlySavings.emit({val: '', status: 1});
      }
      this.currentBucket[this.bucketNumber] = updatedResponse;
      this.processData(updatedResponse);
    }
  }

  massageData(data) {
    /*
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
   try {
    let innerArr = {};
    const totalVariablesObj = {};
    let cellObj = {};
    this.outerArr = [];
    data.forEach((element, i) => {
      delete element['Description'];
      if (this.general === false) {
        data[i]['Recommendation For'] = element.recommended + ' out of ' + element.total + ' Assets';
      }
    });
    const getData = data;
    const getCols = Object.keys(getData[0]);

    for ( let row = 0; row < getData.length; row++) {
      innerArr = {};
      for ( let col = 0; col < getCols.length; col++) {
         if (getCols[col].toLowerCase() === 'recommendation') {
            cellObj = {
              'link': 'View Recommendation Details',
              'properties':
                {
                  'color': '',
                  'text-decoration': ''
                },
              'colName': getCols[col],
              'hasPreImg': false,
              'imgLink': '',
              'text': getData[row][getCols[col]],
              'valText': getData[row][getCols[col]]
            };
          } else if (getCols[col].toLowerCase() === 'potential monthly savings') {
            cellObj = {
              'link': '',
              'properties':
                {},
              'colName': getCols[col],
              'hasPreImg': false,
              'imgLink': '',
              'text': '$ ' + getData[row][getCols[col]].toLocaleString(),
              'valText': getData[row][getCols[col]]
              };
          } else if (getCols[col].toLowerCase() === 'recommendation for') {
            cellObj = {
              'link': '',
              'properties':
                {},
              'colName': getCols[col],
              'hasPreImg': false,
              'imgLink': '',
              'text': getData[row][getCols[col]],
              'valText': getData[row]['recommended']
            };
          } else {
            cellObj = {
            'link': '',
            'properties': {
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
      this.storeRecommendations['category'][this.selectedTab.category] = '';
      this.getData();
    }

  }
  searchCalled(search) {
    this.searchTxt = search;
  }
  callNewSearch() {
    this.bucketNumber = 0;
    this.currentBucket = [];
    this.storeRecommendations['category'][this.selectedTab.category] = '';
    this.getData();
  }

    goToDetails(row) {
      try {
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        this.router.navigate(['../recommendations-detail', row.row['Recommendation ID'].text, row.row['recommendation'].text, this.general],
        {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
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
      this.errorValue = -1;
    }
  }

}
