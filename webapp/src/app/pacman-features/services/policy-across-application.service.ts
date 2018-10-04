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

/**
 * Created by sauravdutta on 11/10/17.
 */
import { Observable } from 'rxjs/Rx';
import { Injectable, Inject } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../shared/services/http-response.service';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';

@Injectable()
export class PolicyAcrossApplicationService {
    constructor(
                private httpService: HttpService,
                private errorHandling: ErrorHandlingService) { }

    // function call for api 'policyDetailsByAppliication'
    getpolicyApplication(queryParams, PolicyAcrossApplicationUrl, PolicyAcrossApplicationMethod): Observable<any> {

        const url = PolicyAcrossApplicationUrl;
        const method = PolicyAcrossApplicationMethod;
        const payload = {};

        try {
            return this.httpService.getHttpResponse(url, method, payload, queryParams)
                    .map(response => {
                        this.dataCheck(response);
                        return this.massageData(response);
                    });
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    }

    dataCheck(data) {
        const APIStatus = this.errorHandling.checkAPIResponseStatus(data);
        if (!APIStatus.dataAvailble) {
            throw new Error('noDataAvailable');
        }
    }

    // massage function call for api 'policyDetailsByAppliication'
    massageData(data): any {
            /**
             * here goes the massaging the data to a desired format
             * the format is described in mockdata */

            const tablebodyData = data.response;
            const jsonObjOuter = [];
            let applicationName;
            let nonCompliant;
            tablebodyData.forEach((elementOut , indexOut) => {
              /**
               * this is the final object for table
               */

              if (elementOut.application === undefined) {
                applicationName = elementOut.environment;
                nonCompliant = elementOut['noncompliant'];
              } else {
                applicationName = elementOut.application;
                nonCompliant = elementOut['non-compliant'];
              }
              jsonObjOuter.push({
                'AppName' : applicationName,
                'Totalcount' : elementOut.total,
                'compliant' : elementOut.total,
                'non-compliant' : elementOut.total,
                'AppDetails' : [{
                  'CountType' : 'Totalcount',
                  'count' :  elementOut.total
                },
                {
                    'CountType' : 'compliant',
                    'count' :  elementOut.compliant
                },
                {
                    'CountType' : 'non-compliant',
                    'count' :  nonCompliant
                }
                ]
              });
            });
            jsonObjOuter.forEach(element => {
                element.AppDetails.forEach(details => {
                    element[details.CountType] = details.count;
                });
            });
            return jsonObjOuter;
    }

}
