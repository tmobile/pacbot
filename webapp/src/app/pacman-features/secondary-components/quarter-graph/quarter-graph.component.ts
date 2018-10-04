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

import { Component, OnInit, Input, NgZone, ViewChild, ElementRef, OnChanges, SimpleChanges, OnDestroy, AfterViewInit } from '@angular/core';

import { AutorefreshService } from '../../services/autorefresh.service';
import * as d3 from 'd3-selection';
import * as d3Shape from 'd3-shape';
import * as d3Scale from 'd3-scale';
import * as d3Array from 'd3-array';
import * as d3Axis from 'd3-axis';

@Component({
  selector: 'app-quarter-graph',
  templateUrl: './quarter-graph.component.html',
  styleUrls: ['./quarter-graph.component.css'],
  providers: [AutorefreshService]
})

export class QuarterGraphComponent implements OnInit, OnChanges, OnDestroy,  AfterViewInit {

  @Input() graphWidth: any;
  @Input() graphHeight: any = 90;
  @Input() xAxisValues: any;
  @Input() smoothEdge: any;
  @Input() yAxisLabel: any;
  @Input() axisUnit: any;
  @Input() dataResponse: any;
  @Input() verticalLines: any;
  @Input() idUnique: any;
  @Input() colorSet: any = ['#645ec5', '#26ba9d', '#289cf7'];
  @Input() multipleData: any;
  @Input() targetType: any;
  @Input() yCoordinates: any;
  @Input() colorSetLegends: any;
  durationParams: any;
  autoRefresh: boolean;

  @ViewChild('widgetQuarter') widgetContainer: ElementRef;

  private margin = {top: 15, right: 20, bottom: 30, left: 60};

  // Lowest and Highest Line in the graph
  private lowerLine: any;
  private lowerLineIndex: any = 0;
  private higherLine: any;
  private higherLineIndex: any = 1;

  // Smaller and longer line (to help plot the area between the bottom and top lines)
  private smallerLine: any;
  private longerLine: any;

  private width: number;
  private timeLineWidth: number;
  private height: number;
  private areaLower: any;
  private x: any;
  private y: any;
  private svg: any;
  private line: d3Shape.Line<[number, number]>;
  private combinedData: any = [];
  private data: any;
  private focus: any;
  bisectDate: any;
  focusContent: any;
  i: any;
  legendHover: any = [];
  searchAnObjectFromArray: any;
  tickValues: any;
  curveBasis: any;
  tickUnit: any;
  showdata = false;
  wholeData: any = {};
  legendsvalue: any = [];
  interval: any;
  nonZeroValues: any = [];

  private graphData: any = [];
  private error = false;
  private dataLoaded = false;

  private autorefreshInterval;

  constructor(private ngZone: NgZone,
              private autorefreshService: AutorefreshService) {

    window.onresize = (e) => {
        // ngZone.run will help to run change detection
        this.ngZone.run(() => {

          this.graphWidth = parseInt(window.getComputedStyle(this.widgetContainer.nativeElement, null).getPropertyValue('width'), 10);
          this.resizeGraph();
        });
    };

    this.durationParams = this.autorefreshService.getDuration();
    this.durationParams = parseInt(this.durationParams, 10);
    this.autoRefresh = this.autorefreshService.autoRefresh;
  }

  ngAfterViewInit() {

    const afterLoad = this;
    if (this.autoRefresh !== undefined) {
      if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {

        this.autorefreshInterval = setInterval(function() {
          afterLoad.ngOnInit();
        }, this.durationParams);
      }
    }
  }

  plotGraph() {
    const idValue = this.idUnique;
    const uniqueId = document.getElementById(idValue);
    if (uniqueId != null) {
      this.initSvg();
      this.initComponents();
      this.computeLowerAndHigherLines();
      this.formatDataForArea();
      this.drawAxisAndGrid();
      this.drawLine();
    }

  }

