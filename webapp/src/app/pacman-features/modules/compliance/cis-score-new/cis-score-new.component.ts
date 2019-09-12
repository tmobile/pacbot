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

import {Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewInit, OnChanges} from '@angular/core';
import { Router } from '@angular/router';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { Subscription } from 'rxjs/Subscription';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { environment } from '../../../../../environments/environment';
import { UtilsService } from '../../../../shared/services/utils.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { RefactorFieldsService } from '../../../../shared/services/refactor-fields.service';


@Component({
  selector: 'app-cis-score-new',
  templateUrl: './cis-score-new.component.html',
  styleUrls: ['./cis-score-new.component.css'],
  // tslint:disable-next-line:use-host-property-decorator
  host: {
    '(window:resize)': 'onResize($event)'
  }
})
export class CisScoreNewComponent implements OnInit, OnDestroy, AfterViewInit {
  pageTitle = 'CIS Compliance';
  private doughnutDataSubscription: Subscription;
  private bargraphSubscription: Subscription;
  assetGroupSubscription: Subscription;
  cisApplicationSubscription: Subscription;
  policyDetailsSubscription: Subscription;

  public widgetWidth: any;
  public widgetHeight = 450;
  public widgetHeight2 = 230;
  public widgetWidth2: any;
  public innerRadious = 60;
  public outerRadious = 50;
  selectedAssetGroup;
  violationAccountSelect;
  benchmarkSelect;
  detailsPopup;
  popupText;
  performersArr = ['org', 'application'];
  cisScoreArr = ['Application', 'AccountOwner', 'all'];
  performersCnt = 0;
  cisScoreCount = 2;
  objectKeys = Object.keys;
  detailsValue;
  violatedRuleId;
  benchmarkError;
  categoryType;
  yAxisLabel = 'Score %';
  policyDescription;
  cisScoreResponsibility;
  cisApplicationBenchmarksCount = 0;
  cisResponsibiltiyCheckbox = {
    applicationRelated: false,
    accountRelated: false
  };

  public totalServiceData: any = {
    Doughnut: {
      status: 0
    },
    barGraph: {
      status: 0,
      processData: []
    },
    cisApplication: {
      status: 0,
    },
    cisAccount: {
      status: 0
    },
    cisBenchMarks: {
      status: 0
    },
    cisViolation: {
      status: 0
    }
  };

  @ViewChild('statsDoughnut') widgetContainer: ElementRef;

  constructor(
    private utilsService: UtilsService,
    private router: Router,
    private refactorFieldsService: RefactorFieldsService,
    private commonResponseService: CommonResponseService,
    private logger: LoggerService,
    private assetGroupObservableService: AssetGroupObservableService
    ) {

      this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(
        assetGroupName => {
            this.selectedAssetGroup = assetGroupName;
            this.getDimensions();
            this.totalServiceData.cisBenchMarks.status = 0;
            this.performersCnt = 0;
            this.updateComponent();
      });

    }

  ngAfterViewInit() {
  }

  onResize() {
    this.getDimensions();
  }

  getDimensions() {
    try {
      setTimeout(() => {
        const donutElement = document.getElementById('statsDoughnut1');
        if (donutElement) {
          this.widgetWidth = parseInt((window.getComputedStyle(donutElement, null).getPropertyValue('width')).split('px')[0], 10);
          this.widgetHeight = parseInt((window.getComputedStyle(donutElement, null).getPropertyValue('height')).split('px')[0], 10);
        }
        const trendElement = document.getElementById('cisTrend');
        if (trendElement) {
          this.widgetWidth2 = parseInt((window.getComputedStyle(trendElement, null).getPropertyValue('width')).split('px')[0], 10);
        // this.widgetHeight2 = parseInt((window.getComputedStyle(trendElement, null).getPropertyValue('height')).split('px')[0], 10);
        }
      }, 50);
    } catch (error) {
        this.logger.log('error', error);
    }
  }

  ngOnInit() {

  }

  /* Function to repaint component */
  updateComponent() {
    /* All functions variables which are required to be set for component to be reloaded should go here */
    this.getStatsData();
  }

