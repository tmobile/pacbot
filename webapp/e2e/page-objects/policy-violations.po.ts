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
import { CONFIGURATIONS } from './../../src/config/configurations';

const domain = CONFIGURATIONS.optional.general.e2e.DOMAIN;

const fs = require('fs');

export class PolicyViolations {

  navigateToPolicyViolations() {
    return browser.driver.get(domain + '/post-login/(compliance/issue-listing)?ag=aws-all&domain=Infra%20%26%20Platforms');
  }

  getpolicyviolationscount() {
    return element(by.xpath('//div/app-data-table/div/div[3]/div[1]/span[2]'));
  }

  getAssetTotalRows() {
    return element(by.className('total-rows'));
  }

  getPolicyViolationsHeaderText() {
    return element(by.xpath('//div/h1[text()=\'Policy Violations\']'));
  }

  getIssueIdHeaderText() {
    return element(by.xpath('//app-data-table/div/div[2]/div[1]/div/div[1]'));
  }

  getPolicyNameHeaderText() {
    return element(by.xpath('//app-data-table/div/div[2]/div[1]/div/div[2]'));
  }

  getResourceIdHeaderText() {
    return element(by.xpath('//app-data-table/div/div[2]/div[1]/div/div[3]'));
  }

  getIssueIdFirstRowValue() {
    return element(by.xpath('//app-data-table/div/div[contains(@class, \'data-table-content\')]/div[contains(@class,\'data-table-inner-wrap\')]/div/div[1]/div[contains(@class,\'row-cells\')]/div[1]/a'));
  }

  getPolicyNameFirstRowValue() {
    return element(by.xpath('//app-data-table/div/div[contains(@class, \'data-table-content\')]/div[contains(@class,\'data-table-inner-wrap\')]/div/div[1]/div[2]/div[1]/a'));
  }

  getResourceIdFirstRowValue() {
    return element(by.xpath('//app-data-table/div/div[contains(@class, \'data-table-content\')]/div[contains(@class,\'data-table-inner-wrap\')]/div/div[1]/div[3]/div[1]/a'));
  }

  getSeverityFirstRowValue() {
    return element(by.xpath('//app-data-table/div/div[contains(@class, \'data-table-content\')]/div[contains(@class,\'data-table-inner-wrap\')]/div/div[1]/div[4]/div[1]'));
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

  getRuleIdFirstRowValue() {
    return element(by.xpath('//app-data-table/div/div[contains(@class, \'data-table-content\')]/div[contains(@class,\'data-table-inner-wrap\')]/div/div[1]/div[5]/div[1]'));
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

}
