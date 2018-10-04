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


import { Component, OnInit, Input, NgZone, ViewChild, ElementRef, OnChanges, SimpleChanges } from '@angular/core';
import { AutorefreshService } from '../../services/autorefresh.service';

import * as d3 from 'd3-selection';

import * as d3Shape from 'd3-shape';
import * as d3Scale from 'd3-scale';
import * as d3Array from 'd3-array';
import * as d3Axis from 'd3-axis';

import { UtilsService } from '../../../shared/services/utils.service';


@Component({
  selector: 'app-patching-graph',
  templateUrl: './patching-graph.component.html',
  styleUrls: ['./patching-graph.component.css'],
  providers: [ AutorefreshService ]
})

export class PatchingGraphComponent implements OnInit, OnChanges {

  @Input() graphWidth: any;
  @Input() position: any;
  @Input() axis: any;
  @Input() graphHeight: any;
  @Input() graphLinesData: any;
  @Input() yAxisLabel = '';
  @Input() xAxisLabel = '';
  @Input() showLegend = true;
  @Input() amiavail_date: any;
  @Input() end_date: any;
  @Input() internal_target: any;
  @Input() lastDate: any;
  @Input() idUnique: any;
  @Input() today: any;

  @ViewChild('graphContainer') graphContainer: ElementRef;

  private margin = {top: 15, right: 20, bottom: 30, left: 60};

  public lineColors = ['#afd9f9', '#ed0295', '#00b946', '#289cf7', '#ED0295'];

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
  private x: any;
  private y: any;
  private svg: any;
  private line: d3Shape.Line<[number, number]>;
  private area: any;
  private areaLower: any;
  private combinedData: any = [];
  private data: any;
  private focus: any;
  private graphData: any = [];

  // Variables for auto-refresh
  private durationParams: any;
  private autoRefresh: boolean;

  private dotsDate: any = [];
  private nonZeroValues: any = [];

  private todayDate: any;


  constructor(private ngZone: NgZone,
              private autorefreshService: AutorefreshService,
              private utils: UtilsService) {

    // Variables initialized for auto-refresh
                this.durationParams = this.autorefreshService.getDuration();
                this.durationParams = parseInt(this.durationParams, 10);
                this.autoRefresh = this.autorefreshService.autoRefresh;

                window.onresize = (e) => {
                    // ngZone.run will help to run change detection
                    this.ngZone.run(() => {
                      this.graphWidth = parseInt(window.getComputedStyle(this.graphContainer.nativeElement, null).getPropertyValue('width'), 10);

                      // Re-plot the graph for resized window
                      this.resizeGraph();
                    });
                };

  }

  plotGraph() {
    try {
      this.initSvg();
      this.initComponents();
      this.computeLowerAndHigherLines();
      this.formatDataForArea();
      this.drawAxisAndGrid();
      this.drawLine();
      this.addMarkerAMI();
      this.addMarkerInternal();
      this.addMarkerExternal();
      if (this.today !== false) {
        this.addMarkerToday();
      }
    } catch (error) {
    }

  }

  init() {
    try {
      if (this.graphLinesData) {
        this.nonZeroValues = [];
        this.graphData = this.graphLinesData;
        let dotsDateVal = [];
        for ( let i = 1; i <= this.graphData[0][`values`].length; i++) {
          if (this.graphData[0][`values`][6 * i]) {
            dotsDateVal.push(this.graphData[0][`values`][i * 6]);
          }
        }
        dotsDateVal = dotsDateVal.splice(0, dotsDateVal.length);
        this.dotsDate = dotsDateVal;

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
        if (this.nonZeroValues.length > 3) {
          this.nonZeroValues = this.nonZeroValues.splice(0, 3);
        }

        // Set dimensions for the graph and timeline axis
        this.width = this.graphWidth - this.margin.left - this.margin.right ;
        if (this.width < 1) {
          this.width = 1;
        }
        this.timeLineWidth = this.width * 1;

        if (this.graphHeight === undefined) {
          this.graphHeight = 280;
          this.height = this.graphHeight - this.margin.top - this.margin.bottom - 70;
        } else {
          this.graphHeight = 180;
          this.height = this.graphHeight - this.margin.top - this.margin.bottom - 70;
        }

        this.graphData = this.graphLinesData;

        // To remove the graph content if its present before re-plotting
        this.removeGraphSvg();

        // Plot the graph and do all associated processes
        this.plotGraph();

      }
    } catch (error) {
    }

  }

