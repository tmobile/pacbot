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
  selector: 'app-vuln-report-tables',
  templateUrl: './vuln-report-tables.component.html',
  styleUrls: ['./vuln-report-tables.component.css'],
  providers: []
})

export class VulnReportTablesComponent implements OnDestroy, OnChanges {

  assetGroupSubscription: Subscription;
  remediationTableSubscription: Subscription;
  performersTableSubscription: Subscription;
  selectedAssetGroup;
  remediateResponse = 0;
  performersResponse = 0;
  remediationTableData = [];
  direction = -1;
  performersType = 'director';
  performersArr = ['org', 'application', 'environment'];
  performersCnt = 0;
  property = 'count';
  performersTableData = { data: [], highest: [], lowest: [], total: []};
  currentFilters = '3,4,5';
  sortArr = [{'showUp': 0}, {'showUp': -1}];

  @Input() filter: any;
  @Output() emitError = new EventEmitter();

  constructor (
    private assetGroupObservableService: AssetGroupObservableService,
    private logger: LoggerService,
    private commonResponseService: CommonResponseService ) {
      this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
          this.selectedAssetGroup = assetGroupName;
          this.updateComponent();
      });
  }

  ngOnChanges(changes: SimpleChanges) {
    try {
      const DataChange = changes['filter'];
      if (DataChange && DataChange.currentValue) {
        this.currentFilters  = DataChange.currentValue.severity;
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
    this.remediateResponse = 0;
    this.performersResponse = 0;
    this.direction = -1;
    this.property = 'count';
    this.performersType = 'director';
    this.performersCnt = 0;
    this.remediationTableData = [];
    this.sortArr = [{'showUp': 0}, {'showUp': -1}];
    this.performersTableData = { data: [], highest: [], lowest: [], total: []};

    if ( this.remediationTableSubscription ) {
      this.remediationTableSubscription.unsubscribe();
    }
    if ( this.performersTableSubscription ) {
      this.performersTableSubscription.unsubscribe();
    }
    this.getData();
  }

  getData() {
    this.getRemediateData();
    this.getPerformersData();
  }

  getPerformersData() {
    const performersPayload = {};
    const performersQueryParam = { ag: this.selectedAssetGroup, severity: this.currentFilters, type: this.performersArr[this.performersCnt] };
    const performersTableUrl = environment.performersTable.url;
    const performersTableMethod = environment.performersTable.method;
    this.performersTableSubscription = this.commonResponseService.getData(
      performersTableUrl, performersTableMethod, performersPayload, performersQueryParam).subscribe(
        response => {

            try {
              if (response.response['data'].length === 0 ) {
                if (this.performersCnt < this.performersArr.length - 1 ) {
                  this.performersType = response.response['category'];
                  this.performersCnt++;
                  this.getPerformersData();
                } else {
                  this.performersResponse = -1;
                  if ( this.remediateResponse === -1 && this.performersResponse === -1 ) {
                    this.emitError.emit();
                  }
                }
              } else {
                this.performersTableData = this.processData(response.response);
                this.performersResponse = 1;
                this.performersType = response.response['category'];
              }
            } catch (e) {
              this.performersResponse = -1;
              if ( this.remediateResponse === -1 && this.performersResponse === -1 ) {
                this.emitError.emit();
              }
              this.logger.log('error', e);
            }
        },
        error => {
          this.performersResponse = -1;
          if ( this.remediateResponse === -1 && this.performersResponse === -1 ) {
            this.emitError.emit();
          }
          this.logger.log('error', error);
        });
  }

  getRemediateData() {
    const remediationPayload = {};
    const remediationQueryParam = { ag: this.selectedAssetGroup, severity: this.currentFilters};
    const remediationTableUrl = environment.remediationTable.url;
    const remediationTableMethod = environment.remediationTable.method;
    this.remediationTableSubscription = this.commonResponseService.getData(
      remediationTableUrl, remediationTableMethod, remediationPayload, remediationQueryParam).subscribe(
        response => {

          try {
            if (response.actions.length === 0) {
              this.remediateResponse = -1;
              if ( this.remediateResponse === -1 && this.performersResponse === -1 ) {
                this.emitError.emit();
              }
            } else {
              this.remediationTableData = response.actions;
              this.remediationTableData.sort( function (a, b) {
                return parseFloat(b.contribution) - parseFloat(a.contribution);
              });
              this.remediateResponse = 1;
            }
          } catch (e) {
            this.remediateResponse = -1;
            if ( this.remediateResponse === -1 && this.performersResponse === -1 ) {
              this.emitError.emit();
            }
            this.logger.log('error', e);
          }
      },
      error => {
        this.remediateResponse = -1;
        if ( this.remediateResponse === -1 && this.performersResponse === -1 ) {
          this.emitError.emit();
        }
        this.logger.log('error', error);
      });
  }

  processData(data) {
    try {
      const obj = {data: [], lowest: [], highest: [], total: []};
      for ( let i = 0; i < data.data.length; i++ ) {
          const eachObj = {
            name: Object.keys(data.data[i])[0],
            count: data.data[i][Object.keys(data.data[i])[0]]
          };
          obj['data'].push(eachObj);
      }
      obj.total = JSON.parse(JSON.stringify(obj.data));
      obj.total.sort( function (a, b) {
        return b.count - a.count;
      });
      obj.data.sort( function (a, b) {
        return a.count - b.count;
      });

      obj.highest = obj.data.slice(0, 5);
      obj.lowest = obj.data.slice(5).slice(-5);
      return obj;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  sortTable(index) {
    try {
      if (index === 0) {
        this.sortArr[1].showUp = 0;
      } else {
        this.sortArr[0].showUp = 0;
      }
      if (!this.sortArr[index].showUp) {
        this.sortArr[index].showUp = 1;
      } else {
        this.sortArr[index].showUp = this.sortArr[index].showUp * -1;
      }
      this.direction = this.sortArr[index].showUp;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  ngOnDestroy() {
    if ( this.remediationTableSubscription ) {
      this.remediationTableSubscription.unsubscribe();
    }
    if ( this.performersTableSubscription ) {
      this.performersTableSubscription.unsubscribe();
    }
  }

}
