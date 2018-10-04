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

export class PatchingCompliance {

    navigateToPatchingComplianceget() {
        return browser.driver.get(domain + '/pl/compliance/patching-compliance?ag=aws-all&domain=Infra%20%26%20Platforms');
    }

    navigateToPatchingCompliance() {
        return element(by.xpath('//div[contains(@class, "contextual-menu-wrapper")]//a[text()="Patching Compliance"]'));
    }

    getPatchingHeaderText() {
        return $('.floating-widgets-header h1.title-text');
    }

    getOverallPatching() {
        return element(by.xpath('//div/app-generic-summary/section/div/div[2]/div/div[1]'));
    }

    getUnPatching() {
        return element(by.xpath('//div/app-generic-summary/section/div/div[4]/div/div[1]'));
    }

    getPatching() {
        return element(by.xpath('//div/app-generic-summary/section/div/div[3]/div/div[1]'));
    }

    getTableTotal() {
        return $('.all-patch-container span.total-rows');
    }

    getSearchInput() {
        return $('.all-patch-container input.header-search-input');
    }

    getFirstRowCell() {
        return element(by.xpath('//app-all-patching-table/div/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[2]/div/div'));
    }

    getSecondRowCell() {
        return element(by.xpath('//app-all-patching-table/div/div/app-data-table/div/div[2]/div[2]/div/div[2]/div[2]/div/div'));
    }

    getTableSort() {
        return element(by.xpath('//app-all-patching-table/div/div/app-data-table/div/div[2]/div[1]/div/div[2]'));
    }

    getSearchLabel() {
        return $('.all-patch-container label.search-label');
    }

    additionalDetailsTxt() {
        return element(by.xpath('//app-patching-compliance/div/div[1]/div[2]/section/ul/li[2]/div/app-all-patching-table/div/div/app-data-table/div/div[4]/div[1]/app-title-burger-head/div/div[1]/div'));
    }

    additionalDetailsClose() {
        return element(by.xpath('//app-all-patching-table/div/div/app-data-table/div/div[4]/div[1]/img'));
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

    getResourceId() {
        return element(by.xpath('//app-all-patching-table/div/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[1]/div/a'));
    }

    getTotalAssets() {
        return element(by.xpath('//app-patching-issue/div/div/app-generic-summary/section/div/div[2]/div/div[1]'));
    }

    getPatchedAssets() {
        return element(by.xpath('//app-patching-issue/div/div/app-generic-summary/section/div/div[3]/div/div[1]'));
    }

    getUnPatchedAssets() {
        return element(by.xpath('//app-patching-issue/div/div/app-generic-summary/section/div/div[4]/div/div[1]'));
    }

    getPatchedPercent() {
        return element(by.xpath('//app-patching-issue/div/div/app-generic-summary/section/div/div[1]/div/div[1]'));
    }

    openHelp() {
        return $('.sub-head .help-text-container');
    }

    getHelpTitle() {
        return $('.help-content .help-title');
    }

    getQuarters() {
        return $$('ul.patching-quarter-wrapper .li-container .patching-each-quarter');
    }

    clickQuarters() {
        return $('.issue-trend-wrapper .quarter-desc');
    }

    closeQuarters() {
        return $('.patching-quarter-header .mr-close');
    }

    getLatestWeek() {
        return $$('.x-axes-wrap .x-percent span.percent-value');
    }

    viewCurrentQuarter() {
        return $('a.pp-view-quarter');
    }

    getTopDirector() {
        return element(by.xpath('//app-state-table/section/div[2]/div[2]/div[1]/div[3]/span'));
    }

    getDirectorAppl() {
        return element(by.xpath('//app-state-table/section/div[2]/div[2]/div[1]/div[1]'));
    }
}
