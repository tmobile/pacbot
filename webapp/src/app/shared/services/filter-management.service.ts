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
import {environment} from '../../../environments/environment';
import {UtilsService} from './utils.service';
import {LoggerService} from './logger.service';
import {RefactorFieldsService} from './refactor-fields.service';

@Injectable()
export class FilterManagementService {

    constructor(
                private httpService: HttpService,
                private utils: UtilsService,
                private logger: LoggerService,
                private refactorFieldsService: RefactorFieldsService) {}

    getApplicableFilters(filterId, filterParams = {}) {

        const url = environment.issueFilter.url;
        const method = environment.issueFilter.method;
        const payload = {};
        const queryParams = filterParams;
        queryParams['filterId'] = filterId;

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

    getValuesForFilterType(currentFilterType, queryParam = {}, payload = {}) {

        const url = environment.base + this.utils.getParamsFromUrlSnippet(currentFilterType.optionURL).url;
        const method = 'GET';

        try {
            return Observable.combineLatest(
                this.httpService.getHttpResponse(url, method, payload, queryParam)
                    .map(response => this.massageData(response) )
                    .catch(this.handleError)
            );
        } catch (error) {
            this.handleError(error);
        }
    }

    getFilterArray(pageLevelAppliedFilters) {
        try {
            const localFilters = []; // <<-- this filter is used to store data for filter
            const filterObjKeys = Object.keys(pageLevelAppliedFilters);
            const dataArray = [];
            for ( let i = 0; i < filterObjKeys.length; i++) {
                let obj = {};
                obj = {
                    name: filterObjKeys[i]
                };
                dataArray.push(obj);
            }

            const filterValues = dataArray;
            const formattedFilters = dataArray.map(data => {
                data.name = this.refactorFieldsService.getDisplayNameForAKey(data.name) || data.name;
                return data;
            });

            for ( let i = 0; i < formattedFilters.length; i++) {
                const eachObj = {
                    key: formattedFilters[i].name, // <-- displayKey-- Resource Type
                    value: pageLevelAppliedFilters[filterObjKeys[i]], // <<-- value to be shown in the filter UI-- S2
                    filterkey: filterObjKeys[i].trim(), // <<-- filter key that to be passed -- 'resourceType '
                    compareKey: filterObjKeys[i].toLowerCase().trim() // <<-- key to compare whether a key is already present -- 'resourcetype'
                };
                localFilters.push(eachObj);
            }

            return localFilters;

        } catch (error) {
            this.logger.log('error', error);
        }
    }

    handleError(error: any): Observable<any> {
        return Observable.throw(error.message || error);
    }

    massageData(data): any {
        return data;
    }

}
