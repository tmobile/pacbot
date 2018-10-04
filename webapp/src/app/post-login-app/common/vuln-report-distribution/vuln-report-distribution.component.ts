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

import { Component, OnInit, Inject, Input, OnDestroy, OnChanges, SimpleChanges, Output, EventEmitter, HostListener, AfterViewInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { HttpService } from '../../../shared/services/http-response.service';
import { UtilsService } from '../../../shared/services/utils.service';
import { CommonResponseService } from '../../../shared/services/common-response.service';

@Component({
  selector: 'app-vuln-report-distribution',
  templateUrl: './vuln-report-distribution.component.html',
  styleUrls: ['./vuln-report-distribution.component.css']
})
export class VulnReportDistributionComponent implements OnInit, OnChanges, OnDestroy, AfterViewInit {
  selectedAssetGroup: string;
  private errorMessage = 'apiResponseError';
  private InfraDataSubscription: Subscription;
  private EnvDataSubscription: Subscription;
  private VulnTypeDataSubscription: Subscription;
  private subscriptionToAssetGroup: Subscription;
  private failureOccurred = 0;
  public responseReceived = 0;
  private donutData = {};
  widgetWidth;
  widgetHeight;
  MainTextcolor = '#000';
  innerRadious: any = 80;
  outerRadious: any = 50;
  strokeColor = '#fff';
  index = 0;
  distributionArray = [];
  @Input() filter: any;
  currentFilters = '3,4,5';
  @Output() emitFailureError = new EventEmitter();

  constructor(
    private assetGroupObservableService: AssetGroupObservableService,
    private logger: LoggerService,
    private utilsService: UtilsService,
    private commonResponseService: CommonResponseService,
    @Inject(HttpService) private httpService: HttpService) {
      this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
        assetGroupName => {
            this.selectedAssetGroup = assetGroupName;
            this.updateComponent();
        });
     }

@HostListener('window:resize', ['$event']) onResize(event) {
  this.setWidthForDonutChart();
}

 ngOnInit() {
  }

  ngAfterViewInit() {
    this.setWidthForDonutChart();
  }

  ngOnChanges(changes: SimpleChanges) {
    try {
      const filterChange = changes['filter'];
      if (filterChange && filterChange.currentValue) {
        this.currentFilters = filterChange.currentValue.severity;
        const cur = JSON.stringify(filterChange.currentValue);
        const prev = JSON.stringify(filterChange.previousValue);
        if (cur !== prev && !filterChange.firstChange) {
          this.updateComponent();
        }
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

updateComponent() {
  try {
    if (this.VulnTypeDataSubscription) {
      this.VulnTypeDataSubscription.unsubscribe();
    }
    if (this.InfraDataSubscription) {
      this.InfraDataSubscription.unsubscribe();
    }
    if (this.EnvDataSubscription) {
      this.EnvDataSubscription.unsubscribe();
    }
    this.donutData = {};
    this.failureOccurred = 0;
    this.responseReceived = 0;
    this.distributionArray = [];
    this.index = 0;
    // function call to apis of each donut
    this.getVulnTypeData();
    this.getInfraData();
    this.getEnvData();
  } catch (e) {
    this.logger.log('error', e);
  }
}

getInfraData() {
  const payload = {};
  const queryParam = {
    'ag': this.selectedAssetGroup,
    'severity': this.currentFilters
  };
  const url = environment.VulnerabilitiesDistributionInfra.url;
  const method = environment.VulnerabilitiesDistributionInfra.method;

  this.InfraDataSubscription = this.commonResponseService.getData(url, method, payload, queryParam).subscribe(
    response => {
    try {
      this.responseReceived++;
      if (this.utilsService.checkIfAPIReturnedDataIsEmpty(response.distribution)) {
        this.errorMessage = 'vulnerabilityMessage';
        this.failureOccurred++;
        if (this.failureOccurred === 3) {
          this.emitFailureError.emit();
        }
      } else {
        // order for displaying donut
        const order = 2;
        const category = ['Cloud', 'On-Prem'];
        this.processGraphData(response.distribution, category, order);
      }

    } catch (e) {
      this.errorMessage = 'jsError';
      this.logger.log('error', e);
      this.failureOccurred++;
      if (this.failureOccurred === 3) {
        this.emitFailureError.emit();
      }
    }
  },
  error => {
    this.responseReceived++;
    this.errorMessage = 'apiResponseError';
    this.logger.log('error', error);
    this.failureOccurred++;
    if (this.failureOccurred === 3) {
      this.emitFailureError.emit();
    }
  });
}

getEnvData() {
  const payload = {};
  const queryParam = {
    'ag': this.selectedAssetGroup,
    'severity': this.currentFilters
  };
  const url = environment.VulnerabilitiesDistributionEnv.url;
  const method = environment.VulnerabilitiesDistributionEnv.method;

  this.EnvDataSubscription = this.commonResponseService.getData(url, method, payload, queryParam).subscribe(
    response => {
    try {
      this.responseReceived++;
      if (this.utilsService.checkIfAPIReturnedDataIsEmpty(response.distribution)) {
        this.errorMessage = 'vulnerabilityMessage';
        this.failureOccurred++;
        if (this.failureOccurred === 3) {
          this.emitFailureError.emit();
        }
      } else {
        // order for displaying donut
        const order = 3;
        const category = ['Prod', 'Non-Prod'];
        this.processGraphData(response.distribution, category, order);
      }

    } catch (e) {
      this.errorMessage = 'jsError';
      this.logger.log('error', e);
      this.failureOccurred++;
      if (this.failureOccurred === 3) {
        this.emitFailureError.emit();
      }
    }
  },
  error => {
    this.responseReceived++;
    this.errorMessage = 'apiResponseError';
    this.logger.log('error', error);
    this.failureOccurred++;
    if (this.failureOccurred === 3) {
      this.emitFailureError.emit();
    }
  });
}

getVulnTypeData() {
  const payload = {};
  const queryParam = {
    'ag': this.selectedAssetGroup,
    'severity': this.currentFilters
  };
  const url = environment.VulnerabilitiesDistributionVulnType.url;
  const method = environment.VulnerabilitiesDistributionVulnType.method;

  this.VulnTypeDataSubscription = this.commonResponseService.getData(url, method, payload, queryParam).subscribe(
    response => {
    try {
      this.responseReceived++;
      if (this.utilsService.checkIfAPIReturnedDataIsEmpty(response.distribution)) {
        this.errorMessage = 'vulnerabilityMessage';
        this.failureOccurred++;
        if (this.failureOccurred === 3) {
          this.emitFailureError.emit();
        }
      } else {
        // order for displaying donut
        const order = 1;
        const category = ['OS', 'Application'];
        this.processGraphData(response.distribution, category, order);
      }

    } catch (e) {
      this.errorMessage = 'jsError';
      this.logger.log('error', e);
      this.failureOccurred++;
      if (this.failureOccurred === 3) {
        this.emitFailureError.emit();
      }
    }
  },
  error => {
    this.responseReceived++;
    this.errorMessage = 'apiResponseError';
    this.logger.log('error', error);
    this.failureOccurred++;
    if (this.failureOccurred === 3) {
      this.emitFailureError.emit();
    }
  });
}


processGraphData(data, categoryArray, order) {
  try {
  // check if data available for both categories. if not, add another with zero percent.
  if (data.length === 1) {
    const zeroContributionObj = {
      'contribution': 0,
      'totalVulnerableAssets': 0,
      'uniqueVulnCount': 0,
      'vulnerabilities': 0
    };
    if (categoryArray.includes(data[0].category)) {
        if (data[0].category === categoryArray[0]) {
          Object.assign(zeroContributionObj, {'category': categoryArray[1]} );
        } else {
          Object.assign(zeroContributionObj, {'category': categoryArray[0]} );
        }
    }
    data.push(zeroContributionObj);
  }
  // color Obj for all donut
  const donutColor = {
    'OS': ['#645EC5 ', '#D0CEED'], /* Previous colors: 'OS': ['#ffb00d', '#f2cd80'],*/
    'Cloud': ['#289cf7', '#bee1fc'],
    'Prod': ['#aee6df', '#26ba9d']
  };
  //  dataobj is the format of data expected by donut chart
  const dataValue = [data[0].contribution, data[1].contribution];
  const legendText = [data[0].category, data[1].category];
  const dataObj = {
    'color': donutColor[categoryArray[0]],
    'data': dataValue,
    'legendText' : legendText,
    'legendTextcolor': '#000',
    'centerText' : data[0].category + ' vs. ' + data[1].category,
    'link': false,
    'styling': {
     'cursor': 'text'
    }
  };
  // storing the final data in donutData variable to pass it to the donut chart component
  this.donutData = dataObj;
  // appending donutData obj to response data array received
  data.push(this.donutData);
  const eachDonutObj = {};
  Object.assign(eachDonutObj, {'data': data});
  Object.assign(eachDonutObj, {'order': order});
  // Pushing data of each donut to common distributionArray.
  this.distributionArray.push(eachDonutObj);
  } catch (error) {
    this.logger.log('error', error);
  }
}

setWidthForDonutChart() {
  const wrapperElement: any = document.getElementsByClassName('vuln-distribution-wrapper')[0];
  if (wrapperElement) {
    const donutElement: any = wrapperElement.clientWidth / 3;
     let donutWidth = donutElement / 2.5;
    if (donutWidth > 250) {
      donutWidth = 250;
    }
    this.widgetWidth = donutWidth;
    this.widgetHeight = this.widgetWidth;
  } else {
    this.widgetWidth = 140;
    this.widgetHeight = this.widgetWidth;
  }
}

  ngOnDestroy() {
    try {
      if (this.subscriptionToAssetGroup) {
        this.subscriptionToAssetGroup.unsubscribe();
      }
      if (this.VulnTypeDataSubscription) {
        this.VulnTypeDataSubscription.unsubscribe();
      }
      if (this.InfraDataSubscription) {
        this.InfraDataSubscription.unsubscribe();
      }
      if (this.EnvDataSubscription) {
        this.EnvDataSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }
}
