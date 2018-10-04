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

import { Component, OnInit, ViewEncapsulation, OnDestroy } from '@angular/core';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { OverallComplianceService } from '../../services/overall-compliance.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { Subscription } from 'rxjs/Subscription';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-overall-compliance',
  templateUrl: './overall-compliance.component.html',
  styleUrls: ['./overall-compliance.component.css'],
  providers: [ LoggerService, ErrorHandlingService, OverallComplianceService, AutorefreshService ],
  encapsulation: ViewEncapsulation.None
})

export class OverallComplianceComponent implements OnInit, OnDestroy {

  private overallComplianceUrl = environment.overallCompliance.url;
  private overallComplianceMethod = environment.overallCompliance.method;
  subscriptionToAssetGroup: Subscription;
  subscriptionDomain: Subscription;
  subscriptionToOverallCompliance: Subscription;
  complianceData: any = [];
  selectedAssetGroup: string;
  selectedDomain: any;
  overallPercentage: any;
  errorMessage: any;
  durationParams: any;
  loaded = false;
  autoRefresh = false;
  contValue = false;
  seekdata = false;

  private autorefreshInterval;


  constructor(private overallComplianceService: OverallComplianceService,
              private autorefreshService: AutorefreshService,
              private assetGroupObservableService: AssetGroupObservableService,
              private logger: LoggerService,
              private errorHandling: ErrorHandlingService,
              private domainObservableService: DomainTypeObservableService) {

    this.initializeSubscriptions();

  }

  ngOnInit() {
    /* Variables to be set only first time when component is loaded should go here. */
    try {
      this.durationParams = this.autorefreshService.getDuration();
      this.durationParams = parseInt(this.durationParams, 10);
      this.autoRefresh = this.autorefreshService.autoRefresh;
      const afterLoad = this;
      if (this.autoRefresh !== undefined) {
        if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {

          this.autorefreshInterval = setInterval(function() {
            afterLoad.getOverallCompliance();
          }, this.durationParams);
        }
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
    }
  }

  /* subscribe to the asset group and domains mandatory to get data */
  initializeSubscriptions() {
   this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroupName => {
    this.selectedAssetGroup = assetGroupName;
   });

   this.subscriptionDomain = this.domainObservableService.getDomainType().subscribe(domain => {
     this.selectedDomain = domain;
     this.updateComponent();
   });

  }

  /* Function to get Data */
  getData() {
    /* All functions to get data should go here */
    this.getOverallCompliance();
  }

  updateComponent() {
    if (this.subscriptionToOverallCompliance) {
      this.subscriptionToOverallCompliance.unsubscribe();
    }
    this.contValue = false;
    this.loaded = false;
    /* All functions variables which are required to be set for component to be reloaded should go here */
    this.getData();
  }

  getOverallCompliance(): void {
    try {
      const queryParams = {
            'ag': this.selectedAssetGroup,
            'domain': this.selectedDomain
      };
      this.seekdata = false;
      this.subscriptionToOverallCompliance = this.overallComplianceService.getOverallCompliance(queryParams, this.overallComplianceUrl, this.overallComplianceMethod).subscribe(
        response => {

          try {

            this.contValue = true;
            this.loaded = true;
            this.complianceData = response[0].data;

            if (this.complianceData.length === 0) {
              if (document.getElementById('overallComplianceSvg') != null) {
                  document.getElementById('overallComplianceSvg').innerHTML = '';
              }
              this.seekdata = true;
              this.errorMessage = 'noDataAvailable';

            } else {
                this.seekdata = false;
                this.overallPercentage = response[0].percent;
            }

          } catch (e) {
              this.seekdata = true;
              this.errorMessage = this.errorHandling.handleJavascriptError(e);
          }

        },
        error => {
          this.contValue = true;
          this.seekdata = true;
          this.errorMessage = 'apiResponseError';
        });
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
    }
  }

  ngOnDestroy() {
    try {
      this.subscriptionToAssetGroup.unsubscribe();
      this.subscriptionDomain.unsubscribe();
      this.subscriptionToOverallCompliance.unsubscribe();
      clearInterval(this.autorefreshInterval);
    } catch (error) {
      this.logger.log('info', '--- Error while unsubscribing ---');
    }
  }

}
