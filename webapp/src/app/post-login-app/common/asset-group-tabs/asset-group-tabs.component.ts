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

import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit } from '@angular/core';
import { UtilsService } from '../../../shared/services/utils.service';
import { Router } from '@angular/router';
import { LoggerService } from '../../../shared/services/logger.service';

@Component({
  selector: 'app-asset-group-tabs',
  templateUrl: './asset-group-tabs.component.html',
  styleUrls: ['./asset-group-tabs.component.css']
})
export class AssetGroupTabsComponent implements OnInit, AfterViewInit {

  constructor(private utilService: UtilsService,
              private router: Router,
              private logger: LoggerService) { }

  @Input()  assetTabName: any;
  @Input() selectedTabName: any;
  selectedTab = 0;
  displayMore = false;
  moreArray: any = [];
  clickedAsset: number;
  loaded: boolean;
  clickedShow: boolean;
  selectItemIndex: any;
  thisAssetTile: any;
  assetTempTabName: any;
  showMorePop = false;
  @Output() updateTabs: EventEmitter<string> = new EventEmitter<string>();

  ngOnInit() {
    this.assetTempTabName = this.assetTabName;
  }

  ngAfterViewInit() {
    this.calcMore();
  }

  calcMore() {
    setTimeout(() => {
      try {
        const tempArrayStr = JSON.stringify(this.assetTabName);
        this.assetTempTabName = JSON.parse(tempArrayStr);

        this.displayMore = false;
        const outerWrap = document.getElementsByClassName('asset-tabs-outer')[0];
        const innerTabs = document.getElementsByClassName('tabs-enclosure');
        let remainingSpace = outerWrap.clientWidth;
        const moreSpace = 60;
        const moreArr = [];
        let flag = false;
        let calcMore = true;
        let count = 0;

        // function to form the More section array
        for ( let i = 0; i < innerTabs.length; i++) {
          if (innerTabs[i].clientWidth >= remainingSpace || flag) {
            flag = true;
            count++;
            this.displayMore = true;
            moreArr.push(this.assetTempTabName[i]);
          } else {
            if (innerTabs[i].clientWidth < remainingSpace) {
              remainingSpace = remainingSpace - innerTabs[i].clientWidth;
            }
          }
        }

        // function to remove moreArray elements from main array
        for ( let j = 0; j < count; j++) {
          this.assetTempTabName.pop();
        }

        // function to assign space to 'More' tab and replace any elements wrapped due to shortage of space in main array,
        // to moreArray

        while (calcMore) {
            if (remainingSpace > moreSpace) {
              calcMore = false;
            } else {
              const popLast = this.assetTempTabName[this.assetTempTabName.length - 1];
              remainingSpace = remainingSpace + innerTabs[this.assetTempTabName.length - 1].clientWidth;
              this.assetTempTabName.pop();
              moreArr.push(popLast);
            }
        }
        this.moreArray = moreArr;

        const selectedTabNameAvailability = this.utilService.findValueInArray(this.assetTabName, this.selectedTabName);
        if (selectedTabNameAvailability < 0) {
          this.selectedTabName = this.assetTabName[0];
        }
        const selectedTabPartOfMoreSection = this.utilService.findValueInArray(this.moreArray, this.selectedTabName);
        if (selectedTabPartOfMoreSection >= 0 ) {
          this.selectCategoryFromMore(null, this.selectedTabName);
        } else {
          this.tabsClicked(this.selectedTabName);
        }
      } catch (error) {
        this.logger.log('error', 'js error - ' + error);
      }
    }, 10);

  }

  selectCategoryFromMore($event, tabName) {
    if ($event) {
      $event.stopPropagation();
    }
    this.selectedTabName = tabName;
    this.showMorePop = false;
    this.selectedTab = -1;
    this.tabsClicked(this.selectedTabName);
  }

  tabsClicked(tabName) {
    this.router.navigate([], {queryParamsHandling: 'merge', fragment: tabName});
    /* Changing the fragment of the url and not emitting the selected category */
    // this.updateTabs.emit(tabName);
  }
}
