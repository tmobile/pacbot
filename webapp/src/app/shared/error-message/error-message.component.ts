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
import { MESSAGES } from './../constants/messages';

@Component({
  selector: 'app-error-message',
  templateUrl: './error-message.component.html',
  styleUrls: ['./error-message.component.css']
})
export class ErrorMessageComponent implements OnInit {

  @Input() selectedValue: any;

  errorMessages;

  constructor() { }

  ngOnInit() {
    if (typeof(this.selectedValue) === 'string' && this.selectedValue.match('"')) {
      this.selectedValue = this.selectedValue.slice(1, -1);
    }
    this.errorMessages = MESSAGES.errorMessages[this.selectedValue] || this.selectedValue;
  }

}
