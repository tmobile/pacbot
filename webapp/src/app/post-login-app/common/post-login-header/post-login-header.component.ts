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

import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { DataCacheService } from '../../../core/services/data-cache.service';
import { WorkflowService } from '../../../core/services/workflow.service';
import { LoggerService } from '../../../shared/services/logger.service';
import { DomainMappingService } from '../../../core/services/domain-mapping.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { PermissionGuardService } from '../../../core/services/permission-guard.service';
import { CONFIGURATIONS } from '../../../../config/configurations';
import { CONTENT } from './../../../../config/static-content';
import { RecentlyViewedObservableService } from '../../../core/services/recently-viewed-observable.service';
import { environment } from './../../../../environments/environment';
import { ActivatedRoute, Router } from '@angular/router';
import { UpdateRecentAGService } from '../../common/services/update-recent-ag.service';
import { RouterUtilityService } from '../../../shared/services/router-utility.service';
import { HttpHeaders } from '@angular/common/http';
import { AdalService } from '../../../core/services/adal.service';
import { HttpService } from '../../../shared/services/http-response.service';
import { UtilsService } from '../../../shared/services/utils.service';

@Component({
    selector: 'app-post-login-header',
    templateUrl: './post-login-header.component.html',
    styleUrls: ['./post-login-header.component.css'],
    providers: [UpdateRecentAGService]
})

export class PostLoginHeaderComponent implements OnInit, OnDestroy {

    @Input() navigationDetails: any;
    @Input() authorizationPassed: any;

    config;
    dynamicIconPath;
    showRecents = false;
    staticContent;
    assetGroupSubscription: Subscription;
    subscriptionToDomainType: Subscription;
    recentSubscription: Subscription;
    showUserInfo = false;
    FirstName: string;
    userType;
    haveAdminPageAccess = false;
    currentAg;
    recentTiles = [];
    provider = [];
    profilePictureSrc: any = '/assets/icons/profile-picture.svg';
    cloudIconDataLoaded = false;
    public agAndDomain = {};
    private selectedDomainName;
    public burgerMenuModuleLinks;
    public footerData;
    public showMenu;
    public environment;
    tvState;
    querySubscription: Subscription;
    updateRecentAGSubscription: Subscription;
    subscriptionToAssetGroup: Subscription;

    constructor(private dataCacheService: DataCacheService,
                private workflowService: WorkflowService,
                private loggerService: LoggerService,
                private activatedRoute: ActivatedRoute,
                private router: Router,
                private recentAssetsObservableService: RecentlyViewedObservableService,
                private domainMappingService: DomainMappingService,
                private domainTypeObservableService: DomainTypeObservableService,
                private assetGroupObservableService: AssetGroupObservableService,
                private permissions: PermissionGuardService,
                private routerUtilityService: RouterUtilityService,
                private updateRecentAGService: UpdateRecentAGService,
                private adalService: AdalService,
                private httpResponseService: HttpService,
                private utilService: UtilsService
    ) {
       this.config = CONFIGURATIONS;
       this.staticContent = CONTENT;
       this.environment = environment;
       this.subscriptionToAssetGroup = this.assetGroupObservableService
            .getAssetGroup()
            .subscribe(assetGroupName => {
            if (assetGroupName) {
                this.currentAg = assetGroupName;
                this.cloudIconDataLoaded = false;
                this.updateRecentAssetGroup(this.currentAg);
            }
       });

       this.recentSubscription = this.recentAssetsObservableService.getRecentAssets().subscribe(recentList => {
        this.recentTiles = recentList;
      });
    }


