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

import { browser, protractor} from 'protractor';
import { Login } from '../page-objects/login.po';
import { OverviewCompliance } from '../page-objects/overview.po';
import { PolicyViolations } from '../page-objects/policy-violations.po';
import {PolicyViolationsDetail} from '../page-objects/policy-violations-detail.po';
import {PolicyDetails} from '../page-objects/policy-details.po';
import {AssetDetails} from '../page-objects/asset-details.po';
import { Menu } from '../page-objects/menu.po';
import { CONFIGURATIONS } from '../../src/config/configurations';

describe('PolicyViolationDetails', () => {
  let login_po: Login;
  let OverviewCompliance_po: OverviewCompliance;
  let menu_po: Menu;
  let policyviolations_po: PolicyViolations;
  let policyviolationsdetail_po: PolicyViolationsDetail;
  let policydetails_po: PolicyDetails;
  let assetdetails_po: AssetDetails;
  const EC = protractor.ExpectedConditions;
  const maxTimeOut = 1800000;
  const emailId = CONFIGURATIONS.optional.general.e2e.EMAIL_ID;

  beforeAll(() => {
    login_po = new Login();
    OverviewCompliance_po = new OverviewCompliance();
    menu_po = new Menu();
    policyviolations_po = new  PolicyViolations();
    policyviolationsdetail_po = new PolicyViolationsDetail();
    policydetails_po = new PolicyDetails();
    assetdetails_po = new AssetDetails();
    });

  it('navigate to Policy Violations page', () => {
    browser.wait(EC.visibilityOf(menu_po.MenuClick()), maxTimeOut);
    menu_po.MenuClick().click();
    browser.wait(EC.visibilityOf(menu_po.PolicyViolationClick()), maxTimeOut);
    menu_po.PolicyViolationClick().click();
    browser.wait(EC.visibilityOf(policyviolations_po.getPolicyViolationsHeaderText()), maxTimeOut);
    const violation_path = policyviolations_po.getPolicyViolationsHeaderText().getText();
    expect(violation_path).toEqual('Policy Violations');
  });

    it('verify navigation to policy violations detail page', () => {
      browser.wait(EC.visibilityOf(policyviolations_po.getIssueIdFirstRowValue()), maxTimeOut);
      policyviolations_po.getIssueIdFirstRowValue().click();
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getPolicyViolationDetailHeading()), maxTimeOut);
      const policy_detail_path = policyviolationsdetail_po.getPolicyViolationDetailHeading().getText();
      expect(policy_detail_path).toEqual('Policy Violations Details');
    });

    it('verify status, severity, target type, rule category headers', () => {
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getStatus()), maxTimeOut);
      const status_text = policyviolationsdetail_po.getStatus().getText();
      expect(status_text).toEqual('Status');
      const severity_text = policyviolationsdetail_po.getSeverity().getText();
      expect(severity_text).toEqual('Severity');
      const target_type_text = policyviolationsdetail_po.getTargetType().getText();
      expect(target_type_text).toEqual('Target Type');
      const rule_category_text = policyviolationsdetail_po.getRuleCategory().getText();
      expect(rule_category_text).toEqual('Rule Category');
    });

    it('verify heading for policy violated', () => {
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getPolicyViolatedHeading()), maxTimeOut);
      const policy_violated_text = policyviolationsdetail_po.getPolicyViolatedHeading().getText();
      expect(policy_violated_text).toEqual('Policy Violated');
    });

    it('verify navigation to policy details page from violated policy content', () => {
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getPolicyViolatedValue()), maxTimeOut);
      policyviolationsdetail_po.getPolicyViolatedValue().click();
      browser.wait(EC.visibilityOf(policydetails_po.getPolicyDetailsHeading()), maxTimeOut);
      const policy_detail_path = policydetails_po.getPolicyDetailsHeading().getText();
      expect(policy_detail_path).toEqual('Policy Details');
      browser.wait(EC.elementToBeClickable(policydetails_po.getBackArrow()), maxTimeOut);
      policydetails_po.getBackArrow().click();
    });

    it('verify navigation to asset details page from resource id', () => {
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getResourceId()), maxTimeOut);
      browser.wait(EC.elementToBeClickable(policyviolationsdetail_po.getResourceId()), maxTimeOut);
      policyviolationsdetail_po.getResourceId().click();
      browser.wait(EC.visibilityOf(assetdetails_po.getAssetHeaderText()), maxTimeOut);
      const asset_detail_path = assetdetails_po.getAssetHeaderText().getText();
      expect(asset_detail_path).toEqual('Asset Details');
      browser.wait(EC.elementToBeClickable(assetdetails_po.getBackArrow()), maxTimeOut);
      assetdetails_po.getBackArrow().click();
    });

    it('verify navigation to policy details page to view more details', () => {
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getViewDetailLink()), maxTimeOut);
      browser.wait(EC.elementToBeClickable(policyviolationsdetail_po.getViewDetailLink()), maxTimeOut);
      policyviolationsdetail_po.getViewDetailLink().click();
      browser.wait(EC.visibilityOf(policydetails_po.getPolicyDetailsHeading()), maxTimeOut);
      const policy_detail_path = policydetails_po.getPolicyDetailsHeading().getText();
      expect(policy_detail_path).toEqual('Policy Details');
      browser.wait(EC.elementToBeClickable(policydetails_po.getBackArrow()), maxTimeOut);
      policydetails_po.getBackArrow().click();
    });

    it('verify Issue Audit log table is displayed with additional details', () => {
      browser.actions().mouseMove(policyviolationsdetail_po.getIssueAuditHeader()).perform();
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getIssueAuditHeader()), maxTimeOut);
      const issue_audit_header = policyviolationsdetail_po.getIssueAuditHeader().getText();
      expect(issue_audit_header).toEqual('Issue Audit Log');
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getFirstStatusRow()), maxTimeOut);
      browser.wait(EC.elementToBeClickable(policyviolationsdetail_po.getFirstStatusRow()), maxTimeOut);
      policyviolationsdetail_po.getFirstStatusRow().click();
      const additional_detail_path = policyviolationsdetail_po.getAdditionalDetailsHeaderText().getText();
      expect(additional_detail_path).toEqual('Additional Details');
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getAdditionaldetailsCrossMark()), maxTimeOut);
      browser.wait(EC.elementToBeClickable(policyviolationsdetail_po.getAdditionaldetailsCrossMark()), maxTimeOut);
      policyviolationsdetail_po.getAdditionaldetailsCrossMark().click();
    });

    it('verify Issue Audit log table search', () => {
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getSearchLabel()), maxTimeOut);
      browser.wait(EC.elementToBeClickable(policyviolationsdetail_po.getSearchLabel()), maxTimeOut);
      policyviolationsdetail_po.getSearchLabel().click();
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getSearchInput()), maxTimeOut);
      policyviolationsdetail_po.getSearchInput().sendKeys('aws');
      browser.actions().sendKeys(protractor.Key.ENTER).perform();
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getFirstStatusRow()), maxTimeOut);
      const status_path = policyviolationsdetail_po.getFirstStatusRow().getText();
      expect(status_path).toEqual('aws');
    });

    it('verify email sent', () => {
      browser.wait(EC.elementToBeClickable(policyviolationsdetail_po.getEmailButton()), maxTimeOut);
      policyviolationsdetail_po.getEmailButton().click();
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getEmailInput()), maxTimeOut);
      policyviolationsdetail_po.getEmailInput().sendKeys(emailId);
      browser.driver.actions().sendKeys(protractor.Key.DOWN).perform();
      browser.wait(EC.elementToBeClickable(policyviolationsdetail_po.submitEmail()), maxTimeOut);
      policyviolationsdetail_po.submitEmail().click();
      browser.wait(EC.visibilityOf(policyviolationsdetail_po.getEmailResponse()), maxTimeOut);
      browser.wait(EC.presenceOf(policyviolationsdetail_po.successEmailMark()), 1000).then (function (response) {
        expect(policyviolationsdetail_po.successEmailMark().getText()).toBe('Email sent successfully');
      });
    });

  });
