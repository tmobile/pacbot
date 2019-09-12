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

import { Component, OnInit, Input, OnChanges } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { environment } from '../../../../../environments/environment';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { RefactorFieldsService } from '../../../../shared/services/refactor-fields.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { DownloadService } from '../../../../shared/services/download.service';

@Component({
    selector: 'app-cis-violating-assets',
    templateUrl: './cis-violating-assets.component.html',
    styleUrls: ['./cis-violating-assets.component.css']
})
export class CisViolatingAssetsComponent implements OnInit, OnChanges {
    public errorMessage: string;
    public outerArr: any;
    public pageTitle = 'Violating Assets';
    public pageRuleTitle;
    private selectedRule;
    violatingAssetsData;
    searchTxt = '';
    totalRows = 0;
    bucketNumber = 0;
    responseStatus = 0;
    currentPointer = 0;
    firstPaginator = 1;
    lastPaginator: number;
    paginatorSize = 10;
    showGenericMessage = false;
    tableDataLoaded = false;
    selectedAssetGroup: any;
    allColumns = [];
    currentBucket: any = [];
    violatingAssetsSubscription: Subscription;
    assetGroupSubscription: Subscription;
    popRows = ['Download Data'];
    @Input() RuleId;

    constructor(private activeRoute: ActivatedRoute,
        private router: Router,
        private assetGroupObservableService: AssetGroupObservableService,
        private logger: LoggerService,
        private commonResponseService: CommonResponseService,
        private errorHandling: ErrorHandlingService,
        private workflowService: WorkflowService,
        private refactorFieldsService: RefactorFieldsService,
        private downloadService: DownloadService
    ) {
        this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroupName => {
            this.selectedAssetGroup = assetGroupName;
        });
    }


    getCISViolatingAssets() {
        this.responseStatus = 0;
        if (this.violatingAssetsSubscription) {
            this.violatingAssetsSubscription.unsubscribe();
        }
        const cisViolatingAssetsUrl = environment.cisViolatingAssets.url;
        const cisViolatingAssetsMethod = environment.cisViolatingAssets.method;
        const bodyObject = {
            'ag': this.selectedAssetGroup,
            'filter': {
                'ruleId': this.RuleId
            },
            'from': (this.bucketNumber) * this.paginatorSize,
            'key': '',
            'searchtext': this.searchTxt,
            'size': this.paginatorSize
        };

        try {
            this.violatingAssetsSubscription = this.commonResponseService.getData(cisViolatingAssetsUrl, cisViolatingAssetsMethod, bodyObject, {}).subscribe(
                response => {
                    try {
                        const data = response.data;
                        this.showGenericMessage = false;
                        this.tableDataLoaded = false;
                        this.violatingAssetsData = data.response;
                        if (data.response.length === 0) {
                            this.responseStatus = -1;
                            this.outerArr = [];
                            this.allColumns = [];
                            this.totalRows = 0;
                            this.errorMessage = 'noDataAvailable';
                        }

                        if (data.response.length > 0) {
                            this.totalRows = data.total;
                            this.firstPaginator = (this.bucketNumber * this.paginatorSize) + 1;
                            this.lastPaginator = (this.bucketNumber * this.paginatorSize) + this.paginatorSize;

                            this.currentPointer = this.bucketNumber;

                            if (this.lastPaginator > this.totalRows) {
                                this.lastPaginator = this.totalRows;
                            }

                            const updatedResponse = this.massageData(data.response);
                            this.currentBucket[this.bucketNumber] = updatedResponse;
                            this.processData(updatedResponse);
                            this.responseStatus = 1;
                        }
                    } catch (e) {
                        this.errorMessage = 'jsError';
                        this.responseStatus = -1;
                        this.logger.log('error', e);
                    }
                },
                error => {
                    this.responseStatus = -1;
                    this.errorMessage = 'apiResponseError';
                });
        } catch (error) {
            this.showGenericMessage = true;
            this.errorMessage = 'jsError';
            this.responseStatus = -1;
            this.logger.log('error', error);
        }
    }

    goToDetails(row) {
      try {
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        if (row.col.toLowerCase() === 'resource id') {
          const resourceType = row.row['Asset Type'].text;
          const resourceId = encodeURIComponent(row.row['Resource ID'].text);
          this.router.navigate(
            ['pl', { outlets: { details: ['assets-details', resourceType, resourceId] } }],
            { queryParams: this.selectedAssetGroup, queryParamsHandling: 'merge' }
          ).then(response => {
            this.logger.log('info', 'Successfully navigated to asset details page: ' + response);
          })
            .catch(error => {
              this.logger.log('error', 'Error in navigation - ' + error);
            });
        }
      } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.logger.log('error', error);
      }
    }

    massageData(data) {
        /*
           * the function replaces keys of the table header data to a readable format
         */
        const refactoredService = this.refactorFieldsService;
        const newData = [];
        data.map(function (eachRow) {
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
            this.getCISViolatingAssets();
        }
    }

    updatePaginator(event) {
        if (event !== this.paginatorSize) {
          this.paginatorSize = event;
          this.updateComponent();
        }
    }

    processData(data) {
        try {
            let innerArr = {};
            const totalVariablesObj = {};
            let cellObj = {};
            this.outerArr = [];
            const getData = data;
            let getCols;
            if (getData.length) {
                getCols = Object.keys(getData[0]);
            }

            for (let row = 0; row < getData.length; row++) {
                innerArr = {};
                for (let col = 0; col < getCols.length; col++) {
                    if (
                        getCols[col].toLowerCase() === 'resource id' ||
                        getCols[col].toLowerCase() === '_resourceid'
                    ) {
                        cellObj = {
                            link: 'View Asset details',
                            properties: {
                                'text-shadow': '0.1px 0',
                                'text-transform': 'lowercase'
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
                                color: '',
                            },
                            colName: getCols[col],
                            hasPreImg: false,
                            imgLink: '',
                            text: getData[row][getCols[col]],
                            valText: getData[row][getCols[col]]
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

    searchCalled(search) {
      this.searchTxt = search;
    }

    callNewSearch() {
      this.bucketNumber = 0;
      this.currentBucket = [];
      this.updateComponent();
    }

    handlePopClick(rowText) {
        const fileType = 'csv';
        try {
          let queryParams;

          queryParams = {
            fileFormat: 'csv',
            serviceId: 24,
            fileType: fileType,
          };

          const downloadRequest = {
            ag: this.selectedAssetGroup,
            filter: {'ruleId': this.RuleId},
            from: 0,
            searchtext: this.searchTxt,
            size: this.totalRows
          };

          const downloadUrl = environment.download.url;
          const downloadMethod = environment.download.method;

          this.downloadService.requestForDownload(
            queryParams,
            downloadUrl,
            downloadMethod,
            downloadRequest,
            'CIS Violating Assets', this.totalRows);

        } catch (error) {
          this.logger.log('error', error);
        }
      }

    updateComponent() {
        /* All functions variables which are required to be set for component to be reloaded should go here */
        this.outerArr = [];
        this.currentBucket = [];
        this.violatingAssetsData = [];
        this.tableDataLoaded = false;
        this.bucketNumber = 0;
        this.firstPaginator = 1;
        this.currentPointer = 0;
        this.responseStatus = 0;
        this.showGenericMessage = false;
        this.getCISViolatingAssets();
    }

    ngOnInit() {
        this.pageRuleTitle = this.activeRoute.snapshot.queryParamMap.get('benchmark');
        this.selectedRule = this.activeRoute.snapshot.queryParamMap.get('ruleId');
        this.updateComponent();
    }
    ngOnChanges() {
        this.updateComponent();
    }
}
