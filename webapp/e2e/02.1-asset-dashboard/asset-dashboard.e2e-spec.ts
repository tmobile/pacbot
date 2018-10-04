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
import { Login } from '../page-objects/login.po';
import { OverviewCompliance } from '../page-objects/overview.po';
import { CertificateCompliance } from '../page-objects/certificate-compliance.po';
import { AssetList } from '../page-objects/asset-list.po';
import { AssetDashboard } from '../page-objects/assetdashboard.po';
const maxTimeOut = 1800000;

describe('AssetDashboard', () => {
  let CertificateCompliance_po: CertificateCompliance;
  let assetdashboard_po: AssetDashboard;
  const EC = protractor.ExpectedConditions;
  let certificate_count;

  beforeAll(() => {
    CertificateCompliance_po = new CertificateCompliance();
    assetdashboard_po = new AssetDashboard();
  });

  it('Navigate To All Certificates', () => {
    browser.wait(EC.elementToBeClickable(assetdashboard_po.getAssetDashboardClick()), maxTimeOut);
    assetdashboard_po.getAssetDashboardClick().click();
    browser.wait(EC.visibilityOf(assetdashboard_po.getAssetDashboardHeaderText()), maxTimeOut);
    certificate_count = assetdashboard_po.getCertificates().getText();
    browser.wait(EC.elementToBeClickable(assetdashboard_po.getCertificates()), maxTimeOut);
    assetdashboard_po.getCertificates().click();
    browser.wait(EC.visibilityOf(CertificateCompliance_po.getAllCertificateHeader()), maxTimeOut);
    expect(CertificateCompliance_po.getAllCertificateHeader().getText()).toEqual('All Certificates');
    browser.wait(EC.visibilityOf(assetdashboard_po.goBack()), maxTimeOut);
    browser.wait(EC.elementToBeClickable(assetdashboard_po.goBack()), maxTimeOut);
    assetdashboard_po.goBack().click();

  });

 it('Verify Certificate Count', () => {
    browser.wait(EC.visibilityOf(assetdashboard_po.getCertificates()), maxTimeOut);
    certificate_count = assetdashboard_po.getCertificates().getText();
    browser.wait(EC.elementToBeClickable(assetdashboard_po.getCertificatesClick()), maxTimeOut);
    assetdashboard_po.getCertificatesClick().click();
    browser.wait(EC.visibilityOf(CertificateCompliance_po.getAssetTotalRows()), maxTimeOut);
    expect(CertificateCompliance_po.getAssetTotalRows().getText()).toEqual(certificate_count);
  });

});
