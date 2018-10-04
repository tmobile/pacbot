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

import { browser, protractor, $} from 'protractor';
import { Login } from '../page-objects/login.po';
import { OverviewCompliance } from '../page-objects/overview.po';
import { CompliancePolicy } from '../page-objects/compliance-policy.po';
import { PolicyDetails } from '../page-objects/policy-details.po';
import { AssetDetails } from '../page-objects/asset-details.po';
import { AssetList } from '../page-objects/asset-list.po';
import { PolicyViolationsDetail } from '../page-objects/policy-violations-detail.po';

const timeOutHigh = 180000;

describe('CompliancePolicy', () => {
  let login_po: Login;
  let OverviewCompliance_po: OverviewCompliance;
  let AssetDetails_po: AssetDetails;
  let AssetList_po: AssetList;
  let CompliancePolicy_po: CompliancePolicy;
  let policyDetailspo: PolicyDetails;
  let violationDetails_po: PolicyViolationsDetail;
  const EC = protractor.ExpectedConditions;

  beforeAll(() => {
    login_po = new Login();
    OverviewCompliance_po = new OverviewCompliance();
    policyDetailspo = new PolicyDetails();
    CompliancePolicy_po = new CompliancePolicy();
    AssetDetails_po = new AssetDetails();
    AssetList_po = new AssetList();
    violationDetails_po = new PolicyViolationsDetail();
  });

  it('Check page title', () => {
    browser.wait(EC.visibilityOf( OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    OverviewCompliance_po.navigateToOverviewCompliance().click();
    browser.wait(EC.visibilityOf( OverviewCompliance_po.policyTitleSort()), timeOutHigh);
    OverviewCompliance_po.policyTitleSort().click();
    browser.wait(EC.visibilityOf( OverviewCompliance_po.getSixthRowCell()), timeOutHigh);
    OverviewCompliance_po.getSixthRowCell().click();
    const page_title = CompliancePolicy_po.getTitle().getText();
    expect(page_title).toEqual('Policy Compliance');
  });


  it('Check if percentage lies in range 0-100', () => {
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getCompliancePercent()), timeOutHigh);
    CompliancePolicy_po.getCompliancePercent().getText().then(function (text) {
      let checkPercentRange = false;
      if (parseInt(text, 10) >= 0 && parseInt(text, 10) <= 100) {
        checkPercentRange = true;
      }
      expect(checkPercentRange).toEqual(true);
    });
  });

  it('Verify list table filter search', () => {
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getListTableFirstTotal()), timeOutHigh);
    CompliancePolicy_po.getSearchInput().click();
    browser.sleep(401);
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getSearchInput()), timeOutHigh);
    CompliancePolicy_po.getSearchInput().sendKeys('my');
    CompliancePolicy_po.getFirstRowCell().getText().then(function (text) {
      expect(text.toLowerCase()).toContain('my');
    });
    browser.sleep(401);
    CompliancePolicy_po.getSearchInput().sendKeys('');
  });

  it('Verify table filter search', () => {
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getPolicyName()), timeOutHigh);
    CompliancePolicy_po.getSearchLabel().click();
    browser.sleep(401);
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getTableSearchInput()), timeOutHigh);
    CompliancePolicy_po.getTableSearchInput().sendKeys('amazon');
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getTableSearchInput()), timeOutHigh);
    CompliancePolicy_po.getPolicyName().getText().then(function (text) {
      expect(text.toLowerCase()).toContain('amazon');
    });
    browser.sleep(401);
    CompliancePolicy_po.getSearchInput().sendKeys('');
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
    $('label.search-label img').click();
  });

  it('Verify redirect to policy details page', () => {
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getDataLoad()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(CompliancePolicy_po.getDataLoad()), timeOutHigh);
    CompliancePolicy_po.getMoreArrow().click();
    CompliancePolicy_po.getViewMore().click();
    browser.wait(EC.visibilityOf(policyDetailspo.getPolicyDetailsHeading()), timeOutHigh);
    expect(policyDetailspo.getPolicyDetailsHeading().getText()).toEqual('Policy Details');
    browser.wait(EC.elementToBeClickable(policyDetailspo.getBackArrow()), timeOutHigh);
    policyDetailspo.getBackArrow().click();
  });

  it('Verify redirect to asset listing page', () => {
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getListTableFirstTotal()), timeOutHigh);
    CompliancePolicy_po.getListTableFirstTotal().click();
    browser.wait(EC.visibilityOf(AssetList_po.getAssetHeaderText()), timeOutHigh);
    expect(AssetList_po.getAssetHeaderText().getText()).toEqual('Asset List');
    browser.wait(EC.elementToBeClickable(AssetList_po.goBack()), timeOutHigh);
    AssetList_po.goBack().click();
  });

  it('Verify total number match to asset listing page', () => {
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getListTableFirstTotal()), timeOutHigh);
    const first_list_total = CompliancePolicy_po.getListTableFirstTotal().getText();
    CompliancePolicy_po.getListTableFirstTotal().click();
    browser.wait(EC.visibilityOf(AssetList_po.getAssetTotalRows()), timeOutHigh);
    expect(AssetList_po.getAssetTotalRows().getText()).toEqual(first_list_total);
    browser.wait(EC.elementToBeClickable(AssetList_po.goBack()), timeOutHigh);
    AssetList_po.goBack().click();
  });

  it('Verify redirect to policy details page from table', () => {
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getPolicyName()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(CompliancePolicy_po.getPolicyName()), timeOutHigh);
    CompliancePolicy_po.getPolicyName().click();
    expect(policyDetailspo.getPolicyDetailsHeading().getText()).toEqual('Policy Details');
    policyDetailspo.getBackArrow().click();
  });

  it('Verify redirect to policy violation details page from table', () => {
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getIssueId()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(CompliancePolicy_po.getIssueId()), timeOutHigh);
    CompliancePolicy_po.getIssueId().click();
    expect(violationDetails_po.getPolicyViolationDetailHeading().getText()).toEqual('Policy Violations Details');
    violationDetails_po.getBackArrow().click();
  });

  it('Verify redirect to asset details page from table', () => {
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getResourceId()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(CompliancePolicy_po.getResourceId()), timeOutHigh);
    CompliancePolicy_po.getResourceId().click();
    browser.wait(EC.visibilityOf(AssetDetails_po.getAssetHeaderText()), timeOutHigh);
    expect(AssetDetails_po.getAssetHeaderText().getText()).toEqual('Asset Details');
    browser.sleep(401);
    browser.wait(EC.elementToBeClickable(AssetDetails_po.getBackArrow()), timeOutHigh);
    AssetDetails_po.getBackArrow().click();
  });

  it('Check table sort functionality', () => {
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getPolicyName()), timeOutHigh);
    CompliancePolicy_po.resourceIdSort().click();
    let first_row;
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getResourceId()), timeOutHigh);
    CompliancePolicy_po.getResourceId().getText().then(function (text) {
      first_row = text.toLowerCase();
    });
    let second_row;
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getSecondResourceId()), timeOutHigh);
    CompliancePolicy_po.getSecondResourceId().getText().then(function (text) {
      second_row = text.toLowerCase();
      expect(first_row < second_row).toEqual(true);
    });
  });

  it('Check compliance percent calculation', () => {
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getCompliancePercent()), timeOutHigh);
    let percent;
    let passed;
    let total;
    CompliancePolicy_po.getCompliancePercent().getText().then(function (text) {
      percent = parseInt(text.replace(/,/g, ''), 10);
    });
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getPassedAssets()), timeOutHigh);
    CompliancePolicy_po.getPassedAssets().getText().then(function (text) {
      passed = parseInt(text.replace(/,/g, ''), 10);
    });
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getTotalAssets()), timeOutHigh);
    CompliancePolicy_po.getTotalAssets().getText().then(function (text) {
      total = parseInt(text.replace(/,/g, ''), 10);
      expect(percent).toEqual(Math.floor((100 * passed / total)));
    });

  });

  it('Check if sum of total passed and failed equals Total', () => {
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getTotalAssets()), timeOutHigh);
    let total_percent = 0;
    let passed_percent;
    let failed_percent;
    CompliancePolicy_po.getTotalAssets().getText().then(function (text) {
      total_percent = parseInt(text.replace(/,/g, ''), 10);
    });
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getPassedAssets()), timeOutHigh);
    CompliancePolicy_po.getPassedAssets().getText().then(function (text) {
      passed_percent = parseInt(text.replace(/,/g, ''), 10);
    });
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getFailedAssets()), timeOutHigh);
    CompliancePolicy_po.getFailedAssets().getText().then(function (text) {
      failed_percent = parseInt(text.replace(/,/g, ''), 10);
      expect(total_percent.toString()).toEqual((passed_percent + failed_percent).toString());
    });
  });

  it('Check if sum of passed and failed equals Total in list table', () => {
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getListTableFirstTotal()), timeOutHigh);
    let total_percent = 0;
    let passed_percent;
    let failed_percent;
    CompliancePolicy_po.getListTableFirstTotal().getText().then(function (text) {
      total_percent = parseInt(text.replace(/,/g, ''), 10);
    });
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getListTableFirstPassed()), timeOutHigh);
    CompliancePolicy_po.getListTableFirstPassed().getText().then(function (text) {
      passed_percent = parseInt(text.replace(/,/g, ''), 10);
    });
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getListTableFirstFailed()), timeOutHigh);
    CompliancePolicy_po.getListTableFirstFailed().getText().then(function (text) {
      failed_percent = parseInt(text.replace(/,/g, ''), 10);
      expect(total_percent.toString()).toEqual((passed_percent + failed_percent).toString());
    });
  });

  it('Check filter tags', () => {
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getListTableFirstPassed()), timeOutHigh);
    let appl_name;
    CompliancePolicy_po.getFirstRowCell().getText().then(function(text) {
      appl_name = text.toLowerCase();
    });
    CompliancePolicy_po.getListTableFirstPassed().click();
    browser.wait(EC.visibilityOf(AssetList_po.getFirstFilter()), timeOutHigh);
    AssetList_po.getAllFilters().then(function(items) {
      for (let i = 1; i <= items.length; i++) {
        $('.floating-widgets-filter-wrapper .each-filter:nth-child(' + i + ')').getText().then(function(text) {
          if (text.toLowerCase().match(appl_name.toLowerCase())) {
            expect(text.toLowerCase()).toContain(appl_name.toLowerCase());
          }
        });
      }
      browser.wait(EC.elementToBeClickable(AssetList_po.goBack()), timeOutHigh);
      AssetList_po.goBack().click();
    });
  });

  it('Check if sum of all passed in list table equals table total passed', () => {
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getPassedAssets()), timeOutHigh);
    let total_passed = 0;
    let each_pass = 0;
    CompliancePolicy_po.getPassedAssets().getText().then(function (text) {
      total_passed = parseInt(text.replace(/,/g, ''), 10);
    });
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getListTableFirstTotal()), timeOutHigh);
    CompliancePolicy_po.getAllList().then(function(items) {
      for (let i = 1; i < items.length; i++) {
        browser.executeScript('arguments[0].scrollIntoView();', $('.list-table-inner-wrapper .list-table-each-list:nth-child(' + (i + 1) + ') .list-table-value .list-table-count-each:nth-child(2)').getWebElement());
        $('.list-table-inner-wrapper .list-table-each-list:nth-child(' + (i + 1) + ') .list-table-value .list-table-count-each:nth-child(2)').getText().then(function (text) {
          each_pass = each_pass + parseInt(text, 10);
          if ( i === items.length - 1) {
            expect(each_pass).toEqual(total_passed);
          }
        });
      }
    });
  });

  it('Check if sum of all failed in list table equals table total failed', () => {
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getFailedAssets()), timeOutHigh);
    let total_failed = 0;
    let each_fail = 0;
    CompliancePolicy_po.getFailedAssets().getText().then(function (text) {
      total_failed = parseInt(text.replace(/,/g, ''), 10);
    });
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getListTableFirstTotal()), timeOutHigh);
    CompliancePolicy_po.getAllList().then(function(items) {
      for (let i = 1; i < items.length; i++) {
        browser.executeScript('arguments[0].scrollIntoView();', $('.list-table-inner-wrapper .list-table-each-list:nth-child(' + (i + 1) + ') .list-table-value .list-table-count-each:nth-child(3)').getWebElement());
        $('.list-table-inner-wrapper .list-table-each-list:nth-child(' + (i + 1) + ') .list-table-value .list-table-count-each:nth-child(3)').getText().then(function (text) {
          each_fail = each_fail + parseInt(text, 10);
          if ( i === items.length - 1) {
            expect(each_fail).toEqual(total_failed);
          }
        });
      }
    });
  });

  it('Check if sum of all total in list table equals table total in summary', () => {
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getTotalAssets()), timeOutHigh);
    let total_assets = 0;
    let each_total = 0;
    CompliancePolicy_po.getTotalAssets().getText().then(function (text) {
      total_assets = parseInt(text.replace(/,/g, ''), 10);
    });
    browser.wait(EC.visibilityOf( CompliancePolicy_po.getListTableFirstTotal()), timeOutHigh);
    CompliancePolicy_po.getAllList().then(function(items) {
      for (let i = 1; i < items.length; i++) {
        browser.executeScript('arguments[0].scrollIntoView();', $('.list-table-inner-wrapper .list-table-each-list:nth-child(' + (i + 1) + ') .list-table-value .list-table-count-each:nth-child(1)').getWebElement());
        $('.list-table-inner-wrapper .list-table-each-list:nth-child(' + (i + 1) + ') .list-table-value .list-table-count-each:nth-child(1)').getText().then(function (text) {
          each_total = each_total + parseInt(text, 10);
          if ( i === items.length - 1) {
            expect(each_total).toEqual(total_assets);
          }
        });
      }
    });
  });

  it('Verify CSV download', () => {
    let download_successful = false;
    browser.wait(EC.presenceOf(CompliancePolicy_po.getPolicyName()), timeOutHigh);
    const filename = process.cwd() + '/e2e/downloads/List of Violations.csv';
    const fs = require('fs');
    const myDir = process.cwd() + '/e2e/downloads';
    if (!CompliancePolicy_po.checkDirExists(myDir)) {
      fs.mkdirSync(myDir);
    } else if ((fs.readdirSync(myDir).length) > 0 && fs.existsSync(filename)) {
      fs.unlinkSync(filename);
    }
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getdownloadIcon()), timeOutHigh);
    CompliancePolicy_po.getdownloadIcon().click();
    browser.wait(EC.visibilityOf(CompliancePolicy_po.getToastMsg()), timeOutHigh).then(function() {
      browser.wait(EC.invisibilityOf(CompliancePolicy_po.getDownloadRunningIcon()), 600000).then(function() {
        browser.sleep(1001);
        browser.driver.wait(function() {
          if (fs.existsSync(filename)) {
            download_successful = true;
            const fileContent = fs.readFileSync(filename, { encoding: 'utf8' });
            expect(fileContent.toString().indexOf('\n')).toBeGreaterThan(0);
          }
          expect(download_successful).toEqual(true);
          return fs.existsSync(filename);
        }, timeOutHigh);
      });
    });
  });
});
