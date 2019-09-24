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

/* Created by Puneet Baser 20/11/2017 */

import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, ActivatedRoute, Router} from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LoggerService } from './logger.service';
import { DataCacheService } from '../../core/services/data-cache.service';
import { CONFIGURATIONS } from '../../../config/configurations';

@Injectable()
export class AuthGuardService implements CanActivate {
    constructor(
                private authService: AuthService,
                private loggerService: LoggerService,
                private dataStore: DataCacheService,
                private router: Router,
                private activatedRoute: ActivatedRoute) {}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

        /* Full url is taken, as redirect function will remove the hostname and just add the pathname */
        const returnUrl = location.href;
        if (!this.authService.authenticated) {
            this.loggerService.log('info', 'AuthGuard - Authentication required to access this page');
            // Store the redirect url in cache
            this.dataStore.setRedirectUrl(returnUrl);

            // Redirect to login
            this.authService.doLogin();
            return false;
        }
        this.loggerService.log('info', 'AuthGuard - User authenticated, user is granted access for this page');
        const checkUserRoles = this.dataStore.getUserDetailsValue().getRoles();
        if (checkUserRoles.length <= 0) {
            this.authService.setUserFetchedInformation().subscribe(response => {
                this.loggerService.log('info', '**Successfully set user Fetched information**');
            },
            error => {
                this.loggerService.log('info', '**Error in setting user Fetched information**');
            });
        }

        if (state.url.includes('vulnerabilities-compliance') && !CONFIGURATIONS.optional.general.qualysEnabled) {
            this.loggerService.log('info', 'Qualys required to access this page');
            this.router.navigate(
                ['../../../../compliance/compliance-dashboard'],
                { relativeTo: this.activatedRoute, queryParamsHandling: 'merge' }
              );
            return false;
        }
        return true;
    }
}
