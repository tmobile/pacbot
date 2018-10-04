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

import {Component, OnInit, OnDestroy, Input} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { PolicyAcrossApplicationService } from '../../services/policy-across-application.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { UtilsService } from '../../../shared/services/utils.service';
import {ActivatedRoute, Router} from '@angular/router';
import {WorkflowService} from '../../../core/services/workflow.service';
import {DomainTypeObservableService} from '../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-policy-across-application',
  templateUrl: './policy-across-application.component.html',
  styleUrls: ['./policy-across-application.component.css'],
  providers: [PolicyAcrossApplicationService, AutorefreshService]
})

export class PolicyAcrossApplicationComponent implements OnInit, OnDestroy {

  selectedAssetGroup: string;
  selectedDomain: string;
  public apiData: any;
  public applicationValue: any;
  public errorMessage: any;
  public dataComing = true;
  public showLoader = true;
  public tableHeaderData: any;
  private subscriptionToAssetGroup: Subscription;
  private subscriptionToDomain: Subscription;

  private dataSubscription: Subscription;
  public placeHolderTextasset = '';
  public placeHolderTextenv = '';
  public returnedSearchapp = '';
  public returnedSearchenv = '';
  public seekdata = false;
  public sampleData;
  durationParams: any;
  autoRefresh: boolean;
  @Input() ruleID: any;
  public applicationName: any = '';
  apiAppData: any = {};
  apiEnvData: any = {};
  showEnv = false;
  showAsset = true;
  private autorefreshInterval;
  @Input() pageLevel: number;
  private urlToRedirect: string;

  constructor(

    private policyAcrossApplicationService: PolicyAcrossApplicationService,
    private assetGroupObservableService: AssetGroupObservableService,
    private autorefreshService: AutorefreshService,
    private logger: LoggerService, private errorHandling: ErrorHandlingService,
    private utils: UtilsService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private workflowService: WorkflowService,
    private domainObservableService: DomainTypeObservableService

  ) {

    this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
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

    const afterLoad = this;
        if (this.autoRefresh !== undefined) {
          if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {

            this.autorefreshInterval = setInterval(function() {
              afterLoad.getData();
            }, this.durationParams);
          }
        }

    this.placeHolderTextasset = 'Search by App';
    this.placeHolderTextenv = 'Search by Env';
    this.updateComponent();
  }

  getappSearch(search) {
    this.returnedSearchapp = search;
  }
  getenvSearch(search) {
    this.returnedSearchenv = search;
  }
   /* Function to repaint component */
   updateComponent() {

      /* All functions variables which are required to be set for component to be reloaded should go here */

      this.showLoader = true;
      this.dataComing = false;
      this.seekdata = false;
      this.showEnv = false;
      this.showAsset = true;
      this.getData();
  }

  /* Function to get Data */

  getData() {

      /* All functions to get data should go here */
      this.getPolicyAcrossApplication();
  }

/**
 * This function fetches data for distribution across application
 */