    ngOnInit() {
        try {
            document.addEventListener('keyup', this.logKey.bind(this));
            this.querySubscription = this.activatedRoute.queryParams.subscribe(queryParams => {
                this.tvState = queryParams['tv'];
            });
            this.dynamicIconPath = '../assets/icons/' + this.config.required.APP_NAME.toLowerCase() + '-white-text-logo.svg';
            this.haveAdminPageAccess = this.permissions.checkAdminPermission();
            this.userType = this.haveAdminPageAccess ? 'Admin' : '';
            this.FirstName = 'Guest';
            const detailsData = this.dataCacheService.getUserDetailsValue();
            const firstNameData = detailsData.getFirstName();
            if (firstNameData) {
                this.FirstName = firstNameData;
            }
            this.selectedDomainName = '';
            this.getModuleLinks();
            this.subscribeToAssetGroup();
            this.subscribeToDomainType();

            this.getProfilePictureOfUser();

        } catch (error) {
            this.loggerService.log('error', 'JS Error' + error);
        }
    }

    logKey(e) {
        e.stopPropagation();
        if (e.keyCode === 27) {
          this.showMenu = false;
        }
      }

    closeUserInfo() {
      try {
        const x = this;
        setTimeout(function () {
          x.showUserInfo = false;
        }, 300);
      } catch (error) {
        this.loggerService.log('error', error);
      }

    }

    clearPageLevel() {
      this.workflowService.clearAllLevels();
    }

