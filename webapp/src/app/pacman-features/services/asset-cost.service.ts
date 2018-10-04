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


import { Injectable, Inject } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../shared/services/http-response.service';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';

@Injectable()

export class AssetCostService {
    constructor(@Inject(HttpService) private httpService: HttpService,
                private errorHandling: ErrorHandlingService) {}

    getData(Url, Method): Observable<any> {
        const url = Url;
        const method = Method;
        const payload = {};
        const queryParams = {};
        const costArray = [];
        try {
            return this.httpService.getHttpResponse(url, method, payload, queryParams)
                    .map(response => {
                        if (Object.keys(response).length > 0) {
                            for (let i = 0; i < Object.keys(response).length; i++) {
                                costArray.push({
                                    name: Object.keys(response)[i],
                                    value: '$' + this.toInt(Object.values(response)[i]) // adding USD symbol to cost
                                });
                            }
                        }
                        return costArray;
                    });
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }

    }

    // round off value and cast to integer
    toInt (n) {
        return Math.round(Number(n));
    }
}
