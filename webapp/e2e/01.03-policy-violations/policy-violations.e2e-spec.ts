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

import { browser, protractor} from 'protractor';
import { PolicyViolations } from '../page-objects/policy-violations.po';
import {PolicyViolationsDetail} from '../page-objects/policy-violations-detail.po';
import {PolicyDetails} from '../page-objects/policy-details.po';
import {AssetDetails} from '../page-objects/asset-details.po';
import { Menu } from '../page-objects/menu.po';
import { Login } from '../page-objects/login.po';
import { OverviewCompliance } from '../page-objects/overview.po';

describe('PolicyViolations', () => {
  let menu_po: Menu;
  let policyviolations_po: PolicyViolations;
  let policyviolationsdetail_po: PolicyViolationsDetail;
  let policydetails_po: PolicyDetails;
  let assetdetails_po: AssetDetails;
  const EC = protractor.ExpectedConditions;
  let login_po: Login;
  let OverviewCompliance_po: OverviewCompliance;
  const maxTimeOut = 300000;

  beforeAll(() => {
    menu_po = new Menu();
    policyviolations_po = new  PolicyViolations();
    policyviolationsdetail_po = new PolicyViolationsDetail();
    policydetails_po = new PolicyDetails();
    assetdetails_po = new AssetDetails();
    login_po = new Login();
    OverviewCompliance_po = new OverviewCompliance();
    });

  it('navigate to Policy Violations page', () => {
    browser.wait(EC.visibilityOf(menu_po.MenuClick()), maxTimeOut);
    menu_po.MenuClick().click();
    browser.wait(EC.visibilityOf(menu_po.PolicyViolationClick()), maxTimeOut);
    menu_po.PolicyViolationClick().click();
    browser.wait(EC.visibilityOf(policyviolations_po.getPolicyViolationsHeaderText()), maxTimeOut);
    const violation_path = policyviolations_po.getPolicyViolationsHeaderText().getText();
    expect(violation_path).toEqual('Policy Violations');
  });

  it('verify table headers', () => {
    browser.wait(EC.visibilityOf(policyviolations_po.getIssueIdHeaderText()), maxTimeOut);
    const issue_id_path = policyviolations_po.getIssueIdHeaderText().getText();
    expect(issue_id_path).toEqual('Issue ID');
    const policy_name_path = policyviolations_po.getPolicyNameHeaderText().getText();
    expect(policy_name_path).toEqual('Policy Name');
    const resource_id_path = policyviolations_po.getResourceIdHeaderText().getText();
    expect(resource_id_path).toEqual('Resource ID');
  });

  it('verify navigation to policy violations detail page', () => {
    browser.wait(EC.visibilityOf(policyviolations_po.getIssueIdFirstRowValue()), maxTimeOut);
    policyviolations_po.getIssueIdFirstRowValue().click();
    browser.wait(EC.visibilityOf(policyviolationsdetail_po.getPolicyViolationDetailHeading()), maxTimeOut);
    const policy_detail_path = policyviolationsdetail_po.getPolicyViolationDetailHeading().getText();
    expect(policy_detail_path).toEqual('Policy Violations Details');
    browser.wait(EC.elementToBeClickable(policyviolationsdetail_po.getBackArrow()), maxTimeOut);
    policyviolationsdetail_po.getBackArrow().click();
  });

  it('verify navigation to policy details page', () => {
    browser.wait(EC.visibilityOf(policyviolations_po.getPolicyNameFirstRowValue()), maxTimeOut);
    policyviolations_po.getPolicyNameFirstRowValue().click();
    browser.wait(EC.visibilityOf(policydetails_po.getPolicyDetailsHeading()), maxTimeOut);
    const policy_detail_path = policydetails_po.getPolicyDetailsHeading().getText();
    expect(policy_detail_path).toEqual('Policy Details');
    browser.wait(EC.elementToBeClickable(policydetails_po.getBackArrow()), maxTimeOut);
    policydetails_po.getBackArrow().click();
  });

  it('verify navigation to asset details page', () => {
    browser.wait(EC.visibilityOf(policyviolations_po.getResourceIdFirstRowValue()), maxTimeOut);
    policyviolations_po.getResourceIdFirstRowValue().click();
    browser.wait(EC.visibilityOf(assetdetails_po.getAssetHeaderText()), maxTimeOut);
    const asset_detail_path = assetdetails_po.getAssetHeaderText().getText();
    expect(asset_detail_path).toEqual('Asset Details');
    browser.wait(EC.elementToBeClickable(assetdetails_po.getBackArrow()), maxTimeOut);
    assetdetails_po.getBackArrow().click();
  });

  it('verify appearance of additional details', () => {
    browser.wait(EC.visibilityOf(policyviolations_po.getSeverityFirstRowValue()), maxTimeOut);
    policyviolations_po.getSeverityFirstRowValue().click();
    browser.wait(EC.visibilityOf(policyviolations_po.getAdditionalDetailsHeaderText()), maxTimeOut);
    const additional_detail_path = policyviolations_po.getAdditionalDetailsHeaderText().getText();
    expect(additional_detail_path).toEqual('Additional Details');
    browser.wait(EC.elementToBeClickable(policyviolations_po.getAdditionaldetailsCrossMark()), maxTimeOut);
    policyviolations_po.getAdditionaldetailsCrossMark().click();
  });

  it('verify filter for policy violation table', () => {
    // click on filter dropdown to get list
    browser.wait(EC.presenceOf(policyviolations_po.getFilterArrow()), maxTimeOut);
    policyviolations_po.getFilterArrow().click();
    browser.sleep(2000);
    let filterKey, filterValue;
    // select first filter type
    browser.wait(EC.visibilityOf(policyviolations_po.getFilterType()), maxTimeOut);
    policyviolations_po.getFilterType().getText().then( function(text) {
      filterKey = text;
    });
    policyviolations_po.getFilterType().click();
    // verify whether filter tags present
    browser.sleep(3000);
    // select first filter tag
    policyviolations_po.getFilterTagInput().click();
    browser.wait(EC.visibilityOf(policyviolations_po.getFilterTags()), maxTimeOut);
    policyviolations_po.getFilterTags().getText().then( function(text) {
      filterValue = text;
    });
    policyviolations_po.getFilterTags().click();
    // equate selected filter key and value with filter tags displayed
    browser.wait(EC.visibilityOf(policyviolations_po.getFilterSelected()), maxTimeOut);
    policyviolations_po.getFilterSelected().getText().then( function(text) {
      const textArray = text.split(':');
      expect(textArray[0]).toContain(filterKey);
      expect(textArray[1]).toContain(filterValue);
    });
    policyviolations_po.getClearAllFilter().click();
  });

  it('verify search for policy violation table', () => {
    browser.wait(EC.presenceOf(policyviolations_po.getSearchLabel()), maxTimeOut);
    policyviolations_po.getSearchLabel().click();
    browser.wait(EC.visibilityOf(policyviolations_po.getSearchInput()), maxTimeOut);
    policyviolations_po.getSearchInput().sendKeys('security');
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
    browser.wait(EC.visibilityOf(policyviolations_po.getRuleIdFirstRowValue()), maxTimeOut);
    const rule_id_path = policyviolations_po.getRuleIdFirstRowValue().getText();
    expect(rule_id_path).toContain('security');
  });

  it('verify csv download', () => {
    let download_successful = false;
    const filename = process.cwd() + '/e2e/downloads/Open Violations.csv';
    const fs = require('fs');
    const myDir = process.cwd() + '/e2e/downloads';
    if (!policyviolations_po.checkDirExists(myDir)) {
      fs.mkdirSync(myDir);
    } else if ((fs.readdirSync(myDir).length) > 0 && fs.existsSync(filename)) {
      fs.unlinkSync(filename);
    }
    browser.wait(EC.visibilityOf(policyviolations_po.getdownloadIcon()), maxTimeOut);
    browser.wait(EC.elementToBeClickable(policyviolations_po.getdownloadIcon()), maxTimeOut);
    policyviolations_po.getdownloadIcon().click();
    browser.wait(EC.visibilityOf(policyviolations_po.getToastMsg()), maxTimeOut).then(function() {
      browser.wait(EC.invisibilityOf(policyviolations_po.getDownloadRunningIcon()), 600000).then(function() {
        browser.sleep(4000);
        browser.driver.wait(function() {
          if (fs.existsSync(filename)) {
            download_successful = true;
            const fileContent = fs.readFileSync(filename, { encoding: 'utf8' });
            expect(fileContent.toString().indexOf('\n')).toBeGreaterThan(0);
          }
          expect(download_successful).toEqual(true);
          return fs.existsSync(filename);
        }, maxTimeOut);
      });
    });
  });

});
