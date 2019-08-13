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

import { Component, OnInit, Inject, OnDestroy, Input, SimpleChanges, OnChanges } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { HttpService } from '../../../shared/services/http-response.service';
import { Observable } from 'rxjs/Rx';
import { UtilsService } from '../../../shared/services/utils.service';

@Component({
  selector: 'app-dev-standard-stale-branch-age',
  templateUrl: './dev-standard-stale-branch-age.component.html',
  styleUrls: ['./dev-standard-stale-branch-age.component.css'],
  providers: [AutorefreshService, LoggerService, ErrorHandlingService]
})
export class DevStandardStaleBranchAgeComponent implements OnInit, OnDestroy , OnChanges {

  selectedAssetGroup: string;
  private errorMessage = 'apiResponseError';
  private dataSubscription: Subscription;
  private subscriptionToAssetGroup: Subscription;

  private errorValue = 0;
  public graphData: any = [];
  private legend_text: any;

  // auto refresh variables
  durationParams: any;
  autoRefresh = false;
  private autorefreshInterval;

  @Input() filter: any;

  constructor(
  private assetGroupObservableService: AssetGroupObservableService,
  private autorefreshService: AutorefreshService,
  private logger: LoggerService,
  private errorHandling: ErrorHandlingService,
  private utilsService: UtilsService,
  @Inject(HttpService) private httpService: HttpService ) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
          this.selectedAssetGroup = assetGroupName;
          this.updateComponent();
      });
   }

   ngOnChanges (changes: SimpleChanges) {
     try {
       const DataChange = changes['filter'];
       if (DataChange) {
         const cur  = JSON.stringify(DataChange.currentValue);
         const prev = JSON.stringify(DataChange.previousValue);
         if ((cur !== prev)) {
           this.getData();
         }
       }
     } catch (error) {
       this.errorValue = -1;
       this.errorMessage = 'jsError';
     }
   }

  ngOnInit() {
    this.durationParams = this.autorefreshService.getDuration();
      this.durationParams = parseInt(this.durationParams, 10);
      this.autoRefresh = this.autorefreshService.autoRefresh;
      const afterLoad = this;
      if (this.autoRefresh !== undefined) {
        if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {

          this.autorefreshInterval = setInterval(function(){
            afterLoad.updateComponent();
          }, this.durationParams);
        }
      }
  }

  updateComponent() {
    this.getData();
  }

  getData() {
    if (this.dataSubscription) {
        this.dataSubscription.unsubscribe();
      }
    const payload = {};
    const queryParam = {
      'ag': this.selectedAssetGroup,
      'application': this.filter
    };
    this.errorValue = 0;

    const url = environment.devStandardBranchAgeRange.url;
    const method = environment.devStandardBranchAgeRange.method;

    this.dataSubscription = this.getHTTPData(url, method, payload, queryParam).subscribe(
      response => {
      try {
        if (this.utilsService.checkIfAPIReturnedDataIsEmpty(response)) {
          this.errorValue = -1;
          this.errorMessage = 'noDataAvailable';
        } else {
          this.errorValue = 1;
          this.processGraphData(response.response);
        }

      } catch (e) {
        this.errorValue = -1;
        this.errorMessage = 'jsError';
        this.logger.log('error', e);
      }
    },
    error => {
      this.errorValue = -1;
      this.errorMessage = 'apiResponseError';
      this.logger.log('error', error);
    });
  }

  processGraphData(data): void {
      const formatted_obj = [];
      const barColor = ['#00B946', '#26BA9D', '#F75C03', '#F2425F', '#D40325'];
      let i = 0;
      const obj =  Object.keys(data).map(function(key) {
        if (barColor[i] === undefined) {
          i = 0;
        }
        formatted_obj.push({
          'x_domain': key,
          'y_domain': data[key],
          'bar_color': barColor[i]
        });
        i++;
      });

      const legend = {
        y_axis: 'Stale Branches',
        x_axis: 'Age (days)'
      };
      this.graphData = formatted_obj;
      this.legend_text = legend;

  }

  onResize() {
    this.updateComponent();
  }

  // assign error values...

  getErrorValues(message?: any ): void {
    this.errorValue = -1;
    if (message) {
      this.errorMessage = message;
    }
  }

  getHTTPData(tableUrl, tableMethod, payload, queryParam): Observable<any> {

    const url = tableUrl;
    const method = tableMethod;
    const queryParams = queryParam;

    try {
        return this.httpService.getHttpResponse(url, method, payload, queryParams)
                .map(response => {
                    return response;
                });
    } catch (error) {
        this.errorHandling.handleJavascriptError(error);
    }

  }

  redirectGraph(event) {
  }

  ngOnDestroy() {
    try {
      if (this.subscriptionToAssetGroup) {
        this.subscriptionToAssetGroup.unsubscribe();
      }
      if (this.dataSubscription) {
        this.dataSubscription.unsubscribe();
      }
      clearInterval(this.autorefreshInterval);
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }

}
