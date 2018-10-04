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

import { browser, by, element, $$ } from 'protractor';

export class PolicyKnowledgebase {

  getAssetTotalRows() {
    return element(by.className('total-rows'));
  }

  getPolicyKnowledgebaseHeaderText() {
    return element(by.xpath('//div/h1[text()="Policy Knowledgebase"]'));
  }
  TotalPage() {
    return element(by.xpath('//app-root/app-post-login-app/div[1]/section/app-compliance/div/div/div[2]/app-policy-knowledgebase/div'));
  }
  navigateToPolicyKnowledgdebase() {
    return element(by.xpath('//app-root/app-post-login-app/div[1]/section/app-compliance/div/div/div[1]/app-contextual-menu/div/ul/li[7]/a'));
  }
  PolicyKnowledgdebaseheading() {
    return element(by.xpath('//app-root/app-post-login-app/div[1]/section/app-compliance/div/div/div[2]/app-policy-knowledgebase/div/div[1]/div/div/h1'));
  }
  checkSearchBar() {
    return element(by.xpath('//app-root/app-post-login-app/div[1]/section/app-compliance/div/div/div[2]/app-policy-knowledgebase/div/div[2]/div[2]/div[1]/div[1]/input'));
  }
  PolicyList() {
    return element(by.xpath('//app-root/app-post-login-app/div[1]/section/app-compliance/div/div/div[2]/app-policy-knowledgebase/div/div[2]/div[3]'));
  }
  policyItem() {
    return element(by.xpath('//app-root/app-post-login-app/div[1]/section/app-compliance/div/div/div[2]/app-policy-knowledgebase/div/div[2]/div[3]/div[1]/div[1]'));
  }
  checkHeading() {
    return element(by.xpath('//app-root/app-post-login-app/div[1]/section/app-compliance/div/div/div[2]/app-policy-knowledgebase-details/div/div[1]/div/div/h1'));
  }
  filterLow() {
    return element(by.xpath('//app-root/app-post-login-app/div[1]/section/app-compliance/div/div/div[2]/app-policy-knowledgebase/div/div[2]/div[1]/div[3]/div[9]'));
  }
  SearchList() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-policy-knowledgebase/div/div[2]/div[3]/div[1]/div[1]'));

  }
  Low() {
    return $$('.pk-main-content .each-card');
  }
 CountValueText() {
  return element(by.xpath('//app-compliance/div/div/div[2]/app-policy-knowledgebase/div/div[2]/div[2]/div[2]'));

 }
 CountValue() {
  return element(by.xpath('//app-compliance/div/div/div[2]/app-policy-knowledgebase/div/div[2]/div[2]/div[2]/span'));

 }
}
