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
 * Created by adityaagarwal on 23/11/17.
 */

import { Injectable, Inject } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../shared/services/http-response.service';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';

@Injectable()
export class IssueListingService {
    constructor(
                @Inject(HttpService) private httpService: HttpService,
                private errorHandling: ErrorHandlingService) {}

    getData(listingPayload, listingUrl, listingMethod): Observable<any> {

        try {
            const url = listingUrl;
            const method = listingMethod;
            const payload = listingPayload;
            const queryParams = {};
            return Observable.combineLatest(
                this.httpService.getHttpResponse(url, method, payload, queryParams)
                .map(response => {
                    try {
                        this.dataCheck(response);
                        return response['data'];
                    } catch (error) {
                        this.errorHandling.handleJavascriptError(error);
                    }
                })
            );
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

    handleError(error: any): Observable<any> {
        return Observable.throw(error.message || error);
    }

}
