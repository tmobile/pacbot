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
  SimpleChanges,
  Input,
  OnChanges
} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { environment } from './../../../../environments/environment';
import { ActivatedRoute, Router } from '@angular/router';
import { LoggerService } from '../../../shared/services/logger.service';
import { WorkflowService } from '../../../core/services/workflow.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { UtilsService } from '../../../shared/services/utils.service';

@Component({
  selector: 'app-digital-dev-strategy-distribution',
  templateUrl: './digital-dev-strategy-distribution.component.html',
  styleUrls: ['./digital-dev-strategy-distribution.component.css'],
  providers: [CommonResponseService]
})
export class DigitalDevStrategyDistributionComponent implements OnInit, OnDestroy, OnChanges {

  private selectedAssetGroup: string;
  private subscriptionToAssetGroup: Subscription;
  private getDevStrategyDistributionService: Subscription;
  widgetWidth = 200;
  widgetHeight = 200;
  MainTextcolor = '#000';
  innerRadious: any = 80;
  outerRadious: any = 50;
  strokeColor = '#fff';
  appFilter;
  private donutData = {};
  public dataComing = true;
  public showLoader = true;
  public seekdata = false;
  public errorMessage: any;
  @Input() filter: any;

  constructor(
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private workflowService: WorkflowService,
    private utils: UtilsService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
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
      this.appFilter = DataChange.currentValue;
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
    this.showLoader = true;
    this.dataComing = false;
    this.seekdata = false;
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

    const url = environment.devStrategyDist.url;
    const method = environment.devStrategyDist.method;
    this.getDevStrategyDistributionService = this.commonResponseService
      .getData(url, method, {}, queryParams)
      .subscribe(
        response => {
          try {
            this.showLoader = false;
            this.seekdata = false;
            this.dataComing = true;
            // check if data is empty
            if (!this.utils.checkIfAPIReturnedDataIsEmpty(response)) {
              this.processData(response);
            } else {
              this.errorMessage = 'noDataAvailable';
              this.getErrorValues();
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

  getErrorValues(): void {
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
  }
  /**
   * @function processData
   * @param data gets the api response
   * @description processes data for donut chart
   */

  processData(data) {
    try {
      // donut chart legends and data value and the total value
      const dataValue = [];
      const legendText = [];
      const total = data.total;

      data = data.distribution;

      // pushing fields to array to plot donut chart

      if (data.gitBased >= 0) {
        dataValue.push(data.gitBased);
        legendText.push('Gitflow');
      }
      if (data.trunkBased >= 0) {
        dataValue.push(data.trunkBased);
        legendText.push('Trunk');
      }
      if (data.featureBased >= 0) {
        dataValue.push(data.featureBased);
        legendText.push('Feature branching');
      }
      if (data.none >= 0) {
        dataValue.push(data.none);
        legendText.push('No branching strategy');
      }
      // dataobj is the format of data expected by dunut chart
      const dataObj = {
        color: ['#59616A', '#00BA9F', '#139EF0', '#F73F5F'],
        data: dataValue,
        legendWithText: legendText,
        legendTextcolor: '#000',
        totalCount: total,
        centerText: 'Total Repositories',
        link: false,
        styling: {
          cursor: 'text'
        }
      };
      // storing the final data in donutData variable to pass it to the donut chart component
      this.donutData = dataObj;
    } catch (e) {
      this.logger.log('error', e);
    }

  }

  handleLink(event) {
    this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);

      const stateObj = {
        'Gitflow' : 'Gitflow',
        'Trunk' : 'Trunk',
        'Feature branching': 'FeatureBranching',
        'No branching strategy': 'None'
      };
      const filter = {
         'strategyType': stateObj[event] || event,
         'resourceType': 'dgtldsgn-branch',
         'branchingStrategyType': 'repositories',
         'tags.Application.keyword': this.appFilter
      };
      const params = this.utils.makeFilterObj(filter);
      const apiTarget = {
        'TypeAsset' : 'branching-strategy'
      };
      const newParams = Object.assign(params, apiTarget);
      newParams['mandatory'] = 'resourceType|strategyType|branchingStrategyType';
      this.router.navigate(['../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
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
