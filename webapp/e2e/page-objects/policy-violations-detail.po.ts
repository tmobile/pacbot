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

import { browser, by, element, $ } from 'protractor';

export class PolicyViolationsDetail {

  getPolicyViolationDetailHeading() {
    return element(by.className('heading-text'));
  }

  getBackArrow() {
    return $('.arrow-img img');
  }

  getStatus() {
    return element.all(by.css('.issue-blocks-wrapper .header')).first();
  }

  getSeverity() {
    return element.all(by.css('.issue-blocks-wrapper .header')).get(1);
  }

  getTargetType() {
    return element.all(by.css('.issue-blocks-wrapper .header')).get(2);
  }

  getRuleCategory() {
    return element.all(by.css('.issue-blocks-wrapper .header')).get(3);
  }

  getPolicyViolatedValue() {
    return element.all(by.css('.policy-violation-label-content')).first();
  }

  getResourceId() {
    return element.all(by.css('.blocks-section.blocks-section-bottom .policy-violation-label-content.anchor-link')).get(1);
  }

  getPolicyViolatedHeading() {
    return element(by.xpath('//app-policy-violation-desc/section/div[1]/div[1]'));
  }

  getViewDetailLink() {
    return element(by.css('.goto-link'));
  }

  getIssueAuditHeader() {
    return element(by.xpath('//app-data-table/div/div[1]/app-title-burger-head/div/div[1]/div'));
  }

  getFirstStatusRow() {
    return element(by.xpath('//app-data-table/div/div[2]/div[2]/div/div[1]/div[2]/div/div'));
  }

  getAdditionalDetailsHeaderText() {
    return element(by.css('.details-bar-header .header-text'));
  }

  getSearchInput() {
    return element(by.css('.header-search-input'));
  }

  getSearchLabel() {
    return element(by.css('.search-label'));
  }

  getAdditionaldetailsCrossMark() {
    return element(by.xpath('//app-data-table/div/div[4]/div[1]/img'));
  }

  getEmailButton() {
    return element(by.xpath('//app-button-icon/button/img[@src="../assets/icons/email.svg"]'));
  }

  getEmailInput() {
    return element(by.css('.inputarea-container'));
    // return element(by.model('queryValue'));
  }

  submitEmail() {
    return element(by.css('.after-click-button-opposite button'));
  }

  getEmailResponse() {
    return element(by.css('.circle-container'));
  }

  successEmailMark() {
    return $('.success_msg_email');
  }

  failureEmailText() {
    return $('.error_msg_email');
  }

  selectUsersEmailIdFromDropdown() {
    return element(by.css('li.lists-suggestion active a'));
  }

}
