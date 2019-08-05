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

import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-autofix-schedule',
  templateUrl: './autofix-schedule.component.html',
  styleUrls: ['./autofix-schedule.component.css']
})

export class AutofixScheduleComponent implements OnInit {

  constructor() { }

  @Input() autofixData;
  transformVal = -1;

  ngOnInit() {
    if (this.autofixData && this.autofixData.planItems && this.autofixData.planItems.length > 1
      && new Date().getTime()
      >= new Date( this.autofixData.planItems[0].plannedActionTime ).getTime()
      && new Date().getTime()
      <= new Date( this.autofixData.planItems[this.autofixData.planItems.length - 1].plannedActionTime ).getTime()
    ) {
      this.transformVal = 0;
      setTimeout(() => {
        this.transformVal = (
          ( new Date().getTime()
          - new Date(this.autofixData.planItems[0].plannedActionTime).getTime()
          )
          * (this.autofixData.planItems.length - 1)
          * 150
        )
        / (
          new Date( this.autofixData.planItems[this.autofixData.planItems.length - 1].plannedActionTime ).getTime()
          - new Date( this.autofixData.planItems[0].plannedActionTime ).getTime()
        );
      }, 100);
    }
  }
}
