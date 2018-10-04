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
  OnDestroy,
  Input,
  SimpleChanges,
  OnChanges
} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { UtilsService } from '../../../shared/services/utils.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { WorkflowService } from '../../../core/services/workflow.service';
import * as d3 from 'd3-selection';
import * as d3Shape from 'd3-shape';
import * as d3Scale from 'd3-scale';
import * as d3Array from 'd3-array';
import * as d3Axis from 'd3-axis';
import * as _ from 'lodash';

@Component({
  selector: 'app-pull-request-line-metrics',
  templateUrl: './pull-request-line-metrics.component.html',
  styleUrls: ['./pull-request-line-metrics.component.css'],
  providers: [CommonResponseService, LoggerService, AutorefreshService]
})
export class PullRequestLineMetricsComponent implements OnInit, OnDestroy, OnChanges {
  constructor(
    private commonResponseService: CommonResponseService,
    private logger: LoggerService,
    private assetGroupObservableService: AssetGroupObservableService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private workflowService: WorkflowService,
    private autorefreshService: AutorefreshService,
    private utilsService: UtilsService
  ) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        if (assetGroupName) {
          this.selectedAssetGroup = assetGroupName;
          this.updateComponent();
        }
      });
  }

  // Subscription variables
  onpremSubscription: Subscription;
  subscriptionToAssetGroup: Subscription;

  // Page Variables
  dataResponse: any = [];
  rawData: any = [];
  finalData: any = [];
  yearPicked;
  yearPickedIndex = 0;
  errorMessage = 'jsError';
  maxVal: any = [0, 0];
  currentQuarter;
  currentYear;
  firstQuarter;
  firstYear;
  selectedQuarter;
  selectedYear = 0;
  currentQuarterWeeks;
  appFilter;
  axisValues = {
    y0: [],
    y1: []
  };
  selectedAssetGroup: string;
  urlToRedirect: string;
  dataState = 0;
  percentToday = -1;
  selectedWeek = -1;

  // Graph colors init
  darkBlue = '#198EFB';
  lightBlue = '#0BB972';
  lightGreen = 'rgb(255,178,0)';

  // donut chart variables

  widgetWidth = 200;
  widgetHeight = 200;
  MainTextcolor = '#000';
  innerRadious: any = 80;
  outerRadious: any = 50;
  strokeColor = '#e8ebee88';
  graphData: any = [];
  private legend_text: any;

  // auto refresh variables

  durationParams: any;
  autoRefresh = false;
  autorefreshInterval;

  @Input() filter: any;

  ngOnChanges (changes: SimpleChanges) {
    try {
      const DataChange = changes['filter'];
      this.appFilter = DataChange.currentValue;
      if (DataChange) {
        const cur = JSON.stringify(DataChange.currentValue);
        const prev = JSON.stringify(DataChange.previousValue);
        if (cur !== prev) {
          this.updateComponent();
        }
      }
    } catch (error) {
      this.dataState = -1;
      this.errorMessage = 'jsError';
    }
  }

  ngOnInit() {
    try {
      this.urlToRedirect = this.router.routerState.snapshot.url;
      this.durationParams = this.autorefreshService.getDuration();
      this.durationParams = parseInt(this.durationParams, 10);
      this.autoRefresh = this.autorefreshService.autoRefresh;
      const afterLoad = this;
      if (this.autoRefresh !== undefined) {
        if (this.autoRefresh === true || this.autoRefresh.toString() === 'true') {
          this.autorefreshInterval = setInterval(function() {
            afterLoad.updateComponent();
          }, this.durationParams);
        }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  updateComponent() {
    this.setGraphParams();
    this.getData();
  }

  getData() {
    this.resetPage();
    const date = new Date();
    this.currentQuarter = Math.ceil((date.getMonth() + 1) / 3);
    this.currentYear = date.getFullYear();
    this.yearPicked = this.currentYear;
    this.currentQuarterWeeks = this.utilsService.getNumberOfWeeks(
      this.currentYear,
      this.currentQuarter
    );
    const url = environment.PullReqLineMetrics.url;
    const queryParam = {
      ag: this.selectedAssetGroup,
      quarter: this.currentQuarter,
      year: this.currentYear,
      application: this.filter
    };
    const payload = {};
    const method = environment.PullReqLineMetrics.method;

    this.onpremSubscription = this.commonResponseService
      .getData(url, method, payload, queryParam)
      .subscribe(
        response => {
          try {
            this.dataResponse = response;
            this.rawData = JSON.parse(JSON.stringify(response));
            if (this.dataResponse.pullRequestStateByWeek.length === 0) {
              this.dataState = -1;
              this.errorMessage = 'noDataAvailable';
            } else {
              const firstDate = new Date(this.dataResponse.startDateOfData);
              this.firstQuarter = Math.ceil((firstDate.getMonth() + 1) / 3);
              this.firstYear = firstDate.getFullYear();
              this.computeYearObj();
              this.dataState = 1;
              this.selectedWeek =
                this.dataResponse.pullRequestStateByWeek.length - 1;
              for (
                let p = this.dataResponse.pullRequestStateByWeek.length;
                p < this.currentQuarterWeeks;
                p++
              ) {
                const weekObj = {
                  totalMerged: -1,
                  week: p + 1,
                  avgAge: -1,
                  totalCreated: -1,
                  declined: 0,
                  totalDeclined: -1,
                  merged: 0,
                  pullRequests: 0,
                  totalPullRequests: 0,
                  open: 0,
                  futureWeek: true
                };
                this.dataResponse.pullRequestStateByWeek.push(weekObj);
              }

              this.processData(this.dataResponse.pullRequestStateByWeek);
              this.plotDonut(this.selectedWeek);
            }
          } catch (e) {
            this.dataState = -1;
            this.errorMessage = 'jsError';
            this.logger.log('error', e);
          }
        },
        error => {
          this.dataState = -1;
          this.errorMessage = 'apiResponseError';
          this.logger.log('error', error);
        }
      );
  }

  loadNewData() {
    try {
      this.setGraphParams();
      this.maxVal = [0, 0];
      this.dataResponse = this.finalData[this.selectedYear].quartersData[this.selectedQuarter].data;
      this.selectedWeek = this.finalData[this.selectedYear].quartersData[this.selectedQuarter].rawData.pullRequestStateByWeek.length - 1;
      this.processData(this.dataResponse.pullRequestStateByWeek);
      this.plotDonut(this.selectedWeek);
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  resetPage() {
    if (this.onpremSubscription) {
      this.onpremSubscription.unsubscribe();
    }
    this.dataState = 0;
    this.selectedYear = 0;
    this.yearPickedIndex = 0;
    this.selectedWeek = -1;
    this.dataResponse = [];
    this.percentToday = -1;
    this.maxVal = [0, 0];
    this.axisValues = {
      y0: [],
      y1: []
    };
  }

  computeYearObj() {
    try {
      const yearData = [];
      let cnt = -1;
      for ( let i = this.currentYear; i >= this.firstYear; i--) {
        cnt++;
        const eachObj = {
          'year': i,
          'quartersData': []
        };
        yearData.push(eachObj);
        for ( let j = 0; j < 4 ; j++ ) {
          const quarterObj = {
            'number': j + 1,
            'data': {},
            'rawData': {}
          };
          if ( i === this.currentYear && j + 1 <= this.currentQuarter ) {
            yearData[cnt].quartersData.push(quarterObj);
          }

          if ( i !== this.currentYear && i !== this.firstYear ) {
            yearData[cnt].quartersData.push(quarterObj);
          }

          if ( i === this.firstYear && j + 1 >= this.firstQuarter ) {
            yearData[cnt].quartersData.push(quarterObj);
          }
        }
      }
      this.finalData = yearData;
      this.finalData[this.selectedYear].quartersData[this.finalData[this.selectedYear].quartersData.length - 1].rawData = this.rawData;
      this.finalData[this.selectedYear].quartersData[this.finalData[this.selectedYear].quartersData.length - 1].data = this.dataResponse;
      this.selectedQuarter = this.finalData[this.selectedYear].quartersData.length - 1;
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  setGraphParams() {
    try {
      if (document.getElementById('onpremGraph')) {
        document.getElementById('onpremGraph').innerHTML = '';
        document.getElementById('onpremGraph').style.width =
          document.getElementById('onpremContainer').clientWidth + 'px';
        document.getElementById('onpremGraph').style.height =
          document.getElementById('onpremContainer').clientHeight + 'px';
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  processData(data) {
    const firstDay = new Date(data[0].date).getTime();
    const lastDay =
      new Date(data[data.length - 1].date).getTime() + 7 * 24 * 60 * 60 * 1000;
      const today = new Date().getTime();
    this.percentToday = (today - firstDay) * 100 / (lastDay - firstDay);

    const graphThis = this;
    setTimeout(() => {
      const margin = { top: 40, right: 27, bottom: 45, left: 77 },
        width =
          document.getElementById('onpremContainer').clientWidth -
          margin.left -
          margin.right,
        height =
          document.getElementById('onpremContainer').clientHeight -
          margin.top -
          margin.bottom;

      const strData = JSON.stringify(data);
      const newData = JSON.parse(strData);
      const zeroObj = {
        totalMerged: 0,
        week: 0,
        avgAge: 0,
        totalCreated: 0,
        declined: 0,
        totalDeclined: 0,
        merged: 0,
        pullRequests: 0,
        totalPullRequests: 0,
        open: 0
      };
      newData.unshift(zeroObj);

      // set the ranges of each axis
      const x = d3Scale.scalePoint().range([0, width]);
      x.domain(newData.map(d => d['week']));

      const xScale = d3Scale.scaleBand().range([0, width]);

      xScale.domain(data.map(d => d['week']));

      const y0 = d3Scale.scaleLinear().range([height, 0]);

      const area = d3Shape
        .area()
        .defined(function(d) {
          return d['totalCreated'] === -1 ? false : true;
        })
        .x(function(d) {
          return x(d['week']);
        })
        .y1(function(d) {
          return y0(d['totalCreated']);
        });

      // define the 1st line
      const valueline1 = d3Shape
        .line()
        .defined(function(d) {
          return d['totalDeclined'] === -1 ? false : true;
        })
        .x(function(d) {
          return x(d['week']);
        })
        .y(function(d) {
          graphThis.findMaxVal(d);
          return y0(d['totalDeclined']);
        });

      // define the 2nd line
      const valueline2 = d3Shape
        .line()
        .defined(function(d) {
          return d['totalCreated'] === -1 ? false : true;
        })
        .x(function(d) {
          return x(d['week']);
        })
        .y(function(d) {
          graphThis.findMaxVal(d);
          return y0(d['totalCreated']);
        });

      // define the 3rd line
      const valueline3 = d3Shape
        .line()
        .defined(function(d) {
          return d['totalMerged'] === -1 ? false : true;
        })
        .x(function(d) {
          return x(d['week']);
        })
        .y(function(d) {
          graphThis.findMaxVal(d);
          return y0(d['totalMerged']);
        });

      // define the 4th line
      const valueline4 = d3Shape
        .line()
        .defined(function(d) {
          return d['totalCreated'] === -1 ? false : true;
        })
        .x(function(d) {
          return x(d['week']);
        })
        .y(function(d) {
          graphThis.findMaxVal(d);
          return y0(d['totalCreated']);
        });

      // define the 5th line
      const valueline5 = d3Shape
        .line()
        .defined(function(d) {
          return d['totalMerged'] === -1 ? false : true;
        })
        .x(function(d) {
          return x(d['week']);
        })
        .y(function(d) {
          graphThis.findMaxVal(d);
          return y0(d['totalMerged']);
        });

      // set dimentions of svg
      const svg = d3
        .select('#onpremGraph')
        .attr('width', width + margin.left + margin.right)
        .attr('height', height + margin.top + margin.bottom)
        .append('g')
        .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');

      // format the data
      data.forEach(function(d) {
        d['week'] = +d['week'];
        d['totalDeclined'] = +d['totalDeclined'];
        d['totalMerged'] = +d['totalMerged'];
        d['totalCreated'] = +d['totalCreated'];
      });

      // Scale the range of the data for each axes
      y0.domain([
        d3Array.min(newData, function(d) {
          return Math.min(
            d['totalCreated'],
            d['totalMerged'],
            d['totalDeclined'],
            5
          );
        }),
        d3Array.max(newData, function(d) {
          return Math.max(
            d['totalCreated'],
            d['totalMerged'],
            d['totalDeclined'],
            5
          );
        })
      ]);

      // Add the X Bottom Band(Bar) Axis
      svg
        .append('g')
        .attr('transform', 'translate(0,' + height + ')')
        .attr('class', 'bottomAxis')
        .call(d3Axis.axisBottom(x))
        .call(make_x_gridlines().tickSize(-height));

      svg
        .append('clipPath')
        .data([newData])
        .attr('id', 'clip-below')
        .append('path')
        .attr('d', area.y0(height));

      svg
        .append('clipPath')
        .data([newData])
        .attr('id', 'clip-above')
        .append('path')
        .attr('d', area.y0(0));

      svg
        .append('path')
        .data([newData])
        .attr('class', 'areaLine above')
        .attr('clip-path', 'url(#clip-above)')
        .attr(
          'd',
          area.y0(function(d) {
            return y0(d['totalMerged']);
          })
        );

      svg
        .append('path')
        .data([newData])
        .attr('class', 'areaLine below')
        .attr('clip-path', 'url(#clip-below)')
        .attr('d', area);

      // Add the valueline2 path.
      svg
        .append('path')
        .data([newData])
        .attr('class', 'line thin-line')
        .style('stroke', graphThis.darkBlue)
        .attr('d', valueline2);

      // Add the valueline3 path.
      svg
        .append('path')
        .data([newData])
        .attr('class', 'line thin-line')
        .style('stroke', graphThis.lightBlue)
        .attr('d', valueline3);

      // Add the valueline4 path.
      svg
        .append('path')
        .data([newData])
        .attr('class', 'line')
        .style('stroke', graphThis.darkBlue)
        .style('stroke-width', '3px')
        .style('stroke-dasharray', '4,5')
        .attr('d', valueline4);

      // Add the valueline5 path.
      svg
        .append('path')
        .data([newData])
        .attr('class', 'line')
        .style('stroke', graphThis.lightBlue)
        .style('stroke-width', '3px')
        .style('stroke-dasharray', '4,5')
        .attr('d', valueline5);

      // Add the valueline1 path.
      svg
        .append('path')
        .data([newData])
        .attr('class', 'line thin-line')
        .style('stroke', graphThis.lightGreen)
        .attr('d', valueline1);

      // gridlines in x axis function
      function make_x_gridlines() {
        return d3Axis.axisBottom(x).ticks(5);
      }
    }, 20);
  }

  findMaxVal(obj) {
    try {
      if (
        !isNaN(obj.totalMerged) &&
        !isNaN(obj.totalCreated) &&
        !isNaN(obj.totalDeclined)
      ) {
        this.maxVal[0] = Math.max(
          this.maxVal[0],
          obj['totalMerged'],
          obj['totalCreated']
        );
        for ( let i = 5; i > 0; i--) {
          this.axisValues['y0'].push(Math.ceil(this.maxVal[0] / 5 * i));
        }
        this.axisValues['y0'] = this.axisValues['y0'].slice(
          this.axisValues['y0'].length - 5
        );
        if (this.axisValues['y0'][0] < 5) {
          this.axisValues['y0'] = [5, 4, 3, 2, 1];
        }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  onResize() {
    try {
      this.setGraphParams();
      if (
        this.dataResponse.pullRequestStateByWeek &&
        this.dataResponse.pullRequestStateByWeek.length
      ) {
        this.processData(this.dataResponse.pullRequestStateByWeek);
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  processBarData(data) {
    const dataValue = [];
    const legendText = [];

    // filtering necessary fields from the obj
    const filter = 'open, merged, declined, created';

    const result = _.pick(data, filter.split(', '));

    const formatted_obj = [];
    const barColor = ['#00B946', '#26BA9D', '#F75C03', '#F2425F', '#D40325'];

    if (result.created >= 0) {
      formatted_obj.push({
        x_domain: 'created',
        y_domain: result.created,
        bar_color: barColor[0]
      });
    }
    if (result.merged >= 0) {
      formatted_obj.push({
        x_domain: 'merged',
        y_domain: result.merged,
        bar_color: barColor[1]
      });
    }
    if (result.declined >= 0) {
      formatted_obj.push({
        x_domain: 'declined',
        y_domain: result.declined,
        bar_color: barColor[2]
      });
    }
    if (result.open >= 0) {
      formatted_obj.push({
        x_domain: 'open',
        y_domain: result.open,
        bar_color: barColor[3]
      });
    }

    const legend = {
      y_axis: 'Pull Requests',
      x_axis: 'Pull Requests State'
    };
    this.graphData = formatted_obj;
    this.legend_text = legend;
  }

  checkIfEmptyObj() {
    try {

      for ( let i = 0; i < this.finalData[this.yearPickedIndex].quartersData.length; i++) {
        if (Object.keys(this.finalData[this.yearPickedIndex].quartersData[i].rawData).length === 0) {
          this.callNewData(this.finalData[this.yearPickedIndex].year, this.finalData[this.yearPickedIndex].quartersData[i].number, i);
        }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  callNewData(year, quarter, quarterIndex) {

    const url = environment.PullReqLineMetrics.url;
    const queryParam = {
      ag: this.selectedAssetGroup,
      quarter: quarter,
      year: year,
      application: this.filter
    };
    const payload = {};
    const method = environment.PullReqLineMetrics.method;

    this.finalData[this.currentYear - year].quartersData[quarterIndex].loading = true;

    this.onpremSubscription = this.commonResponseService
      .getData(url, method, payload, queryParam)
      .subscribe(
        response => {
          try {
            const dataResponse = response;
            this.finalData[this.currentYear - year].quartersData[quarterIndex].loading = false;
            if (dataResponse.pullRequestStateByWeek.length === 0) {
              this.finalData[this.currentYear - year].quartersData[quarterIndex].errorOccured = 'Oops! It\'s deserted here.';
            } else {
              this.finalData[this.currentYear - year].quartersData[quarterIndex].rawData = dataResponse;
              this.finalData[this.currentYear - year].quartersData[quarterIndex].data = dataResponse;
              this.finalData[this.currentYear - year].quartersData[quarterIndex].errorOccured = '';
            }
          } catch (e) {
            this.logger.log('error', e);
            this.finalData[this.currentYear - year].quartersData[quarterIndex].loading = false;
            this.finalData[this.currentYear - year].quartersData[quarterIndex].errorOccured = 'Oops! Something went wrong while fetching your data.';
          }
        },
        error => {
          this.logger.log('error', error);
          this.finalData[this.currentYear - year].quartersData[quarterIndex].loading = false;
          this.finalData[this.currentYear - year].quartersData[quarterIndex].errorOccured = 'Oops! Something went wrong while fetching your data.';
        }
      );

  }

  redirectGraph(data) {

    if (data.y_domain > 0) {

      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);

      const prStateObj = {
        'merged' : 'MERGED',
        'open' : 'OPEN',
        'declined': 'DECLINED',
        'created': 'CREATED'
      };
      const filter = {
         'resourceType': 'dgtldsgn-pullrequest',
         'prstate': prStateObj[data.x_domain],
         'year': this.currentYear - this.selectedYear,
         'qtr': this.finalData[this.selectedYear].quartersData[this.selectedQuarter].number,
         'week': this.selectedWeek + 1,
         'tags.Application.keyword': this.appFilter
      };
      const params = this.utilsService.makeFilterObj(filter);
      const apiTarget = {
        'TypeAsset' : 'pull-request-trend',
      };

      const newParams = Object.assign(params, apiTarget);
      newParams['mandatory'] = 'qtr|resourceType|prstate|week|year';

      this.router.navigate(['../../', 'assets' , 'asset-list'], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
    }
  }

  plotDonut(index) {
    this.processBarData(this.dataResponse.pullRequestStateByWeek[index]);
  }

  ngOnDestroy() {
    this.subscriptionToAssetGroup.unsubscribe();
    if (this.onpremSubscription) {
      this.onpremSubscription.unsubscribe();
    }
    clearInterval(this.autorefreshInterval);
  }
}
