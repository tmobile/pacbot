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

import { browser, element, by, ExpectedConditions, protractor, $} from 'protractor';
import { Alert } from 'selenium-webdriver';
import { Login } from '../page-objects/login.po';
import { OmniSearch } from '../page-objects/omnisearch.po';
import { OverviewCompliance } from '../page-objects/overview.po';
import { TaggingCompliance } from '../page-objects/tagging-compliance.po';
import { PolicyViolations } from '../page-objects/policy-violations.po';
import { AssetDetails } from '../page-objects/asset-details.po';
import { PolicyKnowledgebase } from '../page-objects/policy-knowledgebase.po';
import { Menu } from '../page-objects/menu.po';
import { toBase64String } from '../../node_modules/@angular/compiler/src/output/source_map';

const timeOutHigh = 180000;

describe('OmniSearch', () => {
  let login_po: Login;
  let AssetDetails_po: AssetDetails;
  let OverviewCompliance_po: OverviewCompliance;
  let menu_po: Menu;
  let taggingcompliance_po: TaggingCompliance;
  let OmniSearch_po: OmniSearch;
  let policyviolations_po: PolicyViolations;
  const EC = protractor.ExpectedConditions;
  let policyknowledgebase_po: PolicyKnowledgebase;

  beforeAll(() => {
    login_po = new Login();
    AssetDetails_po = new AssetDetails();
    OverviewCompliance_po = new OverviewCompliance();
    menu_po = new Menu();
    taggingcompliance_po = new TaggingCompliance();
    OmniSearch_po = new OmniSearch();
    policyviolations_po = new  PolicyViolations();
    policyknowledgebase_po = new PolicyKnowledgebase();
    });


 it('Checking navigates to OmniSearch  and all the dropdown functionlities  ', () => {

      browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOmniSearch() ), timeOutHigh);
      browser.wait(EC.visibilityOf(OverviewCompliance_po.navigateToOmniSearch() ), timeOutHigh);
      OmniSearch_po.navigateToOmniSearch().click();
   browser.wait(EC.visibilityOf(OmniSearch_po.navigateToDropDownButton() ), timeOutHigh);
   OmniSearch_po.navigateToDropDownButton().click();
    // OmniSearch_po.AssetDropDownCheck();
    expect(OmniSearch_po.AssetDropDownCheck().getText()).toEqual('Assets');
    // OmniSearch_po.PolicyViolationsDropDownCheck();
    expect(OmniSearch_po.PolicyViolationsDropDownCheck().getText()).toEqual('Policy Violations');
  });

 it('Verifies whether search is working on clicking search button', () => {
  browser.wait(EC.elementToBeClickable(OmniSearch_po.searchFunction() ), timeOutHigh);
  OmniSearch_po.searchFunction().click();
  OmniSearch_po.searchFunction().sendKeys('management');
  OmniSearch_po.clickButton().click();
  browser.wait(EC.visibilityOf(OmniSearch_po.searchResult() ), timeOutHigh);
  browser.wait(EC.elementToBeClickable(OmniSearch_po.searchResult() ), timeOutHigh);
  OmniSearch_po.searchResult().click();
  browser.wait(EC.visibilityOf(OmniSearch_po.Attributes() ), timeOutHigh);
  const Temp =  OmniSearch_po.accountName().getText();
  expect(Temp).toEqual('Management');
  browser.wait(EC.elementToBeClickable(AssetDetails_po.navigateToOmniSearch() ), timeOutHigh);
   AssetDetails_po.navigateToOmniSearch().click();

});


 it('Verifies whether assetlist on click goes to asset details', () => {
  browser.wait(EC.elementToBeClickable(OmniSearch_po.checkDeletedAssets() ), timeOutHigh);
  browser.wait(EC.visibilityOf(OmniSearch_po.AssetList() ), timeOutHigh);
  OmniSearch_po.goToAssetDetails().click();
  browser.wait(EC.visibilityOf(AssetDetails_po.checkHeading() ), timeOutHigh);
  expect(AssetDetails_po.checkHeading().getText()).toEqual('Asset Details');
  browser.wait(EC.elementToBeClickable(AssetDetails_po.navigateToOmniSearch() ), timeOutHigh);
  AssetDetails_po.navigateToOmniSearch().click();
 });

