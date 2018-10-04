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
 * Created by adityaagarwal on 16/10/17.
 */
import { Observable } from 'rxjs/Rx';
import { Injectable, Inject } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../shared/services/http-response.service';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';

@Injectable()
export class ComplianceCategoriesService {

    dataArray: any = {};
    constructor( @Inject(HttpService) private httpService: HttpService,
        private errorHandling: ErrorHandlingService) {}
    getData(queryParams, category, categoryUrl, categoryMethod) {
        const url = categoryUrl;
        const method = categoryMethod;
        const payload = {};
        const query = queryParams;
        try {
            return Observable.combineLatest(
            this.httpService.getHttpResponse(url, method, payload, query)
            .map(response => this.massageData(response, category) )
            );
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    }

    massageData(data, category): any {
        this.dataArray[category] = data;
        this.dataArray[category].loaded = true;
        return this.dataArray;
    }

}



