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

import { Component, OnInit, OnDestroy } from '@angular/core';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { Subscription } from 'rxjs/Subscription';
import { environment } from './../../../../../environments/environment';
import { Router} from '@angular/router';
import { LoggerService } from '../../../../shared/services/logger.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { UtilsService } from '../../../../shared/services/utils.service';
import * as _ from 'lodash';
const date = new Date();

@Component({
  selector: 'app-patching-projections',
  templateUrl: './patching-projections.component.html',
  styleUrls: ['./patching-projections.component.css'],
  providers: [CommonResponseService, LoggerService, UtilsService]
})
export class PatchingProjectionsComponent implements OnInit, OnDestroy {

  pageTitle = 'Patching Projections';
  breadcrumbArray: any= ['Compliance'];
  breadcrumbLinks: any= ['compliance-dashboard'];
  breadcrumbPresent: any;
  selectedAssetGroup: string;
  subscriptionToAssetGroup: Subscription;
  targetSubscription: Subscription;
  errorMessage: any;
  errorValue = 0;
  targetTiles: any = [];
  tiles: any = [];
  targetDropdown: any = [];
  targetTypeSelected: string;
  dropdownDisable = true;
  urlToRedirect: any = '';
  years: any= ['2019', '2018', '2017'];
  quarter: any= ['1', '2', '3', '4'];
  yearSelected = '2018';
  quarterSelected = '1';
  weekProjections: any = [];
  totalAseets = 0;
  index = 0;
  totalweeksProjection = 0;
  dropdownValue: any;
  private pageLevel = 0;
  updateState = 0;
  startUpdate= false;
  enableSaveButton= false;
  public backButtonRequired;
  numberOfWeeks: number;
  weeksArray: any= [];
  yearsArray: any= [];
  yearsDropdown: any = [];
  quarterArray: any= [];
  quarterDropdown: any= [];
  storeProjectionData: any = [];

