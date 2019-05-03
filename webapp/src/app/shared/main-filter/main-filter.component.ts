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
  EventEmitter,
  Output,
  ElementRef
} from '@angular/core';
import { LoggerService } from '../../shared/services/logger.service';
import { DataCacheService } from '../../core/services/data-cache.service';
import { UtilsService } from './../services/utils.service';
import { RefactorFieldsService } from '../services/refactor-fields.service';

@Component({
  selector: 'app-main-filter',
  templateUrl: './main-filter.component.html',
  styleUrls: ['./main-filter.component.css'],
  providers: [LoggerService],
  // tslint:disable-next-line:use-host-property-decorator
  host: {
    '(document:click)': 'onClickOutside($event)'
  }
})
export class MainFilterComponent implements OnInit {
  /**
   * @author Trinanjan
   * @type Component
   * @desc  This component paints the omni search filter
   */

  private hideFilter = true;
  @Output() filterOptionClicked: EventEmitter<any> = new EventEmitter();
  @Output() filterOpenEvent: EventEmitter<any> = new EventEmitter();

  /**
   * @type {object}
   * @desc secondaryLevelData,tertiaryLevelData,eachRefineByData holds the data for that level
   */
  secondaryLevelData = {};
  tertiaryLevelData;
  eachRefineByData = {};

  /**
   * @type {object}
   * @desc filterData gets data to paint the entire filter
   * filterQuery is the query Obj that is send to the parent for post request
   */
  filterData: {};
  showLoader = true;
  public errorMessage; // -> error messages
  filterQuery = {}; // after filter options are appplied it is saved in filterQuery
  showError = false; // to show and hide error
  // searchClickedToGetFilter input variable -->it detects whether filter is cliked and api call is done or not
  @Input()
  set searchClickedToGetFilter(value: boolean) {
    this.showLoader = value;
  }
  get searchClickedToGetFilter() {
    return this.showLoader;
  }
  // filterDataChange input variable --> it detects the change in filterdata
  @Input()
  set filterDataChange(value) {
    this.filterData = value;
    this.checkApiReturnValue(this.filterData);
  }
  get filterDataChange() {
    return this.filterData;
  }
  /**
   * @type {number}
   * @desc firstLevelIndex,secondLevelIndex,thirdLevelIndex,fourthLevelIndex
   * holds the index for the respective level ngfor
   * these variable are used to trace a node loaction in the tree
   */

  firstLevelIndex: any;
  secondLevelIndex: any;
  thirdLevelIndex: any;
  fourthLevelIndex: any;

  public checkBoxSelectedCount = new Array<number>();

  constructor(
    private logger: LoggerService,
    private eref: ElementRef,
    private dataStore: DataCacheService,
    private utils: UtilsService,
    private refactorFieldService: RefactorFieldsService
  ) {}

  ngOnInit() {
    try {
      this.onloadSetValues();
    } catch (error) {
      this.logger.log('error', 'js error - ' + error);
    }
  }

