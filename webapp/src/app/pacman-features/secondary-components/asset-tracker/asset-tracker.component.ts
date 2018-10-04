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
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UtilsService } from '../../../shared/services/utils.service';
import { WorkflowService } from '../../../core/services/workflow.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';
import { environment } from '../../../../environments/environment';


@Component({
  selector: 'app-asset-tracker',
  templateUrl: './asset-tracker.component.html',
  styleUrls: ['./asset-tracker.component.css'],
  providers: [CommonResponseService, AutorefreshService]
})
export class AssetTrackerComponent implements OnInit, OnDestroy {

  selectedAssetGroup: string;
  public valueType1: string;
  public valueType2: string;
  public totaltagValue: number;
  public untagValue: number;
  public tagValue: number;
  public widthValue: number;
  public transit = true;
  valueType = {
    'value1': 'tagged',
    'value2': 'untagged'
  };
  public borderSet = true;
  public lessWidthLeft = false;
  public lessWidthRight = false;
  public ShowLabel = false;
  public leftLableWidth: any;
  public rightLableWidth: any;
  public leftWidthValue: any;
  public rightWidthValue: any;
  public apiData: any;
  public barHeight = true;

  private dataSubscription: Subscription;
  private subscriptionToAssetGroup: Subscription;
  subscriptionDomain: Subscription;
  selectedDomain: any;
  public errorMessage: any;
  private autorefreshInterval;

  durationParams: any;
  autoRefresh: boolean;
  @Input() pageLevel: number;
  private urlToRedirect;
  constructor(
    private commonResponseService: CommonResponseService,
    private assetGroupObservableService: AssetGroupObservableService,
    private autorefreshService: AutorefreshService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private utils: UtilsService,
    private workflowService: WorkflowService,
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
    this.urlToRedirect = this.router.routerState.snapshot.url;


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

      this.barHeight = false;
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

    const url = environment.InventoryTraker.url;
    const method = environment.InventoryTraker.method;

    this.dataSubscription = this.commonResponseService.getData(url, method, {}, queryParams).subscribe(
    response => {
      try {
       this.apiData = response[0];
       this.totaltagValue = this.apiData.assets;
       this.untagValue = this.apiData.untagged;
       this.tagValue = (this.totaltagValue) - (this.untagValue);
       if (this.totaltagValue === undefined || this.totaltagValue == null || this.untagValue === undefined || this.untagValue == null || this.untagValue <= 0 || this.tagValue <= 0) {
        this.barHeight = false;
       } else {
        this.getCalculatedValues();
        this.barHeight = true;
       }

      } catch (error) {
          this.barHeight = false;
      }

   },
   error => {
     this.barHeight = false;
   });

  }

  getCalculatedValues(): void {

    const x = this;
    // this.widthValue = 0;
    this.widthValue = Math.round((this.tagValue / this.totaltagValue) * 100);
    if (this.widthValue > 99) {
      this.widthValue = 99;
    } else if (this.widthValue < 1) {
      this.widthValue = 1;
    }
    const RunSettimeout = setTimeout(function(){
      // -------- get the width of total progress bar,left side bar,right side bar,both the labels
      if ((document.getElementsByClassName('total_issues_bar')[0] != null) && (document.getElementsByClassName('loading_bar_right')[0]) != null && (document.getElementsByClassName('left-count-value')[0] != null) && (document.getElementsByClassName('right-count-value')[0] != null)) {
        x.leftWidthValue = document.getElementsByClassName('total_issues_bar')[0].getBoundingClientRect().width;
        x.rightWidthValue = document.getElementsByClassName('loading_bar_right')[0].getBoundingClientRect().width;
        x.leftLableWidth = document.getElementsByClassName('left-count-value')[0].getBoundingClientRect().width;
        x.rightLableWidth = document.getElementsByClassName('right-count-value')[0].getBoundingClientRect().width;
        if ((x.leftLableWidth + 50) > x.leftWidthValue) {
          x.lessWidthLeft = true;
          x.lessWidthRight = false;
        } else if ((x.rightLableWidth + 50) > x.rightWidthValue) {
          x.lessWidthLeft = false;
          x.lessWidthRight = true;
        }
        x.ShowLabel = true;
        x.borderSet = true;
      }
      clearTimeout(RunSettimeout);
    }, 1000);
  }
  capitalizeFirstLetter(string): any {
    return string.charAt(0).toUpperCase() + string.slice(1);
    }

  /**
   * This function navigates the page mentioned  with a ruleID
   */
    navigatePage(event) {

    try {
            this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
            const clickText = event;
            const apiTarget = {'TypeAsset' : 'taggable'};
            if ( clickText.toLowerCase() === 'tagged' ) {
              const eachParams = {'tagged': true};
              let newParams = this.utils.makeFilterObj(eachParams);
              newParams = Object.assign(newParams , apiTarget);
              this.router.navigate(['../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
            } else if (clickText.toLowerCase() === 'untagged' ) {
              const eachParams = {'tagged': false};
              let newParams = this.utils.makeFilterObj(eachParams);
              newParams = Object.assign(newParams , apiTarget);
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
      this.dataSubscription.unsubscribe();
      this.subscriptionDomain.unsubscribe();
      clearInterval(this.autorefreshInterval);
    } catch (error) {
    }
  }
}
