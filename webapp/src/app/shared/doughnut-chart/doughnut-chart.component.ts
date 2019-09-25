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
  Input,
  OnChanges,
  SimpleChanges,
  Output,
  EventEmitter
} from '@angular/core';

// d3 imports

import * as d3 from 'd3';
import * as d3Shape from 'd3-shape';
import * as d3Interpolate from 'd3-interpolate';

// import { select , selectAll } from 'd3-selection';
// import { transition } from 'd3-transition';


@Component({
  selector: 'app-doughnut-chart',
  templateUrl: './doughnut-chart.component.html',
  styleUrls: ['./doughnut-chart.component.css']
})

export class DoughnutChartComponent implements OnInit, OnChanges {
  @Input() chartContId: any;
  @Input() graphData: any;
  @Input() graphWidth: any;
  @Input() graphHeight: any;
  @Input() isPieChart = false;
  @Input() ringData;
  @Input() selectedLevel;
  @Input() MainTextcolor;
  @Input() innerRadious: 0;
  @Input() outerRadious: 0;
  @Input() strokeColor: 'transparent';
  @Input() flexTrue: any;
  @Input() isFullScreen;
  @Output() error: EventEmitter<any> = new EventEmitter();
  @Output() navigatePage: EventEmitter<any> = new EventEmitter();
  @Output() linkData: EventEmitter<any> = new EventEmitter();
  @Output() emitClick: EventEmitter<any> = new EventEmitter();

  public dataset: any;
  public labels: any;
  public arc: any;
  public path: any;
  public pie: any;
  public radius: any;
  public svg: any;
  public duration = 600;
  public zeroData = false;
  public innerRadius = 0;
  public outerRadius = 0;
  public fontSize = 0;

  constructor() {
    // select.prototype.transition = transition;
    // selectAll.prototype.transition = transition;
  }

  ngOnInit() {
    setTimeout(() => {
      this.removeExistingGraph();
      this.processGraphdata();
    }, 3);
  }

