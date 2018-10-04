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

export class OverviewCompliance {

  navigateToOverviewCompliance() {
    return element(by.xpath('//div[contains(@class, "contextual-menu-wrapper")]//a[text()="Overview"]'));
  }

  goToDashboard() {
    return element(by.xpath('//app-home-page/div/div[1]/div[2]/div[1]/app-button/button'));
  }

  navigateToOmniSearch() {
    return (element(by.xpath('//app-root/app-post-login-app/div[1]/header/app-post-login-header/div[1]/div[2]/nav/app-nav-icon[3]/a')));
  }

  navigateToPolicyCompliance() {
    return element(by.xpath('//div[contains(@class, "contextual-menu-wrapper")]//a[text()="Policy Compliance Overview"]'));
  }

  navigateToOverviewComplianceGet() {
    return browser.driver.get(domain + '/pl/(compliance/compliance-dashboard)?ag=aws-all&domain=Infra%20%26%20Platforms');
  }

  getCertificateTotalClick() {
    return element(by.xpath('//app-overview-certificates/div/app-issues-category/div/div/div/div[3]'));
  }

  getOverviewHeaderText() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-compliance-dashboard/div/div[1]/div[1]/h1'));
  }

  getVulnerabilitiesClick() {
    return element(by.xpath('//app-overview-vulnerabilities/div/app-issues-category/div/div/div/div[1]/div'));
  }

  getVulnerabilities() {
    return element(by.xpath('//app-compliance/div/div/div[1]/app-contextual-menu/div/ul/li[2]'));
  }

  getTaggingClick() {
    return element(by.xpath('//app-overview-tagging/div/app-issues-category/div/div/div/div[1]/div'));
  }

  getCertificateClick() {
    return element(by.xpath('//app-overview-certificates/div/app-issues-category/div/div/div/div[1]/div'));
  }

  getPatchingClick() {
    return element(by.xpath('//app-overview-patching/div/app-issues-category/div/div/div'));
  }

  getTotalViolations() {
    return element(by.id('countval'));
  }

  getTotalCriticalViolations() {
    return element(by.xpath('//div/div[2]/app-pacman-issues/div/div/article/div/div[1]/div/div[2]/div[3]/div[1]'));
  }

   getPolicyComplianceTable() {
    return element(by.xpath('//app-compliance-dashboard//div[contains(@class, "data-table-inner-content")]'));
  }

  getTotalDataTableCnt() {
    return $('.policies-count div');
  }

  getNumberOfRows() {
    const list = element.all(by.css('.data-table-inner-content .data-table-rows'));
    return list;
  }

  getPageTitle() {
    return $('.floating-widgets-header h1');
  }

  getSecondTab() {
    return element(by.xpath('//app-compliance-dashboard/div/div[1]/div[3]/section/ul/li[1]/div/div/app-data-table/div/app-table-tabs/div/div[1]/div[3]'));
  }

  getFirstTab() {
    return element(by.xpath('//app-compliance-dashboard/div/div[1]/div[3]/section/ul/li[1]/div/div/app-data-table/div/app-table-tabs/div/div[1]/div[2]'));
  }

  openHelp() {
    return $('.sub-heading .help-text-container');
  }

  getHelpTitle() {
    return $('.help-content .help-title');
  }

  getPolicyViolationPercents() {
    return $$('.enclosure-issue .flex.flex-align-center');
  }

  getAllTabs() {
    return $$('.tabs-header .individual-tag-header');
  }

  getPolicyName() {
    return element(by.xpath('//app-compliance-dashboard/div/div[1]/div[3]/section/ul/li[1]/div/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[2]/div/a'));
  }

  getSixthRowCell() {
    return element(by.xpath('//app-compliance-dashboard/div/div[1]/div[3]/section/ul/li[1]/div/div/app-data-table/div/div[2]/div[2]/div/div[6]/div[2]/div/a'));
  }

  policyTitleSort() {
    return element(by.xpath('//app-compliance-dashboard/div/div[1]/div[3]/section/ul/li[1]/div/div/app-data-table/div/div[2]/div[1]/div/div[2]'));
  }

  getFirstRowCell() {
    return element(by.xpath('//app-compliance-dashboard/div/div[1]/div[3]/section/ul/li[1]/div/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[2]/div/a'));
  }

  getSecondRowCell() {
    return element(by.xpath('//app-compliance-dashboard/div/div[1]/div[3]/section/ul/li[1]/div/div/app-data-table/div/div[2]/div[2]/div/div[2]/div[2]/div/a'));
  }

  getPolicyCompliancePercent() {
    return element(by.xpath('//app-compliance-dashboard/div/div[1]/div[3]/section/ul/li[1]/div/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[3]/div/div'));
  }

  getSearchLabel() {
    return $('label.search-label');
  }

  getSearchInput() {
    return $('.search-container input.header-search-input');
  }

  getViolationFirstPercent() {
    return element(by.xpath('//app-compliance-dashboard/div/div[2]/div/app-compliance-issues/div/div[2]/app-pacman-issues/div/div/article/div/div[3]/div[1]/div[2]'));
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

  getOverallPercent() {
    return element(by.xpath('//app-overall-compliance/div[2]/app-multi-band-donut/div/div[1]/div[1]/div'));
  }

  getSecurityPercent() {
    return element(by.xpath('//app-overall-compliance/div[2]/app-multi-band-donut/div/div[2]/div[2]/div[1]'));
  }

  getCostPercent() {
    return element(by.xpath('//app-overall-compliance/div[2]/app-multi-band-donut/div/div[2]/div[2]/div[2]'));
  }

  getGovernancePercent() {
    return element(by.xpath('//app-overall-compliance/div[2]/app-multi-band-donut/div/div[2]/div[2]/div[3]'));
  }

  getTaggingPercent() {
    return element(by.xpath('//app-overall-compliance/div[2]/app-multi-band-donut/div/div[2]/div[2]/div[4]'));
  }

  getProfileDropDown() {
    return element(by.xpath('//app-post-login-header/div[1]/div[3]/div[2]/div[2]/img'));
  }

  getKnowYourApplHeader() {
    return $('.first-time-head-wrap .first-time-head');
  }

  closeKnowYourAppl() {
    return $('.first-time-container img.delete-icon');
  }

  changeAssetGroupPath() {
    return $('app-default-asset-group .default-asset-group-wrapper');
  }

  getAssetGroupSearch() {
    return element(by.xpath('//app-asset-group-search/article/input'));
  }

  selectFirstAssetgroupInList() {
    return $$('.select-tile').first();
  }

}
