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


import { Component, OnInit, Input, NgZone, ViewChild, ElementRef, OnChanges } from '@angular/core';

import * as d3 from 'd3-selection';
import * as d3Shape from 'd3-shape';
import * as d3Scale from 'd3-scale';
import * as d3Array from 'd3-array';
import * as d3Axis from 'd3-axis';
import * as d3TimeFormat from 'd3-time-format';

@Component({
  selector: 'app-multiline-chart',
  templateUrl: './multiline-chart.component.html',
  styleUrls: ['./multiline-chart.component.css'],
  providers: []
})

export class MultilineChartComponent implements OnInit, OnChanges {

  @Input() graphWidth: any;
  @Input() graphHeight: any;
  @Input() xAxisValues: any;
  @Input() smoothEdge: any;
  @Input() yAxisLabel: any;
  @Input() axisUnit: any;
  @Input() dataResponse: any;
  @Input() verticalLines: any;
  @Input() idUnique: any;
  @Input() colorSet: any;
  @Input() hover: any;
  @Input() multipleData: any;
  @Input() targetType: any;
  @Input() yCoordinates: any;
  @Input() colorSetLegends: any;
  @Input() translateChange: any;
  @Input() fullArea: any;

  @ViewChild('widgetContainer') widgetContainer: ElementRef;

  private margin = {top: 15, right: 20, bottom: 30, left: 60};

  public lineColors = ['#26ba9d', '#645ec5', '#289cf7'];
  public textMax = ['MAX', 'MIN'];

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
  private areaFull: any;
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

  private graphData: any = [];
  public error = false;
  private dataLoaded = false;

  constructor(private ngZone: NgZone) {

  window.onresize = (e) => {
        // ngZone.run will help to run change detection
        this.ngZone.run(() => {
        this.graphWidth = parseInt(window.getComputedStyle(this.widgetContainer.nativeElement, null).getPropertyValue('width'), 10);
        });
    };
   }

  ngOnChanges() {
    this.updateComponent();
  }

  updateComponent() {
    this.dataLoaded = true;
    if (this.graphWidth) {

      // Issue using this.margin.top, this.margin.left, this.margin.bottom and this.margin.right, values getting appended, that's why using the exacr values instead of variables..
      this.width = this.graphWidth - 60 - 40;
      this.timeLineWidth = this.width * 0.75;
      this.height = this.graphHeight - 15 - 30 - 70;
    }
    this.getIssues();
  }

   getIssues() {
        if (this.dataResponse) {
            this.wholeData = {'val': this.dataResponse};
            this.interval = this.dataResponse[0].values.length;
            for ( let i = 0 ; i < this.dataResponse.length; i++) {
              this.legendsvalue.push(this.dataResponse[0].values[0].legends[i]);
            }
            this.dataLoaded = true;
            this.error = false;
            this.graphData = this.dataResponse;
            const idValue = this.idUnique;
            const uniqueId = document.getElementById(idValue);

            // To remove the graph content if its present before re-plotting
            if (d3.select(uniqueId).select('g') !== undefined) {
              d3.select(uniqueId).select('g').remove();
              d3.select(uniqueId).append('g');
            }

            // Plot the graph and do all associated processes
            try {
              this.initSvg();
              this.initComponents();
              if (this.graphData[0].values.length >= 2) {
                this.computeLowerAndHigherLines();
                this.formatDataForArea();
              }
              this.drawAxisAndGrid();
              this.drawLine();
              if (this.graphData[0].values.length >= 2 && ((this.hover === true) || (this.hover === 'true'))) {
                this.drawHover();
              }
            } catch (e) {
              this.handleError(e);
            }


        }
  }

  handleError(error) {
    // To remove the graph content in case of error to make space for error message
     if (d3.selectAll('#' + this.idUnique).selectAll('g') !== undefined) {
      d3.selectAll('#' + this.idUnique).selectAll('g').remove();
      d3.select('#' + this.idUnique).append('g');
    }


    this.dataLoaded = false;
    // this.errorMessage = 'apiResponseError';
    this.error = true;
  }

