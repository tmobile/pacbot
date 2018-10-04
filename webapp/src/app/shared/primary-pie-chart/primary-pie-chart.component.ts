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
  SimpleChanges,
  Output,
  EventEmitter,
  OnChanges
} from '@angular/core';

// d3 imports

import * as d3 from 'd3-selection';
import * as d3Shape from 'd3-shape';
import * as d3Interpolate from 'd3-interpolate';
import { transition } from 'd3-transition';

@Component({
  selector: 'app-primary-pie-chart',
  templateUrl: './primary-pie-chart.component.html',
  styleUrls: ['./primary-pie-chart.component.css']
})
export class PrimaryPieChartComponent implements OnInit, OnChanges {
  @Input() chartContId: any;
  @Input() graphData: any;
  @Input() graphWidth: any;
  @Input() graphHeight: any = 185;
  @Input() MainTextcolor: '#fff';
  @Input() innerRadious: 0;
  @Input() outerRadious: 0;
  @Input() strokeColor: 'transparent';
  @Input() flexTrue: any;
  @Output() error: EventEmitter<any> = new EventEmitter();
  @Output() navigatePage: EventEmitter<any> = new EventEmitter();
  public dataset: any;
  public labels: any;
  public arc: any;
  public path: any;
  public pie: any;
  public radius: any;
  public svg: any;
  public duration = 600;
  public zeroData = false;
  constructor() {}

  ngOnInit() {
    this.processGraphdata();
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
          this.processGraphdata();
        }
      } else if (graphDimensionChange) {
        const curDimension = JSON.stringify(graphDimensionChange.currentValue);
        const prevDimension = JSON.stringify(graphDimensionChange.previousValue);
        if (curDimension !== prevDimension && this.graphWidth) {
          this.processGraphdata();
        }
      } else if (graphRadiusChange) {
        const cur = JSON.stringify(graphRadiusChange.currentValue);
        const prev = JSON.stringify(graphRadiusChange.previousValue);
        if (cur !== prev) {
          this.processGraphdata();
        }
      }
    } catch (error) {
      this.error.emit('jsError');
    }
  }
  processGraphdata() {
    let total = this.graphData.totalCount;

    total = this.abbreviateNumber(this.graphData.totalCount);
    if (this.graphData.totalCount !== 0) {
      try {
        this.zeroData = false;
        setTimeout(() => {
          this.radius = Math.min(this.graphWidth, this.graphHeight) / 1.5;

          // --------- remove old svg before plotting -------------//
          if (
            d3
              .selectAll('#' + this.chartContId)
              .select('svg')
              .selectAll('g') !== undefined
          ) {
            d3.selectAll('#' + this.chartContId)
              .select('svg')
              .selectAll('g')
              .remove();
            d3.selectAll('#' + this.chartContId)
              .select('svg')
              .append('g');
          }

          this.pie = d3Shape.pie().sort(null);

          // -------- defines the inner and outer radious--------//
          this.arc = d3Shape
            .arc()
            .innerRadius(0)
            .outerRadius(this.radius - this.outerRadious);

          // --------this appends a svg to the selected container and positions the svg------//
          this.svg = d3
            .select('#' + this.chartContId)
            .select('svg')
            .attr('width', this.graphWidth)
            .attr('height', this.graphHeight)
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
          .innerRadius(0)
          .outerRadius(this.radius - this.outerRadious + 5);

          const arcHoverOut = d3Shape
          .arc()
          .innerRadius(0)
          .outerRadius(this.radius - this.outerRadious);

          // --------plots the pie chart--------//
          this.path = this.svg
            .selectAll('path')
            .data(this.pie(this.graphData.data))
            .enter()
            .append('path')
            .attr('fill', (d, i) => {
              return this.graphData.color[i];
            })
            .attr('d', this.arc)
            .attr('stroke-width', 1)
            .on('mouseover', function(d, i) {
              d3.select(this)
                .transition()
                .duration(250)
                .attr('d', arcHoverIn)
                .attr('stroke-width', 4);
            })
            .on('mouseout', function(d, i) {
              d3.select(this)
                .transition()
                .duration(250)
                .attr('d', arcHoverOut)
                .attr('stroke-width', 1);
            });
        }, 100);
      } catch (error) {
        this.error.emit('jsError');
      }
    } else {
      this.zeroData = true;
    }
  }

  tweenPie(finish) {
    const start = {
      startAngle: 0,
      endAngle: 0
    };
    const i = d3Interpolate.interpolate(start, finish);
    return function(d) {
      return this.arc(i(d));
    };
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
}
