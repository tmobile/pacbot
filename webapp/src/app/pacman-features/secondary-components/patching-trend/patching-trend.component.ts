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

import { Component, OnInit, Input, ViewEncapsulation, OnDestroy, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { ActivatedRoute, Router} from '@angular/router';
import { DataCacheService } from '../../../core/services/data-cache.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { AllPatchingProgressService } from '../../services/patching-progress.service';
import { environment } from './../../../../environments/environment';
import { Subscription } from 'rxjs/Subscription';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { CommonResponseService } from '../../../shared/services/common-response.service';

@Component({
  selector: 'app-patching-trend',
  templateUrl: './patching-trend.component.html',
  styleUrls: ['./patching-trend.component.css'],
  providers: [AllPatchingProgressService, AutorefreshService, CommonResponseService],
  encapsulation: ViewEncapsulation.None,
  // tslint:disable-next-line:use-host-property-decorator
  host: {
    '(window:resize)': 'onResize($event)'
  }
})
export class PatchingTrendComponent implements OnInit, OnDestroy, AfterViewInit {

  @ViewChild('patchProgressContainer') widgetContainer: ElementRef;

  selectedAssetGroup: string;
  durationParams: any;
  autoRefresh: boolean;


  /* Subscription variables*/

  private assetGroupSubscription: Subscription;
  private getPatchingSubscriptionQuarter: Subscription;
  private getPatchingQuarterData: Subscription;

  /* Variables for handling data and for calculations*/

  checkBtn: any;
  private nonComplianceQuarter: any = {};
  private ComplianceQuarter: any = {};
  private compliantDataQuarter: any = {};
  private dataStorage: any = [];
  private dataWholeSetQuarter1: any = {};
  weekValue: any = [];
  weekNumber: any = [];
  quarterData: any = [];
  weekValueQuarter1: any = [];
  weekNumberQuarter1: any = [];
  dayQuarter: any;
  private monthQuarter: any;
  private amiavail_dateQuarter: any;
  private end_dateQuarter: any;
  private internal_targetQuarter: any;
  private quarterGraph1: any = [];
  showQuarter: any;
  private start: any;
  private end: any;
  private dataWholeSet: any;
  private selectedData: any;
  private showToday: any;
  year: any;
  private startDate: any;
  private endDate: any;
  yearArray: any = [];
  private quarterArray: any = [];
  quarterDataArray: any = [];
  private selected: any;

  /* Input variables for the graph*/

  graphHeight: any;
  todayValue: boolean;
  graphWidth: any;
  graphData: any;
  graphDataQuarter1: any = [];
  amiavail_date: any;
  end_date: any;
  internal_target: any;
  lastDate: any;

  /* Boolean variables for setting the data*/

  dataLoaded = false;
  error = false;
  loading = false;
  errorMessage: any = 'apiResponseError';
  yAxisLabel = 'Instances';
  showGraphLegend = true;
  showOpposite = false;
  tempLoader = false;
  currentQuarterViewed = false;

  private autorefreshInterval;

  @Input() hiddenComponent: string;

  constructor(private activatedRoute: ActivatedRoute,
              private assetGroupObservableService: AssetGroupObservableService,
              private dataStore: DataCacheService,
              private router: Router,
              private allPatchingProgressService: AllPatchingProgressService,
              private autorefreshService: AutorefreshService,
              private commonResponseService: CommonResponseService,
              private logger: LoggerService, private errorHandling: ErrorHandlingService) {

    /* On change of asset group, calls the init function to update the data*/

    this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(
        assetGroupName => {
        this.selectedAssetGroup = assetGroupName;
        this.init();

        this.durationParams = this.autorefreshService.getDuration();
        this.durationParams = parseInt(this.durationParams, 10);
        this.autoRefresh = this.autorefreshService.autoRefresh;
  });

        this.durationParams = this.autorefreshService.getDuration();
        this.durationParams = parseInt(this.durationParams, 10);
        this.autoRefresh = this.autorefreshService.autoRefresh;
  }

  onResize() {
      const element = document.getElementById('current');
      if (element) {
          this.graphWidth = parseInt((window.getComputedStyle(element, null).getPropertyValue('width')).split('px')[0], 10);
      }
  }

  /* Function to show/hide the dropdown container */

  showOtherDiv() {
    this.showOpposite = !this.showOpposite;
  }

  /* Function to show current quarter data */

  showCurrentQuarter() {

    const yearClass = document.getElementsByClassName('patching-each-year')[this.selected];
    if (yearClass !== undefined) {
      yearClass.classList.remove('selected');
    }
    this.currentQuarterViewed = true;
    this.setDataLoading();
    this.tempLoader = true;
    let patchingData = this.dataStore.get('patchingAvailableQuarters-' + this.selectedAssetGroup);
    patchingData = patchingData ? JSON.parse(patchingData) : undefined;
    this.getIssues(patchingData);

  }

  /* Function to choose individual quarter and update graph according to that quarter */

  showQuarterDiv(index,  year) {
    this.showOpposite = !this.showOpposite;
    this.setDataLoading();
    if (index === 1) {
      this.checkBtn = 1;
    } else if (index === 2) {
      this.checkBtn = 2;
    } else if (index === 3) {
      this.checkBtn = 3;
    } else {
      this.checkBtn = 4;
    }

    let patchingData = this.dataStore.get('patchingAvailableQuarters-' + this.selectedAssetGroup);
    patchingData = patchingData ? JSON.parse(patchingData) : undefined;

    this.getIssues(patchingData, year, index);

  }

  clearVariables(data) {
    this.graphData = undefined;
    this.weekNumber = undefined;
    this.weekValue = undefined;
    this.amiavail_date = undefined;
    this.end_date = undefined;
    this.internal_target = undefined;
    this.lastDate = undefined;
    this.year = undefined;
    this.showQuarter = undefined;
  }

  setGraphVariables(data) {
    this.graphData = data.data;
    this.weekNumber = data.weekNum;
    this.weekValue = data.weekVal;
    this.amiavail_date = data.ami;
    this.end_date = data.end;
    this.internal_target = data.int;
    this.lastDate = data.lastDate;
    this.year = data.year;
    this.showQuarter = data.key;
    this.setDataLoaded();
  }

  isActive(item) {
      if (this.currentQuarterViewed === false ) {
        return this.selected === item;
      }
  }

  getMetaData(year?: any, index?: any) {
    try {
      this.currentQuarterViewed = false;
      if (index !== undefined) {
        this.selected = index;
      }

      this.tempLoader = true;
      this.yearArray = [];
      let yearlyQuarterData = this.dataStore.get('patchingAvailableQuarters-' + this.selectedAssetGroup);
      yearlyQuarterData = yearlyQuarterData ? JSON.parse(yearlyQuarterData) : undefined;
      if ((!yearlyQuarterData) || (yearlyQuarterData.length === 0)) {
        const patchingQuarterUrl = environment.patchingQuarter.url;
        const patchingQuarterMethod = environment.patchingQuarter.method;
        const payload = {};
        const queryParams = {
          'assetGroup': this.selectedAssetGroup
        };

        this.getPatchingQuarterData = this.allPatchingProgressService.getQuarterData(payload, patchingQuarterUrl, patchingQuarterMethod, queryParams).subscribe(
          response => {

            try {

              if (response.length) {
                response.forEach(yearData => {
                 this.yearArray.push(yearData.year);
               });
               this.yearArray = Array.from(new Set(this.yearArray));
               this.dataStore.set('patchingAvailableQuarters-' + this.selectedAssetGroup, JSON.stringify(response));
               this.getIssues(response);
             } else {
               this.setError('noDataAvailable');
             }

            } catch (error) {
              this.setError('jsError');
            }
          },
          error => {
            this.setError('apiResponseError');
          }
        );
      } else {
        yearlyQuarterData.forEach(yearData => {
         this.yearArray.push(yearData.year);
        });
        this.yearArray = Array.from(new Set(this.yearArray));
        if (yearlyQuarterData !== undefined) {
          if (year === new Date().getFullYear()) {
            this.getIssues(yearlyQuarterData, year, index);
          } else {
            this.getIssues(yearlyQuarterData, year);
          }
        }
      }
    } catch (error) {
      this.setError('jsError');
    }
  }

  getIssues(data, year?: any, index?: any) {

    try {

      let patchingData = this.dataStore.get('patching_' + year);
      patchingData = patchingData ? JSON.parse(patchingData) : undefined;
        const patchingQuarterUrl = environment.patchingProgress.url;
        const patchingQuarterMethod = environment.patchingProgress.method;
        const payload = {};
        this.getPatchingSubscriptionQuarter = this.allPatchingProgressService.getData(data, patchingQuarterUrl, patchingQuarterMethod, this.selectedAssetGroup, year).subscribe(
          response => {
            this.quarterDataArray = [];
            this.quarterDataArray = response;
            this.dataStore.set('patching_' + response[0].year, JSON.stringify(response));
            if ((response[0].year === new Date().getFullYear()) && (index === undefined)) {
              this.checkBtn = response.length;
            } else if (index !== undefined) {
              this.checkBtn = index;
            } else {
              this.checkBtn = undefined;
            }
            if ((year === undefined) || ((index !== undefined) && (year !== new Date().getFullYear())) || ((index !== 0) && (year === new Date().getFullYear())) ) {
              this.setDataLoading();
              this.clearVariables(response[this.checkBtn - 1]);
              this.setGraphVariables(response[this.checkBtn - 1]);
            }
            this.tempLoader = false;

          },
          error => {
            this.setError(error);
          }
        );
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.setError(error);
    }

  }

  getData() {
    this.getMetaData();
  }

  init() {
    this.setDataLoading();
    this.getData();
  }

  setDataLoaded() {
    this.dataLoaded = true;
    this.loading = false;
  }

  setDataLoading() {
    this.dataLoaded = false;
    this.error = false;
    this.loading = true;
  }

  setError(message?: any) {
    this.dataLoaded = false;
    this.error = true;
    this.loading = false;
    if (message) {
      this.errorMessage = message;
    }
  }

  ngAfterViewInit() {
    const afterLoad = this;
    if (this.autoRefresh !== undefined ) {
      if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {
        this.autorefreshInterval = setInterval(function() {
          afterLoad.init();
        }, this.durationParams);
      }
    }
  }

  ngOnInit() {
    this.graphWidth = parseInt(window.getComputedStyle(this.widgetContainer.nativeElement, null).getPropertyValue('width'), 10);
    this.graphHeight = parseInt(window.getComputedStyle(this.widgetContainer.nativeElement, null).getPropertyValue('height'), 10);
  }

  ngOnDestroy() {
    try {
      this.assetGroupSubscription.unsubscribe();
      clearInterval(this.autorefreshInterval);
    } catch (error) {
    }
  }
}