    subscribeToAssetGroup() {
        try {
            this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroup => {
                if (assetGroup) {
                    this.agAndDomain['ag'] = assetGroup;
                }
            });
        } catch (error) {
            this.loggerService.log('error', error);
        }
    }

    /**
     *This is the subscribtion function for domain selection
     */
    subscribeToDomainType() {
        try {
            this.subscriptionToDomainType = this.domainTypeObservableService
                .getDomainType()
                .subscribe(domainName => {
                    if (domainName) {
                        this.agAndDomain['domain'] = domainName;
                        this.selectedDomainName = domainName;
                        this.getModuleLinks();
                    }
                });
        } catch (error) {
            this.loggerService.log('error', error);
        }
    }

    getModuleLinks() {
        const complianceLinks = this.domainMappingService.getDashboardsApplicableForADomain(this.selectedDomainName, 'compliance');
        const assetsLinks = this.domainMappingService.getDashboardsApplicableForADomain(this.selectedDomainName, 'assets');
        const statisticsLinks = [
            {
                route: 'stats-overlay',
                name: 'Statistics',
                overlay: true
            }
        ];


        let complianceLinksUpdated = JSON.parse(JSON.stringify(complianceLinks));
        complianceLinksUpdated = complianceLinksUpdated.map(eachRoute => {
            eachRoute.route = 'compliance/' + eachRoute.route;
            return eachRoute;
        });

        let assetsLinksUpdated = JSON.parse(JSON.stringify(assetsLinks));
        assetsLinksUpdated = assetsLinksUpdated.map(eachRoute => {
            eachRoute.route = 'assets/' + eachRoute.route;
            return eachRoute;
        });

        this.burgerMenuModuleLinks = [
            {
                img: '../assets/icons/compliance.svg',
                title: 'compliance',
                rows: complianceLinksUpdated,
                shown: this.config.required.featureModules.COMPLIANCE_MODULE
            },
            {
                img: '../assets/icons/assets.svg',
                title: 'assets',
                rows: assetsLinksUpdated,
                shown: this.config.required.featureModules.ASSETS_MODULE
            },
            {
                img: '../assets/icons/Statistics.svg',
                title: 'Statistics',
                rows: statisticsLinks,
                shown: true
            }
        ];
        this.footerData = [];

        if (
            this.staticContent &&
            this.staticContent.homePage &&
            this.staticContent.homePage.contactUs &&
            this.staticContent.homePage.contactUs.email &&
            this.staticContent.homePage.contactUs.slack) {
                this.footerData = [
                {
                    img: '../assets/icons/contact.svg', title: 'Contact',
                    makePresent: false,
                    rows: [{
                    'name': 'Email Us',
                    'route': this.staticContent.homePage.contactUs.email,
                    'target': ''
                    }]
                },
                {
                    img: '../assets/icons/social.svg', title: 'Social',
                    makePresent: false,
                    rows: [{
                    'name': 'Slack',
                    'route': this.staticContent.homePage.contactUs.slack,
                    'target': '_blank'
                    }]
                }
            ];
        }
    }

    makePresent(index) {
        this.footerData[index].makePresent = true;
        return true;
    }

    changeAg(agData) {
        const updatedFilters = JSON.parse(JSON.stringify(this.routerUtilityService.getQueryParametersFromSnapshot(this.router.routerState.snapshot.root)));
        updatedFilters['ag'] = agData.ag;
        this.router.navigate([], {
            relativeTo: this.activatedRoute,
            queryParams: updatedFilters
          });
        this.showRecents = false;
    }

    updateRecentAssetGroup(groupName) {
        if (this.updateRecentAGSubscription) {
          this.updateRecentAGSubscription.unsubscribe();
        }
        const updateRecentAGUrl = environment.updateRecentAG.url;
        const updateRecentAGMethod = environment.updateRecentAG.method;
        const userId = this.dataCacheService.getUserDetailsValue().getUserId();
        const queryParams = {
          'ag': groupName,
          'userId': userId
        };
        if (queryParams['ag'] !== undefined) {
         this.updateRecentAGSubscription = this.updateRecentAGService.updateRecentlyViewedAG(queryParams, updateRecentAGUrl, updateRecentAGMethod).subscribe(
           response => {
             this.recentTiles = response.data.response[0].recentlyViewedAg;
             /* Store the recently viewed asset list in stringify format */
             this.dataCacheService.setRecentlyViewedAssetGroups(JSON.stringify(this.recentTiles));
             const currentAGDetails = this.recentTiles.filter(element => element.ag === groupName);
             this.provider = this.fetchprovider(currentAGDetails);
             this.cloudIconDataLoaded = true;
             this.recentAssetsObservableService.updateRecentAssets(this.recentTiles);
          },
          error => {

          });
        }
      }

      fetchprovider(assetGroupObject) {
        const provider = [];
        if (assetGroupObject.length && assetGroupObject[0].providers) {
          assetGroupObject[0].providers.forEach(element => {
            provider.push(element.provider);
          });
        }
        return provider;
    }

    openAgModal() {
        this.router.navigate(['/pl', {outlets: { modal: ['change-default-asset-group'] } }], {queryParamsHandling: 'merge'});
    }

    handleAssetGroupFlow() {
        if (this.recentTiles.length) {
            this.showRecents = !this.showRecents;
        } else {
            this.openAgModal();
        }
    }

    getProfilePictureOfUser() {
        // Get profile picture of user from azure ad.

        // this.adalService.acquireToken(CONFIGURATIONS.optional.auth.resource).subscribe(token => {
        //     const api = environment.fetchProfilePic.url;
        //     const httpMethod = environment.fetchProfilePic.method;
        //     const header = new HttpHeaders();
        //     const updatedHeader = header.append('Authorization', 'Bearer ' + token);

        //     this.httpResponseService.getBlobHttpResponse(api, httpMethod, {}, {}, {headers: updatedHeader}).subscribe(response => {
        //         this.utilService.generateBase64String(response).subscribe(image => {
        //             this.loggerService.log('info', 'user profile pic received');
        //             this.dataCacheService.setUserProfileImage(image);
        //             this.profilePictureSrc = image;
        //         });
        //     },
        //     error => {
        //         this.loggerService.log('error', 'error while fetching image from azure ad - ' + error);
        //     });

        // }, error => {
        //     this.loggerService.log('error', 'Error while fetching access token for resource - ' + error);
        // });
    }

    ngOnDestroy() {
        if (this.assetGroupSubscription) { this.assetGroupSubscription.unsubscribe(); }
        if (this.subscriptionToDomainType) { this.subscriptionToDomainType.unsubscribe(); }
    }

  }
