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

import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, ActivatedRouteSnapshot, NavigationEnd, Router} from '@angular/router';
import {LoggerService} from './shared/services/logger.service';
import {DataCacheService} from './core/services/data-cache.service';
import {RouterUtilityService} from './shared/services/router-utility.service';
import { AdalService } from './core/services/adal.service';
import { CONFIGURATIONS } from './../config/configurations';

declare var gtag: Function;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';

  constructor(private activatedRoute: ActivatedRoute,
              private router: Router,
              private loggerService: LoggerService,
              private dataStore: DataCacheService,
              private routerService: RouterUtilityService,
              private adalService: AdalService) {

              if (CONFIGURATIONS.optional.auth.AUTH_TYPE === 'azuresso') {
                adalService.init(CONFIGURATIONS.optional.auth.adConfig);
              }
  }

  private getDeepestTitle(routeSnapshot: ActivatedRouteSnapshot) {
    let title = routeSnapshot.data ? routeSnapshot.data['title'] : '';
    if (routeSnapshot.firstChild) {
      title = this.getDeepestTitle(routeSnapshot.firstChild) || title;
    }
    return title;
  }

  private getDeepestUrl(routeSnapshot: ActivatedRouteSnapshot) {
    let url = routeSnapshot.url;
    if (routeSnapshot.firstChild) {
      url = this.getDeepestUrl(routeSnapshot.firstChild) || url;
    }
    return url;
  }

  ngOnInit() {

    if (CONFIGURATIONS.optional.auth.AUTH_TYPE === 'azuresso') {
      this.adalService.handleWindowCallback();
    }

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {

        const hashedUserID = this.dataStore.getHashedIdOfUser();

        const title = this.routerService.getDeepestPageTitle(this.router.routerState.snapshot.root);
        const url = this.routerService.getDeepestPageUrl(this.router.routerState.snapshot.root);
        /* Add page title and active page to google analytics */
        gtag('set', {'userId' : hashedUserID});
        gtag('config', 'UA-112530781-1', {'page_title': title, 'active_page': url});
      }
    });
  }
}
