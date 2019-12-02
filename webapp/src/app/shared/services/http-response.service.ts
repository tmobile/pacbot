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


import {catchError} from 'rxjs/operators/catchError';
import {map} from 'rxjs/operators/map';
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {HttpResponse} from '../models/http-response';
import {Observable} from 'rxjs/Observable';
import {UtilsService} from './utils.service';
import {DataCacheService} from '../../core/services/data-cache.service';
import {ErrorHandlingService} from './error-handling.service';
import {environment} from '../../../environments/environment';
import {CONFIGURATIONS} from '../../../config/configurations';
import * as objectHash from 'object-hash';

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
    responseStore = {};

    getBlobHttpResponse(url, method, payload?, queryParams?, options?) {

        const updatedUrl = this.formatUrl(url, queryParams);

        if (method.toUpperCase() === 'GET') {
            try {
                return this.getBlobData(updatedUrl, options);
            } catch (error) {
                this.errorHandling.handleJavascriptError(error);
            }
        } else if (method.toUpperCase() === 'POST') {
            try {
                return this.postBlobData(updatedUrl, payload);
            } catch (error) {
                this.errorHandling.handleJavascriptError(error);
            }
        }
    }

    // headers is kept as an optional parameter....
    getHttpResponse(url, method, payload = {}, queryParams = {}, options = {}, noCache?): Observable<HttpResponse[]> {

        const updatedUrl = this.formatUrl(url, queryParams);

        if (method.toUpperCase() === 'GET') {
            try {
                return this.getData(updatedUrl, options, noCache);
            } catch (error) {
                this.errorHandling.handleJavascriptError(error);
            }
        } else if (method.toUpperCase() === 'POST') {
            try {
                return this.postData(updatedUrl, payload, options, noCache);
            } catch (error) {
                this.errorHandling.handleJavascriptError(error);
            }
        } else if (method.toUpperCase() === 'DELETE') {
            try {
                return this.deleteData(updatedUrl, options, noCache);

            } catch (error) {
                this.errorHandling.handleJavascriptError(error);
            }
        }  else if (method.toUpperCase() === 'PUT') {
            try {
                return this.putData(updatedUrl, payload, options, noCache);
            } catch (error) {
                this.errorHandling.handleJavascriptError(error);
            }
        }
    }

    getBlobData(url, options) {

        // Add response type as blob
        options['responseType'] = 'blob';

        const httpObservable = this.http.get(url, options).pipe(
            map(response => {
                return response;
            }),
            catchError(error => this.errorHandling.handleAPIError(error)));

        return httpObservable;
    }

    getData(url, headers, noCache): Observable<HttpResponse[]> {
        const urlKey = url + 'GET';
        if (this.responseStore.hasOwnProperty(urlKey) && !noCache && !urlKey.match('admin')) {
            const returnJson = JSON.parse(JSON.stringify(this.responseStore[urlKey]));
            const cacheObservable: Observable<HttpResponse[]> = new Observable((observer) => {
                observer.next(returnJson);
                observer.complete();
            });
            return cacheObservable;
        } else {
            const httpObservable = this.http.get(url, headers).pipe(
                map(response => {
                    if (url.match('logout-session')) {
                        return response;
                    } else {
                        if (!urlKey.match('admin') && !noCache) {
                            this.responseStore[urlKey] = JSON.parse(JSON.stringify(response['data']));
                            setTimeout(() => {
                                delete this.responseStore[urlKey];
                            }, 300000);
                        }
                        return JSON.parse(JSON.stringify(response['data']));
                    }
                }),
                catchError(error => this.errorHandling.handleAPIError(error)));
            return httpObservable;
        }
    }

    postData(url, payload, headers, noCache): Observable<HttpResponse[]> {
        const urlKey = url + objectHash(payload) + 'POST';
        if (this.responseStore.hasOwnProperty(urlKey) && !noCache && !urlKey.match('admin')) {
            const returnJson = JSON.parse(JSON.stringify(this.responseStore[urlKey]));
            const cacheObservable: Observable<HttpResponse[]> = new Observable((observer) => {
                observer.next(returnJson);
                observer.complete();
            });
            return cacheObservable;
        } else {
            const httpObservable = this.http.post(url, payload, headers).pipe(
                map(response => {
                    if (!response) {
                        return response;
                    }
                    if (!urlKey.match('admin') && !noCache) {
                        if (response['_body']) {
                            this.responseStore[urlKey] = JSON.parse(JSON.stringify(response['_body']));
                        } else {
                            this.responseStore[urlKey] = JSON.parse(JSON.stringify(response));
                        }
                        setTimeout(() => {
                            delete this.responseStore[urlKey];
                        }, 300000);
                    }
                    return response['_body'] ? JSON.parse(response['_body']) : response;
                }),
                catchError(error => this.errorHandling.handleAPIError(error)));
            return httpObservable;
        }
    }

    putData(url, payload, headers, noCache) {

        const httpObservable = this.http.put(url, payload, headers).pipe(
            map(response => {
                if (!response) {
                    return response;
                }
                return response['_body'] ? JSON.parse(response['_body']) : response;
            }),
            catchError(error => this.errorHandling.handleAPIError(error)));

        return httpObservable;

    }

    deleteData(url, headers, noCache) {
        const httpObservable = this.http.delete(url, headers).pipe(
            map(response => {
                return response['_body'] ? JSON.parse(response['_body']) : response;
            }),
            catchError(error => this.errorHandling.handleAPIError(error)));

        return httpObservable;
    }

    postBlobData(url, payload) {
        const httpObservable = this.http.post(url, payload, {
            responseType: 'blob'
        }).pipe(
        map(response => {
            return response;
        }),
        catchError(error => this.errorHandling.handleAPIError(error)));

        return httpObservable;
    }

    /*** Utility functions ***/

    convertQueryParametersToString(queryParams: any) {
        let queryParamString = '';
        if (!this.utilityService.isObjectEmpty(queryParams)) {
            queryParamString += '?';
            const queryArray = Object.keys(queryParams);
            queryArray.sort();
            queryArray.forEach((param) => {
                queryParamString += (queryParams[param] !== '' && queryParams[param] !== undefined) ? param + '=' + encodeURIComponent(queryParams[param]) + '&' : '';
            });
            if (queryParamString[queryParamString.length - 1] === '&') {
                queryParamString = queryParamString.substr(0, queryParamString.length - 1);
            }
        }
        return queryParamString;
    }

    formatUrl(url, queryParams) {

        let updatedUrl = url;
        updatedUrl = updatedUrl.replace('{{baseUrl}}', this.baseUrl);
        updatedUrl = updatedUrl.replace('{{cloudBaseUrl}}', this.cloudBaseUrl);
        if (url.indexOf('/api/') !== 0 && Object.keys(queryParams).length !== 0) {
            updatedUrl += this.convertQueryParametersToString(queryParams);
        }

        return updatedUrl;
    }

}
