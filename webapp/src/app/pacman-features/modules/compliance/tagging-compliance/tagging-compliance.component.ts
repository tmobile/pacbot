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
import { Router} from '@angular/router';
import {LoggerService} from '../../../../shared/services/logger.service';
import {WorkflowService} from '../../../../core/services/workflow.service';

@Component({
  selector: 'app-tagging-compliance',
  templateUrl: './tagging-compliance.component.html',
  styleUrls: ['./tagging-compliance.component.css'],
  providers: [ LoggerService ]
})
export class TaggingComplianceComponent implements OnInit , OnDestroy {

  @ViewChild('widget') widgetContainer: ElementRef;
    pageTitle = 'Tagging';
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
    selectedComplianceDropdown: any;
    public pageLevel = 0;
    public backButtonRequired;
    constructor(
      private assetGroupObservableService: AssetGroupObservableService,
      private selectComplianceDropdown: SelectComplianceDropdown,
      private router: Router,
      private logger: LoggerService, private workflowService: WorkflowService) {
        this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
          assetGroupName => {
            this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(this.pageLevel);
            this.selectedAssetGroup = assetGroupName;
          });
          this.selectComplianceDropdown.getCompliance().subscribe(
            complianceName => {
              this.selectedComplianceDropdown = complianceName;
          });
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  ngOnInit() {
    this.breadcrumbPresent = 'Tagging Compliance';
  }
  /*
	 	* unsubscribing component
	 */
  ngOnDestroy() {
    try {
      // pushes the current url to datastore
        if (this.subscriptionToAssetGroup) {
          this.subscriptionToAssetGroup.unsubscribe();
        }
    } catch (error) {
        this.logger.log('info', '--- Error while unsubscribing ---');
    }
  }
}
