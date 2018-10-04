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
  ElementRef,
  Output,
  EventEmitter
} from '@angular/core';

@Component({
  selector: 'app-nested-accordion',
  templateUrl: './nested-accordion.component.html',
  styleUrls: ['./nested-accordion.component.css'],
  // tslint:disable-next-line:use-host-property-decorator
  host: {
    '(document:click)': 'onClickOutsideAccordion($event)'
  }
})
export class NestedAccordionComponent implements OnInit {
  @Input() nestedAccordionData;
  @Output() accodionClicked: EventEmitter<any> = new EventEmitter();

  constructor(private eref: ElementRef) {}

  ngOnInit() {}
  /**
   * @func onClickOutsideAccordion
   * @param event
   * @desc need to add code to hide accordion on click of outside
   */
  onClickOutsideAccordion(event) {}
}
