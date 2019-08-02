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


import { Component, Input, OnChanges, SimpleChanges, Output, EventEmitter, ViewChild, ElementRef, Renderer, OnInit } from '@angular/core';

@Component({
  selector: 'app-table-list',
  templateUrl: './table-list.component.html',
  styleUrls: ['./table-list.component.css']
})

export class TableListComponent implements OnChanges, OnInit {

  @ViewChild('tableInp') tableInp: ElementRef;

  checkValue = false;
  selectedValue;
  @Input() DataObject;
  @Input() errorValue;
  @Input() errorMessage;
  @Input() keysToSkip;
  @Input() clickableKeys;
  @Input() headersWithHelp;
  @Input() direction = 0;
  @Input() headerColName;
  rowIndex = -1;
  sortArr: any = [];
  header;
  currentLength = 0;
  searchTxt = '';
  @Output() firstRowClick = new EventEmitter();
  @Output() showHelpContent = new EventEmitter();

  constructor(
    private renderer: Renderer) {
  }

  /**
   * Toggles the expanded section for each row.
   * @param {integer}   listItem Index of row that will be expanded.
   */
  getToggle(value) {
    this.checkValue = !this.checkValue;
    this.selectedValue = value;
  }

  ngOnInit() {
    this.getDataList();
  }

  /**
   * List out keys of the object
   * @param {object}
   * @return {array} Array with object keys.
   */
  objectKeys(obj) {
    return Object.keys(obj);
  }

  /**
   * Determines if certain keys from data object should be displayed as a column.
   * @param {string}   listItem Header name of the column
   * @return {boolean}
   */
  displayListItem(listItem) {
    if (!this.keysToSkip) { return true; }
    return this.keysToSkip.includes(listItem) ? false : true;
  }

  /**
   * Determines if items in table columns should be clickable or not.
   * @param {string}   listItem Header name of the column
   * @return {boolean}
   */
  clickableColumn(listItem) {
    if (!this.clickableKeys) {return false; }
    return this.clickableKeys.includes(listItem);
  }

  /**
   * Determines if help icon should be shown with table header
   * @param {string}   headerItem Header name of the column
   * @return {boolean}
   */
  headerWithHelp(headerItem) {
    if (!this.headersWithHelp) {return false; }
    return this.headersWithHelp.includes(headerItem);
  }

  /**
   * Emits out event that help icon is clicked.
   * @param {event}  event Click event
   * @param {string}   helpIcon Header name of the column which help icon is clicked.
   */
  helpClicked(event, helpIcon) {
    event.stopPropagation();
    this.showHelpContent.emit(helpIcon);
  }

  /**
   * Sorts the table order
   * @param {integer}  index Index of the column with which the table will be sorted.
   * @param {string}   header Header of the column with which the table will be sorted.
   */
  headerClicked(index, header) {
    this.rowIndex = -1;
    for (let i = 0; i < this.sortArr.length; i++) {
      if (i !== index) {
        this.sortArr[i].showUp = undefined;
        this.sortArr[i].direction = 0;
      } else {
        if (this.sortArr[i].direction === 0) {
          this.sortArr[i].direction = 1;
        } else {
          this.sortArr[i].direction = this.sortArr[i].direction * -1;
        }
        this.direction = this.sortArr[i].direction;
      }
    }
    this.headerColName = header;
  }

  getDataList() {
    if (this.DataObject.header) {
      this.DataObject.header.forEach((element, index) => {
        this.sortArr[index] = {
          'showUp': undefined,
          'direction': 0
        };
      });
    }
  }

  downloadCsv() {
  }

  /**
   * Emits out event with the row object after certain cell is clicked.
   * @param {object}   dataObject Data object that will be emitted.
   * @param {string}   key Title of column in which item was clicked.
   */
  getRuleClick(dataObject, key) {
    if (key) {
      dataObject.selectedKey = key;
    }
    this.firstRowClick.emit(dataObject);
  }

  ngOnChanges(changes: SimpleChanges) {

    if (this.tableInp) {
      this.renderer.invokeElementMethod(this.tableInp.nativeElement, 'focus');
    }

    // const DataChanges = changes['DataObject'];
    // if (DataChanges) {
    //   const cur = JSON.stringify(DataChanges.currentValue);
    //   const prev = JSON.stringify(DataChanges.previousValue);
    //   if ((cur !== prev)) {
    //     this.getDataList();
    //   }
    // }
  }

}
