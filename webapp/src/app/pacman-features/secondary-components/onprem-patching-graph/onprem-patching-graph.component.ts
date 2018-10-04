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

import { Component, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { ActivatedRoute, UrlSegment, Router } from '@angular/router';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { WorkflowService } from '../../../core/services/workflow.service';
import { PermissionGuardService } from '../../../core/services/permission-guard.service';

import * as d3 from 'd3-selection';
import * as d3Shape from 'd3-shape';
import * as d3Scale from 'd3-scale';
import * as d3Array from 'd3-array';
import * as d3Axis from 'd3-axis';

@Component({
  selector: 'app-onprem-patching-graph',
  templateUrl: './onprem-patching-graph.component.html',
  styleUrls: ['./onprem-patching-graph.component.css'],
  providers: [CommonResponseService, LoggerService]
})

export class OnpremPatchingGraphComponent implements OnInit, OnDestroy {

  constructor( private commonResponseService: CommonResponseService,
                private logger: LoggerService,
                private assetGroupObservableService: AssetGroupObservableService,
                private router: Router,
                private activatedRoute: ActivatedRoute,
                private workflowService: WorkflowService,
                private permissions: PermissionGuardService
            ) {
                  this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
                  assetGroupName => {
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
  maxVal: any = [0, 0];
  axisValues = {
    'y0': [],
    'y1': []
  };
  selectedAssetGroup: string;
  urlToRedirect: string;
  dataState = 0;
  percentToday = -1;
  onpremAccess = false;

  // Graph colors init
  darkBlue = '#1C5066'; // Weekly patches completed
  lightBlue = '#709eb1'; // Weekly patches target
  lightGreen = '#26BA9D'; // Overall patches

  // Emitter Events
  @Output() errorOccurred = new EventEmitter();
  @Output() dataSuccess = new EventEmitter();

  ngOnInit() {

    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.onpremAccess = this.permissions.checkOnPremAdminPermission();
  }

  updateComponent() {
    this.setGraphParams();
    this.getData();
  }

  getData() {
    this.resetPage();
    const url = environment.onPremGraph.url;
    const queryParam = {
      ag: this.selectedAssetGroup
    };
    const payload = {};
    const method = environment.onPremGraph.method;

    this.onpremSubscription = this.commonResponseService.getData( url, method, payload, queryParam).subscribe(
      response => {

        try {
            this.dataResponse = response;
            if (this.dataResponse.projectionByWeek.length === 0) {
              this.errorOccurred.emit();
              this.dataState = -1;
            } else {
              this.dataSuccess.emit();
              this.dataState = 1;
              this.processData(this.dataResponse.projectionByWeek);
            }

        } catch (e) {
          this.errorOccurred.emit();
          this.dataState = -1;
          this.logger.log('error', e);
        }
    },
    error => {
      this.errorOccurred.emit();
      this.dataState = -1;
      this.logger.log('error', error);
    });

  }

  resetPage() {
    if ( this.onpremSubscription ) {
      this.onpremSubscription.unsubscribe();
    }
  this.dataState = 0;
    this.dataResponse = [];
    this.percentToday = -1;
    this.maxVal = [0, 0];
    this.axisValues = {
      'y0': [],
      'y1': []
    };
  }

  setGraphParams() {
    try {
      if (document.getElementById('onpremGraph')) {
        document.getElementById('onpremGraph').innerHTML = '';
        document.getElementById('onpremGraph').style.width = document.getElementById('onpremContainer').clientWidth + 'px';
        document.getElementById('onpremGraph').style.height = document.getElementById('onpremContainer').clientHeight + 'px';
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  processData(data) {

    const firstDay = (new Date(data[0].date)).getTime();
    const lastDay = (new Date(data[data.length - 1].date)).getTime() + 7 * 24 * 60 * 60 * 1000;
    const today = (new Date()).getTime();
    this.percentToday = ((today - firstDay) * 100) / (lastDay - firstDay);

    const graphThis = this;
    setTimeout(() => {
      const margin = {top: 40, right: 77, bottom: 45, left: 77},
      width = document.getElementById('onpremContainer').clientWidth - margin.left - margin.right,
      height = document.getElementById('onpremContainer').clientHeight - margin.top - margin.bottom;
      const barWidth = width / data.length;
      let xDisplacement = 20;
      let difference = 17.5;
      if (Math.ceil(barWidth / 5) < 15) {
        xDisplacement = Math.ceil(barWidth / 5) + 3;
        difference = 1.5 + Math.ceil(barWidth / 5);
      }

      const strData = JSON.stringify(data);
      const newData = JSON.parse(strData);
      const zeroObj = {projected: '0', week: '0', totalPatched: '0', patched: '0', totalProjected: '0'};
      newData.unshift(zeroObj);

      // parse the date / time
      // var parseTime = d3TimeFormat.timeParse('%d-%b-%y');

      // set the ranges of each axis
      const x = d3Scale.scalePoint().range([0, width]);
      x.domain(newData.map((d) => d['week']));

      const xScale = d3Scale.scaleBand().range([0, width]);

      xScale.domain(data.map((d) => d['week']));
            // .rangeRound([0, width], .3);
      const y0 = d3Scale.scaleLinear().range([height, 0]);
      const y1 = d3Scale.scaleLinear().range([height, 0]);

      // define the 1st line
      const valueline = d3Shape.line()
          .x(function(d) { return x(d['week']); })
          .y(function(d) {
           graphThis.findMaxVal(d);
           return y1(d['totalPatched']); })
          .curve(d3Shape.curveMonotoneX);

      // define the 2nd line
      const valueline2 = d3Shape.line()
          .x(function(d) { return x(d['week']); })
          .y(function(d) { return y1(d['totalProjected']); })
          .curve(d3Shape.curveMonotoneX);

      // set dimentions of svg
      const svg = d3.select('#onpremGraph')
          .attr('width', width + margin.left + margin.right)
          .attr('height', height + margin.top + margin.bottom)
          .append('g')
          .attr('transform',
              'translate(' + margin.left + ',' + margin.top + ')');

      // format the data
      data.forEach(function(d) {
          d['week'] = +d['week'];
          d['totalPatched'] = +d['totalPatched'];
          d['totalProjected'] = +d['totalProjected'];
      });

      // Scale the range of the data for each axes
      y0.domain([0, d3Array.max(data, function(d) {return Math.max(d['patched'], d['projected'], 5); })]);
      y1.domain([0, d3Array.max(newData, function(d) {return Math.max(d['totalPatched'], d['totalProjected'], 5); })]);

      // Add the X Bottom Band(Bar) Axis
      svg.append('g')
          .attr('transform', 'translate(0,' + height + ')')
          .attr('class', 'bottomAxis')
          .call(d3Axis.axisBottom(x))
          .call(make_x_gridlines()
            .tickSize(-height)
      );

      // Graph Bars
      const sets = svg.selectAll('.set')
        .data(data)
        .enter()
        .append('g')
          .attr('class', 'set')
          .attr('transform', function(d, i) {
               return 'translate(' + (xScale(d['week']) - difference + barWidth / 2)  + ',0)';
           })  ;

      // bar 2
      sets.append('rect')
        .attr('class', 'local')
        .attr('shape-rendering', 'crispEdges')
          .transition()
          .delay(700)
          .duration(600)
        .attr('width', Math.min(Math.ceil(barWidth / 5), 15))
        .attr('y', function(d) {
          return y0(d['projected']);
        })
          .attr('x', xDisplacement)
          .attr('height', function(d){
              return height - y0(d['projected']);
          })
        .attr('fill', graphThis.lightBlue)
        ;

      // bar 1
      sets.append('rect')
        .attr('class', 'global')
        .attr('shape-rendering', 'crispEdges')
          .transition()
          .delay(500)
          .duration(600)
        .attr('width', Math.min(Math.ceil(barWidth / 5), 15))
        .attr('y', function(d) {
          return y0(d['patched']);
        })
          .attr('height', function(d){
              return height - y0(d['patched']);
          })
        .attr('fill', graphThis.darkBlue);

      // Add the valueline1 path.
      svg.append('path')
          .data([newData])
          .attr('class', 'line')
          .style('stroke', graphThis.lightGreen)
          .attr('d', valueline)
          ;

      // Add the valueline2 path.
      svg.append('path')
          .data([newData])
          .attr('class', 'line line-dashed')
          .style('stroke', graphThis.lightGreen)
          .attr('d', valueline2)
          ;

      // gridlines in x axis function
      function make_x_gridlines() {
          return d3Axis.axisBottom(x)
              .ticks(5);
      }

    }, 10);
  }

  findMaxVal(obj) {
    this.maxVal[0] = Math.max(this.maxVal[0], obj['projected'], obj['patched']);
    this.maxVal[1] = Math.max(this.maxVal[1], obj['totalProjected'], obj['totalPatched']);
    for ( let i = 5; i > 0 ; i--) {
      this.axisValues['y0'].push(Math.ceil(this.maxVal[0] / 5 * i));
      this.axisValues['y1'].push(Math.ceil(this.maxVal[1] / 5 * i ));
    }
    this.axisValues['y0'] = this.axisValues['y0'].slice(this.axisValues['y0'].length - 5);
    this.axisValues['y1'] = this.axisValues['y1'].slice(this.axisValues['y1'].length - 5);
    if (this.axisValues['y0'][0] < 5) {
      this.axisValues['y0'] = [5, 4, 3, 2, 1];
    }
    if (this.axisValues['y1'][0] < 5) {
      this.axisValues['y1'] = [5, 4, 3, 2, 1];
    }
  }

  onResize() {
    try {
      this.setGraphParams();
      if (this.dataResponse.projectionByWeek.length) {
        this.processData(this.dataResponse.projectionByWeek);
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  redirectTo(page) {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
    if (page === 'updateAssets') {
      this.router.navigate(['../../assets/update-assets'], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
    } else if (page === 'patchingProjections') {
      this.router.navigate(['../patching-projections'], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
    }
  }

  ngOnDestroy() {
    if ( this.onpremSubscription ) {
      this.onpremSubscription.unsubscribe();
    }
    this.subscriptionToAssetGroup.unsubscribe();
  }

}
