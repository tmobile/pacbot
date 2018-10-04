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
import { Subscription } from 'rxjs/Subscription';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { environment } from './../../../../../environments/environment';
import { Router, ActivatedRoute} from '@angular/router';
import { LoggerService } from '../../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../../shared/services/error-handling.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { DomainTypeObservableService } from '../../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-policy-knowledgebase',
  templateUrl: './policy-knowledgebase.component.html',
  styleUrls: ['./policy-knowledgebase.component.css'],
  providers: [CommonResponseService, LoggerService, ErrorHandlingService]
})
export class PolicyKnowledgebaseComponent implements OnInit, OnDestroy {

  pageTitle = 'Policy Knowledgebase';
  breadcrumbArray: any= ['Compliance'];
  breadcrumbLinks: any= ['compliance-dashboard'];
  breadcrumbPresent: any;
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
  selName: any = [];
  selectedTab = 0;
  selectedFilter = 0;
  selectedFilterName = '';
  searchQuery = '';
  loaded = false;
  currentLength = 0;
  datacoming = false;
  seekdata = false;
  errorMessage: any;
  urlToRedirect: any = '';
  private pageLevel = 0;
  public backButtonRequired;

  constructor(private assetGroupObservableService: AssetGroupObservableService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private commonResponseService: CommonResponseService,
    private logger: LoggerService,
    private errorHandling: ErrorHandlingService,
    private workflowService: WorkflowService,
    private domainObservableService: DomainTypeObservableService) {
      this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroupName => {
        this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(this.pageLevel);
        this.selectedAssetGroup = assetGroupName;
      });
      this.domainSubscription = this.domainObservableService.getDomainType().subscribe(domain => {
        this.selectedDomain = domain;
        this.updateComponent();
    });
  }

  ngOnInit() {
      this.breadcrumbPresent = 'Policy Knowledgebase';
  }

  updateComponent() {
      this.getData();
  }

  processData(data) {
    try {
      const getData = data;
        const typeObj = {
          'All': 'typeVal'
        };
        for (let i = 0 ; i < getData.length; i++) {
          typeObj[getData[i].ruleCategory] = 'typeVal';
        }
        typeObj[`critical`] = 'selVal';
        typeObj[`high`] = 'selVal';
        typeObj[`medium`] = 'selVal';
        typeObj[`low`] = 'selVal';
        for (let i = 0 ; i < getData.length; i++) {
          typeObj[getData[i].severity] = 'selVal';
        }
        delete typeObj[''];
        let typeArr = [];
        const selArr = [];
        typeArr = Object.keys(typeObj);
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
            this.currentLength = this.knowledgebaseData.length;
            this.dataLoaded = true;
            const x = this;
              setTimeout(function () {
              x.loaded = true;
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

    getLength() {
      setTimeout(() => {
        const data = document.getElementsByClassName('mr-pkb-cards');
        this.currentLength = data.length;
      }, 10);
  }

  /*
    * this function is used to fetch the rule id and to navigate to the next page
    */

  gotoNextPage(ruleId) {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      this.router.navigate(['../policy-knowledgebase-details', ruleId],
        {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
    } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.logger.log('error', error);
    }
  }
  navigateBack() {
    this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
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
