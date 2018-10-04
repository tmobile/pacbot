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
const fs = require('fs');

export class CompliancePolicy {

    getTitle() {
        return $('.floating-widgets-header h1');
    }

    getPolicyCompliancePercent() {
        return element(by.xpath('//app-policy-details/div/div[2]/div/app-policy-summary/div/div[2]/app-generic-summary/section/div/div[1]/div/div[1]'));
    }

    getMoreArrow() {
        return $('.slider-arrow');
    }

    getViewMore() {
        return $('.navigate-link span');
    }

    getDataLoad() {
        return $('.content-slider-wrapper .content-header');
    }

    getCompliancePercent() {
        return element(by.xpath('//app-policy-summary/div/div[2]/app-generic-summary/section/div/div[1]/div/div[1]'));
    }

    getTotalAssets() {
        return element(by.xpath('//app-policy-summary/div/div[2]/app-generic-summary/section/div/div[2]/div/div[1]'));
    }

    getPassedAssets() {
        return element(by.xpath('//app-policy-summary/div/div[2]/app-generic-summary/section/div/div[3]/div/div[1]'));
    }

    getFailedAssets() {
        return element(by.xpath('//app-policy-summary/div/div[2]/app-generic-summary/section/div/div[4]/div/div[1]'));
    }

    getdownloadIcon() {
        return element(by.css('.contextual-menu-img'));
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

    getPolicyName() {
        return element(by.xpath('//app-policy-details/div/div[1]/div[2]/section/ul/li[4]/div/app-all-policy-violations/div/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[1]/div/a'));
    }

    getIssueId() {
        return element(by.xpath('//app-policy-details/div/div[1]/div[2]/section/ul/li[4]/div/app-all-policy-violations/div/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[2]/div/a'));
    }

    getResourceId() {
        return element(by.xpath('//app-policy-details/div/div[1]/div[2]/section/ul/li[4]/div/app-all-policy-violations/div/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[3]/div/a'));
    }

    resourceIdSort() {
        return element(by.xpath('//app-policy-details/div/div[1]/div[2]/section/ul/li[4]/div/app-all-policy-violations/div/div/app-data-table/div/div[2]/div[1]/div/div[3]'));
    }

    getSecondResourceId() {
        return element(by.xpath('//app-policy-details/div/div[1]/div[2]/section/ul/li[4]/div/app-all-policy-violations/div/div/app-data-table/div/div[2]/div[2]/div/div[2]/div[3]/div/a'));
    }

    getListTableFirstTotal() {
        return element(by.xpath('//app-policy-details/div/div[2]/div/app-policy-across-application/div/section/div[2]/div/div[2]/app-list-table/section/ul/li[2]/ul/li[1]'));
    }

    getListTableFirstPassed() {
        return element(by.xpath('//app-policy-details/div/div[2]/div/app-policy-across-application/div/section/div[2]/div/div[2]/app-list-table/section/ul/li[2]/ul/li[2]'));
    }

    getListTableFirstFailed() {
        return element(by.xpath('//app-policy-details/div/div[2]/div/app-policy-across-application/div/section/div[2]/div/div[2]/app-list-table/section/ul/li[2]/ul/li[3]'));
    }

    getSearchInput() {
        return $('.search-section input.input-text');
    }

    getFirstRowCell() {
        return element(by.xpath('//app-policy-details/div/div[2]/div/app-policy-across-application/div/section/div[2]/div/div[2]/app-list-table/section/ul/li[2]/div'));
    }

    getSearchLabel() {
        return $('label.search-label');
    }

    getTableSearchInput() {
        return $('.search-container input');
    }

    getAllList() {
        return $$('.list-table-inner-wrapper .list-table-each-list');
    }
}
