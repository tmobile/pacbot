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

import {Component, OnDestroy, OnChanges, SimpleChanges, Input, Output, EventEmitter} from '@angular/core';
import {AssetGroupObservableService} from '../../../core/services/asset-group-observable.service';
import {CommonResponseService} from '../../../shared/services/common-response.service';
import {Subscription} from 'rxjs/Subscription';
import {environment} from '../../../../environments/environment';
import {LoggerService} from '../../../shared/services/logger.service';

@Component({
  selector: 'app-vuln-report-stats',
  templateUrl: './vuln-report-stats.component.html',
  styleUrls: ['./vuln-report-stats.component.css'],
  providers: []
})

export class VulnReportStatsComponent implements OnDestroy, OnChanges {

  statsSubscription: Subscription;
  assetGroupSubscription: Subscription;
  selectedAssetGroup;
  statsResponse = 0;
  statsData = [];
  currentFilters = '3,4,5';
  summaryObj = {
    vulnerabilities: 0,
    assetsAffected: 0,
    occurrences: 0
  };

  @Input() filter: any;
  @Output() emitError = new EventEmitter();

  constructor (
    private assetGroupObservableService: AssetGroupObservableService,
    private logger: LoggerService,
    private commonResponseService: CommonResponseService ) {
      this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
          this.selectedAssetGroup = assetGroupName;
          // this.currentFilters = '3,4,5';
          this.updateComponent();
      });
  }

  ngOnChanges(changes: SimpleChanges) {
    try {
      const DataChange = changes['filter'];
      if (DataChange && DataChange.currentValue ) {
        this.currentFilters = DataChange.currentValue.severity;
        const prev = DataChange.previousValue;
        if ((JSON.stringify(DataChange.currentValue) !== JSON.stringify(prev)) && !DataChange.firstChange) {
          this.updateComponent();
        }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  updateComponent() {
    this.statsResponse = 0;
    this.statsData = [];
    if ( this.statsSubscription ) {
      this.statsSubscription.unsubscribe();
    }
    this.getData();
  }

  getData() {
        const statsPayload = {};
        const statsQueryParam = { ag: this.selectedAssetGroup, severity: this.currentFilters };
        const statsTableUrl = environment.vulnerabilitySummary.url;
        const statsTableMethod = environment.vulnerabilitySummary.method;
        this.statsSubscription = this.commonResponseService.getData(
          statsTableUrl, statsTableMethod, statsPayload, statsQueryParam).subscribe(
            response => {

                try {
                  if (!response.distribution) {
                    this.statsResponse = -1;
                    this.emitError.emit();
                  } else {
                    this.statsData = response.distribution;
                    this.getSummaryObj();
                    this.statsResponse = 1;
                  }
                } catch (e) {
                  this.statsResponse = -1;
                  this.emitError.emit();
                  this.logger.log('error', e);
                }
            },
            error => {
              this.statsResponse = -1;
              this.emitError.emit();
              this.logger.log('error', error);
            });
  }

  getSummaryObj() {

    try {

      this.summaryObj = {
        vulnerabilities: 0,
        assetsAffected: 0,
        occurrences: 0
      };

      /* TODO: Commenting it temporarily */
      /*const filterArr = this.currentFilters.split(',');
      for ( let i = 0; i < this.statsData['severityinfo'].length ; i++ ) {
        for ( let j = 0; j < filterArr.length ; j++ ) {
          if (this.statsData['severityinfo'][i].severitylevel.toString() === filterArr[j]) {
            this.summaryObj['vulnerabilities'] += this.statsData['severityinfo'][i].uniqueVulnCount;
            this.summaryObj['assetsAffected'] =
            this.summaryObj['assetsAffected'] < this.statsData['severityinfo'][i].hostCount
            ? this.statsData['severityinfo'][i].hostCount : this.summaryObj['assetsAffected'];
            this.summaryObj['occurrences'] += this.statsData['severityinfo'][i].vulnInstanceCount;
            break;
          }
        }
      }*/

      this.summaryObj.vulnerabilities = this.statsData['uniqueVulnCount'] || this.summaryObj.vulnerabilities;
      this.summaryObj.assetsAffected = this.statsData['assetsWithVulns'] || this.summaryObj.assetsAffected;
      this.summaryObj.occurrences = this.statsData['vulnerabilities'] || this.summaryObj.occurrences;

    } catch (error) {
      this.logger.log('error', 'js error - ' + error);
    }
  }

  ngOnDestroy() {
    if ( this.statsSubscription ) {
      this.statsSubscription.unsubscribe();
    }
  }

}