  init() {
        if (this.dataResponse ) {
            this.nonZeroValues = [];
            this.dataLoaded = true;
            this.error = false;

            for ( let i = 0 ; i < this.dataResponse.length; i++) {
              for ( let q = 0; q < this.dataResponse[i].values.length; q++) {
                if (this.dataResponse[i].values[q].value === -1) {
                  this.dataResponse[i].values[q].value = 0;
                }
              }
            }

            this.graphData = this.dataResponse;

            for ( let j = 0; j < this.graphData.length; j++) {
              const nonZeroArray = [];
              for ( let t = 0; t < this.graphData[j].values.length; t++) {
                if (this.graphData[j].values[t].value !== -1) {
                  nonZeroArray.push(this.graphData[j].values[t]);
                }
              }
              const objData = {
                'values': nonZeroArray
              };

              this.nonZeroValues.push(objData);
            }

            this.nonZeroValues = this.nonZeroValues.splice(0, 1);
            this.graphData = this.nonZeroValues;

            if (this.graphWidth > 262) {
              this.graphWidth = 200;
            }

            this.width = this.graphWidth - this.margin.left - this.margin.right ;
            if (this.width < 1) {
              this.width = 1;
            }
            this.timeLineWidth = this.width * 1;
            this.height = this.graphHeight - this.margin.top - this.margin.bottom;

              // To remove the graph content if its present before re-plotting
            this.removeGraphSvg();


            // Plot the graph and do all associated processes
            this.plotGraph();


        }
  }

  ngOnChanges(changes: SimpleChanges) {
    const graphDataChange = changes['dataResponse'];
    if (graphDataChange) {
      const cur  = JSON.stringify(graphDataChange.currentValue);
      const prev = JSON.stringify(graphDataChange.previousValue);
      if ((cur !== prev) && (this.dataResponse)) {
        this.init();
      }
    }
  }

  resizeGraph() {
    if (this.dataResponse) {

        // Reset the dimensions
        if (this.graphWidth > 262) {
              this.graphWidth = 200;
        }

        if (this.graphWidth < 1) {
          this.graphWidth = 40;
        }
        this.width = this.graphWidth - this.margin.left - this.margin.right ;
        if (this.width < 1 ) {
          this.width = 1;
        }
        this.timeLineWidth = this.width * 1;
        this.height = this.graphHeight - this.margin.top - this.margin.bottom;

        // To remove the graph content if its present before re-plotting
        this.removeGraphSvg();

        // Plot the graph and do all associated processes
        this.plotGraph();
    }
  }

  removeGraphSvg() {

    const idValue = this.idUnique;
    const uniqueId = document.getElementById(idValue);


    if (d3.select(uniqueId).select('g') !== undefined) {
      d3.select(uniqueId).select('g').remove();
      d3.select(uniqueId).append('g');
    }
  }

  ngOnInit() {
    this.init();
  }

  private initSvg() {

    const idValue = this.idUnique;
    const uniqueId = document.getElementById(idValue);
    if (this.graphWidth < 1) {
      this.graphWidth = 40;
    }
    d3.select(uniqueId).select('svg').attr('width', this.graphWidth);
    d3.select(uniqueId).select('svg').attr('height', this.graphHeight);

    if (this.graphWidth > 262) {

      this.svg = d3.select(uniqueId)
                 .select('svg')
                 .append('g')
                 .attr('transform', 'translate(' + 30 + ',' + (-this.margin.bottom) + ')');

    } else {

      this.svg = d3.select(uniqueId)
                 .select('svg')
                 .append('g')
                 .attr('transform', 'translate(' + 40 + ',' + (-this.margin.bottom) + ')');

    }

  }

  private initComponents() {
    this.data = this.graphData.map((v) => v.values.map((z) => z.date ))[0];

    this.x = d3Scale.scaleTime().range([0, this.width]);
    this.y = d3Scale.scaleLinear().range([this.height, 0]);

    this.x.domain(d3Array.extent(this.data, (d: Date) => d ));
    this.y.domain([
      0,
      d3Array.max(this.graphData, function(c) { return d3Array.max(c[`values`], function(d) { return d[`value`]; }); })
    ]);

    this.svg.append('defs').append('clipPath')
        .attr('id', 'clip')
        .append('rect')
        .attr('width', 0)
        .attr('height', this.height);


      this.focus = this.svg.append('g')
        .attr('class', 'focus')
        .attr('transform', 'translate(0,' + ( 2 *  this.margin.top + 40) + ')');

  }

