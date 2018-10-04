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
import { HttpClient, HttpRequest, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { HttpService } from '../../shared/services/http-response.service';
import { ErrorHandlingService } from "../../shared/services/error-handling.service";
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {Http, Headers, RequestOptions, ResponseContentType} from '@angular/http';
import {forEach} from '@angular/router/src/utils/collection';
import {DataCacheService} from '../../core/services/data-cache.service';
import { environment } from '../../../environments/environment';
import { UtilsService } from '../../shared/services/utils.service';
import { LoggerService } from '../../shared/services/logger.service';
import { HttpResponse } from '../../shared/models/http-response';
import { CONFIGURATIONS } from '../../../config/configurations';


@Injectable()
export class UploadFileService {

  constructor(
    private http: Http,
    @Inject(HttpService) 
    private httpService: HttpService,
    private errorHandling: ErrorHandlingService,
    private utilityService: UtilsService,
    private logger: LoggerService,
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
  envName: string;
  baseUrl: string;
  cloudBaseUrl: string;
  payload: any;
  HTTP_RESPONSE: HttpResponse[];

  pushFileToStorage(url, method, file: File, formDataValues) {

    let formdata: FormData = new FormData();
    formdata.append('file', file);
    
    for (var key in formDataValues) {
      if (formDataValues.hasOwnProperty(key)) {
        formdata.append(key, formDataValues[key]);
      }
    }

    try {
      return this.httpService.getHttpResponse(url, method, formdata)
        .map(response => {
            return this.massageData(response);
        })
        .catch(error => this.errorHandling.handleAPIError(error));
    } catch(error){
        this.errorHandling.handleJavascriptError(error);
    }
  }

  //Function for getting just the auth token....
  getAuthValue(){
    /*const authToken = this.dataStore.getUserDetailsValue();
    const authTokenValue = authToken.getAuthToken();
    return authTokenValue;*/
  }

  //Function for creating just the header object...
  getHeaders(){
    const headersValue = {};
    /* Auth token is not required now */
    // headersValue = {headers: new Headers({'Authorization': this.getAuthValue()})};
    return headersValue;
  }

  getHttpResponse(url, method, payload = {}, queryParams = {}, headers = this.getHeaders()): Observable<HttpResponse[]> {
    this.payload = JSON.stringify(payload);
    // TODO GET NOT TESTED YET
    if (method.toUpperCase() === 'GET') {
        try {
            let updatedUrl = url;
            updatedUrl = updatedUrl.replace('{{baseUrl}}', this.baseUrl);
            updatedUrl = updatedUrl.replace('{{cloudBaseUrl}}', this.cloudBaseUrl);
            if (url.indexOf('/api/') !== 0) {
                updatedUrl += this.convertQueryParametersToString(queryParams);
            }
            /* Removing the http caching for now as sometimes cached data is shown for new values as well. This needs thorough checking                 */
            //return this.httpCacheService.get(updatedUrl, this.getData(updatedUrl));
            return this.getData(updatedUrl,headers);

        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    } else if (method.toUpperCase() === 'POST') {
        try {

            let updatedUrl = url;
            updatedUrl = updatedUrl.replace('{{baseUrl}}', this.baseUrl);
            updatedUrl = updatedUrl.replace('{{cloudBaseUrl}}', this.cloudBaseUrl);
            if (url.indexOf('/api/') !== 0 && Object.keys(queryParams).length != 0) {
                updatedUrl += this.convertQueryParametersToString(queryParams);
            }
         
            return this.postData(updatedUrl, payload, headers);
            
        } catch (error) {
            this.errorHandling.handleJavascriptError(error);
        }
    }
  }

  postData(url, payload, headers) {
    let httpObservable = this.http.post(url, payload, headers)
        .map(response => {
            return response['_body'] ? JSON.parse(response['_body']) : response;                
        })
        .catch(error => this.errorHandling.handleAPIError(error));
    return httpObservable;
  }

  convertQueryParametersToString(queryParams: any) {
    var queryParamString = '';
    if (!this.utilityService.isObjectEmpty(queryParams)) {
        queryParamString += '?';
        Object.keys(queryParams).forEach((param) => {
            queryParamString += (queryParams[param] !== '' && queryParams[param] !== undefined) ? param + '=' + encodeURIComponent(queryParams[param]) + '&' : '';
        });
        if (queryParamString[queryParamString.length - 1] == '&') {
            queryParamString = queryParamString.substr(0, queryParamString.length - 1);
        }
    }
    return queryParamString;
  };

  getData(url, headers) {
    let httpObservable = this.http.get(url,headers)
      .map(response => {
          if(url.match("logout-session")){
              return response;
          } else {    
              return response['data'];
          }
      })
      .catch(error => this.errorHandling.handleAPIError(error));
    return httpObservable;
  }

  massageData(data): any {
    return data;
 } 
}