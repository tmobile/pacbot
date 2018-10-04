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

import { browser, by, element } from 'protractor';
import { CONFIGURATIONS } from './../../src/config/configurations';

const domain = CONFIGURATIONS.optional.general.e2e.DOMAIN;

export class Login {

  navigateTo() {
    return browser.get(domain);
  }

  getLoginButton() {
    return element(by.xpath('//div[1]/app-button/button'));
  }

  getUserNameInput() {
      return element(by.xpath('//app-form-input[1]/div/input'));
  }

  getPasswordInput() {
    return element(by.xpath('//app-form-input[2]/div/input'));
  }

  submitLoginButton() {
    return element(by.xpath('//app-button/button'));
  }
  submitUsername() {
    return element(by.id('i0116'));
  }
  typeUserText() {
    return element(by.xpath('//input[@name="loginfmt"]'));
  }
  clickNext() {
    return element(by.xpath('//input[@type="email"]'));
  }

  submitNext() {
     return element(by.id('idSIButton9'));
  }
}
