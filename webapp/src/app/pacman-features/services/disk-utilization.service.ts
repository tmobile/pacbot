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
 * Created by sauravdutta on 16/01/18.
 */
import { Observable } from 'rxjs/Rx';
import { Injectable, Inject } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../shared/services/http-response.service';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';

@Injectable()
export class DiskUtilizationService {

    values: any = [];
    dataArray: any = [];

    constructor(
                private httpService: HttpService,
                private errorHandling: ErrorHandlingService) { }

    getData(Url, Method): Observable<any> {
        const url = Url;
        const method = Method;
        const payload = {};
        const queryParams = {};

        try {
            return this.httpService.getHttpResponse(url, method, payload, queryParams)
                    .map(response => {
                        try {
                            this.dataCheck(response);
                            return this.massageData(response);
                        } catch (error) {
                            this.errorHandling.handleJavascriptError(error);
                        }
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


    abbreviateNumber(number) {
        number = parseInt(number, 10);
        number = number > 1000000 ? (number / 1000000) : (number > 1000 ? (number / 1000) : number > 100 ? (number / 100) : number);
        return number;
    }

    abbreviateNumberWithUnit(number) {
        number = parseInt(number, 10);
        number = number > 1000000000 ? (number / 1000000000).toFixed(1) + ' GB' : (number > 1000000 ? (number / 1000000).toFixed(1) + ' MB' : (number > 1000 ? (number / 1000).toFixed(1) + ' KB' : number));
        return number;
    }


    massageData(data): any {
        for (let i = 0; i < data.response.length; i++) {
            let obj = {};
            const dataValue = {};
            let free = parseInt(this.abbreviateNumber(data.response[i].free), 10);
            const size = parseInt(this.abbreviateNumber(data.response[i].size), 10);
            free = size - free;
            obj = {
              'color': ['#084949', '#cedae2'],
              'data': [free, size],
              'legendTextcolor': '#000',
              'totalCount': -1,
              'name': data.response[i].name,
              'size': this.abbreviateNumberWithUnit(data.response[i].size - data.response[i].free),
              'free': this.abbreviateNumberWithUnit(data.response[i].free),
              'link': true,
                'styling': {
                 'cursor': 'pointer'
                }
            };
            this.values.push(obj);
        }
        this.dataArray = [
            {
                'values': this.values,
            }
        ];
        return this.dataArray;
    }
}
