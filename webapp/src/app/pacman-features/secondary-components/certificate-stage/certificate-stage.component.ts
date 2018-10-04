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

import { Component, OnInit, Input, OnDestroy } from '@angular/core';
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
  selector: 'app-certificate-stage',
  templateUrl: './certificate-stage.component.html',
  styleUrls: ['./certificate-stage.component.css'],
  providers: [LoggerService, ErrorHandlingService, CommonResponseService, AutorefreshService]
})
export class CertificateStageComponent implements OnInit, OnDestroy {


  public progressdata: any;
  subscriptionToAssetGroup: Subscription;
  selectedAssetGroup: string;
  public dataSubscriber: any;
  public apiData: any;
  public applicationValue: any;
  public errorMessage: any;
  public dataComing = true;
  public showLoader = true;
  durationParams: any;
  autoRefresh: boolean;
  public seekdata = false;
  @Input() headerText: any;
  routeTo = 'certificates';
  private autorefreshInterval;
  @Input() pageLevel: number;
  private urlToRedirect;

    constructor(
    private commonResponseService: CommonResponseService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private assetGroupObservableService: AssetGroupObservableService,
    private autorefreshService: AutorefreshService,
    private logger: LoggerService, private errorHandling: ErrorHandlingService,
    private utils: UtilsService, private workflowService: WorkflowService) {

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

      const certificateStageUrl = environment.certificateStage.url;
      const certificateStageMethod = environment.certificateStage.method;

      try {
        this.dataSubscriber = this.commonResponseService.getData( certificateStageUrl, certificateStageMethod, {}, queryParams).subscribe(
        response => {
          try {
            if (Object.keys(response.distribution).length === 0) {
              this.getErrorValues();
              this.errorMessage = 'certificateMessage';
            } else {
              this.showLoader = false;
              this.seekdata = false;
              this.dataComing = true;
              this.apiData = response.distribution;
              this.stageDataProcess(this.apiData);
            }
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
    // assign error values...

    getErrorValues(message?: any): void {
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
    if (message) {
      this.errorMessage = message;
  }
    }
    stageDataProcess(data) {
      /* function to process api response to desired json object */
      const localData = data;
      const localDataKeys = Object.keys(localData);
      let jsonObj = [];

      /* function to create desired json object */

      localDataKeys.forEach((element, index) => {

        /* here goes the calculation for the width of the progress bar */
        const barcolorMain = ['#e60127', '#F89257'];
        const barcolorSecondary = ['#00b946', '#00b946'];

        let leftBarWidth = localData[element].expiry30Days;
        let rightBarWidth = localData[element].expiry45Days;

        const checkLeftWidth = isNaN(leftBarWidth);
        const checkRightWidth =  isNaN(rightBarWidth);

        // var totalNum = localData[element].expiry30Days + localData[element].expiry45Days;
        const totalNum = localData[element].expiry45Days;

        if ((leftBarWidth === 0 && rightBarWidth === 0) || (checkLeftWidth === true && checkRightWidth === true)) {
          leftBarWidth = 1;
          rightBarWidth = 1;
        }
         /*this creates the Json obj */
         if (localData[element].expiry45Days === 0 || localData[element].expiry30Days === 0) {

            if (localData[element].expiry45Days === 0 && localData[element].expiry30Days !== 0) {
              /* if the appnum is 0 then push barcolorSecondary to barcolor */
              jsonObj.push({
                'appNum': totalNum,
                'appName': element,
                'expiry30Days': localData[element].expiry30Days,
                'expiry45Days': localData[element].expiry45Days,
                'leftBarWidth': leftBarWidth,
                'rightBarWidth': rightBarWidth,
                'barColor': barcolorMain,
                'zeroValueColor' : '#000',
                'showBar' : true,
                'link': true
            });
            }
            if (localData[element].expiry30Days === 0 && localData[element].expiry45Days !== 0) {
              jsonObj.push({
                'appNum': totalNum,
                'appName': element,
                'expiry30Days': localData[element].expiry30Days,
                'expiry45Days': localData[element].expiry45Days,
                'leftBarWidth': leftBarWidth,
                'rightBarWidth': rightBarWidth,
                'barColor': barcolorMain,
                'zeroValueColor' : '#000',
                'showBar' : true,
                'link': true
            });
            }
            if (localData[element].expiry45Days === 0 && localData[element].expiry30Days === 0) {
              jsonObj.push({
                'appNum': totalNum,
                'appName': element,
                'expiry30Days': localData[element].expiry30Days,
                'expiry45Days': localData[element].expiry45Days,
                'leftBarWidth': leftBarWidth,
                'rightBarWidth': rightBarWidth,
                'barColor': barcolorSecondary,
                'zeroValueColor' : '#00b946',
                'showBar' : false,
                'link': true
            });
            }
        } else {
            /* if the appnum is not 0 then push barcolorMain to barcolor  */

            jsonObj.push({
              'appNum': totalNum,
              'appName': element,
              'expiry30Days': localData[element].expiry30Days,
              'expiry45Days': localData[element].expiry45Days,
              'leftBarWidth': leftBarWidth,
              'rightBarWidth': rightBarWidth,
              'barColor' : barcolorMain,
              'zeroValueColor' : '#000',
              'showBar' : true,
              'link': true
          });
        }

      });​

      /* Sort the json in desc order based on appnum */

      jsonObj =  jsonObj.sort(function(a, b) {
        const x = a.appNum > b.appNum ? -1 : 1;
        return x;
    });

    /* this is the final json obj after processing */

    this.progressdata = {
        'data': jsonObj
      };
    }
    capitalizeFirstLetter(string): any {
      return string.charAt(0).toUpperCase() + string.slice(1);
    }
    /**
   * This function navigates the page mentioned  with a ruleID
   */
    navigatePage(event) {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      try {
        const currentApp = event;
        if (this.routeTo !== undefined) {
          const eachParams = {'tags.Application.keyword': this.capitalizeFirstLetter(currentApp), 'expiringIn': '45'};
          const newParams = this.utils.makeFilterObj(eachParams);
          this.router.navigate(['../', this.routeTo], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
        }
      } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.logger.log('error', error);
      }
    }
    /* navigatePage function ends here */

    ngOnDestroy() {
    try {
      clearInterval(this.autorefreshInterval);
      this.dataSubscriber.unsubscribe();
      this.subscriptionToAssetGroup.unsubscribe();
    } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.getErrorValues();
    }
  }
}
