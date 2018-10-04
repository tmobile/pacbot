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

import * as d3 from 'd3-selection';

import * as d3Shape from 'd3-shape';
import * as d3Scale from 'd3-scale';
import * as d3Array from 'd3-array';
import * as d3Axis from 'd3-axis';

@Component({
  selector: 'app-multiline-trend',
  templateUrl: './multiline-trend.component.html',
  styleUrls: ['./multiline-trend.component.css']
})

export class MultilineTrendComponent implements OnInit, OnChanges {

  constructor() {}

  ngOnInit() {}

  ngOnChanges() {}

}
