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

import { Component, OnInit, OnChanges, Input, Output, EventEmitter, SimpleChanges } from '@angular/core';
import { LoggerService } from '../services/logger.service';

@Component({
  selector: 'app-filtered-selector',
  templateUrl: './filtered-selector.component.html',
  styleUrls: ['./filtered-selector.component.css']
})
export class FilteredSelectorComponent implements OnInit, OnChanges {
  countMandatoryFilter = 0;
  constructor(private logger: LoggerService) {}

  @Input() filteredArray: any;
  @Input() mandatoryFilter: any;
  @Output() deleteFilter = new EventEmitter();
  @Output() deleteAllFilters = new EventEmitter();

  @Output() updateFilterArray = new EventEmitter();
  @Input() clearSelectedFilterValue;
  ngOnInit() {
    this.updateComponent();
  }

  ngOnChanges(changes: SimpleChanges) {
    // clear all filter by default - currenlty used in recommendations to reset on ag change
    const toClearValueChange = changes['clearSelectedFilterValue'];
    if (toClearValueChange && !toClearValueChange.firstChange) {
      const cur  = JSON.stringify(toClearValueChange.currentValue);
      const prev = JSON.stringify(toClearValueChange.previousValue);
        if (cur !== prev) {
          this.mandatoryFilter = undefined;
          this.countMandatoryFilter = 0;
          this.clearAll(this.filteredArray);
        }
    } else {
      this.updateComponent();
    }
  }

  updateComponent() {
    // To show clear All text only when optional filters are present.
    if (this.mandatoryFilter && this.mandatoryFilter.includes('|')) {
      this.mandatoryFilter = this.mandatoryFilter.split('|');
    }
    if (this.mandatoryFilter) {
      this.countMandatoryFilter = 0;
      this.filteredArray.forEach(obj => {
        if (this.mandatoryFilter.includes(obj.filterkey)) {
          obj['mandatoryFilter'] = true;
          this.countMandatoryFilter++;
        }
      });
    }
  }

  removeFilter(arr, indx) {
    const obj = {
      array: arr,
      index: indx
    };
    if (this.deleteFilter && this.deleteFilter.observers.length > 0) {
      this.deleteFilter.emit(obj);
    } else {
      this.deleteFilters(obj);
    }
  }

  clearAll(arr) {
    const obj = {
      array: arr,
      clearAll: true
    };
    if (this.deleteAllFilters && this.deleteAllFilters.observers.length > 0) {
      this.deleteAllFilters.emit(obj);
    } else {
      this.deleteFilters(obj);
    }
  }

  deleteFilters(event) {
    try {
      if (!event) {
        this.filteredArray = [];
      } else {
        if (event.clearAll) {
          this.filteredArray = [];
          // Adding again Mandatory filters if found any.
          event.array.forEach(obj => {
            if (obj.hasOwnProperty('mandatoryFilter')) {
              this.filteredArray.push(obj);
            }
          });
        } else {
          this.filteredArray.splice(event.index, 1);
        }
      }
      this.updateFilterArray.emit(this.filteredArray);
    } catch (error) {
      this.logger.log('error', error);
    }
  }
}
