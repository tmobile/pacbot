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
 * Created by Mohammed_Furqan on 10/10/17.
 */
import { Observable } from 'rxjs/Rx';
import { Injectable, Inject } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../shared/services/http-response.service';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';



@Injectable()
export class ComplianceOverviewService {

    constructor (
                 @Inject(HttpService) private httpService: HttpService,
                 private errorHandling: ErrorHandlingService) { }


    private combinedData: any = [];

    getDailyData(url, method, payload, queryParameters = {}): Observable<any> {

        try {

            return this.httpService.getHttpResponse(url, method, payload, queryParameters)
                        .map(response => this.massageDailyResponse(response['data'].response));
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    }

    getWeeklyData(url, method, queryParameters): Observable<any> {

        let payload = {};
        const queryParams = {};

        try {
            payload = {
                'ag': queryParameters.ag,
                'from': queryParameters.from,
                'filters': {}
            };

            return this.httpService.getHttpResponse(url, method, payload, queryParams)
                        .map(response => this.massageWeeklyResponse(response['data'].response));
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

    massageDailyResponse(data) {

        // datacheck function is added by Trinanjan on 10.03.2018 to check for empty data
        this.dataCheck(data);

        // *************************************************************** //
        const finalData = [];
        const apiResponse = data['compliance_info'];
        if (apiResponse.length) {
            const types = Object.keys(apiResponse[0]);
            types.splice(types.indexOf('date'), 1);
            types.forEach(type => {
                const values = [];
                let formattedObject = {};
                apiResponse.forEach(details => {
                    const obj = {
                        'date' : new Date(details['date']),
                        'value': details[type],
                        'zero-value': details[type] === 0 ? true : false
                    };
                    values.push(obj);
                });
                formattedObject = {
                    'key'    : type,
                    'values' : values
                };
                if (type.toLowerCase() !== 'overall' && type.toLowerCase() !== 'total') {
                    finalData.unshift(formattedObject);
                } else {
                    finalData.push(formattedObject);
                }
            });
        }

        return finalData;

    }

    massageWeeklyResponse (data) {
        // datacheck function is added by Trinanjan on 10.03.2018 to check for empty data
        this.dataCheck(data);
        // *************************************************************** //
        const finalData = [];
        const currentData = data.compliance_trend;
        const types = Object.keys(currentData[0]['compliance_info'][0]);
        types.splice(types.indexOf('date'), 1);
        types.forEach(type => {
            let formattedObject = {};
            const values = [];
            currentData.forEach(weeklyData => {
                const apiResponse = weeklyData['compliance_info'];
                apiResponse.forEach(details => {
                    const obj = {
                        'date' : new Date(details['date']),
                        'value': details[type],
                        'zero-value': details[type] === 0 ? true : false
                    };
                    values.push(obj);
                });
            });
            formattedObject = {
                'key'    : type,
                'values' : values
            };
            if (type.toLowerCase() !== 'overall' && type.toLowerCase() !== 'total') {
                finalData.unshift(formattedObject);
            } else {
                finalData.push(formattedObject);
            }
        });
        return finalData;
    }

}
