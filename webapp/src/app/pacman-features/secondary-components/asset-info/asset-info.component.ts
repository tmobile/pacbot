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

import {
  Component,
  OnInit,
  Input,
  OnDestroy,
  OnChanges
} from '@angular/core';
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
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-asset-info',
  templateUrl: './asset-info.component.html',
  styleUrls: ['./asset-info.component.css'],
  providers: [CommonResponseService, AutorefreshService]
})

export class AssetInfoComponent implements OnInit, OnDestroy, OnChanges {
  selectedAssetGroup: string;
  public assetDistributionData: any = [];
  public assetCountData: any = {};
  public untaggedApplicationData;

  private subscriptionToAssetGroup: Subscription;
  private progressSubscription: Subscription;
  private assetDistributionSubscription: Subscription;
  subscriptionDomain: Subscription;
  private applicationUntaggedSubscription: Subscription;
  selectedDomain: any;

  public seekdata = false;
  @Input() targetType: any;
  public dataComing = true;
  public showLoader = true;
  durationParams: any;
  autoRefresh: boolean;
  errorMessage: any;
  @Input() pageLevel: number;
  urlToRedirect = '';
  private autorefreshInterval;

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
    private domainObservableService: DomainTypeObservableService) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        this.selectedAssetGroup = assetGroupName;
      });

      this.subscriptionDomain = this.domainObservableService.getDomainType().subscribe(domain => {
         this.selectedDomain = domain;
         this.updateComponent();
      });
  }

  ngOnChanges() {
    this.updateComponent();
  }

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.durationParams = this.autorefreshService.getDuration();
    this.durationParams = parseInt(this.durationParams, 10);
    this.autoRefresh = this.autorefreshService.autoRefresh;
    const afterLoad = this;
    if (this.autoRefresh !== undefined) {
      if (this.autoRefresh === true || this.autoRefresh.toString() === 'true') {
        this.autorefreshInterval = setInterval(function() {
          afterLoad.getData();
        }, this.durationParams);
      }
    }
  }
  updateComponent() {
    /* All functions variables which are required to be set for component to be reloaded should go here */

    this.showLoader = true;
    this.dataComing = true;
    this.seekdata = false;
    this.assetDistributionData = [];
    this.assetCountData = {};
    this.getData();
  }
  getData() {
    /* All functions to get data should go here */
    this.getProgressData();
    this.getApplicationUntaggedData();
  }


  getProgressData() {

    try {

    if (this.progressSubscription) {
      this.progressSubscription.unsubscribe();
    }
    if (this.targetType !== undefined) {
      const queryParams = {
        'ag': this.selectedAssetGroup,
        'type': this.targetType,
        'domain': this.selectedDomain
      };
      const assetDistributionUrl = environment.AssetDistribution.url;
      const assetDistributionMethod = environment.AssetDistribution.method;

      this.progressSubscription = this.commonResponseService
        .getData(assetDistributionUrl, assetDistributionMethod, {}, queryParams)
        .subscribe(
          response => {
            try {

              this.assetDistributionData = response.assetcount;

              this.showLoader = false;
              this.seekdata = false;
              this.dataComing = true;
            } catch (e) {
              this.errorMessage = this.errorHandling.handleJavascriptError(e);
              this.getErrorValues();
              this.logger.log('error', e);

            }
          },
          error => {
            this.errorMessage = error;
            this.getErrorValues();
            this.logger.log('error', error);
          }
        );
    }
  } catch (e) {
    this.logger.log('error', e);
  }

  }

  getApplicationUntaggedData() {

    try {
    if (this.targetType !== undefined) {
      const queryParams = {
        ag: this.selectedAssetGroup,
        targettype: this.targetType
      };
      const applicationUntaggedUrl = environment.applicationUntagged.url;
      const applicationUntaggedMethod = environment.applicationUntagged.method;

      this.applicationUntaggedSubscription = this.commonResponseService
        .getData( applicationUntaggedUrl, applicationUntaggedMethod, {}, queryParams)
        .subscribe(
          response => {
            try {

              if (!this.utils.checkIfAPIReturnedDataIsEmpty(response)) {
                this.untaggedApplicationData = response.response[0][this.targetType];
              }
              this.showLoader = false;
              this.seekdata = false;
              this.dataComing = true;
            } catch (e) {
              this.errorMessage = this.errorHandling.handleJavascriptError(e);
              this.getErrorValues();
              this.logger.log('error', e);
            }
          },
          error => {
            this.errorMessage = error;
            this.getErrorValues();
            this.logger.log('error', error);
          }
        );
    }
  } catch (e) {
    this.logger.log('error', e);
  }

  }

  // function to get data for tagging/untagginf api ends here
  getErrorValues(): void {
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
  }

  capitalizeFirstLetter(string): any {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }

  /**
   * This function navigates the page mentioned  with a ruleID
   */
  NavigatetoAsset(event) {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      const clickData = event;
      const apiTarget = {'TypeAsset' : 'taggable'};

      if (clickData.toLowerCase() === 'tagged') {
        const eachParams = { 'tagged': true, 'resourceType': this.targetType };
        let newParams = this.utils.makeFilterObj(eachParams);
        newParams = Object.assign(newParams , apiTarget);
        this.router.navigate(['../../', 'assets', 'asset-list'], {
          relativeTo: this.activatedRoute,
          queryParams: newParams,
          queryParamsHandling: 'merge'
        });
      } else if (clickData.toLowerCase() === 'untagged') {
        const eachParams = { 'tagged': false, 'resourceType': this.targetType, tagName: 'Application' };
        let newParams = this.utils.makeFilterObj(eachParams);
        newParams = Object.assign(newParams , apiTarget);
        this.router.navigate(['../../', 'assets', 'asset-list'], {
          relativeTo: this.activatedRoute,
          queryParams: newParams,
          queryParamsHandling: 'merge'
        });
      } else {
        const eachParams = { 'application': clickData, 'resourceType': this.targetType };
        const newParams = this.utils.makeFilterObj(eachParams);
        this.router.navigate(['../../', 'assets', 'asset-list'], {
          relativeTo: this.activatedRoute,
          queryParams: newParams,
          queryParamsHandling: 'merge'
        });
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
      if (this.progressSubscription) {this.progressSubscription.unsubscribe(); }
      if (this.assetDistributionSubscription) {this.assetDistributionSubscription.unsubscribe(); }
      if (this.subscriptionDomain) { this.subscriptionDomain.unsubscribe(); }
      if (this.applicationUntaggedSubscription) {this.applicationUntaggedSubscription.unsubscribe(); }
      clearInterval(this.autorefreshInterval);
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.getErrorValues();
    }
  }
}
