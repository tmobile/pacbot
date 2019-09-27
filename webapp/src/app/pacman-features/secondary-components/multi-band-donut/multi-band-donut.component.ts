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

import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';

import * as d3 from 'd3-selection';
import * as d3Scale from 'd3-scale';
import * as d3Shape from 'd3-shape';

@Component({
  selector: 'app-multi-band-donut',
  templateUrl: './multi-band-donut.component.html',
  styleUrls: ['./multi-band-donut.component.css']
})

export class MultiBandDonutComponent implements OnInit, OnChanges {

  @Input() overallPercentage: any = 0;
  @Input() loaded = false;
  @Input() contValue = false;
  @Input() donutData = [];
  @Input() seekdata = false;

  @Output() error: EventEmitter<any> = new EventEmitter();

  private svg: any;

  @Input() colorData: any = ['#26ba9d', '#f2425f', '#645ec5', '#3c5079', '#289cf7'];
  @Input() colorTransData: any = ['rgba(38,186,157,0.2)', 'rgba(242,66,95,0.2)', 'rgba(100,94,197,0.2)', 'rgba(60,80,121,0.2)', 'rgba(40,156,247,0.2)'];

  private width: number;
  private height: number;
  private radius: number;
  
  private arc: any;
  private pie: any;
  private color: any;
  private g: any;
  
  private outerRadiusLimit = 54;
  private innerRadiusLimit = 66;
  textTransformVal = 0;
  radiusDelta = 14;

  constructor() {}

  ngOnInit() {
    this.drawGraph();
  }

  ngOnChanges(changes: SimpleChanges) {
    try {
      const donutDataChange = changes['donutData'];
      if (donutDataChange) {
        const cur  = JSON.stringify(donutDataChange.currentValue);
        const prev = JSON.stringify(donutDataChange.previousValue);
        if ((cur !== prev) && (this.donutData)) {
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
        if (document.getElementById('overallComplianceSvg') != null) {
          document.getElementById('overallComplianceSvg').innerHTML = '';
        }

        for (let i = 0; i < this.donutData.length; i++) {
          this.initSvg(i);
          this.drawChart(this.donutData[i]);
        }

        const afterLoad = this;
        setTimeout(function(){
            afterLoad.loaded = true;
        }, 100);
    } catch (error) {
      this.error.emit('jsError');
    }
  }

  private initSvg(indx: any) {
      const dataDelta = this.donutData.length>5 ? this.donutData.length - 5 : 0;
      this.radiusDelta = 14; // Reset to 14 everytime calling this function.
      if(dataDelta) {
        this.radiusDelta =  dataDelta<4 ? this.radiusDelta-dataDelta-1 : this.radiusDelta-dataDelta-2;
        if(dataDelta === 4) {
          this.textTransformVal = -3;
        }
      } else {
        this.textTransformVal = (( this.donutData.length - 5 ) * 14 - 3 )
      }
      this.svg = d3.select('#overallComplianceSvg');
	
      const svgContainer = document.getElementsByClassName('complaince-graph-container');

      if (document.getElementById('overallComplianceSvg') != null ) {
        document.getElementById('overallComplianceSvg').setAttribute('width', svgContainer[0].clientWidth + '');
        document.getElementById('overallComplianceSvg').setAttribute('height', svgContainer[0].clientHeight + '');
      }

      this.width = +this.svg.attr('width');
      this.height = +this.svg.attr('height');

      this.radius = ( Math.min(this.height, this.width) / 2.1 ) + this.radiusDelta * indx ;

      this.color = d3Scale.scaleOrdinal()
          .range([ this.colorTransData[indx % 5], this.colorData[indx % 5], 'transparent']);

      if (this.donutData.length > 1) {
          if(dataDelta) {
            const arcRadiusDelta = 12 - (2*dataDelta)
            const innerStart = 48 + arcRadiusDelta;
            this.outerRadiusLimit = 48 + (arcRadiusDelta*dataDelta);
            this.innerRadiusLimit = innerStart + (arcRadiusDelta*dataDelta);
          }
          this.arc = d3Shape.arc()
            .outerRadius(this.radius - this.outerRadiusLimit)
            .innerRadius(this.radius - this.innerRadiusLimit);
      } else {
          this.arc = d3Shape.arc()
            .outerRadius(this.radius - 34)
            .innerRadius(this.radius - 46);
      }

      this.pie = d3Shape.pie()
          .sort(null)
          .value((d: any) => d.val);

      this.svg = d3.select('#overallComplianceSvg')
          .append('g')
          .attr('transform', 'translate(' + this.width / 2 + ',' + this.height / 2 + ')');
  }

  private drawChart(data: any[]) {
        const g = this.svg.selectAll('.arc')
            .data(this.pie(data))
            .enter().append('g')
            .attr('class', 'arc');

        g.append('path')
            .attr('d', this.arc)
            .style('fill', d => this.color(d.data.title));
  }

  onResize() {
    if (document.getElementById('overallComplianceSvg') != null) {
      document.getElementById('overallComplianceSvg').innerHTML = '';
    }

    for (let i = 0; i < this.donutData.length; i++) {
      this.initSvg(i);
      this.drawChart(this.donutData[i]);
    }

  }

}
