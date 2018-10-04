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

@Injectable()
export class IssueFilterService {

    constructor( @Inject(HttpService) private httpService: HttpService) { }


    getFilters(filterParams, filterUrl, filterMethod): Observable<any> {

        const url = filterUrl;
        const method = filterMethod;
        const payload = {};
        const queryParams = filterParams;

        try {
            return Observable.combineLatest(
                this.httpService.getHttpResponse(url, method, payload, queryParams)
                    .map(response => this.massageData(response) )
                    .catch(this.handleError)
            );
        } catch (error) {
            this.handleError(error);
        }
    }

    handleError(error: any): Observable<any> {
        return Observable.throw(error.message || error);
    }

    massageData(data): any {
        return data;
    }

}
