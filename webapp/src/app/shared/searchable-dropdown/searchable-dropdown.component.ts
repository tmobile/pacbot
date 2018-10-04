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

import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChange, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-searchable-dropdown',
  templateUrl: './searchable-dropdown.component.html',
  styleUrls: ['./searchable-dropdown.component.css']
})
export class SearchableDropdownComponent implements OnInit {
  constructor() {}

  @Input() items: any;
  @Input() placeholder;
  @Input() firstDD;
  @Input() initValue;
  @Input() active = [];

  @Output() selection = new EventEmitter();

  private value: any = {};
  private _disabledV = '0';
  private disabled = false;

  ngOnInit() {
    if (this.initValue) {
      this.active[0] = this.initValue;
    }
  }

  private get disabledV(): string {
    return this._disabledV;
  }

  private set disabledV(value: string) {
    this._disabledV = value;
    this.disabled = this._disabledV === '1';
  }

  public selected(value: any): void {
    this.selection.emit(value);
  }

  public removed(value: any): void {}

  public typed(value: any): void {}

  public refreshValue(value: any): void {
    this.value = value;
  }
}
