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
 * Created by TJ SAHA on 20/12/17.
 */
import { Observable } from 'rxjs/Rx';
import { Injectable, Inject } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { environment } from './../../../environments/environment';
import { HttpService } from '../../shared/services/http-response.service';
import { LoggerService } from '../../shared/services/logger.service';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';

@Injectable()
export class PolicyTrendService {
    constructor (
                 @Inject(HttpService) private httpService: HttpService,
                 private logger: LoggerService,
                 private errorHandling: ErrorHandlingService) { }

    getData(severeties, payload): Observable<any> {
        const historyUrl = environment.policyTrend.url;
        const method = environment.policyTrend.method;
        try {
            const queryParams = {};
            return this.httpService.getHttpResponse(historyUrl, method, payload, queryParams)
                .map(response => this.massageResponse(response['data'].response));
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    }

    massageResponse(data) {
        const finalData = [];
        if (data.hasOwnProperty('compliance_trend')) {
            const data_trend = data.compliance_trend;
            if (data_trend.length) {
                const types = Object.keys(data_trend[0]['compliance_info'][0]);
                types.splice(types.indexOf('date'), 1);
                types.forEach(type => {
                    let formattedObject = {};
                    const values = [];
                    data_trend.forEach(weeklyData => {
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
            }
        }
        return finalData;
    }

}