  private computeLowerAndHigherLines() {
    // Computing the Lowest / Highest line and their indices respectively
    this.lowerLine = this.graphData[0];
    for ( let i = 0 ; i < this.graphData.length; i++) {
      if (this.graphData[i][`values`].length < this.lowerLineIndex) {
        this.lowerLineIndex = i;
      } else {
        if (this.graphData[i][`values`].length > this.higherLineIndex) {
          this.higherLineIndex = i;
        }
      }
    }
    this.lowerLine = this.graphData[this.lowerLineIndex];
    this.higherLine = this.graphData[this.higherLineIndex];

    if ((this.lowerLine !== undefined) && (this.higherLine !== undefined)) {
      if (this.lowerLine[`values`].length > this.higherLine[`values`].length) {
        this.smallerLine = this.higherLine;
        this.longerLine = this.lowerLine;
      } else {
        this.smallerLine = this.lowerLine;
        this.longerLine = this.higherLine;
      }
    }
  }

  private formatDataForArea() {
    // Merging the data of top and bottom lines to supply to plot shaded area
    // between top and bottom graph lines
    this.combinedData = [];
    if (this.smallerLine !== undefined) {
        for ( let i = 0; i < this.smallerLine[`values`].length; i++) {

          const lowerX = new Date(this.smallerLine[`values`][i].date);
          let lowerY = 0;
          let higherX = 0;
          let higherY = 0;

          // Forming mm/dd/yyyy of both higher and lower line data points as we cannot directly compare both,
          // as time may change in the data point for any given day

          const smallerLineDate = new Date(this.smallerLine[`values`][i].date);
          const smallerLineFormattedDate = smallerLineDate.getUTCMonth() + '/' +  smallerLineDate.getUTCDate() + '/' + smallerLineDate.getUTCFullYear();


          for ( let j = 0; j < this.longerLine[`values`].length; j++) {

            const longerLineDate = new Date(this.longerLine[`values`][j].date);
            const longerLineFormattedDate = longerLineDate.getUTCMonth() + '/' +  longerLineDate.getUTCDate() + '/' + longerLineDate.getUTCFullYear();

            if (longerLineFormattedDate === smallerLineFormattedDate) {
              higherX = this.longerLine[`values`][j].date;
              this.longerLine[`values`][j].value === 0 ? higherY = 1 : higherY = this.longerLine[`values`][j].value;
              this.smallerLine[`values`][i].value === 0 ? lowerY = 1 : lowerY = this.smallerLine[`values`][i].value;
              const obj = {
                'x0': higherX,
                'x1': lowerX,
                'y0': higherY,
                'y1': lowerY
              };
              this.combinedData.push(obj);
              break;
            }
          }
      }
    }
  }

  private drawAxisAndGrid() {

        // Horizontal Grid Lines
        this.svg.append('g')
              .attr('class', 'grid horizontal multiline')
              .attr('transform', 'translate(0,' + (2 * this.margin.top + 40) + ')')
              .call(d3Axis.axisLeft(this.y)
                  .ticks(3)
                  .tickSize(-this.width)
                  .tickFormat(d => '')
              );

  }

  private drawLine() {

  this.line = d3Shape.line()
    .x( (d: any) => this.x(d.date) )
    .y( (d: any) => this.y(d.value) )
    .curve(d3Shape.curveBasis);

    // Line Graphs

    for ( let i = 0; i < this.graphData.length; i++) {
      this.focus.append('path')
        .datum(this.graphData[i].values)
        .attr('clip-path', 'url(#clip)')
        .transition()
        .duration(2000)
        .attr('class', 'line line' + `${ i + 1 }`)
        .attr('fill', 'none')
        .attr('stroke-width', '1.5px')
        .attr('stroke', this.colorSet[i])
        .attr('d', this.line);
    }


    this.areaLower = d3Shape.area()
            .x((d: any) => this.x(d.date))
            .y0(this.height)
            .y1((d: any) =>  this.y(d.value))
            .curve(d3Shape.curveBasis);

      // Draw area between the top and bottom lines
      this.focus.append('path')
            .datum(this.graphData[0]['values'])
            .attr('class', 'areaPatchLower')
            .attr('fill', '#afd9f9')
            .attr('stroke-width', '0.5')
            .attr('stroke', '#2c2e3d')
            .attr('d', this.areaLower);

    d3.selectAll('.ticks');

    this.svg.select('#clip rect')
    .transition()
    .duration(2000)
    .attr('width', this.width);
  }

  ngOnDestroy() {
    try {
      clearInterval(this.autorefreshInterval);
    } catch (error) {
    }
  }

}
