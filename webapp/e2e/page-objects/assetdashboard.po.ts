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

export class AssetDashboard {

  getAssetDashboardClick() {
    return element(by.xpath('//div[1]/header/app-post-login-header/div[1]/div[2]/nav/app-nav-icon[2]/a'));
  }

  getAssetDashboardHeaderText() {
    return element(by.xpath('//app-asset-dashboard/div/div/div[1]/h1'));
  }

  getAssetDashboard() {
    return element(by.xpath('//header/app-post-login-header/div[1]/div[2]/nav/app-nav-icon[2]/a'));
  }

  getCertificatesClick() {
    return element(by.xpath('//app-asset-dashboard/div/div/div[2]/div[3]/app-asset-certificate/app-asset-type'));
  }

  getCertificates() {
    return element(by.className('mr-asset-value ilb underline-link-num'));
  }

  getAssetListPage() {
    return element(by.xpath('//app-contextual-menu/div/ul/li[2]'));
  }

  goBack() {
    return $('img.arrow-img');
  }

}
