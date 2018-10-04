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

import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import {LoggerService} from '../../../shared/services/logger.service';
import {ErrorHandlingService} from '../../../shared/services/error-handling.service';
import {Router} from '@angular/router';
import {CommonResponseService} from '../../../shared/services/common-response.service';

@Component({
  selector: 'app-patching-current-state',
  templateUrl: './patching-current-state.component.html',
  styleUrls: ['./patching-current-state.component.css'],
  providers: [CommonResponseService, AutorefreshService]
})

export class PatchingCurrentStateComponent implements OnInit, OnDestroy {

  selectedAssetGroup: string;
  public errorMessage: any;
  public showLoaderExec = true;
  public showLoader = true;
  private subscriptionToAssetGroup: Subscription;
  private dataSubscription: Subscription;
  private dataSubscriptionExec: Subscription;
  public seekdata = false;
  public seekdataExec = false;
  durationParams: any;
  autoRefresh: boolean;
  topState: any;
  listArrays: any;
  execState: any;
  execArrays: any;
  reportDate: any;
  reportTime: any;
  minutes: any;
  private autorefreshInterval;

  @Input() pageLevel: number;
  private urlToRedirect: string;

  constructor(private commonResponseService: CommonResponseService,
              private assetGroupObservableService: AssetGroupObservableService,
              private autorefreshService: AutorefreshService,
              private logger: LoggerService,
              private errorHandling: ErrorHandlingService,
              private router: Router) {

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
          afterLoad.getData();
        }, this.durationParams);
      }
    }
  }

  /* Function to repaint component */
   updateComponent() {
      /* All functions variables which are required to be set for component to be reloaded should go here */
      this.showLoader = true;
      this.showLoaderExec = true;
      this.seekdata = false;
      this.seekdataExec = false;
      this.getData();
  }

  getData() {

      /* All functions to get data should go here */
      this.getPatchingCurrentState();
      this.getPatchingExecState();
      this.getDateValue();
      this.getTimevalue();
  }

  getDateValue() {

    const date = new Date();
    let month = date.getMonth();
    month++;
    const day = date.getDate();
    let year = date.getFullYear().toString();
    year = year.slice(-2);
    this.reportDate =  month + '/' + day + '/' + year;
  }

  getTimevalue() {

    const date = new Date();
    const timezone = /\((.*)\)/.exec(date.toString())[1];
    const timezoneTxt = timezone.replace(/[^A-Z]/g, '');
    let hours = date.getHours();
    this.minutes = date.getMinutes();
    if (this.minutes < 10) {
        this.minutes = '0' + this.minutes;
    }

    if (hours > 12) {
        hours = hours - 12;
        this.reportTime =  hours + ':' + this.minutes + 'PM' + ' ' + timezoneTxt;
    } else {
        this.reportTime =  hours + ':' + this.minutes + 'AM' + ' ' + timezoneTxt;
    }
  }

  getPatchingCurrentState() {

    const queryParams = {
        'ag': this.selectedAssetGroup
    };

    const patchingStateUrl = environment.patchingState.url;
    const patchingStateMethod = environment.patchingState.method;


    this.dataSubscription = this.commonResponseService.getData( patchingStateUrl, patchingStateMethod, {}, queryParams).subscribe(
      response => {
        try {
          if (response.response.length === 0) {
            this.getErrorValues();
            this.errorMessage = 'patchingMessage';
          } else {
            this.showLoader = false;
            this.seekdata = false;
            this.processDataforCompliance(response);
          }

        }catch (e) {
            this.errorMessage = this.errorHandling.handleJavascriptError(e);
            this.getErrorValues();
        }
    },
    error => {
      this.errorMessage = error;
      this.getErrorValues();
    });
  }

  getPatchingExecState() {

    if (this.dataSubscriptionExec) {
      this.dataSubscriptionExec.unsubscribe();
    }

    const queryParams = {
        'ag': this.selectedAssetGroup
    };

    const patchingExecUrl = environment.patchingStateexec.url;
    const patchingExecMethod = environment.patchingStateexec.method;


    this.dataSubscriptionExec = this.commonResponseService.getData( patchingExecUrl, patchingExecMethod, {}, queryParams).subscribe(
      response => {
        try {
          if (response.response.length === 0) {
            this.getErrorValuesExec();
            this.errorMessage = 'patchingMessage';
          } else {
            this.showLoaderExec = false;
            this.seekdataExec = false;
            this.processDataforExec(response);
            // this.topState = response[0].response;
          }

        }catch (e) {
            this.errorMessage = this.errorHandling.handleJavascriptError(e);
            this.getErrorValuesExec();
        }
    },
    error => {
      this.errorMessage = error;
      this.getErrorValuesExec();
    });
  }


  processDataforCompliance(data) {
    this.topState = data;
    this.listArrays = ['App', 'Director', 'Non Compliant'];
  }

  processDataforExec(data) {
    this.execState = data;
    this.execArrays = ['Executive Sponsor', 'Non Compliant'];
  }

  getErrorValues(): void {
    this.showLoader = false;
    this.topState = false;
    this.seekdata = true;
  }

  getErrorValuesExec(): void {
    this.showLoaderExec = false;
    this.execState = false;
    this.seekdataExec = true;
  }

  ngOnDestroy() {
    try {
      this.subscriptionToAssetGroup.unsubscribe();
      this.dataSubscription.unsubscribe();
      this.dataSubscriptionExec.unsubscribe();
      clearInterval(this.autorefreshInterval);
    } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.getErrorValues();
        this.getErrorValuesExec();
    }
  }

}
