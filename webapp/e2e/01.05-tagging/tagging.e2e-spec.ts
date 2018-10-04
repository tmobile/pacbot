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

import { browser, by, element, protractor, $ } from 'protractor';
import { Login } from '../page-objects/login.po';
import { OverviewCompliance } from '../page-objects/overview.po';
import { TaggingCompliance } from '../page-objects/tagging-compliance.po';
import { AssetList } from '../page-objects/asset-list.po';
import { Menu } from '../page-objects/menu.po';
const timeOutHigh = 1800000;

describe('Tagging Compliance', () => {
  let login_po: Login;
  let OverviewCompliance_po: OverviewCompliance;
  let menu_po: Menu;
  const EC = protractor.ExpectedConditions;
  let taggingcompliance_po: TaggingCompliance;
  let assetList_po: AssetList;
  let tag_percent_summary;
  let tag_percent_compliance;
  let asset_count1;
  let asset_count2;
  let asset_count3;
  let Temp;

  beforeAll(() => {
    login_po = new Login();
    OverviewCompliance_po = new OverviewCompliance();
    menu_po = new Menu();
    taggingcompliance_po = new TaggingCompliance();
    assetList_po = new AssetList();
  });

  it('SSO login', () => {
    OverviewCompliance_po.navigateToOverviewComplianceGet();
    // browser.wait(EC.elementToBeClickable(login_po.getLoginButton()), timeOutHigh);
    // login_po.getLoginButton().click();
    // login_po.clickNext().sendKeys('pacbot@t-mobile.com');
    // login_po.submitNext().click();
    // OverviewCompliance_po.navigateToOverviewCompliance().click();
    const page_title = OverviewCompliance_po.getPageTitle().getText();
    expect(page_title).toEqual('Overview');
  });

  it('Check if  Untagged Assets by App (Environment Untagged counts) matches Asset List Table', () => {
  // browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
  //   OverviewCompliance_po.navigateToOverviewCompliance().click();
    taggingcompliance_po.navigateToTaggingCompliance().click();
      browser.wait(EC.visibilityOf(taggingcompliance_po.getApplicationfirstValue()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(taggingcompliance_po.getApplicationfirstValue()), timeOutHigh);
      taggingcompliance_po.getAllListUntaggedAssetsTable().then(function(items) {
        for (let i = 1; i < 8; i++) {
        browser.executeScript('arguments[0].scrollIntoView();', $('.data-table-inner-wrap .data-table-rows:nth-child(' + i + ') .row-cells:nth-child(2) .column-wrapper a').getWebElement());
        $('.data-table-inner-wrap .data-table-rows:nth-child(' + i + ') .row-cells:nth-child(2) .column-wrapper a').getText().then(function (text) {
          $('.data-table-inner-wrap .data-table-rows:nth-child(' + i + ') .row-cells:nth-child(2) .column-wrapper a').click();
          browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), timeOutHigh);
          asset_count1 = text;
           assetList_po.getAssetTotalRows().getText().then(function(bottomText) {
            expect(asset_count1).toEqual(bottomText);
            assetList_po.goBack().click();
            });
          });
        }
      });
    });

    it('Check if  Untagged Assets by App (Role Untagged counts) matches Asset List Table', () => {
      // browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
      //   OverviewCompliance_po.navigateToOverviewCompliance().click();
        taggingcompliance_po.navigateToTaggingCompliance().click();
          browser.wait(EC.visibilityOf(taggingcompliance_po.getApplicationfirstValue()), timeOutHigh);
          browser.wait(EC.elementToBeClickable(taggingcompliance_po.getApplicationfirstValue()), timeOutHigh);
          taggingcompliance_po.getAllListUntaggedAssetsTable().then(function(items) {
            for (let i = 1; i < items.length; i++) {
            browser.executeScript('arguments[0].scrollIntoView();', $('.data-table-inner-wrap .data-table-rows:nth-child(' + i + ') .row-cells:nth-child(3) .column-wrapper a').getWebElement());
            $('.data-table-inner-wrap .data-table-rows:nth-child(' + i + ') .row-cells:nth-child(3) .column-wrapper a').getText().then(function (text) {
              $('.data-table-inner-wrap .data-table-rows:nth-child(' + i + ') .row-cells:nth-child(3) .column-wrapper a').click();
              browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), timeOutHigh);
              asset_count1 = text;
               assetList_po.getAssetTotalRows().getText().then(function(subtext) {
                expect(asset_count1).toEqual(subtext);
                assetList_po.goBack().click();
                });
              });
            }
          });
        });

        it('Check if  Untagged Assets by App (Stack Untagged counts) matches Asset List Table', () => {
          // browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
          //   OverviewCompliance_po.navigateToOverviewCompliance().click();
            taggingcompliance_po.navigateToTaggingCompliance().click();
              browser.wait(EC.visibilityOf(taggingcompliance_po.getApplicationfirstValue()), timeOutHigh);
              browser.wait(EC.elementToBeClickable(taggingcompliance_po.getApplicationfirstValue()), timeOutHigh);
              taggingcompliance_po.getAllListUntaggedAssetsTable().then(function(items) {
                for (let i = 1; i < items.length; i++) {
                browser.executeScript('arguments[0].scrollIntoView();', $('.data-table-inner-wrap .data-table-rows:nth-child(' + i + ') .row-cells:nth-child(4) .column-wrapper a').getWebElement());
                $('.data-table-inner-wrap .data-table-rows:nth-child(' + i + ') .row-cells:nth-child(4) .column-wrapper a').getText().then(function (text) {
                  $('.data-table-inner-wrap .data-table-rows:nth-child(' + i + ') .row-cells:nth-child(4) .column-wrapper a').click();
                  browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), timeOutHigh);
                  asset_count1 = text;
                   assetList_po.getAssetTotalRows().getText().then(function(subtext) {
                    expect(asset_count1).toEqual(subtext);
                    assetList_po.goBack().click();
                    });
                  });
                }
              });
            });

    it('Check if  Tagging across target types for Tagging matches Asset List Table', () => {
      // browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
      //   OverviewCompliance_po.navigateToOverviewCompliance().click();
        taggingcompliance_po.navigateToTaggingCompliance().click();
          browser.wait(EC.visibilityOf(taggingcompliance_po.getApplicationfirstValue()), timeOutHigh);
          taggingcompliance_po.getAllListUntaggedAssetsTable().then(function(items) {
            for (let i = 1; i <= 2; i++) {
            browser.executeScript('arguments[0].scrollIntoView();', $('.tiles-wrapper .container-parent li:nth-child(' + i + ') .tagging-info-wrapper .left-wrapper .count').getWebElement());
            $('.tiles-wrapper .container-parent li:nth-child(' + i + ') .tagging-info-wrapper .left-wrapper .count').getText().then(function (text) {
              $('.tiles-wrapper .container-parent li:nth-child(' + i + ') .tagging-info-wrapper .left-wrapper .count').click();
              browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), timeOutHigh);
              asset_count1 = text;
                assetList_po.getAssetTotalRows().getText().then(function(subtext) {
                  expect(asset_count1).toEqual(subtext);
                assetList_po.goBack().click();
                });
              });
            }
          });
        });

        it('Check if  Tagging across target types for Untagging matches Asset List Table', () => {
          // browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
          //   OverviewCompliance_po.navigateToOverviewCompliance().click();
            taggingcompliance_po.navigateToTaggingCompliance().click();
              browser.wait(EC.visibilityOf(taggingcompliance_po.getApplicationfirstValue()), timeOutHigh);
              taggingcompliance_po.getAllListUntaggedAssetsTable().then(function(items) {
                for (let i = 1; i <= 2; i++) {
                browser.executeScript('arguments[0].scrollIntoView();', $('.tiles-wrapper .container-parent li:nth-child(' + (i + 1) + ') .tagging-info-wrapper .right-wrapper .count').getWebElement());
                $('.tiles-wrapper .container-parent li:nth-child(' + (i + 1) + ') .tagging-info-wrapper .right-wrapper .count').getText().then(function (text) {
                  $('.tiles-wrapper .container-parent li:nth-child(' + (i + 1) +  ') .tagging-info-wrapper .right-wrapper .count').click();
                  browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), timeOutHigh);
                  asset_count1 = text;
                    assetList_po.getAssetTotalRows().getText().then(function(subtext) {
                    expect(asset_count1).toEqual(subtext);
                   assetList_po.goBack().click();
                    });
                  });
                }
              });
            });

  it('Verify Tagging Percent ', () => {
  // browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
  //   OverviewCompliance_po.navigateToOverviewCompliance().click();
    taggingcompliance_po.navigateToTaggingCompliance().click();
    browser.wait(EC.visibilityOf(taggingcompliance_po.getTaggingPercent()), timeOutHigh);
    tag_percent_summary = taggingcompliance_po.getTaggingPercent().getText().then(function (text) {
      text = text.replace(/,/g, '');
      tag_percent_summary = parseInt(text, 10);
      });
      browser.wait(EC.visibilityOf(taggingcompliance_po.getTotalTaggingPercent()), timeOutHigh);
      taggingcompliance_po.getTotalTaggingPercent().getText().then(function (text) {
      text = text.replace(/,/g, '');
      tag_percent_compliance = parseInt(text, 10);
      expect(tag_percent_summary).toEqual(tag_percent_compliance);
      });
  });

  it('Verify Total Asset Count', () => {
    browser.wait(EC.presenceOf(menu_po.MenuClick()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(menu_po.MenuClick()), timeOutHigh);
    menu_po.MenuClick().click();
    browser.wait(EC.visibilityOf(menu_po.TaggingClick()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(menu_po.TaggingClick()), timeOutHigh);
    menu_po.TaggingClick().click();
    browser.wait(EC.visibilityOf(taggingcompliance_po.getOverallAssets()), timeOutHigh);
    taggingcompliance_po.getOverallAssets().getText().then(function (text) {
      text = text.replace(/,/g, '');
      asset_count1 = text;
      });
    taggingcompliance_po.getOverallAssets().click();
    browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), timeOutHigh);
    Temp = assetList_po.getAssetTotalRows().getText().then(function (text) {
      text = text.replace(/,/g, '');
      expect(asset_count1).toEqual(text);
      });
    browser.wait(EC.elementToBeClickable(assetList_po.getBackArrowEle()), timeOutHigh);
    assetList_po.getBackArrow().click();
  });

  it('Verify Total Tagging Count', () => {
    browser.wait(EC.elementToBeClickable(taggingcompliance_po.getOverallAssets()), timeOutHigh);
    taggingcompliance_po.navigateToTaggingCompliance().click();
    browser.wait(EC.visibilityOf(taggingcompliance_po.getOverallTagging()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(taggingcompliance_po.getOverallTagging()), timeOutHigh);
    taggingcompliance_po.getOverallTagging().getText().then(function (text) {
      text = text.replace(/,/g, '');
      asset_count2 = text;
     });
    taggingcompliance_po.getOverallTagging().click();
    browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), timeOutHigh);
    Temp = assetList_po.getAssetTotalRows().getText().then(function (text) {
      text = text.replace(/,/g, '');
      expect(asset_count2).toEqual(text);
    });
    browser.wait(EC.elementToBeClickable(assetList_po.getBackArrowEle()), timeOutHigh);
     assetList_po.getBackArrow().click();
  });

  it('Verify Total Untagging Count', () => {
    browser.wait(EC.elementToBeClickable(taggingcompliance_po.getOverallAssets()), timeOutHigh);
    taggingcompliance_po.navigateToTaggingCompliance().click();
    browser.wait(EC.visibilityOf(taggingcompliance_po.getOverallunTagging()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(taggingcompliance_po.getOverallunTagging()), timeOutHigh);
    taggingcompliance_po.getOverallunTagging().getText().then(function (text) {
      text = text.replace(/,/g, '');
      asset_count3 = text;
     });
    taggingcompliance_po.getOverallunTagging().click();
    browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), timeOutHigh);
    Temp = assetList_po.getAssetTotalRows().getText().then(function (text) {
      text = text.replace(/,/g, '');
      expect(asset_count3).toEqual(text);
    });
    browser.wait(EC.elementToBeClickable(assetList_po.getBackArrowEle()), timeOutHigh);
     assetList_po.getBackArrow().click();
  });

    it('Verify Total Tagging Sum', () => {
    browser.wait(EC.elementToBeClickable(taggingcompliance_po.getOverallAssets()), timeOutHigh);
    taggingcompliance_po.navigateToTaggingCompliance().click();
    browser.wait(EC.visibilityOf(taggingcompliance_po.getOverallunTagging()), timeOutHigh);

    asset_count1 = taggingcompliance_po.getOverallAssets().getText().then(function (text) {
      text = text.replace(/,/g, '');
      asset_count1 = text;
    });
    asset_count2 = taggingcompliance_po.getOverallTagging().getText().then(function (text) {
      text = text.replace(/,/g, '');
      asset_count2 = text;
    });
    asset_count3 = taggingcompliance_po.getOverallunTagging().getText().then(function (text) {
      text = text.replace(/,/g, '');
      asset_count3 = text;
      Temp = parseInt(asset_count2, 10) + parseInt(asset_count3, 10);
      expect(Temp).toEqual(Number.parseInt(asset_count1, 10));
      });
  });

  it('verify untagged assets by app table name and  headers', () => {
    browser.wait(EC.visibilityOf(taggingcompliance_po.getTableHeader()), timeOutHigh);
    browser.wait(EC.visibilityOf(taggingcompliance_po.getApplicationHeader()), timeOutHigh);
    const application_path = taggingcompliance_po.getApplicationHeader().getText();
    expect(application_path).toEqual('application');
    const environment_path = taggingcompliance_po.getEnvironmentUntagged().getText();
    expect(environment_path).toEqual('environment Untagged');
     const role_path = taggingcompliance_po.getRoleUntagged().getText();
    expect(role_path).toEqual('role Untagged');
    const stack_path = taggingcompliance_po.getStackUntagged().getText();
    expect(stack_path).toEqual('stack Untagged');
  });

  it('verify appearance of additional details', () => {
    browser.wait(EC.visibilityOf(taggingcompliance_po.getApplicationfirstValue()), timeOutHigh);
    taggingcompliance_po.getApplicationfirstValue().click();
    browser.wait(EC.visibilityOf(taggingcompliance_po.getAdditionalDetailsHeaderText()), timeOutHigh);
    const additional_detail_path = taggingcompliance_po.getAdditionalDetailsHeaderText().getText();
    expect(additional_detail_path).toEqual('Additional Details');
    browser.wait(EC.elementToBeClickable(taggingcompliance_po.getAdditionaldetailsCrossMark()), timeOutHigh);
    taggingcompliance_po.getAdditionaldetailsCrossMark().click();
  });

  it('verify csv download', () => {
    let download_successful = false;
    browser.wait(EC.presenceOf( taggingcompliance_po.getAssetTotalRows()), timeOutHigh);
    const filename = process.cwd() + '/e2e/downloads/Tagging Untagged Assets.csv';
    const fs = require('fs');
    const myDir = process.cwd() + '/e2e/downloads';
    if (!taggingcompliance_po.checkDirExists(myDir)) {
      fs.mkdirSync(myDir);
    } else if ((fs.readdirSync(myDir).length) > 0 && fs.existsSync(filename)) {
      fs.unlinkSync(filename);
    }
    browser.wait(EC.visibilityOf(taggingcompliance_po.getAssetTotalRows()), timeOutHigh);
    taggingcompliance_po.getdownloadIcon().click();
    browser.wait(EC.visibilityOf(taggingcompliance_po.getToastMsg()), timeOutHigh).then(function() {
      browser.wait(EC.invisibilityOf(taggingcompliance_po.getDownloadRunningIcon()), 600000).then(function() {
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

    it('verify Untagged Assets by App table search', () => {
      browser.wait(EC.visibilityOf(taggingcompliance_po.getSearchLabel()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(taggingcompliance_po.getSearchLabel()), timeOutHigh);
      taggingcompliance_po.getSearchLabel().click();
      browser.wait(EC.visibilityOf(taggingcompliance_po.getSearchInput()), timeOutHigh);
      taggingcompliance_po.getSearchInput().sendKeys('datalake');
      browser.actions().sendKeys(protractor.Key.ENTER).perform();
      browser.wait(EC.visibilityOf(taggingcompliance_po.getFirstStatusRow()), timeOutHigh);
      const status_path = taggingcompliance_po.getFirstStatusRow().getText();
      expect(status_path).toEqual('DataLake');
    });

    it('Verify help text window  is opening', () => {
      browser.wait(EC.visibilityOf(taggingcompliance_po.openHelp()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(taggingcompliance_po.openHelp()), timeOutHigh);
      taggingcompliance_po.openHelp().click();
      const help_title = OverviewCompliance_po.getHelpTitle().getText();
      expect(help_title).toEqual('Help');
      browser.wait(EC.elementToBeClickable(taggingcompliance_po.getHelpClose()), timeOutHigh);
      taggingcompliance_po.getHelpClose().click();
    });

    it('Check if  Untagged counts in Total Tag Compliance matches Asset List Table', () => {
      browser.wait(EC.visibilityOf(taggingcompliance_po.navigateToTaggingCompliance()), timeOutHigh);
      // browser.wait(EC.elementToBeClickable( taggingcompliance_po.getListTableFirstTotal()), timeOutHigh);
      taggingcompliance_po.getAllList().then(function(items) {
      for (let i = 1; i < items.length; i++) {
        browser.executeScript('arguments[0].scrollIntoView();', $('.list-table-inner-wrapper .list-table-each-list:nth-child(' + (i + 1) + ') .list-table-value .list-table-count-each:nth-child(1)').getWebElement());
        $('.list-table-inner-wrapper .list-table-each-list:nth-child(' + (i + 1) + ') .list-table-value .list-table-count-each:nth-child(1)').getText().then(function (text) {
        $('.list-table-inner-wrapper .list-table-each-list:nth-child(' + (i + 1) + ') .list-table-value .list-table-count-each:nth-child(1)').click();
        browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), timeOutHigh);
        asset_count1 = text;
        assetList_po.getAssetTotalRows().getText().then(function(subtext) {
        expect(asset_count1).toEqual(subtext);
        assetList_po.goBack().click();
              });
            }
          );
        }
      });
    });

    it('Check if  Tagged counts in Total Tag Compliance matches Asset List Table', () => {
      browser.wait(EC.visibilityOf(taggingcompliance_po.getApplicationtagHeader()), timeOutHigh);
      browser.wait(EC.elementToBeClickable( taggingcompliance_po.getListTableSecondTotal()), timeOutHigh);
      taggingcompliance_po.getAllList().then(function(items) {
      for (let i = 1; i < items.length; i++) {
        browser.executeScript('arguments[0].scrollIntoView();', $('.list-table-inner-wrapper .list-table-each-list:nth-child(' + (i + 1) + ') .list-table-value .list-table-count-each:nth-child(2)').getWebElement());
        $('.list-table-inner-wrapper .list-table-each-list:nth-child(' + (i + 1) + ') .list-table-value .list-table-count-each:nth-child(2)').getText().then(function (text) {
        $('.list-table-inner-wrapper .list-table-each-list:nth-child(' + (i + 1) + ') .list-table-value .list-table-count-each:nth-child(2)').click();
        browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), timeOutHigh);
        asset_count1 = text;
        assetList_po.getAssetTotalRows().getText().then(function(subtext) {
        expect(asset_count1).toEqual(subtext);
        assetList_po.goBack().click();
              });
            }
          );
        }
      });
    });
});
