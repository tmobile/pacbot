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
  Output,
  EventEmitter,
  OnDestroy,
  ViewChild,
  ElementRef,
  AfterViewInit
} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { CommonResponseService } from '../../shared/services/common-response.service';
import { AutorefreshService } from '../../pacman-features/services/autorefresh.service';
import { environment } from '../../../environments/environment';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';
import { ActivatedRoute, Router } from '@angular/router';
import html2canvas from 'html2canvas';
import * as _ from 'lodash';
import { RefactorFieldsService } from '../../shared/services/refactor-fields.service';
import { CONFIGURATIONS } from './../../../config/configurations';

@Component({
  selector: 'app-stats-overlay',
  templateUrl: './stats-overlay.component.html',
  styleUrls: ['./stats-overlay.component.css'],
  providers: [CommonResponseService, AutorefreshService],
  // tslint:disable-next-line:use-host-property-decorator
  host: {
    '(window:resize)': 'onResize($event)'
  }
})
export class StatsOverlayComponent implements OnInit, OnDestroy, AfterViewInit {
  public currentMonth: any = '';
  public currentYear: any = 0;

  selectedAssetGroup: string;
  public apiData: any;
  public applicationValue: any;
  public errorMessage: any;
  public dataComing = false;
  public showLoader = true;
  public tableHeaderData: any;
  private dataSubscription: Subscription;
  public placeHolderText: any;
  public returnedSearch = '';
  public seekdata = false;
  public sampleData;
  durationParams: any;
  autoRefresh: boolean;