  getStatsData() {
    this.getDoughnutData();
    this.getBarGraphData();
    this.getCisApplicationData();
    this.getCisAccountData();
  }

  getDoughnutData() {
    const url = environment.cisScores.url;
    const method = environment.cisScores.method;
    const queryParams = {
        'ag': this.selectedAssetGroup,
        'type': this.cisScoreArr[this.cisScoreCount]
    };
    this.totalServiceData.Doughnut.status = 0;
    this.cisApplicationBenchmarksCount = 0;
    this.cisResponsibiltiyCheckbox.applicationRelated = false;
    this.cisResponsibiltiyCheckbox.accountRelated = false;
    this.doughnutDataSubscription = this.commonResponseService
      .getData(url, method, {}, queryParams)
      .subscribe(
        response => {
          try {
            if (response.details.length === 0 ) {
                this.totalServiceData.Doughnut.status = -1;
                this.totalServiceData.cisBenchMarks.status = -1;
                this.totalServiceData.Doughnut.errorMessage = 'noDataAvailable';
              } else {
                this.getDimensions();
                this.totalServiceData.Doughnut.rawData = response;
                this.cisScoreResponsibility = response.agType;
                this.processDataForDoughnut(response);
                this.processViolation(response);
                this.totalServiceData.Doughnut.status = 1;
                this.totalServiceData.cisBenchMarks.status = 1;
              }
          } catch (e) {
              this.totalServiceData.Doughnut.errorMessage = 'jsError';
              this.totalServiceData.Doughnut.status = -1;
              this.totalServiceData.cisBenchMarks.status = -1;
              this.logger.log('error', e);
          }
        },
        error => {
          this.totalServiceData.Doughnut.status = -1;
          this.totalServiceData.cisBenchMarks.status = -1;
          this.totalServiceData.Doughnut.errorMessage = 'apiResponseError';
        }
      );
  }

  getBarGraphData() {

    if (this.bargraphSubscription) {
      this.bargraphSubscription.unsubscribe();
    }
    this.totalServiceData.barGraph.status = 0;
    const fromDate = new Date();
    fromDate.setMonth(fromDate.getMonth() - 3);
    const payload = {};
    const queryParam = {
      ag: this.selectedAssetGroup,
      filters: {},
      from: fromDate.toISOString().split('T')[0]
    };

    const url = environment.cisTrend.url;
    const method = environment.cisTrend.method;

    this.bargraphSubscription =  this.commonResponseService.getData(url, method, queryParam, payload).subscribe(
      response => {
        try {
          if (this.utilsService.checkIfAPIReturnedDataIsEmpty(response.data.response.cisTrend)) {
            this.totalServiceData.barGraph.status = -1;
            this.totalServiceData.barGraph.errorMessage = 'jsError';
          } else {
              this.totalServiceData.barGraph.rawData = response.data.response;

              this.processTrendGraph(response.data.response);
              this.totalServiceData.barGraph.status = 1;
          }

        } catch (e) {
          this.totalServiceData.barGraph.status = -1;
          this.totalServiceData.barGraph.errorMessage = 'jsError';
        }
      },
    error => {
      this.totalServiceData.barGraph.status = -1;
      this.totalServiceData.barGraph.errorMessage = 'apiResponseError';
    });
  }


  getCisApplicationData() {
    this.totalServiceData.cisApplication.status = 0;
    const payload = {};
    const cisApplicationQuery = { ag: this.selectedAssetGroup, type: this.performersArr[this.performersCnt] };
    const cisApplicationUrl = environment.cisApplication.url;
    const cisApplicationMethod = environment.cisApplication.method;
    this.cisApplicationSubscription = this.commonResponseService.getData(
      cisApplicationUrl, cisApplicationMethod, payload, cisApplicationQuery).subscribe(
        response => {
            try {
              if (response.response['data'].length === 0 ) {
                if (this.performersCnt < this.performersArr.length - 1 ) {
                  this.performersCnt++;
                  this.getCisApplicationData();
                } else {
                  this.totalServiceData.cisApplication.status = -1;
                  this.totalServiceData.cisApplication.errorMessage = 'noDataAvailable';
                }
              } else {
                this.totalServiceData.cisApplication.rawData = response.response;
                this.cisApplicationProcessData(response.response);
                this.totalServiceData.cisApplication.status = 1;

              }
            } catch (e) {
              this.totalServiceData.cisApplication.status = -1;
              this.totalServiceData.cisApplication.errorMessage = 'jsError';
              this.logger.log('error', e);
            }
        },
        error => {
          this.totalServiceData.cisApplication.status = -1;
          this.totalServiceData.cisApplication.errorMessage = 'apiResponseError';
          this.logger.log('error', error);
        });
  }

