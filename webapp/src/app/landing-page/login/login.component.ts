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

import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {fadeInOut} from './../common/animations/animations';
import {OnPremAuthenticationService} from '../../core/services/onprem-authentication.service';
import {Router} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';
import {environment} from '../../../environments/environment';
import {UtilsService} from '../../shared/services/utils.service';
import { AssetTilesService } from '../../core/services/asset-tiles.service';
import { DataCacheService } from '../../core/services/data-cache.service';
import {DomainTypeObservableService} from '../../core/services/domain-type-observable.service';
import { CONTENT } from './../../../config/static-content';
import { AuthService } from '../../core/services/auth.service';

import { CONFIGURATIONS } from './../../../config/configurations';
import { AdalService } from '../../core/services/adal.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css'],
    animations: [fadeInOut],
    providers: []
})
export class LoginComponent implements OnInit {
    CLIENT_ID = 'pacman2_api_client';
    username: string;
    password: string;
    throwError = false;
    showErrorMessage = 'hide';
    loginPending = false;
    subscriptionToAssetGroup: Subscription;
    content;
    showOnPremLogin = false;


    constructor(private authenticationService: OnPremAuthenticationService,
                private authService: AuthService,
                private router: Router,
                private utilityService: UtilsService,
                private adalService: AdalService,
                private onPremAuthentication: OnPremAuthenticationService) {

                    if (CONFIGURATIONS.optional.auth.AUTH_TYPE === 'azuresso') {
                        this.adalService.login();
                    } else {
                        this.showOnPremLogin = true;
                    }

                    this.content = CONTENT;
    }

    @Input() menuState: string;
    @Output() onClose = new EventEmitter();

    ngOnInit() {
    }

    login() {
        if (!this.username || !this.password) {
            this.throwLoginError();
        }else {
            this.loginPending = true;
            const payload = {
                clientId: this.CLIENT_ID,
                username: this.authenticationService.formatUsernameWithoutDomain(this.username),
                password: this.password
            };

            this.authService.authenticateUserOnPrem(
                environment.login.url,
                environment.login.method,
                payload, {} )
                .subscribe(
                    result => {
                        this.loginPending = false;
                        if (result.success) {
                            this.takeActionPostLogin(result);
                        } else {
                            this.throwLoginError();
                        }
                    },
                    error => {
                        this.loginPending = false;
                        this.throwLoginError();
                    }
                );
        }
    }

    closeSidemenu($event?) {
        this.router.navigate(['/home']);
    }

    resetError() {
        this.showErrorMessage = 'hide';
        this.throwError = false;
    }

    throwLoginError() {
        this.showErrorMessage = 'show';
        this.throwError = true;
    }

    takeActionPostLogin(fetchedResult) {
        // Save user Details in cache
        this.onPremAuthentication.massageAndStoreUserDetails(fetchedResult);
        // Post saving user details to cache. Redirect user to dashboard.
        let defaultAssetGroup = null;
        if (fetchedResult && fetchedResult.userInfo) {
            defaultAssetGroup = fetchedResult.userInfo.defaultAssetGroup;
        }
        this.authService.redirectPostLogin(defaultAssetGroup);
    }
}
