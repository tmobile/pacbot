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

import { Component, OnInit, ElementRef, ViewChild, OnDestroy } from '@angular/core';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { SelectComplianceDropdown } from './../../../services/select-compliance-dropdown.service';
import { Subscription } from 'rxjs/Subscription';
import { environment } from './../../../../../environments/environment';
import { Router } from '@angular/router';
import { IssueFilterService } from './../../../services/issue-filter.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import { WorkflowService } from '../../../../core/services/workflow.service';

@Component({
  selector: 'app-patching-compliance',
  templateUrl: './patching-compliance.component.html',
  styleUrls: ['./patching-compliance.component.css'],
  providers: [ IssueFilterService, LoggerService, ErrorHandlingService]
})

export class PatchingComplianceComponent implements OnInit, OnDestroy {

  @ViewChild('widget') widgetContainer: ElementRef;

  pageTitle = 'Cloud - Linux OS Patching';
  widgetWidth: number;
  widgetHeight: number;

  /*variables for breadcrumb data*/

  breadcrumbArray: any= ['Compliance'];
  breadcrumbLinks: any= ['compliance-dashboard'];
  breadcrumbPresent: any;

  complianceDropdowns: any = [];
  searchDropdownData: any = {};
  selectedDD = '';
  currentObj: any = {};
  filterArr: any = [];
  subscriptionToAssetGroup: Subscription;
  selectedAssetGroup: string;
  hiddenComponent = 'onpremPatchingGraph';
  // hiddenComponent = '';
  patchingSnapshotHidden = false;
  patchingSponsorHidden = false;
  selectedComplianceDropdown: any;
  errorMessage: any;
  private issueFilterSubscription: Subscription;
  public pageLevel = 0;
  public backButtonRequired;

  constructor(
    private assetGroupObservableService: AssetGroupObservableService,
    private selectComplianceDropdown: SelectComplianceDropdown,
    private router: Router,
    private issueFilterService: IssueFilterService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService, private workflowService: WorkflowService) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
          if (assetGroupName) {
            this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(this.pageLevel);
            this.hiddenComponent = 'onpremPatchingGraph';
            // this.hiddenComponent = '';
            this.patchingSnapshotHidden = false;
            this.patchingSponsorHidden = false;
          }
          this.selectedAssetGroup = assetGroupName;
          this.updateComponent();
      });
      this.selectComplianceDropdown.getCompliance().subscribe(
        complianceName => {
            this.selectedComplianceDropdown = complianceName;
      });
  }

  ngOnInit() {
    try {
      this.breadcrumbPresent = 'Cloud - Linux OS Patching';
      this.widgetWidth = parseInt(window.getComputedStyle(this.widgetContainer.nativeElement, null).getPropertyValue('width'), 10);
    }catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  updateComponent() {
    this.getFilters();
  }

  getFilters() {
    const queryParams = {
          'filterId' : 7
    };
    const issueFilterUrl = environment.issueFilter.url;
    const issueFilterMethod = environment.issueFilter.method;
    this.issueFilterSubscription = this.issueFilterService.getFilters(queryParams, issueFilterUrl, issueFilterMethod).subscribe(
       response => {
         try {
           const filterData = response[0];
           for (let i = 0 ; i < filterData.response.length ; i++ ) {
             this.complianceDropdowns.push(filterData.response[i].optionName);
             this.getFilterAttr(filterData.response);
           }
         }catch (e) {
         }
      },
      error => {
      });
  }

  getFilterAttr(filterObj) {}

  navigateBack() {
    this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
  }


    changedDropdown(val) {
      let isFirstDD = false;
      for (let i = 0 ; i < this.complianceDropdowns.length; i++) {
        if (val.text === this.complianceDropdowns[i]) {
          this.selectedDD = val.text;
          isFirstDD = true;
        }
      }
      if (!isFirstDD) {
        this.currentObj[this.selectedDD] = val.text;
        this.selectedDD = '';
        setTimeout(function(){
          const clear = document.getElementsByClassName('btn btn-xs btn-link pull-right');
          for (let len = 0 ; len < clear.length; len++) {
            const element: HTMLElement = clear[len] as HTMLElement;
            element.click();
          }
        }, 10);
      }
      this.filterArr = [];
      const keyArr = Object.keys(this.currentObj);
      for (let j = 0; j < keyArr.length; j++) {
        const thisObjInstance = {
          key: keyArr[j],
          value: this.currentObj[keyArr[j]]
        };
        this.filterArr.push(thisObjInstance);
      }
      this.selectComplianceDropdown.updateCompliance(this.currentObj);
    }

    removeFilter(obj) {
      delete this.currentObj[obj.array.key];
      this.filterArr.splice(obj.index, 1);
      this.selectComplianceDropdown.updateCompliance(this.currentObj);
    }

    clearAllFilters() {
      this.currentObj = {};
      this.selectComplianceDropdown.updateCompliance(this.currentObj);
      this.filterArr = [];
    }

    ngOnDestroy() {
      try {
        // pushes the current url to datastore
        if (this.issueFilterSubscription) {
          this.issueFilterSubscription.unsubscribe();
        }
        if (this.subscriptionToAssetGroup) {
          this.subscriptionToAssetGroup.unsubscribe();
        }
      } catch (error) {
        this.logger.log('error', '--- Error while unsubscribing ---');
      }
    }

}
