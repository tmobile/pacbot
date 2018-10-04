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
import { LoggerService } from '../../../shared/services/logger.service';

@Component({
  selector: 'app-domain-dropdown',
  templateUrl: './domain-dropdown.component.html',
  styleUrls: ['./domain-dropdown.component.css']
})

export class DomainDropdownComponent implements OnInit {
  /*
   * This component is used for the domain dropdown
   * Onclick of the dropdown data is send to the calling component by event emmiter
   */

  @Input() dropdownDataStr: string;
  @Output() onChange = new EventEmitter();

  dropdownData: any = [];

  constructor(
    private logger: LoggerService
  ) {

  }

  ngOnInit() {
    // domaindata is stored in the session storage seperated by tilda
    try {
      this.dropdownData = this.dropdownDataStr.split('~');
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /*
   * This function sends cilcked domain dropdown to the calling component
   */
  sendClickedValue(data, index) {

    try {
      const emitdata = {
        value: data,
        index: index
      };
      this.onChange.emit(emitdata.value);
    } catch (error) {
      this.logger.log('error', error);
    }
  }
}
