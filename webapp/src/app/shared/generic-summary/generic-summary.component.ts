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

@Component({
  selector: 'app-generic-summary',
  templateUrl: './generic-summary.component.html',
  styleUrls: ['./generic-summary.component.css']
})
export class GenericSummaryComponent implements OnInit, OnChanges {
  @Input() data: any;
  @Input() colors: any;
  @Input() percent: any;
  @Input() headerText: any;
  @Output() navigatePage: EventEmitter<any> = new EventEmitter();
  @Input() textValue: any;
  @Input() text: any;
  @Input() outOf: any;
  dataValue: any;
  finalData: any;

  constructor() {}

  ngOnChanges(changes: SimpleChanges) {
    try {
      const DataChange = changes['data'];
      if (DataChange) {
        const cur = JSON.stringify(DataChange.currentValue);
        const prev = JSON.stringify(DataChange.previousValue);
        if (cur !== prev && this.data) {
          this.ngOnInit();
        }
      }
    } catch (error) {}
  }

  ngOnInit() {
    this.finalData = this.processData(this.data);
  }

  isEven(n) {
    return n === parseFloat(n) ? !(n % 2) : void 0;
  }

  processData(data) {
    return data;
  }
  instructParentToNavigate(data) {
    this.navigatePage.emit(data);
  }
}