it('Should click on deleted assets and check the functionalities', () => {
  browser.wait(EC.elementToBeClickable(OmniSearch_po.checkDeletedAssets() ), timeOutHigh);
  OmniSearch_po.checkDeletedAssets().click();
  browser.wait(EC.elementToBeClickable(OmniSearch_po.clickSearchButton() ), timeOutHigh);
  browser.wait(EC.visibilityOf(OmniSearch_po.clickSearchButton() ), timeOutHigh);
  OmniSearch_po.clickSearchButton().click();
   const Temp = OmniSearch_po.checkAssets().getText();
   expect(Temp).toEqual('Include terminated/deleted assets');

// //   const item = OverviewCompliance_po.getPolicyViolationPercents();
// // let percent_total = 0;
// // item.then(function(items){
// // for (let i = 0; i < items.length; i++) {
// // $('.enclosure-issue .flex.flex-align-center:nth-child(' + (1 + i) + ') .total-issues-text-issue').getText().then(function (text) {
// // percent_total = percent_total + parseInt(text, 10);
// // if ( i === items.length - 1) {
// // expect(percent_total).toEqual(100);
// // }
// // });
// // }
// // });
  });
  it(' Should navigate to policyViolations details page when policy violations is active', () => {
    browser.wait(EC.visibilityOf(OmniSearch_po.DropDown() ), timeOutHigh);
    browser.wait(EC.elementToBeClickable(OmniSearch_po.DropDown() ), timeOutHigh);
    OmniSearch_po.DropDown().click();
    browser.wait(EC.visibilityOf(OmniSearch_po.PolicyViolationsDropDownCheck() ), timeOutHigh);
    browser.wait(EC.elementToBeClickable(OmniSearch_po.PolicyViolationsDropDownCheck() ), timeOutHigh);
    OmniSearch_po.PolicyViolationsDropDownCheck().click();
    browser.wait(EC.elementToBeClickable(OmniSearch_po.clickSearchButton() ), timeOutHigh);
    browser.wait(EC.visibilityOf(OmniSearch_po.clickSearchButton() ), timeOutHigh);
    OmniSearch_po.clickSearchButton().click();
    browser.wait(EC.visibilityOf(OmniSearch_po.PolicyViolationsDetails() ), timeOutHigh);
    OmniSearch_po.PolicyViolationsDetails().click();
    browser.wait(EC.visibilityOf(OmniSearch_po.checkHeadingPolicyViolations() ), timeOutHigh);
    expect(OmniSearch_po.checkHeadingPolicyViolations().getText()).toEqual('Policy Violations Details');
    browser.wait(EC.elementToBeClickable(AssetDetails_po.navigateToOmniSearch() ), timeOutHigh);
    AssetDetails_po.navigateToOmniSearch().click();
  });


// it('Should click on filter and check the functionalities', () => {
//   browser.wait(EC.visibilityOf(OmniSearch_po.AssetList() ), timeOutHigh);
//   browser.wait(EC.visibilityOf(OmniSearch_po.FilterClick()), timeOutHigh);
//   browser.wait(EC.elementToBeClickable(OmniSearch_po.FilterClick()), timeOutHigh);
//   OmniSearch_po.FilterClick().click();
//   browser.wait(EC.visibilityOf(OmniSearch_po.filterContainer()), timeOutHigh);
//   const eachFilter = OmniSearch_po.refineCount();
//   let count = 0;

//   let Countoriginal;
//    OmniSearch_po.CountOriginal().getText().then(function(text){
//       Countoriginal = parseInt(text, 10);
//    });

//   eachFilter.then(function(items) {
//   for (let i = 0 ; i < items.length; i++) {
//     $('.each-filter-desc .each-filter-options:nth-child(' + (1 + i) + ') .refine-criteria-count').getText().then(function(text){
//       count = count + parseInt(text, 10);
//       if ( i === items.length - 2) {
//         expect(count.toString()).toEqual(Countoriginal.toString());
//       }
//      });
//     }
//    });
//   });
});
