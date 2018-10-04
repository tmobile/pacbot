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
  SimpleChanges,
  OnChanges
} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { UtilsService } from '../../../shared/services/utils.service';

@Component({
  selector: 'app-dev-standard-total-stale-branches',
  templateUrl: './dev-standard-total-stale-branches.component.html',
  styleUrls: ['./dev-standard-total-stale-branches.component.css'],
  providers: [
    CommonResponseService,
    LoggerService,
    ErrorHandlingService
  ]
})
export class DevStandardTotalStaleBranchesComponent implements OnInit, OnDestroy, OnChanges {
  private selectedAssetGroup: string;
  private subscriptionToAssetGroup: Subscription;
  private getDevStrategyDistributionService: Subscription;
  widgetWidth = 200;
  widgetHeight = 250;
  MainTextcolor = '#000';
  innerRadious: any = 80;
  outerRadious: any = 50;
  strokeColor = '#fff';
  private donutData = {};
  public errorMessage: any;
  private errorValue = 0;
  @Input() filter: any;
  constructor(
    private errorHandling: ErrorHandlingService,
    private utils: UtilsService,
    private assetGroupObservableService: AssetGroupObservableService,
    private commonResponseService: CommonResponseService
  ) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        this.selectedAssetGroup = assetGroupName;
        this.updateComponent();
      });
  }


  ngOnChanges(changes: SimpleChanges) {
    try {
      const DataChange = changes['filter'];
      if (DataChange) {
        const cur = JSON.stringify(DataChange.currentValue);
        const prev = JSON.stringify(DataChange.previousValue);
        if (cur !== prev) {
          this.updateComponent();
        }
      }
    } catch (error) {
      this.errorMessage = 'jsError';
          this.getErrorValues();
    }
  }
  ngOnInit() {}
  updateComponent() {
    /* All functions variables which are required to be set for component to be reloaded should go here */
    this.errorValue = 0;
    this.getData();
  }

  getData() {
    /* All functions to get data should go here */
    this.getDevStrategyDistribution();
  }
  getDevStrategyDistribution() {
    if (this.getDevStrategyDistributionService) {
        this.getDevStrategyDistributionService.unsubscribe();
      }
    const queryParams = {
      ag: this.selectedAssetGroup,
      application: this.filter
    };

    const url = environment.devTotalStaleBranches.url;
    const method = environment.devTotalStaleBranches.method;
    this.getDevStrategyDistributionService = this.commonResponseService
      .getData(url, method, {}, queryParams)
      .subscribe(
        response => {
          try {
            if (this.utils.checkIfAPIReturnedDataIsEmpty(response)) {
              this.errorValue = -1;
              this.errorMessage = 'noDataAvailable';
            } else {
              this.errorValue = 1;
              this.processData(response);
            }
          } catch (e) {
            this.errorMessage = this.errorHandling.handleJavascriptError(e);
            this.getErrorValues();
          }
        },
        error => {
          this.errorMessage = error;
          this.getErrorValues();
        }
      );
  }
  // assign error values...

  getErrorValues(message?: any): void {
    this.errorValue = -1;
    if (message) {
      this.errorMessage = message;
    }
  }
      /**
     * @function processData
     * @param data gets the api response
     * @description processes data for donut chart
     */
    processData(data) {
      // expected data format

      // donut chart legends and data value
      const dataValue = [];
      const legendText = [];
      // push the required data to the declared array
      if (data.response.activeBranches >= 0) {
        dataValue.push(data.response.activeBranches);
        legendText.push('active Branches');
      }
      if (data.response.staleBranches >= 0) {
        dataValue.push(data.response.staleBranches);
        legendText.push('stale Branches');
      }

      // dataobj is the format of data expected by dunut chart

      const dataObj = {
          'color': ['#00BA9F', '#F73F5F', '#59616A', '#139EF0'],
          'data': dataValue,
          'legendWithText' : legendText,
          'legendTextcolor': '#000',
          'totalCount': data.total,
          'centerText' : 'Total Branches',
          'link': false,
          'styling': {
           'cursor': 'text'
          }
       };
       // storing the final data in donutData variable to pass it to the donut chart component
       this.donutData = dataObj;
  }
  ngOnDestroy() {
    if (this.getDevStrategyDistributionService) {
        this.getDevStrategyDistributionService.unsubscribe();
      }
      if (this.subscriptionToAssetGroup) {
        this.subscriptionToAssetGroup.unsubscribe();
      }
  }
}

