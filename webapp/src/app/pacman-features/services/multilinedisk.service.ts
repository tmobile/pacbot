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
 * Created by Saurav Dutta on 4/11/17.
 */
import { Observable } from 'rxjs/Rx';
import { Injectable, Inject } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { environment } from '../../../environments/environment';
import { HttpService } from '../../shared/services/http-response.service';


@Injectable()
export class MultilineChartServiceDisk {

    constructor (
                 @Inject(HttpService) private httpService: HttpService) { }


    private combinedData: any = [];

    getData(queryParameters): Observable<any> {
        const MultilineChartDiskUrl = environment.MultilineChartDisk.url;
        const method = environment.MultilineChartDisk.method;
        const payload = {};
        try {
            const allObservables: Observable<any>[] = [];
            const queryParams = {};
            allObservables.push(
                this.httpService.getHttpResponse(MultilineChartDiskUrl, method, payload, queryParameters)
                    .map(response => this.massageResponse(response))
                    .catch(error => this.handleCombiningError(error))
            );
            return allObservables.length > 0 ? Observable.combineLatest(allObservables) : Observable.of([]);
        } catch (error) {
            this.handleError(error);
        }
    }

    massageResponse(data) {
        const apiResponse = data.response;
        const valuesWrite = [];
        const valuesRead = [];
        let formattedObject = {};
        const allDates = [];
        let keys = [];
        const legends = ['WRITE', 'READ'];
        for (let i = 0; i < apiResponse.length; i++) {
            allDates.push(apiResponse[i].date);
        }
        keys.push(Object.keys(apiResponse[0]));

        const searchTerm = 'date';
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
            const write_obj = {
                'date' : new Date(allDates[i]),
                'value' : apiResponse[i].diskWriteinBytes,
                'keys' : keys[0],
                'legends' : legends
            };
            valuesWrite.push(write_obj);

            const read_obj = {
                'date' : new Date(allDates[i]),
                'value' : apiResponse[i].diskReadinBytes,
                'keys' :  keys[0],
                'legends' : legends
            };
            valuesRead.push(read_obj);

        }

        valuesWrite.sort(function(a, b) {
            return new Date(a.date).getTime() - new Date(b.date).getTime();
        });

        valuesRead.sort(function(a, b) {
            return new Date(a.date).getTime() - new Date(b.date).getTime();
        });

        formattedObject = [{
            'values' : valuesWrite
        }, {
            'values' : valuesRead
        }];
        return formattedObject;
    }

    handleCombiningError(error: any): Observable<any> {
        const errorMessage = error.message;
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

