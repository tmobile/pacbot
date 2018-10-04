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
import { OverviewCompliance } from '../page-objects/overview.po';
import { PatchingCompliance } from '../page-objects/patching-compliance.po';
import { Login } from '../page-objects/login.po';
import { AssetDetails } from '../page-objects/asset-details.po';
import { AssetList } from '../page-objects/asset-list.po';

const timeOutHigh = 180000;

describe('PatchingCompliance', () => {
  let OverviewCompliance_po: OverviewCompliance;
  let PatchingCompliance_po: PatchingCompliance;
  let Login_po: Login;
  let AssetDetails_po: AssetDetails;
  let AssetList_po: AssetList;
  const EC = protractor.ExpectedConditions;

  beforeAll(() => {
    OverviewCompliance_po = new OverviewCompliance();
    PatchingCompliance_po = new PatchingCompliance();
    AssetDetails_po = new AssetDetails();
    AssetList_po = new AssetList();
    Login_po = new Login();
  });


  it('Check title of page', () => {
    browser.wait(EC.visibilityOf( OverviewCompliance_po.getPatchingClick()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( OverviewCompliance_po.getPatchingClick()), timeOutHigh);
    OverviewCompliance_po.getPatchingClick().click();
    browser.wait(EC.visibilityOf(PatchingCompliance_po.getPatchingHeaderText()), timeOutHigh);
    expect(PatchingCompliance_po.getPatchingHeaderText().getText()).toContain('Patching');
  });

  it('Verify list table filter search', () => {
    browser.wait(EC.visibilityOf(PatchingCompliance_po.getTableTotal()), timeOutHigh);
    browser.wait(EC.visibilityOf(PatchingCompliance_po.getSearchLabel()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(PatchingCompliance_po.getSearchLabel()), timeOutHigh);
    PatchingCompliance_po.getSearchLabel().click();
    browser.sleep(201);
    PatchingCompliance_po.getSearchInput().sendKeys('dev');
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
    browser.sleep(201);
    browser.wait(EC.visibilityOf(PatchingCompliance_po.getTableTotal()), timeOutHigh);
    PatchingCompliance_po.getFirstRowCell().getText().then(function(text) {
      expect(text.toLowerCase()).toContain('dev');
      browser.sleep(401);
      PatchingCompliance_po.getSearchInput().sendKeys('');
    });
  });

  it('Check table sort functionality', () => {
    browser.wait(EC.visibilityOf(PatchingCompliance_po.getTableTotal()), timeOutHigh);
    PatchingCompliance_po.getTableSort().click();
    let first_row;
    PatchingCompliance_po.getFirstRowCell().getText().then(function(text) {
      first_row = text.toLowerCase();
    });
    let second_row;
    PatchingCompliance_po.getSecondRowCell().getText().then(function(text) {
      second_row = text.toLowerCase();
      expect(first_row < second_row).toEqual(true);
    });
  });

  it('Check table additional details functionality', () => {
    browser.wait(EC.visibilityOf( PatchingCompliance_po.getTableTotal()), timeOutHigh);
    browser.wait(EC.visibilityOf( PatchingCompliance_po.getFirstRowCell()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( PatchingCompliance_po.getFirstRowCell()), timeOutHigh);
    PatchingCompliance_po.getFirstRowCell().click();
    browser.wait(EC.visibilityOf( PatchingCompliance_po.additionalDetailsTxt()), timeOutHigh);
    expect(PatchingCompliance_po.additionalDetailsTxt().getText()).toEqual('Additional Details');
    browser.wait(EC.visibilityOf( PatchingCompliance_po.additionalDetailsClose()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( PatchingCompliance_po.additionalDetailsClose()), timeOutHigh);
    PatchingCompliance_po.additionalDetailsClose().click();
  });

  it('verify csv download', () => {
    let download_successful = false;
    browser.wait(EC.visibilityOf( PatchingCompliance_po.getTableTotal()), timeOutHigh);
    const filename = process.cwd() + '/e2e/downloads/Patching Compliance Details.csv';
    const fs = require('fs');
    const myDir = process.cwd() + '/e2e/downloads';
    if (!PatchingCompliance_po.checkDirExists(myDir)) {
      fs.mkdirSync(myDir);
    } else if ((fs.readdirSync(myDir).length) > 0 && fs.existsSync(filename)) {
      fs.unlinkSync(filename);
    }
    browser.wait(EC.visibilityOf(PatchingCompliance_po.getdownloadIcon()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(PatchingCompliance_po.getdownloadIcon()), timeOutHigh);
    PatchingCompliance_po.getdownloadIcon().click();
    browser.wait(EC.visibilityOf(PatchingCompliance_po.getToastMsg()), timeOutHigh).then(function() {
      browser.wait(EC.invisibilityOf(PatchingCompliance_po.getDownloadRunningIcon()), 600000).then(function() {
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

  it('Verify redirect to asset details clicking resourceId', () => {
    browser.wait(EC.visibilityOf( PatchingCompliance_po.getResourceId()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( PatchingCompliance_po.getResourceId()), timeOutHigh);
    PatchingCompliance_po.getResourceId().click();
    browser.wait(EC.visibilityOf(AssetDetails_po.getAssetHeaderText()), timeOutHigh);
    expect(AssetDetails_po.getAssetHeaderText().getText()).toEqual('Asset Details');
    browser.sleep(401);
    browser.wait(EC.elementToBeClickable(AssetDetails_po.getBackArrow()), timeOutHigh);
    AssetDetails_po.getBackArrow().click();
  });

  it('Verify total patched and scan %', () => {
    browser.wait(EC.visibilityOf( PatchingCompliance_po.getPatchedAssets()), timeOutHigh);
    browser.wait(EC.visibilityOf( PatchingCompliance_po.getTotalAssets()), timeOutHigh);
    browser.wait(EC.visibilityOf( PatchingCompliance_po.getUnPatchedAssets()), timeOutHigh);
    browser.wait(EC.visibilityOf( PatchingCompliance_po.getPatchedPercent()), timeOutHigh);
    browser.wait(EC.visibilityOf( PatchingCompliance_po.getTableTotal()), timeOutHigh);
    let total;
    let patched;
    let unpatched;
    let percent;
    let table_total;
    PatchingCompliance_po.getPatchedAssets().getText().then(function(text) {
      patched = parseInt(text.replace(/,/g, ''), 10);
    });
    PatchingCompliance_po.getTotalAssets().getText().then(function(text) {
      total = parseInt(text.replace(/,/g, ''), 10);
    });
    PatchingCompliance_po.getUnPatchedAssets().getText().then(function(text) {
      unpatched = parseInt(text.replace(/,/g, ''), 10);
    });
    PatchingCompliance_po.getTableTotal().getText().then(function(text) {
      table_total = parseInt(text.replace(/,/g, ''), 10);
    });
    PatchingCompliance_po.getPatchedPercent().getText().then(function(text) {
      percent = parseInt(text.replace(/,/g, ''), 10);
      expect(total).toEqual(unpatched + patched);
      expect(total).toEqual(table_total);
      expect(Math.floor((patched * 100) / total)).toEqual(percent);
      let is_percent = false;
      if ( percent <= 100 && percent >= 0 ) {
        is_percent = true;
      }
      expect(is_percent).toEqual(true);
      browser.wait(EC.visibilityOf(PatchingCompliance_po.clickQuarters()), timeOutHigh);
      PatchingCompliance_po.getLatestWeek().then(function(items){
        const latest_week = items.length;
        const latest_percent = $('.percent-wrap .x-percent:nth-child(' + latest_week + ')').getText().then(function(subtext) {
          expect(percent).toEqual(parseInt(subtext, 10));
        });
      });
    });
  });

  it('Verify help text modal is opening', () => {
    browser.wait(EC.visibilityOf( PatchingCompliance_po.openHelp()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( PatchingCompliance_po.openHelp()), timeOutHigh);
    PatchingCompliance_po.openHelp().click();
    browser.wait(EC.visibilityOf( PatchingCompliance_po.getHelpTitle()), timeOutHigh);
    const help_title = PatchingCompliance_po.getHelpTitle().getText();
    expect(help_title).toEqual('Help');
    $('.help-text-modal .close-popup').click();
  });

  it('Check quarter details match summary', () => {
    let percent;
    let patched;
    let unpatched;
    browser.wait(EC.visibilityOf( PatchingCompliance_po.getPatchedAssets()), timeOutHigh);
    PatchingCompliance_po.getPatchedAssets().getText().then(function(text) {
      patched = parseInt(text.replace(/,/g, ''), 10);
    });
    PatchingCompliance_po.getPatchedPercent().getText().then(function(text) {
      percent = parseInt(text.replace(/,/g, ''), 10);
    });
    PatchingCompliance_po.getUnPatchedAssets().getText().then(function(text) {
      unpatched = parseInt(text.replace(/,/g, ''), 10);
    });
    browser.wait(EC.visibilityOf( PatchingCompliance_po.clickQuarters()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( PatchingCompliance_po.clickQuarters()), timeOutHigh);
    PatchingCompliance_po.clickQuarters().click();
    PatchingCompliance_po.getQuarters().then(function(items) {
      const i = items.length;
      let q_percent;
      let q_patched;
      let q_unpatched;
      $('ul.patching-quarter-wrapper .li-container .patching-each-quarter:nth-child(' + i + ') .patching-each-quarter-desc .stats-wrap .pp-stats:nth-child(1) .pp-stats-txt').getText().then(function(text) {
        q_percent = parseInt(text.replace(/,/g, ''), 10);
      });
      $('ul.patching-quarter-wrapper .li-container .patching-each-quarter:nth-child(' + i + ') .patching-each-quarter-desc .stats-wrap .pp-stats:nth-child(2) .pp-stats-txt').getText().then(function(text) {
        q_patched = parseInt(text.replace(/,/g, ''), 10);
      });
      $('ul.patching-quarter-wrapper .li-container .patching-each-quarter:nth-child(' + i + ') .patching-each-quarter-desc .stats-wrap .pp-stats:nth-child(3) .pp-stats-txt').getText().then(function(text) {
        q_unpatched = parseInt(text.replace(/,/g, ''), 10);
        expect(q_percent).toEqual(percent);
        expect(q_patched).toEqual(patched);
        expect(q_unpatched).toEqual(unpatched);
        browser.wait(EC.visibilityOf( PatchingCompliance_po.closeQuarters()), timeOutHigh);
        browser.wait(EC.elementToBeClickable( PatchingCompliance_po.closeQuarters()), timeOutHigh);
        PatchingCompliance_po.closeQuarters().click();
      });
    });
  });

  it('Check view current quarter details', () => {
    browser.wait(EC.visibilityOf(PatchingCompliance_po.viewCurrentQuarter()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(PatchingCompliance_po.viewCurrentQuarter()), timeOutHigh);
    PatchingCompliance_po.viewCurrentQuarter().click();
    browser.wait(EC.visibilityOf(PatchingCompliance_po.clickQuarters()), timeOutHigh);
    browser.wait(EC.visibilityOf(PatchingCompliance_po.getPatchedPercent()), timeOutHigh);
    let percent;
    PatchingCompliance_po.getPatchedPercent().getText().then(function(text) {
      percent = parseInt(text.replace(/,/g, ''), 10);
    });
    PatchingCompliance_po.getLatestWeek().then(function(items){
      const latest_week = items.length;
      const latest_percent = $('.percent-wrap .x-percent:nth-child(' + latest_week + ')').getText().then(function(subtext) {
        expect(percent).toEqual(parseInt(subtext, 10));
      });
    });
  });

  it('Check redirect to asset list page with matching count', () => {
    browser.wait(EC.visibilityOf(PatchingCompliance_po.getTotalAssets()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(PatchingCompliance_po.getTotalAssets()), timeOutHigh);
    let total;
    PatchingCompliance_po.getTotalAssets().getText().then(function(text) {
      total = parseInt(text.replace(/,/g, ''), 10);
    });
    PatchingCompliance_po.getTotalAssets().click();
    browser.wait(EC.visibilityOf(AssetList_po.getAssetHeaderText()), timeOutHigh);
    expect(AssetList_po.getAssetHeaderText().getText()).toEqual('Asset List');
    browser.wait(EC.visibilityOf(AssetList_po.getAssetTotalRows()), timeOutHigh);
    AssetList_po.getAssetTotalRows().getText().then(function(text) {
      expect(parseInt(text.replace(/,/g, ''), 10)).toEqual(total);
      browser.wait(EC.visibilityOf(AssetList_po.getBackArrow()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(AssetList_po.getBackArrow()), timeOutHigh);
      AssetList_po.getBackArrow().click();
    });
  });

  it('Check redirect to asset list page with filter passed', () => {
    browser.wait(EC.visibilityOf(PatchingCompliance_po.getTopDirector()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(PatchingCompliance_po.getTopDirector()), timeOutHigh);
    browser.wait(EC.visibilityOf(PatchingCompliance_po.getDirectorAppl()), timeOutHigh);
    let total;
    let appl_name;
    PatchingCompliance_po.getDirectorAppl().getText().then(function(text) {
      appl_name = text;
    });
    PatchingCompliance_po.getTopDirector().getText().then(function(text) {
      total = parseInt(text.replace(/,/g, ''), 10);
    });
    PatchingCompliance_po.getTopDirector().click();
    browser.wait(EC.visibilityOf(AssetList_po.getAssetHeaderText()), timeOutHigh);
    expect(AssetList_po.getAssetHeaderText().getText()).toEqual('Asset List');
    browser.wait(EC.visibilityOf(AssetList_po.getAssetTotalRows()), timeOutHigh);
    AssetList_po.getAssetTotalRows().getText().then(function(text) {
      expect(parseInt(text.replace(/,/g, ''), 10)).toEqual(total);
      browser.wait(EC.visibilityOf(AssetList_po.getFirstFilter()), timeOutHigh);
      AssetList_po.getAllFilters().then(function(items) {
        for ( let i = 1; i <= items.length; i++) {
          $('.floating-widgets-filter-wrapper .each-filter:nth-child(' + i + ')').getText().then(function(subtext) {
            if (subtext.toLowerCase().match(appl_name.toLowerCase())) {
              expect(subtext.toLowerCase()).toContain(appl_name.toLowerCase());
            }
          });
        }
      });
      browser.wait(EC.visibilityOf(AssetList_po.getBackArrow()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(AssetList_po.getBackArrow()), timeOutHigh);
      AssetList_po.getBackArrow().click();
    });
  });

});
