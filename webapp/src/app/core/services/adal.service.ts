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

/// <reference path="adal-angular.d.ts" />
import { Injectable } from '@angular/core';
import { Observable} from 'rxjs/Observable';
import * as lib from 'adal-angular';
import { DataCacheService } from './data-cache.service';
import { AuthSessionStorageService } from './auth-session-storage.service';

@Injectable()
export class AdalService {

    private context: adal.AuthenticationContext = <any>null;

    private user: adal.User = {
        authenticated: false,
        userName: '',
        error: '',
        token: '',
        profile: {}
    };

    constructor(private dataStore: DataCacheService,
                private authSessionStorage: AuthSessionStorageService) { }

    public init(configOptions: adal.Config) {
        if (!configOptions) {
            throw new Error('You must set config, when calling init.');
        }

        // redirect and logout_redirect are set to current location by default
        const existingHash = window.location.hash;
        // default path is always set to origin.
        let pathDefault = window.location.origin;
        if (existingHash) {
            pathDefault = pathDefault.replace(existingHash, '');
        }

        configOptions.redirectUri = configOptions.redirectUri || pathDefault;
        configOptions.postLogoutRedirectUri = configOptions.postLogoutRedirectUri || pathDefault;

        // create instance with given config
        this.context = lib.inject(configOptions);

        window.AuthenticationContext = this.context.constructor;

        // loginresource is used to set authenticated status
        this.updateDataFromCache(<any>this.context.config.loginResource);
    }

    public get config(): adal.Config {
        return this.context.config;
    }

    public get userInfo(): adal.User {
        return this.user;
    }

    public login(): void {
        this.context.login();
    }

    public loginInProgress(): boolean {
        return this.context.loginInProgress();
    }

    public logout(): void {
        this.context.logOut();
    }

    public handleWindowCallback(): void {
        const hash = window.location.hash;
        if (this.context.isCallback(hash)) {
            const requestInfo = this.context.getRequestInfo(hash);
            this.context.saveTokenFromHash(requestInfo);
            if (requestInfo.requestType === this.context.REQUEST_TYPE.LOGIN) {
                this.updateDataFromCache(<any>this.context.config.loginResource);

            } else if (requestInfo.requestType === this.context.REQUEST_TYPE.RENEW_TOKEN) {
                this.context.callback = window.parent.callBackMappedToRenewStates[requestInfo.stateResponse];
            }

            if (requestInfo.stateMatch) {
                if (typeof this.context.callback === 'function') {
                    if (requestInfo.requestType === this.context.REQUEST_TYPE.RENEW_TOKEN) {
                        // Idtoken or Accestoken can be renewed
                        if (requestInfo.parameters['access_token']) {
                            this.context.callback(this.context._getItem(this.context.CONSTANTS.STORAGE.ERROR_DESCRIPTION)
                                , requestInfo.parameters['access_token']);
                        } else if (requestInfo.parameters['id_token']) {
                            this.context.callback(this.context._getItem(this.context.CONSTANTS.STORAGE.ERROR_DESCRIPTION)
                                , requestInfo.parameters['id_token']);
                        } else if (requestInfo.parameters['error']) {
                            this.context.callback(this.context._getItem(this.context.CONSTANTS.STORAGE.ERROR_DESCRIPTION), null);
                            this.context._renewFailed = true;
                        }
                    }
                }
            }
        }

        // Remove hash from url
        if (window.location.hash) {
            if (window.history.replaceState) {
                window.history.replaceState('', '/', window.location.pathname);
            } else {
                window.location.hash = '';
            }
        }
    }

    public getCachedToken(resource: string): string {
        
        return this.context.getCachedToken(resource);
    }

    public acquireToken(resource: string) {
        const _this = this;   // save outer this for inner function
        let errorMessage: string;
        return Observable.bindCallback(acquireTokenInternal, function (token: string) {
            if (!token && errorMessage) {
                throw (errorMessage);
            }
            return token;
        })();

        function acquireTokenInternal(cb: any) {
            let s: any = null;

            _this.context.acquireToken(resource, (error: string, tokenOut: string) => {
                if (error) {
                    _this.context.error('Error when acquiring token for resource: ' + resource, error);
                    errorMessage = error;
                    cb(<any>null);
                } else {
                    cb(tokenOut);
                    s = tokenOut;
                }
            });
            return s;
        }
    }

    public getUser(): Observable<any> {
        return Observable.bindCallback((cb: any) => {
            this.context.getUser(function (error: string, user: any) {
                if (error) {
                    this.context.error('Error when getting user', error);
                    cb(null);
                } else {
                    cb(user);
                }
            });
        })();
    }

    public clearCache(): void {
        this.context.clearCache();
    }

    public clearCacheForResource(resource: string): void {
        this.context.clearCacheForResource(resource);
    }

    public info(message: string): void {
        this.context.info(message);
    }

    public verbose(message: string): void {
        this.context.verbose(message);
    }

    public GetResourceForEndpoint(url: string): string {
        return this.context.getResourceForEndpoint(url);
    }

    public refreshDataFromCache() {
        this.updateDataFromCache(<any>this.context.config.loginResource);
    }

    private updateDataFromCache(resource: string): void {
        const token = this.context.getCachedToken(resource);
        this.user.authenticated = token !== null && token.length > 0;
        const user = this.context.getCachedUser() || { userName: '', profile: <any>undefined };
        if (user) {
            this.user.userName = user.userName;
            this.user.profile = user.profile;
            this.user.token = token;
            this.user.error = this.context.getLoginError();

            this.updateSessionStorage(this.user);

        } else {
            this.user.userName = '';
            this.user.profile = {};
            this.user.token = '';
            this.user.error = '';
        }
    }

    private updateSessionStorage(user): void {

        let modifiedUserInfo;
        const userLoginDetails = this.dataStore.getCurrentUserLoginDetails();
        if (!userLoginDetails) {

            modifiedUserInfo = {
                'userInfo': {
                    'firstName': '',
                    'lastName': '',
                    'userRoles': [],
                    'defaultAssetGroup': '',
                    'userName': '',
                    'userId': '',
                    'email': ''
                }
            };
        } else {
            modifiedUserInfo = JSON.parse(userLoginDetails);
        }

        if (this.user.profile) {
            modifiedUserInfo.userInfo.firstName = this.user.profile.given_name || 'Guest';
            modifiedUserInfo.userInfo.lastName = this.user.profile.family_name || modifiedUserInfo.userInfo.lastName;
            modifiedUserInfo.userInfo.userName = this.user.userName || modifiedUserInfo.userInfo.userName;
            modifiedUserInfo.userInfo.email = this.user.userName || modifiedUserInfo.userInfo.email;
        }

        this.authSessionStorage.saveUserDetails(modifiedUserInfo);
    }

    /* Deprecated: this function is not being used anymore */
    public getLoginRequestEndPoint() {
        return this.context._getItem(this.context.CONSTANTS.STORAGE.LOGIN_REQUEST);
    }

    public getIdToken() {
        return this.context._getItem(this.context.CONSTANTS.STORAGE.IDTOKEN);
    }
}
