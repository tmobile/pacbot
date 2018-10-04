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
import { CONFIGURATIONS } from '../../src/config/configurations';

const timeOutHigh = 180000;
const config = CONFIGURATIONS.optional.general.e2e;

describe('login', () => {
  let login_po: Login;
  let OverviewCompliance_po: OverviewCompliance;
  const EC = protractor.ExpectedConditions;

  beforeAll(() => {
    login_po = new Login();
    OverviewCompliance_po = new OverviewCompliance();
  });

  it('SSO login', () => {
    OverviewCompliance_po.navigateToOverviewComplianceGet();
    login_po.clickNext().sendKeys(config.EMAIL_ID);
    login_po.submitNext().click();
    OverviewCompliance_po.navigateToOverviewCompliance();
    const page_title = OverviewCompliance_po.getPageTitle().getText();
    expect(page_title).toEqual('Overview');
  });

  // prefix 'x' to 'it{}' will skip the test.
  xit('login', () => {
    OverviewCompliance_po.navigateToOverviewComplianceGet();
    browser.wait(EC.visibilityOf(login_po.submitLoginButton()), timeOutHigh);
    login_po.getUserNameInput().sendKeys(config.NT_ID);
    login_po.getPasswordInput().sendKeys(config.NT_PASSWORD);
    login_po.submitLoginButton().click();
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getPageTitle()), timeOutHigh);
    const page_title = OverviewCompliance_po.getPageTitle().getText();
    expect(page_title).toEqual('Overview');
  });

});