  // checks whether data is empty or undefined or proper
  // based on that errormessages/loader values are set
  checkApiReturnValue(DataChange) {
    try {
      if (DataChange) {
        if (this.utils.isObjectEmpty(DataChange)) {
          this.showError = true;
          this.errorMessage = 'noDataAvailable';
        } else if (DataChange.value) {
          if (DataChange.value === 'errorInApiCall') {
            this.showError = true;
            this.errorMessage = 'apiResponseError';
          }
        } else {
          this.showError = false;
          this.secondaryLevelData = {};
          this.tertiaryLevelData = {};
          // added on 04/06/2018 by trinanjan
          // onload setting the data for the second level
          if (
            this.dataStore.get('OmniSearchFirstLevelIndex') === undefined ||
            this.dataStore.get('OmniSearchFirstLevelIndex') === 'undefined'
          ) {
            this.storeFirstLevel(this.filterData['groupBy'].values[0], 0);
          }
        }
      } else {
        this.showError = true;
        this.errorMessage = 'apiResponseError';
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  /**
   *  @func onloadSetValues
   * @description onload the default values are set
   */
  onloadSetValues() {
    this.secondaryLevelData = {};
    this.tertiaryLevelData = {};
    this.eachRefineByData = {};
    if (
      !(
        this.dataStore.get('omnisearchLastAppliedFilter') === undefined ||
        this.dataStore.get('omnisearchLastAppliedFilter') === 'undefined'
      )
    ) {
      this.filterQuery = JSON.parse(
        this.dataStore.get('omnisearchLastAppliedFilter')
      );
      if (
        !(
          this.dataStore.get('OmniSearchFirstLevelIndex') === undefined ||
          this.dataStore.get('OmniSearchFirstLevelIndex') === 'undefined'
        )
      ) {
        this.firstLevelIndex = JSON.parse(
          this.dataStore.get('OmniSearchFirstLevelIndex')
        ).firstLevelIndex;
        this.secondaryLevelData = this.filterData['groupBy'].values[
          this.firstLevelIndex
        ];
      }
      if (
        !(
          this.dataStore.get('OmniSearchSecondLevelIndex') === undefined ||
          this.dataStore.get('OmniSearchSecondLevelIndex') === 'undefined'
        )
      ) {
        this.secondLevelIndex = JSON.parse(
          this.dataStore.get('OmniSearchSecondLevelIndex')
        ).secondLevelIndex;
        const thirdLevelData = this.filterData['groupBy'].values[0][
          'groupBy'
        ].values[this.secondLevelIndex];
        thirdLevelData.groupBy.values.forEach(element => {
          element.displayName = this.refactorFieldService.getDisplayNameForAKey(
            element.name.toLowerCase()
          ) || element.name;
        });
        this.tertiaryLevelData = thirdLevelData;
      }
      if (
        !(this.dataStore.get('omniSearchFilterRefineByCount') === undefined) ||
        this.dataStore.get('omniSearchFilterRefineByCount') === 'undefined'
      ) {
        this.checkBoxSelectedCount = JSON.parse(
          this.dataStore.get('omniSearchFilterRefineByCount')
        );
      }
    }
  }
  /**
   * @function checkAccordionEvent
   *@desc this save acc
   */
  checkAccordionEvent(event) {
  }
  /**
   * @function onClickOutside
   *@desc this function check the click outside filter to close it
   */

  onClickOutside(event) {
    if (!this.eref.nativeElement.contains(event.target)) {
      this.hideFilter = true; // close the filter onclick of outside
      this.filterOpenEvent.emit(this.hideFilter);
    }
  }
  // hideFilter variable is used to show/hide filter
  toggleFilter() {
    this.hideFilter === false
      ? (this.hideFilter = true)
      : (this.hideFilter = false);
    this.filterOpenEvent.emit(this.hideFilter);
  }

  /**
   * @function findAndReplace
   * @param obj The obj which has tobe searched through
   * @param key the Key name whose value will be changed
   * @param val current value
   * @param newVal new value to be update
   * @desc searchs through the entire obj to change a value of particular key(eg: all "applied" : true changes to "applied" : true)
   */
  findAndReplace(obj, key, val, newVal) {
    try {
      const newValue = newVal;
      let objects = [];
      for (const i in obj) {
        if (!obj.hasOwnProperty(i)) {
          continue;
        }
        if (typeof obj[i] === 'object') {
          objects = objects.concat(
            this.findAndReplace(obj[i], key, val, newValue)
          );
        } else if (i === key && obj[key] === val) {
          obj[key] = false;
        }
      }
      return obj;
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /**
   * @function storeFirstLevel
   * @param data
   * @param index
   * @desc this function executes onclick of each firstlevel filter options
   * creats new secondaryLevelData to paint the secondaryLevel filter
   * also filterQuery is update with {"applied": true} for the clicked field
   * filterQuery is emmited to the parent for post request of filter
   */

  storeFirstLevel(data, index, event?) {
    try {
      if (this.utils.isObjectEmpty(this.secondaryLevelData)) {
        if (event) {
          event.stopPropagation();
        }

        this.firstLevelIndex = index;
        /**
         *  First empty all the secondaryLevel,thirdLevel values on click of the primary level
         *  to repaint the 2nd and 3rd level with new values
         */
        this.secondaryLevelData = {};
        this.tertiaryLevelData = {};
        // store the data to paint the secondary filter level
        if (data.groupBy == null || data.groupBy.values.length === 0) {
          this.secondaryLevelData = {};
        } else {
          this.secondaryLevelData = data;
        }
        /**
         * filterQuery is a clone of filterData
         * filterdata is used to repaint the filter UI
         * where as filterQuery is changed based on filteroptions clicked
         */

        this.filterQuery = JSON.parse(JSON.stringify(this.filterData));
        /**
         * whenever any node is clicked
         * search for {"applied" : true} in the same level or deep inside of that node
         * First flip everything to {"applied" : false}
         * Then make the ckicked node {"applied" : true}
         */

        this.filterQuery = this.findAndReplace(
          this.filterQuery,
          'applied',
          true,
          false
        );
        this.filterQuery['groupBy'].values[index].applied = true;
        /**
         * Update the modal for the filter
         * Because to apply active class on the first level
         * "applied = true" field is tested
         */
        this.filterData = this.filterQuery;
        this.dataStore.set(
          'OmniSearchFirstLevelIndex',
          JSON.stringify({ firstLevelIndex: this.firstLevelIndex })
        );
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /**
   * @function checkRadio
   * @param id
   * @desc this function executes onclick of each Third filter options.
   * It closes the open accordion
   */
  checkRadio(id) {
    for (let i = 0; i < this.tertiaryLevelData.groupBy.values.length; i++) {
      if (i !== id) {
        const ele = (<HTMLInputElement>document.getElementById('selectBox' + i));
        ele.checked = false;
      }
    }
  }

  /**
   * @function storeSecondLevel
   * @param data
   * @param index
   * @desc this function executes onclick of each SecondLevel filter options
   * creats new tertiaryLevelData to paint the tertiaryLevelData filter (eg: refineBy)
   * also filterQuery is update with {"applied": true} for the clicked field
   * filterQuery is emmited to the parent for post request of filter
   */

  storeSecondLevel(data, index) {
    try {
      data.groupBy.values.forEach(element => {
        element.displayName = this.refactorFieldService.getDisplayNameForAKey(
          element.name.toLowerCase()
        ) || element.name;
      });
      // empty the checkBoxSelectedCount array to reset chcekbox count value
      this.checkBoxSelectedCount = [];
      this.secondLevelIndex = index;
      /**
       *  First empty all the thirdLevel values on click of the secondary level
       *  to repaint the 3rd level with new values
       * secondaryLevelData will hold the last obj,it should not be emptied
       *
       */
      this.tertiaryLevelData = {};
      // store the data to paint the tertiary filter level
      this.tertiaryLevelData = data;
      if (data.groupBy == null || data.groupBy.values.length === 0) {
        this.tertiaryLevelData = {};
      } else {
        this.tertiaryLevelData = data;
      }
      /**
       * whenever any node is clicked
       * search for {"applied" : true} in the same level or deep inside of that node
       * First flip everything to {"applied" : false}
       * Then make the ckicked node {"applied" : true}
       */
      this.findAndReplace(
        this.filterQuery['groupBy'].values[this.firstLevelIndex]['groupBy']
          .values,
        'applied',
        true,
        false
      );
      this.filterQuery['groupBy'].values[this.firstLevelIndex][
        'groupBy'
      ].values[this.secondLevelIndex].applied = true;
      this.filterOptionClicked.emit(this.filterQuery);
      this.dataStore.set(
        'OmniSearchSecondLevelIndex',
        JSON.stringify({ secondLevelIndex: this.secondLevelIndex })
      );
      this.dataStore.clear('omniSearchFilterRefineByCount');
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /**
   * @function storeIndex
   * @param data
   * @param index
   * @desc this function just stores the index globally for that particular node
   * Third level has nested ngFor (refined by options are combined together)
   */

  storeIndex(data, index) {
    try {
      this.thirdLevelIndex = index;
      // this.dataStore.set("OmniSearchThirdLevelIndex",JSON.stringify({'thirdLevelIndex':this.thirdLevelIndex}))
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /**
   * @function storeThirdLevel
   * @param data
   * @param index1
   * @param index2
   * @desc this function gets the parent and child ngFor index along with input checkbox checked event(true/false)
   * also filterQuery is update with {"applied": true} for the clicked field
   * filterQuery is emmited to the parent for post request of filter
   */

  storeThirdLevel(data, index1, index2, checkBoxChecked) {
    try {
      this.thirdLevelIndex = index1;
      this.fourthLevelIndex = index2;
      // store the data for future reference
      this.eachRefineByData = data;

      /**
       * As in this level we can select mutiple filter options
       * all {"applied" : true} fields for this level doesn't have to be replaced by {"applied" : false}
       * depending on the checkBoxChecked value {"applied" : true}/{"applied" : false} options are set
       */
      this.filterQuery['groupBy'].values[this.firstLevelIndex][
        'groupBy'
      ].values[this.secondLevelIndex]['groupBy'].values[
        this.thirdLevelIndex
      ].values[this.fourthLevelIndex].applied = checkBoxChecked;
      this.filterOptionClicked.emit(this.filterQuery);
      /**
       * call the numOfCheckedCount func to find out the num of checkbox true for that particular
       * refined by criteria Obj
       */
      this.numOfCheckedCount(
        this.filterQuery['groupBy'].values[this.firstLevelIndex]['groupBy']
          .values[this.secondLevelIndex]['groupBy'].values[this.thirdLevelIndex]
          .values,
        index1
      );
      this.dataStore.set(
        'OmniSearchThirdLevelIndex',
        JSON.stringify({ thirdLevelIndex: this.thirdLevelIndex })
      );
      this.dataStore.set(
        'OmniSearchFourthLevelIndex',
        JSON.stringify({ fourthLevelIndex: this.fourthLevelIndex })
      );
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /**
   * @function numOfCheckedCount
   * @param data
   * @param index1
   * @desc this function counts the Number of checkbox checked
   * based on the refined by obj index count is stored in checkBoxSelectedCount array
   * the same count is shown in the html tracked by index
   */

  numOfCheckedCount(data, index) {
    // eachCategoryCheckBoxCount is local count for the number of checkbox selected for the obj
    let eachCategoryCheckBoxCount = 0;
    data.forEach(item => {
      if (item.applied === true) {
        eachCategoryCheckBoxCount = eachCategoryCheckBoxCount + 1;
      }
    });
    // Final eachCategoryCheckBoxCount value is stored in the checkBoxSelectedCount array traked By parent indexvalue
    this.checkBoxSelectedCount[index] = eachCategoryCheckBoxCount;
    this.dataStore.set(
      'omniSearchFilterRefineByCount',
      JSON.stringify(this.checkBoxSelectedCount)
    );
  }
}
