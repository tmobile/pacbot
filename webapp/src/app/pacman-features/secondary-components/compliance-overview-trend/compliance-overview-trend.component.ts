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

import { Component, OnInit, ViewEncapsulation, OnDestroy, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { ComplianceOverviewService } from '../../services/compliance-overview.service';
import { Subscription } from 'rxjs/Subscription';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { SelectComplianceDropdown } from '../../services/select-compliance-dropdown.service';
import { LoggerService } from '../../../shared/services/logger.service';
import { environment } from './../../../../environments/environment';
import { AutorefreshService } from '../../services/autorefresh.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-compliance-overview-trend',
  templateUrl: './compliance-overview-trend.component.html',
  styleUrls: ['./compliance-overview-trend.component.css'],
  providers: [ ComplianceOverviewService, AutorefreshService ],
  encapsulation: ViewEncapsulation.None,
  // tslint:disable-next-line:use-host-property-decorator
  host: {
    '(window:resize)': 'onResize($event)'
  }
})

export class ComplianceOverviewTrendComponent implements OnInit, OnDestroy, AfterViewInit {

  @ViewChild('complianceOverviewContainer') widgetContainer: ElementRef;

  private assetGroupSubscription: Subscription;
  private complianceDropdownSubscription: Subscription;
  private issuesSubscription: Subscription;
  subscriptionDomain: Subscription;
  selectedDomain: any;
  private selectedAssetGroup: any = 'rebellion';
  private selectedComplianceDropdown: any = {
      'Target Types': '',
      'Applications': '',
      'Environments': ''
  };

  private graphWidth: any;
  private graphData: any;
  private dataLoaded: any = false;
  private error: any = false;
  private loading: any = false;
  private errorMessage: any = 'jsError';
  private distributedFiltersObject: any = {};

  // Graph customization variables
  private yAxisLabel = 'Compliance %';
  private showGraphLegend = true;
  private showArea = false;
  private hoverActive = true;

  private autorefreshInterval;

  durationParams: any;
    autoRefresh: boolean;

  constructor(private complianceOverviewService: ComplianceOverviewService,
              private assetGroupObservableService: AssetGroupObservableService,
              private selectComplianceDropdown: SelectComplianceDropdown,
              private autorefreshService: AutorefreshService,
              private logger: LoggerService,
              private domainObservableService: DomainTypeObservableService ) {

        // Get latest asset group selected and re-plot the graph
        this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(
            assetGroupName => {
                this.selectedAssetGroup = assetGroupName;
            });

        this.subscriptionDomain = this.domainObservableService.getDomainType().subscribe(domain => {
             this.selectedDomain = domain;
             this.init();
        });

        // Get latest targetType/Application/Environment
        this.complianceDropdownSubscription = this.selectComplianceDropdown.getCompliance().subscribe(
            distributedFiltersObject => {
                this.distributedFiltersObject = distributedFiltersObject;
            });

        this.durationParams = this.autorefreshService.getDuration();
                  this.durationParams = parseInt(this.durationParams, 10);
                  this.autoRefresh = this.autorefreshService.autoRefresh;

  }

  onResize() {
      const element = document.getElementById('complianceOverview');
      if (element) {
          this.graphWidth = parseInt((window.getComputedStyle(element, null).getPropertyValue('width')).split('px')[0], 10);
      }
  }

  ngAfterViewInit() {

        const afterLoad = this;
        if (this.autoRefresh !== undefined) {
          if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {

            this.autorefreshInterval = setInterval(function() {
              afterLoad.init();
            }, this.durationParams);
          }
        }
    }

  getOverview() {
      try {

        if (this.issuesSubscription) {
          this.issuesSubscription.unsubscribe();
        }

          const complianceOverviewUrl = environment.complianceOverview.url;
          const method = environment.complianceOverview.method;

        const prevDate = new Date();
        prevDate.setMonth(prevDate.getMonth() - 1);
        let fromDay;
        fromDay = prevDate.toISOString().split('T')[0];

        const payload = {
            'ag': this.selectedAssetGroup,
            'from': fromDay,
            'filters': {
                'domain': this.selectedDomain
            }
        };

          this.issuesSubscription = this.complianceOverviewService.getDailyData(complianceOverviewUrl, method, payload, {}).subscribe(
              response => {
                  try {

                      this.setDataLoaded();
                      this.graphData = response;
                      if (this.graphData.constructor.name === 'Object' || this.graphData.length === 0) {
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
      } catch (error) {
          this.setError('jsError');
      }
  }


  getData() {
      this.getOverview();
  }

  init() {
      if (this.issuesSubscription) {
        this.issuesSubscription.unsubscribe();
      }
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
      try {
          this.graphWidth = parseInt(window.getComputedStyle(this.widgetContainer.nativeElement, null).getPropertyValue('width'), 10);
      } catch (error) {
          this.setError('jsError');
      }
  }

  ngOnDestroy() {
      try {
          this.issuesSubscription.unsubscribe();
          this.assetGroupSubscription.unsubscribe();
          this.subscriptionDomain.unsubscribe();
          this.complianceDropdownSubscription.unsubscribe();
          clearInterval(this.autorefreshInterval);
      } catch (error) {
          this.logger.log('error', '--- Error while unsubscribing ---');
      }
  }

}
