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
 * Created by Mohammed_Furqan on 10/10/17.
 */
import { Observable } from 'rxjs/Rx';
import { combineLatest } from 'rxjs/observable/combineLatest';
import { Injectable, Inject } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { environment } from './../../../environments/environment';
import { HttpService } from '../../shared/services/http-response.service';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';


@Injectable()
export class FetchResourcesService {

    constructor (
                 @Inject(HttpService) private httpService: HttpService,
                 private errorHandling: ErrorHandlingService) {}


    getAllResourceCounts(queryParams) {
        try {
            const url = environment.resourceCount.url;
            const method = environment.resourceCount.method;
            return this.httpService.getHttpResponse(url, method, {}, queryParams)
                    .map(response => {
                        return response;
                    })
                    .catch(error => {
                        return Observable.of(null);
                    });
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    }

    getAllResourceCategories(queryParams) {
        try {
            const url = environment.resourceCategories.url;
            const method = environment.resourceCategories.method;
            return this.httpService.getHttpResponse(url, method, {}, queryParams)
                    .map(response => {
                        return response;
                    });
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    }

    getRecommendations(queryParams) {
        try {
            const url = environment.recommendationStatus.url;
            const method = environment.recommendationStatus.method;
            return this.httpService.getHttpResponse(url, method, {}, queryParams)
                    .map(response => {
                        return response;
                    })
                    .catch(error => {
                        return Observable.of(null);
                    });
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    }

    getResourceTypesAndCount(queryParams) {
        try {
            const resourceType = this.getAllResourceCategories(queryParams);
            const resourceTypeCount = this.getAllResourceCounts(queryParams);
            const recommendations = this.getRecommendations(queryParams);
            const resourceTypeAndCountAndRecommendation = combineLatest(resourceType, resourceTypeCount, recommendations);
            return resourceTypeAndCountAndRecommendation;
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

}
