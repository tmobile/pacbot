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
import { WorkflowService } from '../../../core/services/workflow.service';
import { HttpService } from '../../../shared/services/http-response.service';
import { Observable } from 'rxjs/Rx';
import { UtilsService } from '../../../shared/services/utils.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-dev-standard-pull-request-age',
  templateUrl: './dev-standard-pull-request-age.component.html',
  styleUrls: ['./dev-standard-pull-request-age.component.css'],
  providers: [AutorefreshService, LoggerService, ErrorHandlingService]
})
export class DevStandardPullRequestAgeComponent implements OnInit, OnDestroy, OnChanges {

  selectedAssetGroup: string;
  private errorMessage = 'apiResponseError';
  private dataSubscription: Subscription;
  private subscriptionToAssetGroup: Subscription;

  private errorValue = 0;
  public graphData: any = [];
  private legend_text: any;
  private total = 0;

  // auto refresh variables
  durationParams: any;
  autoRefresh = false;
  private autorefreshInterval;

  appFilter;

  constructor(
  private assetGroupObservableService: AssetGroupObservableService,
  private autorefreshService: AutorefreshService,
  private logger: LoggerService,
  private errorHandling: ErrorHandlingService,
  private router: Router,
  private activatedRoute: ActivatedRoute,
  private workflowService: WorkflowService,
  private utilsService: UtilsService,
  @Inject(HttpService) private httpService: HttpService) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
          this.selectedAssetGroup = assetGroupName;
          this.updateComponent();
      });
   }

  @Input() filter: any;

  ngOnChanges(changes: SimpleChanges) {
    try {
      const DataChange = changes['filter'];
      this.appFilter = DataChange.currentValue;
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

    const url = environment.devStandardPullRequestAge.url;
    const method = environment.devStandardPullRequestAge.method;

    this.dataSubscription = this.getHTTPData(url, method, payload, queryParam).subscribe(
      response => {
      try {
        if (this.utilsService.checkIfAPIReturnedDataIsEmpty(response)) {
          this.errorValue = -1;
          this.errorMessage = 'noDataAvailable';
      } else {
          this.errorValue = 1;
          this.total = response.total;
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
        y_axis: 'Pull Requests',
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


  redirectGraph(data) {

    if (data.y_domain > 0) {

      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);

      const filter = {
         'resourceType': 'dgtldsgn-pullrequest',
         'daysRange': data.x_domain,
         'tags.Application.keyword': this.appFilter
      };
      const params = this.utilsService.makeFilterObj(filter);
      const apiTarget = {
        'TypeAsset' : 'pull-request-age',
      };
      const newParams = Object.assign(params , apiTarget);
      newParams['mandatory'] = 'resourceType|daysRange';

      this.router.navigate(['../../', 'assets' , 'asset-list'] , {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
    }
  }

  getHTTPData(tableUrl, tableMethod, payload, queryParam): Observable<any> {

    const url = tableUrl;
    const method = tableMethod;
    const payloadPassed = payload;
    const queryParams = queryParam;

    try {
        return this.httpService.getHttpResponse(url, method, payloadPassed, queryParams)
                .map(response => {
                    return response;
                });
    } catch (error) {
        this.errorHandling.handleJavascriptError(error);
    }

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
