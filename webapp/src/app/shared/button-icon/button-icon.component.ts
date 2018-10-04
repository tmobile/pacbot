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

import { Component, OnInit , Input , Output , EventEmitter } from '@angular/core';

@Component({
  selector: 'app-button-icon',
  templateUrl: './button-icon.component.html',
  styleUrls: ['./button-icon.component.css']
})
export class ButtonIconComponent implements OnInit {

  constructor() { }
  @Input() iconSrc: string;
  @Input() disabled = false;
  @Output() onClickInner = new EventEmitter();
  @Input() iconSrcAfter: string;
  afterData = false;
  beforeDate = false;

  ngOnInit() {
    if (this.iconSrcAfter !== undefined) {
      this.afterData = true;
    }

    if (this.iconSrc !== undefined) {
      this.beforeDate = true;
    }
  }

  onButtonClick() {
    if (!this.disabled) {
        this.onClickInner.emit();
    }
  }
}
