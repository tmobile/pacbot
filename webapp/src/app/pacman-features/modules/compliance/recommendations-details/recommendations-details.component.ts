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
import { environment } from '../../../../../environments/environment';
import { Subscription } from 'rxjs/Subscription';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { Router, ActivatedRoute } from '@angular/router';
import {RefactorFieldsService} from '../../../../shared/services/refactor-fields.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import { DownloadService } from '../../../../shared/services/download.service';
import { UtilsService } from '../../../../shared/services/utils.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { DomainTypeObservableService } from '../../../../core/services/domain-type-observable.service';
@Component({
  selector: 'app-recommendations-details',
  templateUrl: './recommendations-details.component.html',
  styleUrls: ['./recommendations-details.component.css']
})
export class RecommendationsDetailsComponent implements OnInit, OnDestroy {
  AssetGroupSubscription: Subscription;
  domainSubscription: Subscription;
  recommandationsInfoSubscription: Subscription;
  recommandationsDetailsSubscription: Subscription;
  selectedAssetGroup;
  selectedDomain;
  recommendationParams;
  recommendationInfoData;
  recommendationDetailsData;
  public errorMessage: string;
  public responseStatus: any = 0;
  responseStatusInfo = 0;
  showGenericMessage = false;
  allColumns = [];
  public outerArr: any;
  totalRows = 0;
  currentPointer = 0;
  currentBucket: any = [];
  firstPaginator = 1;
  lastPaginator: number;
  paginatorSize = 10;
  bucketNumber = 0;
  pageLevel = 0;
  backButtonRequired;
  popRows: any = ['Download Data'];
  tableDataLoaded = false;
  searchTxt = '';
  public agAndDomain = {};
  constructor(private assetGroupObservableService: AssetGroupObservableService,
              private domainObservableService: DomainTypeObservableService,
              private logger: LoggerService,
              private activRoute: ActivatedRoute,
              private downloadService: DownloadService,
              private errorHandling: ErrorHandlingService,
              private refactorFieldsService: RefactorFieldsService,
              private commonResponseService: CommonResponseService,
              private utils: UtilsService,
              private router: Router,
              private workflowService: WorkflowService) {
      this.AssetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroupName => {
        this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(
          this.pageLevel
        );
        this.selectedAssetGroup = assetGroupName;
        this.agAndDomain['ag'] = this.selectedAssetGroup;
      });
      this.domainSubscription = this.domainObservableService.getDomainType().subscribe(domain => {
        this.selectedDomain = domain;
        this.agAndDomain['domain'] = this.selectedDomain;
        this.recommendationParams = this.activRoute.snapshot.params;
        this.getRecommandationsInfoData();
        this.updateComponent();
      });
  }


  ngOnInit() {
  }

  getRecommandationsInfoData() {
    this.responseStatusInfo = 0;
    if (this.recommandationsInfoSubscription) {
      this.recommandationsInfoSubscription.unsubscribe();
    }

    const recommendationsInfoUrl = environment.recommendationsInfo.url;
    const recommendationsInfoMethod = environment.recommendationsInfo.method;
    const queryParams = {
        'ag': this.selectedAssetGroup,
        'recommendationId': this.recommendationParams['recommendationId'],
        'general': this.recommendationParams['general']
    };

    try {
          this.recommandationsInfoSubscription = this.commonResponseService.getData(recommendationsInfoUrl, recommendationsInfoMethod, {}, queryParams).subscribe(
            response => {
                try {
                    this.recommendationInfoData = response;
                    this.responseStatusInfo = 1;
                } catch (e) {
                    this.errorMessage = 'jsError';
                    this.responseStatusInfo = -1;
                    this.logger.log('error', e);
                }
            },
            error => {
                this.errorMessage = error;
                this.responseStatusInfo = -1;
            });
        } catch (error) {
            this.errorMessage = 'jsError';
            this.responseStatusInfo = -1;
            this.logger.log('error', error);
        }

  }

  getRecommandationsDetailsData() {
    this.responseStatus = 0;
    if (this.recommandationsDetailsSubscription) {
      this.recommandationsDetailsSubscription.unsubscribe();
    }

    const recommendationsdetailsUrl = environment.recommendationsDetails.url;
    const recommendationsdetailsMethod = environment.recommendationsDetails.method;
    const queryParams = {
        'ag': this.selectedAssetGroup,
        'filter': {
          'recommendationId': this.recommendationParams['recommendationId'],
          'general': this.recommendationParams['general']
        },
        'from': (this.bucketNumber) * this.paginatorSize,
        'size': this.paginatorSize,
        'searchtext': this.searchTxt
    };

    try {
        if (this.recommendationParams['tags.Application.keyword']) {
          queryParams['filter']['application'] = this.recommendationParams['tags.Application.keyword'];
        } else if (this.recommendationParams['filter'] && this.recommendationParams.filter.includes('Application')) {
          const filter = this.utils.processFilterObj(
              this.recommendationParams
            );
            queryParams['filter']['application'] = filter['tags.Application.keyword'];
          }
          this.recommandationsInfoSubscription = this.commonResponseService.getData(recommendationsdetailsUrl, recommendationsdetailsMethod, queryParams, {}).subscribe(
            response => {
                try {
                    this.showGenericMessage = false;
                    this.tableDataLoaded = false;
                    this.recommendationDetailsData = response.data.response;
                    if (response.data.response.length === 0) {
                      this.responseStatus = -1;
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

                    const updatedResponse  = this.massageData(response.data.response);
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
            getCols[col].toLowerCase() === 'resourceid'
          ) {
            cellObj = {
              link: 'View Asset Details',
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
          } else if (getCols[col].toLowerCase() === 'severity') {
            if (getData[row][getCols[col]] === 'low') {
              cellObj = {
                link: '',
                properties: {
                  color: '',
                  'text-transform': 'capitalize',
                },
                colName: getCols[col],
                hasPreImg: true,
                imgLink: '',
                text: getData[row][getCols[col]],
                valText: 1,
                statusProp: {
                  'background-color': '#50C17C'
                }
              };
            } else if (getData[row][getCols[col]] === 'medium') {
              cellObj = {
                link: '',
                properties: {
                  color: '',
                  'text-transform': 'capitalize'
                },
                colName: getCols[col],
                hasPreImg: true,
                imgLink: '',
                valText: 2,
                text: getData[row][getCols[col]],
                statusProp: {
                  'background-color': '#289CF7'
                }
              };
            } else if (getData[row][getCols[col]] === 'high') {
              cellObj = {
                link: '',
                properties: {
                  color: '',
                  'text-transform': 'capitalize'
                },
                colName: getCols[col],
                hasPreImg: true,
                valText: 3,
                imgLink: '',
                text: getData[row][getCols[col]],
                statusProp: {
                  'background-color': '#F58544'
                }
              };
            } else {
              cellObj = {
                link: '',
                properties: {
                  color: '',
                  'text-transform': 'capitalize'
                },
                colName: getCols[col],
                hasPreImg: true,
                imgLink: '',
                valText: 4,
                text: getData[row][getCols[col]],
                statusProp: {
                  'background-color': '#F2425F'
                }
              };
            }
          }else if (getCols[col].toLowerCase() === 'monthlysavings') {
              cellObj = {
                link: '',
                properties: {
                  color: '#50c17c',
                },
                colName: getCols[col],
                hasPreImg: false,
                imgLink: '',
                text: '$' + getData[row][getCols[col]],
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
      this.getRecommandationsDetailsData();
    }

  }

  searchCalled(search) {
    this.searchTxt = search;
  }
  callNewSearch() {
    this.bucketNumber = 0;
    this.currentBucket = [];
    this.getRecommandationsDetailsData();
  }

  handlePopClick(rowText) {
    const fileType = 'csv';

    try {

        let queryParams;

        queryParams = {
            'fileFormat': 'csv',
            'serviceId': 15,
            'fileType': fileType,
        };

        const downloadRequest =  {
              'ag': this.selectedAssetGroup,
              'filter': {
                'recommendationId': this.recommendationParams['recommendationId'],
                'general': this.recommendationParams['general']
              },
              'from': 0,
              'size': this.totalRows,
              'searchtext': this.searchTxt
        };
        if (this.recommendationParams['filter'] && this.recommendationParams.filter.includes('Application')) {
          let arr = [];
          arr = this.recommendationParams.filter.split('=');
          downloadRequest['filter']['application'] = arr.length > 1 ? arr[1] : '';
        }

        const downloadUrl = environment.download.url;
        const downloadMethod = environment.download.method;

        this.downloadService.requestForDownload(queryParams, downloadUrl, downloadMethod, downloadRequest, 'Recommendations Detail', this.totalRows);

    } catch (error) {
        this.logger.log('error', error);
    }
}

  updateComponent() {
    /* All functions variables which are required to be set for component to be reloaded should go here */
    this.outerArr = [];
    this.currentBucket = [];
    this.recommendationDetailsData = [];
    this.tableDataLoaded = false;
    this.bucketNumber = 0;
    this.firstPaginator = 1;
    this.currentPointer = 0;
    this.responseStatus = 0;
    this.showGenericMessage = false;
    this.searchTxt = '';
    this.getRecommandationsDetailsData();

  }
  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  goToDetails(row) {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      if (row.col.toLowerCase() === 'resource id') {
        const resourceType = row.row['type'].text;
        const resourceId = encodeURIComponent(row.row['Resource ID'].text);
        this.router.navigate(
          ['../../../../../assets/assets-details', resourceType, resourceId],
          { relativeTo: this.activRoute, queryParams: this.agAndDomain, queryParamsHandling: 'merge' }
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
    // try {
    //   this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
    //   if (row.col.toLowerCase() === 'resource id') {
    //     const resourceType = row.row['type'].text;
    //     const resourceId = encodeURIComponent(row.row['Resource ID'].text);
    //     this.router.navigate(
    //       ['pl', { outlets: { details: ['assets-details', resourceType, resourceId] } }],
    //       { queryParams: this.agAndDomain, queryParamsHandling: 'merge' }
    //     ).then(response => {
    //       this.logger.log('info', 'Successfully navigated to asset details page: ' + response);
    //     })
    //       .catch(error => {
    //         this.logger.log('error', 'Error in navigation - ' + error);
    //       });
    //   }
    // } catch (error) {
    //   this.errorMessage = this.errorHandling.handleJavascriptError(error);
    //   this.logger.log('error', error);
    // }
  }

  ngOnDestroy() {
    try {
      this.AssetGroupSubscription.unsubscribe();
      if (this.recommandationsInfoSubscription) {
        this.recommandationsInfoSubscription.unsubscribe();
      }
      if (this.recommandationsDetailsSubscription) {
        this.recommandationsDetailsSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

}
