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

import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChange, SimpleChanges,  } from '@angular/core';

@Component({
  selector: 'app-searchable-dropdown',
  templateUrl: './searchable-dropdown.component.html',
  styleUrls: ['./searchable-dropdown.component.css']
})
export class SearchableDropdownComponent implements OnInit, OnChanges {
  @Input() items: any;
  @Input() placeholder;
  @Input() firstDD;
  @Input() initValue;
  @Input() active = [];
  @Input() disable? = false;
  @Output() selection = new EventEmitter();
  @Input() displayKey;
  @Input() dataAvailable;
  @Input() selectOnSingleValue?;
  @Input() clearSelectedFilterValue?;
  secondDDSelected = false;
  placeholderVisible = true;
  @Input() dataModel;
  config = {
    displayKey: 'optionName', // if objects array passed which key to be displayed defaults to description
    search: false, // true/false for the search functionlity defaults to false,
    height: '25em', // height of the list so that if there are more no of items it can show a scroll defaults to auto. With auto height scroll will never appear
    placeholder: this.placeholder, // text to be displayed when no item is selected defaults to Select,
    customComparator: () => {}, // a custom function using which user wants to sort the items. default is undefined and Array.sort() will be used in that case,
    // limitTo: options.length, // a number thats limits the no of options displayed in the UI similar to angular's limitTo pipe
    // moreText: 'more', // text to be displayed whenmore than one items are selected like Option 1 + 5 more
    noResultsFound: 'No data available', // text to be displayed when no items are found while searching
    searchPlaceholder: 'Search' // label thats displayed in search input,
    // searchOnKey: 'name' // key on which search should be performed this will be selective search. if undefined this will be extensive search on all keys
    };

  ngOnInit() {
    if (this.initValue) {
      this.dataModel = this.initValue;
      this.placeholderVisible = false;
    }
    if (this.displayKey) {
      this.config.displayKey = this.displayKey;
    }
    this.setConfig();
  }

  ngOnChanges(changes: SimpleChanges) {
    try {
      const reset = changes['clearSelectedFilterValue'];
      if (reset) {
        // reset value having selected by default - currenlty used in recommendations to reset on ag change
        const cur  = JSON.stringify(reset.currentValue);
        const prev = JSON.stringify(reset.previousValue);
        if (cur !== prev) {
          this.disable = false;
          this.clearFilterTagsDropdown();
          this.placeholderVisible = true;
          this.updateSelectedValue(this.placeholder, this.firstDD);
          this.setConfig();
        }
      } else {
      this.updateProperties();
      if (!this.firstDD && this.secondDDSelected) {
        this.placeholderVisible = true;
        this.updateSelectedValue(this.placeholder, this.firstDD);
      }
      if (this.items.length === 1 && (this.firstDD || this.selectOnSingleValue)) {
        this.displayKey ? this.dataModel = this.items[0][this.displayKey] : this.dataModel = this.items[0];
          this.placeholderVisible = false;
          this.updateSelectedValue(this.dataModel, this.firstDD);
          this.disable = true;
      }
     }
    } catch (error) {
    }
  }

  public selected(value: any ): void {
    // document.getElementsByClassName('search-container')[0].childNodes[1]['value'] = '';
    if (value && value.value) {
      this.selection.emit(value);
      if (typeof value.value === 'object') {
        this.dataModel = value.value[this.displayKey];

      } else {
        this.dataModel = value.value;
      }
      this.placeholderVisible = false;
      this.updateSelectedValue(this.dataModel, this.firstDD);
      setTimeout(() => {
        if (!this.firstDD && this.secondDDSelected) {
          this.placeholderVisible = true;
          this.updateSelectedValue(this.placeholder, this.firstDD);
        }
      }, 100);
    }
    if (!this.firstDD) {
      this.clearFilterTagsDropdown();
      this.secondDDSelected = true;
    }
  }

  updateProperties() {
    this.config.placeholder = this.placeholder;
    this.config.search = !this.firstDD ? true : false;
    if (!this.firstDD && this.secondDDSelected) {
      this.dataModel = [];
    }
    this.setConfig();
  }

  public open(value: any): void {
    // document.getElementsByClassName('search-container')[0].odes[1]['value'] = '';
    if (!this.firstDD && this.secondDDSelected) {
      this.clearFilterTagsDropdown();
      this.placeholderVisible = true;
      this.updateSelectedValue(this.placeholder, this.firstDD);
      this.setConfig();
    }
  }

  public close(value: any): void {
  }

  public selectFromDropdown(value: any): void {
    if (value.keyCode === 13) {
      this.selected({'value': value.target.innerText});

    }
    // this.searchValue = value.target.value;
  }

  public updateSelectedValue(updateValue, firstDD) {
    // below code is for search and select value
    const selectedDoc = document.getElementsByClassName('ngx-dropdown-button');
    if (firstDD) {
    // Commented below code as search is not available in filter type

    // for (let j = 0; j < selectedDoc.length; j++) {
    //   if (selectedDoc[j].nextElementSibling && selectedDoc[j].nextElementSibling.className === 'ngx-dropdown-list-container') {
    //     const selectedText = selectedDoc[j].childNodes;
    //     for (let k = 0; k < selectedText.length; k++) {
    //       if (selectedText[k]['innerHTML']) {
    //         selectedText[k]['innerHTML'] = updateValue;
    //         break;
    //       }
    //     }
    //     break;
    //   }
    // }
  } else {
      const selectedText = selectedDoc[selectedDoc.length - 1].childNodes;
        for (let k = 0; k < selectedText.length; k++) {
          if (selectedText[k]['innerHTML']) {
            selectedText[k]['innerHTML'] = updateValue;
            break;
          }
        }
    }
  }

  setConfig() {
    if (this.items.length > 0) {
      this.config.noResultsFound = 'No data available';
    } else {
      this.config.noResultsFound = 'Loading...';
    }
    if (this.dataAvailable && this.items.length === 0) {
      this.config.noResultsFound = 'No results found';
    }
  }

  clearFilterTagsDropdown() {
    setTimeout(function() {
      const clear = document.getElementsByClassName(
        'nsdicon-close'
      );
      if (clear.length > 0 ) {
        const element: HTMLElement = clear[0] as HTMLElement;
        element.click();
      }
    }, 10);
  }
}
