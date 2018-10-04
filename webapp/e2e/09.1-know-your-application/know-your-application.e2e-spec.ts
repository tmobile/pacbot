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

import { browser, protractor } from 'protractor';
import { OverviewCompliance } from '../page-objects/overview.po';
import { CONFIGURATIONS } from './../../src/config/configurations';

const domain = CONFIGURATIONS.optional.general.e2e.DOMAIN;

const timeOutHigh = 180000;

describe('KnowYourApplication', () => {
  let OverviewCompliance_po: OverviewCompliance;
  const EC = protractor.ExpectedConditions;

  beforeAll(() => {
    OverviewCompliance_po = new OverviewCompliance();
  });

  it('Verify know your application is working', () => {
    browser.get(domain + '/pl/(compliance/compliance-dashboard//kydModal:know-your-dashboard)?ag=aws-all&domain=Infra%20%26%20Platforms');
    browser.wait(EC.visibilityOf(OverviewCompliance_po.getKnowYourApplHeader()), timeOutHigh);
    expect(OverviewCompliance_po.getKnowYourApplHeader().getText()).toEqual('Get to Know Your Application');
    browser.wait(EC.visibilityOf(OverviewCompliance_po.closeKnowYourAppl()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(OverviewCompliance_po.closeKnowYourAppl()), timeOutHigh);
    OverviewCompliance_po.closeKnowYourAppl().click();
  });

});