  public numberOfAwsAccounts: any = '';
  public numberOfEventsProcessed: any = '';
  public numberOfPolicyWithAutoFixes: any = '';
  public numberOfPolicyEvaluations: any = '';
  public numberOfPoliciesEnforced: any = '';
  public totalNumberOfAssets: any = '';
  public totalViolationsGraph: any = [];
  public doughNutData: any = [];
  public widgetWidth: number;
  public widgetHeight: number;
  public MainTextcolor: any = '';
  public innerRadious = 60;
  public outerRadious = 50;
  public strokeColor = 'transparent';
  public totalAutoFixesApplied: any = '';
  private autorefreshInterval;
  @ViewChild('statsDoughnut') widgetContainer: ElementRef;
  config;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private commonResponseService: CommonResponseService,
    private autorefreshService: AutorefreshService,
    private errorHandling: ErrorHandlingService,
    private refactorFieldsService: RefactorFieldsService
  ) {
    /* ***************************************************************** */

    this.config = CONFIGURATIONS;

    this.durationParams = this.autorefreshService.getDuration();
    this.durationParams = parseInt(this.durationParams, 10);
    this.autoRefresh = this.autorefreshService.autoRefresh;
  }

  @Output() closeOverlay = new EventEmitter<any>();

  takeScreenshot() {
    const page: any = document.getElementById('stats-overlay-screenshot');
    const pageClone: any = page.cloneNode(true);
    const back = pageClone.querySelector('#stats-overlay-back');
    back.style.opacity = 0;
    const download = pageClone.querySelector('#stats-overlay-download');
    download.style.opacity = 0;

    const statsOverlayPage = document.getElementById('stats-overlay-page');
    statsOverlayPage.appendChild(pageClone);

    html2canvas(pageClone).then(function(canvas) {
      statsOverlayPage.removeChild(pageClone);
      const url = canvas.toDataURL('image/png');
      const binStr = atob(url.split(',')[1]),
        len = binStr.length,
        arr = new Uint8Array(len);

      for ( let i = 0; i < len; i++) {
        arr[i] = binStr.charCodeAt(i);
      }
      const blob = new Blob([arr]);
      const a = document.createElement('a');
      a.download = 'PacBot-Statistics.png';
      a.innerHTML = 'download';
      a.href = URL.createObjectURL(blob);

      a.click();
    });
  }

  ngAfterViewInit() {
    const afterLoad = this;
    if (this.autoRefresh !== undefined) {
      if (this.autoRefresh === true || this.autoRefresh.toString() === 'true') {
        this.autorefreshInterval = setInterval(function() {
          afterLoad.getStatsData();
        }, this.durationParams);
      }
    }
  }

  onResize() {
    this.getDimensions();
  }

  getDimensions() {

    const element = document.getElementById('statsDoughnut');
    if (element) {
      this.widgetWidth = parseInt(
        window
          .getComputedStyle(element, null)
          .getPropertyValue('width')
          .split('px')[0], 10
      ) + 20;
      this.widgetHeight = parseInt(
        window
          .getComputedStyle(element, null)
          .getPropertyValue('height')
          .split('px')[0], 10
      ) - 20;
    }
  }

  ngOnInit() {
    this.updateComponent();
    this.getMonth();
  }

  /* Function to repaint component */
  updateComponent() {
    /* All functions variables which are required to be set for component to be reloaded should go here */

    this.showLoader = true;
    this.dataComing = false;
    this.seekdata = false;
    this.getData();
  }

  /* Function to get Data */
  getData() {
    /* All functions to get data should go here */

    this.getStatsData(); // returns full stats data
  }

  BackPage() {
    this.closeOverlay.emit();
  }

  getMonth() {
    const months = [
      'January',
      'February',
      'March',
      'April',
      'May',
      'June',
      'July',
      'August',
      'September',
      'October',
      'November',
      'December'
    ];

    const current_date = new Date();
    const month_value = current_date.getMonth();
    const year_value = current_date.getFullYear();
    this.currentMonth = months[month_value];
    this.currentYear = year_value;
  }

  getStatsData() {
    const queryParams = {
  };

  const statspageUrl = environment.statspage.url;
  const statspageMethod = environment.statspage.method;
    this.dataSubscription = this.commonResponseService
      .getData(statspageUrl, statspageMethod, {}, queryParams)
      .subscribe(
        response => {
          try {
            if (response.response.length === 0) {
              // response.response.length == 0
              // fails on mock data
              this.getErrorValues();
              this.errorMessage = 'noDataAvailable';
            } else {
              this.showLoader = false;
              this.seekdata = false;
              this.dataComing = true;
              this.getDimensions();
              this.processData(response);
            }
          } catch (e) {
            this.errorMessage = this.errorHandling.handleJavascriptError(e);
            this.getErrorValues();
          }
        },
        error => {
          this.errorMessage = error;
          this.getErrorValues();
        }
      );
  }

  // error values
  getErrorValues(message?: any): void {
    this.showLoader = false;
    this.dataComing = false;
    this.seekdata = true;
    if (message) {
      this.errorMessage = message;
    }
  }

  processData(data) {
    const response = data;
    this.numberOfAwsAccounts = response.response[0].numberOfAwsAccounts;
    this.numberOfEventsProcessed = response.response[0].numberOfEventsProcessed;
    this.numberOfPolicyEvaluations =
      response.response[0].numberOfPolicyEvaluations;
    this.numberOfPoliciesEnforced =
      response.response[0].numberOfPoliciesEnforced;
    this.totalNumberOfAssets = response.response[0].totalNumberOfAssets;
    this.totalViolationsGraph = response.response[0].totalViolations;
    this.numberOfPolicyWithAutoFixes = response.response[0].numberOfPolicyWithAutoFixes;
    this.totalAutoFixesApplied = response.response[0].totalAutoFixesApplied;

    /**
         ------ this is the data for statspage doughnut chart for policy with violations ---------
         */
    this.MainTextcolor = '#fff';
    this.strokeColor = 'eff3f6';
    const colorTransData = ['#D40325', '#F75C03', '#FFE00D', '#FFB00D'];
    const graphLegend = ['Critical', 'High', 'Medium', 'Low'];
    const graphDataArray = [];
    const legendTextcolor = '#fff';
    /**
     * Added by Trinanjan on 02/03/2018
     * Inorder to sort objkeys in a logical way, objKeys are hardcoded
     */
    const objKeys = ['critical', 'high', 'medium', 'low', 'totalViolations'];
    /* ****************************************************************** */
    objKeys.splice(objKeys.indexOf('totalViolations'), 1);
    objKeys.forEach(element => {
      graphDataArray.push(this.totalViolationsGraph[element]);
    });
    this.innerRadious = 70;
    this.outerRadious = 50;
    const formattedObject = {
      color: colorTransData,
      data: graphDataArray,
      legend: graphLegend,
      totalCount: this.totalViolationsGraph.totalViolations,
      legendTextcolor: legendTextcolor,
      link: false,
      styling: {
        cursor: 'text'
      }
    };
    this.doughNutData = formattedObject;
  }

  /**
   * this function closes the stats model page and navigates to the last active route
   */

  closeStatsModal(value?: string) {
    this.router.navigate(
      [
        // No relative path pagination
        {
          outlets: {
            modalBGMenu: null
          }
        }
      ],
      {
        relativeTo: this.activatedRoute.parent, // <-- Parent activated route
        queryParamsHandling: 'merge'
      }
    );
  }

  ngOnDestroy() {
    try {
      if (this.dataSubscription) {
        this.dataSubscription.unsubscribe();
      }
      clearInterval(this.autorefreshInterval);
    } catch (error) {
      this.errorMessage = this.errorHandling.handleJavascriptError(error);
      this.getErrorValues();
    }
  }
}
