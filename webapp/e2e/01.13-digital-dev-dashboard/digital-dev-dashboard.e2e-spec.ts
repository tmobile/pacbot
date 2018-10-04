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

import { browser, protractor, $, $$, Browser} from 'protractor';
import {AssetList} from '../page-objects/asset-list.po';
import { Menu } from '../page-objects/menu.po';
import { Login } from '../page-objects/login.po';
import { OverviewCompliance } from '../page-objects/overview.po';
import { CONFIGURATIONS } from '../../src/config/configurations';
import { DigitalDevDashboard } from '../page-objects/digital-dev-dashboard.po';
import { AssetGroups } from '../page-objects/asset-groups.po';
const config = CONFIGURATIONS.optional.general.e2e;

describe('DigitalDevDashboard', () => {
  let menu_po: Menu;
  let assetList_po: AssetList;
  const EC = protractor.ExpectedConditions;
  let login_po: Login;
  let OverviewCompliance_po: OverviewCompliance;
  let digitalDevDashboard_po: DigitalDevDashboard;
  let assetGroups_po: AssetGroups;
  const maxTimeOut = 120000;
  let pr_count;

  beforeAll(() => {
    menu_po = new Menu();
    assetList_po = new AssetList();
    login_po = new Login();
    OverviewCompliance_po = new OverviewCompliance();
    digitalDevDashboard_po = new DigitalDevDashboard();
    assetGroups_po = new AssetGroups();
  });

  it('navigate to Digital dev dashboard page', () => {
    browser.wait(EC.visibilityOf(OverviewCompliance_po.changeAssetGroupPath()), maxTimeOut);
    browser.wait(EC.elementToBeClickable(OverviewCompliance_po.changeAssetGroupPath()), maxTimeOut);
    OverviewCompliance_po.changeAssetGroupPath().click();
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getAssetGroupSearch()), maxTimeOut);
    OverviewCompliance_po.getAssetGroupSearch().sendKeys('digital development');
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
    browser.wait(EC.visibilityOf(assetGroups_po.clickFirstAssetGroup()), maxTimeOut);
    browser.wait(EC.elementToBeClickable(assetGroups_po.clickFirstAssetGroup()), maxTimeOut);
    assetGroups_po.clickFirstAssetGroup().click().then( function() {
      browser.wait(EC.elementToBeClickable(OverviewCompliance_po.selectFirstAssetgroupInList()), maxTimeOut);
      OverviewCompliance_po.selectFirstAssetgroupInList().click();
      browser.wait(EC.visibilityOf(OverviewCompliance_po.getPageTitle()), maxTimeOut);
      const page_title = OverviewCompliance_po.getPageTitle().getText();
      expect(page_title).toEqual('Overview');
      browser.wait(EC.elementToBeClickable(menu_po.MenuClick()), maxTimeOut);
      menu_po.MenuClick().click();
      browser.wait(EC.elementToBeClickable(menu_po.digitalDashboardClick()), maxTimeOut);
      menu_po.digitalDashboardClick().click();
      browser.wait(EC.visibilityOf(digitalDevDashboard_po.getDigitalDashboardHeaderText()), maxTimeOut);
      const header_path = digitalDevDashboard_po.getDigitalDashboardHeaderText().getText();
      expect(header_path).toEqual('Digital Dev');
    });
  });


  it('Check if repository distribution donut graph legend percentage sums to 100', () => {
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getRepositoryDistributionDonutGraph()), maxTimeOut);
    const total_percent = 100;
    let each_pass = 0;
    digitalDevDashboard_po.getRepositoryDistributionDonutlegend().then(function(items) {
      for (let i = 0; i < items.length; i++) {
        browser.wait(EC.visibilityOf($$('.digital-dev-strategy-distribution-wrapper .legend-each .legend-text-right').get(i)), maxTimeOut);
        $$('.digital-dev-strategy-distribution-wrapper .legend-each .legend-text-right').get(i).getText().then(function (text) {
          text = text.split('(')[1].replace('%)', '');
          each_pass = each_pass + parseInt(text, 10);
          if ( i === items.length - 1) {
            expect(each_pass).toEqual(total_percent);
          }
        });
      }
    });
  });

  it('verify total PR metrics count of a quarter with total PR count of all weeks using bar graph', () => {
    let total_created = 0;
    let total_merged = 0;
    let total_declined = 0;
    let total_open = 0;
    let weeks = 0;
    let weeks_total_created = 0;
    let weeks_total_merged = 0;
    let weeks_total_declined = 0;
    let weeks_total_open = 0;
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getQuarterSelector()), maxTimeOut);
    browser.actions().mouseMove(digitalDevDashboard_po.getQuarterSelector());
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getQuarterDisplay()), maxTimeOut);
    // browser.driver.executeScript('arguments[0].scrollIntoView();', digitalDevDashboard_po.getQuarterDisplay());
    browser.wait(EC.elementToBeClickable(digitalDevDashboard_po.getQuarterDisplay()), maxTimeOut);
    digitalDevDashboard_po.getQuarterDisplay().click();
    // browser.actions().mouseMove(digitalDevDashboard_po.getQuarterDisplay()).click().perform();
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getQuarterViewButton()), maxTimeOut);
    browser.wait(EC.elementToBeClickable(digitalDevDashboard_po.getQuarterViewButton()), maxTimeOut);
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getFirstQuaterTotalCreatedCount()), maxTimeOut).then(function () {
      digitalDevDashboard_po.getFirstQuaterTotalCreatedCount().getText().then( function (count) {
        total_created = parseInt(count, 10);
      });
      browser.wait(EC.visibilityOf(digitalDevDashboard_po.getFirstQuaterTotalMergedCount()), maxTimeOut);
      digitalDevDashboard_po.getFirstQuaterTotalMergedCount().getText().then( function (count) {
        total_merged = parseInt(count, 10);
      });
      browser.wait(EC.visibilityOf(digitalDevDashboard_po.getFirstQuaterTotalDeclinedCount()), maxTimeOut);
      digitalDevDashboard_po.getFirstQuaterTotalDeclinedCount().getText().then( function (count) {
        total_declined = parseInt(count, 10);
      });
      browser.wait(EC.visibilityOf(digitalDevDashboard_po.getFirstQuaterTotalOpenCount()), maxTimeOut);
      digitalDevDashboard_po.getFirstQuaterTotalOpenCount().getText().then( function (count) {
        total_open = parseInt(count, 10);
      });
      digitalDevDashboard_po.getQuarterViewButton().click();
      browser.wait(EC.visibilityOf(digitalDevDashboard_po.getTotalWeeksInSelectedQuarter()), maxTimeOut);
      digitalDevDashboard_po.getTotalWeeksInSelectedQuarter().getText().then( function (text) {
        text = text.split(' ')[1];
        weeks = parseInt(text, 10);
        console.log('weeks:', weeks);
        for (let i = weeks - 1; i >= 0; i--) {
          browser.wait(EC.visibilityOf($$('.pull-request-matrix-line-chart .graph-container .z-3').$$('.each-column').get(i)), maxTimeOut);
          $$('.pull-request-matrix-line-chart .graph-container .z-3').$$('.each-column').get(i).click().then(function () {
            browser.wait(EC.visibilityOf(digitalDevDashboard_po.getPRMetricsCreatedBarChartTextLink()), maxTimeOut);
            browser.wait(EC.elementToBeClickable(digitalDevDashboard_po.getPRMetricsCreatedBarChartTextLink()), maxTimeOut);
            digitalDevDashboard_po.getPRMetricsCreatedBarChartTextLink().getText().then( function (count) {
              weeks_total_created = weeks_total_created + parseInt(count, 10);
              if (i === 0 ) {
                expect(total_created).toEqual(weeks_total_created);
              }
            });
            browser.wait(EC.visibilityOf(digitalDevDashboard_po.getPRMetricsMergedBarChartTextLink()), maxTimeOut);
            browser.wait(EC.elementToBeClickable(digitalDevDashboard_po.getPRMetricsMergedBarChartTextLink()), maxTimeOut);
            digitalDevDashboard_po.getPRMetricsMergedBarChartTextLink().getText().then( function (count) {
              weeks_total_merged = weeks_total_merged + parseInt(count, 10);
              if (i === 0 ) {
                expect(total_merged).toEqual(weeks_total_merged);
              }
            });
            browser.wait(EC.visibilityOf(digitalDevDashboard_po.getPRMetricsDeclinedBarChartTextLink()), maxTimeOut);
            browser.wait(EC.elementToBeClickable(digitalDevDashboard_po.getPRMetricsDeclinedBarChartTextLink()), maxTimeOut);
            digitalDevDashboard_po.getPRMetricsDeclinedBarChartTextLink().getText().then( function (count) {
              weeks_total_declined = weeks_total_declined + parseInt(count, 10);
              if (i === 0 ) {
                expect(total_declined).toEqual(weeks_total_declined);
              }
            });
            browser.wait(EC.visibilityOf(digitalDevDashboard_po.getPRMetricsOpenBarChartTextLink()), maxTimeOut);
            browser.wait(EC.elementToBeClickable(digitalDevDashboard_po.getPRMetricsOpenBarChartTextLink()), maxTimeOut);
            digitalDevDashboard_po.getPRMetricsOpenBarChartTextLink().getText().then( function (count) {
              weeks_total_open = weeks_total_open + parseInt(count, 10);
              if (i === 0) {
                expect(total_open).toEqual(weeks_total_open);
              }
            });
          });
        }
      });
    });
  });

  it('verify navigation of PR age bar graph text link to asset list with verification of pagination count', () => {
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getPRAgeBarChartTextLink()), maxTimeOut);
    browser.actions().mouseMove(digitalDevDashboard_po.getPRAgeBarChartTextLink()).perform();
    browser.wait(EC.elementToBeClickable(digitalDevDashboard_po.getPRAgeBarChartTextLink()), maxTimeOut);
    pr_count = digitalDevDashboard_po.getPRAgeBarChartTextLink().getText();
    digitalDevDashboard_po.getPRAgeBarChartTextLink().click();
    browser.wait(EC.visibilityOf(assetList_po.getAssetHeaderText()), maxTimeOut);
    const asset_list_header_path = assetList_po.getAssetHeaderText().getText();
    expect(asset_list_header_path).toEqual('Asset List');
    browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), maxTimeOut);
    assetList_po.getAssetTotalRows().getText().then( function(text){
      expect(text).toEqual(pr_count);
      browser.wait(EC.elementToBeClickable(assetList_po.goBack()), maxTimeOut);
      assetList_po.goBack().click();
    });
  });

  it('Check if stale branch donut graph legend percentage sums to 100', () => {
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getStaleBranchDonutGraph()), maxTimeOut);
    browser.actions().mouseMove(digitalDevDashboard_po.getPRAgeBarChartTextLink()).perform();
    const total_percent = 100;
    let each_pass = 0;
    digitalDevDashboard_po.getStaleBranchDonutlegend().then(function(items) {
      for (let i = 0; i < items.length; i++) {
        browser.wait(EC.visibilityOf($$('.donut-container-staleBranchDonut .legend-each .legend-text-right').get(i)), maxTimeOut);
        $$('.donut-container-staleBranchDonut .legend-each .legend-text-right').get(i).getText().then(function (text) {
          text = text.split('(')[1].replace('%)', '');
          each_pass = each_pass + parseInt(text, 10);
          if ( i === items.length - 1) {
            expect(each_pass).toEqual(total_percent);
          }
        });
      }
    });
  });

  it('Check if PR age bar graph text sums to Open PR', () => {
    let total_percent = 0;
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getPRAgeBarChartTextLink()), maxTimeOut);
    browser.actions().mouseMove(digitalDevDashboard_po.getPRAgeBarChartTextLink()).perform();
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getTotalOpenPR()), maxTimeOut);
    digitalDevDashboard_po.getTotalOpenPR().getText().then( function(count) {
      total_percent = parseInt(count, 10);
    });
    let each_pass = 0;
    digitalDevDashboard_po.getPRAgeBarGraph().then(function(items) {
      for (let i = 0; i < items.length; i++) {
        browser.wait(EC.visibilityOf($$('app-dev-standard-pull-request-age .show-links text.bar.bar-links').get(i)), maxTimeOut);
        $$('app-dev-standard-pull-request-age .show-links text.bar.bar-links').get(i).getText().then(function (text) {
          each_pass = each_pass + parseInt(text, 10);
          if ( i === items.length - 1) {
            expect(each_pass).toEqual(total_percent);
          }
        });
      }
    });
  });

  it('Check if stale branch age bar graph text sums to stale branches legend in donut graph', () => {
    let total_percent = 0;
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getStaleBranchesDonutlegendCount()), maxTimeOut);
    browser.actions().mouseMove(digitalDevDashboard_po.getStaleBranchesDonutlegendCount()).perform();
    digitalDevDashboard_po.getStaleBranchesDonutlegendCount().getText().then( function(count) {
      count = count.split('(')[0];
      total_percent = parseInt(count, 10);
    });
    let each_pass = 0;
    digitalDevDashboard_po.getStaleBranchBarGraph().then(function(items) {
      for (let i = 0; i < items.length; i++) {
        browser.wait(EC.visibilityOf($$('app-dev-standard-stale-branch-age text.bar.bar-links').get(i)), maxTimeOut);
        $$('app-dev-standard-stale-branch-age text.bar.bar-links').get(i).getText().then(function (text) {
          each_pass = each_pass + parseInt(text, 10);
          if ( i === items.length - 1) {
            expect(each_pass).toEqual(total_percent);
          }
        });
      }
    });
  });


  it('verify filter for policy violation table', () => {
    // click on filter dropdown to get list
    browser.wait(EC.presenceOf(digitalDevDashboard_po.getFilterArrow()), maxTimeOut);
    digitalDevDashboard_po.getFilterArrow().click();
    browser.sleep(2000);
    let filterKey, filterValue;
    // select first filter type
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getFilterType()), maxTimeOut);
    digitalDevDashboard_po.getFilterType().getText().then( function(text) {
      filterKey = text;
    });
    digitalDevDashboard_po.getFilterType().click();
    // verify whether filter tags present
    browser.sleep(3000);
    // select first filter tag
    digitalDevDashboard_po.getFilterTagInput().click();
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getFilterTags()), maxTimeOut);
    digitalDevDashboard_po.getFilterTags().getText().then( function(text) {
      filterValue = text;
    });
    digitalDevDashboard_po.getFilterTags().click();
    // equate selected filter key and value with filter tags displayed
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getFilterSelected()), maxTimeOut);
    digitalDevDashboard_po.getFilterSelected().getText().then( function(text) {
      const textArray = text.split(':');
      expect(textArray[0]).toContain(filterKey);
      expect(textArray[1]).toContain(filterValue);
    });
    digitalDevDashboard_po.getClearAllFilter().click();
  });

  it('verify navigation of PR metrics bar graph text link to asset list with verification of pagination count', () => {
    browser.wait(EC.visibilityOf(digitalDevDashboard_po.getPRMetricsCreatedBarChartTextLink()), maxTimeOut).then (function() {
    browser.wait(EC.elementToBeClickable(digitalDevDashboard_po.getPRMetricsCreatedBarChartTextLink()), maxTimeOut);
    pr_count = digitalDevDashboard_po.getPRMetricsCreatedBarChartTextLink().getText();
    digitalDevDashboard_po.getPRMetricsCreatedBarChartTextLink().click();
    browser.wait(EC.visibilityOf(assetList_po.getAssetHeaderText()), maxTimeOut);
    const asset_list_header_path = assetList_po.getAssetHeaderText().getText();
    expect(asset_list_header_path).toEqual('Asset List');
    browser.wait(EC.visibilityOf(assetList_po.getAssetTotalRows()), maxTimeOut);
    assetList_po.getAssetTotalRows().getText().then( function(text){
      expect(text).toEqual(pr_count);
      browser.wait(EC.elementToBeClickable(assetList_po.goBack()), maxTimeOut);
      assetList_po.goBack().click();
    });
    });
  });

  it('change asset group back to aws all', () => {
    browser.wait(EC.visibilityOf(OverviewCompliance_po.changeAssetGroupPath()), maxTimeOut);
    browser.wait(EC.elementToBeClickable(OverviewCompliance_po.changeAssetGroupPath()), maxTimeOut);
    OverviewCompliance_po.changeAssetGroupPath().click();
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getAssetGroupSearch()), maxTimeOut);
    OverviewCompliance_po.getAssetGroupSearch().sendKeys('aws all');
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
    browser.wait(EC.visibilityOf(assetGroups_po.clickFirstAssetGroup()), maxTimeOut);
    browser.wait(EC.elementToBeClickable(assetGroups_po.clickFirstAssetGroup()), maxTimeOut);
    assetGroups_po.clickFirstAssetGroup().click().then( function() {
      browser.wait(EC.elementToBeClickable(OverviewCompliance_po.selectFirstAssetgroupInList()), maxTimeOut);
      OverviewCompliance_po.selectFirstAssetgroupInList().click();
      browser.wait(EC.visibilityOf(OverviewCompliance_po.getPageTitle()), maxTimeOut);
      const page_title = OverviewCompliance_po.getPageTitle().getText();
      expect(page_title).toEqual('Overview');
    });
  });


});
