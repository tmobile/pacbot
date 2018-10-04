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
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges
} from '@angular/core';

import * as d3 from 'd3-selection';
import * as d3Scale from 'd3-scale';
import * as d3Shape from 'd3-shape';

@Component({
  selector: 'app-mulitidoughnutband',
  templateUrl: './mulitidoughnutband.component.html',
  styleUrls: ['./mulitidoughnutband.component.css']
})
export class MulitidoughnutbandComponent implements OnInit, OnChanges {
  @Input() overallPercentage: any = 0;
  @Input() loaded = false;
  @Input() contValue = false;
  @Input() donutData: any = [];
  @Input() seekdata = false;

  @Output() error: EventEmitter<any> = new EventEmitter();

  private svg: any;

  colorData = [
    'rgb(255, 125, 49)',
    'rgb(255, 75, 33)',
    'rgb(247, 64, 96)',
    'rgb(0, 173, 226)',
    'rgb(0, 170, 142)'
  ];
  colorTransData = [
    'rgba(255, 125, 49 , 0.2)',
    'rgba(255, 75, 33, 0.2)',
    'rgba(247, 64, 96, 0.2)',
    'rgba(0, 173, 226, 0.2)',
    'rgba(0, 170, 142, 0.2)'
  ];
  private width: number;
  private height: number;

  private radius: number;

  private arc: any;
  private pie: any;
  private color: any;

  private g: any;

  constructor() {
    this.drawGraph();
  }

  ngOnInit() {
    this.drawGraph();
  }

  ngOnChanges(changes: SimpleChanges) {
    try {
      const donutDataChange = changes['donutData'];
      if (donutDataChange) {
        const cur = JSON.stringify(donutDataChange.currentValue);
        const prev = JSON.stringify(donutDataChange.previousValue);
        if (cur !== prev && this.donutData) {
          this.drawGraph();
        }
      }
    } catch (error) {
      this.error.emit('jsError');
    }
  }

  private drawGraph() {
    try {
      // Remove existing graph if present
      if (document.getElementById('overallComplianceSvgNewId') != null) {
        document.getElementById('overallComplianceSvgNewId').innerHTML = '';
      }

      for (let i = 0; i < this.donutData.length; i++) {
        this.initSvg(i);
        this.drawChart(this.donutData[i]);
      }

      setTimeout(() =>  {
        this.loaded = true;
      }, 100);
    } catch (error) {
      this.error.emit('jsError');
    }
  }

  private initSvg(indx: any) {
    this.svg = d3.select('#overallComplianceSvgNewId');

    const svgContainer = document.getElementsByClassName(
      'complaince-graph-container-new'
    );

    if (document.getElementById('overallComplianceSvgNewId') != null) {
      document
        .getElementById('overallComplianceSvgNewId')
        .setAttribute('width', svgContainer[0].clientWidth + '');
      document
        .getElementById('overallComplianceSvgNewId')
        .setAttribute('height', svgContainer[0].clientHeight + '');
    }

    this.width = +this.svg.attr('width');
    this.height = +this.svg.attr('height');
    this.radius = Math.min(this.height, this.width) / 2.1 + 14 * indx;

    this.color = d3Scale
      .scaleOrdinal()
      .range([
        this.colorTransData[indx % 5],
        this.colorData[indx % 5],
        'transparent'
      ]);

    this.arc = d3Shape
      .arc()
      .outerRadius(this.radius - 54)
      .innerRadius(this.radius - 66);

    this.pie = d3Shape
      .pie()
      .sort(null)
      .value((d: any) => d.val);

    this.svg = d3
      .select('#overallComplianceSvgNewId')
      .append('g')
      .attr(
        'transform',
        'translate(' + this.width / 2 + ',' + this.height / 2 + ')'
      );
  }

  private drawChart(data: any[]) {
    const g = this.svg
      .selectAll('.arc')
      .data(this.pie(data))
      .enter()
      .append('g')
      .attr('class', 'arc');

    g.append('path')
      .attr('d', this.arc)
      .style('fill', d => this.color(d.data.title));
  }

  onResize() {
    if (document.getElementById('overallComplianceSvgNewId') != null) {
      document.getElementById('overallComplianceSvgNewId').innerHTML = '';
    }

    for (let i = 0; i < this.donutData.length; i++) {
      this.initSvg(i);
      this.drawChart(this.donutData[i]);
    }
  }
}
