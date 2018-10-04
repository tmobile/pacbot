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
import { HttpService } from './http-response.service';
import { LoggerService } from './logger.service';
import { ErrorHandlingService } from './error-handling.service';

@Injectable()
export class CommonResponseService {

    constructor(private httpService: HttpService,
                private logger: LoggerService,
                private errorHandling: ErrorHandlingService) { }

    getData(dataUrl, dataMethod, dataPayload = {}, dataQuery = {}, headers = {}): Observable<any> {

        const url = dataUrl;
        const method = dataMethod;
        const payload = dataPayload;
        const queryParams = dataQuery;
        try {
            return this.httpService.getHttpResponse(url, method, payload, queryParams, headers)
                    .map(response => {
                        return response;
                    });
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
            this.logger.log('error', error);
        }
    }
}
