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

import { OmnisearchComponent } from './../modules/omnisearch/omnisearch.component';
/**
 * Created by Trinanjan on 28/02/18.
 */
import { Observable } from 'rxjs/Rx';
import { Injectable, Inject } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../shared/services/http-response.service';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';

@Injectable()
export class OmniSearchDataService {
    getMethod: any;
    constructor(
                private httpService: HttpService,
                private errorHandling: ErrorHandlingService) { }

    getOmniSearchData(omniSearchUrl, omniSearchMethod, OmnisearchPayload): Observable<any> {
        const url = omniSearchUrl;
        const method = omniSearchMethod;
        const payload = OmnisearchPayload;

        try {
            return this.httpService.getHttpResponse(url, method, payload)
                    .map(response => {
                        this.dataCheck(response);
                        return this.massageData(response);
                    });
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    }
    getOmniSearchCategories(omniSearchUrl, omniSearchMethod , queryParam ): Observable<any> {
        const url = omniSearchUrl;
        const method = omniSearchMethod;
        const payload = {};
        try {
            return this.httpService.getHttpResponse(url, method, {}, queryParam)
                    .map(response => {
                        this.dataCheck(response);
                        return this.massageData(response);
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
        return data;
    }

}
