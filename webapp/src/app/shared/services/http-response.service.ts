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

import {Injectable} from '@angular/core';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import { Headers, ResponseContentType} from '@angular/http';
import {HttpClient} from '@angular/common/http';
import {HttpResponse} from '../models/http-response';
import {Observable} from 'rxjs/Rx';
import {UtilsService} from './utils.service';
import {DataCacheService} from '../../core/services/data-cache.service';
import {ErrorHandlingService} from './error-handling.service';
import {environment} from '../../../environments/environment';
import {CONFIGURATIONS} from '../../../config/configurations';


@Injectable()
export class HttpService {

    constructor(private http: HttpClient,
                private utilityService: UtilsService,
                private errorHandling: ErrorHandlingService,
                private dataStore: DataCacheService) {

                    this.envName = environment.envName;
                    if ( this.envName === 'dev' ) {
                        this.baseUrl = CONFIGURATIONS.required.domains.DEV_BASE_URL;
                    } else if ( this.envName === 'stg' ) {
                        this.baseUrl = CONFIGURATIONS.required.domains.STG_BASE_URL;
                    } else {
                        this.baseUrl = CONFIGURATIONS.required.domains.PROD_BASE_URL;
                    }
                    this.cloudBaseUrl = CONFIGURATIONS.required.domains.CLOUD_BASE_URL;
    }

    called: any = 0;
    formBody: any = [];
    timeSet: any = 0;
    envName: string;
    baseUrl: string;
    cloudBaseUrl: string;

    getBlobResponse(url, method, payload = { responseType: ResponseContentType.Blob }, queryParams = {}) {
        if (method.toUpperCase() === 'GET') {
            try {
                let updatedUrl = url;
                updatedUrl = updatedUrl.replace('{{baseUrl}}', this.baseUrl);
                updatedUrl = updatedUrl.replace('{{cloudBaseUrl}}', this.cloudBaseUrl);
                if (url.indexOf('/api/') !== 0) {
                    updatedUrl += this.convertQueryParametersToString(queryParams);
                }
                const headers = {headers: new Headers({})};
                return this.getData(updatedUrl, headers);

            } catch (error) {
                this.errorHandling.handleJavascriptError(error);
            }
        } else if (method.toUpperCase() === 'POST') {
            try {

                let updatedUrl = url;
                updatedUrl = updatedUrl.replace('{{baseUrl}}', this.baseUrl);
                updatedUrl = updatedUrl.replace('{{cloudBaseUrl}}', this.cloudBaseUrl);

                if (url.indexOf('/api/') !== 0 && Object.keys(queryParams).length !== 0) {
                    updatedUrl += this.convertQueryParametersToString(queryParams);
                }
                return this.postBlobData(updatedUrl, payload);
            } catch (error) {
                this.errorHandling.handleJavascriptError(error);
            }
        }
    }

    // headers is kept as an optional parameter....
    getHttpResponse(url, method, payload = {}, queryParams = {}, headers = {}): Observable<HttpResponse[]> {

        if (method.toUpperCase() === 'GET') {
            try {
                let updatedUrl = url;
                updatedUrl = updatedUrl.replace('{{baseUrl}}', this.baseUrl);
                updatedUrl = updatedUrl.replace('{{cloudBaseUrl}}', this.cloudBaseUrl);

                if (url.indexOf('/api/') !== 0) {
                    updatedUrl += this.convertQueryParametersToString(queryParams);
                }
                return this.getData(updatedUrl, headers);

            } catch (error) {
                this.errorHandling.handleJavascriptError(error);
            }
        } else if (method.toUpperCase() === 'POST') {
            try {

                let updatedUrl = url;
                updatedUrl = updatedUrl.replace('{{baseUrl}}', this.baseUrl);
                updatedUrl = updatedUrl.replace('{{cloudBaseUrl}}', this.cloudBaseUrl);
                if (url.indexOf('/api/') !== 0 && Object.keys(queryParams).length !== 0) {
                    updatedUrl += this.convertQueryParametersToString(queryParams);
                }

                return this.postData(updatedUrl, payload, headers);

            } catch (error) {
                this.errorHandling.handleJavascriptError(error);
            }
        } else if (method.toUpperCase() === 'DELETE') {
            try {
                let updatedUrl = url;
                updatedUrl = updatedUrl.replace('{{baseUrl}}', this.baseUrl);
                updatedUrl = updatedUrl.replace('{{cloudBaseUrl}}', this.cloudBaseUrl);
                if (url.indexOf('/api/') !== 0 && Object.keys(queryParams).length !== 0) {
                    updatedUrl += this.convertQueryParametersToString(queryParams);
                }

                return this.deleteData(updatedUrl, headers);

            } catch (error) {
                this.errorHandling.handleJavascriptError(error);
            }
        }  else if (method.toUpperCase() === 'PUT') {
            try {

                let updatedUrl = url;
                updatedUrl = updatedUrl.replace('{{baseUrl}}', this.baseUrl);
                updatedUrl = updatedUrl.replace('{{cloudBaseUrl}}', this.cloudBaseUrl);
                if (url.indexOf('/api/') !== 0 && Object.keys(queryParams).length !== 0) {
                    updatedUrl += this.convertQueryParametersToString(queryParams);
                }

                return this.putData(updatedUrl, payload, headers);

            } catch (error) {
                this.errorHandling.handleJavascriptError(error);
            }
        }
    }

    convertQueryParametersToString(queryParams: any) {
        let queryParamString = '';
        if (!this.utilityService.isObjectEmpty(queryParams)) {
            queryParamString += '?';
            Object.keys(queryParams).forEach((param) => {
                queryParamString += (queryParams[param] !== '' && queryParams[param] !== undefined) ? param + '=' + encodeURIComponent(queryParams[param]) + '&' : '';
            });
            if (queryParamString[queryParamString.length - 1] === '&') {
                queryParamString = queryParamString.substr(0, queryParamString.length - 1);
            }
        }
        return queryParamString;
    }

    getData(url, headers) {

        const httpObservable = this.http.get(url, headers)
            .map(response => {
                if (url.match('logout-session')) {
                    return response;
                } else {
                    return response['data'];
                }
            })
            .catch(error => this.errorHandling.handleAPIError(error));

    return httpObservable;

    }

    postData(url, payload, headers) {

        const httpObservable = this.http.post(url, payload, headers)
            .map(response => {
                if (!response) {
                    return response;
                }
                return response['_body'] ? JSON.parse(response['_body']) : response;
            })
            .catch(error => this.errorHandling.handleAPIError(error));

        return httpObservable;

    }

    putData(url, payload, headers) {

        const httpObservable = this.http.put(url, payload, headers)
            .map(response => {
                if (!response) {
                    return response;
                }
                return response['_body'] ? JSON.parse(response['_body']) : response;
            })
            .catch(error => this.errorHandling.handleAPIError(error));

        return httpObservable;

    }



    deleteData(url, headers) {
        const httpObservable = this.http.delete(url, headers)
            .map(response => {
                return response['_body'] ? JSON.parse(response['_body']) : response;
            })
            .catch(error => this.errorHandling.handleAPIError(error));

        return httpObservable;
    }

    postBlobData(url, payload) {
        const httpObservable = this.http.post(url, payload, {
            responseType: 'blob'
        })
        .map(response => {
            return response;
        })
        .catch(error => this.errorHandling.handleAPIError(error));

        return httpObservable;
    }

}
