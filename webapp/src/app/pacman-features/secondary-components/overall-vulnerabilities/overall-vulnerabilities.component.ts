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
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { Router, ActivatedRoute } from '@angular/router';
import { LoggerService } from '../../../shared/services/logger.service';
import { UtilsService } from '../../../shared/services/utils.service';
import { WorkflowService } from '../../../core/services/workflow.service';
import { CommonResponseService } from '../../../shared/services/common-response.service';

@Component({
  selector: 'app-overall-vulnerabilities',
  templateUrl: './overall-vulnerabilities.component.html',
  styleUrls: ['./overall-vulnerabilities.component.css'],
  providers: [AutorefreshService]
})
export class OverallVulnerabilitiesComponent implements OnInit, OnDestroy {

  subscriptionToAssetGroup: Subscription;
  dataSubscription: Subscription;
  selectedAssetGroup;
  durationParams;
  autoRefresh;
  autorefreshInterval;
  errorVal = 0;
  errorMessage = 'apiResponseError';
  urlToRedirect;
  routeTo = 'vulnerabilities';
  vulnData;
  donutData = {};
  widgetWidth = 210;
  widgetHeight = 250;
  innerRadius: any = 0;
  selectedLink = 0;
  outerRadius: any = 50;
  errorSumVal = 0;
  cntInterval;
  selectedLevel = 0;
  lastLevelData;
  lastLevelSelectedKeys;
  modifiedResponse;
  selectedGraph = 'total';
  colorsData = {
    inscope: '#00B946',
    exempted: '#BA808A',
    S3: '#ffe003',
    S4: '#f75c03',
    S5: '#da0c0c',
    compliant: '#00B946',
    noncompliant: '#E60127',
    scanned: '#00B946',
    unscanned: '#BA808A'
  };
  donutObj;
  linksData = [{name: 'Total', level: 0, key: 'total'}, {name: 'Inscope', level: 1, key: 'inscope'}, {name: 'Scanned', level: 2, key: 'scanned'}, {name: 'Non Compliant', level: 3, key: 'noncompliant'}, {name: 'Compliant', level: 3, key: 'compliant'}];

  constructor(private commonResponseService: CommonResponseService,
              private assetGroupObservableService: AssetGroupObservableService,
              private autorefreshService: AutorefreshService,
              private logger: LoggerService,
              private router: Router,
              private utils: UtilsService,
              private activatedRoute: ActivatedRoute,
              private workflowService: WorkflowService) {
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
    this.urlToRedirect = this.router.routerState.snapshot.url;
    const afterLoad = this;
    if (this.autoRefresh !== undefined) {
      if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {
        this.autorefreshInterval = setInterval(function() {
          afterLoad.getData();
        }, this.durationParams);
      }
    }
  }

