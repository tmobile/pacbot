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
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from './../../../../environments/environment';
import { UtilsService } from '../../../shared/services/utils.service';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { WorkflowService } from '../../../core/services/workflow.service';

@Component({
  selector: 'app-digital-application-distribution',
  templateUrl: './digital-application-distribution.component.html',
  styleUrls: ['./digital-application-distribution.component.css'],
  providers: [CommonResponseService]
})
export class DigitalApplicationDistributionComponent
  implements OnInit, OnDestroy, OnChanges {
  private selectedAssetGroup: string;
  private subscriptionToAssetGroup: Subscription;
  private getDevStrategyDistributionService: Subscription;
  widgetWidth = 200;
  widgetHeight = 200;
  MainTextcolor = '#000';
  innerRadious: any = 80;
  outerRadious: any = 50;
  strokeColor = '#fff';
  private donutData = {};
  public dataComing = true;
  public showLoader = true;
  public seekdata = false;
  appFilter;
  public errorMessage: any;
  @Input() filter: any;
  public graphData: any = [];
  private legend_text: any;
  private repowithoutApplication;
  private total;
  private barChartHeight = 200;

  constructor(
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private utils: UtilsService,
    private workflowService: WorkflowService,
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

  ngOnChanges (changes: SimpleChanges) {
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

    const url = environment.devApplicationDist.url;
    const method = environment.devApplicationDist.method;
    this.getDevStrategyDistributionService = this.commonResponseService
      .getData(url, method, {}, queryParams)
      .subscribe(
        response => {
          try {
            this.showLoader = false;
            this.seekdata = false;
            this.dataComing = true;

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
   * @description processes data for bar chart
   */
  processData(data) {
    try {
      this.repowithoutApplication = data.repowithoutApplication;
      this.total = data.total;
      data = data.distribution;

      const formatted_obj = [];
      const barColor = ['#59616A', '#00BA9F', '#139EF0', '#F73F5F'];
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
        y_axis: 'Applications',
        x_axis: 'Strategies'
      };
      this.graphData = formatted_obj;
      this.legend_text = legend;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  totalClicked(cnt) {
    const data = {
      y_domain: cnt,
      x_domain: 'repoWithoutApplication'
    };
    this.redirectGraph(data);
  }

  redirectGraph(data) {

    if (data.y_domain > 0) {

      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);

      const prStateObj = {
        'gitflow' : 'Gitflow',
        'trunk' : 'Trunk',
        'featureBranching': 'FeatureBranching',
        'none': 'None',
        'repoWithoutApplication': 'repoWithoutApplication'
      };
      const filter = {
         'strategyType': prStateObj[data.x_domain] || data.x_domain,
         'branchingStrategyType': 'applications',
         'resourceType': 'dgtldsgn-branch',
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
