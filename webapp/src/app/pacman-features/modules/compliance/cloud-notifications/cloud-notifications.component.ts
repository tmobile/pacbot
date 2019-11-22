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
import { environment } from './../../../../../environments/environment';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { UtilsService } from '../../../../shared/services/utils.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { CommonResponseService } from './../../../../shared/services/common-response.service';
import { DownloadService } from '../../../../shared/services/download.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { RouterUtilityService } from '../../../../shared/services/router-utility.service';
import { FilterManagementService } from '../../../../shared/services/filter-management.service';

@Component({
  selector: 'app-cloud-notifications',
  templateUrl: './cloud-notifications.component.html',
  styleUrls: ['./cloud-notifications.component.css'],
  providers: [
    LoggerService
  ]
})

export class CloudNotificationsComponent implements OnInit, OnDestroy {

  constructor(
    private assetGroupObservableService: AssetGroupObservableService,
    private router: Router,
    private utils: UtilsService,
    private logger: LoggerService,
    private workflowService: WorkflowService,
    private commonResponseService: CommonResponseService,
    private downloadService: DownloadService,
    private routerUtilityService: RouterUtilityService,
    private activatedRoute: ActivatedRoute,
    private filterManagementService: FilterManagementService
  ) {
    this.currentPageLevel = this.routerUtilityService.getpageLevel(this.router.routerState.snapshot.root);
    this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
      this.pageLevel
    );
  }

  popRows = ['Download Data'];
  tabSelected = 'asset';
  backButtonRequired;
  assetGroupSubscription: Subscription;
  dataSubscription: Subscription;
  pageLevel = 0;
  summarySubscription: Subscription;
  selectedAssetGroup;
  currentPageLevel = 0;
  filtersObj = {};
  tilesObj = [
    {
      title: 'Autofixes',
      icon: '../assets/icons/auto-fix-enabled.svg',
      value: -1,
      active: false,
      key: 'Autofix'
    },
    {
      title: 'Scheduled',
      icon: '../assets/icons/scheduled.svg',
      value: -1,
      active: false,
      key: 'scheduledChange'
    },
    {
      title: 'Notifications',
      icon: '../assets/icons/alarm.svg',
      value: -1,
      active: false,
      key: 'accountNotification'
    },
    {
      title: 'Issues',
      icon: '../assets/icons/critical.svg',
      value: -1,
      active: false,
      key: 'issue'
    }
  ];
  paginatorSize = 25;
  totalRows = 0;
  bucketNumber = 0;
  currentBucket: any = [];
  firstPaginator = 1;
  lastPaginator: number;
  currentPointer = 0;
  prevFilter = {};
  outerArr = [];
  allColumns = [];
  errorValue = 0;
  summaryValue = 0;
  errorMsg = 'apiResponseError';
  searchTxt = '';
  filter = {
    'eventtypecategory': ''
  };
  filterArray = [];

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(params => {
      this.routerParam();
    });
    this.assetGroupSubscription = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        this.selectedAssetGroup = assetGroupName;
        if (this.selectedAssetGroup.match('azure')) {
          setTimeout(() => {
            this.router.navigate(['pl', 'compliance', 'compliance-dashboard'], {
              queryParamsHandling: 'merge'
            });
          }, 10);
        } else {
          this.calibrateFilter();
          this.getSummary();
          this.updateComponent();
        }
    });
  }

  routerParam() {
    try {
      const currentQueryParams = this.routerUtilityService.getQueryParametersFromSnapshot(this.router.routerState.snapshot.root);
      if (currentQueryParams) {

        this.filtersObj = this.utils.processFilterObj(
          currentQueryParams
        );
        delete this.filter['eventstatus'];
        delete this.filter['_resourceid'];
        Object.assign(this.filter, this.utils.processFilterObj(
          currentQueryParams
        ));

        this.filterArray = this.filterManagementService.getFilterArray(this.utils.processFilterObj(
          currentQueryParams
        ));

        if (JSON.stringify(this.prevFilter) !== JSON.stringify(this.filtersObj)) {
          this.prevFilter = JSON.parse(JSON.stringify(this.filtersObj));
          this.getSummary();
          this.updateComponent();
        }
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  deleteFilters(event?) {
    try {
        if (!event) {
            this.filterArray = [];
        } else {
            if (event.clearAll) {
                this.filterArray = [];
                event.array.forEach(element => {
                  delete this.filter[element.filterkey];
                  delete this.filtersObj[element.filterkey];
                });
            } else {
                this.filterArray.splice(event.index, 1);
                delete this.filter[event.array.filterkey];
                delete this.filtersObj[event.array.filterkey];
            }
            this.router.navigate([], {
              relativeTo: this.activatedRoute,
              queryParams: this.filterArray.length ? this.utils.makeFilterObj(this.filtersObj) : {filter: ''},
              queryParamsHandling: 'merge'
            });
        }
    } catch (error) {
      this.logger.log('error', error);
    }
}

  calibrateFilter () {
    const arr = [];
    for (let i = 0 ; i < this.tilesObj.length; i++) {
      if (this.tilesObj[i].active) {
        arr.push(this.tilesObj[i].key);
      }
    }
    this.filter['eventtypecategory'] =  this.utils.arrayToCommaSeparatedString(arr);
  }

  handleTileClick(val, i) {
    val.active = !val.active;
    this.calibrateFilter();
    this.updateComponent();
  }

  contextChange(val) {
    if (this.tabSelected !== val) {
      this.tabSelected = val;
      for (let j = 0; j < this.tilesObj.length; j++) {
        this.tilesObj[j].active = false;
      }
      this.calibrateFilter();
      this.getSummary();
      this.updateComponent();
    }
  }

  toggleSlider() {
    if (this.tabSelected === 'general') {
      return 'asset';
    } else {
      return 'general';
    }
  }

  updateComponent() {
    this.outerArr = [];
    this.searchTxt = '';
    this.currentBucket = [];
    this.bucketNumber = 0;
    this.firstPaginator = 1;
    this.currentPointer = 0;
    this.errorMsg = 'apiResponseError';
    this.allColumns = [];
    this.getData();
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }
  }


  getSummary() {
    if (this.summarySubscription) {
      this.summarySubscription.unsubscribe();
    }
    this.summaryValue = 0;
    const queryParam = {
      'ag': this.selectedAssetGroup,
      'global': this.tabSelected === 'general',
      'resourceId': this.filter['_resourceid'],
      'eventStatus': this.filter['eventstatus']
    };
    const url = environment.cloudNotifSummary.url;
    const method = environment.cloudNotifSummary.method;
    const payload = {};
    this.summarySubscription = this.commonResponseService.getData( url, method, payload, queryParam).subscribe(
      response => {
        try {
          this.summaryValue = 1;
          this.tilesObj[0].value = response[0].autofixCount;
          this.tilesObj[1].value = response[0].eventscheduledCount;
          this.tilesObj[2].value = response[0].eventNotificationCount;
          this.tilesObj[3].value = response[0].evnetIssuesCount;
        } catch (e) {
            this.summaryValue = -1;
            this.logger.log('error', e);
        }
    },
    error => {
      this.summaryValue = -1;
      this.logger.log('error', error);
    });
  }

  getData() {
    if (this.dataSubscription) {
      this.dataSubscription.unsubscribe();
    }
    this.errorValue = 0;
    const payload = {
      'ag': this.selectedAssetGroup,
      'filter': this.filter,
      'from': (this.bucketNumber) * this.paginatorSize,
      'searchtext': this.searchTxt,
      'size': this.paginatorSize
    };
    const TableUrl = environment.cloudNotifications.url;
    const TableMethod = environment.cloudNotifications.method;
    const queryParam = {
      global: this.tabSelected === 'general'
    };
    this.dataSubscription = this.commonResponseService.getData( TableUrl, TableMethod, payload, queryParam).subscribe(
      response => {
        try {
          this.errorValue = 1;
          if (response.data.response.length === 0) {
            this.errorValue = -1;
            this.errorMsg = 'noDataAvailable';
          }
          this.totalRows = response.data.total;
          if (response.data.response.length > 0) {
            this.firstPaginator = (this.bucketNumber * this.paginatorSize) + 1;
            this.lastPaginator = (this.bucketNumber * this.paginatorSize) + this.paginatorSize;
            this.currentPointer = this.bucketNumber;
            if (this.lastPaginator > this.totalRows) {
              this.lastPaginator = this.totalRows;
            }
            const updatedResponse = this.utils.massageTableData(response.data.response);
            this.currentBucket[this.bucketNumber] = updatedResponse;
            this.processData(updatedResponse);
          }
        } catch (e) {
            this.errorValue = -1;
            this.logger.log('error', e);
            this.errorMsg = 'jsError';
        }
    },
    error => {
      this.errorValue = -1;
      this.logger.log('error', error);
      this.errorMsg = 'apiResponseError';
    });
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
        if (getCols[col].toLowerCase() === 'affected resources' || getCols[col].toLowerCase() === 'event') {
          cellObj = {
            'link': 'Event Details',
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
        } else if (getCols[col].toLowerCase() === 'start time' || getCols[col].toLowerCase() === 'end time') {
          cellObj = {
            'link': '',
            'properties':
              {
                  'color': ''
              },
            'colName': getCols[col],
            'hasPreImg': false,
            'imgLink': '',
            'text': getData[row][getCols[col]] ? new Date(getData[row][getCols[col]]).toLocaleString() : '',
            'valText': getData[row][getCols[col]] ? new Date(getData[row][getCols[col]]).getTime() : ''
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
      if (row.col.toLowerCase() === 'affected resources' || row.col.toLowerCase() === 'event') {
        const arnId = encodeURIComponent(row.row['eventarn'].text);
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        this.router.navigate(
          ['pl/compliance/event-details', arnId],
          { queryParams: {'global': this.tabSelected === 'general', autofix: row.row['Event Category'].text.toLowerCase() === 'autofix'}, queryParamsHandling: 'merge' }
        ).then(response => {
          this.logger.log('info', 'Successfully navigated to details page: ' + response);
        })
          .catch(error => {
            this.logger.log('error', 'Error in navigation - ' + error);
          });
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  callHelp() {
    const newParams = { widgetId: 'w8' };
    this.router.navigate(
      ['/pl', { outlets: { helpTextModal: ['help-text'] } }],
      { queryParams: newParams, queryParamsHandling: 'merge' }
    );
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
            'serviceId': this.tabSelected === 'general' ? 18 : 17,
            'fileType': fileType
        };
        const downloadRequest =  {
              'ag': this.selectedAssetGroup,
              'filter': this.filter,
              'from': 0,
              'searchtext': this.searchTxt,
              'size': this.totalRows
        };
        const downloadUrl = environment.download.url;
        const downloadMethod = environment.download.method;
        const downloadName = 'Event Logs';
        this.downloadService.requestForDownload(
          queryParams,
          downloadUrl,
          downloadMethod,
          downloadRequest,
          downloadName,
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
    if (this.assetGroupSubscription) {
      this.assetGroupSubscription.unsubscribe();
    }
    if (this.dataSubscription) {
      this.dataSubscription.unsubscribe();
    }
    if (this.summarySubscription) {
      this.summarySubscription.unsubscribe();
    }
  }
}
