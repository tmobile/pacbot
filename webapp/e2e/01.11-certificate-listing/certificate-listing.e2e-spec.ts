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
import { CertificateCompliance } from '../page-objects/certificate-compliance.po';
import { PolicyViolations } from '../page-objects/policy-violations.po';
import { AssetDetails } from '../page-objects/asset-details.po';
import { PolicyKnowledgebase } from '../page-objects/policy-knowledgebase.po';
import { CertificateListing } from '../page-objects/certificate-listing.po';

import { Menu } from '../page-objects/menu.po';

const timeOutHigh = 180000;

describe('Certificate-Listing', () => {
  let login_po: Login;
  let AssetDetails_po: AssetDetails;
  let OverviewCompliance_po: OverviewCompliance;
  let menu_po: Menu;
  let taggingcompliance_po: TaggingCompliance;
  let OmniSearch_po: OmniSearch;
  let policyviolations_po: PolicyViolations;
  const EC = protractor.ExpectedConditions;
  let policyknowledgebase_po: PolicyKnowledgebase;
  let certificatelisting_po: CertificateListing;
  let CertificateCompliance_po: CertificateCompliance;
  beforeAll(() => {
    login_po = new Login();
    AssetDetails_po = new AssetDetails();
    OverviewCompliance_po = new OverviewCompliance();
    menu_po = new Menu();
    taggingcompliance_po = new TaggingCompliance();
    OmniSearch_po = new OmniSearch();
    policyviolations_po = new  PolicyViolations();
    policyknowledgebase_po = new PolicyKnowledgebase();
    CertificateCompliance_po = new CertificateCompliance();
    certificatelisting_po = new CertificateListing();
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
    //     OverviewCompliance_po.navigateToOverviewComplianceGet();
    //     login_po.clickNext().sendKeys('pacbot@t-mobile.com');
    //     login_po.submitNext().click();
    //     OverviewCompliance_po.navigateToOverviewCompliance();
    //     const page_title = OverviewCompliance_po.getPageTitle().getText();
    //     expect(page_title).toEqual('Overview');
    // });


//     /*it('authenticationlogin', () => {
//     //describe('Protractor Typescript Demo', function() {
// 	//browser.ignoreSynchronization = true; // for non-angular websites
// 	WebDriverWait wait = new WebDriverWait(driver, 3);
// Alert alert = wait.until(ExpectedConditions.alertIsPresent());
// alert.authenticateUsing(new UserAndPassword("", ""));
//     })*/

it('Navigate to Certificate Listing page and check heading', () => {
    browser.wait(EC.visibilityOf(CertificateCompliance_po.navigateToCertificateCompliance()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(CertificateCompliance_po.navigateToCertificateCompliance()), timeOutHigh);
    CertificateCompliance_po.navigateToCertificateCompliance().click();
    browser.wait(EC.visibilityOf(certificatelisting_po.navigatetolisting()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(certificatelisting_po.navigatetolisting()), timeOutHigh);
    certificatelisting_po.navigatetolisting().click();
    browser.wait(EC.visibilityOf(certificatelisting_po.CertificateHeading()), timeOutHigh);
       const text = certificatelisting_po.CertificateHeading().getText();
       expect(text).toEqual('All Certificates');

});
it('Checks the search functionality inside the page', () => {
    browser.wait(EC.elementToBeClickable(certificatelisting_po.SearchFunction()), timeOutHigh);
    certificatelisting_po.SearchFunction().click();
    certificatelisting_po.SearchFunctionInput().sendKeys('dev2');
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
    browser.wait(EC.visibilityOf(certificatelisting_po.searchResult()), timeOutHigh);
    certificatelisting_po.searchRow().click();
    browser.wait(EC.visibilityOf(certificatelisting_po.rowDetail()), timeOutHigh);
    const searchText = certificatelisting_po.heading().getText();
    expect(searchText).toContain('dev2');
    certificatelisting_po.closeresult().click();
});

it('Checks the heading of Additional Details', () => {
    browser.wait(EC.visibilityOf(certificatelisting_po.searchResult()), timeOutHigh);
    certificatelisting_po.searchRow().click();
    browser.wait(EC.visibilityOf(certificatelisting_po.rowDetail()), timeOutHigh);
        const valtext = certificatelisting_po.mainHeading().getText();
        expect(valtext).toEqual('Additional Details');
        certificatelisting_po.closeresult().click();
});

it('Verify the sort functionality', () => {
    browser.wait(EC.presenceOf( certificatelisting_po.getCertificateName()), timeOutHigh);
    browser.wait(EC.visibilityOf(certificatelisting_po.certificateSort()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(certificatelisting_po.certificateSort()), timeOutHigh);
    certificatelisting_po.certificateSort().click();
    let first_row;
    certificatelisting_po.getFirstRowCell().getText().then(function (text) {
      first_row = text.toLowerCase();
    });
    let second_row;
    certificatelisting_po.getSecondRowCell().getText().then(function (text) {
      second_row = text.toLowerCase();
      expect(first_row < second_row).toEqual(true);
    });
  });
});
