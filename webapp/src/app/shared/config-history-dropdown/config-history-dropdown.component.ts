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

import { Component, OnInit, Input, Output, EventEmitter, ElementRef } from '@angular/core';

@Component({
  selector: 'app-config-history-dropdown',
  templateUrl: './config-history-dropdown.component.html',
  styleUrls: ['./config-history-dropdown.component.css'],
  // tslint:disable-next-line:use-host-property-decorator
  host: {
    '(document:click)': 'onClickOutside($event)'
  }
})
export class ConfigHistoryDropdownComponent implements OnInit {
  constructor(
    private eref: ElementRef
  ) {}

  @Input() items: any;
  @Input() placeholder;
  @Input() firstDD;
  @Input() activeTile;
  @Input() errorValue;
  @Input() errorMessage;
  @Output() selection = new EventEmitter();
  @Output() closeDropdown = new EventEmitter();
  tiles = [];
  showDropdown = false;

  ngOnInit() {
    this.processData(this.items);
  }

  public selected(value, index): void {
    this.selection.emit({'value': value, 'activeIndex': index});
  }

  processData(items) {
    this.tiles = items;
  }

  onClickOutside(event) {
    // if (!this.eref.nativeElement.contains(event.target)) {
    //   this.showDropdown = false; // close dropdown
    //   this.closeDropdown.emit(this.showDropdown);
    // }
  }
}
