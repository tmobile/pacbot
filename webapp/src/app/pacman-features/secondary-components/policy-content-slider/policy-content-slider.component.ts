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
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { ActivatedRoute, Router } from '@angular/router';
import { WorkflowService } from '../../../core/services/workflow.service';

@Component({
  selector: 'app-policy-content-slider',
  templateUrl: './policy-content-slider.component.html',
  styleUrls: ['./policy-content-slider.component.css'],
  providers: [LoggerService, ErrorHandlingService, CommonResponseService, AutorefreshService]
})

export class PolicyContentSliderComponent implements OnInit, OnDestroy {
  subscriptionToAssetGroup: Subscription;
  selectedAssetGroup: string;
  public dataSubscriber: Subscription;
  public apiData: any;
  public applicationValue: any;
  public errorMessage: any;
  public dataComing = true;
  public showLoader = true;
  durationParams: any;
  autoRefresh: boolean;
  public seekdata = false;

  private autorefreshInterval;

  @Input() ruleID: any;
  @Input() pageLevel: number;
  public title = '';
  public titleDesc = '';
  private urlToRedirect;

  constructor(
    private commonResponseService: CommonResponseService,
    private assetGroupObservableService: AssetGroupObservableService,
    private autorefreshService: AutorefreshService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
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
        this.updateComponent();
        const afterLoad = this;
    if (this.autoRefresh !== undefined) {
      if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {

        this.autorefreshInterval = setInterval(function() {
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
            const queryParams = {
                'ruleId': this.ruleID
        };

        if (this.dataSubscriber) {
            this.dataSubscriber.unsubscribe();
        }

            const policyContentSliderUrl = environment.policyContentSlider.url;
            const policyContentSliderMethod = environment.policyContentSlider.method;

            try {
                this.dataSubscriber = this.commonResponseService.getData( policyContentSliderUrl, policyContentSliderMethod, {}, queryParams).subscribe(
                response => {
                    try {
                        this.showLoader = false;
                        this.seekdata = false;
                        this.dataComing = true;
                        this.apiData = response.response;
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

    this.title = data.displayName;
    this.titleDesc = data.ruleDescription;
    /*
        * This is to make the required json structure
    */
    }

    /**
   * This function navigates the page mentioned  with a ruleID
   */

    navigateToPlolicy(event) {
        try {
            this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
            this.router.navigate(['../../policy-knowledgebase-details', this.ruleID], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});

        } catch (error) {
                this.errorMessage = this.errorHandling.handleJavascriptError(error);
                this.logger.log('error', error);
        }
    }

    /* navigateToPlolicy function ends here */

    /*
        * unsubscribing component
     */
    ngOnDestroy() {
        try {
          this.subscriptionToAssetGroup.unsubscribe();
          this.dataSubscriber.unsubscribe();
          clearInterval(this.autorefreshInterval);
        } catch (error) {
          this.logger.log('info', '--- Error while unsubscribing ---');
        }
      }
}
