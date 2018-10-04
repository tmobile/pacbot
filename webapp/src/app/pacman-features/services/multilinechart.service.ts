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
 * Created by Saurav Dutta on 2/11/17.
 */
import { Observable } from 'rxjs/Rx';
import { Injectable, Inject } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { environment } from '../../../environments/environment';
import { HttpService } from '../../shared/services/http-response.service';


@Injectable()
export class MultilineChartService {

    constructor (
                 @Inject(HttpService) private httpService: HttpService) { }



    private combinedData: any = [];

    getDataDiffNew(queryParameters): Observable<any> {
        const MultilineChartNewUrl = environment.MultilineChartNew.url;
        const method = environment.MultilineChartNew.method;
        const payload = {};
        try {
            const allObservables: Observable<any>[] = [];
                const queryParams = {};
                allObservables.push(
                    this.httpService.getHttpResponse(MultilineChartNewUrl, method, payload, queryParameters)
                        .map(response => this.massageResponseNew(response))
                        .catch(error => this.handleCombiningError(error))
                );
            return allObservables.length > 0 ? Observable.combineLatest(allObservables) : Observable.of([]);
        } catch (error) {
            this.handleError(error);
        }    }

    massageResponse(data) {
        const apiResponse = data;
        const values = [];
        let formattedObject = {};
        const allDates = Object.keys(apiResponse).sort();
        for (let i = 0; i < allDates.length; i++) {
            // Additional property 'zero-value' being added to keep track of zero values, as the zero values are replaced
            // with 1 during plotting graph with a log axis (as [log 0]  is infinity)
            const obj = {
                'date' : new Date(allDates[i]),
                'value' : apiResponse[allDates[i]]
            };
            values.push(obj);
        }
        formattedObject = {
            'values' : values
        };
        return formattedObject;
    }

    massageResponseNew(data) {
        const apiResponse = data.trend;
        const valuesMin = [];
        const valuesMax = [];
        let formattedObject = {};
        const allDates = [];
        let keys = [];
        const legends = ['MAX', 'MIN'];
        for (let i = 0; i < apiResponse.length; i++) {
            allDates.push(apiResponse[i].date);
        }

        const searchTerm = 'date';

        if (apiResponse.length > 0) {
            keys.push(Object.keys(apiResponse[0]));
            for (let i = keys[0].length - 1; i >= 0; i--) {
                if (keys[0][i] === searchTerm) {
                    keys[0].splice(i, 1);
                }
            }

            keys = keys.filter(function(item, index, inputArray) {
                    return inputArray.indexOf(item) === index;
                });

            for (let i = 0; i < allDates.length; i++) {
                // Additional property 'zero-value' being added to keep track of zero values, as the zero values are replaced
                // with 1 during plotting graph with a log axis (as [log 0]  is infinity)
                const obj = {
                    'date' : new Date(allDates[i]),
                    'value': apiResponse[i].min,
                    'keys': keys[0],
                    'legends': legends
                };
                valuesMin.push(obj);

                const objmax = {
                    'date' : new Date(allDates[i]),
                    'value': apiResponse[i].max,
                    'keys': keys[0],
                    'legends': legends
                };
                valuesMax.push(objmax);
            }

            valuesMin.sort(function(a, b) {
                return new Date(a.date).getTime() - new Date(b.date).getTime();
            });

            valuesMax.sort(function(a, b) {
                return new Date(a.date).getTime() - new Date(b.date).getTime();
            });
        }



        formattedObject = [{
            'values' : valuesMin
        }, {
            'values' : valuesMax
        }];
        return formattedObject;
    }

    handleCombiningError(error: any): Observable<any> {
        let errorMessage = 'Error while processing response';
        if (error._body !== undefined) {
            errorMessage = JSON.parse(error._body).message;
        }
        if (errorMessage === 'no data found') {
            return Observable.of(this.massageResponse([]));
        } else {
            return Observable.throw(error.message || error);
        }
    }

    handleError(error: any): Observable<any> {
        return Observable.throw(error.message || error);
    }
    handlePromiseError(error: any): Promise<any> {
        return Promise.reject(error.message || error);
    }
}
