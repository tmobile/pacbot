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
import { OverviewCompliance } from '../page-objects/overview.po';
import { TaggingCompliance } from '../page-objects/tagging-compliance.po';
import { PolicyViolations } from '../page-objects/policy-violations.po';
import { AssetDetails } from '../page-objects/asset-details.po';
import { PolicyKnowledgebase } from '../page-objects/policy-knowledgebase.po';
import { Menu } from '../page-objects/menu.po';

const timeOutHigh = 180000;

describe('PolicyKnowledgeBase', () => {
  let login_po: Login;
  let AssetDetails_po: AssetDetails;
  let OverviewCompliance_po: OverviewCompliance;
  let menu_po: Menu;
  let taggingcompliance_po: TaggingCompliance;
  let PolicyKnowledgebase_po: PolicyKnowledgebase;
  let policyviolations_po: PolicyViolations;
  const EC = protractor.ExpectedConditions;
  let policyknowledgebase_po: PolicyKnowledgebase;

  beforeAll(() => {
    login_po = new Login();
    AssetDetails_po = new AssetDetails();
    OverviewCompliance_po = new OverviewCompliance();
    menu_po = new Menu();
    PolicyKnowledgebase_po = new PolicyKnowledgebase();
    taggingcompliance_po = new TaggingCompliance();
    policyviolations_po = new  PolicyViolations();
    policyknowledgebase_po = new PolicyKnowledgebase();
    });
/*it('login', () => {
    vulnerabilityCompliance_po.navigateToVulnerabilityCompliance();
    browser.wait(EC.visibilityOf(login_po.submitLoginButton()),120000);
    login_po.getUserNameInput().sendKeys('');
    login_po.getPasswordInput().sendKeys('');
    login_po.submitLoginButton().click();
    browser.wait(EC.visibilityOf(vulnerabilityCompliance_po.getVulHeaderText()),30000);
  })
*/


    // it('SSO login', () => {
    //     // OverviewCompliance_po.navigateToOverviewComplianceGet();
    //     // login_po.clickNext().sendKeys('pacbot@t-mobile.com');
    //     // login_po.submitNext().click();
    //     OverviewCompliance_po.navigateToOverviewCompliance().click();
    //     const page_title = OverviewCompliance_po.getPageTitle().getText();
    //   expect(page_title).toEqual('Overview');
    // });


    /*it('authenticationlogin', () => {
    //describe('Protractor Typescript Demo', function() {
	//browser.ignoreSynchronization = true; // for non-angular websites
	WebDriverWait wait = new WebDriverWait(driver, 3);
Alert alert = wait.until(ExpectedConditions.alertIsPresent());
alert.authenticateUsing(new UserAndPassword("", ""));
    })*/
    it('Checks of the heading of the policy Knowledge', () => {
        browser.wait(EC.visibilityOf(policyknowledgebase_po.navigateToPolicyKnowledgdebase() ), timeOutHigh);
        policyknowledgebase_po.navigateToPolicyKnowledgdebase().click();
        browser.wait(EC.visibilityOf(policyknowledgebase_po.PolicyKnowledgdebaseheading() ), timeOutHigh);
       const temp =  policyknowledgebase_po.PolicyKnowledgdebaseheading().getText();
       expect(temp).toEqual('Policy Knowledgebase');
    });
    it('Verify search is working fine', () => {
        browser.wait(EC.presenceOf(policyknowledgebase_po.TotalPage() ), timeOutHigh);
        browser.wait(EC.visibilityOf(policyknowledgebase_po.checkSearchBar() ), timeOutHigh);
        browser.wait(EC.elementToBeClickable(policyknowledgebase_po.checkSearchBar() ), timeOutHigh);
        policyknowledgebase_po.checkSearchBar().click();
        policyknowledgebase_po.checkSearchBar().sendKeys('Tagging');

    });

    it('Verify, when clicked on each policy, it navigates to Policy Details page.', () => {
        browser.wait(EC.visibilityOf(policyknowledgebase_po.PolicyList() ), timeOutHigh);
        browser.wait(EC.elementToBeClickable(policyknowledgebase_po.policyItem() ), timeOutHigh);
        policyknowledgebase_po.policyItem().click();
        browser.wait(EC.visibilityOf(policyknowledgebase_po.checkHeading() ), timeOutHigh);
         const Temp = policyknowledgebase_po.checkHeading().getText();
         expect(Temp).toEqual('Policy Details');
         browser.wait(EC.visibilityOf(policyknowledgebase_po.navigateToPolicyKnowledgdebase() ), timeOutHigh);
         policyknowledgebase_po.navigateToPolicyKnowledgdebase().click();
        // policyknowledgebase_po.checkPolicy()
    });

    it ('Verify low etc clicked have all filtered as low', () => {
        browser.wait(EC.visibilityOf(policyknowledgebase_po.filterLow() ), timeOutHigh);
        browser.wait(EC.elementToBeClickable(policyknowledgebase_po.filterLow() ), timeOutHigh);
        policyknowledgebase_po.filterLow().click();
        browser.wait(EC.visibilityOf(policyknowledgebase_po.PolicyList() ), timeOutHigh);
        const eachCard = policyknowledgebase_po.Low();
        eachCard.then(function(items){
            for (let i = 0; i < items.length; i++) {
                        $('flex.flex-col.pk-main-content.flex.flex-wrap:nth-child(' + (1 + i) + ') pk-desc.flex.flex-align-center.flex-between.pk-btn.low-bg').getText().then(function (text) {
                            expect(text).toEqual('LOW');
            });
        }
    });


});
});
