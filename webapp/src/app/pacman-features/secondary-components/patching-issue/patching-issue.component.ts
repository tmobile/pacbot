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
  selector: 'app-patching-issue',
  templateUrl: './patching-issue.component.html',
  styleUrls: ['./patching-issue.component.css'],
  providers: [LoggerService, ErrorHandlingService, CommonResponseService, AutorefreshService]
})

export class PatchingIssueComponent implements OnInit, OnDestroy {
  dataValue: any;
  colors: any = ['#d40325', '#1c5066', '#00b946', '#e60127'];
  complianceColors = {
    'non-compliant' : '#D40325',
    'compliant'     : '#00B946',
    'intermediate'  : '#F75C03'
  };

  percent: any = [true, false, false, false];
  subscriptionToAssetGroup: Subscription;
  dataSubscriber: Subscription;
  selectedAssetGroup: string;
  errorMessages;
  durationParams: any;
  autoRefresh: boolean;
  error: boolean;
  showLoader = true;
  errorMessage: any;
  seekdata = false;
  dataComing = false;
  urlToRedirect = '';
  @Input() pageLevel: number;

  private autorefreshInterval;

  constructor(private commonResponseService: CommonResponseService,
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

            this.autorefreshInterval = setInterval(function() {
              afterLoad.getData();
            }, this.durationParams);
          }
        }
  }

  /* Function to repaint component */
    updateComponent() {

        /* All functions variables which are required to be set for component to be reloaded should go here */
        this.showLoader = true;
        this.seekdata = false;
        this.dataComing = false;
        this.getData();
    }

    /* Function to get Data */
    getData() {

        /* All functions to get data should go here */
        this.getPatchingData();
    }

    getPatchingData() {

      if (this.dataSubscriber) {
        this.dataSubscriber.unsubscribe();
      }

      const queryParams = {
          'ag': this.selectedAssetGroup
      };

      const Url = environment.patchingSummary.url;
      const Method = environment.patchingSummary.method;


      try {
        this.dataSubscriber = this.commonResponseService.getData( Url, Method, {}, queryParams).subscribe(
        response => {
          try {
            this.processData(response);

              this.showLoader = false;
              this.seekdata = false;
              this.dataComing = true;
          }catch (e) {
              this.errorMessage = this.errorHandling.handleJavascriptError(e);
              this.showLoader = false;
              this.seekdata = true;
              this.dataComing = false;

          }
        },
        error => {
          this.errorMessage = error;
          this.showLoader = false;
          this.seekdata = true;
          this.dataComing = false;
        });
      } catch (error) {
      }
   }

   processData(data) {

    const response = [];
    let compliant;
    let total;
    let patched;
    let unpatched;

    // Set the compliance percentage color with respect to the value
    this.colors[0] = data.output.patching_percentage === 100 ? this.complianceColors.compliant : (data.output.patching_percentage >= 50 ? this.complianceColors.intermediate : this.complianceColors['non-compliant']);

    if (data.output.patching_percentage !== undefined) {
      compliant = {
        'value': data.output.patching_percentage,
         'text': 'Compliant',
         'link' : false,
         'styling': {
         'cursor': 'text'
        }
      };

      response.push(compliant);
    }

    if (data.output.total_instances !== undefined) {
      total = {
        'value': data.output.total_instances,
        'text': 'Total Instances',
         'link' : true,
         'styling': {
         'cursor': 'pointer'
        }
      };

      response.push(total);
    }

    if ( data.output.patched_instances !== undefined) {
      patched = {
        'value': data.output.patched_instances,
         'text': 'Patched',
         'link' : true,
         'styling': {
         'cursor': 'pointer'
        }
      };

      response.push(patched);
    }

    if (data.output.unpatched_instances !== undefined) {
      unpatched = {
        'value': data.output.unpatched_instances,
         'text': 'Unpatched',
         'link' : true,
         'styling': {
         'cursor': 'pointer'
        }
      };

      response.push(unpatched);
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
          const localObjKeys = Object.keys(event);
          const apiTarget = {'TypeAsset' : 'patchable'};
            this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
          if ( event[localObjKeys[1]].toLowerCase() === 'total instances' ) {
            const eachParams = {};
              let newParams = this.utils.makeFilterObj(eachParams);
              newParams = Object.assign(newParams , apiTarget);
              this.router.navigate(['../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
          }
          if ( event[localObjKeys[1]].toLowerCase() === 'patched' ) {
              const eachParams = {'patched': true};
              let newParams = this.utils.makeFilterObj(eachParams);
              newParams = Object.assign(newParams , apiTarget);
              this.router.navigate(['../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
          } else if ( event[localObjKeys[1]].toLowerCase() === 'unpatched' ) {
              const eachParams = {'patched': false};
              let newParams = this.utils.makeFilterObj(eachParams);
              newParams = Object.assign(newParams, apiTarget);
              this.router.navigate(['../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
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
      this.dataSubscriber.unsubscribe();
      clearInterval(this.autorefreshInterval);
    } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.showLoader = false;
        this.seekdata = true;
        this.dataComing = false;
    }
  }

}
