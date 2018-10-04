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
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { environment } from './../../../../environments/environment';
import { WorkflowService } from '../../../core/services/workflow.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UtilsService } from '../../../shared/services/utils.service';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-asset-certificate',
  templateUrl: './asset-certificate.component.html',
  styleUrls: ['./asset-certificate.component.css'],
  providers: [CommonResponseService, AutorefreshService]
})
export class AssetCertificateComponent implements OnInit, OnDestroy {
  selectedAssetGroup: string;
  public apiData: any;
  public applicationValue: any;
  public errorMessage: any;
  assetDetails = {
    imgLocation: './../../assets/icons/certificates_type2.svg',
    assetValue: 13,
    assetName: 'Certificates'
  };
  public dataComing = true;
  public showLoader = true;
  public seekdata = false;
  private subscriptionToAssetGroup: Subscription;
  private dataSubscription: Subscription;
  subscriptionDomain: Subscription;
  selectedDomain: any;

  private autorefreshInterval;
  durationParams: any;
  autoRefresh: boolean;
  urlToRedirect: any = '';
  @Input() pageLevel: number;
  routeTo = 'certificates';

  constructor(
    private commonResponseService: CommonResponseService,
    private assetGroupObservableService: AssetGroupObservableService,
    private autorefreshService: AutorefreshService,
    private workflowService: WorkflowService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private utils: UtilsService,
    private domainObservableService: DomainTypeObservableService
  ) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        this.selectedAssetGroup = assetGroupName;
      });
      this.subscriptionDomain = this.domainObservableService.getDomainType().subscribe(domain => {
               this.selectedDomain = domain;
               this.updateComponent();
         });
    this.durationParams = this.autorefreshService.getDuration();
    this.durationParams = parseInt(this.durationParams, 10);
    this.autoRefresh = this.autorefreshService.autoRefresh;
  }

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
    const afterLoad = this;
    if (this.autoRefresh !== undefined) {
      if (this.autoRefresh === true || this.autoRefresh.toString() === 'true') {
        this.autorefreshInterval = setInterval(function() {
          afterLoad.getProgressData();
        }, this.durationParams);
      }
    }
  }
  updateComponent() {
    /* All functions variables which are required to be set for component to be reloaded should go here */

    this.showLoader = true;
    this.dataComing = false;
    this.seekdata = false;
    this.getData();
  }
  getData() {
    /* All functions to get data should go here */
    this.getProgressData();
  }
  getProgressData() {
    if (this.dataSubscription) {
      this.dataSubscription.unsubscribe();
    }
    const queryParams = {
      'ag': this.selectedAssetGroup,
      'domain': this.selectedDomain
    };
    const assetGroupApplicationUrl = environment.AssetGroupCirtificate.url;
    const assetGroupApplicationMethod = environment.AssetGroupCirtificate.method;

    this.dataSubscription = this.commonResponseService
      .getData(

        assetGroupApplicationUrl,
        assetGroupApplicationMethod,
        {},
        queryParams

      )
      .subscribe(
        response => {

          this.apiData = response.output;

          try {
            if (
              this.apiData === undefined ||
              this.apiData === '' ||
              this.apiData == null
            ) {
              this.errorMessage = 'noDataAvailable';
              this.getErrorValues();
            } else {
              this.applicationValue = this.apiData.certificates;
              if (this.applicationValue < 0) {
                this.errorMessage = 'noDataAvailable';
                this.getErrorValues();
              } else {
                this.showLoader = false;
                this.seekdata = false;
                this.dataComing = true;
              }
            }
          } catch (error) {
            this.errorMessage = 'noDataAvailable';
            this.getErrorValues();
          }
        },
        error => {
          this.errorMessage = 'apiResponseError';
          this.getErrorValues();
        }
      );
  }

  // assign error values...

  getErrorValues(): void {
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
  }


  /**
   * This function navigates the page mentioned  with a ruleID
   */
  navigatePage(event) {
    const clickText = event;
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      if (this.routeTo !== undefined) {
        const eachParams = {};
        const newParams = this.utils.makeFilterObj(eachParams);
        if (clickText.toLowerCase() === 'total certificates') {
          this.router.navigate(['../../', 'compliance', this.routeTo], {
            relativeTo: this.activatedRoute,
            queryParams: newParams,
            queryParamsHandling: 'merge'
          });
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
      this.dataSubscription.unsubscribe();
      this.subscriptionDomain.unsubscribe();
      clearInterval(this.autorefreshInterval);
    } catch (error) {}
  }
}
