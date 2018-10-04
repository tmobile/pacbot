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

import { by, element, $, $$ } from 'protractor';

export class Menu {

    MenuClick() {
        // return element(by.className('fa fa-bars mr-burger-menu'));
        return element(by.xpath('//app-post-login-header//div[contains(@class, \'burger-container\')]//i'));
    }

    OverviewComplianceClick() {
        return element(by.xpath('//div[1]/header/app-post-login-header/div[2]/div[1]/div[1]/div[2]/span'));
    }

    VulnerabilityClick() {
        return element(by.xpath('//div[1]/header/app-post-login-header/div[2]/div[1]/div[1]/div[3]/span'));
    }

    TaggingClick() {
        return element(by.xpath('//div[1]/header/app-post-login-header/div[2]/div[1]/div[1]/div[4]/span'));
    }

    CertificateClick() {
        return element(by.xpath('//div[1]/header/app-post-login-header/div[2]/div[1]/div[1]/div[5]/span'));
    }

    PatchingClick() {
        return element(by.xpath('//div[1]/header/app-post-login-header/div[2]/div[1]/div[1]/div[6]/span'));
    }

    PolicyViolationClick() {
        return element(by.xpath('//div[1]/header/app-post-login-header/div[2]/div[1]/div[1]/div[7]/span'));
    }

    PolicyKnowledgeBaseClick() {
        return element(by.xpath('//div[1]/header/app-post-login-header/div[2]/div[1]/div[1]/div[8]/span'));
    }

    EmailClick() {
        return element(by.xpath('//app-post-login-app/div[1]/header/app-post-login-header/div[2]/div[2]/div/div[1]/a'));
    }
    AssetDashboardClick() {
        return element(by.xpath('//app-post-login-header/div[2]/div[1]/div[2]/div[2]/span'));
    }

    AssetListClick() {
        return element(by.xpath('//app-post-login-app/div[1]/header/app-post-login-header/div[2]/div[1]/div[2]/div[3]/span'));
    }

    ToolsOverview() {
        return element(by.xpath('//div[1]/header/app-post-login-header/div[2]/div[1]/div[3]/div[2]/span'));
    }

    SlackClick() {
        return element(by.xpath('//div[1]/header/app-post-login-header/div[2]/div[2]/div/div[2]/a'));
    }

    PacmanStatisticsClick() {
        return element(by.xpath('//app-post-login-header/div[2]/div[1]/div[4]/div[2]/span'));
    }

    ComplianceSummaryClick() {
        return element(by.xpath('//app-post-login-header/div[2]/div[1]/div[4]/div[3]/span'));
    }

    VulnerabilityReportClick() {
        return element(by.xpath('//app-post-login-header/div[2]/div[1]/div[4]/div[4]/span'));
    }

    dummyclick() {
        return element(by.xpath('//abcd'));
    }

    digitalDashboardClick() {
        return $$('.menu-content-item span').get(1);
    }

}