  constructor(private assetGroupObservableService: AssetGroupObservableService,
    private router: Router,
    private commonResponseService: CommonResponseService,
    private logger: LoggerService,
    private workflowService: WorkflowService,
    private utilsService: UtilsService) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
        this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(this.pageLevel);
        this.selectedAssetGroup = assetGroupName;
        this.clickClearDropdown();
        this.resetPage();
        this.selectYear(this.years);
        this.selectQuarter(this.quarter);
        this.numberOfWeeks = utilsService.getNumberOfWeeks(this.yearSelected, this.quarterSelected);
        this.weekProjections = this.createObjectToIterateUpon(this.numberOfWeeks);
        this.storeProjectionData = JSON.parse(JSON.stringify(this.createObjectToIterateUpon(this.numberOfWeeks)));
        this.getTargetTypes();
          });
        }

  ngOnInit() {
    this.breadcrumbPresent = 'Patching Projections';
  }

  createObjectToIterateUpon(numberOfWeeks) {
    this.weeksArray = [];
    for (let i = 1; i <= numberOfWeeks; i++) {
      this.weeksArray.push({
      'week': i,
      'projection': 0
      });
    }
    return this.weeksArray;
  }

  selectYear(years) {
    this.yearsArray = [];
    this.index = 0;
    this.years.forEach(element => {
      this.dropdownValue = {
        id: this.index++,
        text: element
      };
      this.yearsArray.push(this.dropdownValue);
    });
    this.yearsDropdown = _.map(this.yearsArray, 'text');
    this.yearSelected = date.getFullYear().toString();
  }

  selectQuarter(quarter) {
    this.quarterArray = [];
    this.index = 0;
    this.quarter.forEach(element => {
      this.dropdownValue = {
        id: this.index++,
        text: element
      };
      this.quarterArray.push(this.dropdownValue);
    });
    this.quarterDropdown = _.map(this.quarterArray, 'text');
    this.quarterSelected = Math.ceil((date.getMonth() + 1) / 3).toString();
  }

  changeTargetFilterTags(val) {
    if (this.targetTypeSelected !== val.text) {
      this.targetTypeSelected = val.text;
      this.getProjections();
    }
  }

  changeYearFilterTags(val) {
    if (this.yearSelected !== val.text) {
      this.yearSelected = val.text;
      this.getProjections();
    }
  }

  changeQuarterFilterTags(val) {
    if (this.quarterSelected !== val.text) {
      this.quarterSelected = val.text;
      this.getProjections();
    }
  }


  getProjections() {
    if (this.targetSubscription) {
    this.targetSubscription.unsubscribe();
    }
    this.enableSaveButton = false;
    this.dropdownDisable = false;
    this.totalAseets = 0;
    this.totalweeksProjection = 0;
    this.numberOfWeeks = this.utilsService.getNumberOfWeeks(this.yearSelected, this.quarterSelected);
    this.weekProjections = this.createObjectToIterateUpon(this.numberOfWeeks);
    this.storeProjectionData = JSON.parse(JSON.stringify(this.createObjectToIterateUpon(this.numberOfWeeks)));
    const payload = {};
    const queryParam = {
    'targettype': this.targetTypeSelected,
    'year': this.yearSelected,
    'quarter': this.quarterSelected
    };
    this.errorValue = 0;
    const url = environment.patchingProjections.url;
    const method = environment.patchingProjections.method;
    this.targetSubscription = this.commonResponseService.getData( url, method, payload, queryParam).subscribe(
    response => {
    try {
      this.errorValue = 1;
      if (response.length === 0 ) {
        this.errorValue = -2;
      }
      this.totalAseets = response.totalAssets;
      if (response.projectionByWeek.length === 0) {
        this.errorValue = -3;
      } else {
        this.weekProjections = response.projectionByWeek;
        this.weekProjections = this.checkifProjectionEqualsWeeks(this.weekProjections);
        this.totalweeksProjection = 0;
        this.weekProjections.forEach(element => {
        this.totalweeksProjection = this.totalweeksProjection + element.projection;
        });
        this.storeProjectionData = JSON.parse(JSON.stringify(response.projectionByWeek));
      }
    } catch (e) {
      this.errorValue = -1;
      this.logger.log('error', e);
    }
    },
    error => {
    this.errorValue = -1;
    });
  }

  getTargetTypes() {
    if (this.targetSubscription) {
      this.targetSubscription.unsubscribe();
    }
    const payload = {};
    const queryParam = {
      'ag': this.selectedAssetGroup
    };
    this.errorValue = 0;
    this.targetTiles = [];
    this.tiles = [];
    const url = environment.complianceTargetType.url;
    const method = environment.complianceTargetType.method;
    this.targetSubscription = this.commonResponseService.getData(url, method, payload, queryParam).subscribe(
      response => {
      try {
        this.errorValue = 1;
        if (response.resourceType.length === 0) {
          this.errorValue = -2;
        }
        this.index = 0;
        this.targetTiles = response.resourceType;
        this.targetTiles.forEach(element => {
          this.dropdownValue = {
            id: this.index++,
            text: element
          };
          this.tiles.push(this.dropdownValue);
        });
        this.targetDropdown = _.map(this.tiles, 'text');
      } catch (e) {
        this.errorValue = -4;
        this.logger.log('error', e);
      }
    },
    error => {
      this.errorValue = -4;
    });
    }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
      } catch (error) {
          this.logger.log('error', error);
      }
  }

  resetToFetchData() {
    this.weekProjections = JSON.parse(JSON.stringify(this.storeProjectionData));
  }

  saveData() {
    this.storeProjectionData = JSON.parse(JSON.stringify(this.weekProjections));
    this.startUpdate = true;
    this.updateState = 0;
    const queryParam = {};
    const payload = {
      'projectionByWeek': this.weekProjections,
      'quarter': this.quarterSelected,
      'resourceType': this.targetTypeSelected,
      'year': this.yearSelected
    };
    const url = environment.updateProjections.url;
    const method = environment.updateProjections.method;
    this.targetSubscription = this.commonResponseService.getData( url, method, payload, queryParam).subscribe(
      response => {
      try {
        this.updateState = 1;
      } catch (e) {
        this.updateState = -1;
        this.logger.log('error', e);
      }
    },
    error => {
      this.updateState = -1;
    });

  }

  closeUpdateloader() {
    this.startUpdate = false;
    if (this.updateState === 1 ) {
      this.enableSaveButton = false;
      }
  }

  updateTotalAssets(val, index) {
    this.enableSaveButton = true;
      this.totalweeksProjection = 0;
      if (this.weekProjections.length > 0) {
      this.weekProjections.forEach(element => {
        this.totalweeksProjection = this.totalweeksProjection + element.projection;
      });
      }
  }

  resetPage() {
    this.enableSaveButton = false;
    this.startUpdate = false;
    this.errorValue = 0;
    this.targetTiles = [];
    this.tiles = [];
    this.targetTypeSelected = '';
    this.dropdownDisable = true;
    this.yearSelected = '2018';
    this.quarterSelected = '1';
    this.weekProjections = [];
    this.storeProjectionData = [];
    this.totalAseets = 0;
    this.totalweeksProjection = 0;
    this.getTargetTypes();
  }

    clearPageLevel() {
      this.workflowService.clearAllLevels();
    }

  // clears target type dropdown
  clickClearDropdown() {
    setTimeout(function() {
      const	element = document.getElementById('clear-value');
      const clear = element.getElementsByClassName(
      'btn btn-xs btn-link pull-right'
      );
      for (let len = 0; len < clear.length; len++) {
        const htmlElement: HTMLElement = clear[len] as HTMLElement;
        htmlElement.click();
      }
    }, 10);
    }

  checkifProjectionEqualsWeeks(receivedWeekProjections) {
    const numOfreceivedProjection = receivedWeekProjections.length;
    if ((this.numberOfWeeks - numOfreceivedProjection) > 0 ) {
      for (let i = numOfreceivedProjection; i < this.numberOfWeeks; i++) {
        receivedWeekProjections.push({
          'week': i + 1,
          'projection': 0
          });
      }
    }
    return receivedWeekProjections;
  }

  ngOnDestroy() {
    try {
      if (this.subscriptionToAssetGroup) {
        this.subscriptionToAssetGroup.unsubscribe();
      }
      if (this.targetSubscription) {
        this.targetSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
      }
  }
}


