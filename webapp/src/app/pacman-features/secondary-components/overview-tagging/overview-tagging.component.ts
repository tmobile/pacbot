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

import { Component, OnInit, OnDestroy , Input} from '@angular/core';
import { ComplianceCategoriesService } from '../../services/compliance-categories.service';
import { Subscription } from 'rxjs/Subscription';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';
import { DataCacheService } from '../../../core/services/data-cache.service';

@Component({
  selector: 'app-overview-tagging',
  templateUrl: './overview-tagging.component.html',
  styleUrls: ['./overview-tagging.component.css'],
  providers: [ComplianceCategoriesService, AutorefreshService, LoggerService, ErrorHandlingService]
})

export class OverviewTaggingComponent implements OnInit, OnDestroy {

  subscriptionToOverviewTagging: Subscription;
  subscriptionDomain: Subscription;
  selectedDomain: any;
  errorMessages: string;
  hideLoader = false;
  showError = false;
  routeTo = 'tagging-compliance';
  error: any = {

    Tagging: false
  };
  complianceCategoriesFinal: any = {
    Tagging: {output : {}}
  };
  complianceData: any =  [
    {
        'categoriesHeader': 'Tagging',
        'categoriesTitle': 'untagged',
        'categoriesSubTitle': 'out of',
        'categoriesPostTitle': 'assets'
    }
  ];

  private autorefreshInterval;
  durationParams: any;
  autoRefresh: boolean;
  @Input() pageLevel: number;
  private selectedAssetGroup: string;

  constructor(private complianceCategoriesService: ComplianceCategoriesService,
              private errorHandling: ErrorHandlingService,
              private logger: LoggerService,
              private autorefreshService: AutorefreshService,
              private domainObservableService: DomainTypeObservableService,
              private dataCacheService: DataCacheService) {


      this.subscriptionDomain = this.domainObservableService.getDomainType().subscribe(domain => {
           this.selectedDomain = domain;
           this.selectedAssetGroup = this.dataCacheService.getCurrentSelectedAssetGroup();
           this.updateComponent();
      });
  }

  ngOnInit() {
    try {
        this.durationParams = this.autorefreshService.getDuration();
        this.durationParams = parseInt(this.durationParams,  10);
        this.autoRefresh = this.autorefreshService.autoRefresh;
        const afterLoad = this;
        if (this.autoRefresh !== undefined) {
          if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {
            this.autorefreshInterval = setInterval(function() {
              afterLoad.getComplianceData();
            }, this.durationParams);
          }
        }
    } catch (error) {
      this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  updateComponent() {
    try {
      this.showError = false;
      this.hideLoader = false;
      if (this.subscriptionToOverviewTagging) {
        this.subscriptionToOverviewTagging.unsubscribe();
      }
      this.error = {
          Tagging: false
      };
      this.getComplianceData();
    } catch (error) {
      this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  getComplianceCategories(category): void {
    try {

        const afterLoad = this;

        const queryParams = {
                'ag': this.selectedAssetGroup,
                'domain': this.selectedDomain
        };

        const categoryUrl = environment.complianceCategories.complianceCategoriesData[category].url;
        const categoryMethod = environment.complianceCategories.complianceCategoriesData[category].method;
        const categoryType = category;

          this.subscriptionToOverviewTagging = this.complianceCategoriesService.getData(queryParams, categoryType, categoryUrl, categoryMethod).subscribe(
            response => {
              this.showError = false;
              this.complianceCategoriesFinal = response[0];

              this.hideLoader = true;
              this.error[categoryType] = false;

              this.complianceCategoriesFinal[categoryType].loaded = false;
              const x = this;

              setTimeout(function(){
                  x.complianceCategoriesFinal[categoryType].loaded = true;
              }, 500);
            },
            error => {
              this.showError = true;
              this.errorMessages = 'apiResponseError';
              this.hideLoader = true;
              this.error[categoryType] = true;
            });
    } catch (error) {
      this.showError = true;
      this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  getComplianceData() {
    for ( let i = 0; i < this.complianceData.length; i++ ) {
        this.getComplianceCategories(this.complianceData[i].categoriesHeader);
    }
  }

  ngOnDestroy() {
    try {
      this.subscriptionDomain.unsubscribe();
      this.subscriptionToOverviewTagging.unsubscribe();
      clearInterval(this.autorefreshInterval);
    } catch (error) {
      this.logger.log('error', error);
    }
  }

}
