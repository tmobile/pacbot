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
const fs = require('fs');
export class CertificateCompliance {

  navigateToCertificateCompliance() {
    return element(by.xpath('//app-compliance/div/div/div[1]/app-contextual-menu/div/ul/li[4]/a'));
  }

  CertificateHeaderText() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[1]/div[1]/div/h1'));
  }

  getOverallcertificate() {
    return element(by.xpath('//div/div[2]/app-generic-summary/section/div/div[2]/div/div[1]'));
  }
 Overallcertificateresult() {
  return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[1]/div[2]/section/ul/li[2]/div/app-all-certificate-table/div/div/app-data-table/div/div[3]/div[1]/span[2]'));
 }
 helpbutton() {
  return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[2]/div/app-certificate-summary/div/div[1]/app-title-burger-head/div/div[2]/div/img'));

 }
 helpText() {
  return element(by.xpath('//app-root/app-post-login-app/app-help-text/div'));
}
helpTextclose() {
  return element(by.xpath('//app-post-login-app/app-help-text/div/div[1]/img'));

}
helpTextTitle() {
  return element(by.xpath('//app-post-login-app/app-help-text/div/div[2]/div[1]'));

}
  getCertificate30() {
    return element(by.xpath('//div/div[2]/app-generic-summary/section/div/div[3]/div/div[1]'));
  }

  getCertificate45() {
    return element(by.xpath('//div/div[2]/app-generic-summary/section/div/div[4]/div/div[1]'));
  }

  getAllcertificateTable() {
    return element(by.xpath('///div/div/app-data-table/div/div[3]/div[1]/span[2]'));
  }

  getAllCertificateHeader() {
    return element(by.xpath('//div/h1[text()="All Certificates"]'));
  }

  getAssetTotalRows() {
    return element(by.className('total-rows'));
  }
  searchBar() {
    return element(by.xpath('//*[@id="headSearch"]'));

  }
  searchresultrow() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[1]/div[2]/section/ul/li[2]/div/app-all-certificate-table/div/div/app-data-table/div/div[2]'));

  }
  searchresultElement() {
  return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[1]/div[2]/section/ul/li[2]/div/app-all-certificate-table/div/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[1]/div/div'));
  }
  Searchcard() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[1]/div[2]/section/ul/li[2]/div/app-all-certificate-table/div/div/app-data-table/div/div[4]/div[2]'));
  }
  resultText() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[1]/div[2]/section/ul/li[2]/div/app-all-certificate-table/div/div/app-data-table/div/div[4]/div[2]/div[1]/div[2]/div/span'));

    }
    closeresult() {
      return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[1]/div[2]/section/ul/li[2]/div/app-all-certificate-table/div/div/app-data-table/div/div[4]/div[1]/img'));

    }
    ExpiryCount() {
      return element(by.xpath('//app-certificate-compliance/div/div[2]/div/app-certificate-summary/div/div[2]/app-generic-summary/section/div/div[4]/div'));

    }
    getTotalDataTableCnt() {
      return $('.total-rows');

    }
  checkDirExists(aPath) {
    try {
      return fs.statSync(aPath).isDirectory();
    } catch (e) {
      if (e.code === 'ENOENT') {
        return false;
      } else {
        throw e;
      }
    }
  }
  getdownloadIcon() {
    return element(by.css('.contextual-menu-img img'));
  }
  getToastMsg() {
    return element(by.css('.toast-msg'));
  }
  getDownloadRunningIcon() {
    return element(by.css('.pacman-anim img'));
  }
  TotalCompliance() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[2]/div/app-certificate-summary/div/div[2]/app-generic-summary/section/div/div[2]/div'));

  }
  TotalCompliancenumber() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[2]/div/app-certificate-summary/div/div[2]/app-generic-summary/section/div/div[2]/div/div[1]'));

  }
  expirycertifcates() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[3]/section/ul'));
  }
  getexpiryNumber() {
    return element(by.xpath('//app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[3]/div[1]/span[2]'));
  }
  cloudCICD() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[2]/div/app-certificate-stage/div/ul/li[1]/app-progress-summary/section'));

  }
  getCICDnumber() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[3]/section/ul/li/div/app-data-table/div/div[3]/div[1]/span[2]'));

  }
  cloudCICDnumber() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[2]/div/app-certificate-stage/div/ul/li[1]/app-progress-summary/section/div[1]'));

  }
  certificateSort() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[1]/div[2]/section/ul/li[2]/div/app-all-certificate-table/div/div/app-data-table/div/div[2]/div[1]/div/div[1]'));
  }
  getSecondRowCell() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[1]/div[2]/section/ul/li[2]/div/app-all-certificate-table/div/div/app-data-table/div/div[2]/div[2]/div/div[2]/div[1]/div/div'));

  }
  getFirstRowCell()  {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[1]/div[2]/section/ul/li[2]/div/app-all-certificate-table/div/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[1]/div/div'));

  }
  getCertificateName() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[1]/div[2]/section/ul/li[2]/div/app-all-certificate-table/div/div/app-data-table/div/div[2]'));

  }
  navigateback() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[1]/div[1]/img'));

   }
   navigatebackCICD() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[1]/div[1]/img'));

   }
   heading() {
    return $('.details-bar-header .header-text');
   }
   headingbar() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[4]/div[1]'));

   }
   closeresultagain() {
    return element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[4]/div[1]/img'));

   }

  }
