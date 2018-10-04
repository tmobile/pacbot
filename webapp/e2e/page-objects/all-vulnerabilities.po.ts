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

import { browser, by, element, $ } from 'protractor';
const fs = require('fs');

export class AllVulnerabilities {

  getOverallVulnerabilities() {
    return element(by.xpath('//div/app-vulnerability-overall/div/div[2]/div[1]/div[2]/div[1]'));
  }

  getAllVulHeaderText() {
    return element(by.xpath('//div/h1[text()="All Vulnerabilities"]'));
  }

  getAssetAffectedItem() {
    return element(by.xpath('//div/div[4]/div/a'));
  }

  getAssetHeaderText() {
    return element(by.xpath('//div/h1[text()="Asset List"]'));
  }

  getAssetTotalRows() {
    return element(by.className('total-rows'));
  }

  getBackArrow() {
    return $('.floating-widgets-header img.arrow-img');
  }

  getTableTotal() {
    return $('span.total-rows');
  }

  getFilters() {
    return $('.floating-widgets-filter-wrapper .each-filter');
  }

  getPolicyTitle() {
    return element(by.xpath('//app-vulnerabilities/div/div[2]/section/ul/li/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[1]/div/a'));
  }

  getNextPolicyTitle() {
    return element(by.xpath('//app-vulnerabilities/div/div[2]/section/ul/li/div/app-data-table/div/div[2]/div[2]/div/div[2]/div[1]/div/a'));
  }

  getQid() {
    return element(by.xpath('//app-vulnerabilities/div/div[2]/section/ul/li/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[2]/div/a'));
  }

  getAssetsAffected() {
    return element(by.xpath('//app-vulnerabilities/div/div[2]/section/ul/li/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[4]/div/a'));
  }

  getSearchInput() {
    return $('.sub-head .header-search-input');
  }

  getSearchLabel() {
    return $('.sub-head .search-label');
  }

  policyTitleSort() {
    return element(by.xpath('//app-vulnerabilities/div/div[2]/section/ul/li/div/app-data-table/div/div[2]/div[1]/div/div[1]'));
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

  additionalDetails() {
    return element(by.xpath('//app-vulnerabilities/div/div[2]/section/ul/li/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[3]'));
  }

  additionalDetailsTxt() {
    return $('.details-bar .header-text');
  }

  additionalDetailsClose() {
    return $('.details-bar .details-bar-header img.pointer');
  }

}
