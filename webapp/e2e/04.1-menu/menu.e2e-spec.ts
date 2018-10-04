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

import { browser, by, element, protractor } from 'protractor';
import { Login } from '../page-objects/login.po';
import { OverviewCompliance } from '../page-objects/overview.po';
import { TaggingCompliance } from '../page-objects/tagging-compliance.po';
import { PolicyViolations } from '../page-objects/policy-violations.po';
import { PolicyKnowledgebase } from '../page-objects/policy-knowledgebase.po';
import { AssetList } from '../page-objects/asset-list.po';
import { Menu } from '../page-objects/menu.po';

var timeOutHigh = 1800000;

describe('Menu', () => {
  let login_po: Login;
  let OverviewCompliance_po: OverviewCompliance;
  let taggingcompliance_po:TaggingCompliance;
  let policyviolations_po:PolicyViolations;
  let policyknowledgebase_po:PolicyKnowledgebase;
  let assetList_po: AssetList;
  let menu_po: Menu;
  var EC = protractor.ExpectedConditions;
  var asset_count1;
  var asset_count2;
  var asset_count3;
  var Temp;

  beforeAll(() => {
    login_po = new Login();
    OverviewCompliance_po = new OverviewCompliance();
    taggingcompliance_po = new TaggingCompliance();
    policyviolations_po = new  PolicyViolations();
    policyknowledgebase_po = new PolicyKnowledgebase();
    menu_po = new Menu();
    assetList_po = new AssetList();
  });

  it('Verify Overview Click from Menu', () => {
    browser.wait(EC.visibilityOf(menu_po.MenuClick()), 1800000);
    browser.wait(EC.elementToBeClickable(menu_po.MenuClick()), 1800000);
    menu_po.MenuClick().click();
    browser.wait(EC.visibilityOf(menu_po.OverviewComplianceClick()), 1800000);
    browser.wait(EC.elementToBeClickable(menu_po.OverviewComplianceClick()), 1800000);
    menu_po.OverviewComplianceClick().click();
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getOverviewHeaderText()), 1800000);
    var Temp=OverviewCompliance_po.getOverviewHeaderText().getText();
    expect(Temp).toEqual('Overview');
  });

  it('Verify Tagging Compliance Click from Menu', () => {
    browser.wait(EC.visibilityOf(menu_po.MenuClick()), 1800000);
    browser.wait(EC.elementToBeClickable(menu_po.MenuClick()), 1800000);
    menu_po.MenuClick().click();
    browser.wait(EC.visibilityOf(menu_po.TaggingClick()), 1800000);
    browser.wait(EC.elementToBeClickable(menu_po.TaggingClick()), 1800000);
    menu_po.TaggingClick().click();
    browser.wait(EC.visibilityOf(taggingcompliance_po.getTagHeaderText()), 1800000);
    var Temp=taggingcompliance_po.getTagHeaderText().getText();
    expect(Temp).toEqual('Tagging');
  });
 
  it('Verify Policy Violations Click from Menu', () => {
    browser.wait(EC.visibilityOf(menu_po.MenuClick()), 1800000);
    browser.wait(EC.elementToBeClickable(menu_po.MenuClick()), 1800000);
    menu_po.MenuClick().click();
    browser.wait(EC.visibilityOf(menu_po.PolicyViolationClick()), 1800000);
    browser.wait(EC.elementToBeClickable(menu_po.PolicyViolationClick()), 1800000);
    menu_po.PolicyViolationClick().click();
    browser.wait(EC.visibilityOf(policyviolations_po.getPolicyViolationsHeaderText()),1800000);
    var Temp=policyviolations_po.getPolicyViolationsHeaderText().getText();
    expect(Temp).toEqual('Policy Violations');
    
  });
  
  it('Verify PolicyKnowledgebase  Click from Menu', () => {
    browser.wait(EC.visibilityOf(menu_po.MenuClick()), 1800000);
    browser.wait(EC.elementToBeClickable(menu_po.MenuClick()), 1800000);
    menu_po.MenuClick().click();
    browser.wait(EC.visibilityOf(menu_po.PolicyKnowledgeBaseClick()),1800000);
    browser.wait(EC.elementToBeClickable(menu_po.PolicyKnowledgeBaseClick()),1800000);
    menu_po.PolicyKnowledgeBaseClick().click();
    browser.wait(EC.visibilityOf(policyknowledgebase_po.getPolicyKnowledgebaseHeaderText()),1800000);
    var Temp=policyknowledgebase_po.getPolicyKnowledgebaseHeaderText().getText();
    expect(Temp).toEqual('Policy Knowledgebase');
    
  });
  
    
});