  getPolicyAcrossApplication() {

    if (this.dataSubscription) {
        this.dataSubscription.unsubscribe();
      }

    if (this.ruleID !== undefined) {
      const queryParams = {
        'ag': this.selectedAssetGroup,
        'ruleId' : this.ruleID,
        'domain': this.selectedDomain
    };

    const policyAcrossApplicationUrl = environment.policyAcrossApplication.url;
    const policyAcrossApplicationMethod = environment.policyAcrossApplication.method;


    this.dataSubscription = this.policyAcrossApplicationService.getpolicyApplication(queryParams, policyAcrossApplicationUrl, policyAcrossApplicationMethod).subscribe(
      response => {
        try {

          if (response.length === 0) {
            this.getErrorValues();
            this.errorMessage = 'policyDetailsMessage';
          } else {

            /**
             * policy across application uses 2api
             * if the DISTRIBUTION accross asset api returns only one value , 2nd api(DISTRIBUTION across envs) will be called
             */

            this.processAppData(response);

             if (response.length === 1) {
              /**
               * call the funtion which hits the 2nd api
               */
              this.applicationName = response[0].AppName;
              this.getPolicyAcrossEnv();
             } else {
                this.showAsset = true;
                this.showEnv = false;
                this.showLoader = false;
                this.seekdata = false;
                this.dataComing = true;
             }
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

  }

  /**
   * this function gets environment  details for  a particular application
   * this funtion is call if the api response = 1 from getPolicyAcrossApplication function
   */


  getPolicyAcrossEnv() {

    if (this.dataSubscription) {
        this.dataSubscription.unsubscribe();
      }

    if (this.ruleID !== undefined) {
      const queryParams = {
        'ag': this.selectedAssetGroup,
        'application' : this.applicationName,
        'ruleId' : this.ruleID,
        'domain': this.selectedDomain
    };

    const policyAcrossEnvUrl = environment.policyAcrossEnv.url;
    const policyAcrossEnvMethod = environment.policyAcrossEnv.method;


    this.dataSubscription = this.policyAcrossApplicationService.getpolicyApplication(queryParams, policyAcrossEnvUrl, policyAcrossEnvMethod).subscribe(
      response => {
        try {

          this.showLoader = false;
          this.seekdata = false;
          this.dataComing = true;

          if (response.length === 0) {

            this.showEnv = false;
            this.showAsset = true;

          } else {

            this.showEnv = true;
            this.showAsset = false;
            this.processEnvData(response);
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
  }

/**
 * This function is used to convert first letter to uppercase
 */

  uppercasefirst(value) {
    if (value === null) {
      return 'Not assigned';
    }
    return value.charAt(0).toUpperCase() + value.slice(1);
  }


  /**
   * This function  is called to set the error blocks
   */

  getErrorValues(message?: any): void {
    this.showEnv = false;
    this.showAsset = true;
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
    if (message) {
      this.errorMessage = message;
  }
  }

  /**
   * This processAppData,processEnvData process data before passing it to asset/env component
   */

  processAppData(data) {
    this.tableHeaderData = ['', 'Total asset', 'Compliant asset', 'Non-compliant asset'];
    this.apiAppData = data;
  }
  processEnvData(data) {

    this.showEnv = true;
    this.tableHeaderData = ['', 'Total asset', 'Compliant asset', 'Non-compliant asset'];
    this.apiEnvData = data;
  }

    /**
   * This function navigates the page mentioned in the routeTo variable with a querypareams
   */

  navigatePage(event) {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      const apiTarget = {'TypeAsset' : 'scanned'};
      const localObjKeys = Object.keys(event);
      const currentApp = event[localObjKeys[0]];
      const colName = event[localObjKeys[1]];
      const eachParams = {'ruleId': this.ruleID, 'application': currentApp};
        if (colName.toLowerCase() === 'non-compliant asset') {
          eachParams['compliant'] = false;
        }
        if (colName.toLowerCase() === 'compliant asset') {
          eachParams['compliant'] = true;
        }
        let newParams = this.utils.makeFilterObj(eachParams);
        newParams = Object.assign(newParams, apiTarget);
        newParams['mandatory'] = 'ruleId';
        this.router.navigate(['../../../', 'assets', 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  navigatePageEnv(event) {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      const apiTarget = {'TypeAsset' : 'scanned'};
      const localObjKeys = Object.keys(event);
      const currentApp = event[localObjKeys[0]];
      const colName = event[localObjKeys[1]];
      const eachParams = {'ruleId': this.ruleID, 'environment': currentApp};
      if (colName.toLowerCase() === 'non-compliant asset') {
        eachParams['compliant'] = false;
      }
      if (colName.toLowerCase() === 'compliant asset') {
        eachParams['compliant'] = true;
      }
      let newParams = this.utils.makeFilterObj(eachParams);
      newParams = Object.assign(newParams, apiTarget);
      newParams['mandatory'] = 'ruleId';
      this.router.navigate(['../../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
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
      this.dataSubscription.unsubscribe();
      clearInterval(this.autorefreshInterval);
    } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.getErrorValues();
    }
  }
}
