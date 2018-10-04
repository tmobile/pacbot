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
import * as _ from 'lodash';

@Injectable()
export class PatchingSponsorService {

    constructor(@Inject(HttpService) private httpService: HttpService,
                private errorHandling: ErrorHandlingService) { }
    getData(patchingTableUrl, patchingTableMethod, payload): Observable<any> {
        const url = patchingTableUrl;
        const method = patchingTableMethod;
        const queryParams = {};
        try {
            return this.httpService.getHttpResponse(url, method, payload, queryParams)
                    .map(response => {
                        return this.massageData(response);
                    });
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    }
    massageData(data) {
        // sort data as per scope
        data.data.patchingProgress = _.orderBy(data.data.patchingProgress, ['q2 scope'], ['desc']);
        return data;
    }
}
