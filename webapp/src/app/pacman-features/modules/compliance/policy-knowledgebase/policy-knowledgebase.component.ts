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

import { Component, OnDestroy, ViewChild, ElementRef, AfterViewInit, Renderer2 } from '@angular/core';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { Subscription } from 'rxjs';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { environment } from './../../../../../environments/environment';
import { Router } from '@angular/router';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { DomainTypeObservableService } from '../../../../core/services/domain-type-observable.service';
import { RouterUtilityService } from '../../../../shared/services/router-utility.service';

@Component({
  selector: 'app-policy-knowledgebase',
  templateUrl: './policy-knowledgebase.component.html',
  styleUrls: ['./policy-knowledgebase.component.css'],
  providers: [CommonResponseService, LoggerService, ErrorHandlingService]
})
export class PolicyKnowledgebaseComponent implements AfterViewInit, OnDestroy {
  pageTitle = 'Policies';
  selectedAssetGroup: string;
  selectedDomain: string;
  subscriptionToAssetGroup: Subscription;
  domainSubscription: Subscription;
  complianceTableSubscription: Subscription;
  assetTiles: any = ['Security', 'Governance'];
  assetTabName: any;
  selectedTabName = 'All';
  dataLoaded = false;
  searchTxt = '';
  knowledgebaseData: any = [];
  tabName: any = [];
  count = [];
  num = 0;
  selName: any = [];
  selectedTab = 0;
  selectedFilter = 0;
  selectedFilterName = '';
  typeObj;
  searchQuery = '';
  loaded = false;
  datacoming = false;
  seekdata = false;
  errorMessage: any;
  urlToRedirect: any = '';
  public agAndDomain = {};
  currentPageLevel = 0;

  @ViewChild('pkInp') pkInp: ElementRef;

  constructor(private assetGroupObservableService: AssetGroupObservableService,
    private renderer: Renderer2,
    private router: Router,
    private commonResponseService: CommonResponseService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private workflowService: WorkflowService,
    private domainObservableService: DomainTypeObservableService,
    private routerUtilityService: RouterUtilityService) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroupName => {
      this.selectedAssetGroup = assetGroupName;
      this.agAndDomain['ag'] = this.selectedAssetGroup;
    });
    this.domainSubscription = this.domainObservableService.getDomainType().subscribe(domain => {
      this.selectedDomain = domain;
      this.agAndDomain['domain'] = this.selectedDomain;
      this.updateComponent();
    });
    this.currentPageLevel = this.routerUtilityService.getpageLevel(this.router.routerState.snapshot.root);
  }

  ngAfterViewInit() {

  }

  updateComponent() {
    this.loaded = false;
    this.datacoming = false;
    this.seekdata = false;
    this.knowledgebaseData = [];
    this.typeObj = undefined;
    this.getData();
  }

  processData(data) {
    try {
      const getData = data;
      this.typeObj = {
        'All': 0
      };
      for (let i = 0; i < getData.length; i++) {
        this.typeObj[getData[i].ruleCategory] = 0;
      }
      this.typeObj[`critical`] = 0;
      this.typeObj[`high`] = 0;
      this.typeObj[`medium`] = 0;
      this.typeObj[`low`] = 0;
      for (let i = 0; i < getData.length; i++) {
        this.typeObj[getData[i].severity] = 0;
      }
      this.typeObj[`Auto Fix`] = 0;
      delete this.typeObj[''];
      for (let i = 0; i < getData.length; i++) {
        this.typeObj['All']++;
        this.typeObj[getData[i].ruleCategory]++;
        this.typeObj[getData[i].severity]++;
        if (getData[i].autoFixEnabled === true) {
          this.typeObj['Auto Fix']++;
        }
      }
      let typeArr = [];
      typeArr = Object.keys(this.typeObj);
      this.tabName = typeArr;
      this.selectedTabName = this.tabName[this.selectedTab];
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  getData() {
    this.seekdata = false;
    this.dataLoaded = false;
    if (this.complianceTableSubscription) {
      this.complianceTableSubscription.unsubscribe();
    }
    const payload = {
      'ag': this.selectedAssetGroup,
      'searchtext': this.searchTxt,
      'filter': {
        'domain': this.selectedDomain
      },
      'from': 0,
      'size': 10
    };

    const queryParams = {};
    const complianceTableUrl = environment.complianceTable.url;
    const complianceTableMethod = environment.complianceTable.method;
    this.complianceTableSubscription = this.commonResponseService.getData(
      complianceTableUrl, complianceTableMethod, payload, queryParams).subscribe(
        response => {
          if (response.data.response.length !== 0) {
            this.datacoming = true;
            this.knowledgebaseData = response.data.response;
            this.dataLoaded = true;
            const x = this;
            setTimeout(function () {
              x.loaded = true;
              if (x.pkInp) {
                x.pkInp.nativeElement.focus();
              }
            }, 200);
            this.processData(this.knowledgebaseData);
          } else {
            this.datacoming = false;
            this.dataLoaded = true;
            this.seekdata = true;
            this.errorMessage = 'noDataAvailable';
          }
        },
        error => {
          this.datacoming = false;
          this.dataLoaded = true;
          this.seekdata = true;
          this.errorMessage = 'apiResponseError';
        });
  }

  /*
    * this function is used to fetch the rule id and to navigate to the next page
    */

  gotoNextPage(tileData){
   let autofixEnabled = false;
    if ( tileData.autoFixEnabled) {
      autofixEnabled = true;
    }
    const ruleId = tileData.ruleId;
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      this.router.navigate(
        ['pl', 'compliance', 'policy-knowledgebase-details', ruleId, autofixEnabled],
        { queryParams: this.agAndDomain,
          queryParamsHandling: 'merge' });
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  ngOnDestroy() {
    try {
      if (this.complianceTableSubscription) {
        this.complianceTableSubscription.unsubscribe();
      }
      if (this.subscriptionToAssetGroup) {
        this.subscriptionToAssetGroup.unsubscribe();
      }
      if (this.domainSubscription) {
        this.domainSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }
}
