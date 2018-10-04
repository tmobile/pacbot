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

import { Component, OnInit, OnDestroy, Input} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { ActivatedRoute, UrlSegment, Router } from '@angular/router';
import { UtilsService } from '../../../shared/services/utils.service';
import { WorkflowService } from '../../../core/services/workflow.service';


@Component({
  selector: 'app-certificate-summary',
  templateUrl: './certificate-summary.component.html',
  styleUrls: ['./certificate-summary.component.css'],
  providers: [LoggerService, ErrorHandlingService, AutorefreshService, CommonResponseService]
})
export class CertificateSummaryComponent implements OnInit, OnDestroy {
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

  private autorefreshInterval;

  dataValue: any;
  colors: any = ['#00b946', '#1c5066', '#e60127', 'rgb(248,146,87)'];
  complianceColors = {
    'non-compliant' : '#D40325',
    'compliant'     : '#00B946',
    'intermediate'  : '#F75C03'
  };
  percent: any = [true, false, false, false];
  routeTo = 'certificates';
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

        const certificateSummaryUrl = environment.certificateSummary.url;
        const certificateSummaryMethod = environment.certificateSummary.method;

        try {
            this.dataSubscriber = this.commonResponseService.getData( certificateSummaryUrl, certificateSummaryMethod, {}, queryParams).subscribe(
            response => {
                try {
                    this.showLoader = false;
                    this.seekdata = false;
                    this.dataComing = true;
                    this.apiData = response;
                    this.progressDataProcess(this.apiData);
                }catch (e) {
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

    getErrorValues(): void {
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
    }
    progressDataProcess(data) {

        const response = [];
        let compliant;
        let total;
        let expiry30Days;
        let expiry45Days;

        if (data.distribution.compliantPercent !== undefined) {
            compliant = {
                'value': data.distribution.compliantPercent,
                'text': 'Compliant',
                'link': false,
                'styling': {
                     'cursor': 'text'
                }
            };

            // Set the compliance percentage color with respect to the value
            this.colors[0] = data.distribution.compliantPercent === 100 ? this.complianceColors.compliant : (data.distribution.compliantPercent >= 50 ? this.complianceColors.intermediate : this.complianceColors['non-compliant']);

            response.push(compliant);
        }

        if (data.distribution.totalCertificates !== undefined) {
            total = {
                'value': data.distribution.totalCertificates,
                'text': 'Total Certificates',
                'link': true,
                'styling': {
                     'cursor': 'pointer'
                }
            };

            response.push(total);
        }

        if ( data.distribution.expiry30Days !== undefined) {
            expiry30Days = {
                'value': data.distribution.expiry30Days,
                'text': 'Expiring in 30 Days',
                'link': true,
                'styling': {
                       'cursor': 'pointer'
                }
            };

            response.push(expiry30Days);
        }

        if (data.distribution.expiry45Days !== undefined) {
            expiry45Days = {
                'value': data.distribution.expiry45Days,
                'text': 'Expiring in 45 days',
                'link': true,
                'styling': {
                      'cursor': 'pointer'
                }
            };

            response.push(expiry45Days);
        }

        this.dataValue = {
            'response': response
        };

    }

    capitalizeFirstLetter(string): any {
        return string.charAt(0).toUpperCase() + string.slice(1);
      }

    /**
    * This function navigates the page mentioned  with a queryparams
    */
      navigatePage(event) {

        try {
            this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);

            const localObjKeys = Object.keys(event);

            if (this.routeTo !== undefined) {
                if ( event[localObjKeys[1]].toLowerCase() === 'total certificates' ) {
                    const eachParams = {};
                    const newParams = this.utils.makeFilterObj(eachParams);
                        this.router.navigate(['../', this.routeTo], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
                } else if ( event[localObjKeys[1]].toLowerCase() === 'expiring in 30 days' ) {
                    const eachParams = {'expiringIn': '30'};
                const newParams = this.utils.makeFilterObj(eachParams);
                        this.router.navigate(['../', this.routeTo], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
                } else if ( event[localObjKeys[1]].toLowerCase() === 'expiring in 45 days' ) {
                    const eachParams = {'expiringIn': '45'};
                    const newParams = this.utils.makeFilterObj(eachParams);
                        this.router.navigate(['../', this.routeTo], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
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
        this.dataSubscriber.unsubscribe();
        clearInterval(this.autorefreshInterval);
    } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.getErrorValues();
    }
  }
}
