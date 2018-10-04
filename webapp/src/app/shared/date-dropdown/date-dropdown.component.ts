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
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';
import { DatepickerOptions } from 'ng2-datepicker';

@Component({
  selector: 'app-date-dropdown',
  templateUrl: './date-dropdown.component.html',
  styleUrls: ['./date-dropdown.component.css']
})

export class DateDropdownComponent implements OnInit {

  @Input() dataForm: FormGroup;
  @Input() options: DatepickerOptions;
  date = new Date();
  @Input() dateToday = this.date.getFullYear() + '-' + (this.date.getMonth() + 1) + '-' + this.date.getDate();
  @Output() dataSent = new EventEmitter();
  user: FormGroup;

  constructor(private formBuilder: FormBuilder) {
  }

  ngOnInit() {
    if (this.dataForm !== undefined) {
      this.user = this.formBuilder.group({
        dateValue: ''
      });
      this.user = new FormGroup({
        dateValue: new FormControl('')
      });
    }

  }

  searchCalled(val: any): any {
      this.dataSent.emit(val);
  }

}
