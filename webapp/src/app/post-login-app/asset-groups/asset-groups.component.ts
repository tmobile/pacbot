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

import { Component, Input, Output, EventEmitter, OnDestroy, AfterViewInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { AssetTilesService } from '../../core/services/asset-tiles.service';
import { AssetGroupObservableService } from '../../core/services/asset-group-observable.service';
import { UpdateRecentAGService } from './../common/services/update-recent-ag.service';
import { Subscription } from 'rxjs/Subscription';
import { AutorefreshService } from '../../pacman-features/services/autorefresh.service';
import { environment } from './../../../environments/environment';
import { ErrorHandlingService } from '../../shared/services/error-handling.service';
import { DataCacheService } from '../../core/services/data-cache.service';
import { LoggerService } from '../../shared/services/logger.service';
import { UtilsService } from '../../shared/services/utils.service';

@Component({
  selector: 'app-asset-groups',
  templateUrl: './asset-groups.component.html',
  styleUrls: ['./asset-groups.component.css'],
  providers: [AssetTilesService, AutorefreshService, LoggerService, ErrorHandlingService, UpdateRecentAGService]

})
export class AssetGroupsComponent implements AfterViewInit, OnDestroy {

  constructor(private router: Router,
              private dataStore: DataCacheService,
              private assetGroupsService: AssetTilesService,
              private assetGroupObservableService: AssetGroupObservableService,
              private updateRecentAGService: UpdateRecentAGService,
              private activatedRoute: ActivatedRoute,
              private errorHandlingService: ErrorHandlingService,
              private logger: LoggerService,
              private utils: UtilsService) {
              this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
              assetGroupName => {

                  this.thisAssetTile = assetGroupName;
                  this.selectedGroup = assetGroupName;
                  this.assetTileClicked(this.thisAssetTile);
              });

}

  @Input() clickedVal = false;
  @Input() hideCloseButton;
  @Input() notLoadedAsModel;
  @Output() closeAssetGroup = new EventEmitter();

  assetTabName: any;
  selectedTab = 0;
  selectedTabName;
  returnedSearch = '';
  assetTiles;
  loaded: boolean;
  thisAssetTile: string;
  selectedGroup: string;
  assetDetailTiles: any;
  recentTiles: any = [];
  userDetails: any;
  assetGroup: any = {};
  showError = false;
  assetDetailsState = 0;

  private subscriptionToAssetGroup: Subscription;
  private assetDetailsSubscription: Subscription;
  private assetTilesSubscription: Subscription;
  private updateRecentAGSubscription: Subscription;


  ngAfterViewInit() {
    try {
      if (this.subscriptionToAssetGroup) {
        this.subscriptionToAssetGroup.unsubscribe();
      }
      this.loaded = false;
      this.retrieveFragment();
      this.getAssetTiles();
    } catch (error) {
      this.errorHandlingService.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  retrieveFragment() {
    this.activatedRoute.fragment.subscribe((fragment: string) => {
      this.selectedTabName = fragment;
    });
  }

  getSearch(search) {
    this.returnedSearch = search;
  }

  getAssetTiles(): void {
    this.showError = false;

    const assetGroupList = this.dataStore.getListOfAssetGroups();

    if (!assetGroupList || assetGroupList === 'undefined') {

        const assetUrl = environment.assetTiles.url;
        const assetMethod = environment.assetTiles.method;

        this.assetTilesSubscription = this.assetGroupsService.getAssetTiles(assetUrl, assetMethod).subscribe(
            response => {
                this.assetTiles = response[0];
                this.dataStore.setListOfAssetGroups(JSON.stringify(this.assetTiles));
                this.processData();
            },
            error => {
                this.loaded = true;
                this.showError = true;
                this.logger.log('error', error);
            });
    } else {
        this.assetTiles = JSON.parse(assetGroupList);
        this.processData();
    }
  }

  getCurrentAssetInfo(assetInfo) {

  }
  assetTileClicked(tile) {
     this.thisAssetTile = tile;
     this.getAssetDetailTiles(tile);
     this.updateRecentAssetGroup(tile);
  }

  setDefault() {
    try {

      this.instructParentToCloseAssetGroup(this.thisAssetTile);
      const userDefaultAssetGroup = this.dataStore.getUserDefaultAssetGroup();

      if (this.thisAssetTile !== userDefaultAssetGroup) {
        this.updateDefaultAssetGroupForUser(this.thisAssetTile);
      }
    } catch (error) {
      this.errorHandlingService.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  selectAsset(assetGroup) {
    try {
      this.instructParentToCloseAssetGroup(assetGroup.name);
      if (assetGroup.name !== this.selectedGroup) {
        this.selectedGroup = assetGroup.name;

        this.assetTileClicked(assetGroup.name);
      }
    } catch (error) {
      this.errorHandlingService.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  updateDefaultAssetGroupForUser(assetGroup) {
    try {

      const updateAssetGroupUrl = environment.saveDefaultAssetGroup.url;
      const updateAssetGroupMethod = environment.saveDefaultAssetGroup.method;

      const userId = this.dataStore.getUserDetailsValue().getUserId();

      this.assetTilesSubscription = this.assetGroupsService.updateDefaultAssetGroupForUser(updateAssetGroupUrl, updateAssetGroupMethod, assetGroup, userId).subscribe(
        response => {
          this.dataStore.setUserDefaultAssetGroup(assetGroup);
        },
        error => {
       });
    } catch (error) {
      this.errorHandlingService.handleJavascriptError(error);
    }
  }

  processData() {
    try {
      const typeObj = {
      'all': 'typeVal',
      'recently viewed': 'typeVal'
      };
      for ( let i = 0 ; i < this.assetTiles.length; i++) {
        typeObj[this.assetTiles[i].type.toLowerCase()] = 'typeVal';
      }
      delete typeObj[''];
      let typeArr = [];
      typeArr = Object.keys(typeObj);
      this.assetTabName = typeArr;
      /* Bottom line is not required */
      // this.selectedTabName = this.assetTabName[this.selectedTab];
      this.loaded = true;
    } catch (error) {
      this.errorHandlingService.handleJavascriptError(error);
      this.logger.log('error', error);
    }
  }

  updateTab(newTab) {
    this.selectedTabName = newTab.toLowerCase();
  }

  getAssetDetailTiles(groupName) {

    const assetDetailUrl = environment.assetTilesdata.url;

    const assetDetailMethod = environment.assetTilesdata.method;

    const queryParams = {
      'ag': groupName
    };

    this.assetDetailsState = 0;

     if (queryParams['ag'] !== undefined) {

     this.assetDetailsSubscription = this.assetGroupsService.getAssetdetailTiles(queryParams, assetDetailUrl, assetDetailMethod).subscribe(
       response => {
         this.assetDetailsState = 1;
         this.assetDetailTiles = response[0];
      },
      error => {
          this.assetDetailsState = -1;
      });
     }
  }

  updateRecentAssetGroup(groupName) {

    const updateRecentAGUrl = environment.updateRecentAG.url;

    const updateRecentAGMethod = environment.updateRecentAG.method;

    const userId = this.dataStore.getUserDetailsValue().getUserId();

    const queryParams = {
      'ag': groupName,
      'userId': userId
    };

    if (queryParams['ag'] !== undefined) {

     this.updateRecentAGSubscription = this.updateRecentAGService.updateRecentlyViewedAG(queryParams, updateRecentAGUrl, updateRecentAGMethod).subscribe(
       response => {
         this.recentTiles = response.data.response[0].recentlyViewedAg;
      },
      error => {

      });
    }
  }

  instructParentToCloseAssetGroup (assetGroupName) {
      this.closeAssetGroup.emit(assetGroupName);
  }

  checkRecentlyViewed(name) {

    if (!name || !this.selectedTabName) {
      return false;
    }
    const tiles = this.recentTiles.map(item => {
      return item['ag'];
    });
    if (this.selectedTabName.toLowerCase() === 'recently viewed') {
       if (tiles.includes(name.name.toLowerCase())) {
         return true;
       } else {
         return false;
       }
    } else {
      return false;
    }

  }

    /**
   * This function navigates the page mentioned  with a ruleID
   */
    navigatePage(data1, data2) {
      /**
       * selectAsset function closes the modal window and update the asset group
       * after that router.navigate is used to navigate
       */

    try {
      const clickText = data1;
      const apiTarget = {'TypeAsset' : 'TotalAsset'};
            /**
             * Router navigation is not working , need to check --> Trinanjan
             */

            if (clickText.toLowerCase() === 'total asset' ) {
                /**
                 * This router.navigate function is added By Trinanjan on 31.01.2018
                 * This router.navigate function first closes the modal window and then navigates to the path specified
                 */
                const eachParams = {};
                let newParams = this.utils.makeFilterObj(eachParams);
                newParams = Object.assign(newParams, apiTarget);
                newParams['ag'] = data2;


                this.router.navigate([{
                    outlets: {
                        modal: null
                    }
                }],
                    {
                        relativeTo: this.activatedRoute.parent,
                        queryParamsHandling: 'merge'
                    }).then(() => this.router.navigate(['pl', 'assets' , 'asset-list' ], {queryParams: newParams, queryParamsHandling: 'merge'}));
            }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  ngOnDestroy() {
    try {
      this.subscriptionToAssetGroup.unsubscribe();
    } catch (error) {

    }
  }

}
