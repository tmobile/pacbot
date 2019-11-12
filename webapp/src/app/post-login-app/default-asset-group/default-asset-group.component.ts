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

import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { DataCacheService } from '../../core/services/data-cache.service';
import { AssetGroupObservableService } from '../../core/services/asset-group-observable.service';
import { LoggerService } from '../../shared/services/logger.service';
import { AssetTilesService } from '../../core/services/asset-tiles.service';
import { environment } from './../../../environments/environment';
import { DomainTypeObservableService } from '../../core/services/domain-type-observable.service';
import {ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import {RouterUtilityService} from '../../shared/services/router-utility.service';
import {Subscription} from 'rxjs/Subscription';
import {WorkflowService} from '../../core/services/workflow.service';

@Component({
  selector: 'app-default-asset-group',
  templateUrl: './default-asset-group.component.html',
  styleUrls: ['./default-asset-group.component.css'],
  providers: [DataCacheService]
})

export class DefaultAssetGroupComponent implements OnInit, OnDestroy {

  constructor(private dataStore: DataCacheService,
              private assetGroupObservableService: AssetGroupObservableService,
              private logger: LoggerService,
              private assetTileService: AssetTilesService,
              private domainTypeObservableService: DomainTypeObservableService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              private routingUtilityService: RouterUtilityService,
              private workflowService: WorkflowService) { }

  clicked = false;

  @Input() defaultAssetGroup: string;
  @Input() provider = [];
  @Input() dataLoaded;
  private assetGroupSubscription: Subscription;
  private domainSubscription: Subscription;

  public assetGroupDisplayName = '';
  private assetGroupName;
  private assetGroupList;
  private domainName: string;
  ngOnInit() {
    try {
      this.subscribeToAssetGroupChange();
      this.subscribeToDomainChange();

    } catch (error) {
      this.logger.log('error', error);
    }
  }

  openOverlay(): void {
    this.clicked = true;
  }

  closeOverlay() {
    this.clicked = false;
  }

  subscribeToAssetGroupChange() {

    this.assetGroupName = this.dataStore.getCurrentSelectedAssetGroup();
    if (this.assetGroupName) {
      const navigationExtras: NavigationExtras = {
        queryParams: {
          'ag': this.assetGroupName
        },
        queryParamsHandling: 'merge'
      };
      this.router.navigate([], navigationExtras);
    }

    this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(assGroupName => {
      if (assGroupName) {
        this.assetGroupName = assGroupName;
        this.getAssetGroupDisplayName(this.assetGroupName);
      }
    });
  }

  subscribeToDomainChange() {
    this.domainSubscription = this.domainTypeObservableService.getDomainType().subscribe(domain => {
      this.domainName = domain;
    });
  }

  getAssetGroupDisplayName(assetGroupName) {

    /* To check if asset group list is available in cache */
    const assetGroupList = this.dataStore.getListOfAssetGroups();

    if (!assetGroupList) {

      /* Asset group list not available, getting the list */

      const assetUrl = environment.assetTiles.url;
      const assetMethod = environment.assetTiles.method;

      this.assetTileService.getAssetTiles(assetUrl, assetMethod).subscribe(
          response => {
            this.assetGroupList = response[0];
            if (this.assetGroupList) {
              /* Store the list in stringify format */
              this.dataStore.setListOfAssetGroups(JSON.stringify(this.assetGroupList));
              this.fetchAssetGroupDisplayName(assetGroupName);
            }
          },
          error => {
            this.logger.log('error', error);
          }
      );
    } else {
      /* If list is availbe then get asset group display name, domain list from a matchign asset group name */
      this.assetGroupList = JSON.parse(assetGroupList);
      this.fetchAssetGroupDisplayName(assetGroupName);
    }
  }

  fetchAssetGroupDisplayName(assetGroupName) {
    if (this.assetGroupList) {
      let allAssetGroups;
      if (typeof(this.assetGroupList) === 'string') {
        allAssetGroups = JSON.parse(this.assetGroupList);
      } else {
        allAssetGroups = this.assetGroupList;
      }

      let isAgPresent = false;

      const filteredArray = allAssetGroups.filter(element => element.name === assetGroupName);

      if (filteredArray.length) {
        isAgPresent = true;
        const assetGroupObject = filteredArray[0];
        this.assetGroupDisplayName = assetGroupObject.displayname;
        this.getUpdatedDomain(assetGroupName, assetGroupObject);
      }

      if (!isAgPresent) {
        this.router.navigate(['/pl', {outlets: { modal: ['change-default-asset-group'] } }]);
      }
    }
  }

  getUpdatedDomain(assetGroupName, assetGroupObject) {

    if (assetGroupObject.domains && assetGroupObject.domains.length > 0 ) {
      this.domainTypeObservableService.updateListOfDomains(assetGroupObject.domains.join('~'));

      const newDomain = this.dataStore.getCurrentSelectedDomain(assetGroupName) ? this.dataStore.getCurrentSelectedDomain(assetGroupName) : assetGroupObject.domains[0];

      const deepestUrl = this.routingUtilityService.getJointDeepestPageUrl(this.activatedRoute.snapshot);

      const isActiveUrlACommonModulePage = this.routingUtilityService.checkIfCurrentRouteBelongsToCommonPages(deepestUrl);

      const currentModule = this.routingUtilityService.getModuleNameFromCurrentRoute(this.activatedRoute.snapshot);

      const landingPageUrl = this.routingUtilityService.getLandingPageInAModule(currentModule);

      const navigationExtras: NavigationExtras = {
        queryParams: {
          'domain': newDomain
        },
        queryParamsHandling: 'merge'
      };

      if (!isActiveUrlACommonModulePage && this.domainName && landingPageUrl && newDomain !== this.domainName) {
        /* Clears the saved url from module as different module may not have that page */
        this.workflowService.clearDataOfOpenedPageInModule();
        this.router.navigate([landingPageUrl], navigationExtras);
      } else {
        this.router.navigate([], navigationExtras);
      }
    } else {
      this.domainTypeObservableService.updateListOfDomains('');
      const navigationExtras: NavigationExtras = {
        queryParams: {
          'domain': ''
        },
        queryParamsHandling: 'merge'
      };

      this.router.navigate([], navigationExtras);
    }
  }

  ngOnDestroy() {
    try {
      if (this.assetGroupSubscription) { this.assetGroupSubscription.unsubscribe(); }
      if (this.assetGroupSubscription) { this.domainSubscription.unsubscribe(); }
    } catch (error) {
      this.logger.log('error', error);
    }
  }
}
