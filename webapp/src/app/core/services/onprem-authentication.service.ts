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

import { Injectable } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../shared/services/http-response.service';
import { Observable } from 'rxjs/Observable';
import { DataCacheService } from './data-cache.service';
import { environment } from '../../../environments/environment';
import { LoggerService } from '../../shared/services/logger.service';
import {AuthSessionStorageService} from './auth-session-storage.service';

@Injectable()
export class OnPremAuthenticationService {
  constructor(
    private httpService: HttpService,
    private dataStore: DataCacheService,
    private loggerService: LoggerService,
    private authSessionStorageService: AuthSessionStorageService
  ) {}

  handleError(error: any): Observable<any> {
    return Observable.throw(error.message || error);
  }

  logout() {
    const logoutUrl = environment.logout.url;
    const logoutMethod = environment.logout.method;

    const logoutObserver = this.httpService
      .getHttpResponse(logoutUrl, logoutMethod, {}, {})
      .map(response => {
        return response;
      })
      .catch(error => this.handleError(error));

    logoutObserver.subscribe(
      response => {
        this.loggerService.log('info', 'Logged out successfully');
        window.location.replace('/home'); // Reloading the page, to reset the observable sujects
      },
      error => {
        this.loggerService.log('error', error);
        window.location.replace('/home'); // Reloading the page, to reset the observable sujects
      }
    );
  }

  formatUsernameWithoutDomain(username) {
    const regex = /^(gsm1900\/|gsm1900\\)/gi;
    return username.replace(regex, '');
  }

  isAuthenticated() {
    const isAuthenticated = (this.dataStore.getUserDetailsValue() && this.dataStore.getUserDetailsValue().isAuthenticated());
    // Return true if authenticated. Return false if user is not authenticated.
    return isAuthenticated;
    // return this.utilityService.strToBool(token);
  }

  /* Deprecated: this function is not being used anymore */
  getRedirectUrl() {
     return this.dataStore.getRedirectUrl();
  }

  massageAndStoreUserDetails(userDetails) {
    this.authSessionStorageService.saveUserDetails(userDetails);
  }
}
