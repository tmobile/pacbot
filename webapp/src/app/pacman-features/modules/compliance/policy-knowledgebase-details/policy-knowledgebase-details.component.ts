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

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { environment } from './../../../../../environments/environment';
import { Router, ActivatedRoute } from '@angular/router';
import { AutorefreshService } from '../../../services/autorefresh.service';
import {LoggerService} from '../../../../shared/services/logger.service';
import {ErrorHandlingService} from '../../../../shared/services/error-handling.service';
import {WorkflowService} from '../../../../core/services/workflow.service';
import {CommonResponseService} from '../../../../shared/services/common-response.service';

@Component({
  selector: 'app-policy-knowledgebase-details',
  templateUrl: './policy-knowledgebase-details.component.html',
  styleUrls: ['./policy-knowledgebase-details.component.css'],
  providers: [LoggerService, ErrorHandlingService, CommonResponseService, AutorefreshService]
})

export class PolicyKnowledgebaseDetailsComponent implements OnInit, OnDestroy {
  pageTitle = 'Policy Details';
  breadcrumbArray: any = ['Compliance', 'Policy Knowledgebase'];
  breadcrumbLinks: any = ['compliance-dashboard', 'policy-knowledgebase'];
  breadcrumbPresent: any;
  selectedAssetGroup: string;
  subscriptionToAssetGroup: Subscription;
  public autoFix = false;
  public ruleID: any = '';
  public setRuleIdObtained = false;
  public dataComing = true;
  public showLoader = true;
  public durationParams: any;
  public autoRefresh: boolean;
  public seekdata = false;
  public dataSubscriber: Subscription;
  public errorMessage: any;
  public policyDesc: {};
  displayName: any = '';
  ruleDescription: any = '';
  resolution: any = [];
  private routeSubscription: Subscription;
  urlToRedirect: any = '';
  private previousUrl: any = '';
  private pageLevel = 0;
  public backButtonRequired;

  constructor(private assetGroupObservableService: AssetGroupObservableService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private commonResponseService: CommonResponseService,
    private autorefreshService: AutorefreshService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService, private workflowService: WorkflowService) {
      this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
        assetGroupName => {
          this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(this.pageLevel);
          this.selectedAssetGroup = assetGroupName;
          this.updateComponent();
        });
  }
  ngOnInit() {
      try {
      this.durationParams = this.autorefreshService.getDuration();
      this.durationParams = parseInt(this.durationParams, 10);
      this.autoRefresh = this.autorefreshService.autoRefresh;
      this.breadcrumbPresent = 'Policy Details ';
    } catch (error) {
      this.logger.log('error', error);
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
    this.getRuleId();
    this.getProgressData();
  }

  /**
  * this funticn gets the ruleid from the url
  */
  getRuleId() {
      /*  TODO:Trinanjan Wrong way of doing it */
      this.routeSubscription = this.activatedRoute.params.subscribe(params => {
          this.ruleID = params['ruleID'];
          this.autoFix = (params['autoFix'] === 'true');
      });
      if (this.ruleID !== undefined) {
          this.setRuleIdObtained = true;
      }
  }

  getProgressData() {
    if (this.ruleID !== undefined) {
      if (this.dataSubscriber) {
        this.dataSubscriber.unsubscribe();
      }
      const queryParams = {
        'ruleId': this.ruleID
      };
      const policyContentSliderUrl = environment.policyContentSlider.url;
      const policyContentSliderMethod = environment.policyContentSlider.method;
      try {
        this.dataSubscriber = this.commonResponseService.getData(
          policyContentSliderUrl, policyContentSliderMethod, {}, queryParams).subscribe(
        response => {
          try {
            this.showLoader = false;
            this.seekdata = false;
            this.dataComing = true;
            this.processData(response.response);
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

  getErrorValues(): void {
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
    }

  processData(data) {
    this.displayName = this.uppercasefirst(data.displayName);
    this.ruleDescription = data.ruleDescription;
    this.resolution = data.resolution;
    this.policyDesc = [

      {'value' : data.ruleCategory,
      'key' : 'Category'
      },
      {'value' : data.severity,
      'key' : 'Severity'
      },
      {'value' : data.policyVersion,
      'key' : 'PolicyVersion'
      }
    ];

  }

  /**
   * This function returns the first char as upper case
   */
  uppercasefirst(value) {
    if (value === null) {
      return 'Not assigned';
    }
    value = value.toLocaleLowerCase();
    return value.charAt(0).toUpperCase() + value.slice(1);
  }

  navigateBack() {
      try {
        this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
      } catch (error) {
        this.logger.log('error', error);
      }
  }
    /*
    * unsubscribing component
    */
  ngOnDestroy() {
    try {
      if (this.subscriptionToAssetGroup) {
        this.subscriptionToAssetGroup.unsubscribe();
      }
      if (this.dataSubscriber) {
        this.dataSubscriber.unsubscribe();
      }
      if (this.routeSubscription) {
        this.routeSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('info', '--- Error while unsubscribing ---');
    }
  }

}
