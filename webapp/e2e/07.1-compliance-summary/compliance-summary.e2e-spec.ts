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
import { OverviewCompliance } from '../page-objects/overview.po';
import { Menu } from '../page-objects/menu.po';
import { ComplianceSummary } from '../page-objects/compliance-summary.po';


const timeOutHigh = 180000;

describe('Compliance Summary', () => {
  let OverviewCompliance_po: OverviewCompliance;
  let menu_po: Menu;
  let ComplianceSummary_po: ComplianceSummary;
  const EC = protractor.ExpectedConditions;
  var percent_obj = {};

  beforeAll(() => {
    OverviewCompliance_po = new OverviewCompliance();
    menu_po = new Menu();
    ComplianceSummary_po = new ComplianceSummary();
  });

  it('Verify page title', () => {
    browser.wait(EC.visibilityOf( OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    OverviewCompliance_po.navigateToOverviewCompliance().click();
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getOverallPercent()), timeOutHigh);
    OverviewCompliance_po.getOverallPercent().getText().then(function(text) {
      percent_obj['overall'] = parseInt(text, 10);
    });
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getSecurityPercent()), timeOutHigh);
    OverviewCompliance_po.getSecurityPercent().getText().then(function(text) {
      percent_obj['security'] = parseInt(text, 10);
    });
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getCostPercent()), timeOutHigh);
    OverviewCompliance_po.getCostPercent().getText().then(function(text) {
      percent_obj['costOptimization'] = parseInt(text, 10);
    });
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getGovernancePercent()), timeOutHigh);
    OverviewCompliance_po.getGovernancePercent().getText().then(function(text) {
      percent_obj['governance'] = parseInt(text, 10);
    });
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getTaggingPercent()), timeOutHigh);
    OverviewCompliance_po.getTaggingPercent().getText().then(function(text) {
      percent_obj['tagging'] = parseInt(text, 10);
    });
    browser.wait(EC.visibilityOf(menu_po.MenuClick()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(menu_po.MenuClick()), timeOutHigh);
    menu_po.MenuClick().click();
    browser.wait(EC.visibilityOf(menu_po.ComplianceSummaryClick()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(menu_po.ComplianceSummaryClick()), timeOutHigh);
    menu_po.ComplianceSummaryClick().click();
    expect(ComplianceSummary_po.getHeaderText().getText()).toContain('Compliance summary');
  });

  it('Verify compliance percentages', () => {
    let count = 0;
    browser.wait(EC.visibilityOf(ComplianceSummary_po.getOverallPercent()), timeOutHigh);
    ComplianceSummary_po.getOverallPercent().getText().then(function(text) {
      if (percent_obj['overall'] === parseInt(text, 10)) {
        count++;
      } else {
        count = -1;
      }
    });
    browser.wait(EC.visibilityOf(ComplianceSummary_po.getSecurityPercent()), timeOutHigh);
    ComplianceSummary_po.getSecurityPercent().getText().then(function(text) {
      if (percent_obj['security'] === parseInt(text, 10)) {
        count++;
      } else {
        count = -1;
      }
    });
    browser.wait(EC.visibilityOf(ComplianceSummary_po.getCostPercent()), timeOutHigh);
    ComplianceSummary_po.getCostPercent().getText().then(function(text) {
      if (percent_obj['costOptimization'] === parseInt(text, 10)) {
        count++;
      } else {
        count = -1;
      }
    });
    browser.wait(EC.visibilityOf(ComplianceSummary_po.getGovernancePercent()), timeOutHigh);
    ComplianceSummary_po.getGovernancePercent().getText().then(function(text) {
      if (percent_obj['governance'] === parseInt(text, 10)) {
        count++;
      } else {
        count = -1;
      }
    });
    browser.wait(EC.visibilityOf(ComplianceSummary_po.getTaggingPercent()), timeOutHigh);
    ComplianceSummary_po.getTaggingPercent().getText().then(function(text) {
      if (percent_obj['tagging'] === parseInt(text, 10)) {
        count++;
      } else {
        count = -1;
      }
      expect(count).toEqual(5);
      browser.wait(EC.visibilityOf(ComplianceSummary_po.goBack()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(ComplianceSummary_po.goBack()), timeOutHigh);
      ComplianceSummary_po.goBack().click();
    });
  });

});
