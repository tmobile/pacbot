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

import { Component, OnInit, NgZone } from '@angular/core';
import { UtilsService } from '../../shared/services/utils.service';
import { loginRouterTransition } from './../common/animations/animations';
import { DataCacheService } from '../../core/services/data-cache.service';
import { LoggerService } from '../../shared/services/logger.service';
import { CONTENT } from './../../../config/static-content';
import { AdalService } from './../../core/services/adal.service';
import { AuthService } from '../../core/services/auth.service';
import { environment } from '../../../environments/environment';
import { CommonResponseService } from '../../shared/services/common-response.service';
import { CONFIGURATIONS } from '../../../config/configurations';

@Component({
    selector: 'app-home-page',
    templateUrl: './home-page.component.html',
    styleUrls: ['./home-page.component.css'],
    providers: [UtilsService, DataCacheService],
    animations: [loginRouterTransition]
})
export class HomePageComponent implements OnInit {
    public sidemenuState = 'out';
    connectline: any = 3;
    lastScrollTop: any = 0;
    connectlineheight: any = 1;
    transYvalue: any = -90;
    transXvalue: any = -50;
    public animateVisible: false;
    public animateOutView: false;
    public startAnimation: any = 'false';
    public ciecle1StartTime: 5;
    public ciecle2StartTime: 8;
    public showHeader = true;
    public currentYear;
    public content;
    public roleAndDefaultAssetGroupLoaded = true;

    constructor(private ngZone: NgZone,
                private dataStore: DataCacheService,
                private logger: LoggerService,
                private adalService: AdalService,
                private authService: AuthService,
                private commonResponseService: CommonResponseService) {

        this.content = CONTENT;

        window.onload = (e) => {
            this.ngZone.run(() => {
                this.connectlineheight = 1.2;
                this.connectline = 4;
            });
        };
    }

    ngOnInit() {
        try {

            this.currentYear = (new Date()).getFullYear();
            /* Will not redirect user to post login automatically. User will be redirected when he clicks on Go to dashboard */
            this.redirectLoggedinUser();
        }catch (error) {
            this.logger.log('error', error);
        }
    }

    // -------- page animaiton on scroll
    onScroll(event): void {
        const scroll = document.getElementsByClassName('mr-wrapper')[0].scrollTop;
        // -------- animation for scroll main pacman text
        if (scroll === 0) {
            this.connectlineheight = 1.2;
            this.connectline = 4;
        } else if (scroll > 0) {
            this.connectlineheight = 1;
            this.connectline = 3;
        }
        // ---------- animation for header
        if (scroll > this.lastScrollTop) {
            this.showHeader = false;
        } else if (scroll < this.lastScrollTop) {
            this.showHeader = true;
        }
        this.lastScrollTop = scroll;
    }

    // -------- on scroll animation end here
    redirectLoggedinUser() {
        this.logger.log('info', '**Redirection triggered from Home page**');
        this.logger.log('info', '**Home page redirection - To check if user is authenticated**');
        if (this.authService.authenticated) {
            this.logger.log('info', '**Home page redirection - User is authenticated to proceed with post login section**');
            // If user is already logged in
            const userDefaultAssetGroup = this.dataStore.getUserDefaultAssetGroup();
            const userRoles = this.dataStore.getUserDetailsValue().getRoles();

               // if (!userDefaultAssetGroup || userDefaultAssetGroup === null) {
               /* This step is valid only for azure sso type of authentication. */
               if (CONFIGURATIONS.optional.auth.AUTH_TYPE === 'azuresso') {

                this.logger.log('info', '**Fetching users default asset group and roles**');
                // Get information when default asset group is not set
                this.roleAndDefaultAssetGroupLoaded = false;

                this.authService.setUserFetchedInformation().subscribe(response => {
                    this.logger.log('info', '**Successfully set user Fetched information**');
                    this.authService.redirectPostLogin(this.dataStore.getUserDefaultAssetGroup());
                },
                error => {
                    this.logger.log('info', '**Error in setting user Fetched information**');
                    this.authService.redirectPostLogin(this.dataStore.getUserDefaultAssetGroup());
                });
            } else {
                // Redirect when default asset group is already set
                this.authService.redirectPostLogin(userDefaultAssetGroup);
            }
        } else {
            this.logger.log('info', '**Home page redirection - user is not authenticated to move to post login**');
        }
    }

    // ------- login section routing
    openLoginMenu() {
        // Auth service will decide the method of login.
        this.authService.doLogin();
    }

    isLoginState(outlet) {
        const url = window.location.href;
        return url.indexOf('/home/login') >= 0;
    }

    goToSlackLink(link) {
        window.open(link, '_blank');
    }

    get authenticated(): boolean {
        return this.adalService.userInfo.authenticated;
    }
}


