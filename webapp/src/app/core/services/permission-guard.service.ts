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
import {Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {DataCacheService} from './data-cache.service';
import * as _ from 'lodash';

@Injectable()
export class PermissionGuardService implements CanActivate {
    constructor(private dataCacheService: DataCacheService,
                private router: Router) {}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

        // this will be passed from the route config
        const urlPermissions = route.data.roles;
        const userRoles = this.dataCacheService.getUserDetailsValue().getRoles();

        const canUserAccess = this.checkUserPermissionToAccessThisUrl(urlPermissions, userRoles);

        if (!canUserAccess) {
            this.router.navigate(['/home']);
            return false;
        }
        return true;
    }

    checkUserPermissionToAccessThisUrl(urlPermissions, userRoles) {
        return !(_.difference(urlPermissions, userRoles).length );
    }

    checkAdminPermission() {
        const userDetailsRoles = this.dataCacheService.getUserDetailsValue().getRoles();
        const adminAccess = userDetailsRoles.includes('ROLE_ADMIN');
        return adminAccess;
    }

    checkOnPremAdminPermission() {
        const userDetailsRoles = this.dataCacheService.getUserDetailsValue().getRoles();
        const adminAccess = userDetailsRoles.includes('ROLE_ONPREM_ADMIN');
        return adminAccess;
    }
}