  getCisAccountData() {
    const url = environment.cisAccount.url;
    const method = environment.cisAccount.method;
    const queryParams = {
        'ag': this.selectedAssetGroup
    };
    this.totalServiceData.cisAccount.status = 0;
    this.doughnutDataSubscription = this.commonResponseService
      .getData(url, method, {}, queryParams)
      .subscribe(
        response => {
          try {
            if (this.utilsService.checkIfAPIReturnedDataIsEmpty(response.response.data)) {
                this.totalServiceData.cisAccount.status = -1;
                this.totalServiceData.cisAccount.errorMessage = 'noDataAvailable';
                return;
              }

            this.totalServiceData.cisAccount.rawData = response.response;
            this.processDataForAccount(response.response);
            this.totalServiceData.cisAccount.status = 1;

          } catch (e) {
              this.totalServiceData.cisAccount.errorMessage = 'jsError';
              this.totalServiceData.cisAccount.status = -1;
              this.logger.log('error', e);
          }
        },
        error => {
          this.totalServiceData.cisAccount.status = -1;
          this.totalServiceData.cisAccount.errorMessage = 'apiResponseError';
        }
      );
  }


  processTrendGraph(response) {
    const totalArray = [];
    this.totalServiceData.barGraph.colorSet = ['#645ec5', '#26ba9d', '#289cf7'];
    this.totalServiceData.barGraph.processData = [];
    response.cisTrend.forEach(element => {
    const obj = {
        date: new Date(element.date),
        keys: ['score'],
        legends: ['Score'],
        value: element.score
      };
      totalArray.push(obj);
    });
    totalArray.sort(function(a, b) {
      return new Date(a.date).getTime() - new Date(b.date).getTime();
  });

  this.totalServiceData.barGraph.processData.push({ 'values' : totalArray });
  }

  processViolation(response) {
      const raw_data = response.details;
      try {
        const application_benchmarks = raw_data.filter((element) =>
          element.innerDetails.some((subElement) => subElement.responsbility === 'Application'))
          .map(element => {
            let n = Object.assign({}, element, {'innerDetails': element.innerDetails.filter(
              subElement => subElement.responsbility === 'Application')});
              return n;
            });
        const account_benchmarks = raw_data.filter((element) =>
          element.innerDetails.some((subElement) => subElement.responsbility === 'Account Owner'))
          .map(element => {
            let n = Object.assign({}, element, {'innerDetails': element.innerDetails.filter(
              subElement => subElement.responsbility === 'Account Owner')});
              return n;
            });

        raw_data.unshift({
          entityType: 'All',
          innerDetails: this.getAllBenchmarks(raw_data)
        });
        application_benchmarks.unshift({
          entityType: 'All',
          innerDetails: this.getAllBenchmarks(application_benchmarks)
        });
        account_benchmarks.unshift({
          entityType: 'All',
          innerDetails: this.getAllBenchmarks(account_benchmarks)
        });

        this.cisResponsibiltiyCheckbox.applicationRelated = true;
        if (response.agType === 'Account') {
          this.cisResponsibiltiyCheckbox.accountRelated = true;
        }
        this.totalServiceData.cisViolation.rawData = raw_data;
        this.totalServiceData.cisViolation.applicationData = application_benchmarks;
        this.totalServiceData.cisViolation.accountData = account_benchmarks;
        this.cisApplicationBenchmarksCount = application_benchmarks.length;
        this.totalServiceData.cisViolation.displayData = response.agType === 'Application' ? application_benchmarks : raw_data;
        const key = this.massageData(this.totalServiceData.cisViolation.displayData);
        if (this.totalServiceData.cisViolation.displayData) {
          this.accountSelection(this.totalServiceData.cisViolation.displayData[0].entityType);
        }
      } catch (e) {
        this.logger.log('error', e);
      }
  }

