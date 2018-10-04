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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import * as _ from 'lodash';

@Component({
  selector: 'app-table-tabs',
  templateUrl: './table-tabs.component.html',
  styleUrls: ['./table-tabs.component.css']
})
export class TableTabsComponent implements OnInit {
  constructor() {}

  @Input() tabsData = [];
  @Input() displayProperty = null;
  @Input() selected = null;
  @Output() onSelectChange = new EventEmitter();

  ngOnInit() {}

  selectTab(tab, index) {
    if (tab === this.selected) {
      return;
    }
    let direction;
    const prevIndex = !!this.selected
      ? _.findIndex(this.tabsData, tabData => {
          return (
            tabData[this.displayProperty] ===
            this.selected[this.displayProperty]
          );
        }) + 1
      : 0;
    direction = index < prevIndex ? 'left' : 'right';

    this.selected = tab;
    this.onSelectChange.emit({
      tab: this.selected,
      index: index,
      direction: direction
    });
  }

  tabSelected(tab) {
    if (!this.selected) {
      return false;
    }
    return this.selected[this.displayProperty] === tab[this.displayProperty];
  }
}
