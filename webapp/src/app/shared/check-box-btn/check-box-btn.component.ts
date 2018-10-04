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

import { Component, Input , Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-check-box-btn',
  templateUrl: './check-box-btn.component.html',
  styleUrls: ['./check-box-btn.component.css']
})
export class CheckBoxBtnComponent {

  @Output() checkBoxClickEvent: EventEmitter<any> = new EventEmitter();
  @Input() isChecked: boolean ;
  @Input() checkboxColor?;

  constructor() { }

  checkBoxClick () {
    this.isChecked  = !this.isChecked;
    this.checkBoxClickEvent.emit(this.isChecked);
  }

}
