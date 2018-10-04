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

import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter,
  OnDestroy
} from '@angular/core';
import { ActivatedRoute, UrlSegment, Router } from '@angular/router';
import { AssetGroupObservableService } from '../../core/services/asset-group-observable.service';
import { Subscription } from 'rxjs/Subscription';
import { WorkflowService } from '../../core/services/workflow.service';

@Component({
  selector: 'app-title-burger-head',
  templateUrl: './title-burger-head.component.html',
  styleUrls: ['./title-burger-head.component.css']
})
export class TitleBurgerHeadComponent implements OnInit, OnDestroy {
  constructor(
    private router: Router,
    private assetGroupObservableService: AssetGroupObservableService,
    private activatedRoute: ActivatedRoute,
    private workflowService: WorkflowService
  ) {
    this.assetGroupSubscription = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        this.selectedAssetGroup = assetGroupName;
        this.searchQuery = '';
        this.searchCalled();
      });
  }

  searchQuery = '';
  private assetGroupSubscription: Subscription;
  selectedAssetGroup;

  @Input() subHeadTitle;
  @Input() routeTo;
  @Input() routeParams: {};
  @Input() arrow;
  @Input() contextualMenuAbsent;
  @Input() imagePresent;
  @Input() dropDowndata;
  @Input() showSearch;
  @Input() selectedDD;
  @Input() popRows;
  @Input() complianceDropdowns;
  @Input() dropDownpresent;
  @Output() dataSent = new EventEmitter();
  @Output() searchTxt = new EventEmitter<string>();
  @Output() rowText = new EventEmitter<string>();
  @Output() enterPressed = new EventEmitter();
  showdata = false;
  @Input() tableIdAppend;
  @Input() pageLevel;
  @Input() helpId;

  private urlToRedirect;

  loaded: boolean;

  ngOnInit() {
    this.loaded = true;
    if (this.dropDowndata !== undefined) {
      this.showdata = true;
    }
    this.urlToRedirect = this.router.routerState.snapshot.url;
  }

  selectionDD(val) {
    this.dataSent.emit(val);
  }

  searchCalled() {
    this.searchTxt.emit(this.searchQuery);
  }

  keyDownFunction(event) {
    if (event.keyCode === 13) {
      this.enterPressed.emit(this.searchQuery);
    }
  }

  navigateTo() {
    if (this.routeTo !== undefined) {
      this.workflowService.addRouterSnapshotToLevel(
        this.router.routerState.snapshot.root
      );
      this.router.navigate(['../', this.routeTo], {
        relativeTo: this.activatedRoute,
        queryParams: this.routeParams,
        queryParamsHandling: 'merge'
      });
    }
  }

  emitRowClicked(rowText) {
    this.rowText.emit(rowText);
  }

  ngOnDestroy() {
    if (this.assetGroupSubscription) {
      this.assetGroupSubscription.unsubscribe();
    }
  }

  callHelp() {
    const newParams = { widgetId: this.helpId };
    this.router.navigate(
      ['/pl', { outlets: { helpTextModal: ['help-text'] } }],
      { queryParams: newParams, queryParamsHandling: 'merge' }
    );
  }
}