  ngOnChanges(changes: SimpleChanges) {
    try {

      const graphDataChange = changes['graphLinesData'];
      const graphWidth = changes['graphWidth'];

      if (graphDataChange && !graphWidth) {
        const cur  = JSON.stringify(graphDataChange.currentValue);
        const prev = JSON.stringify(graphDataChange.previousValue);
        if ((cur !== prev) && (this.graphLinesData)) {
          if (graphWidth) {
            const prevWidth = JSON.stringify(graphWidth.previousValue);
            const currWidth = JSON.stringify(graphWidth.currentValue);
              if (prevWidth !== currWidth) {
                  this.graphWidth = currWidth;
              }
            }
          this.init();
        }
      }

      if (!graphDataChange && graphWidth) {
        const prevWidth = JSON.stringify(graphWidth.previousValue);
        const currWidth = JSON.stringify(graphWidth.currentValue);
          if (prevWidth !== currWidth) {
              this.graphWidth = currWidth;
              this.resizeGraph();
          }
      }

    } catch (error) {

    }

  }

  resizeGraph() {
    if (this.graphLinesData) {

        // Reset the dimensions
        this.width = this.graphWidth - this.margin.left - this.margin.right ;
        if (this.width < 1) {
          this.width = 1;
        }
        this.timeLineWidth = this.width * 1;
        if (this.graphHeight === undefined) {
          this.graphHeight = 280;
          this.height = this.graphHeight - this.margin.top - this.margin.bottom - 70;
        } else {
          this.graphHeight = 180;
          this.height = this.graphHeight - this.margin.top - this.margin.bottom - 70;
        }

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

    d3.select(uniqueId).select('svg').attr('width', this.graphWidth);
    d3.select(uniqueId).select('svg').attr('height', this.graphHeight);

    if (this.position === true) {

      this.svg = d3.select(uniqueId).select('svg')
                 .append('g')
                 .attr('transform', 'translate(' + this.margin.left + ',' + (-this.margin.bottom) + ')');

    } else {

      this.svg = d3.select(uniqueId).select('svg')
                 .append('g')
                 .attr('transform', 'translate(' + 30 + ',' + (-this.margin.bottom - 30) + ')');
    }

                 this.graphHeight = undefined;
  }

  private initComponents() {

    this.data = this.graphData.map((v) => v.values.map((z) => z.date ))[0];

    this.x = d3Scale.scaleTime().range([0, this.width]);
    this.y = d3Scale.scaleLinear().range([this.height, 0]);

    // To get the starting and ending dates within which data value is > 0
    // this.x.domain(d3Array.extent(this.data, (d: Date) => d ));


    this.x.domain(d3Array.extent(this.data, (d: Date) => d ));
    // Note : You can add '.nice()' function at the end of this.x.domain() to have evenly spaced ticks with starting and
    //        ending point included


    // this.x2.domain(d3Array.extent(this.data, (d: Date) => d ));

    this.y.domain([
      0,
      d3Array.max(this.graphData, function(c) { return d3Array.max(c[`values`], function(d) { return d[`value`] * 1.1; }); })

    ]);
    // Note : You can add '.nice()' function at the end of this.x.domain() to have evenly spaced ticks with starting and
    //        ending point included

  }


  private computeLowerAndHigherLines() {
    // Computing the Lowest / Highest line and their indices respectively
    this.lowerLine = this.graphData[0];
    for ( let i = 0; i < this.graphData.length - 1; i++) {
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

    if (this.lowerLine[`values`].length > this.higherLine[`values`].length) {
      this.smallerLine = this.higherLine;
      this.longerLine = this.lowerLine;
    } else {
      this.smallerLine = this.lowerLine;
      this.longerLine = this.higherLine;
    }

  }

  private formatDataForArea() {
    // Merging the data of top and bottom lines to supply to plot shaded area
    // between top and bottom graph lines
    this.combinedData = [];
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

    const nonZeroArray = [];

    for ( let k = 0; k < this.combinedData.length; k++ ) {
        if (this.combinedData[k].y0 !== -1 ) {
          nonZeroArray.push(this.combinedData[k]);
        }
    }
    this.combinedData = nonZeroArray;

  }

  private drawAxisAndGrid() {

    // Horizontal Grid Lines
    this.svg.append('g')
          .attr('class', 'grid horizontal')
          .attr('transform', 'translate(0,' + (2 * this.margin.top + 40) + ')')
          .call(d3Axis.axisLeft(this.y)
              .ticks(3)
              .tickSize(-this.width)
              .tickFormat(d => '')
          );
    this.focus = this.svg.append('g')
        .attr('class', 'focus')
        .attr('transform', 'translate(0,' + (2 * this.margin.top + 40) + ')');

    this.svg.append('defs').append('clipPath')
        .attr('id', 'clip')
        .append('rect')
        .attr('width', 0)
        .attr('height', this.height);

    // Main Graph y-axis and associated Label
    if (this.yAxisLabel !== undefined) {

      if (this.axis === true) {

        this.focus.append('g')
          .attr('class', 'axis axis--y')
          .attr('stroke-width', '0')
          .attr('stroke', '#fff')
          // .call(d3Axis.axisLeft(this.y).ticks(3).tickFormat(d3Format.format('.0s')))
          .call(d3Axis.axisLeft(this.y).ticks(4).tickFormat(d => this.abbreviateNumber(d)))
          .append('text')
          .attr('class', 'axis-title')
          .attr('transform', 'rotate(-90)')
          .attr('y', -47)
          .attr('x', -40)
          .attr('dy', '.71em')
          .attr('stroke-width', '0.5')
          .attr('fill', '#2c2e3d')
          .attr('stroke', '#2c2e3d')
          .style('text-anchor', 'end')
          .text(this.yAxisLabel);

      }

    }

  }

  abbreviateNumber(number) {

      if (number < '99') {
        return number;
      } else {
        number = parseInt(number, 10);
        number = number > 1000000 ? (number / 1000000) + 'M' : (number > 1000 ? (number / 1000) + 'K' : number);
        return number;
      }

  }

  private drawLine() {

    d3.selectAll('.ticks');
    // Line Graphs

    this.line = d3Shape.line()
                       .x( (d: any) => this.x(d.date) )
                       .y( (d: any) => this.y(d.value) )
                       .curve(d3Shape.curveMonotoneX);  // changing the shape of graph from curveBasis to curveMonotoneX
    for ( let i = 0; i < this.nonZeroValues.length; i++ ) {
      this.focus.append('path')
        .datum(this.nonZeroValues[i].values)
        .attr('clip-path', 'url(#clip)')
        .transition()
        .duration(5000)
        .attr('class', 'line line' + `${ i + 1 }`)
        .attr('fill', 'none')
        .attr('stroke-width', '1.5px')
        .attr('stroke', this.lineColors[i])
        .attr('d', this.line);
    }

    this.areaLower = d3Shape.area()
            .x((d: any) => this.x(d.date))
            .y0(this.height)
            .y1((d: any) =>  this.y(d.value))
            .curve(d3Shape.curveMonotoneX);  // changing the shape of graph from curveBasis to curveMonotoneX

    this.area = d3Shape.area()
            .x0((d: any) =>  this.x(d.x0))
            .x1((d: any) =>  this.x(d.x1))
            .y0( (d: any) =>  this.y(d.y1))
            .y1( (d: any) => this.y(d.y0))
            .curve(d3Shape.curveMonotoneX);  // changing the shape of graph from curveBasis to curveMonotoneX

    this.focus.append('path')
            .datum(this.combinedData)
            .attr('class', 'areaPatch')
            .attr('fill', '#fcd8ef')
            .attr('stroke-width', '0.5')
            .attr('stroke', '#2c2e3d')
            .attr('d', this.area);

    this.focus.append('path')
            .datum(this.nonZeroValues[0]['values'])
            .attr('class', 'areaPatchLower')
            .attr('fill', '#afd9f9')
            .attr('stroke-width', '0.5')
            .attr('stroke', '#2c2e3d')
            .attr('d', this.areaLower);


    this.svg.select('#clip rect')
    .transition()
    .duration(2000)
    .attr('width', this.width);

  }

  private addMarkerAMI() {

    if ((this.amiavail_date !== undefined) && (this.amiavail_date !== '')) {

      const dateStamp = new Date(this.amiavail_date);
      const utcTime = dateStamp.getTime() - (dateStamp.getTimezoneOffset() * 60 * 1000);
      const date2 = new Date(utcTime);

      const radius = 32,
        xPos = this.x(date2) - radius - 3,
        yPosStart = 100 - radius - 3;

        const markerG = this.svg.append('g')
        .attr('class', 'marker ')
        .attr('transform', 'translate(' + xPos + ', ' + yPosStart + ')')
        .attr('opacity', 1);

      markerG.append('path')
        .attr('d', 'M' + radius + ',' + (65 - yPosStart) + 'L' + radius + ',' + (yPosStart + 105));

      markerG.append('rect')
        .attr('class', 'marker-bg')
        .attr('height', '2.5rem')
        .attr('width', '5rem')
        .attr('x', 32)
        .attr('y', -28);

      markerG.append('text')
        .attr('class', 'marker-bg-text1')
        .attr('x', 42)
        .attr('y', -16)
        .text('AMI Avail');

      markerG.append('text')
        .attr('class', 'marker-bg-text2')
        .attr('x', 41)
        .attr('y', -2)
        .text(this.amiavail_date);

    }

  }

  private addMarkerInternal() {

    if ((this.internal_target !== undefined) && (this.internal_target !== '')) {

      const dateStamp = new Date(this.internal_target);
      const utcTime = dateStamp.getTime() - (dateStamp.getTimezoneOffset() * 60 * 1000);
      const date2 = new Date(utcTime);

      const radius = 32,
        xPos = this.x(date2) - radius - 3,
        yPosStart = 100 - radius - 3;

        const markerG = this.svg.append('g')
        .attr('class', 'marker ')
        .attr('transform', 'translate(' + xPos + ', ' + yPosStart + ')')
        .attr('opacity', 1);

      markerG.append('path')
        .attr('d', 'M' + radius + ',' + (65 - yPosStart) + 'L' + radius + ',' + (yPosStart + 105));

      markerG.append('rect')
        .attr('class', 'marker-bg')
        .attr('height', '2.5rem')
        .attr('width', '5rem')
        .attr('x', 32)
        .attr('y', -28);

      markerG.append('text')
        .attr('class', 'marker-bg-text1')
        .attr('x', 42)
        .attr('y', -16)
        .text('Int. Target');

      markerG.append('text')
        .attr('class', 'marker-bg-text2')
        .attr('x', 43)
        .attr('y', -2)
        .text(this.internal_target);

    }

  }

  private addMarkerExternal() {
    if ((this.end_date !== '') && (this.end_date !== undefined)) {
      const dateStamp = new Date(this.end_date);
      const utcTime = dateStamp.getTime(); // - (dateStamp.getTimezoneOffset() * 60 * 1000);
      const date2 = new Date(utcTime);
      let xPos;
      const radius = 32,
        yPosStart = 100 - radius - 3;

      /* This calculation nor required. */

      /*if ((dateStamp.getTimezoneOffset() === 480)) {
        xPos = this.x(date2) - radius + 1;
      } else {
        xPos = this.x(date2) - radius - 3;
      }*/

      xPos = this.x(date2) - radius;

      const markerG = this.svg.append('g')
        .attr('class', 'marker ')
        .attr('transform', 'translate(' + xPos + ', ' + yPosStart + ')')
        .attr('opacity', 1);

      markerG.append('path')
        .attr('d', 'M' + radius + ',' + (65 - yPosStart) + 'L' + radius + ',' + (yPosStart + 105));

      markerG.append('rect')
        .attr('class', 'marker-bg')
        .attr('height', '30px')
        .attr('width', '60px')
        .attr('x', -28)
        .attr('y', -28);

      markerG.append('text')
        .attr('class', 'marker-bg-text1')
        .attr('x', -20)
        .attr('y', -16)
        .text('Ext. Target');

      markerG.append('text')
        .attr('class', 'marker-bg-text2')
        .attr('x', -19)
        .attr('y', -2)
        .text(this.end_date);

    }

  }


  private addMarkerToday() {

    const firstDate = this.graphData[0][`values`][0][`date`];
    const date1 = firstDate;
    let timeDiff;
    let diffDays;
    let utcTime;
    let date2;
    if (this.lastDate !== undefined) {
      this.todayDate = new Date(this.lastDate);
      timeDiff = Math.abs(this.todayDate.getTime() - date1.getTime());
      diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24));
    } else {
      this.todayDate = new Date();
      utcTime = this.todayDate.getTime() - (this.todayDate.getTimezoneOffset() * 60 * 1000);
      date2 = new Date(utcTime);
      date2.setUTCHours(0, 0, 0, 0);

      timeDiff = Math.abs(date2.getTime() - date1.getTime());
      diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24));
    }

    if (this.graphData[0][`values`][diffDays] !== undefined) {
      const lengthPatched = this.graphData[0][`values`][diffDays][`value`];
      const radius = 32,
        xPos = this.x(new Date(this.todayDate)) - radius,
        yPosStart = 100 - radius - 3;

        const markerG = this.svg.append('g')
        .attr('class', 'markerToday')
        .attr('transform', 'translate(' + xPos + ', ' + yPosStart + ')')
        .attr('opacity', 1);

      markerG.append('path')
        .attr('d', 'M' + radius + ',' + (85 - yPosStart) + 'L' + radius + ',' + (yPosStart + 105));

      markerG.append('circle')
        .attr('class', 'marker-bg-circle')
        .attr('cx', 32)
        .attr('cy', this.y(lengthPatched))
        .attr('r', 5);

      markerG.append('text')
        .attr('class', 'marker-today')
        .attr('x', 19)
        .attr('y', 15)
        .text('TODAY');
      }
  }

}
