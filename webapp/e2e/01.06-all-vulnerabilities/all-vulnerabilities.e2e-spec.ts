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

import { browser, protractor } from 'protractor';
import { VulnerabilityCompliance } from '../page-objects/vulnerability-compliance.po';
import { AllVulnerabilities } from '../page-objects/all-vulnerabilities.po';
import { AssetList } from '../page-objects/asset-list.po';
import { VulnerabilityDetails } from '../page-objects/vulnerability-details.po';
import { OverviewCompliance } from '../page-objects/overview.po';

describe('AllVulnerabilities', () => {
  let VulnerabilityCompliance_po: VulnerabilityCompliance;
  let AllVulnerabilities_po: AllVulnerabilities;
  let OverviewCompliance_po: OverviewCompliance;
  let vulnerabilityDetails_po: VulnerabilityDetails;
  let assetList_po: AssetList;
  const EC = protractor.ExpectedConditions;
  const timeOutHigh = 180000;

  beforeAll(() => {
    AllVulnerabilities_po = new AllVulnerabilities();
    VulnerabilityCompliance_po = new VulnerabilityCompliance();
    OverviewCompliance_po = new OverviewCompliance();
    vulnerabilityDetails_po = new VulnerabilityDetails();
    assetList_po = new AssetList();
  });

  it('Check page title', () => {
    browser.wait(EC.visibilityOf( OverviewCompliance_po.getVulnerabilities()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( OverviewCompliance_po.getVulnerabilities()), timeOutHigh);
    OverviewCompliance_po.getVulnerabilities().click();
    browser.wait(EC.visibilityOf( VulnerabilityCompliance_po.getOverallVulnerabilities()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( VulnerabilityCompliance_po.getOverallVulnerabilities()), timeOutHigh);
    VulnerabilityCompliance_po.getOverallVulnerabilities().click();
    browser.wait(EC.visibilityOf(AllVulnerabilities_po.getAllVulHeaderText()), timeOutHigh);
    expect(AllVulnerabilities_po.getAllVulHeaderText().getText()).toEqual('All Vulnerabilities');
  });

  it('verify search for all vulnerability table', () => {
    browser.wait(EC.visibilityOf(AllVulnerabilities_po.getSearchLabel()), timeOutHigh);
    AllVulnerabilities_po.getSearchLabel().click();
    browser.wait(EC.visibilityOf(AllVulnerabilities_po.getSearchInput()), timeOutHigh);
    AllVulnerabilities_po.getSearchInput().sendKeys('Java');
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
    browser.wait(EC.visibilityOf(AllVulnerabilities_po.getPolicyTitle()), timeOutHigh);
    const policy_name = AllVulnerabilities_po.getPolicyTitle().getText();
    expect(policy_name).toContain('Java');
  });

  it('Check redirect to vulnerability details', () => {
    browser.wait(EC.visibilityOf( AllVulnerabilities_po.getPolicyTitle()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( AllVulnerabilities_po.getPolicyTitle()), timeOutHigh);
    AllVulnerabilities_po.getPolicyTitle().click();
    browser.wait(EC.visibilityOf( vulnerabilityDetails_po.getPageTitle()), timeOutHigh);
    expect(vulnerabilityDetails_po.getPageTitle().getText()).toEqual('Vulnerability Details');
    browser.wait(EC.visibilityOf( vulnerabilityDetails_po.getBackArrow()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( vulnerabilityDetails_po.getBackArrow()), timeOutHigh);
    vulnerabilityDetails_po.getBackArrow().click();
  });

  it('Check redirect to vulnerability details with qid match', () => {
    browser.wait(EC.visibilityOf( AllVulnerabilities_po.getQid()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( AllVulnerabilities_po.getQid()), timeOutHigh);
    const qid = AllVulnerabilities_po.getQid().getText();
    AllVulnerabilities_po.getQid().click();
    browser.wait(EC.visibilityOf(vulnerabilityDetails_po.getQid()), timeOutHigh);
    expect(vulnerabilityDetails_po.getQid().getText()).toEqual(qid);
    browser.wait(EC.visibilityOf(vulnerabilityDetails_po.getBackArrow()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(vulnerabilityDetails_po.getBackArrow()), timeOutHigh);
    vulnerabilityDetails_po.getBackArrow().click();
  });

  it('Check redirect to asset list with matching count', () => {
    browser.wait(EC.visibilityOf( AllVulnerabilities_po.getAssetsAffected()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( AllVulnerabilities_po.getAssetsAffected()), timeOutHigh);
    const assetsAffected = AllVulnerabilities_po.getAssetsAffected().getText();
    AllVulnerabilities_po.getAssetsAffected().click();
    browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), timeOutHigh);
    expect(assetList_po.getAssetTotalRows().getText()).toEqual(assetsAffected);
    browser.wait(EC.visibilityOf(assetList_po.getBackArrowEle()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(assetList_po.getBackArrowEle()), timeOutHigh);
    assetList_po.getBackArrowEle().click();
  });

  it('Check table sort functionality', () => {
    browser.wait(EC.visibilityOf( AllVulnerabilities_po.getPolicyTitle()), timeOutHigh);
    AllVulnerabilities_po.policyTitleSort().click();
    let first_row;
    AllVulnerabilities_po.getPolicyTitle().getText().then(function (text) {
      first_row = text.toLowerCase();
    });
    let second_row;
    AllVulnerabilities_po.getNextPolicyTitle().getText().then(function (text) {
      second_row = text.toLowerCase();
      expect(first_row < second_row).toEqual(true);
    });
  });

  it('Check table additional details functionality', () => {
    browser.wait(EC.visibilityOf( AllVulnerabilities_po.getPolicyTitle()), timeOutHigh);
    browser.wait(EC.visibilityOf( AllVulnerabilities_po.additionalDetails()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( AllVulnerabilities_po.additionalDetails()), timeOutHigh);
    AllVulnerabilities_po.additionalDetails().click();
    browser.wait(EC.visibilityOf( AllVulnerabilities_po.additionalDetailsTxt()), timeOutHigh);
    expect(AllVulnerabilities_po.additionalDetailsTxt().getText()).toEqual('Additional Details');
    browser.wait(EC.visibilityOf( AllVulnerabilities_po.additionalDetailsClose()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( AllVulnerabilities_po.additionalDetailsClose()), timeOutHigh);
    AllVulnerabilities_po.additionalDetailsClose().click();
  });

  it('verify csv download', () => {
    let download_successful = false;
    browser.wait(EC.visibilityOf( AllVulnerabilities_po.getPolicyTitle()), timeOutHigh);
    const filename = process.cwd() + '/e2e/downloads/All Vulnerabilities.csv';
    const fs = require('fs');
    const myDir = process.cwd() + '/e2e/downloads';
    if (!AllVulnerabilities_po.checkDirExists(myDir)) {
      fs.mkdirSync(myDir);
    } else if ((fs.readdirSync(myDir).length) > 0 && fs.existsSync(filename)) {
      fs.unlinkSync(filename);
    }
    browser.wait(EC.visibilityOf(AllVulnerabilities_po.getdownloadIcon()), timeOutHigh);
    AllVulnerabilities_po.getdownloadIcon().click();
    browser.wait(EC.visibilityOf(AllVulnerabilities_po.getToastMsg()), timeOutHigh).then(function() {
      browser.wait(EC.invisibilityOf(AllVulnerabilities_po.getDownloadRunningIcon()), 600000).then(function() {
        browser.sleep(4000);
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