  processDataForDoughnut(responseData) {
    const response = responseData;
    this.innerRadious = 70;
    this.outerRadious = 90;
    const formattedObject: any = {
        legendTextcolor: '#000',
        link: false,
        styling: {
          cursor: 'text'
        }
      };

    formattedObject.data =  [response.score, 100 - response.score];
    formattedObject.legendText = ['Compliant', 'Non Compliant'];
    formattedObject.totalCount = response.score + '%';
    formattedObject.color = ['#50c17c', '#e52f4d'];
    this.totalServiceData.Doughnut.processData = formattedObject;
  }

  cisApplicationProcessData(response) {
    const reverse_response = response.data.reverse();
    const leadersObj = this.processLeadersAndLaggards(reverse_response);
    this.totalServiceData.cisApplication.processData = reverse_response;
    this.totalServiceData.cisApplication.leaders = leadersObj.leaders;
    this.totalServiceData.cisApplication.laggards = leadersObj.laggards;
  }

  processLeadersAndLaggards(reverse_response) {
    const leaders_laggars_obj = {
      leaders: [],
      laggards: []
    };
    let sum_of_scores = 0;
    for (let i = 0; i < reverse_response.length; i++) {
      const cis_score_for_key = this.valueOfObjectKey(reverse_response[i]);
      sum_of_scores = sum_of_scores + cis_score_for_key;
    }

    const average_cis_score = sum_of_scores / reverse_response.length;

    for (let i = 0; i < reverse_response.length; i++) {
      const cis_score_for_key = this.valueOfObjectKey(reverse_response[i]);
      if (cis_score_for_key > average_cis_score || cis_score_for_key === 100) {
        leaders_laggars_obj.leaders.push(reverse_response[i]);
      } else {
        leaders_laggars_obj.laggards.push(reverse_response[i]);
      }
    }
    leaders_laggars_obj.laggards.reverse();
    return leaders_laggars_obj;
  }
  valueOfObjectKey(obj) {
    const current_key = Object.keys(obj)[0];
    return obj[current_key];
  }
  accountSelection(account) {
    this.violationAccountSelect = this.totalServiceData.cisViolation.displayData.find(value => account === value.entityType);
    if (this.violationAccountSelect) {
      this.benchmarkSelection(this.violationAccountSelect.innerDetails[0].ruleId);
    }

  }
  benchmarkSelection(selectedRuleId) {
    this.benchmarkError = 0;
    this.benchmarkSelect = this.violationAccountSelect.innerDetails.find(value => selectedRuleId === value.ruleId);
    this.violatedRuleId = this.benchmarkSelect.ruleId;
    this.benchmarkError = 1;
    this.getPolicyDescriptionData(this.benchmarkSelect.policyId);
    if (!this.benchmarkSelection) {
        this.benchmarkError = -1;
      }
  }

  getPolicyDescriptionData(policyId) {
   const queryParams = {
      ruleId: policyId
    };
    const url = environment.policyContentSlider.url;
    const method = environment.policyContentSlider.method;

    this.policyDetailsSubscription = this.commonResponseService
      .getData(url, method, {}, queryParams)
      .subscribe(
        response => {
          try {
            this.policyDescription = response.response;
            }catch (e) {
              this.logger.log('error', e);
            }
          },
        error => {
          this.logger.log('error', error);
        }
      );
  }

  processDataForAccount(response) {
    const reverse_response = response.data.reverse();
    const leadersObj = this.processLeadersAndLaggards(reverse_response);

    this.totalServiceData.cisAccount.processData = reverse_response;
    this.totalServiceData.cisAccount.leaders = leadersObj.leaders;
    this.totalServiceData.cisAccount.laggards = leadersObj.laggards;
  }
  openDetailsModal(name) {
    this.detailsPopup = true;
    if (name === 'application') {
      this.detailsValue = this.totalServiceData.cisApplication.processData;
      this.categoryType = this.totalServiceData.cisApplication.rawData.category;
      this.popupText = 'application';
    }
    if (name === 'account') {
      this.detailsValue = this.totalServiceData.cisAccount.processData;
      this.categoryType = this.totalServiceData.cisAccount.rawData.category;
      this.popupText = 'account';
    }

  }

