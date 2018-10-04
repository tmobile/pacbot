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

import { browser, by, element, $, $$ } from 'protractor';
import { CONFIGURATIONS } from './../../src/config/configurations';

const domain = CONFIGURATIONS.optional.general.e2e.DOMAIN;

export class AssetGroups {

    navigateToChangeAssetGroup() {
        return browser.get(domain + '/pl/(compliance/compliance-dashboard//modal:change-default-asset-group)?ag=aws-all&domain=Infra%20%26%20Platforms#all');
    }

    getAssetGroup() {
        return element(by.xpath('//app-post-login-header/div[1]/div[1]/a/app-default-asset-group/a'));
    }

    getAssetGroupTitle() {
        return $('.event-container .header-asset .header-title');
    }

    getAssetGroupSearch() {
        return $('.event-container .sub-header-asset .search input.input-text');
    }

    getFirstAssetGroup() {
        return element(by.xpath('//app-asset-groups/div/div/div/article[3]/div/section[1]/div[1]/div[1]'));
    }

    clickFirstAssetGroup() {
        return element(by.xpath('//app-asset-groups/div/div/div/article[3]/div/section[1]/div[1]'));
    }

    getSetDefault() {
        return element(by.xpath('//app-asset-groups/div/div/div/article[3]/div/section[2]/a/div/app-button/button'));
    }

    getAgDetails() {
        return element(by.xpath('//app-asset-groups/div/div/div/article[3]/div/section[2]/div/app-asset-group-details/div/section/article[2]/div[1]/div[1]'));
    }

    currentAssetGroup() {
        return element(by.xpath('//app-default-asset-group/a/div/h4'));
    }

    getAllPath() {
        return element(by.xpath('//app-asset-groups/div/div/div/article[2]/div[1]/app-asset-group-tabs/div/div[1]/div'));
    }
}