  ngOnChanges(changes: SimpleChanges) {
    try {
      const graphDataChange = changes['graphData'];
      const graphDimensionChange = changes['graphWidth'];
      const graphRadiusChange = changes['innerRadious'];
      if (graphDataChange) {
        const cur = JSON.stringify(graphDataChange.currentValue);
        const prev = JSON.stringify(graphDataChange.previousValue);
        if (cur !== prev && this.graphData) {
          setTimeout(() => {
            this.removeExistingGraph();
            this.processGraphdata();
          }, 3);
        }
      } else if (graphDimensionChange) {
        const curDimension = JSON.stringify(graphDimensionChange.currentValue);
        const prevDimension = JSON.stringify(graphDimensionChange.previousValue);
        if (curDimension !== prevDimension && this.graphWidth) {
          setTimeout(() => {
            this.removeExistingGraph();
            this.processGraphdata();
          }, 3);
        }
      } else if (graphRadiusChange) {
        const cur = JSON.stringify(graphRadiusChange.currentValue);
        const prev = JSON.stringify(graphRadiusChange.previousValue);
        if (cur !== prev) {
          setTimeout(() => {
            this.removeExistingGraph();
            this.processGraphdata();
          }, 3);
        }
      }
    } catch (error) {
      this.error.emit('jsError');
    }
  }
  processGraphdata() {
    if (this.isFullScreen) {
      this.MainTextcolor = '#fff';
    } else {
      this.MainTextcolor = '#000';
    }
    let total;
    let centerText;
    if (this.graphData.centerText) {
      centerText = this.graphData.centerText;
    }
    if (this.graphData.totalCount) {
      total = this.abbreviateNumber(this.graphData.totalCount);
    }
    /*this.graphWidth = document.getElementsByClassName(
      'donut-container-' + this.chartContId
    )[0].clientWidth - 100;
    this.graphHeight =  document.getElementsByClassName(
      'donut-container-' + this.chartContId
    )[0].clientHeight - 50;*/
    if (this.graphData.data) {
      try {
        this.zeroData = false;
        const self = this;
        setTimeout(() => {

          if (Math.min(this.graphWidth, this.graphHeight) < 140) {
            this.graphWidth = 140;
            this.graphHeight = 140;
          }
          this.radius = Math.min(this.graphWidth, this.graphHeight) / 1.5;
          this.outerRadius = (Math.min(this.graphWidth, this.graphHeight) / 2) - 10;
          if (!this.isPieChart) {
            // If donut
            // this.innerRadius = this.outerRadius - 20;
            this.innerRadius = this.outerRadius - 10;
          } else {
            // If Pie chart
            this.innerRadius = 0;
          }
          // --------- remove old svg before plotting -------------//

          if (d3.
            selectAll('#' + this.chartContId)
              .select('svg')
              .selectAll('g') !== undefined) {
                d3.
            selectAll('#' + this.chartContId)
              .select('svg')
              .selectAll('g')
              .remove();
            d3.
            selectAll('#' + this.chartContId)
              .select('svg')
              .append('g');
          }

          this.pie = d3Shape.pie().sort(null)
          .startAngle(1.1 * Math.PI)
          .endAngle(3.1 * Math.PI)
          .value(function(d) { return +d; });

          // -------- defines the inner and outer radious--------//
          const arc = d3Shape
            .arc()
            .innerRadius(this.innerRadius)
            .outerRadius(this.outerRadius);

          // --------this appends a svg to the selected container and positions the svg------//
          this.svg = d3.select('#' + this.chartContId)
            .select('svg')
            .attr('width', this.graphWidth)
            .attr('height', this.graphHeight)
            .attr('viewBox', '0 0 ' + Math.min(this.graphWidth, this.graphHeight) + ' ' + Math.min(this.graphWidth, this.graphHeight) )
            .attr('preserveAspectRatio', 'xMinYMin')
            .append('g')
            .attr(
              'transform',
              'translate(' +
                this.graphWidth / 2 +
                ',' +
                this.graphHeight / 2 +
                ')'
            )
            .attr('stroke', this.strokeColor);

          // ------------ Hover in and out animation ------------------  //
          const arcHoverIn = d3Shape
            .arc()
            .innerRadius(this.innerRadius)
            .outerRadius(this.outerRadius + 5);
          const arcHoverOut = d3Shape
            .arc()
            .innerRadius(this.innerRadius)
            .outerRadius(this.outerRadius);

          // --------plots the doughnut chart--------//
          const g = this.svg
            .selectAll('path')
            .data(this.pie(this.graphData.data))
            .enter()
            .append('g')
            .attr('class', 'arc');

            // --------fill color with animation to donut chart--------//
            g.append('path')
            .attr('fill', (d, i) => {
              if (this.graphData.legendWithText && this.graphData.legendWithText[i]) {
                d['legend'] = this.graphData.legendWithText[i];
              }
              return this.graphData.color[i];
            })
            .style('cursor', (d, i) => {
              if (this.graphData.cursor && this.graphData.cursor[i]) {
                return this.graphData.cursor[i] === 'pointer' ? 'pointer' : 'default';
              } else {
                return 'default';
              }
            })
            .transition('rotate').delay(function(d, i) {
              return i * 250; }).duration(250)
              .attrTween('d', function(d) {
                const i = d3Interpolate.interpolate(d.startAngle + 0.1, d.endAngle);
                return function(t) {
                  d.endAngle = i(t);
                  return arc(d);
                  };
                });

          if (this.isPieChart) {

            setTimeout(() => {
              d3.
              selectAll('path')
                .on('mouseover', function(d, i) {
                  d3.
                  select(this)
                    .transition('hoverIn')
                    .duration(250)
                    .attr('d', arcHoverIn)
                    .attr('stroke-width', 4);
                })
                .on('mouseout', function(d, i) {
                  d3.
                  select(this)
                    .transition('hoverOut')
                    .duration(250)
                    .attr('d', arcHoverOut)
                    .attr('stroke-width', 1);
                })
                .on('click', function(d) {
                  self.emitClick.emit(d);
                });
            }, 350 );
          }

            // --------plots total count------------//
          if (centerText === undefined) {
            if (this.graphData.totalCount && total !== -1) {
              this.svg
                .append('text')
                .text(total)
                .attr('class', 'units-label')
                .attr('fill', this.MainTextcolor)
                .attr('stroke', this.MainTextcolor)
                .attr('text-anchor', 'middle')
                .attr('y', this.radius / 15);
            }
          } else {
            if ((this.graphData.totalCount && total !== -1) && centerText !== undefined && !this.isPieChart ) {
              this.svg
                .append('text')
                .text(total)
                .attr('class', 'units-label')
                .attr('fill', this.MainTextcolor)
                .attr('stroke', this.MainTextcolor)
                .attr('text-anchor', 'middle')
                .attr('y', this.radius / 50);
              this.svg
                .append('text')
                .text(centerText)
                .attr('class', 'units-label-text')
                .attr('fill', this.MainTextcolor)
                .attr('stroke', this.MainTextcolor)
                .attr('text-anchor', 'middle')
                .attr('y', this.radius / 8);
            }
          }

        }, 100);
      } catch (error) {
        this.error.emit('jsError');
      }
    } else {
      this.zeroData = true;
    }
  }

  linkClicked(data) {
    this.linkData.emit(data);
  }

  abbreviateNumber(number) {
    if (number < '99') {
      return number;
    } else {
      number = parseInt(number, 10);
      number =
        number > 1000000
          ? number / 1000000 + 'M'
          : number > 1000
            ? (number / 1000).toFixed(1) + 'K'
            : number;
      return number;
    }
  }

  instructParentToNavigate(severity) {
    this.navigatePage.emit(severity);
  }

  removeExistingGraph() {
    d3.
    selectAll('svg#' + this.chartContId + ' > *').remove();
  }

  onResize() {
    this.removeExistingGraph();
    this.processGraphdata();
  }
}
