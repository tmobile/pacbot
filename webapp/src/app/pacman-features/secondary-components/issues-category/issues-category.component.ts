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
import { ActivatedRoute, UrlSegment, Router } from '@angular/router';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { WorkflowService } from '../../../core/services/workflow.service';

@Component({
  selector: 'app-issues-category',
  templateUrl: './issues-category.component.html',
  styleUrls: ['./issues-category.component.css']
})

export class IssuesCategoryComponent implements OnInit, OnDestroy {

  subscriptionToAssetGroup: Subscription;
  selectedAssetGroup: string;
  errorMessages: string;

  @Input() categoriesHeader: string;
  @Input() categoriesTitle: string;
  @Input() categoriesSubTitle: string;
  @Input() categoriesTotal: string;
  @Input() categoriesPostTitle: string;
  @Input() categoriesVal: number;
  @Input() categoriesLoaded: string;
  @Input() errorOccurred = false;
  @Input() hideLoader = false;

  @Input() routeTo;
  private assetGroupSubscription: Subscription;
  @Input() pageLevel: number;
  private urlToRedirect;

  constructor(private router: Router,
              private assetGroupObservableService: AssetGroupObservableService, private activatedRoute: ActivatedRoute, private workflowService: WorkflowService) {

    this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(
                assetGroupName => {
                    this.selectedAssetGroup = assetGroupName;
                  });
  }

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
  }

  navigateTo() {
    if (this.routeTo !== undefined) {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      this.router.navigate(['../', this.routeTo], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
    }
  }

  ngOnDestroy() {
    try {
      if (this.assetGroupSubscription) {
        this.assetGroupSubscription.unsubscribe();
      }
    } catch (e) {
    }
  }

}
