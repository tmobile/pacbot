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

import { browser, element, by, protractor, $} from 'protractor';
import { Login } from '../page-objects/login.po';
import { OverviewCompliance } from '../page-objects/overview.po';
import { TaggingCompliance } from '../page-objects/tagging-compliance.po';
import { PolicyViolations } from '../page-objects/policy-violations.po';
import { VulnerabilityCompliance } from '../page-objects/vulnerability-compliance.po';
import { PatchingCompliance } from '../page-objects/patching-compliance.po';
import { CertificateCompliance } from '../page-objects/certificate-compliance.po';
import { CompliancePolicy } from '../page-objects/compliance-policy.po';
import { CONFIGURATIONS } from '../../src/config/configurations';

const timeOutHigh = 180000;
const emailId = CONFIGURATIONS.optional.general.e2e.EMAIL_ID;

describe('Overview', () => {
  let login_po: Login;
  let OverviewCompliance_po: OverviewCompliance;
  let taggingcompliance_po: TaggingCompliance;
  let policyviolations_po: PolicyViolations;
  let vulnerabilitycompliance_po: VulnerabilityCompliance;
  let patchingcompliance_po: PatchingCompliance;
  let certificatecompliance_po: CertificateCompliance;
  let CompliancePolicy_po: CompliancePolicy;
  const EC = protractor.ExpectedConditions;
  let tagging_count;
  let vuln_count;
  let patch_count;
  let cert_count;
  let violation_count;
  let critical_violation_count;
  let Temp;

  beforeAll(() => {
    login_po = new Login();
    OverviewCompliance_po = new OverviewCompliance();
    taggingcompliance_po = new TaggingCompliance();
    policyviolations_po = new  PolicyViolations();
    vulnerabilitycompliance_po = new VulnerabilityCompliance();
    certificatecompliance_po = new CertificateCompliance();
    patchingcompliance_po = new PatchingCompliance();
    CompliancePolicy_po = new CompliancePolicy();
    });

  it('Verify Tagging count in Overall Compliance', () => {
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getTaggingClick()), timeOutHigh);
    tagging_count = OverviewCompliance_po.getTaggingClick().getText();
    element(by.xpath('//app-compliance/div/div/div[1]/app-contextual-menu/div/ul/li[3]')).click();
    browser.wait(EC.visibilityOf(taggingcompliance_po.getOverallunTagging()), timeOutHigh);
    Temp = taggingcompliance_po.getOverallunTagging().getText().then(function (text) {
      text = text.replace(/,/g, '');
      expect(text).toEqual(tagging_count);
      browser.wait(EC.visibilityOf(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
      OverviewCompliance_po.navigateToOverviewCompliance().click();
    });
  });

  it('Verify Vulnerability count in Overall Compliance', () => {
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getVulnerabilitiesClick()), timeOutHigh);
    vuln_count = OverviewCompliance_po.getVulnerabilitiesClick().getText();
    element(by.xpath('//app-compliance/div/div/div[1]/app-contextual-menu/div/ul/li[2]')).click();
    browser.wait(EC.visibilityOf(vulnerabilitycompliance_po.getOverallVulnerabilities()), timeOutHigh);
    Temp = vulnerabilitycompliance_po.getOverallVulnerabilities().getText().then(function (text) {
      text = text.replace(/,/g, '');
      expect(text).toEqual(vuln_count);
      browser.wait(EC.visibilityOf(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
      OverviewCompliance_po.navigateToOverviewCompliance().click();
    });
  });

  it('Verify Certificate count in Overall Compliance', () => {
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getCertificateTotalClick()), timeOutHigh);
    OverviewCompliance_po.getCertificateTotalClick().getText().then(function (text) {
      text = text.replace(/[a-zA-Z ]/g, '');
      cert_count = text;
    });
    const elm = OverviewCompliance_po.getCertificateTotalClick();
    browser.executeScript('arguments[0].scrollIntoView();', elm.getWebElement());
    browser.actions().mouseMove(OverviewCompliance_po.getCertificateTotalClick()).perform();
    element(by.xpath('//app-compliance/div/div/div[1]/app-contextual-menu/div/ul/li[4]')).click();
    browser.wait(EC.visibilityOf(certificatecompliance_po.getOverallcertificate()), timeOutHigh);
    Temp = certificatecompliance_po.getOverallcertificate().getText().then(function (text) {
      text = text.replace(/,/g, '');
      expect(cert_count).toContain(text);
      browser.wait(EC.visibilityOf(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
      OverviewCompliance_po.navigateToOverviewCompliance().click();
    });
  });

  it('Verify Patching count in Overall Compliance', () => {
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getPatchingClick()), timeOutHigh);
    patch_count = OverviewCompliance_po.getPatchingClick().getText();
    element(by.xpath('//app-compliance/div/div/div[1]/app-contextual-menu/div/ul/li[5]')).click();
    browser.wait(EC.visibilityOf(patchingcompliance_po.getUnPatching()), timeOutHigh);
    Temp = patchingcompliance_po.getUnPatching().getText().then(function (text) {
      text = text.replace(/,/g, '');
      expect(text).toEqual(patch_count);
    });
    browser.wait(EC.visibilityOf(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    OverviewCompliance_po.navigateToOverviewCompliance().click();
  });

  it('Verify Total Violations count in Overall Compliance', () => {
    browser.wait(EC.presenceOf(OverviewCompliance_po.getTotalViolations()), timeOutHigh);
    violation_count = OverviewCompliance_po.getTotalViolations().getText();
    browser.actions().mouseMove(OverviewCompliance_po.getTotalViolations()).perform();
    browser.actions().click(protractor.Button.LEFT).perform();
    browser.wait(EC.visibilityOf(policyviolations_po.getpolicyviolationscount()), timeOutHigh);
    Temp = policyviolations_po.getpolicyviolationscount().getText();
    expect(Temp).toEqual(violation_count);
    browser.wait(EC.visibilityOf(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
      browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
      OverviewCompliance_po.navigateToOverviewCompliance().click();
  });

  it('Verify Critical Violations count in Overall Compliance', () => {
    browser.wait(EC.presenceOf(OverviewCompliance_po.getTotalCriticalViolations()), timeOutHigh);
    critical_violation_count = OverviewCompliance_po.getTotalCriticalViolations().getText();
    browser.actions().mouseMove(OverviewCompliance_po.getTotalCriticalViolations()).perform();
    browser.actions().click(protractor.Button.LEFT).perform();
    browser.wait(EC.visibilityOf(policyviolations_po.getpolicyviolationscount()), timeOutHigh);
    Temp = policyviolations_po.getpolicyviolationscount().getText();
    expect(Temp).toEqual(critical_violation_count);
    browser.wait(EC.visibilityOf(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    OverviewCompliance_po.navigateToOverviewCompliance().click();
  });

  it('Verify length of table rows in Overall Compliance equal to total length', () => {
    browser.wait(EC.presenceOf( OverviewCompliance_po.getTotalDataTableCnt()), timeOutHigh);
    const total_count = OverviewCompliance_po.getTotalDataTableCnt().getText();
    const total_rows = OverviewCompliance_po.getNumberOfRows().count();
    expect(total_count).toContain(total_rows);
  });

  it('Verify table tabs functionality', () => {
    browser.wait(EC.presenceOf( OverviewCompliance_po.getTotalDataTableCnt()), timeOutHigh);
    OverviewCompliance_po.getSecondTab().click();
    const total_count = OverviewCompliance_po.getTotalDataTableCnt().getText();
    const total_rows = OverviewCompliance_po.getNumberOfRows().count();
    expect(total_count).toContain(total_rows);
    OverviewCompliance_po.getFirstTab().click();
  });

  it('Verify search functionality', () => {
    browser.wait(EC.presenceOf( OverviewCompliance_po.getPolicyName()), timeOutHigh);
    OverviewCompliance_po.getSearchLabel().click();
    browser.sleep(401);
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getSearchInput()), timeOutHigh);
    OverviewCompliance_po.getSearchInput().sendKeys('amazon');
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
    browser.wait(EC.presenceOf( OverviewCompliance_po.getPolicyName()), timeOutHigh);
    OverviewCompliance_po.getFirstRowCell().getText().then(function (text) {
      expect(text.toLowerCase()).toContain('amazon');
    });
    browser.sleep(401);
    OverviewCompliance_po.getSearchInput().sendKeys('');
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
    $('label.search-label img').click();
  });

  it('Redirect to Compliance details on click of policy name', () => {
    browser.wait(EC.presenceOf( OverviewCompliance_po.getPolicyName()), timeOutHigh);
    OverviewCompliance_po.getPolicyName().click();
    const compliance_policy_title = CompliancePolicy_po.getTitle().getText();
    expect(compliance_policy_title).toEqual('Policy Compliance');
    browser.wait(EC.visibilityOf(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    OverviewCompliance_po.navigateToOverviewCompliance().click();
  });

  it('Verify help text modal is opening', () => {
    browser.wait(EC.visibilityOf( OverviewCompliance_po.openHelp()), timeOutHigh);
    browser.wait(EC.elementToBeClickable( OverviewCompliance_po.openHelp()), timeOutHigh);
    OverviewCompliance_po.openHelp().click();
    browser.wait(EC.visibilityOf( OverviewCompliance_po.getHelpTitle()), timeOutHigh);
    const help_title = OverviewCompliance_po.getHelpTitle().getText();
    expect(help_title).toEqual('Help');
    $('.help-text-modal .close-popup').click();
  });

  it('Check policy violations percentages sum to 100', () => {
    const each_violation = OverviewCompliance_po.getPolicyViolationPercents();
    let percent_total = 0;
    each_violation.then(function(items){
      for (let i = 0; i < items.length; i++) {
        $('.enclosure-issue .flex.flex-align-center:nth-child(' + (1 + i) + ') .total-issues-text-issue').getText().then(function (text) {
          percent_total = percent_total + parseInt(text, 10);
          if ( i === items.length - 1) {
            expect(percent_total).toEqual(100);
          }
        });
      }
    });
  });

  it('Check table sort functionality', () => {
    browser.wait(EC.presenceOf( OverviewCompliance_po.getPolicyName()), timeOutHigh);
    OverviewCompliance_po.policyTitleSort().click();
    let first_row;
    OverviewCompliance_po.getFirstRowCell().getText().then(function (text) {
      first_row = text.toLowerCase();
    });
    let second_row;
    OverviewCompliance_po.getSecondRowCell().getText().then(function (text) {
      second_row = text.toLowerCase();
      expect(first_row < second_row).toEqual(true);
    });
  });

  it('Check table row compliance matches policy compliance summary', () => {
    browser.wait(EC.presenceOf( OverviewCompliance_po.getPolicyName()), timeOutHigh);
    let table_policy_compliance;
    OverviewCompliance_po.getPolicyCompliancePercent().getText().then(function (text) {
      table_policy_compliance = text;
    });
    OverviewCompliance_po.getPolicyName().click();
    browser.wait(EC.presenceOf( CompliancePolicy_po.getPolicyCompliancePercent()), timeOutHigh);
    const policy_percent = CompliancePolicy_po.getPolicyCompliancePercent().getText().then(function (text) {
      text = text.replace(/ /g, '');
      expect(text).toEqual(table_policy_compliance);
    });
    browser.wait(EC.visibilityOf(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    OverviewCompliance_po.navigateToOverviewCompliance().click();
  });

  it('Verify table tabs All total is sum of other tabs total policies', () => {
    browser.wait(EC.presenceOf( OverviewCompliance_po.getTotalDataTableCnt()), timeOutHigh);
    let total_count;
    OverviewCompliance_po.getTotalDataTableCnt().getText().then(function (text) {
      total_count = text.replace(/[a-zA-z ]/g, '');
    });
    OverviewCompliance_po.getAllTabs().then(function(items) {
      let total_rows = 0;
      for (let i = 2; i < items.length; i++) {
        $('.tabs-header .individual-tag-header:nth-child(' + (1 + i) + ')').click();
        OverviewCompliance_po.getTotalDataTableCnt().getText().then(function (text) {
          total_rows = total_rows + parseInt(text.replace(/[a-zA-z ]/g, ''), 10);
          if ( i === items.length - 1) {
            expect(total_count).toEqual(total_rows.toString());
          }
        });
      }
    });
    OverviewCompliance_po.getFirstTab().click();
  });

  it('Verify policy violation % click is redirecting to violations page', () => {
    browser.wait(EC.presenceOf( OverviewCompliance_po.getViolationFirstPercent()), timeOutHigh);
    OverviewCompliance_po.getViolationFirstPercent().click();
    browser.wait(EC.presenceOf( policyviolations_po.getPolicyViolationsHeaderText()), timeOutHigh);
    const policy_violation_title = policyviolations_po.getPolicyViolationsHeaderText().getText();
    expect(policy_violation_title).toEqual('Policy Violations');
    browser.wait(EC.visibilityOf(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(OverviewCompliance_po.navigateToOverviewCompliance()), timeOutHigh);
    OverviewCompliance_po.navigateToOverviewCompliance().click();
  });

  it('Check if percentage lies in range 0-100', () => {
    browser.wait(EC.visibilityOf( OverviewCompliance_po.getOverallPercent()), timeOutHigh);
    OverviewCompliance_po.getOverallPercent().getText().then(function (text) {
      let checkPercentRange = false;
      if (parseInt(text, 10) >= 0 && parseInt(text, 10) <= 100) {
        checkPercentRange = true;
      }
      expect(checkPercentRange).toEqual(true);
    });
  });

  it('verify csv download', () => {
    let download_successful = false;
    browser.wait(EC.presenceOf( OverviewCompliance_po.getTotalDataTableCnt()), timeOutHigh);
    const filename = process.cwd() + '/e2e/downloads/Policy Compliance Overview.csv';
    const fs = require('fs');
    const myDir = process.cwd() + '/e2e/downloads';
    if (!OverviewCompliance_po.checkDirExists(myDir)) {
      fs.mkdirSync(myDir);
    } else if ((fs.readdirSync(myDir).length) > 0 && fs.existsSync(filename)) {
      fs.unlinkSync(filename);
    }
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getdownloadIcon()), timeOutHigh);
    OverviewCompliance_po.getdownloadIcon().click();
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getToastMsg()), timeOutHigh).then(function() {
      browser.wait(EC.invisibilityOf(OverviewCompliance_po.getDownloadRunningIcon()), 600000).then(function() {
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

});