  ngOnInit() {

    this.updateComponent();
  }

  private initSvg() {

    const idValue = this.idUnique;
    const uniqueId = document.getElementById(idValue);
    let yCoordinate;

    if ((this.axisUnit === 'false')) {

      yCoordinate = -(this.graphHeight / 7);
      d3.select(uniqueId).select('svg').attr('width', this.graphWidth);

    } else {

      yCoordinate = -(this.graphHeight / this.yCoordinates);
      d3.select(uniqueId).select('svg').attr('width', this.graphWidth - 40);

    }

    d3.select(uniqueId).select('svg').attr('height', this.graphHeight - 59);

    this.svg = d3.select(uniqueId)
                .select('svg')
               .append('g')
               .attr('transform', 'translate(' + 60 + ',' + yCoordinate + ')');

  }

  private initComponents() {
    this.data = this.graphData.map((v) => v.values.map((z) => z.date ))[0];

    this.x = d3Scale.scaleTime().range([0, this.width]);
    this.y = d3Scale.scaleLinear().range([this.height, 0]);

    const maxValue = d3Array.max(this.graphData, function(c) { return d3Array.max(c[`values`], function(d) { return d[`value`]; }); });
    this.x.domain(d3Array.extent(this.data, (d: Date) => d ));
    if (this.translateChange === true) {
      if (maxValue > 10) {
        this.y.domain([
          0,
          100
        ]);
      } else {
        this.y.domain([
          0,
          d3Array.max(this.graphData, function(c) { return d3Array.max(c[`values`], function(d) { return d[`value`] * 1.3; }); })
        ]);
      }
    } else {
      this.y.domain([
        0,
        d3Array.max(this.graphData, function(c) { return d3Array.max(c[`values`], function(d) { return d[`value`] * 1.3; }); })
      ]);
    }

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
    for ( let i = 0; i < this.graphData.length; i++) {
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

    if ((this.xAxisValues === true) || (this.xAxisValues === 'true')) {
      this.tickValues = '%b %e';
    } else if ((this.xAxisValues === false) || (this.xAxisValues === 'false')) {
      this.tickValues = '';
    }

    if (this.translateChange === true) {
      this.tickValues = '%m/%e';
    }

    let ticksNumber;
    if (this.interval < 4) {
      ticksNumber = 2;
    } else if ((this.interval >= 4 ) && (this.interval < 8)) {
      ticksNumber = 4;
    } else {
      ticksNumber = 6;
    }

    // Main Graph x-axis
    this.focus.append('g')
          .attr('class', 'axis axis--x')
          .attr('transform', 'translate(0,' + this.height + ')')
          .call(d3Axis.axisBottom(this.x)
          .ticks(ticksNumber)
          .tickSizeInner(10)
          .tickSizeOuter(0)
          .tickFormat(d3TimeFormat.timeFormat(this.tickValues))
        );

    if ((this.verticalLines === true) || (this.verticalLines === 'true')) {
      // Vertical Grid Lines
      this.svg.append('g')
            .attr('class', 'grid vertical')
            .attr('transform', 'translate(0,' + (2 * this.margin.top + this.height + 40) + ')')
            .call(d3Axis.axisBottom(this.x)
                .ticks(7)
                .tickSize(-this.height)
                .tickFormat(d => '')
          );
    }

    // Horizontal Grid Lines
    this.svg.append('g')
              .attr('class', 'grid horizontal multiline')
              .attr('transform', 'translate(0,' + (2 * this.margin.top + 40) + ')')
              .call(d3Axis.axisLeft(this.y)
                  .ticks(3)
                  .tickSize(-this.width)
                  .tickFormat(d => '')
              );


    // Main Graph y-axis and associated Label
    if (this.axisUnit === '%') {
        this.focus.append('g')
            .attr('class', 'axis axis--y')
            .attr('stroke-width', '0')
            .attr('transform', 'translate(0,' + 0 + ')')
            .attr('stroke', '#fff')
            .call(d3Axis.axisLeft(this.y).ticks(3).tickFormat(d => d + '%'));


        this.svg.append('text')
            .attr('class', 'axis-title')
            .attr('transform', 'rotate(-90)')
            .attr('y', -47)
            .attr('x', -100)
            .attr('dy', '.71em')
            .attr('stroke-width', '0')
            .attr('fill', '#2c2e3d')
            .attr('stroke', '#2c2e3d')
            .style('text-anchor', 'end')
            .text(this.yAxisLabel);
    } else if (this.axisUnit === 'false') {
          this.focus.append('g')
            .attr('class', 'axis axis--y')
            .attr('stroke-width', '0')
            .attr('stroke', '#fff')
            .call(d3Axis.axisLeft(this.y).ticks(3).tickFormat(d => this.abbreviateNumber(d, 'top')));

          if (this.translateChange === true) {
              this.svg.append('text')
                .attr('class', 'axis-title')
                .attr('transform', 'rotate(-90)')
                .attr('y', -50)
                .attr('x', -100)
                .attr('dy', '.71em')
                .attr('stroke-width', '0')
                .attr('fill', '#2c2e3d')
                .attr('stroke', '#2c2e3d')
                .style('text-anchor', 'end')
                .text(this.yAxisLabel);
          } else {
              this.svg.append('text')
                .attr('class', 'axis-title')
                .attr('transform', 'rotate(-90)')
                .attr('y', -50)
                .attr('x', -160)
                .attr('dy', '.71em')
                .attr('stroke-width', '0')
                .attr('fill', '#2c2e3d')
                .attr('stroke', '#2c2e3d')
                .style('text-anchor', 'end')
                .text(this.yAxisLabel);
          }

      } else {
          this.focus.append('g')
            .attr('class', 'axis axis--y')
            .attr('stroke-width', '0')
            .attr('transform', 'translate(0,' + 0 + ')')
            .attr('stroke', '#fff')
            .call(d3Axis.axisLeft(this.y).ticks(3).tickFormat(d => this.abbreviateNumber(d, 'bottom')));


        this.svg.append('text')
            .attr('class', 'axis-title')
            .attr('transform', 'rotate(-90)')
            .attr('y', -57)
            .attr('x', -90)
            .attr('dy', '.71em')
            .attr('stroke-width', '0')
            .attr('fill', '#2c2e3d')
            .attr('stroke', '#2c2e3d')
            .style('text-anchor', 'end')
            .text(this.yAxisLabel);
      }

  }

  abbreviateNumber(number, value) {
    if (value === 'top') {

      if (number < '99') {
        return number;
      } else {
        number = parseInt(number, 10);
        number = number > 1000000 ? (number / 1000000) + 'M' : (number > 1000 ? (number / 1000) + 'K' : number);
        return number;
      }

    } else {

      if ( number < '100' ) {
        number = number + this.axisUnit;
        return number;
      } else {
        number = parseInt(number, 10);
        number = number > 1000000 ? (number / 1000000) + this.axisUnit : (number >= 1000 ? (number / 1000) + this.axisUnit : (number >= 100 ? (number / 1000) + this.axisUnit : number));
        return number;
      }

    }

  }

  private drawLine() {

    if ((this.smoothEdge === true) || (this.smoothEdge === 'true')) {

      this.line = d3Shape.line()
        .x( (d: any) => this.x(d.date) )
        .y( (d: any) => this.y(d.value) )
        .curve(d3Shape.curveMonotoneX);

    } else if ((this.smoothEdge === false) || (this.smoothEdge === 'false')) {

      this.line = d3Shape.line()
        .x( (d: any) => this.x(d.date) )
        .y( (d: any) => this.y(d.value) );
    }
    // Line Graphs

      for ( let i = 0; i < this.graphData.length; i++) {
        this.focus.append('path')
          .datum(this.graphData[i].values)
          .attr('clip-path', 'url(#clip)')
          .transition()
          .duration(2000)
          .attr('class', 'line line' + `${i + 1}`)
          .attr('fill', 'none')
          .attr('stroke-width', '1.5px')
          .attr('stroke', this.colorSet[i])
          .attr('d', this.line);
      }


    this.area = d3Shape.area()
            .x0((d: any) =>  this.x(d.x0))
            .x1((d: any) =>  this.x(d.x1))
            .y0( (d: any) =>  this.y(d.y1))
            .y1( (d: any) => this.y(d.y0))
            .curve(d3Shape.curveMonotoneX);

    this.areaFull = d3Shape.area()
            .x((d: any) => this.x(d.x0))
            .y0(this.height)
            .y1((d: any) =>  this.y(d.y1))
            .curve(d3Shape.curveLinear);

    if ((this.multipleData === true) || (this.multipleData === 'true')) {

      // Draw area between the top and bottom lines
      this.focus.append('path')
              .datum(this.combinedData)
              .attr('class', 'area')
              .attr('fill', '#ccc')
              .attr('stroke-width', '0.5')
              .attr('stroke', '#2c2e3d')
              .attr('d', this.area);

    }

    if ((this.fullArea === true) || (this.fullArea === 'true')) {

      // Draw area between the top line and x-axis
      this.focus.append('path')
              .datum(this.combinedData)
              .attr('class', 'area')
              .attr('fill', '#ccc')
              .attr('stroke-width', '0.5')
              .attr('stroke', '#2c2e3d')
              .attr('d', this.areaFull);

    }

    d3.selectAll('.ticks');

    this.svg.select('#clip rect')
    .transition()
    .duration(2000)
    .attr('width', this.width);
  }

  private drawHover() {

    this.legendHover = this.dataResponse.map(function(eachLine){
      return eachLine.value;
    });

    this.searchAnObjectFromArray = function( key, value, array ) {
        const obj = array.filter( function( objs ) {
            return objs[ key ] === value;
        } )[ 0 ];

        return obj;
    };

    this.margin.left = this.margin.left + 20;

    this.focus = this.svg.append('g')
      .attr('class', 'focus')
      .style('display', 'none')
      .attr('transform', 'translate(0,' + ( 2 * this.margin.top + 40) + ')');

    this.focus.append('line')
        .attr('class', 'x')
        .style('stroke', '#bbb')
        .style('stroke-width', '2px')
        .style('opacity', 1)
        .attr('y1', 0)
        .attr('y2', this.height);

    this.focus.append('text')
          .attr('class', 'dateData')
          .attr('x', 9)
          .attr('dy', '.35em');

    this.focus.append('text')
          .attr('class', 'yearData')
          .attr('x', 9)
          .attr('dy', '1.5em');

    for ( let i = 0; i < this.dataResponse.length; i++ ) {
      if (this.colorSetLegends === 'true') {

        this.focus.append('rect')
          .attr('class', 'rectData' + i)
          .attr('fill', this.colorSet[this.dataResponse.length - 1 - i])
          .attr('height', '1.5rem')
          .attr('width', '3.8rem')
          .style('stroke', '#fff')
          .attr('rx', 4)
          .attr('ry', 4)
          .attr('x', -93)
          .attr('y', -7);

      } else {

        this.focus.append('rect')
          .attr('class', 'rectData' + i)
          .attr('fill', this.colorSet[this.dataResponse.length - 1 - i])
          .attr('height', '1.5rem')
          .attr('width', '2.7rem')
          .style('stroke', '#fff')
          .attr('rx', 4)
          .attr('ry', 4)
          .attr('x', -93)
          .attr('y', -7);
      }

      this.focus.append('text')
          .attr('class', 'valueData' + i)
          .attr('x', 9)
          .attr('dy', '.35em');

      this.focus.append('text')
          .attr('class', 'rectText' + i )
          .style('stroke', '#fff')
          .style('font-size', '10px')
          .attr('x', 9)
          .attr('dy', '.35em');

      this.focus.append('circle')
        .attr('class', 'c' + i )
        .style('stroke', this.colorSet[this.dataResponse.length - 1 - i ])
        .style('stroke-width', '2px')
        .attr('r', 4.5);

    }


      this.svg.append('rect')
        .attr('transform', 'translate(' + 0 + ',' + (60) + ')')
        .attr('class', 'overlay')
        .attr('width', this.width)
        .attr('height', this.height)
        .on('mouseover', () => this.focus.style('display', null))
        .on('mouseout', () => this.focus.style('display', 'none'))
        .on('mousemove', mousemove);

      const self = this;
    function mousemove() {

      const mousePosition = d3.mouse(this)[0];
      const formatDate = d3TimeFormat.timeFormat('%b %d');
      const formatYear = d3TimeFormat.timeFormat('%Y');

      const label = self.x.invert(d3.mouse(this)[0]);
      const dobj = {};
      dobj[`label`] = label;

      const getIssuesForHoverDate = self.combinedData.filter(function (issue) {
        const issueDate = issue.x0.getDate() + '-' + issue.x0.getMonth() + '-' + issue.x0.getFullYear();
        const labelDate = dobj[`label`].getDate() + '-' + dobj[`label`].getMonth() + '-' + dobj[`label`].getFullYear();

        return (issueDate === labelDate);
      });

      self.legendHover.map(function (legend) {
        const searchedObj = self.searchAnObjectFromArray('key', legend, getIssuesForHoverDate);
        for (let j = 0; j < self.dataResponse.length; j++) {
          if (searchedObj) {
            dobj['value' + j] = searchedObj[`y` + j];
          } else {
            dobj['value' + j] = 'NO DATA';
          }
        }
      });

      self.focus.select('.x')
        .attr('transform',
          'translate(' + self.x(dobj[`label`]) + ',' +
          0 + ')')
        .attr('y2', self.height);

      for (let k = 0; k < self.dataResponse.length; k++) {

        if (dobj[`value` + k] === 'NO DATA') {

          self.focus.select('.c' + k)
            .attr('r', 0);
        } else {

          self.focus.select('.c' + k)
            .attr('transform',
              'translate(' + self.x(dobj[`label`]) + ',' +
              self.y(dobj[`value` + k]) + ')')
            .attr('r', 4.5)
            .attr('y2', self.height);

        }

      }

      let rightSide;

      if ((this.axisUnit === 'false')) {

        if (self.graphWidth < 770) {
          rightSide = 610;
        } else if (self.graphWidth >= 770) {
          rightSide = 1030;
        }

      } else {

        if (self.graphWidth < 498) {
          rightSide = 175;
        } else if (self.graphWidth >= 498) {
          rightSide = 279;
        }

      }

      if (mousePosition < 55) {

        self.dataResponse.forEach((eachline) => {
          self.focus.select('.dateData')
            .attr('transform',
              'translate(' + self.x(dobj[`label`]) + ',' +
              0 + ')')
            .text((formatDate(dobj[`label`])).toUpperCase())
            .attr('dx', '.35em')
            .attr('dy', '.35em');
        });

        self.dataResponse.forEach((eachline) => {
          self.focus.select('.yearData')
            .attr('transform',
              'translate(' + self.x(dobj[`label`]) + ',' +
              0 + ')')
            .text((formatYear(dobj[`label`])).toUpperCase())
            .attr('dx', '.35em')
            .attr('dy', '1.5em');
        });

        for (let z = 0; z < self.dataResponse.length; z++) {

          self.dataResponse.forEach((eachline) => {
            self.focus.select('.valueData' + z)
              .attr('transform',
                'translate(' + self.x(dobj[`label`]) + ',' +
                0 + ')')
              .text(dobj['value' + z])
              .attr('dx', '5.5em')
              .attr('dy', .35 + (z * 3) + 'em');
          });


          if (self.colorSetLegends === 'true') {

            self.dataResponse.forEach((eachline) => {
              self.focus.select('.rectText' + z)
                .attr('transform',
                  'translate(' + self.x(dobj[`label`]) + ',' +
                  0 + ')')
                .text(self.legendsvalue[self.dataResponse.length - 1 - z])
                .attr('dx', '6.7em')
                .attr('dy', 2.2 + (z * 3.5) + 'em');
            });

          } else {

            self.dataResponse.forEach((eachline) => {
              self.focus.select('.rectText' + z)
                .attr('transform',
                  'translate(' + self.x(dobj[`label`]) + ',' +
                  0 + ')')
                .text(self.legendsvalue[z])
                .attr('dx', '6.7em')
                .attr('dy', 2 + (z * 3.5) + 'em');
            });

          }

          self.dataResponse.forEach((eachline) => {
            self.focus.select('.rectData' + z)
              .attr('transform',
                'translate(' + self.x(dobj[`label`]) + ',' +
                0 + ')')
              .attr('x', '70')
              .attr('y', 10 + (z * 32));
          });

        }


      } else if (mousePosition > rightSide) {

        self.dataResponse.forEach((eachline) => {
          self.focus.select('.dateData')
            .attr('transform',
              'translate(' + self.x(dobj[`label`]) + ',' +
              0 + ')')
            .text((formatDate(dobj[`label`])).toUpperCase())
            .attr('dx', '-12em')
            .attr('dy', '.5em');
        });

        self.dataResponse.forEach((eachline) => {
          self.focus.select('.yearData')
            .attr('transform',
              'translate(' + self.x(dobj[`label`]) + ',' +
              0 + ')')
            .text((formatYear(dobj[`label`])).toUpperCase())
            .attr('dx', '-12em')
            .attr('dy', '1.8em');
        });

        for (let i = 0; i < self.dataResponse.length; i++) {

          if (dobj[`value` + i] === 'NO DATA') {

            self.dataResponse.forEach((eachline) => {
              self.focus.select('.valueData' + i)
                .attr('transform',
                  'translate(' + self.x(dobj[`label`]) + ',' +
                  0 + ')')
                .text(dobj['value' + i])
                .attr('dx', '-5.5em')
                .attr('dy', .35 + (i * 2.7) + 'em');
            });

            if (self.colorSetLegends === 'true') {

              self.dataResponse.forEach((eachline) => {
                self.focus.select('.rectText' + i)
                  .attr('transform',
                    'translate(' + self.x(dobj[`label`]) + ',' +
                    0 + ')')
                  .text(self.legendsvalue[self.dataResponse.length - 1 - i])
                  .attr('dx', '-9.7em')
                  .attr('dy', 0.5 + (i * 3.5) + 'em');
              });

            } else {

              self.dataResponse.forEach((eachline) => {
                self.focus.select('.rectText' + i)
                  .attr('transform',
                    'translate(' + self.x(dobj[`label`]) + ',' +
                    0 + ')')
                  .text(self.legendsvalue[i])
                  .attr('dx', '-9.7em')
                  .attr('dy', 0.6 + (i * 3.5) + 'em');
              });

            }

            self.dataResponse.forEach((eachline) => {
              self.focus.select('.rectData' + i)
                .attr('transform',
                  'translate(' + self.x(dobj[`label`]) + ',' +
                  0 + ')')
                .attr('x', '-93')
                .attr('y', -7 + (i * 34));
            });

          } else {

            self.dataResponse.forEach((eachline) => {
              self.focus.select('.valueData' + i)
                .attr('transform',
                  'translate(' + self.x(dobj[`label`]) + ',' +
                  0 + ')')
                .text(dobj['value' + i])
                .attr('dx', '-6.8em')
                .attr('dy', 2 + (i * 2.7) + 'em');
            });

            if (self.colorSetLegends === 'true') {

              self.dataResponse.forEach((eachline) => {
                self.focus.select('.rectText' + i)
                  .attr('transform',
                    'translate(' + self.x(dobj[`label`]) + ',' +
                    0 + ')')
                  .text(self.legendsvalue[self.dataResponse.length - 1 - i])
                  .attr('dx', '-7.6em')
                  .attr('dy', 0.6 + (i * 3.5) + 'em');
              });

            } else {

              self.dataResponse.forEach((eachline) => {
                self.focus.select('.rectText' + i)
                  .attr('transform',
                    'translate(' + self.x(dobj[`label`]) + ',' +
                    0 + ')')
                  .text(self.legendsvalue[i])
                  .attr('dx', '-7.6em')
                  .attr('dy', 0.6 + (i * 3.5) + 'em');
              });
            }

            self.dataResponse.forEach((eachline) => {
              self.focus.select('.rectData' + i)
                .attr('transform',
                  'translate(' + self.x(dobj[`label`]) + ',' +
                  0 + ')')
                .attr('x', '-73')
                .attr('y', -7 + (i * 34));
            });
          }

        }

      } else {

        self.dataResponse.forEach((eachline) => {
          self.focus.select('.dateData')
            .attr('transform',
              'translate(' + self.x(dobj[`label`]) + ',' +
              0 + ')')
            .text((formatDate(dobj[`label`])).toUpperCase())
            .attr('dx', '.35em')
            .attr('dy', '.35em');
        });

        self.dataResponse.forEach((eachline) => {
          self.focus.select('.yearData')
            .attr('transform',
              'translate(' + self.x(dobj[`label`]) + ',' +
              0 + ')')
            .text((formatYear(dobj[`label`])).toUpperCase())
            .attr('dx', '.35em')
            .attr('dy', '1.5em');
        });

        for (let i = 0; i < self.dataResponse.length; i++) {

          if (dobj[`value` + i] === 'NO DATA') {

            self.dataResponse.forEach((eachline) => {
              self.focus.select('.valueData' + i)
                .attr('transform',
                  'translate(' + self.x(dobj[`label`]) + ',' +
                  0 + ')')
                .text(dobj['value' + i])
                .attr('dx', '-5.5em')
                .attr('dy', .35 + (i * 2.7) + 'em');
            });

            if (self.colorSetLegends === 'true') {

              self.dataResponse.forEach((eachline) => {
                self.focus.select('.rectText' + i)
                  .attr('transform',
                    'translate(' + self.x(dobj[`label`]) + ',' +
                    0 + ')')
                  .text(self.legendsvalue[self.dataResponse.length - 1 - i])
                  .attr('dx', '-9.7em')
                  .attr('dy', 0.5 + (i * 3.5) + 'em');
              });

            } else {

              self.dataResponse.forEach((eachline) => {
                self.focus.select('.rectText' + i)
                  .attr('transform',
                    'translate(' + self.x(dobj[`label`]) + ',' +
                    0 + ')')
                  .text(self.legendsvalue[i])
                  .attr('dx', '-9.7em')
                  .attr('dy', 0.6 + (i * 3.5) + 'em');
              });

            }

            self.dataResponse.forEach((eachline) => {
              self.focus.select('.rectData' + i)
                .attr('transform',
                  'translate(' + self.x(dobj[`label`]) + ',' +
                  0 + ')')
                .attr('x', '-93')
                .attr('y', -7 + (i * 34));
            });

          } else {

            self.dataResponse.forEach((eachline) => {
              self.focus.select('.valueData' + i)
                .attr('transform',
                  'translate(' + self.x(dobj[`label`]) + ',' +
                  0 + ')')
                .text(dobj['value' + i])
                .attr('dx', '-6.8em')
                .attr('dy', 2 + (i * 2.7) + 'em');
            });

            if (self.colorSetLegends === 'true') {

              self.dataResponse.forEach((eachline) => {
                self.focus.select('.rectText' + i)
                  .attr('transform',
                    'translate(' + self.x(dobj[`label`]) + ',' +
                    0 + ')')
                  .text(self.legendsvalue[self.dataResponse.length - 1 - i])
                  .attr('dx', '-7.6em')
                  .attr('dy', 0.6 + (i * 3.5) + 'em');
              });

            } else {

              self.dataResponse.forEach((eachline) => {
                self.focus.select('.rectText' + i)
                  .attr('transform',
                    'translate(' + self.x(dobj[`label`]) + ',' +
                    0 + ')')
                  .text(self.legendsvalue[i])
                  .attr('dx', '-7.6em')
                  .attr('dy', 0.6 + (i * 3.5) + 'em');
              });

            }

            self.dataResponse.forEach((eachline) => {
              self.focus.select('.rectData' + i)
                .attr('transform',
                  'translate(' + self.x(dobj[`label`]) + ',' +
                  0 + ')')
                .attr('x', '-73')
                .attr('y', -7 + (i * 34));
            });

          }

        }

      }
    }
  }

}
