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
import { CONFIGURATIONS } from './../../src/config/configurations';

const domain = CONFIGURATIONS.optional.general.e2e.DOMAIN;

export class AssetDashboard {

  navigateToassetdashboardGet() {
    return browser.driver.get(domain + '/post-login/assets/asset-dashboard?ag=aws-all&domain=Infra%20%26%20Platforms');
  }

  getAssetDashboardClick() {
      return element(by.className('module-name'));
  }

  getAssetDashboardHeaderText() {
    return element(by.xpath('//div/div/div[2]/app-asset-dashboard/div/div/div[1]/h1'));
  }

  getMenuAssetDashboardClick() {
    return element(by.xpath('//div[1]/section/app-assets/div/div/div[1]/app-contextual-menu/div/ul/li[1]/a'));
  }

  getCertificates() {
    return element(by.className('mr-asset-value ilb underline-link-num'));
  }

  getawsApps() {
    return element(by.className('aws-app-type-container active'));
  }

  getInventory() {
    return element(by.xpath('//app-inventory-container/div/header/app-title-burger-head/div/div[1]/div'));
  }

  getawsAppsTitle() {
    return element(by.xpath('//div[1]/div[1]/ul/li[1]/a/app-aws-app-tile/div/div/div[1]'));
  }

  getawsAppscount() {
    return element(by.xpath('//div[2]/div[2]/app-aws-resource-details/div[1]/div[1]/ul/li[1]/a/app-aws-app-tile/div/div/div[2]'));
  }

  getAssetListHeader() {
    return element(by.xpath('//div/div[2]/app-filtered-selector/div/div[1]'));
  }

}
