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

import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-progress-summary',
  templateUrl: './progress-summary.component.html',
  styleUrls: ['./progress-summary.component.css']
})

export class ProgressSummaryComponent implements OnInit {
  @Input() appNum: any;
  @Input() appName: any;
  @Input() bardata: any;
  @Input() flexleftvalue: 1;
  @Input() flexrightvalue: 1;
  @Input() leftBarValue: 0;
  @Input() rightBarValue: 0;
  @Input() barColor: any = '#000';
  @Input() zeroValueColor: any = '#000';
  @Input() showBar = true;
  @Input() link: false;
  @Output() navigatePage: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit() {

  }

  instructParentToNavigate (appName) {
    this.navigatePage.emit(appName);
  }

}
