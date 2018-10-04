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
 * Created by adityaagarwal on 11/02/18.
 */

import { Observable } from 'rxjs/Observable';
import { Injectable, Inject } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../../shared/services/http-response.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';

@Injectable()

export class HelpObservableService {

    constructor(@Inject(HttpService) private httpService: HttpService,
                private errorHandling: ErrorHandlingService) { }



    getData(queryParam, tableUrl, tableMethod, payload): Observable<any> {

        const url = tableUrl;
        const method = tableMethod;
        const queryParams = queryParam;

        try {
            return this.httpService.getHttpResponse(url, method, payload, queryParams)
                    .map(response => {
                        return response;
                    });
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }

    }

}
