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

import { browser, by, element, $, $$ } from 'protractor';
import { CONFIGURATIONS } from './../../src/config/configurations';

const domain = CONFIGURATIONS.optional.general.e2e.DOMAIN;

const fs = require('fs');


export class AssetList {

  navigateToAssetListGet() {
    return browser.driver.get(domain + '/pl/(assets/asset-list)?ag=aws-all&domain=Infra%20%26%20Platforms');
  }

  getAssetHeaderText() {
    return element(by.xpath('//div/h1[text()="Asset List"]'));
  }

  getAssetTotalRows() {
    return element(by.className('total-rows'));
  }

  getBackArrowEle() {
    return element(by.xpath('//div/div/div[2]/app-asset-list/div/div[1]/div[1]/img'));
  }

  getBackArrow() {
    return element(by.xpath('//div/div/div[2]/app-asset-list/div/div[1]/div[1]/img'));
  }

  getResourceIdHeaderText() {
    return element(by.xpath('//app-data-table/div/div[2]/div[1]/div/div[1]'));
  }

  getTargetTypeHeaderText() {
    return element(by.xpath('//app-data-table/div/div[2]/div[1]/div/div[4]'));
  }

  getResourceIdFirstRowValue() {
    return element(by.xpath('//app-data-table/div/div[2]/div[2]/div/div[1]/div[1]/div/a'));
  }

  getTargetTypeFirstRowValue() {
    return element(by.xpath('//app-data-table/div/div[2]/div[2]/div/div[1]/div[4]/div/div'));
  }

  getAdditionalDetailsHeaderText() {
    return element.all(by.css('.header-text')).get(1);
  }

  getSearchInput() {
    return element(by.css('.header-search-input'));
  }

  getSearchLabel() {
    return element(by.css('.search-label'));
  }

  getAdditionaldetailsCrossMark() {
    return element(by.xpath('//app-data-table/div/div[4]/div[1]/img'));
  }

  getFilterArrow() {
    return element(by.css('.drop-options .ui-select-container.dropdown'));
  }

  getFilterType() {
    return element(by.css('.drop-options .ui-select-container.dropdown.open .ui-select-choices-row.active .dropdown-item div'));
  }

  getFilterTags() {
    return element(by.css('.drop-search-box .ui-select-container.dropdown.open .ui-select-choices-row.active .dropdown-item div'));
  }

  getFilterTagInput() {
    return element(by.css('.drop-search-box .ui-select-container.dropdown'));
  }

  getFilterSelected() {
    return element(by.css('.each-filter'));
  }

  getClearAllFilter() {
    return element(by.css('.clear-filter'));
  }

  verifyFilterTagValues() {
    return element(by.css('.drop-search-box .ui-select-container.dropdown .ui-select-match'));
  }

  getdownloadIcon() {
    return element(by.css('.contextual-menu-img img'));
  }

  getToastMsg() {
    return element(by.css('.toast-msg'));
  }

  getDownloadRunningIcon() {
    return element(by.css('.pacman-anim img'));
  }

  checkDirExists(aPath) {
    try {
      return fs.statSync(aPath).isDirectory();
    } catch (e) {
      if (e.code === 'ENOENT') {
        return false;
      } else {
        throw e;
      }
    }
  }

  getDetailsofAsset() {
    return element(by.xpath('//app-assets/div/div/div[2]/app-asset-list/div/div[2]/section/ul/li/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[1]/div')).click();
  }

  getDetailsInsidePage() {
      return $('.floating-widgets-header.assets-header h1');
  }

  goBack() {
    return $('.floating-widgets-header img.arrow-img');
  }

  getFirstFilter() {
    return element(by.xpath('//app-asset-list/div/div[2]/app-filtered-selector/div/div[1]'));
  }

  getAllFilters() {
    return $$('.floating-widgets-filter-wrapper .each-filter');
  }
}
