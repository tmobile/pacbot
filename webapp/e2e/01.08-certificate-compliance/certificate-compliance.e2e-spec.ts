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
import { Login } from '../page-objects/login.po';
import { OverviewCompliance } from '../page-objects/overview.po';
import { CompliancePolicy } from '../page-objects/compliance-policy.po';
import { CertificateCompliance } from '../page-objects/certificate-compliance.po';
import { PolicyDetails } from '../page-objects/policy-details.po';
import { AssetDetails } from '../page-objects/asset-details.po';
import { AssetList } from '../page-objects/asset-list.po';
import { PolicyViolationsDetail } from '../page-objects/policy-violations-detail.po';

const timeOutHigh = 180000;

 describe('CertificateCompliance', () => {
  let login_po: Login;
  let OverviewCompliance_po: OverviewCompliance;
  let AssetDetails_po: AssetDetails;
  let AssetList_po: AssetList;
  let CompliancePolicy_po: CompliancePolicy;
  let CertificateCompliance_po: CertificateCompliance;
  let policyDetailspo: PolicyDetails;
  let violationDetails_po: PolicyViolationsDetail;
  const EC = protractor.ExpectedConditions;

  beforeAll(() => {
    login_po = new Login();
    OverviewCompliance_po = new OverviewCompliance();
    policyDetailspo = new PolicyDetails();
    CompliancePolicy_po = new CompliancePolicy();
    CertificateCompliance_po = new CertificateCompliance();
    AssetDetails_po = new AssetDetails();
    AssetList_po = new AssetList();
    violationDetails_po = new PolicyViolationsDetail();
  });

  // it('SSO login', () => {
  //   OverviewCompliance_po.navigateToOverviewComplianceGet();
  //   login_po.clickNext().sendKeys('pacbot@t-mobile.com');
  //   login_po.submitNext().click();
  //   OverviewCompliance_po.navigateToOverviewCompliance();
  //   const page_title = OverviewCompliance_po.getPageTitle().getText();
  //   expect(page_title).toEqual('Overview');
  // });

    it('Navigates to Certificate Complaince and check for Heading', () => {
      browser.wait(EC.visibilityOf(CertificateCompliance_po.navigateToCertificateCompliance()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(CertificateCompliance_po.navigateToCertificateCompliance()), timeOutHigh);
      CertificateCompliance_po.navigateToCertificateCompliance().click();
      browser.wait(EC.visibilityOf(CertificateCompliance_po.CertificateHeaderText()), timeOutHigh);
      expect(CertificateCompliance_po.CertificateHeaderText().getText()).toEqual('Certificates');

  });
  it('Verify whether the data is matching the data in the certificate summary', () => {

    browser.wait(EC.visibilityOf( CertificateCompliance_po. getOverallcertificate() ), timeOutHigh);
    let Temp1;
    let Temp2;
    CertificateCompliance_po. getOverallcertificate().getText().then(function(text){
        Temp1 = text.replace(/,/g, '');
    });
    browser.wait(EC.visibilityOf(CertificateCompliance_po.Overallcertificateresult()), timeOutHigh);
     CertificateCompliance_po.Overallcertificateresult().getText().then(function(text){
        Temp2 = text;
        expect(Temp1).toEqual(Temp2);
     });

});

it('Verify the search icon is displayed on top of the table and is functioning Properly based on a given criteria. ', () => {
  browser.wait(EC.elementToBeClickable( CertificateCompliance_po. searchBar() ), timeOutHigh);
  CertificateCompliance_po.searchBar().sendKeys('bitbucket');
  browser.actions().sendKeys(protractor.Key.ENTER).perform();
  browser.wait(EC.visibilityOf(CertificateCompliance_po.searchresultrow()), timeOutHigh);
  browser.wait(EC.elementToBeClickable(CertificateCompliance_po.searchresultElement()), timeOutHigh);
  CertificateCompliance_po.searchresultElement().click();
  browser.wait(EC.visibilityOf(CertificateCompliance_po.Searchcard()), timeOutHigh);
  const val = CertificateCompliance_po.resultText().getText();
  expect(val).toContain('bitbucket');
  const valtext = CertificateCompliance_po.heading().getText();
      expect(valtext).toContain('Additional Details');
  CertificateCompliance_po.closeresult().click();
});

it('Verify the help Text is appearing properly when you click the "?" in the summary and the text is right '
, () => {
  browser.wait(EC.elementToBeClickable( CertificateCompliance_po. helpbutton() ), timeOutHigh);
  CertificateCompliance_po. helpbutton().click();
  browser.wait(EC.visibilityOf( CertificateCompliance_po. helpText() ), timeOutHigh);
  expect( CertificateCompliance_po.helpTextTitle().getText()).toEqual('Help');
  CertificateCompliance_po.helpTextclose().click();

});
it('verify csv download', () => {
  let download_successful = false;
  browser.wait(EC.presenceOf( CertificateCompliance_po.getTotalDataTableCnt()), timeOutHigh);
  const filename = process.cwd() + '/e2e/downloads/All Certificates.csv';
  const fs = require('fs');
  const myDir = process.cwd() + '/e2e/downloads';
  if (!OverviewCompliance_po.checkDirExists(myDir)) {
    fs.mkdirSync(myDir);
  } else if ((fs.readdirSync(myDir).length) > 0 && fs.existsSync(filename)) {
    fs.unlinkSync(filename);
  }
  browser.wait(EC.visibilityOf(CertificateCompliance_po.getdownloadIcon()), timeOutHigh);
  OverviewCompliance_po.getdownloadIcon().click();
  browser.wait(EC.visibilityOf(CertificateCompliance_po.getToastMsg()), timeOutHigh).then(function() {
    browser.wait(EC.invisibilityOf(CertificateCompliance_po.getDownloadRunningIcon()), 600000).then(function() {
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
it('Verify the certificate Summary should navigate to All Certificates and check the count', () => {
  let value1;
  let value2;
  browser.wait(EC.visibilityOf( CertificateCompliance_po.TotalCompliance()), timeOutHigh);
  browser.wait(EC.elementToBeClickable( CertificateCompliance_po.TotalCompliance() ), timeOutHigh);
  CertificateCompliance_po.TotalCompliancenumber().getText().then(function(text){
     value1 = text;

  });
  CertificateCompliance_po.TotalCompliance().click();
  browser.wait(EC.visibilityOf( CertificateCompliance_po.getexpiryNumber()), timeOutHigh);
  CertificateCompliance_po.getexpiryNumber().getText().then(function(textvalue){
  value2 = textvalue;

    });
    expect(value1).toEqual(value2);
    browser.wait(EC.visibilityOf(CertificateCompliance_po.navigateback()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(CertificateCompliance_po.navigateback()), timeOutHigh);
    CertificateCompliance_po.navigateback().click();

});
it('Verify the cloud CICD should navigate to All Certificates and check the count', () => {
  let val1;
  let val2;
  browser.wait(EC.visibilityOf( CertificateCompliance_po.cloudCICD()), timeOutHigh);
  browser.wait(EC.elementToBeClickable( CertificateCompliance_po.cloudCICD() ), timeOutHigh);
  CertificateCompliance_po.cloudCICDnumber().getText().then(function(textval){
     val1 = textval;
  });
  CertificateCompliance_po.cloudCICDnumber().click();
  browser.wait(EC.visibilityOf( CertificateCompliance_po.getCICDnumber()), timeOutHigh);
    CertificateCompliance_po.getCICDnumber().getText().then(function(textval2){
     val2 = textval2;
     console.log(val2);
    });
    expect(val1).toEqual(val2);
    browser.wait(EC.visibilityOf(CertificateCompliance_po.navigatebackCICD()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(CertificateCompliance_po.navigatebackCICD()), timeOutHigh);
    CertificateCompliance_po.navigatebackCICD().click();
});
it('Check table sort functionality', () => {
  browser.wait(EC.presenceOf( CertificateCompliance_po.getCertificateName()), timeOutHigh);
  CertificateCompliance_po.certificateSort().click();
  let first_row;
  CertificateCompliance_po.getFirstRowCell().getText().then(function (text) {
    first_row = text.toLowerCase();
  });
  let second_row;
  CertificateCompliance_po.getSecondRowCell().getText().then(function (text) {
    second_row = text.toLowerCase();
    expect(first_row < second_row).toEqual(true);
  });
});

});
