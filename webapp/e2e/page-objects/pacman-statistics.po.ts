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

export class PacmanStatistics {

    getTopHeader() {
   return element(by.xpath('//header[contains(@class,\'header\')]//div[contains(@class,\'header-text\')]'));
  }
  getPoliciesEnforced() {
    return element(by.xpath('//main/article[1]/div[1]/div[contains(@class,\'events-text\')]'));
  }
  getPolicyEvaluations() {
    return element(by.xpath('//main/article[1]/div[2]/div[contains(@class,\'events-text\')]'));
  }
  getAvailableAutoFixes() {
    return element(by.xpath('//main/article[1]/div[3]/div[contains(@class,\'events-text\')]'));
  }
  getAutofixesApplied() {
    return element(by.xpath('//main/article[1]/div[4]/div[contains(@class,\'events-text\')]'));
  }
  getAwsAccountScanned() {
    return element(by.xpath('//main/article[2]/div[1]/div[contains(@class,\'events-text\')]'));
  }
  getEventsProcessed() {
    return element(by.xpath('//main/article[2]/div[2]/div[contains(@class,\'events-text\')]'));
  }
  getAssetsScanned() {
    return element(by.xpath('//main/article[2]/div[3]/div[contains(@class,\'events-text\')]'));
  }
  getTotalViolations() {
    return element(by.xpath('//main/article[2]/div[4]/div[contains(@class,\'events-text\')]'));
  }
   getBackArrow() {
    return element(by.xpath('//div[contains(@class,\'goBack\')]'));
  }
}
