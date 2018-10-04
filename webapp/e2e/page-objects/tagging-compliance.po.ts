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

export class TaggingCompliance {

  navigateToTaggingget() {
    return browser.driver.get(domain + '/post-login/(compliance/tagging-compliance)?ag=aws-all&domain=Infra%20%26%20Platforms');
  }

  navigateToTaggingCompliance() {
    return element(by.xpath('//app-compliance/div/div/div[1]/app-contextual-menu/div/ul/li[3]'));
  }
  getTagHeaderText() {
     return element(by.xpath('//div/h1[text()="Tagging"]'));
  }

  getOverallAssets() {
    return element(by.xpath('//div/div[2]/div/app-tagging-summary/div/div[2]/app-generic-summary/section/div/div[2]/div/div[1]'));
  }
  getOverallTagging() {
    return element(by.xpath('//div/div[2]/app-generic-summary/section/div/div[3]/div/div[1]'));
  }

  getOverallunTagging() {
     return element(by.xpath('//div/div[2]/app-generic-summary/section/div/div[4]/div/div[1]'));
  }
  openHelp() {
    return $('.sub-heading .help-text-container');
  }
  getHelpTitle() {
    return $('.help-content .help-title');
  }
  getHelpClose() {
    return element(by.xpath('//app-root/app-post-login-app/app-help-text/div/div[1]/img'));
  }
  getAssetTotalRows() {
    return element(by.className('total-rows'));
  }
  getdownloadIcon() {
    return element(by.css('.contextual-menu-img img'));
  }
  getToastMsg() {
    return element(by.css('.toast-msg'));
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
  getDownloadRunningIcon() {
    return element(by.css('.pacman-anim img'));
  }
  getTaggingPercent() {
    return element(by.xpath('//app-tagging-summary/div/div[2]/app-generic-summary/section/div/div[1]/div/div[1]'));
  }
  getTotalTaggingPercent() {
    // return element(by.className('title-percent anim-percent after-load'));
    return element(by.xpath('//app-compliance/div/div/div[2]/app-tagging-compliance/div/div[2]/div/app-total-tag-compliance/div/div[2]/app-multi-band-donut/div/div[1]/div[1]/div[1]'));
  }
  getUntaggedheader() {
    return element(by.xpath('//app-total-tag-compliance/div/div[3]/app-list-table/section/ul/li[1]/ul/li[2]'));
  }
  getListTableFirstTotal() {
    return element(by.xpath('//app-tagging-compliance/div/div[2]/div/app-total-tag-compliance/div/div[3]/app-list-table/section/ul/li[2]/ul/li[1]'));
  }
  getListTableSecondTotal() {
    return element(by.xpath('//app-tagging-compliance/div/div[2]/div/app-total-tag-compliance/div/div[3]/app-list-table/section/ul/li[2]/ul/li[2]'));
  }
  getAllList() {
     return $$('.list-table-inner-wrapper .list-table-each-list');
    }
    getAllListUntaggedAssetsTable() {
      return $$('.data-table-inner-wrap .data-table-rows');
    }
  getSearchLabel() {
    return element(by.css('.search-label'));
    }
  getSearchInput() {
    return element(by.css('.header-search-input'));
    }
  getFirstStatusRow() {
    return element(by.xpath('//app-data-table/div/div[2]/div[2]/div/div[1]/div[1]/div/div'));
    }
  getTableHeader() {
     return element(by.xpath('//app-data-table/div/div[1]/app-title-burger-head/div/div[1]/div'));
    }
  getApplicationHeader() {
    return element(by.xpath('//app-data-table/div/div[2]/div[1]/div/div[1]'));
    }
  getEnvironmentUntagged() {
    return element(by.xpath('//app-data-table/div/div[2]/div[1]/div/div[2]'));
    }
  getRoleUntagged() {
    return element(by.xpath('//app-data-table/div/div[2]/div[1]/div/div[3]'));
  }
  getStackUntagged() {
    return element(by.xpath('//app-data-table/div/div[2]/div[1]/div/div[4]'));
  }
  getEnvironmentUntaggedFirstRowValue() {
    return element(by.xpath('//app-data-table/div/div[contains(@class, \'data-table-content\')]/div[contains(@class,\'data-table-inner-wrap\')]/div/div[1]/div[contains(@class,\'row-cells\')]/div[1]/a'));
  }

  getRoleFirstRowValue() {
    return element(by.xpath('//app-data-table/div/div[contains(@class, \'data-table-content\')]/div[contains(@class,\'data-table-inner-wrap\')]/div/div[1]/div[2]/div[1]/a'));
  }

  getStackFirstRowValue() {
    return element(by.xpath('//app-data-table/div/div[contains(@class, \'data-table-content\')]/div[contains(@class,\'data-table-inner-wrap\')]/div/div[1]/div[3]/div[1]/a'));
  }
  getApplicationtagHeader() {
    return element(by.xpath('//app-total-tag-compliance/div/div[3]/app-list-table/section/ul/li[2]/div[1]'));
  }
  getEnvironmentUntaggedassetsbyApp() {
    return element(by.xpath('//app-tagging-instances-table/div/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[2]/div/a'));
  }
  getAdditionaldetailsCrossMark() {
    return element(by.xpath('//app-data-table/div/div[4]/div[1]/img'));
  }

  getAdditionalDetailsHeaderText() {
    return element.all(by.css('.header-text')).get(2);
  }
  getApplicationfirstValue(){
    return element(by.xpath('//app-tagging-instances-table/div/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[1]/div/div'));
  }
  getAdditionalDetailsenv(){
    return element(by.xpath('//app-tagging-instances-table/div/div/app-data-table/div/div[4]/div[2]/div[2]/div[2]/a'))
  }

}
