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
  Output,
  EventEmitter,
  SimpleChanges
} from '@angular/core';

// d3 imports

import * as d3 from 'd3-selection';
import * as d3Scale from 'd3-scale';
import * as d3Array from 'd3-array';
import * as d3Axis from 'd3-axis';

// d3 imports end here

@Component({
  selector: 'app-bar-chart',
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.css']
})
export class BarChartComponent implements OnInit, OnChanges {
  @Input() chartContId: any;
  @Input() graphData: any;
  @Input() legend_text: any;
  @Output() error: EventEmitter<any> = new EventEmitter();
  @Output() graphTextClick: EventEmitter<any> = new EventEmitter();
  @Input() barChartHeight?: any;

  private width: number;
  private height: number;
  private margin = { top: 30, right: 40, bottom: 40, left: 85 };

  private x: any;
  private y: any;
  private svg: any;
  private g: any;
  private data: any;
  private graphWidth: any;
  private graphHeight: any;
  private yTicks: any;

  constructor() {
  }

  ngOnInit() {
    setTimeout(() => {
      this.processGraphdata();
    }, 3);
  }


  // added ngOnChanges for bar chart to replot on change of data
    ngOnChanges(changes: SimpleChanges) {
    try {
      const graphDataChange = changes['graphData'];
      if (graphDataChange) {
            setTimeout(() => {
            this.processGraphdata();
          }, 3);
      }
    } catch (error) {
      console.log(error);
      this.error.emit('jsError');
    }
  }

  private processGraphdata() {
    try {
      this.graphWidth = document.getElementsByClassName(
        'bar-container-' + this.chartContId
      )[0].clientWidth;
      if (this.barChartHeight) {
        this.graphHeight = this.barChartHeight;
      } else {
        this.graphHeight = 310;
      }

      this.removeExistingGraph();
      this.init();
      this.initSvg();
      this.initAxis();
      this.drawBars();
      this.drawAxis();
    } catch (error) {
      this.error.emit('jsError');
    }
  }

  private init() {
    this.width = this.graphWidth - this.margin.left - this.margin.right;
    this.height = this.graphHeight - this.margin.top - this.margin.bottom;
    this.data = this.graphData;
  }

  private initSvg() {
    this.svg = d3
      .select('svg#' + this.chartContId)
      .attr('width', this.graphWidth)
      .attr('height', this.graphHeight);
    this.g = this.svg
      .append('g')
      .attr(
        'transform',
        'translate(' + this.margin.left + ',' + (this.margin.top - 10) + ')'
      );
  }

  private initAxis() {
    // initialize domain to x and y axis
    this.x = d3Scale
      .scaleBand()
      .rangeRound([0, this.width])
      .padding(0.5);
    this.y = d3Scale.scaleLinear().rangeRound([this.height, 0]);
    this.x.domain(this.data.map(d => d.x_domain));
    const array_max = d3Array.max(this.data, d => d['y_domain']);
    this.yTicks = this.getSmartTicks(array_max);
    this.y.domain([0, this.yTicks.endPoint]);

    // Horizontal Grid Lines
    this.g
      .append('g')
      .attr('class', 'grid horizontal')
      .call(
        d3Axis
          .axisLeft(this.y)
          .ticks(this.yTicks.count)
          .tickSize(-this.width)
          .tickFormat(d => '')
      );
  }

  private drawAxis() {
    // Main Graph x-axis
    this.g
      .append('g')
      .attr('class', 'axis axis--x')
      .attr('stroke-width', '0')
      .attr('stroke', '#fff')
      .attr('transform', 'translate(0,' + this.height + ')')
      .attr('fill', '#2c2e3d')
      .attr('stroke', '#2c2e3d')
      .style('font-size', '12')
      .style('text-transform', 'capitalize')
      .call(d3Axis.axisBottom(this.x))
      .append('text')
      .attr('class', 'axis-title')
      .attr('y', 40)
      .attr('x', '46%')
      .attr('dx', '.71em')
      .attr('stroke-width', '0')
      .attr('fill', '#2c2e3d')
      .attr('stroke', '#2c2e3d')
      .style('font-size', '12')
      .style('text-anchor', 'end')
      .text(this.legend_text.x_axis);

    // Main Graph y-axis and associated Label

    this.g
      .append('g')
      .attr('class', 'axis axis--y')
      .attr('stroke-width', '0')
      .attr('stroke', '#fff')
      .call(d3Axis.axisLeft(this.y).ticks(this.yTicks.count))
      .style('font-size', '12')
      .style('text-transform', 'capitalize');

    this.svg
      .append('text')
      .attr('class', 'axis-title')
      .attr('transform', 'rotate(-90)')
      .attr('y', 30)
      .attr('x', 0 - this.graphHeight / 2)
      .attr('dy', '-10')
      .attr('stroke-width', '0')
      .attr('fill', '#2c2e3d')
      .attr('stroke', '#2c2e3d')
      .style('font-size', '12')
      .style('text-anchor', 'start')
      .text(this.legend_text.y_axis);
  }

  private drawBars() {
    // create bar with rising animation
    this.g
      .selectAll('.bar')
      .data(this.data)
      .enter()
      .append('rect')
      .attr('class' , function(d) {
          return d.y_domain > 0 ? 'bar bar-links' : 'bar';
      })
      .attr('x', d => this.x(d.x_domain))
      .attr('y', this.height)
      .attr('width', this.x.bandwidth())
      .attr('fill', d => d.bar_color)
      .attr('height', 0)
      .transition()
      .duration(500)
      .delay((d, i) => i * 300) // a different delay for each bar
      .attr('y', d => this.y(d.y_domain))
      .attr('height', d => this.height - this.y(d.y_domain));

    // add y value above bars with animation
    this.g
      .selectAll('text.bar')
      .data(this.data)
      .enter()
      .append('text')
      .attr('class' , function(d) {
          return d.y_domain > 0 ? 'bar bar-links' : 'bar';
      })
      .attr('text-anchor', 'middle')
      .attr('x', d => this.x(d.x_domain) + this.x.bandwidth() / 2)
      .attr('y', d => this.x(d.x_domain) + this.x.bandwidth() / 2) // added to show animation
      .on('click', (d) => {
        d3.event.stopPropagation();
        this.graphTextClick.emit(d);
      })
      .transition()
      .duration(500)
      .delay((d, i) => i * 350) // a different delay for each bar
      // .attr("x", (d) => this.x(d.letter) +this.x.bandwidth()/2 )
      .attr('y', d => this.y(d.y_domain) - 5)
      .text(d => d.y_domain)
      .style('font-size', '12')
      .style('font-weight', '600');
  }

  removeExistingGraph() {
    d3.selectAll('svg#' + this.chartContId + ' > *').remove();
  }

  onResize() {
    this.removeExistingGraph();
    this.processGraphdata();
  }

  getSmartTicks(val) {
    if (val === 0) {
      return {
        endPoint: 0.6,
        count: 4
      };
    }
    // base step between nearby two ticks
    let step = Math.pow(10, val.toString().length - 1);

    // modify steps either: 1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000...
    if (val / step < 2) {
        step = step / 5;
    } else if (val / step < 5) {
        step = step / 2;
    }

    /* add one more step if the last tick value is the same as the max value
    if you don't want to add, remove "+1" */
    const slicesCount = Math.ceil((val + 1) / step);

    return {
      endPoint: slicesCount * step,
      count: Math.min(5, slicesCount) // show max 5 ticks
    };
  }
}
