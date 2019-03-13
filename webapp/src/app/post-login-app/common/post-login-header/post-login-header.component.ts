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
import { environment } from './../../../../environments/environment';

@Component({
    selector: 'app-post-login-header',
    templateUrl: './post-login-header.component.html',
    styleUrls: ['./post-login-header.component.css'],
    providers: []

})
export class PostLoginHeaderComponent implements OnInit, OnDestroy {

    constructor(private dataCacheService: DataCacheService,
                private workflowService: WorkflowService,
                private loggerService: LoggerService,
                private domainMappingService: DomainMappingService,
                private domainTypeObservableService: DomainTypeObservableService,
                private assetGroupObservableService: AssetGroupObservableService,
                private permissions: PermissionGuardService
    ) {
       this.config = CONFIGURATIONS;
       this.staticContent = CONTENT;
       this.environment = environment;
    }

    @Input() navigationDetails: any;

    config;
    staticContent;
    assetGroupSubscription: Subscription;
    subscriptionToDomainType: Subscription;
    showUserInfo = false;
    FirstName: string;
    userType;
    haveAdminPageAccess = false;

    public agAndDomain = {};
    private selectedDomainName;
    public burgerMenuModuleLinks;
    public footerData;
    public showMenu;
    public environment;


    ngOnInit() {
        try {
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
        } catch (error) {
            this.loggerService.log('error', 'JS Error' + error);
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

    /**
     * added by Trinanjan on 09/02/2018 for back button functionality
     * To clear page levels
     */
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
        const toolsLinks = this.domainMappingService.getDashboardsApplicableForADomain(this.selectedDomainName, 'tools');
        const statisticsLinks = [
            {
                route: 'stats-overlay',
                name: 'Statistics',
                overlay: true
            }
            // {
            //     route: 'domain-overlay',
            //     name: 'Compliance',
            //     overlay: true
            // }
            // {
            //     route: 'vulnerability-report',
            //     name: 'Vulnerability Report',
            //     overlay: true
            // }
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

        let toolsLinksUpdated = JSON.parse(JSON.stringify(toolsLinks));
        toolsLinksUpdated = toolsLinksUpdated.map(eachRoute => {
            eachRoute.route = 'tools/' + eachRoute.route;
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
                img: '../assets/icons/tools.svg',
                title: 'tools',
                rows: toolsLinksUpdated,
                shown: this.config.required.featureModules.TOOLS_MODULE
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


    ngOnDestroy() {
        this.assetGroupSubscription.unsubscribe();
        if (this.subscriptionToDomainType) { this.subscriptionToDomainType.unsubscribe(); }
    }

  }
