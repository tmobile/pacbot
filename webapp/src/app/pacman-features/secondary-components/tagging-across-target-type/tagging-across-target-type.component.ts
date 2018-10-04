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

import { Component, OnInit, ViewEncapsulation, OnDestroy, Input } from '@angular/core';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { TaggingComplianceService } from '../../services/tagging-compliance.service';
import { Subscription } from 'rxjs/Subscription';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UtilsService } from '../../../shared/services/utils.service';
import { WorkflowService } from '../../../core/services/workflow.service';

@Component({
  selector: 'app-tagging-across-target-type',
  templateUrl: './tagging-across-target-type.component.html',
  styleUrls: ['./tagging-across-target-type.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService,
    TaggingComplianceService
  ],
  encapsulation: ViewEncapsulation.None
})

export class TaggingAcrossTargetTypeComponent implements OnInit , OnDestroy {
  subscriptionToAssetGroup: Subscription;
  subscriptionToTaggingSummary: Subscription;

  selectedAssetGroup: string;
  errorMessage = '';
  taggingSummary: any = [];

  error = false;
  loading = false;
  @Input() pageLevel: number;
  private urlToRedirect: string;

  constructor(
    private taggingComplianceService: TaggingComplianceService,
    private assetGroupObservableService: AssetGroupObservableService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private utils: UtilsService,
    private workflowService: WorkflowService
  ) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        this.selectedAssetGroup = assetGroupName;
        this.updateComponent();
      });
  }

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.getData();
  }

  getData() {
    try {

      if (this.subscriptionToTaggingSummary) {
        this.subscriptionToTaggingSummary.unsubscribe();
      }
      this.setloading();
      const payload = {
        ag: this.selectedAssetGroup,
        filter: {}
      };
      const url = environment.taggingSummaryByTargetType.url;
      const method = environment.taggingSummaryByTargetType.method;

      this.subscriptionToTaggingSummary = this.taggingComplianceService
        .getTaggingSummaryByTargetType(payload, url, method)
        .subscribe(
          response => {
            try {
              if (response.length === 0) {
                this.errorMessage = 'taggingTargetMessage';
                this.setError();
              } else {
                this.taggingSummary = response;
                this.setDataLoaded();
              }
            } catch (error) {
              this.setError();
              this.errorMessage = this.errorHandling.handleJavascriptError(
                error
              );
            }
          },
          error => {
            this.setError();
            if (error === 'apiResponseError') {
              this.errorMessage = error;
            }
          }
        );
    } catch (error) {
      this.setError();
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
    }
  }

  setloading() {
    this.loading = true;
    this.error = false;
  }

  setError() {
    this.loading = false;
    this.error = true;
  }

  setDataLoaded() {
    this.loading = false;
    this.error = false;
  }

  updateComponent() {
    this.getData();
  }

  /**
   * This function navigates the page mentioned  with a queryparams
   */

  navigatePage(event) {
    try {
      const localObjKeys = Object.keys(event);
      const currentApp = this.uppercasefirst(event[localObjKeys[0]]);
      const intanceType = event[localObjKeys[1]].toLowerCase();
      const apiTarget = {'TypeAsset' : 'taggable'};

      if ( (intanceType !== undefined) && (currentApp !== undefined) ) {
        this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
        if (intanceType === 'tagged') {
          const eachParams = {'tagged': true , resourceType: currentApp.toLowerCase()};
          let newParams = this.utils.makeFilterObj(eachParams);
          newParams = Object.assign(newParams, apiTarget);
          this.router.navigate(['../../', 'assets' , 'asset-list'] , {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
        } else if (intanceType === 'untagged') {
          const eachParams = {'tagged': false, resourceType: currentApp.toLowerCase()};
          let newParams = this.utils.makeFilterObj(eachParams);
          newParams = Object.assign(newParams, apiTarget);
          this.router.navigate(['../../', 'assets' , 'asset-list'] , {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
        }
      }
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }
  /* navigatePage function ends here */
  uppercasefirst(value) {
    if (value === null) {
      return 'Not assigned';
    }
    return value.charAt(0).toUpperCase() + value.slice(1);
  }

  ngOnDestroy() {
    this.subscriptionToAssetGroup.unsubscribe();
     if (this.subscriptionToTaggingSummary) {
        this.subscriptionToTaggingSummary.unsubscribe();
      }
  }
}
