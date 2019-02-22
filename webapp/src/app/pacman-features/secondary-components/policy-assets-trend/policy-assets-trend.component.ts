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

import { Component, OnInit, ViewEncapsulation, OnDestroy, Input, OnChanges, SimpleChanges, ViewChild, ElementRef } from '@angular/core';
import { PolicyTrendService } from '../../services/policy-trend.service';
import { Subscription } from 'rxjs/Subscription';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { SelectComplianceDropdown } from '../../services/select-compliance-dropdown.service';
import { LoggerService } from '../../../shared/services/logger.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-policy-assets-trend',
  templateUrl: './policy-assets-trend.component.html',
  styleUrls: ['./policy-assets-trend.component.css'],
  providers: [ PolicyTrendService , AutorefreshService ],
  encapsulation: ViewEncapsulation.None,
  // tslint:disable-next-line:use-host-property-decorator
  host: {
    '(window:resize)': 'onResize($event)'
  }
})

export class PolicyAssetsTrendComponent implements OnInit, OnChanges, OnDestroy {

  @ViewChild('policyAssetsTrendContainer') widgetContainer: ElementRef;

    private assetGroupSubscription: Subscription;
    private domainSubscription: Subscription;

    private complianceDropdownSubscription: Subscription;
    private issuesSubscription: Subscription;

    private selectedAssetGroup: string;
    private selectedDomain: string;
    private selectedComplianceDropdown: any = {
      'Target Types': '',
      'Applications': '',
      'Environments': ''
    };

    private graphWidth: any;
    private graphData: any;
    private dataLoaded:  any = false;
    private error: any = false;
    private loading: any = false;
    private errorMessage: any = 'apiResponseError';

    // Graph customization variables
    private yAxisLabel = 'Assets';
    private showGraphLegend = true;
    durationParams: any;
    autoRefresh: boolean;

    private autorefreshInterval;

    @Input() ruleID: any;
    constructor(private policyTrendService: PolicyTrendService,
                private assetGroupObservableService: AssetGroupObservableService,
                private selectComplianceDropdown: SelectComplianceDropdown,
                private autorefreshService: AutorefreshService,
                private logger: LoggerService,
                private domainObservableService: DomainTypeObservableService) {

                  // Get latest asset group selected and re-plot the graph
                  this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(
                    assetGroupName => {
                        this.selectedAssetGroup = assetGroupName;
                  });

                  this.domainSubscription = this.domainObservableService.getDomainType().subscribe(domain => {
                      this.selectedDomain = domain;
                      this.init();
                  });

                  // Get latest targetType/Application/Environment
                  this.complianceDropdownSubscription = this.selectComplianceDropdown.getCompliance().subscribe(
                  complianceName => {
                      this.selectedComplianceDropdown = complianceName;
                  });

                }


    getIssues() {

      if (this.issuesSubscription) {
        this.issuesSubscription.unsubscribe();
      }

      const prevDate = new Date();
      prevDate.setMonth(prevDate.getMonth() - 1);
      let fromDay;
      fromDay = prevDate.toISOString().split('T')[0];
      const issuesQueryParameters = {
          'ag': this.selectedAssetGroup,
          'filters': {
              'domain': this.selectedDomain
          },
          'from': fromDay,
          'ruleid': this.ruleID,
      };

      this.issuesSubscription = this.policyTrendService.getData([], issuesQueryParameters).subscribe(
        response => {
          try {
              this.graphData = [];
              if (response.length) {
                response.forEach(type => {
                  const key = type.key.toLowerCase();
                  if (key === 'total' || key === 'compliant') {
                      this.graphData.push(type);
                  }
                });
                this.setDataLoaded();
              } else {
                this.setError('noDataAvailable');
              }

          } catch (error) {
            this.setError('jsError');
          }
        },
        error => {
          this.setError('apiResponseError');
        }
      );
    }

    ngOnChanges(changes: SimpleChanges) {
      try {
        const ruleIdChange = changes['ruleID'];
        if (ruleIdChange) {
          const prevId = JSON.stringify(ruleIdChange.previousValue);
          const currId = JSON.stringify(ruleIdChange.currentValue);
          if (prevId !== currId) {
            this.init();
          }
        }
      } catch (error) {
        this.setError('jsError');
      }
    }

    onResize() {
      const element = document.getElementById('policyAssetsTrend');
        if (element) {
            this.graphWidth = parseInt((window.getComputedStyle(element, null).getPropertyValue('width')).split('px')[0], 10);
        }
    }

    getData() {
      if (this.ruleID) {
        this.getIssues();
      }
    }

    init() {
      this.setDataLoading();
      this.getData();
    }

    setDataLoaded() {
      this.dataLoaded = true;
      this.error = false;
      this.loading = false;
    }

    setDataLoading() {
      this.dataLoaded = false;
      this.error = false;
      this.loading = true;
    }

    setError(message?: any) {
      this.dataLoaded = false;
      this.error = true;
      this.loading = false;
      if (message) {
        this.errorMessage = message;
      }
    }

    ngOnInit() {
      this.durationParams = this.autorefreshService.getDuration();
      this.durationParams = parseInt(this.durationParams, 10);
      this.autoRefresh = this.autorefreshService.autoRefresh;

      const afterLoad = this;
        if (this.autoRefresh !== undefined) {
          if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {

            this.autorefreshInterval = setInterval(function() {
              afterLoad.getData();
            }, this.durationParams);
          }
        }

        setTimeout(() => {
          this.graphWidth = parseInt(window.getComputedStyle(this.widgetContainer.nativeElement, null).getPropertyValue('width'), 10);
          this.init();
        }, 0);
    }

    ngOnDestroy() {
      try {
        this.issuesSubscription.unsubscribe();
        this.domainSubscription.unsubscribe();
        this.assetGroupSubscription.unsubscribe();
        this.complianceDropdownSubscription.unsubscribe();
        clearInterval(this.autorefreshInterval);
      } catch (error) {
        this.logger.log('error', '--- Error while unsubscribing ---');
      }
    }
  }
