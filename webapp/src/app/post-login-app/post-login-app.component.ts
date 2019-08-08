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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { moduleTransition } from './common/animations/animations';
import {
  ActivatedRoute,
  Router
} from '@angular/router';
import { AssetGroupObservableService } from '../core/services/asset-group-observable.service';
import { Subscription } from 'rxjs/Subscription';
import { MainRoutingAnimationEventService } from '../shared/services/main-routing-animation-event.service';
import { LoggerService } from '../shared/services/logger.service';
import { DataCacheService } from '../core/services/data-cache.service';
import { DownloadService } from '../shared/services/download.service';
import { DomainTypeObservableService } from '../core/services/domain-type-observable.service';
import { ThemeObservableService } from '../core/services/theme-observable.service';
import { WorkflowService } from '../core/services/workflow.service';

declare var Offline: any;

@Component({
  selector: 'app-post-login-app',
  templateUrl: './post-login-app.component.html',
  styleUrls: ['./post-login-app.component.css'],
  animations: [moduleTransition()]
})
export class PostLoginAppComponent implements OnInit, OnDestroy {
  navigationDetails: any;
  domainList: string;
  queryParameters: any = {};
  private agAndDomainKey: string;
  showPacLoader: any = [];

  private themeSubscription: Subscription;
  private activatedRouteSubscription: Subscription;
  private downloadSubscription: Subscription;
  private previousRouteSequence;
  public theme;
  private pageReloadInterval; // Default time is 30 minutes in miliseconds
  private reloadTimeout;
  isOffline = false;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private assetGroupObservableService: AssetGroupObservableService,
    private dataStore: DataCacheService,
    private logger: LoggerService,
    private workflowService: WorkflowService,
    private mainRoutingAnimationEventService: MainRoutingAnimationEventService,
    private downloadService: DownloadService,
    private domainTypeObservableService: DomainTypeObservableService,
    private themeObservableService: ThemeObservableService
  ) {

    if (this.pageReloadInterval) {
      this.reloadTimeout = this.setReloadTimeOut(this.pageReloadInterval);
    }

    this.getRouteQueryParameters();
  }

  ngOnInit() {
    try {
      this.agAndDomainKey = '';

      this.navigationDetails = {
        compliance: {
          image: '/assets/icons/compliance.svg',
          activeImage: '/assets/icons/compliance-active.svg',
          route: 'compliance',
          data: {
            sequence: 1
          }
        },
        assets: {
          image: '/assets/icons/assets.svg',
          activeImage: '/assets/icons/assets-active.svg',
          route: 'assets',
          data: {
            sequence: 2
          }
        },
        tools: {
          image: '/assets/icons/tools.svg',
          activeImage: '/assets/icons/tools-active.svg',
          route: 'tools',
          data: {
            sequence: 3
          }
        },
        omnisearch: {
          image: '/assets/icons/omni-search.svg',
          activeImage: '/assets/icons/omni-search-active.svg',
          route: 'omnisearch',
          data: {
            sequence: 4
          }
        },
        admin: {
          image: '/assets/icons/admin.svg',
          activeImage: '/assets/icons/admin-active.svg',
          route: 'admin',
          data: {
            sequence: 5
          }
        }
      };


      Offline.on('down',
        () => {
          this.logger.log('info', 'Connection lost :-(');
          this.isOffline = true;
        },
        this
      );

      Offline.on('up',
        () => {
          this.logger.log('info', 'Connection was lost, It is back now');
          this.isOffline = false;
          location.reload();
        },
        this
      );

      this.downloadSubscription = this.downloadService
        .getDownloadStatus()
        .subscribe(val => {
          if (val) {
            this.showPacLoader.push('downloading');
          } else {
            this.showPacLoader.pop();
          }
        });

      this.themeSubscription = this.themeObservableService.getTheme().subscribe(theme => {
        this.theme = theme;
      });
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  navigateBackToRootLevel() {
    this.dataStore.set(
      'selectedApplicationSummary',
      JSON.stringify(null)
    );
    this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root, 0);
    const navigationParams = {
      relativeTo: this.activatedRoute // <-- Parent activated route
    };

    navigationParams['queryParams'] = { 'ag': this.queryParameters['ag'], 'domain': this.queryParameters['domain'] };

    this.router.navigate(
      [
        {
          outlets: {
            details: null
          }
        }
      ],
      navigationParams
    );
    this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root, 0);
    this.workflowService.clearAllLevels();
  }


  setReloadTimeOut(timeoutInterval) {
    this.logger.log('info', 'Setting the page reload interval to: ' + timeoutInterval);
    const reloadTimeout = setTimeout(function(){
      window.location.reload(true);
    }, timeoutInterval);

    return reloadTimeout;
  }

  getState(outlet) {
    return outlet.activatedRouteData.sequence;
  }

  updateAssetGroup(assetGroupName) {
    if (assetGroupName) {
      this.assetGroupObservableService.updateAssetGroup(assetGroupName);
    } else {
      this.router.navigate(['/post-login', {outlets: { modal: ['change-default-asset-group'] } }]);
    }
  }

  updateDomainName(domainName) {
    const currentAssetGroup = this.dataStore.getCurrentSelectedAssetGroup();
    if (domainName) {
      this.domainTypeObservableService.updateDomainType(domainName, currentAssetGroup);
    }
  }

  getRouteQueryParameters(): any {
    this.activatedRouteSubscription = this.activatedRoute.queryParams.subscribe(
      params => {
        if (params['ag'] || params['domain']) {

          const newKey = params['ag'] + params['domain'];

          if (newKey === this.agAndDomainKey) {
            return false;
          }

          this.queryParameters = params;
          this.agAndDomainKey = newKey;

          this.updateAssetGroup(this.queryParameters['ag']);
          this.updateDomainName(this.queryParameters['domain']);
        }
        /* User will enter it in minutes */
        if (params['reload']) {
          this.pageReloadInterval = params['reload'] * 60000;
          if (this.reloadTimeout) {
            clearTimeout(this.reloadTimeout);
            this.reloadTimeout = this.setReloadTimeOut(this.pageReloadInterval);
          }
        }
      }
    );
  }

  routerTransitionStart() {
    this.mainRoutingAnimationEventService.updateAnimationStatus(false);
  }

  routerTransitionDone() {
    this.mainRoutingAnimationEventService.updateAnimationStatus(true);
  }

  onDeactivate(event) {
    try {
      event.activatedRoute.data.subscribe(data => {
        if ( data) {
          this.previousRouteSequence = data.sequence;
        }
      });

    } catch (e) {

    }
  }

  ngOnDestroy() {
    try {
      this.activatedRouteSubscription.unsubscribe();
    } catch (error) {
      this.logger.log('error', error);
    }
  }
}
