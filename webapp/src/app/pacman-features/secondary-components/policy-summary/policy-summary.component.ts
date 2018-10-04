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
import { Subscription } from 'rxjs/Subscription';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { ActivatedRoute, UrlSegment, Router } from '@angular/router';
import { UtilsService } from '../../../shared/services/utils.service';
import { WorkflowService } from '../../../core/services/workflow.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-policy-summary',
  templateUrl: './policy-summary.component.html',
  styleUrls: ['./policy-summary.component.css'],
  providers: [LoggerService, ErrorHandlingService, CommonResponseService, AutorefreshService]
})

export class PolicySummaryComponent implements OnInit, OnDestroy {

    subscriptionToAssetGroup: Subscription;
    subscriptionToDomain: Subscription;
    public dataSubscriber: Subscription;

    selectedAssetGroup: string;
    selectedDomain: string;
    public apiData: any;
    public applicationValue: any;
    public errorMessage: any;
    public dataComing = true;
    public showLoader = true;
    durationParams: any;
    autoRefresh: boolean;
    public seekdata = false;
    private autorefreshInterval;

    private urlToRedirect: string;
    @Input() pageLevel: number;
    @Input() ruleID: any;

  dataValue: any;
  colors: any = ['#00b946', '#1c5066', '#00b946', 'rgb(248,146,87)'];
  complianceColors = {
        'non-compliant' : '#D40325',
        'compliant'     : '#00B946',
        'intermediate'  : '#F75C03'
    };
  percent: any = [true, false, false, false];
  constructor(
    private commonResponseService: CommonResponseService,
        private assetGroupObservableService: AssetGroupObservableService,
        private autorefreshService: AutorefreshService,
        private logger: LoggerService,
        private errorHandling: ErrorHandlingService,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private utils: UtilsService,
        private workflowService: WorkflowService,
        private domainObservableService: DomainTypeObservableService
    ) {

        this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroupName => {
            this.selectedAssetGroup = assetGroupName;
        });

        this.subscriptionToDomain = this.domainObservableService.getDomainType().subscribe(domain => {
            this.selectedDomain = domain;
            this.updateComponent();
        });
   }

  ngOnInit() {
        this.urlToRedirect = this.router.routerState.snapshot.url;
        this.durationParams = this.autorefreshService.getDuration();
        this.durationParams = parseInt(this.durationParams, 10);
        this.autoRefresh = this.autorefreshService.autoRefresh;
        this.updateComponent();
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
        if (this.ruleID !== undefined) {

            if (this.dataSubscriber) {
                this.dataSubscriber.unsubscribe();
            }

            const payload = {
                'ag': this.selectedAssetGroup,
                'filter': { 'ruleId.keyword': this.ruleID, 'domain': this.selectedDomain},
                'from': 0,
                'searchtext': '',
                'size': 0
                };

            const policySummaryUrl = environment.policySummary.url;
            const policySummaryMethod = environment.policySummary.method;

            try {
                this.dataSubscriber = this.commonResponseService.getData( policySummaryUrl, policySummaryMethod, payload, {}).subscribe(
                response => {
                    try {
                        this.showLoader = false;
                        this.seekdata = false;
                        this.dataComing = true;
                        this.apiData = response.data;
                        this.progressDataProcess(this.apiData);
                    } catch (e) {
                            this.errorMessage = this.errorHandling.handleJavascriptError(e);
                            this.getErrorValues();
                    }
                },
                error => {
                    this.errorMessage = error;
                    this.getErrorValues();
                });
            } catch (error) {
                this.errorMessage = this.errorHandling.handleJavascriptError(error);
                this.getErrorValues();
            }
        }
    }
    // assign error values...

    getErrorValues(): void {
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
    }

    /*
        * this funciton process data to a desired format before passing it to app-generic-summary
    */

    progressDataProcess(data) {

        const response = [];
        let compliant;
        let totalScanned;
        let passed;
        let failed;

    /*
        * This is to make the required json structure
    */


        if (data.response[0].compliance_percent !== undefined) {
            compliant = {
                'value': data.response[0].compliance_percent,
                'text': 'Compliant',
                'link': false,
                'styling': {
                     'cursor': 'text'
                }
            };

            // Set the compliance percentage color with respect to the value
            this.colors[0] = data.response[0].compliance_percent === 100 ? this.complianceColors.compliant : (data.response[0].compliance_percent >= 50 ? this.complianceColors.intermediate : this.complianceColors['non-compliant']);

            response.push(compliant);
        }

        if (data.response[0].assetsScanned !== undefined) {
            totalScanned = {
                'value': data.response[0].assetsScanned,
                'text': 'Assets Scanned',
                'link': true,
                'styling': {
                     'cursor': 'pointer'
                }
            };

            response.push(totalScanned);
        }

        if (data.response[0].passed !== undefined) {
            passed = {
                'value': data.response[0].passed,
                'text': 'Passed',
                'link': true,
                'styling': {
                     'cursor': 'pointer'
                }
            };

            response.push(passed);
        }

        if (data.response[0].failed !== undefined) {
            failed = {
                'value': data.response[0].failed,
                'text': 'failed',
                'link': true,
                'styling': {
                     'cursor': 'pointer'
                }
            };

            response.push(failed);
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
            const apiTarget = {'TypeAsset' : 'scanned'};

                if ( event[localObjKeys[1]].toLowerCase() === 'assets scanned' ) {
                    const eachParams = {'ruleId': this.ruleID};
                      let newParams = this.utils.makeFilterObj(eachParams);
                      newParams = Object.assign(newParams, apiTarget);
                      newParams['mandatory'] = 'ruleId';
                        this.router.navigate(['../../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
                } else if ( event[localObjKeys[1]].toLowerCase() === 'passed' ) {
                    const eachParams = {'ruleId': this.ruleID , 'compliant' : true};
                      let newParams = this.utils.makeFilterObj(eachParams);
                      newParams = Object.assign(newParams, apiTarget);
                      newParams['mandatory'] = 'ruleId';
                        this.router.navigate(['../../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
                } else if ( event[localObjKeys[1]].toLowerCase() === 'failed' ) {
                        const eachParams = {'ruleId': this.ruleID , 'compliant' : false};
                        let newParams = this.utils.makeFilterObj(eachParams);
                        newParams = Object.assign(newParams, apiTarget);
                        newParams['mandatory'] = 'ruleId';
                        this.router.navigate(['../../../', 'assets', 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
                }
        } catch (error) {
            this.errorMessage = this.errorHandling.handleJavascriptError(error);
            this.logger.log('error', error);
        }
    }
    /* navigatePage function ends here */
    /*
        * unsubscribing component
     */
    ngOnDestroy() {
        try {
          this.subscriptionToAssetGroup.unsubscribe();
          this.subscriptionToDomain.unsubscribe();
          this.dataSubscriber.unsubscribe();
          clearInterval(this.autorefreshInterval);
        } catch (error) {
          this.logger.log('info', '--- Error while unsubscribing ---');
        }
      }
}
