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

import { browser, by, element , $} from 'protractor';

export class CertificateListing {
    navigatetolisting() {
       return (element(by.xpath('//app-compliance/div/div/div[2]/app-certificate-compliance/div/div[2]/div/app-certificate-summary/div/div[2]/app-generic-summary/section/div/div[2]/div')));
    }
    CertificateHeading() {
        return (element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[1]/app-title-burger-head/div/div[1]/div')));

    }
    SearchFunction() {
        return element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[1]/app-title-burger-head/div/div[2]/div[1]/label/img'));
    }
    SearchFunctionInput() {
        return element(by.xpath('//*[@id="headSearch"]'));
    }
    searchResult() {
        return (element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[2]')));
    }
    searchRow() {
        return (element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[1]/div/div')));

    }
    rowDetail() {
        return (element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[4]/div[2]')));

    }
    heading() {
        return (element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[4]/div[2]/div[1]/div[2]/div/span')));
    }
    mainHeading() {
        return $('.details-bar-header .header-text');
    }
    certificateSort() {
        return element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[2]/div[1]/div/div[1]'));

    }

    getCertificateName() {

        return element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[2]'));
      }
    getSecondRowCell() {
        return element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[2]/div[2]/div/div[2]/div[1]/div/div'));

      }
    getFirstRowCell() {
        return element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[2]/div[2]/div/div[1]/div[1]/div/div'));
      }

    closeresult() {
        return element(by.xpath('//app-compliance/div/div/div[2]/app-certificates/div/div[2]/section/ul/li/div/app-data-table/div/div[4]/div[1]/img'));
    }
}


