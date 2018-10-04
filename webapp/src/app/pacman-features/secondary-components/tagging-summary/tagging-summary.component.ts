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

import { Component, OnInit, OnDestroy, ViewEncapsulation, Input} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UtilsService } from '../../../shared/services/utils.service';
import { WorkflowService } from '../../../core/services/workflow.service';

@Component({
  selector: 'app-tagging-summary',
  templateUrl: './tagging-summary.component.html',
  styleUrls: ['./tagging-summary.component.css'],
  providers: [LoggerService, ErrorHandlingService, CommonResponseService, AutorefreshService],
  encapsulation: ViewEncapsulation.None
})

export class TaggingSummaryComponent implements OnInit, OnDestroy {

  subscriptionToAssetGroup: Subscription;
  selectedAssetGroup: string;
  public dataSubscriber: any;
  public apiData: any;
  public applicationValue: any;
  public errorMessage: any;
  public dataComing = true;
  public showLoader  = true;
  durationParams: any;
  autoRefresh: boolean;
  public seekdata = false;

  dataValue: any;
  colors: any= ['#00b946', '#1c5066', '#00b946', '#e60127'];

    complianceColors = {
        'non-compliant' : '#D40325',
        'compliant'     : '#00B946',
        'intermediate'  : '#F75C03'
    };

    percent: any = [true, false, false, false];
    routeTo = 'assets/asset-list';
    private autorefreshInterval;
    private urlToRedirect: any = '';
    @Input() pageLevel: number;

  constructor(
    private commonResponseService: CommonResponseService,
        private assetGroupObservableService: AssetGroupObservableService,
        private autorefreshService: AutorefreshService,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private logger: LoggerService, private errorHandling: ErrorHandlingService,
        private utils: UtilsService,
        private workflowService: WorkflowService) {
        this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
          assetGroupName => {
              this.selectedAssetGroup = assetGroupName;
              this.updateComponent();
        });
   }

  ngOnInit() {
        this.urlToRedirect = this.router.routerState.snapshot.url;
        this.durationParams = this.autorefreshService.getDuration();
        this.durationParams = parseInt(this.durationParams, 10);
        this.autoRefresh = this.autorefreshService.autoRefresh;

        const afterLoad = this;
        if (this.autoRefresh !== undefined) {
              if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {
                this.autorefreshInterval = setInterval(function(){
                  afterLoad.getProgressData();
                }, this.durationParams);
              }
            }
        }
    /* Function to repaint component */
  updateComponent() {

    /* All functions variables which are required to be set for component to be reloaded should go here */
    this.seekdata = false;
    this.dataComing = false;
    this.showLoader = true;
    this.getData();
  }

  /* Function to get Data */
  getData() {

      /* All functions to get data should go here */
      this.getProgressData();
  }
    getProgressData() {

        if (this.dataSubscriber) {
          this.dataSubscriber.unsubscribe();
        }

        const queryParams = {
                'ag': this.selectedAssetGroup
        };

        const taggingSummaryUrl = environment.taggingSummary.url;
        const taggingSummaryMethod = environment.taggingSummary.method;

        try {
            this.dataSubscriber = this.commonResponseService.getData( taggingSummaryUrl, taggingSummaryMethod, {}, queryParams).subscribe(
            response => {
                try {

                    this.apiData = response.output;
                    this.progressDataProcess(this.apiData);
                    this.showLoader = false;
                    this.seekdata = false;
                    this.dataComing = true;
                } catch (e) {
                    this.logger.log('error', e);
                        this.errorMessage = this.errorHandling.handleJavascriptError(e);
                        this.getErrorValues();
                }
            },
            error => {
                this.logger.log('error', error);
                this.errorMessage = error;
                this.getErrorValues();
            });
        } catch (error) {
          this.logger.log('error', error);
            this.errorMessage = this.errorHandling.handleJavascriptError(error);
            this.getErrorValues();
        }
    }
    // assign error values...

    getErrorValues(): void {
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
    }
    progressDataProcess(data) {

      const response = [];
      let compliant;
      let totalAssets;
      let tagged;
      let untagged;

        if (data.compliance !== undefined) {
            compliant = {
                'value': data.compliance,
                'text': 'Compliant',
                'link': false,
                'styling': {
                    'cursor': 'text'
                }
            };
            // Set the compliance percentage color with respect to the value
            this.colors[0] = data.compliance === 100 ? this.complianceColors.compliant : (data.compliance >= 50 ? this.complianceColors.intermediate : this.complianceColors['non-compliant']);
            response.push(compliant);
        }

        if (data.assets !== undefined) {
            totalAssets = {
                'value': data.assets,
                'text': 'Total Assets',
                'link': true,
                'styling': {
                    'cursor': 'pointer'
                }
            };

            response.push(totalAssets);
        }

        if (data.tagged !== undefined) {
            tagged = {
                'value': data.tagged,
                'text': 'Tagged',
                'link': true,
                'styling': {
                    'cursor': 'pointer'
                }
            };

            response.push(tagged);
        }

        if (data.untagged !== undefined) {
            untagged = {
                'value': data.untagged,
                'text': 'Untagged',
                'link': true,
                'styling': {
                    'cursor': 'pointer'
                }
            };

            response.push(untagged);
        }

        this.dataValue = {
            'response': response
        };

    }



    capitalizeFirstLetter(string): any {
        return string.charAt(0).toUpperCase() + string.slice(1);
      }

    /**
   * This function navigates the page mentioned  with a ruleID
   */
      navigatePage(event) {

        try {
            this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
            const localObjKeys = Object.keys(event);
            const apiTarget = {'TypeAsset' : 'taggable'};

                if ( event[localObjKeys[1]].toLowerCase() === 'total assets' ) {
                    const eachParams = {};
                    let newParams = this.utils.makeFilterObj(eachParams);
                    newParams = Object.assign(newParams, apiTarget);
                        this.router.navigate(['../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge' });
                } else if ( event[localObjKeys[1]].toLowerCase() === 'tagged' ) {
                    const eachParams = {'tagged': true};
                    let newParams = this.utils.makeFilterObj(eachParams);
                    newParams = Object.assign(newParams, apiTarget);
                        this.router.navigate(['../../', 'assets', 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
                } else if ( event[localObjKeys[1]].toLowerCase() === 'untagged' ) {
                    const eachParams = {'tagged': false};
                    let newParams = this.utils.makeFilterObj(eachParams);
                    newParams = Object.assign(newParams, apiTarget);
                        this.router.navigate(['../../assets/asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge' });
                }
        } catch (error) {
            this.errorMessage = this.errorHandling.handleJavascriptError(error);
            this.logger.log('error', error);
        }
    }

  ngOnDestroy() {
    try {
      this.subscriptionToAssetGroup.unsubscribe();
      this.dataSubscriber.unsubscribe();
      clearInterval(this.autorefreshInterval);
    } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.getErrorValues();
      }
    }
}
