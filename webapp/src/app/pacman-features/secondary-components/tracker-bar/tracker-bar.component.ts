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

import { Component , Input, Output, EventEmitter } from '@angular/core';


@Component({
  selector: 'app-tracker-bar',
  templateUrl: './tracker-bar.component.html',
  styleUrls: ['./tracker-bar.component.css']
})
export class TrackerBarComponent {

  constructor() {}
  @Input() valueType1: string;
  @Input() valueType2: string;
  @Input() totaltagValue: number;
  @Input() untagValue: number;
  @Input() tagValue: number;
  @Input() widthValue: number;
  @Input() transit = true;
  @Input() borderSet = true;
  @Input() lessWidthLeft = false;
  @Input() lessWidthRight = false;
  @Input() ShowLabel = false;
  @Input() barHeight = true;
  @Output() navigatePage: EventEmitter<any> = new EventEmitter();

  instructParentToNavigate(data) {
    this.navigatePage.emit(data);
  }
}