  selectCategoryType(event) {
    console.log('Test is: ', this.cisResponsibiltiyCheckbox);
    // const toggledItemIndex = this.cisScoreArr.indexOf(title);
    const displayBenchmarksOptions = ['applicationData', 'accountData', 'rawData'];
    if (this.cisResponsibiltiyCheckbox.applicationRelated && this.cisResponsibiltiyCheckbox.accountRelated) {
      this.totalServiceData.cisViolation.displayData = this.totalServiceData.cisViolation.rawData;
    } else if (this.cisResponsibiltiyCheckbox.applicationRelated) {
      this.totalServiceData.cisViolation.displayData = this.totalServiceData.cisViolation.applicationData;
    } else if (this.cisResponsibiltiyCheckbox.accountRelated) {
      this.totalServiceData.cisViolation.displayData = this.totalServiceData.cisViolation.accountData;
    } else {
      this.totalServiceData.cisViolation.displayData = this.totalServiceData.cisViolation.rawData;
    }
    this.massageData(this.totalServiceData.cisViolation.displayData);
    this.accountSelection(this.totalServiceData.cisViolation.displayData[0].entityType);
  }

  massageData(data) {
    /*
       * the function replaces keys of the table header data to a readable format
     */
    const refactoredService = this.refactorFieldsService;
    this.totalServiceData.cisViolation.processData = [];
    data.map(eachRow => {
        const newObj = eachRow;
         const elementnew = refactoredService.getDisplayNameForAKey(eachRow.entityType.toLocaleLowerCase());
         if (elementnew) {
              newObj.updated_entity = elementnew;
          } else {
            newObj.updated_entity = eachRow.entityType;
          }
          this.totalServiceData.cisViolation.processData.push(newObj);
     });
  }

  getAllBenchmarks(data) {
    const all_benchmarks = [];
    for (let i = 0; i < data.length; i++) {
      for (let j = 0; j < data[i].innerDetails.length; j++) {
        all_benchmarks.push(data[i].innerDetails[j]);
      }
    }
    try {
      all_benchmarks.sort(function(a, b) {
        const firstRule = a.ruleId.split('.');
        const secondRule = b.ruleId.split('.');
        return (parseInt(firstRule[0], 10) * 1000 + parseInt(firstRule[1], 10)) - (parseInt(secondRule[0], 10) * 1000 + parseInt(secondRule[1], 10));
      });
    } catch (e) {
      this.logger.log('error', e);
    }
    return all_benchmarks;
  }
  getErrorFromDoughnut(event) {
    if (event) {
      this.totalServiceData.Doughnut.status = -1;
      this.totalServiceData.Doughnut.errorMessage = event;
    }
  }

  showHelpContent(cisScoreHelpText) {
    let widgetId = 'w10';
    if (cisScoreHelpText) {
      widgetId = this.cisScoreResponsibility === 'Application' ? 'w6' : 'w7';
    }
    const newParams = { widgetId: widgetId };
    this.router.navigate(
        ['/pl', { outlets: { helpTextModal: ['help-text'] } }],
        { queryParams: newParams, queryParamsHandling: 'merge' }
    );
  }

  ngOnDestroy() {
    try {
      if (this.doughnutDataSubscription) {
        this.doughnutDataSubscription.unsubscribe();
      }
      if (this.cisApplicationSubscription) {
        this.cisApplicationSubscription.unsubscribe();
      }
      if (this.bargraphSubscription) {
        this.bargraphSubscription.unsubscribe();
      }
      if (this.policyDetailsSubscription) {
        this.policyDetailsSubscription.unsubscribe();
      }
      this.assetGroupSubscription.unsubscribe();
    } catch (error) {
        this.logger.log('on destroy error', error);
      }
  }

}
