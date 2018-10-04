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
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-asset-application',
  templateUrl: './asset-application.component.html',
  styleUrls: ['./asset-application.component.css'],
  providers: [CommonResponseService, AutorefreshService]

})

export class AssetApplicationComponent implements OnInit, OnDestroy {
  selectedAssetGroup: string;
  public apiData: any;
  public applicationValue: any;
  public errorMessage: any;
  assetDetails = {
    'imgLocation': './../../assets/icons/apps.svg',
    'assetName': 'Applications'
  };
  public dataComing = true;
  public showLoader = true;
  public seekdata = false;
  private subscriptionToAssetGroup: Subscription;
  private agApplicationTypeSubscription: Subscription;
  subscriptionDomain: Subscription;
  selectedDomain: any;

  private autorefreshInterval;

  durationParams: any;
  autoRefresh: boolean;
  @Input() pageLevel: number;

  constructor(
    private commonResponseService: CommonResponseService,
    private assetGroupObservableService: AssetGroupObservableService,
    private autorefreshService: AutorefreshService,
    private domainObservableService: DomainTypeObservableService
  ) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
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
    // this.updateComponent();

    const afterLoad = this;
    if (this.autoRefresh !== undefined) {
      if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {

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

    if (this.agApplicationTypeSubscription) {
      this.agApplicationTypeSubscription.unsubscribe();
    }
    const queryParams = {
      'ag': this.selectedAssetGroup,
      'domain': this.selectedDomain
    };
    const assetGroupApplicationUrl = environment.AssetGroupApplication.url;
    const assetGroupApplicationMethod = environment.AssetGroupApplication.method;

    this.agApplicationTypeSubscription = this.commonResponseService.getData( assetGroupApplicationUrl, assetGroupApplicationMethod, {}, queryParams).subscribe(
      response => {
        this.apiData = response;
        try {
        if (this.apiData === undefined || this.apiData === '' || this.apiData === null) {
            this.errorMessage = 'noDataAvailable';
            this.getErrorValues();
        } else {
          this.showLoader = false;
          this.seekdata = false;
          this.dataComing = true;
          this.applicationValue = this.apiData.applications.length;
        }
      } catch (error) {
          this.errorMessage = 'noDataAvailable';
          this.getErrorValues();
      }
    },
    error => {
      this.errorMessage = 'apiResponseError';
      this.getErrorValues();
    });

  }
  getErrorValues(): void {
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
  }

  ngOnDestroy() {
    try {
      this.subscriptionToAssetGroup.unsubscribe();
      this.agApplicationTypeSubscription.unsubscribe();
      this.subscriptionDomain.unsubscribe();
      clearInterval(this.autorefreshInterval);
    } catch (error) {
    }
  }

}
