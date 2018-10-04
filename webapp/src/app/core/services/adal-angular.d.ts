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

// Typings needed for using ADAL with Angular 4
declare module 'adal-angular' {
    export function inject(config: adal.Config): adal.AuthenticationContext;

    export class adal{
        
    }
}

declare var AuthenticationContext: adal.AuthenticationContextStatic;
declare var Logging: adal.Logging;

declare namespace adal {

    /**
     * 
     * 
     * @interface Config
     */
    interface Config {
        tenant?: string;
        clientId: string;
        redirectUri?: string;
        instance?: string;
        endpoints?: any;  // If you need to send CORS api requests.
        popUp?: boolean;
        localLoginUrl?: string;
        displayCall?: (urlNavigate: string) => any;
        postLogoutRedirectUri?: string; // redirect url after succesful logout operation
        cacheLocation?: string;
        anonymousEndpoints?: string[];
        expireOffsetSeconds?: number;
        correlationId?: string;
        loginResource?: string;
        resource?: string;
        extraQueryParameter?: string;
        navigateToLoginRequestUrl?: boolean;
    }

    /**
     * 
     * 
     * @interface User
     */
    interface User {
        userName: string;
        profile: any;
        authenticated: any;
        error: any;
        token: any;
    }

    /**
     * 
     * 
     * @interface RequestInfo
     */
    interface RequestInfo {
        valid: boolean;
        parameters: any;
        stateMatch: boolean;
        stateResponse: string;
        requestType: string;
    }

    /**
     * 
     * 
     * @interface Logging
     */
    interface Logging {
        log: (message: string) => void;
        level: LoggingLevel;
    }

    /**
     * 
     * 
     * @enum {number}
     */
    enum LoggingLevel {
        ERROR = 0,
        WARNING = 1,
        INFO = 2,
        VERBOSE = 3
    }

    /**
     * 
     * 
     * @interface AuthenticationContextStatic
     */
    interface AuthenticationContextStatic {
        new (config: Config): AuthenticationContext;
    }

    /**
     * 
     * 
     * @interface AuthenticationContext
     */
    interface AuthenticationContext {

        // Additional items for Angular 4
        CONSTANTS: any;

        REQUEST_TYPE: {
            LOGIN: string,
            RENEW_TOKEN: string,
            UNKNOWN: string
        };

        // Methods
        callback: any;

        _getItem: any;

        _renewFailed: any;

        // Original ADAL Types
        instance: string;
        config: Config;

        /**
         * Gets initial Idtoken for the app backend
         * Saves the resulting Idtoken in localStorage.
         */
        login(): void;

        /**
         * Indicates whether login is in progress now or not.
         */
        loginInProgress(): boolean;

        /**
         * Gets token for the specified resource from local storage cache
         * @param {string}   resource A URI that identifies the resource for which the token is valid.
         * @returns {string} token if exists and not expired or null
         */
        getCachedToken(resource: string): string;

        /**
         * Retrieves and parse idToken from localstorage
         * @returns {User} user object
         */
        getCachedUser(): User;

        registerCallback(expectedState: string, resource: string, callback: (message: string, token: string) => any): void;

        /**
         * Acquire token from cache if not expired and available. Acquires token from iframe if expired.
         * @param {string}   resource  ResourceUri identifying the target resource
         * @param {requestCallback} callback
         */
        acquireToken(resource: string, callback: (message: string, token: string) => any): void;

        /**
         * Redirect the Browser to Azure AD Authorization endpoint
         * @param {string}   urlNavigate The authorization request url
         */
        promptUser(urlNavigate: string): void;

        /**
         * Clear cache items.
         */
        clearCache(): void;

        /**
         * Clear cache items for a resource.
         */
        clearCacheForResource(resource: string): void;

        /**
         * Logout user will redirect page to logout endpoint.
         * After logout, it will redirect to post_logout page if provided.
         */
        logOut(): void;

        /**
         * Gets a user profile
         * @param {requestCallback} callback - The callback that handles the response.
         */
        getUser(callback: (message: string, user?: User) => any): void;

        /**
         * Checks if hash contains access token or id token or error_description
         * @param {string} hash  -  Hash passed from redirect page
         * @returns {Boolean}
         */
        isCallback(hash: string): boolean;

        /**
         * Gets login error
         * @returns {string} error message related to login
         */
        getLoginError(): string;

        /**
         * Gets requestInfo from given hash.
         * @returns {RequestInfo} for appropriate hash.
         */
        getRequestInfo(hash: string): RequestInfo;

        /**
         * Saves token from hash that is received from redirect.
         */
        saveTokenFromHash(requestInfo: RequestInfo): void;

        /**
         * Gets resource for given endpoint if mapping is provided with config.
         * @param {string} endpoint  -  API endpoint
         * @returns {string} resource for this API endpoint
         */
        getResourceForEndpoint(endpoint: string): string;

        /**
         * Handles redirection after login operation. 
         * Gets access token from url and saves token to the (local/session) storage
         * or saves error in case unsuccessful login.
         */
        handleWindowCallback(): void;

        log(level: number, message: string, error: any): void;
        error(message: string, error: any): void;
        warn(message: string): void;
        info(message: string): void;
        verbose(message: string): void;
    }

}

/**
 * 
 * 
 * @interface Window
 */
interface Window {
    AuthenticationContext: any;
    callBackMappedToRenewStates: any;
}