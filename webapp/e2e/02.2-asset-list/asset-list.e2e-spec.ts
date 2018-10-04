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
import { AssetList } from '../page-objects/asset-list.po';
import { AssetDetails } from '../page-objects/asset-details.po';
import { Menu } from '../page-objects/menu.po';

describe('AssetList', () => {
  let menu_po: Menu;
  let assetlist_po: AssetList;
  let assetdetails_po: AssetDetails;
  const EC = protractor.ExpectedConditions;
  const maxTimeOut = 300000;

  beforeAll(() => {
    menu_po = new Menu();
    assetlist_po = new  AssetList();
    assetdetails_po = new AssetDetails();
  });

  it('navigate to Asset List page', () => {
    browser.wait(EC.visibilityOf(menu_po.MenuClick()), maxTimeOut);
    browser.wait(EC.elementToBeClickable(menu_po.MenuClick()), maxTimeOut);
    menu_po.MenuClick().click();
    browser.wait(EC.visibilityOf(menu_po.AssetListClick()), maxTimeOut);
    browser.wait(EC.elementToBeClickable(menu_po.AssetListClick()), maxTimeOut);
    menu_po.AssetListClick().click();
    browser.wait(EC.visibilityOf(assetlist_po.getAssetHeaderText()), maxTimeOut);
    const asset_list_header_path = assetlist_po.getAssetHeaderText().getText();
    expect(asset_list_header_path).toEqual('Asset List');
  });

  it('verify table headers', () => {
    browser.wait(EC.visibilityOf(assetlist_po.getResourceIdHeaderText()), maxTimeOut);
    const resource_id_path = assetlist_po.getResourceIdHeaderText().getText();
    expect(resource_id_path).toEqual('Resource ID');
    const target_type_path = assetlist_po.getTargetTypeHeaderText().getText();
    expect(target_type_path).toEqual('Target Type');
  });

  it('verify navigation to asset details page', () => {
    browser.wait(EC.visibilityOf(assetlist_po.getResourceIdFirstRowValue()), maxTimeOut);
    assetlist_po.getResourceIdFirstRowValue().click();
    browser.wait(EC.visibilityOf(assetdetails_po.getAssetHeaderText()), maxTimeOut);
    const asset_detail_path = assetdetails_po.getAssetHeaderText().getText();
    expect(asset_detail_path).toEqual('Asset Details');
    browser.wait(EC.elementToBeClickable(assetdetails_po.getBackArrow()), maxTimeOut);
    assetdetails_po.getBackArrow().click();
  });

  it('verify appearance of additional details', () => {
    browser.wait(EC.visibilityOf(assetlist_po.getTargetTypeFirstRowValue()), maxTimeOut);
    assetlist_po.getTargetTypeFirstRowValue().click();
    browser.wait(EC.visibilityOf(assetlist_po.getAdditionalDetailsHeaderText()), maxTimeOut);
    const additional_detail_path = assetlist_po.getAdditionalDetailsHeaderText().getText();
    expect(additional_detail_path).toEqual('Additional Details');
    browser.wait(EC.visibilityOf(assetlist_po.getAdditionaldetailsCrossMark()), maxTimeOut);
    browser.wait(EC.elementToBeClickable(assetlist_po.getAdditionaldetailsCrossMark()), maxTimeOut);
    assetlist_po.getAdditionaldetailsCrossMark().click();
  });

  it('verify search for asset list table', () => {
    browser.wait(EC.visibilityOf(assetlist_po.getSearchLabel()), maxTimeOut);
    browser.wait(EC.elementToBeClickable(assetlist_po.getSearchLabel()), maxTimeOut);
    assetlist_po.getSearchLabel().click();
    browser.wait(EC.visibilityOf(assetlist_po.getSearchInput()), maxTimeOut);
    assetlist_po.getSearchInput().sendKeys('lambda');
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
    browser.wait(EC.visibilityOf(assetlist_po.getResourceIdFirstRowValue()), maxTimeOut);
    const resource_id_path = assetlist_po.getResourceIdFirstRowValue().getText();
    expect(resource_id_path).toContain('lambda');
  });

  it('verify filter for asset list table', () => {
    // click on filter dropdown to get list
    browser.wait(EC.presenceOf(assetlist_po.getFilterArrow()), maxTimeOut);
    assetlist_po.getFilterArrow().click();
    browser.sleep(1000);
    let filterKey, filterValue;
    // select first filter type
    browser.wait(EC.visibilityOf(assetlist_po.getFilterType()), maxTimeOut);
    assetlist_po.getFilterType().getText().then( function(text) {
      filterKey = text;
    });
    assetlist_po.getFilterType().click();
    // verify whether filter tags present
    browser.sleep(3000);
    // select first filter tag
    assetlist_po.getFilterTagInput().click();
    browser.wait(EC.visibilityOf(assetlist_po.getFilterTags()), maxTimeOut);
    assetlist_po.getFilterTags().getText().then( function(text) {
      filterValue = text;
    });
    assetlist_po.getFilterTags().click();
    // equate selected filter key and value with filter tags displayed
    browser.wait(EC.visibilityOf(assetlist_po.getFilterSelected()), maxTimeOut);
    assetlist_po.getFilterSelected().getText().then( function(text) {
      const textArray = text.split(':');
      expect(textArray[0]).toContain(filterKey);
      expect(textArray[1]).toContain(filterValue);
    });
    assetlist_po.getClearAllFilter().click();
  });

  it('verify csv download', () => {
    let download_successful = false;
    const filename = process.cwd() + '/e2e/downloads/All Assets.csv';
    const fs = require('fs');
    const myDir = process.cwd() + '/e2e/downloads';
    if (!assetlist_po.checkDirExists(myDir)) {
      fs.mkdirSync(myDir);
    } else if ((fs.readdirSync(myDir).length) > 0 && fs.existsSync(filename)) {
      console.log('file exists');
      fs.unlinkSync(filename);
    }
    browser.wait(EC.visibilityOf(assetlist_po.getdownloadIcon()), maxTimeOut);
    assetlist_po.getdownloadIcon().click();
    browser.wait(EC.visibilityOf(assetlist_po.getToastMsg()), maxTimeOut).then(function() {
      browser.wait(EC.invisibilityOf(assetlist_po.getDownloadRunningIcon()), 600000).then(function() {
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
