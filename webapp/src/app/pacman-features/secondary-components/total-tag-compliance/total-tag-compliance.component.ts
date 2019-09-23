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

import { Component, OnInit, ViewEncapsulation, OnDestroy, Input } from '@angular/core';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { TaggingComplianceService } from '../../services/tagging-compliance.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { Subscription } from 'rxjs/Subscription';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { ActivatedRoute, UrlSegment, Router } from '@angular/router';
import { UtilsService } from '../../../shared/services/utils.service';
import { WorkflowService } from '../../../core/services/workflow.service';

@Component({
  selector: 'app-total-tag-compliance',
  templateUrl: './total-tag-compliance.component.html',
  styleUrls: ['./total-tag-compliance.component.css'],
  providers: [ LoggerService, ErrorHandlingService, TaggingComplianceService, AutorefreshService ],
  encapsulation: ViewEncapsulation.None
})
export class TotalTagComplianceComponent implements OnInit, OnDestroy {

  private tagsComplianceUrl = environment.taggingCompliance.url;
  private tagsComplianceMethod = environment.taggingCompliance.method;
  public returnedSearch = '';

  subscriptionToAssetGroup: Subscription;
  subscriptionToTaggingCompliance: Subscription;
  complianceData: any = [];
  selectedAssetGroup: string;
  overallPercentage: any;
  errorMessage: any;
  loaded = false;
  forData: any = [];
  durationParams: any;
  autoRefresh: boolean;
  contValue = false;
  seekdata = false;
  sampleData: any = [];
  tableHeaderData: any = [];
  complianceTableData: any = [];
  complianceTableHeaderData: any = [];
  donutId = 'taggingCompliance';

  private autorefreshInterval;
  private urlToRedirect: string;
  @Input() pageLevel: number;

  constructor(private taggingComplianceService: TaggingComplianceService,
    private autorefreshService: AutorefreshService,
    private assetGroupObservableService: AssetGroupObservableService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private utils: UtilsService,
    private workflowService: WorkflowService ) {

      this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
          this.selectedAssetGroup = assetGroupName;
          this.updateComponent();
      });
      this.tableHeaderData = ['Untagged', 'Tagged'];
  }

  ngOnInit() {
    /* Variables to be set only first time when component is loaded should go here. */
    try {
      this.urlToRedirect = this.router.routerState.snapshot.url;
      this.durationParams = this.autorefreshService.getDuration();
      this.durationParams = parseInt(this.durationParams, 10);
      this.autoRefresh = this.autorefreshService.autoRefresh;
      const afterLoad = this;
      if (this.autoRefresh !== undefined) {
        if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {

          this.autorefreshInterval = setInterval(function(){
            afterLoad.getTaggingCompliance();
          }, this.durationParams);
        }
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
    }
  }

  /* Function to get Data */
  getData() {
    /* All functions to get data should go here */
    this.getTaggingCompliance();
  }

  updateComponent() {
    this.contValue = false;
    this.loaded = false;
    /* All functions variables which are required to be set for component to be reloaded should go here */
    this.getData();
  }

  getTaggingCompliance(): void {
    try {

      if (this.subscriptionToTaggingCompliance) {
          this.subscriptionToTaggingCompliance.unsubscribe();
        }
      const queryParams = {
            'ag': this.selectedAssetGroup
      };
      this.seekdata = false;
      this.subscriptionToTaggingCompliance = this.taggingComplianceService.getTaggingCompliance(queryParams, this.tagsComplianceUrl, this.tagsComplianceMethod).subscribe(
        response => {

          try {
            this.complianceData = response[0].data;
            this.complianceTableData = response[0].taggingStatus.data;
            this.complianceTableHeaderData = response[0].taggingStatus.header;
            this.contValue = true;
            this.loaded = true;
            if (this.complianceData.length === 0) {
              if (document.getElementById(this.donutId) != null) {
                  document.getElementById(this.donutId).innerHTML = '';
              }
              this.seekdata = true;
              this.errorMessage = 'noDataAvailable';

            } else {
                this.seekdata = false;
                this.overallPercentage = response[0].percent;
            }

          } catch (e) {
              this.contValue = true;
              this.seekdata = true;
              this.errorMessage = this.errorHandling.handleJavascriptError(e);
          }

        }, error => {
          this.contValue = true;
          this.seekdata = true;
          if (error === 'apiResponseError') {
            this.errorMessage = error;
          }
        });
    } catch (e) {
      this.contValue = true;
      this.seekdata = true;
      this.errorMessage = this.errorHandling.handleJavascriptError(e);
    }
  }
  capitalizeFirstLetter(string): any {
      return string.charAt(0).toUpperCase() + string.slice(1);
  }

    /**
   * This function navigates the page mentioned in the routeTo variable with a querypareams
   */

  navigatePage(event) {
    try {
      const localObjKeys = Object.keys(event);
      const currentApp = event[localObjKeys[0]];
      const intanceType = event[localObjKeys[1]].toLowerCase();
      const apiTarget = {'TypeAsset' : 'taggable'};

      if ((intanceType !== undefined) && (currentApp !== undefined) ) {
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        if (intanceType === 'tagged') {
          const eachParams = {'tagged': true, tagName: currentApp};
          let newParams = this.utils.makeFilterObj(eachParams);
          newParams = Object.assign(newParams, apiTarget);
          newParams['mandatory'] = 'tagged';
          this.router.navigate(['../../', 'assets' , 'asset-list'], { relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
        } else if (intanceType === 'untagged') {
          const eachParams = {'tagged': false , tagName: currentApp};
          let newParams = this.utils.makeFilterObj(eachParams);
          newParams = Object.assign(newParams, apiTarget);
          newParams['mandatory'] = 'tagged';
          this.router.navigate(['../../', 'assets' , 'asset-list'], { relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
        }
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }
  /* navigatePage function ends here */

  ngOnDestroy() {
    try {
      this.subscriptionToAssetGroup.unsubscribe();
      this.subscriptionToTaggingCompliance.unsubscribe();
      clearInterval(this.autorefreshInterval);
    } catch (error) {
      this.logger.log('info', '--- Error while unsubscribing ---');
    }
  }

}