  navigatePage() {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      const eachParams = {};
      const newParams = this.utils.makeFilterObj(eachParams);
      if (this.routeTo !== undefined ) {
        this.router.navigate(['../vulnerabilities-compliance', this.routeTo], { relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  updateComponent() {
    this.errorVal = 0;
    this.errorSumVal = 0;
    this.selectedLink = 0;
    this.selectedGraph = 'total';
    this.donutData = {};
    this.selectedLevel = 0;
    if (this.cntInterval) {
      clearInterval(this.cntInterval);
    }
    this.getData();
  }

  clearLinkInterval() {
    if (this.cntInterval) {
      clearInterval(this.cntInterval);
    }
    this.widgetWidth = 210 - this.linksData[this.selectedLink].level * 10;
    this.selectedLevel = this.linksData[this.selectedLink].level;
    this.selectedGraph = this.linksData[this.selectedLink].key;
  }

  getData() {
    this.getSummaryData();
    this.getGraphData();
  }

  getSummaryData() {
    if (this.dataSubscription) {
      this.dataSubscription.unsubscribe();
    }
    const queryParams = {
      'ag': this.selectedAssetGroup
    };
    const vulnerabilitySummaryUrl = environment.vulnerabilitySummary.url;
    const vulnerabilitySummaryMethod = environment.vulnerabilitySummary.method;

    this.dataSubscription = this.commonResponseService.getData(vulnerabilitySummaryUrl, vulnerabilitySummaryMethod, {}, queryParams).subscribe(
      response => {
        try {
          if (!response.distribution) {
            this.errorSumVal = -1;
            this.errorMessage = 'noDataAvailable';
            this.logger.log('error', 'noDataAvailable');
          } else {
            this.errorSumVal = 1;
            this.vulnData = response.distribution;
          }
        } catch (error) {
            this.errorMessage = 'jsError';
            this.logger.log('error', error);
            this.errorSumVal = -1;
        }
    },
    error => {
      this.errorMessage = 'apiResponseError';
      this.logger.log('error', error);
      this.errorSumVal = -1;
    });
  }

  getGraphData() {
    const queryParams = {
      'ag': this.selectedAssetGroup
    };
    const vulnerabilitySummaryUrl = environment.vulnerabilityGraphSummary.url;
    const vulnerabilitySummaryMethod = environment.vulnerabilityGraphSummary.method;

    this.commonResponseService.getData(vulnerabilitySummaryUrl, vulnerabilitySummaryMethod, {}, queryParams).subscribe(
      response => {
        try {
          if (!response.count && response.count !== 0 && response.count !== '0') {
            this.errorVal = -1;
            this.errorMessage = 'noDataAvailable';
            this.logger.log('error', 'noDataAvailable');
          } else {
            this.createObjectForVulSummary(response);
            const self = this;
            this.cntInterval = setInterval(function(){
              if (self.selectedLink < self.linksData.length - 1) {
                self.selectedLink++;
              } else {
                self.selectedLink = 0;
              }
              self.widgetWidth = 210 - self.linksData[self.selectedLink].level * 10;
              self.selectedLevel = self.linksData[self.selectedLink].level;
              self.selectedGraph = self.linksData[self.selectedLink].key;
            }, 7000);
            this.errorVal = 1;
          }
        } catch (error) {
            this.errorMessage = 'jsError';
            this.logger.log('error', error);
            this.errorVal = -1;
        }
    },
    error => {
      this.errorMessage = 'apiResponseError';
      this.logger.log('error', error);
      this.errorVal = -1;
    });
  }

  createObjectForVulSummary(apiResponse) {
    this.lastLevelSelectedKeys = [];
    this.lastLevelData = {};
    this.donutData = {};
    this.modifiedResponse = this.processGraphData(apiResponse, null, this.donutData);
  }

  processGraphData(data, parent, donutData) {
    this.donutObj = {};
    if (data) {
      const currentData = JSON.parse(JSON.stringify(data));
      const currentObj = Object.keys(currentData);
      this.donutObj = {
        'color': [],
        'data': [], 'legendWithText': [],
        'legendTextcolor': '#000',
        'legend': '',
        'totalCount': 0,
        'centerText': 'Total',
        'link': false,
        'styling': {
          'cursor': 'pointer'
        },
        'cursor': []
      };
      const selectedKeys = [];
      for (let i = 0; i < currentObj.length; i++) {
        if (currentObj[i].toLowerCase() !== 'total' && currentObj[i].toLowerCase() !== 'count') {
          this.donutObj['legendWithText'].push(currentObj[i]);
          this.donutObj['color'].push(this.colorsData[currentObj[i]]);
          this.donutObj['data'].push(currentData[currentObj[i]].count);
          this.donutObj['totalCount'] += currentData[currentObj[i]].count;
          if (currentObj[i].toLowerCase() === 'inscope' || currentObj[i].toLowerCase() === 'scanned' || currentObj[i].toLowerCase() === 'compliant' || currentObj[i].toLowerCase() === 'noncompliant' ) {
            this.donutObj['cursor'].push('pointer');
          } else {
            this.donutObj['cursor'].push('default');
          }
          if (Object.keys(currentData[currentObj[i]]).length > 1) {
            selectedKeys.push(currentObj[i]);
          }
        }
      }
      if (parent) {
        donutData[parent] = this.donutObj;
      } else {
        donutData['total'] = this.donutObj;
      }
      for (let j = selectedKeys.length - 1; j >= 0; j--) {
        if (j === selectedKeys.length - 1) {
          this.lastLevelData = currentData;
          this.lastLevelSelectedKeys = selectedKeys;
        }
      const pop = this.lastLevelSelectedKeys[j];
      this.processGraphData(this.lastLevelData[pop], pop, donutData);
      }
    }
    return donutData;
  }

  pieClicked(data) {
    const type = data.legend;
    if (type === 'inscope' || type === 'scanned' || type === 'noncompliant' || type === 'compliant') {
      if (this.cntInterval) {
        clearInterval(this.cntInterval);
      }
      this.selectedGraph = type;
      for (let i = 0; i < this.linksData.length; i++ ) {
        if (this.selectedGraph === this.linksData[i].key) {
          this.selectedLink = i;
          break;
        }
        this.widgetWidth = 210 - this.linksData[this.selectedLink].level * 10;
        this.selectedLevel = this.linksData[this.selectedLink].level;
      }
    }

  }

  ngOnDestroy() {
    this.subscriptionToAssetGroup.unsubscribe();
    if (this.dataSubscription) {
      this.dataSubscription.unsubscribe();
    }
    clearInterval(this.autorefreshInterval);
    clearInterval(this.cntInterval);
  }

}
