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

import { Injectable } from '@angular/core';
import { DOMAIN_MAPPING } from '../../../config/domain-mapping';
import { COMPLIANCE_ROUTES, TOOLS_ROUTES, ADMIN_ROUTES, OMNISEARCH_ROUTES } from '../../shared/constants/routes';
import { ASSETS_ROUTES } from '../../shared/constants/routes';
import { DataCacheService } from './data-cache.service';
import * as _ from 'lodash';
import { Router } from '@angular/router';
import { CONFIGURATIONS } from '../../../config/configurations';

@Injectable()
export class DomainMappingService {
    constructor(private dataCacheService: DataCacheService, private router: Router) {}

    getDomainInfoForSelectedDomain(key) {
        /*
            * this function returns the info related to the selected domain
            * which is defined in the doamin constant file
        */

        const domainInfoObj = DOMAIN_MAPPING.find(o => o.domain === key );
        return domainInfoObj;
    }

    /*@returns: It returns the list of links applicable for a domain and module */
    getDashboardsApplicableForADomain(domainName, moduleName) {
        try {

            const domains = [];
            if (!domainName ) {
                domains.push(domainName);
            } else {
                domains.push('');
                domains.push(domainName);
            }

            let ListOfDashboards = [];

            domains.forEach((domain) => {
                const domainObj = this.getDomainInfoForSelectedDomain(domain);
                const dashboardsObj = this.getDashboardsPathForADomain(domainObj.dashboards, moduleName);
                ListOfDashboards = ListOfDashboards.concat(dashboardsObj.dashboards);
            });

            const currentSelectedAg = this.dataCacheService.getCurrentSelectedAssetGroup();
            let recentList = '';
            let provider = [];
            recentList = this.dataCacheService.getRecentlyViewedAssetGroups();
            if (recentList) {
            const currentAGDetails = JSON.parse(recentList).filter(element => element.ag === currentSelectedAg);
            provider = this.fetchprovider(currentAGDetails);
            }
            if (currentSelectedAg.includes('azure') || (provider.length === 1 && provider[0] === 'azure')) {
                ListOfDashboards = ListOfDashboards.filter(element => {
                    if (window.location.pathname.includes(element.route) && element.cloudSpecific) {
                        this.router.navigate(['pl/compliance/compliance-dashboard'], {
                            queryParams: { domain: domainName },
                            queryParamsHandling: 'merge'
                        });
                    }
                    return element.cloudSpecific !== true;
                });
            }

             // check qualys enabled or not
            if (!CONFIGURATIONS.optional.general.qualysEnabled) {
                ListOfDashboards = ListOfDashboards.filter(item => !(item.name === 'Vulnerabilities' && item.route === 'vulnerabilities-compliance'));
            }

            let updatedListOfLinks = ListOfDashboards.map(dashboard => {
                // Get title from routes data

                const data = this.getRouteData(dashboard.route, moduleName);

                dashboard['name'] = data.title || 'No page title';
                dashboard['data'] = data;
                return dashboard;
            });

            /* To check permissions if user has access to some roles. */
            const userRoles = this.dataCacheService.getUserDetailsValue().getRoles();
            updatedListOfLinks = this.checkForUserPermissions(updatedListOfLinks, userRoles);

            // Sort the link by their priority sequence */
            updatedListOfLinks.sort((a, b) => {
                return a.sequence - b.sequence;
            });

            return updatedListOfLinks;
        } catch (e) {
            console.log(e);
        }
    }

    /*@returns: List of all routes for a module */
    getAllRoutesOfAModule(moduleName) {
        if (moduleName === 'compliance' ) {
            return COMPLIANCE_ROUTES;
        } else if (moduleName === 'assets') {
            return ASSETS_ROUTES;
        } else if (moduleName === 'tools') {
            return TOOLS_ROUTES;
        } else if (moduleName === 'omnisearch') {
            return OMNISEARCH_ROUTES;
        } else if (moduleName === 'admin') {
            return ADMIN_ROUTES;
        }
    }

    /*@returns: data of the route which can be used as a link in side nav and burger menu and to check roles */
    getRouteData(path, moduleName) {
        let routes = [];
        routes = this.getAllRoutesOfAModule(moduleName);
        const route = routes.find( routing => {
            return routing.path === path;
        });
        if (route && route.data) {
            return route.data;
        } else {
            return {'title': 'No page title'};
        }
    }

    checkForUserPermissions(links, userRoles) {

        return links.filter((eachLink) => {
            if (eachLink.data && eachLink.data.roles && eachLink.data.roles.length) {
                const linkRoles = eachLink.data.roles;
                /* If all link roles are available in userRoles, that means, user has access to that link */
                const hasAccess = !(_.difference(linkRoles, userRoles).length);
                return hasAccess;
            } else {
                return true;
            }
        });

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

    getDashboardsPathForADomain(dashboards, moduleName) {
        return dashboards.find(eachModule => eachModule.moduleName === moduleName) || {'dashboards': []};
    }

    getThemeForADomain(domainName) {
        return this.getDomainInfoForSelectedDomain(domainName).theme;
    }
}
