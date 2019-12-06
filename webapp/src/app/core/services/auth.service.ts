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
import { HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { AdalService } from './adal.service';
import { CONFIGURATIONS } from './../../../config/configurations';
import { OnPremAuthenticationService } from './onprem-authentication.service';
import { Router } from '@angular/router';
import { AssetGroupObservableService } from './asset-group-observable.service';
import { LoggerService } from './../../shared/services/logger.service';
import { HttpService } from '../../shared/services/http-response.service';
import { DataCacheService } from './data-cache.service';
import { UtilsService } from '../../shared/services/utils.service';
import { environment } from '../../../environments/environment';
import { CommonResponseService } from '../../shared/services/common-response.service';

@Injectable()
export class AuthService {

    private adAuthentication;

    constructor(private adalService: AdalService,
                private onPremAuthentication: OnPremAuthenticationService,
                private router: Router,
                private assetGroupObservableService: AssetGroupObservableService,
                private loggerService: LoggerService,
                private httpService: HttpService,
                private dataStore: DataCacheService,
                private utilService: UtilsService,
                private commonResponseService: CommonResponseService,
                private logger: LoggerService) {

        this.adAuthentication = CONFIGURATIONS.optional.auth.AUTH_TYPE === 'azuresso';
    }

    /*
    desc: This initiates the login process based on configuration
    */
    doLogin() {
        if (this.authenticated) {
            const userDefaultAssetGroup = this.dataStore.getUserDefaultAssetGroup();
            this.redirectPostLogin(userDefaultAssetGroup);
        } else {
            const loginUrl = '/home/login';

            this.router.navigateByUrl(loginUrl).then(result => {
                this.loggerService.log('info', 'Redirected to login page successfully - ' + result);
            },
            error => {
                this.loggerService.log('error', 'Error navigating to login - ' + error);
            });
        }
    }

    doLogout() {
        if (this.adAuthentication) {
            this.clearSessionStorage();
           this.adalService.logout();
        } else {
            this.onPremAuthentication.logout();
            this.clearSessionStorage();
        }
    }

    clearSessionStorage() {
        this.dataStore.clearAll(); // Calling clear session from data store
        localStorage.setItem('logout', 'true');
        localStorage.removeItem('logout');
    }

    authenticateUserOnPrem(url, method, payload, headers) {

        return this.httpService.getHttpResponse(url, method, payload, {}, headers)
        .map(response => {
            return response;
        })
        .catch(error => {
            return Observable.throw(error.message || error);
        });
    }

    refreshToken() {
        // Write API code to refresh token
        try {

            const tokenObj = this.dataStore.getUserDetailsValue().getAuthToken();
            if (!tokenObj || !tokenObj.refresh_token) {
                return null;
            }

            return new Observable(observer => {
                const refreshToken = tokenObj.refresh_token;
                const url = environment.refresh.url;
                const method = environment.refresh.method;

                const payload = {
                    refreshToken: refreshToken
                };

                let userLoginDetails = JSON.parse(this.dataStore.getCurrentUserLoginDetails());
                this.commonResponseService.getData(url, method, payload, {}).subscribe(response => {
                    if (response && response.success && response.access_token) {
                        // Successful response
                        /* Response will have user info and access tokens. */
                        userLoginDetails = response;
                        this.dataStore.setCurrentUserLoginDetails(JSON.stringify(userLoginDetails));
                        observer.next(userLoginDetails.access_token);
                        observer.complete();
                    } else {
                        const errorMessage = response.message || 'Error renewing the access token';
                        this.logger.log('error ', errorMessage);
                        observer.error(null);
                    }
                },
                error => {
                    this.logger.log('info', '**Error renewing the access token**');
                    observer.error(null);
                });
            });
         } catch (error) {
            this.logger.log('error', 'JS Error - ' + error);
         }
    }

    getAuthToken() {
        /* Get the custom access token retuned from API */
        // Get the token object from data store and return access token

        let accessToken;

        const tokenObject = this.dataStore.getUserDetailsValue().getAuthToken();
        accessToken = tokenObject.access_token || null;
        return accessToken;
    }

    redirectPostLogin(defaultAssetGroup?) {
        const redirectUrl = this.redirectUrl;

        if (redirectUrl && redirectUrl !== '') {
            const redirect = this.utilService.getContextUrlExceptDomain(redirectUrl);

            if (redirect && this.redirectIsNotHomePage(redirect)) {
                this.router.navigateByUrl(redirect).then(result => {
                    this.loggerService.log('info', 'returnUrl navigated successfully');
                },
                error => {
                    this.loggerService.log('error', 'returnUrl - error in navigation - ' + error);
                });
            } else {
                this.redirectToPostLoginDefault(defaultAssetGroup);
            }
        } else {
            this.redirectToPostLoginDefault(defaultAssetGroup);
        }
    }

    private redirectToPostLoginDefault(defaultAssetGroup) {
        let url;
            if (!defaultAssetGroup || defaultAssetGroup === '') {
                url = '/pl/first-time-user-journey';
            } else {
                this.assetGroupObservableService.updateAssetGroup(defaultAssetGroup);
                url = '/pl/compliance/compliance-dashboard?ag=' + defaultAssetGroup;
            }
            this.router.navigateByUrl(url).then(result => {
                if ( result ) {
                    this.loggerService.log('info', 'Successful navigation to ' + url);
                } else {
                    this.loggerService.log('info', 'You are not authorised to access ' + url);
                }
            },
            error => {
                this.loggerService.log('error', 'Error while navigating - ' + error);
            });
    }

    get authenticated(): boolean {
      let authenticationStatus;

      // If adAuthentication is enabled for this app.
      if (this.adAuthentication) {
        authenticationStatus = this.adalService.userInfo ? this.adalService.userInfo.authenticated : false;
      } else { /* When on premise server authentication is enabled for application */
        authenticationStatus = this.onPremAuthentication.isAuthenticated();
      }
      return authenticationStatus;
    }

    get redirectUrl(): string {
        let redirectUrl = '';

        redirectUrl = this.dataStore.getRedirectUrl() || redirectUrl;
        return redirectUrl;
    }

    redirectIsNotHomePage(redirect) {
        return redirect !== '/home' && redirect !== '/home/login';
    }

     /* User informatin like user roles, user id */
     setUserFetchedInformation() {
         try {

            const idToken = this.adalService.getIdToken();
            const authToken = idToken;

            return new Observable(observer => {
                const url = environment.azureAuthorize.url;
                const method = environment.azureAuthorize.method;

                let headers: HttpHeaders = new HttpHeaders();
                headers = headers.set('Content-Type', 'application/json');
                headers = headers.set('Authorization', 'Bearer ' + authToken);

                const httpOptions = {
                    headers: headers
                };

                let userLoginDetails = JSON.parse(this.dataStore.getCurrentUserLoginDetails());
                this.commonResponseService.getData(url, method, {}, {}, httpOptions).subscribe(response => {
                    if (response && response.success) {
                        // Successful response
                        /* Response will have user info and access tokens. */
                        userLoginDetails = response;
                        this.dataStore.setCurrentUserLoginDetails(JSON.stringify(userLoginDetails));
                        this.dataStore.setUserDefaultAssetGroup(userLoginDetails.userInfo.defaultAssetGroup);

                        observer.next('success');
                        observer.complete();
                    } else {
                        const errorMessage = response.message || 'Error authenticating the id_token';
                        this.logger.log('error ', errorMessage);
                        userLoginDetails.userInfo.defaultAssetGroup = 'aws-all';
                        this.dataStore.setCurrentUserLoginDetails(JSON.stringify(userLoginDetails));
                        this.dataStore.setUserDefaultAssetGroup(userLoginDetails.userInfo.defaultAssetGroup);
                        observer.error(errorMessage);
                    }
                },
                error => {
                    this.logger.log('info', '**Error fetching the user roles from backend**');
                    userLoginDetails.userInfo.defaultAssetGroup = 'aws-all';

                    this.dataStore.setCurrentUserLoginDetails(JSON.stringify(userLoginDetails));
                    this.dataStore.setUserDefaultAssetGroup(userLoginDetails.userInfo.defaultAssetGroup);
                    observer.error('error');
                });
            });
         } catch (error) {
            this.logger.log('error', 'JS Error - ' + error);
         }
    }
}
