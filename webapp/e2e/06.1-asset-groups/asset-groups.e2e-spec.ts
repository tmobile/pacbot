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
import { AssetGroups } from '../page-objects/asset-groups.po';
import { OverviewCompliance } from '../page-objects/overview.po';
import { Login } from '../page-objects/login.po';

const timeOutHigh = 180000;

describe('AssetGroups', () => {
  let OverviewCompliance_po: OverviewCompliance;
  let AssetGroups_po: AssetGroups;
  let login_po: Login;
  const EC = protractor.ExpectedConditions;

  beforeAll(() => {
    OverviewCompliance_po = new OverviewCompliance();
    login_po = new Login();
    AssetGroups_po = new AssetGroups();
  });

  it('Verify asset groups modal title', () => {
    AssetGroups_po.navigateToChangeAssetGroup();
    browser.wait(EC.visibilityOf(AssetGroups_po.getAssetGroupTitle()), timeOutHigh);
    const title = AssetGroups_po.getAssetGroupTitle().getText();
    expect(title).toEqual('Asset Groups');
  });

  it('Verify search functionality', () => {
    browser.wait(EC.visibilityOf(AssetGroups_po.getAssetGroupSearch()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(AssetGroups_po.getAssetGroupSearch()), timeOutHigh);
    AssetGroups_po.getAssetGroupSearch().click();
    AssetGroups_po.getAssetGroupSearch().sendKeys('adapt');
    browser.sleep(100);
    browser.wait(EC.visibilityOf(AssetGroups_po.getFirstAssetGroup()), timeOutHigh);
    AssetGroups_po.getFirstAssetGroup().getText().then(function(text) {
      expect(text.toLowerCase()).toContain('adapt');
    });
  });

  it('Verify asset group click and details', () => {
    browser.wait(EC.visibilityOf(AssetGroups_po.clickFirstAssetGroup()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(AssetGroups_po.clickFirstAssetGroup()), timeOutHigh);
    AssetGroups_po.clickFirstAssetGroup().click();
    browser.wait(EC.visibilityOf(AssetGroups_po.getAgDetails()), timeOutHigh);
    expect(AssetGroups_po.getAgDetails().getText()).toEqual('Applications');
  });

  it('Verify asset group change', () => {
    browser.wait(EC.visibilityOf(AssetGroups_po.getSetDefault()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(AssetGroups_po.getSetDefault()), timeOutHigh);
    AssetGroups_po.getSetDefault().click();
    browser.sleep(100);
    browser.wait(EC.visibilityOf(AssetGroups_po.currentAssetGroup()), timeOutHigh);
    AssetGroups_po.currentAssetGroup().getText().then(function(text) {
      expect(text.toLowerCase()).toContain('adapt');
      AssetGroups_po.navigateToChangeAssetGroup();
      browser.wait(EC.visibilityOf(AssetGroups_po.getAssetGroupSearch()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(AssetGroups_po.getAssetGroupSearch()), timeOutHigh);
      AssetGroups_po.getAssetGroupSearch().click();
      AssetGroups_po.getAssetGroupSearch().sendKeys('aws-all');
      browser.sleep(100);
      browser.wait(EC.visibilityOf(AssetGroups_po.clickFirstAssetGroup()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(AssetGroups_po.clickFirstAssetGroup()), timeOutHigh);
      AssetGroups_po.clickFirstAssetGroup().click();
      browser.wait(EC.visibilityOf(AssetGroups_po.getSetDefault()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(AssetGroups_po.getSetDefault()), timeOutHigh);
      AssetGroups_po.getSetDefault().click();
    });

  });

});
