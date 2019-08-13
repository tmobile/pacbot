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

import { Component, AfterViewInit, Input, EventEmitter, Output, ViewChild, ElementRef, Renderer } from '@angular/core';
import { DataCacheService } from '../../core/services/data-cache.service';
import { LoggerService } from '../../shared/services/logger.service';

@Component({
  selector: 'app-searchbar-dropdown',
  templateUrl: './searchbar-dropdown.component.html',
  styleUrls: ['./searchbar-dropdown.component.css'],
  providers: [LoggerService]
})

export class SearchbarDropdownComponent implements AfterViewInit {
  /*
    ***************  Component details  **********************
    *This component is a combination of filter dropdown with a search button
    *Accepts the below parameters(*it accepts fontsize from the calling component to manupulate the entire size)
    ************ Component details ends here  ******************
   */

  @ViewChild('omniInp') omniInp: ElementRef;

  @Input() dropdownData: any; // --> stores the dropdown for search category
  @Input() searchboxPlaceHolder: any; // --> search box placeholder
  @Input() searchboxValueSelected: any; // -->
  @Input() dropDownSelectedValue: string; // --> onload the dropdwon is preselected with this value
  @Input() showTerminatedCheckBox: any; // --> show/hide terminated checkbox
  @Input() displaySearchDropDown: boolean; // -->   show/hide the dropdown
  @Output() navigatePage: EventEmitter<any> = new EventEmitter(); // -->emits the selected searchtext/category/checkbox event
  selectedFilterType = {}; // --> search category;by default it has dropDownSelectedValue as default value
  formEmpty = true; // --> checkd if the searchText is empty as api requires mandetary searchtext
  terminatedIsChecked: boolean; // --> holds the state of checkbox
  assetSelected = false; // -->shows/hides the terminated checkbox based on search category dropdown
  searchBtnActive = false; // -->highlights the searchBtn if new text/category is selected
  constructor(
    private dataStore: DataCacheService,
    private logger: LoggerService,
    private renderer: Renderer
  ) {}

  ngAfterViewInit() {
    try {

      if (this.omniInp) {
        this.renderer.invokeElementMethod(this.omniInp.nativeElement, 'focus');
      }
      // Onload passing same value as the selected value
      this.selectedFilterType = {
        id: this.dropDownSelectedValue,
        value: this.dropDownSelectedValue
      };
      // Checking if the terminated check box is already checked
      if (this.dropDownSelectedValue.toLocaleLowerCase() === 'assets') {
        this.assetSelected = true;
      }
      if (
        !(
          this.dataStore.get('terminated-cliked') === undefined ||
          this.dataStore.get('terminated-cliked') === 'undefined'
        )
      ) {
        this.terminatedIsChecked = JSON.parse(
          this.dataStore.get('terminated-cliked')
        );
      } else {
        this.terminatedIsChecked = false;
      }
      // Calling the function to validate if the user should be able to search
      if (
        !this.dataStore.getSearhCriteria(
          this.selectedFilterType['value'],
          this.searchboxValueSelected,
          this.terminatedIsChecked
        )
      ) {
        this.searchBtnActive = true;
      }
      this.dataStore.setSearhCriteria(
        this.selectedFilterType['value'],
        this.searchboxValueSelected,
        this.terminatedIsChecked
      );
      this.checkFieldEmpty();
    } catch (error) {
      this.logger.log('error', error);
    }
  }
  /**
   * @func changeFilterType
   * @value gets the value of search category dropdown
   * @desc this function runs on dropdown select and checks whether assets is selected
   * based on that we show or hide the terminated checkbox
   */

  changeFilterType(value) {
    try {
      this.selectedFilterType = JSON.parse(JSON.stringify(value));
      if (!(this.selectedFilterType['value'].toLowerCase() === 'assets')) {
        this.terminatedIsChecked = false;
        this.assetSelected = false;
      } else {
        this.assetSelected = true;
      }
      if (
        !this.dataStore.getSearhCriteria(
          this.selectedFilterType['value'],
          this.searchboxValueSelected,
          this.terminatedIsChecked
        )
      ) {
        this.searchBtnActive = true;
      }

      this.dataStore.setSearhCriteria(
        this.selectedFilterType['value'],
        this.searchboxValueSelected,
        this.terminatedIsChecked
      );
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /**
   * @func changeFilterType
   * @author Trinanjan/add on 29/05/2018
   * @desc this function checks if the the input search box field is empty
   */

  checkFieldEmpty() {
    try {
      if (this.searchboxValueSelected === '') {
        this.formEmpty = true;
      } else {
        this.formEmpty = false;
      }
      if (this.searchboxValueSelected === '') {
        this.searchBtnActive = false;
      } else if (
        !this.dataStore.getSearhCriteria(
          this.selectedFilterType['value'],
          this.searchboxValueSelected,
          this.terminatedIsChecked
        )
      ) {
        this.searchBtnActive = true;
      }
      this.dataStore.setSearhCriteria(
        this.selectedFilterType['value'],
        this.searchboxValueSelected,
        this.terminatedIsChecked
      );
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /**
   * @func CallTerminatedInstances
   * @author Trinanjan/add on 29/05/2018
   * @desc checks whether the terminated checked is checked or not
   */

  CallTerminatedInstances(Event) {
    try {
      this.terminatedIsChecked = Event;
      if (
        !this.dataStore.getSearhCriteria(
          this.selectedFilterType['value'],
          this.searchboxValueSelected,
          this.terminatedIsChecked
        )
      ) {
        this.searchBtnActive = true;
      }
      this.dataStore.setSearhCriteria(
        this.selectedFilterType['value'],
        this.searchboxValueSelected,
        this.terminatedIsChecked
      );
      // Not storing the value of terminatedIsChecked here as it should be saved after the user clicks the searchBtn
    } catch (e) {
      this.logger.log('error', e);
    }
  }
  /*
   * @func instructParentToNavigate
   * @author Trinanjan/add on 29/05/2018
   * @desc this function emits the data on search clicked to the parent
   */
  instructParentToNavigate() {
    try {
      // check if the btn is disabled
      if (!this.formEmpty) {
        if (
          !this.dataStore.getSearhCriteria(
            this.selectedFilterType['value'],
            this.searchboxValueSelected,
            this.terminatedIsChecked
          )
        ) {
          this.searchBtnActive = true;
        } else {
          this.searchBtnActive = false;
        }
        const emitData = {
          filterValue: this.searchboxValueSelected,
          searchValue: this.selectedFilterType,
          terminatedIsChecked: this.terminatedIsChecked
        };
        // Storing the terminatedIsChecked in cache as it is one of the criteria to load data from cache
        this.dataStore.set('terminated-cliked', this.terminatedIsChecked);
        this.navigatePage.emit(emitData);
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }
}
