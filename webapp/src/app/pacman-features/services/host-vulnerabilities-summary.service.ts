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
export class HostVulnerabilitiesSummaryService {
    constructor(
                private httpService: HttpService,
                private errorHandling: ErrorHandlingService) {}

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

    massageData(data): any {
        const dataArray = [];
        for (let i = 0; i < data.distribution.severityInfo.length; i++) {
            dataArray.push(data.distribution.severityInfo[i].count);
        }
        dataArray.reverse();
        const dataValue = {
            'color': ['#d40325', '#f75c03', '#ffb00d'],
            'data': dataArray,
            'legend': ['S5', 'S4', 'S3'],
            'legendTextcolor': '#000',
            'totalCount': data.distribution.total,
            'link': true,
            'styling': {
             'cursor': 'pointer'
            }
          };
        return dataValue;
    }
}
